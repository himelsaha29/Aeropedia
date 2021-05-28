package com.willblaschko.android.alexa.interfaces.alerts;

import com.willblaschko.android.alexa.interfaces.AvsItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * An AVS Item to handle setting alerts on the device
 *
 * {@link com.willblaschko.android.alexa.data.Directive} response item type parsed so we can properly
 * deal with the incoming commands from the Alexa server.
 */
public class AvsSetAlertItem extends AvsItem {
    private String type;
    private String scheduledTime;

    public static final String TIMER = "TIMER";
    public static final String ALARM = "ALARM";

    /**
     * Create a new AVSItem directive for an alert
     *
     * @param token the alert identifier
     * @param type the alert type
     * @param scheduledTime the alert time
     */
    public AvsSetAlertItem(String token, String type, String scheduledTime){
        super(token);
        this.type = type;
        this.scheduledTime = scheduledTime;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public long getScheduledTimeMillis() throws ParseException {
        return getDate().getTime();
    }

    public int getHour() throws ParseException {
        return getDate().getHours();
    }
    public int getMinutes() throws ParseException {
        return getDate().getMinutes();
    }

    private Date getDate() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US).parse(scheduledTime);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isTimer() {
        return type.equals(TIMER);
    }

    public boolean isAlarm() {
        return type.equals(ALARM);
    }
}
