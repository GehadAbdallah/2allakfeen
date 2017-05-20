package com.example.gp.a2allakfeendemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.example.gp.a2allakfeendemo.WelcomeActivity.controller;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        final EditText user_name = (EditText) findViewById(R.id.userName_text2);
        final EditText password = (EditText) findViewById(R.id.password_text2);
        final EditText email = (EditText) findViewById(R.id.email_text);

        final String user_name_text = user_name.getText().toString();
        final String password_text = password.getText().toString();
        final String email_text = email.getText().toString();

        Button signUp_btn = (Button) findViewById(R.id.signUp_btn2);
        signUp_btn.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                if (user_name_text == null)
                    user_name.setError("Field Cannot be left blank");
                if (password_text == null)
                    password.setError("Field Cannot be left blank");
                if (email_text == null)
                    email.setError("Field Cannot be left blank");

                controller.SignUp(user_name_text, email_text, password_text,view,getApplicationContext());

//                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
//                startActivity(intent);
            }
        });
    }
}
