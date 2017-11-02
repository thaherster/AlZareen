package com.mainacreations.zareen;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
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

public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener  {

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private AdView mAdView;

    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener playbackEventListener;
    Video_Info video_info;
    private ArrayList<Video_Info> videos;
    private RecyclerView recyclerView;
    private AppsAdapterIn mAdapter;
    FirebaseAuth mAuth;
    DatabaseReference myRef;
    String playing = "";
    YouTubePlayer mPlayer;
    private static final int TOTAL_ITMES_TO_LOAD=5;
    private int mCurrentPage =1;
    boolean mIsLoading = false;
    String lastkey="";
    Boolean endoflist=false;
    LinearLayoutManager mLayoutManager;

    int pos = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(Config.YOUTUBE_API_KEY, this);
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        video_info = (Video_Info) getIntent().getSerializableExtra("Video_Info");
        playerStateChangeListener = new MyPlayerStateChangeListener();
        playbackEventListener = new MyPlaybackEventListener();
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
        pos = 0;
        getdata();

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
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(playbackEventListener);
            if (!wasRestored) {
                if (!video_info.getCode().isEmpty()) {
                    mPlayer = youTubePlayer;
                    mPlayer.cueVideo(video_info.getCode().trim());
                    // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo}
                    playing =video_info.getCode().trim();
                }
            }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format("error : ", youTubeInitializationResult.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubeView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void startVideo(Video_Info video_info) {
        if( mPlayer != null ) {
            if(!playing.trim().equals(video_info.getCode().trim()))
            {mPlayer.cueVideo(video_info.getCode());}
            playing= video_info.getCode().trim();
        }
    }
}
