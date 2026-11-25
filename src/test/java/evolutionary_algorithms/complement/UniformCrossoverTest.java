package evolutionary_algorithms.complement;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import problem.definition.State;

class UniformCrossoverTest {
    private final UniformCrossover crossover = new UniformCrossover();
    private State state(Object... genes) { return new State(new ArrayList<>(List.of(genes))); }

    @Test void mascaraEsIndependientePorPosicion() {
        int[] mask = crossover.mascara(128);
        assertEquals(128, mask.length);
        Set<Integer> seen = new HashSet<>();
        for (int bit : mask) {
            assertTrue(bit == 0 || bit == 1);
            seen.add(bit);
        }
        assertEquals(Set.of(0, 1), seen);
        assertEquals(0, crossover.mascara(0).length);
        assertThrows(IllegalArgumentException.class, () -> crossover.mascara(-1));
    }

    @Test void noCruzaCuandoLaProbabilidadEsCero() {
        assertEquals(List.of(1, 1), crossover.crossover(state(1, 1), state(0, 0), 0).getCode());
    }

    @Test void cruceUniformeNoInventaGenesNiModificaPadres() {
        State first = state(1, 1, 1, 1, 1);
        State second = state(0, 0, 0, 0, 0);
        State child = crossover.crossover(first, second, 1);
        for (Object gene : child.getCode()) assertTrue(List.of(0, 1).contains(gene));
        assertEquals(List.of(1, 1, 1, 1, 1), first.getCode());
    }

    @Test void validaElDominio() {
        assertThrows(IllegalArgumentException.class, () -> crossover.crossover(state(1), state(1), -0.01));
        assertThrows(IllegalArgumentException.class, () -> crossover.crossover(state(1), state(1), 2));
        assertThrows(IllegalArgumentException.class, () -> crossover.crossover(state(1), state(1), Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> crossover.crossover(state(1, 1), state(0), 1));
    }
}
