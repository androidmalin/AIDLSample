package com.ryg.chapter_2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.ryg.chapter_2.manager.UserManager;
import com.ryg.chapter_2.model.User;
import com.ryg.chapter_2.utils.MyConstants;
import com.ryg.chapter_2.utils.MyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class SecondActivity extends AppCompatActivity {
    private static final String TAG = "SecondActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        findViewById(R.id.button1).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(SecondActivity.this, ThirdActivity.class);
            intent.putExtra("time", System.currentTimeMillis());
            startActivity(intent);
        });
        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = (User) getIntent().getSerializableExtra("extra_user");
        Log.d(TAG, "onResume user:" + user.toString());
        Log.d(TAG, "UserManage.sUserId=" + UserManager.sUserId);
        recoverFromFile();
    }

    private void recoverFromFile() {
        new Thread(() -> {
            User user;
            File cachedFile = new File(MyConstants.CACHE_FILE_PATH);
            if (cachedFile.exists()) {
                ObjectInputStream objectInputStream = null;
                try {
                    objectInputStream = new ObjectInputStream(new FileInputStream(cachedFile));
                    user = (User) objectInputStream.readObject();
                    Log.d(TAG, "recover user:" + user);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    MyUtils.close(objectInputStream);
                }
            }
        }).start();
    }
}
