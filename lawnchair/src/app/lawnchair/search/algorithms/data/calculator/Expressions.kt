package app.lawnchair.search.algorithms.data.calculator

import app.lawnchair.search.algorithms.data.calculator.internal.Evaluator
import app.lawnchair.search.algorithms.data.calculator.internal.Expr
import app.lawnchair.search.algorithms.data.calculator.internal.Function
import app.lawnchair.search.algorithms.data.calculator.internal.Parser
import app.lawnchair.search.algorithms.data.calculator.internal.Scanner
import app.lawnchair.search.algorithms.data.calculator.internal.Token
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.cos as kotlinCos
import kotlin.math.ln as kotlinLn
import kotlin.math.log10 as kotlinLog10
import kotlin.math.pow as kotlinPow
import kotlin.math.sin as kotlinSin
import kotlin.math.sqrt as kotlinSqrt
import kotlin.math.tan as kotlinTan

class ExpressionException(message: String) : RuntimeException(message)

@Suppress("unused")
class Expressions {
    private val evaluator = Evaluator()

    init {
        define("pi", BigDecimal(Math.PI).setScale(33, RoundingMode.HALF_EVEN))
        define("e", BigDecimal(Math.E).setScale(33, RoundingMode.HALF_EVEN))

        evaluator.addFunction("abs") { arguments ->
            if (arguments.size != 1) {
                throw ExpressionException(
                    "abs requires one argument",
                )
            }

            arguments.first().abs()
        }

        evaluator.addFunction("sum") { arguments ->
            if (arguments.isEmpty()) {
                throw ExpressionException(
                    "sum requires at least one argument",
                )
            }

            arguments.reduce { sum, bigDecimal ->
                sum.add(bigDecimal)
            }
        }

        evaluator.addFunction("floor") { arguments ->
            if (arguments.size != 1) {
                throw ExpressionException(
                    "floor requires one argument",
                )
            }

            arguments.first().setScale(0, RoundingMode.FLOOR)
        }

        evaluator.addFunction("ceil") { arguments ->
            if (arguments.size != 1) {
                throw ExpressionException(
                    "ceil requires one argument",
                )
            }

            arguments.first().setScale(0, RoundingMode.CEILING)
        }

        evaluator.addFunction("round") { arguments ->
            if (arguments.size !in listOf(1, 2)) {
                throw ExpressionException(
                    "round requires either one or two arguments",
                )
            }

            val value = arguments.first()
            val scale = if (arguments.size == 2) arguments.last().toInt() else 0

            value.setScale(scale, roundingMode)
        }

        evaluator.addFunction("min") { arguments ->
            if (arguments.isEmpty()) {
                throw ExpressionException(
                    "min requires at least one argument",
                )
            }

            arguments.minOrNull()!!
        }

        evaluator.addFunction("max") { arguments ->
            if (arguments.isEmpty()) {
                throw ExpressionException(
                    "max requires at least one argument",
                )
            }

            arguments.maxOrNull()!!
        }

        evaluator.addFunction("if") { arguments ->
            val condition = arguments[0]
            val thenValue = arguments[1]
            val elseValue = arguments[2]

            if (condition != BigDecimal.ZERO) {
                thenValue
            } else {
                elseValue
            }
        }

        evaluator.addFunction("sqrt") { arguments ->
            if (arguments.size != 1) {
                throw ExpressionException("sqrt requires one argument")
            }
            BigDecimal.valueOf(kotlinSqrt(arguments.first().toDouble()))
        }

        evaluator.addFunction("pow") { arguments ->
            if (arguments.size != 2) {
                throw ExpressionException("pow requires two arguments")
            }
            val base = arguments[0].toDouble()
            val exp = arguments[1].toDouble()
            BigDecimal.valueOf(base.kotlinPow(exp))
        }

        evaluator.addFunction("sin") { arguments ->
            if (arguments.size != 1) {
                throw ExpressionException("sin requires one argument")
            }
            BigDecimal.valueOf(kotlinSin(arguments.first().toDouble()))
        }

        evaluator.addFunction("cos") { arguments ->
            if (arguments.size != 1) {
                throw ExpressionException("cos requires one argument")
            }
            BigDecimal.valueOf(kotlinCos(arguments.first().toDouble()))
        }

        evaluator.addFunction("tan") { arguments ->
            if (arguments.size != 1) {
                throw ExpressionException("tan requires one argument")
            }
            BigDecimal.valueOf(kotlinTan(arguments.first().toDouble()))
        }

        evaluator.addFunction("log") { arguments ->
            if (arguments.size != 1) {
                throw ExpressionException("log requires one argument")
            }
            BigDecimal.valueOf(kotlinLog10(arguments.first().toDouble()))
        }

        evaluator.addFunction("ln") { arguments ->
            if (arguments.size != 1) {
                throw ExpressionException("ln requires one argument")
            }
            BigDecimal.valueOf(kotlinLn(arguments.first().toDouble()))
        }
    }

    val precision: Int
        get() = evaluator.mathContext.precision

    val roundingMode: RoundingMode
        get() = evaluator.mathContext.roundingMode

    fun setPrecision(precision: Int): Expressions {
        evaluator.mathContext = MathContext(precision, roundingMode)

        return this
    }

    fun setRoundingMode(roundingMode: RoundingMode): Expressions {
        evaluator.mathContext = MathContext(precision, roundingMode)

        return this
    }

    fun define(name: String, value: Long): Expressions {
        define(name, value.toString())

        return this
    }

    fun define(name: String, value: Double): Expressions {
        define(name, value.toString())

        return this
    }

    fun define(name: String, value: BigDecimal): Expressions {
        define(name, value.toPlainString())

        return this
    }

    fun define(name: String, expression: String): Expressions {
        val expr = parse(expression)
        evaluator.define(name, expr)

        return this
    }

    fun addFunction(name: String, function: Function): Expressions {
        evaluator.addFunction(name, function)

        return this
    }

    fun addFunction(name: String, func: (List<BigDecimal>) -> BigDecimal): Expressions {
        evaluator.addFunction(name) { arguments -> func(arguments) }

        return this
    }

    fun eval(expression: String): BigDecimal {
        return evaluator.eval(parse(expression))
    }

    /**
     * eval an expression then round it with {@link Evaluator#mathContext} and call toEngineeringString <br>
     * if error will return message from Throwable
     * @param expression String
     * @return String
     */
    fun evalToString(expression: String): String {
        return try {
            evaluator.eval(parse(expression)).round(evaluator.mathContext).stripTrailingZeros()
                .toEngineeringString()
        } catch (e: Throwable) {
            e.cause?.message ?: e.message ?: "unknown error"
        }
    }

    private fun parse(expression: String): Expr {
        return parse(scan(expression))
    }

    private fun parse(tokens: List<Token>): Expr {
        return Parser(tokens).parse()
    }

    private fun scan(expression: String): List<Token> {
        return Scanner(expression, evaluator.mathContext).scanTokens()
    }
}
