package local_search.candidate_type;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import problem.definition.State;

/** Selecciona de manera uniforme cualquier vecino, incluido el último. */
public class RandomCandidate extends SearchCandidate {
    @Override
    public State stateSearch(List<State> listNeighborhood) {
        if (listNeighborhood == null || listNeighborhood.isEmpty()) {
            throw new IllegalArgumentException("Vecindario vacío");
        }
        return listNeighborhood.get(ThreadLocalRandom.current().nextInt(listNeighborhood.size()));
    }
}
