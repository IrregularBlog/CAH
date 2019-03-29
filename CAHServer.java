import javax.swing.*;
import java.net.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.net.*;
import java.awt.*;
import java.util.*;
import java.io.*;

public class CAHServer implements Runnable
{

    ArrayList clientAusgabeStröme;
    ArrayList <Card> whiteList = null;
    ArrayList <Card> blackList = null;
    ArrayList <Spieler> spieler = new ArrayList <Spieler>();
    boolean spielstart = true, cardSzarDran = false;
    Thread serverThread = new Thread();

    int id = 0, zähler =0, runden = 0;
    public class ClientHandler implements Runnable{
        BufferedReader reader;
        Socket sock;
        int clientNr = 0;

        public ClientHandler(Socket clientSocket){
            try{
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
            }catch(Exception ex) {ex.printStackTrace();}
        }

        public void nachrichtVerarbeiten(String nachricht){
            if(nachricht.contains("*")){ 
                nachricht = nachricht.replace("*","");
                int siegID = -1;
                //nachricht.replace("*","");
                
                if(!spieler.get(clientNr).cardSzar){
                    spieler.get(clientNr).addKarte(Integer.parseInt(nachricht));
                    System.out.println("Spieler: "+spieler.get(clientNr).spielerID+" hat nun: "+whiteList.get(Integer.parseInt(nachricht)).text);
                    if(alleSpielerBisAufCS()){
                        System.out.println("Karten der anderen");
                        kartenDerAnderen();
                    }
                }
                else{ 
                    siegID = Integer.parseInt(nachricht);
                    werHatDieKarteGespielt(siegID).punkte++;
                    neueRunde();
                }
            }
            else if(nachricht.contains("p")){
                
                spielerSenden(clientNr);
            }

        }

        public void run(){
            String nachricht;
            try{
                neueWhiteCardsPersonal(3, clientNr);
                neueBlackCard();
                
                if(clientNr == 1) neueRunde();
                

                while(true){

                    //System.out.println("Bin in der schleife");
                    if(sock.isClosed() || !sock.isConnected()){ 
                        clientAusgabeStröme.remove(clientNr);
                        System.out.println("Ausgelogged: "+clientNr);
                        return;
                    }
                    //nachricht = reader.readLine();
                    //System.out.println("Bin in der schleife 2" + reader);
                    if((nachricht = reader.readLine())!=null) nachrichtVerarbeiten(nachricht);
                }

            }catch(Exception ex) {ex.printStackTrace();}
        }

    }
    
    public void run(){
        los();
    }

    public CAHServer()
    {
        
        loadCards("whitecards.txt");
        loadBlackCards("blackcards.txt");
        System.out.println("Loaded cards");
        serverThread.start();
        
    }
    
    public void spielerSenden(int clientNr){
        
        PrintWriter writer = (PrintWriter) clientAusgabeStröme.get(clientNr);
        System.out.println("Hab keine PRo");
        for(int i=0; i<spieler.size(); i++){
                    
                    if(i<(spieler.size()-1))writer.print(spieler.get(i).spielerID+"%"+spieler.get(i).punkte+"=");
                    else writer.print(spieler.get(i).spielerID+"%"+spieler.get(i).punkte);
                }
        writer.flush();
        
    }

    public Spieler werHatDieKarteGespielt(int id){
        for(int i=0; i<spieler.size(); i++){
            for(int j=0; j<spieler.get(i).karte.size(); j++){
                if(spieler.get(i).karte.get(j) == id) return spieler.get(i);

            }
        }
        return null;
    }

    public boolean alleSpielerBisAufCS(){
        boolean fertig = true;
        for(int i=0; i<spieler.size(); i++){
            if(spieler.get(i).karte.size() == 0 && !spieler.get(i).cardSzar){ fertig = false;
             System.out.println("Abbruch: "+spieler.get(i).spielerID+"hat nun: "+spieler.get(i).karte.size()+" Karten");
            }
            
        }
        
        return fertig;
    }

    public void spielerKartenZurücksetzen(){
        for(int i=0; i<spieler.size(); i++){

            spieler.get(i).clearKarten();
        }
    }

