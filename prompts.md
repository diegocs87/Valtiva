/// # Configuración inicial del proyecto Android — Currency Converter MVP

Quiero comenzar la construcción de un MVP de una aplicación Android nativa de conversión de divisas.

## Objetivo de esta primera tarea

NO implementar todavía funcionalidades de negocio ni pantallas de la aplicación.

La única finalidad de esta tarea es preparar una base técnica sólida y reproducible para el proyecto, incorporando las dependencias necesarias y verificando que TODAS las versiones sean compatibles entre sí.

## Stack tecnológico objetivo

El proyecto debe utilizar:

* Kotlin
* Android nativo
* Jetpack Compose
* Material 3
* AndroidX
* ViewModel
* Kotlin Coroutines
* Flow / StateFlow
* Navigation Compose
* Room
* Hilt / Dagger Hilt
* Retrofit
* OkHttp
* Kotlin Serialization
* WorkManager
* Firebase Crashlytics
* Firebase Analytics
* Firebase Performance Monitoring
* JUnit
* MockK
* Android Lint
* Detekt

La aplicación será Android-only. NO utilizar Kotlin Multiplatform.

## Regla fundamental sobre versiones

Antes de modificar los archivos Gradle:

1. Investiga las versiones estables más recientes disponibles actualmente de:

   * Android Gradle Plugin
   * Gradle
   * Kotlin
   * Jetpack Compose BOM
   * AndroidX Core
   * Lifecycle
   * Navigation Compose
   * Room
   * Hilt
   * Hilt Navigation Compose
   * Retrofit
   * OkHttp
   * Kotlin Serialization
   * WorkManager
   * Firebase Android BoM
   * Firebase Crashlytics
   * Firebase Analytics
   * Firebase Performance
   * JUnit
   * MockK
   * Detekt
   * KSP

2. Utiliza fuentes oficiales como prioridad:

   * developer.android.com
   * kotlinlang.org
   * firebase.google.com
   * documentación oficial de cada librería

3. NO selecciones versiones simplemente porque sean las más nuevas individualmente.

4. Construye una combinación de versiones que sea mutuamente compatible.

5. Si una versión más reciente de una dependencia introduce incompatibilidades, utiliza la versión estable inmediatamente anterior que permita una combinación estable.

6. Prioriza:

   1. compatibilidad
   2. estabilidad
   3. soporte oficial
   4. actualidad de las versiones

   sobre utilizar obligatoriamente la versión numéricamente más reciente.

7. NO utilices versiones alpha, beta, RC, EAP, SNAPSHOT o experimentales salvo que sean estrictamente necesarias. Para este MVP queremos versiones estables.

## Gradle

Utiliza Gradle Kotlin DSL (`build.gradle.kts`).

Utiliza Version Catalog (`gradle/libs.versions.toml`) para centralizar las versiones de las dependencias.

No dupliques versiones directamente dentro de los archivos `build.gradle.kts`.

El proyecto debe quedar preparado para que actualizar una dependencia posteriormente sea sencillo.

## Kotlin y compilación

Configura Kotlin utilizando la versión estable compatible seleccionada durante la investigación.

Configura Java/JVM utilizando una versión LTS compatible con todo el stack.

Evita mezclar versiones de Java, Kotlin, AGP y Gradle que puedan generar incompatibilidades.

Verifica explícitamente:

* Kotlin ↔ Gradle
* Kotlin ↔ AGP
* AGP ↔ Gradle
* Compose ↔ Kotlin
* KSP ↔ Kotlin
* Hilt ↔ Kotlin/KSP
* Room ↔ Kotlin/KSP
* Firebase ↔ AGP
* todas las demás dependencias ↔ compileSdk/minSdk

## Android configuration

Configura:

* compileSdk con la versión estable actual recomendada.
* targetSdk con la versión estable actual requerida/recomendada.
* minSdk con un valor razonable para un MVP moderno de conversión de divisas.

Antes de elegir minSdk, verifica la compatibilidad real de las dependencias seleccionadas.

No agregues configuraciones innecesarias.

## Jetpack Compose

Utiliza Compose BOM para administrar las versiones de las librerías Compose.

No especifiques versiones individuales para:

* compose-ui
* compose-material3
* compose-foundation
* compose-runtime
* etc.

cuando dichas versiones estén administradas por el BOM.

