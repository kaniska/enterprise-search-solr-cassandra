/**
 * 
 */
package com.enterprise.app.mediscene.util;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.enterprise.app.mediscene.context.BatchInitializationResponse;


/**
 * @author Kaniska_Mandal
 *
 */
public class BatchScheduler {
	
	private static final Logger log = Logger.getLogger("Batch Scheduler");
	
	/**
	 * @param appid
	 * @param appName
	 * @param tenantid
	 * @param appDateFormat
	 * @param appTimeZone
	 * @param tblName
	 * @param jdbcCommand
	 * @param resultMap
	 * @return
	 * @throws Exception
	 */
	public static BatchInitializationResponse calculateNextFetchDateForIncrementalLoadByLastSuccessfulRunDate(
			int appid, String appName, int tenantid, String appDateFormat,
			String appTimeZone, String tblName, CustomJdbcCommand jdbcCommand,
			Map<String, Object> resultMap) throws Exception {
		
		BatchInitializationResponse dcbaBatchInfoResponse;
		int batchid;
		Number num;
		String current_run_date_from = null;
		String current_run_date_to = null;
		Map<String, Object> parameters = new HashMap<String, Object>(8);

		//long buffer = 1000; // 1 second
		// Expires on one minute from the date object date

		// the last successful run date now becomes new current run date
		// from
		current_run_date_from = resultMap.get(
				Constants.BATCH_LOG_RUN_DATE_TO).toString();

			current_run_date_to = DateUtil.convertDateToStr(new Date(),
					appDateFormat, appTimeZone);


		// POPULATE PARAMS
		parameters.put(Constants.BATCH_LOG_RUN_DATE_FROM,
				current_run_date_from);
		parameters.put(Constants.BATCH_LOG_RUN_DATE_TO,
				current_run_date_to);		
		parameters.put(Constants.BATCH_LOG_APP_ID, appid);
		parameters.put(Constants.BATCH_LOG_TENANT_ID, tenantid);
		parameters.put(Constants.BATCH_LOG_STATUS, "R");
		parameters.put(Constants.BATCH_LOG_GENERIC_APP_NAME, appName);
		num = jdbcCommand.executeAndReturnKey(parameters);

		// Verify if New Data was created fine
		if (num == null) {
			throw new Exception();
		}

		// prepare the response object from Query Result
		dcbaBatchInfoResponse = new BatchInitializationResponse(
				num.toString(), current_run_date_from, current_run_date_to,
				"N", "");

		batchid = num.intValue();
		
		/*log.debug(
				"||| Batch Initialization Info :: App Id= %s, App Name= %s, Tenant Id= %s, App Sys Date Format=%s, "
						+ "App Timezone=%s, Table Name=%s, Batch Id=%s, Current Run Date From=%s, Current Run Date To=%s",
				String.valueOf(appid), appName, String.valueOf(tenantid),
				appDateFormat, appTimeZone, tblName, String.valueOf(batchid),
				current_run_date_from, current_run_date_to);*/
		return dcbaBatchInfoResponse;
	}

	
	/**
	 * @param appid
	 * @param appName
	 * @param tenantid
	 * @param appDateFormat
	 * @param appTimeZone
	 * @param initialLoadStartDate
	 * @param jdbcCommand
	 * @return
	 * @throws Exception
	 */
	public static  BatchInitializationResponse calculateNextFetchTimeForInitialLoadByLastSuccessfulRunDate(
			int appid, String appName, int tenantid, String appDateFormat,
			String appTimeZone, String initialLoadStartDate,
			CustomJdbcCommand jdbcCommand) throws Exception {
		
		BatchInitializationResponse dcbaBatchInfoResponse;
		int batchid;
		Number num;
		String current_run_date_from = null;
		String current_run_date_to = null;

		// INITIAL LOAD // The Query did not return any ROW
		// Create the first batch Time Slot
		Map<String, Object> parameters = new HashMap<String, Object>(3);

		// EPOCH => "1970-01-01T05:05:05.000Z"
			current_run_date_from = DateUtil
					.convertDefaultDateToSpecificFormat(initialLoadStartDate, appDateFormat,
							appTimeZone);
			parameters.put(Constants.BATCH_LOG_RUN_DATE_FROM,
					current_run_date_from);

			Date currentTime = new Date();
			current_run_date_to = DateUtil.convertDateToStr(
					currentTime, appDateFormat, appTimeZone);
		
		parameters.put(Constants.BATCH_LOG_RUN_DATE_TO,
				current_run_date_to);
		parameters.put(Constants.BATCH_LOG_APP_ID, appid);
		parameters.put(Constants.BATCH_LOG_TENANT_ID, tenantid);
		parameters.put(Constants.BATCH_LOG_STATUS, "R");
		parameters.put(Constants.BATCH_LOG_GENERIC_APP_NAME, appName);
		num = jdbcCommand.executeAndReturnKey(parameters);

		// Verify if New Data was created fine
		if (num == null) {
			throw new Exception(
					"Batch Initialization Info not generated properly!");
		}

		// Else prepare the response object
		dcbaBatchInfoResponse = new BatchInitializationResponse(
				num.toString(), current_run_date_from, current_run_date_to,
				"N", "");

		batchid = num.intValue();

		/*log.debug("Batch Params >> Batch Id - " + batchid
				+ " || Run date From - " + current_run_date_from
				+ " || Run date to - " + current_run_date_to
				+ " || App Id - " + appid + " || Tenant Id - " + tenantid
				+ " || App Name - " + appName);*/
		
		System.out.println("Batch Params >> Batch Id - " + batchid
				+ " || Run date From - " + current_run_date_from
				+ " || Run date to - " + current_run_date_to
				+ " || App Id - " + appid + " || Tenant Id - " + tenantid
				+ " || App Name - " + appName);
		
		return dcbaBatchInfoResponse;
		
		
	}
	
	
	/**
	 * @param appid
	 * @param appName
	 * @param tenantid
	 * @param appDateFormat
	 * @param appTimeZone
	 * @param initialLoadStartDate
	 * @param jdbcCommand
	 * @return
	 * @throws Exception
	 */
	public static  BatchInitializationResponse calculateNextFetchTimeForInitialLoadBySpecifiedTimeRange(
			int appid, String appName, int tenantid, String appDateFormat,
			String appTimeZone, String initialLoadStartDate,
			CustomJdbcCommand jdbcCommand) throws Exception {
		
		BatchInitializationResponse dcbaBatchInfoResponse;
		int batchid;
		Number num;
		String current_run_date_from = null;
		String current_run_date_to = null;

		// INITIAL LOAD // The Query did not return any ROW
		// Create the first batch Time Slot
		Map<String, Object> parameters = new HashMap<String, Object>(3);

		// EPOCH => "1970-01-01T05:05:05.000Z"
		if (appName.equals("QB")) { // TODO - Later on turn it ON for QB
			current_run_date_from = DateUtil
					.convertDefaultDateToSpecificFormat(initialLoadStartDate, appDateFormat,
							appTimeZone);
			parameters.put(Constants.BATCH_LOG_RUN_DATE_FROM,
					current_run_date_from);

			Date currentTime = new Date();
			current_run_date_to = DateUtil.convertDateToStr(
					currentTime, appDateFormat, appTimeZone);
		} else { // Iterate over time periods Only for SFDC
			Calendar cal = Calendar.getInstance();
			Date initialLoad_fromDate = DateUtil.convertStrToDate(
					initialLoadStartDate, appDateFormat, appTimeZone);
			cal.setTime(initialLoad_fromDate);
			cal.add(Calendar.MONTH, 1);
			Date currentTime = new Date();
			Date initialLoad_toDate = (cal.getTime().compareTo(currentTime) < 0) ? cal
					.getTime() : currentTime;

			SimpleDateFormat sfdcFormatter = new SimpleDateFormat(
					appDateFormat, Locale.US);
			sfdcFormatter.setTimeZone(TimeZone.getTimeZone(appTimeZone));
			current_run_date_from = sfdcFormatter
					.format(initialLoad_fromDate);
			parameters.put(Constants.BATCH_LOG_RUN_DATE_FROM,
					current_run_date_from);

			current_run_date_to = DateUtil.convertDateToStr(
					initialLoad_toDate, appDateFormat, appTimeZone);
			
		}
		
		parameters.put(Constants.BATCH_LOG_RUN_DATE_TO,
				current_run_date_to);
		parameters.put(Constants.BATCH_LOG_APP_ID, appid);
		parameters.put(Constants.BATCH_LOG_TENANT_ID, tenantid);
		parameters.put(Constants.BATCH_LOG_STATUS, "R");
		parameters.put(Constants.BATCH_LOG_GENERIC_APP_NAME, appName);
		num = jdbcCommand.executeAndReturnKey(parameters);

		// Verify if New Data was created fine
		if (num == null) {
			throw new Exception(
					"Batch Initialization Info not generated properly!");
		}

		// Else prepare the response object
		dcbaBatchInfoResponse = new BatchInitializationResponse(
				num.toString(), current_run_date_from, current_run_date_to,
				"N", "");

		batchid = num.intValue();

		/*log.debug("Batch Params >> Batch Id - " + batchid
				+ " || Run date From - " + current_run_date_from
				+ " || Run date to - " + current_run_date_to
				+ " || App Id - " + appid + " || Tenant Id - " + tenantid
				+ " || App Name - " + appName);*/
		System.out.println("Batch Params >> Batch Id - " + batchid
				+ " || Run date From - " + current_run_date_from
				+ " || Run date to - " + current_run_date_to
				+ " || App Id - " + appid + " || Tenant Id - " + tenantid
				+ " || App Name - " + appName);
		return dcbaBatchInfoResponse;
	}

