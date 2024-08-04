package de.siramac.hexomato.ws;

import de.siramac.hexomato.domain.Game;
import de.siramac.hexomato.domain.Player;
import de.siramac.hexomato.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/ws/game")
public class GameController {

    //    private final Sinks.Many<ServerSentEvent<GameWs>> sink = Sinks.many().replay().all();
    private final Map<Long, Sinks.Many<ServerSentEvent<GameWs>>> gameIdToSinkMap = new ConcurrentHashMap<>();
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping(value = "/register/sse/gameId/{gameId}", produces = "text/event-stream")
    public Flux<ServerSentEvent<GameWs>> registerSse(@PathVariable Long gameId) {
        Sinks.Many<ServerSentEvent<GameWs>> sink = gameIdToSinkMap
//                .computeIfAbsent(gameId, id -> Sinks.many().multicast().onBackpressureBuffer());
                .computeIfAbsent(gameId, id -> Sinks.many().replay().all());
        return sink.asFlux()
                .doOnSubscribe(subscription -> log.info("client connected"))
                .doOnCancel(() -> log.info("client disconnected"));
    }

    @GetMapping("/start/gameId/{gameId}")
    public Mono<ResponseEntity<Object>> startGame(@PathVariable Long gameId) {
        return Mono.fromCallable(() -> gameService.loadGame(gameId))
                .subscribeOn(Schedulers.boundedElastic())
                .map(game -> {
                    triggerSse(game);
                    return ResponseEntity.ok().build();
                }).defaultIfEmpty(ResponseEntity.unprocessableEntity().build());
    }
//    @PostConstruct
//    public void scheduleTriggerEvent() {
//        Flux.interval(Duration.ofSeconds(1))
//                .flatMap(tick -> {
//                    triggerEvent(Player.PLAYER_1, tick);
//                    return Flux.empty();
//                })
//                .subscribe();
//    }

    @GetMapping("/makeMove/gameId/{gameId}/row/{row}/col/{col}/player/{player}")
    public Mono<ResponseEntity<Object>> makeMove(@PathVariable Long gameId, @PathVariable int row, @PathVariable int col,
                                                 @PathVariable Player player) {
        return Mono.fromCallable(() -> gameService.makeMove(gameId, row, col, player))
                .subscribeOn(Schedulers.boundedElastic())
                .map(game -> {
                    triggerSse(game);
                    return ResponseEntity.ok().build();
                })
                .defaultIfEmpty(ResponseEntity.unprocessableEntity().build());
    }

    private void triggerSse(Game game) {
        ServerSentEvent<GameWs> event = ServerSentEvent.<GameWs>builder()
                .event("message")
                .data(new GameWs(game))
                .build();
        gameIdToSinkMap.get(game.getId()).tryEmitNext(event);
    }
}
