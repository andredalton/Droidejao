package br.usp.ime.droidejao;

import android.app.Activity;
import android.os.Bundle;
import java.io.*;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
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

import android.text.Html;

public class Droidejao extends Activity
{
    // Variaveis estaticas.
    private static final int MSG_ID = 1;
    private static final int SWIPE_MIN_DISTANCE = 60;
    private static final int SWIPE_MAX_OFF_PATH = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;
    private static final int SEGUNDA = 0;
    private static final int TERCA = 1;
    private static final int QUARTA = 2;
    private static final int QUINTA = 3;
    private static final int SEXTA = 4;
    private static final int SABADO = 5;
    private static final int DOMINGO = 6;
    private static final int TOTAL_BANDEX = 4;
    // Animacoes.
    private Animation lhide;
    private Animation lshow;
    private Animation rhide;
    private Animation rshow;
    

    // Variavel de captura de movimentos.
    private GestureDetector gestureDetector;
    
    // Campos da tela
    private TextView title;
    private TextView fisica;
    private TextView quimica;
    private TextView pco;
    private TextView central;
    private TextView bandexText[] = {fisica, quimica, pco, central};
    
    private LinearLayout page;
    
    //URL do servidor.
    String server_url = "http://www.linux.ime.usp.br/~debonis/droidejao/";
    
    // ???
    View.OnTouchListener gestureListener;
    
