package llvm;

import java.util.Stack;

public class CodeGeneratorHelper {
    private static int nextID = 0;
    private static int labelID = 0;
    private static Stack<String> loopStartLabels = new Stack<>();
    private static Stack<String> loopEndLabels = new Stack<>();
    private static java.util.Map<String, String> globalStrings = new java.util.LinkedHashMap<>();
    private static int stringCount = 0;

    private CodeGeneratorHelper(){}

    public static String getNewPointer(){
        nextID += 1;
        return "%" + nextID; // Ej: %1, %2, %3... (más estándar en LLVM)
    }

    public static String getNewLabel(){
        labelID += 1;
        return "label_" + labelID;
    }


    public static void enterLoop(String startLabel, String endLabel) {
        loopStartLabels.push(startLabel);
        loopEndLabels.push(endLabel);
    }

    public static void exitLoop() {
        loopStartLabels.pop();
        loopEndLabels.pop();
    }

    public static String getCurrentLoopStart() {
        return loopStartLabels.isEmpty() ? "error_no_loop" : loopStartLabels.peek();
    }

    public static String getCurrentLoopEnd() {
        return loopEndLabels.isEmpty() ? "error_no_loop" : loopEndLabels.peek();
    }

    // Podés agregar un reseteo por si querés compilar múltiples veces en la misma corrida
    //public static void reset() {
    //    nextID = 0;
    //    labelID = 0;
    //}

    public static String addGlobalString(String texto) {
        String ref = "@.str.custom" + stringCount++;
        // Limpiamos comillas si vienen del parser y agregamos salto de línea LLVM
        String cleanText = texto.replace("\"", "") + "\\0A\\00";
        // Calculamos longitud real en bytes
        int len = texto.replace("\"", "").length() + 2;

        String definicion = String.format("%s = private constant [%d x i8] c\"%s\"", ref, len, cleanText);
        globalStrings.put(ref, definicion);

        return ref + "||" + len; // Devolvemos ref y tamaño empaquetados
    }

    public static String getGlobalStringsDef() {
        StringBuilder sb = new StringBuilder();
        for (String def : globalStrings.values()) {
            sb.append(def).append("\n");
        }
        return sb.toString();
    }

    // Acordate de vaciar las pilas en tu método reset()
    public static void reset() {
        nextID = 0;
        labelID = 0;
        loopStartLabels.clear();
        loopEndLabels.clear();
        globalStrings.clear();
        stringCount = 0;
    }

    public static String mapearTipoLLVM(String tipoDato) {
        if ("FLOAT".equals(tipoDato)) return "double";
        if ("BOOL".equals(tipoDato)) return "i1";
        if (tipoDato != null && tipoDato.startsWith("FLOAT_ARRAY")) return "double*";
        return "i32"; // Para INT (y por defecto)
    }
}