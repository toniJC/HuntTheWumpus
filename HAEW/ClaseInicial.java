package HAEW;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.logging.*;

public class ClaseInicial extends JFrame {

    /*
     * Main program
     */
    Logger logger = Logger.getLogger("myLogger");

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: string args
     * @return: N/A
     * Descripción: Este es el punto de entrada de la herramienta.
     */
    public static void main(String args[]) throws IOException {
        new PantallaCargandoMain();
        introducirNombre nombre =  new introducirNombre();
        nombre.inicia();
    }

    /*
     * Constructor
     */

    public ClaseInicial() {
        super();
    }
    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: N/A
     * Descripción: Inicializa todos los compoenentes necesarios para arrancar la GUI
     */
    public void inicia() throws IOException {
        JRootPane rootPane = getRootPane();
        Container contentPane = rootPane.getContentPane();

        InterfazPrincipal GUI = new InterfazPrincipal(this);
        rootPane.setJMenuBar(GUI.getBarraMenu());
        contentPane.add(GUI);
        setTitle("IA1 El mundo del Wumpus");
        setLocation(0, 0);
        setSize(1355, 741);
        setVisible(true);
    }
}
