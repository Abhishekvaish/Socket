package com.example.socket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
            PackageManager.PERMISSION_GRANTED)
        {
            if(! ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE))
                Toast.makeText(this, "You Permanently denied the permission To restore defaults clear appdata in settings->App->Socket->Permission->Storage", Toast.LENGTH_LONG).show();
            else
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},10);
        }


        String root = Environment.getExternalStorageDirectory().toString();
        File dir = new File(root+File.separator+"Socket");
        if(!dir.exists() || dir.isDirectory())
            dir.mkdir();

    }

    public void btnSend(View view){
        if( !ApplicationClass.type.equals("RECEIVER"))
        {
            Intent intent = new Intent(MainActivity.this,Send.class);
            startActivity(intent);
        }
        else
            Toast.makeText(this, "Receiving Some Files Disconnect to Stop", Toast.LENGTH_SHORT).show();

    }
    public void btnReceive(View view){
        if( !ApplicationClass.type.equals("SENDER"))
        {
            Intent intent = new Intent(MainActivity.this,Receive.class);
            startActivity(intent);
        }
        else
            Toast.makeText(this, "Sending Some Files Disconnect to Stop", Toast.LENGTH_SHORT).show();
    }

    public void Disconnect(View view){
        ApplicationClass.type= "";
        ApplicationClass.listFiles.clear();
        if(Send.socket!=null) {
            try {
                Send.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (Receive.socket!=null){
            try {
                Receive.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        view.setVisibility(View.GONE);

    }


    @Override
    protected void onResume() {
        super.onResume();
        Button btnDisconnect = findViewById(R.id.BtnDisconnect);
        if(!ApplicationClass.type.isEmpty())
            btnDisconnect.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==10){
            if(PackageManager.PERMISSION_GRANTED== grantResults[0])
                Toast.makeText(this, "Thankyou", Toast.LENGTH_SHORT).show();
        }
    }


}
