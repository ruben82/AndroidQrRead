package com.application.food;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import com.application.food.R;
import com.application.food.utils.Utils;
import com.application.food.utils.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: r.deluca
 * Date: 16/04/12
 * Time: 12.20
 * To change this template use File | Settings | File Templates.
 */
public class DemoVideo extends Activity implements MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener {

    private VideoView videoView;
    private  String urlStr=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.video);
        urlStr=getIntent().getStringExtra("url");
        System.out.println("URL VIDEO:"+urlStr);
        videoView = (VideoView) findViewById(R.id.YoutubeVideoView);
        videoView.setOnCompletionListener(this);
        videoView.setOnErrorListener(this);
        if(Utils.getConnectivityStatus(this, true)){
          /*  pb=findViewById(R.id.layout_progress_video);
            genericView=findViewById(R.id.table_video);*/
            videoView.setVideoURI(Uri.parse(urlStr));
            System.out.println("ON GOT VIDEO"+ videoView.getCurrentPosition());
            videoView.setMediaController(new MediaController(
                    DemoVideo.this));
            videoView.requestFocus();
            videoView.start();
        }else
            this.finish();

    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i("ON COMPLETION", "" + mp.getCurrentPosition());
       // ActivityUtils.viewHideProgress(pb,genericView,false);
        finish();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Toast.makeText(this, "Errore nell'esezuzione del video. Ritorno al dettaglio.", Toast.LENGTH_SHORT).show();
     //   ActivityUtils.viewHideProgress(pb,genericView,false);
        finish();
        return true;
    }


    @Override
    public void onStart(){
        super.onStart();
    }
}