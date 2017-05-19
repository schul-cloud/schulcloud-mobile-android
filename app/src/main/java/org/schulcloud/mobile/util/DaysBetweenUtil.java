package org.schulcloud.mobile.util;


import java.util.Date;

public class DaysBetweenUtil {
    public static int daysBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    public static int weeksBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24 * 7));
    }
}
