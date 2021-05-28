package com.willblaschko.android.alexa.data;

import android.text.TextUtils;

/**
 * A catch-all Directive to classify return responses from the Amazon Alexa v20160207 API
 * Will handle calls to:
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/speechrecognizer">Speech Recognizer</a>
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/alerts">Alerts</a>
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/audioplayer">Audio Player</a>
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/playbackcontroller">Playback Controller</a>
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/speaker">Speaker</a>
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/speechsynthesizer">Speech Synthesizer</a>
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/system">System</a>
 *
 *
 * @author wblaschko on 5/6/16.
 */
public class Directive {
    private Header header;
    private Payload payload;

    public static final String TYPE_SPEAK = "Speak";
    public static final String TYPE_PLAY = "Play";
    public static final String TYPE_STOP = "Stop";
    public static final String TYPE_STOP_CAPTURE = "StopCapture";
    public static final String TYPE_SET_ALERT = "SetAlert";
    public static final String TYPE_DELETE_ALERT = "DeleteAlert";
    public static final String TYPE_SET_VOLUME = "SetVolume";
    public static final String TYPE_ADJUST_VOLUME = "AdjustVolume";
    public static final String TYPE_SET_MUTE = "SetMute";
    public static final String TYPE_EXPECT_SPEECH = "ExpectSpeech";
    public static final String TYPE_MEDIA_PLAY = "PlayCommandIssued";
    public static final String TYPE_MEDIA_PAUSE = "PauseCommandIssued";
    public static final String TYPE_MEDIA_NEXT = "NextCommandIssued";
    public static final String TYPE_MEDIA_PREVIOUS = "PreviousCommandIssue";
    public static final String TYPE_EXCEPTION = "Exception";
    public static final String TYPE_SET_ENDPOINT = "SetEndpoint";

    private static final String PLAY_BEHAVIOR_REPLACE_ALL = "REPLACE_ALL";
    private static final String PLAY_BEHAVIOR_ENQUEUE = "ENQUEUE";
    private static final String PLAY_BEHAVIOR_REPLACE_ENQUEUED = "REPLACE_ENQUEUED";


    //PLAY BEHAVIORS

    public boolean isPlayBehaviorReplaceAll(){
        return TextUtils.equals(payload.getPlayBehavior(), PLAY_BEHAVIOR_REPLACE_ALL);
    }
    public boolean isPlayBehaviorEnqueue(){
        return TextUtils.equals(payload.getPlayBehavior(), PLAY_BEHAVIOR_ENQUEUE);
    }
    public boolean isPlayBehaviorReplaceEnqueued(){
        return TextUtils.equals(payload.getPlayBehavior(), PLAY_BEHAVIOR_REPLACE_ENQUEUED);
    }


    public Header getHeader() {
        return header;
    }

    public Payload getPayload() {
        return payload;
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

        public String getMessageId() {
            return messageId;
        }

        public String getDialogRequestId() {
            return dialogRequestId;
        }
    }
    public static class Payload{
        String url;
        String endpoint;
        String format;
        String token;
        String type;
        String scheduledTime;
        String playBehavior;
        AudioItem audioItem;
        long volume;
        boolean mute;
        long timeoutInMilliseconds;
        String description;
        String code;

        public String getUrl() {
            return url;
        }

        public String getFormat() {
            return format;
        }

        public String getToken() {
            if(token == null){
                //sometimes we need to return the stream tokens, not the top level tokens
                if(audioItem != null && audioItem.getStream() != null){
                    return audioItem.getStream().getToken();
                }
            }
            return token;
        }

        public String getType() {
            return type;
        }

        public String getScheduledTime() {
            return scheduledTime;
        }

        public String getPlayBehavior() {
            return playBehavior;
        }

        public AudioItem getAudioItem() {
            return audioItem;
        }

        public long getVolume() {
            return volume;
        }

        public boolean isMute(){
            return mute;
        }

        public long getTimeoutInMilliseconds(){ return timeoutInMilliseconds; }

        public String getDescription() {
            return description;
        }

        public String getCode() {
            return code;
        }

        public String getEndpoint() {
            return endpoint;
        }
    }

    public static class AudioItem{
        String audioItemId;
        Stream stream;

        public String getAudioItemId() {
            return audioItemId;
        }

        public Stream getStream() {
            return stream;
        }
    }
    public static class Stream{
        String url;
        String streamFormat;
        long offsetInMilliseconds;
        String expiryTime;
        String token;
        String expectedPreviousToken;
        //todo progressReport


        public String getUrl() {
            return url;
        }

        public String getStreamFormat() {
            return streamFormat;
        }

        public long getOffsetInMilliseconds() {
            return offsetInMilliseconds;
        }

        public String getExpiryTime() {
            return expiryTime;
        }

        public String getToken() {
            return token;
        }

        public String getExpectedPreviousToken() {
            return expectedPreviousToken;
        }
    }

    public static class DirectiveWrapper{
        Directive directive;
        public Directive getDirective(){
            return directive;
        }
    }
}
