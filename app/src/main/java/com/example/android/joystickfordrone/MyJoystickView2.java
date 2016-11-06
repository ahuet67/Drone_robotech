package com.example.android.joystickfordrone;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by USUARIO on 02/11/2016.
 */
public class MyJoystickView2 extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    private float topRectX;
    private float topRectY;
    private float bottomRectX;
    private float bottomRectY;
    private float largeurRect;
    private float hauteurRect;
    private float hatRadius;
    private MyJoystickListener2 joystickCallBack;



    public interface MyJoystickListener2 {
        void onMyJostickListener(float xPourcent,float yPourcent,int id);
    }
    //Constructeurs
    public MyJoystickView2(Context c) {
        super(c);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if(c instanceof MyJoystickListener2)
            joystickCallBack = (MyJoystickListener2) c;

    }
    public MyJoystickView2(Context c,AttributeSet a) {
        super(c,a);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if(c instanceof MyJoystickListener2)
            joystickCallBack = (MyJoystickListener2) c;
    }
    public MyJoystickView2(Context c, AttributeSet a,int style) {
        super(c,a,style);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if(c instanceof MyJoystickListener2)
            joystickCallBack = (MyJoystickListener2) c;
    }
    //Getters Setters
    public float getTopRectX() {
        return topRectX;
    }

    public void setTopRectX(float topRectX) {
        this.topRectX = topRectX;
    }

    public float getTopRectY() {
        return topRectY;
    }

    public void setTopRectY(float topRectY) {
        this.topRectY = topRectY;
    }

    public float getBottomRectX() {
        return bottomRectX;
    }

    public void setBottomRectX(float bottomRectX) {
        this.bottomRectX = bottomRectX;
    }

    public float getBottomRectY() {
        return bottomRectY;
    }

    public void setBottomRectY(float bottomRectY) {
        this.bottomRectY = bottomRectY;
    }

    public float getHatRadius() {
        return hatRadius;
    }

    public void setHatRadius(float hatRadius) {
        this.hatRadius = hatRadius;
    }
    public float getLargeurRect() {
        return largeurRect;
    }

    public void setLargeurRect(float largeurRect) {
        this.largeurRect = largeurRect;
    }

    public float getHauteurRect() {
        return hauteurRect;
    }

    public void setHauteurRect(float hauteurRect) {
        this.hauteurRect = hauteurRect;
    }

    private void setupDimensions() {
        setLargeurRect((float)10);
        setTopRectX(getWidth()/2 -getLargeurRect()/2);
        setTopRectY(getHeight()/4);
        setHauteurRect((getHeight())/2);
        setBottomRectX(getTopRectX()+getLargeurRect());
        setBottomRectY(getTopRectY()+getHauteurRect());
        setHatRadius(Math.min(getHeight(),getWidth())/6);

    }

    private void drawJoystick(float newX,float newY) {
        if(getHolder().getSurface().isValid()) {
            Canvas myCanvas = this.getHolder().lockCanvas();
            Paint couleur= new Paint();

            myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            couleur.setARGB(255,60,60,60);

            myCanvas.drawRect(getTopRectX(),getTopRectY(),getBottomRectX(),getBottomRectY(),couleur);

            couleur.setARGB(200,150,150,200);
            myCanvas.drawCircle(newX,newY,getHatRadius(),couleur);
            couleur.setARGB(255,60,60,60);
            myCanvas.drawLine(getWidth()/4,newY,(3*getWidth())/4,newY,couleur);

            getHolder().unlockCanvasAndPost(myCanvas);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setupDimensions();
        drawJoystick(getBottomRectX()+getLargeurRect()/2,getBottomRectY()-getHauteurRect()/2);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.equals(this)) {
            if(event.getAction() != event.ACTION_UP) {
                float deplacementYhaut = getTopRectY();
                float deplacementYbas = getBottomRectY();
                float deplacementXgauche = getWidth()/4;
                float deplacementXdroite = (3*getWidth())/4;

                if(event.getY() > deplacementYhaut && event.getY() < deplacementYbas && event.getX() > deplacementXgauche
                        && event.getX() < deplacementXdroite ) {
                    joystickCallBack.onMyJostickListener(((event.getX()-getWidth()/4)*100)/((3*getWidth()/4)-(getWidth()/4)),(event.getY()-getBottomRectY())*100/(getTopRectY()-getBottomRectY()),getId());
                    drawJoystick(event.getX(),event.getY());
                }
                else {
                    if(event.getY()<deplacementYhaut)
                        drawJoystick(event.getX(),deplacementYhaut);
                    else if(event.getY()>deplacementYbas)
                        drawJoystick(event.getX(),deplacementYbas);
                    if(event.getX() > deplacementXdroite)
                        drawJoystick(deplacementXdroite,event.getY());
                    else if(event.getX()< deplacementXgauche)
                        drawJoystick(deplacementXgauche,event.getY());
                }
            }
            else{
                drawJoystick(getBottomRectX()+getLargeurRect()/2,getBottomRectY()-getHauteurRect()/2);
                joystickCallBack.onMyJostickListener(50,50,getId());
            }
         //IL MANQUE A METTRE EN PLACE LE CALLBACK ET REGLER LE PB DES EXTREMES

        }
        return true;
    }


}
