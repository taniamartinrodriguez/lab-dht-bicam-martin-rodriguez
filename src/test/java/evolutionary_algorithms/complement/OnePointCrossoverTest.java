package evolutionary_algorithms.complement;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import problem.definition.State;

class OnePointCrossoverTest {
    private final OnePointCrossover crossover = new OnePointCrossover();
    private State state(Object... genes) { return new State(new ArrayList<>(List.of(genes))); }

    @Test void probabilidadCeroNoCruzaNiDevuelveMismoObjeto() {
        State first = state(1, 1, 1, 1);
        State result = crossover.crossover(first, state(0, 0, 0, 0), 0);
        assertEquals(first.getCode(), result.getCode());
        assertNotSame(first, result);
    }

    @Test void probabilidadUnoProduceUnCorteInterior() {
        State first = state(1, 1, 1, 1, 1);
        State second = state(0, 0, 0, 0, 0);
        for (int repetition = 0; repetition < 30; repetition++) {
            State result = crossover.crossover(first, second, 1);
            assertTrue(result.getCode().contains(0));
            assertTrue(result.getCode().contains(1));
            assertEquals(List.of(1, 1, 1, 1, 1), first.getCode());
        }
    }

    @Test void validaPadresYProbabilidades() {
        assertThrows(IllegalArgumentException.class, () -> crossover.crossover(state(1), state(1), -0.1));
        assertThrows(IllegalArgumentException.class, () -> crossover.crossover(state(1), state(1), 1.1));
        assertThrows(IllegalArgumentException.class, () -> crossover.crossover(state(1), state(1), Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> crossover.crossover(state(1), state(1, 0), 1));
        assertEquals(List.of(1), crossover.crossover(state(1), state(0), 1).getCode());
    }
}
