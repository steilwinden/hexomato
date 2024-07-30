package de.siramac.hexomato.ws;

import de.siramac.hexomato.domain.Game;
import de.siramac.hexomato.domain.Player;
import de.siramac.hexomato.service.GameService;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping("/ws/setup")
public class SetupController {

    private final Sinks.Many<ServerSentEvent<List<GameWs>>> sink = Sinks.many().replay().all();
    private final GameService gameService;

    public SetupController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping(value = "/register/sse", produces = "text/event-stream")
    public Flux<ServerSentEvent<List<GameWs>>> registerSse() {
        return sink.asFlux()
                .doOnSubscribe(subscription -> log.info("client connected"))
                .doOnCancel(() -> log.info("client disconnected"));
    }

    @GetMapping("/createGame/player/{player}/name/{name}")
    public Mono<Void> createGame(@PathVariable Player player, @PathVariable String name) {
        return Mono.fromCallable(() -> {
                    gameService.createGame(player, name);
                    return gameService.loadCurrentGames();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(this::triggerSse)
                .then();
    }

    private void triggerSse(List<Game> gameList) {
        ServerSentEvent<List<GameWs>> event = ServerSentEvent.<List<GameWs>>builder()
                .id(String.valueOf(System.currentTimeMillis()))
                .event("message")
                .data(gameList.stream().map(GameWs::new).toList())
                .build();
        sink.tryEmitNext(event);
    }
}