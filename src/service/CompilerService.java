package service;
import Parser.Parser;
import Parser.CupScannerAdapter;
import Lexer.Lexer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import Lexer.Token;
import Exception.SyntaxException;
import java_cup.runtime.Symbol;
import ast.Programa;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * Orquesta analisis lexico y sintactico para CLI y GUI.
 */
public class CompilerService {

    // Ejecuta to do el proceso del compilador paso a paso.
    // Analiza las palabras, revisa la gramática, y crea todos los archivos finales
    // (el código LLVM, la imagen del árbol AST y la tabla de símbolos).
    public String compileSource(String sourceCode) {
        StringBuilder out = new StringBuilder();

        try {
            // Antes de parsear, reseteamos la tabla para que no arrastre datos viejos.
            Parser.tablaSimbolos.clear();
            llvm.CodeGeneratorHelper.reset();
            List<Token> tokens = lex(sourceCode);

            out.append("=== ANALISIS LEXICO ===\n");
            for (Token token : tokens) {
                out.append(token).append('\n');
            }

            out.append("\n=== ANALISIS SINTACTICO ===\n");
            CupScannerAdapter scanner = new CupScannerAdapter(tokens);
            Parser parser = new Parser(scanner);
            Symbol result = parser.parse();

            out.append("\n=== ACCIONES SEMÁNTICAS (ORDEN DE REDUCCIÓN) ===\n");
            List<String> logs = parser.getLogs();
            for (String log : logs) {
                out.append(log).append("\n");
            }

            java.nio.file.Files.write(java.nio.file.Path.of("logs_reglas.txt"), logs);
            out.append("\n[OK] Logs guardados en logs_reglas.txt\n");

            // GENERACIÓN DEL AST, ARCHIVO .DOT Y .PNG ---
            if (result != null && result.value != null) {
                Programa raiz = (Programa) result.value;
                String dotCode = raiz.generarGrafoDot();
                java.nio.file.Files.writeString(java.nio.file.Path.of("arbol_ast.dot"), dotCode);
                out.append("[OK] Árbol AST (Graphviz) generado en arbol_ast.dot\n");

                // --- GENERAR PNG AUTOMÁTICAMENTE ---
                try {
                    // Ejecutamos Graphviz desde la consola del sistema operativo
                    ProcessBuilder pbDot = new ProcessBuilder("dot", "-Tpng", "arbol_ast.dot", "-o", "arbol_ast.png");
                    Process p = pbDot.start();
                    p.waitFor(); // Esperamos a que termine de dibujar
                    out.append("[OK] Imagen generada con éxito en arbol_ast.png\n");
                } catch (Exception ex) {
                    out.append("[AVISO] No se pudo generar el PNG. Asegúrese de tener Graphviz instalado y en el PATH.\n");
                }

                String llvmCode = raiz.generarCodigo();
                java.nio.file.Files.writeString(java.nio.file.Path.of("programa.ll"), llvmCode);
                out.append("[OK] Código LLVM IR generado en programa.ll\n");
                String rutaExe = generarEjecutable("programa.ll");
                if (rutaExe != null) {
                    out.append("[OK] ¡Ejecutable generado exitosamente en " + rutaExe + "!\n");
                } else {
                    out.append("[ERROR] Falló la generación del ejecutable con Clang.\n");
                }

            }

            parser.tablaSimbolos.generateFile();
            out.append("[OK] Tabla de símbolos generada en ts.txt\n");
            out.append("\nCompilación finalizada sin errores.\n");

        } catch (SyntaxException e) {
            out.append("\n[ERROR SINTACTICO] ").append(e.getMessage()).append('\n');
        } catch (Exception e) {
            out.append("\n[ERROR LEXICO/GENERAL] ").append(e.getMessage()).append('\n');
        }

        return out.toString();
    }

    private List<Token> lex(String sourceCode) throws Exception {
        Lexer lexer = new Lexer(new StringReader(sourceCode));
        List<Token> tokens = new ArrayList<>();

        Token token;
        while ((token = lexer.proximoToken()) != null) {
            tokens.add(token);
        }
        return tokens;
    }

    private void generarArchivoLogs(List<String> logs) {
        // Validación de seguridad por si la lista viene nula o vacía
        if (logs == null || logs.isEmpty()) {
            System.err.println("No hay logs sintácticos para guardar.");
            return;
        }

        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter("logs_sintacticos.txt"))) {
            writer.println("=== LOGS DE REDUCCION (ANALIZADOR SINTACTICO) ===");
            writer.println("Total de reglas aplicadas: " + logs.size() + "\n");
            for (String log : logs) {
                writer.println(log);
            }
        } catch (Exception e) {
            System.err.println("Error al guardar el archivo de logs: " + e.getMessage());
        }
    }

    public String generarEjecutable(String llFilePath) {
        // Cambiamos la extensión .ll por .exe
        String exePath = llFilePath.substring(0, llFilePath.lastIndexOf('.')) + ".exe";

        try {
            ProcessBuilder pbClang = new ProcessBuilder("clang", "programa.ll", "scanf.o", "-o", "programa.exe");
            Process proceso = pbClang.start();

            // Leemos si Clang tira algún error de sintaxis o linkeo
            BufferedReader reader = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
            String linea;
            StringBuilder logClang = new StringBuilder();
            while ((linea = reader.readLine()) != null) {
                logClang.append(linea).append("\n");
            }

            int exitCode = proceso.waitFor();
            if (exitCode == 0) {
                return exePath; // Éxito: retornamos la ruta del ejecutable creado
            } else {
                System.err.println("Error de Clang:\n" + logClang.toString());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String ejecutarCodigoNativo(String exePath) {
        StringBuilder resultadoConsola = new StringBuilder();
        try {
            ProcessBuilder pb = new ProcessBuilder(exePath);
            pb.redirectErrorStream(true);
            Process proceso = pb.start();

            // Capturamos lo que el programa imprime con 'printf' o 'scanf'
            BufferedReader reader = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
            String linea;
            while ((linea = reader.readLine()) != null) {
                resultadoConsola.append(linea).append("\n");
            }

            proceso.waitFor();
            return resultadoConsola.toString();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error al ejecutar el programa nativo: " + e.getMessage();
        }
    }
}