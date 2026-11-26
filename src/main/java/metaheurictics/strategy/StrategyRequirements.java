package metaheurictics.strategy;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import problem.definition.Problem;
import problem.definition.Operator;
import problem.definition.Codification;
import problem.definition.ObjetiveFunction;
import problem.definition.Problem.ProblemType;
import problem.definition.State;

/** Validaciones compartidas para operaciones que trabajan con estados evaluados. */
public final class StrategyRequirements {

    private StrategyRequirements() {
        // Utilidad sin estado.
    }

    /** Devuelve un problema configurado, o falla inmediatamente con una causa comprensible. */
    public static Problem requireProblem() {
        Problem problem = Strategy.getStrategy().getProblem();
        if (problem == null) {
            throw new IllegalStateException("Strategy no tiene un Problem configurado");
        }
        return problem;
    }

    /** Devuelve la codificacion del sentido de optimizacion. */
    public static ProblemType requireProblemType() {
        ProblemType type = requireProblem().getTypeProblem();
        if (type == null) {
            throw new IllegalStateException("El Problem no tiene tipo de objetivo configurado");
        }
        return type;
    }

    /** Impide generar vecinos sin un operador valido. */
    public static Operator requireOperator() {
        Operator operator = requireProblem().getOperator();
        if (operator == null) {
            throw new IllegalStateException("El Problem no tiene Operator configurado");
        }
        return operator;
    }

    /** Impide utilizar una codificacion no configurada. */
    public static Codification requireCodification() {
        Codification codification = requireProblem().getCodification();
        if (codification == null) {
            throw new IllegalStateException("El Problem no tiene Codification configurada");
        }
        return codification;
    }

    /** Impide clonar un estado inicial inexistente. */
    public static State requireInitialState() {
        State state = requireProblem().getState();
        if (state == null) {
            throw new IllegalStateException("El Problem no tiene State inicial configurado");
        }
        return state;
    }

    /** Exige una lista no vacia de funciones objetivo. */
    public static ArrayList<ObjetiveFunction> requireFunctions() {
        ArrayList<ObjetiveFunction> functions = requireProblem().getFunction();
        if (functions == null || functions.isEmpty() || functions.get(0) == null) {
            throw new IllegalStateException("El Problem no tiene funciones objetivo validas");
        }
        return functions;
    }

    /** Impide indexar poblaciones vacias o con elementos nulos. */
    public static List<State> requireStates(List<State> states) {
        if (states == null || states.isEmpty()) {
            throw new IllegalArgumentException("La poblacion debe contener estados validos");
        }
        for (State state : states) {
            if (state == null) {
                throw new IllegalArgumentException("La poblacion contiene un State nulo");
            }
        }
        return states;
    }

    /** Obtiene la primera evaluacion o falla con un mensaje claro. */
    public static double requireFirstEvaluation(State state) {
        Objects.requireNonNull(state, "state");
        List<Double> evaluation = state.getEvaluation();
        if (evaluation == null || evaluation.isEmpty() || evaluation.get(0) == null) {
            throw new IllegalArgumentException("El State debe tener al menos una evaluacion valida");
        }
        return evaluation.get(0);
    }
}
