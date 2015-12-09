/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.DB;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import TDS.Shared.Exceptions.DBLockNotSupportedException;
import TDS.Shared.Exceptions.ReturnStatusException;

/**
 * @author akulakov
 * 
 */
public class UnittestFlagUpdator
{
  private static Logger       _logger                = LoggerFactory.getLogger (UnittestFlagUpdator.class);

  // ============================FOR STATISTICS================================

  final String                SELECT                 = "select";
  final String                UPDATE                 = "update";
  final String                INSERT                 = "insert into";
  final String                FROM                   = "from";
  final String                WHERE                  = "where";
  final String                UNION                  = "union";
  final String                UNIONALL               = "union all";
  final String                EXIST                  = "exist";
  final String                LIMIT                  = "limit";
  final String                SET                    = "set ";
  final String                INSERTINTOARCHIVE      = "insert into tds_archive_";
  final String                INTO                   = "into";

  final String                root                   = "C:\\java_workspace\\TdsDLLDev\\database scripts - mysql\\test data for unit tests db\\";

  final String                statistics             = "statistics.csv";
  final String                goodUpdateUnittestflag = "good update unit test flag queries.txt";
  final String                goodResultFileName     = root + goodUpdateUnittestflag;
  final String                errorSelectFile        = "error create statements test flag.txt";
  final String                statementsFile         = " statements test flag.txt";
  final String                errorSelectFileName    = root + errorSelectFile;
  final String                otherFile              = "unit tests other statements.txt";
  final String                otherFileName          = root + otherFile;
  final String                notExists              = "not exists";
  final String                exists                 = "exists";
  final String                unittestflagName       = "unittestflag";

  final String                selectStarFile         = "select count(star).txt";                                                                // selectStarFileName
  final String                selectStarFileName     = root + selectStarFile;

  final String                logFile                = "log.txt";
  final String                logName                = root + logFile;

  final String                tab8                   = "        ";
  final String                unittestflagValue      = unittestflagName + " = 1";
  final char                  spaceChar              = ' ';
  final char                  leftBracketChar        = '(';
  final char                  rightBracketChar       = ')';
  final String                leftBracket            = "(";
  final String                rightBracket           = ")";
  final String                space                  = " ";
  final String                rbSelect               = "(select";
  final String                rbUpdate               = "(update";
  final String                rbInsert               = "(insert";

  final String                ls                     = System.getProperty ("line.separator");
  private Map<String, String> aliasToName            = new HashMap<String, String> ();

  private List<String>        views                  = new ArrayList<String> ();

  private int                 goodUpdates            = 0;
  private int                 badUpdates             = 0;

  private int                 MAXLENGTH              = 5000;

  // ===========================================================================

   // this is main function
  public void updateUnittestflag (SQLConnection connection,
      String reformulatedQuery, boolean useNoLock) throws ReturnStatusException {

    // TODO: not hardcoded
    views.add ("externs");
    views.add ("itembank_tbladminstrand");
    views.add ("itembank_tblsetofadminitems");
    views.add ("itembank_tblsetofadminsubjects");
    views.add ("statuscodes");
    views.add ("timelimits");
    views.add ("tdscore_test_configs.v_client_tooldependencies");

    final String messageTemplate = "Exception %1$s executing query. Updated query is \"%2$s\". Exception message: %3$s.";
    int count = 1;
    // for unit tests
    List<String> updatedQueries = null;
    try {
      updatedQueries = getUpdatedQueries (reformulatedQuery);

    } catch (Exception e) {
      // TODO
      // here we write in file reformulatedQuery which cannot transform in
      // update query from
      appendFile (errorSelectFileName, reformulatedQuery + ls);
    }

    for (String updatedQuery : updatedQueries)
    {
      _logger.info (ls + "Query : " + reformulatedQuery + "; " + ls + "updatedQuery : " + updatedQuery);

      int currentTransactionIsolation = -1;
      PreparedStatement st = null;
      try {
        st = connection.prepareStatement (updatedQuery);

        if (useNoLock) {

          // if (not supported )then throw DBLockNotSupportedException
          DatabaseMetaData dbMetaData = connection.getMetaData ();

          if (dbMetaData
              .supportsTransactionIsolationLevel (Connection.TRANSACTION_READ_UNCOMMITTED) == false)
            throw new DBLockNotSupportedException (String.format (
                "Select NoLock DB unsupported on %1$s",
                connection.getCatalog ()));

          currentTransactionIsolation = connection.getTransactionIsolation ();
          connection.setTransactionIsolation (Connection.TRANSACTION_READ_UNCOMMITTED);
        }
        // main operator!
        @SuppressWarnings ("unused")
        boolean gotResultSet = st.execute ();
        appendFile (goodResultFileName, updatedQuery);
        goodUpdates++;

      } catch (SQLException ex) {
        _logger.error (String.format (messageTemplate, "SQLException", updatedQuery, ex.getMessage ()));
        appendNeededFile (reformulatedQuery, updatedQuery, count);
        badUpdates++;

        count++;
      } catch (Exception ex) {
        _logger.error (String.format (messageTemplate, ex.getClass ()
            .getName (), updatedQuery, updatedQuery, ex
            .getMessage ()));
        appendNeededFile (reformulatedQuery, updatedQuery, count);
        badUpdates++;

        count++;
      } finally {
        try {
          if (currentTransactionIsolation != -1)
            connection.setTransactionIsolation (currentTransactionIsolation);
        } catch (SQLException ex) {
          _logger.error (String.format (messageTemplate, "setTransactionIsolation exception",
              null, null, ex.getMessage ()));
          throw new ReturnStatusException (ex);
        } finally {
          try {
            st.close ();
          } catch (Throwable t) {
            _logger.error (t.getMessage ());
          }
        }
      }
    }
    String text = goodUpdates + ", " + badUpdates + ", " + (goodUpdates + badUpdates);
    appendFile (root + statistics, text);
  }

