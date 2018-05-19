package com.example.ahmed.sqlitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class SqliteHelper extends SQLiteOpenHelper {
    String medicine, details;
    byte image[];

    public SqliteHelper(Context context) {
        super(context, "mDatabase", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table if not exists medicines(id integer primary key autoincrement, disease varchar, medicine varchar,details varchar , image blob)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists medicines");
        onCreate(sqLiteDatabase);
    }

    public void add(String disease, String medicine, String details, byte[] image) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("disease", disease);
        contentValues.put("medicine", medicine);
        contentValues.put("details", details);
        contentValues.put("image", image);
        database.insert("medicines", null, contentValues);
    }

    public void delete(int id) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("delete from medicines where id=" + id);
    }

    public ArrayList show_all_Data() {
        ArrayList arr = new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cu = db.rawQuery("select * from medicines", null);
        cu.moveToFirst();
        while (cu.isAfterLast() == false) {
            int id = cu.getInt(cu.getColumnIndex("id"));
            String disease = cu.getString(cu.getColumnIndex("disease"));
            String medicine = cu.getString(cu.getColumnIndex("medicine"));
            String details = cu.getString(cu.getColumnIndex("details"));
            byte[] image = cu.getBlob(cu.getColumnIndex("image"));

            arr.add(new List_medicine(id, disease, medicine, details, image));
            cu.moveToNext();
        }
        return arr;
    }

    public ArrayList getDiseaseSearch(String s) {
        ArrayList arr = new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cu = db.rawQuery("select * from medicines", null);
        cu.moveToFirst();
        while (cu.isAfterLast() == false) {
            int id = cu.getInt(cu.getColumnIndex("id"));
            String disease = cu.getString(cu.getColumnIndex("disease"));
            String medicine = cu.getString(cu.getColumnIndex("medicine"));
            String details = cu.getString(cu.getColumnIndex("details"));
            byte[] image = cu.getBlob(cu.getColumnIndex("image"));

            if (disease.contains(s)) {
                arr.add(new List_medicine(id, disease, medicine, details, image));
            }
            cu.moveToNext();
        }
        return arr;
    }

    public void update(String id, String disease, String medicine, String details, byte[] image) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("disease", disease);
        contentValues.put("medicine", medicine);
        contentValues.put("details", details);
        contentValues.put("image", image);
        database.update("medicines", contentValues, "id=?", new String[]{id});
    }

    public Cursor getData(String sql) {
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    public String getItemToUpdate(int id) {
        String disease = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cu = db.rawQuery("select * from medicines where id = " + id, null);
        cu.moveToFirst();
        while (cu.isAfterLast() == false) {
            disease = cu.getString(cu.getColumnIndex("disease"));
            medicine = cu.getString(cu.getColumnIndex("medicine"));
            details = cu.getString(cu.getColumnIndex("details"));
            image = cu.getBlob(cu.getColumnIndex("image"));

            cu.moveToNext();
        }
        return disease;
    }

}
