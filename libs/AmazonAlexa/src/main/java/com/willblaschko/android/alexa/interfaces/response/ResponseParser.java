package com.willblaschko.android.alexa.interfaces.response;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.willblaschko.android.alexa.data.Directive;
import com.willblaschko.android.alexa.interfaces.AvsException;
import com.willblaschko.android.alexa.interfaces.AvsItem;
import com.willblaschko.android.alexa.interfaces.AvsResponse;
import com.willblaschko.android.alexa.interfaces.alerts.AvsDeleteAlertItem;
import com.willblaschko.android.alexa.interfaces.alerts.AvsSetAlertItem;
import com.willblaschko.android.alexa.interfaces.audioplayer.AvsPlayAudioItem;
import com.willblaschko.android.alexa.interfaces.audioplayer.AvsPlayRemoteItem;
import com.willblaschko.android.alexa.interfaces.errors.AvsResponseException;
import com.willblaschko.android.alexa.interfaces.playbackcontrol.AvsMediaNextCommandItem;
import com.willblaschko.android.alexa.interfaces.playbackcontrol.AvsMediaPauseCommandItem;
import com.willblaschko.android.alexa.interfaces.playbackcontrol.AvsMediaPlayCommandItem;
import com.willblaschko.android.alexa.interfaces.playbackcontrol.AvsMediaPreviousCommandItem;
import com.willblaschko.android.alexa.interfaces.playbackcontrol.AvsReplaceAllItem;
import com.willblaschko.android.alexa.interfaces.playbackcontrol.AvsReplaceEnqueuedItem;
import com.willblaschko.android.alexa.interfaces.playbackcontrol.AvsStopItem;
import com.willblaschko.android.alexa.interfaces.speaker.AvsAdjustVolumeItem;
import com.willblaschko.android.alexa.interfaces.speaker.AvsSetMuteItem;
import com.willblaschko.android.alexa.interfaces.speaker.AvsSetVolumeItem;
import com.willblaschko.android.alexa.interfaces.speechrecognizer.AvsExpectSpeechItem;
import com.willblaschko.android.alexa.interfaces.speechrecognizer.AvsStopCaptureItem;
import com.willblaschko.android.alexa.interfaces.speechsynthesizer.AvsSpeakItem;
import com.willblaschko.android.alexa.interfaces.system.AvsSetEndpointItem;

import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Headers;
import okhttp3.Response;

import static okhttp3.internal.Util.UTF_8;

/**
 * Static helper class to parse incoming responses from the Alexa server and generate a corresponding
 * {@link AvsResponse} item with all the directives matched to their audio streams.
 *
 * @author will on 5/21/2016.
 */
public class ResponseParser {

    public static final String TAG = "ResponseParser";

    private static final Pattern PATTERN = Pattern.compile("<(.*?)>");

    /**
     * Get the AvsItem associated with a Alexa API post/get, this will contain a list of {@link AvsItem} directives,
     * if applicable.
     *
     * Includes hacky work around for PausePrompt items suggested by Eric@Amazon
     * @see <a href="https://forums.developer.amazon.com/questions/28021/response-about-the-shopping-list.html">Forum Discussion</a>
     *
     * @param stream the input stream as a result of our  OkHttp post/get calls
     * @param boundary the boundary we're using to separate the multiparts
     * @return the parsed AvsResponse
     * @throws IOException
     */

    public static AvsResponse parseResponse(InputStream stream, String boundary) throws IOException, IllegalStateException, AvsException {
        return parseResponse(stream, boundary, false);
    }

