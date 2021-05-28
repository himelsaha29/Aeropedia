package com.willblaschko.android.alexa;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.authorization.api.AmazonAuthorizationManager;
import com.google.gson.Gson;
import com.willblaschko.android.alexa.connection.ClientUtil;
import com.willblaschko.android.alexa.utility.Util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A utility class designed to request, receive, store, and renew Amazon authentication tokens using a Volley interface and the Amazon auth API
 *
 * Some more details here: https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/docs/authorizing-your-alexa-enabled-product-from-a-website
 */
public class TokenManager {

    private final static String TAG = "TokenManager";

    private static String REFRESH_TOKEN;
    private static String ACCESS_TOKEN;

    private final static String ARG_GRANT_TYPE = "grant_type";
    private final static String ARG_CODE = "code";
    private final static String ARG_REDIRECT_URI = "redirect_uri";
    private final static String ARG_CLIENT_ID = "client_id";
    private final static String ARG_CODE_VERIFIER = "code_verifier";
    private final static String ARG_REFRESH_TOKEN = "refresh_token";


    public final static String PREF_ACCESS_TOKEN = "access_token_042017";
    public final static String PREF_REFRESH_TOKEN = "refresh_token_042017";
    public final static String PREF_TOKEN_EXPIRES = "token_expires_042017";

    /**
     * Get an access token from the Amazon servers for the current user
     * @param context local/application level context
     * @param authCode the authorization code supplied by the Authorization Manager
     * @param codeVerifier a randomly generated verifier, must be the same every time
     * @param authorizationManager the AuthorizationManager class calling this function
     * @param callback the callback for state changes
     */
    public static void getAccessToken(final Context context, @NotNull String authCode, @NotNull String codeVerifier, AmazonAuthorizationManager authorizationManager, @Nullable final TokenResponseCallback callback){
        //this url shouldn't be hardcoded, but it is, it's the Amazon auth access token endpoint
        String url = "https://api.amazon.com/auth/O2/token";

        //set up our arguments for the api call, these will be the call headers
        FormBody.Builder builder = new FormBody.Builder()
                .add(ARG_GRANT_TYPE, "authorization_code")
                .add(ARG_CODE, authCode);
        try {
            builder.add(ARG_REDIRECT_URI, authorizationManager.getRedirectUri());
            builder.add(ARG_CLIENT_ID, authorizationManager.getClientId());
        } catch (AuthError authError) {
            authError.printStackTrace();
        }
        builder.add(ARG_CODE_VERIFIER, codeVerifier);

        OkHttpClient client = ClientUtil.getTLS12OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();

        final Handler handler = new Handler(Looper.getMainLooper());


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                e.printStackTrace();
                if(callback != null){
                    //bubble up error
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(e);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                if(BuildConfig.DEBUG) {
                    Log.i(TAG, s);
                }
                final TokenResponse tokenResponse = new Gson().fromJson(s, TokenResponse.class);
                //save our tokens to local shared preferences
                saveTokens(context, tokenResponse);

                if(callback != null){
                    //bubble up success
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(tokenResponse);
                        }
                    });
                }
            }
        });

    }

    /**
     * Check if we have a pre-existing access token, and whether that token is expired. If it is not, return that token, otherwise get a refresh token and then
     * use that to get a new token.
     * @param authorizationManager our AuthManager
     * @param context local/application context
     * @param callback the TokenCallback where we return our tokens when successful
     */
    public static void getAccessToken(@NotNull AmazonAuthorizationManager authorizationManager, @NotNull Context context, @NotNull TokenCallback callback) {
        SharedPreferences preferences = Util.getPreferences(context.getApplicationContext());
        //if we have an access token
        if(preferences.contains(PREF_ACCESS_TOKEN)){

            if(preferences.getLong(PREF_TOKEN_EXPIRES, 0) > System.currentTimeMillis()){
                //if it's not expired, return the existing token
                callback.onSuccess(preferences.getString(PREF_ACCESS_TOKEN, null));
                return;
            }else{
                //if it is expired but we have a refresh token, get a new token
                if(preferences.contains(PREF_REFRESH_TOKEN)){
                    getRefreshToken(authorizationManager, context, callback, preferences.getString(PREF_REFRESH_TOKEN, ""));
                    return;
                }
            }
        }

        //uh oh, the user isn't logged in, we have an IllegalStateException going on!
        callback.onFailure(new IllegalStateException("User is not logged in and no refresh token found."));
    }

    /**
     * Get a new refresh token from the Amazon server to replace the expired access token that we currently have
     * @param authorizationManager
     * @param context
     * @param callback
     * @param refreshToken the refresh token we have stored in local cache (sharedPreferences)
     */
    private static void getRefreshToken(@NotNull AmazonAuthorizationManager authorizationManager, @NotNull final Context context, @NotNull final TokenCallback callback, String refreshToken){
        //this url shouldn't be hardcoded, but it is, it's the Amazon auth access token endpoint
        String url = "https://api.amazon.com/auth/O2/token";


        //set up our arguments for the api call, these will be the call headers
        FormBody.Builder builder = new FormBody.Builder()
                .add(ARG_GRANT_TYPE, "refresh_token")
                .add(ARG_REFRESH_TOKEN, refreshToken);
            builder.add(ARG_CLIENT_ID, authorizationManager.getClientId());


        OkHttpClient client = ClientUtil.getTLS12OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();

        final Handler handler = new Handler(Looper.getMainLooper());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                e.printStackTrace();
                if(callback != null){
                    //bubble up error
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(e);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                if(BuildConfig.DEBUG) {
                    Log.i(TAG, s);
                }

                //get our tokens back
                final TokenResponse tokenResponse = new Gson().fromJson(s, TokenResponse.class);
                //save our tokens
                saveTokens(context, tokenResponse);
                //we have new tokens!
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(tokenResponse.access_token);
                    }
                });
            }
        });
    }

    /**
     * Save our new tokens in SharePreferences so we can access them at a later point
     * @param context
     * @param tokenResponse
     */
    private static void saveTokens(Context context, TokenResponse tokenResponse){
        REFRESH_TOKEN = tokenResponse.refresh_token;
        ACCESS_TOKEN = tokenResponse.access_token;

        SharedPreferences.Editor preferences = Util.getPreferences(context.getApplicationContext()).edit();
        preferences.putString(PREF_ACCESS_TOKEN, ACCESS_TOKEN);
        preferences.putString(PREF_REFRESH_TOKEN, REFRESH_TOKEN);
        //comes back in seconds, needs to be milis
        preferences.putLong(PREF_TOKEN_EXPIRES, (System.currentTimeMillis() + tokenResponse.expires_in * 1000));
        preferences.commit();
    }

    public interface TokenResponseCallback {
        void onSuccess(TokenResponse response);
        void onFailure(Exception error);
    }

    //for JSON parsing of our token responses
    public static class TokenResponse{
        public String access_token;
        public String refresh_token;
        public String token_type;
        public long expires_in;
    }

    public interface TokenCallback{
        void onSuccess(String token);
        void onFailure(Throwable e);
    }
}
