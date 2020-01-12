package practice.und3i2c0v3i2.dusterchat.model;


public class Message {

    private String from;
    private String message;
    private String timestamp;
    private String type;

    public Message() {}

    public Message(String from, String message, String timestamp, String type) {
        this.from = from;
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
