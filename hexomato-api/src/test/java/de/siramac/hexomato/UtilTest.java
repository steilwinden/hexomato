package de.siramac.hexomato;

import de.siramac.hexomato.agent.mcts.Util;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UtilTest {

    @Test
    void argMaxTest() {
        double[] array = {4.0, 2.1, 0.3, 5.0, 2.1, 3.5};
        int bestAction = Util.getArgMax(array);
        assertThat(bestAction).isEqualTo(3);
    }
}
