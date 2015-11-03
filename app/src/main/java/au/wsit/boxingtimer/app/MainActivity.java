package au.wsit.boxingtimer.app;

import android.app.Activity;

import android.media.MediaPlayer;
import android.os.CountDownTimer;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ProgressBar;

import android.widget.TextView;



public class MainActivity extends ActionBarActivity {

    // Set vars
    //Timer Display
    TextView MinutesDisplay;
    TextView SecondsDisplay;
    ProgressBar CountDown;
    EditText Minutes_EDITEXT;
    EditText Seconds_EDITTEXT;
    EditText RoundRestDuration_EDITTEXT;
    int RoundRestTime = 30; // Default is 30

    // Initialise the CountDown Timers
    CountDownTimer FIGHTTIMER;
    CountDownTimer BREAKTIMER;


    // Keep track of the edit text boxes
    int MinCount = 0;
    int SecCount = 0;

    long Seconds = 0;
    long Minutes;
    long SecondstoTake;

    // Round rest duration edit text box int value
    int REST_DURATION_VALUE;





    // Keep track of how many rounds we do
    int NumOfRounds = 0;
    TextView RoundCount;

    // Check if the countdown timers are running
    boolean FightTimerRunning = false;
    boolean BreakerTimerRunning = false;

    boolean isFightPressed = false;
    boolean isStopPressed = false;
    boolean isResetPressed = false;

    Button STOP_BUTTON;

    // Used for storing temporary values for the stop button so it can restart at the correct point
    int minTemp;
    int secTemp;

    // Progress bar stuff
    long InitialSeconds;

    // Sounds stuff
    MediaPlayer BellStart;
    MediaPlayer BellStop;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect to xml objects
        MinutesDisplay = (TextView) findViewById(R.id.ID_MINUTES);
        SecondsDisplay = (TextView) findViewById(R.id.ID_SECONDS);


        Minutes_EDITEXT = (EditText) findViewById(R.id.ID_Minutes_EditText);
        Seconds_EDITTEXT = (EditText) findViewById(R.id.ID_Seconds_EditText);
        RoundRestDuration_EDITTEXT = (EditText) findViewById(R.id.ID_RoundDuration_EditText);

        CountDown = (ProgressBar) findViewById(R.id.progressBar);




        RoundCount = (TextView) findViewById(R.id.ID_RoundCount_SetTextView);
        STOP_BUTTON = (Button) findViewById(R.id.ID_STOP_BUTTON);

        // Link the sounds
        BellStart = MediaPlayer.create(MainActivity.this, R.raw.bellstart);
        BellStop = MediaPlayer.create(MainActivity.this, R.raw.bellstop);

        // Keep the display on - add on off button in settings
        KeepDisplayOn(this, true);

