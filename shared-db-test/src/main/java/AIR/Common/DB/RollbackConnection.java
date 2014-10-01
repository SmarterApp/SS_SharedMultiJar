/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.DB;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;
import java.util.Vector;

public class RollbackConnection extends SQLConnection {

	private List<Savepoint> _existingSavepoints = new Vector<Savepoint> ();
	private int _transactionCounter = 0;

	public RollbackConnection(Connection conn) throws SQLException {
		super(conn);
		//starting a transaction that will be rolled back upon closing the connection
		setAutoCommit(false);		
	}

	@Override
	public void setAutoCommit (boolean autoCommit) throws SQLException {
		if (!autoCommit) {
			//beginning of new or nested transaction here. Create new savepoint
			super.setAutoCommit(false);
			Savepoint svp = super.setSavepoint(); //unnamed
			_existingSavepoints.add(_transactionCounter, svp);
			_transactionCounter++;
		}
		else {
			//do nothing
		}
	}

	@Override
	public boolean getAutoCommit () throws SQLException {
		return true; 
	}

	@Override
	public void commit () throws SQLException {
		_transactionCounter--;
	}

	@Override
	public void rollback () throws SQLException {
		_transactionCounter--;
		if (_transactionCounter >= 0 && _existingSavepoints.size() > _transactionCounter) {
			super.rollback(_existingSavepoints.get(_transactionCounter));
		}
		else {
			super.rollback();
		}
	}

	@Override
	public void close () throws SQLException {
		_transactionCounter = 0; //make sure we are rolling over the highest level transaction
		rollback();
		super.setAutoCommit(true);
		super.close();
	}
	
	@Override
	public Savepoint setSavepoint () throws SQLException {
		return super.setSavepoint ();
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		return super.setSavepoint(name);
	}
	
	@Override
	public void rollback (Savepoint savepoint) throws SQLException {
		super.rollback (savepoint);
	}

	@Override
	public void releaseSavepoint (Savepoint savepoint) throws SQLException {
		super.releaseSavepoint (savepoint);
	}

	@Override
	protected void finalize () throws Throwable {
		this.close ();
		super.finalize ();
	}
}
