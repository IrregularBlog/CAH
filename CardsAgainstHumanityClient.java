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
    
    Card[] cards = new Card[10];
    Card blackCard = null;
    ArrayList<Card> cardsOther = new ArrayList<Card>();
    JPanel[] jpnl = new JPanel[4];
    BufferedReader reader;
    PrintWriter writer;
    MyButton sender = null;
    JTextArea spielerListe = new JTextArea();
    ArrayList<Spieler> spieler = new ArrayList<Spieler>();
    String ip = "";
    int port = 5000;

    
    int leer = 0;
    boolean cardSzar = false, amZug = true,dumm = false;

    Socket sock;

    public class MyButton extends JLabel {
        String text = "";
        PrintWriter writer;
        Card selected = null;

        public MyButton(String text, PrintWriter writer){
            this.text = text;
            this.writer = writer;
            this.setText(text);
            Font font = new Font("Verdana", Font.PLAIN, 20);
            this.setFont( font);
            this.setOpaque(true);

            this.addMouseListener(new MouseAdapter(){
                    public void mouseClicked(MouseEvent e) {

                        if(amZug){

                            System.out.println("gucke nach selected");
                            Integer[] selected = getSelected();
                            //System.out.println("selected u.A.: "+selected[0]);
                            if (selected.length != 1) dumm = true;
                            
                            if(!dumm){
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
                                log(this+ ": Nicht mehr am Zug");
                            }
                            else{
                                JOptionPane.showMessageDialog(null,
                                    "Du hast das Spiel nicht verstanden, als CardSzar kannst du keine Karten spielen und sonst nie mehr als eine Karte");
                                    dumm = false;
                            }

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
        setBounds(600,400,800,400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        for(int i=0; i<jpnl.length; i++){
            jpnl[i] = new JPanel();
        }

        setLayout(new GridLayout(2,1)); 
        jpnl[0].setLayout(new GridLayout(1,2));
        jpnl[1].setLayout(new GridLayout(1,cards.length+2)/*new FlowLayout()*/);
        jpnl[2].setLayout(new BoxLayout(jpnl[2], BoxLayout.Y_AXIS));
        jpnl[2].setBackground(Color.white);

        add(jpnl[0]);
        add(jpnl[1]);
        jpnl[0].add(jpnl[2]);
        jpnl[0].add(jpnl[3]);
        
        jpnl[3].setLayout(new BorderLayout());
        

        
        ip = (String)JOptionPane.showInputDialog(
                    this,
                    "Gib die IP Adresse ein\n",
                    "IP Adresse",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "IP Adresse");
                    
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
        jpnl[3].removeAll();
        jpnl[3].add(blackCard, "Center");
    }

    public void updateCardsOther(){
        jpnl[2].removeAll();
        if(cardSzar)System.out.println("update cards other");

        for(int i=0; i<cardsOther.size(); i++){
            jpnl[2].add(cardsOther.get(i));
        }
        
        jpnl[2].repaint();

    }

    public Integer[] getSelected(){
        ArrayList<Integer> select = new ArrayList<Integer>();
        if(cardSzar){
            for(int i=0; i<cardsOther.size(); i++){

                if(cardsOther.get(i)!=null && cardsOther.get(i).selected){
                    select.add(cardsOther.get(i).id);
                    cardsOther.remove(i);

                }

            }}
           
        else{
            for(int i=0; i<cards.length; i++){
                if(i<cards.length && cards[i]!=null && cards[i].selected){ 
                    select.add(cards[i].id);
                    jpnl[1].remove(cards[i]);
                    jpnl[1].repaint();
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
            //"127.0.0.1"
            sock = new Socket(ip,5000);
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
        System.out.println("Nachricht bekommen"+nachricht);

        if(nachricht.contains("°")){

            String[] temp = nachricht.split("°");
            int id = Integer.parseInt(temp[0]);
            String text = temp[1];

            //cardSzar = false;
            for(int i=0; i<cards.length; i++){
                if(cards[i] == null){ 
                    cards[i] = new Card(id,0, text); 
                    log("Neue Karte: "+cards[i].text);
                    updateMyWhitecards();
                    return;
                }
            }

            // for(Card c: cards){
            // c.usable = true;
            // }


            //Whitecard wird ersetzt
        }
        else if(nachricht.contains("±")){

            String[] temp = nachricht.split("±");
            int id = Integer.parseInt(temp[0]);
            String text = temp[1];

            blackCard = new Card(id,1, text);
            blackCard.setBounds(0, 0, 60, 100);
            updateMyBlackCard();

            //Blackcard wird ersetzt
        }
        else if (nachricht.equals("Start")){

            
            sender.setText("Senden");
            amZug = true;

            jpnl[2].removeAll();
            jpnl[2].repaint();

            cardsOther.clear();

            cardSzar = false;


        }
        else if(nachricht.equals(":")){

            
            cardSzar = true;
            sender.setText("CardSzar");


        }
        else if(nachricht.contains("=")){

            String[] temp = nachricht.split("=");

            spieler.clear();
            spielerListe.setText("");
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
            if(cardSzar)System.out.println("Als CardSzar karte der anderen bekommen: "+cardsOther.get(cardsOther.size()-1).text);
            //if(!cardSzar) cardsOther.get(cardsOther.size()-1).usable = false;
            //else cardsOther.get(cardsOther.size()-1).usable = true;
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
    
    public static void main(String[] args){
        CardsAgainstHumanityClient c = new CardsAgainstHumanityClient();
    
    }

    //Karte hat ID 
}
