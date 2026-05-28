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
import ast.NodoPrograma;


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
                NodoPrograma raiz = (NodoPrograma) result.value;
                String dotCode = raiz.generarGrafoDot();
                java.nio.file.Files.writeString(java.nio.file.Path.of("arbol_ast.dot"), dotCode);
                out.append("[OK] Árbol AST (Graphviz) generado en arbol_ast.dot\n");

                // --- GENERAR PNG AUTOMÁTICAMENTE ---
                try {
                    // Ejecutamos Graphviz desde la consola del sistema operativo
                    Process p = Runtime.getRuntime().exec("\"C:\\Program Files\\Graphviz\\bin\\dot.exe\" -Tpng arbol_ast.dot -o arbol_ast.png");
                    p.waitFor(); // Esperamos a que termine de dibujar
                    out.append("[OK] Imagen generada con éxito en arbol_ast.png\n");
                } catch (Exception ex) {
                    out.append("[AVISO] No se pudo generar el PNG. Asegúrese de tener Graphviz instalado y en el PATH.\n");
                }

                String llvmCode = raiz.generarCodigo();
                java.nio.file.Files.writeString(java.nio.file.Path.of("programa.ll"), llvmCode);
                out.append("[OK] Código LLVM IR generado en programa.ll\n");

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
}