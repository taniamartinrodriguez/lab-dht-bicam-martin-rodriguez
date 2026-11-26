package local_search.acceptation_type;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import metaheurictics.strategy.Strategy;
import metaheuristics.generators.SimulatedAnnealing;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

class AcceptNotBadTTest {
    private Problem previousProblem;
    private Double previousTemperature;
    private final AcceptNotBadT acceptance = new AcceptNotBadT();

    @BeforeEach
    void setUp() {
        previousProblem = Strategy.getStrategy().getProblem();
        previousTemperature = SimulatedAnnealing.tinitial;
        SimulatedAnnealing.tinitial = 1.0;
    }

    @AfterEach
    void tearDown() {
        Strategy.getStrategy().setProblem(previousProblem);
        SimulatedAnnealing.tinitial = previousTemperature;
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
    void maximizationAlwaysAcceptsImprovement() {
        objective(ProblemType.Maximizar);
        assertTrue(acceptance.acceptCandidate(state(1), state(2)));
    }

    @Test
    void minimizationAlwaysAcceptsImprovement() {
        objective(ProblemType.Minimizar);
        assertTrue(acceptance.acceptCandidate(state(2), state(1)));
    }

    @Test
    void tiesAlwaysAccepted() {
        objective(ProblemType.Maximizar);
        assertTrue(acceptance.acceptCandidate(state(2), state(2)));
        objective(ProblemType.Minimizar);
        assertTrue(acceptance.acceptCandidate(state(2), state(2)));
    }

    @Test
    void veryBadCandidateIsRejectedInMaximization() {
        objective(ProblemType.Maximizar);
        assertFalse(acceptance.acceptCandidate(state(0), state(-1000)));
    }

    @Test
    void veryBadCandidateIsRejectedInMinimization() {
        objective(ProblemType.Minimizar);
        assertFalse(acceptance.acceptCandidate(state(0), state(1000)));
    }

    @Test
    void missingAndInvalidTemperaturesAreRejected() {
        objective(ProblemType.Maximizar);
        SimulatedAnnealing.tinitial = null;
        assertThrows(IllegalStateException.class, () -> acceptance.acceptCandidate(state(1), state(2)));
        SimulatedAnnealing.tinitial = 0.0;
        assertThrows(IllegalStateException.class, () -> acceptance.acceptCandidate(state(1), state(2)));
        SimulatedAnnealing.tinitial = -1.0;
        assertThrows(IllegalStateException.class, () -> acceptance.acceptCandidate(state(1), state(2)));
        SimulatedAnnealing.tinitial = Double.NaN;
        assertThrows(IllegalStateException.class, () -> acceptance.acceptCandidate(state(1), state(2)));
    }

    @Test
    void missingProblemIsRejected() {
        Strategy.getStrategy().setProblem(null);
        assertThrows(IllegalStateException.class, () -> acceptance.acceptCandidate(state(1), state(2)));
    }

    @Test
    void missingEvaluationIsRejected() {
        objective(ProblemType.Maximizar);
        assertThrows(IllegalArgumentException.class, () -> acceptance.acceptCandidate(new State(), state(2)));
    }
}
