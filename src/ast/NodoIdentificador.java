package ast;

public class NodoIdentificador extends Expresion {
    private final String nombre;

    public NodoIdentificador(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String getEtiqueta() {
        // Si ya tiene un tipo asignado, lo mostramos; si no, solo el nombre
        if (getTipoDato() != null && !getTipoDato().equals("UNKNOWN")) {
            return "ID: " + nombre + " (" + getTipoDato() + ")";
        }
        return "ID: " + nombre;
    }
}