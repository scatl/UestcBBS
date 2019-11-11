package com.scatl.uestcbbs.custom.postview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.adapters.PollAdapter;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.custom.imageview.RoundedImageView;
import com.scatl.uestcbbs.entities.PostDetailBean;
import com.scatl.uestcbbs.utils.CommonUtil;
import com.scatl.uestcbbs.utils.Constants;
import com.scatl.uestcbbs.utils.FileUtil;
import com.scatl.uestcbbs.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * author: sca_tl
 * description: 0、纯文本  1、图片  5、附件  3、音频  4、链接
 * 网页论坛只允许上传这些格式的文件：flv,mp3,mp4,zip,rar,tar,gz,xz,bz2,7z,apk,ipa,crx,pdf,caj,
 * ppt,pptx,doc,docx,xls,xlsx,txt,png,jpg,jpe,jpeg,gif
 */
public class ContentView extends RelativeLayout {

    private LayoutInflater inflater;
    private LinearLayout root_layout;
    private List<String> imagesUrl = new ArrayList<>(); //所有类型1的url
    private List<String> allImagesUrl = new ArrayList<>(); //类型1和类型5的图片类型的所有url

    private OnClickListener imageClickListener, pollBtnClickListener;
    private OnClickListener playClickListener;

    private OnImageClickListener onImageClickListener;
    private OnPollBtnClickListener onPollBtnClickListener;
    private OnPlayClickListener onPlayClickListener;

    private final int TEXT_SIZE = 16;
    private final String TAG_TEXT_VIEW = "textView";
    private final String TAG_IMAGE_VIEW = "imageView";
    private final String TAG_ATTACHMENT_VIEW = "attachment";

    private JSONObject voteJson;

    PollAdapter pollAdapter;

    public ContentView(Context context) {
        this(context, null);
        init(context);
    }

    public ContentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public ContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    /**
     * author: sca_tl
     * description: 初始化
     */
    private void init(Context context) {
        inflater = LayoutInflater.from(context);

        setOnItemClick();

        root_layout = new LinearLayout(context);
        root_layout.setOrientation(LinearLayout.VERTICAL);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        root_layout.setPadding(0,15,0,15);//设置间距，防止生成图片时文字太靠边
        addView(root_layout, layoutParams);
    }


    /**
     * author: sca_tl
     * description: 清除所有的view
     */
    public void clearAllLayout(){
        root_layout.removeAllViews();
    }

    /**
     * author: sca_tl
     * description: 点击事件
     */
    public void setOnItemClick() {

        //图片点击
        imageClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view instanceof RoundedImageView) {
                    RoundedImageView roundedImageView = (RoundedImageView) view;
                    int selected = allImagesUrl.indexOf(roundedImageView.getAbsolutePath());
                    onImageClickListener.onImageClick(roundedImageView, allImagesUrl, selected);
                }

            }
        };

        //投票按钮点击
        pollBtnClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                onPollBtnClickListener.onPollBtnClick(pollAdapter.getPollItemIds());
            }
        };

        //音乐播放
