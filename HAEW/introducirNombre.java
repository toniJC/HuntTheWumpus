package HAEW;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.*;

public class introducirNombre implements ActionListener{

    JTextField userText = new JTextField(20);
    JTextField passwordText = new JTextField(20);

    JFrame frame = new JFrame("Nombre Completo");

    private static final Logger log = Logger.getLogger(introducirNombre.class.getName());

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: N/A
     * Descripción: Inicializa los componentes visuales del cuadro de introduccion del nombre.
     */
    public void inicia() {

        frame.setSize(300, 150);
        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);
        frame.setVisible(true);

    }
    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: Jpanel panel
     * @return: N/A
     * Descripción: Crea los componentes necesarios para la introduccion del nombre
     */

    private void placeComponents(JPanel panel)  {


        panel.setLayout(null);

        JLabel userLabel = new JLabel("Nombre");
        userLabel.setBounds(10, 10, 80, 25);
        panel.add(userLabel);

        userText.setBounds(100, 10, 160, 25);
        panel.add(userText);

        JLabel passwordLabel = new JLabel("Apellidos");
        passwordLabel.setBounds(10, 40, 80, 25);
        panel.add(passwordLabel);

        passwordText.setBounds(100, 40, 160, 25);
        panel.add(passwordText);

        JButton registerButton = new JButton("Aceptar");
        registerButton.setBounds(180, 80, 80, 25);
        panel.add(registerButton);

        registerButton.addActionListener(this::actionPerformed);
    }
    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: ActionEvent event
     * @return: N/A
     * Descripción: Metodo abstracto realizar accion segun el evento
     */
    @Override
    public void actionPerformed(ActionEvent event) {

        ClaseInicial ww = new ClaseInicial();
        try {
            ww.inicia();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        frame.setVisible(false);
        Handler fileHandler = null;
        Formatter simpleFormattter = null;
        try {
            fileHandler = new FileHandler("./Ejercicio Wumpus "+ userText.getText() + " " + passwordText.getText());
            simpleFormattter = new SimpleFormatter();
        } catch (IOException a) {
            a.printStackTrace();
        }

        log.addHandler(fileHandler);
        fileHandler.setFormatter(simpleFormattter);
        log.info("Nombre del Alumno: " + userText.getText() + " " + passwordText.getText());
    }
}