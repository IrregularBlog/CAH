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
    Card[] cardsOther;
    JPanel[] jpnl = new JPanel[2];
    BufferedReader reader;
    PrintWriter writer;

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
        jpnl[1].setLayout(new FlowLayout());

        add(jpnl[0]);
        add(jpnl[1]);

        netzwerkEinrichten();
        Thread readerThread = new Thread(new EingehendReader());
        readerThread.start();

       
        setVisible(true);
    }
    public void updateMyWhitecards(){
        for(int i=0; i<cards.length; i++){
            if(cards[i]!= null)jpnl[1].add(cards[i]);
        }
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
    }

    public class EingehendReader implements Runnable{
        public void run(){
            String nachricht;
            try{

                int it=0;
                while ((nachricht = reader.readLine()) != null){
                    //hier müssen die Karten unterschieden werden etc.
                    System.out.println("Hab was  bekommen");

                    if(nachricht.contains("#+")){

                        String[] temp = nachricht.split("\\#+");
                        int id = Integer.parseInt(temp[0]);
                        String text = temp[1];
                        for(int i=0; i<cards.length; i++){
                            if(cards[i] == null) cards[i] = new Card(id,0, text);
                        }
                        updateCards();

                        //Whitecard wird ersetzt
                    }
                    else if(nachricht.contains("#-")){

                        String[] temp = nachricht.split("\\#-");
                        int id = Integer.parseInt(temp[0]);
                        String text = temp[1];
                        for(int i=0; i<cards.length; i++){
                            if(cards[i].art == 0) cards[i] = new Card(id,1, text);
                        }
                        updateCards();

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
                        updateCards();
                    }

                        
                    
                }
            }catch(Exception ex){
            }
        }
    }

    public void updateCards(){
        for(int i=0; i<cards.length; i++){
            jpnl[1].add(cards[i]);
        }
    }

    //Karte hat ID 

}
