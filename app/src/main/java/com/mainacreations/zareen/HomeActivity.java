package com.mainacreations.zareen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {
    private ArrayList<Video_Info> videos;
    private RecyclerView recyclerView;
    private AppsAdapter mAdapter;
    FirebaseAuth mAuth;
    DatabaseReference myRef;
    private AdView mAdView;

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
                        Toast.makeText(HomeActivity.this, "signInAnonymously:FAILURE" + exception,Toast.LENGTH_LONG).show();
                        Log.d("APKURL","signInAnonymously:FAILURE" + exception);
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        videos =  new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);




        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("apkrecords");
        myRef.keepSynced(true);
        mAdapter = new AppsAdapter(videos,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        videos.clear();


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
                    Log.i("GLOIDE"," url : "+app.getThumbNailUrl());

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

    public void startVideo(Video_Info video_info) {
        Intent i = new Intent(HomeActivity.this, MainActivity.class);
        i.putExtra("Video_Info",video_info);
        startActivity(i);
    }
}


