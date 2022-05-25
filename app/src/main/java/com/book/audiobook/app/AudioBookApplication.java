package com.book.audiobook.app;

import android.app.Application;
import io.paperdb.Paper;

/**
 * This is application class which initialize all the required modules, libraries,etc
 */
public class AudioBookApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Paper.init(getApplicationContext());


    }
}
