import javax.swing.*;
import java.net.*;
import java.io.*;


public class Card extends JLabel{
        String text = "";
        int id;
        int art;
        
        public Card(int id,int art, String text){
            this.text = text;
            this.setText(text);
            this.id = id;
            this.art = art;
            
            }
        public void senden(PrintWriter writer){
            if(art == 0)writer.println(id+"#+"+text);
            if(art == 1)writer.println(id+"#-"+text);
            writer.flush();
        }
        
    }
    
    