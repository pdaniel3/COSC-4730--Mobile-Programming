//Phil Daniels

package com.example.pdaniel3.tipcalculator;


import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;


public class Tip_Calculator extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "com.example.pdaniel3.tipcalculator.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tip__calculator);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the user presses the Get Total button
     */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);

       //Three variables for the three inputs
        EditText editText = (EditText) findViewById(R.id.edit_message);
        EditText editText2 = (EditText) findViewById(R.id.edit_message2);
        EditText editText3 = (EditText) findViewById(R.id.edit_message3);
        RadioButton radioButton = (RadioButton) findViewById(R.id.radioButton_id);
        RadioButton radioButton2 = (RadioButton) findViewById(R.id.radioButton_id2);
        RadioButton radioButton3 = (RadioButton) findViewById(R.id.radioButton_id3);

        //Turn the user inputs into strings
        String tempMessage = editText.getText().toString();
        String tempMessage2 = editText2.getText().toString();
        String tempMessage3 = editText3.getText().toString();

        //Error handling if the user decides to be a jackass and tries to break my app
        //All of the annoying nested ifs and elses prevents the app from continuing after an error
        if (tempMessage.trim().equals("") || tempMessage.trim().equals(".")) {
            //EditText field was empty/invalid
            String message = "You didn't enter anything for the bill...";
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        } else if (tempMessage2.trim().equals("")|| tempMessage2.trim().equals("+") || tempMessage2.trim().equals("-")) {
            //EditText field was empty/invalid
            String message = "You didn't enter anything for the tip...";
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        } else if (tempMessage3.trim().equals("") || tempMessage3.trim().equals("+")|| tempMessage3.trim().equals("-")) {
            //EditText field was empty/invalid value, default set to 1
            tempMessage3 = "1";
        } else {
            //Takes the strings, turns them into doubles
            double totalBill = Double.parseDouble(tempMessage);
            double tipPercent = Double.parseDouble(tempMessage2);

            //More error handling
            if(totalBill > 100000000 || tipPercent > 100000000) {
                String message = "Either your bill or your tip percent was unrealistically large...";
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            } else {
                if (tipPercent < 0) {
                    //Handles negative inputs
                    String message = "You entered a negative tip...";
                    intent.putExtra(EXTRA_MESSAGE, message);
                    startActivity(intent);
                } else {
                    //Split variable into a double
                    double split = Double.parseDouble(tempMessage3);

                    //handles splits that would break my app
                    String splitError = ".";
                    if (split <= 0 || split > 5000) {
                        split = 1.0;
                        splitError = ", the split was set to 1.";
                    }

                    //turns percentage into useable double
                    //IE a 15% tip is the same as multiplying the total by 1.15
                    double tip = ((tipPercent / 100) * totalBill);

                    //Finally calculating the total
                    double total = (totalBill + tip) / split;

                    //if the user wants their answer rounded round it the way they specify, else display result
                    if (radioButton.isChecked() == true) {
                        //Rounds the total bill to the nearest integer
                        int roundedTotal = (int) Math.round(total);

                        //Display the rounded result
                        String message = "You owe $" + Integer.toString(roundedTotal) + splitError;
                        intent.putExtra(EXTRA_MESSAGE, message);
                        startActivity(intent);
                    } else if (radioButton2.isChecked() == true) {
                        //total bill plus a rounded tip

                        //so the split up tip is displayed and rounded evenly
                        int tipWithSplit = (int) Math.round(tip / split);
                        int roundedTip = (int) Math.round(tip);

                        //Set decimal precision
                        String result = String.format("%.2f", total);

                        String message = "You owe $" + total + " which includes a $" + tipWithSplit + " tip" + splitError;
                        intent.putExtra(EXTRA_MESSAGE, message);
                        startActivity(intent);

                    } else {
                        //Set precision on double and convert to string
                        String result = String.format("%.2f", total);

                        //Display the result
                        String message = "You owe $" + result + splitError;
                        intent.putExtra(EXTRA_MESSAGE, message);
                        startActivity(intent);
                    }

                }
            }
        }
    }
}