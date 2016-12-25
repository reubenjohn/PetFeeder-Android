package com.aspirephile.petfeeder.android.db;

import android.provider.BaseColumns;
import android.support.annotation.NonNull;

/**
 * Created by Reuben John on 9/9/2016.
 */
public final class Contract {
    private static final String INTEGER_TYPE = "INTEGER";
    private static final String DATE_TYPE = "DATE";
    private static final String TEXT_TYPE = "TEXT";
    private static final String NUMBER_TYPE = "NUMBER";
    private static final String COMMA_SEP = ", ";
    private static final String NOT_NULL = "NOT NULL";
    private static final String UNIQUE = "UNIQUE";
    private static final String DEFAULT = "DEFAULT";

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private Contract() {
    }

    @NonNull
    private static String separated(String separator, String... elements) {
        String res = "";
        if (elements.length > 0) {
            for (int i = 0; i < elements.length - 1; i++) {
                res += (elements[i] + separator);
            }
            res += elements[elements.length - 1];
        }
        return res;
    }

    private static String COLUMN(String name, String type, String... constraints) {
        return name + " " + type + " " + separated(" ", constraints);
    }

    @NonNull
    private static String COLUMNS(String... columns) {
        return separated(COMMA_SEP, columns);
    }

    private static String PRIMARY_KEY(String... columns) {
        String res = "PRIMARY KEY(";
        res += separated(COMMA_SEP, columns);
        res += ")";
        return res;
    }

    private static String FOREIGN_KEY(String key, String referenceTable, String referenceColumn) {
        return "FOREIGN KEY(" + key + ") REFERENCES " + referenceTable + "(" + referenceColumn + ")";
    }

    private static String FOREIGN_KEYS(String... foreignKeys) {
        return separated(COMMA_SEP, foreignKeys);
    }

    private static String CREATE_TABLE(String name, String columns, String primaryKeys, String foreignKeys) {
        String res = "CREATE TABLE " + name + " ( " + columns;
        if (primaryKeys != null && primaryKeys.length() > 0) {
            res += COMMA_SEP + primaryKeys;
        }
        if (foreignKeys != null && foreignKeys.length() > 0) {
            res += COMMA_SEP + foreignKeys;
        }
        res += " )";
        return res;
    }

    private static String DROP_TABLE(String name) {
        return "DROP TABLE " + name;
    }

    public static final class Location implements BaseColumns {
        public static final String TABLE_NAME = "`" + Location.class.getSimpleName() + "`";
        public static final String NAME = "`name`";
        public static final String LAT = "`lat`";
        public static final String LON = "`lon`";
        //TODO Ensure distinct emails
        public static final String SQL_CREATE_ENTRIES = CREATE_TABLE(
                TABLE_NAME,
                COLUMNS(
                        COLUMN(_ID, INTEGER_TYPE, NOT_NULL, UNIQUE),
                        COLUMN(NAME, TEXT_TYPE, NOT_NULL, UNIQUE),
                        COLUMN(LAT, NUMBER_TYPE, NOT_NULL),
                        COLUMN(LON, NUMBER_TYPE, NOT_NULL)
                ),
                PRIMARY_KEY(_ID), null);
        public static final String SQL_DELETE_ENTRIES = DROP_TABLE(TABLE_NAME);
    }

    public static final class User implements BaseColumns {
        public static final String TABLE_NAME = "`" + User.class.getSimpleName() + "`";
        public static final String EMAIL = "`email`";
        public static final String NAME = "`name`";
        //TODO Ensure distinct emails
        public static final String SQL_CREATE_ENTRIES = CREATE_TABLE(
                TABLE_NAME,
                COLUMNS(
                        COLUMN(_ID, INTEGER_TYPE, NOT_NULL, UNIQUE),
                        COLUMN(EMAIL, TEXT_TYPE, NOT_NULL, UNIQUE),
                        COLUMN(NAME, TEXT_TYPE, NOT_NULL)
                ),
                PRIMARY_KEY(_ID), null);
        public static final String SQL_DELETE_ENTRIES = DROP_TABLE(TABLE_NAME);
    }

    public static class Schedule implements BaseColumns {
        public static final String TABLE_NAME = "`" + Schedule.class.getSimpleName() + "`";
        public static final String NAME = "`name`";
        public static final String QUANTITY = "`quantity`";
        public static final String DATETIME = "`datetime`";
        public static final String SQL_CREATE_ENTRIES = CREATE_TABLE(
                TABLE_NAME,
                COLUMNS(
                        COLUMN(_ID, INTEGER_TYPE, NOT_NULL, UNIQUE),
                        COLUMN(NAME, TEXT_TYPE, NOT_NULL, UNIQUE),
                        COLUMN(QUANTITY, INTEGER_TYPE, NOT_NULL),
                        COLUMN(DATETIME, DATE_TYPE, NOT_NULL)
                ),
                PRIMARY_KEY(_ID),
                null);
        public static final String SQL_DELETE_ENTRIES = DROP_TABLE(TABLE_NAME);
    }

