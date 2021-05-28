package com.willblaschko.android.alexa.interfaces.audioplayer;

import com.willblaschko.android.alexa.interfaces.AvsItem;

/**
 * Directive to play a remote URL item
 *
 * {@link com.willblaschko.android.alexa.data.Directive} response item type parsed so we can properly
 * deal with the incoming commands from the Alexa server.
 *
 * @author will on 5/21/2016.
 */
public class AvsPlayRemoteItem extends AvsItem {
    private String mUrl;
    private String mStreamId;
    private long mStartOffset;

    public AvsPlayRemoteItem(String token, String url, long startOffset) {
        super(token);
        mUrl = url;
        mStartOffset = (startOffset < 0) ? 0 : startOffset;
    }
    public String getUrl() {
        return mUrl;
    }

    public long getStartOffset() {
        return mStartOffset;
    }

}
