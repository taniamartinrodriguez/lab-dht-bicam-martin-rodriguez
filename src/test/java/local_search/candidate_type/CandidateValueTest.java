package local_search.candidate_type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import local_search.complement.StrategyType;
import local_search.complement.TabuSolutions;
import metaheurictics.strategy.Strategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import problem.definition.Operator;
import problem.definition.Problem;
import problem.definition.State;

/** Pruebas de regresion del selector y de la regeneracion TABU. */
class CandidateValueTest {
    private final CandidateValue selector = new CandidateValue();
    private Problem previousProblem;
    private List<State> previousTabu;

    @BeforeEach
    void save() {
        previousProblem = Strategy.getStrategy().getProblem();
        previousTabu = new ArrayList<>(TabuSolutions.listTabu);
        TabuSolutions.listTabu.clear();
    }

    @AfterEach
    void restore() {
        Strategy.getStrategy().setProblem(previousProblem);
        TabuSolutions.listTabu.clear();
        TabuSolutions.listTabu.addAll(previousTabu);
    }

    private static State state(int code) {
        return new State(new ArrayList<>(List.of((Object) code)));
    }

    @Test
    void factoryCreatesPermittedCandidateType() throws Exception {
        assertInstanceOf(RandomCandidate.class,
                selector.newSearchCandidate(CandidateType.RandomCandidate));
    }

    @Test
    void factoryRejectsNullCandidateType() {
        assertThrows(NullPointerException.class, () -> selector.newSearchCandidate(null));
    }

    @Test
    void normalStrategySelectsOnlyState() throws Exception {
        State item = state(1);
        assertSame(item, selector.stateCandidate(item, CandidateType.RandomCandidate,
                StrategyType.NORMAL, null, List.of(item)));
    }

    @Test
    void rejectsNullNeighborhood() {
        assertThrows(IllegalArgumentException.class,
                () -> selector.stateCandidate(null, CandidateType.RandomCandidate,
                        StrategyType.NORMAL, null, null));
    }

    @Test
    void rejectsEmptyNeighborhood() {
        assertThrows(IllegalArgumentException.class,
                () -> selector.stateCandidate(null, CandidateType.RandomCandidate,
                        StrategyType.NORMAL, null, List.of()));
    }

    @Test
    void rejectsNullMemberInNeighborhood() {
        assertThrows(IllegalArgumentException.class,
                () -> selector.stateCandidate(null, CandidateType.RandomCandidate,
                        StrategyType.NORMAL, null, Arrays.asList(state(1), null)));
    }

    @Test
    void rejectsMissingStrategy() {
        assertThrows(NullPointerException.class,
                () -> selector.stateCandidate(null, CandidateType.RandomCandidate,
                        null, null, List.of(state(1))));
    }

    @Test
    void rejectsMissingCandidateType() {
        assertThrows(NullPointerException.class,
                () -> selector.stateCandidate(null, null,
                        StrategyType.NORMAL, null, List.of(state(1))));
    }

    @Test
    void tabuFiltersForbiddenNeighborsWithoutModifyingOriginal() throws Exception {
        State forbidden = state(1);
        State allowed = state(2);
        List<State> neighbors = new ArrayList<>(List.of(forbidden, allowed));
        TabuSolutions.listTabu.add(forbidden);
        assertSame(allowed, selector.stateCandidate(allowed, CandidateType.RandomCandidate,
                StrategyType.TABU, 1, neighbors));
        assertEquals(2, neighbors.size());
        assertTrue(selector.getTabusolution() != null);
    }

    @Test
    void tabuRequiresAnOperatorWhenAllCandidatesForbidden() {
        State forbidden = state(1);
        TabuSolutions.listTabu.add(forbidden);
        Strategy.getStrategy().setProblem(null);
        assertThrows(IllegalStateException.class,
                () -> selector.stateCandidate(forbidden, CandidateType.RandomCandidate,
                        StrategyType.TABU, 1, List.of(forbidden)));
    }

    @Test
    void tabuRegeneratesOnceWhenAllNeighborsForbidden() throws Exception {
        State forbidden = state(1);
        State replacement = state(2);
        TabuSolutions.listTabu.add(forbidden);
        Problem problem = new Problem();
        problem.setOperator(new Operator() {
            @Override
            public List<State> generatedNewState(State from, Integer operatorNumber) {
                assertSame(forbidden, from);
                assertEquals(1, operatorNumber);
                return List.of(replacement);
            }
            @Override
            public List<State> generateRandomState(Integer operatorNumber) {
                return List.of();
            }
        });
        Strategy.getStrategy().setProblem(problem);
        assertSame(replacement,
                selector.stateCandidate(forbidden, CandidateType.RandomCandidate,
                        StrategyType.TABU, 1, List.of(forbidden)));
    }

    @Test
    void tabuStopsWhenRegeneratedCandidatesAreStillForbidden() {
        State forbidden = state(1);
        TabuSolutions.listTabu.add(forbidden);
        Problem problem = new Problem();
        problem.setOperator(new Operator() {
            @Override
            public List<State> generatedNewState(State from, Integer operatorNumber) {
                return List.of(forbidden);
            }
            @Override
            public List<State> generateRandomState(Integer operatorNumber) {
                return List.of();
            }
        });
        Strategy.getStrategy().setProblem(problem);
        assertThrows(IllegalStateException.class,
                () -> selector.stateCandidate(forbidden, CandidateType.RandomCandidate,
                        StrategyType.TABU, 1, List.of(forbidden)));
    }
}
