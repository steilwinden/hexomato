package de.siramac.hexomato.ai;

import de.siramac.hexomato.domain.Game;
import de.siramac.hexomato.domain.Player;
import de.siramac.hexomato.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AiTools {

    public record LoadGameRequest(
            Long gameId) {
    }

    public record MakeMoveRequest(
            Long gameId,
            int row,
            int col,
            Player player) {
    }

    private final GameService gameService;

    @Bean
    @Description("loads the current game state, including the board")
    public Function<LoadGameRequest, Game> loadGame() {
        return request -> {
            log.info("AI calling loadGame(), gameId: {}", request.gameId);
            return gameService.loadGame(request.gameId);
        };
    }

    @Bean
    @Description("makes a move on the board")
    public Function<MakeMoveRequest, Game> makeMove() {
        return request -> {
            log.info("AI calling makeMove(), gameId: {}, row: {}, col: {}, player: {}",
                    request.gameId, request.row, request.col, request.player);
            Game game = gameService.makeMove(request.gameId, request.row, request.col, request.player);
            if (game == null) {
                log.info("AI calling makeMove() returned null");
            }
            return game;
        };
    }

}