Configura correctamente el Compose Compiler/plugin compatible con la versión de Kotlin seleccionada.

## Room

Agrega:

* Room runtime
* Room KTX si continúa siendo apropiado para la versión seleccionada
* Room compiler mediante KSP
* Room Gradle Plugin si aporta valor y es compatible con la configuración seleccionada

NO utilizar KAPT para Room si KSP es la alternativa oficialmente soportada y compatible.

Todavía NO crear entidades, DAOs ni base de datos.

Solamente dejar Room correctamente configurado.

## Hilt

Agrega:

* Hilt Android
* Hilt compiler utilizando el mecanismo de procesamiento recomendado y compatible con la versión de Hilt/Kotlin seleccionada
* Hilt Navigation Compose si es necesaria para la arquitectura prevista
* integración de Hilt con WorkManager si será utilizada posteriormente

NO crear todavía módulos de dependencias ni clases de negocio.

Solamente dejar Hilt correctamente configurado.

## Networking

Agrega:

* Retrofit
* OkHttp
* logging interceptor únicamente para builds debug, si es apropiado
* Kotlin Serialization Converter o la alternativa oficialmente compatible que se haya seleccionado

NO implementar todavía el cliente de Frankfurter.

Solamente preparar las dependencias.

## WorkManager

Agregar WorkManager y las dependencias necesarias para su futura integración con Hilt.

Todavía NO implementar ningún Worker.

## Firebase

Preparar Firebase utilizando Firebase Android BoM.

Agregar:

* Firebase Analytics
* Firebase Crashlytics
* Firebase Performance Monitoring

Utilizar el Firebase BoM para mantener las versiones de los SDK Firebase compatibles.

No utilizar módulos Firebase KTX independientes/deprecados si la arquitectura actual de Firebase recomienda utilizar las APIs Kotlin desde los módulos principales.

No implementar todavía eventos de Analytics ni lógica de Crashlytics.

Si el proyecto Firebase todavía no está conectado, deja la configuración preparada sin inventar un `google-services.json`.

NO crear credenciales ni secretos.

## Testing

Configura:

* JUnit
* MockK
* AndroidX Test
* coroutines test si es necesario
* Turbine si consideras que aporta valor para probar Flow/StateFlow

El objetivo es que el proyecto tenga una infraestructura de testing preparada desde el inicio.

## Calidad de código

Configura:

* Android Lint
* Detekt

Utiliza reglas razonables para un proyecto Android moderno.

No introduzcas cientos de reglas personalizadas en esta primera etapa.

## Arquitectura

Prepara únicamente la estructura base para:

```text
data/
domain/
presentation/
```

No implementar todavía casos de uso, repositorios, ViewModels ni pantallas.

La arquitectura completa se implementará en tareas posteriores.

## Verificación obligatoria

Esta parte es MUY importante.

Después de modificar las dependencias y configuración:

1. Ejecuta sincronización/configuración de Gradle.
2. Ejecuta el build completo.
3. Ejecuta los unit tests.
4. Ejecuta Android Lint.
5. Ejecuta Detekt si está configurado.
6. Verifica que no existan errores de compilación.
7. Verifica que no existan conflictos de dependencias.
8. Verifica que no existan warnings críticos relacionados con incompatibilidad de versiones.

No consideres esta tarea terminada simplemente porque los archivos Gradle hayan sido modificados.

La tarea solamente se considera completada si el proyecto COMPILA correctamente y las pruebas configuradas pasan.

## Diagnóstico de problemas

Si encuentras una incompatibilidad:

1. Identifica exactamente qué dependencias/versiones entran en conflicto.
2. Consulta documentación oficial.
3. Ajusta la combinación de versiones.
4. Vuelve a compilar.
5. Vuelve a ejecutar los tests.
6. Repite hasta obtener una configuración estable.

NO soluciones incompatibilidades deshabilitando validaciones, ignorando errores o utilizando flags que oculten problemas.

## Entregables

Al finalizar:

1. Proyecto compilando correctamente.
2. `gradle/libs.versions.toml` correctamente organizado.
3. Plugins Gradle correctamente configurados.
4. Dependencias organizadas por responsabilidad.
5. Firebase preparado.
6. Room preparado.
7. Hilt preparado.
8. Retrofit/OkHttp preparados.
9. WorkManager preparado.
10. Testing preparado.
11. Lint y Detekt preparados.
12. Sin funcionalidades de negocio todavía.

