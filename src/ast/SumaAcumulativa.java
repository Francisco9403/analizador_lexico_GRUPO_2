package ast;

import llvm.CodeGeneratorHelper;

import java.util.ArrayList;
import java.util.List;

public class SumaAcumulativa extends Expresion {

    private Expresion limite;
    private Expresion arreglo;

    private static int contadorSumaAcumulativa = 0;
    private final int idSuma;

    private Asignacion asigSuma;
    private Asignacion asigIndice;
    private While bucleWhile;

    private String nombreVarSum;
    private String nombreVarIndice;

    public SumaAcumulativa(Expresion limite, Expresion arreglo, String nombreArreglo) {
        this.limite = limite;
        this.arreglo = arreglo;
        this.setTipoDato("FLOAT");

        this.idSuma = ++contadorSumaAcumulativa;

        this.nombreVarSum = "_sum_temp_" + this.idSuma;
        this.nombreVarIndice = "_i_temp_" + this.idSuma;

        Identificador idSum1 = new Identificador(this.nombreVarSum); idSum1.setTipoDato("FLOAT");
        Constante ceroFloat = new Constante("0.0"); ceroFloat.setTipoDato("FLOAT");
        this.asigSuma = new Asignacion(idSum1, ceroFloat);

        Identificador idIndice1 = new Identificador(this.nombreVarIndice); idIndice1.setTipoDato("INT");
        Constante ceroInt = new Constante("0"); ceroInt.setTipoDato("INT");
        this.asigIndice = new Asignacion(idIndice1, ceroInt);

        Identificador idIndice2 = new Identificador(this.nombreVarIndice); idIndice2.setTipoDato("INT");
        OperacionComparar condicion = new OperacionComparar("<", idIndice2, limite); condicion.setTipoDato("BOOL");

        Identificador idIndice3 = new Identificador(this.nombreVarIndice); idIndice3.setTipoDato("INT");
        AccesoArreglo acceso = new AccesoArreglo(nombreArreglo, idIndice3); acceso.setTipoDato("FLOAT");

        Identificador idSum2 = new Identificador(this.nombreVarSum); idSum2.setTipoDato("FLOAT");
        OperacionAritmetica suma = new OperacionAritmetica("+", idSum2, acceso); suma.setTipoDato("FLOAT");

        Identificador idSum3 = new Identificador(this.nombreVarSum); idSum3.setTipoDato("FLOAT");
        Asignacion asigAcumular = new Asignacion(idSum3, suma);

        Identificador idIndice4 = new Identificador(this.nombreVarIndice); idIndice4.setTipoDato("INT");
        Constante unoInt = new Constante("1"); unoInt.setTipoDato("INT");
        OperacionAritmetica incremento = new OperacionAritmetica("+", idIndice4, unoInt); incremento.setTipoDato("INT");

        Identificador idIndice5 = new Identificador(this.nombreVarIndice); idIndice5.setTipoDato("INT");
        Asignacion asigIncrementar = new Asignacion(idIndice5, incremento);

        List<Nodo> bloqueWhile = new ArrayList<>();
        bloqueWhile.add(asigAcumular);
        bloqueWhile.add(asigIncrementar);

        this.bucleWhile = new While(condicion, bloqueWhile, new ArrayList<>());
    }

    @Override
    public String getEtiqueta() {
        return "Suma Acumulativa";
    }

    @Override
    public List<Nodo> getHijos() {
        return List.of(asigSuma, asigIndice, bucleWhile);
    }

    @Override
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder();

        codigo.append(arreglo.generarCodigo());
        codigo.append(limite.generarCodigo());

        // --- VALIDACIÓN DE LÍMITES (Bounds Checking Dinámico) ---
        String tipoArreglo = arreglo.getTipoDato();
        int tamanoArreglo = 999999;
        if (tipoArreglo != null && tipoArreglo.contains("[") && tipoArreglo.contains("]")) {
            int inicio = tipoArreglo.indexOf('[') + 1;
            int fin = tipoArreglo.indexOf(']');
            tamanoArreglo = Integer.parseInt(tipoArreglo.substring(inicio, fin));
        }

        String labelOk = "lim_ok_" + CodeGeneratorHelper.getNewLabel();
        String labelError = "lim_err_" + CodeGeneratorHelper.getNewLabel();
        String regCond = CodeGeneratorHelper.getNewPointer();

        codigo.append(String.format("  %s = icmp sle i32 %s, %d\n", regCond, limite.getIrRef(), tamanoArreglo));
        codigo.append(String.format("  br i1 %s, label %%%s, label %%%s\n", regCond, labelOk, labelError));

        codigo.append(String.format("\n%s:\n", labelError));
        codigo.append("  call void @exit(i32 1)\n");
        codigo.append("  unreachable\n");

        codigo.append(String.format("\n%s:\n", labelOk));
        // --------------------------------------------------------

        String varSum = "%_sum_temp_" + this.idSuma;
        String varIdx = "%_i_temp_" + this.idSuma;

        codigo.append(String.format("  %s = alloca double\n", varSum));
        codigo.append(String.format("  %s = alloca i32\n", varIdx));
        codigo.append(asigSuma.generarCodigo());
        codigo.append(asigIndice.generarCodigo());
        codigo.append(bucleWhile.generarCodigo());

        this.setIrRef(CodeGeneratorHelper.getNewPointer());
        codigo.append(String.format("  %s = load double, double* %s\n", this.getIrRef(), varSum));

        return codigo.toString();
    }
}