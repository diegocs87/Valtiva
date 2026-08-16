# Valtiva

MVP de aplicación Android nativa de conversión de divisas.

## Matriz de versiones

| Componente | Versión | Por qué / compromisos |
|---|---|---|
| Gradle | 9.5.0 | Mínimo requerido y versión por defecto de AGP 9.3. Existe 9.7.0, pero 9.5.0 es la combinación testeada por AGP. Además Gradle 9.5 embebe Kotlin 2.3.20, alineado con el proyecto. |
| Android Gradle Plugin | 9.3.1 | Última estable (la 9.4 está en alpha). Trae Kotlin integrado (built-in), por eso no se aplica `org.jetbrains.kotlin.android`. |
| Android Studio | Quail 3 (2026.1.3) | AGP 9.3 requiere exactamente Quail 3 o superior. Versiones anteriores (p. ej. Narwhal 2025.1) soportan como máximo AGP 8.11 y no pueden sincronizar el proyecto. |
| Kotlin | 2.3.20 | **Compromiso principal**: Kotlin 2.4.0 (11/08/2026) es demasiado reciente — AGP 9.3 declara soporte hasta 2.3 y Detekt estable tampoco lo soporta. 2.3.20 es la estable inmediatamente anterior con soporte pleno de todo el stack. |
| Java / JVM | 17 (LTS) | Mínimo de AGP 9. Toolchain 17 con resolver Foojay configurado. |
| compileSdk | 37 | Requerido por OkHttp 5.5.0 (artefacto Android) y varias AndroidX actuales en sus metadatos AAR. AGP 9.3 soporta hasta API 37. |
| targetSdk | 36 | Nivel requerido por Google Play en agosto 2026. Se mantiene bajo el compileSdk deliberadamente: subir targetSdk activa comportamientos runtime nuevos que conviene adoptar con testing. |
| minSdk | 26 | Hilt exige ≥23; con 26 se usa `java.time` sin desugaring (relevante para conversión de divisas) y cubre la gran mayoría de dispositivos en 2026. |
| Compose BOM | 2026.08.00 | Última estable (Compose 1.12). Los módulos Compose no declaran versión individual: las gestiona el BOM. El compilador de Compose se aplica vía `org.jetbrains.kotlin.plugin.compose` con la versión de Kotlin. |
| Navigation Compose | 2.9.8 | Última estable de Navigation 2 (2.10 está en RC). Reemplaza a Navigation 3 del template para alinearse con Hilt Navigation Compose. |
| Room | 2.8.4 | Última estable, incluye Room Gradle Plugin con `schemaDirectory`. Compilador vía KSP (no KAPT). |
| Hilt | 2.60.1 | Última estable; 2.59+ es requisito para AGP 9 y 2.60.1 corrige el bug del plugin con AGP 9. minSdk de Hilt ahora es 23. |
| androidx.hilt | 1.4.0 | `hilt-navigation-compose`, `hilt-work` y `hilt-compiler` (KSP) para la integración con WorkManager. |
| KSP | 2.3.11 | KSP ya no versiona atado a Kotlin (`2.2.20-1.0.x`); ahora es standalone. 2.3.11 está compilado contra stdlib 2.3.20. |
| Retrofit | 3.0.0 | Última estable, con `converter-kotlinx-serialization` oficial del mismo release. |
| OkHttp | 5.5.0 | Última estable de la serie 5.x. Es la dependencia que fuerza compileSdk 37. |
| Kotlin Serialization | 1.11.0 | Compilada contra stdlib 2.3.20 exacto. El plugin de serialización usa la versión de Kotlin. |
| Coroutines | 1.11.0 | Última estable (stdlib 2.2.20 ≤ 2.3.20, compatible). |
| WorkManager | 2.11.2 | Última estable (2.12 está en RC). |
| Firebase BoM | 34.17.0 | Última estable. Módulos principales sin KTX deprecados: Analytics, Crashlytics, Performance. |
| Plugins Firebase | google-services 4.5.0 · crashlytics 3.0.7 · perf 2.0.2 | Versiones con fix para AGP 9. Declarados en el catálogo y en el root con `apply false`, comentados en `:app` hasta que exista `google-services.json`. |
| AndroidX Core | 1.19.0 | Última estable. |
| Lifecycle | 2.11.0 | Última estable (2.12 está en alpha). ViewModel, `collectAsStateWithLifecycle`. |
| Activity Compose | 1.13.0 | Última estable. |
| JUnit | 4.13.2 | Última estable. |
| MockK | 1.14.11 | Última estable. |
| Turbine | 1.2.1 | Última estable. Para testear Flow / StateFlow. |
| AndroidX Test | core 1.7.0 · ext-junit 1.3.0 · runner 1.7.0 · espresso 3.7.0 | Últimas estables. |
| Detekt | 1.23.8 | **Compromiso**: Detekt 2.0 (que soporta Kotlin 2.4) está en alpha. 1.23.8 es la última estable; configuración en `config/detekt/detekt.yml`. |
