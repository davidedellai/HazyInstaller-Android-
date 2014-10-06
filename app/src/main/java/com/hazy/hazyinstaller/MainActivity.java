package com.hazy.hazyinstaller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

public class MainActivity extends Activity {

    //MainActivity instance.
    public static MainActivity mainActivity;

    //Install Button.
    private Button btnShowProgress;

    //Need to colour the StatusBar.
    private SystemBarTintManager systemBars;

    //ActionBar ColorDrawable.
    private static ColorDrawable actionBarColor;

    //Openrecoveryscript file.
    public static File openrecovery;

    //The Process Dialog.
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;

    //Latest version available.
    private static int lastversion;

    //Some important booleans.
    public static boolean hasHazyROM = false;
    public static boolean newUpdate = false;
    public static boolean stable = false;

    //Various Intents.
    private Intent viewAbout;
    private Intent gotoFeaturesActivity;
    private Intent viewHelpUsPage;

    //Getting the current device.
    public String currentDevice = android.os.Build.MODEL;

    //Devices supported.
    public String S3 = "m0xx";
    public String secondS3 = "GT-I9300";
    public String Nexus4 = "Nexus 4";
    public String Nexus5 = "Nexus 5";
    public String Tab = "p3100";
    // private String OnePlusOne = "A0001";

    //Current version.
    private static int currentversion;

    //Download Links.
    public static String s3DownloadROM;
    public static String nexus4DownloadROM;
    public static String nexus5DownloadROM;
    public static String tabDownloadROM;

    //Some string for checking the current version.
    private static String name1;
    private static String name8;

    private String device;

    //Need to check connection.
    private ConnectivityManager connectivityManager;
    private NetworkInfo wifiInfo, mobileInfo;

    //Banner ImageView.
    private static ImageView image3;

    //Check if device is connected to the Internet (WiFi or Mobile Data).
    public boolean isConnectedToInternet(Context con){
        try {
            connectivityManager = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
            wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if(mobileInfo.isConnected()) {
                return true;
            } else if (wifiInfo.isConnected()) {
                return true;
            }
        }
        catch(Exception e){}
        return false;
    }

    /**************************
     * Getting current Device *
     **************************/
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
            return "noDevice";
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv4 = (TextView)findViewById(R.id.textView4);
        TextView tv8 = (TextView)findViewById(R.id.textView8);
        TextView tv1 = (TextView)findViewById(R.id.textView1);
        ImageView image2 = (ImageView)findViewById(R.id.imageView2);
        image3 = (ImageView)findViewById(R.id.imageView3);
        ImageView image1 = (ImageView)findViewById(R.id.imageView);

        //Colour the StatusBar.
        systemBars = new SystemBarTintManager(this);
        systemBars.setStatusBarTintEnabled(true);
        systemBars.setStatusBarTintColor(Color.parseColor("#7EAF42"));

        //Set ActionBar Color.
        actionBarColor = new ColorDrawable(Color.parseColor("#8AC249"));
        getActionBar().setBackgroundDrawable(actionBarColor);

        //Start Service.
        startService(new Intent(this, UpdateService.class));

        Thread t = new Thread() {
            public void run() {
                downloadToCheckVersion("http://hazyrom.net/download/lastversion", "lastversion");
            }
        }; t.start();

        try {
            checkVersion();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isConnectedToInternet(getApplicationContext())) {
            deviceHasntInternetConnection();
        }

