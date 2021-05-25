package com.himel.aeropedia.treeview;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.himel.aeropedia.R;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingMenuLayout;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import io.alterac.blurkit.BlurLayout;

public class TreeView extends AppCompatActivity {

    private AndroidTreeView tView;

    private FlowingDrawer mDrawer;
    private FlowingMenuLayout flowingMenuLayout;
    private BlurLayout blur;
    private RelativeLayout layout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_dark);


        layout = findViewById(R.id.inside);

        blur = findViewById(R.id.blurLayout);
        flowingMenuLayout = findViewById(R.id.menulayout);
        mDrawer = findViewById(R.id.drawerlayout);
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_FULLSCREEN);
        mDrawer.setOnDrawerStateChangeListener(new ElasticDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (newState == ElasticDrawer.STATE_CLOSING) {
                    Log.i("MainActivity", "Drawer STATE_CLOSED");
                    blur.setVisibility(View.GONE);
                    blur.setAlpha(0f);
                }
                else if (newState == ElasticDrawer.STATE_OPENING) {
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

        createTreeView(savedInstanceState);

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tState", tView.getSaveState());
    }

    private void createTreeView(Bundle savedInstanceState) {
        /** TreeView **/
        ViewGroup containerView = (ViewGroup) findViewById(R.id.inside);


        TreeNode root = TreeNode.root();
        TreeNode manufacturerRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_laptop, "Manufacturers", "No", "Manufacturers"));
        TreeNode amazonRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.drawable.ic_amazon_alexa, "Amazon Alexa", "No", "Alexa"));
        TreeNode firebaseRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_laptop, "Firebase", "No", "firebase"));



        TreeNode airbus = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, this.getString(R.string.airbus), "No", "airbus"));
        TreeNode a220Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a220), "No", "a220"));
        TreeNode a319Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a319), "No", "a319"));
        TreeNode a320Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a320), "No", "a320"));
        TreeNode a321Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, this.getString(R.string.a321), "No", "a321"));

        TreeNode a330Node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_alexa, this.getString(R.string.a330), "Highlight", "a330"));
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
        manufacturerRoot.setExpanded(true);
        airbus.setExpanded(true);


        tView = new AndroidTreeView(this, root);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        tView.setDefaultViewHolder(IconTreeItemHolder.class);

        containerView.addView(tView.getView());

        if (savedInstanceState != null) {
            String state = savedInstanceState.getString("tState");
            if (!TextUtils.isEmpty(state)) {
                tView.restoreState(state);
            }
        }

        /** TreeView **/
    }
}
