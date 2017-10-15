package com.sprayme.teamrsm.analyticspraydown;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sprayme.teamrsm.analyticspraydown.models.Tick;
import com.sprayme.teamrsm.analyticspraydown.models.User;
import com.sprayme.teamrsm.analyticspraydown.utilities.DataCache;

import java.util.List;
import java.util.UUID;

public class UserLoginActivity extends AppCompatActivity {

    private DataCache dataCache = null;
    private UUID callbackUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        dataCache = DataCache.getInstance();
        // Get the Intent that started this activity and extract the string
//        Intent intent = getIntent();
//        String message = intent.getBundleExtra("").getStringExtra(MainActivity.EXTRA_MESSAGE);
//
//        // Capture the layout's TextView and set the string as its text
//        TextView textView = (TextView) findViewById(R.id.textView);
//        textView.setText(message);
    }

    public void onLogin(View view){
        EditText email = (EditText)findViewById(R.id.emailEntry);
        EditText apiKey = (EditText)findViewById(R.id.apiKeyEntry);
        callbackUUID = dataCache.subscribe(new DataCache.DataCacheUserHandler() {
            @Override
            public void onUserCached(User user) {
                if (dataCache.unsubscribeUserHandler(callbackUUID))
                    callbackUUID = null;

                Intent _result = new Intent();
                setResult(Activity.RESULT_OK, _result);
                finish();
            }
        });
        dataCache.createNewUser(email.getText().toString(), apiKey.getText().toString());

        // todo: listen to datacache for success / failure
//        if (dataCache.createNewUser(email.getText().toString(), apiKey.getText().toString()) != null)
//            // kill activity
//            ;
//        Intent _result = new Intent();
////        _result.putExtra("USER",
//        setResult(Activity.RESULT_OK, _result);
//        finish();

    }
}
