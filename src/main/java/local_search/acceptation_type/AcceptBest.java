package local_search.acceptation_type;

import metaheurictics.strategy.StrategyRequirements;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

/** Acepta el candidato si no empeora la evaluacion del estado actual. */
public class AcceptBest extends AcceptableCandidate {

    @Override
    public Boolean acceptCandidate(State stateCurrent, State stateCandidate) {
        ProblemType type = StrategyRequirements.requireProblemType();
        double currentValue = StrategyRequirements.requireFirstEvaluation(stateCurrent);
        double candidateValue = StrategyRequirements.requireFirstEvaluation(stateCandidate);
        if (type == ProblemType.Maximizar) {
            return candidateValue >= currentValue;
        }
        return candidateValue <= currentValue;
    }
}
