package com.example.android.transmissionencoder;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * Created by USUARIO on 06/11/2016.
 */
public class SignalPPM {

    public int SAMPLE_RATE=44100;
    public int CHANNELS_NUM = 6; //on peut aller jusau'q 8
    public int tailleBufferPPM = (int)(SAMPLE_RATE*0.0225); // on veut une impulsion de 22.5 a uen freq de 22KHz;
    public ArrayList<Float> channels = new ArrayList<Float>(CHANNELS_NUM);

    public boolean state;

    private AudioManager audiomanager;
    private EnvoiSignalPPM envoisignalppm;

    public SignalPPM(Context c) {
        audiomanager = (AudioManager)c.getSystemService(Context.AUDIO_SERVICE);

        int audiofocus = audiomanager.requestAudioFocus(AudioManager.OnAudioFocusChangeListener,AudioManager.STREAM_MUSIC,AudioManager.AUTOFOCUS_GAIN_TRANSIENT_EXCLUSIVE);
        if(audiofocus == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Erreur");
            alert.setMessage("AUDIOFOCUS NOT GRANTED" + audiofocus);
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
            });
            alert.show();
        }
        audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC,audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);
        for(int i=0;i<channels.size();i++)
            channels.add((float)0.68181818);//Valeurs nulles (pas de mouvement du joystick quoi
    }
    /*
    @params temps
    @return int
    Fonction permettant la conversion d'un temps donné en un nombre de cases lié au buffer (défini par le SAMPLE_RATE)
     */
    public int conversionTempsACases(float temps){
        return (int)Math.round(temps*0.001*SAMPLE_RATE);
    }

    /*
    Fixe les valeurs des cannaux (renvoyé par une position)
     */
    public void setChannel(int canal,float value) {
        channels.set(canal-1,(float) 0.68181818 + (float)1.0 *((float)value/(float)255));
    }

    public int debutGeneration() {
        try {
            if(AudioTrack.getMinBufferSize(SAMPLE_RATE,AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT) <= 0)
                return -1;

            state = true;
            envoisignalppm = new EnvoiSignalPPM();
            envoisignalppm.execute();
            return 0;

        } catch(Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    public int arretForce() {
        try {
            state = false;
            envoisignalppm.cancel(true);
            envoisignalppm = null;
            return 0;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return -1; //en cas d'erreur
    }

    public int mapValues(int x,int in_min,int in_max,int out_min,int out_max) {
        return (x-in_min)*(out_max-out_min) / (in_max - in_min) + out_min;
    }

    public void abandonFocus() {
        audiomanager.abandonAudioFocus(AudioManager.OnAudioFocusChangeListener);
    }

    public class EnvoiSignalPPM extends AsyncTask<Void,Double,Void> {


         //On peut envoyer jusqu'a 8 canneaux sur 22.5 ms (on en utilise que 4 , c'est pas grave)
        //On doit choisir les valeurs de ce canal donc rendre cette classe interne dans une autre classe qui fait le job
        //c'est plus pratique;
        public EnvoiSignalPPM() {
            //je prefere mettre le constructeur plutot que pas le definir du tout
        }


        @Override
        protected Void doInBackground(Void... params) {
            try {
                AudioTrack trackPpm = new AudioTrack(AudioManager.STREAM_MUSIC,SAMPLE_RATE,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        tailleBufferPPM,
                        AudioTrack.MODE_STREAM);

                trackPpm.setStereoVolume((float)1.0,(float)1.0); //Volume au max sa mere
                if(trackPpm.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) trackPpm.play();


                int nbrCasesOqp; //le nombre de cases occupés par le signal voulu

                while(state) {
                    short[] signal = new short[tailleBufferPPM];

                    int i = 0;
                    nbrCasesOqp = i + conversionTempsACases((float) 0.3); //le bas de depart de 0.3ms
                    for (; i < nbrCasesOqp; i++) {
                        signal[i] = Short.MIN_VALUE; //Le minimum pour le bas , ici c'est -2^15
                    }

                    //Le tour des signaux venant des canneaux
                    for (int canal = 0; canal < CHANNELS_NUM; canal++) {
                        nbrCasesOqp = i + conversionTempsACases((float) channels.get(canal));
                        for (; i < nbrCasesOqp; i++) {
                            signal[i] = Short.MAX_VALUE;
                        }

                        nbrCasesOqp = i + conversionTempsACases((float) 0.3);
                        for (; i < nbrCasesOqp; i++) {
                            signal[i] = Short.MIN_VALUE;
                        }
                    }

                    for (; i < signal.length; i++) { //que du haut pour finir
                        signal[i] = Short.MAX_VALUE;
                    }
                    trackPpm.write(signal,0,tailleBufferPPM);
                }


            }catch(Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
