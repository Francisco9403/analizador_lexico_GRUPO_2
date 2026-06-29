package ast;

import llvm.CodeGeneratorHelper;

public class Control extends Nodo {
    private String tipoControl; // "BREAK" o "CONTINUE"

    public Control(String tipoControl) {
        this.tipoControl = tipoControl;
    }

    @Override
    public String getEtiqueta() {
        return tipoControl;
    }

    @Override
    public String generarCodigo() {
        if (tipoControl.equals("BREAK")) {
            String endLabel = CodeGeneratorHelper.getCurrentLoopEnd();
            // Validación semántica:
            if (endLabel.equals("error_no_loop")) {
                throw new RuntimeException("Error Semántico: Se encontró un BREAK fuera de un ciclo.");
            }
            return String.format("  br label %%%s\n", endLabel);

        } else if (tipoControl.equals("CONTINUE")) {
            String startLabel = CodeGeneratorHelper.getCurrentLoopStart();
            // Validación semántica:
            if (startLabel.equals("error_no_loop")) {
                throw new RuntimeException("Error Semántico: Se encontró un CONTINUE fuera de un ciclo.");
            }
            return String.format("  br label %%%s\n", startLabel);
        }
        return "";
    }
}