package problem.definition;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import factory_interface.IFFactorySolutionMethod;
import factory_method.FactorySolutionMethod;
import metaheurictics.strategy.Strategy;
import org.junit.jupiter.api.Test;
import problem.extension.FactoresPonderados;
import problem.extension.MultiObjetivoPuro;
import problem.extension.SolutionMethod;
import problem.extension.TypeSolutionMethod;

/** Pruebas de las propiedades, la evaluación y la selección de métodos de Problem. */
class ProblemTest {

    private static ObjetiveFunction funcionConstante(double resultado,
            Problem.ProblemType tipo, float peso) {
        ObjetiveFunction funcion = new ObjetiveFunction() {
            @Override
            public Double Evaluation(State state) {
                return resultado;
            }
        };
        funcion.setTypeProblem(tipo);
        funcion.setWeight(peso);
        return funcion;
    }

    @Test
    void evaluarAplicaFuncionObjetivo() throws Exception {
        Problem problem = new Problem();
        problem.setFunction(new ArrayList<>(List.of(new ObjetiveFunction() {
            @Override
            public Double Evaluation(State state) {
                return (double) state.getCode().size();
            }
        })));
        problem.setTypeProblem(Problem.ProblemType.Maximizar);
        State state = new State(new ArrayList<>(List.of(1, 0, 1)));
        problem.Evaluate(state);
        assertEquals(List.of(3.0), state.getEvaluation());
        assertEquals(Problem.ProblemType.Maximizar, problem.getTypeProblem());
    }

    @Test
    void propiedadesSeGuardanYSeRecuperanCorrectamente() {
        Problem problem = new Problem();
        ArrayList<ObjetiveFunction> funciones = new ArrayList<>(List.of(
                funcionConstante(4.0, Problem.ProblemType.Minimizar, 1.0f)));
        State estado = new State(new ArrayList<>(List.of(1, 2)));
        Codification codificacion = new Codification() {
            @Override public boolean validState(State state) { return true; }
            @Override public Object getVariableAleatoryValue(int key) { return key; }
            @Override public int getAleatoryKey() { return 0; }
            @Override public int getVariableCount() { return 2; }
        };
        Operator operador = new Operator() {
            @Override
            public List<State> generatedNewState(State state, Integer numero) {
                return List.of(state);
            }
            @Override
            public List<State> generateRandomState(Integer numero) {
                return List.of();
            }
        };
        IFFactorySolutionMethod fabrica = new FactorySolutionMethod();

        problem.setFunction(funciones);
        problem.setState(estado);
        problem.setTypeProblem(Problem.ProblemType.Minimizar);
        problem.setCodification(codificacion);
        problem.setOperator(operador);
        problem.setPossibleValue(9);
        problem.setTypeSolutionMethod(TypeSolutionMethod.FactoresPonderados);
        problem.setFactorySolutionMethod(fabrica);

        assertSame(funciones, problem.getFunction());
        assertSame(estado, problem.getState());
        assertEquals(Problem.ProblemType.Minimizar, problem.getTypeProblem());
        assertSame(codificacion, problem.getCodification());
        assertSame(operador, problem.getOperator());
        assertEquals(9, problem.getPossibleValue());
        assertEquals(TypeSolutionMethod.FactoresPonderados,
                problem.getTypeSolutionMethod());
        assertSame(fabrica, problem.getFactorySolutionMethod());
    }

    @Test
    void fabricaCreaLosDosTiposDeMetodoDeSolucion() throws Exception {
        Problem problem = new Problem();

        SolutionMethod multiobjetivo = problem.newSolutionMethod(
                TypeSolutionMethod.MultiObjetivoPuro);
        assertInstanceOf(MultiObjetivoPuro.class, multiobjetivo);
        assertInstanceOf(FactorySolutionMethod.class, problem.getFactorySolutionMethod());

        SolutionMethod ponderado = problem.newSolutionMethod(
                TypeSolutionMethod.FactoresPonderados);
        assertInstanceOf(FactoresPonderados.class, ponderado);
        assertNotSame(multiobjetivo, ponderado);
    }

    @Test
    void evaluarEnModoMultiobjetivoGeneraUnValorPorFuncion() throws Exception {
        Problem problem = new Problem();
        problem.setTypeProblem(Problem.ProblemType.Maximizar);
        problem.setTypeSolutionMethod(TypeSolutionMethod.MultiObjetivoPuro);
        problem.setFunction(new ArrayList<>(List.of(
                funcionConstante(0.2, Problem.ProblemType.Maximizar, 1.0f),
                funcionConstante(0.7, Problem.ProblemType.Minimizar, 1.0f))));
        State estado = new State(new ArrayList<>(List.of(1, 0)));

        // Los métodos de solución originales consultan la estrategia global.
        Strategy estrategia = Strategy.getStrategy();
        Problem anterior = estrategia.getProblem();
        try {
            estrategia.setProblem(problem);
            problem.Evaluate(estado);
            assertEquals(2, estado.getEvaluation().size());
            assertEquals(0.2, estado.getEvaluation().get(0), 1e-9);
            assertEquals(0.3, estado.getEvaluation().get(1), 1e-9);
        } finally {
            estrategia.setProblem(anterior);
        }
    }

    @Test
    void evaluarEnModoPonderadoCombinaLosPesos() throws Exception {
        Problem problem = new Problem();
        problem.setTypeProblem(Problem.ProblemType.Maximizar);
        problem.setTypeSolutionMethod(TypeSolutionMethod.FactoresPonderados);
        problem.setFunction(new ArrayList<>(List.of(
                funcionConstante(0.5, Problem.ProblemType.Maximizar, 0.25f),
                funcionConstante(0.2, Problem.ProblemType.Minimizar, 0.75f))));
        State estado = new State(new ArrayList<>(List.of(1, 0)));

        Strategy estrategia = Strategy.getStrategy();
        Problem anterior = estrategia.getProblem();
        try {
            estrategia.setProblem(problem);
            problem.Evaluate(estado);
            assertEquals(1, estado.getEvaluation().size());
            // 0.5 * 0.25 + (1 - 0.2) * 0.75 = 0.725
            assertEquals(0.725, estado.getEvaluation().get(0), 1e-9);
        } finally {
            estrategia.setProblem(anterior);
        }
    }
}
