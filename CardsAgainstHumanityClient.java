import javax.swing.*;
import java.net.*;
import java.awt.*;
import java.util.*;
import java.net.*;
import java.util.*;
import java.io.*;
import javax.swing.text.StyleContext;
import javax.swing.text.StyleConstants;
import java.awt.event.*;

public class CardsAgainstHumanityClient extends JFrame
{

    Card[] cards = new Card[8];
    Card blackCard = null;
    ArrayList<Card> cardsOther = new ArrayList<Card>();
    JPanel[] jpnl = new JPanel[3];
    BufferedReader reader;
    PrintWriter writer;
    MyButton sender = null;
    JTextArea spielerListe = new JTextArea();
    ArrayList<Spieler> spieler = null;
    int leer = 0;
    boolean cardSzar = false, amZug = true;

    Socket sock;

    public class MyButton extends JLabel {
        String text = "";
        PrintWriter writer;
        Card selected = null;

        public MyButton(String text, PrintWriter writer){
            this.text = text;
            this.writer = writer;
            this.setText(text);
            Font font = new Font("Verdana", Font.PLAIN, 40);
            this.setFont( font);
            this.setOpaque(true);

            this.addMouseListener(new MouseAdapter(){
                    public void mouseClicked(MouseEvent e) {

                        if(amZug){

                            System.out.println("gucke nach selected");
                            Integer[] selected = getSelected();
                            //System.out.println("selected u.A.: "+selected[0]);
                            for(int i=0; i<selected.length; i++){
                                writer.println("*"+selected[i]);
                                System.out.println("Karte gesendet: "+selected[i]);
                            }
                            writer.flush();


                            setBackground(Color.YELLOW);
                            setForeground(Color.white);
                            paintComponent(getGraphics());
                            try{
                                Thread.sleep(300);}catch(Exception ex){};
                            setBackground(Color.GRAY);
                            setForeground(Color.black);

                            amZug = false;
                        }
                    }                
                });

        }

        public void setSelected(Card c){
            selected = c;
        }

    }
    public CardsAgainstHumanityClient()
    {
        super();
        setBounds(600,400,600,400);

        for(int i=0; i<jpnl.length; i++){
            jpnl[i] = new JPanel();
        }

        setLayout(new GridLayout(2,1)); 
        jpnl[0].setLayout(new BorderLayout());
        jpnl[1].setLayout(new GridLayout(1,cards.length+2)/*new FlowLayout()*/);
        jpnl[2].setLayout(new FlowLayout());
        jpnl[2].setBackground(Color.white);

        add(jpnl[0]);
        add(jpnl[1]);
        jpnl[0].add(jpnl[2],"Center");

        netzwerkEinrichten();

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
        
        jpnl[0].add(blackCard, "West");
    }

    public void updateCardsOther(){
        jpnl[2].removeAll();
        System.out.println("update cards other");
        
        for(int i=0; i<cardsOther.size(); i++){
            jpnl[2].add(cardsOther.get(i));
        }
        
    }



    public Integer[] getSelected(){
        ArrayList<Integer> select = new ArrayList<Integer>();
        if(cardSzar){for(int i=0; i<cardsOther.size(); i++){

                if(cardsOther.get(i)!=null && cardsOther.get(i).selected){
                    select.add(cardsOther.get(i).id);
                    cardsOther.remove(i);
                }

            }}
        else{
            for(int i=0; i<cards.length; i++){
                if(i<cards.length && cards[i]!=null && cards[i].selected){ 
                    select.add(cards[i].id);
                    cards[i] = null;
                }
            }
        }
        Integer[] selected = select.toArray(new Integer[select.size()]);
        return selected;
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

        Thread readerThread = new Thread(new EingehendReader());
        readerThread.start();

        sender = new MyButton("Senden",writer);
        jpnl[1].add(sender);
        jpnl[1].add(spielerListe);
        

        spielerListe.setLineWrap(true);
        spielerListe.setWrapStyleWord(true);
        spielerListe.setEditable(false);

    }

    public void nachrichtVerarbeiten(String nachricht){
        int it=0;

        if(nachricht.contains("°")){

            String[] temp = nachricht.split("°");
            int id = Integer.parseInt(temp[0]);
            String text = temp[1];
            sender.setText("Senden");
             writer.println("p");
             writer.flush();
            System.out.println("Anfrage geschickt");
            for(int i=0; i<cards.length; i++){
                if(cards[i] == null){ 
                    cards[i] = new Card(id,0, text); 
                    log("Neue Karte: "+cards[i].text);
                    updateMyWhitecards();
                    return;
                }
                if(cardSzar) cards[i].selectable = false;
                else cards[i].selectable = true;
            }
            
            amZug = true;
            
   
       
    

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
        else if(nachricht.contains("::")){
            cardSzar = true;
            sender.setText("CardSzar");

        }
        else if(nachricht.contains("=")){
            
            String[] temp = nachricht.split("=");
            spielerListe.setText(nachricht);
            spieler.clear();
            for(int i=0; i<temp.length; i++){
                String[] temp2 = temp[i].split("%");
                spieler.add(new Spieler(Integer.parseInt(temp2[0])));
                spieler.get(spieler.size()-1).punkte = Integer.parseInt(temp2[1]);
                spielerListe.append(spieler.get(i).spielerID+": "+spieler.get(i).punkte+ "\n");
            }
            
            
        }
        else if(nachricht.contains("<")){
            String[] temp = nachricht.split("<");
            
            int id = Integer.parseInt(temp[0]);
            
            String text = temp[1];
            
            cardsOther.add( new Card(id,0,text));
            System.out.println("Ich bin cardSzar: "+cardSzar);
            if(!cardSzar) cardsOther.get(cardsOther.size()-1).selectable = false;
            else cardsOther.get(cardsOther.size()-1).selectable = true;
            updateCardsOther();
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

                    nachrichtVerarbeiten(nachricht);
                }
            }catch(Exception ex){
            }
        }
    }

    //Karte hat ID 
}