    public static AvsResponse parseResponse(InputStream stream, String boundary, boolean checkBoundary) throws IOException, IllegalStateException, AvsException {
        long start = System.currentTimeMillis();

        List<Directive> directives = new ArrayList<>();
        HashMap<String, ByteArrayInputStream> audio = new HashMap<>();

        byte[] bytes;
        try {
            bytes = IOUtils.toByteArray(stream);
        } catch (IOException exp) {
            exp.printStackTrace();
            Log.e(TAG, "Error copying bytes[]");
            return new AvsResponse();
        }

        String responseString = string(bytes);
        Log.i(TAG, responseString);
        if (checkBoundary) {
            final String responseTrim = responseString.trim();
            final String testBoundary = "--" + boundary;
            if (!StringUtils.isEmpty(responseTrim) && StringUtils.endsWith(responseTrim, testBoundary) && !StringUtils.startsWith(responseTrim, testBoundary)) {
                responseString = "--" + boundary + "\r\n" + responseString;
                bytes = responseString.getBytes();
            }
        }

        MultipartStream mpStream = new MultipartStream(new ByteArrayInputStream(bytes), boundary.getBytes(), 100000, null);

        //have to do this otherwise mpStream throws an exception
        if (mpStream.skipPreamble()) {
            Log.i(TAG, "Found initial boundary: true");

            //we have to use the count hack here because otherwise readBoundary() throws an exception
            int count = 0;
            while (count < 1 || mpStream.readBoundary()) {
                String headers;
                try {
                    headers = mpStream.readHeaders();
                } catch (MultipartStream.MalformedStreamException exp) {
                    break;
                }
                ByteArrayOutputStream data = new ByteArrayOutputStream();
                mpStream.readBodyData(data);
                if (!isJson(headers)) {
                    // get the audio data
                    //convert our multipart into byte data
                    String contentId = getCID(headers);
                    if(contentId != null) {
                        Matcher matcher = PATTERN.matcher(contentId);
                        if (matcher.find()) {
                            String currentId = "cid:" + matcher.group(1);
                            audio.put(currentId, new ByteArrayInputStream(data.toByteArray()));
                        }
                    }
                } else {
                    // get the json directive
                    String directive = data.toString(Charset.defaultCharset().displayName());

                    directives.add(getDirective(directive));
                }
                count++;
            }

        } else {
            Log.i(TAG, "Response Body: \n" + string(bytes));
            try {
                directives.add(getDirective(responseString));
            }catch (JsonParseException e) {
                e.printStackTrace();
                throw new AvsException("Response from Alexa server malformed. ");
            }
        }

        AvsResponse response = new AvsResponse();

        for (Directive directive: directives) {

            if(directive.isPlayBehaviorReplaceAll()){
                response.add(0, new AvsReplaceAllItem(directive.getPayload().getToken()));
            }
            if(directive.isPlayBehaviorReplaceEnqueued()){
                response.add(new AvsReplaceEnqueuedItem(directive.getPayload().getToken()));
            }

            AvsItem item = parseDirective(directive, audio);

            if(item != null){
                response.add(item);
            }
        }

        Log.i(TAG, "Parsing response took: " + (System.currentTimeMillis() - start) +" size is " + response.size());

        if(response.size() == 0){
            Log.i(TAG, string(bytes));
        }

        return response;
    }

    public static AvsItem parseDirective(Directive directive) throws IOException {
        return parseDirective(directive, null);
    }

