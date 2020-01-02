package practice.und3i2c0v3i2.dusterchat.util;

import android.view.View;
import android.widget.TextView;

import androidx.databinding.InverseBindingAdapter;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;

public class DateTimeBindingAdapter {

    @androidx.databinding.BindingAdapter("android:dateTime")
    public static void setDateTime(View view, Date date) {
        if (date != null) {
            String strDate = UtilDateConverter.convertDateTimeToString(date);
            ((TextInputEditText) view).setText(strDate);
        }
    }

    @InverseBindingAdapter(attribute = "android:dateTime", event = "android:textAttrChanged")
    public static Date DateTimeValue(View view) {
        CharSequence date = ((TextInputEditText) view).getText();

        if (date != null) {
            return UtilDateConverter.convertToDateTime(date.toString());

        }


        return new Date();
    }

    @androidx.databinding.BindingAdapter({"app:displayDateTime", "app:displayText"})
    public static void displayDateTime(View view, Date date, String text) {
        if (date != null) {
            String strDate = text + UtilDateConverter.convertDateTimeToString(date);
            ((TextView) view).setText(strDate);
        }
    }
}
