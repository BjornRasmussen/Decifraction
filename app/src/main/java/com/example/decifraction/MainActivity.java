package com.example.decifraction;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText decimalInput;
    TextView calculateTextView;
//    TextView resultTextView;
    ClipboardManager clipboardManager;
    RecyclerView resultRecyclerView;
    MainRecyclerViewAdapter adapter;

    ArrayList<String> calculatedValues;

    final char equals = '=';
    final char approxEquals = '≈';
    final char veryApproxEquals = '~';

    final String NO_FRACTION_FOUND_MESSAGE = "No fractions found for";
    final String NO_INPUT_GIVEN_MESSAGE = "Please enter a decimal";
    final String COPIED_TO_CLIPBOARD = "Copied to clipboard:\n";

    final String pi = "3.14159265358979323846264338327950288419716939937510582097494459230781640628620899";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // data to populate the RecyclerView with
        calculatedValues = new ArrayList<>();

        // set up the RecyclerView
        resultRecyclerView = findViewById(R.id.resultRecyclerView);
        resultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MainRecyclerViewAdapter(this, calculatedValues);

        // Add dividers to the recyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(resultRecyclerView.getContext(), 1); // FIXME is the orientation right?
        resultRecyclerView.addItemDecoration(dividerItemDecoration);

        // Set up adapter onClick methods - for when the user clicks on a displayed result.
        adapter.setClickListener(new MainRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                /////////////// COPY VALUE TO INPUT AREA //////////////
                decimalInput.setText(removeAllAfterFirstSpace(calculatedValues.get(position)));
                decimalInput.requestFocus();
                int pos = decimalInput.getText().length();
                decimalInput.setSelection(pos);

                // Make the keyboard appear
                InputMethodManager manager = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                manager.showSoftInput(decimalInput, InputMethodManager.SHOW_IMPLICIT);

                ////////////// Copy to Clipboard //////////////////
                String text = calculatedValues.get(position);
                text = getFractionPart(text);
                ClipData clipData = ClipData.newPlainText("text", text);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getApplicationContext(), COPIED_TO_CLIPBOARD + text, Toast.LENGTH_SHORT).show();
                vibrate();
            }
        });

        // Set the adapter as the adapter of the recyclerView
        resultRecyclerView.setAdapter(adapter);

        // Set up the swipe to remove capability.
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView resultRecylerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                calculatedValues.remove(viewHolder.getAdapterPosition());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                final View foregroundView = resultRecyclerView.findContainingItemView(viewHolder.itemView);

                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
            }
        };

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(resultRecyclerView);


        // Set up all non recyclerView related stuff
        decimalInput = (EditText) findViewById(R.id.decimalInput);
        calculateTextView = (TextView) findViewById(R.id.calculateTextView);

        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        calculateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This runs when the calculate button is clicked.
                vibrate();
                useDecimalToFractionConversion();
            }
        });

        

        decimalInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!decimalInput.getText().toString().equals("")) {
                    useDecimalToFractionConversion();
                }
                return false;
            }
        });
    }

    public void useDecimalToFractionConversion() {
        String textFromInput = decimalInput.getText().toString();
        String resultString = "";

        if (textFromInput.equals("")) {
            Toast.makeText(getApplicationContext(), NO_INPUT_GIVEN_MESSAGE, Toast.LENGTH_SHORT).show();
        } else {
            boolean inputValid = true;
            try {
                new BigDecimal(textFromInput);
            } catch (Exception e) {
                inputValid = false;
            }
            if (!inputValid) {
                Toast.makeText(getApplicationContext(), (NO_FRACTION_FOUND_MESSAGE + " " + textFromInput), Toast.LENGTH_SHORT).show();
                decimalInput.setText("");
            } else if (new BigDecimal(textFromInput).compareTo(new BigDecimal("0")) == 0) {
                resultString = textFromInput + " = 0 / 1";
            } else {


                DecimalToFractionConverter converter = new DecimalToFractionConverter();
                Fraction result;
                result = calculateFractionComplex(textFromInput);
                try {
                    resultString = textFromInput + " " + getEqualsType(new BigDecimal(textFromInput), result) + " " + result;
                } catch (Exception e) {
                    // No fraction being found creates an exception, which is caught here.
                    resultString = textFromInput + " = ?";
                    Toast.makeText(getApplicationContext(), (NO_FRACTION_FOUND_MESSAGE + " " + textFromInput), Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (!resultString.equals("") && !resultString.equals("-")) {
            calculatedValues.add(0, resultString);
            adapter.notifyDataSetChanged();
            resultRecyclerView.smoothScrollToPosition(0);
        }
        decimalInput.setText("");
    }

    private char getEqualsType(BigDecimal decimal, Fraction fraction) {
        if (decimal.compareTo(fraction.getValue()) == 0) {
            return equals;
        } else {
            BigDecimal inputValue = decimal;
            BigDecimal valueOfFraction = fraction.getValue();

            if (Utils.valuesAlmostEqual(inputValue, valueOfFraction)) {
                return approxEquals;
            } else {
                return veryApproxEquals;
            }
        }
    }

    private void vibrate() {
        vibrate(40);
    }

    private void vibrate(int time) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            // This vibration action was deprecated in API 26
            vibrator.vibrate(time);
        }
    }

    private String removeAllAfterFirstSpace(String input) {
        input = input.trim();
        String output = "";
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == ' ') {
                break;
            }
            output += input.charAt(i);
        }
        return output;
    }

    private String getFractionPart(String input) {
        String output = "";
        if (input.contains(equals + "")) {
            output = input.split(equals + "")[1];
        } else if (input.contains(approxEquals + "")) {
            output = input.split(approxEquals + "")[1];
        } else if (input.contains(veryApproxEquals + "")) {
            output = input.split(veryApproxEquals + "")[1];
        }
        return output.trim();
    }

    private Fraction calculateFractionComplex(String input) {
        DecimalToFractionConverter converter = new DecimalToFractionConverter();
        Fraction fraction = converter.calcFraction(input);

        return fraction;
     }

    private Fraction[] getEveryModifier() {
        // TODO make this method automatically generate values instead of just returning a predefined list.
        MathValue one = new MathValue("1");
        Fraction[] output = new Fraction[102];
        output[100] = new Fraction(new MathValue("Pi", new BigDecimal(pi), true), one);
        output[101] = new Fraction(one, new MathValue("Pi", new BigDecimal(pi), true));
        // FIXME GET FIRST 100 values - sqrt(1 - 100);
        for (int i = 1; i < 101; i++) {
            output[i] = new Fraction(new SquareRootOperator(new MathValue(i + "")), one);
        }
        return output;
    }

    private Fraction getMostAccurateFraction(Fraction[] input) {
        int bestFraction = 0;
        for (int i = 1; i < input.length; i++) {
            if (input[bestFraction].inaccuracy.compareTo(input[i].inaccuracy) == -1) {
                bestFraction = 1;
            }
        }
        return input[bestFraction];
    }
}