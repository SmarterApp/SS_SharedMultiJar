/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Web;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import AIR.Common.Configuration.ConfigurationManager;
import TDS.Shared.Security.IEncryption;
import TDS.Shared.Security.TDSEncryptionException;

public class Encryption implements IEncryption
{

  private static final Logger  _logger            = LoggerFactory.getLogger (Encryption.class);

  public static final int      MINIMUM_KEY_LENGTH = 10;

  @Autowired
  private ConfigurationManager configurationManager;

  private static final byte[]  PBE_SALT           = { (byte) 0xC1, (byte) 0x24, (byte) 0x5B, (byte) 0x9A, (byte) 0x17, (byte) 0x62, (byte) 0xF4, (byte) 0x80 };
  private static final int     PBE_NUM_ITERATIONS = 1000;
  private static final int     PBE_KEY_LENGTH     = 128;
  private static final int     IV_LENGTH          = 16;
  private static final String  CIPHER_ALGORITHM   = "AES";
  private static final String  PBE_KEY_ALGORITHM  = "PBKDF2WithHmacSHA1";
  private static final String  TRANSFORMATION     = "AES/CBC/PKCS5Padding";
  private static final String  CHARSET_NAME       = "UTF-8";

  private Cipher               _encryptCipher;

  private Cipher               _decryptCipher;

  private SecretKey            _secretKey;

  private String               _jceProvider;

  private String               _jceProviderClass;

  public Encryption ()
  {
  }

  /**
   * initializes ciphers and adds jce provider if provided
   * 
   * @throws TDSEncryptionException
   */
  @PostConstruct
  protected void init () {
    final String encryptionKey = configurationManager.getAppSettings ().get ("EncryptionKey");

    if (encryptionKey == null || encryptionKey.length () < MINIMUM_KEY_LENGTH) {
      throw new TDSEncryptionException (String.format ("Number of characters for key must be greater than %s", MINIMUM_KEY_LENGTH));
    }

    if (_jceProvider != null) {
      try {
        Security.addProvider (((Provider) Class.forName (_jceProviderClass).newInstance ()));
      } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
        _logger.error (e.getMessage (), e);
        throw new TDSEncryptionException ("JCE Provider class name is not valid");
      }
    }

