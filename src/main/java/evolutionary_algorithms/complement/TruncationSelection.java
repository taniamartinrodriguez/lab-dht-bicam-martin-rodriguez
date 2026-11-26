package evolutionary_algorithms.complement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import metaheurictics.strategy.StrategyRequirements;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

/** Seleccion por truncamiento: primero se ordena la poblacion. */
public class TruncationSelection extends FatherSelection {

    /** Orden descendente para problemas de maximizacion. */
    public List<State> OrderBetter(List<State> listState) {
        Objects.requireNonNull(listState, "listState");
        listState.sort(Comparator.comparingDouble(StrategyRequirements::requireFirstEvaluation).reversed());
        return listState;
    }

    /** Orden ascendente para problemas de minimizacion. */
    public List<State> ascOrderBetter(List<State> listState) {
        Objects.requireNonNull(listState, "listState");
        listState.sort(Comparator.comparingDouble(StrategyRequirements::requireFirstEvaluation));
        return listState;
    }

    @Override
    public List<State> selection(List<State> listState, int truncation) {
        Objects.requireNonNull(listState, "listState");
        if (truncation < 0 || truncation > listState.size()) {
            throw new IllegalArgumentException("El numero de seleccionados debe estar entre 0 y el tamano de la poblacion");
        }
        ProblemType type = StrategyRequirements.requireProblemType();
        if (type == ProblemType.Maximizar) {
            OrderBetter(listState);
        } else {
            ascOrderBetter(listState);
        }
        return new ArrayList<>(listState.subList(0, truncation));
    }
}
