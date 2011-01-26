package br.usp.ime.droidejao;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;

public class Info extends Activity
{
    private ScrollText text;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        
        String body = new String
        (
            "<b>Desenvolvedores</b><br><br>Andr&eacute; Meneghelli (andredalton@gmail.com)<br>Gregory DeBonis (gregorydebonis@gmail.com)"
        );

        text = (ScrollText) new ScrollText(this);

        text.setBackgroundColor(0xff000000);
        text.setTextColor(0xffffffff);
        text.setGravity( 0x00000011  );
		text.setText( Html.fromHtml(body));
		
		setContentView(text);
    }   
}
