/**
 * 
 */
package com.enterprise.app.mediscene.util;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

/**
 * @author kaniska_mac
 *
 */
public class CustomJdbcCommand extends SimpleJdbcInsert {

	public CustomJdbcCommand(DataSource dataSource) {
		super(dataSource);
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return super.getJdbcTemplate();
	}


}