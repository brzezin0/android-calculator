package com.example.calculator

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.calculator.ui.theme.CalculatorTheme
import androidx.activity.compose.setContent
import android.view.View;
import android.content.Intent;

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val aboutButton = findViewById<Button>(R.id.aboutButton)

        aboutButton.setOnClickListener{view->
            val intent = Intent(this,AboutActivity::class.java);
            startActivity(intent)
        }
    }
}

@Composable
fun Greeting( modifier: Modifier = Modifier) {
    Text(
            text = "Calculator",
            modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CalculatorTheme {
        Greeting()
    }
}