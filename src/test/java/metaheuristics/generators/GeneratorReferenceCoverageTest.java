package metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import metaheurictics.strategy.Strategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

/** Pruebas sobre las ramas de seleccion de referencias en algoritmos poblacionales. */
class GeneratorReferenceCoverageTest {

    private Problem previousProblem;
    private SortedMap<GeneratorType, Generator> previousGenerators;

    @BeforeEach
    void save() {
        previousProblem = Strategy.getStrategy().getProblem();
        previousGenerators = Strategy.getStrategy().mapGenerators;
        Strategy.getStrategy().mapGenerators = new TreeMap<>();
    }

    @AfterEach
    void restore() {
        Strategy.getStrategy().setProblem(previousProblem);
        Strategy.getStrategy().mapGenerators = previousGenerators;
    }

    private static void setProblemType(ProblemType type) {
        Problem p = new Problem();
        p.setTypeProblem(type);
        Strategy.getStrategy().setProblem(p);
    }

    private static State state(int code, double score) {
        State s = new State(new ArrayList<>(List.of(code)));
        s.setEvaluation(new ArrayList<>(List.of(score)));
        return s;
    }

    private static List<State> references() {
        return new ArrayList<>(List.of(state(1, 4), state(2, 9), state(3, 2)));
    }

    @Test
    void geneticAlgorithmReturnsMaximum() {
        setProblemType(ProblemType.Maximizar);
        GeneticAlgorithm algorithm = new GeneticAlgorithm();
        algorithm.setListState(references());
        assertEquals(9.0, algorithm.getReference().getEvaluation().get(0));
    }

    @Test
    void geneticAlgorithmReturnsMinimum() {
        setProblemType(ProblemType.Minimizar);
        GeneticAlgorithm algorithm = new GeneticAlgorithm();
        algorithm.setListState(references());
        assertEquals(2.0, algorithm.getReference().getEvaluation().get(0));
    }

    @Test
    void geneticAlgorithmHandlesSingleReference() {
        setProblemType(ProblemType.Maximizar);
        GeneticAlgorithm algorithm = new GeneticAlgorithm();
        algorithm.setListState(List.of(state(7, 7.0)));
        assertEquals(7.0, algorithm.getReference().getEvaluation().get(0));
    }

    @Test
    void evolutionStrategiesReturnsMaximum() {
        setProblemType(ProblemType.Maximizar);
        EvolutionStrategies algorithm = new EvolutionStrategies();
        algorithm.setListStateReference(references());
        assertEquals(9.0, algorithm.getReference().getEvaluation().get(0));
    }

    @Test
    void evolutionStrategiesReturnsMinimum() {
        setProblemType(ProblemType.Minimizar);
        EvolutionStrategies algorithm = new EvolutionStrategies();
        algorithm.setListStateReference(references());
        assertEquals(2.0, algorithm.getReference().getEvaluation().get(0));
    }

    @Test
    void evolutionStrategiesHandlesSingleReference() {
        setProblemType(ProblemType.Maximizar);
        EvolutionStrategies algorithm = new EvolutionStrategies();
        algorithm.setListStateReference(List.of(state(7, 7.0)));
        assertEquals(7.0, algorithm.getReference().getEvaluation().get(0));
    }

    @Test
    void distributionEstimationReturnsMaximum() {
        setProblemType(ProblemType.Maximizar);
        DistributionEstimationAlgorithm algorithm = new DistributionEstimationAlgorithm();
        algorithm.setListReference(references());
        assertEquals(9.0, algorithm.getReference().getEvaluation().get(0));
    }

    @Test
    void distributionEstimationReturnsMinimum() {
        setProblemType(ProblemType.Minimizar);
        DistributionEstimationAlgorithm algorithm = new DistributionEstimationAlgorithm();
        algorithm.setListReference(references());
        assertEquals(2.0, algorithm.getReference().getEvaluation().get(0));
    }

    @Test
    void distributionEstimationHandlesSingleReference() {
        setProblemType(ProblemType.Maximizar);
        DistributionEstimationAlgorithm algorithm = new DistributionEstimationAlgorithm();
        algorithm.setListReference(List.of(state(7, 7.0)));
        assertEquals(7.0, algorithm.getReference().getEvaluation().get(0));
    }
}
