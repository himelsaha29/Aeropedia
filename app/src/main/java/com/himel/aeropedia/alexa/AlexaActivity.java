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
import com.himel.aeropedia.airbus.AirbusA220;
import com.himel.aeropedia.airbus.AirbusA300;
import com.himel.aeropedia.airbus.AirbusA310;
import com.himel.aeropedia.airbus.AirbusA318;
import com.himel.aeropedia.airbus.AirbusA319;
import com.himel.aeropedia.airbus.AirbusA320;
import com.himel.aeropedia.airbus.AirbusA320neoFamily;
import com.himel.aeropedia.airbus.AirbusA321;
import com.himel.aeropedia.airbus.AirbusA330;
import com.himel.aeropedia.airbus.AirbusA330neo;
import com.himel.aeropedia.airbus.AirbusA340;
import com.himel.aeropedia.airbus.AirbusA350;
import com.himel.aeropedia.airbus.AirbusA380;
import com.himel.aeropedia.airbus.AirbusBeluga;
import com.himel.aeropedia.antonov.AntonovAn124Ruslan;
import com.himel.aeropedia.antonov.AntonovAn225Mriya;
import com.himel.aeropedia.antonov.AntonovAn22Antei;
import com.himel.aeropedia.antonov.AntonovAn72Cheburashka;
import com.himel.aeropedia.boeing.Boeing737;
import com.himel.aeropedia.boeing.Boeing747;
import com.himel.aeropedia.boeing.Boeing757;
import com.himel.aeropedia.boeing.Boeing767;
import com.himel.aeropedia.boeing.Boeing777;
import com.himel.aeropedia.boeing.Boeing787;
import com.himel.aeropedia.bombardier.CRJ100200;
import com.himel.aeropedia.bombardier.Challenger650;
import com.himel.aeropedia.bombardier.Global7500;
import com.himel.aeropedia.bombardier.Learjet75;
import com.himel.aeropedia.embraer.EJetE2;
import com.himel.aeropedia.embraer.ERJFamily;
import com.himel.aeropedia.firebase.Firebase;
import com.himel.aeropedia.flightmap.FlightMap;
import com.himel.aeropedia.manufacturers.Airbus;
import com.himel.aeropedia.manufacturers.ManufacturerMenu;
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

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                loggedIn = checkLogin();
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if (!loggedIn) {

            loadLocale();
            locale = Locale.getDefault();
            enableDarkOnCreate = verifyDarkMode();
            if(enableDark.equals("No")) {
                setContentView(R.layout.activity_alexa_login_light);
                Thread thread2 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loggedIn = checkLogin();
                    }
                });
                thread2.start();

                try {
                    thread2.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                /**REFRESH**/
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
                /**REFRESH**/

            } else {
                setContentView(R.layout.activity_alexa_login_dark);
                Thread thread2 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loggedIn = checkLogin();
                    }
                });
                thread2.start();

                try {
                    thread2.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                /**REFRESH**/
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
                /**REFRESH**/
            }

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


        mp = MediaPlayer.create(this, R.raw.google_notification);
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
        if(enableDark.equals("No")) {
            dialog.setContentView(R.layout.activity_dialog_alexa_light);
        } else {
            dialog.setContentView(R.layout.activity_dialog_alexa_dark);
        }
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
            //createTreeView();
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
            //createTreeView();
        }
        if(mDrawer.getDrawerState() == ElasticDrawer.STATE_OPEN) {
            mDrawer.closeMenu(false);
            blur.setVisibility(View.GONE);
            blur.setAlpha(0f);
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
        TreeNode manufacturerRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_manufacturer, this.getString(R.string.manufacturers), "No", "manufacturers", null));
        TreeNode alexaRoot = null;
        if (verifyDarkMode().equals("Yes")) {
            alexaRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_manufacturer, this.getString(R.string.ask_alexa), "HighlightLight", "ask_alexa", null));
        } else {
            alexaRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_manufacturer, this.getString(R.string.ask_alexa), "HighlightDark", "ask_alexa", null));
        }
        TreeNode flightTrackerRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_radar, this.getString(R.string.flight_tracker), "No", "flightTracker", FlightMap.class));
        TreeNode firebaseRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_firebase, this.getString(R.string.firebase), "No", "firebase", Firebase.class));


        TreeNode airbus = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.airbus), "No", "airbus", null));
        TreeNode a220Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a220), "No", "a220", AirbusA220.class));
        TreeNode a300Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a300), "No", "a300", AirbusA300.class));
        TreeNode a310Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a310), "No", "a310", AirbusA310.class));
        TreeNode a318Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a318), "No", "a318", AirbusA318.class));
        TreeNode a319Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a319), "No", "a319", AirbusA319.class));
        TreeNode a320Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a320), "No", "a320", AirbusA320.class));
        TreeNode a321Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a321), "No", "a321", AirbusA321.class));
        TreeNode a320neoNode = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a320neofamily), "No", "a320neofamily", AirbusA320neoFamily.class));
        TreeNode a330Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a330), "No", "a330", AirbusA330.class));
        TreeNode a330neoNode = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a330neo), "No", "a330neo", AirbusA330neo.class));
        TreeNode a340Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a340), "No", "a340", AirbusA340.class));
        TreeNode a350Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a350), "No", "a350", AirbusA350.class));
        TreeNode a380Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.a380), "No", "a380", AirbusA380.class));
        TreeNode belugaNode = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.beluga), "No", "beluga", AirbusBeluga.class));

        airbus.addChildren(a220Node, a300Node, a310Node, a318Node, a319Node, a320Node, a321Node, a320neoNode, a330Node, a330neoNode, a340Node, a350Node, a380Node, belugaNode);

        TreeNode antonov = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_airplane, this.getString(R.string.antonov), "No", "antonov", null));

        TreeNode an124Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.an124), "No", "an124", AntonovAn124Ruslan.class));
        TreeNode an72Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.an72), "No", "an72", AntonovAn72Cheburashka.class));
        TreeNode an22Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.an22), "No", "an22", AntonovAn22Antei.class));
        TreeNode an225Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.an225), "No", "an22", AntonovAn225Mriya.class));


        antonov.addChildren(an22Node, an72Node, an124Node, an225Node);
        TreeNode boeing = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_airplane, this.getString(R.string.boeing), "No", "boeing", null));
        TreeNode b777 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, "777", "No", "b777", Boeing777.class));
        TreeNode b787 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, "787", "No", "b787", Boeing787.class));
        TreeNode b737 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.b737), "No", "b737", Boeing737.class));
        TreeNode b757 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.b757), "No", "b757", Boeing757.class));
        TreeNode b747 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.b747), "No", "b747", Boeing747.class));
        TreeNode b767 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.b767), "No", "b767", Boeing767.class));
        boeing.addChildren(b737, b747, b757, b767, b777, b787);
        TreeNode bombardier = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_airplane, this.getString(R.string.bombardier), "No", "bombardier", null));
        TreeNode learjet75 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.learjet75), "No", "b767", Learjet75.class));
        TreeNode challenger650 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.challenger650), "No", "challenger650", Challenger650.class));
        TreeNode global7500 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.global7500), "No", "global7500", Global7500.class));
        TreeNode crj100200 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.crj_100_200), "No", "crj100200", CRJ100200.class));

        bombardier.addChildren(challenger650, crj100200, learjet75, global7500);
        TreeNode embraer = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_airplane, this.getString(R.string.embraer), "No", "embraer", null));
        TreeNode erjFamily = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.erj_family), "No", "erjfamily", ERJFamily.class));

        TreeNode ejete2Family = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.drawer_tail, this.getString(R.string.ejet_e2_family), "No", "ejete2Family", EJetE2.class));

        embraer.addChildren(erjFamily, ejete2Family);

        manufacturerRoot.addChildren(airbus, antonov, boeing, bombardier, embraer);

        root.addChildren(manufacturerRoot);
        root.addChildren(alexaRoot);
        root.addChildren(flightTrackerRoot);
        root.addChildren(firebaseRoot);

        tView = new AndroidTreeView(this, root);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        tView.setDefaultViewHolder(IconTreeItemHolder.class);

        containerView.addView(tView.getView());

    }

    /** TreeView **/


    private void languageDarkToggle() {

        darkToggle = findViewById(R.id.dark_toggle);
        langToggle = findViewById(R.id.lang_toggle);
        // setting NeumorphismButton shape based on state
        if (locale.toString().contains("en")) {
            langToggle.setShapeType(ShapeType.FLAT);
        } else if (locale.toString().contains("fr")) {
            langToggle.setShapeType(ShapeType.PRESSED);
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