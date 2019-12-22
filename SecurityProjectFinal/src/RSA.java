import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;

public class RSA {

	public static KeyPair genRSAKeyPair() throws NoSuchAlgorithmException {
		SecureRandom secureRandom = new SecureRandom();
		KeyPairGenerator gen;
		gen = KeyPairGenerator.getInstance("RSA");
		gen.initialize(512, secureRandom);
		KeyPair keyPair = gen.genKeyPair();
		return keyPair;
	}
	
	public static String EncryptPublicKey(String msg, PublicKey publickey) throws Exception {
		
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publickey);
		
		byte[] bmsg = cipher.doFinal(msg.getBytes());
		String encryptMsg=Base64.encodeBase64String(bmsg);
		
		return encryptMsg;
		
	}
	
	public static String EncryptPrivateKey(String msg, PrivateKey privatekey) throws Exception {
		
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, privatekey);
		
		byte[] bmsg = cipher.doFinal(msg.getBytes());
		String encryptMsg=Base64.encodeBase64String(bmsg);
		
		return encryptMsg;
		
	}
	
	public static String DecryptPublickey(String cipherText, PublicKey publickey) throws Exception {
		
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, publickey);
		byte[] bcipher = Base64.decodeBase64(cipherText.getBytes());
		byte[] bplain = cipher.doFinal(bcipher);
		
		return new String(bplain.toString());
	}
	
	public static String DecryptPrivatekey(String cipherText, PrivateKey privatekey) throws Exception {
		
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privatekey);
		byte[] bcipher = Base64.decodeBase64(cipherText.getBytes());
		byte[] bplain = cipher.doFinal(bcipher);
		
		return new String(bplain);
		
	}
}