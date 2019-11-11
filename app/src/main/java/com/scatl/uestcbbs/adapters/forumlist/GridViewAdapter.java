package com.scatl.uestcbbs.adapters.forumlist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.activities.BoardActivity;
import com.scatl.uestcbbs.custom.imageview.RoundedImageView;
import com.scatl.uestcbbs.entities.ForumListBean;
import com.scatl.uestcbbs.utils.Constants;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/07/21 17:07
 */
public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private List<ForumListBean.BoardBean.BoardListBean> boardListBeans;

    public GridViewAdapter(Context context, List<ForumListBean.BoardBean.BoardListBean> listBeans) {
        this.context = context;
        this.boardListBeans = listBeans;
    }

    @Override
    public int getCount() {
        return boardListBeans.size();
    }

    @Override
    public Object getItem(int i) {
        return boardListBeans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {

            view = LayoutInflater.from(context).inflate(R.layout.item_item_forum_list_right, new RelativeLayout(context));
            holder = new ViewHolder();

            holder.name = view.findViewById(R.id.forum_list_right_name);
            holder.desc = view.findViewById(R.id.forum_list_right_desc);
            holder.imageView = view.findViewById(R.id.forum_list_right_img);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, BoardActivity.class);
                    intent.putExtra(Constants.Key.DATA, boardListBeans.get(i));
                    context.startActivity(intent);
                }
            });

            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        ForumListBean.BoardBean.BoardListBean boardListBean = boardListBeans.get(i);
        holder.name.setText(boardListBean.board_name);
        holder.desc.setText(context.getResources().getString(R.string.today_posts, boardListBean.td_posts_num));

        String ddd = Constants.Api.BOARD_IMAGE_500_500 + boardListBean.board_id + ".jpg";
        Glide.with(context).load(ddd).into(holder.imageView);
//        Glide.with(context).load(boardListBean.board_img).into(holder.imageView);

        return view;
    }

    public static class ViewHolder {
        TextView name, desc;
        RoundedImageView imageView;
    }
}
