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

class AcceptNotBadUTest {
    private Problem previousProblem;
    private double previousThreshold;
    private final AcceptNotBadU acceptance = new AcceptNotBadU();

    @BeforeEach
    void setUp() {
        previousProblem = Strategy.getStrategy().getProblem();
        previousThreshold = Strategy.getStrategy().getThreshold();
        Strategy.getStrategy().setThreshold(0.0);
    }

    @AfterEach
    void tearDown() {
        Strategy.getStrategy().setProblem(previousProblem);
        Strategy.getStrategy().setThreshold(previousThreshold);
    }

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
    void maximizingAcceptsImprovement() {
        objective(ProblemType.Maximizar);
        assertTrue(acceptance.acceptCandidate(state(1), state(2)));
    }

    @Test
    void maximizingRejectsWorsening() {
        objective(ProblemType.Maximizar);
        assertFalse(acceptance.acceptCandidate(state(2), state(1)));
    }

    @Test
    void minimizingAcceptsImprovement() {
        objective(ProblemType.Minimizar);
        assertTrue(acceptance.acceptCandidate(state(2), state(1)));
    }

    @Test
    void minimizingRejectsWorsening() {
        objective(ProblemType.Minimizar);
        assertFalse(acceptance.acceptCandidate(state(1), state(2)));
    }

    @Test
    void equalityUsesOriginalStrictThresholdComparison() {
        objective(ProblemType.Maximizar);
        assertFalse(acceptance.acceptCandidate(state(2), state(2)));
        objective(ProblemType.Minimizar);
        assertFalse(acceptance.acceptCandidate(state(2), state(2)));
    }

    @Test
    void nonZeroThresholdIsAppliedInBothModes() {
        Strategy.getStrategy().setThreshold(2.0);
        objective(ProblemType.Maximizar);
        assertTrue(acceptance.acceptCandidate(state(3), state(2)));
        objective(ProblemType.Minimizar);
        assertFalse(acceptance.acceptCandidate(state(3), state(2)));
    }

    @Test
    void nonFiniteThresholdIsRejected() {
        objective(ProblemType.Maximizar);
        Strategy.getStrategy().setThreshold(Double.NaN);
        assertThrows(IllegalStateException.class, () -> acceptance.acceptCandidate(state(1), state(2)));
    }

    @Test
    void missingProblemIsRejected() {
        Strategy.getStrategy().setProblem(null);
        assertThrows(IllegalStateException.class, () -> acceptance.acceptCandidate(state(1), state(2)));
    }

    @Test
    void missingEvaluationsAreRejected() {
        objective(ProblemType.Maximizar);
        assertThrows(IllegalArgumentException.class, () -> acceptance.acceptCandidate(state(1), new State()));
        assertThrows(IllegalArgumentException.class, () -> acceptance.acceptCandidate(new State(), state(1)));
    }
}
