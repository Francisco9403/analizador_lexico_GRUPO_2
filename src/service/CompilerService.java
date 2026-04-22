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
/**
 * Orquesta analisis lexico y sintactico para CLI y GUI.
 */
public class CompilerService {

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
            if (result != null && result.value != null) {
                out.append("Resultado: ").append(result.value).append('\n');
            }
            out.append("\nCompilacion finalizada sin errores.\n");
            Lexer.tablaSimbolos.generateFile();
            out.append("\nTabla de símbolos generada en ts.txt\n");
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
}

