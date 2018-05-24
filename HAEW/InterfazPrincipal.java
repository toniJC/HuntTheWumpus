package HAEW;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

public class InterfazPrincipal extends JPanel
        implements WindowListener, ActionListener, KeyListener, Observer {


    private static int altoEstadisticas = 10;                    // ALTO DEL PANEL DE ESTADISTICAS
    private static int anchoDelTexto = 60;                     // ANCHO DEL PANEL
    private static int tiempoAntesDeReinicio = 2;                 // SEGUNDOS ANTES DE REINICIAR

    /*
     * Instance variables
     */

    private Container contenedorPadre;                             // CONTENEDOR PADRE DEL JUEGO
    private boolean applet;                               // APPLET SI O NO
    private Mundo mundoDelJuego;                               // VARIABLE DEL JUEGO
    private JMenuBar barraMenu;                             // BARRA DEL MENU

    private JRadioButtonMenuItem humano;               // JUGADOR HUMANO
    private JRadioButtonMenuItem boot;               // JUGADOR BOOT

    private JMenuItem itemAyuda;                           // PESTAÑA DE AYUDA
    private JMenuItem cargarItem;                           // CARGAR ITEM
    private JCheckBoxMenuItem reinicioItem;            // ELEMENTO DE AUNTOREINICIO

    private JTextArea areaDeEstadisticas;                          // AREA DE ESTADISITICAS
    private JTextArea areaDeMensajes;                        // AREA DE MENSAJES

    private int altoMensaje;                                // ALTO DEL AREA DE MENSAJES

    private JTextArea areaDeAyuda;                          // PESTAÑA DE AYUDA
    private JPanel panelDeAyuda;                            // PANEL DE LA PESTAÑA DE AYUDA
    private String[] textoAyuda;                           // TEXTO DEL AREA DE AYUDA
    private int paginaAyuda;                                // NUMERO DE PAGINAS DE AYUDA
    private JButton botonTextoAyudaSiguiente;                            // BOTON DE SIGUIENTE EN LA AYUDA
    private JButton botonTextoAyudaAnterior;                            // BOTON DE ANTERIOR EN LA AYUDA

    private DisplayPanel canvas;                         // EL PANEL D JUEGO AL USO

    private AgenteGlobal[] agentes;                        // ARRAY DE AGENTES EN JUEGO
    private int identificacionDelAgente;                                   // IDENTIFICACION DE LOS AGENTES

    private int velocidad;                                   // MOVIMIENTOS POR MINUTO
    private MultiplesPasos pasoMultiple;                             // MULTIPASO

    private boolean presionadaTeclaControl;                        // SE HA PRESIONADO CTRL
    private String nuevoTamañoDelTablero;                               // NUEVO TAMAÑO DE TABLERO

    private File enlaceAFichero;                                   // FICHERO DE TABLERO
    private Graphics g;


    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: Container padre
     * @return: N/A
     * Descripción: inicializa la interfaz principal de la aplicacion
     */
    public InterfazPrincipal(Container padre) {
        super();

        this.contenedorPadre = padre;

        if (padre instanceof JApplet) {
            applet = true;
        } else if (padre instanceof JFrame) {
            applet = false;
        } else {
            System.err.println("Error: No se reconoce el tipo de parametro! --> saliendo...");
            System.exit(-1);
        }


        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
        }

        mundoDelJuego = new Mundo();
        mundoDelJuego.addObserver(this);
        velocidad = 100;
        pasoMultiple = new MultiplesPasos(mundoDelJuego, null, velocidad);
        pasoMultiple.addObserver(this);

        presionadaTeclaControl = false;
        nuevoTamañoDelTablero = "";
        agentes = new AgenteGlobal[1];
        identificacionDelAgente = 0;

        agentes[0] = new AgenteNormal(mundoDelJuego, pasoMultiple);

        barraMenu = new JMenuBar();

        JMenu menuJuego = new MyJMenu("Juego", ' ');
        if (!applet) {
            menuJuego.add(new MyJMenuItem("Nuevo ( nxn )", null, "newN", this));
        } else {
            menuJuego.add(new MyJMenuItem("Nuevo ( nxn )", null, "newN", this));
        }
        if (!applet) {
            menuJuego.addSeparator();
            cargarItem = new MyJMenuItem("Cargar Mapa", null, "cargaFichero", this);
            menuJuego.add(cargarItem);
            menuJuego.addSeparator();
            menuJuego.add(new MyJMenuItem("Salir", null, "salida", this));
        }
        barraMenu.add(menuJuego);
        JMenu menuOpciones = new MyJMenu("Opciones", 'O');
        ButtonGroup grupoBotonera = new ButtonGroup();
        humano = new MyJRadioButtonMenuItem("Humano", null, grupoBotonera, true, "humano", this);
        boot = new MyJRadioButtonMenuItem("Bot", null, grupoBotonera, false, "agente", this);
        menuOpciones.add(humano);
        menuOpciones.add(boot);
        menuOpciones.addSeparator();

        reinicioItem = new JCheckBoxMenuItem("Reinicio Automático");
        reinicioItem.setState(false);
        reinicioItem.setMnemonic('A');
        reinicioItem.setActionCommand("reinicioAutomatico");
        reinicioItem.addActionListener(this);
        menuOpciones.add(reinicioItem);
        menuOpciones.addSeparator();
        barraMenu.add(menuOpciones);

        JMenu menuAyuda = new MyJMenu("?", '?');
        itemAyuda = new MyJMenuItem("Ayuda", null, "ayuda", this);
        menuAyuda.add(itemAyuda);
        menuAyuda.add(new MyJMenuItem("Info...", null, "informacion", this));
        barraMenu.add(menuAyuda);


        areaDeEstadisticas = new JTextArea(altoEstadisticas, 0);
        areaDeEstadisticas.setEditable(false);
        areaDeEstadisticas.setLineWrap(false);
        areaDeEstadisticas.addKeyListener(this);

        altoMensaje = 5;
        areaDeMensajes = new JTextArea(altoMensaje + 2, 10);
        areaDeMensajes.setEditable(false);
        areaDeMensajes.setLineWrap(false);
        areaDeMensajes.addKeyListener(this);


        JPanel panelIzquierdo = new JPanel();
        JPanel panelNorte = new JPanel();

        panelNorte.setLayout(new BorderLayout());
        panelIzquierdo.setLayout(new BorderLayout());

        JPanel panelAyuda1 = new JPanel();
        panelAyuda1.setBorder(new TitledBorder(new EtchedBorder(), "Estadísticas"));
        panelAyuda1.getHeight();
        panelAyuda1.add(areaDeEstadisticas);

        JPanel panelAyuda2 = new JPanel();
        panelAyuda2.setBorder(new TitledBorder(new EtchedBorder(), "Mensajes"));
        panelAyuda2.add(areaDeMensajes);

        JPanel PanelAyuda5 = new JPanel();
        PanelAyuda5.setBorder(new TitledBorder(new EtchedBorder(), "Datos Generales"));

        panelIzquierdo.add(panelAyuda1, BorderLayout.NORTH);
        panelIzquierdo.add(panelAyuda2, BorderLayout.CENTER);

        canvas = new DisplayPanel(mundoDelJuego);
        canvas.addKeyListener(this);
        canvas.setBorder(new EtchedBorder());

        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);
        add(panelIzquierdo, BorderLayout.EAST);
        add(panelNorte, BorderLayout.WEST);

        areaDeEstadisticas.setBackground(panelAyuda1.getBackground());
        areaDeMensajes.setBackground(panelAyuda2.getBackground());


        botonTextoAyudaAnterior = new JButton("<<<");
        botonTextoAyudaAnterior.setActionCommand("anterior");
        botonTextoAyudaAnterior.addActionListener(this);

        botonTextoAyudaSiguiente = new JButton(">>>");
        botonTextoAyudaSiguiente.setActionCommand("siguiente");
        botonTextoAyudaSiguiente.addActionListener(this);

        areaDeAyuda = new JTextArea();
        areaDeAyuda.setEditable(false);
        areaDeAyuda.setLineWrap(false);
        areaDeAyuda.setBorder(new CompoundBorder(new EmptyBorder(6, 6, 6, 6), new LineBorder(Color.black, 1)));

        textoAyuda = new String[6];
        paginaAyuda = 0;
        textoAyuda[paginaAyuda++] =
                "  \n" +
                        "  Explicación del menu                                                                                     \n" +
                        "  Juego: Despliega una lista de las opciones posiobles del juego                                           \n" +
                        "   - Nuevo: carga un nuevo mapa preguntando por el tamaño del tablero.                                     \n" +
                        "   - Cargar Mapa: Carga un mapa prefijado desde archivo.                                                   \n" +
                        "   - Mostrar Mapa: Muestra el mapa del tablero actual.                                                     \n" +
                        "   - Salir: Sale de la aplicación.                                                                         \n" +
                        "  Opciones: Despliega el listado de opciones disponibles.                                                  \n" +
                        "   -Reinicio: Pestaña que activa el reinicio automático pasados 2 segundos desde el final de partida.      \n" +
                        "  ?: Despliega la lista de posibles ayudas en el juego.                                                    \n" +
                        "   - Ayuda: Despliega el panelDeJustificacion de ayuda del juego.                                                         \n" +
                        "                                                                                                           \n" +
                        "                                                                       Página " + paginaAyuda + " de " +
                        textoAyuda.length;
        textoAyuda[paginaAyuda++] =
                "  \n" +
                        "  El mundo del Wumpus:                                                                                     \n" +
                        "  El mundo del wumpus es un juego en el que un cazador armado con un arco y flecha                         \n" +
                        "  explora una caverna intricada con pasadizos y estancias en las que habita el WUMPUS                      \n" +
                        "  un ser mosntruoso y vago que se come a cualquiera que entre en la estancia en la que se encuentra.       \n" +
                        "  Para empeorar las cosas, hay por ahí pozos sin fondo que se tragan a todo lo que caiga en él             \n" +
                        "  menos al wumpus, que no cabe por ellos.                                                                  \n" +
                        "  Entonces, ¿Por qué entrar en la caverna?                                                                 \n" +
                        "  Porque dentro hay, escondido un montón de oro                                                            \n" +
                        "                                                                                                           \n" +
                        "                                                                                                           \n" +
                        "                                                                                                           \n" +
                        "                                                                       Página " + paginaAyuda + " de " +
                        textoAyuda.length;
        textoAyuda[paginaAyuda++] =
                "  \n" +
                        "  Percepciones (Sólo en la casilla que ocupa el agente).                                                   \n" +
                        "  Cuadros adyacentes a los wumpus huelen (Hedor).                                                          \n" +
                        "  Cuadros adyacentes a los pozos tienen Brisa.                                                             \n" +
                        //"  El cuadro donde esta el oro tiene Resplandor.                                                            \n" +
                        "  Si avanza hasta el muro percibe Choque.                                                                  \n" +
                        "  Cuando mata al wumpus percibe Grito.                                                                     \n" +
                        "                                                                                                           \n" +
                        "                                                                                                           \n" +
                        "                                                                                                           \n" +
                        "                                                                                                           \n" +
                        "                                                                                                           \n" +
                        "                                                                       Página " + paginaAyuda + " de " +
                        textoAyuda.length;
        textoAyuda[paginaAyuda++] =
                "  \n" +
                        " Acciones:                                                                                                 \n" +
                        "  Avanzar: Si hay un apared delante del agente esta acción no tiene efecto sobre el mundo.                 \n" +
                        "  Girar 90º a izquierda.                                                                                   \n" +
                        "  Girar 90º a derecha.                                                                                     \n" +
                        "  Morir: Si entre en la celda donde hay un pozo o donde está el wumpus (vivo)                              \n" +
                        "  Disparar: Utiliza la única flecha en la direccion en la que mira el cazador. Llega hasta el wumpus       \n" +
                        "  (y lo mata) o bién, hasta la pared.                                                                      \n" +
                        "  Agarrar: Coge el oro en la casilla en la que se encuentra el agente                                      \n" +
                        "                                                                                                           \n" +
                        "                                                                                                           \n" +
                        "                                                                                                           \n" +
                        "                                                                       Página " + paginaAyuda + " de " +
                        textoAyuda.length;
        textoAyuda[paginaAyuda++] =
                "  \n" +
                        "  Controles                                                                                                \n" +
                        "  Avanzar: El movimiento de avanzar se realiza con la tecla A                                              \n" +
                        "  Derecha: Girar 90º a derecha se reliza con la tecla D                                                    \n" +
                        "  Izquierda: Girar 90º a izquierda se realiza con la tecla I                                               \n" +
                        "  Flecha: Disparar el arco se realiza con la tecla F                                                       \n" +
                        "  Oro: Para coger el oro hay que pulsar la tecla O                                                         \n" +
                        "  Nueva Partida: Una vez finalizada la partida para volver a empezar se reliza con la tecla Enter          \n" +
                        "                                                                                                           \n" +
                        "                                                                                                           \n" +
                        "                                                                                                           \n" +
                        "                                                                                                           \n" +
                        "                                                                       Página " + paginaAyuda + " de " +
                        textoAyuda.length;
        textoAyuda[paginaAyuda++] =
                "  \n" +
                        "                          Formato del fichero:                                                              \n" +
                        "  Las líneas vacías y líneas que comienzan con \\\" # \\ \"se ignoran y pueden aparecer en cualquier parte. \n" +
                        "  La primera valida, sólo debe contener un número entero N (4 <= N <= 16),                                  \n" +
                        "  Que será interpretado como el tamaño del campo de juego,                                                  \n" +
                        "  Después tantas lineas como N hemos introduido separados por espacios en blanco.                           \n" +
                        "  Las entradas posibles son: 0 (cero): campo está vacío, P: campo contiene un pozo,                         \n" +
                        "  W: una Wumpus se esconde aquí, G: un tesoro de oro,                                                       \n" +
                        "  E:. Para la salida No tiene que ser exactamente una salida.                                               \n" +
                        "   El número de flechas del Cazatesoros es igual al número de Wumpuses en el mapa.                          \n" +
                        "  Después de haber creado su propio archivo de mapa Wumpus World le puede cargar a través de                \n" +
                        "  \"Juego -> Cargar Mapa\" \"entrada de menú                                                                \n" +
                        "                                                                       Página " + paginaAyuda + " de " +
                        textoAyuda.length;


        paginaAyuda = 0;
        areaDeAyuda.setText(textoAyuda[paginaAyuda]);
        botonTextoAyudaAnterior.setEnabled(false);
        botonTextoAyudaSiguiente.setEnabled(true);

        panelDeAyuda = new JPanel();
        panelDeAyuda.setBorder(new EtchedBorder());
        panelDeAyuda.setLayout(new BorderLayout());
        panelDeAyuda.add(areaDeAyuda, BorderLayout.CENTER);
        areaDeAyuda.setBackground(panelDeAyuda.getBackground());

        JPanel panelAyuda4 = new JPanel();
        panelAyuda4.setLayout(new FlowLayout());
        panelAyuda4.add(botonTextoAyudaAnterior);
        panelAyuda4.add(botonTextoAyudaSiguiente);
        panelDeAyuda.add(panelAyuda4, BorderLayout.SOUTH);

        mundoDelJuego.juegoNuevo(5); // 5x5 por defecto
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: JMenuBar
     * Descripción: Devuelve la barra del menu de la GUI
     */
    public JMenuBar getBarraMenu() {
        return barraMenu;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: WindowEvent
     * @return: void
     * Descripción: Implementacion de los meteodos extendidos de windowListener
     */
    public void windowOpened(WindowEvent we) {
        canvas.requestFocus();
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: WindowEvent
     * @return: void
     * Descripción: Implementacion de los meteodos extendidos de windowListener
     */
    public void windowClosing(WindowEvent we) {
        ((JFrame) contenedorPadre).dispose();
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: WindowEvent
     * @return: void
     * Descripción: Implementacion de los meteodos extendidos de windowListener
     */
    public void windowClosed(WindowEvent we) {
        System.exit(0);
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: WindowEvent
     * @return: void
     * Descripción: Implementacion de los meteodos extendidos de windowListener
     */
    public void appletStart() {
        canvas.requestFocus();
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: WindowEvent
     * @return: void
     * Descripción: Implementacion de los meteodos extendidos de windowListener
     */
    public void windowActivated(WindowEvent we) {
        canvas.requestFocus();
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: WindowEvent
     * @return: void
     * Descripción: Implementacion de los meteodos extendidos de windowListener
     */
    public void windowDeactivated(WindowEvent we) {
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: WindowEvent
     * @return: void
     * Descripción: Implementacion de los meteodos extendidos de windowListener
     */
    public void windowIconified(WindowEvent we) {
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: WindowEvent
     * @return: void
     * Descripción: Implementacion de los meteodos extendidos de windowListener
     */
    public void windowDeiconified(WindowEvent we) {
        canvas.requestFocus();
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: ActionEvent
     * @return: void
     * Descripción: Implementacion de los meteodos extendidos de ActionEvent
     */
    public void actionPerformed(ActionEvent ae) {
        String accion = ae.getActionCommand();

        if (accion.equals("humano")) {
            agentes[identificacionDelAgente].buzonDeEntrada(new Chivato(Mundo.SINOPERACION, -1, -1, "desactivado")); //TODO:cambiar
            this.mundoDelJuego.tipoJuego = 0;
        } else if (accion.equals("agente")) {
            agentes[identificacionDelAgente].buzonDeEntrada(new Chivato(Mundo.SINOPERACION, -1, -1, "activado")); //TODO:cambiar
            this.mundoDelJuego.tipoJuego = 1;
        } else if (accion.startsWith("new")) {
            pasoMultiple.pause();
            pasoMultiple.setListadoDePasos(null);
            int n = -1;
            if (accion.equals("new4")) {
                n = 4;
            } else if (accion.equals("new16")) {
                n = 16;
            } else if (accion.equals("new8")) {
                n = 8;
            } else {
                String answer = JOptionPane.showInputDialog(this,
                        "Introduce el tamaño del lado (4<=n<=" + (applet ? "8" : "16") + ")!",
                        "Tamaño del tablero...",
                        JOptionPane.QUESTION_MESSAGE);
                if (answer == null) return;
                n = mundoDelJuego.getTamañoDelMapa();

                try {
                    n = (new Integer(answer)).intValue();
                    if (n < 4 || (applet && n > 8) || (!applet && n > 16)) throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            "El valor introducido \"" + answer + "\" no es correcto!",
                            "Error de tamaño...",
                            JOptionPane.ERROR_MESSAGE);
                    n = -1;
                }
            }
            if (n > 3) mundoDelJuego.juegoNuevo(n);
        } else if (accion.equals("ayuda")) {
            JOptionPane.showMessageDialog(this, panelDeAyuda,
                    "Ayuda...",
                    JOptionPane.INFORMATION_MESSAGE);
            repaint();
        } else if (accion.equals("salida")) {
            ((JFrame) contenedorPadre).dispose();
        } else if (accion.equals("siguiente")) {
            if (paginaAyuda >= textoAyuda.length - 1) return;
            botonTextoAyudaAnterior.setEnabled(true);
            areaDeAyuda.setText(textoAyuda[++paginaAyuda]);
            if (paginaAyuda >= textoAyuda.length - 1) {
                botonTextoAyudaSiguiente.setEnabled(false);
                botonTextoAyudaAnterior.requestFocus();
            }
        } else if (accion.equals("anterior")) {
            if (paginaAyuda <= 0) return;
            botonTextoAyudaSiguiente.setEnabled(true);
            areaDeAyuda.setText(textoAyuda[--paginaAyuda]);
            if (paginaAyuda <= 0) {
                botonTextoAyudaAnterior.setEnabled(false);
                botonTextoAyudaSiguiente.requestFocus();
            }
        } else if (accion.equals("cargaFichero")) {
            String eleccion = null;
            JFileChooser ficheroSeleccionado = new JFileChooser();
            ficheroSeleccionado.setCurrentDirectory(enlaceAFichero);
            ficheroSeleccionado.addChoosableFileFilter(new LecturaDeFichero("mundo", "Wumpus Map"));
            ficheroSeleccionado.setDialogTitle("Carga de mapa personalizado");
            if (ficheroSeleccionado.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                eleccion = ficheroSeleccionado.getSelectedFile().toString();
                enlaceAFichero = ficheroSeleccionado.getCurrentDirectory();
                pasoMultiple.pause();
                agentes[identificacionDelAgente].buzonDeEntrada(new Chivato(Mundo.SINOPERACION, -1, -1, "desactivado")); //TODO:cambiar
                String mensajeError = mundoDelJuego.juegoNuevo(eleccion);

                if (mensajeError != null) {
                    JOptionPane.showMessageDialog(this,
                            "Error al cargar el archivo:\n\n" +
                                    mensajeError + "\n\n",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
                if (boot.isSelected()) {
                    agentes[identificacionDelAgente].buzonDeEntrada(new Chivato(Mundo.SINOPERACION, -1, -1, "activado"));
                }
            }
        } else if (accion.equals("reinicioAutomatico")) {
            if (!mundoDelJuego.chequeaJuegoActivado() && reinicioItem.getState()) {
                pasoMultiple.pause();
                pasoMultiple.setListadoDePasos(null);
                mundoDelJuego.iniciaJuego(mundoDelJuego.getTamañoDelMapa());
            }
        }

    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: KeyEvent
     * @return: void
     * Descripción: Implementacion de los meteodos extendidos de KeyListener
     */
    public void keyTyped(KeyEvent evt) {
        //System.out.println("Key Typed");
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: KeyEvent
     * @return: void
     * Descripción: Implementacion de los meteodos extendidos de KeyListener
     */
    public void keyPressed(KeyEvent evt) {
        int key = evt.getKeyCode();  // keyboard code for the key that was pressed
        if (key == KeyEvent.VK_CONTROL) {
            nuevoTamañoDelTablero = "";
            presionadaTeclaControl = true;
        }
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: KeyEvent
     * @return: void
     * Descripción: Implementacion de los meteodos extendidos de KeyListener
     */
    public void keyReleased(KeyEvent evento) {
        int tecla = evento.getKeyCode();  // keyboard code for the tecla that was released

        if (tecla == KeyEvent.VK_CONTROL) { // Ctrl was released
            presionadaTeclaControl = false;
            if (nuevoTamañoDelTablero.length() > 0) {    // if some number was entered while it was pressed
                int nuevoTamaño = -1;  // try to find out which
                try {
                    nuevoTamaño = (new Integer(nuevoTamañoDelTablero)).intValue();
                    if (nuevoTamaño < 4 || (applet && nuevoTamaño > 8) || (!applet && nuevoTamaño > 16))
                        throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    nuevoTamaño = -1;
                }
                if (nuevoTamaño > 0) {
                    pasoMultiple.pause();
                    mundoDelJuego.juegoNuevo(nuevoTamaño); // start a new game
                }
            }
        } else if (presionadaTeclaControl) {
            if (tecla == KeyEvent.VK_A) { // Ctrl-A for toggle auto restart
                reinicioItem.doClick();
                presionadaTeclaControl = false;
            }
        } else if (!mundoDelJuego.chequeaJuegoActivado()) {
            if (tecla == KeyEvent.VK_ENTER) {
                pasoMultiple.pause();
                mundoDelJuego.iniciaJuego(mundoDelJuego.getTamañoDelMapa());
            }
        } else if (tecla == KeyEvent.VK_A) {
            humano.doClick();
            pasoMultiple.pause();
            mundoDelJuego.realizaMovimiento(mundoDelJuego.AVANZAR);
        } else if (tecla == KeyEvent.VK_D) {
            humano.doClick();
            pasoMultiple.pause();
            mundoDelJuego.realizaMovimiento(mundoDelJuego.GIRARDERECHA);
        } else if (tecla == KeyEvent.VK_I) {
            humano.doClick();
            pasoMultiple.pause();
            mundoDelJuego.realizaMovimiento(mundoDelJuego.GIRARIZQUIERDA);
        } else if (tecla == KeyEvent.VK_F) {
            humano.doClick();
            pasoMultiple.pause(); // take over control
            mundoDelJuego.realizaMovimiento(mundoDelJuego.DISPARA);
        } else if (tecla == KeyEvent.VK_O) {
            humano.doClick();
            pasoMultiple.pause();
            mundoDelJuego.realizaMovimiento(mundoDelJuego.RECOGEORO);
        }
        evento.consume();
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: Observable observador, Object arg
     * @return: void
     * Descripción: Implementacion de los meteodos extendidos de Obervador Observable
     */
    public void update(Observable observador, Object arg) {
        Chivato notificacion = (Chivato) arg;

        if (observador == mundoDelJuego) {

            try {
                int altura = getHeight() - areaDeEstadisticas.getHeight() - 100;
                int anchoRelativo = (int) (altura / (areaDeMensajes.getGraphics().getFont().getSize2D() + 2));
                areaDeMensajes.setRows((anchoRelativo > 1 ? anchoRelativo - 1 : 1));
                areaDeMensajes.setPreferredSize(new Dimension(areaDeEstadisticas.getWidth(),
                        (int) ((anchoRelativo > 1 ? anchoRelativo - 1 : 1) * (areaDeMensajes.getGraphics().getFont().getSize2D() + 2))));
                altoMensaje = (anchoRelativo > 2 ? anchoRelativo - 2 : 1);
            } catch (Exception ex) {
            }

            if (notificacion.percepciones == Mundo.JUEGONUEVO) {
                if (areaDeMensajes.getLineCount() + 4 > altoMensaje) {
                    try {
                        int offset = areaDeMensajes.getLineEndOffset(4 - (altoMensaje - areaDeMensajes.getLineCount()));
                        areaDeMensajes.replaceRange(null, 0, offset);
                        areaDeMensajes.setText("");

                    } catch (Exception e) {
                        areaDeMensajes.setText("");
                    }
                }
                areaDeMensajes.setText("");
            }

            if (notificacion.informacion != null && notificacion.informacion.length() > 0) {
                String posicion = "(" + (char) (97 + notificacion.posicionX) + (notificacion.posicionY + 1) + ")-> ";
                String mensaje = notificacion.informacion;
                int longitud = posicion.length();
                StringBuffer buffer = new StringBuffer();
                int lineas = 1;
                while (longitud + mensaje.length() > anchoDelTexto) {
                    int equilibrador = mensaje.lastIndexOf(' ', anchoDelTexto - longitud - 1);
                    equilibrador = (equilibrador <= 0 ? anchoDelTexto - longitud : equilibrador);
                    buffer.append(posicion + mensaje.substring(0, equilibrador) + "\n");
                    ++lineas;
                    posicion = "       ";
                    longitud = 7;
                    mensaje = mensaje.substring(equilibrador);
                }
                buffer.append(posicion + mensaje + "\n\n");
                if (areaDeMensajes.getLineCount() + lineas > altoMensaje) {
                    try {
                        int offset = areaDeMensajes.getLineEndOffset(lineas - (altoMensaje - areaDeMensajes.getLineCount()));
                        areaDeMensajes.replaceRange(null, 0, offset);
                    } catch (Exception e) {
                        areaDeMensajes.setText("");
                    }
                }
                int historial = areaDeMensajes.getText().length();
                areaDeMensajes.replaceRange(null, (historial > 0 ? historial - 1 : 0), historial);
                areaDeMensajes.append(buffer.toString());
            }

            if (notificacion.percepciones == Mundo.MOVIMIENTOCOMPLETADO || notificacion.percepciones == Mundo.JUEGONUEVO) {
                areaDeEstadisticas.setText(
                        "Pozos: " + mundoDelJuego.getContadorDePozos() + "\n"
                                + "Wumpus: " + mundoDelJuego.getCuentaWumpusMuertos() + " muertos  " + mundoDelJuego.getCuantosWumpus() + " vivos\n"
                                + "Tesoros: " + mundoDelJuego.getcontadorDeOrosEncontrados() + "/ 1" + "\n"
                                + "Flechas Restantes: " + mundoDelJuego.getFlechasRestantes() + " / 1 \n"
                                + "Posición Actual: " + (char) (65 + mundoDelJuego.getCoordenadaX()) + (mundoDelJuego.getPosicionY() + 1) + " \n"
                                + "Mirando a: " + (mundoDelJuego.getMirandoA() == mundoDelJuego.NORTE ? "Norte" :
                                (mundoDelJuego.getMirandoA() == mundoDelJuego.SUR ? "Sur" :
                                        (mundoDelJuego.getMirandoA() == mundoDelJuego.OESTE ? "Oeste" : "Este"))) + "\n"
                                + "Contador de movimientos: " + mundoDelJuego.getContadorDeMovimientos() + "\n"
                                + "Coste: " + mundoDelJuego.getCoste() + "\n");
                canvas.repaint();
            }

            if (notificacion.percepciones == Mundo.PARTIDAPERDIDA || notificacion.percepciones == Mundo.PARTIDAGANADA) {
                if (reinicioItem.getState()) {
                    pasoMultiple.pause();
                    Vector proximosPasos = new Vector(tiempoAntesDeReinicio + 1);
                    for (int i = 0; i < tiempoAntesDeReinicio; i++) proximosPasos.add(null);
                    proximosPasos.add(new Integer(mundoDelJuego.JUEGONUEVO));
                    pasoMultiple.setListadoDePasos(proximosPasos);
                    pasoMultiple.setVelocidad(60);
                    pasoMultiple.start();

                }
            }
        }
        for (int i = 0; i < agentes.length; ++i) {
            agentes[i].buzonDeEntrada(notificacion);
        }
    }

    private class MyJMenu extends JMenu {

        MyJMenu(String text, char mnemonic) {
            super();
            setText(text);
            setMnemonic(mnemonic);
        }
    }

    private class MyJMenuItem extends JMenuItem {
        /*
         * Constructors
         */
        MyJMenuItem(String text, String shortCut, String actionCommand,
                    ActionListener actionListener) {
            super();
            setText(text);
            setMnemonic(getMnemonic());
            if (shortCut != null) setAccelerator(KeyStroke.getKeyStroke(shortCut));

            if (actionListener != null) {
                setActionCommand(actionCommand);
                addActionListener(actionListener);
            }
        }
    }

    private class MyJRadioButtonMenuItem extends JRadioButtonMenuItem {

        MyJRadioButtonMenuItem(String text, String shortCut, ButtonGroup buttonGroup,
                               boolean selected,
                               String actionCommand,
                               ActionListener actionListener) {
            super();
            setText(text);
            setMnemonic(getMnemonic());
            if (shortCut != null) setAccelerator(KeyStroke.getKeyStroke(shortCut));
            buttonGroup.add(this);
            setSelected(selected);
            if (actionListener != null) {
                setActionCommand(actionCommand);
                addActionListener(actionListener);
            }
        }
    }

    class DisplayPanel extends JPanel {

        private int rand = 30;
        private Mundo mapa;

        public DisplayPanel(Mundo mundoAct) {
            super();
            this.mapa = mundoAct;

        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            int ancho = getSize().width;
            int alto = getSize().height;
            int tamanoCasilla = ((ancho < alto ? ancho : alto) - rand) / (mapa.getTamañoDelMapa() < 4 ? 4 : mapa.getTamañoDelMapa());
            int tamanoCuadro = tamanoCasilla * mapa.getTamañoDelMapa();
            int xMaxima = (ancho - tamanoCuadro) / 2;
            int yMaxima = (alto - tamanoCuadro) / 2;

            java.net.URL img = getClass().getResource("Image/Logo_UFV.png");

            ImageIcon logo = new ImageIcon(img);
            logo.paintIcon(this, g, 0, 0);

            g.drawRect(xMaxima, yMaxima, tamanoCuadro, tamanoCuadro);

            g.setColor(Color.black);

            g.setColor(Color.white);
            for (int i = 0; i < mapa.getTamañoDelMapa(); ++i) {
                g.drawLine(xMaxima + i * tamanoCasilla, yMaxima, xMaxima + i * tamanoCasilla, yMaxima + tamanoCuadro);
                g.drawLine(xMaxima, yMaxima + i * tamanoCasilla, xMaxima + tamanoCuadro, yMaxima + i * tamanoCasilla);
            }

            g.setColor(Color.black);
            for (int i = 0; i < mapa.getTamañoDelMapa(); ++i) {
                int tmp = ((mapa.getTamañoDelMapa() - i) < 10 ? 10 : 16);
                g.drawString("" + (mapa.getTamañoDelMapa() - i), xMaxima - tmp, yMaxima + tamanoCasilla * i + tamanoCasilla / 2 + 2);
                g.drawString("" + (mapa.getTamañoDelMapa() - i), xMaxima + tamanoCuadro + 2, yMaxima + tamanoCasilla * i + tamanoCasilla / 2 + 2);
            }
            for (int i = 0; i < mapa.getTamañoDelMapa(); ++i) {
                g.drawString("" + (char) (65 + i), xMaxima + tamanoCasilla * i + tamanoCasilla / 2 + 2, yMaxima - 2);
                g.drawString("" + (char) (65 + i), xMaxima + tamanoCasilla * i + tamanoCasilla / 2 + 2, yMaxima + tamanoCuadro + 12);
                g.drawString("" + (char) (65 + i), xMaxima + tamanoCasilla * i + tamanoCasilla / 2 + 2, yMaxima - 2);
                g.drawString("" + (char) (65 + i), xMaxima + tamanoCasilla * i + tamanoCasilla / 2 + 2, yMaxima + tamanoCuadro + 12);
            }

            for (int i = 0; i < mapa.getTamañoDelMapa(); ++i) {
                for (int j = 0; j < mapa.getTamañoDelMapa(); ++j) {
                    Casilla casillaActual = mapa.getCasilla(i, j);
                    if (casillaActual.valida && casillaActual.oculta && mapa.chequeaJuegoActivado()) continue;
                    int x = xMaxima + i * tamanoCasilla;
                    int y = yMaxima + tamanoCuadro - (j + 1) * tamanoCasilla;

                    g.setColor(Color.lightGray);
                    if (casillaActual.oculta) {
                        g.setColor(Color.darkGray);
                    }
                    if (!casillaActual.valida) {
                        g.setColor(Color.red.darker().darker());
                    }
                    g.fillRect(x + 2, y + 2, tamanoCasilla - 2, tamanoCasilla - 2);

                    if (casillaActual.contador == mapa.WUMPUSMUERTO) {


                        String rutaMuerto = "/" + this.mapa.getTamañoDelMapa() + "X/rip.png";
                        java.net.URL muerto = getClass().getResource(rutaMuerto);

                        ImageIcon imgMuerto = new ImageIcon(muerto);
                        imgMuerto.paintIcon(this, g, x, y);

                    }
                    if (casillaActual.oro) {

                        String oroRuta = "/" + this.mapa.getTamañoDelMapa() + "X/arqueroOeste.png";
                        java.net.URL oro = getClass().getResource(oroRuta);
                        ImageIcon oroImg = new ImageIcon(oro);
                        oroImg.paintIcon(this, g, x, y);
                    }
                    if (mapa.chequeaJuegoActivado() && mapa.getCoordenadaX() == i && mapa.getPosicionY() == j) {
                        if (mapa.getMirandoA() == mapa.NORTE) {
                            String rutaArqNorte = "/" + this.mapa.getTamañoDelMapa() + "X/arqueroNorte.png";
                            java.net.URL arqueroNorte = getClass().getResource(rutaArqNorte);
                            ImageIcon arqNorte = new ImageIcon(arqueroNorte);
                            arqNorte.paintIcon(this, g, x, y);

                        } else if (mapa.getMirandoA() == mapa.SUR) {
                            String rutaArqSur = "/" + this.mapa.getTamañoDelMapa() + "X/arqueroSur.png";
                            java.net.URL arqueroSur = getClass().getResource(rutaArqSur);
                            ImageIcon arqSur = new ImageIcon(arqueroSur);
                            arqSur.paintIcon(this, g, x, y);
                        } else if (mapa.getMirandoA() == mapa.OESTE) {
                            String rutaArqOeste = "/" + this.mapa.getTamañoDelMapa() + "X/arqueroOeste.png";
                            java.net.URL arqueroOeste = getClass().getResource(rutaArqOeste);
                            ImageIcon arqOeste = new ImageIcon(arqueroOeste);
                            arqOeste.paintIcon(this, g, x, y);
                        } else {
                            String rutaArqEste = "/" + this.mapa.getTamañoDelMapa() + "X/arqueroEste.png";
                            java.net.URL arqueroEste = getClass().getResource(rutaArqEste);
                            ImageIcon arqEste = new ImageIcon(arqueroEste);
                            arqEste.paintIcon(this, g, x, y);
                        }
                    }
                    if (casillaActual.salida) {
                        g.drawString("Salida", x + 9, y + tamanoCasilla / 2 + 2);
                    }
                    if (casillaActual.brisa) {
                        String rutaBrisa = "/" + this.mapa.getTamañoDelMapa() + "X/brisa.png";
                        java.net.URL brisa = getClass().getResource(rutaBrisa);
                        ImageIcon imgBrisa = new ImageIcon(brisa);
                        imgBrisa.paintIcon(this, g, x, y);
                    }
                    if (casillaActual.hedor) {
                        String rutaHedor = "/" + this.mapa.getTamañoDelMapa() + "X/hedor.png";
                        java.net.URL hedor = getClass().getResource(rutaHedor);
                        ImageIcon imgHedor = new ImageIcon(hedor);
                        imgHedor.paintIcon(this, g, x, y);
                    }
                    if (casillaActual.wumpus) {
                        String rutaWumpus = "/" + this.mapa.getTamañoDelMapa() + "X/wump.png";
                        java.net.URL wumpus = getClass().getResource(rutaWumpus);
                        ImageIcon imgWumpus = new ImageIcon(wumpus);
                        imgWumpus.paintIcon(this, g, x, y);
                    }
                    if (casillaActual.oro) {
                        String rutaOro = "/" + this.mapa.getTamañoDelMapa() + "X/oro.png";
                        java.net.URL oro = getClass().getResource(rutaOro);
                        ImageIcon imgOro = new ImageIcon(oro);
                        imgOro.paintIcon(this, g, x, y);
                    }

                    if (casillaActual.pozo) {
                        String rutaPozo = "/" + this.mapa.getTamañoDelMapa() + "X/pozo.png";
                        java.net.URL pozo = getClass().getResource(rutaPozo);
                        ImageIcon imgPozo = new ImageIcon(pozo);
                        imgPozo.paintIcon(this, g, x, y);
                    }
                }
            }
        }
    }

}



