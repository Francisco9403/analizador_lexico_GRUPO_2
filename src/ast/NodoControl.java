package ast;

public class NodoControl extends NodoC {
    private String tipo;

    public NodoControl(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String getEtiqueta() {
        return tipo;
    }
}
