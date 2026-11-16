package com.example

import com.example.util.MathEvaluator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class MathEvaluatorTest {

    @Test
    fun evaluatesStandardExpressionsWithParenthesesAndSymbols() {
        val result = MathEvaluator.evaluateExpression("(15+5)×4÷2")
        assertNotNull(result)
        assertEquals(40.0, result!!, 0.0001)
    }

    @Test
    fun evaluatesPortugueseSpokenMultiplication() {
        val pair = MathEvaluator.extractAndEvaluateSpokenMath("quanto é 150 vezes 4")
        assertNotNull(pair)
        assertEquals(600.0, pair!!.second, 0.0001)
        assertEquals("600", MathEvaluator.formatResult(pair.second))
    }

    @Test
    fun evaluatesPortuguesePercentage() {
        val pair = MathEvaluator.extractAndEvaluateSpokenMath("calcule 10 por cento de 250")
        assertNotNull(pair)
        assertEquals(25.0, pair!!.second, 0.0001)
    }

    @Test
    fun handlesDivisionByZeroGracefully() {
        assertNull(MathEvaluator.evaluateExpression("100÷0"))
    }
}
