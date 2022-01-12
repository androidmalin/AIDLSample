package com.ryg.chapter_2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.ryg.chapter_2.aidl.Book;
import com.ryg.chapter_2.manager.UserManager;
import com.ryg.chapter_2.model.User;
import com.ryg.chapter_2.utils.MyConstants;
import com.ryg.chapter_2.utils.MyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UserManager.sUserId = 2;
        findViewById(R.id.button1).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, SecondActivity.class);
            User user = new User(0, "jake", true);
            user.book = new Book();
            intent.putExtra("extra_user", (Serializable) user);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "UserManage.sUserId=" + UserManager.sUserId);
        persistToFile();
        super.onResume();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void persistToFile() {
        new Thread(() -> {
            User user = new User(1, "hello world", false);
            File dir = new File(MyConstants.CHAPTER_2_PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File cachedFile = new File(MyConstants.CACHE_FILE_PATH);
            ObjectOutputStream objectOutputStream = null;
            try {
                objectOutputStream = new ObjectOutputStream(new FileOutputStream(cachedFile));
                objectOutputStream.writeObject(user);
                Log.d(TAG, "persist user:" + user);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                MyUtils.close(objectOutputStream);
            }
        }).start();
    }
}
