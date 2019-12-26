package org.epcdiy.bilibiliboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.epcdiy.bilibiliboard.BilibiliBoard;
import org.epcdiy.bilibiliboard.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AvListViewAdapter extends BaseAdapter {
    private List<AvContent> avContents;
    private LayoutInflater inflater;
    public AvListViewAdapter(List<AvContent> avContents, Context context) {
        this.avContents = avContents;
        this.inflater = LayoutInflater.from(context);

    }


    @Override
    public int getCount() { return avContents.size(); }

    @Override
    public Object getItem(int position) { return avContents.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_item, null);
            holder.avname = convertView.findViewById(R.id.avname);
            holder.play = convertView.findViewById(R.id.play);
            holder.playup = convertView.findViewById(R.id.playup);
            holder.damu = convertView.findViewById(R.id.damu);
            holder.damuup = convertView.findViewById(R.id.damuup);
            holder.comment = convertView.findViewById(R.id.comment);
            holder.commentup = convertView.findViewById(R.id.commentup);
            holder.fav = convertView.findViewById(R.id.fav);
            holder.favup = convertView.findViewById(R.id.favup);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        AvContent avContent = avContents.get(position);
        holder.avname.setText(avContent.title);
        holder.play.setText(dealWithNumber(avContent.play));
        holder.playup.setText(dealWithNumber(avContent.playup));
        holder.damu.setText(dealWithNumber(avContent.video_review));
        holder.damuup.setText(dealWithNumber(avContent.video_reviewup));
        holder.comment.setText(dealWithNumber(avContent.comment));
        holder.commentup.setText(dealWithNumber(avContent.commentup));
        holder.fav.setText(dealWithNumber(avContent.favorites));
        holder.favup.setText(dealWithNumber(avContent.favoritesup));
        return convertView;
    }

    String dealWithNumber(String inNum)
    {
        try {
            int number = Integer.parseInt(inNum);
            if (number >= 10000)
            {
                return String.format("%.2f", (float)number/10000.0f)+"w";
            }
        }
        catch (Exception e)
        {
        }
        return  inNum;
    }

    public class ViewHolder {
        public TextView avname;
        public TextView play;
        public TextView playup;
        public TextView damu;
        public TextView damuup;
        public TextView comment;
        public TextView commentup;
        public TextView fav;
        public TextView favup;
    }
}

