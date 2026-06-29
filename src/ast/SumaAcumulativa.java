package ast;

import llvm.CodeGeneratorHelper;

import java.util.ArrayList;
import java.util.List;

public class SumaAcumulativa extends Expresion {

    private Expresion limite;
    private Expresion arreglo;

    // 1. Contador global para TODAS las instancias de SumaAcumulativa
    private static int contadorSumaAcumulativa = 0;

    // 2. ID único para ESTA instancia en particular
    private final int idSuma;

    // Nodos sintéticos (Los hijos que arman el árbol del pizarrón)
    private Asignacion asigSuma;
    private Asignacion asigIndice;
    private While bucleWhile;

    // Nombres únicos para LLVM
    private String nombreVarSum;
    private String nombreVarIndice;

    // Pasamos el ID del arreglo como String porque NodoAccesoArreglo lo necesita así
    public SumaAcumulativa(Expresion limite, Expresion arreglo, String nombreArreglo) {
        this.limite = limite;
        this.arreglo = arreglo;
        this.setTipoDato("FLOAT");

        this.idSuma = ++contadorSumaAcumulativa;

        // --- ARREGLO: Variables con nombre único para LLVM ---
        this.nombreVarSum = "_sum_temp_" + this.idSuma;
        this.nombreVarIndice = "_i_temp_" + this.idSuma;

        // a) sumatoria = 0.0
        Identificador idSum1 = new Identificador(this.nombreVarSum); idSum1.setTipoDato("FLOAT");
        Constante ceroFloat = new Constante("0.0"); ceroFloat.setTipoDato("FLOAT");
        this.asigSuma = new Asignacion(idSum1, ceroFloat);

        // b) indice = 0
        Identificador idIndice1 = new Identificador(this.nombreVarIndice); idIndice1.setTipoDato("INT");
        Constante ceroInt = new Constante("0"); ceroInt.setTipoDato("INT");
        this.asigIndice = new Asignacion(idIndice1, ceroInt);

        // c) Condición del While: indice < limite
        Identificador idIndice2 = new Identificador(this.nombreVarIndice); idIndice2.setTipoDato("INT");
        OperacionComparar condicion = new OperacionComparar("<", idIndice2, limite); condicion.setTipoDato("BOOL");

        // d) Cuerpo del While: sumatoria = sumatoria + arreglo[indice]
        Identificador idIndice3 = new Identificador(this.nombreVarIndice); idIndice3.setTipoDato("INT");
        AccesoArreglo acceso = new AccesoArreglo(nombreArreglo, idIndice3); acceso.setTipoDato("FLOAT");

        Identificador idSum2 = new Identificador(this.nombreVarSum); idSum2.setTipoDato("FLOAT");
        OperacionAritmetica suma = new OperacionAritmetica("+", idSum2, acceso); suma.setTipoDato("FLOAT");

        Identificador idSum3 = new Identificador(this.nombreVarSum); idSum3.setTipoDato("FLOAT");
        Asignacion asigAcumular = new Asignacion(idSum3, suma);

        // e) Incremento: indice = indice + 1
        Identificador idIndice4 = new Identificador(this.nombreVarIndice); idIndice4.setTipoDato("INT");
        Constante unoInt = new Constante("1"); unoInt.setTipoDato("INT");
        OperacionAritmetica incremento = new OperacionAritmetica("+", idIndice4, unoInt); incremento.setTipoDato("INT");

        Identificador idIndice5 = new Identificador(this.nombreVarIndice); idIndice5.setTipoDato("INT");
        Asignacion asigIncrementar = new Asignacion(idIndice5, incremento);

        // f) Armar el bloque del While
        List<Nodo> bloqueWhile = new ArrayList<>();
        bloqueWhile.add(asigAcumular);
        bloqueWhile.add(asigIncrementar);

        // g) Crear el Nodo While final
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

        // (Si es una variable normal, esto se ignora. Si es un literal [10, 20], lo crea en LLVM).
        codigo.append(arreglo.generarCodigo());

        // Reemplazar los nombres fijos por nombres dinámicos únicos
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