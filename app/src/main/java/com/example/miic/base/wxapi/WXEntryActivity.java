package com.example.miic.base.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.miic.base.http.PostRequest;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by XuKe on 2018/3/12.
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{

    String access_token;
    Long expires_in;
    String refreshToken;
    String openid;

    private IWXAPI api;
    public static final String APP_ID="wx798XXXXXXXXX";//(这个APP_ID为在微信开放平台创建应用后获得的ID)
    public static final String APP_SECRET="";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        api= WXAPIFactory.createWXAPI(this, APP_ID, false);
        api.handleIntent(getIntent(),this);
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent,this);
    }
    @Override
    public void onReq(BaseReq baseReq) {

    }

    //在这个方法里面进行结果处理，获得CODE（微信登录，微信分享，微信朋友圈分享都会调用这个方法）
    @Override
    public void onResp(BaseResp resp){
        switch(resp.errCode){
            case BaseResp.ErrCode.ERR_OK:
                //返回成功，判断是哪种类型（如果不进行判断，当微信登录和微信分享同时存在的话，会出现异常）
                switch(resp.getType()){
                    case ConstantsAPI.COMMAND_SENDAUTH:
                        //登录回调,获得CODE
                        String code=((SendAuth.Resp)resp).code;
                        //通过code获得access_token
                        getAccess_token(code);
                        break;
                    case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
                        //微信分享回调
                        break;
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //用户取消
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //用户拒绝授权
                break;
        }
    }

    //通过得到的CODE获取access_token
    void getAccess_token(String code){

        Call<String> call = WXPostRequest.Instance.request.GetAccessToken (APP_ID,APP_SECRET,code,"authorization_code");
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("body:" + response.body());
                try {
                    JSONObject resObj=new JSONObject(response.body());
                    //得到openid和access_token，调用接口登录
                    access_token=resObj.getString("access_token");
                    openid=resObj.getString("openid");
                    refreshToken=resObj.getString("refresh_token");
                    expires_in=resObj.getLong("expires_in");
                    getUserInfo();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(WXEntryActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);


    }

    //通过access_token调用接口
    void getUserInfo(){
//        if(isAccessTokenIsInvalid() && System.currentTimeMillis() < expires_in){
//            String url="https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openid;
//            HttpClient client = add DefaultHttpClient();
//            HttpGet get = add HttpGet(URI.create(uri));
//            try {
//                HttpResponse response = client.execute(get);
//                if (response.getStatusLine().getStatusCode() == 200) {
//                    BufferedReader reader = add BufferedReader(add InputStreamReader(response.getEntity().getContent(), "UTF-8"));
//                    StringBuilder builder = add StringBuilder();
//                    for (String temp = reader.readLine(); temp != null; temp = reader.readLine()) {
//                        builder.append(temp);
//                    }
//                    JSONObject object = add JSONObject(builder.toString().trim());
//                    String nikeName = object.getString("nickname");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        Call<String> call = WXPostRequest.Instance.request.GetUserinfo (access_token,openid);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("body:" + response.body());

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(WXEntryActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
}