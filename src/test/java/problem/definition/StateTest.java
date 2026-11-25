package problem.definition;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import metaheuristics.generators.GeneratorType;
import org.junit.jupiter.api.Test;

/** Contratos básicos y regresiones de copia, clonación y distancia. */
class StateTest {
    private State state(Object... genes) { return new State(new ArrayList<>(List.of(genes))); }

    @Test void estadoVacioYSetters() {
        State state = new State();
        assertTrue(state.getCode().isEmpty());
        state.setNumber(4);
        state.setTypeGenerator(GeneratorType.RandomSearch);
        state.setEvaluation(new ArrayList<>(List.of(2.0, 4.0)));
        assertEquals(4, state.getNumber());
        assertEquals(GeneratorType.RandomSearch, state.getTypeGenerator());
        assertEquals(List.of(2.0, 4.0), state.getEvaluation());
        state.setEvaluation(null);
        assertNull(state.getEvaluation());
    }

    @Test void constructoresNoCompartenListaDeEntrada() {
        ArrayList<Object> genes = new ArrayList<>(List.of(1, 2));
        State first = new State(genes);
        genes.set(0, 99);
        assertEquals(1, first.getCode().get(0));
        first.setCode(new ArrayList<>(List.of(3, 4)));
        assertEquals(List.of(3, 4), first.getCode());
        assertThrows(NullPointerException.class, () -> new State((ArrayList<Object>) null));
        assertThrows(NullPointerException.class, () -> first.setCode(null));
    }

    @Test void cloneYGetCopySonProfundosEnLasListas() {
        State original = state(1, 0);
        original.setEvaluation(new ArrayList<>(List.of(1.5)));
        original.setNumber(3);
        original.setTypeGenerator(GeneratorType.RandomSearch);
        State copy = original.clone();
        State copy2 = (State) original.getCopy();
        State copy3 = new State(original);
        assertNotSame(original, copy);
        assertNotSame(original.getCode(), copy.getCode());
        assertNotSame(original.getEvaluation(), copy.getEvaluation());
        for (State cloned : List.of(copy, copy2, copy3)) {
            assertEquals(3, cloned.getNumber());
            assertEquals(GeneratorType.RandomSearch, cloned.getTypeGenerator());
            cloned.getCode().set(0, 9);
            cloned.getEvaluation().set(0, 10.0);
        }
        assertEquals(List.of(1, 0), original.getCode());
        assertEquals(List.of(1.5), original.getEvaluation());
        assertThrows(NullPointerException.class, () -> new State((State) null));
    }

    @Test void comparatorRevisaLosGenes() {
        State a = state(1, 0, 1);
        assertTrue(a.Comparator(state(1, 0, 1)));
        assertFalse(a.Comparator(state(1, 1, 1)));
        assertFalse(a.Comparator(null));
    }

    @Test void distanceEsHammingYExigeLongitudesIguales() {
        State a = state(1, 0, 1);
        assertEquals(0.0, a.Distance(state(1, 0, 1)));
        assertEquals(2.0, a.Distance(state(0, 0, 0)));
        assertThrows(IllegalArgumentException.class, () -> a.Distance(state(1)));
        assertThrows(NullPointerException.class, () -> a.Distance(null));
    }

    @Test void distanceSoportaGenesNulos() {
        State a = new State(new ArrayList<>());
        a.setCode(new ArrayList<>(java.util.Arrays.asList(null, 1)));
        State b = new State(new ArrayList<>(java.util.Arrays.asList(null, null)));
        assertEquals(1.0, a.Distance(b));
    }
}
