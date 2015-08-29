/**
 * 
 */
package com.enterprise.app.mediscene.dao.core;

import java.sql.SQLException;

import com.enterprise.app.mediscene.context.BatchFinalizationRequest;
import com.enterprise.app.mediscene.context.BatchInitializationResponse;
import com.enterprise.app.mediscene.context.BatchRequestContext;

/**
 * @author Kaniska_Mandal
 * Batch Process Life Cycle :
 * (i) Initialize Batch Process
 * (ii) Create/Update/Delete Groups of records for each Entity for a correspoding Batch
 * (iii) Finalize the Batch Process
 */
public interface BulkRequestDAO {

	public int initializeBatch() throws Exception;

	public void finalizeBatch(int batchId) throws Exception;

	// ////////REQUEST LOG ////////////////

	/**
	 * 
	 * @param requestContext
	 * @param errorCode
	 * @param errorLog
	 * @throws DataAccessException
	 * @throws DatasourceNotFoundException
	 * @throws SQLException 
	 */
	public void logInProcessError(BatchRequestContext requestContext,
			String errorCode, String errorLog) throws Exception;

	/**
	 * 
	 * @param requestContext
	 * @throws DatasourceNotFoundException
	 * @throws SQLException 
	 * @throws CannotGetJdbcConnectionException 
	 * @throws Exception 
	 */
	public void insertBatch(BatchRequestContext requestContext)
			throws  Exception;

}
