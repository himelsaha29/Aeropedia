package com.willblaschko.android.alexa.interfaces.audioplayer;

import com.willblaschko.android.alexa.interfaces.speechsynthesizer.AvsSpeakItem;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Directive to play a local, returned audio item
 *
 * See: {@link AvsSpeakItem}
 *
 * {@link com.willblaschko.android.alexa.data.Directive} response item type parsed so we can properly
 * deal with the incoming commands from the Alexa server.
 *
 * @author will on 5/21/2016.
 */
public class AvsPlayAudioItem extends AvsSpeakItem {
    public AvsPlayAudioItem(String token, String cid, ByteArrayInputStream audio) throws IOException {
        super(token, cid, audio);
    }
}
