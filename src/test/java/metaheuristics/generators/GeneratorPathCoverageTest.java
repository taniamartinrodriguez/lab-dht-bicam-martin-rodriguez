package metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import local_search.complement.TabuSolutions;
import metaheurictics.strategy.Strategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import problem.definition.Operator;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

/** Recorridos de generacion con un operador determinista, sin aleatoriedad en las aserciones. */
class GeneratorPathCoverageTest {
    private Problem previousProblem;
    private List<State> previousTabu;

    private static State scored(int code, double score) {
        State state = new State(new ArrayList<>(List.of(code)));
        state.setEvaluation(new ArrayList<>(List.of(score)));
        return state;
    }

    @BeforeEach
    void setup() {
        previousProblem = Strategy.getStrategy().getProblem();
        previousTabu = new ArrayList<>(TabuSolutions.listTabu);
        TabuSolutions.listTabu.clear();
        Problem problem = new Problem();
        problem.setTypeProblem(ProblemType.Maximizar);
        problem.setOperator(new Operator() {
            @Override
            public List<State> generatedNewState(State current, Integer count) {
                return List.of(scored(99, 9.0));
            }
            @Override
            public List<State> generateRandomState(Integer count) {
                return List.of(scored(77, 7.0));
            }
        });
        Strategy.getStrategy().setProblem(problem);
    }

    @AfterEach
    void restore() {
        Strategy.getStrategy().setProblem(previousProblem);
        TabuSolutions.listTabu.clear();
        TabuSolutions.listTabu.addAll(previousTabu);
    }

    @Test
    void randomSearchGeneratesEvaluatedCandidate() throws Exception {
        RandomSearch algorithm = new RandomSearch();
        algorithm.setInitialReference(scored(1, 1.0));
        State result = algorithm.generate(1);
        assertEquals(7.0, result.getEvaluation().get(0));
    }

    @Test
    void hillClimbingGeneratesHigherCandidate() throws Exception {
        HillClimbing algorithm = new HillClimbing();
        algorithm.setInitialReference(scored(1, 1.0));
        State result = algorithm.generate(1);
        assertEquals(9.0, result.getEvaluation().get(0));
    }

    @Test
    void hillClimbingSupportsMinimization() throws Exception {
        Strategy.getStrategy().getProblem().setTypeProblem(ProblemType.Minimizar);
        HillClimbing algorithm = new HillClimbing();
        algorithm.setInitialReference(scored(1, 10.0));
        assertEquals(9.0, algorithm.generate(1).getEvaluation().get(0));
    }

    @Test
    void limitThresholdGeneratesCandidate() throws Exception {
        LimitThreshold algorithm = new LimitThreshold();
        algorithm.setInitialReference(scored(1, 1.0));
        assertEquals(9.0, algorithm.generate(1).getEvaluation().get(0));
    }

    @Test
    void limitThresholdSupportsMinimization() throws Exception {
        Strategy.getStrategy().getProblem().setTypeProblem(ProblemType.Minimizar);
        LimitThreshold algorithm = new LimitThreshold();
        algorithm.setInitialReference(scored(1, 10.0));
        assertEquals(9.0, algorithm.generate(1).getEvaluation().get(0));
    }

    @Test
    void simulatedAnnealingGeneratesCandidate() throws Exception {
        SimulatedAnnealing algorithm = new SimulatedAnnealing();
        algorithm.setInitialReference(scored(1, 1.0));
        assertEquals(9.0, algorithm.generate(1).getEvaluation().get(0));
    }

    @Test
    void tabuSearchGeneratesCandidate() throws Exception {
        TabuSearch algorithm = new TabuSearch();
        algorithm.setInitialReference(scored(1, 1.0));
        assertEquals(9.0, algorithm.generate(1).getEvaluation().get(0));
    }

    @Test
    void tabuSearchSupportsMinimization() throws Exception {
        Strategy.getStrategy().getProblem().setTypeProblem(ProblemType.Minimizar);
        TabuSearch algorithm = new TabuSearch();
        algorithm.setInitialReference(scored(1, 10.0));
        assertEquals(9.0, algorithm.generate(1).getEvaluation().get(0));
    }

    @Test
    void multiCaseAnnealingGeneratesCandidate() throws Exception {
        MultiCaseSimulatedAnnealing algorithm = new MultiCaseSimulatedAnnealing();
        algorithm.setInitialReference(scored(1, 1.0));
        assertEquals(9.0, algorithm.generate(1).getEvaluation().get(0));
    }

    @Test
    void multiobjectiveHillClimbingRestartGeneratesCandidate() throws Exception {
        MultiobjectiveHillClimbingRestart algorithm = new MultiobjectiveHillClimbingRestart();
        algorithm.setInitialReference(scored(1, 1.0));
        assertEquals(9.0, algorithm.generate(1).getEvaluation().get(0));
    }

    @Test
    void multiobjectiveHillClimbingDistanceGeneratesCandidate() throws Exception {
        MultiobjectiveHillClimbingDistance algorithm = new MultiobjectiveHillClimbingDistance();
        algorithm.setInitialReference(scored(1, 1.0));
        assertEquals(9.0, algorithm.generate(1).getEvaluation().get(0));
    }

    @Test
    void multiobjectiveHillClimbingGeneratesCandidate() throws Exception {
        MultiobjectiveStochasticHillClimbing algorithm = new MultiobjectiveStochasticHillClimbing();
        algorithm.setInitialReference(scored(1, 1.0));
        assertEquals(9.0, algorithm.generate(1).getEvaluation().get(0));
    }
}
