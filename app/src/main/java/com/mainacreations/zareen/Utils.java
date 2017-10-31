package com.mainacreations.zareen;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Thaher on 31-10-2017.
 */

public class Utils {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

}