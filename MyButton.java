import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import javax.swing.text.StyleContext;
import javax.swing.text.StyleConstants;
import java.awt.event.*;

public class MyButton extends JLabel {
    String text = "";
    PrintWriter writer;
    
    

    public MyButton(String text, PrintWriter writer){
        this.text = text;
        this.writer = writer;
        this.setText(text);
        Font font = new Font("Verdana", Font.PLAIN, 40);
        this.setFont( font);
        this.setOpaque(true);
        

        this.addMouseListener(new MouseAdapter(){
                public void mouseClicked(MouseEvent e) {
                   writer.println("Hi");
                    

            
                }                
            });

        

    }

    


}

