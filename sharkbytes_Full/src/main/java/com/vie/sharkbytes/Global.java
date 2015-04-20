package com.vie.sharkbytes;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.HashMap;

public class Global extends Application {
    private static final String PROPERTY_TRACKER_ID = "UA-58453573-2";
    private static final String PROPERTY_GOOGLE_DEV_API_ID = "1045410847987";
    private static final String PROPERTY_AWS_ACCESS_ID = "AKIAJNLVN3VCWD76C5UA ";
    private static final String PROPERTY_AWS_SECRET_KEY = "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 ";
    private static final String PROPERTY_SNS_APP_ARN = "arn:aws:sns:us-west-2:843463439465:app/GCM/Shark_Bytes_Paid_Android_Production";
    private static final String PROPERTY_SNS_TOPIC_ARN = "arn:aws:sns:us-west-2:843463439465:SharkBytes_Android_Paid_Production";

    private static final String PROPERTY_GCM_PREFS = "GCM_Prefs";
    private static final String PROPERTY_REG_ID = "Registration_ID";
    private static final String PROPERTY_APP_VERSION = "App_Version";

    public enum TrackerName {
        APP_TRACKER,
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public Global() { super(); }

    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            //analytics.setDryRun(true);
            Tracker t = analytics.newTracker(PROPERTY_TRACKER_ID);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

    protected String getRegistrationId() {
        final SharedPreferences prefs = getSharedPreferences(PROPERTY_GCM_PREFS, 0);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            return "";
        }

        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = BuildConfig.VERSION_CODE;
        if (registeredVersion != currentVersion) {
            return "";
        }

        return registrationId;
    }

    protected void registerInBackground() {
        new AsyncTask() {
            @Override
            protected Void doInBackground(Object[] objects) {
                try {
                    // Register device id with GCM API
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    String registrationId = gcm.register(PROPERTY_GOOGLE_DEV_API_ID);
                    Log.d("test", "GCM Registered, id=" + registrationId);

                    // Store on AWS SNS server and in local Prefs
                    sendRegistrationIdToBackend(registrationId);
                    storeRegistrationId(registrationId);
                } catch (IOException ex) {}

                return null;
            }
        }.execute(null, null, null);
    }

    private void sendRegistrationIdToBackend(String registrationId) {
        AWSCredentials awsCredentials = new BasicAWSCredentials(PROPERTY_AWS_ACCESS_ID, PROPERTY_AWS_SECRET_KEY);
        AmazonSNSClient pushClient = new AmazonSNSClient(awsCredentials);
        pushClient.setRegion(Region.getRegion(Regions.US_WEST_2));

        // register new device endpoint
        CreatePlatformEndpointRequest platformEndpointRequest = new CreatePlatformEndpointRequest();
        platformEndpointRequest.setToken(registrationId);
        platformEndpointRequest.setPlatformApplicationArn(PROPERTY_SNS_APP_ARN);
        CreatePlatformEndpointResult appResult = pushClient.createPlatformEndpoint(platformEndpointRequest);

        // subscribe endpoint to topic
        if(!(appResult.getEndpointArn() == null) && !(appResult.getEndpointArn().isEmpty())) {
            SubscribeRequest subscribeRequest = new SubscribeRequest();
            subscribeRequest.setEndpoint(appResult.getEndpointArn());
            subscribeRequest.setProtocol("application");
            subscribeRequest.setTopicArn(PROPERTY_SNS_TOPIC_ARN);
            SubscribeResult subResult = pushClient.subscribe(subscribeRequest);
        }
    }

    private void storeRegistrationId(String registrationId) {
        final SharedPreferences prefs = getSharedPreferences(PROPERTY_GCM_PREFS, 0);
        int appVersion = BuildConfig.VERSION_CODE;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, registrationId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
}