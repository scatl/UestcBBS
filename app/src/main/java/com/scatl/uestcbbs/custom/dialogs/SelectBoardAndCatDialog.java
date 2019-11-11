package com.scatl.uestcbbs.custom.dialogs;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseDialogFragment;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.entities.ForumListBean;
import com.scatl.uestcbbs.interfaces.OnHttpRequest;
import com.scatl.uestcbbs.utils.CommonUtil;
import com.scatl.uestcbbs.utils.Constants;
import com.scatl.uestcbbs.utils.SharePrefUtil;
import com.scatl.uestcbbs.utils.httprequest.HttpRequestUtil;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class SelectBoardAndCatDialog extends BaseDialogFragment {

    private TagFlowLayout layout1, layout2, layout3, layout4;
    private TextView sub_board_loading, cat_loading;

    private int current_cat_select;
    private int current_board_select;

    //所有的子版块，包括母版块
    private List<ForumListBean.BoardBean.BoardListBean> subBoardList = new ArrayList<>();
    //板块所有分类
    private List<ForumListBean.BoardBean.BoardListBean.BoardCatBean> boardCatBeanList = new ArrayList<>();

    @Override
    protected int setLayoutResourceId() {
        return R.layout.dialog_select_board_and_cat;
    }

    @Override
    protected void init() {
        layout1 = view.findViewById(R.id.dialog_select_board_and_cat_tag_layout1);
        layout2 = view.findViewById(R.id.dialog_select_board_and_cat_tag_layout2);
        layout3 = view.findViewById(R.id.dialog_select_board_and_cat_tag_layout3);
        layout4 = view.findViewById(R.id.dialog_select_board_and_cat_tag_layout4);
        cat_loading = view.findViewById(R.id.text20);
        sub_board_loading = view.findViewById(R.id.text21);

        getForumListData();
    }

    /**
     * author: sca_tl
     * description: 获取总的板块分类数据
     */
    private void getForumListData() {
        Map<String, String> map = new HashMap<>();
        map.put("accessToken", SharePrefUtil.getAccessToken(mActivity));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(mActivity));
        map.put("apphash", CommonUtil.getAppHashValue());

        HttpRequestUtil.post(Constants.Api.GET_FORUM_LIST, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) { }

            @Override
            public void onRequestInProgress(float progress, long total, int id) { }

            @Override
            public void onRequestSuccess(String response, int id) {
                JSONObject jsonObject = JSONObject.parseObject(response);
                int rs = jsonObject.getIntValue("rs"); //通常 1 表示成功，0 表示失败。

                if (rs == 1) {

                    List<ForumListBean.BoardBean> boardBeans = new ArrayList<>();

                    JSONArray jsonArray = jsonObject.getJSONArray("list");

                    for (int i = 0; i < jsonArray.size(); i ++) {
                        ForumListBean.BoardBean boardBean = new ForumListBean.BoardBean();

                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        JSONArray jsonArray1 = jsonObject1.getJSONArray("board_list");
                        List<ForumListBean.BoardBean.BoardListBean> boardListBean =
                                JSON.parseObject(jsonArray1.toString(),
                                        new TypeReference<List<ForumListBean.BoardBean.BoardListBean>>(){});
                        boardBean.boardListBeans.addAll(boardListBean);
                        boardBean.board_category_name = jsonObject1.getString("board_category_name");
                        boardBeans.add(boardBean);
                    }

                    setMainBoardData(boardBeans);

                }
            }
        });
    }


    /**
     * author: sca_tl
     * description: 设置论坛总的板块分类数据
     */
    private void setMainBoardData(final List<ForumListBean.BoardBean> boardBeans) {
        List<String> item = new ArrayList<>();
        for (int i = 0; i < boardBeans.size(); i ++) {
            item.add(boardBeans.get(i).board_category_name);
        }

        final TagAdapter<String> tagAdapter = new TagAdapter<String>(item) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView textView = new TextView(mActivity);
                textView.setClickable(true);
                textView.setFocusable(true);
                textView.setTextSize(14);
                textView.setText(s);
                textView.setTextColor(getResources().getColor(R.color.colorPrimary));
                textView.setBackgroundResource(R.drawable.shape_item_dialog_board_cat);
                return textView;
            }

            @Override
            public void onSelected(int position, View view) {
                super.onSelected(position, view);
                ((TextView)view).setTextColor(Color.WHITE);
            }

            @Override
            public void unSelected(int position, View view) {
                super.unSelected(position, view);
                ((TextView)view).setTextColor(getResources().getColor(R.color.colorPrimary));
            }

        };

        layout1.setAdapter(tagAdapter);
        layout1.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                layout2.setVisibility(View.GONE);
                layout3.setVisibility(View.GONE);
                layout4.setVisibility(View.GONE);
                setSuperBoardData(boardBeans.get(position).boardListBeans);
                return true;
            }
        });
    }

    /**
     * author: sca_tl
     * description: 设置母版块数据
     */
    private void setSuperBoardData(final List<ForumListBean.BoardBean.BoardListBean> boardListBeans) {
        layout2.setVisibility(View.VISIBLE);
        layout3.setVisibility(View.GONE);
        layout4.setVisibility(View.GONE);
        List<String> item = new ArrayList<>();
        for (int i = 0; i < boardListBeans.size(); i ++) {
            item.add(boardListBeans.get(i).board_name);
        }

        final TagAdapter<String> tagAdapter = new TagAdapter<String>(item) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView textView = new TextView(mActivity);
                textView.setClickable(true);
                textView.setFocusable(true);
                textView.setTextSize(14);
                textView.setText(s);
                textView.setTextColor(getResources().getColor(R.color.colorPrimary));
                textView.setBackgroundResource(R.drawable.shape_item_dialog_board_cat);
                return textView;
            }

            @Override
            public void onSelected(int position, View view) {
                super.onSelected(position, view);
                ((TextView)view).setTextColor(Color.WHITE);
            }

            @Override
            public void unSelected(int position, View view) {
                super.unSelected(position, view);
                ((TextView)view).setTextColor(getResources().getColor(R.color.colorPrimary));
            }

        };

        layout2.setAdapter(tagAdapter);
        layout2.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                getSubBoardData(boardListBeans.get(position));
                return true;
            }
        });
    }

    /**
     * author: sca_tl
     * description: 获取母板块下的所有子版块
     */
    private void getSubBoardData(final ForumListBean.BoardBean.BoardListBean bean) {
        Map<String, String> map = new HashMap<>();
        map.put("fid", bean.board_id + "");
        map.put("accessToken", SharePrefUtil.getAccessToken(mActivity));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(mActivity));
        map.put("apphash", CommonUtil.getAppHashValue());

        HttpRequestUtil.post(Constants.Api.GET_FORUM_LIST, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                sub_board_loading.setText("（加载子版块失败）");
                subBoardList.clear();
                ForumListBean.BoardBean.BoardListBean all = new ForumListBean.BoardBean.BoardListBean();
                all.board_name = bean.board_name;
                all.board_id = bean.board_id;
                subBoardList.add(all);
                setSubBoardData(subBoardList);
            }

            @Override
            public void onRequestInProgress(float progress, long total, int id) {
                sub_board_loading.setText("（正在加载子版块...）");
            }

            @Override
            public void onRequestSuccess(String response, int id) {
                sub_board_loading.setText("");
                JSONObject jsonObject = JSONObject.parseObject(response);
                int rs = jsonObject.getIntValue("rs"); //通常 1 表示成功，0 表示失败。
                if (rs == 1) {
                    JSONArray jsonArray = jsonObject.getJSONArray("list");
                    if (jsonArray != null && jsonArray.size() == 1) {
                        subBoardList.clear();
                        JSONArray jsonArray1 = jsonArray.getJSONObject(0).getJSONArray("board_list");
                        if (jsonArray1 != null) {
                            ForumListBean.BoardBean.BoardListBean a = new ForumListBean.BoardBean.BoardListBean();
                            a.board_id = bean.board_id;
                            a.board_name = bean.board_name;
                            subBoardList.add(a);
                            List<ForumListBean.BoardBean.BoardListBean> listBeans =
                                    JSON.parseObject(jsonArray1.toString(),
                                            new TypeReference<List<ForumListBean.BoardBean.BoardListBean>>(){});
                            subBoardList.addAll(listBeans);
                        }
                        setSubBoardData(subBoardList);
                    } else if (jsonArray != null && jsonArray.size() == 0) {
                        subBoardList.clear();
                        ForumListBean.BoardBean.BoardListBean all = new ForumListBean.BoardBean.BoardListBean();
                        all.board_name = bean.board_name;
                        all.board_id = bean.board_id;
                        subBoardList.add(all);
                        setSubBoardData(subBoardList);
                    }
                }
            }
        });
    }



    /**
     * author: sca_tl
     * description: 设置板块数据
     */
    private void setSubBoardData(final List<ForumListBean.BoardBean.BoardListBean> subBoardList) {
        layout3.setVisibility(View.VISIBLE);
        layout4.setVisibility(View.GONE);
        List<String> item = new ArrayList<>();
        for (int i = 0; i < subBoardList.size(); i ++) {
            item.add(subBoardList.get(i).board_name);
        }

        final TagAdapter<String> tagAdapter = new TagAdapter<String>(item) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView textView = new TextView(mActivity);
                textView.setClickable(true);
                textView.setFocusable(true);
                textView.setTextSize(14);
                textView.setText(s);
                textView.setTextColor(getResources().getColor(R.color.colorPrimary));
                textView.setBackgroundResource(R.drawable.shape_item_dialog_board_cat);
                return textView;
            }

            @Override
            public void onSelected(int position, View view) {
                super.onSelected(position, view);
                ((TextView)view).setTextColor(Color.WHITE);
            }

            @Override
            public void unSelected(int position, View view) {
                super.unSelected(position, view);
                ((TextView)view).setTextColor(getResources().getColor(R.color.colorPrimary));
            }

        };

        layout3.setAdapter(tagAdapter);
        layout3.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                current_board_select = position;
                getCatData(subBoardList.get(position).board_id);
                return true;
            }
        });
    }

    /**
     * author: TanLei
     * description: 获取板块的所有分类信息
     */
    private void getCatData(int board_id) {
        Map<String, String> map = new HashMap<>();
        map.put("page", "1");
        map.put("pageSize", "1");
        map.put("boardId", board_id + "");
        map.put("accessToken", SharePrefUtil.getAccessToken(mActivity));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(mActivity));
        map.put("apphash", CommonUtil.getAppHashValue());

        HttpRequestUtil.post(Constants.Api.GET_TOPIC_LIST, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                cat_loading.setText("（加载分类失败）");
                boardCatBeanList.clear();
                ForumListBean.BoardBean.BoardListBean.BoardCatBean all = new ForumListBean.BoardBean.BoardListBean.BoardCatBean();
                all.classificationType_name = "不选择分类";
                all.classificationType_id = 0;
                boardCatBeanList.add(all);
                setCatData();
            }

            @Override
            public void onRequestInProgress(float progress, long total, int id) {
                cat_loading.setText("（正在加载分类...）");
            }

            @Override
            public void onRequestSuccess(String response, int id) {
                cat_loading.setText("");
                JSONObject jsonObject = JSONObject.parseObject(response);

                //获取该板块下的所有分类
                JSONArray jsonArray1 = jsonObject.getJSONArray("classificationType_list");

                boardCatBeanList.clear();
                ForumListBean.BoardBean.BoardListBean.BoardCatBean all = new ForumListBean.BoardBean.BoardListBean.BoardCatBean();
                all.classificationType_name = "不选择分类";
                all.classificationType_id = 0;
                boardCatBeanList.add(all);
                if (jsonArray1 != null) {
                    for (int j = 0; j < jsonArray1.size(); j ++) {
                        ForumListBean.BoardBean.BoardListBean.BoardCatBean boardCatBean = new ForumListBean.BoardBean.BoardListBean.BoardCatBean();
                        boardCatBean.classificationType_id = jsonArray1.getJSONObject(j).getIntValue("classificationType_id");
                        boardCatBean.classificationType_name = jsonArray1.getJSONObject(j).getString("classificationType_name");
                        boardCatBeanList.add(boardCatBean);
                    }
                }
                setCatData();
            }
        });
    }


    private void setCatData() {
        layout4.setVisibility(View.VISIBLE);
        List<String> item = new ArrayList<>();
        for (int i = 0; i < boardCatBeanList.size(); i ++) {
            item.add(boardCatBeanList.get(i).classificationType_name);
        }

        final TagAdapter<String> tagAdapter = new TagAdapter<String>(item) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView textView = new TextView(mActivity);
                textView.setClickable(true);
                textView.setFocusable(true);
                textView.setTextSize(14);
                textView.setText(s);
                textView.setTextColor(getResources().getColor(R.color.colorPrimary));
                textView.setBackgroundResource(R.drawable.shape_item_dialog_board_cat);
                return textView;
            }

            @Override
            public void onSelected(int position, View view) {
                super.onSelected(position, view);
                ((TextView)view).setTextColor(Color.WHITE);
            }

            @Override
            public void unSelected(int position, View view) {
                super.unSelected(position, view);
                ((TextView)view).setTextColor(getResources().getColor(R.color.colorPrimary));
            }

        };

        layout4.setAdapter(tagAdapter);
        layout4.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                current_cat_select = position;

                BaseEvent.BoardSelected boardSelected = new BaseEvent.BoardSelected();
                boardSelected.board_name = subBoardList.get(current_board_select).board_name;
                boardSelected.board_id = subBoardList.get(current_board_select).board_id;
                boardSelected.cat_name = boardCatBeanList.get(current_cat_select).classificationType_name;
                boardSelected.cat_id = boardCatBeanList.get(current_cat_select).classificationType_id;
                EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.SELECTED_BOARD, boardSelected));

                dismiss();
                return true;
            }
        });
    }


}
