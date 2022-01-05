import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class CipherHandler {
    static byte[] salt = { '4', '8', '2', '0', 'd', 'j', 'c', 'p', 'a', 'r', 'n', 't', '6', '9', 's', 'a' };

    static String encrypt(String text, String password) throws InvalidKeySpecException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 1000, 128 * 8);

        SecretKey key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec);
        Cipher aesCipher = Cipher.getInstance("AES");

        aesCipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedBytes = aesCipher.doFinal(text.getBytes());
        String encryptedString = Base64.getEncoder().encodeToString(encryptedBytes);

        return encryptedString;
    }

    static String decrypt(String encodedText, String password) throws InvalidKeySpecException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 1000, 128 * 8);

        SecretKey key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec);
        Cipher aesCipher = Cipher.getInstance("AES");

        aesCipher.init(Cipher.DECRYPT_MODE, key);

        byte[] encryptedBytes = Base64.getDecoder().decode(encodedText);
        byte[] decryptedBytes = aesCipher.doFinal(encryptedBytes);

        String decryptedString = new String(decryptedBytes);

        return decryptedString;
    }
}
