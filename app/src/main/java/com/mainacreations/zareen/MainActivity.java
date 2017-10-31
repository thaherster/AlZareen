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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
        mAuth = FirebaseAuth.getInstance();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("apkrecords");
        myRef.keepSynced(true);
        mAdapter = new AppsAdapterIn(videos,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        videos.clear();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                getdata();                }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(MainActivity.this, "signInAnonymously:FAILURE" + exception,Toast.LENGTH_LONG).show();
                        Log.d("APKURL","signInAnonymously:FAILURE" + exception);
                    }
                });
    }

    public void   getdata() {
        Log.d("APKURL","getdata");


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("APKURL","onComplete");
                videos.clear();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Log.d("APKURL",postSnapshot.getRef().toString());
                    Log.d("APKURL",postSnapshot.toString());
                    HashMap newmap = (HashMap) postSnapshot.getValue();
                    Log.d("APKURL",newmap.toString());
                    Video_Info app =  new Video_Info();
                    app.setVideoName((String) newmap.get("VideoName"));
                    app.setCode((String) newmap.get("Code"));
                    app.setChannelName((String) newmap.get("ChannelName"));
                    app.setTime((String) newmap.get("Time"));
                    app.setViews((String) newmap.get("Views"));
                    app.setThumbNailUrl((String) newmap.get("ThumbNailUrl"));

                    videos.add(app);
                }
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("APKURL","Cancelled");
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
