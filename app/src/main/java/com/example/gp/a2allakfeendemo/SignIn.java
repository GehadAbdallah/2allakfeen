package com.example.gp.a2allakfeendemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.example.gp.a2allakfeendemo.WelcomeActivity.controller;

public class SignIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        final EditText user_name = (EditText) findViewById(R.id.userName_text);
        final EditText password = (EditText) findViewById(R.id.password_text);


        final Button signIn_btn = (Button) findViewById(R.id.signIn_btn);
        signIn_btn.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                final String user_name_text = user_name.getText().toString();
                final String password_text = password.getText().toString();
                //validate inputs exist
                if (user_name_text == null)
                    user_name.setError("Field Cannot be left blank");
                if (password_text == null)
                    password.setError("Field Cannot be left blank");
                //pass inputs to controller
                controller.SignIn(user_name_text, password_text,view);

            }
        });


    }
}
