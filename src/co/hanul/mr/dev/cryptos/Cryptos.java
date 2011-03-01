package co.hanul.mr.dev.cryptos;

import java.security.InvalidKeyException;
import java.security.Key;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;

import biz.source_code.base64Coder.Base64Coder;

/**
 * 암호화 모듈
 * 
 * @author Mr. 하늘
 */
public class Cryptos {

	private static Cryptos instance = new Cryptos();

	private final static String algorithm = "DESede";
	private Key key = null;
	private Cipher cipher = null;

	public static Cryptos getInstance() {
		return instance;
	}

	private Cryptos() {
		try {
			setUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setUp() throws Exception {
		key = KeyGenerator.getInstance(algorithm).generateKey();
		cipher = Cipher.getInstance(algorithm);
	}

	public String encrypt(String input) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] inputBytes = input.getBytes();
		return Base64Coder.encodeLines(cipher.doFinal(inputBytes));
	}

	public String decrypt(String encryptionString) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		byte[] encryptionBytes = Base64Coder.decodeLines(encryptionString);
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] recoveredBytes = cipher.doFinal(encryptionBytes);
		String recovered = new String(recoveredBytes);
		return recovered;
	}

}
