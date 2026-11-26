package problem.definition;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import metaheurictics.strategy.Strategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import problem.extension.TypeSolutionMethod;

/** Verifica las rutas de minimizacion de los dos metodos multiobjetivo. */
class ObjectiveMinimizationCoverageTest {
    private Problem previous;

    @BeforeEach
    void before() { previous = Strategy.getStrategy().getProblem(); }

    @AfterEach
    void after() { Strategy.getStrategy().setProblem(previous); }

    private static ObjetiveFunction constant(double value, Problem.ProblemType type, float weight) {
        ObjetiveFunction objective = new ObjetiveFunction() {
            @Override public Double Evaluation(State state) { return value; }
        };
        objective.setTypeProblem(type);
        objective.setWeight(weight);
        return objective;
    }

    @Test
    void pureMultiObjectiveMinimizationTransformsMaximizationObjective() throws Exception {
        Problem p = new Problem();
        p.setTypeProblem(Problem.ProblemType.Minimizar);
        p.setTypeSolutionMethod(TypeSolutionMethod.MultiObjetivoPuro);
        p.setFunction(new ArrayList<>(List.of(
                constant(0.3, Problem.ProblemType.Maximizar, 1f),
                constant(0.6, Problem.ProblemType.Minimizar, 1f))));
        Strategy.getStrategy().setProblem(p);
        State state = new State();
        p.Evaluate(state);
        assertEquals(0.7, state.getEvaluation().get(0), 1e-8);
        assertEquals(0.6, state.getEvaluation().get(1), 1e-8);
    }

    @Test
    void weightedMinimizationAppliesBothWeightBranches() throws Exception {
        Problem p = new Problem();
        p.setTypeProblem(Problem.ProblemType.Minimizar);
        p.setTypeSolutionMethod(TypeSolutionMethod.FactoresPonderados);
        p.setFunction(new ArrayList<>(List.of(
                constant(0.2, Problem.ProblemType.Maximizar, 0.25f),
                constant(0.4, Problem.ProblemType.Minimizar, 0.75f))));
        Strategy.getStrategy().setProblem(p);
        State state = new State();
        p.Evaluate(state);
        assertEquals(0.5, state.getEvaluation().get(0), 1e-8);
    }
}
