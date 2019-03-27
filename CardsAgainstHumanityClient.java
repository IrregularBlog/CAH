import javax.swing.*;
import java.net.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.net.*;
import java.awt.*;
import java.util.*;
import java.io.*;

public class CardsAgainstHumanityClient extends JFrame
{

    Card[] cards = new Card[8];
    Card blackCard = null;
    Card[] cardsOther;
    JPanel[] jpnl = new JPanel[2];
    BufferedReader reader;
    PrintWriter writer;
    int leer = 0;

    Socket sock;

    public CardsAgainstHumanityClient()
    {
        super();
        setBounds(600,400,600,400);

        for(int i=0; i<jpnl.length; i++){
            jpnl[i] = new JPanel();
        }

        setLayout(new GridLayout(2,1)); 
        jpnl[0].setLayout(new BorderLayout());
        jpnl[1].setLayout(new GridLayout(1,cards.length+1)/*new FlowLayout()*/);
        
        add(jpnl[0]);
        add(jpnl[1]);

        netzwerkEinrichten();
        Thread readerThread = new Thread(new EingehendReader());
        readerThread.start();

        setVisible(true);
    }

    public void updateMyWhitecards(){
        log("added card");
        for(int i=0; i<cards.length; i++){
            if(cards[i]!= null){
                jpnl[1].add(cards[i]);
            }
        }
        
    }
    
    public void updateMyBlackCard(){
        jpnl[0].add(blackCard);
    }

    private void netzwerkEinrichten(){
        try{
            //sock = new Socket("195.202.41.170",5000);
            sock = new Socket("127.0.0.1",5000);
            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(sock.getOutputStream());

            System.out.println("Netzwerkverbindung steht");
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
        jpnl[1].add(new MyButton("Senden", writer));
    }

    public void nachrichtVerarbeiten(String nachricht){
        int it=0;

        if(nachricht.contains("°")){

            String[] temp = nachricht.split("°");
            int id = Integer.parseInt(temp[0]);
            String text = temp[1];

            for(int i=0; i<cards.length; i++){
                if(cards[i] == null){ 
                    cards[i] = new Card(id,0, text); 
                    log("Neue Karte: "+cards[i].text);
                    updateMyWhitecards();
                    return;
                }

            }

            //Whitecard wird ersetzt
        }
        else if(nachricht.contains("||")){

            String[] temp = nachricht.split("||");
            int id = Integer.parseInt(temp[0]);
            String text = temp[1];

            blackCard = new Card(id,1, text);
            updateMyBlackCard();
            
            //Blackcard wird ersetzt
        }
        else if(nachricht.contains("\\#:")){
            nachricht = nachricht.replaceAll("\\#:", "");
            cardsOther = new Card[Integer.parseInt(nachricht)];
            it =0;
            //wie viele haben die anderen

        }
        else if(nachricht.contains("\\#4")){
            String[] temp = nachricht.split("\\#4");
            int id = Integer.parseInt(temp[0]);
            String text = temp[1];
            cardsOther[it] = new Card(id,0,text);
            it++;

        }

    }
    public void log(Object e){System.out.println(""+e);}
    public class EingehendReader implements Runnable{
        public void run(){
            String nachricht;
            try{

                while (true){
                    //hier müssen die Karten unterschieden werden etc.
                    nachricht = reader.readLine();
                    log("Nachricht in:"+nachricht);

                    nachrichtVerarbeiten(nachricht);

                }
            }catch(Exception ex){
            }
        }
    }


    //Karte hat ID 

}
