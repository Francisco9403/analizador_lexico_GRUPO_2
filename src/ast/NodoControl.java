package ast;

import llvm.CodeGeneratorHelper;

public class NodoControl extends NodoC {
    private String tipoControl; // "BREAK" o "CONTINUE"

    public NodoControl(String tipoControl) {
        this.tipoControl = tipoControl;
    }

    @Override
    public String getEtiqueta() {
        return tipoControl;
    }

    @Override
    public String generarCodigo() {
        if (tipoControl.equals("BREAK")) {
            // Salta al final del ciclo
            return String.format("  br label %%%s\n", CodeGeneratorHelper.getCurrentLoopEnd());
        } else if (tipoControl.equals("CONTINUE")) {
            // Salta a la evaluación de la condición del ciclo
            return String.format("  br label %%%s\n", CodeGeneratorHelper.getCurrentLoopStart());
        }
        return "";
    }
}
