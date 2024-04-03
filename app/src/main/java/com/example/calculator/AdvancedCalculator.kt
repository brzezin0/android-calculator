package com.example.calculator

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable

class AdvancedCalculator : AppCompatActivity() {

    private lateinit var result: TextView
    private lateinit var solution: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advanced_calculator)
        result = findViewById(R.id.result)
        solution = findViewById(R.id.solution)

        val buttons = listOf(
            R.id.button_c,
            R.id.button_1, R.id.button_2, R.id.button_3,
            R.id.button_4, R.id.button_5, R.id.button_6,
            R.id.button_7, R.id.button_8, R.id.button_9,
            R.id.button_0, R.id.button_AC, R.id.button_divide,
            R.id.button_plus, R.id.button_minus, R.id.button_multiplication,
            R.id.button_equals, R.id.button_dot, R.id.button_open_bracket, R.id.close_bracket,
            R.id.button_switch_sign, R.id.button_ln, R.id.button_log, R.id.button_sqrt, R.id.button_power,
            R.id.button_power_xy, R.id.button_ctg, R.id.button_tan, R.id.button_cos, R.id.button_sin
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
                    solution.text = if (solution.text.isNotEmpty()) solution.text.substring(0, solution.length() - 1) else "0"
                }
                "AC" -> {
                    solution.text = "0"
                    result.text = "0"
                }
                "√" -> {
                    if(solution.text.isNotEmpty() && solution.text.matches("-?\\d+(\\.\\d+)?".toRegex())){
                        solution.text = "Math.pow(${solution.text},2)"
                    }else{
                        Toast.makeText(this, "Błędne wyrażenie, spróbuj jeszcze raz", Toast.LENGTH_SHORT).show()
                    }
                }

                "=" -> {
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
                        Toast.makeText(this, "Błędne wyrażenie, spróbuj jeszcze raz", Toast.LENGTH_SHORT).show()
                    }
                }
                "x^2"->{
                    if(solution.text.isNotEmpty() ){
                        val res = getResult("Math.pow(${solution.text},2)")
                        result.text = res
                        solution.text = res
                    }else{
                        Toast.makeText(this,"Niepoprawne wyrazenie", Toast.LENGTH_SHORT).show()
                    }
                }
                "sin" -> {
                    if (solution.text.isNotEmpty() && solution.text.matches("-?\\d+(\\.\\d+)?".toRegex())) {
                        solution.text = "sin(${solution.text})"
                    } else {
                        Toast.makeText(this, "Niepoprawne dane wejściowe dla sinusa", Toast.LENGTH_SHORT).show()
                    }
                }
                "±" -> {
                    if(solution.text.isNotEmpty()){
                        if(solution.text.get(0)== '-'){
                            solution.text = solution.text.substring(1)
                        }else{
                            solution.text = "-" + solution.text
                        }
                    }
                }
                "cos" -> {
                if (solution.text.isNotEmpty() && solution.text.matches("-?\\d+(\\.\\d+)?".toRegex())) {
                    solution.text = "cos(${solution.text})"
                } else {
                    Toast.makeText(this, "Niepoprawne dane wejściowe dla cosinusa", Toast.LENGTH_SHORT).show()
                }
            }
                "tan" -> {
                    if (solution.text.isNotEmpty() && solution.text.matches("-?\\d+(\\.\\d+)?".toRegex())) {
                        solution.text = "tan(${solution.text})"
                    } else {
                        Toast.makeText(this, "Niepoprawne dane wejściowe dla tangensa", Toast.LENGTH_SHORT).show()
                    }
                }
                "ctg" -> {
                    if (solution.text.isNotEmpty() && solution.text.matches("-?\\d+(\\.\\d+)?".toRegex())) {
                        solution.text = "ctg(${solution.text})"
                    } else {
                        Toast.makeText(this, "Niepoprawne dane wejściowe dla cotangensa", Toast.LENGTH_SHORT).show()
                    }
                }

                else -> {
                    if (solution.text.toString() == "0") {
                        solution.text = buttonText
                    } else {
                        if (result.text != "Err") {
                            solution.text = "${solution.text}$buttonText"
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
            context.evaluateString(scriptable, data, "JavaScript", 1, null).toString()
        } catch (e: Exception) {
            "Err"
        } finally {
            Context.exit()
        }
    }


}