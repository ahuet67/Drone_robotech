package com.example.paul.myapplication;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;


public class MainActivity extends AppCompatActivity implements MyJoystickView.MyJoystickListener,MyJoystickView2.MyJoystickListener2{
    private ProgressBar progressbargaucheX;
    private ProgressBar progressBarGaucheY;
    private ProgressBar progressBarDroiteX;
    private ProgressBar progressBarDroiteY;

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


    }

    @Override
    public void onJoystickMoved(float xPourcent, float yPourcent, int source) {
        Log.d("Main Method","X pourcent :"+xPourcent+" Y pourcent "+yPourcent);
        progressbargaucheX.setProgress((int) xPourcent);
        progressBarGaucheY.setProgress((int) yPourcent);
    }

    @Override
    public void onMyJostickListener(float xPourcent, float yPourcent, int id) {
        Log.d("Main Method","X pourcent :"+xPourcent+" Y pourcent "+yPourcent);
        progressBarDroiteX.setProgress((int) xPourcent);
        progressBarDroiteY.setProgress((int) yPourcent);
    }
}
