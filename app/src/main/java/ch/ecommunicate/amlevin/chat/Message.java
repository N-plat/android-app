package ch.ecommunicate.amlevin.chat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    private String username;
    private String name;
    private String message;
    private boolean forward;
    private Date date;

    public Message() {
    }

    public Message(String username, String name, String message, boolean forward, Date date) {
        this.username = username;
        this.name = name;
        this.message = message;
        this.forward = forward;
        this.date = date;
    }

    public String name() {
        return name;
    }

    public String username() {
        return username;
    }

    public void set_username(String username) {
        this.username = username;
    }

    public String message() {
        return message;
    }

    public void set_message(String message) {
        this.message = message;
    }

    public boolean forward() {
        return forward;
    }

    public void set_forward(boolean forward) {
        this.forward = forward;
    }

    public void set_date(Date date) { this.date = date; }

    public String date() {

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

        return sdf.format(date);

    }

    public String time() {

        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");

        return sdf.format(date);

    }

}