package com.himel.aeropedia.treeview;

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
        //setHasOptionsMenu(true);

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




        ViewGroup containerView = (ViewGroup) findViewById(R.id.inside);


        TreeNode root = TreeNode.root();
        TreeNode computerRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_laptop, "My Computer"));

        TreeNode myDocuments = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "My Documents"));
        TreeNode downloads = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Downloads"));
        TreeNode file1 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Folder 1"));
        TreeNode file2 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Folder 2"));
        TreeNode file3 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Folder 3"));
        TreeNode file4 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Folder 4"));
        fillDownloadsFolder(downloads);
        downloads.addChildren(file1, file2, file3, file4);

        TreeNode myMedia = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_photo_library, "Photos"));
        TreeNode photo1 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_photo, "Folder 1"));
        TreeNode photo2 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_photo, "Folder 2"));
        TreeNode photo3 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_photo, "Folder 3"));
        myMedia.addChildren(photo1, photo2, photo3);

        myDocuments.addChild(downloads);
        computerRoot.addChildren(myDocuments, myMedia);

        root.addChildren(computerRoot);

        tView = new AndroidTreeView(this, root);
        //tView.setDefaultAnimation(true);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        tView.setDefaultViewHolder(IconTreeItemHolder.class);

        containerView.addView(tView.getView());

        if (savedInstanceState != null) {
            String state = savedInstanceState.getString("tState");
            if (!TextUtils.isEmpty(state)) {
                tView.restoreState(state);
            }
        }
    }


    private int counter = 0;

    private void fillDownloadsFolder(TreeNode node) {
        TreeNode downloads = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Downloads" + (counter++)));
        node.addChild(downloads);
        if (counter < 15) {
            fillDownloadsFolder(downloads);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tState", tView.getSaveState());
    }
}
