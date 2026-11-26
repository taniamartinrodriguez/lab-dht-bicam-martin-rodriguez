package problem.extension;
import metaheurictics.strategy.StrategyRequirements;

import java.util.ArrayList;

import metaheurictics.strategy.Strategy;

import problem.definition.State;
import problem.definition.Problem.ProblemType;

public class FactoresPonderados extends SolutionMethod {

	@Override
	public void evaluationState(State state) {
		// TODO Auto-generated method stub
		double eval = 0;       
		double tempWeight = 0;	
		ArrayList<Double> evaluation = new ArrayList<Double>(StrategyRequirements.requireFunctions().size());
		
		for (int i = 0; i < StrategyRequirements.requireFunctions().size(); i++) {

			tempWeight = 0;
			if(StrategyRequirements.requireProblemType().equals(ProblemType.Maximizar)){
				if(StrategyRequirements.requireFunctions().get(i).getTypeProblem().equals(ProblemType.Maximizar)){
					tempWeight = StrategyRequirements.requireFunctions().get(i).Evaluation(state);
					tempWeight = tempWeight * StrategyRequirements.requireFunctions().get(i).getWeight();
				}
				else{
					tempWeight = 1 - StrategyRequirements.requireFunctions().get(i).Evaluation(state);
					tempWeight = tempWeight * StrategyRequirements.requireFunctions().get(i).getWeight();
				}
			}
			else{
				if(StrategyRequirements.requireFunctions().get(i).getTypeProblem().equals(ProblemType.Maximizar)){
					tempWeight = 1 - StrategyRequirements.requireFunctions().get(i).Evaluation(state);
					tempWeight = tempWeight * StrategyRequirements.requireFunctions().get(i).getWeight();
				}
				else{
					tempWeight = StrategyRequirements.requireFunctions().get(i).Evaluation(state);
					tempWeight = tempWeight * StrategyRequirements.requireFunctions().get(i).getWeight();
				}
			}
			eval += tempWeight;
		}
		evaluation.add(evaluation.size(), eval);
		state.setEvaluation(evaluation);
		
	}

}
