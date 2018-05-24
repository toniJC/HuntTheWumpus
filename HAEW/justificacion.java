package HAEW;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.*;

public class justificacion implements ActionListener {

    JFrame cuadroDeJustificacion = new JFrame("Justifica tu movimiento");
    JPanel panelDeJustificacion = new JPanel();
    JLabel JustificacionLabel = new JLabel("Justificación:");
    JButton aceptarButton = new JButton("Aceptar");
    JTextField UsuarioText = new JTextField(20);
    private static final Logger log = Logger.getLogger(introducirNombre.class.getName());

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: N/A
     * Descripción: Es el contructor de la clase.
     */
    public justificacion() {

    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: void
     * Descripción: Inicia los componentes necesarios para la justificacion de un movimiento.
     */
    public void inicia() {
        Handler fileHandler = null;
        Formatter simpleFormattter = null;

        try {
            fileHandler = new FileHandler("./log");
            simpleFormattter = new SimpleFormatter();
        } catch (IOException e) {
            e.printStackTrace();
        }

        cuadroDeJustificacion.setSize(275, 90);
        cuadroDeJustificacion.add(panelDeJustificacion);
        placeComponents(panelDeJustificacion);
        cuadroDeJustificacion.setVisible(false);
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: void
     * Descripción: Inicializa los componentes para la introduccion de la justificacion
     */
    public void introduce() {
        cuadroDeJustificacion.setVisible(true);
        JustificacionLabel.setText("");

    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: JPAnel panel
     * @return: void
     * Descripción: Ubica los componentes en las ubicaciones de la pantalla deseadas.
     */
    public void placeComponents(JPanel panel) {

        JustificacionLabel.setBounds(10, 10, 80, 25);
        panel.add(JustificacionLabel);

        UsuarioText.setBounds(10, 10, 160, 25);
        panel.add(UsuarioText);

        aceptarButton.setBounds(180, 80, 80, 25);
        panel.add(aceptarButton);

        aceptarButton.addActionListener(this::actionPerformed);


    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: ActionEvent e
     * @return: void
     * Descripción: Implementa el observador de los eventos seleccionados.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == aceptarButton) {
            cuadroDeJustificacion.setVisible(false);
            log.info(UsuarioText.getText());
        }
        UsuarioText.setText(" ");
    }
}