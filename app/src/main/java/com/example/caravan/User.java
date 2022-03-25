package com.example.caravan;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caravan.listeners.UserListener;

public class User extends AppCompatActivity implements UserListener {
    private ActivityUsersBinding m_binding;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        m_binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(m_binding.getRoot());

    }
    private void getUsers(){
        //loading(true);
        //FirebaseFirestone database = FirebaseFirestone.getInstance();
        //need to finish writing getUsers - adding database aspects
    }

    private void showErrorMessage() {
        m_binding.textErrorMessage.setText(String.format("&s", "No user available"));
        m_binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading){
        if (isLoading){
            m_binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            m_binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(User user) {

    }
}
