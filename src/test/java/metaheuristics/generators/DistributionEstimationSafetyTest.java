package metaheuristics.generators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import problem.definition.State;

/** El algoritmo no debe indexar una poblacion vacia o evaluaciones nulas. */
class DistributionEstimationSafetyTest {
    private static State state(double score) {
        State s = new State();
        s.setEvaluation(new ArrayList<>(List.of(score)));
        return s;
    }

    @Test
    void maxValueRejectsEmptyPopulation() {
        DistributionEstimationAlgorithm eda = new DistributionEstimationAlgorithm();
        assertThrows(IllegalArgumentException.class, () -> eda.MaxValue(List.of()));
    }

    @Test
    void maxValueRejectsMissingEvaluation() {
        DistributionEstimationAlgorithm eda = new DistributionEstimationAlgorithm();
        assertThrows(IllegalArgumentException.class, () -> eda.MaxValue(List.of(new State())));
    }

    @Test
    void maxValueReturnsBestValue() {
        DistributionEstimationAlgorithm eda = new DistributionEstimationAlgorithm();
        State best = eda.MaxValue(List.of(state(1.0), state(5.0), state(3.0)));
        assertEquals(5.0, best.getEvaluation().get(0));
    }
}
