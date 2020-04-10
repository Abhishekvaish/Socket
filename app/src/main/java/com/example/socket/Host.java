package com.example.socket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Host extends AppCompatActivity {
    Socket socket;
    TextView tvHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        tvHint = findViewById(R.id.tvHint);
        tvHint.setText("Please On Your Hostpot and ask the sender to connect ");
        new Connect().execute();
        new GetFiles().execute();
    }

    public class GetFiles extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                File file = new File(Environment.getExternalStorageDirectory(),"Socket"+File.separator+dataInputStream.readUTF());
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                int BUFFER_SIZE = 4096,count;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ( (count=dataInputStream.read(buffer)) != -1 )
                    fileOutputStream.write(buffer,0,count);

                fileOutputStream.close();
                dataInputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(Host.this, "File Received", Toast.LENGTH_SHORT).show();
        }
    }

    public class Connect extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ServerSocket server =  new ServerSocket(5000);
                socket = server.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            tvHint.setText("Connected");
        }
    }

}
