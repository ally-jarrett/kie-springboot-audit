package com.company.service.util;

public class SQLUtil {

    public static String PROCESS_CORE_VARIABLES = " (id INTEGER NOT NULL PRIMARY KEY, " +
            "PROCESS_INSTANCE_ID INTEGER NOT NULL, " +
            "PARENT_ID INTEGER NOT NULL, " +
            "CORRELATION_KEY VARCHAR(255) NOT NULL, " +
            "PROCESS_ID VARCHAR(255) NOT NULL, " +
            "PROCESS_NAME VARCHAR(255) NOT NULL, " +
            "PROCESS_VERSION VARCHAR(255), " +
            "STATE VARCHAR(255) NOT NULL, " +
            "CONTAINER_ID VARCHAR(255) NOT NULL, " +
            "INITIATOR VARCHAR(255) NOT NULL, " +
            "DATE TIMESTAMP NOT NULL, " +
            "PROCESS_INSTANCE_DESC VARCHAR(255) ";

    public static String PROCESS_CORE_END = "); ";

}
