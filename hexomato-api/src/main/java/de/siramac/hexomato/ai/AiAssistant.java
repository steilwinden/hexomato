package de.siramac.hexomato.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Service
public class AiAssistant {

    private final ChatClient chatClient;

    public AiAssistant(ChatClient.Builder modelBuilder, VectorStore vectorStore, ChatMemory chatMemory) {

        this.chatClient = modelBuilder
                .defaultSystem("""
                        You (as AI) are playing the Hex game against another online player, which can
                        be PLAYER_1 or PLAYER_2. PLAYER_1 places red stones and PLAYER_2 blue stones.
                        If you are PLAYER_1 you try to make a path from the top to the bottom edge of the board,
                        if you are PLAYER_2 you try to make a path from the left to the right edge of the board.
                        The board has a size of 11 x 11 hexagons.
                        If you will get the prompt that start with "It's your turn now" you have
                        to choose a move. You can do so by calling the makeMove() method.
                        You always have to check the return value of that method call. If you get null returned
                        your move was invalid and you have to choose another move by calling makeMove() again.
                        
                        Before you make a move you should investigate the current game state by calling the
                        loadGame() method with the provided gameId from the prompt.
                        The returned game object contains id: the gameId, turn: who's turn it is,
                        humanPlayer1: if false, you are PLAYER_1, humanPlayer2: if false, you are PLAYER_2,
                        winner: the winner, board: a 2-dimensional node-array of the current board state.
                        A node can have the player attribute set, which means
                        the node contains a stone of that respective player.
                        
                        Try your best to win the game! Good luck!
                        """)
                .defaultAdvisors(new PromptChatMemoryAdvisor(chatMemory),
                        new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()))
                .defaultFunctions("loadGame", "makeMove")
                .build();
    }

    public Flux<String> chat(Long gameId, String userMessageContent) {
        return this.chatClient.prompt()
                .user(userMessageContent)
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, gameId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .stream()
                .content();
    }
}