Finalmente, proporciona un resumen indicando:

* versión de Gradle utilizada
* versión de AGP utilizada
* versión de Kotlin utilizada
* versión de Java/JVM utilizada
* compileSdk
* targetSdk
* minSdk
* Compose BOM
* Room
* Hilt
* KSP
* Retrofit
* OkHttp
* WorkManager
* Firebase BoM
* cualquier otra dependencia relevante

Para cada dependencia importante, indica brevemente por qué se seleccionó esa versión y, especialmente, cualquier compromiso realizado para mantener compatibilidad.

También incluye los comandos ejecutados para verificar el proyecto y el resultado de cada uno.

NO avances todavía a la implementación de la aplicación. Esta tarea termina exclusivamente con una base de proyecto Android moderna, estable, compilable y testeada.



//// prompt dieño los modelos y contratos del dominio + tests unitarios del dominio.
Implementación de la capa Domain — Currency Converter MVP

Quiero continuar la construcción del MVP de una aplicación Android nativa de conversión de divisas.

En esta tarea debes implementar EXCLUSIVAMENTE la capa DOMAIN y sus tests unitarios.

NO implementes todavía:
- Room
- Retrofit
- OkHttp
- Frankfurter API
- Firebase
- WorkManager
- ViewModels
- Compose
- Navigation
- UI
- Android-specific logic
- DTOs
- Room Entities
- DAOs
- Repository implementations

El objetivo es dejar un dominio independiente de infraestructura, completamente testeado y listo para que posteriormente Data y Presentation se construyan sobre él.

==================================================
1. PRINCIPIOS ARQUITECTÓNICOS
==================================================

La capa Domain NO debe depender de:

- Android SDK
- Room
- Retrofit
- OkHttp
- Firebase
- WorkManager
- Compose
- Hilt
- DTOs de APIs
- Room Entities

Domain debe contener únicamente:

- Modelos de negocio
- Interfaces/contratos de repositorios
- Servicios de dominio
- Casos de uso
- Reglas de negocio

Los modelos de Domain NO deben ser reutilizados directamente como Room Entities ni Retrofit DTOs.

Posteriormente Data será responsable de realizar los mappings:

Remote DTO -> Domain Model
Local Entity -> Domain Model
Domain Model -> Local Entity

==================================================
2. ESTRUCTURA ESPERADA
==================================================

Crea o utiliza la siguiente estructura:

domain/
├── model/
│   ├── Currency.kt
│   ├── CurrencyPair.kt
│   ├── Money.kt
│   ├── ExchangeRate.kt
│   ├── ExchangeRateSnapshot.kt
│   ├── ConversionResult.kt
│   └── FavoriteExchange.kt
│
├── repository/
│   ├── ExchangeRateRepository.kt
│   └── FavoriteExchangeRepository.kt
│
├── service/
│   └── CurrencyConversionService.kt
│
└── usecase/
    ├── ConvertCurrencyUseCase.kt
    ├── GetLatestRatesUseCase.kt
    ├── GetHistoricalRatesUseCase.kt
    ├── RefreshRatesUseCase.kt
    ├── GetFavoriteExchangesUseCase.kt
    ├── AddFavoriteExchangeUseCase.kt
    └── RemoveFavoriteExchangeUseCase.kt

La ubicación física exacta puede adaptarse a la estructura actual del proyecto, pero conserva esta separación conceptual.

==================================================
3. MODELOS DE DOMINIO
==================================================

Implementa los siguientes modelos.

------------------------------------------
Currency
------------------------------------------

Representa una moneda.

Propuesta:

data class Currency(
    val code: String,
    val name: String,
    val symbol: String?
)

Consideraciones:

- code representa el código ISO 4217.
- No utilices enum class para las monedas.
- Debemos poder soportar nuevas monedas sin necesidad de modificar el código.
- El código debe normalizarse o validarse adecuadamente.
- No acoples Currency a una API concreta.

Ejemplos:

USD
COP
EUR
GBP
JPY

------------------------------------------
CurrencyPair
------------------------------------------

Representa un par de monedas:

data class CurrencyPair(
    val base: Currency,
    val quote: Currency
)

La terminología debe mantenerse:

base:
moneda desde la cual se parte.

quote:
moneda en la cual se expresa el valor de la moneda base.

Ejemplo:

USD -> COP

base = USD
quote = COP

