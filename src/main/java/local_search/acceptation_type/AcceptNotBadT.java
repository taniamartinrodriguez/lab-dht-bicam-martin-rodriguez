package local_search.acceptation_type;

import metaheurictics.strategy.StrategyRequirements;
import metaheuristics.generators.SimulatedAnnealing;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

/** Criterio probabilistico de aceptacion para recocido simulado. */
public class AcceptNotBadT extends AcceptableCandidate {

    @Override
    public Boolean acceptCandidate(State stateCurrent, State stateCandidate) {
        ProblemType type = StrategyRequirements.requireProblemType();
        double current = StrategyRequirements.requireFirstEvaluation(stateCurrent);
        double candidate = StrategyRequirements.requireFirstEvaluation(stateCandidate);

        Double initialTemperature = SimulatedAnnealing.tinitial;
        if (initialTemperature == null || !Double.isFinite(initialTemperature)
                || initialTemperature <= 0.0) {
            throw new IllegalStateException("La temperatura del recocido debe ser positiva y finita");
        }

        // Un candidato igual o mejor se acepta sin intervencion del azar.
        if (type == ProblemType.Maximizar ? candidate >= current : candidate <= current) {
            return true;
        }

        // Para ambas orientaciones un empeoramiento produce un exponente negativo.
        double improvement = type == ProblemType.Maximizar
                ? candidate - current
                : current - candidate;
        double probability = Math.exp(improvement / initialTemperature);
        return Math.random() < probability;
    }
}
