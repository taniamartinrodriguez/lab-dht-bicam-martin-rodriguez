package evolutionary_algorithms.complement;

import java.util.concurrent.ThreadLocalRandom;
import problem.definition.State;

/** Cruce uniforme con máscara independiente para cada gen. */
public class UniformCrossover extends Crossover {
    /** Devuelve una máscara nueva de ceros y unos. */
    public int[] mascara(int length) {
        if (length < 0) throw new IllegalArgumentException("Longitud negativa");
        int[] mask = new int[length];
        for (int i = 0; i < mask.length; i++) {
            mask[i] = ThreadLocalRandom.current().nextInt(2);
        }
        return mask;
    }

    @Override
    public State crossover(State father1, State father2, double PC) {
        if (PC < 0 || PC > 1 || Double.isNaN(PC)) {
            throw new IllegalArgumentException("PC debe estar entre 0 y 1");
        }
        if (father1.getCode().size() != father2.getCode().size()) {
            throw new IllegalArgumentException("Padres con longitudes diferentes");
        }
        State result = father1.clone();
        if (ThreadLocalRandom.current().nextDouble() >= PC) return result;
        int[] mask = mascara(father1.getCode().size());
        for (int i = 0; i < mask.length; i++) {
            if (mask[i] == 0) result.getCode().set(i, father2.getCode().get(i));
        }
        return result;
    }
}
