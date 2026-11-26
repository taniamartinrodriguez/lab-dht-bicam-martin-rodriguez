package local_search.acceptation_type;

import metaheurictics.strategy.StrategyRequirements;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

/** Acepta un candidato cuando su primera evaluacion no empeora el objetivo. */
public class AcceptNotBad extends AcceptableCandidate {

    @Override
    public Boolean acceptCandidate(State stateCurrent, State stateCandidate) {
        ProblemType type = StrategyRequirements.requireProblemType();
        double current = StrategyRequirements.requireFirstEvaluation(stateCurrent);
        double candidate = StrategyRequirements.requireFirstEvaluation(stateCandidate);

        return type == ProblemType.Maximizar
                ? candidate >= current
                : candidate <= current;
    }
}
