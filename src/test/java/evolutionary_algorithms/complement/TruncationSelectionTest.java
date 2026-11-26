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

class TruncationSelectionTest {
    private Problem previous;
    private final TruncationSelection selection = new TruncationSelection();

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
    void maximizingSelectsTwoHighest() {
        objective(ProblemType.Maximizar);
        State high = state(9), medium = state(5), low = state(1);
        List<State> population = new ArrayList<>(List.of(low, high, medium));
        List<State> selected = selection.selection(population, 2);
        assertEquals(2, selected.size());
        assertSame(high, selected.get(0));
        assertSame(medium, selected.get(1));
        assertSame(low, population.get(2));
    }

    @Test
    void minimizingSelectsTwoLowest() {
        objective(ProblemType.Minimizar);
        State high = state(9), medium = state(5), low = state(1);
        List<State> population = new ArrayList<>(List.of(high, low, medium));
        List<State> selected = selection.selection(population, 2);
        assertSame(low, selected.get(0));
        assertSame(medium, selected.get(1));
        assertSame(high, population.get(2));
    }

    @Test
    void bothOrderMethodsKeepSameListInstance() {
        State high = state(9), low = state(1);
        List<State> population = new ArrayList<>(List.of(low, high));
        assertSame(population, selection.OrderBetter(population));
        assertSame(high, population.get(0));
        assertSame(population, selection.ascOrderBetter(population));
        assertSame(low, population.get(0));
    }

    @Test
    void retainsStableOrderForTies() {
        objective(ProblemType.Maximizar);
        State first = state(3), second = state(3);
        List<State> selected = selection.selection(new ArrayList<>(List.of(first, second)), 2);
        assertSame(first, selected.get(0));
        assertSame(second, selected.get(1));
    }

    @Test
    void zeroTruncationReturnsEmptyList() {
        objective(ProblemType.Minimizar);
        assertEquals(0, selection.selection(new ArrayList<>(List.of(state(1))), 0).size());
    }

    @Test
    void rejectsInvalidTruncation() {
        objective(ProblemType.Maximizar);
        List<State> population = new ArrayList<>(List.of(state(1)));
        assertThrows(IllegalArgumentException.class, () -> selection.selection(population, -1));
        assertThrows(IllegalArgumentException.class, () -> selection.selection(population, 2));
        assertThrows(NullPointerException.class, () -> selection.selection(null, 0));
    }

    @Test
    void rejectsMissingObjectiveType() {
        Strategy.getStrategy().setProblem(new Problem());
        assertThrows(IllegalStateException.class,
                () -> selection.selection(new ArrayList<>(List.of(state(1))), 1));
    }

    @Test
    void rejectsInvalidEvaluationsDuringSorting() {
        objective(ProblemType.Maximizar);
        assertThrows(IllegalArgumentException.class,
                () -> selection.selection(new ArrayList<>(List.of(state(1), new State())), 1));
    }
}
