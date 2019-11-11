package com.scatl.uestcbbs.utils;

public class Constants {

    public class Api {

        //每日一图
        public static final String DAILY_PIC = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";

        //sdkVersion API 版本，目前为 2.4.0.2，大部分情况下有没有这个参数以及参数值是什么都没有影响
        public static final String SDKVERSION = "2.6.1.7";

        //forumKey  用于 mobcent 统计，区分不同论坛，清水河畔该参数的值为 CBQJazn9Wws8Ivhr6U。
        public static final String FORUMKEY = "CBQJazn9Wws8Ivhr6U";

        //platType  Android 客户端为 1，iOS 客户端为 5。
        public static final String PLATTYPE = "1";

        public static final String TYPE_HOT_POST = "hot_post";
        public static final String TYPE_LATEST_POST = "latest_post";
        public static final String TYPE_LATEST_REPLY = "latest_reply";


        private static final String BBS_API_URL = "http://bbs.uestc.edu.cn/mobcent/app/web/index.php?r=";

        //=====================================================用户相关==============================================//

        //登陆
        //请求参数：username, password
        public static final String LOGIN_URL = BBS_API_URL + "user/login";

        public static final String REGISTER_URL = "http://bbs.uestc.edu.cn/member.php?mod=register";

        //关注用户
        //type=follow/unfollow  uid=关注用户的uid
        public static final String FOLLOW_USER = BBS_API_URL + "user/useradmin";

        //收藏帖子
        //idType=tid, action=favorite/delfavorite  id=帖子id
        public static final String SET_POST_FAVORITE = BBS_API_URL + "user/userfavorite";

        //用户详情
        //userId=用户id
        public static final String GET_USER_INFO = BBS_API_URL + "user/userinfo";

        //用户帖子
        //uid=用户id, type=reply/topic/favorite
        public static final String GET_USER_POST = BBS_API_URL + "user/topiclist";

        //获取粉丝和关注列表
        //page ,pageSize,orderBy=dateline,type=followed/follow,uid,
        public static final String GET_FOLLOW_LIST = BBS_API_URL + "user/userlist";

        //拉黑用户
        //type=black/delblack,  uid
        public static final String BLACK_USER = BBS_API_URL + "user/useradmin";

        //获取可以at的好友列表，
        //返回值中role_num=6关注的用户，role_num=2好友
        public static final String GET_ICANAT_LIST = BBS_API_URL + "forum/atuserlist";

        //举报用户
        //idType=user举报用户/post举报用户的回复/thread举报用户发的帖子, message, id=相关id
        public static final String REPORT_TYPE_USER = "user";
        public static final String REPORT_TYPE_POST = "post";
        public static final String REPORT_TYPE_THREAD = "thread";
        public static final String REPORT_USER = BBS_API_URL + "user/report";

        //搜索用户
        //keyword=用户名, page,pageSize,searchid=0
        public static final String SEARCH_USER = BBS_API_URL + "user/searchuser";

        //=======================================================帖子相关======================================================//

        //帖子链接
        public static final String TOPIC_URL = "http://bbs.uestc.edu.cn/forum.php?mod=viewthread&tid=";
        //板块链接
        public static final String BOARD_URL = "http://bbs.uestc.edu.cn/forum.php?mod=forumdisplay&fid=";

        //热门帖子：服务器只返回十条记录,帖子时间是发表时间
        //请求参数：page = 1，pageSize = 10，moduleId = 2
        //circle=1可以返回帖子部分回复内容
        public static final String HOT_POST_URL = BBS_API_URL + "portal/newslist";

        //最新发表/回复，服务器返回的帖子列表里只有最近回复时间，并没有帖子发表时间
        //请求参数：page = 1，pageSize = 10，boardId = 0，sortby = new（最新发表）/ all（最新回复）
        //circle=1可以返回帖子部分回复内容
        public static final String LATEST_POST_URL = BBS_API_URL + "forum/topiclist";
        public static final String LATEST_REPLY_URL = BBS_API_URL + "forum/topiclist";

        //获取某一版块的主题列表
        //boardId ,page, pageSize, sortby=new最新，essence精华，all全部,
        //filterType=typeid   filterId 分类 ID，只返回指定分类的主题，每个子版块下都有不同的分类
        //topOrder 0（不返回置顶帖，默认）, 1（返回本版置顶帖）, 2（返回分类置顶帖）, 3（返回全局置顶帖）。置顶帖包含在 topTopicList 字段中。
        public static final String TOPIC_LIST_SORTBY_NEW = "new";
        public static final String TOPIC_LIST_SORTBY_ALL = "all";
        public static final String TOPIC_LIST_SORTBY_ESSENSE = "essence";
        public static final String GET_TOPIC_LIST = BBS_API_URL + "forum/topiclist";

