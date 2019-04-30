package fr.vyfe.helper;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class GetLatestVersion extends AsyncTask<String, String, JSONObject> {

    private ProgressDialog progressDialog;
    private String  latestVersion;
    private String versionNameApp;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try {
//It retrieves the latest version by scraping the content of current version from play store at runtime
            Document doc = Jsoup.connect("http://play.google.com/store/apps/details?id=fr.vyfe").get();
            latestVersion = doc.getElementsByClass("htlgb").get(6).text();

        }catch (Exception e){
            e.printStackTrace();

        }

        return new JSONObject();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        versionNameApp = "latestVersion";
        if(latestVersion!=null) {
            versionNameApp = "a";
          /*  if (!currentVersion.equalsIgnoreCase(latestVersion)){
            if(!isFinishing()){ //This would help to prevent Error : BinderProxy@45d459c0 is not valid; is your activity running? error
                showUpdateDialog();
            }*/
        } else versionNameApp ="b";
    }
      /*  else
                background.start();
        super.onPostExecute(jsonObject);*/


    private String showUpdateDialog(String lastVersion){
       /* final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("A New Update is Available");
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               *//* startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                        ("market://details?id=yourAppPackageName")));
                dialog.dismiss()*//*;
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                *//*  background.start();*//*
            }
        });

        builder.setCancelable(false);*/
        /*  dialog = builder.show();*/
        return lastVersion;
    }

    public static String getCurrentVersion(Context context){
        PackageManager pm = context.getPackageManager();
        PackageInfo pInfo = null;

        try {
            pInfo =  pm.getPackageInfo(context.getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
        return  pInfo.versionName;
    }
}
