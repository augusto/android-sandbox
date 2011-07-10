package com.augusto.mymediaplayer.common;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public final class Formatter {

    private Formatter() {}
    
    
    private static NumberFormat numberFormat = new DecimalFormat("00");
    /**
     * Formats time from milliseconds in the format m:ss
     * 
     * @param timeInMillis time in milliseconds
     * @return
     */
    public static String formatTimeFromMillis(int timeInMillis) {
        int minutes = timeInMillis/60000;
        int seconds = (timeInMillis%60000)/1000;
        
        return minutes + ":" + numberFormat.format(seconds);
    }
}
