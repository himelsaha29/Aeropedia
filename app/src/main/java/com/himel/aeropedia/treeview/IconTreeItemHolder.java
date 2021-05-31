package com.himel.aeropedia.treeview;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.johnkil.print.PrintView;
import com.himel.aeropedia.R;
import com.unnamed.b.atv.model.TreeNode;

public class IconTreeItemHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {
    private TextView tvValue;
    private PrintView arrowView;

    public IconTreeItemHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_icon_node, null, false);
        tvValue = (TextView) view.findViewById(R.id.node_value);
        tvValue.setText(value.text);

        if(value.highlight.equals("HighlightLight")) {
            tvValue.setTextColor(Color.parseColor("#72A8E1"));
        } else if(value.highlight.equals("HighlightDark")) {
            tvValue.setTextColor(Color.parseColor("#042234"));
        }


        final PrintView iconView = (PrintView) view.findViewById(R.id.icon);


        if(value.iconXML.equals("Alexa")) {
            iconView.setIconFont(Typeface.createFromAsset(context.getAssets(), "fonts/icomoon.ttf"));
            iconView.setIconText(context.getResources().getString(value.icon));
        } else {
            iconView.setIconFont(Typeface.createFromAsset(context.getAssets(), "fonts/material-icon-font.ttf"));

        }

        arrowView = (PrintView) view.findViewById(R.id.arrow_icon);

        return view;
    }


    @Override
    public void toggle(boolean active) {
        arrowView.setIconText(context.getResources().getString(active ? R.string.ic_keyboard_arrow_down : R.string.ic_keyboard_arrow_right));
    }

    public static class IconTreeItem {
        public int icon;
        public String text;
        public String highlight;
        public String iconXML;

        public IconTreeItem(int icon, String text, String highlight, String iconXML) {
            this.icon = icon;
            this.text = text;
            this.highlight = highlight;
            this.iconXML = iconXML;
        }

    }
}

