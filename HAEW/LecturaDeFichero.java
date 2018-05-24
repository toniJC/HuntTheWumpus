package HAEW;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

public class LecturaDeFichero extends javax.swing.filechooser.FileFilter {

    private static String TYPE_UNKNOWN = "Type Unknown";
    private static String HIDDEN_FILE = "Hidden File";

    private Hashtable filtros = null;
    private String descripcion = null;
    private String descripcionCompleta = null;
    private boolean extensionesDeLaDescripcion = true;

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: N/A
     * Descripción: Crea un filtro de archivo. Si no se agregan filtros,todos los archivos son aceptados
     */
    public LecturaDeFichero() {
        this.filtros = new Hashtable();
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: String extension
     * @return: N/A
     * Descripción: Crea un filtro de archivos que acepta archivos con la extensión dada.
     */
    public LecturaDeFichero(String extension) {
        this(extension, null);
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: String extension, string descripcion
     * @return: N/A
     * Descripción: Crea un filtro de archivo que acepta el tipo de archivo dado.
     */
    public LecturaDeFichero(String extension, String descripcion) {
        this();
        if (extension != null) addExtension(extension);
        if (descripcion != null) setDescripcion(descripcion);
    }

    /**
     *
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: File fichero
     * @return: boolean
     * Descripción: Devuelve verdadero si este archivo debe mostrarse en el panel de directorio, falso si no debería.
     * Los archivos que comienzan con "." son ignorados
     */
    public boolean accept(File fichero) {
        if (fichero != null) {
            if (fichero.isDirectory()) {
                return true;
            }
            String extension = getExtension(fichero);
            if (extension != null && filtros.get(getExtension(fichero)) != null) {
                return true;
            }
            ;
        }
        return false;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: File fichero
     * @return: String
     * Descripción: Devuelve la porción de extensión del nombre del archivo.
     */
    public String getExtension(File fichero) {
        if (fichero != null) {
            String nombreDelFichero = fichero.getName();
            int i = nombreDelFichero.lastIndexOf('.');
            if (i > 0 && i < nombreDelFichero.length() - 1) {
                return nombreDelFichero.substring(i + 1).toLowerCase();
            }
            ;
        }
        return null;
    }

    /**
     *
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: String extension
     * @return: void
     * Descripción:  Agrega una extensión de "punto" de tipo de archivo para filtrar.
     */

    public void addExtension(String extension) {
        if (filtros == null) {
            filtros = new Hashtable(5);
        }
        filtros.put(extension.toLowerCase(), this);
        descripcionCompleta = null;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: String
     * Descripción: Devuelve la descripción legible por humanos de este filtro.
     */
    public String getDescription() {
        if (descripcionCompleta == null) {
            if (descripcion == null || isExtensionListInDescription()) {
                descripcionCompleta = descripcion == null ? "(" : descripcion + " (";
                // build the descripcion from the extension list
                Enumeration extensiones = filtros.keys();
                if (extensiones != null) {
                    descripcionCompleta += "." + (String) extensiones.nextElement();
                    while (extensiones.hasMoreElements()) {
                        descripcionCompleta += ", " + (String) extensiones.nextElement();
                    }
                }
                descripcionCompleta += ")";
            } else {
                descripcionCompleta = descripcion;
            }
        }
        return descripcionCompleta;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: String descripcion
     * @return: void
     * Descripción: Establece la descripción legible para el ser humano de este filtro.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
        descripcionCompleta = null;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: boolean b
     * @return: void
     * Descripción: Determina si la lista de extensiones debería aparecer en la descripción legible.
     */
    public void setExtensionListInDescription(boolean b) {
        extensionesDeLaDescripcion = b;
        descripcionCompleta = null;
    }

    /**
     * @author: Antonio Junquera Criado
     * @verion: 1.0
     * @param: N/A
     * @return: void
     * Descripción: Determina si la descripcion esta en la lista de extensiones.
     */
    public boolean isExtensionListInDescription() {
        return extensionesDeLaDescripcion;
    }
}
