package com.himel.aeropedia.alexa;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.himel.aeropedia.R;
import com.himel.aeropedia.treeview.IconTreeItemHolder;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;
import com.willblaschko.android.alexa.AuthorizationManager;
import com.willblaschko.android.alexa.callbacks.AsyncCallback;
import com.willblaschko.android.alexa.requestbody.DataRequestBody;

import java.io.IOException;
import java.util.Locale;

import ee.ioc.phon.android.speechutils.AudioRecorder;
import ee.ioc.phon.android.speechutils.RawAudioRecorder;
import io.alterac.blurkit.BlurLayout;
import okio.BufferedSink;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphImageButton;
import soup.neumorphism.ShapeType;


public class AlexaActivity extends CoreActivity {

    private TextView status;
    private View loading;
    private LottieAnimationView listening;
    private LottieAnimationView speaking;
    private final static int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private static final int AUDIO_RATE = 16000;
    private RawAudioRecorder recorder;
    private NeumorphImageButton recorderView;
    private NeumorphImageButton informationButton;
    private NeumorphButton closeDialog;
    private NeumorphButton login;
    private boolean loggedIn = false;
    private boolean speak = false;
    private FlowingDrawer mDrawer;
    private BlurLayout blur;
    private AndroidTreeView tView;
    private NeumorphButton langToggle;
    private NeumorphImageButton darkToggle;
    private String enableDark;
    private String enableDarkOnCreate;
    private Locale locale;
    private MediaPlayer mp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loggedIn = checkLogin();

