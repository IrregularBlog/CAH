import javax.swing.*;
import java.net.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.net.*;
import java.awt.*;
import java.util.*;
import java.io.*;

public class CAHServer
{

    ArrayList clientAusgabeStröme;
    ArrayList <Card> whiteList = null,blackList = null;
    
    int id = 0;
    
    

    public class ClientHandler implements Runnable{
        BufferedReader reader;
        Socket sock;

        public ClientHandler(Socket clientSocket){
            try{
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
            }catch(Exception ex) {ex.printStackTrace();}
        }

        public void run(){
            String nachricht;
            try{
                loadCards("whitecards.txt");
                while((nachricht = reader.readLine()) != null){
                    System.out.println("gelesen: "+nachricht);
                    
                    //esAllenWeitersagen(nachricht);
                }
            }catch(Exception ex) {ex.printStackTrace();}
        }

    }

    public CAHServer()
    {
        los();
    }

    public void los(){
        clientAusgabeStröme = new ArrayList();

        try{
            ServerSocket serverSock = new ServerSocket(5000);
            while(true){
                Socket clientSocket = serverSock.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientAusgabeStröme.add(writer);

                Thread t = new Thread(new ClientHandler(clientSocket));
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
    
    public void kartenVerteilen(int wieViel){
         Iterator it = clientAusgabeStröme.iterator();
         
         ArrayList<Card> whiteListD = (ArrayList<Card>)whiteList.clone();
         
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

    public void loadCards(String daten){
        try{
            File file = new File(daten);
            
            BufferedReader readerz = new BufferedReader(new FileReader(daten));
            String s = "";
            String in = "";
            
            if(id==0){ whiteList= new ArrayList<Card>();}
            else blackList = new ArrayList<Card>();
            while ((s = readerz.readLine()) != null) {
                in += s;
                if (blackList == null){
                    Card tempCard = new Card(id,0,s);
                    whiteList.add(tempCard);}
                else{ Card tempCard = new Card(id,1,s);
                    blackList.add(tempCard);
                }
                id++;
             }
            readerz.close();
        
        }catch(Exception ex){}
    }
    
    public void main(String[] args){
        CAHServer server = new CAHServer();
    
    }

}
