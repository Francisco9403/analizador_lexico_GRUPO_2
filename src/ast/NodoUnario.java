package ast;

import llvm.CodeGeneratorHelper;
import java.util.Collections;
import java.util.List;

public class NodoUnario extends NodoC {
    private final String operador;
    private final NodoC nodo;

    public NodoUnario(String operador, NodoC nodo) {
        this.operador = operador;
        this.nodo = nodo;
    }

    @Override
    public String getEtiqueta() {
        return "Operación Unaria: " + operador;
    }

    @Override
    public List<NodoC> getHijos() {
        return Collections.singletonList(nodo);
    }

    @Override
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder();

        // Generar el código de la expresión interna
        codigo.append(nodo.generarCodigo());

        this.setIrRef(CodeGeneratorHelper.getNewPointer());
        boolean esFloat = "FLOAT".equals(this.getTipoDato());

        if ("-".equals(operador)) {
            if (esFloat) {
                // Negación en punto flotante: fneg double %reg
                codigo.append(String.format("  %s = fneg double %s\n", this.getIrRef(), nodo.getIrRef()));
            } else {
                // Negación entera: restando de 0 (sub i32 0, %reg)
                codigo.append(String.format("  %s = sub i32 0, %s\n", this.getIrRef(), nodo.getIrRef()));
            }
        } else if ("!".equals(operador) || "not".equalsIgnoreCase(operador)) {
            // Negación lógica en booleano i1 haciendo XOR contra 'true'
            codigo.append(String.format("  %s = xor i1 %s, true\n", this.getIrRef(), nodo.getIrRef()));
        }

        return codigo.toString();
    }
}