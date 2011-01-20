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
    private TextView t;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
    FileOutputStream out; // declare a file output object
    PrintStream p; // declare a print stream object
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        FileInputStream in;
        int ch;
        String strContent = new String();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linear);

        slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
        slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
        slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
        slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);
        
        t = (TextView) findViewById(R.id.title);
        update();
        // Gesture detection
        gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };
        try {
            out = openFileOutput("manjuba.txt", MODE_WORLD_READABLE);

	        p = new PrintStream(out);
	
	        p.println("Erguidaaaaaaaaaaa!!!");

	        p.close();
	        out.close();
        } catch (Exception e) {
	        strContent = (e + "\n\n");
        }

        try {
	        // Open
	        in = openFileInput("dalton");;
            while((ch = in.read()) != -1) strContent += ((char)ch);
	        
	        in.close();
        } catch (Exception e) {
	        strContent = (e + "\n\n");
            System.err.println ("Error writing to file");
        }
        t.setText(strContent);
    }
    

    class MyGestureDetector extends SimpleOnGestureListener
    { 
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
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
 
       
        try {
            URL url = new URL("http://www.linux.ime.usp.br/~debonis/droidejao");
            out = openFileOutput("dalton", MODE_PRIVATE);
	        p = new PrintStream(out);
	        p.println("Erguidaaaaaaaaaaa!!!");

//            File file = new File("dalton");

            URLConnection ucon = url.openConnection();

            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1) baf.append((byte) current);

//            FileOutputStream fos = new FileOutputStream(file);
            out.write(baf.toByteArray());
            out.close();
	        p.close();
	        out.close();

        } catch (IOException e) {
            t.setText(e + "\nFUDEL");
        } 
    }
    

}

