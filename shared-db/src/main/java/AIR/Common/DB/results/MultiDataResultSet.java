/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.DB.results;

import java.lang.invoke.WrongMethodTypeException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import TDS.Shared.Data.ColumnResultSet;

/**
 * @author sbehera
 * 
 */
public class MultiDataResultSet
{
  private boolean                   _hasResults  = false;
  private List<SingleDataResultSet> _resultsSets = new ArrayList<SingleDataResultSet> ();
  private int                       _updateCount = -1;
  private boolean                   _fixNulls    = false;

  public MultiDataResultSet (List<SingleDataResultSet> resultsSets) {
    _resultsSets = resultsSets;
    _updateCount = resultsSets.size ();
    for (SingleDataResultSet result : resultsSets) {
      _hasResults = _hasResults || result.getCount () > 0;
    }
  }

  public MultiDataResultSet (Statement st) throws SQLException {
    load (st);
  }

  public boolean getFixNulls () {
    return this._fixNulls;
  }

  public void setFixNulls (boolean fixNulls) {
    this._fixNulls = fixNulls;
    for (SingleDataResultSet result : this._resultsSets) {
      result.setFixNulls (fixNulls);
    }
  }

  /**
   * If this is a update query then it will give the count of the updates. -1 if
   * not.
   * 
   * @return
   */
  public int getUpdateCount () {
    return _updateCount;
  }

  public boolean hasResults () {
    return _hasResults;
  }

  public Iterator<SingleDataResultSet> getResultSets () {
    if (_resultsSets.size () == 0)
      throw new WrongMethodTypeException ("No results. Possibly an update query. Check getUpdateCount() instead.");
    return _resultsSets.iterator ();
  }

  /**
   * 
   * Gets the number of {@link SingleDataResultSet} objects in this
   * {@link MultiDataResultSet} object
   * 
   * @return The number of {@link SingleDataResultSet} objects
   * @author Tongliang LIU [tliu@air.org]
   * @since April 24th, 2014
   */
  public int getCount () {
    return this._resultsSets.size ();
  }

  /**
   * Gets the {@link SingleDataResultSet} object with index = {@code index}
   * 
   * @param index
   * @return A {@link SingleDataResultSet} object if the given {@code index} is
   *         valid;<br/>
   *         Otherwise, {@code null}
   * @author Tongliang LIU [tliu@air.org]
   * @since April 24th, 2014
   */
  public SingleDataResultSet get (int index) {
    if (index < 0 || index > this._resultsSets.size ())
      return null;

    return this._resultsSets.get (index);
  }

  private void load (Statement st) throws SQLException {
    _updateCount = st.getUpdateCount ();
    while (st.getResultSet () != null) {
      ColumnResultSet reader = ColumnResultSet.getColumnResultSet (st.getResultSet ());
      SingleDataResultSet result = new SingleDataResultSet (reader);
      _resultsSets.add (result);
      // did we see any results?
      _hasResults = _hasResults || result.getCount () > 0;
      if (!st.getMoreResults ())
        break;
    }
  }

}
