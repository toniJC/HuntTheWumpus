package HAEW;

import javax.swing.*;

public class PantallaCargandoMain {

    PantallaCargando screen;

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: N/A
     * @Descripción: Inicializa los componentes visuales del cuadro carga de la pantalla.
     */
    public PantallaCargandoMain() {
        inicioPantalla();
        screen.velocidadDeCarga();
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: N/A
     * Descripción: Arranca la pantalla de carga de la aplicacion.
     */
    private void inicioPantalla() {
        java.net.URL img = getClass().getResource("Image/LogoCarga.png");
        ImageIcon myImage =  new ImageIcon(img);
        screen = new PantallaCargando(myImage);
        screen.setLocationRelativeTo(null);
        screen.setProgresoMax(100);
        screen.setVisible(true);
    }
}
