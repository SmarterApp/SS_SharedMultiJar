/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.DB.results;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import AIR.Common.DB.SQL_TYPE_To_JAVA_TYPE;
import AIR.Common.Helpers.CaseInsensitiveMap;
import AIR.Common.Helpers.Constants;
import AIR.Common.Helpers._Ref;
import TDS.Shared.Data.ColumnResultSet;
import TDS.Shared.Exceptions.RuntimeReturnStatusException;

public class DbResultRecord
{
  private SingleDataResultSet _holder = null;
  private Object[]            _values = null;

  public _Ref<Integer> getColumnToIndex (String columnName) {
    return _holder.getColumnToIndex (columnName);
  }

  public _Ref<String> getIndexToColumn (int columnIndex) {
    return _holder.getIndexToColumn (columnIndex);
  }

  public Iterator<String> getColumnNames () {
    return _holder.getColumnNames ();
  }

  public int getNumberOfColumns ()
  {
    return _holder.getNumberOfColumns ();
  }

  /** 
   * @return true if calling class of a derivative of AbstractDLL. Traces stack and uses Java reflection.
   */
  private boolean amIDLL () {
    StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
    for (int i=stackTraceElements.length-1; i>=0; i--){
      try {
        if (Class.forName(stackTraceElements[i].getClassName()).getSuperclass().getCanonicalName().contains("AbstractDLL"))
          return true;
      } catch (ClassNotFoundException e) {
      }
    }
    return false;
  }
  

  @SuppressWarnings ("unchecked")
  public <T> T get (int columnIndex)
  {
    
    if (columnIndex <= _values.length) {
      SQL_TYPE_To_JAVA_TYPE columnType = _holder.getSqlTypeMapEnum (columnIndex);
      Object value = _values[columnIndex];
      
      if (value == null && !amIDLL()) {
        if (_holder.getFixNulls ()== true) {
          if (columnType.matchesMyJavaType (UUID.class)) {
            return (T) Constants.UUIDEmpty;
          } else if (columnType.matchesMyJavaType (String.class)) {
            return (T) "";
          } else if (columnType.matchesMyJavaType (Date.class)) {
            return (T) (new Date (0));
          } 
        }
        if (columnType.matchesMyJavaType (Boolean.class)) {
          return (T) (new Boolean (false));
        } else if (columnType.matchesMyJavaType (Long.class)) {
          return (T) (new Long (0));
        } else if (columnType.matchesMyJavaType (Integer.class)) {
          return (T) (new Integer (0));
        } else if (columnType.matchesMyJavaType (Float.class)) {
          return (T) (new Float (0));
        }
      }
      return (T) value;
    }
    throw new RuntimeReturnStatusException (String.format ("Column %d requested exceeds the number of columns in this record. Available columns are %d", columnIndex, _values.length));
  }

  public <T> T get (String columnName)
  {
    return this.<T> get (getColumnToIndex (columnName).get ());
  }

  public boolean hasColumn (String columnName)
  {
    return _holder.hasColumn (columnName);
  }

  public DbResultRecord addColumnValue (String columnName, Object value)
  {
    // because JDBC columns are 1 indexed. we do the same thing in the
    // constructor for this object.
    int getNumberOfColumns = _holder.getNumberOfColumns () + 1;
    increaseSize (getNumberOfColumns);
    _values[getColumnToIndex (columnName).get ()] = value;
    return this;
  }

  protected DbResultRecord (SingleDataResultSet holder, CaseInsensitiveMap<Object> map)
  {
    this._holder = holder;
    // column indexes in jdbc start with 1.
    int numberOfColumns = this.getNumberOfColumns ();
    _values = new Object[numberOfColumns + 1];
    for (int counter = 1; counter <= numberOfColumns; ++counter)
    {
      _values[counter] = map.get (_holder.getIndexToColumn (counter));
    }
  }

  protected DbResultRecord (SingleDataResultSet holder, ColumnResultSet reader) throws SQLException {
    this._holder = holder;
    // column indexes in jdbc start with 1.
    _values = new Object[_holder.getNumberOfColumns () + 1];
    load (reader);
  }

  private void load (ColumnResultSet reader) throws SQLException
  {
    for (int column = 1; column <= _holder.getNumberOfColumns (); ++column)
    {
      String columnName = getIndexToColumn (column).get ();
      SQL_TYPE_To_JAVA_TYPE tmp = _holder.getSqlTypeMapEnum (columnName);
      _values[column] = tmp.read (reader, column);
      // _values[column] = _holder.getSqlTypeMapEnum (columnName).read (reader,
      // column);
    }
  }

  // we will reallocate the array with the larger space.
  // TODO Elena/Shiva verify boundary cases here.
  private void increaseSize (int newSize)
  {
    if (newSize > _values.length)
      _values = Arrays.<Object> copyOf (_values, newSize);

  }

  public static void main (String[] args)
  {
    Date nullDate = null;
    Object value = nullDate;
    if (value == null) {
      if (value instanceof UUID) {
        System.err.println (Constants.UUIDEmpty.toString ());
      } else if (value instanceof String) {
        System.err.println ("I am a string.");
      } else if (value instanceof Date) {
        System.err.println (new Date (0).toString ());
      }
      else
      {
        System.err.println ("I have an existential crisis.");
      }
    }
  }
}
