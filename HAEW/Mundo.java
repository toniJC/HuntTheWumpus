package HAEW;


import java.lang.*;
import java.util.*;
import java.io.*;
import java.util.logging.*;


public class Mundo extends Observable {

    /*
    * DIRECCIONES POSIBLES
    **/
    public static int NORTE = 0;            // MIRANDO EN DIRECCION NORTE
    public static int SUR = 1;             // MIRANDO EN DIRECCION SUR
    public static int OESTE = 2;             // MIRANDO EN DIRECCION OESTE
    public static int ESTE = 3;            // MIRANDO EN DIRECCION ESTE

    /*
    * SENSACIONES DEL JUGADOR
    */
    public static int BRISA = 4;           // SE SIENTE BRISA
    public static int HEDOR = 5;           // SE SIENTE HEDOR
    public static int GRITO = 7;           // SE ESCUCHA UN GRITO

    /*
    * POSIBLES ESTADOS
    */
    public static int SINOPERACION = 8;             // NO OPERACION
    public static int PARTIDAPERDIDA = 9;        // FIN DEL JUEGO COMO PERDERDEDOR
    public static int PARTIDAGANADA = 10;        // FIN DEL JUEGO COMO GANADOR
    public static int MOVIMIENTOCOMPLETADO = 11;   // MOVIMIENTO COMPLETADO
    public static int MOVIMIENTOERRONEO = 12;        // MOVIMIENTO NO VALIDO
    public static int ORORECOGIDO = 13;    // ORO RECOGIDO
    public static int OROENCONTRADO = 14;      // ORO ENCONTRADO
    public static int OROPUESTOASALVO = 15;      // ORO PUESTO A SALVO
    public static int JUEGONUEVO = 16;       // NUEVO MUNDO DE JUEGO
    public static int MOVIMIENTODESCONOCIDO = 17;    // MOVIMIENTO DESCONOCIDO
    public static int ESTOYPERDIDO = 18;

    /*
    * MOVIMIENTOS DEL JUGADOR
    */

    public static int AVANZAR = 19;         // AVANZAR
    public static int RETROCEDER = 20;         // RETROCEDER
    public static int GIRARIZQUIERDA = 21;       // GIRAR A IZQ
    public static int GIRARDERECHA = 22;      // GIRAR A DCHA
    public static int DISPARA = 23;           // LANZAR FLECHA
    public static int RECOGEORO = 24;            // COGER (ORO)

    /*
    * POSIBLES OBSTACULOS
    */
    public static int SALIDA = 25;            // SALIDA
    public static int WUMPUS = 26;          // WUMPUS VIVO
    public static int WUMPUSMUERTO = 27;     // WUMPUS MUERTO
    public static int ORO = 28;            // ORO
    public static int POZO = 29;             // POZO
    public static int VACIO = 30;           // SALA VACÍA
    public static int PARED = 31;            // PARED

    /*
    * VARIABLES DE LA CLASE
    */
    private static int MAXIMAOSCICLOS = 16 * 16 * 5;
    private int[][] casilla;                  // TERRENO DE JUEGO
    private boolean[][] casillasEscondidas;             // COSAS ESCONDIDAS
    private int coordenadaX;                       // POSICION EN ABCISAS
    private int coordenadaY;                       // POSICION EN ORDENADAS
    private int mirandoA;                  // DIRECCION EN LA QUE SE MIRA
    private int contadorDeOros;                 // CONTADOR DE ORO TOTAL
    private int orosEncontrados;           // CONTADOR DE ORO PUESTO A SALVO
    private int contadorDeWumpus;               // CONTADOR DE WUMPUS VIVOS
    private int contadorWumpusMuertos;          // CONTADOR DE WUMPUS MUERTOS
    private int contadorDePozos;                  // CONTADOR DE POZOS
    private int contadorDeFlechas;                // CONTADOR DE FLECHAS
    private int tamañoDelMapa;                 // TAMAÑO DEL TERRENO DE JUEGO
    private int contadorDeCasillas;                // CONTADOR DE CASILLAS DEL JUEGO
    private int contadorDeMovimientos;                 // CONADOR DE MOVIMIENTOS
    private int ganadas;                        // PARTIDAS GANADAS
    private int perdidas;                       // PARTIDAS PERDIDAS
    private int nivel;                      // NIVEL DE DIFICULTAD
    private boolean oroRecogido;                // ¿AGARRADO?
    private boolean juegoActivado = false;    // JUEGO EN ACTIVO
    private Vector contadorOrosRecogidos;               // ARRAY DE OROS
    private int coste;                       // COSTE DE LOS MOVIMIENTOS
    public justificacion justificacion;
    public int tipoJuego;

