package HAEW;

import java.util.Vector;

public abstract class AgenteGlobal extends Object {


    protected Mundo mundo;
    protected MultiplesPasos multiPaso;
    protected boolean activado;
    protected int velocidad;

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: Mundo mundo, MultiplesPasos pasosmultiples
     * @return: N/A
     * Descripción: Constructor de la clase, incializa todos los componentes de la clase
     */
    public AgenteGlobal(Mundo mundo, MultiplesPasos pasosmultiples) {
        super();
        this.mundo = mundo;
        this.multiPaso = pasosmultiples;
        activado = false;
        velocidad = pasosmultiples.getVelocidad();
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: Bool
     * Descripción: Devuelve si el juego se encuentra activado o no.
     */
    public boolean isActivado() {
        return activado;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: int
     * Descripción: Devuelve la velocidad de juego
     */
    public int getVelocidad() {
        return velocidad;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: string
     * Descripción: Devuelve el nombre dado del agente inteligente
     */
    public abstract String getNombre();

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: Chivato mensaje
     * @return: void
     * Descripción: ejecuta los observadores en funcion del mensaje de entrada recibido.
     */
    public void buzonDeEntrada(Chivato mensaje) {

        if (mensaje.percepciones == Mundo.JUEGONUEVO) {
            if (activado && mundo.chequeaJuegoActivado()) {
                multiPaso.pause();
                realizaMovimientos();
            }
        }

        if (mensaje.informacion.equals("movimientoRealizado")) {
            if (activado && mundo.chequeaJuegoActivado()) {
                multiPaso.pause();
                realizaMovimientos();
            }
        } else if (mensaje.informacion.equals("activado")) {
            if (!activado) {
                activado = true;
                multiPaso.pause();
                if (mundo.chequeaJuegoActivado()) realizaMovimientos();
            }
        } else if (mensaje.informacion.equals("desactivado")) {
            if (activado) {
                multiPaso.pause();
                activado = false;
            }
        } else if (mensaje.informacion.equals("actualizaVelocidad")) {
            velocidad = mensaje.posicionX;
        }
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: Vector ruta
     * @return: void
     * Descripción: Inicia los movimientos recogidos por el buzon de entrada
     */
    protected void iniciaMovimientos(Vector ruta) {
        multiPaso.pause();
        multiPaso.setListadoDePasos(ruta);
        multiPaso.setVelocidad(velocidad);
        multiPaso.start();
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: void
     * Descripción: Lleva a cabo los movimientos seleccionados, este es un metodo abstracto, que se ejecuta en el
     * AgenteNormal
     */
    protected abstract void realizaMovimientos();

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int inicioX, int inicioY, int direccionInicial, int destinoX, int destinoY, int direccionFinal
     * @return: Vector
     * Descripción: Recibe las coordenadas de destino y busca la ruta, para devolver el vector de casillas que
     * componen la ruta.
     */
    public Vector encuentraRuta(int inicioX, int inicioY, int direccionInicial, int destinoX, int destinoY, int direccionFinal) {

        int inicial = 0, tamañoDelMapa = mundo.getTamañoDelMapa();

        boolean rutaEncontrada = false;
        int[][] tablero = new int[tamañoDelMapa][tamañoDelMapa]; // me creo el array del mapa
        Vector proximosPasos = new Vector(); // Vector que contiene los proximos pasos
        Casilla desde = mundo.getCasilla(inicioX, inicioY); // Casilla desde la que venimos
        Casilla hasta = mundo.getCasilla(destinoX, destinoY); // Casilla a la que vamos a ir

        if (!desde.valida || !hasta.valida || (inicioX == destinoX && inicioY == destinoY && (direccionInicial == direccionFinal || direccionFinal == mundo.SINOPERACION)) // CasillaInicial == CasillaFinal ^ (direccionInicial == DireccionFinal V direccionFinal == No operation))
                || (direccionInicial != mundo.NORTE && direccionInicial != mundo.SUR && direccionInicial != mundo.OESTE && direccionInicial != mundo.ESTE) // !Wumpus en casilla Actual
                || (direccionFinal != mundo.NORTE && direccionFinal != mundo.SUR && direccionFinal != mundo.OESTE && direccionFinal != mundo.ESTE && direccionFinal != mundo.SINOPERACION)
                || hasta.visitada == true) {
            return new Vector();
        }

        tablero = inicializaTablero(tamañoDelMapa, tablero);
        tablero[destinoX][destinoY] = 0; // pongo la casilla de destino como 0
        proximosPasos.add(mundo.getCasilla(destinoX, destinoY));

        while (inicial != proximosPasos.size()) { // Mientras que la lista de movimientos no este vacia.
            Casilla casillaActual = (Casilla) (proximosPasos.elementAt(inicial++)); // añado esa posicion
            int distancia = tablero[casillaActual.x][casillaActual.y] + 1; // distancia +1

            if (casillaActual.x == inicioX && casillaActual.y == inicioY) { // si la casillaActual +1  es igual a la casillaActual de destino
                rutaEncontrada = true; // hemos encontrado la ruta
                break;
            }
            /*Si no es la casillaActual que estamos buscando, inciializamos las casilas adyacentes  para ver si estan libres o no.*/
            proximosPasos = validaCasillaAdyacente(casillaActual, tablero, distancia, proximosPasos);
        }

        /*
        * Esta es la zona que toca jugarnosla porque no sabemos que hacer.
        */

        if (!rutaEncontrada) {
            return new Vector(); // si no se encuentra ruta
        }
        // Convertir datos en pasos y giros
        return ConvertirEnPasos(inicioX, inicioY, direccionInicial, direccionFinal, añadePasos(proximosPasos, inicioX, inicioY, tablero, destinoX, destinoY));
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: Vector proximosPasos, int desdeX, int desdeY, int[][] tablero, int hastaX, int hastaY
     * @return: Vector
     * Descripción: a partir de la ruta, añade los pasos necesarios para llevarlos a cabo.
     */
    private Vector añadePasos(Vector proximosPasos, int desdeX, int desdeY, int[][] tablero, int hastaX, int hastaY) {
        proximosPasos.clear(); // reinicializamos el vector de proximos pasos

        proximosPasos.add(mundo.getCasilla(desdeX, desdeY)); // añadimos la primera posicion

        while (true) {
            Casilla casillaActual = (Casilla) (proximosPasos.elementAt(proximosPasos.size() - 1));
            int distancia = tablero[casillaActual.x][casillaActual.y] - 1;
            if (casillaActual.x == hastaX && casillaActual.y == hastaY) {
                break;
            }

            Casilla izquierda = mundo.getCasilla(casillaActual.x - 1, casillaActual.y);
            Casilla derecha = mundo.getCasilla(casillaActual.x + 1, casillaActual.y);
            Casilla abajo = mundo.getCasilla(casillaActual.x, casillaActual.y - 1);
            Casilla arriba = mundo.getCasilla(casillaActual.x, casillaActual.y + 1);

            if (derecha.valida && tablero[derecha.x][derecha.y] == distancia && !derecha.visitada) { //derechaValida ^ CasillaTablero =  distancia
                proximosPasos.add(derecha);
                derecha.visitada = true;
                continue;
            }
            if (arriba.valida && tablero[arriba.x][arriba.y] == distancia && !arriba.visitada) {
                proximosPasos.add(arriba);
                arriba.visitada = true;
                continue;
            }
            if (izquierda.valida && tablero[izquierda.x][izquierda.y] == distancia && !izquierda.visitada) {
                proximosPasos.add(izquierda);
                izquierda.visitada = true;
                continue;
            }
            if (abajo.valida && tablero[abajo.x][abajo.y] == distancia && !abajo.visitada) {
                proximosPasos.add(abajo);
                abajo.visitada = true;
                continue;
            }
        }
        return proximosPasos;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: Casilla casilla, int[][] tablero, int distancia, Vector proximosPasos
     * @return: Vector
     * Descripción: Comprueba si las casillas adyacentes a la actual son validas o no.
     */
    private Vector validaCasillaAdyacente(Casilla casilla, int[][] tablero, int distancia, Vector proximosPasos) {

        Casilla izquierda = mundo.getCasilla(casilla.x - 1, casilla.y);
        Casilla derecha = mundo.getCasilla(casilla.x + 1, casilla.y);
        Casilla abajo = mundo.getCasilla(casilla.x, casilla.y - 1);
        Casilla arriba = mundo.getCasilla(casilla.x, casilla.y + 1);

        if (derecha.valida && !derecha.oculta && !derecha.visitada && tablero[derecha.x][derecha.y] == -1) { //DerechaValida ^ DerechaVisible ^ !Visitada(es valor -1)
            tablero[derecha.x][derecha.y] = distancia; // cambiamos el valor de la casilla y la añadimos al vector de pasos
            proximosPasos.add(derecha);
            derecha.visitada = true;
        }
        if (arriba.valida && !arriba.oculta && !arriba.visitada && tablero[arriba.x][arriba.y] == -1) {
            tablero[arriba.x][arriba.y] = distancia;
            proximosPasos.add(arriba);
            arriba.visitada = true;
        }
        if (izquierda.valida && !izquierda.oculta && !izquierda.visitada && tablero[izquierda.x][izquierda.y] == -1) {
            tablero[izquierda.x][izquierda.y] = distancia;
            proximosPasos.add(izquierda);
            izquierda.visitada = true;
        }
        if (abajo.valida && !abajo.oculta && !abajo.visitada && tablero[abajo.x][abajo.y] == -1) {
            tablero[abajo.x][abajo.y] = distancia;
            proximosPasos.add(abajo);
            abajo.visitada = true;
        }
        return proximosPasos;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int desdeX, int desdeY, int mirandoDesde, int mirandoHacia, Vector listadoDePasos
     * @return: Vector
     * Descripción: a partir de la ruta convierte los movimientos en pasos con sus giros correspondientes.
     */
    private Vector ConvertirEnPasos(int desdeX, int desdeY, int mirandoDesde, int mirandoHacia, Vector listadoDePasos) {
        Vector proximosPasos = new Vector();
        int anteriorX = desdeX;
        int anteriorY = desdeY;
        int ultimaDireccion = mirandoDesde;

        for (int i = 1; i < listadoDePasos.size(); ++i) {
            Casilla casillaActual = (Casilla) (listadoDePasos.elementAt(i));

            if (anteriorX == casillaActual.x) {
                if (anteriorY == casillaActual.y - 1) { // avanzo NORTE
                    proximosPasos = calculaMovimientosNorte(ultimaDireccion, proximosPasos);
                    ultimaDireccion = mundo.NORTE;
                } else {    // step SUR
                    proximosPasos = calculaMovimientosSur(ultimaDireccion, proximosPasos);
                    ultimaDireccion = mundo.SUR;
                }
            } else { // (anteriorY==p.y)

                if (anteriorX == casillaActual.x - 1) { // step ESTE
                    proximosPasos = calculaMovimientosEste(ultimaDireccion, proximosPasos);
                    ultimaDireccion = mundo.ESTE;
                } else {    // step OESTE
                    calculaMovimientosOeste(ultimaDireccion, proximosPasos);
                    ultimaDireccion = mundo.OESTE;
                }
            }

            anteriorX = casillaActual.x;
            anteriorY = casillaActual.y;
            casillaActual.visitada = true;
            casillaActual.oculta = false;
        }
        return conviertePasosEnGiros(ultimaDireccion, mirandoHacia, proximosPasos);
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int tamañoMapa, int[][] tablero
     * @return: array de int
     * Descripción: Inicializa los valores del tablero de juego.
     */
    private int[][] inicializaTablero(int tamañoMapa, int[][] tablero) {
        for (int i = 0; i < tamañoMapa; i++) { // me inicializo el mapa con todos lo valores a -1
            for (int j = 0; j < tamañoMapa; j++) {
                tablero[i][j] = -1;
            }
        }
        return tablero;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int ultimaDireccion, Vector proximosPasos
     * @return: Vector
     * Descripción: Realiza el calculo de los movimientos necesarios para llegar a un objetio situado al norte
     */
    private Vector calculaMovimientosNorte(int ultimaDireccion, Vector proximosPasos) {

        if (ultimaDireccion == mundo.NORTE) {
            proximosPasos.add(new Integer(mundo.AVANZAR));
        } else if (ultimaDireccion == mundo.SUR) {
            proximosPasos.add(new Integer(mundo.GIRARIZQUIERDA));
            proximosPasos.add(new Integer(mundo.GIRARIZQUIERDA));
            // ultimaDireccion = mundo.NORTE;
            proximosPasos.add(new Integer(mundo.AVANZAR));
            //proximosPasos.add(new Integer(mundo.RETROCEDER));
        } else if (ultimaDireccion == mundo.ESTE) {
            proximosPasos.add(new Integer(mundo.GIRARIZQUIERDA));
            // ultimaDireccion = mundo.NORTE;
            proximosPasos.add(new Integer(mundo.AVANZAR));
        } else if (ultimaDireccion == mundo.OESTE) {
            proximosPasos.add(new Integer(mundo.GIRARDERECHA));
            // ultimaDireccion = mundo.NORTE;
            proximosPasos.add(new Integer(mundo.AVANZAR));
        }
        return proximosPasos;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int ultimaDireccion, Vector proximosPasos
     * @return: Vector
     * Descripción: Realiza el calculo de los movimientos necesarios para llegar a un objetio situado al norte
     */
    private Vector calculaMovimientosSur(int ultimaDireccion, Vector proximosPasos) {
        if (ultimaDireccion == mundo.NORTE) {
            proximosPasos.add(new Integer(mundo.GIRARIZQUIERDA));
            proximosPasos.add(new Integer(mundo.GIRARIZQUIERDA));
            // ultimaDireccion = mundo.NORTE;
            proximosPasos.add(new Integer(mundo.AVANZAR));
            // proximosPasos.add(new Integer(mundo.RETROCEDER));
        } else if (ultimaDireccion == mundo.SUR) {
            proximosPasos.add(new Integer(mundo.AVANZAR));
        } else if (ultimaDireccion == mundo.ESTE) {
            proximosPasos.add(new Integer(mundo.GIRARDERECHA));
            ultimaDireccion = mundo.SUR;
            proximosPasos.add(new Integer(mundo.AVANZAR));
        } else if (ultimaDireccion == mundo.OESTE) {
            proximosPasos.add(new Integer(mundo.GIRARIZQUIERDA));
            ultimaDireccion = mundo.SUR;
            proximosPasos.add(new Integer(mundo.AVANZAR));
        }
        return proximosPasos;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int ultimaDireccion, Vector proximosPasos
     * @return: Vector
     * Descripción: Realiza el calculo de los movimientos necesarios para llegar a un objetio situado al norte
     */
    private Vector calculaMovimientosEste(int ultimaDireccion, Vector proximosPasos) {

        if (ultimaDireccion == mundo.OESTE) {
            proximosPasos.add(new Integer(mundo.GIRARIZQUIERDA));
            proximosPasos.add(new Integer(mundo.GIRARIZQUIERDA));
            ultimaDireccion = mundo.NORTE;
            proximosPasos.add(new Integer(mundo.AVANZAR));
            //proximosPasos.add(new Integer(mundo.RETROCEDER));
        } else if (ultimaDireccion == mundo.ESTE) {
            proximosPasos.add(new Integer(mundo.AVANZAR));
        } else if (ultimaDireccion == mundo.SUR) {
            proximosPasos.add(new Integer(mundo.GIRARIZQUIERDA));
            ultimaDireccion = mundo.ESTE;
            proximosPasos.add(new Integer(mundo.AVANZAR));
        } else if (ultimaDireccion == mundo.NORTE) {
            proximosPasos.add(new Integer(mundo.GIRARDERECHA));
            ultimaDireccion = mundo.ESTE;
            proximosPasos.add(new Integer(mundo.AVANZAR));
        }
        return proximosPasos;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int ultimaDireccion, Vector proximosPasos
     * @return: Vector
     * Descripción: Devuelve el nombre dado del agente inteligente
     */
    private Vector calculaMovimientosOeste(int ultimaDireccion, Vector proximosPasos) {
        if (ultimaDireccion == mundo.OESTE) {
            proximosPasos.add(new Integer(mundo.AVANZAR));
        } else if (ultimaDireccion == mundo.ESTE) {
            proximosPasos.add(new Integer(mundo.GIRARIZQUIERDA));
            proximosPasos.add(new Integer(mundo.GIRARIZQUIERDA));
            ultimaDireccion = mundo.OESTE;
            proximosPasos.add(new Integer(mundo.AVANZAR));
            // proximosPasos.add(new Integer(mundo.RETROCEDER));
        } else if (ultimaDireccion == mundo.NORTE) {
            proximosPasos.add(new Integer(mundo.GIRARIZQUIERDA));
            ultimaDireccion = mundo.OESTE;
            proximosPasos.add(new Integer(mundo.AVANZAR));
        } else if (ultimaDireccion == mundo.SUR) {
            proximosPasos.add(new Integer(mundo.GIRARDERECHA));
            ultimaDireccion = mundo.OESTE;
            proximosPasos.add(new Integer(mundo.AVANZAR));
        }
        return proximosPasos;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int ultimaDireccion, Vector proximosPasos
     * @return: Vector
     * Descripción: Realiza el calculo de los movimientos necesarios para llegar a un objetio situado al norte
     */
    private Vector conviertePasosEnGiros(int ultimaDireccion, int mirandoHacia, Vector proximosPasos) {
        if ((ultimaDireccion == mundo.NORTE && mirandoHacia == mundo.SUR)  // Necesito dos giros para poder alcanzar mi siguiente movimiento
                || (ultimaDireccion == mundo.SUR && mirandoHacia == mundo.NORTE)
                || (ultimaDireccion == mundo.OESTE && mirandoHacia == mundo.ESTE)
                || (ultimaDireccion == mundo.ESTE && mirandoHacia == mundo.OESTE)) {
            proximosPasos.add(new Integer(mundo.GIRARDERECHA));
            proximosPasos.add(new Integer(mundo.GIRARDERECHA));
        } else if ((ultimaDireccion == mundo.NORTE && mirandoHacia == mundo.ESTE) // solo necesito un giro para llegar a mi objetivo y lo tengo a la derecha
                || (ultimaDireccion == mundo.SUR && mirandoHacia == mundo.OESTE)
                || (ultimaDireccion == mundo.OESTE && mirandoHacia == mundo.NORTE)
                || (ultimaDireccion == mundo.ESTE && mirandoHacia == mundo.SUR)) {
            proximosPasos.add(new Integer(mundo.GIRARDERECHA));
        } else if ((ultimaDireccion == mundo.NORTE && mirandoHacia == mundo.OESTE) // solo necesito un giro para llegar a mi objetivo y lo tengo a mi izquerda
                || (ultimaDireccion == mundo.SUR && mirandoHacia == mundo.ESTE)
                || (ultimaDireccion == mundo.OESTE && mirandoHacia == mundo.SUR)
                || (ultimaDireccion == mundo.ESTE && mirandoHacia == mundo.NORTE)) {
            proximosPasos.add(new Integer(mundo.GIRARIZQUIERDA));
        }
        return proximosPasos;
    }
}


