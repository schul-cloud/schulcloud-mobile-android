package org.schulcloud.mobile.util;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class DaysBetweenUtilTest {


    @Test
    public void testDaysBetween() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd");
        Date startDate = simpleDateFormat.parse("2017-06-01");
        Date endDate = simpleDateFormat.parse("2017-06-05");
        assertEquals(4, DaysBetweenUtil.daysBetween(startDate, endDate));
    }

    @Test
    public void testWeeksBetween() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd");
        Date startDate = simpleDateFormat.parse("2017-06-01");
        Date endDate = simpleDateFormat.parse("2017-06-18");
        assertEquals(2, DaysBetweenUtil.weeksBetween(startDate, endDate));
    }

}