  //
  private void appendNeededFile (String inputQuery, String outputQuery, int count)
  {
    String tmp = "error ";
    String path = null;
    if (isStartWithSelect (inputQuery))
    {
      tmp = tmp + "select " + statementsFile;
    }
    else if (isStartWithUpdate (inputQuery))
    {
      tmp = tmp + "update " + statementsFile;
    }
    else if (isStartWithInsert (inputQuery))
    {
      tmp = tmp + "insert " + statementsFile;
    }
    else
    {
      tmp = tmp + "other " + statementsFile;
    }
    path = root + tmp;
    appendFile (path, inputQuery);
    appendFile (path, count + ": " + outputQuery);

  }

  //
  private boolean isStartWith (String query, String start1, String start2)
  {
    int maxl = Math.max (start1.length (), start2.length ());
    String tmp = query.substring (0, maxl).toLowerCase ();
    return tmp.startsWith (start1.toLowerCase ()) || tmp.startsWith (start2.toLowerCase ());
  }

  //
  private boolean isStartWithSelect (String query)
  {
    return isStartWith (query, SELECT, rbSelect);
  }

  //
  private boolean isStartWithUpdate (String query)
  {
    String tmp = query.substring (0, 10).toLowerCase ();
    return isStartWith (tmp, UPDATE, rbUpdate);
  }

  //
  private boolean isStartWithInsert (String query)
  {
    String tmp = query.substring (0, 10).toLowerCase ();
    return isStartWith (tmp, INSERT, rbInsert);
  }

  //
  private boolean isQueryContainsString (String query, String subst)
  {
    String tmp = query.toLowerCase ();
    return tmp.contains (subst.toLowerCase ());
  }

  //
  private int getUnsensitiveIndex (String query, String subst)
  {
    String tmp = query.toLowerCase ();
    String subTmp = subst.toLowerCase ();
    return tmp.indexOf (subTmp);
  }

  //
  private int getUnsensitiveIndex (String query, String subst, int begin)
  {
    String tmp = query.toLowerCase ();
    String subTmp = subst.toLowerCase ();
    return tmp.indexOf (subTmp, begin);
  }

  //
  private int getUnsensitiveLastIndex (String query, String subst)
  {
    String tmp = query.toLowerCase ();
    String subTmp = subst.toLowerCase ();
    return tmp.lastIndexOf (subTmp);
  }

