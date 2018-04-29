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

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public String[] params ={"Info","Token","To"};
    private static final String TAG = "MyTag";
    @Override
    public void onCreate() {
        // The service is being created
        Log.d(TAG, "onCreate MyFirebaseMessagingService");
    }
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "remoteMessage.getFrom: " + remoteMessage.getFrom());
        String token = FirebaseInstanceId.getInstance().getToken();

        params[1] = token;
        Log.d(TAG, " params[1] token: " + params[1]);

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            try {

                //JSONObject json= (JSONObject) new JSONTokener(remoteMessage.getData().toString()).nextValue();
                //JSONObject json2 = json.getJSONObject("results");
                //params[2] = (String) json2.get("From");

                JSONObject json = new JSONObject(remoteMessage.getData());    // create JSON obj from string
                //JSONObject json2 = json.getJSONObject("From");
                //params[2] = json.toString();
                params[2] = json.getString("From");
                Log.d(TAG, "From " + params[2]);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, "json error " + e.toString());
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        Log.d(TAG, " before getPackageManager().getLaunchIntentForPackage ");
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.diter.motiondetection");
        launchIntent.putExtra("Hii",remoteMessage.getData().toString());
        Log.d(TAG, " before test launchIntent "  + launchIntent.toString());
        if (launchIntent != null) {
            Log.d(TAG, " before startActivity ");
            startActivity(launchIntent);//null pointer check in case package name was not found
            Log.d(TAG, " after startActivity");
        }

        //ter 20180407 хорошо бы ответить отправителю, что старт/стоп прошёл
        try {
            Log.d("MyTag","AsyncTask   starts  ");
            URL url = new URL("https://fcm.googleapis.com/fcm/send"); //Enter URL here
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            httpURLConnection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
            //httpURLConnection.setRequestProperty("Authorization","key=AAAAZAyzAtg:APA91bFg-Mb80iXj2bsat4eDcuq6LAsaxHLAazZbdwXYL2JMjWAu3eBVH82fj635EjEB4gYqCHl4s2HuOp1l24zddDK2Q-nr-Ulvy9JGxG1WpLrdhrdhkiesWu92d8VmI_Kr0VCZyTH1");
            httpURLConnection.setRequestProperty("Authorization","key=AAAAOsS_sbs:APA91bHv1HdE4id-pOTY2U6OOWcoNnCSLNJWFhtIc9bFSaTGtZ4gZWxM--WWJwJD3JKK6HcsDzfkoiSKK6I0IwaXErXAdapQnwFYxucxr6Mh6pqyEmOEA38jEwh_SQGnPFOISOob8PpN");
            httpURLConnection.connect();

            JSONObject jsonObject = new JSONObject();
            JSONObject param = new JSONObject();
            param.put("Hii",params[0]);
            param.put("From",params[1]);

            jsonObject.put("data",param);
            //black
            //jsonObject.put("to", "e9Acm88_C_U:APA91bHCtb4ex9_2hDKhhtLxL_7kX7Y747M7UkjSTX5iHuNJdacJ3pT9oMtW_FLRkT7OXGLZgP5MReYH9Wlz3abMwzqYm671aj3vritzkm-mgLh52Dq1GsfbloozESGclKmzFrERQm6p");
            // white
            //jsonObject.put("to", "eYn1KPG5u-Y:APA91bHcjmzb-tN_BnuBLFd1WkqStZ2-Ie1B54vWNItyvmF3iMWB3yNjyghtyBUbh2XIm-Q5POs9RRI9L3OiRtgpkcypvei_aCbGQHBVQ1FwT4NA7tmaQntYdu4CKigOwGTyDO8CRssk");
            jsonObject.put("to",params[2]);
            Log.d("MyTag","AsyncTask  before DataOutputStream");
            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());

            Log.d("MyTag","AsyncTask  before writeBytes");
            wr.writeBytes(jsonObject.toString());
            Log.d("MyTag","AsyncTask  before flush");
            wr.flush();
            Log.d("MyTag","AsyncTask  before close");
            wr.close();

            //Read
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));

            String line = null;


            while ((line = bufferedReader.readLine()) != null) {

                Log.d("MyTag","    " + line.toString());
            }

            bufferedReader.close();

        } catch (MalformedURLException e) {
            Log.d("MyTag","AsyncTask  MalformedURLException " + e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("MyTag","AsyncTask  IOException " + e.toString());
            e.printStackTrace();
        } catch (JSONException e) {
            Log.d("MyTag","AsyncTask  JSONException " + e.toString());
            e.printStackTrace();
        }


        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Log.d(TAG, "sendNotification " + messageBody);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        Log.d(TAG, "onDestroy MyFirebaseMessagingService");
    }
}
