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

    private static final char[] SUFFIXES = {'K', 'M', 'G', 'T', 'T', 'E' };

    public static String format(long number) {
        if(number < 1000) {
            // No need to format this
            return String.valueOf(number);
        }
        // Convert to a string
        final String string = String.valueOf(number);
        // The suffix we're using, 1-based
        final int magnitude = (string.length() - 1) / 3;
        // The number of digits we must show before the prefix
        final int digits = (string.length() - 1) % 3 + 1;

        // Build the string
        char[] value = new char[4];
        for(int i = 0; i < digits; i++) {
            value[i] = string.charAt(i);
        }
        int valueLength = digits;
        // Can and should we add a decimal point and an additional number?
        if(digits == 1 && string.charAt(1) != '0') {
            value[valueLength++] = '.';
            value[valueLength++] = string.charAt(1);
        }
        value[valueLength++] = SUFFIXES[magnitude - 1];
        return new String(value, 0, valueLength);
    }

   public static String  MyDateFromat(String duration){


           duration = duration.substring(2);  // del. PT-symbols
           String H, M, S;
           // Get Hours:
           int indOfH = duration.indexOf("H");  // position of H-symbol
           if (indOfH > -1) {  // there is H-symbol
               H = duration.substring(0,indOfH);      // take number for hours
               duration = duration.substring(indOfH); // del. hours
               duration = duration.replace("H","");   // del. H-symbol
           } else {
               H = "";
           }
           // Get Minutes:
           int indOfM = duration.indexOf("M");  // position of M-symbol
           if (indOfM > -1) {  // there is M-symbol
               M = duration.substring(0,indOfM);      // take number for minutes
               duration = duration.substring(indOfM); // del. minutes
               duration = duration.replace("M","");   // del. M-symbol
               // If there was H-symbol and less than 10 minutes
               // then add left "0" to the minutes
               if (H.length() > 0 && M.length() == 1) {
                   M = "0" + M;
               }
           } else {
               // If there was H-symbol then set "00" for the minutes
               // otherwise set "0"
               if (H.length() > 0) {
                   M = "00";
               } else {
                   M = "0";
               }
           }
           // Get Seconds:
           int indOfS = duration.indexOf("S");  // position of S-symbol
           if (indOfS > -1) {  // there is S-symbol
               S = duration.substring(0,indOfS);      // take number for seconds
               duration = duration.substring(indOfS); // del. seconds
               duration = duration.replace("S","");   // del. S-symbol
               if (S.length() == 1) {
                   S = "0" + S;
               }
           } else {
               S = "00";
           }
           if (H.length() > 0) {
               return H + ":" +  M + ":" + S;
           } else {
               return M + ":" + S;
           }

       }



}