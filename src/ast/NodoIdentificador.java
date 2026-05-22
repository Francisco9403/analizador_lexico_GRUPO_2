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
        return "ID: " + nombre;
    }
}