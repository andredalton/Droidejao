package br.usp.ime.droidejao;

import android.app.Activity;
import android.os.Bundle;
import java.io.*;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import org.apache.http.util.ByteArrayBuffer;

public class Droidejao extends Activity
{
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
    private Animation slideRightOut;
    private GestureDetector gestureDetector;
    private TextView t;
    private String strContent = new String();
    View.OnTouchListener gestureListener;
    FileOutputStream out; // declare a file output object
    PrintStream p; // declare a print stream object
    FileInputStream in;
    int ch;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linear);

        slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
        slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
        slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
        slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);
        
        t = (TextView) findViewById(R.id.title);

//          Nao sei se deixo isso comentado:
//        update();

        gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };
    }
    
    @Override
    protected void onResume ()
    {
        super.onResume();
        update();
    }
    
    class MyGestureDetector extends SimpleOnGestureListener
    { 
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                   	findViewById(R.id.pco).startAnimation(slideLeftOut);
                   	findViewById(R.id.central).startAnimation(slideLeftOut);
                   	findViewById(R.id.fisica).startAnimation(slideLeftOut);
                   	findViewById(R.id.quimica).startAnimation(slideLeftOut);
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                   	findViewById(R.id.pco).startAnimation(slideRightOut);
                   	findViewById(R.id.central).startAnimation(slideRightOut);
                   	findViewById(R.id.fisica).startAnimation(slideRightOut);
                   	findViewById(R.id.quimica).startAnimation(slideRightOut);
             }
            } catch (Exception e) {
            }
            return false;
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return (gestureDetector.onTouchEvent(event));
    }
    
    public void update()
    {
        t = (TextView) findViewById(R.id.title); 
       
        try {
            URL url = new URL("http://www.linux.ime.usp.br/~debonis/droidejao");
            out = openFileOutput("dalton", MODE_PRIVATE);

            URLConnection ucon = url.openConnection();

            strContent = "";
            
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1) baf.append((byte) current);

            out.write(baf.toByteArray());
	        out.close();
	        in = openFileInput("dalton");;
            while((ch = in.read()) != -1) strContent += ((char)ch);
	        
	        in.close();
            t.setText(strContent);

        } catch (IOException e) {
            t.setText(e + "\nFUDEL");
            System.err.println ("Error writing to file");
        } 
        
    }
    
}

