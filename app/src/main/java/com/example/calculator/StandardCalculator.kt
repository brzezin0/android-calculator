package com.example.calculator

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

class StandardCalculator : AppCompatActivity() {

    private lateinit var result: TextView
    private lateinit var solution: TextView
    private var lastClickTime: Long = 0
    private val DOUBLE_CLICK_TIME_DELTA: Long = 600
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_standard_calculator)
        result = findViewById(R.id.result)
        solution = findViewById(R.id.solution)

        val buttons = listOf(
            R.id.button_c, R.id.button_7, R.id.button_8, R.id.button_9,
            R.id.button_multiplication, R.id.button_sqrt, R.id.button_4,
            R.id.button_5, R.id.button_6, R.id.button_minus, R.id.button_swapSign,
            R.id.button_1, R.id.button_2, R.id.button_3, R.id.button_add,
            R.id.button_AC, R.id.button_dot, R.id.button_0, R.id.button_equals,
            R.id.button_divide
        )

        buttons.forEach { buttonId ->
            findViewById<MaterialButton>(buttonId).setOnClickListener {
                onClick(it)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("solutionState", solution.text.toString())
        outState.putString("resultState", result.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        solution.text = savedInstanceState.getString("solutionState", "0")
        result.text = savedInstanceState.getString("resultState", "0")
    }

    private fun onClick(view: View) {
        if (view is MaterialButton) {
            val buttonText = view.text.toString()

            when (buttonText) {
                "C" -> {
                    val clickTime = System.currentTimeMillis()
                    if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                        solution.text = "0"
                        result.text = "0"
                        Toast.makeText(this, "All cleared", Toast.LENGTH_SHORT).show()
                    } else {
                        solution.text = if (solution.text.isNotEmpty()) solution.text.substring(0, solution.length() - 1) else "0"
                    }
                    lastClickTime = clickTime
                }
                "AC" -> {
                    solution.text = "0"
                    result.text = "0"
                }
                "√" -> {
                    if (solution.text.isNotEmpty()) {
                        solution.text = "Math.sqrt(${solution.text})"
                        val resultValue = getResult(solution.text.toString())
                        if (resultValue != "Err") {
                            result.text = resultValue
                            solution.text = resultValue
                        } else {
                            Toast.makeText(this, "Invalid expression, try again", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Enter a number first", Toast.LENGTH_SHORT).show()
                    }
                }

                "±" -> {
                    solution.text = toggleLastNumberSign(solution.text.toString())
                }
                "=" -> {
                    val expression = solution.text.toString()

                        .replace("sqrt", "Math.sqrt")
                    val resultValue = getResult(expression)
                    if (resultValue != "Err") {
                        result.text = resultValue
                        solution.text = resultValue
                    } else {
                        Toast.makeText(this, "Błędne wyrażenie, spróbuj jeszcze raz", Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {
                    val currentText = solution.text.toString()
                    if (buttonText in listOf("+", "-", "*", "/")) {
                        if (currentText.isNotEmpty() && listOf("+", "-", "*", "/").contains(currentText.last().toString())) {
                            if (currentText.last().toString() == buttonText) {
                                Toast.makeText(this, "You cannot enter two '$buttonText' in a row", Toast.LENGTH_SHORT).show()
                            } else {
                                // Usunięcie poprzedniego operatora, jeśli nowy zostanie wprowadzony (zamiana operatorów)
                                solution.text = currentText.dropLast(1) + buttonText
                            }
                        } else {
                            solution.text = "$currentText$buttonText"
                        }
                    } else if (buttonText == ".") {
                        if (currentText.isEmpty() || currentText.endsWith("(") || currentText.endsWith("+") || currentText.endsWith("-") || currentText.endsWith("*") || currentText.endsWith("/")) {
                            solution.text = "${currentText}0."
                        } else {
                            val lastNumberSegment = currentText.split(Regex("[+\\-*/]")).last()
                            if (!lastNumberSegment.contains(".")) {
                                solution.text = "${currentText}."
                            }
                        }
                    }
                    else {
                        if (currentText == "0") {
                            if (buttonText != "0") {
                                solution.text = buttonText
                            }
                        } else {
                            if (result.text != "Err") {
                                solution.text = "${currentText}$buttonText"
                            }
                        }
                    }
                }

            }
        }
    }


    private fun getResult(data: String): String {
        return try {
            val context = Context.enter()
            context.optimizationLevel = -1
            val scriptable: Scriptable = context.initStandardObjects()
            val result = context.evaluateString(scriptable, data, "JavaScript", 1, null).toString()
            BigDecimal(result).setScale(5, RoundingMode.HALF_UP).toString()
            formatNumber(result)
        } catch (e: Exception) {
            "Err"
        } finally {
            Context.exit()
        }
    }

    private fun formatNumber(number: String): String {
        return try {
            val decimal = BigDecimal(number).setScale(6, RoundingMode.HALF_UP)
            if (decimal.abs() >= BigDecimal("1e8") || decimal.abs() < BigDecimal("1e-6") && decimal.compareTo(BigDecimal.ZERO) != 0) {
                val formatter = DecimalFormat("0.######E0")
                formatter.format(decimal)
            } else {
                val formatter = DecimalFormat("#.######")
                formatter.format(decimal)
            }
        } catch (e: NumberFormatException) {
            "Err"
        }
    }

    private fun toggleLastNumberSign(expression: String): String {
        if (expression.isEmpty()) return expression
        // Regex to find the last number with optional sign and considering its operation context.
        val regex = Regex("""(?<=^|[\+\-\*/])(-?\d+\.?\d*)$""")
        val matchResult = regex.find(expression)

        matchResult ?: return expression

        val number = matchResult.value
        val numberStartIndex = matchResult.range.first
        val numberEndIndex = matchResult.range.last

        val newNumber = if (number.startsWith("-")) {
            number.substring(1)
        } else {
            "-$number"
        }

        val expressionBeforeNumber = expression.substring(0, numberStartIndex)
        val expressionAfterNumber = expression.substring(numberEndIndex + 1)

        if (expressionBeforeNumber.endsWith("-") && newNumber.startsWith("-")) {
            return expressionBeforeNumber.dropLast(1) + "+" + newNumber.drop(1) + expressionAfterNumber
        } else if (expressionBeforeNumber.endsWith("+") && newNumber.startsWith("-")) {
            return expressionBeforeNumber + newNumber + expressionAfterNumber
        } else {
            return expressionBeforeNumber + newNumber + expressionAfterNumber
        }
    }
}