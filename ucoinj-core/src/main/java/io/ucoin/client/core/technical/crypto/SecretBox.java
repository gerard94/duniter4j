package io.ucoin.client.core.technical.crypto;

import static io.ucoin.client.core.technical.crypto.CryptoUtils.*;
import static org.abstractj.kalium.NaCl.sodium;
import static org.abstractj.kalium.NaCl.Sodium.BOXZERO_BYTES;
import static org.abstractj.kalium.NaCl.Sodium.PUBLICKEY_BYTES;
import static org.abstractj.kalium.NaCl.Sodium.SECRETKEY_BYTES;
import static org.abstractj.kalium.NaCl.Sodium.XSALSA20_POLY1305_SECRETBOX_NONCEBYTES;
import static org.abstractj.kalium.NaCl.Sodium.ZERO_BYTES;
import static org.abstractj.kalium.crypto.Util.checkLength;
import static org.abstractj.kalium.crypto.Util.isValid;
import static org.abstractj.kalium.crypto.Util.removeZeros;
import io.ucoin.client.core.technical.UCoinTechnicalException;

import java.security.GeneralSecurityException;

import jnr.ffi.byref.LongLongByReference;

import org.abstractj.kalium.crypto.Util;
import org.abstractj.kalium.keys.VerifyKey;

import com.lambdaworks.crypto.SCrypt;


public class SecretBox {

	// Length of the key
	private static int SEED_LENGTH = 32;
	private static int SIGNATURE_BYTES = 64;
	private static int SCRYPT_PARAMS_N = 4096;
	private static int SCRYPT_PARAMS_r = 16;
	private static int SCRYPT_PARAMS_p = 1;

	private final String pubKey;
    private final byte[] secretKey;
    private final byte[] seed;

	public SecretBox(String salt, String password) {
		this(computeSeedFromSaltAndPassword(salt, password));
	}
	
	public SecretBox(byte[] seed) {
		checkLength(seed, SEED_LENGTH);
		this.seed = seed;
        this.secretKey = zeros(SECRETKEY_BYTES * 2);
        byte[] publicKey = zeros(PUBLICKEY_BYTES);
        isValid(sodium().crypto_sign_ed25519_seed_keypair(publicKey, secretKey, seed),
                "Failed to generate a key pair");
        this.pubKey = Base58.encode(publicKey);
	}
	
	/**
	 * Retrun the public key, encode in Base58
	 * @return
	 */
	public String getPublicKey() {
		return pubKey;
	}
	
	/**
	 * Return the secret key, encode in Base58
	 * @return
	 */
	public String getSecretKey() {
		return Base58.encode(secretKey);
	}
	
	public String sign(String message) {
		byte[] messageBinary = decodeUTF8(message);
		return encodeBase64(
				sign(messageBinary)
				);
	}
	
	public byte[] sign(byte[] message) {
        byte[] signature = Util.prependZeros(SIGNATURE_BYTES, message);
        LongLongByReference bufferLen = new LongLongByReference(0);
        sodium().crypto_sign_ed25519(signature, bufferLen, message, message.length, secretKey);
        signature = slice(signature, 0, SIGNATURE_BYTES);
        
        checkLength(signature, SIGNATURE_BYTES);
        return signature;
    }


    public byte[] encrypt(byte[] nonce, byte[] message) {
        checkLength(nonce, XSALSA20_POLY1305_SECRETBOX_NONCEBYTES);
        byte[] msg = Util.prependZeros(ZERO_BYTES, message);
        byte[] ct = Util.zeros(msg.length);
        isValid(sodium().crypto_secretbox_xsalsa20poly1305(ct, msg, msg.length,
                nonce, seed), "Encryption failed");
        return removeZeros(BOXZERO_BYTES, ct);
    }

    public byte[] decrypt(byte[] nonce, byte[] ciphertext) {
        checkLength(nonce, XSALSA20_POLY1305_SECRETBOX_NONCEBYTES);
        byte[] ct = Util.prependZeros(BOXZERO_BYTES, ciphertext);
        byte[] message = Util.zeros(ct.length);
        isValid(sodium().crypto_secretbox_xsalsa20poly1305_open(message, ct,
                ct.length, nonce, seed), "Decryption failed. Ciphertext failed verification");
        return removeZeros(ZERO_BYTES, message);
    }
    
	/* -- Internal methods -- */
	
	public static byte[] computeSeedFromSaltAndPassword(String salt, String password) {
		try {
			byte[] seed = SCrypt.scrypt(
					decodeAscii(password), 
					decodeAscii(salt), 
					SCRYPT_PARAMS_N, SCRYPT_PARAMS_r,
					SCRYPT_PARAMS_p, SEED_LENGTH);
			return seed;
		} catch (GeneralSecurityException e) {
			throw new UCoinTechnicalException(
					"Unable to salt password, using Scrypt library", e);
		}
	}
	
	
}
