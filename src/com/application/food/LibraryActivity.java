package com.application.food;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import com.application.food.R;
import com.application.food.adapter.DocumentAdapter;
import com.application.food.adapter.DocumentAdapter;
import com.application.food.model.DocumentWT;
import com.application.food.utils.DBUtils;
import com.application.food.model.DocumentWT;
import com.application.food.utils.ActivityUtils;
import com.application.food.utils.DBUtils;
import com.application.food.utils.FileSystemUtils;


import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: r.deluca
 * Date: 18/04/13
 * Time: 10.27
 * To change this template use File | Settings | File Templates.
 */
public class LibraryActivity extends MainActivity {
    private GridView listGrid;
    private View no_result;
    private List<DocumentWT> list;
    private DocumentAdapter listAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library);
      /*  AdView adview = (AdView)findViewById(R.id.addlibr);
        AdRequest re = new AdRequest();
        adview.loadAd(re);*/
        listGrid=(GridView)findViewById(R.id.thelistlibrary);
        no_result=findViewById(R.id.no_result);


        mMenu = new CustomMenu(this, this, getLayoutInflater());
        mMenu.setHideOnSelect(true);
        mMenu.setItemsPerLineInPortraitOrientation(4);
        mMenu.setItemsPerLineInLandscapeOrientation(8);

        this.view=listGrid;
        this.codeActivity= 5;
        loadMenuItems();
    }

    @Override
    public void onResume() {
        super.onResume();

        list= DBUtils.getInstance(this).getDocuments();
        listAdapter = new DocumentAdapter(this,list);
        listGrid.setAdapter(listAdapter);
        listGrid.setFastScrollEnabled(true);
        listGrid.setVisibility(View.GONE);
        listAdapter.notifyDataSetChanged();
        listGrid.setVisibility(list!=null &&list.size()>0 ? View.VISIBLE : View.GONE);
        no_result.setVisibility(list!=null && list.size()>0 ? View.GONE : View.VISIBLE);
    }

}