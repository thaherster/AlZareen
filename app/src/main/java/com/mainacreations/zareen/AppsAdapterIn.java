package com.mainacreations.zareen;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

/**
 * Created by Thaher on 10-10-2017.
 */
public class AppsAdapterIn extends RecyclerView.Adapter<AppsAdapterIn.MyViewHolder> {

    private List<Video_Info> videos;
    MainActivity mainActivity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, channel , time;
        public String code;
        public ImageView thumb;
        public String thumburl;
        public LinearLayout ll1;
        Video_Info video_info;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            channel = (TextView) view.findViewById(R.id.channel);
//            views = (TextView) view.findViewById(R.id.views);
            time = (TextView) view.findViewById(R.id.time);
            thumb = (ImageView) view.findViewById(R.id.thumb);

            ll1 = (LinearLayout) view.findViewById(R.id.ll1);
            video_info = new Video_Info();

        }
    }


    public AppsAdapterIn(List<Video_Info> appsList, MainActivity mainActivity) {
        this.videos = appsList;
        this.mainActivity = mainActivity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.apps_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Video_Info video_info = videos.get(position);
        holder.video_info = video_info;
        holder.title.setText(video_info.getVideoName());
        holder.channel.setText(video_info.getChannelName());
//        holder.views.setText(video_info.getViews());
        holder.time.setText(video_info.getTime());
        Log.i("GLOIDE","  url : "+holder.thumburl);

        Glide .with(mainActivity)
                .load(video_info.getThumbNailUrl())
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.ic_launcher))
                .into(holder.thumb);

        holder.ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              mainActivity.startVideo(holder.video_info);


            }
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }
}