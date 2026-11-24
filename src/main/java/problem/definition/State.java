package problem.definition;

import java.util.ArrayList;
import java.util.Objects;
import metaheuristics.generators.GeneratorType;

/** Estado de búsqueda de BiCIAM: codificación, evaluación y procedencia. */
public class State implements Cloneable {
    protected GeneratorType typeGenerator;
    protected ArrayList<Double> evaluation;
    protected int number;
    protected ArrayList<Object> code;

    /** Construye una copia independiente de las listas del estado origen. */
    public State(State source) {
        Objects.requireNonNull(source, "source");
        typeGenerator = source.typeGenerator;
        evaluation = source.evaluation == null ? null : new ArrayList<>(source.evaluation);
        number = source.number;
        code = new ArrayList<>(source.code);
    }

    /** Crea un estado a partir de una codificación. */
    public State(ArrayList<Object> code) {
        this.code = new ArrayList<>(Objects.requireNonNull(code, "code"));
    }

    /** Crea un estado vacío. */
    public State() {
        code = new ArrayList<>();
    }

    public ArrayList<Object> getCode() { return code; }
    public void setCode(ArrayList<Object> listCode) {
        code = new ArrayList<>(Objects.requireNonNull(listCode, "listCode"));
    }
    public GeneratorType getTypeGenerator() { return typeGenerator; }
    public void setTypeGenerator(GeneratorType value) { typeGenerator = value; }
    public ArrayList<Double> getEvaluation() { return evaluation; }
    public void setEvaluation(ArrayList<Double> value) {
        evaluation = value == null ? null : new ArrayList<>(value);
    }
    public int getNumber() { return number; }
    public void setNumber(int value) { number = value; }

    /** Evita el aliasing del clone() antiguo, que devolvía this. */
    @Override
    public State clone() { return new State(this); }

    /** Copia completa sin compartir listas mutables. */
    public Object getCopy() { return new State(this); }

    /** Comprueba la equivalencia de la codificación. */
    public boolean Comparator(State state) {
        return state != null && code.equals(state.getCode());
    }

    /** Distancia de Hamming entre estados de igual longitud. */
    public double Distance(State state) {
        Objects.requireNonNull(state, "state");
        if (code.size() != state.code.size()) {
            throw new IllegalArgumentException("Los estados deben tener igual longitud");
        }
        int distance = 0;
        for (int i = 0; i < code.size(); i++) {
            if (!Objects.equals(code.get(i), state.code.get(i))) distance++;
        }
        return distance;
    }
}