//        playClickListener = new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onPlayClickListener.onPlayClick();
//            }
//        };
    }

    /**
     * author: sca_tl
     * description:创建一个textview
     */
    private TextView createTextView() {
        TextView textView = new TextView(getContext());
        textView.setTag(TAG_TEXT_VIEW);
        textView.setId(generateViewId());
        textView.setTextIsSelectable(true);
        textView.setPadding(0, 10, 0, 10);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        textView.setLineSpacing(8, 1.2f);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        return textView;
    }

    /**
     * author: sca_tl
     * description: 创建一个textview
     * 注意表情信息放在文本里：[mobcent_phiz=http:\/\/bbs.uestc.edu.cn\/static\/image\/smiley\/bzmh\/bzmh138.gif]
     * 纯文本还可能有编辑信息：本帖最后由 xxx 于 2019-7-1 22:11 编辑
     *
     * @param index  index代表添加的child的view在linearlayout的行数，从0开始
     */
    private void insertTextView(PostDetailBean.PostContentBean postContentBean, int index){
        String text = postContentBean.infor;

        View view = root_layout.getChildAt(root_layout.getChildCount() - 1);
        //在某个view的后面且前一个view是textview(url)
        if (index != 0 && view.getTag().toString().equals(TAG_TEXT_VIEW)) {

            //获取textview
            TextView textView1 = (TextView) view;
            textView1.setTextColor(getResources().getColor(R.color.text_color));

            setTextWithEmotion(textView1, text, true);

        } else {  //是第一个view或者前一个view不是textview

            //判断帖子是否编辑过，编辑信息只有一条
            Matcher matcher = Pattern.compile("(.*?)本帖最后由(.*?)于(.*?)编辑").matcher(text);
            if (matcher.find()){
                String name = matcher.group(2);
                String time = matcher.group(3);

                //编辑信息
                TextView textView = createTextView();
                textView.setText(getContext().getResources().getString(R.string.edit_info, name, time));
                textView.setTextColor(CommonUtil.getAttrColor(getContext(), R.attr.colorPrimary));
                textView.setTextSize(14);
                textView.getPaint().setFlags(Paint.FAKE_BOLD_TEXT_FLAG); //粗体
                textView.getPaint().setAntiAlias(true);//抗锯齿
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER_HORIZONTAL;
                root_layout.addView(textView, params);

                TextView textView1 = createTextView();
                textView1.setTextColor(getResources().getColor(R.color.text_color));
                String s = text.replace(matcher.group(), "");
                setTextWithEmotion(textView1, s.replaceFirst("\n", ""), false);

            } else {
                TextView textView = createTextView();
                textView.setTextColor(getResources().getColor(R.color.text_color));
                setTextWithEmotion(textView, text, false);
            }

        }
    }

    /**
     * author: TanLei
     * description: 处理纯文本里的表情
     * 表情信息格式：[mobcent_phiz=http:\/\/bbs.uestc.edu.cn\/static\/image\/smiley\/bzmh\/bzmh138.gif]
     * 用html展示表情
     */
    private void setTextWithEmotion(final TextView textView, String text, boolean append) {
        final Matcher matcher = Pattern.compile("(\\[mobcent_phiz=(.*?)])").matcher(text);

        if (matcher.find()) {
            do {
                text = text.replace(matcher.group(0)+"", "<img src = " + matcher.group(2) + ">");
            } while (matcher.find());
            if (append) {
                textView.append(Html.fromHtml(text, new MyImageGetter(getContext(), textView), null));
            } else {
                textView.setText(Html.fromHtml(text, new MyImageGetter(getContext(), textView), null));
                root_layout.addView(textView);
            }

        } else {
            if (append) {
                textView.append(text);
            } else {
                textView.setText(text);
                root_layout.addView(textView);
            }

        }

    }


    /**
     * author: sca_tl
     * description: 若是链接，高亮显示，支持点击
     * 如果是论坛的帖子链接，需要直接在应用内跳转到对应的帖子
     */

    private void insertUrlView(PostDetailBean.PostContentBean postContentBean, int index){

        String text = postContentBean.infor;
        String url = postContentBean.url;

        View view = root_layout.getChildAt(root_layout.getChildCount() - 1);

        //在某个view的后面且前一个view是textview
        if (index != 0 && view.getTag().toString().equals(TAG_TEXT_VIEW)) {

            //获取textview
            TextView textView1 = (TextView) view;

            SpannableString spannableString = new SpannableString(text);
            MyClickableSpan clickableSpan = new MyClickableSpan(getContext(), url);
            //URLSpan urlSpan = new URLSpan(url);
            spannableString.setSpan(clickableSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            textView1.append(spannableString);

        } else {  //是第一个view或者前一个view不是textview
            TextView textView = createTextView();

            SpannableString spannableString = new SpannableString(text);
            MyClickableSpan clickableSpan = new MyClickableSpan(getContext(), url);
            spannableString.setSpan(clickableSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            textView.setText(spannableString);

            root_layout.addView(textView);
        }

    }

    /**
     * author: sca_tl
     * description: 创建一个imageview，并且点击图片能够浏览，提供下载功能
     * glide4.0 若into中设置的是target，占位符（placeholder、error）需要在回调中再次设置，否则无效
     */
    //TODO 加个加载失败图片
    private void insertImageView(PostDetailBean.PostContentBean postContentBean, int index) {
        final String img_url = postContentBean.infor;
        allImagesUrl.add(img_url);

        final RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.view_post_content_imageview, new RelativeLayout(getContext()));
        final RoundedImageView imageView = layout.findViewById(R.id.post_detail_custom_imageView);
        final RelativeLayout long_pic_rl = layout.findViewById(R.id.long_pic_hint);//长图提示

        layout.setTag(TAG_IMAGE_VIEW);
        layout.setPadding(50, 10, 50, 10); //图片左右不和文本内容对齐的话可能会好看点?
        imageView.setLayerType(LAYER_TYPE_SOFTWARE, null);  //这句可以避免某些情况滑动图片卡顿
        imageView.setOnClickListener(imageClickListener);         //图片点击事件
        imageView.setAbsolutePath(img_url);
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        Glide.with(getContext())
                .load(img_url)
                .placeholder(R.drawable.img_loading_img)
                //.error()
                .into(new SimpleTarget<Drawable>() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                        //todo 压缩后不清晰
                        if (resource instanceof GifDrawable) {  //动图直接显示
                            ((GifDrawable) resource).start();
                            imageView.setImageDrawable(resource);
                        } else {
                            BitmapDrawable bitmapDrawable = (BitmapDrawable) resource;
                            final Bitmap bitmap = bitmapDrawable.getBitmap();
                            int height = bitmap.getHeight();
                            int width = bitmap.getWidth();

                            if (height / width > 3) {
                                //显示长图提示文字
                                GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, Constants.LONG_PIC_HINT_BACKGROUND[0]);
                                long_pic_rl.setBackground(gradientDrawable);
                                long_pic_rl.setVisibility(VISIBLE);
                            }

                            imageView.setImageBitmap(ImageUtil.bitmapCompress(bitmap));

//                            if (height / width >= 3) {  //若是长图，简单地认为高宽比大于3是长图
////                                Bitmap new_bitmap = Bitmap.createScaledBitmap(bitmap, )
////                                new_bitmap.setHeight(new_bitmap.getWidth() * 2);
////                                imageView.setImageBitmap(new_bitmap);
//
//                                //显示长图提示文字
//                                GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, Constants.LONG_PIC_HINT_BACKGROUND[0]);
//                                long_pic_rl.setBackground(gradientDrawable);
//                                long_pic_rl.setVisibility(VISIBLE);
//
//                            } else {   //不是长图
//
//                                Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, width/2, height/2, true);
//                                imageView.setImageBitmap(bitmap1);
//
//                            }

                        }

                    }

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        if (placeholder != null) imageView.setImageDrawable(placeholder);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        if (errorDrawable != null);
                    }
                });

        root_layout.addView(layout);
    }


    /**
     * author: sca_tl
     * description: 创建一个附件视图，提供附件下载功能
     *
     * infor:附件名  url：附件链接  desc：附件下载次数信息
     * 附件拓展名 flv,mp3,mp4,zip,rar,tar,gz,xz,bz2,7z,apk,ipa,crx,pdf,caj,ppt,pptx,doc,docx,xls,xlsx,txt,png,jpg,jpe,jpeg,gif
     *
     * 注意：附件有可能是图片，需要直接显示，图片拓展名：png,jpg,jpe,jpeg,gif
     * 但是要注意可能之前已显示了相同的图片（类型1）
     * 可以先获取所有图片的url集合，然后将附件url和获取的比对，若集合存在附件url，则该图片附件不显示
     * 否则显示（因为有可能附件是图片，但是服务器没有返回该图片附件的图片类型（1））
     * 先将文件下载再显示，因为通过链接好像不能直接加载出图片
     */
    private void insertAttachmentView(PostDetailBean.PostContentBean postContentBean, int index) {
        String infor = postContentBean.infor;
        final String url = postContentBean.url;
        String desc = postContentBean.desc;

        if (FileUtil.isPicture(infor)) {

            if (! imagesUrl.contains(url)) {
                //TODO 下载文件再显示
            }

        } else {  //不是图片，创建一个附件视图
            RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.view_post_content_attachment, new RelativeLayout(getContext()));
            relativeLayout.setTag(TAG_ATTACHMENT_VIEW);
            ImageView imageView = relativeLayout.findViewById(R.id.post_detail_attachment_img);
            TextView name_textview = relativeLayout.findViewById(R.id.post_detail_attachment_name);
            TextView desc_textview = relativeLayout.findViewById(R.id.post_detail_attachment_desc);
            RelativeLayout relativeLayout1 = relativeLayout.findViewById(R.id.post_detail_attachment_rl);

            name_textview.setText(infor);
            desc_textview.setText(String.valueOf(desc + "，点击下载"));

            relativeLayout1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommonUtil.openBrowser(getContext(), url);
                }
            });

            if (FileUtil.isVideo(infor)) CommonUtil.setVectorColor(getContext(), imageView, R.drawable.ic_video, R.color.colorPrimary);
            if (FileUtil.isAudio(infor)) CommonUtil.setVectorColor(getContext(), imageView, R.drawable.ic_music, R.color.colorPrimary);
            if (FileUtil.isCompressed(infor)) CommonUtil.setVectorColor(getContext(), imageView, R.drawable.ic_compressed, R.color.colorPrimary);
            if (FileUtil.isApplication(infor)) CommonUtil.setVectorColor(getContext(), imageView, R.drawable.ic_app, R.color.colorPrimary);
            if (FileUtil.isPlugIn(infor)) CommonUtil.setVectorColor(getContext(), imageView, R.drawable.ic_plugin, R.color.colorPrimary);
            if (FileUtil.idPdf(infor)) CommonUtil.setVectorColor(getContext(), imageView, R.drawable.ic_pdf, R.color.colorPrimary);
            if (FileUtil.isDocument(infor)) CommonUtil.setVectorColor(getContext(), imageView, R.drawable.ic_document, R.color.colorPrimary);

            if (! TextUtils.isEmpty(infor)) root_layout.addView(relativeLayout);
        }
    }


    /**
     * author: sca_tl
     * description: 创建一个投票视图
     * 一般投票都是放在帖子最后面
     */
    private void insertPollView() {
        if (voteJson != null && voteJson.size() != 0) {

            int voters = voteJson.getIntValue("voters");
            int type = voteJson.getIntValue("type");
            int poll_status = voteJson.getIntValue("poll_status");
            int is_visible = voteJson.getIntValue("is_visible");

            JSONArray jsonArray = voteJson.getJSONArray("poll_item_list");
            if (jsonArray != null) {
                List<PostDetailBean.PostPollBasicBean.PostPollContentBean> poll_item_beans =
                        JSON.parseObject(jsonArray.toString(),
                        new TypeReference<List<PostDetailBean.PostPollBasicBean.PostPollContentBean>>(){});
                RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.view_post_content_poll, new RelativeLayout(getContext()));
                RecyclerView recyclerView = relativeLayout.findViewById(R.id.view_content_poll_rv);
                TextView textView = relativeLayout.findViewById(R.id.view_content_poll_hint);
                TextView total_tv = relativeLayout.findViewById(R.id.view_content_poll_total);
                Button button = relativeLayout.findViewById(R.id.view_content_poll_btn);

                total_tv.setText(getResources().getString(R.string.total_voters, voters));

                if (poll_status == 1) {
                    textView.setText(getResources().getString(R.string.is_voted));
                    button.setVisibility(GONE);
                }
                if (poll_status == 3) {
                    textView.setText(getResources().getString(R.string.no_vote_permission));
                    button.setVisibility(GONE);
                }
                if (poll_status == 4) {
                    textView.setText(getResources().getString(R.string.vote_closed));
                    button.setVisibility(GONE);
                }
                if (poll_status == 2) {
                    textView.setText(getResources().getString(R.string.can_vote, type));
                    button.setOnClickListener(pollBtnClickListener);
                }

                pollAdapter = new PollAdapter(getContext(), R.layout.item_poll);
                recyclerView.setLayoutManager(new MyLinearLayoutManger(getContext()));
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setAdapter(pollAdapter);
                pollAdapter.addPollData(poll_item_beans, voters, poll_status);

                root_layout.addView(relativeLayout);
            }

        }
    }

    public JSONObject getVoteJson() {
        return voteJson;
    }

    public void setVoteJson(JSONObject voteJson) {
        this.voteJson = voteJson;
    }

    /**
     * author: sca_tl
     * description: 创建一个音频视图
     * infor:音频链接
     */
    private void insertAudioView(final PostDetailBean.PostContentBean postContentBean, int index) {
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.view_post_content_media, new RelativeLayout(getContext()));
        final ImageView play = relativeLayout.findViewById(R.id.view_content_media_play);
        CommonUtil.setVectorColor(getContext(), play, R.drawable.ic_play, R.color.colorPrimary);
        play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onPlayClickListener.onPlayClick(postContentBean.infor);
            }
        });
        root_layout.addView(relativeLayout);
    }

    /**
     * author: sca_tl
     * description: 获取所有类型1（图片）的url
     */
    private List<String> getImagesUrl(List<PostDetailBean.PostContentBean> postContentBeanList){
        List<String> list = new ArrayList<>();
        for (int i = 0; i < postContentBeanList.size(); i ++) {
            if (postContentBeanList.get(i).type == 1) {
                list.add(postContentBeanList.get(i).url);
            }
        }
        return list;
    }

    /**
     * author: sca_tl
     * description: 加载数据
     */
    public void setContentData(final List<PostDetailBean.PostContentBean> postContentBeanList) {

        clearAllLayout();
        imagesUrl = getImagesUrl(postContentBeanList);

        Observable.create(new ObservableOnSubscribe<PostDetailBean.PostContentBean>() {
            @Override
            public void subscribe(ObservableEmitter<PostDetailBean.PostContentBean> emitter) {

                try {
                    for (int i = 0; i < postContentBeanList.size() + 1; i++) {
                        if (i < postContentBeanList.size()) {
                            PostDetailBean.PostContentBean postContentBean = postContentBeanList.get(i);
                            emitter.onNext(postContentBean);
                        } else {
                            emitter.onComplete();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    emitter.onError(e);
                }
            }
        })  .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<PostDetailBean.PostContentBean>() {
                @Override
                public void onComplete() {
                    insertPollView();
                }

                @Override
                public void onError(Throwable e) { }

                @Override
                public void onSubscribe(Disposable d) { }

                @Override
                public void onNext(PostDetailBean.PostContentBean postContentBean) {
                    try {

                        //若是文本
                        if (postContentBean.type == 0) {
                            insertTextView(postContentBean, postContentBeanList.indexOf(postContentBean));
                        }

                        //若是图片
                        if (postContentBean.type == 1) {
                            insertImageView(postContentBean, postContentBeanList.indexOf(postContentBean));
                        }

                        //若是链接
                        if (postContentBean.type == 4) {
                            insertUrlView(postContentBean, postContentBeanList.indexOf(postContentBean));
                        }

                        //若是附件
                        if (postContentBean.type == 5) {
                            insertAttachmentView(postContentBean, postContentBeanList.indexOf(postContentBean));
                        }

                        //若是音频
                        if (postContentBean.type == 3) {
                            insertAudioView(postContentBean, postContentBeanList.indexOf(postContentBean));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
    }


    public interface OnImageClickListener {
        void onImageClick(View view, List<String> urls, int selected);
    }

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }

    public interface OnPollBtnClickListener {
        void onPollBtnClick(List<Integer> ids);
    }

    public void setOnPollBtnClickListener(OnPollBtnClickListener onPollBtnClickListener) {
        this.onPollBtnClickListener = onPollBtnClickListener;
    }

    public interface OnPlayClickListener {
        void onPlayClick(String url);
    }

    public void setOnPlayClickListener (OnPlayClickListener onPlayClickListener) {
        this.onPlayClickListener = onPlayClickListener;
    }

}
