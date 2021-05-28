package com.willblaschko.android.alexa.interfaces.playbackcontrol;

import com.willblaschko.android.alexa.interfaces.AvsItem;

/**
 * Directive to replace all the items in the queue plus the currently playing item
 *
 * {@link com.willblaschko.android.alexa.data.Directive} response item type parsed so we can properly
 * deal with the incoming commands from the Alexa server.
 *
 * @author will on 5/21/2016.
 */
public class AvsReplaceAllItem extends AvsItem {
    public AvsReplaceAllItem(String token) {
        super(token);
    }
}
