package com.example.socket;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Send extends AppCompatActivity {
    TextView tvHint;
    RecyclerView recyclerView;
    ArrayList<RowFiles> listFiles;
    int CHOOSEFILE = 10 , CURRENT_FILE=0;
    String gateway;
    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_member);
        tvHint = findViewById(R.id.tvHint);
        recyclerView = findViewById(R.id.recyclerview);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        int tmp =wifiManager.getDhcpInfo().gateway;
        gateway = String.format("%d.%d.%d.%d", (tmp & 0xff), (tmp >> 8 & 0xff), (tmp >> 16 & 0xff), (tmp >> 24 & 0xff));
        if (!gateway.equals("0.0.0.0"))
            tvHint.setText("Connected to wifi gateway "+gateway);
        else
            tvHint.setText("Please Connect to Receiver's Hostspot");
        listFiles = new ArrayList<>(0);
        recyclerView.setAdapter(new MyAdapter(listFiles));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

    }



    public void chooseFile(View view) {
        if(!gateway.equals("0.0.0.0"))
        {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent,CHOOSEFILE);
        }
        else
            Toast.makeText(this, "Please Connect to Receiver's Hotspot ", Toast.LENGTH_SHORT).show();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CHOOSEFILE)
        {
            if(resultCode == RESULT_OK)
            {
                Uri uri = data.getData();
                listFiles.add(getRowFileFromUri(uri));
                recyclerView.getAdapter().notifyDataSetChanged();
                if(CURRENT_FILE==listFiles.size()-1)
                    new SendFile().execute();
            }
            else
                Toast.makeText(this, "Please Select a File", Toast.LENGTH_SHORT).show();
        }
    }

    public RowFiles getRowFileFromUri(Uri uri)
    {
        String name=null,size=null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    size = cursor.getString(cursor.getColumnIndex(OpenableColumns.SIZE));
                }
            }finally {
                cursor.close();
            }
        }
        if (name == null) {
            name = uri.getPath();
            int cut = name.lastIndexOf('/');
            if (cut != -1) {
                name = name.substring(cut + 1);
            }
        }
        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(uri));
        if(!name.substring(name.length()-extension.length()).equals(extension))
            name = name+"."+extension;

        size = (int)(Float.parseFloat(size)/(1024.0*1024))+"";
        return new RowFiles(name,size,"0",uri);
    }

    public class SendFile extends AsyncTask<Void,String,Void>
    {
        Boolean FILE_SENT = false;
        RowFiles fileObj = listFiles.get(CURRENT_FILE);
        @Override
        protected Void doInBackground(Void... aVoid) {
            Log.d("SHARE",gateway);
            try {
                socket = new Socket(gateway,5000);
                publishProgress(new String[]{"tvHint","Connected"});

                InputStream inputStream = getContentResolver().openInputStream(fileObj.getUri());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeUTF(fileObj.getName());
                dataOutputStream.writeUTF(fileObj.getSize());

                int BUFFER_SIZE = 4096 ,count;
                int SENT_SIZE = 0;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ( (count=inputStream.read(buffer) ) != -1 )
                {
                    dataOutputStream.write(buffer,0,count);
                    SENT_SIZE+=count;
                    publishProgress(""+(int)(SENT_SIZE/(1024.0*1024)));
                }
                inputStream.close();
                dataOutputStream.close();
                socket.close();
                FILE_SENT = true;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if(values[0].equals("tvHint"))
                tvHint.setText(values[1]);
            else
            {
                fileObj.setMidsize(values[0]);
                recyclerView.getAdapter().notifyDataSetChanged();
            }

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(FILE_SENT)
            {
                Toast.makeText(Send.this, "File Sent", Toast.LENGTH_SHORT).show();
                CURRENT_FILE+=1;
            }
            if(CURRENT_FILE < listFiles.size())
                new SendFile().execute();
        }
    }

}
