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

    private final Map<Long, Sinks.Many<ServerSentEvent<GameWs>>> gameIdToSinkMap = new ConcurrentHashMap<>();
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping(value = "/register/sse/gameId/{gameId}/namePlayer/{namePlayer}", produces = "text/event-stream")
    public Flux<ServerSentEvent<GameWs>> registerSse(@PathVariable Long gameId, @PathVariable String namePlayer) {
        Sinks.Many<ServerSentEvent<GameWs>> sink = gameIdToSinkMap
                .computeIfAbsent(gameId, id -> Sinks.many().replay().latest());

        return sink.asFlux()
                .doOnSubscribe(subscription -> {
                    log.info("{} connected", namePlayer);
                    loadAndSendGame(gameId, "");
                })
                .doOnCancel(() -> {
                    log.info("{} disconnected", namePlayer);
                    loadAndSendGame(gameId, namePlayer + " has left the game");
                });
    }

    @GetMapping("/makeMove/gameId/{gameId}/row/{row}/col/{col}/player/{player}")
    public Mono<ResponseEntity<Object>> makeMove(@PathVariable Long gameId, @PathVariable int row, @PathVariable int col,
                                                 @PathVariable Player player) {
        return Mono.fromCallable(() -> gameService.makeMove(gameId, row, col, player))
                .subscribeOn(Schedulers.boundedElastic())
                .map(game -> {
                    sendGame(game, "");
                    return ResponseEntity.ok().build();
                })
                .defaultIfEmpty(ResponseEntity.unprocessableEntity().build());
    }

    private void loadAndSendGame(Long gameId, String connectionMessage) {
        Mono.fromCallable(() -> gameService.loadGame(gameId))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(game -> sendGame(game, connectionMessage))
                .subscribe();
    }

    private void sendGame(Game game, String connectionMessage) {
        if (gameService.isAiTurn(game)) {
            Mono.fromCallable(() -> gameService.makeAiMove(game))
                    .subscribeOn(Schedulers.boundedElastic())
                    .doOnNext(nextGame -> triggerSse(nextGame, connectionMessage))
                    .subscribe();
        } else {
            triggerSse(game, connectionMessage);
        }
    }

    private void triggerSse(Game game, String connectionMessage) {
        GameWs gameWs = new GameWs(game, connectionMessage);
        ServerSentEvent<GameWs> event = ServerSentEvent.<GameWs>builder()
                .event("message")
                .data(gameWs)
                .build();
        gameIdToSinkMap.get(game.getId()).tryEmitNext(event);
    }
}
