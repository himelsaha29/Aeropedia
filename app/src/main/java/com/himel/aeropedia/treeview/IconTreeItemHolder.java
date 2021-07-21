package com.himel.aeropedia.treeview;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.johnkil.print.PrintView;
import com.himel.aeropedia.R;
import com.himel.aeropedia.airbus.AirbusA350;
import com.himel.aeropedia.flightmap.FlightMap;
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
            tvValue.setTextColor(Color.parseColor("#000000"));
        }


        final PrintView iconView = (PrintView) view.findViewById(R.id.icon);




        iconView.setIconFont(Typeface.createFromAsset(context.getAssets(), "fonts/trial.ttf"));
        iconView.setIconText(context.getResources().getString(value.icon));


        arrowView = (PrintView) view.findViewById(R.id.arrow_icon);

        node.setClickListener(new TreeNode.TreeNodeClickListener() {
            @Override
            public void onClick(TreeNode node, Object valuee) {
                if(value.classToOpen != null) {
                    Intent showContent = new Intent(context, value.classToOpen);
                    context.startActivity(showContent);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });
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
        public Class classToOpen;

        public IconTreeItem(int icon, String text, String highlight, String iconXML, Class classToOpen) {
            this.icon = icon;
            this.text = text;
            this.highlight = highlight;
            this.iconXML = iconXML;
            this.classToOpen = classToOpen;
        }

    }
}

