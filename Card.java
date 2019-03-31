import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import javax.swing.text.StyleContext;
import javax.swing.text.StyleConstants;
import java.awt.event.*;

public class Card extends JTextPane {
    String text = "";
    int id;
    int art;
    
    boolean selected = false;
    boolean usable = true;
    

    public Card(int id,int art, String text){

        StyleContext.NamedStyle centerStyle = StyleContext.getDefaultStyleContext().new NamedStyle();
        StyleConstants.setAlignment(centerStyle,StyleConstants.ALIGN_CENTER);
        StyleConstants.setForeground(centerStyle, Color.black);
        
        
        StyleContext.NamedStyle whiteStyle = StyleContext.getDefaultStyleContext().new NamedStyle();
        StyleConstants.setAlignment(whiteStyle,StyleConstants.ALIGN_CENTER);
        StyleConstants.setForeground(whiteStyle, Color.white);
        

        this.text = text;
        this.setText(text);
        this.setEditable(false);
        this.setLogicalStyle(centerStyle);
        Font font = new Font("Verdana", Font.PLAIN, 40);
        
        
        this.setHighlighter(null);
        
        this.setFont( font);
        this.setOpaque(true);
        this.id = id;
        this.art = art;

        this.addMouseListener(new MouseAdapter(){
                public void mouseClicked(MouseEvent e) {
                    if(true){
                        selected = !selected;
                        if(selected && art ==0)setBackground(Color.YELLOW);
                        else if(art == 0) setBackground(Color.WHITE);
                    }

            
                }                
            });

        if(art == 0){ 

            this.setBackground(Color.WHITE);

        }
        else{ 
            
            this.setLogicalStyle(whiteStyle);
            this.setBackground(Color.BLACK);
        
        }

    }

    public void senden(PrintWriter writer){
        System.out.println("Karte gesendet: "+text);
        if(art == 0)writer.println(id+"°"+text);
        if(art == 1)writer.println(id+"±"+text);
        writer.flush();
    }
    
     public void sendenZuAnderen(PrintWriter writer){
        
        if(art == 0)writer.println(id+"<"+text);
        writer.flush();
    }


}

