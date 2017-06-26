package com.example.gp.a2allakfeendemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {

    static public Controller controller = new Controller();
    //To Store the current user id when logged in
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //MYPREFRENCES is the name of the file we store the current user logged in id into
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        //if CurrentUser exists then redirect to maps activity
        if (sharedpreferences.contains("CurrentUser"))
            startActivity(new Intent(WelcomeActivity.this,MapsActivity.class));

        //else if sign_in or sign_up bottons is pressed go to thier corresponding activities
        Button sign_in= (Button) findViewById(R.id.SignInButton);
        Button sign_up= (Button) findViewById(R.id.SignUpButton);


        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this,SignIn.class));
            }
        });
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this,SignUp.class));
            }
        });
    }
}
