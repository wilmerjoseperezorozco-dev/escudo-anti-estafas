package com.escudoantiestafas.app.core

// Patrones de SMS de código de verificación: WhatsApp y bancos/billeteras
// digitales colombianas comunes (Bancolombia, Nequi, Daviplata, Davivienda).
// No se necesita saber quién envía el SMS: basta con que el texto tenga la
// forma de un código OTP para considerarlo sensible.
object OtpPatterns {

    private val PATRONES = listOf(
        Regex("""tu c[oó]digo de whatsapp es[:\s]*\d{3}-?\d{3}""", RegexOption.IGNORE_CASE),
        Regex("""c[oó]digo de verificaci[oó]n[:\s]*\d{4,8}""", RegexOption.IGNORE_CASE),
        Regex("""codigo (otp|de seguridad|de acceso)[:\s]*\d{4,8}""", RegexOption.IGNORE_CASE),
        Regex("""\bOTP\b[:\s]*\d{4,8}""", RegexOption.IGNORE_CASE),
        Regex("""no comparta(s)? este c[oó]digo""", RegexOption.IGNORE_CASE),
        // Más permisivo: la palabra "código" cerca de un número de 4-8
        // dígitos, sin importar la frase exacta alrededor (cubre variantes
        // como "tu código es 374617" o "código: 374617").
        Regex("""c[oó]digo\D{0,15}\d{4,8}""", RegexOption.IGNORE_CASE),
    )

    fun esMensajeConCodigoOtp(cuerpoSms: String): Boolean =
        PATRONES.any { it.containsMatchIn(cuerpoSms) }
}
