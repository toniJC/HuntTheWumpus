package HAEW;

import java.lang.*;

public class Casilla extends Object {

    public boolean valida, oculta, visitada;
    public boolean salida, wumpus, pozo, oro, pared;
    public boolean brisa, hedor;
    public int contador,x,y;

    public Casilla() {
        super();
        oculta = true;
        valida = false;
        pared = false;
        visitada = false;
        salida = false;
        wumpus = false;
        pozo = false;
        oro = false;
        brisa = hedor = false;
        contador = Mundo.VACIO;
        y = -1;
        x = -1;
    }

    public Casilla(int x, int y) {
        this();
        this.y = y;
        this.x = x;
    }

}
