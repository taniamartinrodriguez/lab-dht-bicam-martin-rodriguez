package problem_operators;
import metaheurictics.strategy.StrategyRequirements;

import java.util.ArrayList;
import java.util.List;

import metaheurictics.strategy.Strategy;

import problem.definition.Operator;
import problem.definition.State;


public class MutationOperator extends Operator {

	public List<State> generatedNewState(State stateCurrent, Integer operatornumber){
		List<State> listNeigborhood = new ArrayList<State>();
		for (int i = 0; i < operatornumber; i++){
			int key = StrategyRequirements.requireCodification().getAleatoryKey();
			Object candidate = StrategyRequirements.requireCodification().getVariableAleatoryValue(key);
			State state = (State) stateCurrent.getCopy();
			state.getCode().set(key, candidate);
			listNeigborhood.add(state);
		}
		return listNeigborhood;
	}

	@Override
	public List<State> generateRandomState(Integer operatornumber) {
		// TODO Auto-generated method stub
		return null;
	}

}
