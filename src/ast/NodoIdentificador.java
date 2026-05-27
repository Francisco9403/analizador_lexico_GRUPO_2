package ast;

import llvm.CodeGeneratorHelper;

public class NodoIdentificador extends Expresion {
    private final String nombre;

    public NodoIdentificador(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String getEtiqueta() {
        // Si ya tiene un tipo asignado, lo mostramos; si no, solo el nombre
        if (getTipoDato() != null && !getTipoDato().equals("UNKNOWN")) {
            return "ID: " + nombre + " (" + getTipoDato() + ")";
        }
        return "ID: " + nombre;
    }

    @Override
    public String generarCodigo() {
        this.setIrRef(CodeGeneratorHelper.getNewPointer());
        String tipoLLVM = CodeGeneratorHelper.mapearTipoLLVM(this.getTipoDato());
        // %temp = load i32, i32* %nombre
        return String.format("  %s = load %s, %s* %%%s\n",
                this.getIrRef(), tipoLLVM, tipoLLVM, this.getNombre());
    }
}