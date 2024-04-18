package com.example.calculator

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import org.mozilla.javascript.Context
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

class AdvancedCalculator : AppCompatActivity() {

    private lateinit var result: TextView
    private lateinit var solution: TextView
    private var lastClickTime: Long = 0
    private val DOUBLE_CLICK_TIME_DELTA: Long = 600
    private var isExponentiationPending = false
    private var baseForExponentiation = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advanced_calculator)
        result = findViewById(R.id.result)
        solution = findViewById(R.id.solution)

        val buttons = listOf(
            R.id.button_AC,
            R.id.button_c,
            R.id.button_1,
            R.id.button_2,
            R.id.button_3,
            R.id.button_4,
            R.id.button_5,
            R.id.button_6,
            R.id.button_7,
            R.id.button_8,
            R.id.button_9,
            R.id.button_0,
            R.id.button_divide,
            R.id.button_plus,
            R.id.button_minus,
            R.id.button_multiplication,
            R.id.button_equals,
            R.id.button_percent,
            R.id.button_dot,
            R.id.button_switch_sign,
            R.id.button_ln,
            R.id.button_log,
            R.id.button_sqrt,
            R.id.button_power,
            R.id.button_power_xy,
            R.id.button_ctg,
            R.id.button_tan,
            R.id.button_cos,
            R.id.button_sin
        )


        buttons.forEach { buttonId ->
            findViewById<MaterialButton>(buttonId).setOnClickListener {
                onClick(it)
            }
        }
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
                "/" -> {
                    if (solution.text.isNotEmpty() && !solution.text.endsWith("/")) {
                        solution.text = "${solution.text}/"
                    } else {
                        Toast.makeText(this, "Enter a number first or check the expression", Toast.LENGTH_SHORT).show()
                    }
                }
                "^" -> {
                    if (solution.text.isNotEmpty() && !solution.text.endsWith("^")) {
                        isExponentiationPending = true
                        baseForExponentiation = solution.text.toString()
                        solution.text = "${solution.text}^"
                    } else {
                        Toast.makeText(this, "Enter a base number first or complete the exponentiation", Toast.LENGTH_SHORT).show()
                    }
                }


                "=" -> {
                    if (isExponentiationPending) {
                        val parts = solution.text.split("^")
                        if (parts.size == 2 && parts[0].isNotEmpty() && parts[1].isNotEmpty()) {
                            val base = parts[0]
                            val exponent = parts[1]
                            val expression = "Math.pow($base,$exponent)"
                            val resultValue = getResult(expression)
                            if (resultValue != "Err") {
                                result.text = resultValue
                                solution.text = resultValue
                            } else {
                                Toast.makeText(
                                    this,
                                    "Invalid expression, try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(this, "Complete the expression", Toast.LENGTH_SHORT)
                                .show()
                        }
                        isExponentiationPending = false
                    } else {
                        val expression = solution.text.toString()
                            .replace("sin", "Math.sin")
                            .replace("cos", "Math.cos")
                            .replace("tan", "Math.tan")
                            .replace("ctg", "1/Math.tan")
                            .replace("sqrt", "Math.sqrt")
                        val resultValue = getResult(expression)
                        if (resultValue != "Err") {
                            result.text = resultValue
                            solution.text = resultValue
                        } else {
                            Toast.makeText(
                                this,
                                "Invalid expression, try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                "x^2" -> {
                    if (solution.text.isNotEmpty()) {
                        val res = getResult("Math.pow(${solution.text},2)")
                        result.text = res
                        solution.text = res
                    } else {
                        Toast.makeText(this, "Niepoprawne wyrazenie", Toast.LENGTH_SHORT).show()
                    }
                }
                "ln", "log" -> {
                    if (solution.text.isNotEmpty()) {
                        val expression = if (buttonText == "ln") "Math.log(${solution.text})" else "Math.log10(${solution.text})"
                        val resultValue = getResult(expression)
                        if (resultValue != "Err") {
                            result.text = resultValue
                            solution.text = resultValue
                        } else {
                            Toast.makeText(this, "Invalid expression, try again", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Enter a valid number for $buttonText", Toast.LENGTH_SHORT).show()
                    }
                }


                "sin" -> {
                    if (solution.text.isNotEmpty() && solution.text.matches("-?\\d+(\\.\\d+)?".toRegex())) {
                        solution.text = "sin(${solution.text})"
                    } else {
                        Toast.makeText(
                            this,
                            "Niepoprawne dane wejściowe dla sinusa",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                "±" -> {
                    solution.text = toggleLastNumberSign(solution.text.toString())
                }

                "cos" -> {
                    if (solution.text.isNotEmpty() && solution.text.matches("-?\\d+(\\.\\d+)?".toRegex())) {
                        solution.text = "cos(${solution.text})"
                    } else {
                        Toast.makeText(
                            this,
                            "Niepoprawne dane wejściowe dla cosinusa",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                "tan" -> {
                    if (solution.text.isNotEmpty() && solution.text.matches("-?\\d+(\\.\\d+)?".toRegex())) {
                        solution.text = "tan(${solution.text})"
                    } else {
                        Toast.makeText(
                            this,
                            "Niepoprawne dane wejściowe dla tangensa",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                "ctg" -> {
                    if (solution.text.isNotEmpty() && solution.text.matches("-?\\d+(\\.\\d+)?".toRegex())) {
                        solution.text = "ctg(${solution.text})"
                    } else {
                        Toast.makeText(
                            this,
                            "Niepoprawne dane wejściowe dla cotangensa",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                "%" -> {
                    if (solution.text.isNotEmpty() && solution.text.matches("-?\\d+(\\.\\d+)?".toRegex())) {
                        try {
                            val number = solution.text.toString().toDouble()
                            val percentage = number / 100
                            solution.text = percentage.toString()
                            result.text = percentage.toString()
                        } catch (e: NumberFormatException) {
                            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show()
                            solution.text = "0"
                            result.text = "0"
                        }
                    }
                }

                else -> {
                    val currentText = solution.text.toString()
                    if (buttonText in listOf("+", "-", "*", "/")) {
                        // Sprawdzenie, czy ostatni znak jest operatorem i czy jest taki sam jak wciśnięty przycisk, blokuje dodanie go jeszcze raz
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
                        } else if (!currentText.contains(".")) {
                            solution.text = "${currentText}."
                        }
                    } else {
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

    fun getResult(data: String): String {
        return try {
            val context = Context.enter()
            context.optimizationLevel = -1
            val scriptable = context.initStandardObjects()
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
            if (decimal.abs() >= BigDecimal("1e8") || decimal.abs() < BigDecimal("1e-6") && decimal.compareTo(
                    BigDecimal.ZERO
                ) != 0
            ) {
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

        val regex = Regex("[-]?\\b\\d+\\.?\\d*\\)?$")
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

        return expression.substring(0, numberStartIndex) + newNumber + expression.substring(numberEndIndex + 1)
    }
}