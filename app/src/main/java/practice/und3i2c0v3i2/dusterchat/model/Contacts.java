package practice.und3i2c0v3i2.dusterchat.model;


public class Contacts {

    private String username;
    private String status;
    private String profile_img;

    public Contacts() {}

    public Contacts(String username, String status, String profile_img) {
        this.username = username;
        this.status = status;
        this.profile_img = profile_img;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }
}
