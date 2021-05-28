package com.willblaschko.android.alexa;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.authorization.BuildConfig;
import com.amazon.identity.auth.device.authorization.api.AmazonAuthorizationManager;
import com.amazon.identity.auth.device.authorization.api.AuthorizationListener;
import com.amazon.identity.auth.device.authorization.api.AuthzConstants;
import com.willblaschko.android.alexa.callbacks.AsyncCallback;
import com.willblaschko.android.alexa.callbacks.AuthorizationCallback;
import com.willblaschko.android.alexa.utility.Util;

import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * A static instance class that manages Authentication with the Amazon servers, it uses the TokenManager helper class to do most of its operations
 * including get new/refresh tokens from the server
 *
 * Some more details here: https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/docs/authorizing-your-alexa-enabled-product-from-a-website
 */
public class AuthorizationManager {

    private final static String TAG = "AuthorizationHandler";

    private Context mContext;
    private String mProductId;
    private AmazonAuthorizationManager mAuthManager;
    private static final String[] APP_SCOPES= {"alexa:all"};
    private AuthorizationCallback mCallback;


    private static final String CODE_VERIFIER = "code_verifier";

    /**
     * Create a new Auth Manager based on the supplied product id
     *
     * This will throw an error if our assets/api_key.txt file, our package name, and our signing key don't match the product ID, this is
     * a common sticking point for the application not working
     * @param context
     * @param productId
     */
    public AuthorizationManager(@NotNull Context context, @NotNull String productId){
        mContext = context;
        mProductId = productId;

        try {
            mAuthManager = new AmazonAuthorizationManager(mContext, Bundle.EMPTY);
        }catch(IllegalArgumentException e){
            //This error will be thrown if the main project doesn't have the assets/api_key.txt file in it--this contains the security credentials from Amazon
            Util.showAuthToast(mContext, "APIKey is incorrect or does not exist.");
            Log.e(TAG, "Unable to Use Amazon Authorization Manager. APIKey is incorrect or does not exist. Does assets/api_key.txt exist in the main application?", e);
        }
    }


    /**
     * Check if the user is currently logged in by checking for a valid access token (present and not expired).
     * @param context
     * @param callback
     */
    public void checkLoggedIn(Context context, final AsyncCallback<Boolean, Throwable> callback){
        TokenManager.getAccessToken(mAuthManager, context, new TokenManager.TokenCallback() {
            @Override
            public void onSuccess(String token) {
                callback.success(true);
            }

            @Override
            public void onFailure(Throwable e) {
                callback.success(false);
                callback.failure(e);
            }
        });
    }

    /**
     * Request authorization for the user to be able to use the application, this opens an intent that feeds back to the app:
     *
     * <intent-filter>
     * <action android:name="android.intent.action.VIEW" />
     * <category android:name="android.intent.category.DEFAULT" />
     * <category android:name="android.intent.category.BROWSABLE" />
     * <!-- host should be our application package e.g.: com.example.yourapp.whee //-->
     * <data
     * android:host="APPLICATION.PACKAGE"
     * android:scheme="amzn" />
     * </intent-filter>
     *
     * Make sure this is in the main application's AndroidManifest
     *
     * @param callback our state change callback
     */
    public void authorizeUser(AuthorizationCallback callback){
        mCallback = callback;

        String PRODUCT_DSN = Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        Bundle options = new Bundle();
        String scope_data = "{\"alexa:all\":{\"productID\":\"" + mProductId +
                "\", \"productInstanceAttributes\":{\"deviceSerialNumber\":\"" +
                PRODUCT_DSN + "\"}}}";
        options.putString(AuthzConstants.BUNDLE_KEY.SCOPE_DATA.val, scope_data);

        options.putBoolean(AuthzConstants.BUNDLE_KEY.GET_AUTH_CODE.val, true);
        options.putString(AuthzConstants.BUNDLE_KEY.CODE_CHALLENGE.val, getCodeChallenge());
        options.putString(AuthzConstants.BUNDLE_KEY.CODE_CHALLENGE_METHOD.val, "S256");

        mAuthManager.authorize(APP_SCOPES, options, authListener);
    }

