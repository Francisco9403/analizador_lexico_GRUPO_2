package ast;

import llvm.CodeGeneratorHelper;

public class Declaracion extends Expresion {
    private String tipo;
    private String id;

    public Declaracion(String tipo, String id) {
        this.tipo = tipo;
        this.id = id;
    }

    @Override
    public String getTipoDato() {
        return this.tipo;
    }

    public String getNombreId() {
        return this.id;
    }

    @Override
    public String getEtiqueta() {
        return "DECL\\n" + tipo + " " + id;
    }

    @Override
    public String generarCodigo() {
        // Ejemplo: INT a -> %a = alloca i32
        String tipoLLVM = CodeGeneratorHelper.mapearTipoLLVM(this.getTipoDato());
        // El puntero a la variable se llamará igual que ella (ej: %x)
        return String.format("  %%%s = alloca %s\n", this.getNombreId(), tipoLLVM);
    }
}
