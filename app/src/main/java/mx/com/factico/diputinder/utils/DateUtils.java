package mx.com.factico.diputinder.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by zace3d on 15/10/15.
 */
public class DateUtils {
    public static String TEMPLATE_DATE_TIME = "yyyyMMdd_HHmmss";

    public static String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(TEMPLATE_DATE_TIME, Locale.getDefault());

        return sdf.format(new Date());
    }

    //1 minute = 60 seconds
    //1 hour = 60 x 60 = 3600
    //2 hour = 60 x 60 x 2 = 7200
    //3 hour = 60 x 60 x 3 = 10800
    //1 day = 3600 x 24 = 86400
    public static long getDifferencesBetweenDates(String dateOld, String dateCurrent) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TEMPLATE_DATE_TIME, Locale.getDefault());
        Date startDate = null;
        Date endDate = null;
        long difference = 0;
        try {
            startDate = simpleDateFormat.parse(dateOld);
            endDate = simpleDateFormat.parse(dateCurrent);

            //milliseconds
            difference = endDate.getTime() - startDate.getTime();

            System.out.println("startDate : " + startDate);
            System.out.println("endDate : " + endDate);
            System.out.println("different : " + difference);

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = difference / daysInMilli;
            difference = difference % daysInMilli;

            long elapsedHours = difference / hoursInMilli;
            difference = difference % hoursInMilli;

            long elapsedMinutes = difference / minutesInMilli;
            difference = difference % minutesInMilli;

            long elapsedSeconds = difference / secondsInMilli;

            System.out.printf(
                    "%d days, %d hours, %d minutes, %d seconds%n",
                    elapsedDays,
                    elapsedHours, elapsedMinutes, elapsedSeconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return difference;
    }
}
