package metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import local_search.complement.TabuSolutions;
import metaheurictics.strategy.Strategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import evolutionary_algorithms.complement.CrossoverType;
import evolutionary_algorithms.complement.MutationType;
import evolutionary_algorithms.complement.SelectionType;
import problem.definition.Operator;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

/** Recorridos adicionales que verifican algoritmos antes de importar cobertura a SonarCloud. */
class QualityGateAdditionalPathsTest {
    private Problem oldProblem;
    private State oldBest;
    private int oldCount;
    private SortedMap<GeneratorType, Generator> oldMap;
    private Generator oldActive;
    private Generator[] oldList;
    private List<State> oldTabu;
    private List<State> oldRandomStates;
    private int oldSwarms;
    private int oldParticlesPerSwarm;
    private State[] oldLocalBests;
    private int oldRestartCount;
    private int oldRestartCurrent;
    private int oldParticleRefCount;
    private int oldParticleCount;
    private SelectionType oldEvolutionSelection;
    private MutationType oldEvolutionMutation;
    private double oldEvolutionProbability;
    private int oldEvolutionTruncation;
    private SelectionType oldGeneticSelection;
    private MutationType oldGeneticMutation;
    private CrossoverType oldGeneticCrossover;
    private double oldGeneticPC;
    private double oldGeneticPM;
    private int oldGeneticTruncation;

    private static State state(int code, double value) {
        State result = new State(new ArrayList<>(List.of(code, code + 1)));
        result.setEvaluation(new ArrayList<>(List.of(value)));
        return result;
    }

    @BeforeEach
    void initialize() {
        Strategy strategy = Strategy.getStrategy();
        oldProblem = strategy.getProblem();
        oldBest = strategy.getBestState();
        oldCount = strategy.getCountCurrent();
        oldMap = strategy.mapGenerators;
        oldActive = MultiGenerator.activeGenerator;
        oldList = MultiGenerator.getListGenerators();
        oldTabu = new ArrayList<>(TabuSolutions.listTabu);
        oldRandomStates = new ArrayList<>(RandomSearch.listStateReference);
        oldSwarms = ParticleSwarmOptimization.coutSwarm;
        oldParticlesPerSwarm = ParticleSwarmOptimization.countParticleBySwarm;
        oldLocalBests = ParticleSwarmOptimization.lBest;
        oldRestartCount = HillClimbingRestart.count;
        oldRestartCurrent = HillClimbingRestart.countCurrent;
        oldParticleRefCount = ParticleSwarmOptimization.countRef;
        oldParticleCount = ParticleSwarmOptimization.countParticle;
        oldEvolutionSelection = EvolutionStrategies.selectionType;
        oldEvolutionMutation = EvolutionStrategies.mutationType;
        oldEvolutionProbability = EvolutionStrategies.PM;
        oldEvolutionTruncation = EvolutionStrategies.truncation;
        oldGeneticSelection = GeneticAlgorithm.selectionType;
        oldGeneticMutation = GeneticAlgorithm.mutationType;
        oldGeneticCrossover = GeneticAlgorithm.crossoverType;
        oldGeneticPC = GeneticAlgorithm.PC;
        oldGeneticPM = GeneticAlgorithm.PM;
        oldGeneticTruncation = GeneticAlgorithm.truncation;

        Problem problem = new Problem();
        problem.setTypeProblem(ProblemType.Maximizar);
        problem.setState(state(1, 1.0));
        problem.setOperator(new Operator() {
            @Override
            public List<State> generatedNewState(State current, Integer amount) {
                return List.of(state(10, 10.0));
            }
            @Override
            public List<State> generateRandomState(Integer amount) {
                return List.of(state(20, 20.0));
            }
        });
        strategy.setProblem(problem);
        strategy.setBestState(state(1, 5.0));
        strategy.setCountCurrent(0);
        strategy.mapGenerators = new TreeMap<>();
        MultiGenerator.activeGenerator = null;
        TabuSolutions.listTabu.clear();
        RandomSearch.listStateReference.clear();
    }

    @AfterEach
    void restore() {
        Strategy strategy = Strategy.getStrategy();
        strategy.setProblem(oldProblem);
        strategy.setBestState(oldBest);
        strategy.setCountCurrent(oldCount);
        strategy.mapGenerators = oldMap;
        MultiGenerator.setActiveGenerator(oldActive);
        MultiGenerator.setListGenerators(oldList);
        TabuSolutions.listTabu.clear();
        TabuSolutions.listTabu.addAll(oldTabu);
        RandomSearch.listStateReference.clear();
        RandomSearch.listStateReference.addAll(oldRandomStates);
        ParticleSwarmOptimization.coutSwarm = oldSwarms;
        ParticleSwarmOptimization.countParticleBySwarm = oldParticlesPerSwarm;
        ParticleSwarmOptimization.lBest = oldLocalBests;
        HillClimbingRestart.count = oldRestartCount;
        HillClimbingRestart.countCurrent = oldRestartCurrent;
        ParticleSwarmOptimization.countRef = oldParticleRefCount;
        ParticleSwarmOptimization.countParticle = oldParticleCount;
        EvolutionStrategies.selectionType = oldEvolutionSelection;
        EvolutionStrategies.mutationType = oldEvolutionMutation;
        EvolutionStrategies.PM = oldEvolutionProbability;
        EvolutionStrategies.truncation = oldEvolutionTruncation;
        GeneticAlgorithm.selectionType = oldGeneticSelection;
        GeneticAlgorithm.mutationType = oldGeneticMutation;
        GeneticAlgorithm.crossoverType = oldGeneticCrossover;
        GeneticAlgorithm.PC = oldGeneticPC;
        GeneticAlgorithm.PM = oldGeneticPM;
        GeneticAlgorithm.truncation = oldGeneticTruncation;
    }

