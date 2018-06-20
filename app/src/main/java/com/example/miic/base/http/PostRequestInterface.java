package com.example.miic.base.http;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

/**
 * Created by XuKe on 2018/1/16.
 */

public interface PostRequestInterface {

    /**
     * @param loginView:UserCode,Password
     * @return
     */
    //采用@Post表示Post方法进行请求（传入部分url地址）
    // 采用@FormUrlEncoded注解的原因:API规定采用请求格式x-www-form-urlencoded,即表单形式
    // 需要配合@Field使用LoginSendSMS
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/UserService.asmx/AppLogin")
    Call<String> AppLogin(@Body RequestBody loginView);
    //手机重设密码
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/UserService.asmx/ResetMobilePassword")
    Call<String> ResetMobilePassword(@Body RequestBody MobileResetUserInfoView);

    //发送手机验证码
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/UserService.asmx/SendIdentifyToMobile")
    Call<String> SendIdentifyToMobile(@Body RequestBody mobile);

    //根据columnID获取新闻
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/NewsService.asmx/NewsSearchByColumnForMobile")
    Call<String> NewsSearchByColumn(@Body RequestBody keywordView);

    //根据columnID获取新闻数量
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/NewsService.asmx/GetNewsSearchCountByColumn")
    Call<String> GetNewsSearchCountByColumn(@Body RequestBody keywordView);

    //根据columnID获取新闻详细信息
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/NewsService.asmx/GetDetailInformation")
    Call<String> GetDetailInformation(@Body RequestBody keywordView);


    //浏览新闻
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/NewsService.asmx/Browse")
    Call<String> BrowseNews(@Body RequestBody keywordView);

    //根据columnID获取新闻评论数
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/NewsService.asmx/GetCommentCount")
    Call<String> GetCommentCount(@Body RequestBody keywordView);

    //根据columnID获取新闻评论列表
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/NewsService.asmx/GetCommentList")
    Call<String> GetCommentList(@Body RequestBody keywordView);

    //评论新闻
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/NewsService.asmx/Comment")
    Call<String> Comment(@Body RequestBody keywordView);


    //点赞新闻
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/NewsService.asmx/Praise")
    Call<String> Praise(@Body RequestBody keywordView);

    //撤回评论
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/NewsService.asmx/RemoveComment")
    Call<String> RemoveComment(@Body RequestBody keywordView);

    //回复评论
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/NewsService.asmx/Reply")
    Call<String> ReplyComment(@Body RequestBody keywordView);

    //搜索新闻
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/NewsService.asmx/Search")
    Call<String> GetSearchResultList(@Body RequestBody keywordView);

    //搜索新闻个数
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/NewsService.asmx/GetSearchCount")
    Call<String> GetSearchResultCount(@Body RequestBody keywordView);

    //读取用户个人信息
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/UserService.asmx/GetInformation")
    Call<String> GetUserInfo(@Body RequestBody keywordView);

    //获取首页公告
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/NewsService.asmx/TopNewsSearchByColumn")
    Call<String> GetTopNews(@Body RequestBody keywordView);

    //获取资讯页面的栏目列表
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/RbacService.asmx/GetMobileMenuInfoList")
    Call<String> GetMobileMenuInfoList(@Body RequestBody keywordView);

    //获取民族信息
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/BasicService.asmx/GetNationInfos")
    Call<String> GetNationInfos(@Body RequestBody keywordView);

    //修改用户信息提交
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/UserService.asmx/Submit")
    Call<String> SubmitUserInfos(@Body RequestBody keywordView);

    //获取通讯列表
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/UserService.asmx/GetAddressBookUserInfos")
    Call<String> GetAddressBook(@Body RequestBody keywordView);

    //获取通讯列表（IM）
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/UserService.asmx/GetAllAddressBookList")
    Call<String> GetAllAddressBookList(@Body RequestBody keywordView);

