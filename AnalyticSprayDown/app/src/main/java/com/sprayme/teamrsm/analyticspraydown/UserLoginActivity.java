package com.sprayme.teamrsm.analyticspraydown;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.sprayme.teamrsm.analyticspraydown.utilities.DataCache;

public class UserLoginActivity extends AppCompatActivity {

    private DataCache dataCache = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        dataCache = DataCache.getInstance();
    }

    public void onLogin(View view){
        EditText email = (EditText)findViewById(R.id.emailEntry);
        EditText apiKey = (EditText)findViewById(R.id.apiKeyEntry);

        if (dataCache.createNewUser(email, apiKey) != null)
            // kill activity
            ;

    }
}
