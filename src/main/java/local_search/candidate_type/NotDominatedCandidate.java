package local_search.candidate_type;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import local_search.acceptation_type.Dominance;
import metaheurictics.strategy.Strategy;
import metaheurictics.strategy.StrategyRequirements;
import problem.definition.Problem;
import problem.definition.State;

/** Selecciona de manera determinista un candidato no dominado del vecindario. */
public class NotDominatedCandidate extends SearchCandidate {

    @Override
    public State stateSearch(List<State> listNeighborhood)
            throws IllegalArgumentException, SecurityException, ClassNotFoundException,
            InstantiationException, IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        if (listNeighborhood == null || listNeighborhood.isEmpty()) {
            throw new IllegalArgumentException("El vecindario no puede ser nulo ni vacio");
        }
        for (State neighbor : listNeighborhood) {
            if (neighbor == null) {
                throw new IllegalArgumentException("El vecindario no puede contener estados nulos");
            }
        }
        State best = listNeighborhood.get(0);
        if (listNeighborhood.size() == 1) {
            return best;
        }

        // Validar antes de invocar Evaluate evita el acceso nulo de la incidencia
        // original y permite comunicar una configuracion incompleta claramente.
        StrategyRequirements.requireProblemType();
        Problem problem = Strategy.getStrategy().getProblem();
        if (problem.getFunction() == null || problem.getFunction().isEmpty()) {
            throw new IllegalStateException("El problema no tiene funciones objetivo configuradas");
        }
        problem.Evaluate(best);
        Dominance dominance = new Dominance();
        for (int i = 1; i < listNeighborhood.size(); i++) {
            State candidate = listNeighborhood.get(i);
            problem.Evaluate(candidate);
            if (dominance.dominance(candidate, best)) {
                best = candidate;
            }
        }
        // El algoritmo antiguo podia devolver un State vacio cuando todos los
        // candidatos siguientes dominaban al primero. Siempre devolver uno real.
        return best;
    }
}
