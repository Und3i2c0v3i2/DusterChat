package practice.und3i2c0v3i2.dusterchat;

import android.os.Bundle;

public interface OnItemClickListener {

    String CLICK_ACTION = "click action";

    String BUNDLE_GROUP_NAME = "group name";
    int ACTION_GROUP_CHAT = 1001;
    int ACTION_OPEN_PROFILE = 1002;

    void onItemClick(Bundle bundle);
}
