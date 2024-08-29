package de.siramac.hexomato.scheduler;

import de.siramac.hexomato.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty("scheduling.enabled")
public class SchedulingService {

    private final GameService gameService;

    @Scheduled(cron = "0 0 4 * * ?")
    public void deleteOlderGames() {
        log.info("running deleteOlderGames()");
        gameService.deleteOlderGames();
    }
}
