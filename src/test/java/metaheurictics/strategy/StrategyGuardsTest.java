package metaheurictics.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import metaheuristics.generators.Generator;
import metaheuristics.generators.GeneratorType;
import metaheuristics.generators.HillClimbing;
import problem.definition.ObjetiveFunction;
import problem.definition.Problem;
import problem.definition.State;
import problem.definition.Problem.ProblemType;

/** Regresiones para los accesos nulos identificados en el analisis de fiabilidad. */
class StrategyGuardsTest {
    private Problem previousProblem;
    private SortedMap<GeneratorType, Generator> previousGenerators;

    @BeforeEach
    void remember() {
        previousProblem = Strategy.getStrategy().getProblem();
        previousGenerators = Strategy.getStrategy().mapGenerators;
    }

    @AfterEach
    void restore() {
        Strategy.getStrategy().setProblem(previousProblem);
        Strategy.getStrategy().mapGenerators = previousGenerators;
    }

    @Test
    void rejectsProblemAbsent() {
        Strategy.getStrategy().setProblem(null);
        assertThrows(IllegalStateException.class, StrategyRequirements::requireProblem);
    }

    @Test
    void returnsConfiguredProblem() {
        Problem p = new Problem();
        Strategy.getStrategy().setProblem(p);
        assertSame(p, StrategyRequirements.requireProblem());
    }

    @Test
    void rejectsMissingOperator() {
        Strategy.getStrategy().setProblem(new Problem());
        assertThrows(IllegalStateException.class, StrategyRequirements::requireOperator);
    }

    @Test
    void rejectsMissingInitialState() {
        Strategy.getStrategy().setProblem(new Problem());
        assertThrows(IllegalStateException.class, StrategyRequirements::requireInitialState);
    }

    @Test
    void retrievesConfiguredInitialState() {
        Problem p = new Problem();
        State state = new State();
        p.setState(state);
        Strategy.getStrategy().setProblem(p);
        assertSame(state, StrategyRequirements.requireInitialState());
    }

    @Test
    void rejectsMissingObjectiveFunctions() {
        Strategy.getStrategy().setProblem(new Problem());
        assertThrows(IllegalStateException.class, StrategyRequirements::requireFunctions);
    }

    @Test
    void rejectsEmptyObjectiveFunctions() {
        Problem p = new Problem();
        p.setFunction(new ArrayList<>());
        Strategy.getStrategy().setProblem(p);
        assertThrows(IllegalStateException.class, StrategyRequirements::requireFunctions);
    }

    @Test
    void acceptsValidObjectiveFunctions() {
        Problem p = new Problem();
        ArrayList<ObjetiveFunction> functions = new ArrayList<>();
        functions.add(new ObjetiveFunction() {
            public Double Evaluation(State state) { return 1.0; }
        });
        p.setFunction(functions);
        Strategy.getStrategy().setProblem(p);
        assertSame(functions, StrategyRequirements.requireFunctions());
    }

    @Test
    void rejectsEmptyPopulation() {
        assertThrows(IllegalArgumentException.class,
                () -> StrategyRequirements.requireStates(new ArrayList<>()));
    }

    @Test
    void rejectsPopulationContainingNull() {
        assertThrows(IllegalArgumentException.class,
                () -> StrategyRequirements.requireStates(Arrays.asList(new State(), null)));
    }

    @Test
    void acceptsNonEmptyPopulation() {
        List<State> states = List.of(new State(), new State());
        assertSame(states, StrategyRequirements.requireStates(states));
    }

    @Test
    void getListKeySupportsUninitialisedGeneratorMap() {
        Strategy.getStrategy().mapGenerators = null;
        assertTrue(Strategy.getStrategy().getListKey().isEmpty());
    }

    @Test
    void hillClimbingRejectsMissingProblemType() {
        Strategy.getStrategy().setProblem(new Problem());
        assertThrows(IllegalStateException.class, HillClimbing::new);
    }

    @Test
    void hillClimbingSupportsConfiguredObjective() {
        Problem p = new Problem();
        p.setTypeProblem(ProblemType.Maximizar);
        Strategy.getStrategy().setProblem(p);
        HillClimbing generator = new HillClimbing();
        assertEquals(GeneratorType.HillClimbing, generator.getType());
    }
}
