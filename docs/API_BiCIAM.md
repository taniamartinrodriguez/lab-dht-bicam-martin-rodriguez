# BiCIAM — API y guía de desarrollo {#mainpage}

BiCIAM es un framework Java de algoritmos de búsqueda y optimización basado en
metaheurísticas. Se compila con **Maven y Java 17**, con pruebas **JUnit 5** y
cobertura medida con **JaCoCo**.

## Estructura principal

| Paquete | Responsabilidad |
|---|---|
| `problem.definition` | Modelo de estados, problemas y objetivos |
| `problem.extension` | Evaluación y resolución de problemas multiobjetivo |
| `metaheuristics.generators` | Búsqueda local, genética, PSO, recocido, tabú, etc. |
| `metaheurictics.strategy` | Coordinación y requisitos de la estrategia (nombre original del paquete) |
| `evolutionary_algorithms.complement` | Cruce, mutación, selección y reemplazo |
| `local_search.candidate_type` | Selección de candidatos |
| `local_search.acceptation_type` | Criterios de aceptación de estados |
| `factory_method` | Creación controlada de componentes mediante factorías |
| `biciam.app` | Entrada de consola para demostrar la ejecución del JAR |

## Clases clave

- `problem.definition.State` contiene la codificación de una solución, sus evaluaciones y metadatos. `Distance` calcula una distancia entre dos estados.
- `problem.definition.Problem` reúne el sentido de optimización, las funciones objetivo, el operador y el estado de referencia.
- `factory_method.FactoryLoader` realiza la creación controlada de implementaciones.
- `evolutionary_algorithms.complement.OnePointCrossover`, `UniformCrossover` y `TowPointsMutation` modifican soluciones candidatas.
- `metaheurictics.strategy.StrategyRequirements` verifica precondiciones antes de utilizar componentes de búsqueda.

## Compilación, pruebas y despliegue

Ejecutar los siguientes comandos **desde la raíz del proyecto**:

```powershell
mvn clean verify
doxygen Doxyfile
docker build -t biciam:1.0.0 .
docker run --rm biciam:1.0.0
```

El punto de entrada `biciam.app.BiCIAMApp` ejecuta la opción `--demo`, que
utiliza estados reales de BiCIAM y muestra una distancia de Hamming de dos.
**Es una demostración de ejecución del JAR, no una optimización completa.**

## Documentación de mantenimiento

Las clases, métodos y atributos deben tener comentarios Javadoc adecuados,
en especial `@param`, `@return` y `@throws` cuando corresponda.
Doxygen lista toda la API gracias a `EXTRACT_ALL=YES`; esa opción no documenta
por sí sola el código heredado que todavía carezca de Javadoc.
