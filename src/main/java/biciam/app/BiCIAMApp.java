package biciam.app;

import java.util.ArrayList;
import java.util.List;
import problem.definition.State;

/** Punto de entrada reproducible para comprobar el despliegue de BiCIAM en Docker. */
public final class BiCIAMApp {
    private BiCIAMApp() { }

    /** Ejecuta una demostracion determinista de la representacion de estados. */
    public static void main(String[] args) {
        if (args.length > 0 && "--help".equals(args[0])) {
            System.out.println("BiCIAM 1.0.0. Opciones: --demo (predeterminada), --help");
            return;
        }
        if (args.length > 0 && !"--demo".equals(args[0])) {
            System.err.println("Opcion no reconocida. Usa --help.");
            System.exit(2);
        }
        State initial = new State(new ArrayList<>(List.of(1, 0, 1, 0)));
        State candidate = new State(new ArrayList<>(List.of(1, 1, 0, 0)));
        double distance = initial.Distance(candidate);
        System.out.println("BiCIAM 1.0.0 - demostracion del nucleo");
        System.out.println("Estado inicial: " + initial.getCode());
        System.out.println("Estado candidato: " + candidate.getCode());
        System.out.println("Distancia de Hamming: " + (int) distance);
        System.out.println("Nota: es una demostracion de despliegue, no una optimizacion completa.");
    }
}
