package de.siramac.hexomato.ai;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestClient;

@Configuration
public class AiConfig {

    @Bean
    CommandLineRunner ingestDocsForSpringAi(@Value("classpath:hex-rules.txt") Resource hexomatoRulesResource,
                                            VectorStore vectorStore) {
        return args -> vectorStore
                .write(new TokenTextSplitter(30, 20, 1, 10000, true)
                        .transform(new TextReader(hexomatoRulesResource).read()));
    }

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingClient) {
        return new SimpleVectorStore(embeddingClient);
    }

    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

}
