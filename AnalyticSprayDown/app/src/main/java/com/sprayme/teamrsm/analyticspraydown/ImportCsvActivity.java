package com.sprayme.teamrsm.analyticspraydown;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.sprayme.teamrsm.analyticspraydown.models.Tick;
import com.sprayme.teamrsm.analyticspraydown.models.User;
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
    }


    public void onImport(View view){
        EditText csv = (EditText)findViewById(R.id.csv_import_content);
        callbackUUID = dataCache.subscribe(new DataCache.DataCacheTicksHandler() {
            @Override
            public void onTicksCached(List<Tick> ticks) {
                if (dataCache.unsubscribeUserHandler(callbackUUID))
                    callbackUUID = null;

                Intent _result = new Intent();
                setResult(Activity.RESULT_OK, _result);
                finish();
            }
        });
        dataCache.importFromCsv(csv.getText().toString());

        // todo: listen to datacache for success / failure
    }
}
