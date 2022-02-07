package com.example.caravan;

import android.os.Bundle;
import android.util.Log;

import android.net.*;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Networking extends AppCompatActivity {
    private Socket socket;
    private InetAddress m_serverIP;
    private Integer m_serverPort;

    public Networking(){}

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.networking_layout);
        Bundle extras = getIntent().getExtras();
        try {
            String serverIP = extras.getString("serverIP");
            m_serverPort = Integer.valueOf(extras.getString("serverPort"));
            m_serverIP = InetAddress.getByName(serverIP);
            socket = new Socket(m_serverIP, m_serverPort);
            OutputStream output = socket.getOutputStream();
            String message = "hello\n";
            output.write(message.getBytes());
        }
        catch(UnknownHostException e){
            Log.d("Networking", "Unknown host");
        }
        catch(IOException e){
            Log.d("Networking", "IOException");
        }
    }

    @Override
    protected void onDestroy(){
        try {
            socket.close();
        }
        catch(IOException e){
            Log.d("Networking", e.toString());
        }

        super.onDestroy();
    }
    public Networking(String message){


    }
}
