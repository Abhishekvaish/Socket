package com.example.socket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.inputmethodservice.Keyboard;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Receive extends AppCompatActivity {
    TextView tvHint;
    RecyclerView recyclerView;
    ArrayList<RowFiles> listFiles;
    public static Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        tvHint = findViewById(R.id.tvHint);
        tvHint.setText("Please On Your Hostpot and ask the sender to connect ");

        recyclerView = findViewById(R.id.recyclerview);
//        listFiles = new ArrayList<>(0);

        listFiles = ApplicationClass.listFiles;
        recyclerView.setAdapter(new MyAdapter(listFiles));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        new ReceiveFile().execute();
    }

    public  class ReceiveFile extends AsyncTask<Void,Integer,Void>{
        RowFiles fileObj;
        Boolean FILE_RECEIVED = false;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ServerSocket server =  new ServerSocket(5000);
                socket = server.accept();
                publishProgress(-2);
                ApplicationClass.type="RECEIVER";
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                fileObj = new RowFiles(dataInputStream.readUTF(),dataInputStream.readUTF(),"0",null);
                listFiles.add(fileObj);
                publishProgress(-1);
                File file = new File(Environment.getExternalStorageDirectory(),"Socket"+File.separator+fileObj.getName());
                FileOutputStream fileOutputStream = new FileOutputStream(file);


                int BUFFER_SIZE = 4096,count;
                int RECEIVE_SIZE = 0;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ( (count=dataInputStream.read(buffer)) != -1)
                {
                    fileOutputStream.write(buffer,0,count);
                    RECEIVE_SIZE+=count;
                    publishProgress((int)(RECEIVE_SIZE/(1024.0*1024)));
                }

                dataInputStream.close();
                fileOutputStream.close();
                socket.close();
                server.close();

                FILE_RECEIVED = true;
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
            if(values[0]==-2)
                tvHint.setText("Connected");
            else if(values[0]!=-1)
                fileObj.setMidsize(values[0]+"");
            recyclerView.getAdapter().notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(FILE_RECEIVED &&  ApplicationClass.type.equals("RECEIVER"))
            {
                Toast.makeText(Receive.this, "File Received", Toast.LENGTH_SHORT).show();
                new ReceiveFile().execute();
            }

        }

    }

}
