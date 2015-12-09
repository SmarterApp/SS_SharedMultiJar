/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.DB.results;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import AIR.Common.DB.InvalidDataBaseTypeSpecification;
import AIR.Common.DB.SQL_TYPE_To_JAVA_TYPE;
import AIR.Common.Helpers.CaseInsensitiveBiMap;
import AIR.Common.Helpers.CaseInsensitiveMap;
import AIR.Common.Helpers._Ref;
import TDS.Shared.Data.ColumnResultSet;
import TDS.Shared.Data.ReturnStatus;
import TDS.Shared.Exceptions.ReturnStatusException;
import TDS.Shared.Exceptions.RuntimeReturnStatusException;

public class SingleDataResultSet
{
  // there may be only one in any result set and mostly likely will be count.
  private List<DbResultRecord>                _rows                 = new ArrayList<DbResultRecord> ();
  // TODO Shiva look at the possibility of using Guava :
  // https://code.google.com/p/guava-libraries/
  private CaseInsensitiveBiMap<Integer>       _columnNameToIndexMap = new CaseInsensitiveBiMap<Integer> ();
  private Map<Integer, SQL_TYPE_To_JAVA_TYPE> _indexToJavaTypeMap   = new HashMap<Integer, SQL_TYPE_To_JAVA_TYPE> ();
  private boolean                             _fixNulls             = false;
  private boolean                             _fixMissing           = false;
  private boolean                             _returnStatusCheck    = false;

  protected SingleDataResultSet (ColumnResultSet reader) throws SQLException {
    try {
      reader.checkReturnStatus ();
    } catch (ReturnStatusException e) {
      _returnStatusCheck = true;
    }
    load (reader);
  }

  public boolean getReturnStatusCheck () {
    return _returnStatusCheck;
  }

  public SingleDataResultSet () {
    
  }
  
  @Deprecated
  /**
   * @deprecated
   * @see SingleDataResultSet() and addColumn (?,?) and addRecords(List<?>)
   */
  public SingleDataResultSet (List<CaseInsensitiveMap<Object>> resultList) throws ReturnStatusException {
    if (resultList == null)
      throw new ReturnStatusException ("List cannot be null");
    int maxColumnCount = 0;
    for (CaseInsensitiveMap<Object> result : resultList) {
      // first run through all the keys and create column declarations.
      // we do it this way so that we do not miss columns that exist for only a
      // handlful of records
      // but do not exist in the first record.
      for (String key : result.keySet ())
      {
        if (!_columnNameToIndexMap.containsKey (key))
        {
          // jdbc column indices start from 1.
          ++maxColumnCount;
          _columnNameToIndexMap.put (key, maxColumnCount);
        }
      }
    }
    addRecords (resultList);
  }

  public void addRecords (List<CaseInsensitiveMap<Object>>resultList) throws ReturnStatusException {
    if (resultList.size () == 0)
      return;
    // this method only adds records with columns already defined in this set,
    // for example, by calling 'addColumn'
    // If any record contains unknown column, we skip the whole set
    for (CaseInsensitiveMap<Object> result : resultList) {
      for (String key : result.keySet ()) {
        if (!_columnNameToIndexMap.containsKey (key)) {
          throw new ReturnStatusException (String.format("Records to be added contain unknown column %s",key));
        }
      }
      _rows.add (new DbResultRecord (this, result));
    }
    
    if (!_returnStatusCheck && hasColumn ("status") && hasColumn("reason") && _rows.size() > 0 && 
        ("failed".equalsIgnoreCase((_rows.get(0)).<String> get ("status")) ||
         "denied".equalsIgnoreCase((_rows.get(0)).<String> get ("status"))    )
        )
        _returnStatusCheck = true;
  }
  
  public boolean getFixNulls () {
    return this._fixNulls;
  }

  // Added the new method to remove a item from the List<DbResultRecord>
  public boolean removeitem (List<DbResultRecord> o) {
    return _rows.remove (o);
  }

  // Added the new method to remove a collections of List<DbResultRecord> from
  // the List<DbResultRecord>
  public void removeAll (List<DbResultRecord> list_of_rows) {
    _rows.removeAll (list_of_rows);
  }

  public void setFixNulls (boolean fixNulls) {
    this._fixNulls = fixNulls;
  }