Si:

rate = 3980.25

significa:

1 USD = 3980.25 COP

No utilices "source" y "target" en lugar de base/quote dentro de este modelo.

------------------------------------------
Money
------------------------------------------

Representa una cantidad monetaria:

data class Money(
    val amount: BigDecimal,
    val currency: Currency
)

IMPORTANTE:

Utiliza java.math.BigDecimal.

NO utilices Double o Float para cantidades monetarias.

------------------------------------------
ExchangeRate
------------------------------------------

Representa una tasa de cambio determinada para un par de monedas y una fecha:

data class ExchangeRate(
    val base: Currency,
    val quote: Currency,
    val rate: BigDecimal,
    val date: LocalDate
)

Ejemplo:

base = USD
quote = COP
rate = 3980.25
date = 2026-08-14

Significa:

1 USD = 3980.25 COP

Utiliza java.time.LocalDate para la fecha.

------------------------------------------
ExchangeRateSnapshot
------------------------------------------

Representa el conjunto de tasas disponible para una moneda base en una fecha determinada.

Conceptualmente:

base = USD
date = 2026-08-14

rates:
COP -> 3980.25
EUR -> 0.85
GBP -> 0.74
JPY -> 147.20

Implementa un modelo apropiado, por ejemplo:

data class ExchangeRateSnapshot(
    val baseCurrency: Currency,
    val date: LocalDate,
    val rates: Map<String, BigDecimal>
)

Si consideras que Map<Currency, BigDecimal> es más apropiado para el dominio, puedes utilizarlo.

Prioriza claridad y seguridad del dominio.

No acoples este modelo al formato de respuesta de Frankfurter.

------------------------------------------
ConversionResult
------------------------------------------

Representa el resultado de una conversión:

data class ConversionResult(
    val source: Money,
    val target: Money,
    val exchangeRate: BigDecimal,
    val rateDate: LocalDate
)

Ejemplo:

source:
100 USD

target:
398025 COP

exchangeRate:
3980.25

rateDate:
2026-08-14

------------------------------------------
FavoriteExchange
------------------------------------------

IMPORTANTE:

Un favorito NO representa una tasa concreta.

La tasa cambia diariamente.

El usuario realmente guarda como favorito un CurrencyPair.

Por lo tanto:

data class FavoriteExchange(
    val pair: CurrencyPair
)

Ejemplo:

USD -> COP ⭐

EUR -> COP ⭐

USD -> EUR ⭐

NO crear un modelo llamado FavoriteRate.

==================================================
4. REGLAS DE NEGOCIO
==================================================

Implementa validaciones razonables dentro del dominio.

Como mínimo considera:

- Código de moneda no vacío.
- Código de moneda normalizado.
- Cantidades monetarias válidas.
- Tasas válidas.
- No permitir tasas negativas.
- Manejar correctamente una tasa igual a cero según el significado de la operación.
- Evitar conversiones inválidas.
- Manejar correctamente el caso en que base y quote sean la misma moneda.

No agregues reglas arbitrarias que no tengan justificación.

Si encuentras una decisión de negocio ambigua, documenta la decisión en lugar de inventar comportamiento complejo.

==================================================
5. CurrencyConversionService
==================================================

Implementa un servicio de dominio:

CurrencyConversionService

Su responsabilidad exclusiva es realizar una conversión utilizando una tasa ya disponible.

No debe:

- consultar APIs
- consultar Room
- utilizar Retrofit
- utilizar Android
- obtener tasas
- decidir cómo persistir datos

Debe recibir los datos necesarios para realizar la conversión.

Conceptualmente:

convert(
    money: Money,
    targetCurrency: Currency,
    rate: ExchangeRate
): ConversionResult

Ejemplo:

100 USD
rate USD/COP = 3980.25

resultado:

398025 COP

Utiliza BigDecimal para todo el cálculo.

NO utilices Double para hacer la multiplicación.

Define y documenta una estrategia consistente de precisión y redondeo.

No utilices un redondeo arbitrario solamente para hacer pasar los tests.

La estrategia debe ser apropiada para valores monetarios y fácil de mantener.

==================================================
6. EXCHANGE RATE REPOSITORY
==================================================

Crea únicamente el contrato:

ExchangeRateRepository

NO implementes todavía la clase concreta.

El dominio no debe saber que existe Frankfurter.

El contrato debe permitir como mínimo:

