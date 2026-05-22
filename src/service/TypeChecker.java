package service;

public class TypeChecker {

    // A = B
    public static boolean esAsignacionValida(String tipoLadoIzquierdo, String tipoLadoDerecho) {
        switch (tipoLadoIzquierdo) {
            case "INT" -> {
                return tipoLadoDerecho.equals("INT");
            }
            case "FLOAT" -> {
                return tipoLadoDerecho.equals("INT") || tipoLadoDerecho.equals("FLOAT");
            }
            case "BOOL" -> {
                return tipoLadoDerecho.equals("BOOL");
            }
        }
        if (tipoLadoIzquierdo.startsWith("FLOAT_ARRAY")) {
            if (tipoLadoDerecho.equals("INT") || tipoLadoDerecho.equals("FLOAT")) {
                return true; // Se setea el valor en todos los componentes
            }
            return tipoLadoDerecho.startsWith("FLOAT_ARRAY");
        }
        return false;
    }

    // Para operaciones aritméticas (+, -, *, /)
    public static String getTipoResultadoOperacion(String tipoIzq, String tipoDer) {
        if (tipoIzq.equals("INT") && tipoDer.equals("INT")) return "INT";
        if (tipoIzq.equals("FLOAT") && tipoDer.equals("INT")) return "FLOAT";
        if (tipoIzq.equals("INT") && tipoDer.equals("FLOAT")) return "FLOAT";
        if (tipoIzq.equals("FLOAT") && tipoDer.equals("FLOAT")) return "FLOAT";

        // Interoperabilidad de arreglos con escalares
        if (tipoIzq.startsWith("FLOAT_ARRAY") && (tipoDer.equals("INT") || tipoDer.equals("FLOAT"))) return tipoIzq;
        if ((tipoIzq.equals("INT") || tipoIzq.equals("FLOAT")) && tipoDer.startsWith("FLOAT_ARRAY")) return tipoDer;
        if (tipoIzq.startsWith("FLOAT_ARRAY") && tipoDer.startsWith("FLOAT_ARRAY")) {
            if (tipoIzq.equals(tipoDer)) return tipoIzq;
        }

        return "ERROR";
    }
}