  public void setFixMissing (boolean fixMissing) {
	  this._fixMissing = fixMissing;
  }
  
  public _Ref<Integer> getColumnToIndex (String columnName) {
    if (_columnNameToIndexMap.containsKey (columnName))
      return new _Ref<Integer> (_columnNameToIndexMap.get (columnName));
    throw new RuntimeReturnStatusException (String.format ("Column %s does not exist in this result set.", columnName));
  }

  public _Ref<String> getIndexToColumn (int columnIndex) {
    return new _Ref<String> (_columnNameToIndexMap.inverseGet (columnIndex));
  }

  public Iterator<String> getColumnNames () {
    return _columnNameToIndexMap.keySet ().iterator ();
  }

  public boolean hasColumn (String columnName)
  {
    return _columnNameToIndexMap.containsKey (columnName);
  }

  public int getCount ()
  {
    return _rows.size ();
  }

  public Iterator<DbResultRecord> getRecords ()
  {
    return _rows.iterator ();
  }

  // TODO Shiva / Elena also pass in a type so that all records have consistent
  // datatype for that column.
  public int addColumn (String column, SQL_TYPE_To_JAVA_TYPE type)
  {
    // the +1 is to account for the fact that our column indices are 1 based
    // rather than 0 based.
    // TODO Elena/Shiva a more bug-free approach would be to find out the max
    // columnIndex we have in our mapping.
    int existingNumberOfColumns = getNumberOfColumns ();
    int newColumnIndex = existingNumberOfColumns + 1;
    _columnNameToIndexMap.put (column, newColumnIndex);
    _indexToJavaTypeMap.put (newColumnIndex, type);
    
    return newColumnIndex;
  }

  public int getNumberOfColumns () {
    return _columnNameToIndexMap.size ();
  }

  public SQL_TYPE_To_JAVA_TYPE getSqlTypeMapEnum (String columnName)
  {
    _Ref<Integer> index = getColumnToIndex (columnName);
    return _indexToJavaTypeMap.get (index.get ());
  }

  public SQL_TYPE_To_JAVA_TYPE getSqlTypeMapEnum (int columnIndex)
  {
    return _indexToJavaTypeMap.get (columnIndex);
  }

  public void resetColumnName (int columnPosition, String newColumnName)
  {
    _columnNameToIndexMap.remove (_columnNameToIndexMap.inverseGet (columnPosition));
    _columnNameToIndexMap.put (newColumnName, columnPosition);
  }

  private void load (ColumnResultSet reader) throws SQLException {
    // first build the column names.
    ResultSetMetaData rsMetaData = reader.getMetaData ();

    int numberOfColumns = rsMetaData.getColumnCount ();
    // get each column name and
    for (int counter1 = 1; counter1 <= numberOfColumns; ++counter1) {
      // String columnName = rsMetaData.getColumnName (counter1);
      String columnName = rsMetaData.getColumnLabel (counter1);

      if (_columnNameToIndexMap.containsKey (columnName))
      {
        // TODO Shiva should we throw an exception if there is ambiguity in
        // column names?
        // throw new ColumnAlreadyMappedException
        // (String.format("%1$s has already been added. Column index %2$d. Please use an alias for this column in the SQL query.",
        // columnName, counter1));

        // modify the column name for the time being.
        columnName = String.format ("%1$s_%2$s", columnName, UUID.randomUUID ().toString ());
      }
      _columnNameToIndexMap.put (columnName, counter1);
      String tmp1 = rsMetaData.getColumnTypeName (counter1);
      SQL_TYPE_To_JAVA_TYPE tmp2 = null;
      try{
        tmp2 = SQL_TYPE_To_JAVA_TYPE.getType (tmp1);
      } catch (InvalidDataBaseTypeSpecification e)
      {
        throw new InvalidDataBaseTypeSpecification (String.format (
            " %1$s in column %2$s", e.getMessage (), columnName));
      }
      _indexToJavaTypeMap.put (counter1, tmp2);
      // _indexToJavaTypeMap.put (counter1, SQL_TYPE_To_JAVA_TYPE.getType
      // (rsMetaData.getColumnTypeName (counter1)));
    }

    while (reader.next ()) {
      _rows.add (new DbResultRecord (this, reader));
    }
  }
}
