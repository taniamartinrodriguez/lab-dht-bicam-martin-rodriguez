package evolutionary_algorithms.complement;

import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import problem.definition.State;

/** Mutación de permutaciones mediante intercambio de dos posiciones. */
public class TowPointsMutation extends Mutation {
    @Override
    public State mutation(State state, double PM) {
        if (PM < 0 || PM > 1 || Double.isNaN(PM)) {
            throw new IllegalArgumentException("PM debe estar entre 0 y 1");
        }
        int count = state.getCode().size();
        if (count < 2 || ThreadLocalRandom.current().nextDouble() >= PM) return state;
        int first = ThreadLocalRandom.current().nextInt(count);
        int second = ThreadLocalRandom.current().nextInt(count - 1);
        if (second >= first) second++;
        Collections.swap(state.getCode(), first, second);
        return state;
    }
}