    try {

      SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance (PBE_KEY_ALGORITHM);
      KeySpec keySpec = new PBEKeySpec (encryptionKey.toCharArray (), PBE_SALT, PBE_NUM_ITERATIONS, PBE_KEY_LENGTH);
      SecretKey secretKeyTemp;
      secretKeyTemp = secretKeyFactory.generateSecret (keySpec);
      _secretKey = new SecretKeySpec (secretKeyTemp.getEncoded (), CIPHER_ALGORITHM);
      if (_jceProvider == null) {
        _encryptCipher = Cipher.getInstance (TRANSFORMATION);
        _decryptCipher = Cipher.getInstance (TRANSFORMATION);
      } else {
        _encryptCipher = Cipher.getInstance (TRANSFORMATION, _jceProvider);
        _decryptCipher = Cipher.getInstance (TRANSFORMATION, _jceProvider);
      }
      _encryptCipher.init (Cipher.ENCRYPT_MODE, _secretKey);
    } catch (NoSuchAlgorithmException e) {
      _logger.error ("Encyption.initCipher: " + e.getMessage (), e);
      throw new TDSEncryptionException ("Algorithm is not available");
    } catch (InvalidKeySpecException e) {
      _logger.error ("Encyption.initCipher: " + e.getMessage (), e);
      throw new TDSEncryptionException ("Key specification is not valid");
    } catch (NoSuchPaddingException e) {
      _logger.error ("Encyption.initCipher: " + e.getMessage (), e);
      throw new TDSEncryptionException ("Padding is not valid");
    } catch (NoSuchProviderException e) {
      _logger.error ("Encyption.initCipher: " + e.getMessage (), e);
      throw new TDSEncryptionException ("Provider is not available");
    } catch (InvalidKeyException e) {
      _logger.error ("Encyption.initCipher: " + e.getMessage (), e);
      throw new TDSEncryptionException ("Key is not valid");
    }
  }

  /**
   * encrypts a string
   * 
   * @param strVal
   *          String to encrypt
   * @return encrypted string
   * @throws TDSEncryptionException
   */
  public String scrambleText (String strVal) {
    return encrypt (strVal);
  }

  /**
   * decrypts a string
   * 
   * @param strVal
   *          String to decrypt
   * @return decrypted string
   * @throws TDSEncryptionException
   */
  public String unScrambleText (String strVal) {
    return decrypt (strVal);
  }

  /**
   * Encrypts a string
   * 
   * @param stringToEncrypt
   * @return encrypted string
   * @throws TDSEncryptionException
   */
  private synchronized  String encrypt (String stringToEncrypt) {
    try {

      byte[] plainBytes = stringToEncrypt.getBytes (CHARSET_NAME);
      byte[] encryptedBytes = _encryptCipher.doFinal (plainBytes);
      byte[] encryptIv = _encryptCipher.getParameters ().getParameterSpec (IvParameterSpec.class).getIV ();
      byte[] cipherText = new byte[encryptedBytes.length + encryptIv.length];
      System.arraycopy (encryptIv, 0, cipherText, 0, encryptIv.length);
      System.arraycopy (encryptedBytes, 0, cipherText, encryptIv.length, encryptedBytes.length);
      return DatatypeConverter.printBase64Binary (cipherText);
    } catch (UnsupportedEncodingException e) {
      _logger.error ("Encyption.encrypt: " + e.getMessage (), e);
      throw new TDSEncryptionException ("Encoding is not valid");
    } catch (IllegalBlockSizeException e) {
      _logger.error ("Encyption.encrypt: " + e.getMessage (), e);
      throw new TDSEncryptionException ("Block Size is not valid");
    } catch (BadPaddingException e) {
      _logger.error ("Encyption.encrypt: " + e.getMessage (), e);
      throw new TDSEncryptionException ("Padding is not valid");
    } catch (InvalidParameterSpecException e) {
      _logger.error ("Encyption.encrypt: " + e.getMessage (), e);
      throw new TDSEncryptionException ("Parameter Sepcification is not valid");
    }

  }

  /**
   * decrypts a string
   * 
   * @param stringToDecrypt
   * @return decrypted string
   * @throws TDSEncryptionException
   */
  private synchronized String decrypt (String stringToDecrypt) {
    try {
      
      if ( StringUtils.isEmpty (stringToDecrypt)) {
        return "";
      }

      byte[] encryptedBytes = DatatypeConverter.parseBase64Binary (stringToDecrypt);

      byte[] iv = new byte[IV_LENGTH];
      System.arraycopy (encryptedBytes, 0, iv, 0, iv.length);
      _decryptCipher.init (Cipher.DECRYPT_MODE, _secretKey, new IvParameterSpec (iv));
      byte[] cipherBytes = new byte[encryptedBytes.length - IV_LENGTH];
      System.arraycopy (encryptedBytes, IV_LENGTH, cipherBytes, 0, cipherBytes.length);
      byte[] decryptedBytes = _decryptCipher.doFinal (cipherBytes);
      return new String (decryptedBytes, CHARSET_NAME);
    } catch (InvalidAlgorithmParameterException e) {
      _logger.error ("Encyption.decrypt: " + e.getMessage (), e);
      throw new TDSEncryptionException ("Algorithm is not valid");
    } catch (InvalidKeyException e) {
      _logger.error ("Encyption.decrypt: " + e.getMessage (), e);
      throw new TDSEncryptionException ("Key is not valid");
    } catch (IllegalBlockSizeException e) {
      _logger.error ("Encyption.decrypt: " + e.getMessage (), e);
      throw new TDSEncryptionException ("Block size is not valid");
    } catch (BadPaddingException e) {
      _logger.error ("Encyption.decrypt: " + e.getMessage (), e);
      throw new TDSEncryptionException ("Padding is not valid");
    } catch (UnsupportedEncodingException e) {
      _logger.error ("Encyption.decrypt: " + e.getMessage (), e);
      throw new TDSEncryptionException ("Encoding is not valid");
    }

  }

  public String getJceProvider () {
    return _jceProvider;
  }

  public void setJceProvider (String jceProvider) {
    this._jceProvider = jceProvider;
  }

  public String getJceProviderClass () {
    return _jceProviderClass;
  }

  public void setJceProviderClass (String jceProviderClass) {
    this._jceProviderClass = jceProviderClass;
  }
}
