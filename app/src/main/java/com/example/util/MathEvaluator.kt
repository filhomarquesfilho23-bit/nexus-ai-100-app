package com.example.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

object MathEvaluator {

    /**
     * Avalia expressões matemáticas com suporte a +, -, *, /, ×, ÷, %, parênteses e decimais.
     */
    fun evaluateExpression(rawExpression: String): Double? {
        val sanitized = rawExpression
            .replace("×", "*")
            .replace("÷", "/")
            .replace(",", ".")
            .replace(" ", "")
            .trim()

        if (sanitized.isEmpty()) return null

        return runCatching {
            Parser(sanitized).parse()
        }.getOrNull()?.takeIf { it.isFinite() }
    }

    /**
     * Interpreta contas faladas em português (ex.: "quanto é 150 vezes 4", "200 mais 50 dividido por 2").
     */
    fun extractAndEvaluateSpokenMath(spokenText: String): Pair<String, Double>? {
        var text = spokenText.lowercase(Locale("pt", "BR"))
            .replace("quanto é", "")
            .replace("quanto e", "")
            .replace("quanto dá", "")
            .replace("quanto da", "")
            .replace("calcule", "")
            .replace("calcula", "")
            .replace("resultado de", "")
            .replace("multiplicado por", "*")
            .replace("vezes", "*")
            .replace("dividido por", "/")
            .replace("sobre", "/")
            .replace("mais", "+")
            .replace("somado com", "+")
            .replace("menos", "-")
            .replace("subtraído de", "-")
            .replace("por cento de", "%*")
            .replace(" x ", "*")
            .replace(",", ".")
            .trim()

        // Match expression containing at least one digit and one operator
        val exprPattern = Regex("""[0-9()+*/.\s%-]+""")
        val candidate = exprPattern.findAll(text)
            .map { it.value.trim() }
            .filter { s -> s.any { it.isDigit() } && s.any { it in listOf('+', '-', '*', '/', '%') } }
            .maxByOrNull { it.length } ?: return null

        val value = evaluateExpression(candidate) ?: return null
        return candidate to value
    }

    fun formatResult(value: Double): String {
        val bd = BigDecimal.valueOf(value).setScale(6, RoundingMode.HALF_UP).stripTrailingZeros()
        val plain = bd.toPlainString()
        return if (plain == "-0") "0" else plain.replace(".", ",")
    }

    fun formatSpokenPortuguese(value: Double): String {
        val formatted = formatResult(value)
        return formatted.replace(",", " vírgula ")
    }

    private class Parser(private val input: String) {
        private var pos = -1
        private var ch = 0

        private fun nextChar() {
            pos++
            ch = if (pos < input.length) input[pos].code else -1
        }

        private fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < input.length) throw IllegalArgumentException("Unexpected character: ${ch.toChar()}")
            return x
        }

        private fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                when {
                    eat('+'.code) -> x += parseTerm()
                    eat('-'.code) -> x -= parseTerm()
                    else -> return x
                }
            }
        }

        private fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                when {
                    eat('*'.code) -> x *= parseFactor()
                    eat('/'.code) -> {
                        val divisor = parseFactor()
                        if (divisor == 0.0) throw ArithmeticException("Divisão por zero")
                        x /= divisor
                    }
                    else -> return x
                }
            }
        }

        private fun parseFactor(): Double {
            if (eat('+'.code)) return parseFactor()
            if (eat('-'.code)) return -parseFactor()

            var x: Double
            val startPos = pos
            if (eat('('.code)) {
                x = parseExpression()
                if (!eat(')'.code)) throw IllegalArgumentException("Missing closing parenthesis")
            } else if ((ch in '0'.code..'9'.code) || ch == '.'.code) {
                while ((ch in '0'.code..'9'.code) || ch == '.'.code) nextChar()
                x = input.substring(startPos, pos).toDouble()
            } else {
                throw IllegalArgumentException("Unexpected: ${ch.toChar()}")
            }

            if (eat('%'.code)) {
                x /= 100.0
            }
            return x
        }
    }
}
