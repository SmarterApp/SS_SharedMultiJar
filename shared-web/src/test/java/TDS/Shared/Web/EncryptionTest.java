/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import AIR.Common.Configuration.ConfigurationManager;
import AIR.Common.Configuration.ConfigurationSection;
import AIR.test.framework.AbstractTest;
import TDS.Shared.Security.IEncryption;
import TDS.Shared.Security.TDSEncryptionException;

/**
 * @author jmambo
 *
 */
@ContextConfiguration (locations = "/shared.web-test-context.xml")
public class EncryptionTest  extends AbstractTest
{

  @SuppressWarnings ("unused")
  private static final Logger       _logger = LoggerFactory.getLogger (EncryptionTest.class);
  
  @Autowired
  @InjectMocks
  private Encryption  _encryption;
  
  @Autowired
  private IEncryption  _encryptionJce;
  
  @Mock
  private ConfigurationManager _configurationManager;
  
  @Rule  
  public ExpectedException _thrown = ExpectedException.none();  
  
  
  @Test
  public void testShortTextEncryption () throws Exception {
     String textToEncrypt = "%26T_KEY%3D-611039%26";

     String encryptedText = _encryption.scrambleText(textToEncrypt);
     assertNotNull(encryptedText);
     assertNotEquals (textToEncrypt, encryptedText);
     String decryptedText = _encryption.unScrambleText(encryptedText);
     assertEquals (textToEncrypt, decryptedText);
  }
   
  @Test
  public void testLongTextEncryption () throws Exception {
     String textToEncrypt = "This is test of student taking an assessment. This is test of student taking an assessment. ";

     String encryptedText = _encryption.scrambleText(textToEncrypt);
     assertNotNull(encryptedText);
     assertNotEquals (textToEncrypt, encryptedText);
     String decryptedText = _encryption.unScrambleText(encryptedText);
     assertEquals (textToEncrypt, decryptedText);
  }
  
  @Test
  public void testSeveralStringsWithOneInstance () throws Exception {
   
     String texttoEncrypt1 = "test";
     String encryptedText = _encryption.scrambleText(texttoEncrypt1);
     assertNotEquals (texttoEncrypt1, encryptedText);
     String decryptedText = _encryption.unScrambleText(encryptedText);
     assertEquals (texttoEncrypt1, decryptedText);
  
     String texttoEncrypt2 = "test2";
     encryptedText = _encryption.scrambleText(texttoEncrypt2);
     assertNotEquals (texttoEncrypt1, encryptedText);
     decryptedText = _encryption.unScrambleText(encryptedText);
     assertEquals (texttoEncrypt2, decryptedText);
     
     
     String texttoEncrypt3 = "test3";
     encryptedText = _encryption.scrambleText(texttoEncrypt3);
     assertNotEquals (texttoEncrypt3, encryptedText);
     decryptedText = _encryption.unScrambleText(encryptedText);
     assertEquals (texttoEncrypt3, decryptedText);
     
     String texttoEncrypt4 = "test4";
     encryptedText = _encryption.scrambleText(texttoEncrypt4);
     assertNotEquals (texttoEncrypt4, encryptedText);
     decryptedText = _encryption.unScrambleText(encryptedText);
     assertEquals (texttoEncrypt4, decryptedText);
   }
  
  @Test
  public void testWithJceProvider () throws Exception {
     String textToEncrypt = "test1";
      
     String encryptedText = _encryptionJce.scrambleText(textToEncrypt);
     assertNotNull(encryptedText);
     assertNotEquals (textToEncrypt, encryptedText);
     String decryptedText = _encryptionJce.unScrambleText(encryptedText);
     assertEquals (textToEncrypt, decryptedText);
  }
   
  @Test
  public void testWithEncryptAndDecryptWithDifferentProviders () throws Exception {
     String textToEncrypt = "test1";
     
     String encryptedText = _encryption.scrambleText(textToEncrypt);
     assertNotNull(encryptedText);
     assertNotEquals (textToEncrypt, encryptedText);

     String decryptedText = _encryptionJce.unScrambleText(encryptedText);
     assertEquals (textToEncrypt, decryptedText);
  }
 
  @Test
  public void testWithKeyLessThanMinimumLength () throws Exception {
     _thrown.expect(TDSEncryptionException.class);  
     _thrown.expectMessage(String.format ("Number of characters for key must be greater than %s", Encryption.MINIMUM_KEY_LENGTH));  
     String key  = "testKey";
     
     MockitoAnnotations.initMocks(this);
     ConfigurationSection configurationSection = mock(ConfigurationSection.class);
     when(configurationSection.get ("EncryptionKey")).thenReturn (key);
     when(_configurationManager.getAppSettings()).thenReturn(configurationSection); 
     _encryption.init ();
   }
  
  @Test
  public void testWithNullKey () throws Exception {
     _thrown.expect(TDSEncryptionException.class);  
     _thrown.expectMessage(String.format ("Number of characters for key must be greater than %s", Encryption.MINIMUM_KEY_LENGTH));  
     String key  = null;
     MockitoAnnotations.initMocks(this);
     ConfigurationSection configurationSection = mock(ConfigurationSection.class);
     when(configurationSection.get ("EncryptionKey")).thenReturn (key);
     when(_configurationManager.getAppSettings()).thenReturn(configurationSection); 
     _encryption.init ();
   }
  
  @Test
  public void testWithNullText () throws Exception {
    _thrown.expect(NullPointerException.class);
     String texttoEncrypt = null;
 
    _encryption.scrambleText(texttoEncrypt);
  }
  
  
}
