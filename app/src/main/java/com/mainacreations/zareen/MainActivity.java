package com.mainacreations.zareen;

import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity  {

    private static final int RECOVERY_REQUEST = 1;
    private VideoView youTubeView;
    private AdView mAdView;

    Video_Info video_info;
    private ArrayList<Video_Info> videos;
    private RecyclerView recyclerView;
    private AppsAdapterIn mAdapter;
    FirebaseAuth mAuth;
    DatabaseReference myRef;
    String playing = "";
    private MediaController mediacontroller;

    private static final int TOTAL_ITMES_TO_LOAD=5;
    private int mCurrentPage =1;
    boolean mIsLoading = false;
    String lastkey="";
    Boolean endoflist=false;
    LinearLayoutManager mLayoutManager;
    TextView title;

    int pos = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        youTubeView = (VideoView) findViewById(R.id.youtube_view);
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        video_info = (Video_Info) getIntent().getSerializableExtra("Video_Info");
         title = (TextView)findViewById(R.id.titlexxx);
       title.setText(video_info.getVideoName());
        videos =  new ArrayList<>();
        videos.clear();

        mAuth = FirebaseAuth.getInstance();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("apkrecords");
        myRef.keepSynced(true);
        mAdapter = new AppsAdapterIn(videos,this);
         mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        videos.clear();

        pos = 0;
        getdata();



        try {

            // Start the MediaController
            mediacontroller = new MediaController(this);
            mediacontroller.setAnchorView(youTubeView);

            // Get the URL from String VideoURL
            Uri video = Uri.parse(video_info.getCode());
            youTubeView.setMediaController(mediacontroller);
            youTubeView.setVideoURI(video);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();

        }

        youTubeView.requestFocus();
        youTubeView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {

                youTubeView.start();
            }
        });
        youTubeView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {

                return false;
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (mIsLoading)
                    return;
                int visibleItemCount = mLayoutManager.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();
                int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    //load more
                    mCurrentPage++;
                    mIsLoading =true;
                    pos =0;
                    if(!endoflist)
                    {getmoredata();
                    }
                    else {
                        Toast.makeText(MainActivity.this,"End of List",Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

    }



    public void getmoredata(){
        Query query  = myRef.orderByKey().endAt(lastkey).limitToLast(TOTAL_ITMES_TO_LOAD);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i("ADDED"," "+lastkey);
                Log.i("ADDED"," "+dataSnapshot.getKey());
                if(lastkey.equals(dataSnapshot.getKey()))
                {
                    endoflist=true;
                    Toast.makeText(MainActivity.this,"End of List",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    endoflist=false;
                    if(pos==0)
                    {
                        lastkey=dataSnapshot.getKey();
                    }
                    Log.i("ADDED"," "+s);
                    Log.d("ADDED",dataSnapshot.getRef().toString());
                    Log.d("ADDED",dataSnapshot.toString());
                    HashMap newmap = (HashMap) dataSnapshot.getValue();
                    Log.d("ADDED",newmap.toString());
                    Video_Info app =  new Video_Info();
                    app.setVideoName((String) newmap.get("VideoName"));
                    app.setCode((String) newmap.get("Code"));
                    app.setChannelName((String) newmap.get("ChannelName"));
                    String time = Utils.MyDateFromat((String) newmap.get("Time"));
                    app.setTime(time);
                    String str = (String)newmap.get("Views").toString().trim();
                    String viw = Utils.format(Long.valueOf(str));
                    Log.i("ADDED"," : "+viw);
                    app.setViews(viw+" views");
                    app.setThumbNailUrl((String) newmap.get("ThumbNailUrl"));
                    Log.i("ADDED"," url : "+app.getThumbNailUrl());

                    videos.add(app);
                    mAdapter.notifyDataSetChanged();
                    mIsLoading =false;
                }

                pos++;

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void   getdata() {
        Log.d("APKURL","getdata");

        Query query  = myRef.limitToLast(mCurrentPage*TOTAL_ITMES_TO_LOAD);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i("ADDED"," "+lastkey);
                Log.i("ADDED"," "+dataSnapshot.getKey());
                if(lastkey.equals(dataSnapshot.getKey()))
                {
                    endoflist=true;
                    Toast.makeText(MainActivity.this,"End of List",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    endoflist=false;
                    if(pos==0)
                    {
                        lastkey=dataSnapshot.getKey();
                    }
                    Log.i("ADDED"," "+s);
                    Log.d("ADDED",dataSnapshot.getRef().toString());
                    Log.d("ADDED",dataSnapshot.toString());
                    HashMap newmap = (HashMap) dataSnapshot.getValue();
                    Log.d("ADDED",newmap.toString());
                    Video_Info app =  new Video_Info();
                    app.setVideoName((String) newmap.get("VideoName"));
                    app.setCode((String) newmap.get("Code"));
                    app.setChannelName((String) newmap.get("ChannelName"));
                    String time = Utils.MyDateFromat((String) newmap.get("Time"));
                    app.setTime(time);
                    String str = (String)newmap.get("Views").toString().trim();
                    String viw = Utils.format(Long.valueOf(str));
                    Log.i("ADDED"," : "+viw);

                    app.setViews(viw+" views");
                    app.setThumbNailUrl((String) newmap.get("ThumbNailUrl"));
                    Log.i("ADDED"," url : "+app.getThumbNailUrl());

                    videos.add(app);
                    mAdapter.notifyDataSetChanged();
                    mIsLoading =false;
                }

                pos++;

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }








    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void startVideo(Video_Info video_info) {
        if( youTubeView != null ) {
            if(!playing.trim().equals(video_info.getCode().trim()))
            {

                try {

                    // Start the MediaController

                    // Get the URL from String VideoURL
                    Uri video = Uri.parse(video_info.getCode());
                    youTubeView.setMediaController(mediacontroller);
                    youTubeView.setVideoURI(video);

                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();

                }

                youTubeView.requestFocus();
                youTubeView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    // Close the progress bar and play the video
                    public void onPrepared(MediaPlayer mp) {

                        youTubeView.start();
                    }
                });
                youTubeView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {

                        return false;
                    }
                });

            }
            playing= video_info.getCode().trim();
            title.setText(video_info.getVideoName());

        }
    }
}