- obtener las últimas tasas
- obtener tasas históricas
- actualizar/refrescar tasas

Una propuesta:

interface ExchangeRateRepository {

    suspend fun getLatestRates(
        baseCurrency: Currency
    ): ExchangeRateSnapshot?

    suspend fun getRatesForDate(
        baseCurrency: Currency,
        date: LocalDate
    ): ExchangeRateSnapshot?

    suspend fun refreshLatestRates(
        baseCurrency: Currency
    ): ExchangeRateSnapshot
}

Puedes ajustar la firma si encuentras una alternativa arquitectónicamente mejor, pero mantén estas responsabilidades.

NO agregues métodos relacionados directamente con Retrofit, HTTP, Room o Frankfurter.

==================================================
7. FAVORITE EXCHANGE REPOSITORY
==================================================

Crea el contrato:

FavoriteExchangeRepository

Responsabilidad:

gestionar los pares de monedas favoritos del usuario.

Debe permitir como mínimo:

- obtener favoritos
- agregar favorito
- eliminar favorito

Preferiblemente utilizar Flow para observar cambios:

fun getFavorites(): Flow<List<FavoriteExchange>>

Y operaciones suspend para modificaciones.

Una propuesta:

interface FavoriteExchangeRepository {

    fun getFavorites(): Flow<List<FavoriteExchange>>

    suspend fun addFavorite(
        favorite: FavoriteExchange
    )

    suspend fun removeFavorite(
        pair: CurrencyPair
    )
}

No implementar Room todavía.

==================================================
8. USE CASES
==================================================

Implementa los siguientes casos de uso.

------------------------------------------
ConvertCurrencyUseCase
------------------------------------------

Responsabilidad:

Convertir una cantidad utilizando la tasa disponible.

Debe coordinar:

- ExchangeRateRepository
- CurrencyConversionService

La UI no debe conocer cómo se obtiene la tasa.

Conceptualmente:

convert(
    amount,
    sourceCurrency,
    targetCurrency
)

debe devolver:

ConversionResult

El caso de uso debe:

1. Validar la entrada.
2. Obtener la tasa necesaria.
3. Delegar el cálculo al CurrencyConversionService.
4. Devolver ConversionResult.

No realizar llamadas HTTP directamente.

------------------------------------------
GetLatestRatesUseCase
------------------------------------------

Obtiene las últimas tasas disponibles para una moneda base.

Debe delegar al ExchangeRateRepository.

------------------------------------------
GetHistoricalRatesUseCase
------------------------------------------

Obtiene las tasas correspondientes a una fecha determinada.

Debe delegar al ExchangeRateRepository.

------------------------------------------
RefreshRatesUseCase
------------------------------------------

Solicita al repository la actualización de las tasas.

Este caso de uso será utilizado posteriormente por WorkManager.

No implementar WorkManager todavía.

------------------------------------------
GetFavoriteExchangesUseCase
------------------------------------------

Debe devolver:

Flow<List<FavoriteExchange>>

Debe delegar al FavoriteExchangeRepository.

------------------------------------------
AddFavoriteExchangeUseCase
------------------------------------------

Debe agregar un CurrencyPair a favoritos.

------------------------------------------
RemoveFavoriteExchangeUseCase
------------------------------------------

Debe eliminar un CurrencyPair de favoritos.

==================================================
9. CONVERSIONES ENTRE MONEDAS
==================================================

Para esta primera implementación NO introduzcas todavía lógica compleja de conversiones cruzadas.

No implementes automáticamente:

EUR -> USD -> COP

a menos que sea estrictamente necesario para la primera versión del dominio.

Primero soportaremos una tasa explícita:

base -> quote

Por ejemplo:

USD -> COP
EUR -> USD
GBP -> EUR

Las conversiones cruzadas podrán diseñarse posteriormente cuando definamos exactamente cómo almacenaremos los snapshots de tasas.

==================================================
10. TESTS UNITARIOS
==================================================

Esta parte es OBLIGATORIA.

Cada regla importante del dominio debe tener tests.

Los tests deben ser independientes de:

- Android
- Room
- Retrofit
- Firebase
- WorkManager
- red
- archivos
- base de datos

Utiliza JUnit y las herramientas de testing ya configuradas en el proyecto.

Utiliza MockK solamente donde realmente sea necesario para probar contratos/use cases.

No hagas mocks innecesarios.

