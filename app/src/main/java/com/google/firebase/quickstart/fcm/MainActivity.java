/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.firebase.quickstart.fcm;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyTag";
    TextView tvPwd;
    String pwdFile;
    String etFile;
    String efFile;
    String mdPath;
    TextView tvEF;
    TextView tvET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvPwd = (TextView) findViewById(R.id.tvPWD);
        tvEF = (TextView) findViewById(R.id.tvEF);
        tvET = (TextView) findViewById(R.id.tvET);

        mdPath = Environment.getExternalStorageDirectory().toString()+ "/MD/";
        pwdFile = "pwd.txt";
        etFile = "et.txt";
        efFile = "ef.txt";


        try {
            String pwd = getStringFromFile(mdPath +  pwdFile);

            pwd = pwd.replace("\n", "");
            tvPwd.setText(pwd);
            Log.d("MyTag", "pwd " + tvPwd);
        } catch (Exception ex) {
            Log.d("MyTag", "pwd exception " + ex.toString());
        }

        try {
            String et = getStringFromFile(mdPath +  etFile);

            et = et.replace("\n", "");
            tvET.setText(et);
            Log.d("MyTag", "et " + tvET);
        } catch (Exception ex) {
            Log.d("MyTag", "et exception " + ex.toString());
        }
        try {
            String ef = getStringFromFile(mdPath +  efFile);

            ef = ef.replace("\n", "");
            tvEF.setText(ef);
            Log.d("MyTag", "ef " + tvEF);
        } catch (Exception ex) {
            Log.d("MyTag", "ef exception " + ex.toString());
        }

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]

        Button subscribeButton = (Button) findViewById(R.id.subscribeButton);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // [START subscribe_topics]
                FirebaseMessaging.getInstance().subscribeToTopic("news");
                // [END subscribe_topics]

                // Log and toast
                String msg = getString(R.string.msg_subscribed);
                Log.d(TAG, msg);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        Button logTokenButton = (Button) findViewById(R.id.logTokenButton);
        logTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get token
                String token = FirebaseInstanceId.getInstance().getToken();

                // Log and toast
                String msg = getString(R.string.msg_token_fmt, token);
                Log.d(TAG, msg);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

    }


    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }
    public static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }
    private void writeToFile(String filePath, String data,Context context) {

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filePath, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public void writeToFile(String sPath, String sFile, String data) {
        // Get the directory for the user's public pictures directory.
        Log.d(TAG, "writeToFile starts sfile "+ sFile + " data " + data);
        final File path = new File(sPath);

        // Make sure the path directory exists.
        Log.d(TAG, "writeToFile path exists?");
        if (!path.exists()) {
            // Make it, if it doesn't exit
            path.mkdirs();
            Log.d(TAG, path.toString() + " mkdirs");
        }
        else {
            Log.d(TAG, path.toString() + " already exists");
        }

        final File file = new File(path, sFile);
        Log.d(TAG, "writeToFile file with path " + file.toString());

        // Save your stream, don't forget to flush() it before closing it.

        try {
            file.createNewFile();
            Log.d(TAG, "writeToFile create new file");
            FileOutputStream fOut = new FileOutputStream(file);
            Log.d(TAG, "writeToFile new fout");
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            Log.d(TAG, "writeToFile new out writer");
            myOutWriter.append(data);
            Log.d(TAG, "writeToFile after append");

            myOutWriter.close();

            fOut.flush();
            fOut.close();

            if(file.exists()) {
                Log.d(TAG, "file exists ");
            }
            else { Log.d(TAG, "file NOT  exists ");
            }

            try {
                Log.d(TAG, "Path: " + sPath);
                File directory = new File(sPath);
                Log.d(TAG, "directory: " + directory.toString());
                File[] files = directory.listFiles();

                Log.d(TAG, "Size: " + files.length);
                for (int i = 0; i < files.length; i++) {
                    Log.d(TAG, "FileName:" + files[i].getName());
                }
            }
            catch (Exception ex) {
                Log.d(TAG, "list files in dir failed: " + ex.toString());
            }
            Log.d(TAG, "writeToFile ends");
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
            Log.d(TAG, "File write failed: " + e.toString());
        }
    }


    public void onB_Save(View v) {
        Log.d("MyTag", "onB_Save starts");

        try{
            writeToFile(mdPath,pwdFile,tvPwd.getText().toString());
            writeToFile(mdPath,etFile,tvET.getText().toString());
            writeToFile(mdPath,efFile,tvEF.getText().toString());
        }
        catch (Exception ex){
            Log.d(TAG, "save btn error " + ex.toString());
        }

    };
}
