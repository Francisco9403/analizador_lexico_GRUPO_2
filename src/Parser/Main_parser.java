package Parser;
import java.io.FileReader;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.SymbolFactory;
import Lexer.Token;
import Lexer.Lexer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author itt
 */
public class Main_parser {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        String path = "./src/input_1.txt";
        System.out.println("Análisis sintáctico iniciado:");
        FileReader fileReader = new FileReader(path);
        Lexer lexer = new Lexer(fileReader);

        List<Token> tokens = new ArrayList<>();
        Token token;
        while ((token = lexer.yylex()) != null) {
            tokens.add(token);
        }


        Parser parser = new Parser(new CupScannerAdapter(tokens));
        try{
            parser.parse();
            System.out.println("Análisis sintáctico finalizado.");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}