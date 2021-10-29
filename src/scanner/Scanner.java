package scanner;

import symbolTable.SymbolTable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

public class Scanner {
    List<List<String>> pif;
    SymbolTable        symbolTable;

    public Scanner(List<List<String>> pif, SymbolTable symbolTable) {
        this.pif         = pif;
        this.symbolTable = symbolTable;
    }

    public void scan(File inputFile, File reservedWordsFile) throws IOException, LexicalError {
        var reservedWordsReader = new LineNumberReader(new FileReader(reservedWordsFile));
        var reservedWords       = new ArrayList<String>();
        reservedWords.add(null); // 0 - Reserved for identifiers
        reservedWords.add(null); // 1 - Reserved for constants
        var reservedWord = reservedWordsReader.readLine();
        while (reservedWord != null) {
            reservedWords.add(reservedWord);
            reservedWord = reservedWordsReader.readLine();
        }

        var inputReader = new LineNumberReader(new FileReader(inputFile));
        var token       = readToken(inputReader);
        while (token != null) {
            if (!token.isEmpty()) {
                if (reservedWords.contains(token)) {
                    pif.add(List.of(token, "-1"));
                } else if (isIdentifier(token)) {
                    var pos = symbolTable.lookup(token);
                    if (pos == -1)
                        pos = symbolTable.put(token);
                    pif.add(List.of("IDENTIFIER", String.valueOf(pos)));
                } else if (isNumericConstant(token) || isStringConstant(token)) {
                    var pos = symbolTable.lookup(token);
                    if (pos == -1)
                        pos = symbolTable.put(token);
                    pif.add(List.of("CONSTANT", String.valueOf(pos)));
                } else {
                    throw new LexicalError(inputReader.getLineNumber(), token, "Invalid identifier or literal token");
                }
            }
            token = readToken(inputReader);
        }
    }

    private String readToken(LineNumberReader reader) throws LexicalError, IOException {
        var token = readUntil(reader, " \r\n\"-");
        if (token == null)
            return null;
        if (token.equals("-")) {
            var number = readUntil(reader, " \r\n\"-");
            if (number == null)
                throw new LexicalError(reader.getLineNumber(), token, "Invalid identifier or literal token");
            if (!isNumericConstant(token + number))
                throw new LexicalError(reader.getLineNumber(), token + number, "Invalid identifier or literal token");
            token += number;
        } else if (token.equals("\"")) {
            var string = readUntil(reader, "\"\r\n");
            if (string == null)
                throw new LexicalError(reader.getLineNumber(), token, "Invalid identifier or literal token");
            if (!isStringConstant(token + string))
                throw new LexicalError(reader.getLineNumber(), token + string, "Invalid identifier or literal token");
            token += string;
        }
        return token;
    }

    private String readUntil(LineNumberReader reader, String separators) throws IOException, LexicalError {
        StringBuilder tokenBuilder = new StringBuilder();
        var           character    = reader.read();

        while (true) {
            var lastChar = character;
            if (separators.chars().anyMatch(c -> c == lastChar)) {
                if (!isSeparator(lastChar)) {
                    tokenBuilder.append((char) lastChar);
                }
                break;
            }
            if (character == -1)
                return null;
            if (!isAlphabet(character))
                throw new LexicalError(reader.getLineNumber() + 1, String.valueOf((char) character), "Invalid character");
            tokenBuilder.append((char) character);
            character = reader.read();
        }

        return tokenBuilder.toString();
    }


    // Token checks

    private boolean isIdentifier(String token) {
        if (token.length() == 0)
            return false;
        if (!isLetter(token.charAt(0)))
            return false;
        for (int i = 1; i < token.length(); i++) {
            if (!isLetter(token.charAt(i)) && !isDigit(token.charAt(i)))
                return false;
        }
        return true;
    }

    private boolean isNumericConstant(String token) {
        if (token.length() == 0)
            return false;
        var negative = token.charAt(0) == '-';
        if (negative && token.length() == 1)
            return false;
        for (int i = negative ? 1 : 0; i < token.length(); i++) {
            var character = token.charAt(i);
            if (!isDigit(character))
                return false;
        }
        return true;
    }

    private boolean isStringConstant(String token) {
        if (token.charAt(0) != '\"' || token.charAt(token.length() - 1) != '\"')
            return false;
        for (int i = 1; i < token.length() - 1; i++) {
            var character = token.charAt(i);
            if (!isAlphabet(character) || isQuote(character))
                return false;
        }
        return true;
    }

    // Character checks

    private boolean isAlphabet(int character) {
        return isLetter(character) ||
               isDigit(character) ||
               isSeparator(character) ||
               isQuote(character) ||
               isMinus(character);
    }

    private boolean isLetter(int character) {
        return ("abcdefghijklmnopqrstuvwxyz" +
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ")
                .chars()
                .anyMatch((c) -> c == character);
    }

    private boolean isDigit(int character) {
        return "0123456789"
                .chars()
                .anyMatch((c) -> c == character);
    }

    private boolean isSeparator(int character) {
        return " \r\n"
                .chars()
                .anyMatch((c) -> c == character);
    }

    private boolean isQuote(int character) {
        return character == '"';
    }

    private boolean isMinus(int character) {
        return character == '-';
    }
}