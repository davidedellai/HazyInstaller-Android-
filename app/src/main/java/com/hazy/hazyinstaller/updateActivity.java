package com.hazy.hazyinstaller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.*;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;


public class updateActivity extends Activity {

    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;

    private String currentDevice = android.os.Build.MODEL;
    public String S3 = "m0xx";
    public String secondS3 = "GT-I9300";
    public String Nexus4 = "Nexus 4";
    public String Nexus5 = "Nexus 5";
    public static String Tab = "p3100";
    // private String OnePlusOne = "A0001";
    // private String secondOnePlusOne = "bacon";

    //public static String s3DownloadROM = "http://fs1.d-h.st/download/00139/zHw/hazy-4.4.4-20140905-i9300-Beta_3.zip";
    //public static String nexus4DownloadROM = "http://fs1.d-h.st/download/00139/5fd/hazy-4.4.4-20140905-mako-Beta_3.zip";
    //public static String nexus5DownloadROM = "http://fs1.d-h.st/download/00139/6JU/hazy-4.4.4-20140905-hammerhead-Beta_3.zip";

    public static String s3DownloadROM() throws Exception {
        FileReader s3 = new FileReader("/sdcard/s3Link");
        BufferedReader buf = new BufferedReader(s3);
        return buf.readLine();
    }
    public static String nexus4DownloadROM() throws Exception {
        FileReader n4 = new FileReader("/sdcard/n4Link");
        BufferedReader buf = new BufferedReader(n4);
        return buf.readLine();
    }
    public static String nexus5DownloadROM() throws Exception {

        FileReader n5 = new FileReader("/sdcard/n5Link");
        BufferedReader buf = new BufferedReader(n5);
        return buf.readLine();
    }

    public static String tabDownloadROM() throws Exception {

        FileReader tab = new FileReader("/sdcard/tabLink");
        BufferedReader buf = new BufferedReader(tab);
        return buf.readLine();
    }


    public String device() {
        if (currentDevice.equals(S3)) {
            return S3;
        } else if (currentDevice.equals(secondS3)) {
            return S3;
        } else if (currentDevice.equals(Nexus4)) {
            return Nexus4;
        } else if (currentDevice.equals(Nexus5)) {
            return Nexus5;
        } else if (currentDevice.equals(Tab)) {
            return Tab;
        } else {
            return Nexus5;
        }
    }

    private void deviceNotSupported() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle(R.string.error)
                .setMessage(R.string.notsupported)
                .setCancelable(false)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        dialog.dismiss();
                    }
                }).show();
    }

    private void deviceCheckForDownload() throws Exception {
        updateActivity updateActivity = new updateActivity();
        String device = updateActivity.device();
        if (device.equals(S3)) {
            new DownloadFileFromURL().execute(this.s3DownloadROM());
        } else if (device.equals(Nexus4)) {
            new DownloadFileFromURL().execute(this.nexus4DownloadROM());
        } else if (device.equals(Nexus5)) {
            new DownloadFileFromURL().execute(this.nexus5DownloadROM());
        } else if (device.equals(Tab)) {
            new DownloadFileFromURL().execute(this.tabDownloadROM());
        } else if (device.equals("noDevice")) {
            deviceNotSupported();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type:
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading HazyROM...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setCancelable(false);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8AC249")));
        SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
        systemBarTintManager.setStatusBarTintEnabled(true);
        systemBarTintManager.setStatusBarTintColor(Color.parseColor("#7EAF42"));

        Button btn = (Button)findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deviceCheckForDownload();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.update, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        @Override
        protected String doInBackground(String... f_url) {

            /***********************
             * Downloading HazyROM *
             ***********************/
            int count;
            try {
                File rom = new File("/sdcard/HazyROM.zip");
                if (rom.exists()) {
                    rom.delete();
                }
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                int lenghtOfFile = conection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                OutputStream output = new FileOutputStream("/sdcard/HazyROM.zip");
                byte data[] = new byte[1024];
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) { Log.d("Error", e.getMessage());}

            /**********************
             * Downloading Script *
             **********************/
            try {
                File openrecovery = new File("/sdcard/openrecoveryscript/");
                if (openrecovery.exists()) {
                    openrecovery.delete();
                }
                new DefaultHttpClient().execute(new HttpGet("http://hazyrom.net/download/hazyinstallerapp/openrecoveryscriptwithnowipe"))
                        .getEntity().writeTo(
                        new FileOutputStream(new File("/sdcard/openrecoveryscript")));
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            pDialog.setProgress(Integer.parseInt(progress[0]));
            pDialog.setCancelable(true);
        }

        @Override
        protected void onPostExecute(String file_url) {
            dismissDialog(progress_bar_type);
            new copyScript().execute();
        }


        public class copyScript extends AsyncTask<Void, Void, Void> {

            /****************************************************************
             * Installing BusyBox, copying Script and rebooting to Recovery *
             ****************************************************************/
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Runtime.getRuntime().exec("su");
                    String[] InstallBusyBoxCmd = new String[]{
                            "su", "-c",
                            "cat /sdcard/busybox &gt; /system/xbin/busybox;" +
                                    "chmod 755 /system/xbin/busybox;" +
                                    "busybox --install /system/xbin"
                    };
                    Runtime.getRuntime().exec(InstallBusyBoxCmd);
                    Runtime.getRuntime().exec("mount -ro remount,rw /cache/recovery/");
                    Runtime.getRuntime().exec("mount -ro remount,rw /cache/");
                    Runtime.getRuntime().exec("mount -ro remount,rw /");
                    File openrecovery = new File("/cache/recovery/openrecoveryscript");
                    if (openrecovery.exists()) {
                        openrecovery.delete();
                    }
                    final String sSUCommand = "cp /sdcard/openrecoveryscript /cache/recovery/";

                    final String[] sCommand = {"su", "-c", sSUCommand};
                    Runtime.getRuntime().exec(sCommand);
                    Thread t = new Thread() {
                        public void run() {
                            Intent gotoCongratulation = new Intent(getApplicationContext(), CongratulationActivity.class);
                            startActivity(gotoCongratulation);
                        }
                    };
                    t.start();

                    return null;
                } catch (Exception e) {}

                return null;
            }
        }
    }
}
