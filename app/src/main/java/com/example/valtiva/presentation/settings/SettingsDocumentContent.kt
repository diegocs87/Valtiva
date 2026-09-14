package com.example.valtiva.presentation.settings

/**
 * The static copy behind [SettingsDocumentKind.ABOUT], as Markdown.
 *
 * A Kotlin raw string rather than a string resource: the text is long, holds its own blank lines and
 * apostrophes, and is never localized or formatted with arguments, so a `.xml` string would only add
 * escaping to get right. [PrivacyPolicyMarkdown] is kept the same way for the same reason.
 */
internal val AboutValtivaMarkdown = """
# Acerca de Valtiva

**Valtiva** es una aplicación sencilla y práctica para convertir valores entre diferentes monedas.

Consulta tasas de cambio, realiza conversiones y guarda tus monedas favoritas para tener la información que necesitas al alcance de la mano.

Valtiva utiliza tasas de cambio proporcionadas por servicios externos y actualizadas periódicamente. **Las tasas mostradas no representan necesariamente valores en tiempo real**, por lo que las conversiones deben utilizarse únicamente como referencia.

### Características

* Conversión entre diferentes monedas.
* Tasas de cambio actualizadas periódicamente.
* Monedas favoritas.
* Historial de conversiones.
* Funcionamiento con información almacenada localmente cuando está disponible.
* Interfaz sencilla y fácil de usar.

### Versión

**Valtiva 1.0.0**

Gracias por utilizar Valtiva.
""".trimIndent()

/** The static copy behind [SettingsDocumentKind.PRIVACY_POLICY], as Markdown. */
internal val PrivacyPolicyMarkdown = """
# Política de Privacidad

**Última actualización:** septiembre de 2026

En Valtiva valoramos tu privacidad. Esta Política de Privacidad explica qué información puede recopilarse cuando utilizas la aplicación Valtiva, cómo se utiliza y qué opciones tienes respecto a dicha información.

## 1. Sobre Valtiva

Valtiva es una aplicación de conversión de divisas que permite consultar tasas de cambio y realizar conversiones entre diferentes monedas.

Las tasas de cambio utilizadas por Valtiva son proporcionadas por servicios externos y se actualizan periódicamente. Valtiva no proporciona información financiera, asesoría de inversión ni servicios de cambio de divisas.

## 2. Información que recopilamos

Valtiva está diseñada para funcionar sin requerir que el usuario cree una cuenta ni proporcione información personal como nombre, correo electrónico, dirección o número de teléfono.

La aplicación puede almacenar localmente información necesaria para proporcionar sus funcionalidades, como:

* Monedas seleccionadas.
* Última conversión realizada.
* Monedas favoritas.
* Tasas de cambio almacenadas para permitir el funcionamiento de la aplicación y reducir solicitudes de red.

Esta información se almacena en el dispositivo y se utiliza únicamente para proporcionar y mejorar la experiencia dentro de Valtiva.

## 3. Datos de uso y diagnóstico

Valtiva puede utilizar servicios de Google Firebase, incluyendo **Firebase Analytics** y **Firebase Crashlytics**, para comprender cómo se utiliza la aplicación, detectar errores y mejorar su estabilidad.

Estos servicios pueden recopilar información técnica y de uso, que puede incluir datos como:

* Información sobre el dispositivo.
* Versión del sistema operativo.
* Versión de la aplicación.
* Información sobre errores y fallos.
* Información relacionada con la interacción con determinadas funcionalidades de la aplicación.
* Identificadores técnicos utilizados por estos servicios.

Estos datos se utilizan con fines de análisis, diagnóstico y mejora de la aplicación.

Valtiva no utiliza estos servicios para recopilar directamente información financiera personal, contraseñas o credenciales bancarias.

## 4. Tasas de cambio

Valtiva obtiene información sobre tasas de cambio desde servicios externos. Para obtener estas tasas, la aplicación puede realizar solicitudes de red a dichos servicios.

Las tasas mostradas por Valtiva corresponden a información proporcionada por terceros y pueden no representar valores de mercado en tiempo real.

Por este motivo, las conversiones realizadas en Valtiva deben considerarse únicamente como una referencia.

## 5. Información almacenada en el dispositivo

Valtiva utiliza almacenamiento local para conservar determinadas preferencias y datos necesarios para proporcionar una experiencia más rápida y permitir determinadas funcionalidades cuando no existe conexión a Internet.

La información almacenada localmente permanece en el dispositivo y puede eliminarse al borrar los datos de la aplicación o desinstalarla, dependiendo de la configuración del sistema operativo.

## 6. Servicios de terceros

Valtiva puede utilizar servicios proporcionados por terceros para determinadas funcionalidades, incluyendo servicios de análisis, diagnóstico y obtención de tasas de cambio.

Estos proveedores pueden procesar información de acuerdo con sus propias políticas de privacidad.

El uso de Valtiva implica la aceptación de que determinados datos técnicos necesarios para estos servicios puedan ser procesados por dichos proveedores.

## 7. Seguridad

Tomamos medidas razonables para proteger la información procesada por Valtiva. Sin embargo, ningún sistema de almacenamiento o transmisión de información a través de Internet puede garantizar una seguridad absoluta.

## 8. Privacidad de menores

Valtiva no está dirigida específicamente a menores de edad y no solicita deliberadamente información personal de niños.

## 9. Cambios en esta política

Esta Política de Privacidad puede actualizarse periódicamente para reflejar cambios en Valtiva, en los servicios utilizados o en los requisitos legales aplicables.

Cuando se realicen cambios relevantes, se actualizará la fecha de "Última actualización" indicada al comienzo de esta política.

## 10. Contacto

Si tienes preguntas, comentarios o inquietudes relacionadas con esta Política de Privacidad o con el tratamiento de información en Valtiva, puedes comunicarte con nosotros a través del medio de contacto proporcionado en la aplicación o en su página oficial.

---

**Valtiva**
Conversión de divisas de forma sencilla.
""".trimIndent()

/** [AboutValtivaMarkdown] parsed once, since the copy is a compile-time constant. */
internal val AboutValtivaBlocks: List<MarkdownBlock> by lazy { parseMarkdown(AboutValtivaMarkdown) }

/** [PrivacyPolicyMarkdown] parsed once, since the copy is a compile-time constant. */
internal val PrivacyPolicyBlocks: List<MarkdownBlock> by lazy { parseMarkdown(PrivacyPolicyMarkdown) }
