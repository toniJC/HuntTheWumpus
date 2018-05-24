package HAEW;
import java.lang.*;

public class Chivato extends Object {

    public int posicionX, posicionY, percepciones;
    public String informacion;
    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int percepciones, int x, int y, String informacion
     * @return: N/A
     * Descripción: Inicializa los valores necesarios para el uso de los mensajes
     */
    public Chivato(int percepciones, int x, int y, String informacion) {
        this.percepciones = percepciones;
        posicionX = x;
        posicionY = y;
        this.informacion = informacion;
    }
}
