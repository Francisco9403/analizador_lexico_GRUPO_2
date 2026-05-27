package ast;

import llvm.CodeGeneratorHelper;

public class NodoOr extends OperacionBinaria {
    public NodoOr(Expresion izquierda, Expresion derecha) {
        super(izquierda, derecha);
    }

    @Override
    protected String getNombreOperacion() {
        return "OR";
    }

    @Override
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder();

        codigo.append(this.izquierda.generarCodigo());
        codigo.append(this.derecha.generarCodigo());

        this.setIrRef(CodeGeneratorHelper.getNewPointer());

        // En LLVM: %reg = or i1 %izq, %der
        codigo.append(String.format("  %s = or i1 %s, %s\n",
                this.getIrRef(), this.izquierda.getIrRef(), this.derecha.getIrRef()));

        return codigo.toString();
    }
}