        if (!loggedIn) {

            loadLocale();
            locale = Locale.getDefault();
            enableDarkOnCreate = verifyDarkMode();
            if(enableDark.equals("No")) {
                setContentView(R.layout.activity_alexa_login_light);
            } else {
                setContentView(R.layout.activity_alexa_login_dark);
            }

            login = findViewById(R.id.login);

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alexaManager.sendAudioRequest(requestBody, getRequestCallback());
                }
            });
            languageDarkToggle();
            loadDrawer();
        }
        else {
            loadAlexa();
            languageDarkToggle();
            loadDrawer();
        }



    }

    private void loadAlexa() {
        loadLocale();
        locale = Locale.getDefault();
        enableDarkOnCreate = verifyDarkMode();
        if(enableDark.equals("No")) {
            setContentView(R.layout.activity_alexa_light);
        } else {
            setContentView(R.layout.activity_alexa_dark);
        }


        mp = MediaPlayer.create(this, R.raw.notification_sound);
        recorderView = findViewById(R.id.recorder);
        recorderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (speak) {
                    AlexaActivity.super.avsQueue.clear();
                    onStop();
                    stateFinished();
                    speaking.setVisibility(View.GONE);
                    listening.setVisibility(View.GONE);
                } else if (recorder == null) {
                    startListening();
                } else {
                    stopListening();
                    listening.setVisibility(View.GONE);
                }
            }
        });


        //statusBar = findViewById(R.id.status_bar);
        status = findViewById(R.id.status);
        loading = findViewById(R.id.loading);
        listening = findViewById(R.id.listening);
        speaking = findViewById(R.id.speaking);
        informationButton = findViewById(R.id.info);
        //closeDialog = findViewById(R.id.close_dialog);

        Dialog dialog = new Dialog(AlexaActivity.this);
        dialog.setContentView(R.layout.activity_dialog);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        dialog.getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.95), ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        closeDialog = dialog.findViewById(R.id.close_dialog);

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        informationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }

    private void loadDrawer() {
        mDrawer = findViewById(R.id.drawerlayout);
        blur = findViewById(R.id.blurLayout);

        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_FULLSCREEN);
        mDrawer.setOnDrawerStateChangeListener(new ElasticDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (newState == ElasticDrawer.STATE_CLOSING) {
                    Log.i("MainActivity", "Drawer STATE_CLOSED");
                    blur.setVisibility(View.GONE);
                    blur.setAlpha(0f);
                } else if (newState == ElasticDrawer.STATE_OPENING) {
                    blur.invalidate();
                    blur.setAlpha(0.0f);
                    blur.setVisibility(View.VISIBLE);
                    blur.animate().alpha(1.0f).setDuration(1400);
                }
            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {
                Log.i("MainActivity", "openRatio=" + openRatio + " ,offsetPixels=" + offsetPixels);
            }
        });

        createTreeView();
    }

    @Override
    protected void startListening() {

        if (recorder == null) {
            recorder = new RawAudioRecorder(AUDIO_RATE);
        }
        mp.start();
        recorder.start();
        alexaManager.sendAudioRequest(requestBody, getRequestCallback());

        if (loggedIn) {
            listening.setVisibility(View.VISIBLE);
            speaking.setVisibility(View.GONE);
        }
    }

    private DataRequestBody requestBody = new DataRequestBody() {
        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            while (recorder != null && recorder.getState() != AudioRecorder.State.ERROR && !recorder.isPausing()) {
                if (recorder != null) {
                    if (recorderView != null) {
                        recorderView.post(new Runnable() {
                            @Override
                            public void run() {
                                //recorderView.setRmsdbLevel(rmsdb);
                                //listening.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    if (sink != null && recorder != null) {
                        sink.write(recorder.consumeRecording());
                    }
                }

                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            stopListening();
        }

    };


    private void stopListening() {
        if (loggedIn) {
            if (recorder != null) {
                recorder.stop();
                recorder.release();
                recorder = null;
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //tear down our recorder on stop
        if (loggedIn) {
            if (recorder != null) {
                recorder.stop();
                recorder.release();
                recorder = null;
            }
            listening.setVisibility(View.GONE);
            speak = false;
        }
    }


    protected void stateListening() {
        if (loggedIn) {
            if (status != null) {
                status.setText(R.string.status_listening);
                loading.setVisibility(View.GONE);
                //statusBar.animate().alpha(1);
                speak = false;
            }
            listening.setVisibility(View.VISIBLE);
        }
    }

    protected void stateProcessing() {
        if (loggedIn) {
            if (status != null) {
                status.setText(R.string.status_processing);
                loading.setVisibility(View.VISIBLE);
                //statusBar.animate().alpha(1);
            }
            speak = false;
        }
    }

    protected void stateSpeaking() {
        if (loggedIn) {
            speak = true;
            if (status != null) {
                status.setText(R.string.status_speaking);
                loading.setVisibility(View.VISIBLE);
                //statusBar.animate().alpha(1);
            }
            listening.setVisibility(View.GONE);
            speaking.setVisibility(View.VISIBLE);
        }
    }

    protected void statePrompting() {
        if (loggedIn) {
            if (status != null) {
                status.setText("");
                loading.setVisibility(View.VISIBLE);
                //statusBar.animate().alpha(1);
            }
        }
    }

    protected void stateFinished() {
        if (loggedIn) {
            if (status != null) {
                status.setText("");
                //statusBar.animate().alpha(0);
                speak = false;
            }
            loading.setVisibility(View.GONE);
            speaking.setVisibility(View.GONE);
        }
    }

    protected void stateNone() {
        if (loggedIn) {
            speaking.setVisibility(View.GONE);
            listening.setVisibility(View.GONE);
            speak = false;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        checkLogin();
        if(loggedIn != true) {

            loggedIn = true;
            loadAlexa();
            languageDarkToggle();
            loadDrawer();

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.RECORD_AUDIO)) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                }
            }
        }

        if(!enableDarkOnCreate.equals(verifyDarkMode()) && !locale.equals(Locale.getDefault())) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            createTreeView();
        }
        else if (!locale.equals(Locale.getDefault())) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
        else if (!enableDarkOnCreate.equals(verifyDarkMode())) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            createTreeView();
        }

    }

    private boolean checkLogin() {
        boolean[] loggedIn = {false};
        AuthorizationManager authManager = new AuthorizationManager(this, Global.PRODUCT_ID);

        authManager.checkLoggedIn(this, new AsyncCallback<Boolean, Throwable>() {
            @Override
            public void start() {

            }

            @Override
            public void success(Boolean result) {
                //if we are, return a success
                if (result) {
                    loggedIn[0] = true;
                } else {
                    loggedIn[0] = false;
                }
            }

            @Override
            public void failure(Throwable error) {
                loggedIn[0] = false;
            }

            @Override
            public void complete() {

            }
        });

        System.out.println("logged in method ======= " + loggedIn[0]);
        return loggedIn[0];

    }


    /** TreeView **/

    private void createTreeView() {

        ViewGroup containerView = (ViewGroup) findViewById(R.id.inside);


        TreeNode root = TreeNode.root();
        TreeNode manufacturerRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_laptop, "Manufacturers", "No", "Manufacturers"));
        TreeNode amazonRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.drawable.ic_amazon_alexa, "Amazon Alexa", "HighlightDark", "Alexa"));
        TreeNode firebaseRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_laptop, "Firebase", "No", "firebase"));



        TreeNode airbus = null;
        //if (verifyDarkMode().equals("Yes")) {
            //airbus = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, this.getString(R.string.airbus), "HighlightLight", "airbus"));
