package practice.und3i2c0v3i2.dusterchat.util;


import androidx.databinding.BindingAdapter;

import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;

public class ImageBindingAdapter {


    @BindingAdapter({"load_image"})
    public static void bindImg(CircleImageView view, String imgUrl) {

        if(imgUrl != null && !imgUrl.isEmpty()) {

            Picasso.get()
                    .load(imgUrl)
                    .fit()
                    .into(view);
        }
    }

}
