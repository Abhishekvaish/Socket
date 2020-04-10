package com.example.socket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Receive extends AppCompatActivity {
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        textView = findViewById(R.id.tvRText);
        new doInBackGround().execute();

    }
    public class doInBackGround extends AsyncTask<Void,Void,Void> {
        Socket socket;
        String filename ;

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                ServerSocket server = new ServerSocket(5000);
                socket = server.accept();

                DataInputStream inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()) );
                filename = inputStream.readUTF();


                String root = Environment.getExternalStorageDirectory().toString();
                File dir = new File(root+File.separator+"Socket");
                if(!dir.exists() || dir.isDirectory())
                    dir.mkdir();
                File file = new File(dir,filename);
                Log.d("ABHISHEK",file.getName());
                FileOutputStream outputFile = new FileOutputStream(file);

                int BUFFER_SIZE = 4096,count;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ( (count=inputStream.read(buffer)) != -1 )
                    outputFile.write(buffer,0,count);
                    //outputFile.write(buffer);
                outputFile.flush();
                outputFile.close();

                inputStream.close();
                server.close();

            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            textView.setText(filename);
            Toast.makeText(Receive.this, "File Received", Toast.LENGTH_SHORT).show();
        }
    }
}