        // Default Round and Rest time
        Minutes_EDITEXT.setText("02");
        Seconds_EDITTEXT.setText("00");
        RoundRestDuration_EDITTEXT.setText("30");



    }

    // Keep the screen on
    public void KeepDisplayOn(Activity activity, boolean On)
    {
        if (On == true)
        {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        else if (On == false)
        {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        }
    }

    // Buttons

    public void MinDownButton(View view)
    {
        // Make sure we can't show negative numbers
        MinCount = GetMinutesSet();
        if (MinCount <= 0)
        {
            Minutes_EDITEXT.setText(Integer.toString(0));
        }
        else
        {
            MinCount = MinCount - 1;
            Minutes_EDITEXT.setText(Integer.toString(MinCount));
        }

    }

    public void MinUpButton(View view)
    {
        MinCount = GetMinutesSet();
        MinCount = MinCount + 1;
        Minutes_EDITEXT.setText(Integer.toString(MinCount));

    }

    public void SecDownButton(View view)
    {
        SecCount = GetSecondsSet();

        // Make sure we can't show negative numbers
        if (SecCount <= 0)
        {
            Seconds_EDITTEXT.setText(Integer.toString(0));
        }
        else
        {
            SecCount = SecCount - 1;
            Seconds_EDITTEXT.setText(Integer.toString(SecCount));
        }
    }

    public void SecUpButton(View view)
    {
        SecCount = GetSecondsSet();
        SecCount = SecCount + 1;
        Seconds_EDITTEXT.setText(Integer.toString(SecCount));

    }

    public void RoundUpButton(View view)
    {
            // Get the value

            REST_DURATION_VALUE = Integer.valueOf(RoundRestDuration_EDITTEXT.getText().toString());



            REST_DURATION_VALUE = REST_DURATION_VALUE + 10;
            RoundRestDuration_EDITTEXT.setText("" + REST_DURATION_VALUE);

    }

    public void RoundDownButton(View view)
    {
        // Get the value
        REST_DURATION_VALUE = Integer.valueOf(RoundRestDuration_EDITTEXT.getText().toString());

        if (REST_DURATION_VALUE < 10)
        {
            RoundRestDuration_EDITTEXT.setText("0");
        }
        else
        {
            REST_DURATION_VALUE = REST_DURATION_VALUE - 10;
            RoundRestDuration_EDITTEXT.setText("" + REST_DURATION_VALUE);
        }

    }

    public void ResetRounds(View view)
    {
        RoundCount.setText("" + 0);
    }


    public void StopButton(View view)
    {

        //isFightPressed = false;



        // Check if we are in a break
        if (BreakerTimerRunning == true && FightTimerRunning == false)
        {
            BREAKTIMER.cancel();
            //BreakerTimerRunning = false;
            isStopPressed = true;


        }

        else if (isStopPressed == false && FightTimerRunning == true)
        {
            // First we cancel the timer
            FIGHTTIMER.cancel();
            // Then we get what the timer is at

            String Mins = MinutesDisplay.getText().toString();
            String Secs = SecondsDisplay.getText().toString();

            minTemp = Integer.valueOf(Mins);
            secTemp = Integer.valueOf(Secs);
            // Set boolean to true
            isStopPressed = true;
            FightTimerRunning = false;
            // Change the button to display Start
            STOP_BUTTON.setText("START");
        }

        else if (isStopPressed == true && FightTimerRunning == false) // Check if the timer was already pressed
        {
            // Restart the timer with the values of the TextViews
            StartCountDown(minTemp, secTemp);
            isStopPressed = false;

            // Set the stop button back to say STOP as it would have been START
            STOP_BUTTON.setText("STOP");
        }


    }


    // Gets the data from the edit text boxes
    public int GetMinutesSet()
    {

        int Minutes = Integer.valueOf(Minutes_EDITEXT.getText().toString());

        return Minutes;
    }

    public int GetSecondsSet()
    {
        int Seconds = Integer.valueOf(Seconds_EDITTEXT.getText().toString());
        return Seconds;
    }

    // Calls the get mins and secs then calls the countdown
    public void FightButton(View view)
    {

            // Check if we're already running
            if (isFightPressed == false)
            {
                StartCountDown(GetMinutesSet(), GetSecondsSet()); // StartCountDown Only takes a long
                isFightPressed = true;
            }
            else
            {
                // Do nothing
            }
    }

    public void ResetButton(View view)
    {
        CountDown.setProgress(0);
        isFightPressed = false;

        if (isResetPressed == false)
        {
            if (FightTimerRunning == true)
            {
                FIGHTTIMER.cancel();
            }
            if (BreakerTimerRunning == true)
            {
                BREAKTIMER.cancel();
            }
            isResetPressed = true;

            MinutesDisplay.setText("00");
            SecondsDisplay.setText("00");
        }
        else if (isResetPressed == true)
        {
            isResetPressed = false;
        }

    }


    // Takes the amount of seconds
    public void StartCountDown(int Minutes, int Seconds)
    {

        // Play the start sound
        BellStart.start();

        // Get the user settings from the EditText boxes
       // Seconds = GetSecondsSet();
       // Minutes = GetMinutesSet();
        int SecMins = Minutes * 60; // Multiply the minutes by 60 to get the seconds we need
        long Interval = 1000; // How long between changes - 1 second
        Seconds = Seconds + SecMins; // Add the minutes onto the seconds
        Seconds = Seconds * 1000;


            FIGHTTIMER = new CountDownTimer(Seconds, Interval) {
                public void onTick(long millisUntilFinished)
                {
                    FightTimerRunning = true;

                    TickTimer(millisUntilFinished);


                }

                public void onFinish() {
                    // Set to finish timer
                    FightTimerRunning = false;
                    isFightPressed = false;
                    // Increment the roundcount
                    NumOfRounds++;
                    RoundCount.setText(Integer.toString(NumOfRounds));

                    SecondsDisplay.setText("00");
                    MinutesDisplay.setText("00");


                    // Start rest chrono
                    StartBreakTimer(GetRestTime() * 1000, 1000);


                    // Play the bell stop sound
                    BellStop.start();
                }
            };
            FIGHTTIMER.start();
        }

        public int GetRestTime()
        {

            RoundRestTime = Integer.valueOf(RoundRestDuration_EDITTEXT.getText().toString());
            return RoundRestTime;

        }



    // Method for timing the rest duration - Gets called from the onFinish Method within the StartCoutDown Method
    public void StartBreakTimer(long TimerStart, long Interval)
    {
       BREAKTIMER = new CountDownTimer(TimerStart, Interval) {
           @Override
           public void onTick(long millisUntilFinished)
           {
               BreakerTimerRunning = true;
               setProgress(millisUntilFinished);
               if (millisUntilFinished / 1000 < 10)
               {
                   SecondsDisplay.setText("0" + Long.toString(millisUntilFinished / 1000));



               }
               else
               {
                   SecondsDisplay.setText(Long.toString(millisUntilFinished / 1000));

               }
           }

           @Override
           public void onFinish()
           {
               // Check if we're running
               BreakerTimerRunning = false;
               // Clear the break count down timer
               SecondsDisplay.setText("00");
               // Restart the CountDown
               StartCountDown(GetMinutesSet(), GetSecondsSet());
               CountDown.setProgress(0);


           }
       };
        BREAKTIMER.start();
    }

    public void setProgress(long millisUntilFinished)
    {
        int percent;
        int secondsTotal;

        secondsTotal = Integer.valueOf(RoundRestDuration_EDITTEXT.getText().toString());
        int millis = (int)millisUntilFinished;
        percent = millis / secondsTotal * 100;
        CountDown.setProgress(percent);



    }



    // Takes Seconds and Minutes as arguments then set the text views
    public void TickTimer(long millisUntilFinished)
    {
        Seconds = millisUntilFinished / 1000;

        // First find out of we're dealing with seconds or minutes
        if (Seconds < 60)
        {
            // Add a 0 to make it look nice
            if (Seconds < 10)
            {
                SecondsDisplay.setText("0" + Long.toString(Seconds));
            }
            else
            {
                SecondsDisplay.setText(Long.toString(Seconds));
                // When we get below 60 then make the minutes show as 0
                MinutesDisplay.setText("00");
            }
        }
        if (Seconds > 60) // Check if we need to display Minutes too
        {
            // Get how many mins we need to display
            Minutes = Seconds / 60;
            // Push to GUI
            // Check if we are below 10 and display a 0 infront of the minutes if we are
            if (Minutes < 10)
            {
                MinutesDisplay.setText("0" + Long.toString(Minutes));
            }
            else
            {
                MinutesDisplay.setText(Long.toString(Minutes));
            }

            // Display seconds minus the minutes we just displayed

            // Determine how many seconds to take from the seconds count

            SecondstoTake = Minutes * 60;
            Seconds = Seconds - SecondstoTake;

            if (Seconds < 10)
            {
                SecondsDisplay.setText("0" + Long.toString(Seconds));
            }
            else
            {
                SecondsDisplay.setText(Long.toString(Seconds));
            }


        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
