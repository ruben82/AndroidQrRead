package com.application.food;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.application.food.component.ActionBar;
import com.application.food.component.MenuActivity;
import com.application.food.utils.ActivityUtils;
import com.application.food.utils.Utils;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends MenuActivity
{
    /** Called when the activity is first created. */
    final Activity activity = this;
    private WebView webView;
    private ActionBar actionBar;
    private String initialUrl="";



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        webView = (WebView)findViewById(R.id.webview);
        mMenu = new CustomMenu(this, this, getLayoutInflater());
        mMenu.setHideOnSelect(true);
        mMenu.setItemsPerLineInPortraitOrientation(4);
        mMenu.setItemsPerLineInLandscapeOrientation(8);

        this.view=webView;
        this.codeActivity=ActivityUtils.MENU_ITEM_1;
        loadMenuItems();

     /*   AdView adview = (AdView)findViewById(R.id.addmain);
        AdRequest re = new AdRequest();
        adview.loadAd(re);*/
        actionBar=(ActionBar)findViewById(R.id.title_bar);
        if(Utils.getConnectivityStatus(this, true)){
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
                        /* if(pb.getVisibility()!= View.VISIBLE) {}*/
                        //  if(loadData)ActivityUtils.viewHideProgress(pb, genericView, true);
                        //actionBar.getmTitleView().setText("Calcio News - Loading "+progress+"%");
                    }
                }
            });

            webView.setWebViewClient(new WebViewClient(){
                protected WeakReference<Activity> activityRef = new WeakReference<Activity>(activity);
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    try {
                        view.loadUrl(url);
                    }catch( RuntimeException ignored ) {
                    }
                    return true;
                }
            });
            Date now= new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d");
            //webView.loadUrl("http://www.google.it");
            // webView.loadUrl("http://wol.jw.org/en/wol/dt/r1/lp-e/2013/4/15");
            // webView.loadUrl("http://wol.jw.org/en/wol/dt/r1/lp-e/2013/4/15");
            String string= sdf.format(now);
            if(getString(R.string.ita).equals("true"))
                initialUrl="http://m.wol.jw.org/it/wol/dt/r6/lp-i/"+string;
            else
                initialUrl="http://m.wol.jw.org/en/wol/dt/r1/lp-e/"+string;

            webView.loadUrl(initialUrl);
        }
    }

    @Override
    public void onBackPressed()
    {
        if(webView.canGoBack() && !webView.getOriginalUrl().equalsIgnoreCase(initialUrl)){
            webView.goBack();
        }else{
            if(ActivityUtils.getCurrentTaskActive(this)==1) {
                ActivityUtils.callClosing(this);
            }else
                super.onBackPressed();
        }
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
/*
    @Override
    public void onResume(){
        super.onResume();
        System.out.println("Resume---->"+this.getLocalClassName()+ " -----  BACK CLASS ---->"+getIntent().getStringExtra(ActivityUtils.BACK_ACTIVITY));
    }
    @Override
    public void onPause(){
        super.onPause();
        System.out.println("Pause---->"+this.getLocalClassName()+ " -----  BACK CLASS ---->"+getIntent().getStringExtra(ActivityUtils.BACK_ACTIVITY));
    }
    @Override
    public void onStop(){
        super.onStop();
        System.out.println("Stop---->"+this.getLocalClassName()+ " -----  BACK CLASS ---->"+getIntent().getStringExtra(ActivityUtils.BACK_ACTIVITY));
    }
    @Override
    public void onStart(){
        super.onStart();
        System.out.println("Start---->"+this.getLocalClassName()+ " -----  BACK CLASS ---->"+getIntent().getStringExtra(ActivityUtils.BACK_ACTIVITY));
    }*/
    /**
     * Load up our menu.
     */

}
