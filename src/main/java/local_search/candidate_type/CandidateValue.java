package local_search.candidate_type;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import factory_interface.IFFactoryCandidate;
import factory_method.FactoryCandidate;
import local_search.complement.StrategyType;
import local_search.complement.TabuSolutions;
import metaheurictics.strategy.Strategy;
import problem.definition.Operator;
import problem.definition.Problem;
import problem.definition.State;

/** Seleccion de candidatos de un vecindario, con filtrado opcional de tabu. */
public class CandidateValue {

    private StrategyType strategy;
    private IFFactoryCandidate ifFactory;
    private CandidateType typecand;
    private TabuSolutions tabusolution;
    private SearchCandidate searchcandidate;

    public CandidateValue() {
    }

    public CandidateValue(StrategyType strategy, IFFactoryCandidate ifFactory,
            CandidateType typecand, TabuSolutions tabusolution, SearchCandidate searchcandidate) {
        this.strategy = strategy;
        this.ifFactory = ifFactory;
        this.typecand = typecand;
        this.tabusolution = tabusolution;
        this.searchcandidate = searchcandidate;
    }

    public SearchCandidate newSearchCandidate(CandidateType typecandidate)
            throws IllegalArgumentException, SecurityException, ClassNotFoundException,
            InstantiationException, IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        Objects.requireNonNull(typecandidate, "typecandidate");
        ifFactory = new FactoryCandidate();
        searchcandidate = ifFactory.createSearchCandidate(typecandidate);
        return searchcandidate;
    }

    public State stateCandidate(State stateCurrent, CandidateType typeCandidate,
            StrategyType strategy, Integer operatornumber, List<State> neighborhood)
            throws IllegalArgumentException, SecurityException, ClassNotFoundException,
            InstantiationException, IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        Objects.requireNonNull(typeCandidate, "typeCandidate");
        Objects.requireNonNull(strategy, "strategy");
        List<State> available = checkedNeighborhood(neighborhood);
        tabusolution = new TabuSolutions();

        if (strategy == StrategyType.TABU) {
            available = withoutTabu(available);
            if (available.isEmpty()) {
                // El codigo original se llamaba recursivamente sin limite cuando
                // todos los vecinos eran tabu. Regenerar una sola vez evita ese bucle.
                Problem problem = Strategy.getStrategy().getProblem();
                Operator operator = problem == null ? null : problem.getOperator();
                if (operator == null) {
                    throw new IllegalStateException(
                            "No se pueden regenerar vecinos tabu sin un Operator configurado");
                }
                List<State> generated = operator.generatedNewState(neighborhood.get(0), operatornumber);
                available = withoutTabu(checkedNeighborhood(generated));
                if (available.isEmpty()) {
                    throw new IllegalStateException(
                            "La regeneracion no ha producido candidatos fuera de la lista tabu");
                }
            }
        }
        return newSearchCandidate(typeCandidate).stateSearch(available);
    }

    private static List<State> checkedNeighborhood(List<State> neighborhood) {
        if (neighborhood == null || neighborhood.isEmpty()) {
            throw new IllegalArgumentException("El vecindario no puede ser nulo ni vacio");
        }
        for (State neighbor : neighborhood) {
            if (neighbor == null) {
                throw new IllegalArgumentException("El vecindario no puede contener estados nulos");
            }
        }
        return new ArrayList<>(neighborhood);
    }

    private static List<State> withoutTabu(List<State> neighborhood) {
        List<State> available = new ArrayList<>(neighborhood);
        available.removeIf(TabuSolutions.listTabu::contains);
        return available;
    }

    public TabuSolutions getTabusolution() {
        return tabusolution;
    }

    public void setTabusolution(TabuSolutions tabusolution) {
        this.tabusolution = tabusolution;
    }
}
