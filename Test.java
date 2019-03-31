import javax.swing.*;
public class Test extends JFrame
{
    // Instanzvariablen - ersetzen Sie das folgende Beispiel mit Ihren Variablen
    private int x;

    /**
     * Konstruktor für Objekte der Klasse Test
     */
    public Test()
    {
        super();
        setBounds(0,0,200,100);
        setVisible(true);
    }

    public static void main(String[] args){
        Test t = new Test();
    }
}
