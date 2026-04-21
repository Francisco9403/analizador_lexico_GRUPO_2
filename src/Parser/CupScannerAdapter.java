package Parser;

import Exception.SyntaxException;
import Lexer.Token;
import java.util.List;
import java_cup.runtime.Scanner;
import java_cup.runtime.Symbol;

/**
 * Adapter que transforma tokens del lexer a simbolos esperados por CUP.
 */
public class CupScannerAdapter implements Scanner {

    private final List<Token> tokens;
    private int index = 0;

    public CupScannerAdapter(List<Token> tokens) {
        this.tokens = tokens;
    }

    @Override
    public Symbol next_token() throws Exception {
        if (index >= tokens.size()) {
            return new Symbol(ParserSym.EOF);
        }

        Token token = tokens.get(index++);
        int left = Math.max(token.linea - 1, 0);
        int right = Math.max(token.columna - 1, 0);

        return switch (token.nombre) {
            // Estructura y bloques
            case "PROGRAM" -> new Symbol(ParserSym.PROGRAM, left, right, token.valor);
            case "FIN_PROGRAM" -> new Symbol(ParserSym.FIN_PROGRAM, left, right, token.valor);
            case "DECLARE" -> new Symbol(ParserSym.DECLARE, left, right, token.valor);
            case "FIN_DECLARE" -> new Symbol(ParserSym.FIN_DECLARE, left, right, token.valor);

            // Control de flujo
            case "IF" -> new Symbol(ParserSym.IF, left, right, token.valor);
            case "ELIF" -> new Symbol(ParserSym.ELIF, left, right, token.valor);
            case "ELSE" -> new Symbol(ParserSym.ELSE, left, right, token.valor);
            case "WHILE" -> new Symbol(ParserSym.WHILE, left, right, token.valor);
            case "ALT_WHILE" -> new Symbol(ParserSym.ALT_WHILE, left, right, token.valor);
            case "BREAK" -> new Symbol(ParserSym.BREAK, left, right, token.valor);
            case "CONTINUE" -> new Symbol(ParserSym.CONTINUE, left, right, token.valor);
            case "PRINT" -> new Symbol(ParserSym.PRINT, left, right, token.valor);
            case "SUMA_ACUM" -> new Symbol(ParserSym.SUMA_ACUM, left, right, token.valor);

            // Tipos de datos
            case "TYPE_INT" -> new Symbol(ParserSym.TYPE_INT, left, right, token.valor);
            case "TYPE_FLOAT" -> new Symbol(ParserSym.TYPE_FLOAT, left, right, token.valor);
            case "TYPE_BOOL" -> new Symbol(ParserSym.TYPE_BOOL, left, right, token.valor);
            case "TYPE_ARRAY" -> new Symbol(ParserSym.TYPE_ARRAY, left, right, token.valor);

            // Funciones y Constantes
            case "READ_INT" -> new Symbol(ParserSym.READ_INT, left, right, token.valor);
            case "READ_FLOAT" -> new Symbol(ParserSym.READ_FLOAT, left, right, token.valor);
            case "READ_BOOL" -> new Symbol(ParserSym.READ_BOOL, left, right, token.valor);
            case "CONST_BOOL" -> new Symbol(ParserSym.CONST_BOOL, left, right, token.valor);
            case "CTE_ARREGLO" -> new Symbol(ParserSym.CTE_ARREGLO, left, right, token.valor);
            case "ID" -> new Symbol(ParserSym.ID, left, right, token.valor);
            case "CTE_INT" -> new Symbol(ParserSym.CTE_INT, left, right, token.valor);
            case "CTE_FLOAT" -> new Symbol(ParserSym.CTE_FLOAT, left, right, token.valor);
            case "CTE_STR" -> new Symbol(ParserSym.CTE_STR, left, right, token.valor);

            // Operadores
            case "OP_SUMA" -> new Symbol(ParserSym.OP_SUMA, left, right, token.valor);
            case "OP_RESTA" -> new Symbol(ParserSym.OP_RESTA, left, right, token.valor);
            case "OP_MULT" -> new Symbol(ParserSym.OP_MULT, left, right, token.valor);
            case "OP_DIV" -> new Symbol(ParserSym.OP_DIV, left, right, token.valor);
            case "COMP_IGUAL" -> new Symbol(ParserSym.COMP_IGUAL, left, right, token.valor);
            case "COMP_DISTINTO" -> new Symbol(ParserSym.COMP_DISTINTO, left, right, token.valor);
            case "COMP_MAYOR_IGUAL" -> new Symbol(ParserSym.COMP_MAYOR_IGUAL, left, right, token.valor);
            case "COMP_MENOR_IGUAL" -> new Symbol(ParserSym.COMP_MENOR_IGUAL, left, right, token.valor);
            case "COMP_MAYOR" -> new Symbol(ParserSym.COMP_MAYOR, left, right, token.valor);
            case "COMP_MENOR" -> new Symbol(ParserSym.COMP_MENOR, left, right, token.valor);
            case "ASIG" -> new Symbol(ParserSym.ASIG, left, right, token.valor);
            case "AND" -> new Symbol(ParserSym.AND, left, right, token.valor);
            case "OR" -> new Symbol(ParserSym.OR, left, right, token.valor);
            case "NOT" -> new Symbol(ParserSym.NOT, left, right, token.valor);

            // Delimitadores
            case "PAR_A" -> new Symbol(ParserSym.PAR_A, left, right, token.valor);
            case "PAR_C" -> new Symbol(ParserSym.PAR_C, left, right, token.valor);
            case "CORCH_A" -> new Symbol(ParserSym.CORCH_A, left, right, token.valor);
            case "CORCH_C" -> new Symbol(ParserSym.CORCH_C, left, right, token.valor);
            case "COMA" -> new Symbol(ParserSym.COMA, left, right, token.valor);
            case "DOS_PUNTOS" -> new Symbol(ParserSym.DOS_PUNTOS, left, right, token.valor);
            case "INDENT" -> new Symbol(ParserSym.INDENT, left, right, token.valor);
            case "DEDENT" -> new Symbol(ParserSym.DEDENT, left, right, token.valor);


            default -> throw new SyntaxException(
                    "Token no soportado por esta gramatica CUP: " + token.nombre +
                    " en linea " + token.linea + ", columna " + token.columna);
        };
    }
}
