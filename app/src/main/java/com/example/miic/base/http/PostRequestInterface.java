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
    //获取首页消息数量
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/MessageInfoService.asmx/GetMessageSearchCount")
    Call<String> GetMessageSearchCount(@Body RequestBody keywordView);
    //获取首页消息列表
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/MessageInfoService.asmx/MessageSearch")
    Call<String> MessageSearch(@Body RequestBody keywordView);
    //读消息操作
    @Headers({"Content-Type: application/json","Accept: application/json","url_name:oa"})//需要添加头
    @POST("service/MessageInfoService.asmx/ReadMessage")
    Call<String> ReadMessage(@Body RequestBody keywordView);
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
    //选择工作流
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/SealService.asmx/ChooseProjKV")
    Call<String> ChooseProjKV(@Body RequestBody keywordView );
    //获取下一步审核人员信息
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
    //获取用印类型
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/BasicService.asmx/GetSealTypeInfos")
    Call<String> GetSealTypeInfos( );
    //获取印章类型
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/BasicService.asmx/GetSealApplicationTypeInfos")
    Call<String> GetSealApplicationTypeInfos();
    //获取用印申请部门
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/DeptService.asmx/GetMyDeptInfoList")
    Call<String> GetMyDeptInfoList(@Body RequestBody keywordView);
    //获取登陆用户数据
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/UserService.asmx/GetLoginInfo")
    Call<String> GetLoginInfo(@Body RequestBody keywordView);
    //获取我的用印审批数量
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/SealService.asmx/GetMyApproveSearchCount")
    Call<String> GetMySealApprovalSearchCount(@Body RequestBody keywordView );
    //获取我的用印审批
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/SealService.asmx/MyApproveSearch")
    Call<String> MySealApprovalSearch(@Body RequestBody keywordView );
    //用印审批
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/SealService.asmx/Approve")
    Call<String> ApproveSeal(@Body RequestBody keywordView);
    //获取我的监印数量
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/SealService.asmx/GetSealPrintSearchCount")
    Call<String> GetMySealPrintSearchCount(@Body RequestBody keywordView );
    //获取我的监印列表
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/SealService.asmx/SealPrintSearch")
    Call<String> MySealPrintSearch(@Body RequestBody keywordView );
    //用印监印
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/SealService.asmx/SetSealPrint")
    Call<String> PrintSeal(@Body RequestBody keywordView);

    /*********************************
     * ***********合同管理***********************
     *****************************************************/
    //角色权限
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/RbacService.asmx/GetFunInfoListForMobile") 
    Call<String> GetContractFunInfoList(@Body RequestBody keywordView);
    //获取合同详情
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/GetDetailInfo")
    Call<String> GetDetailInfo(@Body RequestBody keywordView);
    //获取合同标识
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/BasicService.asmx/GetIdentificationItemInfos")
    Call<String> GetIdentificationItemInfos();
    // 获取全部部门
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/DeptService.asmx/GetMiicDeptInfoList")
    Call<String> GetMiicDeptInfoList();
    // 获取计划查询全部部门
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/GetPlanDeptInfoList")
    Call<String> GetPlanDeptInfoList();
    /**
      合同查询列表页面
     */
    //获取我可查询的部门列表
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/GetMyQueryDeptList")
    Call<String> GetMyQueryDeptList();
    //获取合同查询
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/QuerySearch")
    Call<String> QuerySearch(@Body RequestBody keywordView);
    //获取合同查询数量
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/GetQuerySearchCount")
    Call<String> GetQuerySearchCount(@Body RequestBody keywordView);
    /**
     合同管理列表页面
     */
    //获取合同管理查询
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/ContractSearch")
    Call<String> ContractSearch(@Body RequestBody keywordView);
    //获取合同管理查询数量
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/GetContractSearchCount")
    Call<String> GetContractSearchCount(@Body RequestBody keywordView);

    /**
     我的合同申请列表页面
     */
    // 获取合同申请部门===获取用印申请部门

    //获取我的合同申请查询
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/MyApplicationSearch")
    Call<String> MyApplicationSearch(@Body RequestBody keywordView);
    //获取我的合同申请查询数量
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/GetMyApplicationSearchCount")
    Call<String> GetMyApplicationSearchCount(@Body RequestBody keywordView);
    //删除合同申请
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/Remove")
    Call<String> Remove(@Body RequestBody keywordView);
    /**
     我的合同审批列表页面
     */
    // 获取合同审批部门
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/SealService.asmx/GetMyApproveDeptList")
    Call<String> GetMyApproveDeptList();
    //获取我的合同审批查询
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/MyApproveSearch")
    Call<String> MyApproveSearch(@Body RequestBody keywordView);
    //获取我的合同审批查询数量
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/GetMyApproveSearchCount")
    Call<String> GetMyApproveSearchCount(@Body RequestBody keywordView);
    /**
     我的合同审批页面
     */
    //获取 合同审批详情
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/GetContractApproveDetailInfo")
    Call<String> GetContractApproveDetailInfo(@Body RequestBody keywordView);
    //获取当前审批流程数
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/WorkflowService.asmx/GetCurrentSetpNum")
    Call<String> GetCurrentSetpNum(@Body RequestBody keywordView);
    //获取下一步审批人列表
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/WorkflowService.asmx/GetNextApprovalList")
    Call<String> GetNextApprovalList(@Body RequestBody keywordView);
    //审批
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/ApproveContract")
    Call<String> ApproveContract(@Body RequestBody keywordView);
    //判断是否可以撤回
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/CanWithdraw")
    Call<String> CanWithdraw(@Body RequestBody keywordView);
    //合同撤回
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/Withdraw")
    Call<String> Withdraw(@Body RequestBody keywordView);

    /**
     合同监印列表页面、合同执行列表页面
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/CommonContractSearch")
    Call<String> CommonContractSearch(@Body RequestBody keywordView);
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/GetCommonContractSearchCount")
    Call<String> GetCommonContractSearchCount(@Body RequestBody keywordView);
    /**
     合同计划列表页面
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/ContractPlanSearch")
    Call<String> ContractPlanSearch(@Body RequestBody keywordView);
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/GetContractPlanSearchCount")
    Call<String> GetContractPlanSearchCount(@Body RequestBody keywordView);
    /**
     合同监印页面
     */
    //监印通过
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/ContractPrintApproved")
    Call<String> ContractPrintApproved(@Body RequestBody keywordView);
    /**
     合同执行页面
     */
    //获取合同执行详情
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/SearchContractExecute")
    Call<String> SearchContractExecute(@Body RequestBody keywordView);
    //提交合同执行
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/ContractExecuteSubmit")
    Call<String> ContractExecuteSubmit(@Body RequestBody keywordView);
    /**
     合同计划页面
     */
    //获取合同计划详情
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/SearchContractPlan")
    Call<String> SearchContractPlan(@Body RequestBody keywordView);
    //提交合同计划
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/ContractPlanSubmit")
    Call<String> ContractPlanSubmit(@Body RequestBody keywordView);
    //删除合同计划
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/ContractPlanDelete")
    Call<String> ContractPlanDelete(@Body RequestBody keywordView);
    /**
     合同完成列表页面
     */
    //获取合同完成查询
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/ContractFinishSearch")
    Call<String> ContractFinishSearch(@Body RequestBody keywordView);
    //获取合同完成查询数量
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/GetContractFinishSearchCount")
    Call<String> GetContractFinishSearchCount(@Body RequestBody keywordView);
    /**
     合同完成页面
     */
    //设置合同终止
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/SetIllegalFinishContract")
    Call<String> SetIllegalFinishContract(@Body RequestBody keywordView);
    //设置合同完成
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/SetFinishContract")
    Call<String> SetFinishContract(@Body RequestBody keywordView);
    //恢复合同状态
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/ContractService.asmx/RecoverContractStatus")
    Call<String> RecoverContractStatus(@Body RequestBody keywordView);



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

    //用车管理
    //获取我的用车申请数量
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/CarService.asmx/GetMyApplicationSearchCount")
    Call<String> GetMyCarApplicationSearchCount(@Body RequestBody keywordView );
    //获取我的用车申请列表
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/CarService.asmx/MyApplicationSearch")
    Call<String> MyCarApplicationSearch(@Body RequestBody keywordView );
    //获取车牌号
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/CarService.asmx/GetPersonCarNumList")
    Call<String> GetCarNumInfos(@Body RequestBody keywordView);
    //提交用车申请
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/CarService.asmx/GetInformation")
    Call<String> GetCarApply(@Body RequestBody keywordView);
    //选择工作流
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/CarService.asmx/ChooseProjKV")
    Call<String> CarChooseProjKV(@Body RequestBody keywordView );
    //提交用车申请
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/CarService.asmx/SubmitCarInfo")
    Call<String> SubmitCarApplication(@Body RequestBody keywordView );
    //获取用车申请的详情
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/CarService.asmx/GetCarDetailInfoWithApprove")
    Call<String> GetCarDetailInfo(@Body RequestBody keywordView );
    //获取我的用车审核数量
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/CarService.asmx/GetMyApproveSearchCount")
    Call<String> GetMyCarApprovalSearchCount(@Body RequestBody keywordView );
    //获取我的用车审核列表
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/CarService.asmx/MyApproveSearch")
    Call<String> MyCarApprovalSearch(@Body RequestBody keywordView );
    //用车审批
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/CarService.asmx/Approve")
    Call<String> ApproveCar(@Body RequestBody keywordView);

    /***
     * 会议室管理
     */
    //获取会议室列表
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/Meeting/RoomService.asmx/GetRoomList")
    Call<String> GetRoomList(@Body RequestBody keywordView );//{roomStatus:0}
    //通过会议室和选择的日期查询已被占用的情况
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/Meeting/MeetingService.asmx/GetMeetingTimeByDate")
    Call<String> GetMeetingTimeByDate(@Body RequestBody keywordView );//{date:'2018-10-24',roomID:'6d9163c9-fac8-5fb5-16f1-4f81fea9ba7a'}
    //提交会议室预约
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/Meeting/MeetingService.asmx/Submit")
    Call<String> SubmitMeetingRoomApply(@Body RequestBody keywordView );
    //{meetingInfo:{"ID":"b3d804af-222c-b9a6-4ca0-addda6d85abc","RoomID":"6d9163c9-fac8-5fb5-16f1-4f81fea9ba7a",
    // "RoomName":"5009","MeetingTheme":"123","ReverseBeginTime":"2018-10-22 08:00","ReverseEndTime":"2018-10-22 09:00",
    // "ReverseDate":"2018-10-22","ReverseTimeLength":3600000,"DeptID":"中心领导","DeptName":"中心领导"}}
    //获取本月所有的会议预约情况
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/Meeting/MeetingService.asmx/GetMeetingUsedInfosByYearMonth")
    Call<String> GetMeetingUsedInfosByYearMonth(@Body RequestBody keywordView );//{year:'2018',month:'10'}
    //根据点击的日期获取各个会议室已经预约的情况
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/Meeting/MeetingService.asmx/GetMeetingStatisticsDetailInfos")
    Call<String> GetMeetingStatisticsDetailInfos(@Body RequestBody keywordView );//{date:'2018-10-24'}
    //删除会议室预约
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/Meeting/MeetingService.asmx/Delete")
    Call<String> DeleteMeetingRoomApply(@Body RequestBody keywordView );//{id:'911c90f6-9e37-8174-16b6-72ee49e0006c'}
    //缺少一个获取本周的会议室预约情况endDate:''
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("service/Meeting/MeetingService.asmx/GetMeetingUsedInfosByDate")
    Call<String> GetMeetingUsedInfosByDate(@Body RequestBody keywordView );//{beginDate:'2018-10-24',endDate:''}
}
