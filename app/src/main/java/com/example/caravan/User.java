package com.example.caravan;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caravan.listeners.UserListener;

import java.io.Serializable;

public class User implements Serializable {
    public String name, email, userID;
}
