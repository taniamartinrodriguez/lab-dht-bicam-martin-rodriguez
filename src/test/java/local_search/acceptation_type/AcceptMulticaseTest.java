package local_search.acceptation_type;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import metaheurictics.strategy.Strategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import problem.definition.Problem;
import problem.definition.State;

/** La aceptacion determinista por dominancia no depende del generador aleatorio. */
class AcceptMulticaseTest {
    private Problem previous;
    private List<State> previousPopulation;

    @BeforeEach
    void before() {
        Strategy strategy = Strategy.getStrategy();
        previous = strategy.getProblem();
        previousPopulation = strategy.listRefPoblacFinal;
        Problem p = new Problem();
        p.setTypeProblem(Problem.ProblemType.Maximizar);
        strategy.setProblem(p);
        strategy.listRefPoblacFinal = new ArrayList<>();
    }

    @AfterEach
    void after() {
        Strategy strategy = Strategy.getStrategy();
        strategy.setProblem(previous);
        strategy.listRefPoblacFinal = previousPopulation;
    }

    private static State state(double score) {
        State s = new State(new ArrayList<>(List.of((int) score)));
        s.setEvaluation(new ArrayList<>(List.of(score)));
        return s;
    }

    @Test
    void dominatingCandidateIsAcceptedWithProbabilityOne() {
        State current = state(1.0);
        State better = state(2.0);
        assertTrue(new AcceptMulticase().acceptCandidate(current, better));
        assertEquals(1, Strategy.getStrategy().listRefPoblacFinal.size());
        assertEquals(2.0, Strategy.getStrategy().listRefPoblacFinal.get(0).getEvaluation().get(0));
    }
}