  // this is main recursion function
  public List<String> getUpdatedQueries (String _query) throws Exception
  {
    List<String> resQuery = new ArrayList<String> ();
    String query = _query.trim ();
    if(isQueryContainsString (query, unittestflagName))
    {
      // to do nothing
    }
    else if (isQueryContainsString (query, notExists))
    {
      appendFile (root + notExists + statementsFile, query);
    }
    else if (isQueryContainsString (query, exists))
    {
      appendFile (root + exists + statementsFile, query);
    }
    else if (isStartWithSelect (query))
    {
      resQuery = transformSelectStatement (query);
    }
    else if (isStartWithUpdate (query))
    {
      if (query.length () > MAXLENGTH)
      {
        appendFile (otherFileName, query);
      }
      else
      {
        int index = getRealIndexOfSelect (query, 0);
        if (index > 0)
        {
          String query2 = cutSelectFromQuery (query);
          if (getRealIndexOfSelect (query, 0) > 0)
          {
            if (query.charAt (index - 1) == spaceChar)
            { // I cut from index to end
              query2 = query.substring (index);
            }
            else if (query.charAt (index - 1) == leftBracketChar)
            {
              query2 = query.substring (index - 1);
            }
            else
            {// there is left bracket before
              query2 = cutSubSelectStatement (query);
            }
            resQuery = transformSelectStatement (query2);
          }
        }
        else
        {
          resQuery = singleUpdateStatementTransformer (query);
        }
      }
    } else if (isStartWithInsert (query))
    {
      appendFile (root + " insert " + statementsFile, query);
    } else
    {
      if (!query.isEmpty () && !query.equals (ls))
      {
        appendFile (otherFileName, query);
      }
    }
    return resQuery;
  }

  // for insert and update
  private String cutSelectFromQuery (String query)
  {
    int selectPos = getRealIndexOfSelect (query, 1);
    return query.substring (selectPos);

  }

  //
  private Map<String, String> getAliasMap (String[] tbls) throws Exception
  {
    Map<String, String> tbles = new HashMap<String, String> ();
    String[] tmp = null;
    String alias = null;
    String value = null;

    for (int i = 0; i < tbls.length; i++)
    {
      tmp = tbls[i].trim ().split (" ");
      if (tmp.length == 2)
      {
        alias = tmp[1].trim ();
        value = tmp[0].trim ();
        tbles.put (alias, value);
      }
      else if (tmp.length == 1)
      {
        tbles.put (tmp[0], tmp[0]);
      }
      else
      {
        throw new Exception ("table name is empty: ");
      }
    }
    return tbles;
  }

  private List<String> transformSelectStatement (String query) throws Exception
  {
    List<String> resQueries = new ArrayList<String> ();
    int numberSS = numberSelectStatements (query);
    if (numberSS == 1)
    {

      resQueries = singleSelectStatementTransformer (query, true);

    } else { // many select in this query
      resQueries = muitipleSelectStatementTransformer (query);
    }
    return resQueries;
  }

  // recursive
  private List<String> muitipleSelectStatementTransformer (String query) throws Exception
  {
    List<String> result = new ArrayList<String> ();
    String[] qrs = null;
    String tmp = null;
    if (query.contains (UNIONALL))
    {
      qrs = query.split (UNIONALL);
      for (int i = 0; i < qrs.length; i++)
      {
        tmp = cutOuterBrackets (qrs[i].trim ());
        result.addAll (muitipleSelectStatementTransformer (tmp));
      }
    } else {
      if (numberSelectStatements (query) > 1)
      {// I cut only first subselect statement
        query = query.trim ();
        String subst = cutSubSelectStatement (query);
        result.addAll (muitipleSelectStatementTransformer (subst.trim ()));
      }
      result.addAll (singleSelectStatementTransformer (query, false));
    }
    return result;
  }

  //
  private List<String> singleSelectStatementTransformer (String query, boolean isSingle) throws Exception
  {
    List<String> result = new ArrayList<String> ();

    int selectPosition = getUnsensitiveIndex (query, SELECT);

    int fromPosition = getUnsensitiveIndex (query, FROM);// query.indexOf
                                                         // (FROM);

    String tmp = query.substring (selectPosition + 6, fromPosition);

    if (tmp.contains ("count(*)"))
    {
      appendFile (selectStarFileName, query);
    }
    else {
      if (fromPosition < 0)
      {
        appendFile (otherFileName, query);
        return null;
      }

      int wherePosition = getUnsensitiveIndex (query, WHERE);// query.indexOf
                                                             // (WHERE);
      int limitPosition = getUnsensitiveIndex (query, LIMIT);// query.indexOf
                                                             // (LIMIT);
      // get names of the tables
      String tablesPart = null;
      String limitPart = null;
      if (wherePosition > 0)
      {
        tablesPart = query.substring (fromPosition + 4, wherePosition);
      }
      else
      {
        if (limitPosition < 0)
        {
          tablesPart = query.substring (fromPosition + 4).trim ();
        }
        else // limitPosition > 0 TODO: check limitPosition > fromPosition
        {
          tablesPart = query.substring (fromPosition + 4, limitPosition);
          limitPart = query.substring (limitPosition);
          int rightBracketIndex = getUnsensitiveIndex (limitPart, rightBracket);// limitPart.indexOf
                                                                                // (rightBracket);
          if (rightBracketIndex > 0)
          {
            limitPart = limitPart.substring (0, rightBracketIndex - 1);
          }
        }
      }

      // get conditions
      String condition = null;
      if (wherePosition > 0)
      {
        condition = query.substring (wherePosition);
        if (limitPosition > 0)
        // limitPosition > 0 TODO: check limitPosition > fromPosition
        {
          condition = query.substring (wherePosition, limitPosition);
        }
        if (condition.contains ("order"))
        {
          condition = condition.substring (0, getUnsensitiveIndex (condition, "order"));// condition.indexOf
                                                                                        // ("order"));
        }
      }
      else
      {
        condition = space;
      }
      // write update queries in file
      result = getUpdateStatement (tablesPart, condition, limitPart, isSingle);
    }
    return result;
  }