==================================================
11. TESTS DEL MONEY / CURRENCY
==================================================

Prueba como mínimo:

- Currency válida.
- Código vacío.
- Normalización del código si corresponde.
- Money con valores positivos.
- Money con cero.
- Money con valores negativos, según la regla elegida.
- Comparación de Currency.
- CurrencyPair USD/COP.
- USD/COP != COP/USD.

==================================================
12. TESTS DEL CurrencyConversionService
==================================================

Como mínimo:

1. 100 USD -> COP con tasa 3980.25.

Esperado:

398025 COP.

2. Conversión con decimales.

3. Conversión de cero.

4. Conversión de USD -> USD.

5. Tasa igual a cero.

6. Tasa negativa.

7. Cantidades con muchos decimales.

8. Verificar que no se utilice Double y que el resultado mantenga la precisión esperada.

9. Verificar correctamente la estrategia de rounding definida.

==================================================
13. TESTS DE USE CASES
==================================================

Para ConvertCurrencyUseCase:

- repository devuelve tasa correctamente.
- conversion service recibe los valores correctos.
- devuelve ConversionResult.
- propaga errores relevantes.
- comportamiento cuando no existe una tasa.

Para GetLatestRatesUseCase:

- delega correctamente en repository.

Para GetHistoricalRatesUseCase:

- solicita la fecha correcta.

Para RefreshRatesUseCase:

- delega correctamente en repository.

Para favoritos:

GetFavoriteExchangesUseCase:
- devuelve el Flow del repository.

AddFavoriteExchangeUseCase:
- agrega el favorito correcto.

RemoveFavoriteExchangeUseCase:
- elimina el CurrencyPair correcto.

==================================================
14. CALIDAD DEL CÓDIGO
==================================================

Mantén el código:

- idiomático en Kotlin
- simple
- legible
- cohesivo
- con responsabilidades claras
- sin sobreingeniería

Utiliza:

- data class donde corresponda
- operator fun invoke para use cases si es apropiado
- suspend donde corresponda
- Flow para streams reactivos
- BigDecimal para valores monetarios
- LocalDate para fechas

Evita:

- God classes
- Singletons manuales
- Service Locator
- dependencias Android dentro del dominio
- constantes mágicas
- lógica duplicada
- interfaces innecesarias

==================================================
15. DOCUMENTACIÓN
==================================================

Agrega KDoc solamente donde aporte valor.

Documenta especialmente:

- diferencia entre base y quote
- significado de ExchangeRate
- por qué se utiliza BigDecimal
- estrategia de precisión/redondeo
- por qué FavoriteExchange representa un CurrencyPair y no una tasa

==================================================
16. VERIFICACIÓN FINAL
==================================================

Cuando termines:

1. Compila el proyecto.
2. Ejecuta TODOS los unit tests.
3. Ejecuta Android Lint.
4. Ejecuta Detekt si está configurado.
5. Verifica que no existan errores de compilación.
6. Verifica que los tests pasen.
7. Verifica que Domain no tenga dependencias de infraestructura.

Si algún test falla:

- identifica la causa
- corrige la implementación
- vuelve a ejecutar los tests

NO consideres la tarea terminada hasta que todo compile y los tests pasen.

==================================================
17. RESTRICCIÓN IMPORTANTE
==================================================

NO avances a la siguiente capa.

Al terminar esta tarea quiero tener:

Domain Models
+
Repository Contracts
+
Domain Service
+
Use Cases
+
Unit Tests

Pero NO quiero todavía:

Room
Retrofit
Frankfurter
Firebase
WorkManager
ViewModels
Compose
UI

La siguiente etapa será implementar la capa Data y conectar estos contratos con Room + Retrofit/Frankfurter.

==================================================
18. REPORTE FINAL
==================================================

Al finalizar proporciona:

1. Archivos creados/modificados.
2. Resumen de los modelos implementados.
3. Resumen de los contratos.
4. Resumen de los use cases.
5. Reglas de negocio implementadas.
6. Estrategia de precisión/redondeo utilizada.
7. Tests creados.
8. Comandos ejecutados.
9. Resultado del build.
10. Resultado de los tests.
11. Resultado de Lint.
12. Resultado de Detekt.

Si durante la implementación encuentras una decisión de diseño ambigua, NO inventes una solución silenciosamente. Documenta la decisión tomada y explica brevemente por qué.