package com.example.calculator_app_1;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView ressoltv;
    StringBuilder expression = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ressoltv = findViewById(R.id.calculator_display);

        int[] buttonIds = {R.id.button_percent, R.id.button_st_CE, R.id.button_clear, R.id.button_cross,
                R.id.button_1x, R.id.button_x_sq, R.id.button_under_x, R.id.button_div,
                R.id.button_7, R.id.button_8, R.id.button_9, R.id.button_4,
                R.id.button_5, R.id.button_6, R.id.button_1, R.id.button_2,
                R.id.button_3, R.id.button_st_0, R.id.button_arithmatic, R.id.button_dot,
                R.id.button_equal, R.id.button_plus, R.id.button_minus, R.id.button_x};

        for (int id : buttonIds) {
            MaterialButton button = findViewById(id);
            if (button != null) {
                button.setOnClickListener(this);
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onClick(View v) {
        if (!(v instanceof MaterialButton)) return;

        MaterialButton button = (MaterialButton) v;
        String buttonText = button.getText().toString();

        if (buttonText.equals("CE") || buttonText.equals("C")) {
            expression.setLength(0);
            ressoltv.setText("0");
        } else if (buttonText.equals("⌫")) {  // Backspace
            if (expression.length() > 0) {
                expression.deleteCharAt(expression.length() - 1);
                ressoltv.setText(expression.length() > 0 ? expression.toString() : "0");
            }
        } else if (buttonText.equals("=")) {
            calculateResult();
        } else if (buttonText.equals("x²")) {
            applySquare();
        } else if (buttonText.equals("√x")) {
            applySquareRoot();
        } else if (buttonText.equals("1/x")) {
            applyReciprocal();
        } else {
            expression.append(buttonText);
            ressoltv.setText(expression.toString());
        }
    }

    private void calculateResult() {
        try {
            String expressionStr = expression.toString();
            expressionStr = expressionStr.replace("×", "*").replace("÷", "/");

            Context rhino = Context.enter();
            rhino.setOptimizationLevel(-1);
            Scriptable scope = rhino.initStandardObjects();

            String result = rhino.evaluateString(scope, expressionStr, "JavaScript", 1, null).toString();

            if (result.endsWith(".0")) {
                result = result.replace(".0", "");
            }

            expression.setLength(0);
            expression.append(result);
            ressoltv.setText(result);

            Context.exit();
        } catch (Exception e) {
            ressoltv.setText("Error");
            expression.setLength(0);
        }
    }

    private void applySquare() {
        try {
            double value = Double.parseDouble(expression.toString());
            double squared = value * value;
            expression.setLength(0);
            expression.append(squared);
            ressoltv.setText(String.valueOf(squared));
        } catch (Exception e) {
            ressoltv.setText("Error");
            expression.setLength(0);
        }
    }

    private void applySquareRoot() {
        try {
            double value = Double.parseDouble(expression.toString());
            if (value < 0) {
                ressoltv.setText("Error");
                expression.setLength(0);
                return;
            }
            double sqrtValue = Math.sqrt(value);
            expression.setLength(0);
            expression.append(sqrtValue);
            ressoltv.setText(String.valueOf(sqrtValue));
        } catch (Exception e) {
            ressoltv.setText("Error");
            expression.setLength(0);
        }
    }

    private void applyReciprocal() {
        try {
            double value = Double.parseDouble(expression.toString());
            if (value == 0) {
                ressoltv.setText("Error");
                expression.setLength(0);
                return;
            }
            double reciprocal = 1 / value;
            expression.setLength(0);
            expression.append(reciprocal);
            ressoltv.setText(String.valueOf(reciprocal));
        } catch (Exception e) {
            ressoltv.setText("Error");
            expression.setLength(0);
        }
    }
}
