package com.slice.wallpapers.DataAccessLayer;

public class SqliteCheckErrors {
    public static final int ALREADY_ADDED = -1;
    public static final int INSERT_FAULT = -2;
    public static final int NO_READABLE_VAUE = -3;

    public static boolean checkErrors(long id)
    {
        switch (Long.signum(id))
        {
            case ALREADY_ADDED :
                return true;
            case INSERT_FAULT :
                return true;
            case NO_READABLE_VAUE :
                return true;
        }
        return false;
    }
    public static boolean checkErrors(int id)
    {
        switch (id)
        {
            case ALREADY_ADDED :
                return true;
            case INSERT_FAULT :
                return true;
            case NO_READABLE_VAUE :
                return true;
        }
        return false;
    }
}
