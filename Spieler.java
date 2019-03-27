
import java.util.*;

public class Spieler
{
    int spielerID;
    int punkte=0;
    ArrayList <Integer> karte = new ArrayList<Integer>();
    boolean cardSzar = false;
    
    public Spieler(int spielerID)
    {
        this.spielerID = spielerID;
        
    }
    
    public void setPunkte(int points){
        punkte = points;
    }
    
    public void addKarte(int id){
        karte.add(id);
    }
    
    public void clearKarten(){karte.clear();}
    
}
