package evolutionary_algorithms.complement;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import problem.definition.State;

/** Cruce en un punto real (antes la posición era siempre cero por precedencia). */
public class OnePointCrossover extends Crossover {
    @Override
    public State crossover(State father1, State father2, double PC) {
        if (PC < 0.0 || PC > 1.0 || Double.isNaN(PC)) {
            throw new IllegalArgumentException("PC debe estar entre 0 y 1");
        }
        int size = father1.getCode().size();
        if (size != father2.getCode().size()) {
            throw new IllegalArgumentException("Padres con longitudes diferentes");
        }
        State child = father1.clone();
        if (size < 2 || ThreadLocalRandom.current().nextDouble() >= PC) {
            return child;
        }
        int cut = ThreadLocalRandom.current().nextInt(1, size);
        boolean startWithFirst = ThreadLocalRandom.current().nextBoolean();
        ArrayList<Object> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add((i < cut) == startWithFirst
                    ? father1.getCode().get(i) : father2.getCode().get(i));
        }
        child.setCode(result);
        return child;
    }
}
