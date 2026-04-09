import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

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
            Parser parser = new Parser(tokens);
            String trace = parser.parse();
            out.append(trace);
            out.append("\nCompilacion finalizada sin errores.\n");
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
        while ((token = lexer.next_token()) != null) {
            tokens.add(token);
        }

        return tokens;
    }
}

