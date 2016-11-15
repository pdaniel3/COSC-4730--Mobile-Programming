package edu.uwyo.pdaniel3.battlebots;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Controller extends Activity implements Button.OnClickListener
{

    private boolean gameOver;
    private boolean readyToWrite;
    private String serverAddress;

    // The message to send to the server to communicate
    private String messageToSend;

    // Variables the user can add points to
    private int armor;
    private int power;
    private int scanPower;

    // Health
    private int HP;
    private TextView currentHP;

    // Errors
    private Dialog errorMessage;
    private TextView errorMessageText;

	/**/
	/* Buttons */

    // Error message
    private Button errorMessageButton;

    // START move directions
    private Button moveUpLeft;
    private Button moveUp;
    private Button moveUpRight;
    private Button moveLeft;
    private Button moveRight;
    private Button moveDownLeft;
    private Button moveDown;
    private Button moveDownRight;

    // Shot directions
    private Button shotUpLeft;
    private Button shotUp;
    private Button shotUpRight;
    private Button shotLeft;
    private Button shotRight;
    private Button shotDownLeft;
    private Button shotDown;
    private Button shotDownRight;

    // Scan
    private Button scanButton;
	/* END Buttons */
	/**/

    // Network connection
    private Socket connection;

    // Game logic thread
    private Thread thread;

    // Called when Activity is created
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // Initialize
        super.onCreate(savedInstanceState);
        setContentView(R.layout.controller);
        Bundle extras = getIntent().getExtras();

        // Hide the Action Bar
        this.getActionBar().hide();

        //setup all needed variables and dialogs
        errorMessage = new Dialog(this);
        errorMessage.setContentView(R.layout.errormessage);
        errorMessage.setTitle("Error!");

        errorMessageText = (TextView) errorMessage.findViewById(R.id.errorMessageText);

        errorMessageButton = (Button) errorMessage.findViewById(R.id.errorMessageButton);
        errorMessageButton.setOnClickListener(this);

        currentHP = (TextView) findViewById(R.id.currentHP);

        // Init move buttons and listeners
        moveUpLeft = (Button) findViewById(R.id.moveUpLeft);
        moveUpLeft.setOnClickListener(this);
        moveUp = (Button) findViewById(R.id.moveUp);
        moveUp.setOnClickListener(this);
        moveUpRight = (Button) findViewById(R.id.moveUpRight);
        moveUpRight.setOnClickListener(this);
        moveLeft = (Button) findViewById(R.id.moveLeft);
        moveLeft.setOnClickListener(this);
        moveRight = (Button) findViewById(R.id.moveRight);
        moveRight.setOnClickListener(this);
        moveDownLeft = (Button) findViewById(R.id.moveDownLeft);
        moveDownLeft.setOnClickListener(this);
        moveDown = (Button) findViewById(R.id.moveDown);
        moveDown.setOnClickListener(this);
        moveDownRight = (Button) findViewById(R.id.moveDownRight);
        moveDownRight.setOnClickListener(this);

        // Init shot buttons and listeners
        shotUpLeft = (Button) findViewById(R.id.shotUpLeft);
        shotUpLeft.setOnClickListener(this);
        shotUp = (Button) findViewById(R.id.shotUp);
        shotUp.setOnClickListener(this);
        shotUpRight = (Button) findViewById(R.id.shotUpRight);
        shotUpRight.setOnClickListener(this);
        shotLeft = (Button) findViewById(R.id.shotLeft);
        shotLeft.setOnClickListener(this);
        shotRight = (Button) findViewById(R.id.shotRight);
        shotRight.setOnClickListener(this);
        shotDownLeft = (Button) findViewById(R.id.shotDownLeft);
        shotDownLeft.setOnClickListener(this);
        shotDown = (Button) findViewById(R.id.shotDown);
        shotDown.setOnClickListener(this);
        shotDownRight = (Button) findViewById(R.id.shotDownRight);
        shotDownRight.setOnClickListener(this);

        // Scan button
        scanButton = (Button) findViewById(R.id.scanButton);
        scanButton.setOnClickListener(this);

        // Game variables
        gameOver = false;
        serverAddress = extras.getString("serverAddress");
        armor = extras.getInt("armor");
        power = extras.getInt("power");
        scanPower = extras.getInt("scanPower");
        HP = armor+1;
        currentHP.setText(" "+HP);

        // Get ready to write
        readyToWrite = true;

        // Send first message
        messageToSend = "phil" + " "  + armor + " " + power + " " + scanPower;

        // Thread stuff
        thread = new Thread(new doNetwork());
        thread.start();
    }

    @Override
    public void onClick(View view) {
        // Error message dialog button
        if (view == errorMessageButton) {
            // If the okay button is pressed, end the game
            errorMessage.dismiss();
            gameOver = true;

            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            finish();
        }

        // Move buttons
        else if (view == moveUpLeft) {
            messageToSend = "move -1 -1";
        } else if (view == moveUp) {
            messageToSend = "move 0 -1";
        } else if (view == moveUpRight) {
            messageToSend = "move 1 -1";
        } else if (view == moveLeft) {
            messageToSend = "move -1 0";
        } else if (view == moveRight) {
            messageToSend = "move 1 0";
        } else if (view == moveDownLeft) {
            messageToSend = "move -1 1";
        } else if (view == moveDown) {
            messageToSend = "move 0 1";
        } else if (view == moveDownRight) {
            messageToSend = "move 1 1";
        } else if (view == shotUpLeft) {
            messageToSend = "shot 315";
        } else if (view == shotUp) {
            messageToSend = "shot 0";
        } else if (view == shotUpRight) {
            messageToSend = "shot 45";
        } else if (view == shotLeft) {
            messageToSend = "shot 270";
        } else if (view == shotRight) {
            messageToSend = "shot 90";
        } else if (view == shotDownLeft) {
            messageToSend = "shot 225";
        } else if (view == shotDown) {
            messageToSend = "shot 180";
        } else if (view == shotDownRight) {
            messageToSend = "shot 135";
        }

        // Scan button
        else if (view == scanButton) {
            messageToSend = "scan";
        }
    }

    @Override
    //If the back button is pushed, close the connection, end the game, and stop the thread
    protected void onStop() {
        super.onStop();
        gameOver = true;

        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        thread = null;
    }

    //Creates the handler to make messaging easier
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            int handlerMethod = msg.getData().getInt("handlerMethod");
            if (handlerMethod == 1) {
                errorMessageText.setText(msg.getData().getString("msg"));
                errorMessage.show();
            } else if (handlerMethod == 2) {
                Toast.makeText(getApplicationContext(), msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();
            } else {
                System.out.println(msg.getData().getString("msg"));
            }
        }

    };

    //makes the message to send to the handler
    public void makeMessage(String str, int handlerMethod) {
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putString("msg", str);
        b.putInt("handlerMethod", handlerMethod);
        msg.setData(b);
        handler.sendMessage(msg);
    }

    class doNetwork  implements Runnable
    {
        public void run() {
            int port = 3012;
            makeMessage("host is "+ serverAddress +"\n", 4);
            try{
                //Connect to the specified server
                InetAddress serverAddr = InetAddress.getByName(serverAddress);
                makeMessage("Attempting to connect..." + serverAddress +"\n", 4);
                connection = new Socket(serverAddr, port);
                //get the writer and reader
                PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(connection.getOutputStream())),true);
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                while (!gameOver) {
                    try {
                        String str = in.readLine();
                        String[] data = str.split(" ");

                        //If we get an info read, start parsing it
                        while (data[0].equals("Info")) {
                            //if we're dead or it's game over, end the game
                            makeMessage(str, 2);
                            if (data[1].equals("Dead") || data[1].equals("GameOver")) {
                                gameOver = true;
                                break;
                            }
                            str = in.readLine();
                            data = str.split(" ");
                        }

                        //If the info section ended the game, well, end the game
                        if (gameOver)
                        {
                            makeMessage("Game over!", 1);
                            break;
                        }

                        //If we get a status read, parse the data
                        if (data[0].equals("Status"))
                        {
                            readyToWrite = true;
                            if (Integer.parseInt(data[5]) < HP)
                            {
                                currentHP.setText(data[5]);
                                HP = Integer.parseInt(data[5]);
                            }
                        }

                        //After the read, write the data specified by they button we pushed
                        if (readyToWrite && !messageToSend.equals("noop"))
                        {
                            String[] messageData = messageToSend.split(" ");

                            //If we got a setup read, send our setup data
                            if (data[0].equals("setup"))
                            {
                                out.println(messageToSend);
                            }
                            //If we hit a move button, move our bot if it can
                            else if (messageData[0].equals("move"))
                            {
                                if (Integer.parseInt(data[3]) < 0)
                                {
                                    makeMessage("Can't move yet!", 2);
                                }
                                else
                                {
                                    out.println(messageToSend);
                                }
                            }
                            //If we hit a shoot button, shoot, if we can
                            else if (messageData[0].equals("shot"))
                            {
                                if (Integer.parseInt(data[4]) < 0)
                                {
                                    makeMessage("Can't shoot yet!", 2);
                                }
                                else
                                {
                                    out.println(messageToSend);
                                }
                            }
                            //Otherwise, if it's a scan command, run a scan and parse the data while
                            //printing it to the client
                            else if (messageData[0].equals("scan"))
                            {
                                out.println(messageToSend);
                                str = in.readLine();
                                data = str.split(" ");

                                while (data[0].equals("scan") && !data[1].equals("done"))
                                {
                                    makeMessage(str, 2);
                                    str = in.readLine();
                                    data = str.split(" ");
                                }

                                makeMessage(str, 2);
                            }

                            messageToSend = "noop";
                            readyToWrite = false;
                        }

                        out.println("noop");
                        Thread.sleep(400);
                    }
                    //If we can't read or write, end the game for the client to avoid issues
                    catch(Exception e)
                    {
                        makeMessage("Error happened sending/receiving\n", 4);
                        gameOver = true;
                    }
                }
            }
            catch (Exception e)
            {
                //If we can't connect, and the game isn't over, pop up a dialog
                //to let the client know and end the game for the client
                if (!gameOver)
                {
                    makeMessage("Unable to connect...", 1);
                    gameOver = true;
                }
            }
        }
    }
}