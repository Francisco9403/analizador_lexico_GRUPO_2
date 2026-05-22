package ast;

public class NodoDeclaracion extends NodoC {
    private String tipo;
    private String id;

    public NodoDeclaracion(String tipo, String id) {
        this.tipo = tipo;
        this.id = id;
    }

    @Override
    public String getEtiqueta() {
        return "DECL\\n" + tipo + " " + id;
    }
}
