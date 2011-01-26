package br.usp.ime.droidejao;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;

public class Help extends Activity
{
    private ScrollText text;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        
        String body = new String
        (
            "<b>Objetivos:</b> O sistema Droidej&atilde;o foi feito pensando em facilitar a escolha dos card&aacute;pios dos bandeij&otilde;es do campus da capital da USP.<br>\n" +
            "Para tanto ele recolhe informa&ccedil;&otilde;es de um servidor que cont&eacute;m os card&aacute;pios j&aacute; pr&eacute;-processados e um indice de atualiza&ccedil;&atilde;o. Possibilitando assim manter uma quantidade m&iacute;nima de downloads para manter o sistema funcional e atualizado.<br><br>\n" +
            "<b>Mudando de dia:</b> O Droidej&atilde;o j&aacute; seleciona a pr&oacute;xima refei&ccedil;&atilde;o de acordo com o rel&oacute;gio do seu celular, mas &eacute; poss&iacute;vel mudar a data dentre os card&aacute;pios dispon&iacute;veis para a semana mais atualizada. Para tanto basta arrastar o dedo sobre a tela para a direita ou esquerda.<br><br>\n" +
            "<b>Download autom&aacute;tico:</b> O download autom&aacute;tico acontece sempre que a data de validade do card&aacute;pio &eacute; inferior a data do celular. Neste momento o Droidej&atilde;o verifica se existem card&aacute;pios mais atuais no servidor e, caso existam, realiza o download.<br><br>\n" +
            "<b>Cores:</b> Toda vez que a validade do card&aacute;pio estiver vencida, o mesmo ser&aacute; mostrado com uma cor <font color=\"#202020\">cinza escuro</font>, caso contr&aacute;rio aparecer&aacute; com cor <font color=\"#DEDEDE\">cinza claro</font>.<br><br>\n" +
            "<b>Gasto de banda:</b> O Droidej&atilde;o gasta aproximadamente 0.1Kb para verificar se existem novos card&aacute;pios e cerca de 50Kb para o download semanal de informa&ccedil;&otilde;es. Dando um gasto mensal provavelmente inferior a 250Kb de informa&ccedil;&otilde;es mesmo quando estiver com o download automatico setado."
        );

        text = (ScrollText) new ScrollText(this);

        text.setBackgroundColor(0xff000000);
        text.setTextColor(0xffffffff);
		text.setText( Html.fromHtml(body));
		
		setContentView(text);
    }   
}
