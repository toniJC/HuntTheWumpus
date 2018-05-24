package HAEW;

import javax.swing.*;
import java.awt.*;

public class PantallaCargando extends JWindow {
    BorderLayout borderLayout1 = new BorderLayout();
    JLabel imageLabel = new JLabel();
    JPanel southPanel = new JPanel();
    FlowLayout southPanelFlowLayout = new FlowLayout();
    JProgressBar progressBar = new JProgressBar();
    ImageIcon imageIcon;

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: ImageIcon imageicon
     * @return:  N/A
     * Descripción: Es el constructor de la clase, inicializa el icono de la aplicacion
     * @param imageIcon
     */
    public PantallaCargando(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
        dibujaVentana();
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: void
     * Descripción: Coloca los compoenentes necesarios en la pantalla
     */
    public void dibujaVentana() {
        imageLabel.setIcon((Icon) imageIcon);
        this.getContentPane().setLayout(borderLayout1);
        southPanel.setLayout(southPanelFlowLayout);
        southPanel.setBackground(Color.BLUE);
        this.getContentPane().add(imageLabel, BorderLayout.CENTER);
        this.getContentPane().add(southPanel, BorderLayout.SOUTH);
        southPanel.add(progressBar, null);
        this.pack();
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int progresoMaximo
     * @return: void
     * Descripción: Reecoge el maximo progreso posible e inicializa la barra de progreso con ese valor.
     */
    public void setProgresoMax(int progresoMaximo) {
        progressBar.setMaximum(progresoMaximo);
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int progresoACT
     * @return: void
     * Descripción: inicializa el progreso que queremos ponerle a la barra de progreso
     */
    public void setProgreso(int progresoACT) {
        final int progreso = progresoACT;
        progressBar.setValue(progreso);
    }
    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: void
     * Descripción: crea el efecto de carga de la barra de progreso.
     */
    public void velocidadDeCarga() {
        for (int i = 0; i <= 100; i++) {
            for (long j = 0; j < 1000000; ++j)//modifica el numero segun la velidad q desees
            {
                String poop = " " + (j + i);
            }
            //setProgreso("Carcgando..." + i, i);  // si quieres q muestre los numeros y un mensaje
            setProgreso(i);        //si no quieres q muestre nada, solo la barra
        }
        dispose();
    }

}