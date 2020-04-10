package com.example.socket;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.List;

public class Send extends AppCompatActivity {
    TextView tvText;
    String gateway;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        tvText = findViewById(R.id.tvText);

        final PackageManager pm = getPackageManager();
//get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            Log.d("MY", "Installed package :" + packageInfo.packageName);
   //         Log.d("MY", "Source dir : " + packageInfo.sourceDir);
    //        Log.d("MY", "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
        }

            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            if (wifiManager == null)
                Toast.makeText(this, "No wifi connected ", Toast.LENGTH_SHORT).show();
            else{
                int tmp =wifiManager.getDhcpInfo().gateway;
                gateway = String.format("%d.%d.%d.%d", (tmp & 0xff), (tmp >> 8 & 0xff), (tmp >> 16 & 0xff), (tmp >> 24 & 0xff));
                tvText.setText("Connected to wifi gateway "+gateway);
            }
    }



    public class doInBackground extends AsyncTask<Void,Void,Void>{
        Socket socket=null;
        Uri uri;
        String ipad;

        public doInBackground(String ipad , Uri uri){
            this.ipad = ipad;
            this.uri = uri;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                socket = new Socket(ipad,5000);
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

                FileInputStream inputFile = new FileInputStream(Send.this.getContentResolver().openFileDescriptor(uri, "r").getFileDescriptor());
                outputStream.writeUTF(uri.getLastPathSegment());
                File file = new File(uri.getPath());
                Log.d("ABHISHEK",file.getName());


                int BUFFER_SIZE = 4096 ,count;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ( (count=inputFile.read(buffer)) != -1 )
                    outputStream.write(buffer,0,count);

                inputFile.close();
                outputStream.close();
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(Send.this, "File Sent", Toast.LENGTH_SHORT).show();
        }
    }

    public void selectFile(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent,2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2){
            if(resultCode == RESULT_OK){
                Uri uri = data.getData();
                new doInBackground(gateway,uri).execute();
            }
            else {
                Toast.makeText(this, "Please Select a File", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
