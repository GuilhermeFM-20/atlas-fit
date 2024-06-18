package com.example.app_sd;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_sd.ui.login.LoginFragment;
import com.example.app_sd.ui.login.RegisterFragment;

public class RegisterActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RegisterFragment())
                    .commit();
        }
    }
}
