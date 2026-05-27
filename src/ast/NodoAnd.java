package ast;

import llvm.CodeGeneratorHelper;

public class NodoAnd extends OperacionBinaria {
    public NodoAnd(Expresion izquierda, Expresion derecha) {
        super(izquierda, derecha);
    }

    @Override
    protected String getNombreOperacion() {
        return "AND";
    }

    @Override
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder();

        codigo.append(this.izquierda.generarCodigo());
        codigo.append(this.derecha.generarCodigo());

        this.setIrRef(CodeGeneratorHelper.getNewPointer());

        // En LLVM: %reg = and i1 %izq, %der
        codigo.append(String.format("  %s = and i1 %s, %s\n",
                this.getIrRef(), this.izquierda.getIrRef(), this.derecha.getIrRef()));

        return codigo.toString();
    }
}