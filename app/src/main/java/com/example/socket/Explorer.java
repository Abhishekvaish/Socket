package com.example.socket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Explorer extends AppCompatActivity implements RowApps.Itemselected{
    RecyclerView recyclerView;
    List<ResolveInfo> apps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);


        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(Explorer.this,4));
        new loadapps().execute();
    }
    public class loadapps extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            apps = getPackageManager().queryIntentActivities(mainIntent, 0);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            recyclerView.setAdapter(new RowApps(apps,Explorer.this));
//            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onIntemSelected(int i) {
        Intent intent = new Intent();
        File file = new File(apps.get(i).activityInfo.applicationInfo.sourceDir);
        intent.putExtra("name",apps.get(i).loadLabel(getPackageManager()).toString()+".apk");
        intent.putExtra("size",(int)file.length()/(1024*1024)+"");
        intent.setData(Uri.fromFile(file));
        setResult(RESULT_OK,intent);
        finish();
    }
}
