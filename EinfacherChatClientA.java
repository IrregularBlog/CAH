
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EinfacherChatClientA
{
    JTextArea eingehend;
    JTextField ausgehend;
    BufferedReader reader;
    PrintWriter writer;
    Socket sock;

    public static void main(String[] args){
        EinfacherChatClientA client = new EinfacherChatClientA();
        client.los();
    }

    public void los(){
        JFrame frame = new JFrame("Lächerlich einfacher Chat-Client");
        JPanel hauptPanel = new JPanel();
        eingehend = new JTextArea(15,20);
        eingehend.setLineWrap(true);
        eingehend.setWrapStyleWord(true);
        eingehend.setEditable(false);
        JScrollPane fScroller = new JScrollPane(eingehend);
        fScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        fScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        ausgehend = new JTextField(20);
        JButton sendenButton = new JButton("Senden");
        sendenButton.addActionListener(new SendenButtonListener());
        hauptPanel.add(fScroller);
        hauptPanel.add(ausgehend);
        hauptPanel.add(sendenButton);
        netzwerkEinrichten();

        Thread readerThread = new Thread(new EingehendReader());
        readerThread.start();

        frame.getContentPane().add(BorderLayout.CENTER, hauptPanel);
        frame.setSize(260,350);
        frame.setVisible(true);
    }

    private void netzwerkEinrichten(){
        try{
            sock = new Socket("195.202.41.170",5000);
            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(sock.getOutputStream());
            System.out.println("Netzwerkverbindung steht");
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public class SendenButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent ev){
            try{
                writer.println(ausgehend.getText());
                writer.flush();
            }catch(Exception ex){
                ex.printStackTrace();
            }
            ausgehend.setText("");
            ausgehend.requestFocus();

        }
    }

    public class EingehendReader implements Runnable{
        public void run(){
            String nachricht;
            try{
                while ((nachricht = reader.readLine()) != null){
                    System.out.println("gelesen: "+nachricht);
                    eingehend.append(nachricht+ "\n");

                }
            }catch(Exception ex){
            }
        }
    }
    
}


