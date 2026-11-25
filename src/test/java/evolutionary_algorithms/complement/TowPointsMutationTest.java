package evolutionary_algorithms.complement;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import problem.definition.State;

class TowPointsMutationTest {
    private final TowPointsMutation mutation = new TowPointsMutation();
    private State state(Object... genes) { return new State(new ArrayList<>(List.of(genes))); }

    @Test void ceroNoModificaYUnoIntercambiaDosGenes() {
        State original = state(0, 1, 2, 3, 4);
        assertSame(original, mutation.mutation(original, 0));
        assertEquals(List.of(0, 1, 2, 3, 4), original.getCode());
        State result = mutation.mutation(original, 1);
        assertSame(original, result);
        int changed = 0;
        for (int i = 0; i < 5; i++) if (!result.getCode().get(i).equals(i)) changed++;
        assertEquals(2, changed);
        assertTrue(result.getCode().containsAll(List.of(0, 1, 2, 3, 4)));
    }

    @Test void unaVariableNoSePuedeIntercambiar() {
        State solo = state(1);
        assertSame(solo, mutation.mutation(solo, 1));
        assertEquals(List.of(1), solo.getCode());
    }

    @Test void probabilidadDebeEstarEnRango() {
        assertThrows(IllegalArgumentException.class, () -> mutation.mutation(state(1, 2), -1));
        assertThrows(IllegalArgumentException.class, () -> mutation.mutation(state(1, 2), 2));
        assertThrows(IllegalArgumentException.class, () -> mutation.mutation(state(1, 2), Double.NaN));
    }
}