    public void neueRunde(){
       
        spielerKartenZurücksetzen();
        neueWhiteCards(1);
        neueBlackCard();
        spieler.get(runden%spieler.size()).cardSzar = true;
        
        PrintWriter writer = (PrintWriter) clientAusgabeStröme.get(runden%spieler.size());
        writer.println("::");
        writer.flush();
        
        runden++;
    }

    public void los(){
        clientAusgabeStröme = new ArrayList();

        try{
            ServerSocket serverSock = new ServerSocket(5000);
            System.out.println("Serversocket steht");
            while(true){
                Socket clientSocket = serverSock.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());

                clientAusgabeStröme.add(writer);
                //spieler.add(new Spieler(zähler));
                zähler++;
                

                ClientHandler c = new ClientHandler(clientSocket);
                
                c.clientNr = clientAusgabeStröme.size()-1;
                spieler.add(new Spieler(c.clientNr));
                
                
                Thread t = new Thread(c);

                t.start();
                
                
                System.out.println("habe eine Verbindung");

            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void esAllenWeitersagen(String nachricht){

        Iterator it = clientAusgabeStröme.iterator();

        while(it.hasNext()){
            try{
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(nachricht);
                writer.flush();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public void kartenDerAnderen(){
        Iterator it = clientAusgabeStröme.iterator();

        
        while(it.hasNext()){
            try{
                PrintWriter writer = (PrintWriter) it.next();
                for(int i=0; i<spieler.size(); i++){
                    for(int j=0; j<spieler.get(i).karte.size(); j++){
                        whiteList.get(spieler.get(i).karte.get(j)).sendenZuAnderen(writer);
                    }
                }

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
    public void neueWhiteCardsPersonal(int wieViel, int wer){
       PrintWriter writer = (PrintWriter) clientAusgabeStröme.get(wer);
       
       ArrayList<Card> whiteListD = null;
       whiteListD = (ArrayList<Card>) whiteList.clone();
       for(int i=0; i<wieViel; i++){
                    int zufallsZahl = (int) (Math.random()*whiteListD.size());
                    whiteListD.get(zufallsZahl).senden(writer);
                    whiteListD.remove(zufallsZahl);
                }
    }
    public void neueWhiteCards(int wieViel){
        Iterator it = clientAusgabeStröme.iterator();

        ArrayList<Card> whiteListD = null;
        whiteListD = (ArrayList<Card>) whiteList.clone();
        System.out.println("Clientanzahl: "+clientAusgabeStröme.size());

        while(it.hasNext()){
            try{
                PrintWriter writer = (PrintWriter) it.next();

                for(int i=0; i<wieViel; i++){
                    int zufallsZahl = (int) (Math.random()*whiteListD.size());
                    whiteListD.get(zufallsZahl).senden(writer);
                    whiteListD.remove(zufallsZahl);
                }

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public void neueBlackCard(){
        Iterator it = clientAusgabeStröme.iterator();
        int zufallsZahl = (int) (Math.random()*blackList.size());
        while(it.hasNext()){
            try{
                PrintWriter writer = (PrintWriter) it.next();

                blackList.get(zufallsZahl).senden(writer);
            }catch(Exception ex){
                ex.printStackTrace();
            }

        }
    }

    public void loadBlackCards(String daten){
        try{
            File file = new File(daten);

            BufferedReader readerz = new BufferedReader(new FileReader(daten));
            String s = "";
            String in = "";

            blackList = new ArrayList<Card>();

            while (!(s=readerz.readLine()).isEmpty()) {
                in += s;
                Card tempCard = new Card(id,1,s);
                blackList.add(tempCard);
                id++;
            }

            readerz.close();

        }catch(Exception ex){}
    }

    public void loadCards(String daten){
        try{
            File file = new File(daten);

            BufferedReader readerz = new BufferedReader(new FileReader(daten));
            String s = "";
            String in = "";

            whiteList = new ArrayList<Card>();

            while (!(s=readerz.readLine()).isEmpty()) {
                in += s;
                if (blackList == null){

                    Card tempCard = new Card(id,0,s);
                    whiteList.add(tempCard);
                    System.out.println("Server hat nun: "+tempCard.text);
                }
                else{ Card tempCard = new Card(id,1,s);
                    blackList.add(tempCard);
                }
                id++;
            }

            readerz.close();
        }catch(Exception ex){}
    }

    public void main(String[] args){
        //CAHServer server = new CAHServer();

    }

}
