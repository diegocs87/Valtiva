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

**Última actualización:** octubre de 2026

Esta Política de Privacidad describe cómo la aplicación Valtiva accede, trata y comparte información. Valtiva es una aplicación de conversión de divisas que permite consultar tasas de cambio, realizar conversiones, guardar pares favoritos y revisar un historial de conversiones.

## 1. Responsable y contacto

* **Aplicación:** Valtiva
* **Desarrollador:** PENDIENTE: nombre del desarrollador tal como figura en Google Play.
* **Contacto de privacidad:** PENDIENTE: correo electrónico de contacto.

## 2. Resumen

* Valtiva no tiene cuentas de usuario ni inicio de sesión, y no solicita nombre, correo, teléfono, dirección, ubicación, contactos, fotos ni otros datos personales.
* Tus preferencias, favoritos e historial se guardan únicamente en tu dispositivo.
* La aplicación consulta un servicio externo de tasas de cambio sin enviarle datos personales, aunque ese servicio puede recibir tu dirección IP al conectarse.
* Valtiva integra Google Firebase para análisis de uso, informes de fallos y medición de rendimiento. Estos servicios recopilan datos técnicos y de uso, y algunos identificadores asociados a la instalación.

## 3. Datos que la aplicación trata directamente

Estos datos se procesan y almacenan solo en tu dispositivo; la aplicación no los envía a un servidor propio.

* **Historial de conversiones:** importes introducidos, monedas de origen y destino, resultado, tasa aplicada, fecha de la tasa y fecha y hora de la conversión. Se usa para mostrar tu historial y repetir conversiones.
* **Monedas favoritas:** pares de monedas guardados, para acceder rápido a tus conversiones habituales.
* **Tasas de cambio y lista de monedas descargadas:** permiten funcionar sin conexión y reducir solicitudes de red.
* **Preferencias:** tema de la aplicación y actualización automática diaria de tasas.

La aplicación no accede a la ubicación, la cámara, el micrófono, los contactos ni los archivos del dispositivo.

## 4. Tasas de cambio

Valtiva obtiene las tasas mediante solicitudes HTTPS a la API pública de Frankfurter (api.frankfurter.dev) al abrir la aplicación, al actualizar manualmente y, si lo tienes activado, una vez al día en segundo plano.

Las solicitudes contienen solo códigos de moneda y fechas; no incluyen importes ni datos personales. Según la documentación pública de Frankfurter, ese servicio no recopila ni registra datos personales, direcciones IP ni URLs de solicitudes, y usa Cloudflare para caché y protección, que ofrece estadísticas de tráfico agregadas. Valtiva no controla ese servicio.

Las tasas provienen de terceros y pueden no representar valores de mercado en tiempo real, por lo que las conversiones deben considerarse únicamente como una referencia. Valtiva no ofrece asesoría financiera ni servicios de cambio de divisas.

## 5. Servicios de terceros: Google Firebase

Los siguientes SDK de Google Firebase recopilan datos que Google gestiona como proveedor de servicios; no son datos que Valtiva recopile por sí misma.

### Firebase Analytics

Se usa para comprender cómo se utiliza la aplicación y mejorarla. Valtiva envía eventos como conversión realizada, moneda seleccionada, monedas intercambiadas, favorito añadido, quitado o seleccionado, tasas actualizadas, historial abierto, ajustes abiertos y actualización diaria completada. Estos eventos solo incluyen códigos de moneda; nunca se envían importes ni resultados de conversiones.

Firebase Analytics también recopila automáticamente un identificador de instancia de la aplicación, eventos automáticos, información del dispositivo y de la versión de Android, idioma, versión de la aplicación y ubicación aproximada derivada de la dirección IP. La aplicación declara además el permiso de identificador de publicidad (AD_ID) incluido por Google Analytics, por lo que ese identificador puede recopilarse si está disponible. Valtiva no lo usa con fines publicitarios.

### Firebase Crashlytics

