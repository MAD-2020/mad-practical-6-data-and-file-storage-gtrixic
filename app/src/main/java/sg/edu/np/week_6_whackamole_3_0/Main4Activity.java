package sg.edu.np.week_6_whackamole_3_0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class Main4Activity extends AppCompatActivity {

    /* Hint:
        1. This creates the Whack-A-Mole layout and starts a countdown to ready the user
        2. The game difficulty is based on the selected level
        3. The levels are with the following difficulties.
            a. Level 1 will have a new mole at each 10000ms.
                - i.e. level 1 - 10000ms
                       level 2 - 9000ms
                       level 3 - 8000ms
                       ...
                       level 10 - 1000ms
            b. Each level up will shorten the time to next mole by 100ms with level 10 as 1000 second per mole.
            c. For level 1 ~ 5, there is only 1 mole.
            d. For level 6 ~ 10, there are 2 moles.
            e. Each location of the mole is randomised.
        4. There is an option return to the login page.
     */
    private static final String FILENAME = "Main4Activity.java";
    private static final String TAG = "Whack-A-Mole3.0!";
    CountDownTimer readyTimer;
    CountDownTimer newMolePlaceTimer;
    Button backButton;
    int scores = 0;
    TextView score;
    Integer currentLevel;
    String username;
    ArrayList<Button> buttonList = new ArrayList<>();
    final Toast[] checkToast = {null};
    MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);



    private void readyTimer(){
        /*  HINT:
            The "Get Ready" Timer.
            Log.v(TAG, "Ready CountDown!" + millisUntilFinished/ 1000);
            Toast message -"Get Ready In X seconds"
            Log.v(TAG, "Ready CountDown Complete!");
            Toast message - "GO!"
            belongs here.
            This timer countdown from 10 seconds to 0 seconds and stops after "GO!" is shown.
         */
        readyTimer = new CountDownTimer(10000,1000) {

            public void onTick(long millisUntilFinished) {
                Log.v(TAG, "Ready Countdown!" + millisUntilFinished/1000);
                if (checkToast[0] != null){
                    checkToast[0].cancel();
                }
                checkToast[0] = Toast.makeText(getApplicationContext(), "Get Ready In " + millisUntilFinished/1000 + " seconds.", Toast.LENGTH_SHORT);
                checkToast[0].show();
            }

            public void onFinish() {
                Log.v(TAG, "Ready countdown complete!");
                if (checkToast[0] != null)
                {
                    checkToast[0].cancel();
                }
                checkToast[0] = Toast.makeText(getApplicationContext(), "GO!", Toast.LENGTH_SHORT);
                checkToast[0].show();
                placeMoleTimer();
            }
        };
        readyTimer.start();
    }
    private void placeMoleTimer(){
        /* HINT:
           Creates new mole location each second.
           Log.v(TAG, "New Mole Location!");
           setNewMole();
           belongs here.
           This is an infinite countdown timer.
         */
        Integer cdInterval = (11 - currentLevel) * 1000;
        newMolePlaceTimer = new CountDownTimer(cdInterval, cdInterval) {

            public void onTick(long l) {
                Log.v(TAG, "New Mole Location!");
                setNewMole();
            }

            @Override
            public void onFinish() {
                newMolePlaceTimer.start();
            }
        };
        newMolePlaceTimer.start();
    }
    private static final int[] BUTTON_IDS = {
            /* HINT:
                Stores the 9 buttons IDs here for those who wishes to use array to create all 9 buttons.
                You may use if you wish to change or remove to suit your codes.*/
            R.id.button1a,R.id.button1b,R.id.button1c,R.id.button2a,R.id.button2b,R.id.button2c,R.id.button3a,R.id.button3b,R.id.button3c
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        /*Hint:
            This starts the countdown timers one at a time and prepares the user.
            This also prepares level difficulty.
            It also prepares the button listeners to each button.
            You may wish to use the for loop to populate all 9 buttons with listeners.
            It also prepares the back button and updates the user score to the database
            if the back button is selected.
         */
        score = findViewById(R.id.currentScore);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserScore();
                Intent chooseLevel = new Intent(Main4Activity.this, Main3Activity.class);
                chooseLevel.putExtra("username", username);
                startActivity(chooseLevel);
            }
        });

        Intent getInfo = getIntent();
        Bundle b = getInfo.getExtras();
        username = b.getString("Username");
        currentLevel = b.getInt("Level");


        for(final int id : BUTTON_IDS){
            /*  HINT:
            This creates a for loop to populate all 9 buttons with listeners.
            You may use if you wish to remove or change to suit your codes.
            */
            Button button = findViewById(id);
            buttonList.add(button);
        }
    }
    @Override
    protected void onStart(){
        super.onStart();
        readyTimer();
    }

    public void setNewMole()
    {
        /* Hint:
            Clears the previous mole location and gets a new random location of the next mole location.
            Sets the new location of the mole. Adds additional mole if the level difficulty is from 6 to 10.
         */
        Random ran = new Random();
        int randomLocation = ran.nextInt(9);
        if (currentLevel < 6)
        {
            Button rb = buttonList.get(randomLocation);
            for (Button b : buttonList) {
                if (b == rb) {
                    b.setText("*");
                } else {
                    b.setText("O");
                }
            }
        }
        else
        {
            int randomLocation2 = ran.nextInt(9);
            if (randomLocation == randomLocation2)
            {
                randomLocation2 = ran.nextInt(9);
            }
            Button rb = buttonList.get(randomLocation);
            Button rb2 = buttonList.get(randomLocation2);
            for (Button b : buttonList) {
                if (b == rb || b == rb2) {
                    b.setText("*");
                } else {
                    b.setText("O");
                }
            }
        }
    }

    private void updateUserScore()
    {

     /* Hint:
        This updates the user score to the database if needed. Also stops the timers.
        Log.v(TAG, FILENAME + ": Update User Score...");
      */
        newMolePlaceTimer.cancel();
        readyTimer.cancel();

        UserData user = dbHandler.findUser(username);
        int highScore = user.getScores().get(currentLevel-1);
        if (scores > highScore)
        {
            Log.v(TAG, FILENAME + ": Update User Score...");
            user.getScores().set(currentLevel-1, scores);
            dbHandler.deleteAccount(username);
            dbHandler.addUser(user);
        }
    }

    public void OnClickButton(View v) {
        Button b = (Button) v;
        if (Mole(b)) {
            scores++;
            score.setText("" + scores);
            Log.v(TAG, "Hit, score added!");
            setNewMole();
        } else {
            if (scores > 0) {
                scores--;
                score.setText("" + scores);
                Log.v(TAG, "Missed, score deducted!");
            }
            else
            {
                scores = 0;
                score.setText("" + scores);
                Log.v(TAG, "Missed, score deducted!");
            }
        }

    }

    public boolean Mole(Button b) {
        if (b.getText() == "*") {
            return true;
        } else {
            return false;
        }
    }

}
