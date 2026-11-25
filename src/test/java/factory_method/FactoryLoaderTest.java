package factory_method;

import static org.junit.jupiter.api.Assertions.*;
import evolutionary_algorithms.complement.OnePointMutation;
import local_search.candidate_type.RandomCandidate;
import org.junit.jupiter.api.Test;

class FactoryLoaderTest {
    @Test void admiteExclusivamenteLosTiposDeLasFactorias() throws Exception {
        Object mutation = FactoryLoader.getInstance("evolutionary_algorithms.complement.OnePointMutation");
        assertInstanceOf(OnePointMutation.class, mutation);
        assertNotSame(mutation, FactoryLoader.getInstance(
                "evolutionary_algorithms.complement.OnePointMutation"));
        assertInstanceOf(RandomCandidate.class, FactoryLoader.getInstance(
                "local_search.candidate_type.RandomCandidate"));
    }

    @Test void rechazaCargaArbitrariaDeClases() {
        assertThrows(IllegalArgumentException.class,
                () -> FactoryLoader.getInstance("java.lang.Runtime"));
        assertThrows(IllegalArgumentException.class,
                () -> FactoryLoader.getInstance("local_search.candidate_type.NoExiste"));
        assertThrows(NullPointerException.class, () -> FactoryLoader.getInstance(null));
    }

    @Test void factoriasConservanInterfacesPublicas() throws Exception {
        assertInstanceOf(OnePointMutation.class, new FactoryMutation().createMutation(
                evolutionary_algorithms.complement.MutationType.OnePointMutation));
        assertInstanceOf(RandomCandidate.class, new FactoryCandidate().createSearchCandidate(
                local_search.candidate_type.CandidateType.RandomCandidate));
        assertInstanceOf(evolutionary_algorithms.complement.UniformCrossover.class,
                new FactoryCrossover().createCrossover(
                        evolutionary_algorithms.complement.CrossoverType.UniformCrossover));
        assertInstanceOf(problem.extension.FactoresPonderados.class,
                new FactorySolutionMethod().createdSolutionMethod(
                        problem.extension.TypeSolutionMethod.FactoresPonderados));
    }
}
