package com.application.food.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Debug;
import android.util.Log;
import android.widget.Toast;
import com.application.food.R;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: r.deluca
 * Date: 24/08/12
 * Time: 9.48
 * To change this template use File | Settings | File Templates.
 */
public class Utils {


    public static String TAG="WT";



    public static Bitmap loadBitmapScaled(String urlStr,int inSimpleSize,Context context) {
        URL url;
        Bitmap bm=null;
        /*      if(no_image_small==null)
       no_image_small=((BitmapDrawable)context.getResources().getDrawable(R.drawable.not_image_small)).getBitmap();
   if(!getConnectivityStatus(context,false))
       return no_image_small;*/
        try {
            url=new URL(urlStr);
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            if(inSimpleSize>0) bounds.inSampleSize=inSimpleSize;
            bm=BitmapFactory.decodeStream(url.openConnection().getInputStream(),null,bounds);
        } catch (IOException e) {
            Log.e("Error", "Could not load Bitmap from: " + urlStr);
            bm=null;
        }  finally {
            // return (bm==null)?no_image_small:bm;
            return bm;
        }
    }

    public static boolean getConnectivityStatus(Context context,boolean showToast){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean  status=activeNetworkInfo != null;
        if(!status && showToast){
            Toast.makeText(context, context.getString(R.string.no_connection_found), Toast.LENGTH_SHORT).show();
        }
        return status;
    }
    public static String logHeap() {
        Double allocated = new Double(Debug.getNativeHeapAllocatedSize())/new Double((1048576));
        Double available = new Double(Debug.getNativeHeapSize())/1048576.0;
        Double free = new Double(Debug.getNativeHeapFreeSize())/1048576.0;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        String str1="Heap native: allocated " + df.format(allocated) + "MB of " + df.format(available) + "MB (" + df.format(free) + "MB free) in [Cibo a suo tempo]";
        String str2="Memory: allocated: " + df.format(new Double(Runtime.getRuntime().totalMemory()/1048576)) + "MB of " + df.format(new Double(Runtime.getRuntime().maxMemory()/1048576))+ "MB (" + df.format(new Double(Runtime.getRuntime().freeMemory()/1048576)) +"MB free)" ;

        // don't need to add the following lines, it's just an app specific handling in my app
        /*  if (allocated>=(new Double(Runtime.getRuntime().maxMemory())/new Double((1048576))-MEMORY_BUFFER_LIMIT_FOR_RESTART)) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }*/
        return str1+"\n\n\n"+str2;
    }


    public static int rgbStringToExa(String rgb){
        String color=rgb.substring(4,rgb.length()-1);
        String [] first=color.split(",");
        String i = Integer.toString(Integer.parseInt(first[0].trim()), 16);
        String j = Integer.toString(Integer.parseInt(first[1].trim()), 16);
        String k = Integer.toString(Integer.parseInt(first[2].trim()), 16);

        // Color.rgb c = new Color(i,j,k);
        //return Integer.toHexString( c.getRGB() & 0x00ffffff ) );*/
        String ex=((i.length()==1)?"0"+i:i)+((j.length()==1)?"0"+j:j)+((k.length()==1)?"0"+k:k);
        /* if(ex.length()<6){
            while(ex.length()<6){
                ex=ex+"0";
            }
        }*/

        return  Color.parseColor("#"+ex);
    }
    public static String frmt(Date d) {
        if (d == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sdf.format(d);
    }

    public static  String [] documentComponent(String _title,Context context){
        String [] c=new String[3];
        String [] tmp=_title.split("_");
        String title=tmp[0].equalsIgnoreCase("wp")? context.getString(R.string.torre):context.getString(R.string.svegliatevi);
        String language=tmp[1];
        String date =tmp[2].substring(0,tmp[2].length()-4);
        String y=date.substring(0,4);
        String m=date.substring(4,6);
        String d="";
        if(date.length()==8)
            d=date.substring(6,8)+"/";

        if(context.getResources().getString(R.string.ita).equalsIgnoreCase("true"))
            date=d+m+"/"+y;
        else
            date= m+"/"+d+y;
        
        c[0]=title+" - " +date;
        c[1]= language;
        return c;
    }
}
