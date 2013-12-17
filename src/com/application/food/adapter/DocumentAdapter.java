package com.application.food.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.application.food.OpenFileActivity;
import com.application.food.R;
import com.application.food.OpenFileActivity;
import com.application.food.model.DocumentWT;
import com.application.food.utils.Utils;
import com.application.food.model.DocumentWT;
import com.application.food.utils.Utils;

import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: r.deluca
 * Date: 18/04/13
 * Time: 11.42
 * To change this template use File | Settings | File Templates.
 */
public class DocumentAdapter  extends ArrayAdapter<DocumentWT> {
    protected LayoutInflater mInflater;
    protected Context context = null;
    public DocumentAdapter(Context context, List<DocumentWT> items) {
        super(context, R.layout.fragment_list_item, items);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DocumentWT wt=getItem(position);
        convertView = mInflater.inflate(R.layout.fragment_list_item, parent, false);
        View l=convertView.findViewById(R.id.cell_item);
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                File f = new File(wt.path);
                intent.setDataAndType(Uri.fromFile(f), "application/pdf");
                intent.setClass(context, OpenFileActivity.class);
                intent.setAction("android.intent.action.VIEW");
                Bundle b = new Bundle();
                b.putString("completeUrl", wt.path);
                b.putString("title", wt.title);
                b.putBoolean("library", true);
                intent.putExtras(b);
                context.startActivity(intent);
            }
        });
        String[] component= Utils.documentComponent(wt.title, context);
        TextView textView=(TextView) convertView.findViewById(R.id.document_name);
        textView.setText(component[0]);
        ImageView imageView=(ImageView) convertView.findViewById(R.id.lang_image);
        imageView.setImageResource(component[1].equalsIgnoreCase("i") ? R.drawable.it_small : R.drawable.en_small);
        return convertView;
    }
}
