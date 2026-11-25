package evolutionary_algorithms.complement;


import java.util.Objects;
import problem.definition.Codification;
import problem.definition.State;

public class OnePointMutation extends Mutation {

	@Override
	public State mutation(State state, double PM) {
		Objects.requireNonNull(state, "state");
        double probM = Math.random();
		if(PM >= probM)
		{
			Codification codification = MutationConfiguration.requireCodification();
            int key = codification.getAleatoryKey();
            if (key < 0 || key >= state.getCode().size()) {
                throw new IllegalArgumentException("Indice de mutacion fuera de rango: " + key);
            }
            Object value = codification.getVariableAleatoryValue(key);
            state.getCode().set(key, value);
		}
		return state;
	}
}
