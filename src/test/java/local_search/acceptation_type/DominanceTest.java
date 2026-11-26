package local_search.acceptation_type;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import metaheurictics.strategy.Strategy;
import metaheuristics.generators.Generator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

class DominanceTest {
    private Problem previousProblem;
    private Generator previousGenerator;
    private final Dominance dominance = new Dominance();

    @BeforeEach
    void setUp() {
        previousProblem = Strategy.getStrategy().getProblem();
        previousGenerator = Strategy.getStrategy().generator;
        Strategy.getStrategy().generator = null;
        objective(ProblemType.Maximizar);
    }

    @AfterEach
    void tearDown() {
        Strategy.getStrategy().setProblem(previousProblem);
        Strategy.getStrategy().generator = previousGenerator;
    }

    private static void objective(ProblemType type) {
        Problem problem = new Problem();
        problem.setTypeProblem(type);
        Strategy.getStrategy().setProblem(problem);
    }

    private static State state(String code, double... values) {
        State state = new State(new ArrayList<>(List.of((Object) code)));
        ArrayList<Double> evaluations = new ArrayList<>();
        for (double value : values) evaluations.add(value);
        state.setEvaluation(evaluations);
        return state;
    }

    @Test
    void maximizationDominatesIfStrictlyBetterInOneDimension() {
        assertTrue(dominance.dominance(state("x", 4, 3), state("y", 2, 3)));
    }

    @Test
    void maximizationRejectsWorseDimension() {
        assertFalse(dominance.dominance(state("x", 4, 1), state("y", 2, 3)));
    }

    @Test
    void minimizationDominatesIfStrictlyBetterInOneDimension() {
        objective(ProblemType.Minimizar);
        assertTrue(dominance.dominance(state("x", 1, 2), state("y", 1, 4)));
    }

    @Test
    void minimizationRejectsWorseDimension() {
        objective(ProblemType.Minimizar);
        assertFalse(dominance.dominance(state("x", 1, 4), state("y", 1, 2)));
    }

    @Test
    void equalEvaluationNeverStrictlyDominates() {
        assertFalse(dominance.dominance(state("x", 2, 3), state("y", 2, 3)));
    }

    @Test
    void incomparableStatesDoNotDominateEachOther() {
        State x = state("x", 2, 1);
        State y = state("y", 1, 2);
        assertFalse(dominance.dominance(x, y));
        assertFalse(dominance.dominance(y, x));
    }

    @Test
    void mismatchedNumberOfObjectivesIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> dominance.dominance(state("x", 2, 1), state("y", 1)));
    }

    @Test
    void missingEvaluationsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> dominance.dominance(new State(), state("y", 2, 3)));
        State empty = state("x");
        assertThrows(IllegalArgumentException.class,
                () -> dominance.dominance(empty, state("y", 2, 3)));
    }

    @Test
    void nullAndNanEvaluationsAreRejected() {
        State missing = state("x", 1);
        missing.setEvaluation(new ArrayList<>(Arrays.asList((Double) null)));
        assertThrows(IllegalArgumentException.class,
                () -> dominance.dominance(missing, state("y", 2)));
        assertThrows(IllegalArgumentException.class,
                () -> dominance.dominance(state("x", Double.NaN), state("y", 2)));
    }

    @Test
    void missingProblemIsRejected() {
        Strategy.getStrategy().setProblem(null);
        assertThrows(IllegalStateException.class,
                () -> dominance.dominance(state("x", 1), state("y", 2)));
    }

    @Test
    void canAddFirstParetoStateWithoutGenerator() {
        List<State> front = new ArrayList<>();
        State x = state("x", 3, 3);
        assertTrue(dominance.ListDominance(x, front));
        assertEquals(1, front.size());
        assertNotSame(x, front.get(0));
        assertEquals(x.getEvaluation(), front.get(0).getEvaluation());
    }

    @Test
    void rejectsDominatedCandidateWithoutMutatingFront() {
        State strong = state("strong", 4, 4);
        List<State> front = new ArrayList<>(List.of(strong));
        assertFalse(dominance.ListDominance(state("weak", 1, 1), front));
        assertEquals(1, front.size());
        assertSame(strong, front.get(0));
    }

    @Test
    void removesAllDominatedCandidatesSafely() {
        List<State> front = new ArrayList<>(List.of(state("one", 1, 1), state("two", 2, 2)));
        assertTrue(dominance.ListDominance(state("strong", 3, 3), front));
        assertEquals(1, front.size());
        assertEquals("strong", front.get(0).getCode().get(0));
    }

    @Test
    void keepsIncomparableParetoStates() {
        List<State> front = new ArrayList<>(List.of(state("one", 3, 1)));
        assertTrue(dominance.ListDominance(state("two", 1, 3), front));
        assertEquals(2, front.size());
    }

    @Test
    void doesNotAddSameCodeTwice() {
        List<State> front = new ArrayList<>(List.of(state("same", 3, 3)));
        assertFalse(dominance.ListDominance(state("same", 4, 4), front));
        assertEquals(1, front.size());
    }

    @Test
    void invalidFrontArgumentsAreRejected() {
        assertThrows(NullPointerException.class,
                () -> dominance.ListDominance(state("x", 1), null));
        assertThrows(NullPointerException.class,
                () -> dominance.ListDominance(null, new ArrayList<>()));
    }
}