    //通讯列表搜索
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/UserService.asmx/SearchAddressBookUserInfos")
    Call<String> SearchAddressBookUserInfos(@Body RequestBody keywordView);


    //获取即时通讯的用户信息
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/UserService.asmx/GetIMUserInfo")
    Call<String> GetIMUserInfo(@Body RequestBody keywordView);

    //获取首页轮播图（宣传介绍图）
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/ReferralService.asmx/Show")
    Call<String> GetCarouselInfo(@Body RequestBody keywordView);

    //发送手机验证码为了绑定手机
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/UserService.asmx/SendIdentifyToMobileForBind")
    Call<String> SendIdentifyToMobileForBind(@Body RequestBody keywordView);


    //绑定手机
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/UserService.asmx/CheckIdentifyCodeForBind")
    Call<String> CheckIdentifyCodeForBind(@Body RequestBody keywordView);
    //更新绑定状态
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/UserService.asmx/Submit")
    Call<String> UpdateBind(@Body RequestBody keywordView);

    //版本更新GET版本号
    @GET("http://api.fir.im/apps/latest/5abcc4d6ca87a86ab700fb0a?api_token=59d330e18e8928dbf56dbb3922e1e8e6")
    Call<String> GetVersionNum();

    /**
     * sns接口
     */
    //获取用户详细信息
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:manage"})//需要添加头
    @POST("Service/UserService.asmx/GetUserDetailInformation")
    Call<String> GetUserDetailInformation(@Header("Cookie") String cookie,@Body RequestBody keywordView);

    /**
     * 以下：朋友圈相关接口
     */
    //搜索我的朋友圈信息数（含评论）
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:share"})//需要添加头
    @POST("Service/MomentsService.asmx/GetSearchMyMomentsWithCommentCount")
    Call<String> GetSearchMyMomentsWithCommentCount(@Header("Cookie") String cookie,@Body RequestBody keywordView);
    //搜索我的朋友圈信息（含评论）
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:share"})//需要添加头
    @POST("Service/MomentsService.asmx/SearchMyMomentsWithComment")
    Call<String> SearchMyMomentsWithComment (@Header("Cookie") String cookie,@Body RequestBody keywordView);
    //点赞朋友圈
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:share"})//需要添加头
    @POST("Service/MomentsService.asmx/Praise")
    Call<String> PraiseMoments (@Header("Cookie") String cookie,@Body RequestBody keywordView);
    //评论朋友圈
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:share"})//需要添加头
    @POST("Service/MomentsService.asmx/PersonComment")
    Call<String> CommentMoments (@Header("Cookie") String cookie,@Body RequestBody keywordView);
    //发表朋友圈
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:share"})//需要添加头
    @POST("Service/MomentsService.asmx/Submit")
    Call<String> MomentsSubmit (@Header("Cookie") String cookie,@Body RequestBody keywordView);
    //删除评论
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:share"})//需要添加头
    @POST("Service/MomentsService.asmx/RemoveMyComment")
    Call<String> RemoveMomentsComment (@Header("Cookie") String cookie,@Body RequestBody keywordView);
    //根据朋友圈信息ID获取评论
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:share"})//需要添加头
    @POST("Service/MomentsService.asmx/GetCommentInfos")
    Call<String> GetCommentInfos (@Header("Cookie") String cookie,@Body RequestBody keywordView);
    //根据朋友圈信息ID获取评论数量
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:share"})//需要添加头
    @POST("Service/MomentsService.asmx/GetCommentInfoCount")
    Call<String> GetCommentInfoCount (@Header("Cookie") String cookie,@Body RequestBody keywordView);


    //朋友圈上传图片
    @Headers({"Accept: application/json","url_name:share"})//需要添加头
    @POST("Service/PublishInfoAccUploadHandlerService.ashx?Type=Photo")
    Call<String> updateImage(@Header("Content-Type") String contentType,@Header("Cookie") String cookie, @Body MultipartBody Part );

