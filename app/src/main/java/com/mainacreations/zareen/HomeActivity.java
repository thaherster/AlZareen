package com.mainacreations.zareen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {
    private ArrayList<Video_Info> videos;
    private RecyclerView recyclerView;
    private AppsAdapter mAdapter;
    FirebaseAuth mAuth;
    DatabaseReference myRef;
    private AdView mAdView;
    private static final int TOTAL_ITMES_TO_LOAD=5;
    private int mCurrentPage =1;
    LinearLayoutManager mLayoutManager;
    boolean mIsLoading = false;
    String lastkey="";
    Boolean endoflist=false;
    int pos = 0;




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
        videos.clear();



        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("apkrecords");
        myRef.keepSynced(true);
        mAdapter = new AppsAdapter(videos,this);
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
                        Toast.makeText(HomeActivity.this,"End of List",Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(HomeActivity.this,"End of List",Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(HomeActivity.this,"End of List",Toast.LENGTH_SHORT).show();

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

    public void startVideo(Video_Info video_info) {
        Intent i = new Intent(HomeActivity.this, MainActivity.class);
        i.putExtra("Video_Info",video_info);
        startActivity(i);
    }
}


