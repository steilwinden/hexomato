package de.siramac.hexomato.ws;

import jakarta.annotation.PostConstruct;
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

import java.time.Duration;

@Slf4j
@RestController
@RequestMapping("/api")
public class ApiController {

    private final Sinks.Many<ServerSentEvent<HexNode>> sink = Sinks.many().replay().all();

    @GetMapping(value = "/register/sse", produces = "text/event-stream")
    public Flux<ServerSentEvent<HexNode>> streamEvents() {
        return sink.asFlux()
                .doOnSubscribe(subscription -> log.info("client connected"))
                .doOnCancel(() -> log.info("client disconnected"));
    }

    @PostConstruct
    public void scheduleTriggerEvent() {
        Flux.interval(Duration.ofSeconds(1))
                .flatMap(tick -> {
                    triggerEvent(Player.PLAYER_1, tick);
                    return Flux.empty();
                })
                .subscribe();
    }

    @GetMapping("/makeMove/player({player}/hexNodeId/{hexNodeId}")
    public Mono<ResponseEntity<Void>> makeMove(@PathVariable Player player, @PathVariable Long hexNodeId) {
        Mono.fromRunnable(() -> triggerEvent(player, hexNodeId)).subscribeOn(Schedulers.boundedElastic()).subscribe();

        return Mono.just(ResponseEntity.ok().build());
    }

    private void triggerEvent(Player player, Long hexNodeId) {
        ServerSentEvent<HexNode> event = ServerSentEvent.<HexNode>builder()
                .id(String.valueOf(System.currentTimeMillis()))
                .event("message")
                .data(new HexNode(hexNodeId, false, false, player))
                .build();
        sink.tryEmitNext(event);
    }
}
