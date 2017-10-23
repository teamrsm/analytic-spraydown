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
                .append("UNIQUE (").append(USER_ID).append(",").append(ROUTE_ID)
                .append(",").append(TICK_DATE).append(")")
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
    static final String AVATAR_URL = "AVATAR_URL";

    public static String makeUsersTableCreate() {
        return new StringBuilder()
                .append("CREATE TABLE ")
                .append(USERS_TABLE_NAME).append(" (")
                .append(USER_ID).append(" INT ").append("NOT NULL ").append("PRIMARY KEY, ")
                .append(USER_NAME).append(" TEXT ").append("NOT NULL, ")
                .append(EMAIL_ADDR).append(" TEXT ").append("NOT NULL, ")
                .append(API_KEY).append(" TEXT ").append("NOT NULL, ")
                .append(AVATAR_URL).append(" TEXT, ")
                .append(LAST_ACCESS).append(" DATE")
                .append(")").toString();
    }

    public static String makeUsersTableDrop() {
        return new StringBuilder()
                .append("DROP TABLE ").append(USERS_TABLE_NAME).toString();
    }
    /*-********************************************************************/

    /*-********************* GRADEMAP TABLE SCHEMA ***************************/
    static final String GRADEMAP_TABLE_NAME = "GRADEMAP";
    static final String GRADE_ID = "GRADE_ID";
    static final String YOSEMITE_GRADE = "YOSEMITE";
    static final String EURO_GRADE = "EURO";
    static final String HUECO_GRADE = "HUECO";
    static final String FONT_GRADE = "FONT";
    static final String WATERICE_GRADE = "WATERICE";

    public static String makeGradeMapTableCreate() {
        return new StringBuilder()
                .append("CREATE TABLE ")
                .append(GRADEMAP_TABLE_NAME).append(" (")
                .append(GRADE_ID).append(" INTEGER, ")
                .append(YOSEMITE_GRADE).append(" TEXT, ")
                .append(EURO_GRADE).append(" TEXT, ")
                .append(HUECO_GRADE).append(" TEXT, ")
                .append(FONT_GRADE).append(" TEXT, ")
                .append(WATERICE_GRADE).append(" TEXT ")
                .append(")").toString();
    }

    public static String makeGradeMapTableDrop() {
        return new StringBuilder()
                .append("DROP TABLE ").append(GRADEMAP_TABLE_NAME).toString();
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
                .append(" ").append(USER_ID)
                .append("=").append(userId).toString();
    }

    public static String makeGetRoutes(Long[] routeIds) {
        StringBuilder builder =  new StringBuilder()
                .append("SELECT *")
                .append(" FROM ").append(ROUTES_TABLE_NAME);
        if (routeIds != null) {
                builder.append(" WHERE ")
                    .append(ROUTE_ID).append(" IN (")
                    .append(buildInList(routeIds))
                    .append(")");
        }
        return builder.toString();
    }

    private static String buildInList(Long[] inLongs) {
        StringBuilder inList = new StringBuilder();

        for (Long i : inLongs) {
            inList.append(i).append(",");
        }

        int lastIndex = inList.lastIndexOf(",");
        return inList.length() > 0 ? inList.substring(0, lastIndex - 1).toString() : null;
    }

    public static String makeInsertGradeMapStatement() {
        return new StringBuilder()
                .append("INSERT INTO ").append(GRADEMAP_TABLE_NAME)
                .append(" (")
                .append(YOSEMITE_GRADE).append(",")
                .append(EURO_GRADE).append(",")
                .append(HUECO_GRADE).append(",")
                .append(FONT_GRADE).append(",")
                .append(WATERICE_GRADE).append(",")
                .append(GRADE_ID).append(")")
                .append(" VALUES")
                .append("(\"5.1\", NULL, \"VB\", \"3\", \"W10\", 1),")
                .append("(\"5.2\", NULL, \"V0-\", NULL, \"W11\", 2),")
                .append("(\"5.3\", NULL, \"V0\", \"4-\", \"W12\", 3),")
                .append("(\"5.4\", \"4a\", \"V0+\", \"4\", \"W13\", 4),")
                .append("(\"5.5\", \"4b\", \"V1-\", \"5-\", \"W14\", 5),")
                .append("(\"5.6\", \"4c\", \"V1\", \"5\", \"W15\", 6),")
                .append("(\"5.7\", \"5a\", \"V1+\", NULL, \"W16\", 7),")
                .append("(\"5.8\", \"5b\", \"V2-\", NULL, \"W17\", 8),")
                .append("(\"5.9-\", NULL, \"V2\", \"5+\", \"W18\", 9),")
                .append("(\"5.9\", \"5c\", \"V2+\", NULL, NULL, 10),")
                .append("(\"5.9+\", NULL, \"V3-\", NULL, NULL, 11),")
                .append("(\"5.10a\", \"6a\", \"V3\", \"6A\", NULL, 12),")
                .append("(\"5.10b\", \"6a+\", \"V3+\", \"6A+\", NULL, 13),")
                .append("(\"5.10c\", \"6b\", \"V4-\", \"6B-\", NULL, 14),")
                .append("(\"5.10d\", \"6b+\", \"V4\", \"6B\", NULL, 15),")
                .append("(\"5.11a\", \"6c\", \"V4+\", \"6B+\", NULL, 16),")
                .append("(\"5.11b\", NULL, \"V5-\", NULL, NULL, 17),")
                .append("(\"5.11c\", \"6c+\", \"V5\", \"6C\", NULL, 18),")
                .append("(\"5.11d\", \"7a\", \"V5+\", \"6C+\", NULL, 19),")
                .append("(\"5.12a\", \"7a+\", \"V6-\", NULL, NULL, 20),")
                .append("(\"5.12b\", \"7b\", \"V6\", \"7A\", NULL, 21),")
                .append("(\"5.12c\", \"7b+\", \"V6+\", NULL, NULL, 22),")
                .append("(\"5.12d\", \"7c\", \"V7-\", NULL, NULL, 23),")
                .append("(\"5.13a\", \"7c+\", \"V7\", \"7A+\", NULL, 24),")
                .append("(\"5.13b\", \"8a\", \"V7+\", NULL, NULL, 25),")
                .append("(\"5.13c\", \"8a+\", \"V8-\", NULL, NULL, 26),")
                .append("(\"5.13d\", \"8b\", \"V8\", \"7B\", NULL, 27),")
                .append("(\"5.14a\", \"8b+\", \"V8+\", \"7B+\", NULL, 28),")
                .append("(\"5.14b\", \"8c\", \"V9-\", NULL, NULL, 29),")
                .append("(\"5.14c\", \"8c+\", \"V9\", \"7C\", NULL, 30),")
                .append("(\"5.14d\", \"9a\", \"V9+\", NULL, NULL, 31),")
                .append("(\"5.15a\", \"9a+\", \"V10-\", NULL, NULL, 32),")
                .append("(\"5.15b\", \"9b\", \"V10\", \"7C+\", NULL, 33),")
                .append("(\"5.15c\", \"9b+\", \"V10+\", NULL, NULL, 34),")
                .append("(\"5.15d\", \"9c\", \"V11-\", NULL, NULL, 35),")
                .append("(NULL, NULL, \"V11\", \"8A\", NULL, 36),")
                .append("(NULL, NULL, \"V11+\", NULL, NULL, 37),")
                .append("(NULL, NULL, \"V12-\", NULL, NULL, 38),")
                .append("(NULL, NULL, \"V12\", \"8A+\", NULL, 39),")
                .append("(NULL, NULL, \"V12+\", NULL, NULL, 40),")
                .append("(NULL, NULL, \"V13-\", NULL, NULL, 41),")
                .append("(NULL, NULL, \"V13\", \"8B\", NULL, 42),")
                .append("(NULL, NULL, \"V13+\", NULL, NULL, 43),")
                .append("(NULL, NULL, \"V14-\", NULL, NULL, 44),")
                .append("(NULL, NULL, \"V14\", \"8B+\", NULL, 45),")
                .append("(NULL, NULL, \"V14+\", NULL, NULL, 46),")
                .append("(NULL, NULL, \"V15-\", NULL, NULL, 47),")
                .append("(NULL, NULL, \"V15\", \"8C\", NULL, 48),")
                .append("(NULL, NULL, \"V15+\", NULL, NULL, 49),")
                .append("(NULL, NULL, \"V16-\", NULL, NULL, 50),")
                .append("(NULL, NULL, \"V16\", \"8C+\", NULL, 51),")
                .append("(NULL, NULL, \"V16+\", NULL, NULL, 52),")
                .append("(NULL, NULL, \"V17-\", NULL, NULL, 53),")
                .append("(NULL, NULL, \"V17\", \"9A\", NULL, 54),")
                .append("(NULL, NULL, \"V17+\", NULL, NULL, 55),")
                .append("(\"5.10-\", NULL, NULL, NULL, NULL, 12),")
                .append("(\"5.10\", NULL, NULL, NULL, NULL, 14),")
                .append("(\"5.10+\", NULL, NULL, NULL, NULL, 15),")
                .append("(\"5.11-\", NULL, NULL, NULL, NULL, 16),")
                .append("(\"5.11\", NULL, NULL, NULL, NULL, 18),")
                .append("(\"5.11+\", NULL, NULL, NULL, NULL, 19),")
                .append("(\"5.12-\", NULL, NULL, NULL, NULL, 20),")
                .append("(\"5.12\", NULL, NULL, NULL, NULL, 22),")
                .append("(\"5.12+\", NULL, NULL, NULL, NULL, 23),")
                .append("(\"5.13-\", NULL, NULL, NULL, NULL, 24),")
                .append("(\"5.13\", NULL, NULL, NULL, NULL, 26),")
                .append("(\"5.13+\", NULL, NULL, NULL, NULL, 27),")
                .append("(\"5.14-\", NULL, NULL, NULL, NULL, 28),")
                .append("(\"5.14\", NULL, NULL, NULL, NULL, 30),")
                .append("(\"5.14+\", NULL, NULL, NULL, NULL, 31),")
                .append("(\"5.15-\", NULL, NULL, NULL, NULL, 32),")
                .append("(\"5.15\", NULL, NULL, NULL, NULL, 34),")
                .append("(\"5.15+\", NULL, NULL, NULL, NULL, 35)").toString();
    }

}
