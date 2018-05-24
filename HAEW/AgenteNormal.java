package HAEW;

import java.util.Vector;

public class AgenteNormal extends AgenteGlobal {

    private static boolean noRuido = true;

    private Vector sinWumpus;
    private Casilla salida;

    /*
     * Constructor
     */
    public AgenteNormal(Mundo entorno, MultiplesPasos multipaso) {
        super(entorno, multipaso);
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: string
     * Descripción: Devuelve el nombre dado del agente inteligente
     */
    public String getNombre() {
        return "Agente inteligente";
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: Chivato mensaje
     * @return: N/A
     * Descripción: Recupera el mensaje dado y en funcion de la entrada, activa los observadores necesarios.
     */
    public void buzonDeEntrada(Chivato mensaje) {
        if (mensaje.percepciones == Mundo.JUEGONUEVO) {
            if (activado) multiPaso.pause();
            sinWumpus = new Vector();
            salida = mundo.getCasilla(mensaje.posicionX, mensaje.posicionY);
            if (activado && mundo.chequeaJuegoActivado()) realizaMovimientos();

        } else if (mensaje.percepciones == Mundo.ESTOYPERDIDO) {
            if (!noRuido)
                sinWumpus.add(mundo.getCasilla(mensaje.posicionX, mensaje.posicionY));
        }

        if (mensaje.informacion.equals("movimientoRealizado")) {
            if (activado && mundo.chequeaJuegoActivado()) {
                multiPaso.pause();
                realizaMovimientos();
            }
        } else if (mensaje.informacion.equals("activado")) {
            if (!activado) {
                activado = true;
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
     * @param: N/A
     * @return: N/A
     * Descripción: Este es el metodo importante, lo que hace es explorar las distintas estrategias para poder
     * determinar los movimientos que se realizaran posteriormente.
     */
    protected void realizaMovimientos() {
        /* 1. Comprobamos si el juego esta activo, si no lo esta paramos la ejecucion*/
        if (!activado) {
            return;
        }
        /* 2. Paramos el multipaso */
        multiPaso.pause();

        /* 3. Inicializamos el vector de movimientos siguientes y la casillas actual*/
        Vector movimientosSiguientes = null;
        Casilla casillaActual = mundo.getCasilla(mundo.getCoordenadaX(), mundo.getPosicionY());

        /* 4. Lo primero que hacemos es buscar el oro.*/
        if (movimientosSiguientes == null) {
            movimientosSiguientes = buscarElOro(casillaActual, movimientosSiguientes);
        }

        /* 5. Como no hemos encontrado del oro, buscamos todas las casillas seguras al 100% en el mapa actual */
        if (movimientosSiguientes == null) {
            Vector casillasLibresDePeligro = buscadorDeCasillasSeguras();
            if (casillasLibresDePeligro.size() > 0) {
                movimientosSiguientes = buscarDeRutaSegura(casillasLibresDePeligro, casillaActual, movimientosSiguientes);
            }
        }

        /* 7. No hemos cazado al wumpus, asi que vamos a buscar una ruta mas arriesgada */
        if (movimientosSiguientes == null) {
            movimientosSiguientes = buscarRutaArriesgada(movimientosSiguientes, casillaActual);
        }

        /* 8. Arriesgandonos un poco mas, no podemos determinar la ruta segura, por lo que vamos a escoger el siguiente paso por
         * probabiliadad */
        if (movimientosSiguientes == null) {
            movimientosSiguientes = buscarRutaPorProbabilidad(movimientosSiguientes, casillaActual);
        }

        /* 6. Como no hemos encontrado casillas seguras, vamos a intentar darle caza al wumpus*/
        if (movimientosSiguientes == null) {
            Vector listadoDeWupmpus = new Vector();
            listadoDeWupmpus = buscadorDeCasillasConWumpus(listadoDeWupmpus);
            if (listadoDeWupmpus.size() > 0) {
                movimientosSiguientes = rutaDeLosWumpus(listadoDeWupmpus, casillaActual, movimientosSiguientes);
                movimientosSiguientes.add(new Integer(mundo.DISPARA));
            }
        }
        /* 9. Ya tenemos la ruta, por lo que vamos a llevarla a cabo y si no, pues desactivamos el juego  */
        if (movimientosSiguientes != null) {
            iniciaMovimientos(movimientosSiguientes);
        } else {
            buzonDeEntrada(new Chivato(mundo.SINOPERACION, -1, -1, "desactivado"));
        }
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int x, int y
     * @return: el numero de vecinos seguros que tiene una casilla.
     * Descripción: dada la posicion x e y de la casilla, analiza sus adyacentes y determina cuantos de ellos son seguros
     */
    private int contadorDeVecinosSeguros(int x, int y) {
        /* 1. Inicializamos el numero de vecinos seguros*/
        int vecinosSeguros = 0;
        /* 2. Inicializamos sus casillas adyacentes*/
        Casilla izquierda = mundo.getCasilla(x - 1, y);
        Casilla derecha = mundo.getCasilla(x + 1, y);
        Casilla arriba = mundo.getCasilla(x, y + 1);
        Casilla abajo = mundo.getCasilla(x, y - 1);
        /* 3. Si la casilla es valida, no esta visitada y no tiene brisa, significa que es segura*/
        if (izquierda.valida && izquierda.oculta && !tieneBrisa(x - 1, y)) {
            vecinosSeguros++;
        }
        if (derecha.valida && derecha.oculta && !tieneBrisa(x + 1, y)) {
            vecinosSeguros++;
        }
        if (arriba.valida && arriba.oculta && !tieneBrisa(x, y + 1)) {
            vecinosSeguros++;
        }
        if (abajo.valida && abajo.oculta && !tieneBrisa(x, y - 1)) {
            vecinosSeguros++;
        }
        return vecinosSeguros;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int x, int y
     * @return: true o false
     * Descripción: En funcion de la entrada de la posicion de una casilla, analiza a sus vecinos,
     * para saber si tiene wumpus.
     */
    private boolean tieneWumpus(int x, int y) {
        /*1. Inicializamos la casilla Actual*/
        Casilla casillaActual = mundo.getCasilla(x, y);

        /* 2. Comprobamos que la casilla es valida */
        if (!casillaActual.valida) {
            return false;
        }

        /* 3. Inicializamos las casillas vecinas de la casilla actual*/
        Casilla izquierda = mundo.getCasilla(x - 1, y);
        Casilla derecha = mundo.getCasilla(x + 1, y);
        Casilla arriba = mundo.getCasilla(x, y + 1);
        Casilla abajo = mundo.getCasilla(x, y - 1);

        /* 4. Para cada vecino, analizamos si los vecinos de la casilla adyacente estudiada tiene hedor pero no wumpus*/
        if (izquierda.hedor && noTieneWumpus(x - 2, y) && noTieneWumpus(x - 1, y + 1) && noTieneWumpus(x - 1, y - 1))
            return true;
        if (derecha.hedor && noTieneWumpus(x + 2, y) && noTieneWumpus(x + 1, y + 1) && noTieneWumpus(x + 1, y - 1))
            return true;
        if (arriba.hedor && noTieneWumpus(x + 1, y + 1) && noTieneWumpus(x, y + 2) && noTieneWumpus(x - 1, y + 1))
            return true;
        if (abajo.hedor && noTieneWumpus(x + 1, y - 1) && noTieneWumpus(x, y - 2) && noTieneWumpus(x - 1, y - 1))
            return true;
        return false;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int x, int y
     * @return: true o false
     * Descripción: En funcion de la entrada de la posicion de una casilla, analiza sus vecinos,
     * para saber si tiene brisa.
     */
    private boolean tieneBrisa(int x, int y) {
        Casilla f = mundo.getCasilla(x, y);
        if (!f.valida) return false;
        Casilla izquierda = mundo.getCasilla(x - 1, y);
        Casilla derecha = mundo.getCasilla(x + 1, y);
        Casilla arriba = mundo.getCasilla(x, y + 1);
        Casilla abajo = mundo.getCasilla(x, y - 1);
        if (izquierda.brisa && noTienePozo(x - 2, y) && noTienePozo(x - 1, y + 1) && noTienePozo(x - 1, y - 1))
            return true;
        if (derecha.brisa && noTienePozo(x + 2, y) && noTienePozo(x + 1, y + 1) && noTienePozo(x + 1, y - 1))
            return true;
        if (arriba.brisa && noTienePozo(x + 1, y + 1) && noTienePozo(x, y + 2) && noTienePozo(x - 1, y + 1))
            return true;
        if (abajo.brisa && noTienePozo(x + 1, y - 1) && noTieneOro(x, y - 2) && noTienePozo(x - 1, y - 1)) return true;
        return false;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int x, int y
     * @return: true o false
     * Descripción: En funcion de la entrada de la posicion de una casilla, analiza sus vecinos,
     * para saber si tiene oro.
     */
    private boolean tieneOro(int x, int y) {
        Casilla casillaActual = mundo.getCasilla(x, y);
        if (!casillaActual.valida) return false;
        if (casillaActual.oro) return true;
        return false;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int x, int y
     * @return: true o false
     * Descripción:En funcion de la entrada de la posicion de una casilla, analiza sus vecinos,
     * para saber si no tiene wumpus.
     */
    private boolean noTieneWumpus(int x, int y) {
        Casilla casillaActual = mundo.getCasilla(x, y);
        if (!casillaActual.valida || !casillaActual.oculta) return true;
        Casilla izquierda = mundo.getCasilla(x - 1, y);
        Casilla derecha = mundo.getCasilla(x + 1, y);
        Casilla abajo = mundo.getCasilla(x, y - 1);
        Casilla arriba = mundo.getCasilla(x, y + 1);
        if ((!izquierda.oculta && !izquierda.hedor) || (!derecha.oculta && !derecha.hedor)
                || (!arriba.oculta && !arriba.hedor) || (!abajo.oculta && !abajo.hedor)) return true;
        for (int i = 0; i < sinWumpus.size(); ++i) {
            Casilla casillaPivote = (Casilla) sinWumpus.elementAt(i);
            if (casillaPivote.x == x && casillaPivote.y == y) return true;
        }
        return false;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int x, int y
     * @return: true o false
     * Descripción: En funcion de la entrada de la posicion de una casilla, analiza sus vecinos,
     * para saber si no tiene pozo.
     */
    private boolean noTienePozo(int x, int y) {
        Casilla casillaActual = mundo.getCasilla(x, y);
        if (!casillaActual.valida || !casillaActual.oculta) return true;
        Casilla izquierda = mundo.getCasilla(x - 1, y);
        Casilla derecha = mundo.getCasilla(x + 1, y);
        Casilla abajo = mundo.getCasilla(x, y - 1);
        Casilla arriba = mundo.getCasilla(x, y + 1);
        if ((!izquierda.oculta && !izquierda.brisa) || (!derecha.oculta && !derecha.brisa)
                || (!arriba.oculta && !arriba.brisa) || (!abajo.oculta && !abajo.brisa)) return true;
        return false;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int x, int y
     * @return: true o false
     * Descripción: En funcion de la entrada de la posicion de una casilla, analiza sus vecinos,
     * para saber si no tiene oro.
     */
    private boolean noTieneOro(int x, int y) {
        Casilla casillaActual = mundo.getCasilla(x, y);
        if (!casillaActual.valida || (!casillaActual.oculta && !casillaActual.oro)) return true;
        /*Casilla izquierda = mundo.getCasilla(x - 1, y);
        Casilla derecha = mundo.getCasilla(x + 1, y);
        Casilla abajo = mundo.getCasilla(x, y - 1);
        Casilla arriba = mundo.getCasilla(x, y + 1);*/
       /* if ((!izquierda.oculta && !izquierda.resplandor) || (!derecha.oculta && !derecha.resplandor)
                || (!arriba.oculta && !arriba.resplandor) || (!abajo.oculta && !abajo.resplandor)) return true;*/
        return false;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: Casilla casilla Actual, Casilla casilla destino
     * @return: la distancia entre casillas
     * Descripción: calcula la distancia existente entre dos casillas
     */
    private int calculaDistanciaEntreCasillas(Casilla casillaActual, Casilla casillaDestino) {
        return (int)Math.sqrt((casillaActual.x - casillaDestino.x) * (casillaActual.x - casillaDestino.x) + (casillaActual.y - casillaDestino.y) * (casillaActual.y - casillaDestino.y));
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: Casilla casillaObjetivo, int direccionActual
     * @return: Casilla escogida
     * Descripción: Introducimos la casilla objetivo y la direccion hacia la que estamos mirando y el metodo escoge
     * la casilla mas cercana de la direccion en la que estamos mirando.
     */
    private Casilla escogeCasillaEnFuncionDeLaDireccion(Casilla casillaObjetivo, int direccionActual) {
        switch (direccionActual) {
            case 0: { //NORTE
                casillaObjetivo.x = casillaObjetivo.x;
                casillaObjetivo.y = casillaObjetivo.y;
            }
            break;
            case 1: { //SUR
                casillaObjetivo.x = casillaObjetivo.x;
                casillaObjetivo.y = casillaObjetivo.y;
            }
            break;
            case 2: { // OESTE
                casillaObjetivo.x = casillaObjetivo.x;
                casillaObjetivo.y = casillaObjetivo.y;
            }
            break;
            case 3: { // ESTE
                casillaObjetivo.x = casillaObjetivo.x;
                casillaObjetivo.y = casillaObjetivo.y;
            }
            break;
        }
        return casillaObjetivo;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int x, int y
     * @return: true o false
     * Descripción: Determina si una casilla es inofensiva o no, para hacerlo mira sus vecinos y los vecinos de los vecinos.
     */
    private boolean casillaInofensiva(int x, int y) {
    /*
     Supongamos que un Wumpus se esconde en (x, y), sabemos que seguro que nunca iremos allí
     * Entonces, ¿por qué deberíamos dispararle a la pobre criatura? (mi agente tiene una conciencia ecológica * lol *)
     * La razón es eliminar su hedor de las habitaciones vecinas y así obtener nuevos
     * información sobre otras cuevas aún no visitadas (los vecinos de los vecinos de (x, y)).
     * Y también para obtener nueva información sobre los vecinos de (x, y) visitando el campo
     * después de que el Wumpus sea asesinado.
     * Sin embargo, si ya sabemos lo suficiente sobre esas habitaciones, ¿por qué deberíamos desperdiciar una flecha?
     * en este wumpus. ("suficiente", es decir, sabemos con certeza qué hay)
     *
     */
        Casilla izquierda = mundo.getCasilla(x - 1, y);
        Casilla derecha = mundo.getCasilla(x + 1, y);
        Casilla abajo = mundo.getCasilla(x, y - 1);
        Casilla arriba = mundo.getCasilla(x, y + 1);

        Casilla izquierdaIzquierda = mundo.getCasilla(x - 2, y);
        Casilla izquierdaArriba = mundo.getCasilla(x - 1, y + 1);
        Casilla izquierdaAbajo = mundo.getCasilla(x - 1, y - 1);
        Casilla derechaDerecha = mundo.getCasilla(x + 2, y);
        Casilla derechaArriba = mundo.getCasilla(x + 1, y + 1);
        Casilla derechaAbajo = mundo.getCasilla(x + 1, y - 1);
        Casilla arribaArriba = mundo.getCasilla(x, y + 2);
        Casilla abajoAbajo = mundo.getCasilla(x, y - 2);

        // verifica el entorno HAEW de este Wumpus, en cuanto a su hedor y
        // las implicaciones de esto alcanzan. Si todos estos campos están descubiertos o
        // contiene un peligro conocido como cetrain, entonces este Wumpus es "inofensivo"

        if (       (!izquierda.valida || !izquierda.oculta || tieneWumpus(x - 1, y) || tieneBrisa(x - 1, y))
                && (!derecha.valida || !derecha.oculta || tieneWumpus(x + 1, y) || tieneBrisa(x + 1, y))
                && (!arriba.valida || !arriba.oculta || tieneWumpus(x, y + 1) || tieneBrisa(x, y + 1))
                && (!abajo.valida || !abajo.oculta || tieneWumpus(x, y - 1) || tieneBrisa(x, y - 1))
                && (!izquierdaIzquierda.valida || !izquierdaIzquierda.oculta || tieneWumpus(x - 2, y) || tieneBrisa(x - 2, y))
                && (!izquierdaArriba.valida || !izquierdaArriba.oculta || tieneWumpus(x - 1, y + 1) || tieneBrisa(x - 1, y + 1))
                && (!izquierdaAbajo.valida || !izquierdaAbajo.oculta || tieneWumpus(x - 1, y - 1) || tieneBrisa(x - 1, y - 1))
                && (!derechaDerecha.valida || !derechaDerecha.oculta || tieneWumpus(x + 2, y) || tieneBrisa(x + 2, y))
                && (!derechaArriba.valida || !derechaArriba.oculta || tieneWumpus(x + 1, y + 1) || tieneBrisa(x + 1, y + 1))
                && (!derechaAbajo.valida || !derechaAbajo.oculta || tieneWumpus(x + 1, y - 1) || tieneBrisa(x + 1, y - 1))
                && (!arribaArriba.valida || !arribaArriba.oculta || tieneWumpus(x, y + 2) || tieneBrisa(x, y + 2))
                && (!abajoAbajo.valida || !abajoAbajo.oculta || tieneWumpus(x, y - 2) || tieneBrisa(x, y - 2))) {
            return true;
        }
        return false;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: Casilla miPosicion, Vector proximosPasos
     * @return: Vector proximosPasos
     * Descripción: introducimos nuestra posicion actual y el vector de proximos pasos, analizamos el entorno e
     * introducimos en el vector las casillas que pueden contener oro.
     */
    private Vector buscarElOro(Casilla miPosicion, Vector proximosPasos) {
        Vector posiblesCasillasRicas = new Vector();
        for (int i = 0; i < mundo.getTamañoDelMapa(); i++) {
            for (int j = 0; j < mundo.getTamañoDelMapa(); j++) {
                Casilla casillaActual = mundo.getCasilla(i, j);
                Casilla izquierda = mundo.getCasilla(i - 1, j);
                Casilla derecha = mundo.getCasilla(i + 1, j);
                Casilla arriba = mundo.getCasilla(i, j + 1);
                Casilla abajo = mundo.getCasilla(i, j - 1);
                // ignora la casilla si no hay conexion con casillas ya descubiertas
                if (izquierda.oculta && derecha.oculta && arriba.oculta && abajo.oculta) continue;
                // analiza si tiene oro
                if (tieneOro(i, j)) posiblesCasillasRicas.add(casillaActual);
            }
        }
        if (posiblesCasillasRicas.size() > 0) {
            // encuentra el tesoro mas cercano método directo, pero ...
            Casilla mejorCasilla = (Casilla) posiblesCasillasRicas.elementAt(0);
            int mejorDistancia = calculaDistanciaEntreCasillas(miPosicion, mejorCasilla);

            for (int i = 1; i < posiblesCasillasRicas.size(); i++) {
                Casilla casillaActual = (Casilla) posiblesCasillasRicas.elementAt(i);
                int distanciaActual = calculaDistanciaEntreCasillas(miPosicion, casillaActual);

                if (distanciaActual < mejorDistancia) {
                    mejorDistancia = distanciaActual;
                    mejorCasilla = casillaActual;
                } else if (distanciaActual == mejorDistancia) {
                    // bestDist = actualDistance;
                    int dir = mundo.getMirandoA(); // recogemos hacia donde estamos mirando
                    mejorCasilla = escogeCasillaEnFuncionDeLaDireccion(mejorCasilla, dir);
                    if (i == posiblesCasillasRicas.size()) {
                        break;
                    } else {
                        continue;
                    }
                }
            }
            proximosPasos = encuentraRuta(miPosicion.x, miPosicion.y, mundo.getMirandoA(), mejorCasilla.x, mejorCasilla.y, mundo.SINOPERACION);
            proximosPasos.add(new Integer(mundo.RECOGEORO));
        }
        return proximosPasos;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: Vector casillasSeguras
     * Descripción: Analizamos los entornos y buscamos las casillas seguras de nuestro alrededor.
     */
    private Vector buscadorDeCasillasSeguras() {
        /* 1. Inicializamos el vector de casillas seguras*/
        Vector casillasSeguras = new Vector();
        for (int i = 0; i < mundo.getTamañoDelMapa(); i++) {
            for (int j = 0; j < mundo.getTamañoDelMapa(); j++) {
                /* 2. Recuperamos la primera casilla*/
                Casilla casillaActual = mundo.getCasilla(i, j);
                /* 3. Si la casilla ya ha sido descubierta, la ignoramos*/
                if (!(casillaActual.oculta && casillaActual.valida)) {
                    continue;
                }
                /* 4. Si no ha sido descubierta aun, inicializamos sus vecinos */
                Casilla izquierda = mundo.getCasilla(i - 1, j);
                Casilla derecha = mundo.getCasilla(i + 1, j);
                Casilla arriba = mundo.getCasilla(i, j + 1);
                Casilla abajo = mundo.getCasilla(i, j - 1);

                /* 5. Miramos si todos sus vecinos estan ocultos, en ese caso, ignoramos la casilla*/
                if (izquierda.oculta && derecha.oculta && arriba.oculta && abajo.oculta) {
                    continue;
                }
                /* 6. Una casilla se considera segura si ninguno de sus vecinos tiene peligro*/
                if ((!izquierda.oculta && !izquierda.hedor && !izquierda.brisa)
                        || (!derecha.oculta && !derecha.hedor && !derecha.brisa)
                        || (!arriba.oculta && !arriba.hedor && !arriba.brisa)
                        || (!abajo.oculta && !abajo.hedor && !abajo.brisa)) {
                    casillasSeguras.add(casillaActual);
                    continue;
                }
                /* es seguro si no hay wumpus ni pozo, el caso anterior también sería reconocido, lo dejamos de todos modos ya que esto aquí es más complicado **/
                if (noTieneWumpus(i, j) && noTienePozo(i, j)) {
                    casillasSeguras.add(casillaActual);
                    continue;
                }

            }
        }
        return casillasSeguras;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: Vector casillasSeguras, Casilla casillaActual, Vector proximosPasos
     * @return: Vector proximosPasos
     * Descripción: El metodo realiza el analisis del entorno de las casillas seguras en funcion a la casilla actual
     * y decide el orden de visitar las casillas seguras.
     */
    private Vector buscarDeRutaSegura(Vector casillasSeguras, Casilla casillaActual, Vector proximosPasos) {
        /* 1. Nos marcamos la primera casilla segura */
        Casilla mejorCasilla = (Casilla) casillasSeguras.elementAt(0);
        /* 2. Calculamos la distancia entre nosotros y la primera casilla segura */
        int mejorDistancia = calculaDistanciaEntreCasillas(casillaActual, mejorCasilla);
        /* 3. Recorremos el resto de casillas seguras haciendo lo mismo */
        for (int i = 1; i < casillasSeguras.size(); i++) {
            /* 4. Inicialimaos la segunda casilla del vecntor de casillas seguras.*/
            Casilla casillaSegura = (Casilla) casillasSeguras.elementAt(i);
            /* 5. Calculamos la distancia que hay entre la casilla actual y la casilla segura*/
            int distanciaActual = calculaDistanciaEntreCasillas(casillaActual, casillaSegura);
            /* 6. Comparamos las distancias de las casillas posibles */
            if (distanciaActual < mejorDistancia) {
                mejorDistancia = distanciaActual;
                mejorCasilla = casillaSegura;
            } else if (distanciaActual == mejorDistancia) {
                int direccionActual = mundo.getMirandoA(); // recogemos hacia donde estamos mirando
                mejorCasilla = escogeCasillaEnFuncionDeLaDireccion(casillaSegura, direccionActual);
            }
        }
        proximosPasos = encuentraRuta(casillaActual.x, casillaActual.y, mundo.getMirandoA(), mejorCasilla.x, mejorCasilla.y, mundo.SINOPERACION);
        return proximosPasos;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: Vector listadoDeCasillasConWumpus
     * @return: Vector listadoDeCasillasConWumpus
     * Descripción: a partir del listado de casillas introducido,
     * analizamos los vecinos y decidimos si es un wumpus o no.
     */
    private Vector buscadorDeCasillasConWumpus(Vector listadoDeCasillasConWumpus) {
        for (int i = 0; i < mundo.getTamañoDelMapa() && (mundo.getFlechasRestantes() > 0); i++) {
            for (int j = 0; j < mundo.getTamañoDelMapa(); j++) {
                /* 1. Inicializamos la casilla actual.*/
                Casilla casillaActual = mundo.getCasilla(i, j);

                /* 2. Obviamos la casilla si ya ha sido descubierta.*/
                if (!(casillaActual.oculta && casillaActual.valida)) {
                    continue;
                }

                /* 3. Inicializamos los vecinos adyacentes a la casilla actual */
                Casilla izquierda = mundo.getCasilla(i - 1, j);
                Casilla derecha = mundo.getCasilla(i + 1, j);
                Casilla arriba = mundo.getCasilla(i, j + 1);
                Casilla abajo = mundo.getCasilla(i, j - 1);

                /* 4. Ignora la casilla si todos sus vecinos estan ocultos */
                if (izquierda.oculta && derecha.oculta && arriba.oculta && abajo.oculta) {
                    continue;
                }

                /* 5. Comprobamos si la casilla es un wumpus seguro y si no es inofensiva y
                 * lo añadimos a la lista**/
                if (tieneWumpus(i, j) && !casillaInofensiva(i, j)) {
                    listadoDeCasillasConWumpus.add(casillaActual);
                }
            }
        }
        return listadoDeCasillasConWumpus;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: Vector listadoCasillasConWumpus, Casilla casillaActual, Vector proximosPasos
     * @return: Vector proximosPasos
     * Descripción: Creamos la ruta que tomaremos para evitar los wumpus en caso de ser posible.
     */
    private Vector rutaDeLosWumpus(Vector listadoCasillasConWumpus, Casilla casillaActual, Vector proximosPasos) {
        Casilla mejorCasilla = (Casilla) listadoCasillasConWumpus.elementAt(0);
        int mejorDireccion = calculaDistanciaEntreCasillas(casillaActual, mejorCasilla);
        for (int i = 1; i < listadoCasillasConWumpus.size(); i++) {
            Casilla casillaTemporal = (Casilla) listadoCasillasConWumpus.elementAt(i);
            int distanciaActual = calculaDistanciaEntreCasillas(casillaActual, casillaTemporal);
            if (distanciaActual < mejorDireccion) {
                mejorDireccion = distanciaActual;
                mejorCasilla = casillaTemporal;
            } else if (distanciaActual == mejorDireccion) {
                int direccionActual = mundo.getMirandoA(); // recogemos hacia donde estamos mirando
                mejorCasilla = escogeCasillaEnFuncionDeLaDireccion(mejorCasilla, direccionActual);
                if (i == listadoCasillasConWumpus.size()) {
                    break;
                } else {
                    continue;
                }
            }
        }

        int temporalX = -1;
        int temporalY = -1;
        int direccion = -1;

        Casilla izquierda = mundo.getCasilla(mejorCasilla.x - 1, mejorCasilla.y);
        Casilla derecha = mundo.getCasilla(mejorCasilla.x + 1, mejorCasilla.y);
        Casilla arriba = mundo.getCasilla(mejorCasilla.x, mejorCasilla.y + 1);
        Casilla abajo = mundo.getCasilla(mejorCasilla.x, mejorCasilla.y - 1);

        int distancia = 10000000; // para asegurarnos
        // buscamos una casilla que este abierta lo mas cerca posible a nuestra posicion
        if (!arriba.oculta && calculaDistanciaEntreCasillas(arriba, casillaActual) < distancia) {
            temporalX = arriba.x;
            temporalY = arriba.y;
            direccion = mundo.SUR;
            distancia = calculaDistanciaEntreCasillas(arriba, casillaActual);
        }
        if (!derecha.oculta && calculaDistanciaEntreCasillas(derecha, casillaActual) < distancia) {
            temporalX = derecha.x;
            temporalY = derecha.y;
            direccion = mundo.OESTE;
            distancia = calculaDistanciaEntreCasillas(derecha, casillaActual);
        }
        if (!izquierda.oculta && calculaDistanciaEntreCasillas(izquierda, casillaActual) < distancia) {
            temporalX = izquierda.x;
            temporalY = izquierda.y;
            direccion = mundo.ESTE;
            distancia = calculaDistanciaEntreCasillas(izquierda, casillaActual);
        }
        if (!abajo.oculta && calculaDistanciaEntreCasillas(abajo, casillaActual) < distancia) {
            temporalX = abajo.x;
            temporalY = abajo.y;
            direccion = mundo.NORTE;
            distancia = calculaDistanciaEntreCasillas(abajo, casillaActual);
        }

        proximosPasos = encuentraRuta(casillaActual.x, casillaActual.y, mundo.getMirandoA(), temporalX, temporalY, direccion);
        return proximosPasos;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: Casilla izquierda, Casilla derecha, Casilla arriba, Casilla abajo, float probabilidad, int x, int y
     * @return: float probabilidad
     * Descripción: Determinamos el grado de probabilidad de existencia de un wumpus para una casilla
     */
    private float calculaProbabilidadDeWumpus(Casilla izquierda, Casilla derecha, Casilla arriba, Casilla abajo, float probabilidad, int x, int y) {
        if (!izquierda.oculta && izquierda.hedor) {
            int vecinosSeguros = contadorDeVecinosSeguros(x - 1, y);
            probabilidad = probabilidad + 1f / vecinosSeguros;
        }
        if (!derecha.oculta && derecha.hedor) {
            int vecinosSeguros = contadorDeVecinosSeguros(x + 1, y);
            probabilidad = probabilidad + 1f / vecinosSeguros;
        }
        if (!arriba.oculta && arriba.hedor) {
            int vecinosSeguros = contadorDeVecinosSeguros(x, y + 1);
            probabilidad = probabilidad + 1f / vecinosSeguros;
        }
        if (!abajo.oculta && abajo.hedor) {
            int vecinosSeguros = contadorDeVecinosSeguros(x, y - 1);
            probabilidad = probabilidad + 1f / vecinosSeguros;
        }
        return probabilidad;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: Casilla mejorCasilla, Casilla casillaActual, Vector proximosPasos
     * @return: Vector proximosPasos
     * Descripción: El siguiente fue, para disparar solo cuando hay una "buena" oportunidad.
     */
    private Vector disparaSiWumpus(Casilla mejorCasilla, Casilla casillaActual, Vector proximosPasos) {
        int temporalX = -1;
        int temporalY = -1;
        int direccion = -1;

        Casilla izquierda = mundo.getCasilla(mejorCasilla.x - 1, mejorCasilla.y);
        Casilla derecha = mundo.getCasilla(mejorCasilla.x + 1, mejorCasilla.y);
        Casilla arriba = mundo.getCasilla(mejorCasilla.x, mejorCasilla.y + 1);
        Casilla abajo = mundo.getCasilla(mejorCasilla.x, mejorCasilla.y - 1);
        int distanciaSegura = 10000000;    // sholud be enough
        // Encontrar una casilla descubierta lo mas cercana posible a nosotros
        if (!arriba.oculta && calculaDistanciaEntreCasillas(arriba, casillaActual) < distanciaSegura) {
            temporalX = arriba.x;
            temporalY = arriba.y;
            direccion = mundo.SUR;
            distanciaSegura = calculaDistanciaEntreCasillas(arriba, casillaActual);
        }

        if (!derecha.oculta && calculaDistanciaEntreCasillas(derecha, casillaActual) < distanciaSegura) {
            temporalX = derecha.x;
            temporalY = derecha.y;
            direccion = mundo.OESTE;
            distanciaSegura = calculaDistanciaEntreCasillas(derecha, casillaActual);
        }

        if (!izquierda.oculta && calculaDistanciaEntreCasillas(izquierda, casillaActual) < distanciaSegura) {
            temporalX = izquierda.x;
            temporalY = izquierda.y;
            direccion = mundo.ESTE;
            distanciaSegura = calculaDistanciaEntreCasillas(izquierda, casillaActual);
        }

        if (!abajo.oculta && calculaDistanciaEntreCasillas(abajo, casillaActual) < distanciaSegura) {
            temporalX = abajo.x;
            temporalY = abajo.y;
            direccion = mundo.NORTE;
            distanciaSegura = calculaDistanciaEntreCasillas(abajo, casillaActual); // añadida
        }

        proximosPasos = encuentraRuta(casillaActual.x, casillaActual.y, mundo.getMirandoA(), temporalX, temporalY, direccion);
        return proximosPasos;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: Vector movimientosSiguientes, Casilla casillaActual
     * @return: Vector proximosPasos
     * Descripción: Buscamos una ruta, arriesgandonos a jugar con la existencia de wumpus alrededor siempre y cuando
     * detectemos que el wumpus adyacente es inofensivo o esta muerto
     * o podemos matarlo para pasar a traves de la casila.
     */
    private Vector buscarRutaArriesgada(Vector movimientosSiguientes, Casilla casillaActual) {
        Casilla mejorCasilla = null;
        float probabilidadMaxima = 0f;
        int inofesivos = 0;

        for (int i = 0; i < mundo.getTamañoDelMapa() && (mundo.getFlechasRestantes() > 0); i++) {
            for (int j = 0; j < mundo.getTamañoDelMapa(); j++) {

                /* 1. Nos situamos en la casilla actual*/
                Casilla casillaTemporal = mundo.getCasilla(i, j);

                /* 2. Si la casilla no esta visitada **/
                if (!(casillaTemporal.oculta && casillaTemporal.valida && casillaTemporal.visitada)) continue;

                /* 3. Inicializamos las casillas adyacentes*/
                Casilla izquierda = mundo.getCasilla(i - 1, j);
                Casilla derecha = mundo.getCasilla(i + 1, j);
                Casilla arriba = mundo.getCasilla(i, j + 1);
                Casilla abajo = mundo.getCasilla(i, j - 1);

                /* 4. Comprobamos que las adyacentes estan conectadas */
                if (izquierda.oculta && derecha.oculta && arriba.oculta && abajo.oculta) continue;

                /* 5. Comprobamos que la casilla no esta marcada como un pozo */
                if (tieneBrisa(i, j)) continue;

                /* 6. Si alguno de sus vecinos tiene hedor, pasamos de ella */
                if (!izquierda.hedor && !derecha.hedor && !arriba.hedor && !abajo.hedor) continue;

                /* 7. Si detectamos un wumpus inofensivo, pasamos de el, pero teniendolo en cuenta*/
                if (tieneWumpus(i, j) && casillaInofensiva(i, j)) {
                    inofesivos++;
                    continue;
                }

                /* 8. Calcula la probabilidad de que haya un wumpus*/
                float probabilidad = 0f;
                probabilidad = calculaProbabilidadDeWumpus(izquierda, derecha, arriba, abajo, probabilidad, i, j);

                if (probabilidad > probabilidadMaxima) {
                    probabilidadMaxima = probabilidad;
                    mejorCasilla = casillaTemporal;
                } else if (probabilidad == probabilidadMaxima) {
                    int direccion = mundo.getMirandoA();
                    mejorCasilla = escogeCasillaEnFuncionDeLaDireccion(mejorCasilla, direccion);
                }
            }
        }
        if (mejorCasilla != null && (probabilidadMaxima >= 0.5 || (mundo.getFlechasRestantes() > (mundo.getCuantosWumpus() - inofesivos) && probabilidadMaxima >= 0.5))) {
            /* El siguiente fue, para disparar solo cuando hay una "buena" oportunidad.*/
            movimientosSiguientes = disparaSiWumpus(mejorCasilla, casillaActual, movimientosSiguientes);
            movimientosSiguientes.add(new Integer(mundo.DISPARA));
        }
        return movimientosSiguientes;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: Vector movimientosSiguientes, Casilla casillaActual
     * @return: Vector proximosPasos
     * Descripción: Esta es la ultima estrategia a emplear, buscamos la casilla mejor por probabilidad.
     */
    private Vector buscarRutaPorProbabilidad(Vector movimientosSiguientes, Casilla casillaActual) {
        /* Inicializamos la casilla que usaremos de pivote*/
        Casilla casillaTemp = null;
        int distanciaMinima = 0;
        int distanciaPivote = 0;
        /* Establecemos la probabilidad minimma, sabemos que es imposible que tenga mas de 9 signos*/
        float probabilidadMinima = 9f; // Es imposible que una casilla tenga mas de 8 signos de peligro.
        /* Recorremos el mapa*/
        for (int i = 0; i < mundo.getTamañoDelMapa(); ++i) {
            for (int j = 0; j < mundo.getTamañoDelMapa(); ++j) {
                /* Nos situamos en la casilla actual */
                Casilla actual = mundo.getCasilla(i, j);
                /*Comprobamos si la casilla ya ha sido descubierta, en ese caso, la ignoramos*/
                if (!(actual.oculta && actual.valida)) continue;
                /* Si no ha sido descubierta, inicializamos sus vecinos*/
                Casilla izquierda = mundo.getCasilla(i - 1, j);
                Casilla derecha = mundo.getCasilla(i + 1, j);
                Casilla arriba = mundo.getCasilla(i, j + 1);
                Casilla abajo = mundo.getCasilla(i, j - 1);
                /* Ignoramos la casilla si todos sus vecinos esta ocultos */
                if (izquierda.oculta && derecha.oculta && arriba.oculta && abajo.oculta) {
                    continue;
                }
                /*Si la casilla no tiene una conexion interesante, pasamos de ella*/
                if (!(izquierda.valida && izquierda.oculta)
                        && !(derecha.valida && derecha.oculta)
                        && !(arriba.valida && arriba.oculta)
                        && !(abajo.valida && abajo.oculta)) {
                    continue;
                }

                /* Comprobamos que la casilla no es un peligro seguro, si lo es pasamos de ella */
                if (tieneBrisa(i, j) || tieneWumpus(i, j)) continue;

                /* cuenta las advertencias de peligro. 1 por cada signo de peligro en una habitación contigua
                 pero si la advertencia está ahí debido a otro peligro ya conocido
                 resta 0.5 (no 1 sino 0.5 porque el signo de peligro también puede ser
                 significado para esta sala)
                 Contamos la cantidad de advertencias de peligro que puede tener la casilla
                 Inicializamos el contador de cantidad de peligro */
                float cantidadDePeligro = 0;
                /*Calculamos la cantidad de peligro de sus ayacentes*/
                cantidadDePeligro = calculaPeligroDeAdayacentes(derecha, izquierda, arriba, abajo, i, j, probabilidadMinima, casillaTemp, actual, cantidadDePeligro);
                /* Comparamos el peligro actual con la cantidad minima preestablecida,
                 *  si es menor hacemos un swap entre ellas y nos quedamos con la casilla actual como posible proximo paso
                 *  Si es la misma probabilidad, calculamos que casilla requiere menos movimientos,
                 * si la nueva requiere menos movimientos, actulizamos al proxima casilla.*/
                if (cantidadDePeligro < probabilidadMinima) {
                    probabilidadMinima = cantidadDePeligro;
                    casillaTemp = actual;
                    distanciaMinima = calculaDistanciaEntreCasillas(casillaActual, casillaTemp);
                    distanciaMinima = distanciaMinima + encuentraRuta(casillaActual.x, casillaActual.y, mundo.getMirandoA(), casillaTemp.x, casillaTemp.y, mundo.SINOPERACION).size();
                } else if (cantidadDePeligro == probabilidadMinima) {
                    distanciaPivote = calculaDistanciaEntreCasillas(actual, casillaTemp);
                    distanciaPivote = distanciaPivote + encuentraRuta(casillaActual.x, casillaActual.y, mundo.getMirandoA(), casillaTemp.x, casillaTemp.y, mundo.SINOPERACION).size();
                    if (distanciaPivote < distanciaMinima) {
                        casillaTemp = actual;
                    }
                    //int dir = mundo.getMirandoA();
                    //casillaTemp = escogeCasillaEnFuncionDeLaDireccion(actual, dir);
                } else {
                    Vector listadoDeWupmpus = new Vector();
                    listadoDeWupmpus = buscadorDeCasillasConWumpus(listadoDeWupmpus);
                    if (listadoDeWupmpus.size() > 0) {
                        movimientosSiguientes = rutaDeLosWumpus(listadoDeWupmpus, casillaActual, movimientosSiguientes);
                        movimientosSiguientes.add(new Integer(mundo.DISPARA));
                    }
                }
                //break;
            }
        }

        if (casillaTemp != null) {
            movimientosSiguientes = encuentraRuta(casillaActual.x, casillaActual.y, mundo.getMirandoA(), casillaTemp.x, casillaTemp.y, mundo.SINOPERACION);
        }
        return movimientosSiguientes;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: Casilla derecha,Casilla izquierda, Casilla arriba,Casilla abajo,
     * int i, int j,
     * float probabilidadMinima, float cantidadDePeligro
     * Casilla casillaTemp, Casilla actual,
     * @return: float cantidad de peligro
     * Descripción: calcula la cantidad de peligro existente en los vecinos de la casilla.
     */
    public float calculaPeligroDeAdayacentes(Casilla derecha, Casilla izquierda, Casilla arriba, Casilla abajo, int i, int j, float probabilidadMinima, Casilla casillaTemp, Casilla actual, float cantidadDePeligro) {
        if (derecha.hedor) {
            cantidadDePeligro = cantidadDePeligro + 1;
            if (tieneWumpus(i + 2, j) || tieneWumpus(i + 1, j - 1) || tieneWumpus(i + 1, j + 1)) {
                cantidadDePeligro = cantidadDePeligro - 0.5f;
            }
        }
        if (izquierda.hedor) {
            cantidadDePeligro = cantidadDePeligro + 1;
            if (tieneWumpus(i - 2, j) || tieneWumpus(i - 1, j - 1) || tieneWumpus(i - 1, j + 1)) {
                cantidadDePeligro = cantidadDePeligro - 0.5f;
            }
        }
        if (arriba.hedor) {
            cantidadDePeligro = cantidadDePeligro + 1;
            if (tieneWumpus(i, j + 2) || tieneWumpus(i - 1, j + 1) || tieneWumpus(i + 1, j + 1)) {
                cantidadDePeligro = cantidadDePeligro - 0.5f;
            }
        }
        if (abajo.hedor) {
            cantidadDePeligro = cantidadDePeligro + 1;
            if (tieneWumpus(i, j - 2) || tieneWumpus(i - 1, j - 1) || tieneWumpus(i + 1, j - 1)) {
                cantidadDePeligro = cantidadDePeligro - 0.5f;
            }
        }

        if (derecha.brisa) {
            cantidadDePeligro = cantidadDePeligro + 1;
            if (tieneBrisa(i + 2, j) || tieneBrisa(i + 1, j - 1) || tieneBrisa(i + 1, j + 1)) {
                cantidadDePeligro = cantidadDePeligro - 0.5f;
            }
        }
        if (izquierda.brisa) {
            cantidadDePeligro = cantidadDePeligro + 1;
            if (tieneBrisa(i - 2, j) || tieneBrisa(i - 1, j - 1) || tieneBrisa(i - 1, j + 1)) {
                cantidadDePeligro = cantidadDePeligro - 0.5f;
            }
        }
        if (arriba.brisa) {
            cantidadDePeligro = cantidadDePeligro + 1;
            if (tieneBrisa(i, j + 2) || tieneBrisa(i - 1, j + 1) || tieneBrisa(i + 1, j + 1)) {
                cantidadDePeligro = cantidadDePeligro - 0.5f;
            }
        }
        if (abajo.brisa) {
            cantidadDePeligro = cantidadDePeligro + 1;
            if (tieneBrisa(i, j - 2) || tieneBrisa(i - 1, j - 1) || tieneBrisa(i + 1, j - 1)) {
                cantidadDePeligro = cantidadDePeligro - 0.5f;
            }
        }
        //break;
        return cantidadDePeligro;
    }
}