	/**
	 * @param appid
	 * @param appName
	 * @param tenantid
	 * @param appDateFormat
	 * @param appTimeZone
	 * @param tblName
	 * @param jdbcCommand
	 * @param resultMap
	 * @return
	 * @throws Exception
	 */
	public static BatchInitializationResponse calculateNextFetchDateForIncrementalLoadBySpecifiedTimeRange(
			int appid, String appName, int tenantid, String appDateFormat,
			String appTimeZone, String tblName, CustomJdbcCommand jdbcCommand,
			Map<String, String> resultMap) throws Exception {
		
		
		BatchInitializationResponse dcbaBatchInfoResponse;
		int batchid;
		Number num;
		String current_run_date_from = null;
		String current_run_date_to = null;
		Map<String, Object> parameters = new HashMap<String, Object>(8);

		//long buffer = 1000; // 1 second
		// Expires on one minute from the date object date

		// the last successful run date now becomes new current run date
		// from
		current_run_date_from = resultMap.get(
				Constants.BATCH_LOG_RUN_DATE_TO).toString();

		if (appName.equals("QB")) { // TODO - Later on turn it ON for QB
			current_run_date_to = DateUtil.convertDateToStr(new Date(),
					appDateFormat, appTimeZone);
		}else{ // SFDC
			Calendar cal = Calendar.getInstance();
			Date initialLoad_fromDate = DateUtil.convertStrToDate(
					current_run_date_from, appDateFormat, appTimeZone);
			cal.setTime(initialLoad_fromDate);
			cal.add(Calendar.DATE, 30);
			Date currentTime = new Date();
			Date initialLoad_toDate = (cal.getTime().compareTo(currentTime) < 0) ? cal
					.getTime() : currentTime;

			SimpleDateFormat sfdcFormatter = new SimpleDateFormat(
					appDateFormat, Locale.US);
			sfdcFormatter.setTimeZone(TimeZone.getTimeZone(appTimeZone));
			current_run_date_from = sfdcFormatter
					.format(initialLoad_fromDate);
			

			current_run_date_to = DateUtil.convertDateToStr(
					initialLoad_toDate, appDateFormat, appTimeZone);
		}

		// POPULATE PARAMS
		parameters.put(Constants.BATCH_LOG_RUN_DATE_FROM,
				current_run_date_from);
		parameters.put(Constants.BATCH_LOG_RUN_DATE_TO,
				current_run_date_to);		
		parameters.put(Constants.BATCH_LOG_APP_ID, appid);
		parameters.put(Constants.BATCH_LOG_TENANT_ID, tenantid);
		parameters.put(Constants.BATCH_LOG_STATUS, "R");
		parameters.put(Constants.BATCH_LOG_GENERIC_APP_NAME, appName);
		num = jdbcCommand.executeAndReturnKey(parameters);

		// Verify if New Data was created fine
		if (num == null) {
			throw new Exception();
		}

		// prepare the response object from Query Result
		dcbaBatchInfoResponse = new BatchInitializationResponse(
				num.toString(), current_run_date_from, current_run_date_to,
				"N", "");

		batchid = num.intValue();
		
	/*	log.debug(
				"||| Batch Initialization Info :: App Id= %s, App Name= %s, Tenant Id= %s, App Sys Date Format=%s, "
						+ "App Timezone=%s, Table Name=%s, Batch Id=%s, Current Run Date From=%s, Current Run Date To=%s",
				String.valueOf(appid), appName, String.valueOf(tenantid),
				appDateFormat, appTimeZone, tblName, String.valueOf(batchid),
				current_run_date_from, current_run_date_to);*/
		
		return dcbaBatchInfoResponse;
		
	}


	

	

}
