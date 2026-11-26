package local_search.candidate_type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import metaheurictics.strategy.Strategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import problem.definition.ObjetiveFunction;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;
import problem.extension.TypeSolutionMethod;

/** Seleccion de estados no dominados y validacion de la configuracion. */
class NotDominatedCandidateTest {
    private final NotDominatedCandidate selector = new NotDominatedCandidate();
    private Problem previousProblem;

    @BeforeEach
    void save() {
        previousProblem = Strategy.getStrategy().getProblem();
    }

    @AfterEach
    void restore() {
        Strategy.getStrategy().setProblem(previousProblem);
    }

    private static State state(double first, double second) {
        return new State(new ArrayList<>(List.of((Object) first, second)));
    }

    private static void configure(ProblemType type) {
        Problem problem = new Problem();
        problem.setTypeProblem(type);
        ArrayList<ObjetiveFunction> objectives = new ArrayList<>();
        for (int index = 0; index < 2; index++) {
            final int item = index;
            objectives.add(new ObjetiveFunction() {
                @Override public Double Evaluation(State value) {
                    return (Double) value.getCode().get(item);
                }
            });
            objectives.get(index).setTypeProblem(type);
        }
        problem.setFunction(objectives);
        problem.setTypeSolutionMethod(TypeSolutionMethod.MultiObjetivoPuro);
        Strategy.getStrategy().setProblem(problem);
    }

    @Test
    void returnsTheOnlyNeighborWithoutGlobalConfiguration() throws Exception {
        Strategy.getStrategy().setProblem(null);
        State only = state(1, 2);
        assertSame(only, selector.stateSearch(List.of(only)));
    }

    @Test
    void rejectsNullNeighborhood() {
        assertThrows(IllegalArgumentException.class, () -> selector.stateSearch(null));
    }

    @Test
    void rejectsEmptyNeighborhood() {
        assertThrows(IllegalArgumentException.class, () -> selector.stateSearch(List.of()));
    }

    @Test
    void rejectsNullNeighbor() {
        assertThrows(IllegalArgumentException.class,
                () -> selector.stateSearch(Arrays.asList(state(1, 1), null)));
    }

    @Test
    void rejectsMissingProblemForMultipleNeighbors() {
        Strategy.getStrategy().setProblem(null);
        assertThrows(IllegalStateException.class,
                () -> selector.stateSearch(List.of(state(1, 1), state(2, 2))));
    }

    @Test
    void rejectsProblemWithNoObjectiveType() {
        Strategy.getStrategy().setProblem(new Problem());
        assertThrows(IllegalStateException.class,
                () -> selector.stateSearch(List.of(state(1, 1), state(2, 2))));
    }

    @Test
    void rejectsProblemWithNoFunctions() {
        Problem problem = new Problem();
        problem.setTypeProblem(ProblemType.Maximizar);
        Strategy.getStrategy().setProblem(problem);
        assertThrows(IllegalStateException.class,
                () -> selector.stateSearch(List.of(state(1, 1), state(2, 2))));
    }

    @Test
    void selectsDominatingSecondStateWhenMaximizing() throws Exception {
        configure(ProblemType.Maximizar);
        State first = state(1, 1);
        State second = state(3, 3);
        assertSame(second, selector.stateSearch(List.of(first, second)));
        assertEquals(List.of(3.0, 3.0), second.getEvaluation());
    }

    @Test
    void selectsBestOfThreeSuccessiveDominatingStates() throws Exception {
        configure(ProblemType.Maximizar);
        State last = state(3, 3);
        assertSame(last, selector.stateSearch(List.of(state(1, 1), state(2, 2), last)));
    }

    @Test
    void keepsFirstWhenFirstDominatesOthers() throws Exception {
        configure(ProblemType.Maximizar);
        State best = state(3, 3);
        assertSame(best, selector.stateSearch(List.of(best, state(2, 2), state(1, 1))));
    }

    @Test
    void keepsFirstOnIncomparableObjectives() throws Exception {
        configure(ProblemType.Maximizar);
        State first = state(3, 1);
        assertSame(first, selector.stateSearch(List.of(first, state(1, 3))));
    }

    @Test
    void keepsFirstForEqualEvaluations() throws Exception {
        configure(ProblemType.Maximizar);
        State first = state(2, 2);
        assertSame(first, selector.stateSearch(List.of(first, state(2, 2))));
    }

    @Test
    void selectsLowerValuedStateWhenMinimizing() throws Exception {
        configure(ProblemType.Minimizar);
        State best = state(1, 1);
        assertSame(best, selector.stateSearch(List.of(state(2, 2), best)));
    }
}
