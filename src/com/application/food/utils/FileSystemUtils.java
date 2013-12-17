package com.application.food.utils;
import android.content.Context;
import android.os.Environment;
import com.application.food.model.DocumentWT;
import com.application.food.model.DocumentWT;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: r.deluca
 * Date: 04/12/12
 * Time: 14.16
 * To change this template use File | Settings | File Templates.
 */
public class FileSystemUtils {

    public static boolean saveFile(Context context,DocumentWT documentDetail){
        return  DBUtils.getInstance(context).addDocument(documentDetail);
    }

    /*Delete file and folder if necessary*/
    public static boolean deleteFile(Context context,DocumentWT documentDetail){
        File file = new File(documentDetail.path);
        boolean deleteFile= file.delete();
        DBUtils.getInstance(context).deleteDocumentByTitle(documentDetail.title);
        return deleteFile;
    }

    public static boolean deleteDirectory(File path){
      if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return( path.delete() );
    }

    public static boolean cleanTmp(){
        File path=new File("/sdcard/wt/tmp");
        if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return true;
    }


 /*   public static File getPathByDocument(AppLibrDetail documentDetail,boolean toDelete){
        File folder;
        if(toDelete)
            folder = new File(Environment.getExternalStorageDirectory() + "/doctra/"+documentDetail.NIso2Naz);
        else
            folder = new File(Environment.getExternalStorageDirectory() + "/doctra/"+documentDetail.NIso2Naz+"/"+ TipDoc.decodeFromValue(documentDetail.CTipLibrElem).getNTipDoc());
        boolean success = false;
        if (!folder.exists() && !toDelete) {
            success = folder.mkdirs();
        }
        return folder;
    }*/

    public static File createTmpFolder(){
        File folder = new File(Environment.getExternalStorageDirectory() + "/wt/tmp");
        boolean success=false;
        if (!folder.exists())
            success=  folder.mkdirs();
        return folder;
    }
    public static File createDocumentFolder(){
        File folder = new File(Environment.getExternalStorageDirectory() + "/wt/document");
        boolean success=false;
        if (!folder.exists())
            success=  folder.mkdirs();
        return folder;
    }
}