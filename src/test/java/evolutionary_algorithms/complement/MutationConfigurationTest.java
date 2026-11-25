package evolutionary_algorithms.complement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import config.tspDynamic.TSPState;
import java.util.ArrayList;
import java.util.List;
import metaheurictics.strategy.Strategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import problem.definition.Codification;
import problem.definition.Problem;
import problem.definition.State;

/** Regresiones de las precondiciones detectadas por SonarCloud. */
class MutationConfigurationTest {
    private Problem previousProblem;

    @BeforeEach
    void saveConfiguration() {
        previousProblem = Strategy.getStrategy().getProblem();
    }

    @AfterEach
    void restoreConfiguration() {
        Strategy.getStrategy().setProblem(previousProblem);
        AIOMutation.path.clear();
    }

    private static Codification codificationWithAlternatingKeys() {
        return new Codification() {
            private int nextKey;

            @Override
            public boolean validState(State state) {
                return true;
            }

            @Override
            public Object getVariableAleatoryValue(int key) {
                return 99;
            }

            @Override
            public int getAleatoryKey() {
                int result = nextKey;
                nextKey = 1 - nextKey;
                return result;
            }

            @Override
            public int getVariableCount() {
                return 2;
            }
        };
    }

    private static void configure(Codification codification) {
        Problem problem = new Problem();
        problem.setCodification(codification);
        Strategy.getStrategy().setProblem(problem);
    }

    private static TSPState city(int identifier) {
        TSPState city = new TSPState();
        city.setIdCity(identifier);
        city.setValue(0);
        return city;
    }

    @Test
    void onePointMutationFailsClearlyWithoutAProblem() {
        Strategy.getStrategy().setProblem(null);
        State state = new State(new ArrayList<>(List.of(1)));
        assertThrows(IllegalStateException.class,
                () -> new OnePointMutation().mutation(state, 1.0));
    }

    @Test
    void aioMutationFailsClearlyWithoutCodification() {
        configure(null);
        State state = new State(new ArrayList<>(List.of(city(1), city(2))));
        assertThrows(IllegalStateException.class,
                () -> new AIOMutation().mutation(state, 1.0));
        assertThrows(IllegalStateException.class, AIOMutation::fillPath);
    }

    @Test
    void onePointMutationStillChangesAnExpectedPosition() {
        configure(codificationWithAlternatingKeys());
        State state = new State(new ArrayList<>(List.of(1, 2)));
        assertSame(state, new OnePointMutation().mutation(state, 1.0));
        assertEquals(99, state.getCode().get(0));
        assertEquals(2, state.getCode().get(1));
    }

    @Test
    void aioMutationStillChangesTheCityOrder() {
        configure(codificationWithAlternatingKeys());
        State state = new State(new ArrayList<>(List.of(city(10), city(20))));
        assertSame(state, new AIOMutation().mutation(state, 1.0));
        assertEquals(20, ((TSPState) state.getCode().get(0)).getIdCity());
        assertEquals(10, ((TSPState) state.getCode().get(1)).getIdCity());
    }

    @Test
    void fillPathUsesConfiguredNumberOfVariables() {
        configure(codificationWithAlternatingKeys());
        AIOMutation.fillPath();
        assertEquals(List.of(0, 1), AIOMutation.path);
    }

    @Test
    void aioMutationAlsoWorksWhenFirstKeyIsGreater() {
        Codification codification = new Codification() {
            private int calls;
            @Override public boolean validState(State state) { return true; }
            @Override public Object getVariableAleatoryValue(int key) { return key; }
            @Override public int getAleatoryKey() { return calls++ == 0 ? 1 : 0; }
            @Override public int getVariableCount() { return 2; }
        };
        configure(codification);
        State state = new State(new ArrayList<>(List.of(city(10), city(20))));
        new AIOMutation().mutation(state, 1.0);
        assertEquals(20, ((TSPState) state.getCode().get(0)).getIdCity());
        assertEquals(10, ((TSPState) state.getCode().get(1)).getIdCity());
    }

    @Test
    void sortedPathValueReordersCitiesByValue() {
        TSPState first = city(1);
        first.setValue(10);
        TSPState second = city(2);
        second.setValue(2);
        State state = new State(new ArrayList<>(List.of(first, second)));
        new AIOMutation().sortedPathValue(state);
        assertSame(second, state.getCode().get(0));
        assertSame(first, state.getCode().get(1));
    }

    @Test
    void onePointMutationRejectsInvalidIndex() {
        Codification codification = new Codification() {
            @Override public boolean validState(State state) { return true; }
            @Override public Object getVariableAleatoryValue(int key) { return key; }
            @Override public int getAleatoryKey() { return 9; }
            @Override public int getVariableCount() { return 2; }
        };
        configure(codification);
        State state = new State(new ArrayList<>(List.of(1, 2)));
        assertThrows(IllegalArgumentException.class,
                () -> new OnePointMutation().mutation(state, 1.0));
    }
}
