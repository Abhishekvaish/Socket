package com.example.socket;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Region;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Member extends AppCompatActivity {
    Socket socket;
    TextView tvHint;
    String gateway , size;
    int CHOOSEFILE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        tvHint = findViewById(R.id.tvHint);
        new Connect().execute();
    }

    public void chooseFile(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent,CHOOSEFILE);
    }

    public String getFileName(Uri uri) {
        String result = null ;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    size = cursor.getString(cursor.getColumnIndex(OpenableColumns.SIZE));
                }
            }finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(uri));
        if(result.substring(result.length()-extension.length()).equals(extension))
            return result;
        return result+"."+extension;
    }

    public class Share extends AsyncTask<Uri,Integer,Void>
    {
        boolean filesent = false;
        @Override
        protected Void doInBackground(Uri... uris) {
            String filename = getFileName(uris[0]);
            try {
                //FileInputStream fileInputStream = new FileInputStream(getContentResolver().openFileDescriptor(uris[0],"r").getFileDescriptor());
                InputStream inputStream = getContentResolver().openInputStream(uris[0]);
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                dataOutputStream.writeUTF(filename);


                int BUFFER_SIZE = 4096 ,count;
                int SENT_SIZE = 0;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ( (count=inputStream.read(buffer) ) != -1 )
                {
                    dataOutputStream.write(buffer,0,count);
                    SENT_SIZE+=BUFFER_SIZE;
                    publishProgress((int)(SENT_SIZE/(1024.0*1024)));
                }

                filesent=true;

              //  fileInputStream.close();
                inputStream.close();
                dataOutputStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
//            setProgressPercent(values[0]);
            tvHint.setText("Progress "+values[0]+"MB");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(filesent)
                Toast.makeText(Member.this, "File Sent", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(Member.this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CHOOSEFILE)
        {
            if(resultCode == RESULT_OK)
            {
                Uri uri = data.getData();
                Log.d("SHARE",getFileName(uri)+size);
                if(socket != null)
                    new Share().execute(uri);
            }
            else
                Toast.makeText(this, "Please Select a File", Toast.LENGTH_SHORT).show();
        }

    }

    public class Connect extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            int tmp =wifiManager.getDhcpInfo().gateway;
            gateway = String.format("%d.%d.%d.%d", (tmp & 0xff), (tmp >> 8 & 0xff), (tmp >> 16 & 0xff), (tmp >> 24 & 0xff));
            Log.d("SHARE",gateway);
            try {
                socket = new Socket(gateway,5000);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(gateway.equals("0.0.0.0"))
                tvHint.setText("Please Connect to Receiver's Hostspot");
            else
                tvHint.setText("Connected to gateway "+gateway);
        }
    }


}
