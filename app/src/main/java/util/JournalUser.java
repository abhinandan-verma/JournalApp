package util;

import android.app.Application;

public class JournalUser extends Application {
    private static JournalUser instance;
    private  String username;
    private  String userId;

    //Following the Singleton Design Pattern

    public JournalUser(){
        //Empty Constructor
    }

    public static JournalUser getInstance(){
        if (instance == null){
            instance = new JournalUser();
        }
        return instance;
    }

    public  String getUsername(){
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