    public static AvsItem parseDirective(Directive directive, HashMap<String, ByteArrayInputStream> audio) throws IOException {
        Log.i(TAG, "Parsing directive type: "+directive.getHeader().getNamespace()+":"+directive.getHeader().getName());
        switch (directive.getHeader().getName()) {
            case Directive.TYPE_SPEAK:
                String cid = directive.getPayload().getUrl();
                return new AvsSpeakItem(directive.getPayload().getToken(), cid, audio.get(cid));
            case Directive.TYPE_PLAY:
                String url = directive.getPayload().getAudioItem().getStream().getUrl();
                if(url.contains("cid:")){
                    return new AvsPlayAudioItem(directive.getPayload().getToken(), url, audio.get(url));
                }else{
                    return new AvsPlayRemoteItem(directive.getPayload().getToken(), url, directive.getPayload().getAudioItem().getStream().getOffsetInMilliseconds());
                }
            case Directive.TYPE_STOP_CAPTURE:
                return new AvsStopCaptureItem(directive.getPayload().getToken());
            case Directive.TYPE_STOP:
                return new AvsStopItem(directive.getPayload().getToken());
            case Directive.TYPE_SET_ALERT:
                return new AvsSetAlertItem(directive.getPayload().getToken(), directive.getPayload().getType(), directive.getPayload().getScheduledTime());
            case Directive.TYPE_DELETE_ALERT:
                return new AvsDeleteAlertItem(directive.getPayload().getToken());
            case Directive.TYPE_SET_MUTE:
                return new AvsSetMuteItem(directive.getPayload().getToken(), directive.getPayload().isMute());
            case Directive.TYPE_SET_VOLUME:
                return new AvsSetVolumeItem(directive.getPayload().getToken(), directive.getPayload().getVolume());
            case Directive.TYPE_ADJUST_VOLUME:
                return new AvsAdjustVolumeItem(directive.getPayload().getToken(), directive.getPayload().getVolume());
            case Directive.TYPE_EXPECT_SPEECH:
                return new AvsExpectSpeechItem(directive.getPayload().getToken(), directive.getPayload().getTimeoutInMilliseconds());
            case Directive.TYPE_MEDIA_PLAY:
                return new AvsMediaPlayCommandItem(directive.getPayload().getToken());
            case Directive.TYPE_MEDIA_PAUSE:
                return new AvsMediaPauseCommandItem(directive.getPayload().getToken());
            case Directive.TYPE_MEDIA_NEXT:
                return new AvsMediaNextCommandItem(directive.getPayload().getToken());
            case Directive.TYPE_MEDIA_PREVIOUS:
                return new AvsMediaPreviousCommandItem(directive.getPayload().getToken());
            case Directive.TYPE_SET_ENDPOINT:
                return new AvsSetEndpointItem(directive.getPayload().getToken(), directive.getPayload().getEndpoint());
            case Directive.TYPE_EXCEPTION:
                return new AvsResponseException(directive);
            default:
                Log.e(TAG, "Unknown type found");
                return null;
        }
    }

    public static String getBoundary(Response response) throws IOException {
        Headers headers = response.headers();
        String header = headers.get("content-type");
        String boundary = "";

        if (header != null) {
            Pattern pattern = Pattern.compile("boundary=(.*?);");
            Matcher matcher = pattern.matcher(header);
            if (matcher.find()) {
                boundary = matcher.group(1);
            }
        } else {
            Log.i(TAG, "Body: " + response.body().string());
        }
        return boundary;
    }


    private static final String string(byte[] bytes) throws IOException {
        return new String(bytes, UTF_8);
    }

    /**
     * Parse our directive using Gson into an object
     * @param directive the string representation of our JSON object
     * @return the reflected directive
     */
    public static Directive getDirective(String directive) throws AvsException, IllegalStateException {
        Log.i(TAG, directive);
        Gson gson = new Gson();
        Directive.DirectiveWrapper wrapper = gson.fromJson(directive, Directive.DirectiveWrapper.class);
        if (wrapper.getDirective() == null) {
            return gson.fromJson(directive, Directive.class);
        }
        return wrapper.getDirective();
    }


    /**
     * Get the content id from the return headers from the AVS server
     * @param headers the return headers from the AVS server
     * @return a string form of our content id
     */
    private static String getCID(String headers) throws IOException {
        final String contentString = "Content-ID:";
        BufferedReader reader = new BufferedReader(new StringReader(headers));
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            if (line.startsWith(contentString)) {
                return line.substring(contentString.length()).trim();
            }
        }
        return null;
    }

    /**
     * Check if the response is JSON (a validity check)
     * @param headers the return headers from the AVS server
     * @return true if headers state the response is JSON, false otherwise
     */
    private static boolean isJson(String headers) {
        if (headers.contains("application/json")) {
            return true;
        }
        return false;
    }
}
