package scanner;

public class LexicalError extends Exception {
    int    line;
    String target;
    String message;

    public LexicalError(int line, String target, String message) {
        this.line    = line;
        this.target  = target;
        this.message = message;
    }

    @Override
    public String toString() {
        return "Lexical error on line " + line + ": '" + target + "' - " + message;
    }
}
