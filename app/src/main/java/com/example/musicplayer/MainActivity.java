package com.example.musicplayer;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ImageView goToMusic;
    String[] items;
    ArrayList<File> songs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        goToMusic = findViewById(R.id.goToMusic);
        runtimePermission();
        goToMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,PlaySongActivity.class);
                intent.putExtra("allSongsOfMobilePhone",songs);
                intent.putExtra("goToMusicActivity",true);
                startActivity(intent);
            }
        });
    }
    public void runtimePermission()
    {
        Dexter.withContext(MainActivity.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                displaySongs();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(MainActivity.this, "For playing songs of storage, you must do allow ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
    public ArrayList<File> findSongs(File file) {
        ArrayList<File> SongsList = new ArrayList<>();
        File[] files = file.listFiles();
        for(File singleFile : files != null ? files : new File[0])
        {
            if(singleFile.isDirectory() && !singleFile.isHidden())
            {
                SongsList.addAll(findSongs(singleFile));
            }
            else if(singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav") || singleFile.getName().endsWith(".m4a"))
                {
                    SongsList.add(singleFile);
                }
        }
        return SongsList;
    }
    void displaySongs() {
        songs = findSongs(Environment.getExternalStorageDirectory());
        items = new String[songs.size()];
        for(int i=0;i<songs.size();i++)
        {
            items[i] = songs.get(i).getName().toString();
        }
        ArrayAdapter<String> ad = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,items);
        listView.setAdapter(ad);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this,PlaySongActivity.class);
                intent.putExtra("allSongsOfMobilePhone",songs);
                intent.putExtra("indexNoOfSongs",i);
                startActivity(intent);
            }
        });
    }
    public void onBackPressed()
    {
        super.onBackPressed();
        System.exit(0);
    }
}