        //帖子详情
        //topicId,authorId 只返回指定作者的回复，默认为 0 返回所有回复。
        //order 0 或 1（回帖倒序排列）page,pageSize
        public static final String GET_POST_DETAIL = BBS_API_URL + "forum/postlist";


        //板块列表
        //fid 板块ID（可选，加上就是返回该板块下的子版块，否则返回全部板块信息）
        public static final String GET_FORUM_LIST = BBS_API_URL + "forum/forumlist";

        //搜索帖子
        //keyword, page, pageSize
        public static final String SEARCH_POST = BBS_API_URL + "forum/search";


        //投票
        //"poll_info": {
        //            "deadline": "0",
        //            "is_visible": 1,  //投票结果是否投票后可见，但是不管发起投票时有没有勾选投票后可见，都会返回具体的投票人数
        //            "voters": 40,
        //            "type": 3,  最多可选几项
        //            "poll_status": 1, 1：已投 2：可以投票 3：没有权限 4：投票已结束
        //            "poll_id": [
        //                0
        //            ],
        //            "poll_item_list": [
        //                {
        //                    "name": xxx,  选项标题
        //                    "poll_item_id": xxx, 选项id
        //                    "total_num": 12,
        //                    "percent": "29.27%"
        //                }, ...
        //
        //            ]
        //        }
        //tid=帖子id,options=xxx,xxx,xxx(xxx为标题id),boardId,
        //返回
        //{
        //	"rs": 1,
        //	"errcode": "投票成功",
        //   ...
        //	"vote_rs": [{
        //		"name": "我觉得校花是个好人",
        //		"pollItemId": 16572,
        //		"totalNum": 12
        //	}, {
        //		"name": "说话不中听，但人是个好人",
        //		"pollItemId": 16573,
        //		"totalNum": 12
        //	}, {
        //		"name": "写的东西看不懂，但是我支持校花",
        //		"pollItemId": 16574,
        //		"totalNum": 17
        //	}]
        //}
        public static final String VOTE = BBS_API_URL + "forum/vote";



        //=============================================消息相关===========================================
        //获取短消息会话列表
        //json={page:xx,pageSize:xx}
        public static final String GET_PRIVATE_MSG_LIST = BBS_API_URL + "message/pmsessionlist";

        //获取系统通知
        //page, pageSize, type=system
        public static final String GET_SYSTEM_NOTIFICATION = BBS_API_URL + "message/notifylistex";

        //获取回复我的内容
        //page,pageSize,type=post
        public static final String GET_REPLY_ME_MESSAGE = BBS_API_URL + "message/notifylistex";

        //获取at我的消息
        //page,pageSize,type=at
        public static final String GET_AT_ME_MESSAGE = BBS_API_URL + "message/notifylistex";


        //pmlist={
        //  "body": {
        //    "externInfo": {
        //      "onlyFromUid": 0, // 只返回收到的消息（不包括自己发出去的消息）。
        //    },
        //    "pmInfos": [{
        //      "startTime": , // 开始时间（以毫秒为单位）。startTime 和 stopTime 均为 0 表示获取最新（未读）消息，如果要获取历史消息指定一个较早的时间。
        //      "stopTime": , // 结束时间（以毫秒为单位），为零表示获取晚于 startTime 的所有消息。
        //      "cacheCount": 1,
        //      "fromUid": 123, // UID，必须指定。
        //      "pmLimit": 10, // 最多返回几条结果，默认为 15。
        //    }]
        //  }
        //} 获取与用户的私信内容
        public static final String GET_PRIVATE_CHAT_MSG_LIST = BBS_API_URL + "message/pmlist";

        //发送私信
        //json={
        //	"action": "send",
        //	"msg": {
        //		"content": "nnn",
        //		"type": "text"
        //	},
        //	"plid": 0,
        //	"pmid": 0,
        //	"toUid": xxxxxx
        //}
        public static final String SEND_PRIVATE_MSG = BBS_API_URL + "message/pmadmin";


        //获取消息提醒
        //        "body": {
        //            "externInfo": {
        //                "padding": "",
        //                        "heartPeriod": "120000",
        //                        "pmPeriod": "20000"
        //            },
        //            "replyInfo": {  回复
        //                "count": 0,
        //                 "time": "0"
        //            },
        //            "atMeInfo": {   at我的
        //                "count": 0,
        //                 "time": "0"
        //            },
        //            "pmInfos": [  私信
        //            {
        //                "fromUid": 227267,
        //                "plid": 4036801,
        //                "pmid": 4036801,
        //                "time": "1565006051000"
        //            }
        //           ],
        //            "friendInfo": {
        //                "count": 0,
        //                "time": "0"
        //            }
        //        }
        public static final String GET_HEART_MSG = BBS_API_URL + "message/heart";


