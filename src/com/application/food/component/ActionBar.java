package com.application.food.component;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.application.food.R;
import com.application.food.utils.ActivityUtils;
import com.application.food.utils.ActivityUtils;
import com.application.food.utils.Utils;
import com.application.food.zxing.client.android.CaptureActivity;

/**
 * Created by IntelliJ IDEA.
 * User: r.deluca
 * Date: 25/01/13
 * Time: 10.00
 * To change this template use File | Settings | File Templates.
 */

public class ActionBar extends RelativeLayout{

    private LayoutInflater mInflater;
    private RelativeLayout mBarView;
    private ImageView imageView;
    private ImageView sharebar;
    private TextView myTitle;


    public ActionBar(final Context context, AttributeSet attrs) {
        super(context, attrs);

        final Context _context=context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mBarView = (RelativeLayout) mInflater.inflate(R.layout.titlebar, null);
        addView(mBarView);
        //button=(ImageView)findViewById(R.id.scan_button);
        //button.setOnClickListener(this);

        myTitle= (TextView)findViewById(R.id.myTitle);
        imageView= (ImageView)findViewById(R.id.infoBt);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtils.dialogMemory(_context);
            }
        });
        sharebar= (ImageView)findViewById(R.id.sharebar);
        sharebar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                if(context.getString(R.string.ita).equals("true"))
                    sharingIntent.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id=com.application.food&hl=it");
                else
                    sharingIntent.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id=com.application.food&hl=en");
                context.startActivity(Intent.createChooser(sharingIntent,context.getString(R.string.share)));
            }
        });
        /* mHomeLayout = (RelativeLayout) mBarView.findViewById(R.id.actionbar_home_bg);
        mHomeBtn = (ImageButton) mBarView.findViewById(R.id.actionbar_home_btn);
        mTitleView = (TextView) mBarView.findViewById(R.id.actionbar_title);

        TypedArray a = context.obtainStyledAttributes(attrs,  R.styleable.ActionBar);
        CharSequence title = a.getString(R.styleable.ActionBar_title);
        if (title != null) {
            setTitle(title);
        }
        a.recycle();*/
    }


    public TextView getMyTitle() {
        return myTitle;
    }

    public void setMyTitle(TextView myTitle) {
        this.myTitle = myTitle;
    }

}
