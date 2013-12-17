package com.application.food;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.application.food.component.ActionBar;
import com.application.food.component.MenuActivity;
import com.application.food.utils.FileSystemUtils;
import com.application.food.utils.HttpUtil;
import com.application.food.utils.Utils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

/**
 * Created by IntelliJ IDEA.
 * User: r.deluca
 * Date: 15/04/13
 * Time: 14.53
 * To change this template use File | Settings | File Templates.
 */
public class AfterCaptureActivity extends MenuActivity implements CustomMenu.OnMenuItemSelectedListener{
    private String url;
    private DownloadFile downloadFile;
    private String completeUrl = null;
    private ProgressDialog mProgressDialog;
    final Activity activity = this;
    private WebView webView;
    private ActionBar actionBar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        actionBar=(ActionBar)findViewById(R.id.title_bar);
        url=this.getIntent().getStringExtra("qrcode");
        Log.i("URL QR", url);
        webView = (WebView)findViewById(R.id.webview);
        mMenu = new CustomMenu(this, this, getLayoutInflater());
        mMenu.setHideOnSelect(true);
        mMenu.setItemsPerLineInPortraitOrientation(4);
        mMenu.setItemsPerLineInLandscapeOrientation(8);
        this.view=webView;
        loadMenuItems();

        if(!Utils.getConnectivityStatus(this, true))return;
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.clearCache(true);

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress){
                //  actionBar.getmTitleView().setText("Calcio News - Loading " + progress + "%");
                if(progress < 100) {
                        actionBar.getMyTitle().setText(getString(R.string.app_name)+" - "+progress+"%");
                }else{
                    actionBar.getMyTitle().setText(getString(R.string.app_name));
                }
            }
        });

        webView.setWebViewClient(new WebViewClient(){
            protected WeakReference<Activity> activityRef = new WeakReference<Activity>(activity);
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    if (url.endsWith(".pdf")) {
                        handleDownload(url);
                        return true;
                    }else if(url.endsWith(".epub")){
                        Toast.makeText(activity,activity.getString(R.string.no_supported),Toast.LENGTH_SHORT).show();
                        return true;
                    }else if (url.endsWith(".mp3")) {
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(url));
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        }
                        catch (IllegalArgumentException e) {  }
                        catch (IllegalStateException e) {  }
                        catch (IOException e) { }
                        return true;
                    }else if (url.endsWith(".mp4")) {
                       Intent intent=new Intent(activity,DemoVideo.class);
                        intent.putExtra("url",url);
                        activity.startActivity(intent);
                        return true;
                    }else if(url.endsWith(".m4b")){
                        Toast.makeText(activity,activity.getString(R.string.no_supported),Toast.LENGTH_SHORT).show();
                        return true;
                    }else if(url.endsWith(".zip")){
                        Toast.makeText(activity,activity.getString(R.string.no_zip),Toast.LENGTH_SHORT).show();
                        return true;
                    }else{
                        view.loadUrl(url);
                    }
                }catch( RuntimeException ignored ) {
                }
                return true;
            }
        });
        webView.loadUrl(url);
    }


    @Override
    public void onBackPressed()
    {
        if(webView.canGoBack())
            webView.goBack();
        else
            super.onBackPressed();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //System.out.println("Destroy --->"+this.getLocalClassName()+ " -----  BACK CLASS ---->"+getIntent().getStringExtra(ActivityUtils.BACK_ACTIVITY));
        destroyWebView();
    }

    private void destroyWebView(){
        System.out.println("SICK AND DESTRY**************************************");
        if(webView!=null){
            webView.clearHistory();
            webView.freeMemory();
            webView.clearCache(true);
            webView.clearView();
            webView.destroy();
        }
    }

    public void handleDownload(String url) {
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setMessage(getString(R.string.download_progress));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downloadFile = new DownloadFile();
        completeUrl=url;
        downloadFile.execute(completeUrl);
    }

   String [] titleArray;
    private class DownloadFile extends AsyncTask<String, Integer, String> {
        private String ERROR = "ERROR";
        private String SUCCESS = "SUCCESS";

        @Override
        protected String doInBackground(String... sUrl) {
            try {
                String u=sUrl[0];
                HttpResponse response = HttpUtil.httpGetCall(u);
                if (response != null) {
                    System.out.println("dopo Chiamata con status " + response.getStatusLine().getStatusCode());
                    if (response.getStatusLine().getStatusCode() == 200) {
                        int fileLength = 100;
                        Header[] headers = response.getAllHeaders();
                        for (Header h : headers) {
                            if (h.getName().equalsIgnoreCase("Content-Length")) {
                                String d = h.getValue();
                                fileLength = Integer.parseInt(d.trim());
                            }
                        }
                        InputStream input = new BufferedInputStream(response.getEntity().getContent());
                        File path = FileSystemUtils.createTmpFolder();
                        titleArray= u.split("/");
                        OutputStream output = new FileOutputStream(path.getAbsolutePath() + "/" + titleArray[titleArray.length-1]);

                        byte data[] = new byte[1024];
                        long total = 0;
                        int count;
                        while ((count = input.read(data)) != -1) {
                            total += count;
                            // publishing the progress....
                            publishProgress((int) (total * 100 / fileLength));
                            output.write(data, 0, count);
                        }

                        output.flush();
                        output.close();
                        input.close();
                        openFile(path.getAbsolutePath() + "/" + titleArray[titleArray.length-1]);
                    } else if (response.getStatusLine().getStatusCode() == 302) {
                    } else {
                        return ERROR;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ERROR;
            }
            return SUCCESS;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            mProgressDialog.setProgress(progress[0]);
        }

        protected void onPostExecute(String result) {
            if (result.equalsIgnoreCase(ERROR)) {
                mProgressDialog.dismiss();
            }
        }
    }

    public void openFile(String url) {
        Intent intent = new Intent();
        File f = new File(url);
        intent.setDataAndType(Uri.fromFile(f), "application/pdf");
        intent.setClass(activity, OpenFileActivity.class);
        intent.setAction("android.intent.action.VIEW");
        Bundle b = new Bundle();
        b.putString("completeUrl", url);
        b.putString("title", titleArray[titleArray.length-1]);
        b.putBoolean("library", false);
        intent.putExtras(b);
        mProgressDialog.dismiss();
        downloadFile = null;
        activity.startActivity(intent);
    }
}