//        } else {
            airbus = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, this.getString(R.string.airbus), "No", "airbus"));
//        }
        TreeNode a220Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a220), "No", "a220"));
        TreeNode a319Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a319), "No", "a319"));
        TreeNode a320Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a320), "No", "a320"));
        TreeNode a321Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a321), "No", "a321"));

        TreeNode a330Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_alexa, this.getString(R.string.a330), "No", "a330"));
        TreeNode a340Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a340), "No", "a340"));
        TreeNode a350Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a350), "No", "a350"));
        TreeNode a380Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a380), "No", "a380"));


        airbus.addChildren(a220Node, a319Node, a320Node, a321Node, a330Node, a340Node, a350Node, a380Node);


        TreeNode boeing = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_photo_library, this.getString(R.string.boeing), "No", "boeing"));
        TreeNode b777 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_photo, "B777", "No", "b777"));
        TreeNode b787 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_photo, "B787", "No", "b787"));
        boeing.addChildren(b777, b787);

        manufacturerRoot.addChildren(airbus, boeing);


        root.addChildren(manufacturerRoot);
        root.addChildren(amazonRoot);
        root.addChildren(firebaseRoot);


        tView = new AndroidTreeView(this, root);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        tView.setDefaultViewHolder(IconTreeItemHolder.class);

        containerView.addView(tView.getView());

//        if (savedInstanceState != null) {
//            String state = savedInstanceState.getString("tState");
//            if (!TextUtils.isEmpty(state)) {
//                tView.restoreState(state);
//            }
//        }

    }

    /** TreeView **/


    private void languageDarkToggle() {

        darkToggle = findViewById(R.id.dark_toggle);
        langToggle = findViewById(R.id.lang_toggle);
        // setting NeumorphismButton shape based on state
        if (locale.toString().contains("en")) {
            langToggle.setShapeType(ShapeType.FLAT);
        } else if (locale.toString().contains("fr")) {
            langToggle.setShapeType(ShapeType.BASIN);
        }

        langToggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(getResources().getConfiguration().locale.toString().contains("fr")) {
                    setLocale("en");
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                } else if (getResources().getConfiguration().toString().contains("en")) {
                    setLocale("fr");
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            }
        });

        darkToggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
                if (prefs.getString("DarkMode", "").equals("Yes")) {
                    toggleDark("No");
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                } else if (prefs.getString("DarkMode", "").equals("No")) {
                    toggleDark("Yes");
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }

            }
        });
    }

    /** Changing app language **/

    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("Language", language);
        editor.apply();
    }

    private void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        if (prefs.getString("Language", getResources().getConfiguration().locale.toString().substring(0, 2)).equals("")) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("Language", getResources().getConfiguration().locale.toString().substring(0, 2));
            editor.apply();
        }
        String language = prefs.getString("Language", getResources().getConfiguration().locale.toString().substring(0, 2));
        setLocale(language);
    }

    /** Changing app language **/

    /** Dark mode **/

    private void toggleDark(String darkEnabled) {
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("DarkMode", darkEnabled);
        editor.apply();
    }

    private String verifyDarkMode() {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        if (prefs.getString("DarkMode", "").equals("")) {

            SharedPreferences.Editor editor = prefs.edit();
            int currentNightMode = getResources().getConfiguration().uiMode
                    & Configuration.UI_MODE_NIGHT_MASK;

            switch (currentNightMode) {
                case Configuration.UI_MODE_NIGHT_NO:
                    editor.putString("DarkMode", "No");
                    editor.apply();
                    break;
                default:
                    editor.putString("DarkMode", "Yes");
                    editor.apply();
                    break;

            }
        }
        enableDark = prefs.getString("DarkMode", "Yes");

        return enableDark;
    }

    /** Dark mode **/

}