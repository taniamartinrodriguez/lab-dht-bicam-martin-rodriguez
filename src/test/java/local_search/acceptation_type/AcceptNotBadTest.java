package local_search.acceptation_type;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import metaheurictics.strategy.Strategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

class AcceptNotBadTest {
    private Problem previous;
    private final AcceptNotBad acceptance = new AcceptNotBad();

    @BeforeEach
    void setUp() { previous = Strategy.getStrategy().getProblem(); }

    @AfterEach
    void tearDown() { Strategy.getStrategy().setProblem(previous); }

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
        assertFalse(acceptance.acceptCandidate(state(3), state(2)));
    }

    @Test
    void minimizingAcceptsBetter() {
        objective(ProblemType.Minimizar);
        assertTrue(acceptance.acceptCandidate(state(3), state(2)));
    }

    @Test
    void minimizingRejectsWorse() {
        objective(ProblemType.Minimizar);
        assertFalse(acceptance.acceptCandidate(state(2), state(3)));
    }

    @Test
    void tiesAcceptedWithoutConfiguredFunctionList() {
        objective(ProblemType.Maximizar);
        assertTrue(acceptance.acceptCandidate(state(3), state(3)));
        objective(ProblemType.Minimizar);
        assertTrue(acceptance.acceptCandidate(state(3), state(3)));
    }

    @Test
    void missingProblemAndObjectiveAreRejected() {
        Strategy.getStrategy().setProblem(null);
        assertThrows(IllegalStateException.class, () -> acceptance.acceptCandidate(state(1), state(2)));
        Strategy.getStrategy().setProblem(new Problem());
        assertThrows(IllegalStateException.class, () -> acceptance.acceptCandidate(state(1), state(2)));
    }

    @Test
    void missingEvaluationsAreRejected() {
        objective(ProblemType.Maximizar);
        assertThrows(IllegalArgumentException.class, () -> acceptance.acceptCandidate(new State(), state(2)));
        assertThrows(IllegalArgumentException.class, () -> acceptance.acceptCandidate(state(2), new State()));
    }
}
