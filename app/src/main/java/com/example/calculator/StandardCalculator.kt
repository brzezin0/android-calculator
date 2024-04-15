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
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

class StandardCalculator : AppCompatActivity() {

    private lateinit var result: TextView
    private lateinit var solution: TextView
    private var lastClickTime: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_standard_calculator)
        result = findViewById(R.id.result)
        solution = findViewById(R.id.solution)

        val buttons = listOf(
            R.id.button_c,
            R.id.button_1, R.id.button_2, R.id.button_3,
            R.id.button_4, R.id.button_5, R.id.button_6,
            R.id.button_7, R.id.button_8, R.id.button_9,
            R.id.button_0, R.id.button_AC, R.id.button_divide,
            R.id.button_plus, R.id.button_minus, R.id.button_multiplication,
            R.id.button_equals, R.id.button_dot
        )


        buttons.forEach { buttonId ->
            findViewById<MaterialButton>(buttonId).setOnClickListener {
                onClick(it)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("solutionState",solution.text.toString())
        outState.putString("resultState",result.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        solution.text=savedInstanceState.getString("solutionState", "0")
        result.text=savedInstanceState.getString("resultState", "0")

    }

    private fun onClick(view: View) {
        if (view is MaterialButton) {
            val buttonText = view.text.toString()
            val clickTime = System.currentTimeMillis()

            when (buttonText) {
                "C" -> {
                    if (clickTime - lastClickTime < 1000) {
                        solution.text = "0"
                        result.text = "0"
                    } else {
                        solution.text = if (solution.text.isNotEmpty() && solution.text != "0") solution.text.substring(0, solution.length() - 1) else "0"
                    }
                    lastClickTime = clickTime
                }
                "AC" -> {
                    solution.text = "0"
                    result.text = "0"
                }
                "=" -> {
                    val resultValue = getResult(solution.text.toString())
                    if (resultValue != "Err") {
                        result.text = resultValue
                        solution.text=resultValue
                    }else{
                        Toast.makeText(this, "Błędne wyrażenie, spróbuj jeszcze raz", Toast.LENGTH_SHORT).show()
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
            val result = context.evaluateString(scriptable, data, "JavaScript", 1, null).toString()
            BigDecimal(result).setScale(5, RoundingMode.HALF_UP).toString()
            formatNumber(result)

        } catch (e: Exception) {
            "Err"
        } finally {
            Context.exit()
        }
    }
    fun formatNumber(number: String): String {
        val decimal = BigDecimal(number).setScale(6, RoundingMode.HALF_UP)
        val formatter = DecimalFormat("#.#####") // Formatowanie, które ucinają niepotrzebne zera
        return formatter.format(decimal)
    }

}
