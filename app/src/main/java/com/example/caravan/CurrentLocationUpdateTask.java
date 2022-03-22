package com.example.caravan;

import java.util.TimerTask;

import com.example.caravan.Database;

public class CurrentLocationUpdateTask extends TimerTask {
    private Database m_database;
    public CurrentLocationUpdateTask(){
        m_database = Database.get_instance();
    }
    @Override
    public void run() {
        m_database.update_location();
    }
}
