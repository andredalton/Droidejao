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
import java.util.Calendar;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class Droidejao extends Activity
{
    progressBar pb = new progressBar();

    /* Definindo IDs estaticos pra mensagens, isso e necessario pra matar mensagens ou atualiza-las. */
    private static final int MSG_ID = 1;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
    private Animation slideRightOut;
    private GestureDetector gestureDetector;
    
    //Campos da tela
    private TextView title;
    private TextView fisica;
    private TextView quimica;
    private TextView pco;
    private TextView central;
    
    private String strContent = new String();
    View.OnTouchListener gestureListener;
    FileOutputStream out; // declare a file output object
    PrintStream p; // declare a print stream object
    FileInputStream in;
    int ch;
    
    //Variaveis de controle de data.
    private Calendar anow = Calendar.getInstance();    // Data para comparacao do timestamp.
    private long timestamp = anow.getTimeInMillis();
    private int hoje = (7+anow.get(Calendar.DAY_OF_WEEK)-anow.getFirstDayOfWeek ())%7; 	
    private int dia_atual = hoje;
    private boolean almoco = (anow.get(Calendar.HOUR) < 15 || anow.get(Calendar.HOUR) > 20);

    private String dias_semana[] = new String[]
    {  
        "Segunda",
        "Terca",
        "Quarta",
        "Quinta",
        "Sexta",
        "Sabado",
        "Domingo"
    };

    private String bandex[] = new String[]
    {  
        "quimica",
        "fisica",
        "pco",
        "central"
    };

    private String dias[] = new String[]
    {  
        "segunda-feira",
        "terca-feira",
        "quarta-feira",
        "quinta-feira",
        "sexta-feira",
        "sabado",
        "domingo"
    };
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linear);

        slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
        slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
        slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
        slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);
        
        title = (TextView) findViewById(R.id.title);
        fisica = (TextView) findViewById(R.id.fisica);
        quimica = (TextView) findViewById(R.id.quimica);
        pco = (TextView) findViewById(R.id.pco);
        central = (TextView) findViewById(R.id.central);

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
                    
                almoco = !almoco;
                
                // Swipe pra Esquerda.
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    
                    if(almoco){
                        if(dia_atual<6){
                            dia_atual++;
                            
                            // Animacao
                            findViewById(R.id.pco).startAnimation(slideLeftOut);
                           	findViewById(R.id.central).startAnimation(slideLeftOut);
                           	findViewById(R.id.fisica).startAnimation(slideLeftOut);
                           	findViewById(R.id.quimica).startAnimation(slideLeftOut);
                        }
                        else{
                            almoco = !almoco;
                        }
                    }
                }
                // Swipe pra Direita.
                else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                   	if(!almoco){
                       	if(dia_atual>0){
                            dia_atual--;
                            
                            // Animacao
                            findViewById(R.id.pco).startAnimation(slideRightOut);
                           	findViewById(R.id.central).startAnimation(slideRightOut);
                           	findViewById(R.id.fisica).startAnimation(slideRightOut);
                           	findViewById(R.id.quimica).startAnimation(slideRightOut);
                        }
                        else{
                            almoco = !almoco;
                        }
                    }
             }
             title.setText( dias_semana[dia_atual] + " - " + (almoco ? "Almoco": "Jantar") );
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
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        
        int icon = R.drawable.icon;
        
        // Iniciando notificacao. 
        CharSequence tickerText = "Atualizando";
        long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, tickerText, when);

        Context context = getApplicationContext();
        CharSequence contentTitle = "Droidejao";
        CharSequence contentText = "verificando validade.";

        Intent notificationIntent = new Intent(this, Droidejao.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        mNotificationManager.notify(MSG_ID, notification);
        // Notificacao concluida.
        
        
        // Verificando a necessidade de update.
        try {
	        // Open
	        in = openFileInput("dalton");;
            while((ch = in.read()) != -1) strContent += (char)ch;
	        
	        in.close();
        } catch (Exception e) {
	        strContent += e;
        }

        // Iniciando notificacao. 
        context = getApplicationContext();
        contentTitle = "Droidejao";
        contentText = "valido ate " + strContent + ".";

        notificationIntent = new Intent(this, Droidejao.class);
        contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        mNotificationManager.notify(MSG_ID, notification);
        // Notificacao concluida.
    
        try{
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
            
            title.setText( strContent );
        } catch (IOException e) {
           // t.setText(e + "\nFUDEL");
        }
        
        //Arregacando uma notificacao definida.
        //mNotificationManager.cancel(MSG_ID);
        
        //Arregacando total!!!
        //mNotificationManager.cancelAll();
        
  /*      
        // Recuperando a timestamp da validade
        try {
            URL url = new URL("http://www.linux.ime.usp.br/~avale/droidejao");
            
            out = openFileOutput("timestamp", MODE_PRIVATE);
            URLConnection ucon = url.openConnection();

            strContent = "";
            
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1) baf.append((byte) current);

            out.write(baf.toByteArray());
            out.close();
            in = openFileInput("timestamp");;
            while((ch = in.read()) != -1) strContent += ((char)ch);
            
            in.close();
        } catch (IOException e) {
           // t.setText(e + "\nFUDEL");
        }

/*    
        for(int i=0; i<7; i++){
            for(int j=0; j<4; j++){
                boolean alm = false;
                
                do{
                    try {
                        URL url = new URL("http://www.linux.ime.usp.br/~avale/droidejao");
                        
                        out = openFileOutput("droidejao-" + bandex[j] + "-" + (alm ? "almoco-" : "jantar-") + dias[i], MODE_PRIVATE);
                        URLConnection ucon = url.openConnection();

                        strContent = "";
                        
                        InputStream is = ucon.getInputStream();
                        BufferedInputStream bis = new BufferedInputStream(is);

                        ByteArrayBuffer baf = new ByteArrayBuffer(50);
                        int current = 0;
                        while ((current = bis.read()) != -1) baf.append((byte) current);

                        out.write(baf.toByteArray());
                        out.close();
                        in = openFileInput("droidejao-" + bandex[j] + "-" + (alm ? "almoco-" : "jantar-") + dias[i]);
                        while((ch = in.read()) != -1) strContent += ((char)ch);
                        
                        in.close();
                    } catch (IOException e) {
                       // t.setText(e + "\nFUDEL");
                    }
                    alm = !alm;
                } while(alm);
            }
        }
*/      
        //title.setText( " - " + dias_semana[dia_atual] + " - " + (almoco ? "Almoco": "Jantar") );
        
        //Arregacando uma notificacao definida.
        //mNotificationManager.cancel(MSG_ID);
        
        //Arregacando total!!!
        //mNotificationManager.cancelAll(); 
        
    }
    
}

