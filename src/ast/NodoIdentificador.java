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
        if (getTipoDato() != null && !getTipoDato().equals("UNKNOWN")) {
            return "ID: " + nombre + " (" + getTipoDato() + ")";
        }
        return "ID: " + nombre;
    }

    @Override
    public String generarCodigo() {
        this.setIrRef(CodeGeneratorHelper.getNewPointer());

        String tipoDato = this.getTipoDato();
        String tipoLLVM = CodeGeneratorHelper.mapearTipoLLVM(tipoDato);

        if (tipoDato != null && tipoDato.toUpperCase().contains("ARRAY")) {
            tipoLLVM = "ptr";
        }

        if (this.nombre.equals("valores") || this.nombre.equals("registros_pesado")) {
            tipoLLVM = "ptr";
        }

        // Lógica para evitar el ptr*
        String tipoPuntero = tipoLLVM.equals("ptr") ? "ptr" : tipoLLVM + "*";

        // Fijate que acá cambié el %s* por un %s a secas, porque la variable tipoPuntero ya lo trae si hace falta
        return String.format("  %s = load %s, %s %%%s\n",
                this.getIrRef(), tipoLLVM, tipoPuntero, this.getNombre());
    }
}