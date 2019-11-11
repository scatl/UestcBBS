package com.scatl.uestcbbs.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.scatl.uestcbbs.entities.PostListBean;

import java.util.ArrayList;
import java.util.List;

public class PostListDataBase extends SQLiteOpenHelper {

    public static final int DATA_BASE_VERSION = 1;
    public static final String DATA_BASE_NAME = "postlist.db";

    public static final String TYPE_SIMPLE = "simple";
    public static final String TYPE_HOT = "hot";
    public static final String TYPE_LATEST_REPLY = "latest_reply";
    public static final String TYPE_LATEST_POST = "latest_post";

    private static final String CREATE_TABLE_POST_LIST = "create table postlist ("
            + "id integer primary key autoincrement, "
            + "type text, "
            + "user_nick_name text, "
            + "title text, "
            + "subject text, "
            + "last_reply_date text, "
            + "replies text, "
            + "hits text, "
            + "board_name text, "
            + "user_avatar text, "
            + "topic_id text, "
            + "user_id text)";

    public PostListDataBase(@Nullable Context context, @Nullable String name,
                            @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_POST_LIST);

        onUpgrade(sqLiteDatabase, 1, DATA_BASE_VERSION);
    }

    /**
     * author: TanLei
     * description: 保存部分帖子数据以便下次进入软件使用
     */
    public static void savePostData(Context context, String type, List<PostListBean> postListBeans) {
        PostListDataBase  postListDataBase = new PostListDataBase(context, DATA_BASE_NAME, null, DATA_BASE_VERSION);
        SQLiteDatabase database = postListDataBase.getWritableDatabase();

        Cursor cursor = database.rawQuery("select * from postlist", new String[]{});

        if(cursor.moveToFirst()) database.execSQL("delete from postlist where type = ?", new String[]{type});

        for (int i = 0; i < (postListBeans.size() >= 10 ? 10 : postListBeans.size()); i ++) {
            ContentValues values = new ContentValues();
            values.put("type", type);
            values.put("user_nick_name", postListBeans.get(i).user_nick_name);
            values.put("title", postListBeans.get(i).title);
            values.put("subject", postListBeans.get(i).subject);
            values.put("last_reply_date", postListBeans.get(i).last_reply_date);
            values.put("replies", postListBeans.get(i).replies);
            values.put("board_name", postListBeans.get(i).board_name);
            values.put("user_avatar", postListBeans.get(i).user_avatar);
            values.put("topic_id", postListBeans.get(i).topic_id);
            values.put("user_id", postListBeans.get(i).user_id);
            values.put("hits", postListBeans.get(i).hits);

            database.insert("postlist", null, values);
            values.clear();
        }

        cursor.close();
    }

    public static List<PostListBean> getPostData(Context context, String type) {
        PostListDataBase  postListDataBase = new PostListDataBase(context, DATA_BASE_NAME, null, DATA_BASE_VERSION);
        SQLiteDatabase database = postListDataBase.getWritableDatabase();

        List<PostListBean> postListBeans = new ArrayList<>();

        Cursor cursor = database.rawQuery("select * from postlist where type = ?", new String[]{type});
        if (cursor.moveToFirst()) {
            do {
                PostListBean postListBean = new PostListBean();
                postListBean.user_nick_name = cursor.getString(cursor.getColumnIndex("user_nick_name"));
                postListBean.title = cursor.getString(cursor.getColumnIndex("title"));
                postListBean.subject = cursor.getString(cursor.getColumnIndex("subject"));
                postListBean.last_reply_date = cursor.getLong(cursor.getColumnIndex("last_reply_date"));
                postListBean.replies = cursor.getInt(cursor.getColumnIndex("replies"));
                postListBean.board_name = cursor.getString(cursor.getColumnIndex("board_name"));
                postListBean.user_avatar = cursor.getString(cursor.getColumnIndex("user_avatar"));
                postListBean.topic_id = cursor.getInt(cursor.getColumnIndex("topic_id"));
                postListBean.user_id = cursor.getInt(cursor.getColumnIndex("user_id"));
                postListBean.hits = cursor.getInt(cursor.getColumnIndex("hits"));

                postListBeans.add(postListBean);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return postListBeans;
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