    //获取朋友圈好友数量
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:share"})//需要添加头
    @POST("service/AddressBookService.asmx/GetSearchCount")
    Call<String> GetAddressBookCount(@Header("Cookie") String cookie, @Body RequestBody keywordView );
    //获取朋友圈好友列表
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:share"})//需要添加头
    @POST("service/AddressBookService.asmx/Search")
    Call<String> GetAddressBookList(@Header("Cookie") String cookie, @Body RequestBody keywordView );

    //获取好友申请列表
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:share"})//需要添加头
    @POST("service/AddressBookService.asmx/GetMyValidationMessageList")
    Call<String> GetMyValidationMessageList(@Header("Cookie") String cookie, @Body RequestBody keywordView );

    //通过关键词搜索用户以添加好友（数量）
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:share"})//需要添加头
    @POST("service/UserService.asmx/GetSearchFriendsCount")
    Call<String> GetSearchFriendsCount(@Header("Cookie") String cookie, @Body RequestBody keywordView );
    //搜索用户以添加好友
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:share"})//需要添加头
    @POST("service/UserService.asmx/SearchFriends")
    Call<String> SearchFriends(@Header("Cookie") String cookie, @Body RequestBody keywordView );
    //申请添加好友
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:share"})//需要添加头
    @POST("service/AddressBookService.asmx/Application")
    Call<String> Application(@Header("Cookie") String cookie, @Body RequestBody keywordView );
    //同意申请添加好友
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:share"})//需要添加头
    @POST("service/AddressBookService.asmx/Agree")
    Call<String> AgreeApplication(@Header("Cookie") String cookie, @Body RequestBody keywordView );
    //拒绝申请添加好友
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:share"})//需要添加头
    @POST("service/AddressBookService.asmx/Refuse")
    Call<String> RefuseApplication(@Header("Cookie") String cookie, @Body RequestBody keywordView );


    /***
     * 用印管理
     */
    //获取申请信息
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/DeptService.asmx/GetInformation")
    Call<String> GetSealOperatorInfo(@Body RequestBody keywordView );
    //获取申请信息
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/SealService.asmx/GetInformation")
    Call<String> GetSealApply(@Body RequestBody keywordView );
    //获取审核人员信息
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/WorkflowService.asmx/GetChiefInfo")
    Call<String> GetChiefInfo(@Body RequestBody keywordView );
    //提交用印申请
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/SealService.asmx/Submit")
    Call<String> SubmitSealApplication(@Body RequestBody keywordView );
    //临时保存用印申请
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/SealService.asmx/Save")
    Call<String> SaveSealApplication(@Body RequestBody keywordView );
    //获取我的用印申请
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/SealService.asmx/MyApplicationSearch")
    Call<String> MySealApplicationSearch(@Body RequestBody keywordView );
    //获取我的用印申请数量
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/SealService.asmx/GetMyApplicationSearchCount")
    Call<String> GetMySealApplicationSearchCount(@Body RequestBody keywordView );
    //获取用印申请的详情
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/SealService.asmx/GetDetailInfo")
    Call<String> GetMySealDetailInfo(@Body RequestBody keywordView );


    //获得请假列表
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:qj"})//需要添加头
    @POST("Service/LeaveService.asmx/GetMyLeaveMobileList")
    Call<String> GetMyLeaveList (@Header("Cookie") String cookie,@Body RequestBody keywordView);
    //获得请假类型列表
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:qj"})//需要添加头
    @POST("Service/LeaveService.asmx/GetLeaveTypeList")
    Call<String> GetLeaveTypeList (@Header("Cookie") String cookie,@Body RequestBody keywordView);
    //获得流程状态列表
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:qj"})//需要添加头
    @POST("service/CodeTypeService.asmx/GetMySubmitStatusList")
    Call<String> GetMySubmitStatusList (@Header("Cookie") String cookie,@Body RequestBody keywordView);
}
