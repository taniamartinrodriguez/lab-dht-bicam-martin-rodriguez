package factory_method;

import evolutionary_algorithms.complement.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import local_search.acceptation_type.AcceptType;
import local_search.candidate_type.CandidateType;
import metaheuristics.generators.GeneratorType;
import problem.extension.TypeSolutionMethod;

/** Cargador de factorías restringido a los tipos previstos por BiCIAM.
 * No permite instanciar clases arbitrarias recibidas como texto.
 */
public final class FactoryLoader {
    private static final Set<String> ALLOWED_CLASSES = buildAllowedClasses();

    private FactoryLoader() { }

    private static Set<String> buildAllowedClasses() {
        Set<String> allowed = new HashSet<>();
        addEnums(allowed, "local_search.acceptation_type.", AcceptType.values());
        addEnums(allowed, "local_search.candidate_type.", CandidateType.values());
        addEnums(allowed, "evolutionary_algorithms.complement.", CrossoverType.values());
        addEnums(allowed, "evolutionary_algorithms.complement.", DistributionType.values());
        addEnums(allowed, "evolutionary_algorithms.complement.", MutationType.values());
        addEnums(allowed, "evolutionary_algorithms.complement.", SelectionType.values());
        addEnums(allowed, "evolutionary_algorithms.complement.", ReplaceType.values());
        addEnums(allowed, "evolutionary_algorithms.complement.", SamplingType.values());
        addEnums(allowed, "metaheuristics.generators.", GeneratorType.values());
        addEnums(allowed, "problem.extension.", TypeSolutionMethod.values());
        return Set.copyOf(allowed);
    }

    private static void addEnums(Set<String> allowed, String packagePrefix, Enum<?>[] values) {
        for (Enum<?> value : values) {
            allowed.add(packagePrefix + value.name());
        }
    }

    /** Obtiene una instancia nueva o lanza la excepción exacta, sin ocultarla. */
    public static Object getInstance(String className) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        Objects.requireNonNull(className, "className");
        if (!ALLOWED_CLASSES.contains(className)) {
            throw new IllegalArgumentException("Clase no permitida en BiCIAM: " + className);
        }
        Class<?> type = Class.forName(className);
        return type.getDeclaredConstructor().newInstance();
    }
}
