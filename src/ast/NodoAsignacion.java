package ast;

import llvm.CodeGeneratorHelper;

import java.util.List;

public class NodoAsignacion extends NodoC {
    private NodoC identificador;
    private NodoC expresion;

    public NodoAsignacion(NodoC identificador, NodoC expresion) {
        this.identificador = identificador;
        this.expresion = expresion;
    }

    @Override
    public String getEtiqueta() {
        return "ASIG";
    }

    @Override
    public List<NodoC> getHijos() {
        return List.of(identificador, expresion);
    }

    @Override
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder();
        codigo.append(this.expresion.generarCodigo());

        String tipoLLVM = CodeGeneratorHelper.mapearTipoLLVM(this.getTipoDato());

        if (this.identificador instanceof NodoIdentificador) {
            String nombreVar = ((NodoIdentificador)this.identificador).getNombre();
            codigo.append(String.format("  store %s %s, %s* %%%s\n",
                    tipoLLVM, this.expresion.getIrRef(), tipoLLVM, nombreVar));
        } else if (this.identificador instanceof NodoAccesoArreglo) {
            // Acá va a ir la lógica para obtener el puntero del elemento del arreglo (getelementptr)
            // y hacerle el store a ese puntero intermedio.
        }

        return codigo.toString();
    }
}