    @Test
    void restartingHillClimbingGeneratesValidNeighbor() throws Exception {
        HillClimbingRestart.count = Integer.MAX_VALUE;
        HillClimbingRestart generator = new HillClimbingRestart();
        generator.setInitialReference(state(1, 1.0));
        assertEquals(10.0, generator.generate(1).getEvaluation().get(0));
    }

    @Test
    void multiobjectiveTabuGeneratesCandidate() throws Exception {
        MultiobjectiveTabuSearch generator = new MultiobjectiveTabuSearch();
        generator.setInitialReference(state(1, 1.0));
        assertEquals(10.0, generator.generate(1).getEvaluation().get(0));
    }

    @Test
    void particleSwarmBestSelectionMaximizes() {
        ParticleSwarmOptimization.coutSwarm = 3;
        ParticleSwarmOptimization.countParticleBySwarm = 0;
        ParticleSwarmOptimization algorithm = new ParticleSwarmOptimization();
        ParticleSwarmOptimization.lBest = new State[] {
                state(1, 2.0), state(2, 8.0), state(3, 5.0)
        };
        assertEquals(8.0, algorithm.gBestInicial().getEvaluation().get(0));
    }

    @Test
    void particleSwarmBestSelectionMinimizes() {
        Strategy.getStrategy().getProblem().setTypeProblem(ProblemType.Minimizar);
        ParticleSwarmOptimization.coutSwarm = 3;
        ParticleSwarmOptimization.countParticleBySwarm = 0;
        ParticleSwarmOptimization algorithm = new ParticleSwarmOptimization();
        ParticleSwarmOptimization.lBest = new State[] {
                state(1, 2.0), state(2, 8.0), state(3, 5.0)
        };
        assertEquals(2.0, algorithm.gBestInicial().getEvaluation().get(0));
    }

    @Test
    void multiGeneratorRecognizesImprovementInMaximization() {
        HillClimbing active = new HillClimbing();
        MultiGenerator.setActiveGenerator(active);
        MultiGenerator algorithm = new MultiGenerator();
        assertTrue(algorithm.searchState(state(2, 9.0)));
        assertFalse(algorithm.searchState(state(3, 4.0)));
    }

    @Test
    void multiGeneratorRecognizesImprovementInMinimization() {
        Strategy.getStrategy().getProblem().setTypeProblem(ProblemType.Minimizar);
        Strategy.getStrategy().setBestState(state(1, 5.0));
        HillClimbing active = new HillClimbing();
        MultiGenerator.setActiveGenerator(active);
        MultiGenerator algorithm = new MultiGenerator();
        assertTrue(algorithm.searchState(state(2, 2.0)));
        assertFalse(algorithm.searchState(state(3, 9.0)));
    }

    @Test
    void multiGeneratorRewardsImprovement() {
        LimitThreshold active = new LimitThreshold();
        MultiGenerator.setActiveGenerator(active);
        MultiGenerator.setListGenerators(new Generator[]{active});
        new MultiGenerator().updateWeight(state(2, 9.0));
        assertEquals(55.0, active.getWeight(), 0.001);
    }

    @Test
    void multiGeneratorDecreasesWeightWithoutImprovement() {
        LimitThreshold active = new LimitThreshold();
        MultiGenerator.setActiveGenerator(active);
        MultiGenerator.setListGenerators(new Generator[]{active});
        new MultiGenerator().updateWeight(state(2, 4.0));
        assertEquals(45.0, active.getWeight(), 0.001);
    }

    @Test
    void evolutionStrategiesGeneratesFromConfiguredPopulation() throws Exception {
        EvolutionStrategies.selectionType = SelectionType.TruncationSelection;
        EvolutionStrategies.mutationType = MutationType.TowPointsMutation;
        EvolutionStrategies.PM = 0;
        EvolutionStrategies.truncation = 2;
        EvolutionStrategies generator = new EvolutionStrategies();
        generator.setListStateReference(new ArrayList<>(List.of(state(1, 2.0), state(2, 8.0))));
        State candidate = generator.generate(1);
        assertEquals(2, candidate.getCode().size());
        assertTrue(candidate.getEvaluation().get(0) >= 2.0);
    }

    @Test
    void geneticAlgorithmGeneratesWithoutMutationOrCrossover() throws Exception {
        GeneticAlgorithm.selectionType = SelectionType.TruncationSelection;
        GeneticAlgorithm.mutationType = MutationType.TowPointsMutation;
        GeneticAlgorithm.crossoverType = CrossoverType.OnePointCrossover;
        GeneticAlgorithm.PC = 0;
        GeneticAlgorithm.PM = 0;
        GeneticAlgorithm.truncation = 2;
        GeneticAlgorithm algorithm = new GeneticAlgorithm();
        algorithm.setListState(new ArrayList<>(List.of(state(1, 2.0), state(2, 8.0))));
        State candidate = algorithm.generate(1);
        assertEquals(2, candidate.getCode().size());
        assertTrue(candidate.getEvaluation().get(0) >= 2.0);
    }
}