    // Variaveis de controle de data.
    private Calendar anow = Calendar.getInstance(); // Data para comparacao do timestamp.
    private long timestamp = anow.getTimeInMillis() / 1000; // Timestamp em segundos.
    private int hoje = (anow.get(Calendar.DAY_OF_WEEK)-anow.getFirstDayOfWeek()-1); 	
    private boolean almoco =  (anow.get(Calendar.HOUR_OF_DAY) < 14 || anow.get(Calendar.HOUR_OF_DAY) > 20);
    private int dia_atual = (hoje + ((anow.get(Calendar.HOUR_OF_DAY) > 20)? 1 : 0)) % 7;

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
        "fisica",
        "quimica",
        "pco",
        "central"
    };
    
    private int backGround[] = new int[4];
    
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
    int check = R.drawable.check;
    int uncheck = R.drawable.uncheck;

    // Guarda se o aplicativo pode se auto-atualizar ou nao
    private boolean auto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linear);
  
        // Comecando meus testes aqui (dalton)
        lhide = AnimationUtils.loadAnimation(this, R.anim.left_hide);
        lshow = AnimationUtils.loadAnimation(this, R.anim.left_show);
        rhide = AnimationUtils.loadAnimation(this, R.anim.right_hide);
        rshow = AnimationUtils.loadAnimation(this, R.anim.right_show);
        
        if(fileContents("autoupdate").compareTo("0") == 0)
            auto = false;
        else
            auto = true;
        
        title = (TextView) findViewById(R.id.title);
        bandexText[0] = fisica = (TextView) findViewById(R.id.fisica);
        bandexText[1] = quimica = (TextView) findViewById(R.id.quimica);
        bandexText[2] = pco = (TextView) findViewById(R.id.pco);
        bandexText[3] = central = (TextView) findViewById(R.id.central);
        page = (LinearLayout) findViewById(R.id.page);
        
        
        gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };
        
        if (auto) update(true);
        
        loadContext();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        
        inflater.inflate(R.menu.option_menu, menu);
    
        // O metodo getItem pega o item pela ordem, comecando de 0.
        // Seleciona qual icone sera mostrado no menu de opcoes.
        MenuItem item = menu.getItem(3);
        if(auto)    item.setIcon(check);
        else        item.setIcon(uncheck);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MenuInflater inflater = getMenuInflater();
        
        switch (item.getItemId()) {
            case R.id.help:
                startActivity(new Intent(this, Help.class));
                return true;
            case R.id.info:
                startActivity(new Intent(this, Info.class));
                return true;
            case R.id.reload:
                update(false);
                return true;
            case R.id.auto:
                auto = !auto;
                
                if(auto){
                    item.setIcon(check);
                    saveFile("autoupdate", "1");
                }
                else{
                    item.setIcon(uncheck);
                    saveFile("autoupdate", "0");
                }
                return true;
        }
        return false;
    }
    
    class MyGestureDetector extends SimpleOnGestureListener
    { 
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                    
                // Swipe pra Esquerda.
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {                    
                    almoco = !almoco;
                    if(almoco){
                        if(dia_atual<6) dia_atual++;
                        else{
                            almoco = !almoco;
                            return false;
                       }
                    }
                    
                    page.startAnimation(lshow);
                    loadContext();
                }
                // Swipe pra Direita.
                else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                   	almoco = !almoco;
                   	if(!almoco){
                       	if(dia_atual>0) dia_atual--;
                        else{
                            almoco = !almoco;
                            return false;
                        }
                    }
                    
                    page.startAnimation(rshow);
                    loadContext();
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
    
    // Funcionando.
    private byte[] downloadTempFile(String path){
        ByteArrayBuffer baf = new ByteArrayBuffer(50);
        
        try{
            //acesso o arquivo e escrevo no buffer baf
            URL url = new URL(path);
            URLConnection ucon = url.openConnection();
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            int current = 0;
            while ((current = bis.read()) != -1) baf.append((byte) current);
            
            return baf.toByteArray();
        } catch (IOException e) {
           return baf.toByteArray();
        }
    }
    
    // Funcionando.
    private boolean downloadFile(String path, String fileName){
        FileOutputStream out;
        
        try{
            byte[] buffer = downloadTempFile(path);
            
            //escreve o buffer no arquivo
            out = openFileOutput(fileName, MODE_PRIVATE);
            out.write(buffer);
            out.close();
            
            return true;
        } catch (IOException e) {
           return false;
        }
    }
    
    private boolean saveFile(String fileName, String content){
        FileOutputStream out;
        try{
            out = openFileOutput(fileName, MODE_PRIVATE);
            out.write(content.getBytes());
            out.close();
        
            return true;
        } catch (IOException e) {
           return false;
        }
    }
    
    // Funcionando.
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
    
    private void cancelNotifications(){
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        
        //Arregacando uma notificacao definida.
        mNotificationManager.cancel(MSG_ID);
        
        //Arregacando total!!!
        //mNotificationManager.cancelAll(); 
        
    }

    public void loadContext() {
        title.setText(dias_semana[dia_atual] + " - " + (almoco ? "Almoco": "Jantar"));
        String prefixo;
        String sufixo;

        for(int i = 0; i < TOTAL_BANDEX; i++){
            String content = fileContents( bandex[i] + (almoco ? "-almoco-": "-janta-") + dias[dia_atual]);
            
            if (fileContents(bandex[i]+"-timestamp").compareTo(Long.toString(timestamp)) < 0) {
            //if(Long.getLong(fileContents(bandex[i]+"-timestamp"), 0) < timestamp){
                prefixo = "<font color=\"#202020\">";
            } else {
                prefixo = "<font color=\"#DEDEDE\">";
            }
            sufixo = "</font>";
            
            if (content.contains("erro:"))
                bandexText[i].setText( Html.fromHtml(prefixo+"Erro no cardapio."+sufixo));
            else if (content.length() == 0)
                bandexText[i].setText(Html.fromHtml(prefixo+"FECHADO"+sufixo));
            else
                bandexText[i].setText(Html.fromHtml(prefixo+content.replace("\n", "<br>")+sufixo));
        }
    }
    
    public void update(boolean verificarValidade)
    {
        String strContent = new String();
        
        if (verificarValidade == true) newNotification("Verificando validade", "Droidejao", "verificando validade.");

        // Percorrendo os bandex
        for (int j = 0; j < 4; j++) {
            // Abrindo o timestamp para verificar a validade dos dados atuais.
            strContent = fileContents(bandex[j] + "-timestamp");
            
            if (strContent.contains("erro:")) strContent = "0";
            
            // Desatualizado
            if (strContent.compareTo(Long.toString(timestamp)) < 0 || !verificarValidade) {
                String hash_temp = new String(downloadTempFile(server_url + bandex[j] + "/hash"));
                String hash = fileContents(bandex[j] + "-hash");
                if ((hash_temp.compareTo(hash) != 0) || (hash.substring(0, 4).compareTo("erro") == 0) || !verificarValidade) {
                    // Percorrendo os dias da semana
                    for (int i = 0; i < 7; i++) {
                        boolean alm = false;
                        
                        // Alternando em almoco e janta
                        do {
                            // Baixando os arquivos com os cardapios
                            downloadFile (
                                server_url + bandex[j] + (alm ? "/almoco/" : "/janta/") + dias[i],
                                bandex[j] + "-" + (alm ? "almoco-" : "janta-") + dias[i]
                            );
                            alm = !alm;
                        } while (alm);
                    }
                    
                    downloadFile(server_url + bandex[j] + "/timestamp", bandex[j] + "-timestamp");
                    saveFile(bandex[j] + "-hash", hash_temp);
                }
                newNotification("Sincronizacao completa", "Droidejao (" + bandex[j] + ")", "Atualizado.");
            }
            // Arquivos validos, esta tudo bem agora. 
        }

        cancelNotifications();
    }    
}

