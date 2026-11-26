package evolutionary_algorithms.complement;
import metaheurictics.strategy.StrategyRequirements;

import metaheurictics.strategy.Strategy;
import problem.definition.Codification;
import problem.definition.Problem;

/** Precondiciones comunes para las mutaciones que dependen de Strategy. */
final class MutationConfiguration {
    private MutationConfiguration() {
        // Clase de utilidad, no instanciable.
    }

    static Codification requireCodification() {
        Problem problem = StrategyRequirements.requireProblem();
        if (problem == null) {
            throw new IllegalStateException(
                    "No se ha configurado un Problem en Strategy antes de mutar");
        }
        Codification codification = problem.getCodification();
        if (codification == null) {
            throw new IllegalStateException(
                    "El Problem de Strategy no tiene Codification configurada");
        }
        return codification;
    }
}
