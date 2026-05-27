package ast;

import llvm.CodeGeneratorHelper;

import java.util.ArrayList;
import java.util.List;

public class NodoLlamada extends Expresion {
    private final String nombreFuncion;
    private final List<NodoC> argumentos;

    public NodoLlamada(String nombreFuncion, List<NodoC> argumentos) {
        this.nombreFuncion = nombreFuncion;
        this.argumentos = argumentos != null ? argumentos : new ArrayList<>();
    }

    @Override
    public String getEtiqueta() {
        if (getTipoDato() != null && !getTipoDato().equals("UNKNOWN")) {
            return "Llamada: " + nombreFuncion + " (" + getTipoDato() + ")";
        }
        return "Llamada: " + nombreFuncion;
    }

    @Override
    public List<NodoC> getHijos() {
        return new ArrayList<>(argumentos);
    }

    @Override
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder();
        StringBuilder argsLLVM = new StringBuilder();

        // 1. Evaluar todos los argumentos primero
        for (int i = 0; i < argumentos.size(); i++) {
            Expresion arg = (Expresion) argumentos.get(i);
            codigo.append(arg.generarCodigo());

            String tipoLLVM = CodeGeneratorHelper.mapearTipoLLVM(arg.getTipoDato());
            argsLLVM.append(tipoLLVM).append(" ").append(arg.getIrRef());

            if (i < argumentos.size() - 1) {
                argsLLVM.append(", ");
            }
        }

        // 2. Pedir registro para el valor de retorno
        this.setIrRef(CodeGeneratorHelper.getNewPointer());
        String tipoRetornoLLVM = CodeGeneratorHelper.mapearTipoLLVM(this.getTipoDato());

        // 3. Emitir la llamada (Ej: %3 = call double @suma_cumulativa(i32 %1, double* %2))
        codigo.append(String.format("  %s = call %s @%s(%s)\n",
                this.getIrRef(), tipoRetornoLLVM, nombreFuncion, argsLLVM.toString()));

        return codigo.toString();
    }
}