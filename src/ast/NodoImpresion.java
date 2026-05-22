package ast;
import java.util.List;
import java.util.ArrayList;

public class NodoImpresion extends NodoC {
    private final String stringOpcional;
    private final NodoC expresion;

    // Constructor para: PRINT(expresion)
    public NodoImpresion(NodoC expresion) {
        this.stringOpcional = null;
        this.expresion = expresion;
    }

    // Constructor para: PRINT("string", expresion)
    public NodoImpresion(String stringOpcional, NodoC expresion) {
        this.stringOpcional = stringOpcional;
        this.expresion = expresion;
    }

    @Override
    public String getEtiqueta() {
        return stringOpcional != null ? "PRINT(" + stringOpcional + ", ...)" : "PRINT";
    }

    @Override
    public List<NodoC> getHijos() {
        return List.of(expresion);
    }
}