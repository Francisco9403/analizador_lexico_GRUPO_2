package ast;

import llvm.CodeGeneratorHelper;
import java.util.List;

public class Asignacion extends Expresion {
    private Expresion identificador;
    private Expresion expresion;

    public Asignacion(Expresion identificador, Expresion expresion) {
        this.identificador = identificador;
        this.expresion = expresion;
    }

    @Override
    public String getEtiqueta() {
        return "ASIG";
    }

    @Override
    public List<Nodo> getHijos() {
        return List.of(identificador, expresion);
    }

    @Override
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder();
        codigo.append(this.expresion.generarCodigo());

        // 1. Busca el tipo de dato real en el identificador o en la expresión
        String tipoDato = this.getTipoDato();
        if (tipoDato == null || tipoDato.equals("UNKNOWN")) {
            tipoDato = this.identificador.getTipoDato();
        }
        if (tipoDato == null || tipoDato.equals("UNKNOWN")) {
            tipoDato = this.expresion.getTipoDato();
        }

        String tipoLLVM = CodeGeneratorHelper.mapearTipoLLVM(tipoDato);

        if (this.identificador instanceof Identificador) {
            String nombreVar = ((Identificador)this.identificador).getNombre();

            // 2. Si lo que se asigna es un arreglo, forzamos el uso de punteros ('ptr')
            if (this.expresion instanceof Arreglo) {
                codigo.append(String.format("  store ptr %s, ptr %%%s\n",
                        this.expresion.getIrRef(), nombreVar));
            } else {
                codigo.append(String.format("  store %s %s, %s* %%%s\n",
                        tipoLLVM, this.expresion.getIrRef(), tipoLLVM, nombreVar));
            }
        } else if (this.identificador instanceof AccesoArreglo) {
        }

        return codigo.toString();
    }
}