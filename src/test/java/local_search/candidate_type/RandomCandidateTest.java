package local_search.candidate_type;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import problem.definition.State;

class RandomCandidateTest {
    private final RandomCandidate selector = new RandomCandidate();

    @Test void puedeSeleccionarElUltimoIndividuo() {
        State first = new State();
        State last = new State();
        List<State> states = List.of(first, last);
        boolean seenLast = false;
        for (int i = 0; i < 200; i++) {
            State selected = selector.stateSearch(states);
            assertTrue(selected == first || selected == last);
            if (selected == last) seenLast = true;
        }
        assertTrue(seenLast, "El último candidato también debe poder salir");
    }

    @Test void casosLimite() {
        State one = new State();
        assertSame(one, selector.stateSearch(List.of(one)));
        assertThrows(IllegalArgumentException.class, () -> selector.stateSearch(List.of()));
        assertThrows(IllegalArgumentException.class, () -> selector.stateSearch(null));
    }
}
