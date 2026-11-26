package evolutionary_algorithms.complement;

import java.util.List;
import java.util.Objects;
import metaheurictics.strategy.StrategyRequirements;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

/** Reemplaza el peor individuo de la poblacion si el candidato no es peor. */
public class SteadyStateReplace extends Replace {

    @Override
    public List<State> replace(State stateCandidate, List<State> listState) {
        Objects.requireNonNull(listState, "listState");
        ProblemType type = StrategyRequirements.requireProblemType();
        double candidateValue = StrategyRequirements.requireFirstEvaluation(stateCandidate);
        if (listState.isEmpty()) {
            throw new IllegalArgumentException("La poblacion no puede estar vacia");
        }

        State stateToReplace = type == ProblemType.Maximizar
                ? MinValue(listState) : MaxValue(listState);
        double worstValue = StrategyRequirements.requireFirstEvaluation(stateToReplace);
        boolean candidateIsBetter = type == ProblemType.Maximizar
                ? candidateValue >= worstValue : candidateValue <= worstValue;

        if (candidateIsBetter) {
            // Se conserva el indice del peor individuo, sin cambiar el tamano de la lista.
            for (int i = 0; i < listState.size(); i++) {
                if (listState.get(i) == stateToReplace) {
                    listState.set(i, stateCandidate);
                    break;
                }
            }
        }
        return listState;
    }

    /** Individuo con menor evaluacion (sin modificar la lista). */
    public State MinValue(List<State> listState) {
        return extreme(listState, false);
    }

    /** Individuo con mayor evaluacion (sin modificar la lista). */
    public State MaxValue(List<State> listState) {
        return extreme(listState, true);
    }

    private State extreme(List<State> listState, boolean maximum) {
        Objects.requireNonNull(listState, "listState");
        if (listState.isEmpty()) {
            throw new IllegalArgumentException("La poblacion no puede estar vacia");
        }
        State selected = listState.get(0);
        double selectedValue = StrategyRequirements.requireFirstEvaluation(selected);
        for (int i = 1; i < listState.size(); i++) {
            State current = listState.get(i);
            double value = StrategyRequirements.requireFirstEvaluation(current);
            if ((maximum && value > selectedValue) || (!maximum && value < selectedValue)) {
                selected = current;
                selectedValue = value;
            }
        }
        return selected;
    }
}
