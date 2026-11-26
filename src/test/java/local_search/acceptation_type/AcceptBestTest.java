package local_search.acceptation_type;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import metaheurictics.strategy.Strategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

class AcceptBestTest {
    private Problem previous;
    private final AcceptBest acceptance = new AcceptBest();

    @BeforeEach
    void before() { previous = Strategy.getStrategy().getProblem(); }

    @AfterEach
    void after() { Strategy.getStrategy().setProblem(previous); }

    private static void objective(ProblemType type) {
        Problem problem = new Problem();
        problem.setTypeProblem(type);
        Strategy.getStrategy().setProblem(problem);
    }

    private static State state(double value) {
        State state = new State();
        state.setEvaluation(new ArrayList<>(List.of(value)));
        return state;
    }

    @Test
    void maximizingAcceptsBetter() {
        objective(ProblemType.Maximizar);
        assertTrue(acceptance.acceptCandidate(state(2), state(3)));
    }

    @Test
    void maximizingRejectsWorse() {
        objective(ProblemType.Maximizar);
        assertFalse(acceptance.acceptCandidate(state(5), state(2)));
    }

    @Test
    void minimizingAcceptsBetter() {
        objective(ProblemType.Minimizar);
        assertTrue(acceptance.acceptCandidate(state(5), state(2)));
    }

    @Test
    void minimizingRejectsWorse() {
        objective(ProblemType.Minimizar);
        assertFalse(acceptance.acceptCandidate(state(1), state(3)));
    }

    @Test
    void tiesAreAcceptedInBothModes() {
        objective(ProblemType.Maximizar);
        assertTrue(acceptance.acceptCandidate(state(2), state(2)));
        objective(ProblemType.Minimizar);
        assertTrue(acceptance.acceptCandidate(state(2), state(2)));
    }

    @Test
    void rejectsMissingProblemOrEvaluation() {
        Strategy.getStrategy().setProblem(null);
        assertThrows(IllegalStateException.class,
                () -> acceptance.acceptCandidate(state(1), state(2)));
        objective(ProblemType.Maximizar);
        assertThrows(IllegalArgumentException.class,
                () -> acceptance.acceptCandidate(new State(), state(2)));
        assertThrows(IllegalArgumentException.class,
                () -> acceptance.acceptCandidate(state(2), new State()));
    }
}
