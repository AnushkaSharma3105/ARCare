package com.example.arcare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PreviousImageAdapter extends BaseAdapter {
    private Context context;
    private List<PreviousImage> images;

    public PreviousImageAdapter(Context context, List<PreviousImage> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView timestampView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        PreviousImage img = images.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_previous_image, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.previousImageView);
            holder.timestampView = convertView.findViewById(R.id.imageTimestamp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imageView.setImageBitmap(img.getImage());
        holder.timestampView.setText(img.getTimestamp());

        return convertView;
    }
}