        //===============================================发帖相关===========================

        //上传图片
        //type=image/audio,module=pm(私信图片)/forum(帖子图片)/album
        public static final String UPLOAD_IMG = BBS_API_URL + "forum/sendattachmentex";

        //发送帖子/回复
        //act=new发帖/reply回复（回复他人，回复作者）/其他字符串（编辑）
        //{
        //	"body": {
        //		"json": {
        //			"fid": xx, // 发帖时的版块。
        //			"tid": xxxxxx,  // 回复时的帖子ID。
        //			"location": "",
        //			"aid": "1950445,1950446", //附件id
        //			"content": "[{\"type\":0,\"infor\":\"1100\"},{\"type\":1,\"infor\":\"http:\\\/\\\/bbs.uestc.edu.cn\\\/data\\\/attachment\\\/\\\/forum\\\/201908\\\/17\\\/175651c59732z7jwi78zz3.jpg\"},{\"type\":1,\"infor\":\"http:\\\/\\\/bbs.uestc.edu.cn\\\/data\\\/attachment\\\/\\\/forum\\\/201908\\\/17\\\/175651n67n3jqkm5oljl3o.jpg\"}]",
        //			"longitude": "103.93878173828125", //可选
        //			"latitude": "30.76161003112793",  //可选
        //			"isHidden": 0,
        //			"isAnonymous": 0,  //1 表示匿名发帖。貌似不可用
        //			"isOnlyAuthor": 0,  //1 表示回帖仅作者可见。貌似不可用
        //			"isShowPostion": 0,
        //			"replyId": 0,  //引用内容的pid
        //			"isQuote": 0  //是否引用之前回复的内容。1是0否
        //          "title": "Title", // 标题。
        //          "typeId": 1234, // 分类。
        //		}
        //	}
        //}
        public static final String SEND_POST_AND_REPLY = BBS_API_URL + "forum/topicadmin";


        //===============================================其他===========================
        public static final String BOARD_IMAGE_500_500 = "http://47.101.218.117:8080/Uestcbbs/images/boardimages/500_500/";
        public static final String BOARD_IMAGE_600_400 = "http://47.101.218.117:8080/Uestcbbs/images/boardimages/600_400/";
        public static final String UPDATE_URL = "http://47.101.218.117:8080/Uestcbbs/update/update.json";
        public static final String EMOTICON_LIST = "http://47.101.218.117:8080/Uestcbbs/emoticon/emoticon_list.json";

        public static final String GET_SUGGESTION = "http://47.101.218.117:8080/uestcbbs/GetSuggestion";
        public static final String SUBMIT_SUGGESTION = "http://47.101.218.117:8080/uestcbbs/SubmitSuggestion";
    }

    public class AppFilePath {
        //android/data下的应用文件夹，不需要权限
        public static final String IMG_PATH = "images";
        public static final String TEMP_PATH = "temp";
        public static final String EMOTICON_PATH = "emoticon";

        //需要权限
        //public static final String DAILYPIC_DOWNLOAD_PATH = "/daily_pic";
    }

    public class Key {
        public static final String USER_ID = "user_id";
        public static final String TOPIC_ID = "topic_id";
        public static final String TOPIC_URL = "topic_url";
        public static final String TYPE = "type";
        public static final String DATA = "data";
        public static final String SUB_BOARD_DATA = "sub_board_data";
        public static final String BOARD_CAT_DATA = "cat_data";
        public static final String SORT_BY = "sort_by";
        public static final String BOARD_ID = "board_id";
        public static final String FILTER_ID = "filter_id";
        public static final String QUOTE_ID = "quote_id";
        public static final String IS_QUOTE = "is_quote";
        public static final String USER_NAME = "user_name";
        public static final String IMAGE_URL = "image_url";
        public static final String COPY_RIGHT = "copy_right";
        public static final String AT_USER = "at_user";
        public static final String CURRENT_SELECT = "current_select";
    }

    public class HintId {
        public static final int POST_DRAFT_WARNING_HINT = 1;
    }


    //部分帖子无法获取数据，响应500
    public static final String RESPONSE_ERROR_500_TOPIC = "syntax error, unexpected ')'";
    //某些板块响应为空
    public static final Object[] RESPONSE_ERROR_500_BOARD = {20, 219, 326, 316, 382, 211, 2, 46};
    //腾讯buglg
    public static final String BUGLY_ID = "c9542eaf0b";

    //长图提示背景
    public static final int[][] LONG_PIC_HINT_BACKGROUND = {{0xffffffff, 0x00ffffff}};
}
