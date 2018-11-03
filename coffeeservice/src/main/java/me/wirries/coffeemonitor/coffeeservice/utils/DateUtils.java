package me.wirries.coffeemonitor.coffeeservice.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * This util provides functions for {@link Date} operations.
 *
 * @author denisw
 * @version 1.0
 * @since 03.11.2018
 */
public class DateUtils {

    /**
     * Is the same hour?
     */
    public static boolean isSameHour(final Date date1, final Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        final Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameHour(cal1, cal2);
    }

    /**
     * Is the same hour?
     */
    public static boolean isSameHour(final Calendar cal1, final Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.HOUR) == cal2.get(Calendar.HOUR);
    }

    /**
     * Is the same minute?
     */
    public static boolean isSameMinute(final Date date1, final Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        final Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameMinute(cal1, cal2);
    }

    /**
     * Is the same minute?
     */
    public static boolean isSameMinute(final Calendar cal1, final Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.HOUR) == cal2.get(Calendar.HOUR) &&
                cal1.get(Calendar.MINUTE) == cal2.get(Calendar.MINUTE);
    }

}
