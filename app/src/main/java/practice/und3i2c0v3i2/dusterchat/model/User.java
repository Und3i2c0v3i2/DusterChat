package practice.und3i2c0v3i2.dusterchat.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;


public class User extends BaseObservable {

    private String uid;
    private String username;
    private String status;
    private String imgUrl;
    private String phone;
    private String email;
    private String web;
    private String linkedIn;
    private String facebook;
    private String twitter;

    public User() {
    }

    @Bindable
    public String getUid() {
        return uid;
    }

    @Bindable
    public String getUsername() {
        return username;
    }

    @Bindable
    public String getStatus() {
        return status;
    }

    @Bindable
    public String getImgUrl() {
        return imgUrl;
    }

    @Bindable
    public String getPhone() {
        return phone;
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    @Bindable
    public String getWeb() {
        return web;
    }

    @Bindable
    public String getLinkedIn() {
        return linkedIn;
    }

    @Bindable
    public String getFacebook() {
        return facebook;
    }

    @Bindable
    public String getTwitter() {
        return twitter;
    }

    public void setUid(String uid) {
        this.uid = uid;
        notifyPropertyChanged(BR.uid);
    }

    public void setUsername(String username) {
        this.username = username;
        notifyPropertyChanged(BR.username);
    }

    public void setStatus(String status) {
        this.status = status;
        notifyPropertyChanged(BR.status);
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
        notifyPropertyChanged(BR.imgUrl);
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }

    public void setWeb(String web) {
        this.web = web;
        notifyPropertyChanged(BR.web);
    }

    public void setLinkedIn(String linkedIn) {
        this.linkedIn = linkedIn;
        notifyPropertyChanged(BR.linkedIn);
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
        notifyPropertyChanged(BR.facebook);
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
        notifyPropertyChanged(BR.twitter);
    }
}
