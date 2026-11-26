package metaheurictics.strategy;

import static org.junit.jupiter.api.Assertions.*;

import metaheuristics.generators.GeneratorType;
import metaheuristics.generators.HillClimbingRestart;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

/** Contratos de inicializacion: los errores de configuracion no se deben ocultar. */
class StrategyEntryContractTest {
    private Problem previous;
    private int previousRestartCount;

    @BeforeEach
    void remember() {
        previous = Strategy.getStrategy().getProblem();
        previousRestartCount = HillClimbingRestart.count;
    }

    @AfterEach
    void restore() {
        Strategy.getStrategy().setProblem(previous);
        HillClimbingRestart.count = previousRestartCount;
    }

    @Test
    void strategyRejectsMissingProblemAtPublicEntryPoint() {
        Strategy.getStrategy().setProblem(null);
        assertThrows(IllegalStateException.class,
                () -> Strategy.getStrategy().executeStrategy(1, 1, 1, GeneratorType.HillClimbing));
    }

    @Test
    void restartGeneratorRejectsMissingProblemDuringConstruction() {
        Strategy.getStrategy().setProblem(null);
        assertThrows(IllegalStateException.class, HillClimbingRestart::new);
    }

    @Test
    void restartGeneratorFailsExplicitlyIfProblemIsRemovedAfterConstruction() {
        Problem configured = new Problem();
        configured.setTypeProblem(ProblemType.Maximizar);
        Strategy.getStrategy().setProblem(configured);
        HillClimbingRestart generator = new HillClimbingRestart();
        generator.setInitialReference(new State());
        HillClimbingRestart.count = Integer.MAX_VALUE;
        Strategy.getStrategy().setProblem(null);
        assertThrows(IllegalStateException.class, () -> generator.generate(1));
    }
}
