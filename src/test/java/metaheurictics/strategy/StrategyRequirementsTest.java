package metaheurictics.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

class StrategyRequirementsTest {
    private Problem previous;

    @BeforeEach
    void before() {
        previous = Strategy.getStrategy().getProblem();
    }

    @AfterEach
    void after() {
        Strategy.getStrategy().setProblem(previous);
    }

    @Test
    void rejectsUnconfiguredProblem() {
        Strategy.getStrategy().setProblem(null);
        assertThrows(IllegalStateException.class, StrategyRequirements::requireProblemType);
    }

    @Test
    void rejectsUnconfiguredObjectiveType() {
        Strategy.getStrategy().setProblem(new Problem());
        assertThrows(IllegalStateException.class, StrategyRequirements::requireProblemType);
    }

    @Test
    void readsBothObjectiveTypes() {
        Problem problem = new Problem();
        problem.setTypeProblem(ProblemType.Maximizar);
        Strategy.getStrategy().setProblem(problem);
        assertEquals(ProblemType.Maximizar, StrategyRequirements.requireProblemType());
        problem.setTypeProblem(ProblemType.Minimizar);
        assertEquals(ProblemType.Minimizar, StrategyRequirements.requireProblemType());
    }

    @Test
    void rejectsNullState() {
        assertThrows(NullPointerException.class,
                () -> StrategyRequirements.requireFirstEvaluation(null));
    }

    @Test
    void rejectsStateWithoutEvaluation() {
        assertThrows(IllegalArgumentException.class,
                () -> StrategyRequirements.requireFirstEvaluation(new State()));
    }

    @Test
    void rejectsEmptyEvaluation() {
        State state = new State();
        state.setEvaluation(new ArrayList<>());
        assertThrows(IllegalArgumentException.class,
                () -> StrategyRequirements.requireFirstEvaluation(state));
    }

    @Test
    void rejectsNullEvaluationValue() {
        State state = new State();
        state.setEvaluation(new ArrayList<>(Arrays.asList((Double) null)));
        assertThrows(IllegalArgumentException.class,
                () -> StrategyRequirements.requireFirstEvaluation(state));
    }

    @Test
    void readsTheFirstEvaluation() {
        State state = new State();
        state.setEvaluation(new ArrayList<>(List.of(3.25, 10.0)));
        assertEquals(3.25, StrategyRequirements.requireFirstEvaluation(state));
    }
}
