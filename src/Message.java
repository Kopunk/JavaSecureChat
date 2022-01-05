import java.io.Serializable;

public class Message implements Serializable {
    String text;
    String author;
    Code code;

    enum Code {
        MSG, EXIT
    }

    Message(String author, String message, Code code) {
        this.text = message;
        this.author = author;
        this.code = code;
    }

    Message(String author, String message) {
        this(author, message, Code.MSG);
    }

    @Override
    public String toString() {
        return text;
    }
}