  //
  private List<String> getUpdateStatement (String tablesPart, String condition,
      String limitPart, boolean isSingle) throws Exception
  {
    
    Map<String, String> aliasToNameCurrent   = new HashMap<String, String> ();

    String[] tbls = tablesPart.split (",");
    Map<String, String> tables = getAliasMap (tbls);
    for (String alias : tables.keySet ())
    {
      aliasToNameCurrent.put (alias, tables.get (alias));
      aliasToName.put (alias, tables.get (alias));
    }
    List<String> result = new ArrayList<String> ();

    String tablesPart2 = space;
    int cnt = 0;
    for (String alias : aliasToName.keySet ())
    {

      if (cnt > 0)
      {
        tablesPart2 = tablesPart2 + ",";

      }
      cnt++;
      if (!alias.equals (aliasToName.get (alias)))
      {
        tablesPart2 = tablesPart2 + space + aliasToName.get (alias) + space + alias;
      }
      else
      {
        tablesPart2 = tablesPart2 + space + aliasToName.get (alias);
      }
    }
    String res = UPDATE + space;
    if (isSingle)
    {
      res = res + tablesPart + space + ls;
    }
    else
    {
      res = res + tablesPart2 + space + ls;
    }
    for (String alias1 : aliasToNameCurrent.keySet ())
    {
      String res1 = new String (res);
      if (views.contains (aliasToNameCurrent.get (alias1)))
      {
        continue;
      }
      res1 = res1 + tab8 + SET + space + alias1 + "." + unittestflagValue + space + ls;
      if (condition != null)
      {
        res1 = res1 + tab8 + condition;
      }
      if (limitPart != null)
      {
        res1 = res1 + space + limitPart;
      }
      result.add (res1);
    }
    return result;
  }

  //
  private List<String> singleUpdateStatementTransformer (String query) throws Exception
  {
    int updatePosition = getUnsensitiveIndex (query, UPDATE) + 6;// query.indexOf
                                                                 // (UPDATE) +
                                                                 // 6;
    int setPosition = getUnsensitiveIndex (query, SET);// query.indexOf (SET);
    if (setPosition < 0)
      setPosition = query.length ();
    int wherePosition = getUnsensitiveLastIndex (query, WHERE);// query.lastIndexOf
                                                               // (WHERE);
    // get names of the tables
    String tablesPart = query.substring (updatePosition, setPosition);

    // get conditions
    String condition = "";
    if (wherePosition > 0)
      condition = query.substring (wherePosition);

    return getUpdateStatement (tablesPart, condition, null, true);
  }


  public void appendLogFile (String text)
  {
    appendFile (logName, text);
  }

  private void appendFile (String fileName, String text)
  {
    try
    {
      FileWriter fw = new FileWriter (fileName, true); // the true will append
                                                       // the new data
      fw.write (text); // appends the string to the file
      fw.write (ls);
      //fw.write (ls);

      fw.close ();
    } catch (IOException ioe)
    {
      System.err.println ("IOException: " + ioe.getMessage ());
    }
  }

  // private int numberSelectStatements2 (String selectQuery)
  // {
  // int count = 0;
  // int index = 1;
  // while (index > 0)
  // {
  // index = getRealIndexOfSelect (selectQuery, index);
  // if (index > 0)
  // {
  // count++;
  // index++;
  // }
  // }
  // return count;
  // }

