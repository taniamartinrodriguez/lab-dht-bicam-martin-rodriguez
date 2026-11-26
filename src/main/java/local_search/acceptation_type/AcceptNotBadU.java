package local_search.acceptation_type;

import metaheurictics.strategy.Strategy;
import metaheurictics.strategy.StrategyRequirements;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

/** Aceptacion por umbral; conserva la comparacion estricta original. */
public class AcceptNotBadU extends AcceptableCandidate {

    @Override
    public Boolean acceptCandidate(State stateCurrent, State stateCandidate) {
        ProblemType type = StrategyRequirements.requireProblemType();
        double current = StrategyRequirements.requireFirstEvaluation(stateCurrent);
        double candidate = StrategyRequirements.requireFirstEvaluation(stateCandidate);
        double threshold = Strategy.getStrategy().getThreshold();
        if (!Double.isFinite(threshold)) {
            throw new IllegalStateException("El umbral de aceptacion debe ser finito");
        }

        // Mantiene la condicion del codigo previo; no incluye el umbral exacto.
        double difference = current - candidate;
        return type == ProblemType.Maximizar
                ? difference < threshold
                : difference > threshold;
    }
}
