/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package org.air.test.db.framework;

import org.springframework.stereotype.Component;

import AIR.Common.DB.AbstractDAO;
import AIR.Common.DB.SQLConnection;
import AIR.Common.DB.SqlParametersMaps;
import AIR.Common.DB.results.SingleDataResultSet;
import TDS.Shared.Exceptions.ReturnStatusException;

@Component
public class HelperAbstractDLL extends AbstractDAO
{
  public SingleDataResultSet T_LoginRequirements_SP (SQLConnection connection, String clientName) throws ReturnStatusException {
    final String SQL_QUERY1 = "BEGIN; SET NOCOUNT ON; exec T_LoginRequirements ${clientName}; end;";
    // build parameters
    SqlParametersMaps parametersQuery1 = (new SqlParametersMaps ()).put ("clientName", clientName);
    // execute and get first result set - that is only true in this case.
    return executeStatement (connection, SQL_QUERY1, parametersQuery1, false).getResultSets ().next ();
  }

}
