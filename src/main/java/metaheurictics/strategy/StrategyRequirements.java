package metaheurictics.strategy;

import java.util.List;
import java.util.Objects;
import problem.definition.Problem;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

/** Validaciones compartidas para operaciones que trabajan con estados evaluados. */
public final class StrategyRequirements {

    private StrategyRequirements() {
        // Utilidad sin estado.
    }

    /** Exige que la estrategia tenga un problema con objetivo definido. */
    public static ProblemType requireProblemType() {
        Problem problem = Strategy.getStrategy().getProblem();
        if (problem == null) {
            throw new IllegalStateException("Strategy no tiene un Problem configurado");
        }
        ProblemType type = problem.getTypeProblem();
        if (type == null) {
            throw new IllegalStateException("El Problem no tiene tipo de objetivo configurado");
        }
        return type;
    }

    /** Obtiene la primera evaluacion o falla con un mensaje claro. */
    public static double requireFirstEvaluation(State state) {
        Objects.requireNonNull(state, "state");
        List<Double> evaluation = state.getEvaluation();
        if (evaluation == null || evaluation.isEmpty() || evaluation.get(0) == null) {
            throw new IllegalArgumentException("El State debe tener al menos una evaluacion valida");
        }
        return evaluation.get(0);
    }
}