        try {
            stuffs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Some layout problems... Had to fix with this.
        btnShowProgress = (Button) findViewById(R.id.btnProgressBar);
        if (Locale.getDefault().getLanguage().equals("it")) {
            btnShowProgress.setText("        " + "Scarica e installa la HazyROM");
        } else {
            btnShowProgress.setText("        " + "Download and Install Hazy ROM");
        }

        btnShowProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Checking free storage.
                storageChecker sc = new storageChecker();
                long freeSpace = sc.sd_card_free();
                if (freeSpace < 300) {
                    lowSpaceOnDisk();
                } else {
                    try {
                        deviceCheckForDownload();
                    } catch (Exception e) {}
                }
            }
        });

        Animation upAnimation = AnimationUtils.loadAnimation(this, R.anim.in_up);
        image1.startAnimation(upAnimation);


        if (hasHazyROM == true) {
            Log.d("Current Version:", name8);
            btnShowProgress.setVisibility(View.INVISIBLE);
            tv1.setVisibility(View.INVISIBLE);
            String version = name1.replace("_", " ");
            String okversion = version.replace("-", " ");

            //Some layout problems... Had to fix with this.
            if (Locale.getDefault().getLanguage().equals("it")) {
                tv8.setText(okversion + " installata sul tuo dispositivo");
            } else {
                tv8.setText(okversion + " is on your device!");
            }
            image2.setVisibility(View.INVISIBLE);

            Animation inAnimation = AnimationUtils.loadAnimation(this, R.anim.in);
            tv4.startAnimation(inAnimation);

            Animation Animation = AnimationUtils.loadAnimation(this, R.anim.in_left);
            tv8.startAnimation(Animation);

            Animation downAnimation = AnimationUtils.loadAnimation(this, R.anim.in_down);
            image3.startAnimation(downAnimation);

        } else {
            tv4.setVisibility(View.INVISIBLE);
            tv8.setVisibility(View.INVISIBLE);
            image3.setVisibility(View.INVISIBLE);

            Animation leftAnimation = AnimationUtils.loadAnimation(this, R.anim.in_left);
            tv1.startAnimation(leftAnimation);

            Animation Animation = AnimationUtils.loadAnimation(this, R.anim.in);
            image2.startAnimation(Animation);

            btnShowProgress.startAnimation(leftAnimation);
        }
    }


    //This is a really important method.
    public void stuffs() throws Exception {

        Thread t1 = new Thread() {
            public void run() {
                downloadToCheckVersion2("http://hazyrom.net/download/tabLink", "tabLink");
            }
        }; t1.start();

        Thread t2 = new Thread() {
            public void run() {
                downloadToCheckVersion2("http://hazyrom.net/download/nexus5Link", "n5Link");
            }
        }; t2.start();

        //Read build.prop line by line.
        try {
            FileInputStream in = new FileInputStream("/system/build.prop");
            int len = 0;
            byte[] data1 = new byte[1024];
            while (-1 != (len = in.read(data1))) {
                if (new String(data1, 0, len).contains("ro.hazy.version")) {
                    hasHazyROM = true;
                }
            }
        } catch (Exception e) {}

        Thread t = new Thread() {
            public void run() {
                downloadToCheckVersion2("http://hazyrom.net/download/s3Link", "s3Link");
            }
        }; t.start();

        try {
            Runtime.getRuntime().exec("su");
        } catch (Exception e) {
            deviceHasntRoot();
        }

        Thread t3 = new Thread() {
            public void run() {
                downloadToCheckVersion2("http://hazyrom.net/download/nexus4Link", "n4Link");
            }
        }; t3.start();

        if (hasHazyROM == true) {
            FileReader build = new FileReader("/system/build.prop");
            BufferedReader bre = new BufferedReader(build);
            for (int i = 0; i <= lineNum(); ++i) {
                if (i == lineNum()) {
                    String line = bre.readLine();
                    name1 = line.replace("ro.hazy.version=", "");
                    Log.d("VERSION:", String.valueOf(currentversion));
                } else {
                    bre.readLine();
                }
            }

            if (name1.contains("Stable")) {
                stable = true;
            }

            if (stable == false) {
                 String name2 = name1.replace("Hazy", "");
                 String name3 = name2.replace("-", " ");
                 String name4 = name3.replace("_", "");
                 String name5 = name4.replace("Beta", "");
                 String name6 = name5.replace(" _", "");
                 String name7 = name6.replace(".", "");
                 name8 = name7.replace(" ", "");
            } else {
                 String name2 = name1.replace("Hazy", "");
                 String name3 = name2.replace("-", " ");
                 String name4 = name3.replace("_", "");
                 String name5 = name4.replace("Stable", "");
                 String name6 = name5.replace(" _", "");
                 String name7 = name6.replace(".", "");
                 name8 = name7.replace(" ", "");
            }
            if (stable == true) {
                currentversion = Integer.valueOf(name8) + 1000;
            } else {
                currentversion = Integer.valueOf(name8);
            }

        }
    }

    public void finallyChecking() {
        if (currentversion < lastversion) {
            newUpdate = true;
        }

        if (newUpdate == true && hasHazyROM == true) {
            Toast.makeText(getApplicationContext(), R.string.newupdate, Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), updateActivity.class));
        } else if (hasHazyROM == true) {
            Toast.makeText(getBaseContext(), R.string.noupdates, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), R.string.notinstalled, Toast.LENGTH_LONG).show();
        }
    }


    public void downloadToCheckVersion(String DownloadUrl, String fileName) {

        try {
            File root = android.os.Environment.getExternalStorageDirectory();

            File dir = new File("/" +root);
            if (dir.exists() == false) {
                dir.mkdirs();
            }

            URL url = new URL(DownloadUrl);
            File file = new File(dir, fileName);

            long startTime = System.currentTimeMillis();

            // Open a connection to that URL.
            URLConnection ucon = url.openConnection();

            //Define InputStreams to read from the URLConnection.
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            //Read bytes to the Buffer until there is nothing more to read(-1).
            ByteArrayBuffer baf = new ByteArrayBuffer(5000);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

            //Convert the Bytes read to a String.
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baf.toByteArray());
            fos.flush();
            fos.close();

            Scanner s = new Scanner(file);
            lastversion = s.nextInt();
            if (lastversion > currentversion) {
                newUpdate = true;
            }
        } catch (IOException e) {
            Log.d("Download ", "Error: " + e);
        }
    }

    public void downloadToCheckVersion2(String DownloadUrl, String fileName) {

        try {
            File root = android.os.Environment.getExternalStorageDirectory();

            File dir = new File("/" +root);
            if (dir.exists() == false) {
                dir.mkdirs();
            }

            URL url = new URL(DownloadUrl);
            File file = new File(dir, fileName);

            long startTime = System.currentTimeMillis();

            URLConnection ucon = url.openConnection();

            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            ByteArrayBuffer baf = new ByteArrayBuffer(5000);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baf.toByteArray());
            fos.flush();
            fos.close();

        } catch (IOException e) {
            Log.d("Download", "Error: " + e);
        }
    }

    //Get the line number to get the current Hazy version.
    private int lineNum() throws Exception {
        String str;
        int count = 1;
        FileReader fin = new FileReader("/system/build.prop");
        Scanner src = new Scanner(fin);
        while (src.hasNext()) {
            str = src.nextLine();
            if (str.indexOf("ro.hazy.version") >= 0) {
                return count - 1;
            }
            count++;
        }
        fin.close();
        return 0;
    }

    private void checkVersion() throws Exception {
        FileReader s3 = new FileReader("/sdcard/s3Link");
        BufferedReader buf = new BufferedReader(s3);
        for (int i = 0; i <= 2; ++i) {
            if (i == lineNum()) {
                s3DownloadROM = buf.readLine();
            } else {
                buf.readLine();
            }
        }

        FileReader n4 = new FileReader("/sdcard/n4Link");
        BufferedReader buff = new BufferedReader(n4);
        for (int i = 0; i <= 2; ++i) {
            if (i == lineNum()) {
                nexus4DownloadROM = buff.readLine();
            } else {
                buff.readLine();
            }
        }

        FileReader n5 = new FileReader("/sdcard/n5Link");
        BufferedReader buffe = new BufferedReader(n5);
        for (int i = 0; i <= 2; ++i) {
            if (i == lineNum()) {
                nexus5DownloadROM = buffe.readLine();
            } else {
                buffe.readLine();
            }
        }

        FileReader tab = new FileReader("/sdcard/tabLink");
        BufferedReader buffer = new BufferedReader(tab);
        for (int i = 0; i <= 2; ++i) {
            if (i == lineNum()) {
                nexus5DownloadROM = buffer.readLine();
            } else {
                buffer.readLine();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                viewAbout = new Intent(Intent.ACTION_VIEW, Uri.parse("http://hazyrom.net/contacts"));
                startActivity(viewAbout);
                return true;
            case R.id.rom_features:
                gotoFeaturesActivity = new Intent(this, FeaturesActivity.class);
                startActivity(gotoFeaturesActivity);
                return true;
            case R.id.help_us:
                viewHelpUsPage = new Intent(Intent.ACTION_VIEW, Uri.parse("https://goo.gl/oM1nn6"));
                startActivity(viewHelpUsPage);
                return true;
            case R.id.action_update:
                finallyChecking();
                return true;
            case R.id.stop_service:
                stopProcess();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Stop Service.
    private void stopProcess() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.warning)
                .setMessage(R.string.closingapp)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
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

    /*******************************
     * Pass String to Download ROM *
     * *****************************/
    private void deviceCheckForDownload() throws Exception {
        mainActivity = new MainActivity();
        device = mainActivity.device();
        if (device.equals(S3)) {
            new DownloadFileFromURL().execute(s3DownloadROM);
        } else if (device.equals(Nexus4)) {
            new DownloadFileFromURL().execute(nexus4DownloadROM);
        } else if (device.equals(Nexus5)) {
            new DownloadFileFromURL().execute(nexus5DownloadROM);
        } else if (device.equals(Nexus5)) {
            new DownloadFileFromURL().execute(tabDownloadROM);
        } else if (device.equals("noDevice")) {
            deviceNotSupported();
        }
    }

    private void deviceHasntInternetConnection() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle(R.string.error)
                .setMessage(R.string.theresnointernet)
                .setCancelable(false)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        dialog.dismiss();
                    }
                }).show();
    }


    private void lowSpaceOnDisk() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle(R.string.error)
                .setMessage(R.string.lowspace)
                .setCancelable(false)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        dialog.dismiss();
                    }
                }).show();
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

    private void deviceHasntRoot() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.error)
                .setMessage(R.string.notrooted)
                .setCancelable(false)
                .setPositiveButton(R.string.what, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent whatIsRoot = new Intent(Intent.ACTION_VIEW, Uri.parse("http://bit.ly/SspwvM"));
                        startActivity(whatIsRoot);
                        finish();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        dialog.dismiss();
                    }

                }).show();
    }


    //AsyncTask to download the ROM.
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
                 if (rom.exists()) { rom.delete(); }
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

             } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
             }

            /**********************
             * Downloading Script *
             **********************/
            try {
                File openrecovery = new File("/sdcard/openrecoveryscript/");
                if (openrecovery.exists()) {
                    openrecovery.delete();
                }
                new DefaultHttpClient().execute(new HttpGet("http://hazyrom.net/download/hazyinstallerapp/openrecoveryscript"))
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

    //AsyncTask to copy Openrecoveryscript.
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
                    openrecovery = new File("/cache/recovery/openrecoveryscript");
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


