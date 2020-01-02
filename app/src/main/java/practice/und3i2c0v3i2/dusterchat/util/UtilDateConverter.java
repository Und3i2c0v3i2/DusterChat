package practice.und3i2c0v3i2.dusterchat.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UtilDateConverter {



    public static String convertDateToString(Date date) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        return sdf.format(date);

//        return DateFormat.getDateInstance().format(date.getTime());
//
//        return DateFormat.getDateInstance(DateFormat.FULL).format(date.getTime());
    }

    public static String convertDateTimeToString(Date date) {

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm MMM dd, yyyy");

        return sdf.format(date);

//        return DateFormat.getDateInstance().format(date.getTime());
//
//        return DateFormat.getDateInstance(DateFormat.FULL).format(date.getTime());
    }


    public static Date convertToDate(String strDate) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Calendar calendar = Calendar.getInstance();
        if (calendar != null) {
            try {
                calendar.setTime(sdf.parse(strDate));
                date = new Date(calendar.getTimeInMillis());
            } catch (ParseException e) {
                e.printStackTrace();
            }        }

        return date;

    }

    public static Date convertToDateTime(String strDate) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Calendar calendar = Calendar.getInstance();
        if (calendar != null) {
            try {
                calendar.setTime(sdf.parse(strDate));
                date = new Date(calendar.getTimeInMillis());
            } catch (ParseException e) {
                e.printStackTrace();
            }        }

        return date;

    }
}
