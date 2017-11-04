package com.sprayme.teamrsm.analyticspraydown;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.sprayme.teamrsm.analyticspraydown.models.MPProfileDrawerItem;
import com.sprayme.teamrsm.analyticspraydown.models.User;
import com.sprayme.teamrsm.analyticspraydown.uicomponents.SpinnerFragment;
import com.sprayme.teamrsm.analyticspraydown.utilities.DataCache;

import java.util.UUID;

public class UserLoginActivity extends AppCompatActivity {

  private DataCache dataCache = null;
  private UUID callbackUUID;
  private Fragment mSpinnerFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_login);
    dataCache = DataCache.getInstance();

    mSpinnerFragment = new SpinnerFragment();
    // Get the Intent that started this activity and extract the string
//        Intent intent = getIntent();
//        String message = intent.getBundleExtra("").getStringExtra(MainActivity.EXTRA_MESSAGE);
//
//        // Capture the layout's TextView and set the string as its text
//        TextView textView = (TextView) findViewById(R.id.textView);
//        textView.setText(message);
  }

  public void onLogin(View view) {
    EditText email = (EditText) findViewById(R.id.emailEntry);
    EditText apiKey = (EditText) findViewById(R.id.apiKeyEntry);
    callbackUUID = dataCache.subscribe(new DataCache.DataCacheProfileHandler() {
      @Override
      public void onProfileCached(MPProfileDrawerItem profile) {
        if (dataCache.unsubscribeProfileHandler(callbackUUID))
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

  private void showProgress(){
    getFragmentManager().beginTransaction().add(R.id.login_layout, mSpinnerFragment).commit();
  }

  private void hideProgress(){
    getFragmentManager().beginTransaction().remove(mSpinnerFragment).commit();
  }
}
