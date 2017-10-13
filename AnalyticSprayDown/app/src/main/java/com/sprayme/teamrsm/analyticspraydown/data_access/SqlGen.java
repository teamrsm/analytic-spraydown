package com.sprayme.teamrsm.analyticspraydown.data_access;

import java.util.List;

/**
 * Created by Said on 10/9/2017.
 */

class SqlGen {

    /*-********************* TICKS TABLE SCHEMA ***************************/
    static final String TICKS_TABLE_NAME =  "TICKS";
    static final String USER_ID = "USER_ID";
    static final String ROUTE_ID = "ROUTE_ID";
    static final String TICK_DATE = "TICK_DATE";
    static final String NOTES = "NOTES";
    static final String TICK_TYPE = "TICK_TYPE";
    static final String LAST_ACCESS = "LAST_ACCESS";

    public static String makeTicksTableCreate() {
        return new StringBuilder()
                .append("CREATE TABLE ")
                .append(TICKS_TABLE_NAME).append(" (")
                .append(USER_ID).append(" INT ").append("NOT NULL, ")
                .append(ROUTE_ID).append(" INT ").append("NOT NULL, ")
                .append(TICK_DATE).append(" DATE ").append("NOT NULL, ")
                .append(NOTES).append(" TEXT, ")
                .append(TICK_TYPE).append(" TEXT, ")
                .append("UNIQUE (").append(USER_ID).append(",").append(ROUTE_ID).append(")")
                .append(" ON CONFLICT REPLACE")
                .append(")").toString();
    }

    public static String makeTicksTableDrop() {
        return new StringBuilder()
                .append("DROP TABLE ").append(TICKS_TABLE_NAME).toString();
    }
    /*-*********************************************************************/

    /*-********************* ROUTES TABLE SCHEMA ***************************/
    static final String ROUTES_TABLE_NAME = "ROUTES";
    static final String ROUTE_NAME = "ROUTE_NAME";
    static final String ROUTE_TYPE = "ROUTE_TYPE";
    static final String RATING = "RATING";
    static final String STARS = "STARS";
    static final String PITCHES = "PITCHES";
    static final String ROUTE_URL = "ROUTE_URL";

    public static String makeRoutesTableCreate() {
        return new StringBuilder()
                .append("CREATE TABLE ")
                .append(ROUTES_TABLE_NAME).append(" (")
                .append(ROUTE_ID).append(" INT ").append("NOT NULL, ")
                .append(ROUTE_NAME).append(" TEXT, ")
                .append(ROUTE_TYPE).append(" TEXT, ")
                .append(RATING).append(" TEXT, ")
                .append(STARS).append(" REAL, ")
                .append(PITCHES).append(" INT, ")
                .append(ROUTE_URL).append(" TEXT, ")
                .append("UNIQUE (").append(ROUTE_ID).append(")")
                .append(" ON CONFLICT REPLACE")
                .append(")").toString();
    }

    public static String makeRoutesTableDrop() {
        return new StringBuilder()
                .append("DROP TABLE ").append(ROUTES_TABLE_NAME).toString();
    }
    /*-********************************************************************/

    /*-********************* USERS TABLE SCHEMA ***************************/
    static final String USERS_TABLE_NAME = "USERS";
    static final String USER_NAME = "USER_NAME";
    static final String EMAIL_ADDR = "EMAIL_ADDRESS";
    static final String API_KEY = "DATA_KEY";

    public static String makeUsersTableCreate() {
        return new StringBuilder()
                .append("CREATE TABLE ")
                .append(USERS_TABLE_NAME).append(" (")
                .append(USER_ID).append(" INT ").append("NOT NULL ").append("PRIMARY KEY, ")
                .append(USER_NAME).append(" TEXT ").append("NOT NULL, ")
                .append(EMAIL_ADDR).append(" TEXT ").append("NOT NULL, ")
                .append(API_KEY).append(" TEXT ").append("NOT NULL, ")
                .append(LAST_ACCESS).append(" DATE")
                .append(")").toString();
    }

    public static String makeUsersTableDrop() {
        return new StringBuilder()
                .append("DROP TABLE ").append(USERS_TABLE_NAME).toString();
    }
    /*-********************************************************************/

    public static String makeGetLastUser() {
        return new StringBuilder()
                .append("SELECT *")
                .append(" FROM ").append(USERS_TABLE_NAME)
                .append(" ORDER BY ")
                .append(LAST_ACCESS).append(" DESC ")
                .append("LIMIT 1").toString();
    }

    public static String makeLastUserAccess(long userId) {
        return new StringBuilder()
                .append("SELECT ").append(LAST_ACCESS)
                .append(" FROM ").append(USERS_TABLE_NAME)
                .append(" WHERE ")
                .append(USER_ID).append(" = ").append(userId).toString();
    }

    public static String makeGetUserTicks(long userId) {
        return new StringBuilder()
                .append("SELECT * ")
                .append(" FROM ").append(TICKS_TABLE_NAME)
                .append(" WHERE ")
                .append(USER_ID).append(" = ").append(userId).toString();
    }

    public static String makeUserIdWhereClause(long userId) {
        return new StringBuilder()
                .append("WHERE ").append(USER_ID)
                .append(" = ").append(userId).toString();
    }

    public static String makeGetRoutes(Long[] routeIds) {
        return new StringBuilder()
                .append("SELECT *")
                .append(" FROM ").append(ROUTES_TABLE_NAME)
                .append(" WHERE ")
                .append(ROUTE_ID).append(" IN (")
                .append(buildInList(routeIds))
                .append(")").toString();
    }

    private static String buildInList(Long[] inLongs) {
        StringBuilder inList = new StringBuilder();

        for (Long i : inLongs) {
            inList.append(i).append(",");
        }

        int lastIndex = inList.lastIndexOf(",");
        return inList.substring(0, lastIndex - 1).toString();
    }

}
