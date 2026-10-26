# Revisión de Merge Request de GitLab

MR solicitado: $ARGUMENTS

## Flujo de trabajo

1. Leer los criterios de revisión desde `~/.config/veritran/review/REVIEW_METRICS.md`.
2. Identificar el proyecto de GitLab y el Merge Request correspondiente.
3. Obtener los metadatos del MR, las ramas de origen y destino, los commits y el diff completo mediante la integración configurada con GitLab.
4. Si no es posible identificar el proyecto a partir del argumento proporcionado, solicitar al usuario la ruta completa del proyecto o la URL del Merge Request. No hacer suposiciones.
5. Inspeccionar el código relacionado cuando sea necesario para validar posibles hallazgos y comprender el impacto de los cambios.
6. Evaluar los cambios frente a todas las métricas aplicables definidas en `REVIEW_METRICS.md`, incluidos los criterios específicos de Android/Kotlin cuando corresponda.
7. Ignorar los archivos generados y el código no relacionado, salvo que afecten directamente a los cambios revisados.
8. No modificar el repositorio, crear commits, publicar comentarios, aprobar o fusionar el Merge Request, ni ejecutar ninguna otra operación de escritura en GitLab.
9. Reportar únicamente hallazgos respaldados por evidencia concreta. No afirmar que se ejecutaron pruebas, compilaciones u otras validaciones si realmente no se realizaron.
10. El informe principal debe incluir únicamente hallazgos de severidad **Crítica, Alta o Media**. Los hallazgos de severidad Baja solo deben presentarse por separado si el formato de salida incluye explícitamente una sección opcional para ellos. No aumentar artificialmente la severidad para incluir un hallazgo.
11. Mantener las sugerencias de estilo y de uso idiomático del lenguaje separadas de los defectos funcionales y de otros hallazgos accionables.

## Formato de salida

Comenzar con un resumen breve del Merge Request que incluya su propósito, las principales áreas afectadas y una evaluación general del riesgo.

Para cada hallazgo del informe principal, proporcionar:

- **Título:** Descripción breve y precisa del problema.
- **Severidad:** Crítica, Alta o Media.
- **Categoría de la métrica:** Criterio de revisión aplicable de `REVIEW_METRICS.md`.
- **Ubicación:** Ruta del archivo y número de línea modificada, cuando esté disponible.
- **Evidencia:** Explicación concreta del problema, respaldada por el código o el comportamiento relevante.
- **Impacto:** Consecuencias potenciales, como regresiones, riesgos de seguridad, comportamiento incorrecto o problemas de mantenimiento.
- **Solución recomendada:** Recomendación específica y accionable.

Dar prioridad a los hallazgos relacionados con las líneas modificadas. Reportar problemas en código circundante o preexistente únicamente cuando el Merge Request los introduzca, exponga, agrave o afecte de manera significativa.

Después de los hallazgos principales, incluir las siguientes secciones:

### Hallazgos por severidad

Agrupar los hallazgos reportados por severidad, de Crítica a Media. Incluir el número total de hallazgos para cada nivel.

### Métricas sin hallazgos

Enumerar las categorías de métricas aplicables que fueron evaluadas y en las que no se identificaron hallazgos que cumplan los criterios de reporte.

### Sugerencias de estilo y uso idiomático

Incluir una lista independiente y opcional de mejoras en legibilidad, mantenibilidad y uso idiomático del lenguaje.

Para código Android/Kotlin, considerar construcciones idiomáticas cuando aporten un beneficio real, como funciones de alcance (*scope functions*), funciones de extensión, operaciones sobre colecciones, mejoras en el manejo de valores nulos y uso apropiado de funciones `inline`.

No presentar preferencias subjetivas como defectos.

### Limitaciones

Describir las limitaciones que afectaron la revisión, incluidas la falta de información del proyecto, los diffs incompletos, el código relacionado inaccesible, las dependencias o configuraciones faltantes y las validaciones que no pudieron realizarse.

Distinguir explícitamente los hechos verificados de las suposiciones.

### Evaluación general

Resumir los principales riesgos y la calidad general de los cambios. Si no se identifican hallazgos que cumplan los criterios de reporte, indicarlo claramente, sin afirmar que el código está libre de defectos.

## Principios de revisión

- Seguir `REVIEW_METRICS.md` como fuente principal de los criterios de revisión, las definiciones de severidad y los requisitos de presentación del informe.
- Centrarse en la corrección funcional, la seguridad, la mantenibilidad, la arquitectura, las pruebas, el rendimiento, la compatibilidad y las buenas prácticas aplicables de Android/Kotlin.
- Respaldar cada hallazgo con evidencia verificable y explicar por qué es relevante.
- Evitar hallazgos duplicados que describan un mismo problema subyacente.
- No inventar hallazgos para completar el informe.
- No incluir problemas de severidad Baja en la sección principal de hallazgos.
- No afirmar que el Merge Request está aprobado, que es seguro fusionarlo o que ha sido validado por completo, salvo que la evidencia disponible respalde esa conclusión.