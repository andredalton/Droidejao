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
    // Ainda sendo desenvolvido.
    progressBar progress = new progressBar();

    // Variaveis estaticas.
    private static final int MSG_ID = 1;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private static final boolean ALMOCO = true;
    private static final boolean JANTAR = false;
    private static final int SEGUNDA = 0;
    private static final int TERCA = 1;
    private static final int QUARTA = 2;
    private static final int QUINTA = 3;
    private static final int SEXTA = 4;
    private static final int SABADO = 5;
    private static final int DOMINGO = 6;

    // Animacoes.
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
    private Animation slideRightOut;

    // Variavel de captura de movimentos.
    private GestureDetector gestureDetector;
    
    // Campos da tela
    private TextView title;
    private TextView fisica;
    private TextView quimica;
    private TextView pco;
    private TextView central;
    
    // ???
    View.OnTouchListener gestureListener;
    
    // Variaveis de controle de data.
    private Calendar anow = Calendar.getInstance();                                                 // Data para comparacao do timestamp.
    private long timestamp = anow.getTimeInMillis() / 1000;                                         // Timestamp em segundos.
    private int hoje = (7+anow.get(Calendar.DAY_OF_WEEK)-anow.getFirstDayOfWeek ())%7; 	
    private int dia_atual = hoje;
    private boolean almoco = (anow.get(Calendar.HOUR) < 15 || anow.get(Calendar.HOUR) > 20);

    // Arrays de Strings.
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

    // Icone, usado nas notificacoes.
    int icon = R.drawable.icon;
	
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
        update();

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
//        update();
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
    
    private boolean downloadFile(String path, String fileName){
        FileOutputStream out;
        
        try{
            //acesso o arquivo e escrevo no buffer baf
            URL url = new URL(path);
            URLConnection ucon = url.openConnection();
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1) baf.append((byte) current);

            //escreve o buffer no arquivo
            out = openFileOutput(fileName, MODE_PRIVATE);
            out.write(baf.toByteArray());
            out.close();
            
            return true;
        } catch (IOException e) {
           return false;
        }
    }
    
    private String fileContents(String name){
        FileInputStream in;
        String tmp = "";
        int ch;
        
        try {
	        // Open
	        in = openFileInput(name);
            while((ch = in.read()) != -1) tmp += ((char)ch);
            in.close();
            
            return tmp;
        } catch (Exception e) {
	        return "erro: " + e;
        }
    }
    
    private void newNotification(CharSequence tickerText, CharSequence contentTitle, CharSequence contentText){
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        
        // Iniciando notificacao. 
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);
        
        Context context = getApplicationContext();

        Intent notificationIntent = new Intent(this, Droidejao.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        mNotificationManager.notify(MSG_ID, notification);
        // Notificacao concluida.
    }
    
    private void refresh(int dia, boolean refeicao){
    
    }
    
    public void update()
    {
        String strContent = new String();
        
        newNotification("Verificando validade", "Droidejao", "verificando validade.");
        
        // Abrindo o timestamp para verificar a validade dos dados atuais. 
        strContent = fileContents("timestamp");
        
        if( strContent.compareTo(Long.toString(timestamp)) > 0 ){
            newNotification("Verificando validade", "Droidejao", "Atualizado. " );
        }
        else{
            newNotification("Verificando validade", "Droidejao", "Atualizando." );
            
            // Baixando o timestamp
            downloadFile("http://www.linux.ime.usp.br/~avale/droidejao/timestamp", "timestamp");
            
            // Percorrendo os bandex.
            for(int j=0; j<4; j++){
                boolean alm = false;
                
                
                
                // Percorrendo os dias da semana.
                for(int i=0; i<7; i++){
                    // Alternando em almoco e janta.
                    do{
                        // Baixando os arquivos com os cardapios.
                        downloadFile
                        (
                            "http://www.linux.ime.usp.br/~avale/droidejao/" + "droidejao-" + bandex[j] + "-" + (alm ? "almoco-" : "jantar-") + dias[i],
                            "droidejao-" + bandex[j] + "-" + (alm ? "almoco-" : "jantar-") + dias[i]
                        );
                        alm = !alm;
                    } while(alm);
                }
            }
            newNotification("Verificando validade", "Droidejao", "Atualizado." );
        }
        
        title.setText( dias_semana[dia_atual] + " - " + (almoco ? "Almoco": "Jantar") );
        
        //Arregacando uma notificacao definida.
        //mNotificationManager.cancel(MSG_ID);
        
        //Arregacando total!!!
        //mNotificationManager.cancelAll(); 
    }    
}

