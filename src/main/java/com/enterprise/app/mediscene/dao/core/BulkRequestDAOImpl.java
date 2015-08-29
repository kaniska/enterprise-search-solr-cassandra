/**
 * 
 */
package com.enterprise.app.mediscene.dao.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.time.DateUtils;
import org.apache.solr.common.SolrInputDocument;
import org.dom4j.Node;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import com.enterprise.app.mediscene.context.BatchFinalizationRequest;
import com.enterprise.app.mediscene.context.BatchInitializationResponse;
import com.enterprise.app.mediscene.context.BatchRequestContext;
import com.enterprise.app.mediscene.dao.cassandra.CassandraDao;
import com.enterprise.app.mediscene.dao.solr.SolrDao;
import com.enterprise.app.mediscene.dao.solr.SolrDaoImpl;
import com.enterprise.app.mediscene.parser.XMLParser;
import com.enterprise.app.mediscene.util.BatchScheduler;
import com.enterprise.app.mediscene.util.Constants;
import com.enterprise.app.mediscene.util.CustomJdbcCommand;


/**
 * @author Kaniska_Mandal
 * 
 *         This is the DAO class that performs the following operations : (i)
 *         initializes / finalizes batch process (ii)
 *         inserts/deletes/updates/reads records (iii) inserts/updates the
 *         request metadata
 */
@Repository("batchRequestDAO")
public class BulkRequestDAOImpl implements BulkRequestDAO {

	//private static final MediSceneLogger log = new MediSceneLogger(BulkRequestDAOImpl.class);

	//@Autowired
	//DataSource dataSource;
	
	@Autowired
	CassandraDao cassandraDao;
	
	@Autowired
	SolrDao solrDao;

	@SuppressWarnings("unchecked")
	public int initializeBatch()
			throws Exception {
		// Get spring jdbc command
		/*SimpleJdbcInsert command = (new CustomJdbcCommand(
				dataSource).withTableName("batch_log")
				.usingGeneratedKeyColumns("pkey"));

		Map<String, Object> parameters = new HashMap<String, Object>();
		Number num =null;
		try {
			parameters.put(Constants.BATCH_LOG_STATUS, "R");
			parameters.put(Constants.BATCH_REQ_BATCH_TIME_START, new Date());
			 num = command.executeAndReturnKey(parameters);

			// Verify if New Data was created fine
			if (num == null) {
				throw new Exception(
						"Batch Initialization Info not generated properly!");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}*/

/*		log.debug(
				"||| Batch Initialized in Warehouse DB -> %s, running on Host -> %s ",
				dbPropMap.get(DataSourceService.DATABASE_NAME),
				dbPropMap.get(DataSourceService.HOST_NAME));*/

	   
	  // return num.intValue();
		return 1;
		
	}


	@SuppressWarnings({ "unused", "unchecked" })
	/*private boolean rollback(int batchId)
			throws DatasourceNotFoundException {
	    
        // delete records from  Cassandra 
		
	}*/

	/**
	 * 
	 * @param requestContext
	 * @throws DatasourceNotFoundException
	 */
	public void insertBatch(BatchRequestContext requestContext)
			throws Exception {
		XMLParser customXMLParser = requestContext.getcustomXMLParser();
		String tableName = requestContext.getTablename();
		List<Node> list = customXMLParser.selectNodes(tableName);
		
		// persist data in solr
		solrDao.createSolrDocumentsFromXMLNodes(list , tableName);
		
		//TODO - we incur some latency as we repeat the iteration .. this is okay as data ingestion need not be super fast.
		// persist data in cassandra
		cassandraDao.createDBDataFromXMLNodes(list, tableName, requestContext.getBatchid());
		
		customXMLParser.cleanup();
	}


	public void finalizeBatch(int batchId) throws Exception {
		/*List<Object> parameterList = new ArrayList<Object>(1);
		//status
		parameterList.add('S');
		//batch_end_time
		parameterList.add(new DateTime());
		//id
		parameterList.add(Integer.valueOf(batchId));
		// Get spring jdbc command
				SimpleJdbcInsert command = (new CustomJdbcCommand(
						dataSource).withTableName("batch_log"));
				
				SqlParameterValue paramval1 = new SqlParameterValue(java.sql.Types.VARCHAR, 0, 'S');
				SqlParameterValue paramval2 = new SqlParameterValue(java.sql.Types.DATE, 0, new Date());
				SqlParameterValue paramval3 = new SqlParameterValue(java.sql.Types.INTEGER, 0, 1);
				
				((CustomJdbcCommand) command).getJdbcTemplate().update(
						Constants.BATCH_FINAL_SQL, paramval1, paramval2,  paramval3);*/
		/**
		 * log.debug(
				"||| Batch Finalized in Warehouse DB -> %s, running on Host -> %s ",
				dbPropMap.get(DataSourceService.DATABASE_NAME),
				dbPropMap.get(DataSourceService.HOST_NAME));
		 */
	}

	@Override
	public void logInProcessError(BatchRequestContext requestContext,
			String errorCode, String errorLog) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	/*
	// //////// LOG ERROR ////////////////

	public void logInProcessError(BatchRequestContext requestContext,
			String errorCode, String errorLog) throws Exception{

		/*
		log.error("Error::  \n" + errorLog);

		// update the batch process status to 'E'

		// //////////also update the batch info

		List<String> parameterList2 = new ArrayList<String>(1);
		parameterList2.add(String.valueOf(requestContext.getBatchid()));
		parameterList2.add(String.valueOf(requestContext.getAppid()));
		parameterList2.add(String.valueOf(requestContext.getTenantid()));

		((CustomJdbcCommand) getNewJdbcCommand(
				DataSourceService.ComponentDBType.warehousedb,
				customConstants.WAREHOUSE_BATCH_LOG_TABLE_NAME)).getJdbcTemplate()
				.update(customConstants.BATCH_ERROR_LOG_SQL,
						parameterList2.toArray());

		// //TODO - truncate the error-log length
		// createdtime=?, tenantid=? ,batchid=?, appid=?,
		// tablename=?, operation=?, errorcode=?, errorlog=?

		List<String> parameterList1 = new ArrayList<String>(1);
		parameterList1.add(null);
		parameterList1.add(String.valueOf(requestContext.getTenantid()));
		parameterList1.add(String.valueOf(requestContext.getBatchid()));
		parameterList1.add(String.valueOf(requestContext.getAppid()));
		parameterList1.add(requestContext.getTablename());
		parameterList1.add("NEW");
		parameterList1.add(errorCode);
		// String errorString =
		// DCBAMySqlUtil.escapeMySqlSpecialCharacters(errorLog).substring(0,
		// 2999);
		parameterList1.add(errorLog);

		// insert the error details into error_log table
		((CustomJdbcCommand) getNewJdbcCommand(
				DataSourceService.ComponentDBType.stagingdb,
				customConstants.STG_ERROR_LOG_TABLE)).getJdbcTemplate().update(
				customConstants.ERROR_LOG_SQL, parameterList1.toArray());
     */



}
