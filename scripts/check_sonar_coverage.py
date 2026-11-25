#!/usr/bin/env python3
"""Verifica la cobertura JaCoCo de las ocho clases que se han probado."""
from pathlib import Path
import sys
import xml.etree.ElementTree as ET

CLASES = {
    "evolutionary_algorithms/complement/OnePointCrossover",
    "evolutionary_algorithms/complement/UniformCrossover",
    "evolutionary_algorithms/complement/TowPointsMutation",
    "factory_method/FactoryLoader",
    "local_search/candidate_type/RandomCandidate",
    "local_search/complement/StopExecute",
    "problem/definition/Problem",
    "problem/definition/State",
}


def main():
    archivo = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("target/site/jacoco/jacoco.xml")
    if not archivo.is_file():
        sys.exit(f"ERROR: no existe el informe JaCoCo: {archivo}")
    raiz = ET.parse(archivo).getroot()
    clases = {n.attrib['name']: n for n in raiz.iter('class')}
    pendientes = []
    suma_cubiertas = 0
    suma_lineas = 0
    print("Clase                        Lineas  Cobertura  >=80%")
    print("-" * 61)
    for nombre in sorted(CLASES):
        clase = clases.get(nombre)
        if clase is None:
            pendientes.append(f"{nombre}: clase ausente del informe")
            continue
        contador = clase.find("counter[@type='LINE']")
        if contador is None:
            pendientes.append(f"{nombre}: sin contador de LINE")
            continue
        cubiertas = int(contador.get('covered', 0))
        perdidas = int(contador.get('missed', 0))
        total = cubiertas + perdidas
        porcentaje = cubiertas / total * 100 if total else 0
        cumple = total > 0 and cubiertas * 100 >= total * 80
        suma_cubiertas += cubiertas
        suma_lineas += total
        print(f"{nombre.rsplit('/', 1)[-1]:28} {cubiertas:>3}/{total:<3} {porcentaje:>7.1f}%  {'SI' if cumple else 'NO'}")
        if not cumple:
            pendientes.append(f"{nombre}: {porcentaje:.1f}%")
    print("-" * 61)
    combinado = suma_cubiertas / suma_lineas * 100 if suma_lineas else 0
    print(f"Total 8 clases: {suma_cubiertas}/{suma_lineas} lineas ({combinado:.1f}%).")
    print("Aviso: el 80% es para las ocho clases seleccionadas, NO el proyecto completo.")
    if pendientes:
        print("ERROR: no se alcanza la meta: " + "; ".join(pendientes), file=sys.stderr)
        sys.exit(1)
    print("OK: las ocho clases cumplen la meta del 80%.")


if __name__ == '__main__':
    main()