  //
  private int numberSelectStatements (String selectQuery)
  {
    int result = 0;
    int bPos = 0;
    int ePos = 0;
    String tmp = selectQuery;
    char[] dstB = new char[1];
    char[] dstE = new char[1];
    while (isQueryContainsString (tmp, SELECT))// (tmp.contains (SELECT))
    {
      bPos = getUnsensitiveIndex (tmp, SELECT);// tmp.indexOf (SELECT);
      ePos = bPos + SELECT.length ();
      if (bPos > 0)
        tmp.getChars (bPos - 1, bPos, dstB, 0);

      tmp.getChars (ePos, ePos + 1, dstE, 0);
      char ttmpB = dstB[0];
      char ttmpE = dstE[0];
      if ((bPos == 0
          || ttmpB == ' '
          || ttmpB == '(')
          && (ttmpE == ' '))
      {
        result++;
      }
      tmp = tmp.substring (bPos + SELECT.length ());
    }
    return result;
  }

  private int getBracketCount (String st, int beginPos)
  {
    int indexRightBracket = st.indexOf (rightBracket, beginPos);
    int indexLeftBracket = st.indexOf (leftBracket, beginPos);

    if (indexRightBracket < 0 && indexLeftBracket < 0)
      return -1000;
    if (indexRightBracket < indexLeftBracket || indexLeftBracket < 0)
    {
      return -1;
    }
    else
      return 1;
  }

  // I suppose that I trimed query already and query starts with left bracket
  // "("
  private String cutOuterBrackets (String query)
  {
    int firstIndex = query.indexOf (leftBracket);
    int lastIndex = query.lastIndexOf (rightBracket);

    if (firstIndex == 0 && lastIndex == query.length () - 1)
      return query.substring (1, query.length () - 1);
    else
      return query;

  }

  // I suppose that I trimed query already
  private boolean startsWithBracket (String query)
  {
    return query.startsWith (leftBracket);
  }
/**
        int len = value.length;
        int st = 0;
        char[] val = value;    // avoid getfield opcode 

        while ((st < len) && (val[st] <= ' ')) {
            st++;
        }
        while ((st < len) && (val[len - 1] <= ' ')) {
            len--;
        }
        return ((st > 0) || (len < value.length)) ? substring(st, len) : this;

 */
//  private String cutSubSelectStatement2(String query)
//  {
//    String qr = new String(query.trim());
//    char[] val = qr.toCharArray ();
//    int count = 1;
//    int i = 1;
//    if(val[0] == '(')
//    {
//      while(count > 0)
//      {
//        if(val[i] == ')')
//          count--;
//        
//        if(val[i] == '(')
//          count++;
//        i++;
//      }
//      return qr.substring (0, i);
//    }
//    else
//    {
//      //TODO
//      return qr;
//    }  
//  }
  // I suppose that all subselect statements begin with "(select" and finish
  // with ")"
  private String cutSubSelectStatement (String query)
  {
    query.trim ();
    String qr = new String (query);
    boolean isStartWithBracket = startsWithBracket (qr);
    if (isStartWithBracket)
    {
      qr = cutOuterBrackets (qr);
    }
    int indexSelect = getRealIndexOfSelect (qr, 0);
    if (indexSelect == 0) // find not first "select in query
    {
      indexSelect = getRealIndexOfSelect (qr, 6);
    }
    if (indexSelect < 0)
    {
      return null;
    }
    else
    {
      // cut subselect statement. I suppose that subselect statement has
      // structure
      // "(select ...)"
      int currentIndex = indexSelect;// this is index of first left bracket + 1
      int currentBracketCount = 0;
      int count = 1; // this is count of the first left bracket
      while (count > 0)
      {
        currentBracketCount = getBracketCount (qr, currentIndex + 1);
        if (currentBracketCount > 0) // left bracket meets first
        {
          currentIndex = qr.indexOf (leftBracketChar, currentIndex + 1); // TODO
                                                                         // check
                                                                         // it
        }
        else // right bracket meets first
        {
          currentIndex = qr.indexOf (rightBracketChar, currentIndex + 1); // TODO
                                                                          // check
                                                                          // it
          if (currentIndex < 0)
            currentIndex = qr.length () - 1;
        }
        count += currentBracketCount;
      }

      qr = qr.substring (indexSelect, currentIndex);
    }
    return qr;
  }

  // return real indexOf "select". Select statement can contain word like
  // "isselected", "selectedpos", and so on
  private int getRealIndexOfSelect (String query, int beginPos)
  {
    int index = getUnsensitiveIndex (query, SELECT, beginPos);// query.indexOf
                                                              // (SELECT,
                                                              // beginPos);

    if (index < 0)
      return index;
    if ((index > 0
        && (query.charAt (index - 1) == leftBracketChar || query.charAt (index - 1) == spaceChar))
        && query.charAt (index + 6) == spaceChar)
    {
      return index;
    }
    else // this is not "select"
    {
      return getRealIndexOfSelect (query, index + 1);
    }
  }
}
