package com.scatl.uestcbbs.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.scatl.uestcbbs.entities.PostDraftBean;

public class PostDraftDataBase extends SQLiteOpenHelper {
    public static final int DATA_BASE_VERSION = 1;
    public static final String DATA_BASE_NAME = "postdraft.db";

    private static final String CREATE_TABLE_POST_DRAFT = "create table postdraft ("
            + "id integer primary key autoincrement, "
            + "time text, "
            + "title text, "
            + "content text, "
            + "board_id text, "
            + "board_name text, "
            + "cat_id text, "
            + "cat_name text)";

    public PostDraftDataBase(@Nullable Context context, @Nullable String name,
                            @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_POST_DRAFT);

        onUpgrade(sqLiteDatabase, 1, DATA_BASE_VERSION);
    }

    /**
     * author: sca_tl
     * description: 保存编辑数据到数据库
     */
    public static void saveEditorData(Context context, PostDraftBean post_draft) {
        PostDraftDataBase postDraftDataBase = new PostDraftDataBase(context,
                PostDraftDataBase.DATA_BASE_NAME, null, PostDraftDataBase.DATA_BASE_VERSION);
        SQLiteDatabase database = postDraftDataBase.getWritableDatabase();


        Cursor cursor = database.rawQuery("select * from postdraft where time = ?",
                new String[]{String.valueOf(post_draft.time)});

        ContentValues values = new ContentValues();
        values.put("time", post_draft.time);
        values.put("title", post_draft.title);
        values.put("content", post_draft.content);
        values.put("board_id", post_draft.board_id);
        values.put("board_name", post_draft.board_name);
        values.put("cat_id", post_draft.cat_id);
        values.put("cat_name", post_draft.cat_name);

        if (cursor.moveToFirst()) {
            database.update("postdraft", values, "time = ?",
                    new String[]{String.valueOf(post_draft.time)});
        } else {
            database.insert("postdraft", null, values);
        }

        values.clear();
        cursor.close();
    }

    /**
     * author: sca_tl
     * description: 从数据库删除草稿数据
     */
    public static void deleteEditorData(Context context, long time) {
        PostDraftDataBase postDraftDataBase = new PostDraftDataBase(context,
                PostDraftDataBase.DATA_BASE_NAME, null, PostDraftDataBase.DATA_BASE_VERSION);
        SQLiteDatabase database = postDraftDataBase.getWritableDatabase();
        Cursor cursor = database.rawQuery("select * from postdraft where time = ?", new String[]{String.valueOf(time)});
        if (cursor.moveToFirst()){
            database.execSQL("delete from postdraft where time = ?", new String[]{String.valueOf(time)});
        }
        cursor.close();
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        for (int j = i; j < i1; j++){
            switch (j){
                case 1:
                    upgradeToVersion2(sqLiteDatabase);
                    break;

                case 2:
                    upgradeToVersion3(sqLiteDatabase);
                    break;

                default:
                    break;
            }
        }
    }

    private void upgradeToVersion2(SQLiteDatabase sqLiteDatabase){

    }

    private void upgradeToVersion3(SQLiteDatabase sqLiteDatabase){

    }
}
