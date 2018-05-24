package HAEW;
import java.util.Observable;
import java.util.Vector;

public class MultiplesPasos extends Observable implements Runnable {

    public static int limite = 6000;

    private Vector listadoDePasos;
    private int contadorDePasos;
    private int pasosPorMinuto;
    private Thread hiloPrincipal;
    private Mundo mundo;
    private boolean sePresionoTecla;


    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: Mundo mundo,Vector listadoDePasos,int movimientosPorMinuto
     * @return: N/A
     * Descripción: Es el constructor de la clase, inciializa las variables necearias para su instanciacion.
     */
    public MultiplesPasos(Mundo mundo,Vector listadoDePasos,int movimientosPorMinuto) {
        super();
        this.listadoDePasos = listadoDePasos;
        this.pasosPorMinuto = movimientosPorMinuto;
        contadorDePasos = 0;
        this.mundo = mundo;
        sePresionoTecla = false;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: Vector listadoDePasos
     * @return: void
     * Descripción: inicializa el listado de pasos de un movimiento
     */
    public void setListadoDePasos(Vector listadoDePasos) {
        this.listadoDePasos = listadoDePasos;
        contadorDePasos = 0;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int movimientosPorMinuto
     * @return: void
     * Descripción: inicializa la velocidad a la que queremos que se mueva el bot por la GUI
     */
    public void setVelocidad(int movimientosPorMinuto) {
        this.pasosPorMinuto = movimientosPorMinuto;
        if (movimientosPorMinuto > 0) sePresionoTecla = true;
        else sePresionoTecla = false;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: int
     * Descripción: Devuelve la velocidad actual del bot
     */
    public int getVelocidad() {
        //System.out.println("MultiplesPasos:getVelocidad()");
        return pasosPorMinuto;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: void
     * Descripción: inicia el hilo de ejecucion de la clase
     */
    public void run() {
        Thread hiloActual = Thread.currentThread();
        while (hiloPrincipal == hiloActual) {
            if (listadoDePasos == null || contadorDePasos >= listadoDePasos.size() || mundo == null) {
                hiloPrincipal = null;
                setChanged();
                notifyObservers(new Chivato(Mundo.SINOPERACION, -1, -1, "movimientoRealizado"));
                break;
            }
            try {
                if (listadoDePasos.elementAt(contadorDePasos) != null) {
                    int movimiento = ((Integer) listadoDePasos.elementAt(contadorDePasos)).intValue();
                    mundo.realizaMovimiento(movimiento);
                }
                contadorDePasos++;
            } catch (Exception ex) {
            }
            if (pasosPorMinuto <= 0) {
                while (!sePresionoTecla) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                sePresionoTecla = false;
            } else if (pasosPorMinuto <= limite) {
                try {
                    Thread.sleep(60000L / pasosPorMinuto);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: void
     * Descripción: Arranca el thread de la clase.
     */
    public void start() {
        sePresionoTecla = false;
        hiloPrincipal = new Thread(this);
        hiloPrincipal.start();
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: void
     * Descripción: Para la ejecucion del hilo actual.
     */
    public void pause() {
        hiloPrincipal = null;
    }
}

