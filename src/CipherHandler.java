import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class CipherHandler {
    static private String cutPassword(String password) {
        int passlen = 128 / 8;
        if (password.length() > passlen) {
            password.substring(0, passlen);

        } else {
            int i = 0;
            while (password.length() < passlen) {
                password += password.charAt(i % passlen);
            }
        }

        return password;
    }

    static String encrypt(String text, String password) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        password = cutPassword(password);

        Key aesKey = new SecretKeySpec(password.getBytes(), "AES");
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);

        // for double encryption?
        // byte[] encrypted = aesCipher.doFinal(text.getBytes());

        byte[] encryptedBytes = aesCipher.doFinal(text.getBytes());
        String encryptedString = Base64.getEncoder().encodeToString(encryptedBytes);

        return encryptedString;
    }

    static String decrypt(String encodedText, String password) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        password = cutPassword(password);

        Key aesKey = new SecretKeySpec(password.getBytes(), "AES");
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey);

        byte[] encryptedBytes = Base64.getDecoder().decode(encodedText);
        byte[] decryptedBytes = aesCipher.doFinal(encryptedBytes);

        String decryptedString = new String(decryptedBytes);

        return decryptedString;
    }

    public static void main(String[] args) {
        final String pass = "a super secret";
        String s = "I loooooooooove Java!!!";
        String beforeDecryption = "LEH9tuv35yK4e/K+z6ympw2fi+dQpUtlYMDUdLrmG9A=";
        String encryptedString = beforeDecryption;
        String decryptedString = "";

        // test encrypt
        try {
            encryptedString = encrypt(s, pass);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
                | IllegalBlockSizeException | BadPaddingException e) {
            System.err.println("ERROR: could't encrypt");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Before encryption: " + s);
        System.out.println("After encryption: " + encryptedString);

        // test decrypt
        try {
            decryptedString = decrypt(encryptedString, pass);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
                | IllegalBlockSizeException | BadPaddingException e) {
            System.err.println("ERROR: could't decrypt");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Before decryption: " + encryptedString);
        System.out.println("After decryption: " + decryptedString);
        System.exit(0);
    }
}
