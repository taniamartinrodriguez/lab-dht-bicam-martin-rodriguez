package evolutionary_algorithms.complement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import metaheurictics.strategy.Strategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

class SteadyStateReplaceTest {
    private Problem previous;
    private final SteadyStateReplace replacement = new SteadyStateReplace();

    @BeforeEach
    void before() {
        previous = Strategy.getStrategy().getProblem();
    }

    @AfterEach
    void after() {
        Strategy.getStrategy().setProblem(previous);
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
    void maximizingReplacesWorstAndPreservesPopulationSize() {
        objective(ProblemType.Maximizar);
        State low = state(1), high = state(9), candidate = state(5);
        List<State> population = new ArrayList<>(List.of(high, low));
        assertSame(population, replacement.replace(candidate, population));
        assertEquals(2, population.size());
        assertSame(high, population.get(0));
        assertSame(candidate, population.get(1));
    }

    @Test
    void maximizingRejectsWorseCandidate() {
        objective(ProblemType.Maximizar);
        State low = state(3), high = state(8);
        List<State> population = new ArrayList<>(List.of(low, high));
        replacement.replace(state(2), population);
        assertSame(low, population.get(0));
        assertSame(high, population.get(1));
    }

    @Test
    void maximizingAcceptsTies() {
        objective(ProblemType.Maximizar);
        State candidate = state(3);
        List<State> population = new ArrayList<>(List.of(state(3), state(8)));
        replacement.replace(candidate, population);
        assertSame(candidate, population.get(0));
    }

    @Test
    void minimizingReplacesWorst() {
        objective(ProblemType.Minimizar);
        State low = state(1), high = state(8), candidate = state(5);
        List<State> population = new ArrayList<>(List.of(low, high));
        replacement.replace(candidate, population);
        assertSame(low, population.get(0));
        assertSame(candidate, population.get(1));
    }

    @Test
    void minimizingRejectsWorseCandidate() {
        objective(ProblemType.Minimizar);
        State low = state(1), high = state(4);
        List<State> population = new ArrayList<>(List.of(low, high));
        replacement.replace(state(6), population);
        assertSame(low, population.get(0));
        assertSame(high, population.get(1));
    }

    @Test
    void minimizingAcceptsTies() {
        objective(ProblemType.Minimizar);
        State candidate = state(8);
        List<State> population = new ArrayList<>(List.of(state(1), state(8)));
        replacement.replace(candidate, population);
        assertSame(candidate, population.get(1));
    }

    @Test
    void minAndMaxHelpersFindCorrectStates() {
        State low = state(-2), high = state(8), medium = state(4);
        List<State> population = new ArrayList<>(List.of(medium, low, high));
        assertSame(low, replacement.MinValue(population));
        assertSame(high, replacement.MaxValue(population));
    }

    @Test
    void rejectEmptyAndNullPopulation() {
        objective(ProblemType.Maximizar);
        assertThrows(IllegalArgumentException.class,
                () -> replacement.replace(state(1), new ArrayList<>()));
        assertThrows(NullPointerException.class,
                () -> replacement.replace(state(1), null));
        assertThrows(IllegalArgumentException.class,
                () -> replacement.MinValue(new ArrayList<>()));
        assertThrows(IllegalArgumentException.class,
                () -> replacement.MaxValue(new ArrayList<>()));
    }

    @Test
    void rejectMissingEvaluationOnCandidateOrPopulation() {
        objective(ProblemType.Maximizar);
        assertThrows(IllegalArgumentException.class,
                () -> replacement.replace(new State(), new ArrayList<>(List.of(state(1)))));
        assertThrows(IllegalArgumentException.class,
                () -> replacement.replace(state(4), new ArrayList<>(List.of(state(1), new State()))));
    }

    @Test
    void rejectMissingStrategyProblem() {
        Strategy.getStrategy().setProblem(null);
        assertThrows(IllegalStateException.class,
                () -> replacement.replace(state(1), new ArrayList<>(List.of(state(2)))));
    }
}
