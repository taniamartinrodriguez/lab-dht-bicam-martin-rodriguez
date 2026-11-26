package metaheurictics.strategy;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import metaheuristics.generators.GeneratorType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import problem.definition.Codification;
import problem.definition.ObjetiveFunction;
import problem.definition.Operator;
import problem.definition.Problem;
import problem.definition.State;
import problem_operators.MutationOperator;

/** Pruebas adicionales de rutas de validacion incorporadas en el paso anterior. */
class AdditionalGuardCoverageTest {
    private Problem previous;
    private java.util.SortedMap<GeneratorType, metaheuristics.generators.Generator> previousMap;

    @BeforeEach
    void save() {
        previous = Strategy.getStrategy().getProblem();
        previousMap = Strategy.getStrategy().mapGenerators;
    }

    @AfterEach
    void restore() {
        Strategy.getStrategy().setProblem(previous);
        Strategy.getStrategy().mapGenerators = previousMap;
    }

    private static Codification codification() {
        return new Codification() {
            public boolean validState(State state) { return state != null; }
            public Object getVariableAleatoryValue(int key) { return 9; }
            public int getAleatoryKey() { return 0; }
            public int getVariableCount() { return 2; }
        };
    }

    @Test
    void missingCodificationHasDescriptiveFailure() {
        Strategy.getStrategy().setProblem(new Problem());
        assertThrows(IllegalStateException.class, StrategyRequirements::requireCodification);
    }

    @Test
    void validCodificationIsReturned() {
        Problem p = new Problem();
        Codification cod = codification();
        p.setCodification(cod);
        Strategy.getStrategy().setProblem(p);
        assertSame(cod, StrategyRequirements.requireCodification());
    }

    @Test
    void validOperatorIsReturned() {
        Operator operator = new MutationOperator();
        Problem p = new Problem();
        p.setOperator(operator);
        Strategy.getStrategy().setProblem(p);
        assertSame(operator, StrategyRequirements.requireOperator());
    }

    @Test
    void nullPopulationIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> StrategyRequirements.requireStates(null));
    }

    @Test
    void objectiveListContainingNullIsRejected() {
        Problem p = new Problem();
        ArrayList<ObjetiveFunction> functions = new ArrayList<>();
        functions.add(null);
        p.setFunction(functions);
        Strategy.getStrategy().setProblem(p);
        assertThrows(IllegalStateException.class, StrategyRequirements::requireFunctions);
    }

    @Test
    void generatorKeysAreReadFromExistingMap() {
        Strategy.getStrategy().mapGenerators = new TreeMap<>();
        Strategy.getStrategy().mapGenerators.put(GeneratorType.RandomSearch, null);
        Strategy.getStrategy().mapGenerators.put(GeneratorType.HillClimbing, null);
        assertEquals(List.of("HillClimbing", "RandomSearch"), Strategy.getStrategy().getListKey());
    }

    @Test
    void mutationOperatorKeepsOriginalStateAndCreatesNeighbours() {
        Problem p = new Problem();
        p.setCodification(codification());
        Strategy.getStrategy().setProblem(p);
        State original = new State(new ArrayList<>(List.of(1, 2)));
        List<State> neighbours = new MutationOperator().generatedNewState(original, 2);
        assertEquals(2, neighbours.size());
        assertEquals(List.of(1, 2), original.getCode());
        for (State neighbour : neighbours) {
            assertEquals(List.of(9, 2), neighbour.getCode());
            assertNotSame(original, neighbour);
        }
    }

    @Test
    void mutationOperatorCanReturnZeroNeighbours() {
        Strategy.getStrategy().setProblem(new Problem());
        assertTrue(new MutationOperator().generatedNewState(new State(), 0).isEmpty());
    }

    @Test
    void problemRejectsNullStateBeforeEvaluation() {
        assertThrows(IllegalArgumentException.class, () -> new Problem().Evaluate(null));
    }

    @Test
    void problemRejectsMissingObjectivesBeforeEvaluation() {
        assertThrows(IllegalStateException.class, () -> new Problem().Evaluate(new State()));
    }
}
