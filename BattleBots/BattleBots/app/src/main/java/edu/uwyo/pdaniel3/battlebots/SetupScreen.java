package edu.uwyo.pdaniel3.battlebots;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SetupScreen extends Activity implements Button.OnClickListener
{
    // Text views
    TextView remainingPoints;
    TextView errorMessageText;

    // Text boxes
    EditText serverAddress;
    EditText armor;
    EditText power;
    EditText scanPower;

    // Button
    Button connectButton;
    Button cancelButton;
    Button errorMessageButton;

    // Buttons for increasing or decreasing armor, power, and scan power
    Button decArmorButton;
    Button incArmorButton;
    Button decPowerButton;
    Button incPowerButton;
    Button decScanButton;
    Button incScanButton;

    // The error dialog
    Dialog errorMessage;

    // Store picked variables
    int armorValue;
    int	powerValue;
    int	scanPowerValue;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState){

        // Initialize
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setupscreen);

        //setup all needed variables as well as dialog and default exit text values
        serverAddress = (EditText) findViewById(R.id.serverAddress);
        armor = (EditText) findViewById(R.id.armorPicker);
        armor.setText("1");
        power = (EditText) findViewById(R.id.powerPicker);
        power.setText("1");
        scanPower = (EditText) findViewById(R.id.scanPicker);
        scanPower.setText("1");

        errorMessage = new Dialog(this);
        errorMessage.setContentView(R.layout.errormessage);
        errorMessage.setTitle("Error!");

        errorMessageText = (TextView) errorMessage.findViewById(R.id.errorMessageText);

        errorMessageButton = (Button) errorMessage.findViewById(R.id.errorMessageButton);
        errorMessageButton.setOnClickListener(this);

        connectButton = (Button) findViewById(R.id.connectButton);
        connectButton.setOnClickListener(this);

        cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(this);

        /* BEGIN Increment and decrement buttons */
        decArmorButton = (Button) findViewById(R.id.decArmorButton);
        decArmorButton.setOnClickListener(this);

        incArmorButton = (Button) findViewById(R.id.incArmorButton);
        incArmorButton.setOnClickListener(this);

        decPowerButton = (Button) findViewById(R.id.decPowerButton);
        decPowerButton.setOnClickListener(this);

        incPowerButton = (Button) findViewById(R.id.incPowerButton);
        incPowerButton.setOnClickListener(this);

        decScanButton = (Button) findViewById(R.id.decScanButton);
        decScanButton.setOnClickListener(this);

        incScanButton = (Button) findViewById(R.id.incScanButton);
        incScanButton.setOnClickListener(this);
        /* END Increment and decrement buttons */

    }

    @Override
    public void onClick(View view) {
        if(view == decArmorButton){
            if(Integer.parseInt(armor.getText().toString()) != 1){
                armorValue = Integer.parseInt(armor.getText().toString());
                armorValue--;
                armor.setText(Integer.toString(armorValue));
            }
        } else if (view == incArmorButton){
            if(Integer.parseInt(armor.getText().toString()) != 5){
                armorValue = Integer.parseInt(armor.getText().toString());
                armorValue++;
                armor.setText(Integer.toString(armorValue));
            }
        } else if (view == decPowerButton){
            if(Integer.parseInt(power.getText().toString()) != 1){
                powerValue = Integer.parseInt(power.getText().toString());
                powerValue--;
                power.setText(Integer.toString(powerValue));
            }
        } else if (view == incPowerButton){
            if(Integer.parseInt(power.getText().toString()) != 5){
                powerValue = Integer.parseInt(power.getText().toString());
                powerValue++;
                power.setText(Integer.toString(powerValue));
            }
        } else if (view == decScanButton){
            if(Integer.parseInt(scanPower.getText().toString()) != 1){
                scanPowerValue = Integer.parseInt(scanPower.getText().toString());
                scanPowerValue--;
                scanPower.setText(Integer.toString(scanPowerValue));
            }
        } else if (view == incScanButton){
            if(Integer.parseInt(scanPower.getText().toString()) != 5){
                scanPowerValue = Integer.parseInt(scanPower.getText().toString());
                scanPowerValue++;
                scanPower.setText(Integer.toString(scanPowerValue));
            }
        } else if (view == connectButton) {
            //Parse ints from incoming strings
            try {
                // Armor, powr, and scan power
                armorValue = Integer.parseInt(armor.getText().toString()) - 1;
                powerValue = Integer.parseInt(power.getText().toString()) - 1;
                scanPowerValue = Integer.parseInt(scanPower.getText().toString()) - 1;

                //If it succeeds, we see if they fit our constraints
                if ((armorValue >= 0 && armorValue <= 4) && (powerValue >= 0 && powerValue <= 4)
                        && (scanPowerValue >= 0 && scanPowerValue <= 4) && (armorValue+powerValue+scanPowerValue <= 5)) {
                    String address = serverAddress.getText().toString();
                    Intent i = new Intent(getApplicationContext(), Controller.class);
                    i.putExtra("serverAddress", address);
                    i.putExtra("armor", armorValue);
                    i.putExtra("power", powerValue);
                    i.putExtra("scanPower", scanPowerValue);
                    startActivity(i);
                } else {
                    // Tell the user to check the input it doesn't succeed
                    errorMessageText.setText("Please enter valid allocations!\n(Must be greater than or equal to 1 and less than or equal to 5)");
                    errorMessage.show();
                }
            }

            //If parsing fails, we tell the user to check their input
            catch (java.lang.NumberFormatException e) {
                errorMessageText.setText("Invalid input! Please check all forms.");
                errorMessage.show();
            }
        } else if (view == cancelButton) {
            finish();
        } else if (view == errorMessageButton) {
            //If the "okay" button on the error dialog was pressed, close the dialog
            errorMessage.dismiss();
        }
    }
}