Se usa para detectar y corregir fallos. Recopila informes de fallos y errores con trazas de pila, tipo y mensaje de la excepción, marca de tiempo, versión de la aplicación, modelo y sistema del dispositivo, memoria y espacio en disco, si el dispositivo tiene root e identificadores de instalación y de sesión. Según Google, se conservan 90 días.

### Firebase Performance Monitoring

Se usa para medir el rendimiento, por ejemplo los tiempos de arranque. Recopila el ID de instalación de Firebase, modelo y sistema del dispositivo, uso de memoria y CPU, operador y tipo de red, país derivado de la IP, idioma, versión de la aplicación y duración de trazas automáticas. Según Google, los datos asociados a la IP se conservan 30 días y los asociados a la instalación, 60 días.

Google trata esta información conforme a su Política de Privacidad y a los términos de procesamiento de datos de Firebase. Valtiva no vende datos personales ni los comparte con terceros distintos de los indicados aquí.

## 6. Datos que Valtiva no recopila

Valtiva no recopila ni solicita nombre, correo electrónico, teléfono, dirección, credenciales, información de pago o financiera personal, ubicación precisa, contactos, fotos, vídeos, audio, mensajes ni archivos. La aplicación no muestra publicidad.

## 7. Copias de seguridad de Android

La aplicación permite las copias de seguridad automáticas de Android. Según la configuración de tu dispositivo y de tu cuenta de Google, el historial y los favoritos podrían incluirse en la copia de seguridad de Google y restaurarse en otro dispositivo. Puedes desactivarlas en los ajustes de Android.

## 8. Seguridad

Las comunicaciones con el servicio de tasas de cambio usan HTTPS y los SDK de Firebase transmiten sus datos con cifrado en tránsito. Los datos locales se guardan en el almacenamiento privado de la aplicación, protegido por el aislamiento de aplicaciones de Android; no se aplica cifrado adicional propio. Ningún sistema puede garantizar una seguridad absoluta.

## 9. Conservación y eliminación

* **Datos locales:** permanecen en tu dispositivo hasta que borres los datos de la aplicación desde los ajustes de Android o la desinstales. Los favoritos pueden quitarse individualmente; Valtiva no ofrece por ahora una opción dentro de la aplicación para borrar el historial.
* **Datos de Firebase:** se conservan en los servidores de Google durante los periodos indicados en la sección 5. Valtiva no tiene cuentas ni identificador de usuario propio, por lo que no puede vincular esos datos a una persona concreta. Puedes escribir al contacto de la sección 1 para consultar o solicitar la eliminación de datos asociados a tu dispositivo, y se atenderá en la medida en que sea técnicamente posible.
* **Cuentas:** Valtiva no permite crear cuentas, por lo que no existe un procedimiento de eliminación de cuenta.

## 10. Tus derechos

Según la legislación que te sea aplicable, puedes tener derecho a acceder, rectificar, suprimir u oponerte al tratamiento de tus datos personales. Para ejercerlos, usa el contacto de la sección 1.

## 11. Privacidad de menores

Valtiva no está dirigida a menores de edad y no solicita deliberadamente información personal de niños.

## 12. Cambios en esta política

Esta Política de Privacidad puede actualizarse para reflejar cambios en Valtiva, en los servicios utilizados o en los requisitos legales. La fecha de «Última actualización» indica la versión vigente.

---

**Valtiva**
Conversión de divisas de forma sencilla.
""".trimIndent()

/** [AboutValtivaMarkdown] parsed once, since the copy is a compile-time constant. */
internal val AboutValtivaBlocks: List<MarkdownBlock> by lazy { parseMarkdown(AboutValtivaMarkdown) }

/** [PrivacyPolicyMarkdown] parsed once, since the copy is a compile-time constant. */
internal val PrivacyPolicyBlocks: List<MarkdownBlock> by lazy { parseMarkdown(PrivacyPolicyMarkdown) }
