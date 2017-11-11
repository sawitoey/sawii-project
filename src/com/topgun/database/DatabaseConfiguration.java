package com.topgun.database;

public class DatabaseConfiguration {
	public static final String POOL_155_URL="jdbc:oracle:thin:@203.146.208.155:1522:interdb";
	public static final String POOL_155_USERNAME="informix";
	public static final String POOL_155_PASSWORD="skpkd252";
	public static final String POOL_155_DRIVER="oracle.jdbc.driver.OracleDriver";
	public static final int POOL_155_INITIAL_SIZE=5;
	public static final int POOL_155_MAX_TOTAL=20;
	public static final boolean POOL_155_PREPARED_STATEMENTS=true;
	
	public static final String POOL_66_URL="jdbc:oracle:thin:@203.146.250.66:1522:interdb";
	public static final String POOL_66_USERNAME="informix";
	public static final String POOL_66_PASSWORD="informix";
	public static final String POOL_66_DRIVER="oracle.jdbc.driver.OracleDriver";
	public static final int POOL_66_INITIAL_SIZE=5;
	public static final int POOL_66_MAX_TOTAL=20;
	public static final boolean POOL_66_PREPARED_STATEMENTS=true;
}
