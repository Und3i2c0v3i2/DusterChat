package practice.und3i2c0v3i2.dusterchat.util;

import android.view.View;
import android.widget.TextView;

import androidx.databinding.InverseBindingAdapter;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;

public class DateBindingAdapter {

    // use android:date tag in xml
    @androidx.databinding.BindingAdapter("android:date")
    public static void setDate(View view, Date date) {
        if (date != null) {
            String strDate = UtilDateConverter.convertDateToString(date);
            ((TextInputEditText) view).setText(strDate);
        }
    }

    @InverseBindingAdapter(attribute = "android:date", event = "android:textAttrChanged")
    public static Date DateValue(View view) {
        CharSequence date = ((TextInputEditText) view).getText();

        if (date != null) {
            return UtilDateConverter.convertToDate(date.toString());

        }


        return new Date();
    }

    // use displayDate tag for date and displayText tag for some title
    @androidx.databinding.BindingAdapter({"app:displayDate", "app:displayText"})
    public static void displayDate(View view, Date date, String text) {
        if (date != null) {
            String strDate = text + UtilDateConverter.convertDateToString(date);
            ((TextView) view).setText(strDate);
        }
    }
}