    //An authorization callback to check when we get success/failure from the Amazon authentication server
    private AuthorizationListener authListener = new AuthorizationListener() {
        /**
         * Authorization was completed successfully.
         * Display the profile of the user who just completed authorization
         * @param response bundle containing authorization response. Not used.
         */
        @Override
        public void onSuccess(Bundle response) {
            String authCode = response.getString(AuthzConstants.BUNDLE_KEY.AUTHORIZATION_CODE.val);

            if(BuildConfig.DEBUG) {
                Log.i(TAG, "Authorization successful");
                Util.showAuthToast(mContext, "Authorization successful.");
            }

            TokenManager.getAccessToken(mContext, authCode, getCodeVerifier(), mAuthManager, new TokenManager.TokenResponseCallback() {
                @Override
                public void onSuccess(TokenManager.TokenResponse response) {

                    if(mCallback != null){
                        mCallback.onSuccess();
                    }
                }

                @Override
                public void onFailure(Exception error) {
                    if(mCallback != null){
                        mCallback.onError(error);
                    }
                }
            });

        }


        /**
         * There was an error during the attempt to authorize the application.
         * Log the error, and reset the profile text view.
         * @param ae the error that occurred during authorize
         */
        @Override
        public void onError(AuthError ae) {
            if(BuildConfig.DEBUG) {
                Log.e(TAG, "AuthError during authorization", ae);
                Util.showAuthToast(mContext, "Error during authorization.  Please try again.");
            }
            if(mCallback != null){
                mCallback.onError(ae);
            }
        }

        /**
         * Authorization was cancelled before it could be completed.
         * A toast is shown to the user, to confirm that the operation was cancelled, and the profile text view is reset.
         * @param cause bundle containing the cause of the cancellation. Not used.
         */
        @Override
        public void onCancel(Bundle cause) {
            if(BuildConfig.DEBUG) {
                Log.e(TAG, "User cancelled authorization");
                Util.showAuthToast(mContext, "Authorization cancelled");
            }

            if(mCallback != null){
                mCallback.onCancel();
            }
        }
    };


    /**
     * Return our stored code verifier, which needs to be consistent, if this doesn't exist, we create a new one and store the new result
     * @return the String code verifier
     */
    private String getCodeVerifier(){
        if(Util.getPreferences(mContext).contains(CODE_VERIFIER)){
            return Util.getPreferences(mContext).getString(CODE_VERIFIER, "");
        }

        //no verifier found, make and store the new one
        String verifier = createCodeVerifier();
        Util.getPreferences(mContext).edit().putString(CODE_VERIFIER, verifier).apply();
        return verifier;
    }

    /**
     * Create a String hash based on the code verifier, this is used to verify the Token exchanges
     * @return
     */
    private String getCodeChallenge(){
        String verifier = getCodeVerifier();
        return base64UrlEncode(getHash(verifier));
    }

    /**
     * Create a new code verifier for our token exchanges
     * @return the new code verifier
     */
    static String createCodeVerifier() {
        return createCodeVerifier(128);
    }

    /**
     * Create a new code verifier for our token exchanges
     * @return the new code verifier
     */
    static String createCodeVerifier(int count) {
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }


    /**
     * Encode a byte array into a string, while trimming off the last characters, as required by the Amazon token server
     *
     * See: http://brockallen.com/2014/10/17/base64url-encoding/
     *
     * @param arg our hashed string
     * @return a new Base64 encoded string based on the hashed string
     */
    private static String base64UrlEncode(byte[] arg)
    {
        String s = Base64.encodeToString(arg, 0); // Regular base64 encoder
        s = s.split("=")[0]; // Remove any trailing '='s
        s = s.replace('+', '-'); // 62nd char of encoding
        s = s.replace('/', '_'); // 63rd char of encoding
        return s;
    }

    /**
     * Hash a string based on the SHA-256 message digest
     * @param password
     * @return
     */
    private static byte[] getHash(String password) {
        MessageDigest digest=null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        digest.reset();
        return digest.digest(password.getBytes());
    }


    public AmazonAuthorizationManager getAmazonAuthorizationManager(){
        return mAuthManager;
    }

}
