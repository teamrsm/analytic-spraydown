package com.sprayme.teamrsm.analyticspraydown;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.sprayme.teamrsm.analyticspraydown.models.Tick;
import com.sprayme.teamrsm.analyticspraydown.uicomponents.EditTextBetterPaste;
import com.sprayme.teamrsm.analyticspraydown.utilities.DataCache;

import java.util.List;
import java.util.UUID;

public class ImportCsvActivity extends AppCompatActivity {

  private DataCache dataCache = null;
  private UUID callbackUUID;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_import_csv);
    dataCache = DataCache.getInstance();
    EditTextBetterPaste et = (EditTextBetterPaste) findViewById(R.id.csv_import_content);
    et.addTextChangedListener(new TextWatcher() {

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        ClipboardManager clip = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        String s1 = clip.getPrimaryClip().toString();
      }

      public void afterTextChanged(Editable s) {

      }
    });
  }


  public void onImport(View view) {
    EditText csv = (EditText) findViewById(R.id.csv_import_content);
    callbackUUID = dataCache.subscribe(new DataCache.DataCacheTicksHandler() {
      @Override
      public void onTicksCached(List<Tick> ticks) {
        if (dataCache.unsubscribeTicksHandler(callbackUUID))
          callbackUUID = null;

        Intent _result = new Intent();
        setResult(Activity.RESULT_OK, _result);
        finish();
      }
    });
    dataCache.importFromMountainProjectCsv(csv.getText().toString());

    // todo: listen to datacache for success / failure
  }
}
