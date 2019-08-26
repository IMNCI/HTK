package org.ministryofhealth.healthtalkkit.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.ministryofhealth.healthtalkkit.model.HealthTalkKit;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "imci_health_talk_db";

    public static final String TABLE_HEALTH_TALK_KIT = "health_talk_kit";
    public static final String TABLE_HEALTH_TALK_KIT_SUBCONTENT = "health_talk_kit_subcontent";
    public static final String TABLE_HEALTH_TALK_KIT_GRANDCHILD = "health_talk_kit_grandchild";

    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_IS_PARENT = "is_parent";
    public static final String KEY_ORDER = "order_";

    public static final String KEY_HEALTH_TALK_KIT_ID = "health_talk_kit_id";
    public static final String KEY_SUBCONTENT_ID = "subcontent_id";

    SQLiteDatabase writableDB;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.writableDB = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_HEALTH_TALK_KIT_TABLE = "CREATE TABLE " + TABLE_HEALTH_TALK_KIT + "("
                + KEY_ID + " INTEGER,"
                + KEY_TITLE + " TEXT,"
                + KEY_CONTENT + " TEXT,"
                + KEY_IS_PARENT + " INTEGER,"
                + KEY_ORDER + " INTEGER"
                + ");";
        String CREATE_HEALTH_TALK_KIT_SUBCONTENT_TABLE = "CREATE TABLE " + TABLE_HEALTH_TALK_KIT_SUBCONTENT + "("
                + KEY_ID + " INTEGER,"
                + KEY_HEALTH_TALK_KIT_ID + " INTEGER,"
                + KEY_TITLE + " TEXT,"
                + KEY_CONTENT + " TEXT,"
                + KEY_IS_PARENT + " INTEGER"
                + ");";
        String CREATE_HEALTH_TALK_KIT_GRANDCHILD_TABLE = "CREATE TABLE " + TABLE_HEALTH_TALK_KIT_GRANDCHILD + "("
                + KEY_ID + " INTEGER,"
                + KEY_SUBCONTENT_ID + " INTEGER,"
                + KEY_TITLE + " TEXT,"
                + KEY_CONTENT + " TEXT,"
                + KEY_IS_PARENT + " INTEGER"
                + ");";

        db.execSQL(CREATE_HEALTH_TALK_KIT_TABLE);
        db.execSQL(CREATE_HEALTH_TALK_KIT_SUBCONTENT_TABLE);
        db.execSQL(CREATE_HEALTH_TALK_KIT_GRANDCHILD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HEALTH_TALK_KIT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HEALTH_TALK_KIT_SUBCONTENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HEALTH_TALK_KIT_GRANDCHILD);

        onCreate(db);
    }

    public void clearTables(){
        writableDB.execSQL("DROP TABLE IF EXISTS " + TABLE_HEALTH_TALK_KIT);
        writableDB.execSQL("DROP TABLE IF EXISTS " + TABLE_HEALTH_TALK_KIT_SUBCONTENT);
        writableDB.execSQL("DROP TABLE IF EXISTS " + TABLE_HEALTH_TALK_KIT_GRANDCHILD);

        onCreate(writableDB);
    }

    public void addHealthTalkKits(List<HealthTalkKit> kits){
        for (HealthTalkKit kit:
             kits) {
            addHealthTalkKit(kit);
        }
    }

    public void addHealthTalkKit(HealthTalkKit kit){
        ContentValues value = new ContentValues();

        value.put(KEY_ID, kit.getId());
        value.put(KEY_TITLE, kit.getTitle());
        value.put(KEY_CONTENT, kit.getContent());
        value.put(KEY_IS_PARENT, kit.getIs_parent());
        value.put(KEY_ORDER, kit.getOrder());

        writableDB.insert(TABLE_HEALTH_TALK_KIT, null, value);
    }

    public void addHealthTalkKitSubcontents(List<HealthTalkKit> kits){
        for (HealthTalkKit kit:
                kits) {
            addHealthTalkKitSubContent(kit);
        }
    }

    public void addHealthTalkKitSubContent(HealthTalkKit kit){
        ContentValues value = new ContentValues();

        value.put(KEY_ID, kit.getId());
        value.put(KEY_HEALTH_TALK_KIT_ID, kit.getHealth_talk_kit_id());
        value.put(KEY_TITLE, kit.getTitle());
        value.put(KEY_CONTENT, kit.getContent());
        value.put(KEY_IS_PARENT, kit.getIs_parent());

        writableDB.insert(TABLE_HEALTH_TALK_KIT_SUBCONTENT, null, value);
    }

    public void addHealthTalkKitGrandchildren(List<HealthTalkKit> kits){
        for (HealthTalkKit kit:
                kits) {
            addHealthTalkKitGrandchild(kit);
        }
    }

    public void addHealthTalkKitGrandchild(HealthTalkKit kit){
        ContentValues value = new ContentValues();

        value.put(KEY_ID, kit.getId());
        value.put(KEY_SUBCONTENT_ID, kit.getSubcontent_id());
        value.put(KEY_TITLE, kit.getTitle());
        value.put(KEY_CONTENT, kit.getContent());

        writableDB.insert(TABLE_HEALTH_TALK_KIT_GRANDCHILD, null, value);
    }

    public List<HealthTalkKit> getHealthTalkKits(){
        List<HealthTalkKit> kits = new ArrayList<HealthTalkKit>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HEALTH_TALK_KIT, null, null, null, null, null, KEY_ORDER + " ASC");
        if (cursor.moveToFirst()){
            do{
                HealthTalkKit kit = new HealthTalkKit();

                kit.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                kit.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                kit.setContent(cursor.getString(cursor.getColumnIndex(KEY_CONTENT)));
                kit.setIs_parent(cursor.getInt(cursor.getColumnIndex(KEY_IS_PARENT)));
                kit.setOrder(cursor.getInt(cursor.getColumnIndex(KEY_ORDER)));

                kits.add(kit);
            }while (cursor.moveToNext());
        }
        return kits;
    }

    public List<HealthTalkKit> getHealthTalkKitSubcontent(int health_talk_kit_id){
        List<HealthTalkKit> kits = new ArrayList<HealthTalkKit>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HEALTH_TALK_KIT_SUBCONTENT, null, KEY_HEALTH_TALK_KIT_ID + "=?", new String[]{String.valueOf(health_talk_kit_id)}, null, null, null);
        if (cursor.moveToFirst()){
            do{
                HealthTalkKit kit = new HealthTalkKit();

                kit.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                kit.setHealth_talk_kit_id(cursor.getInt(cursor.getColumnIndex(KEY_HEALTH_TALK_KIT_ID)));
                kit.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                kit.setContent(cursor.getString(cursor.getColumnIndex(KEY_CONTENT)));
                kit.setIs_parent(cursor.getInt(cursor.getColumnIndex(KEY_IS_PARENT)));

                kits.add(kit);
            }while (cursor.moveToNext());
        }
        return kits;
    }

    public List<HealthTalkKit> getHealthTalkKitGrandchildren(int subcontent_id){
        List<HealthTalkKit> kits = new ArrayList<HealthTalkKit>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HEALTH_TALK_KIT_GRANDCHILD, null, KEY_SUBCONTENT_ID + "=?", new String[]{String.valueOf(subcontent_id)}, null, null, null);
        if (cursor.moveToFirst()){
            do{
                HealthTalkKit kit = new HealthTalkKit();

                kit.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                kit.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                kit.setContent(cursor.getString(cursor.getColumnIndex(KEY_CONTENT)));
                kit.setSubcontent_id(cursor.getInt(cursor.getColumnIndex(KEY_SUBCONTENT_ID)));

                kits.add(kit);
            }while (cursor.moveToNext());
        }
        return kits;
    }

    public HealthTalkKit getKit(int id, String type){
        String table = TABLE_HEALTH_TALK_KIT;
        HealthTalkKit kit = new HealthTalkKit();

        switch (type){
            case "kit":
                table = TABLE_HEALTH_TALK_KIT;
                break;
            case "subcontent":
                table = TABLE_HEALTH_TALK_KIT_SUBCONTENT;
                break;
            case "grandchild":
                table = TABLE_HEALTH_TALK_KIT_GRANDCHILD;
                break;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(table, null, KEY_ID+ "=?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToFirst()){
            kit.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            kit.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
            kit.setContent(cursor.getString(cursor.getColumnIndex(KEY_CONTENT)));
            if (!type.equals("grandchild"))
                kit.setIs_parent(cursor.getInt(cursor.getColumnIndex(KEY_IS_PARENT)));
        }

        return kit;
    }
}