    private static final Logger log = Logger.getLogger(introducirNombre.class.getName());

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: N/A
     * Descripción: Es el contructor del mundo del juego
     */
    public Mundo() {
        super();
        justificacion = new justificacion();
        Handler fileHandler = null;
        java.util.logging.Formatter simpleFormattter = null;

        try {
            fileHandler = new FileHandler("./log");
            simpleFormattter = new SimpleFormatter();
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.addHandler(fileHandler);
        fileHandler.setLevel(Level.ALL);
        fileHandler.setFormatter(simpleFormattter);
        log.setLevel(Level.ALL);
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int TamanoMapa
     * @return: void
     * Descripción: Recoge el tamaño del mapa e inicializa el coste
     */
    public void juegoNuevo(int tamanoMapa) {
        setCoste(1000);
        iniciaJuego(tamanoMapa); //4x4 por defecto
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: String nombreFichero
     * @return: String
     * Descripción: Nuevo Juego desde fichero personalizado
     */
    public String juegoNuevo(String nombreFichero) {

        try {
            File fichero = new File(nombreFichero);
            if (!fichero.isFile() || !fichero.canRead()) {
                return "IO error:\n El fichero no existe o no se puede leer!";
            }
            BufferedReader ficheroDeEntrada = new BufferedReader(new FileReader(fichero));

            String lineaFichero = null;
            while ((lineaFichero = ficheroDeEntrada.readLine()) != null) {
                lineaFichero = lineaFichero.trim();
                if (!(lineaFichero.length() == 0 || lineaFichero.startsWith("#"))) break;
            }
            if (lineaFichero == null) {
                return "Error de formato:\n El fichero esta vacío!";
            }

            int tamano = tamañoDelMapa;
            try {
                tamano = (new Integer(lineaFichero)).intValue();
                if (tamano < 4 || tamano > 16) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                return "error de formato:\n El primer caracter del fichero (n: 4<=n<=16)\n" +
                        " no es el tamaño del mapa!";
            }
            int[][] numeroFilas = new int[tamano][tamano];
            int salidaX = -1;
            int salidaY = -1;
            int oro = 0;
            int nWumpus = 0;
            int nPozos = 0;
            int nFlechas = -1;
            Vector vectorFinal = new Vector();

            int tam = tamano;
            while ((lineaFichero = ficheroDeEntrada.readLine()) != null) {
                lineaFichero = lineaFichero.trim();
                //System.out.println("Reading lineaFichero: " + lineaFichero);

                if (lineaFichero.length() == 0 || lineaFichero.startsWith("#")) continue;

                if (lineaFichero.startsWith("Flechas: ")) {
                    if (nFlechas != -1) {
                        return "Error de formato:\n \"Flechas:\"-se introdujo dos veces!";
                    }
                    try {
                        nFlechas = (new Integer(lineaFichero.substring(7).trim())).intValue();
                        //System.out.println("Read Arrows: "+ nac);
                        if (tamano < 0) throw new NumberFormatException();
                    } catch (NumberFormatException ex) {
                        return "Error de formato:\n numero entero (>=0) antes de\n \"felchas:\"-comando no encontrado!";
                    }
                    continue;
                }


                StringTokenizer strTok = new StringTokenizer(lineaFichero, " \t");
                tam = tam - 1;

                if (tam < 0) {
                    return "Error de formato:\n el fichero tiene demasiadas lineas!";
                }
                int i = 0;
                while (strTok.hasMoreTokens()) {
                    String tok = strTok.nextToken();
                    if (tok.startsWith("0")) {
                        numeroFilas[i][tam] = VACIO;
                    } else if (tok.startsWith("W") || tok.startsWith("w")) {
                        numeroFilas[i][tam] = WUMPUS;
                        nWumpus++;
                    } else if (tok.startsWith("M") || tok.startsWith("m")) {
                        numeroFilas[i][tam] = PARED;
                    } else if (tok.startsWith("O") || tok.startsWith("o")) {
                        numeroFilas[i][tam] = ORO;
                        oro++;
                        vectorFinal.add(new Casilla(i, tam));
                    } else if (tok.startsWith("P") || tok.startsWith("p")) {
                        numeroFilas[i][tam] = POZO;
                        nPozos++;
                    } else if (tok.startsWith("S") || tok.startsWith("s")) {
                        if (salidaX == -1) {
                            numeroFilas[i][tam] = SALIDA;
                            salidaX = i;
                            salidaY = tam;
                        } else {
                            return "Error de formato:\n hay mas de una salida en el fichero!";
                        }
                    } else {
                        return "Error de formato:\n se detectó un simbolo desconocido: \"" + tok + "\"!";
                    }
                    i++;
                }
                if (i != tamano) {
                    return "Error de formato:\n final de linea inesperado!";
                }
            }
            if (tam != 0) {
                return "Error de formato:\n final de fichero inesperado!";
            }
            if (salidaX == -1) {
                return "Error de formato:\n No hay casilla de salida!";
            }
            if (oro <= 0) {
                return "Error de formato:\n no hay oro en el mapa!";
            }

            tamañoDelMapa = tamano;
            contadorDeCasillas = tamañoDelMapa * tamañoDelMapa;
            contadorDeOros = oro;
            contadorOrosRecogidos = vectorFinal;
            contadorDeFlechas = (nFlechas < 0 ? nWumpus : nFlechas);
            contadorDeWumpus = nWumpus;
            contadorDePozos = nPozos;
            oroRecogido = false;

            casilla = numeroFilas;
            casillasEscondidas = new boolean[tamañoDelMapa][tamañoDelMapa];
            for (int casillas = 0; casillas < contadorDeCasillas; casillas++) {
                casillasEscondidas[casillas / tamañoDelMapa][casillas % tamañoDelMapa] = true;
            }
            casillasEscondidas[salidaX][salidaY] = false;
            coordenadaX = salidaX;
            coordenadaY = salidaY;

            oroRecogido = false;
            orosEncontrados = 0;
            contadorWumpusMuertos = 0;
            perdidas = ganadas = 0;
            nivel = 1;
            contadorDeMovimientos = 0;
            juegoActivado = true;
            mirandoA = ESTE;
            setChanged();
            Casilla f = getCasilla(coordenadaX, coordenadaY);
            if (f.hedor) {
                setChanged();
                notifyObservers(new Chivato(HEDOR, coordenadaX, coordenadaY, "Apesta, debe haber un wumpus cerca"));
            }
            if (f.brisa) {
                setChanged();
                notifyObservers(new Chivato(BRISA, coordenadaX, coordenadaY, "esta brisa...estas cerca de un pozo"));
            }
            boolean oroAlcanzable = false;
            for (int i = 0; i < contadorOrosRecogidos.size(); i++) {
                Casilla listadoCasillasRecogidas = (Casilla) contadorOrosRecogidos.get(i);
                oroAlcanzable = oroAlcanzable || calculaSiElOroEsAlcanzable(coordenadaX, coordenadaY, listadoCasillasRecogidas.x, listadoCasillasRecogidas.y, contadorDeFlechas > 0);
                if (oroAlcanzable) break;
            }
            if (!oroAlcanzable) {
                juegoActivado = false;
                setChanged();
                notifyObservers(new Chivato(PARTIDAGANADA, coordenadaX, coordenadaY, "Enhorabuena, has recogido todo el oro"));
                ganadas++;
            }
        } catch (Exception ex) {
            return "Error de fichero:\n " + ex.toString();
        }

        return null;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int tamano
     * @return: void
     * Descripción: inicia el juego de forma random
     */
    public void iniciaJuego(int tamano) {
        tamañoDelMapa = tamano;
        if (tamañoDelMapa < 4) tamañoDelMapa = 5;
        contadorDeCasillas = tamañoDelMapa * tamañoDelMapa;
        contadorDeOros = 1; //tamañoDelMapa * tamañoDelMapa / 16; con esta linea calculamos la posicion de mas oros
        contadorDeFlechas = contadorDeWumpus = tamañoDelMapa / 3;

        contadorDePozos = (int) (0.20 * contadorDeCasillas);
        contadorDeMovimientos = 0;
        oroRecogido = false;
        setCoste(1000);
        casilla = new int[tamañoDelMapa][tamañoDelMapa];
        casillasEscondidas = new boolean[tamañoDelMapa][tamañoDelMapa];
        justificacion.inicia();

        int x, y;
        boolean tieneSolucion = true;
        int contadorDeVueltas = 0;

        do {
            // reninicilaizar paneles
            for (int i = 0; i < contadorDeCasillas; i++) {
                casilla[i / tamañoDelMapa][i % tamañoDelMapa] = VACIO;
                casillasEscondidas[i / tamañoDelMapa][i % tamañoDelMapa] = true;
            }
            // colocar salida
            coordenadaX = 0;
            coordenadaY = 0;
            casilla[coordenadaX][coordenadaY] = SALIDA;
            casillasEscondidas[coordenadaX][coordenadaY] = false;

            //Colocar pozos (No hay pozos adyacentes a la entrada)

            for (int j = 0; j < contadorDePozos; ++j) {
                do {
                    x = plantaAleatoriedad(tamañoDelMapa);
                    y = plantaAleatoriedad(tamañoDelMapa);
                } while (casilla[x][y] != VACIO
                        || (x == coordenadaX && (y == coordenadaY - 1 || y == coordenadaY + 1))
                        || (y == coordenadaY && (x == coordenadaX + 1 || x == coordenadaX - 1)));
                casilla[x][y] = POZO;
            }

            // Colocar Oro

            contadorOrosRecogidos = new Vector();
            for (int j = 0; j < contadorDeOros; ++j) {
                boolean alcanzable;
                int contadorCiclos = 0;
                do {
                    x = plantaAleatoriedad(tamañoDelMapa);
                    y = plantaAleatoriedad(tamañoDelMapa);
                    contadorCiclos++;
                    alcanzable = calculaSiElOroEsAlcanzable(coordenadaX, coordenadaY, x, y, true);
                } while (casilla[x][y] != VACIO || (!alcanzable && contadorCiclos < MAXIMAOSCICLOS));
                tieneSolucion = tieneSolucion && alcanzable;
                casilla[x][y] = ORO;
                contadorOrosRecogidos.add(new Casilla(x, y));
                if (!alcanzable) System.out.println("Mundo: no se puede alcanzar el oro");
            }
            contadorDeVueltas++;
        } while (!tieneSolucion && contadorDeVueltas < MAXIMAOSCICLOS);
        if (!tieneSolucion) System.out.println("Mundo: el mapa es irresouble");

        for (int j = 0; j < contadorDeWumpus; ++j) {
            do {
                x = plantaAleatoriedad(tamañoDelMapa);
                y = plantaAleatoriedad(tamañoDelMapa);
            } while (casilla[x][y] != VACIO);
            casilla[x][y] = WUMPUS;
        }

        orosEncontrados = 0;
        contadorWumpusMuertos = 0;
        juegoActivado = true;
        mirandoA = ESTE;

        setChanged();
        notifyObservers(new Chivato(JUEGONUEVO, coordenadaX, coordenadaY, "Empieza el juego!"));
        Casilla f = getCasilla(coordenadaX, coordenadaY);

        if (f.hedor) {
            f.hedor = true;
            setChanged();
            notifyObservers(new Chivato(HEDOR, coordenadaX, coordenadaY, "Hedor"));
            log.info("sensor: Hedor");
        }

        if (f.brisa) {
            f.brisa = true;
            setChanged();
            notifyObservers(new Chivato(BRISA, coordenadaX, coordenadaY, "Brisa"));
            log.info("Sensor: Brisa");
        }

        // Mirar si el oro es alcanzable.

        boolean oroAlcanzable = false;
        for (int i = 0; i < contadorOrosRecogidos.size(); ++i) {
            Casilla v = (Casilla) contadorOrosRecogidos.get(i);
            oroAlcanzable = oroAlcanzable || calculaSiElOroEsAlcanzable(coordenadaX, coordenadaY, v.x, v.y, contadorDeFlechas > 0);
            if (oroAlcanzable) break;
        }

        if (!oroAlcanzable) {
            juegoActivado = false;
            setChanged();
            notifyObservers(new Chivato(PARTIDAGANADA, coordenadaX, coordenadaY, "Has Ganado!!"));
            log.info("Sensor: Partida Ganada");
        }
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: int
     * Descripción: devuelve la direccion hacia la que estamos mirando
     */
    public int getMirandoA() {
        return mirandoA;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: int
     * Descripción: devuelve la coordenada X de la casilla
     */
    public int getCoordenadaX() {
        return coordenadaX;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: int
     * Descripción: devuelve la coordenada Y de la casilla
     */
    public int getPosicionY() {
        return coordenadaY;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: boolean
     * Descripción: * Descripción: devuelve si el oro esta recogido
     */
    public boolean compruebaOroRecogido() {
        return oroRecogido;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: int
     * Descripción: devuelve la cantidad de oros que hemos encontrado
     */
    public int getcontadorDeOrosEncontrados() {
        return orosEncontrados;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: int
     * Descripción: Devuelve el numero de felchas restantes que tenemos en el carcaj
     */
    public int getFlechasRestantes() {
        return contadorDeFlechas;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: int
     * Descripción: Devuelve el numero de wumpus en el mapa
     */
    public int getCuantosWumpus() {
        return contadorDeWumpus;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: int
     * Descripción: Deveuelve el numero de wumpus muertos
     */
    public int getCuentaWumpusMuertos() {
        return contadorWumpusMuertos;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: int
     * Descripción: Devuelve el numero de pozos
     */
    public int getContadorDePozos() {
        return contadorDePozos;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: int
     * Descripción: Devuelve el tamaño del mapa
     */
    public int getTamañoDelMapa() {
        return tamañoDelMapa;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: boolean
     * Descripción: Decide si el juego esta activo o no
     */
    public boolean chequeaJuegoActivado() {
        return juegoActivado;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: int
     * Descripción: Devuelve el numero de movimientos restantes
     */
    public int getContadorDeMovimientos() {
        return contadorDeMovimientos;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int x int y
     * @return: Casilla
     * Descripción: Devuelve el objeto casilla requerido
     */
    public Casilla getCasilla(int x, int y) {
        Casilla casillaActual = new Casilla();
        casillaActual.x = x;
        casillaActual.y = y;
        if (x < 0 || x >= tamañoDelMapa || y < 0 || y >= tamañoDelMapa || casilla[x][y] == PARED) {
            casillaActual.contador = PARED;
            casillaActual.valida = false;
            return casillaActual;
        }
        casillaActual.valida = true;
        if (juegoActivado && casillasEscondidas[x][y]) return casillaActual;
        casillaActual.oculta = casillasEscondidas[x][y];
        casillaActual.salida = (casilla[x][y] == SALIDA);
        casillaActual.wumpus = (casilla[x][y] == WUMPUS);
        casillaActual.pozo = (casilla[x][y] == POZO);
        casillaActual.oro = (casilla[x][y] == ORO);
        casillaActual.brisa = ((x + 1 < tamañoDelMapa && casilla[x + 1][y] == POZO)
                || (x - 1 >= 0 && casilla[x - 1][y] == POZO)
                || (y + 1 < tamañoDelMapa && casilla[x][y + 1] == POZO)
                || (y - 1 >= 0 && casilla[x][y - 1] == POZO));
        casillaActual.hedor = ((x + 1 < tamañoDelMapa && casilla[x + 1][y] == WUMPUS)
                || (x - 1 >= 0 && casilla[x - 1][y] == WUMPUS)
                || (y + 1 < tamañoDelMapa && casilla[x][y + 1] == WUMPUS)
                || (y - 1 >= 0 && casilla[x][y - 1] == WUMPUS));
       /* casillaActual.resplandor = ((x + 1 < tamañoDelMapa && casilla[x + 1][y] == ORO)
                || (x - 1 >= 0 && casilla[x - 1][y] == ORO)
                || (y + 1 < tamañoDelMapa && casilla[x][y + 1] == ORO)
                || (y - 1 >= 0 && casilla[x][y - 1] == ORO));*/
        casillaActual.contador = casilla[x][y];
        return casillaActual;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: int
     * Descripción: Devuelve el coste de los movimientos
     */
    public int getCoste() {
        return coste;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int coste
     * @return: void
     * Descripción: Inicializa el coste
     */
    public void setCoste(int coste) {
        this.coste = coste;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int accionARealizar
     * @return: void
     * Descripción: Lleva a cabo el movimiento introducido
     */
    public void realizaMovimiento(int accionARealizar) {

        if (!juegoActivado) {
            if (accionARealizar == JUEGONUEVO) {
                iniciaJuego(tamañoDelMapa);
            }
            return;
        }
        if (tipoJuego != 1) {
            justificacion.introduce();
        }
        contadorDeMovimientos++;

        if (accionARealizar == GIRARIZQUIERDA) {
            setChanged();
            notifyObservers(new Chivato(MOVIMIENTOERRONEO, coordenadaX, coordenadaY, "Giro a izquierda"));
            log.info("Movimiento: Giro a la izquierda");
            setCoste(getCoste() - 1);
            if (mirandoA == NORTE) {
                mirandoA = OESTE;
            } else if (mirandoA == OESTE) {
                mirandoA = SUR;
            } else if (mirandoA == SUR) {
                mirandoA = ESTE;
            } else if (mirandoA == ESTE) {
                mirandoA = NORTE;
            }
        } else if (accionARealizar == GIRARDERECHA) {
            setChanged();
            notifyObservers(new Chivato(MOVIMIENTOERRONEO, coordenadaX, coordenadaY, "Giro a derecha"));
            log.info("Movimiento: Giro a la derecha");
            setCoste(getCoste() - 1);
            if (mirandoA == NORTE) {
                mirandoA = ESTE;
            } else if (mirandoA == OESTE) {
                mirandoA = NORTE;
            } else if (mirandoA == SUR) {
                mirandoA = OESTE;
            } else if (mirandoA == ESTE) {
                mirandoA = SUR;
            }
        } else if (accionARealizar == RECOGEORO) {
            setCoste(getCoste() + 1000);
            if (casilla[coordenadaX][coordenadaY] != ORO) {
                setChanged();
                notifyObservers(new Chivato(MOVIMIENTOERRONEO, coordenadaX, coordenadaY, "No vas Cargado"));
                return;
            } else if (oroRecogido) {
                setChanged();
                notifyObservers(new Chivato(MOVIMIENTOERRONEO, coordenadaX, coordenadaY, "No puedes cargar más"));
                return;
            } else {
                oroRecogido = true;
                casilla[coordenadaX][coordenadaY] = VACIO;
                for (int i = 0; i < contadorOrosRecogidos.size(); ++i) {
                    Casilla v = (Casilla) contadorOrosRecogidos.get(i);
                    if (v.x == coordenadaX && v.y == coordenadaY) {
                        contadorOrosRecogidos.remove(i);
                        break;
                    }
                }
                setChanged();
                notifyObservers(new Chivato(ORORECOGIDO, coordenadaX, coordenadaY, "Has cogido el oro."));
                log.info("Ha cogido el oro");
                juegoActivado = false;
                setChanged();
                notifyObservers(new Chivato(PARTIDAGANADA, coordenadaX, coordenadaY, "Has Ganado!!!"));
            }
        } else if (accionARealizar == DISPARA) {
            setCoste(getCoste() - 10);
            if (contadorDeFlechas <= 0) {
                setChanged();
                notifyObservers(new Chivato(MOVIMIENTOERRONEO, coordenadaX, coordenadaY, "Carcaj vacío!"));
                log.info("El Carcaj está vacío");
                return;
            }
            --contadorDeFlechas;
            int x = coordenadaX;
            int y = coordenadaY;
            if (mirandoA == NORTE) {
                ++y;
            } else if (mirandoA == OESTE) {
                --x;
            } else if (mirandoA == ESTE) {
                ++x;
            }
            if (x < 0 || x >= tamañoDelMapa || y < 0 || y >= tamañoDelMapa || casilla[x][y] == PARED) {
                setChanged();
                notifyObservers(new Chivato(ESTOYPERDIDO, coordenadaX, coordenadaY, "No hay nada en esa dirección"));
                log.info("Disparó pero no hay nada en esa dirección");
            } else if (casilla[x][y] == WUMPUS) {
                setCoste(getCoste() - 10);
                --contadorDeWumpus;
                ++contadorWumpusMuertos;
                casilla[x][y] = WUMPUSMUERTO;
                casillasEscondidas[x][y] = false;
                setChanged();
                notifyObservers(new Chivato(GRITO, x, y, "Grito"));
                log.info("Mató al Wumpus");
            } else {
                setChanged();
                notifyObservers(new Chivato(ESTOYPERDIDO, x, y, "No hay wumpus en esa dirección"));
            }
            if (contadorDeFlechas <= 0) {
                boolean oneReached = false;
                for (int i = 0; i < contadorOrosRecogidos.size(); ++i) {
                    Casilla v = (Casilla) contadorOrosRecogidos.get(i);
                    oneReached = oneReached || calculaSiElOroEsAlcanzable(coordenadaX, coordenadaY, v.x, v.y, false);
                    if (oneReached) break;
                }
                if (!oneReached) { // all oro is unreachable for the player
                    // so check if there would be a way, if he player had arrows
                    oneReached = false;
                    for (int i = 0; i < contadorOrosRecogidos.size(); ++i) {
                        Casilla v = (Casilla) contadorOrosRecogidos.get(i);
                        oneReached = oneReached || calculaSiElOroEsAlcanzable(coordenadaX, coordenadaY, v.x, v.y, true);
                        if (oneReached) break;
                    }
                    if (oneReached) { // yes, there would
                        juegoActivado = false;
                        setChanged();
                        notifyObservers(new Chivato(PARTIDAPERDIDA, coordenadaX, coordenadaY, "GAME OVER! No puedes ganar sin flechas"));
                    } else { // no it's really blocked, not by wumpuses
                        juegoActivado = false;
                        setChanged();
                        notifyObservers(new Chivato(PARTIDAGANADA, coordenadaX, coordenadaY, "Has Ganado!!!"));
                    }
                }
            }
        } else if (accionARealizar == AVANZAR || accionARealizar == RETROCEDER) {
            setChanged();
            notifyObservers(new Chivato(MOVIMIENTOERRONEO, coordenadaX, coordenadaY, "Avanza"));
            log.info(getContadorDeMovimientos() + " Avanza " + getCoordenadaX() + getPosicionY() + " " + getCoste() + " ");
            setCoste(getCoste() - 1);
            boolean bm = false;
            if ((accionARealizar == AVANZAR && mirandoA == NORTE) || (accionARealizar == RETROCEDER && mirandoA == SUR)) {
                if (coordenadaY + 1 < tamañoDelMapa && casilla[coordenadaX][coordenadaY + 1] != PARED) ++coordenadaY;
                else bm = true;
            } else if ((accionARealizar == AVANZAR && mirandoA == OESTE) || (accionARealizar == RETROCEDER && mirandoA == ESTE)) {
                if (coordenadaX > 0 && casilla[coordenadaX - 1][coordenadaY] != PARED) --coordenadaX;
                else bm = true;
            } else if ((accionARealizar == AVANZAR && mirandoA == SUR) || (accionARealizar == RETROCEDER && mirandoA == NORTE)) {
                if (coordenadaY > 0 && casilla[coordenadaX][coordenadaY - 1] != PARED) --coordenadaY;
                else bm = true;
            } else if ((accionARealizar == AVANZAR && mirandoA == ESTE) || (accionARealizar == RETROCEDER && mirandoA == OESTE)) {
                if (coordenadaX + 1 < tamañoDelMapa && casilla[coordenadaX + 1][coordenadaY] != PARED) ++coordenadaX;
                else bm = true;
            }

            if (bm) {
                setChanged();
                notifyObservers(new Chivato(MOVIMIENTOERRONEO, coordenadaX, coordenadaY, "Choque"));
                return;
            }
            boolean newroom = casillasEscondidas[coordenadaX][coordenadaY];
            casillasEscondidas[coordenadaX][coordenadaY] = false;
            Casilla casillaActual = getCasilla(coordenadaX, coordenadaY);
            boolean vacio = true;

            if (casillaActual.hedor && newroom) {
                casillaActual.hedor = true;
                setChanged();
                notifyObservers(new Chivato(HEDOR, coordenadaX, coordenadaY, "Hedor"));
                vacio = false;
            }

            if (casillaActual.brisa && newroom) {
                casillaActual.brisa = true;
                setChanged();
                notifyObservers(new Chivato(BRISA, coordenadaX, coordenadaY, "Brisa"));
                vacio = false;
            }

            if (casillaActual.pozo) {
                setCoste(0);
                juegoActivado = false;
                setChanged();
                notifyObservers(new Chivato(PARTIDAPERDIDA, coordenadaX, coordenadaY, "GAME OVER!!!"));
                vacio = false;
            } else if (casillaActual.wumpus) {
                setCoste(0);
                juegoActivado = false;
                setChanged();
                notifyObservers(new Chivato(PARTIDAPERDIDA, coordenadaX, coordenadaY, "GAME OVER!!"));
                vacio = false;
            } else if (casillaActual.oro && newroom) {
                setChanged();
                notifyObservers(new Chivato(OROENCONTRADO, coordenadaX, coordenadaY, "Resplandor!"));
                vacio = false;
            } else if (casillaActual.salida) {
                if (oroRecogido) {
                    oroRecogido = false;
                    contadorDeOros--;
                    orosEncontrados++;
                    setChanged();
                    notifyObservers(new Chivato(OROPUESTOASALVO, coordenadaX, coordenadaY, "Oro puesto a salvo."));
                    vacio = false;
                    boolean alcances = false;
                    for (int i = 0; i < contadorOrosRecogidos.size(); ++i) {
                        Casilla casillaRica = (Casilla) contadorOrosRecogidos.get(i);
                        alcances = alcances || calculaSiElOroEsAlcanzable(coordenadaX, coordenadaY, casillaRica.x, casillaRica.y, contadorDeFlechas > 0);
                        if (alcances) break;
                    }
                    if (!alcances) { // all oro is unreachable for the player
                        if (contadorDeFlechas > 0) { // still arrows left, so Wumpuses are not blocking the way
                            // it must be blocked by pits and/or walls. Thats not the
                            // players fault, and there is vacio he can do about it
                            juegoActivado = false;
                            setChanged();
                            notifyObservers(new Chivato(PARTIDAGANADA, coordenadaX, coordenadaY, "Has Ganado!!!"));
                        } else { // no arrows left
                            // so check if there would be a way, if he player had arrows
                            alcances = false;
                            for (int i = 0; i < contadorOrosRecogidos.size(); ++i) {
                                Casilla v = (Casilla) contadorOrosRecogidos.get(i);
                                alcances = alcances || calculaSiElOroEsAlcanzable(coordenadaX, coordenadaY, v.x, v.y, true);
                                if (alcances) break;
                            }
                            if (alcances) { // yes, there would
                                juegoActivado = false;
                                setChanged();
                                notifyObservers(new Chivato(PARTIDAPERDIDA, coordenadaX, coordenadaY, "GAME OVER! No puedes ganar sin flechas"));
                                vacio = false;
                            } else { // no it's really blocked, not by wumpuses
                                juegoActivado = false;
                                setChanged();
                                notifyObservers(new Chivato(PARTIDAGANADA, coordenadaX, coordenadaY, "Felicidades! has encontrado todos los oros posibles"));
                            }
                        }
                    }
                }
            }

            if (vacio && newroom) {
                setChanged();
                notifyObservers(new Chivato(SINOPERACION, coordenadaX, coordenadaY, "Esta sala esta vacía"));
            }
        } else {
            setChanged();
            notifyObservers(new Chivato(MOVIMIENTODESCONOCIDO, coordenadaX, coordenadaY, ""));
            return;
        }
        setChanged();
        notifyObservers(new Chivato(MOVIMIENTOCOMPLETADO, coordenadaX, coordenadaY, ""));
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: long rnd
     * @return: int
     * Descripción: Crea la semilla para darle aleatoriedad a la aplicacion
     */
    private int plantaAleatoriedad(long random) {
        return (int) Math.round((random - 1) * Math.random());
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: int desdeX, int desdeY, int haciaX, int haciaY, boolean ignorarWumpus
     * @return: boolean
     * Descripción: Realiza los calculos necesarios para determinar si el oro se encuentra en una posocion alcanzable
     * por el cazador o no.
     */
    private boolean calculaSiElOroEsAlcanzable(int desdeX, int desdeY, int haciaX, int haciaY, boolean ignorarWumpus) {
        Casilla casillaInicial = getCasilla(desdeX, desdeY);
        Casilla casillaDestino = getCasilla(haciaX, haciaY);
        if (!casillaInicial.valida || !casillaDestino.valida) {
            return false;
        }
        int tamaño = tamañoDelMapa;
        int[][] tablero = new int[tamaño][tamaño];
        for (int i = 0; i < tamaño; ++i) {
            for (int j = 0; j < tamaño; ++j) {
                tablero[i][j] = -1;
            }
        }
        tablero[haciaX][haciaY] = 0;
        Vector pasosARealizar = new Vector();
        int inicial = 0;
        pasosARealizar.add(getCasilla(haciaX, haciaY));
        boolean rutaEncontrada = false;
        while (inicial != pasosARealizar.size()) {
            Casilla casillaActual = (Casilla) (pasosARealizar.elementAt(inicial++));
            int distancia = tablero[casillaActual.x][casillaActual.y] + 1;
            if (casillaActual.x == desdeX && casillaActual.y == desdeY) {
                rutaEncontrada = true;
                break;
            }
            Casilla izquierda = getCasilla(casillaActual.x - 1, casillaActual.y);
            Casilla derecha = getCasilla(casillaActual.x + 1, casillaActual.y);
            Casilla abajo = getCasilla(casillaActual.x, casillaActual.y - 1);
            Casilla arriba = getCasilla(casillaActual.x, casillaActual.y + 1);

            if (derecha.valida && casilla[derecha.x][derecha.y] != POZO && (ignorarWumpus || casilla[derecha.x][derecha.y] != WUMPUS) && tablero[derecha.x][derecha.y] == -1) {
                tablero[derecha.x][derecha.y] = distancia;
                pasosARealizar.add(derecha);
            }
            if (arriba.valida && casilla[arriba.x][arriba.y] != POZO && (ignorarWumpus || casilla[arriba.x][arriba.y] != WUMPUS) && tablero[arriba.x][arriba.y] == -1) {
                tablero[arriba.x][arriba.y] = distancia;
                pasosARealizar.add(arriba);
            }
            if (izquierda.valida && casilla[izquierda.x][izquierda.y] != POZO && (ignorarWumpus || casilla[izquierda.x][izquierda.y] != WUMPUS) && tablero[izquierda.x][izquierda.y] == -1) {
                tablero[izquierda.x][izquierda.y] = distancia;
                pasosARealizar.add(izquierda);
            }
            if (abajo.valida && casilla[abajo.x][abajo.y] != POZO && (ignorarWumpus || casilla[abajo.x][abajo.y] != WUMPUS) && tablero[abajo.x][abajo.y] == -1) {
                tablero[abajo.x][abajo.y] = distancia;
                pasosARealizar.add(abajo);
            }
        }
        return rutaEncontrada;
    }
}