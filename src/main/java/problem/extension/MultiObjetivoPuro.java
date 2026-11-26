package problem.extension;
import metaheurictics.strategy.StrategyRequirements;

import java.util.ArrayList;

import metaheurictics.strategy.Strategy;

import problem.definition.ObjetiveFunction;
import problem.definition.State;
import problem.definition.Problem.ProblemType;

public class MultiObjetivoPuro extends SolutionMethod {

	@Override
	public void evaluationState(State state) {
		// TODO Auto-generated method stub
		double tempEval = -1;
		ArrayList<Double> evaluation = new ArrayList<Double>(StrategyRequirements.requireFunctions().size());
		for (int i = 0; i < StrategyRequirements.requireFunctions().size(); i++)
		{
			ObjetiveFunction objfunction = (ObjetiveFunction)StrategyRequirements.requireFunctions().get(i);
			if(StrategyRequirements.requireProblemType().equals(ProblemType.Maximizar)){
				if(objfunction.getTypeProblem().equals(ProblemType.Maximizar))
				{
					tempEval = objfunction.Evaluation(state);
				}
				else{
					tempEval = 1-objfunction.Evaluation(state);
				}
			}
			else{
				if(objfunction.getTypeProblem().equals(ProblemType.Maximizar))
				{
					tempEval = 1-objfunction.Evaluation(state);
				}
				else{
					tempEval = objfunction.Evaluation(state);
				}
			}
			evaluation.add(tempEval);
		}
		//evaluation.add( (double) -1);
		state.setEvaluation(evaluation);
	}

}
