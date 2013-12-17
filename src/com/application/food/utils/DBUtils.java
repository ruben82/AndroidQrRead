package com.application.food.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.application.food.model.DocumentWT;
import com.application.food.model.DocumentWT;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: r.deluca
 * Date: 04/12/12
 * Time: 9.26
 */
public class DBUtils extends SQLiteOpenHelper {
    /**
     * db fields
     */

    public static final String KEY_DOC_ID = "ID";
    public static final String KEY_DOC_TITLE = "Title";
    public static final String KEY_DOC_PATH = "Path";


    private static final int DB_VERSION = 2;
    public SQLiteDatabase db;

    private static DBUtils mInstance = null;

    private static final String DATABASE_DOCUMENT_CREATE = "create table if not exists document "
            + " ( ID integer primary key , Title text ,  Path text )";



    public DBUtils(Context context) {
        super(context, "library.db", null, DB_VERSION);
        db = this.getWritableDatabase();
        db.execSQL(DATABASE_DOCUMENT_CREATE);
    }

    public static DBUtils getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DBUtils(ctx.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean addDocument(DocumentWT documentDetail) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_DOC_ID, documentDetail.id);
        cv.put(KEY_DOC_TITLE, documentDetail.title);
        cv.put(KEY_DOC_PATH, documentDetail.path);

        if (db.update("document", cv, KEY_DOC_TITLE + "='" + documentDetail.title+"'", null) == 0) {
            long value = db.insert("document", null, cv);
            return (value > -1);
        }

        return true;
    }

    public String getDocumentUrl(String title) {
        String url = null;

        Cursor cur = db.query(true, "document", new String[]{KEY_DOC_PATH},
                KEY_DOC_PATH + "= '" + title+"'", null,
                null, null, null, "1");
        if (cur != null && cur.moveToFirst()) {
            url = cur.getString(0);
            cur.close();
        }
        return url;
    }


    public DocumentWT getDocument(String title) {
        String query = "select " + KEY_DOC_ID + " , " + KEY_DOC_TITLE +
                " , " + KEY_DOC_PATH + " from document where title= '" + title+"'";

        DocumentWT documentWT= null;
        Cursor cur = db.rawQuery(query, null);
        if (cur != null &&  cur.moveToFirst()) {
            documentWT= new DocumentWT();
            documentWT.id = cur.getLong(0);
            documentWT.title = cur.getString(1);
            documentWT.path = cur.getString(2);
            cur.close();
        }
        return documentWT;
    }

    public boolean deleteDocumentByTitle(String title) {
        db.delete("document", KEY_DOC_TITLE + "= '" + title+"'", null);
        return true;
    }


    public List<DocumentWT> getDocuments() {
        String query = "select " +
                KEY_DOC_ID + ", " +
                KEY_DOC_TITLE + ", " +
                KEY_DOC_PATH +
                " from document";

        List<DocumentWT> list = new ArrayList<DocumentWT>();
        Cursor cur = db.rawQuery(query, null);
        if (cur != null && cur.moveToFirst()) {
            while (!cur.isAfterLast()) {
                DocumentWT objDocLibr = new DocumentWT();
                objDocLibr.id = cur.getLong(0);
                objDocLibr.title = cur.getString(1);
                objDocLibr.path = cur.getString(2);
                cur.moveToNext();
                list.add(objDocLibr);
            }
            cur.close();
        }
        return list;
    }

}
