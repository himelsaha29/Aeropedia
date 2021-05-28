package com.willblaschko.android.alexa.data;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.willblaschko.android.alexa.utility.Util.getUuid;

/**
 * A catch-all Event to classify return responses from the Amazon Alexa v20160207 API
 * Will handle calls to:
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/speechrecognizer">Speech Recognizer</a>
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/alerts">Alerts</a>
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/audioplayer">Audio Player</a>
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/playbackcontroller">Playback Controller</a>
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/speaker">Speaker</a>
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/speechsynthesizer">Speech Synthesizer</a>
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/system">System</a>
 *
 * @author wblaschko on 5/6/16.
 */
public class Event {

    Header header;
    Payload payload;
    List<Event> context;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }


    public static class Header{

        String namespace;
        String name;
        String messageId;
        String dialogRequestId;

        public String getNamespace() {
            return namespace;
        }


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public String getMessageId() {
            return messageId;
        }

        public String getDialogRequestId() {
            return dialogRequestId;
        }

        public void setDialogRequestId(String dialogRequestId) {
            this.dialogRequestId = dialogRequestId;
        }
    }

    public static class Payload{
        String token;
        String profile;
        String format;
        Boolean muted;
        Long volume;
        Long offsetInMilliseconds;

        public String getProfile() {
            return profile;
        }

        public String getFormat() {
            return format;
        }


    }

    public static class EventWrapper{
        Event event;
        List<Event> context = new ArrayList<>();

        public Event getEvent() {
            return event;
        }

        public List<Event> getContext() {
            return context;
        }

        public String toJson(){
            return new Gson().toJson(this)+"\n";
        }
    }

    public static class Builder{
        Event event = new Event();
        Payload payload = new Payload();
        Header header = new Header();
        List<Event> context = new ArrayList<>();

        public Builder(){
            event.setPayload(payload);
            event.setHeader(header);
        }

        public EventWrapper build(){
            EventWrapper wrapper = new EventWrapper();
            wrapper.event = event;

            if (context != null && !context.isEmpty() && !(context.size() == 1 && context.get(0) == null)) {
                wrapper.context = context;
            }

            return wrapper;
        }

        public String toJson(){
            return build().toJson();
        }

        public Builder setContext(List<Event> context) {
            if (context == null) {
                return this;
            }
            this.context = context;
            return this;
        }

        public Builder setHeaderNamespace(String namespace){
            header.namespace = namespace;
            return this;
        }

        public Builder setHeaderName(String name){
            header.name = name;
            return this;
        }

        public Builder setHeaderMessageId(String messageId){
            header.messageId = messageId;
            return this;
        }

        public Builder setHeaderDialogRequestId(String dialogRequestId){
            header.dialogRequestId = dialogRequestId;
            return this;
        }

        public Builder setPayloadProfile(String profile){
            payload.profile = profile;
            return this;
        }
        public Builder setPayloadFormat(String format){
            payload.format = format;
            return this;
        }

        public Builder setPayloadMuted(boolean muted){
            payload.muted = muted;
            return this;
        }

        public Builder setPayloadVolume(long volume){
            payload.volume = volume;
            return this;
        }
        public Builder setPayloadToken(String token){
            payload.token = token;
            return this;
        }
        public Builder setPlayloadOffsetInMilliseconds(long offsetInMilliseconds){
            payload.offsetInMilliseconds = offsetInMilliseconds;
            return this;
        }
    }

    public static String getSpeechRecognizerEvent(){
        Builder builder = new Builder();
        builder.setHeaderNamespace("SpeechRecognizer")
                .setHeaderName("Recognize")
                .setHeaderMessageId(getUuid())
                .setHeaderDialogRequestId("dialogRequest-321")
                .setPayloadFormat("AUDIO_L16_RATE_16000_CHANNELS_1")
                .setPayloadProfile("NEAR_FIELD");
        return builder.toJson();
    }

    public static String getVolumeChangedEvent(long volume, boolean isMute){
        Builder builder = new Builder();
        builder.setHeaderNamespace("Speaker")
                .setHeaderName("VolumeChanged")
                .setHeaderMessageId(getUuid())
                .setPayloadVolume(volume)
                .setPayloadMuted(isMute);
        return builder.toJson();
    }
    public static String getMuteEvent(boolean isMute){
        Builder builder = new Builder();
        builder.setHeaderNamespace("Speaker")
                .setHeaderName("VolumeChanged")
                .setHeaderMessageId(getUuid())
                .setPayloadMuted(isMute);
        return builder.toJson();
    }

    public static String getExpectSpeechTimedOutEvent(){
        Builder builder = new Builder();
        builder.setHeaderNamespace("SpeechRecognizer")
                .setHeaderName("ExpectSpeechTimedOut")
                .setHeaderMessageId(getUuid());
        return builder.toJson();
    }

    public static String getSpeechNearlyFinishedEvent(String token, long offsetInMilliseconds){
        Builder builder = new Builder();
        builder.setHeaderNamespace("SpeechSynthesizer")
                .setHeaderName("PlaybackNearlyFinished")
                .setHeaderMessageId(getUuid())
                .setPayloadToken(token)
                .setPlayloadOffsetInMilliseconds(offsetInMilliseconds);
        return builder.toJson();
    }

    public static String getPlaybackNearlyFinishedEvent(String token, long offsetInMilliseconds){
        Builder builder = new Builder();
        builder.setHeaderNamespace("AudioPlayer")
                .setHeaderName("PlaybackNearlyFinished")
                .setHeaderMessageId(getUuid())
                .setPayloadToken(token)
                .setPlayloadOffsetInMilliseconds(offsetInMilliseconds);
        return builder.toJson();
    }
    public static String getPlaybackControllerPlayCommandIssued(){
        Builder builder = new Builder();
        builder.setHeaderNamespace("PlaybackController")
                .setHeaderName("PlayCommandIssued")
                .setHeaderMessageId(getUuid());
        return builder.toJson();
    }

    public static String getPlaybackControllerPauseCommandIssued(){
        Builder builder = new Builder();
        builder.setHeaderNamespace("PlaybackController")
                .setHeaderName("PauseCommandIssued")
                .setHeaderMessageId(getUuid());
        return builder.toJson();
    }

    public static String getPlaybackControllerNextCommandIssued(){
        Builder builder = new Builder();
        builder.setHeaderNamespace("PlaybackController")
                .setHeaderName("NextCommandIssued")
                .setHeaderMessageId(getUuid());
        return builder.toJson();
    }

    public static String getPlaybackControllerPreviousCommandIssued(){
        Builder builder = new Builder();
        builder.setHeaderNamespace("PlaybackController")
                .setHeaderName("PreviousCommandIssued")
                .setHeaderMessageId(getUuid());
        return builder.toJson();
    }

    public static String getSetAlertSucceededEvent(String token) {
        return getAlertEvent(token, "SetAlertSucceeded");
    }

    public static String getSetAlertFailedEvent(String token) {
        return getAlertEvent(token, "SetAlertFailed");
    }

    public static String getDeleteAlertSucceededEvent(String token) {
        return getAlertEvent(token, "DeleteAlertSucceeded");
    }

    public static String getDeleteAlertFailedEvent(String token) {
        return getAlertEvent(token, "DeleteAlertFailed");
    }

    public static String getAlertStartedEvent(String token) {
        return getAlertEvent(token, "AlertStarted");
    }

    public static String getAlertStoppedEvent(String token) {
        return getAlertEvent(token, "AlertStopped");
    }

    public static String getAlertEnteredForegroundEvent(String token) {
        return getAlertEvent(token, "AlertEnteredForeground");
    }

    public static String getAlertEnteredBackgroundEvent(String token) {
        return getAlertEvent(token, "AlertEnteredBackground");
    }

    private static String getAlertEvent(String token, String type) {
        Builder builder = new Builder();
        builder.setHeaderNamespace("Alerts")
                .setHeaderName(type)
                .setHeaderMessageId(getUuid())
                .setPayloadToken(token);
        return builder.toJson();
    }

    public static String getSpeechStartedEvent(String token){
        Builder builder = new Builder();
        builder.setHeaderNamespace("SpeechSynthesizer")
                .setHeaderName("SpeechStarted")
                .setHeaderMessageId(getUuid())
                .setPayloadToken(token);
        return builder.toJson();
    }

    public static String getSpeechFinishedEvent(String token){
        Builder builder = new Builder();
        builder.setHeaderNamespace("SpeechSynthesizer")
                .setHeaderName("SpeechFinished")
                .setHeaderMessageId(getUuid())
                .setPayloadToken(token);
        return builder.toJson();
    }


    public static String getPlaybackStartedEvent(String token, long offset){
        Builder builder = new Builder();
        builder.setHeaderNamespace("AudioPlayer")
                .setHeaderName("PlaybackStarted")
                .setPlayloadOffsetInMilliseconds(offset)
                .setHeaderMessageId(getUuid())
                .setPayloadToken(token);
        return builder.toJson();
    }

    public static String getPlaybackFinishedEvent(String token){
        Builder builder = new Builder();
        builder.setHeaderNamespace("AudioPlayer")
                .setHeaderName("PlaybackFinished")
                .setHeaderMessageId(getUuid())
                .setPayloadToken(token);
        return builder.toJson();
    }


    public static String getSynchronizeStateEvent(){
        Builder builder = new Builder();
        builder.setHeaderNamespace("System")
                .setHeaderName("SynchronizeState")
                .setHeaderMessageId(getUuid());
        return builder.toJson();
    }


}


