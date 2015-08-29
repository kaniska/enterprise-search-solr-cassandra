/**
 * 
 */
package com.enterprise.app.mediscene.service;

import java.sql.SQLException;

import com.enterprise.app.mediscene.context.BatchInitializationResponse;
import com.enterprise.app.mediscene.context.BatchRequestContext;

/**
 * @author Kaniska_Mandal
 * 
 */

public interface BatchRequestService {

	/**
	 * 
	 * @param requestContext
	 * @throws Exception
	 */
	public void handleBatchInsertion(BatchRequestContext requestContext)
			throws Exception;

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

	
}
