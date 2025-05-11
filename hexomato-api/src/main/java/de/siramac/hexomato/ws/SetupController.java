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

import java.util.List;

import static de.siramac.hexomato.domain.Player.PLAYER_1;
import static de.siramac.hexomato.domain.Player.PLAYER_2;
import static de.siramac.hexomato.service.GameService.ALPHA_MAX_AI_NAME;
import static de.siramac.hexomato.service.GameService.MONTE_CARLO_AI_NAME;

@Slf4j
@RestController
@RequestMapping("/ws/setup")
public class SetupController {

    private final Sinks.Many<ServerSentEvent<List<GameOnlyWs>>> sink = Sinks.many().replay().latest();
    private final GameService gameService;

    public SetupController(GameService gameService) {
        this.gameService = gameService;
        gameService.createGame(PLAYER_1, false, ALPHA_MAX_AI_NAME);
        gameService.createGame(PLAYER_2, false, MONTE_CARLO_AI_NAME);
        triggerSse(gameService.loadCurrentGames());
    }

    @GetMapping(value = "/register/sse", produces = "text/event-stream")
    public Flux<ServerSentEvent<List<GameOnlyWs>>> registerSse() {
        return sink.asFlux()
                .doOnSubscribe(subscription -> log.info("client connected"))
                .doOnCancel(() -> log.info("client disconnected"));
    }

    @GetMapping("/createGame/player/{player}/name/{name}")
    public Mono<Long> createGame(@PathVariable Player player, @PathVariable String name) {
        return Mono.fromCallable(() -> gameService.createGame(player, true, name))
                .subscribeOn(Schedulers.boundedElastic())
                .map(gameId -> {
                    triggerSse(gameService.loadCurrentGames());
                    return gameId;
                });
    }

    @GetMapping("/joinGame/gameId/{gameId}/player/{player}/name/{name}")
    public Mono<ResponseEntity<Object>> joinGame(@PathVariable Long gameId, @PathVariable Player player,
                                                 @PathVariable String name) {

        return Mono.fromCallable(() -> gameService.joinGame(gameId, player, name))
                .subscribeOn(Schedulers.boundedElastic())
                .map(success -> {
                    if (success) {
                        triggerSse(gameService.loadCurrentGames());
                        return ResponseEntity.ok().build();
                    } else {
                        return ResponseEntity.unprocessableEntity().build();
                    }
                });
    }

    public void triggerSse(List<Game> gameList) {
        ServerSentEvent<List<GameOnlyWs>> event = ServerSentEvent.<List<GameOnlyWs>>builder()
                .event("message")
                .data(gameList.stream().map(GameOnlyWs::new).toList())
                .build();
        sink.tryEmitNext(event);
    }
}