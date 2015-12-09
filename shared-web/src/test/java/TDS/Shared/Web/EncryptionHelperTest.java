/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Web;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import AIR.Common.Web.EncryptionHelper;
import AIR.test.framework.AbstractTest;

/**
 * @author jmambo
 *
 */
@ContextConfiguration (locations = "/shared.web-test-context.xml")
public class EncryptionHelperTest  extends AbstractTest
{
  
  @Test
  public void testEncryptToBase64 () throws Exception {
     String textToEncrypt = "C:/tmp/items/test.xml";

     String encryptedText = EncryptionHelper.EncryptToBase64(textToEncrypt);
     System.out.println("======= Encrypted Text =========");
     System.out.println(encryptedText);
     System.out.println();
  }
  
  
  @Test
  public void testDecryptFromBase64 () throws Exception {
     String textToDecrypt = "";

     String decryptedText = EncryptionHelper.DecryptFromBase64(textToDecrypt);
     System.out.println("======= Decrypted Text =========");
     System.out.println(decryptedText);
     System.out.println();
  }
  
}
