package Parser;
import java.io.FileReader;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.SymbolFactory;
import Lexer.Lexer;
import Lexer.Token;

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
        while ((token = lexer.next_token()) != null) {
            tokens.add(token);
        }
        
     
        SymbolFactory sf = new ComplexSymbolFactory();
        Parser parser= new Parser(new CupScannerAdapter(tokens), sf);
        try{
            parser.parse();
            System.out.println("Análisis sintáctico finalizado.");}
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
}