#!/usr/bin/env python3
"""Convierte los hallazgos reales de PMD, Checkstyle y SpotBugs a formato
SonarQube Cloud Generic Issue Data (reglas + issues). Nunca inventa hallazgos.
OWASP Dependency-Check JSON se incluye si existe en target (opcional).
"""
import json
from collections import Counter
from pathlib import Path
import sys
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parent.parent
TARGET = ROOT / 'target'
RULES = {}
ISSUES = []
COUNTS = Counter()


def tag(e):
    return e.tag.rsplit('}', 1)[-1]


def findall(root, target):
    return (e for e in root.iter() if tag(e) == target)


def source_path(raw):
    """Convierte rutas Linux y Windows a rutas del checkout, sin salir del proyecto."""
    if not raw:
        return None
    normalized = str(raw).replace('\\', '/')
    marker = '/src/main/java/'
    if marker in normalized:
        relative = 'src/main/java/' + normalized.split(marker, 1)[1]
    elif normalized.startswith('src/main/java/'):
        relative = normalized
    else:
        path = Path(normalized)
        if path.is_absolute():
            try:
                relative = path.resolve().relative_to(ROOT.resolve()).as_posix()
            except ValueError:
                return None
        else:
            relative = normalized.lstrip('./')
    try:
        target = (ROOT / relative).resolve()
        target.relative_to(ROOT.resolve())
    except ValueError:
        return None
    return relative if target.is_file() else None


def normalize_severity(value):
    value = str(value or '').upper()
    if value in ('1', 'HIGH', 'BLOCKER', 'CRITICAL', 'ERROR'):
        return 'HIGH'
    if value in ('4', '5', 'LOW', 'INFO', 'INFORMATION', 'MINOR'):
        return 'LOW'
    return 'MEDIUM'


def add(engine, rule, message, file_path, line, severity='MEDIUM', quality='MAINTAINABILITY'):
    relative = source_path(file_path)
    if relative is None:
        return
    rule = str(rule or 'generic')
    rid = f'{engine}:{rule}'
    if rid not in RULES:
        RULES[rid] = {
            'id': rid,
            'name': rule,
            'description': f'Regla {rule} informada por {engine}',
            'engineId': engine,
            'cleanCodeAttribute': 'TRUSTWORTHY' if quality == 'SECURITY' else 'CONVENTIONAL',
            'impacts': [{'softwareQuality': quality, 'severity': normalize_severity(severity)}],
        }
    location = {'message': ' '.join(str(message or rule).split())[:1000], 'filePath': relative}
    try:
        line = int(line)
        if line > 0:
            location['textRange'] = {'startLine': line}
    except (TypeError, ValueError):
        pass
    ISSUES.append({'ruleId': rid, 'primaryLocation': location})
    COUNTS[engine] += 1


def parse_xml(filename):
    file = TARGET / filename
    if not file.is_file():
        return None
    try:
        return ET.parse(file).getroot()
    except ET.ParseError as ex:
        sys.exit(f'ERROR: XML invalido {filename}: {ex}')


def pmd():
    root = parse_xml('pmd.xml')
    if root is None:
        sys.exit('ERROR: falta target/pmd.xml (ejecuta mvn clean verify -Pquality)')
    for file in findall(root, 'file'):
        for violation in file:
            if tag(violation) != 'violation':
                continue
            priority = int(violation.get('priority', '3'))
            severity = 'HIGH' if priority <= 2 else ('LOW' if priority >= 4 else 'MEDIUM')
            add('pmd', violation.get('rule'), ''.join(violation.itertext()),
                file.get('name'), violation.get('beginline'), severity)


def checkstyle():
    root = parse_xml('checkstyle-result.xml')
    if root is None:
        sys.exit('ERROR: falta target/checkstyle-result.xml')
    for file in findall(root, 'file'):
        for issue in file:
            if tag(issue) == 'error':
                rule = (issue.get('source') or 'Checkstyle').rsplit('.', 1)[-1]
                add('checkstyle', rule, issue.get('message', ''),
                    file.get('name'), issue.get('line'),
                    'MEDIUM' if issue.get('severity') == 'error' else 'LOW')


def spotbugs():
    root = parse_xml('spotbugsXml.xml')
    if root is None:
        root = parse_xml('spotbugs.xml')
    if root is None:
        sys.exit('ERROR: falta target/spotbugs.xml o target/spotbugsXml.xml')
    for parent in root.iter():
        if tag(parent) != 'file':
            continue
        classname = parent.get('classname', '')
        basefile = 'src/main/java/' + classname.split('$')[0].replace('.', '/') + '.java'
        for bug in parent:
            if tag(bug) != 'BugInstance':
                continue
            quality = 'SECURITY' if bug.get('category') == 'SECURITY' else 'RELIABILITY'
            add('spotbugs', bug.get('type'), bug.get('message'), basefile,
                bug.get('lineNumber'), bug.get('priority'), quality)
    # XML detallado de SpotBugs (contiene <SourceLine>), diferente del simplificado.
    if not any(tag(x) == 'file' for x in root):
        for bug in findall(root, 'BugInstance'):
            locations = [x for x in findall(bug, 'SourceLine') if x.get('sourcepath')]
            if not locations:
                continue
            loc = locations[0]
            file = 'src/main/java/' + loc.get('sourcepath', '').replace('\\', '/')
            desc = next((x.text for x in findall(bug, 'LongMessage') if x.text), '')
            quality = 'SECURITY' if bug.get('category') == 'SECURITY' else 'RELIABILITY'
            add('spotbugs', bug.get('type'), desc or bug.get('type'), file,
                loc.get('start'), bug.get('priority'), quality)


def owasp_optional():
    file = TARGET / 'dependency-check-report.json'
    if not file.is_file():
        print('OWASP JSON no disponible en esta ejecucion: importacion opcional omitida.')
        return
    report = json.loads(file.read_text(encoding='utf-8'))
    for dep in report.get('dependencies', []):
        component = dep.get('fileName') or dep.get('filePath') or 'dependencia'
        for vuln in dep.get('vulnerabilities', []):
            name = vuln.get('name') or 'vulnerabilidad'
            severity = vuln.get('severity') or 'HIGH'
            message = f'{component}: {name}. {vuln.get("description", "")}'
            add('owasp-dependency-check', name, message, 'pom.xml', 1, severity, 'SECURITY')


def main():
    pmd()
    checkstyle()
    spotbugs()
    owasp_optional()
    output = TARGET / 'external-issues.json'
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(json.dumps({'rules': list(RULES.values()), 'issues': ISSUES},
                                 ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    print('Incidencias reales importadas:', dict(COUNTS))
    print(f'Total: {len(ISSUES)} incidencias, {len(RULES)} reglas. Archivo: {output}')
    if not ISSUES:
        print('AVISO: no se encontraron incidencias; comprueba que los XML son correctos.')


if __name__ == '__main__':
    main()
