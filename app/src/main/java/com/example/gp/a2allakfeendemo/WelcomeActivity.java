package com.example.gp.a2allakfeendemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeActivity extends AppCompatActivity {

    static public Controller controller = new Controller();
    //To Store the current user id when logged in
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //MYPREFRENCES is the name of the file we store the current user logged in id into
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Log.e("WelcomeActivity","oncreate");
        //if CurrentUser exists then redirect to maps activity
        Log.e("sharedPrefrences","shared: "+Integer.toString(sharedpreferences.getInt("CurrentUser",-3)));

        if (sharedpreferences.contains("CurrentUser")) {

            Log.e("sharedPrefrences","shared: "+Integer.toString(sharedpreferences.getInt("CurrentUser",-3)));
            startActivity(new Intent(WelcomeActivity.this, MapsActivity.class));
        }
        else {
            Log.e("shredzeft","not in shared");
            //else if sign_in or sign_up bottons is pressed go to thier corresponding activities
            Button sign_in = (Button) findViewById(R.id.SignInButton);
            TextView sign_up = (TextView) findViewById(R.id.SignUpButton);
            final EditText user_name = (EditText) findViewById(R.id.userName_text);
            final EditText password = (EditText) findViewById(R.id.password_text);

            sign_in.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String user_name_text = user_name.getText().toString();
                    String password_text = password.getText().toString();
                    //validate inputs exist
                    if (user_name_text.equals(null) || password_text.equals(null) || user_name_text.isEmpty() || password_text.isEmpty()) {
                        Toast toast = Toast.makeText(v.getContext(), "These fields cannot be left blank", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }

                    //pass inputs to controller
                    if ((!user_name_text.equals(null) && !user_name_text.isEmpty()) && (!password_text.equals(null) && !password_text.isEmpty())) {
                        Log.e("welcome","should sign in");
                        controller.SignIn(user_name_text, password_text, v);
                    }
                }
            });
            sign_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(WelcomeActivity.this, SignUp.class));
                }
            });
        }
    }
}
