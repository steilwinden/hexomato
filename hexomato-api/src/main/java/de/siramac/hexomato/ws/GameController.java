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

@Slf4j
@RestController
@RequestMapping("/ws/game")
public class GameController {

    private final Sinks.Many<ServerSentEvent<GameWs>> sink = Sinks.many().replay().all();
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping(value = "/register/sse", produces = "text/event-stream")
    public Flux<ServerSentEvent<GameWs>> registerSse() {
        return sink.asFlux()
                .doOnSubscribe(subscription -> log.info("client connected"))
                .doOnCancel(() -> log.info("client disconnected"));
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

    @GetMapping("/joinGame/gameId/{gameId}/name/{name}")
    public Mono<ResponseEntity<Object>> joinGame(@PathVariable Long gameId, @PathVariable String name) {
        return Mono.fromCallable(() -> {
            boolean result = gameService.joinGame(gameId, name);
            if (!result) {
                return ResponseEntity.unprocessableEntity().build();
            }
            return ResponseEntity.ok().build();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/makeMove/game/{gameId}/row/{row}/col/{col}/player({player}")
    public Mono<ResponseEntity<Object>> makeMove(@PathVariable Long gameId, @PathVariable int row, @PathVariable int col,
                                                 @PathVariable Player player) {
        return Mono.fromCallable(() -> gameService.makeMove(gameId, row, col, player))
                .subscribeOn(Schedulers.boundedElastic())
                .map(game -> {
                    if (game == null) {
                        return ResponseEntity.unprocessableEntity().build();
                    } else {
                        Mono.fromRunnable(() -> triggerSse(game))
                                .subscribeOn(Schedulers.boundedElastic())
                                .subscribe();
                        return ResponseEntity.ok().build();
                    }
                });
    }

    private void triggerSse(Game game) {
        ServerSentEvent<GameWs> event = ServerSentEvent.<GameWs>builder()
                .id(String.valueOf(System.currentTimeMillis()))
                .event("message")
                .data(new GameWs(game))
                .build();
        sink.tryEmitNext(event);
    }
}