    public static class Bill implements BaseColumns {
        public static final String TABLE_NAME = "`" + Bill.class.getSimpleName() + "`";
        public static final String USER = "`user`";
        public static final String SERVICE = "`service`";
        public static final String ISSUED_AT = "`issuedAt`";
        public static final String PAYED_AT = "`payedAt`";
        public static final String SQL_CREATE_ENTRIES = CREATE_TABLE(
                TABLE_NAME,
                COLUMNS(
                        COLUMN(_ID, INTEGER_TYPE, NOT_NULL, UNIQUE),
                        COLUMN(USER, TEXT_TYPE, NOT_NULL),
                        COLUMN(SERVICE, INTEGER_TYPE, NOT_NULL),
                        COLUMN(ISSUED_AT, INTEGER_TYPE, NOT_NULL),
                        COLUMN(PAYED_AT, INTEGER_TYPE, NOT_NULL, DEFAULT, "-2")
                ),
                PRIMARY_KEY(_ID),
                FOREIGN_KEYS(
                        FOREIGN_KEY(USER, User.TABLE_NAME, User._ID),
                        FOREIGN_KEY(SERVICE, Schedule.TABLE_NAME, Schedule._ID)
                )
        );
        public static final String SQL_DELETE_ENTRIES = DROP_TABLE(TABLE_NAME);
    }

    public static class ItemType implements BaseColumns {
        public static final String TABLE_NAME = "`" + ItemType.class.getSimpleName() + "`";
        public static final String NAME = "`name`";
        public static final String SQL_CREATE_ENTRIES = CREATE_TABLE(
                TABLE_NAME,
                COLUMNS(
                        COLUMN(_ID, INTEGER_TYPE, NOT_NULL, UNIQUE),
                        COLUMN(NAME, TEXT_TYPE, NOT_NULL, UNIQUE)
                ),
                PRIMARY_KEY(_ID), FOREIGN_KEYS());
        public static final String SQL_DELETE_ENTRIES = DROP_TABLE(TABLE_NAME);
    }

    public static class OfferedItemType implements BaseColumns {
        public static final String TABLE_NAME = "`" + OfferedItemType.class.getSimpleName() + "`";
        public static final String ITEM_TYPE = "`itemType`";
        public static final String SERVICE = "`service`";
        public static final String COST = "`cost`";
        public static final String SQL_CREATE_ENTRIES = CREATE_TABLE(
                TABLE_NAME,
                COLUMNS(
                        COLUMN(_ID, INTEGER_TYPE, NOT_NULL, UNIQUE),
                        COLUMN(ITEM_TYPE, INTEGER_TYPE, NOT_NULL),
                        COLUMN(SERVICE, INTEGER_TYPE, NOT_NULL),
                        COLUMN(COST, INTEGER_TYPE, NOT_NULL)
                ),
                PRIMARY_KEY(_ID),
                FOREIGN_KEYS(
                        FOREIGN_KEY(ITEM_TYPE, ItemType.TABLE_NAME, _ID),
                        FOREIGN_KEY(SERVICE, Schedule.TABLE_NAME, _ID)
                ));
        public static final String SQL_DELETE_ENTRIES = DROP_TABLE(TABLE_NAME);
    }

    public static class Item implements BaseColumns {
        public static final String TABLE_NAME = "`" + Item.class.getSimpleName() + "`";
        public static final String OFFERED_ITEM_TYPE = "`offeredItemType`";
        public static final String BILL = "`bill`";
        public static final String SQL_CREATE_ENTRIES = CREATE_TABLE(
                TABLE_NAME,
                COLUMNS(
                        COLUMN(_ID, INTEGER_TYPE, NOT_NULL, UNIQUE),
                        COLUMN(OFFERED_ITEM_TYPE, INTEGER_TYPE, NOT_NULL),
                        COLUMN(BILL, INTEGER_TYPE, NOT_NULL)
                ),
                PRIMARY_KEY(_ID),
                FOREIGN_KEYS(
                        FOREIGN_KEY(OFFERED_ITEM_TYPE, OfferedItemType.TABLE_NAME, _ID),
                        FOREIGN_KEY(BILL, Bill.TABLE_NAME, _ID)
                ));
        public static final String SQL_DELETE_ENTRIES = DROP_TABLE(TABLE_NAME);
    }

    public static class Review implements BaseColumns {
        public static final String TABLE_NAME = "`" + Review.class.getSimpleName() + "`";
        public static final String SERVICE = "`service`";
        public static final String USER = "`user`";
        public static final String TIMESTAMP = "`timestamp`";
        public static final String RATING = "`rating`";
        public static final String DESCRIPTION = "`description`";
        public static final String SQL_CREATE_ENTRIES = CREATE_TABLE(
                TABLE_NAME,
                COLUMNS(
                        COLUMN(_ID, INTEGER_TYPE, NOT_NULL, UNIQUE),
                        COLUMN(SERVICE, INTEGER_TYPE, NOT_NULL),
                        COLUMN(USER, TEXT_TYPE, NOT_NULL),
                        COLUMN(TIMESTAMP, INTEGER_TYPE, NOT_NULL),
                        COLUMN(RATING, INTEGER_TYPE, NOT_NULL),
                        COLUMN(DESCRIPTION, TEXT_TYPE, NOT_NULL)
                ),
                PRIMARY_KEY(_ID),
                FOREIGN_KEYS(
                        FOREIGN_KEY(SERVICE, Schedule.TABLE_NAME, _ID),
                        FOREIGN_KEY(USER, User.TABLE_NAME, _ID)
                ));
        public static final String SQL_DELETE_ENTRIES = DROP_TABLE(TABLE_NAME);
    }
}