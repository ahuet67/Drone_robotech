package com.example.android.joystickfordrone;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.android.transmissionencoder.SignalPPM;

public class MainActivity extends AppCompatActivity implements MyJoystickView.MyJoystickListener,MyJoystickView2.MyJoystickListener2{
    private ProgressBar progressbargaucheX;
    private ProgressBar progressBarGaucheY;
    private ProgressBar progressBarDroiteX;
    private ProgressBar progressBarDroiteY;

    private SignalPPM signalppm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        progressbargaucheX = (ProgressBar) findViewById(R.id.axeXgauche);
        progressBarGaucheY = (ProgressBar) findViewById(R.id.axeYgauche);

        progressBarDroiteY = (ProgressBar) findViewById(R.id.axeYdroite);
        progressBarDroiteX = (ProgressBar) findViewById(R.id.axeXdroite);
        progressBarDroiteX.setMax(100);
        progressBarDroiteY.setMax(100);
        progressBarDroiteX.setProgress(0);
        progressBarDroiteY.setProgress(0);

        progressBarGaucheY.setMax(100);
        progressBarGaucheY.setProgress(0);
        progressbargaucheX.setMax(100);
        progressbargaucheX.setProgress(0);

        signalppm = new SignalPPM(this);


        int startgenerating = signalppm.debutGeneration();
        if(startgenerating == -1) {
            Log.d("MainMehtod","ERROR ON STARTING THE GENERATION ");
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Erreur :");
            alert.setMessage("Erreur pendant la generation." );
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            alert.show();
        }


    }

    @Override
    public void onJoystickMoved(float xPourcent, float yPourcent, int source) {
        Log.d("Main Method","X pourcent :"+xPourcent+" Y pourcent "+yPourcent);
        progressbargaucheX.setProgress((int) xPourcent);
        progressBarGaucheY.setProgress((int) yPourcent);

        int valueMappedYChannel1 = signalppm.mapValues((int)yPourcent,0,100,0,255);
        int valueMappedXChannel2 = signalppm.mapValues((int) xPourcent,0,100,0,255);
        signalppm.setChannel(1,valueMappedYChannel1);
        signalppm.setChannel(2,valueMappedXChannel2);

    }

    @Override
    public void onMyJostickListener(float xPourcent, float yPourcent, int id) {
        Log.d("Main Method","X pourcent :"+xPourcent+" Y pourcent "+yPourcent);
        progressBarDroiteX.setProgress((int) xPourcent);
        progressBarDroiteY.setProgress((int) yPourcent);
        int valueMappedYChannel3 = signalppm.mapValues((int)yPourcent,0,100,0,255);
        int valueMappedXChannel4 = signalppm.mapValues((int) xPourcent,0,100,0,255);
        signalppm.setChannel(3,valueMappedYChannel3);
        signalppm.setChannel(4,valueMappedXChannel4);
    }
}
