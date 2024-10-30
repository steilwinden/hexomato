package de.siramac.hexomato.scheduler;

import de.siramac.hexomato.service.GameService;
import de.siramac.hexomato.ws.SetupController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty("scheduler.enabled")
public class Scheduler {

    private final GameService gameService;
    private final SetupController setupController;

    @Scheduled(cron = "0 0 4 * * ?")
    public void deleteOlderGames() {
        log.info("running deleteOlderGames()");
        gameService.deleteOlderGames();
        setupController.triggerSse(gameService.loadCurrentGames());
    }
}
