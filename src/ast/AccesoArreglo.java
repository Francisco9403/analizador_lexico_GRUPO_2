package ast;

import llvm.CodeGeneratorHelper;
import java.util.List;

public class AccesoArreglo extends Expresion {
    private String id;
    private Expresion indice;

    public AccesoArreglo(String id, Expresion indice) {
        this.id = id;
        this.indice = indice;
    }

    @Override
    public String getEtiqueta() {
        return "ACCESO\\n" + id + "[]";
    }

    @Override
    public List<Nodo> getHijos() {
        return List.of(indice);
    }

    @Override
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder();

        codigo.append(indice.generarCodigo());
        String tipoLLVM = "FLOAT".equals(this.getTipoDato()) ? "double" : "i32";

        String tipoDeclarado = Parser.Parser.tablaSimbolos.getType(id);
        int tamanoArreglo = 0;

        // --- EXTRACCIÓN SEGURA Y ROBUSTA ---
        try {
            if (tipoDeclarado != null && tipoDeclarado.contains("[") && tipoDeclarado.contains("]")) {
                int inicioCorchete = tipoDeclarado.indexOf('[') + 1;
                int finCorchete = tipoDeclarado.indexOf(']');
                String sub = tipoDeclarado.substring(inicioCorchete, finCorchete);
                tamanoArreglo = Integer.parseInt(sub.trim());
            }
        } catch (Exception e) {
            tamanoArreglo = 999999;
        }

        // --- INYECCIÓN DE BOUNDS CHECKING ---
        String labelOk = "idx_ok_" + CodeGeneratorHelper.getNewLabel();
        String labelError = "idx_err_" + CodeGeneratorHelper.getNewLabel();
        String regCond = CodeGeneratorHelper.getNewPointer();

        codigo.append(String.format("  %s = icmp slt i32 %s, %d\n", regCond, indice.getIrRef(), tamanoArreglo));
        codigo.append(String.format("  br i1 %s, label %%%s, label %%%s\n", regCond, labelOk, labelError));

        // -- BLOQUE DE ERROR (Abortar) --
        codigo.append(String.format("\n%s:\n", labelError));
        codigo.append("  call void @exit(i32 1)\n");
        codigo.append("  unreachable\n");

        // -- BLOQUE OK (Continuar normal) --
        codigo.append(String.format("\n%s:\n", labelOk));
        // ------------------------------------

        String basePtr = CodeGeneratorHelper.getNewPointer();
        codigo.append(String.format("  %s = load double*, double** %%%s\n", basePtr, id));

        String elemPtr = CodeGeneratorHelper.getNewPointer();
        codigo.append(String.format("  %s = getelementptr %s, %s* %s, i32 %s\n",
                elemPtr, tipoLLVM, tipoLLVM, basePtr, indice.getIrRef()));

        this.setIrRef(CodeGeneratorHelper.getNewPointer());
        codigo.append(String.format("  %s = load %s, %s* %s\n",
                this.getIrRef(), tipoLLVM, tipoLLVM, elemPtr));

        return codigo.toString();
    }
}