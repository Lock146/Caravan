package com.example.caravan;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.Caravan.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view){
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void connect(View view){
        Log.d("Networking", "Transmit called");
        Intent intent = new Intent(this, Networking.class);

        EditText serverIPText = (EditText) findViewById(R.id.serverIPTextEdit);
        String serverIP = serverIPText.getText().toString();

        EditText serverPortText = (EditText) findViewById(R.id.serverPortTextEdit);
        String serverPort = serverPortText.getText().toString();
        intent.putExtra("serverIP", serverIP);
        intent.putExtra("serverPort", serverPort);
        startActivity(intent);
    }
}