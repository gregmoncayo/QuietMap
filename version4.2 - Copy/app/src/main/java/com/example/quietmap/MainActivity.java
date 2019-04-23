package com.example.quietmap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.quietmap.LoginActivity;
import com.example.quietmap.R;






public class MainActivity extends AppCompatActivity {

    Button registerBtn, loginBtn, registerbusinessBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        registerbusinessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistrationBusiness.class);
                startActivity(intent);
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }



    private void initializeViews() {
        registerbusinessBtn = findViewById(R.id.registerbusiness);
        registerBtn = findViewById(R.id.register);
        loginBtn = findViewById(R.id.login);
    }
}
