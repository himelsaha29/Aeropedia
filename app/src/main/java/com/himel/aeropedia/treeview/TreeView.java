package com.himel.aeropedia.treeview;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.himel.aeropedia.R;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

public class TreeView extends Fragment {

    private TextView statusBar;
    private AndroidTreeView tView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.tree_view, null, false);
        ViewGroup containerView = (ViewGroup) rootView.findViewById(R.id.container);

        statusBar = (TextView) rootView.findViewById(R.id.status_bar);

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

        tView = new AndroidTreeView(this.getContext(), root);
        tView.setDefaultAnimation(true);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        tView.setDefaultViewHolder(IconTreeItemHolder.class);
        tView.setDefaultNodeClickListener(nodeClickListener);
        tView.setDefaultNodeLongClickListener(nodeLongClickListener);

        containerView.addView(tView.getView());

        if (savedInstanceState != null) {
            String state = savedInstanceState.getString("tState");
            if (!TextUtils.isEmpty(state)) {
                tView.restoreState(state);
            }
        }

        return rootView;
    }

    private int counter = 0;

    private void fillDownloadsFolder(TreeNode node) {
        TreeNode downloads = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Downloads" + (counter++)));
        node.addChild(downloads);
        if (counter < 5) {
            fillDownloadsFolder(downloads);
        }
    }

    private TreeNode.TreeNodeClickListener nodeClickListener = new TreeNode.TreeNodeClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
            IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
            statusBar.setText("Last clicked: " + item.text);
        }
    };

    private TreeNode.TreeNodeLongClickListener nodeLongClickListener = new TreeNode.TreeNodeLongClickListener() {
        @Override
        public boolean onLongClick(TreeNode node, Object value) {
            IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
            Toast.makeText(getActivity(), "Long click: " + item.text, Toast.LENGTH_SHORT).show();
            return true;
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tState", tView.getSaveState());
    }
}
