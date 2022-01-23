import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Message implements Serializable {
    String text;
    String author;
    Code code;
    String timeOfCreation;

    enum Code {
        MSG, EXIT, ERR, SERVER
    }

    Message(String author, String message, Code code) {
        this.text = message;
        this.author = author;
        this.code = code;
        this.timeOfCreation = new SimpleDateFormat("HH:mm dd.MM.yyyy").format(new Date());
    }

    Message(String author, String message) {
        this(author, message, Code.MSG);
    }

    @Override
    public String toString() {
        return text;
    }

    void encryptMessage(String password) {
        try {
            text = CipherHandler.encrypt(text, password);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace();
        }
    }

    void decryptMessage(String password) {
        try {
            text = CipherHandler.decrypt(text, password);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace();
        }
    }
}
