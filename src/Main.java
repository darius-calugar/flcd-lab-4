import scanner.LexicalError;
import scanner.Scanner;
import symbolTable.SymbolTable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        var fileNames = List.of("p1", "p2", "p3", "p1err");
        for (var fileName : fileNames) {
            var inputFile         = new File("input/" + fileName + ".verba");
            var reservedWordsFile = new File("input/token.in");

            var pif         = new ArrayList<List<String>>();
            var symbolTable = new SymbolTable();
            var scanner     = new Scanner(pif, symbolTable);

            try {
                System.out.print(fileName + ": ");
                scanner.scan(inputFile, reservedWordsFile);
                savePIF(pif, fileName);
                saveSymbolTable(symbolTable, fileName);
                System.out.println("Lexically correct");
            } catch (LexicalError e) {
                System.out.println(e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void savePIF(List<List<String>> pif, String fileName) throws IOException {
        var pifFile = new File("output/" + fileName + "_pif.out");
        var writer  = new PrintWriter(new FileWriter(pifFile));
        for (var row : pif) {
            writer.println(row.get(0) + ", " + row.get(1));
        }
        writer.flush();
    }

    static void saveSymbolTable(SymbolTable symbolTable, String fileName) throws IOException {
        var stFile    = new File("output/" + fileName + "_st.out");
        var writer    = new PrintWriter(new FileWriter(stFile));
        var positions = symbolTable.positions();
        var symbols   = symbolTable.symbols();
        for (var i = 0; i < positions.size(); i++) {
            writer.println(positions.get(i) + ", " +
                           symbols.get(i) + ", " +
                           "hash=" + (positions.get(i) % SymbolTable.size) + ", " +
                           "index=" + (positions.get(i) / SymbolTable.size));
        }
        writer.flush();
    }
}
