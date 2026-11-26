package local_search.acceptation_type;

import java.util.List;
import java.util.Objects;

import metaheurictics.strategy.Strategy;
import metaheurictics.strategy.StrategyRequirements;
import metaheuristics.generators.Generator;
import metaheuristics.generators.GeneratorType;
import metaheuristics.generators.MultiobjectiveHillClimbingDistance;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

/** Operaciones de dominancia de Pareto para estados multiobjetivo. */
public class Dominance {

    /**
     * Agrega el candidato si no esta repetido ni dominado por un elemento.
     * Si se agrega, retira las soluciones que el candidato domina.
     */
    public boolean ListDominance(State solutionX, List<State> list) {
        Objects.requireNonNull(list, "list");
        requireEvaluations(solutionX);
        StrategyRequirements.requireProblemType();

        // Evaluar primero: no se debe modificar el frente si X es dominado.
        for (State existing : list) {
            if (solutionX.Comparator(existing) || dominance(existing, solutionX)) {
                return false;
            }
        }

        list.removeIf(existing -> dominance(solutionX, existing));
        list.add(solutionX.clone());

        Generator generator = Strategy.getStrategy().generator;
        if (generator != null
                && generator.getType() == GeneratorType.MultiobjectiveHillClimbingDistance) {
            MultiobjectiveHillClimbingDistance.DistanceCalculateAdd(list);
        }
        return true;
    }

    /** Verdadero cuando X es al menos tan bueno como Y en todo y mejor en algo. */
    public boolean dominance(State solutionX, State solutionY) {
        ProblemType type = StrategyRequirements.requireProblemType();
        List<Double> valuesX = requireEvaluations(solutionX);
        List<Double> valuesY = requireEvaluations(solutionY);
        if (valuesX.size() != valuesY.size()) {
            throw new IllegalArgumentException("Los estados deben tener igual numero de objetivos");
        }

        boolean strictlyBetter = false;
        for (int i = 0; i < valuesX.size(); i++) {
            double x = valuesX.get(i);
            double y = valuesY.get(i);
            if (type == ProblemType.Maximizar) {
                if (x < y) return false;
                if (x > y) strictlyBetter = true;
            } else {
                if (x > y) return false;
                if (x < y) strictlyBetter = true;
            }
        }
        return strictlyBetter;
    }

    private static List<Double> requireEvaluations(State state) {
        Objects.requireNonNull(state, "state");
        List<Double> values = state.getEvaluation();
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("El estado necesita evaluaciones no vacias");
        }
        for (Double value : values) {
            if (value == null || !Double.isFinite(value)) {
                throw new IllegalArgumentException("Cada objetivo debe tener evaluacion finita");
            }
        }
        return values;
    }
}
