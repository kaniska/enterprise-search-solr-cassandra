/**
 * 
 */
package com.enterprise.app.mediscene.service;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enterprise.app.mediscene.context.BatchFinalizationRequest;
import com.enterprise.app.mediscene.context.BatchInitializationResponse;
import com.enterprise.app.mediscene.context.BatchRequestContext;
import com.enterprise.app.mediscene.dao.core.BulkRequestDAO;
import com.enterprise.app.mediscene.util.StringUtil;
import com.enterprise.app.mediscene.util.Constants;

/**
 * @author Kaniska_Mandal
 * 
 * Life Cycle of a Batch Request 
 * (i) create Batch Request Context in Rest Controller
 * (ii) initialize the batch process
 *      >> send back the response to Feedback Processing Pipeline 
 * (iii) (a) For sequential records : 
 * 		-- log pre-process status, perform CRUD, log in-process error if any / log post-process status
 *      (b) For group of records
 *      -- log the group request info, perform CRUD
 *      >> send back the response to Feedback Processing Pipeline
 * (iv) finalize the batch process
 *      >> send back the response to Feedback Processing Pipeline  
 * Creation/Modification/Deletion --> log InProcess Error -> log PostProcess Status
 */

@Component("batchRequestService")
public class BatchRequestServiceImpl implements BatchRequestService {

    //private static final MediSceneLogger log = new MediSceneLogger(BatchRequestServiceImpl.class);

	@Autowired
	private BulkRequestDAO bulkRequestDAO;
	
	///////  CREATE RECORDS ///////
	public void handleBatchInsertion(BatchRequestContext requestContext) throws Exception {
		int batchId = bulkRequestDAO.initializeBatch();
		requestContext.setBatchid(batchId);
		
		bulkRequestDAO.insertBatch(requestContext);	
		
		bulkRequestDAO.finalizeBatch(batchId);
	}

	/**
	 * @throws SQLException 
	 * 
	 */
	public void logInProcessError(BatchRequestContext requestContext, String errorCode,
			String errorLog) throws Exception {
		 //bulkRequestDAO.logInProcessError(requestContext, errorCode, errorLog);
	}


}
