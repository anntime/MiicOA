package com.example.miic.share.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.RandomCode;
import com.example.miic.base.http.PostRequest;
import com.example.miic.common.MyApplication;
import com.example.miic.share.adapter.PhotoAdapter;
import com.example.miic.share.adapter.RecyclerItemClickListener;
import com.example.miic.share.item.AccImageItem;
import com.example.miic.share.item.ShareMomentsItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MomentsEditActivity extends AppCompatActivity {
    private EditText contentEt;
    private Button submitBtn;
    private PhotoAdapter photoAdapter;
    private ArrayList<String> selectedPhotos = new ArrayList<>();
    private List<AccImageItem> selectedPhotoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moments_edit);
        setHeader();
        initView();

    }
    private void setHeader() {
        TextView titleTv = (TextView) findViewById(R.id.page_title);
        LinearLayout backLin = (LinearLayout)findViewById(R.id.back);
        LinearLayout rightLin = (LinearLayout)findViewById(R.id.menu);
        titleTv.setText("发布员工圈");
        //back event
        backLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //send friends messgae
        rightLin.setVisibility(View.INVISIBLE);
    }
    private void initView(){
        contentEt = (EditText)findViewById(R.id.content_et);
        submitBtn = (Button)findViewById(R.id.submit_btn);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        photoAdapter = new PhotoAdapter(this, selectedPhotos);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        recyclerView.setAdapter(photoAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (photoAdapter.getItemViewType(position) == PhotoAdapter.TYPE_ADD) {
                            PhotoPicker.builder()
                                    .setPhotoCount(PhotoAdapter.MAX)
                                    .setShowCamera(true)
                                    .setPreviewEnabled(false)
                                    .setSelected(selectedPhotos)
                                    .start(MomentsEditActivity.this);
                        } else {
                            PhotoPreview.builder()
                                    .setPhotos(selectedPhotos)
                                    .setCurrentItem(position)
                                    .start(MomentsEditActivity.this);
                        }
                    }
                }));

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = contentEt.getText().toString();

                    submitMoments(content);




            }
        });
    }
    private void submitMoments(String content ){
        JSONObject requestJson = new JSONObject();
        SimpleDateFormat formatter   =   new   SimpleDateFormat   ("yyyy-MM-dd HH:mm:ss");
        Date curDate =  new Date(System.currentTimeMillis());
        String   timeStr   =   formatter.format(curDate);
//        {publishInfo:{"ID":"abbd7ed8-092d-5ccd-39e3-e1bba40c5828","Content":"？","PublishType":"1"},
//            noticeUserView:{"NoticeType":0,"NoticeSource":0,"Noticers":[]},publishAccessoryInfos:[],removeSimpleAccessoryViews:null}
//        {publishInfo:{"ID":"b8b70c22-7050-2335-200f-b9ea7fb439a9","Content":"从发生大幅度是","PublishType":"1"},noticeUserView:{"NoticeType":0,"NoticeSource":0,"Noticers":[]},
//            publishAccessoryInfos:[{"ID":"e5e18bd9-91c2-a0fa-b3ae-5a915991bb42","FileName":"timg.jpg","FileType":"0","FilePath":"/file/PublishInfoAcc/Photo/69e51003-07c8-45e9-9502-e4c4ce95da3f.jpg","PublishID":"b8b70c22-7050-2335-200f-b9ea7fb439a9","UploadTime":"2018-05-04 11:50:06"},{"ID":"6c689d53-595f-adaa-0e89-3f0f79747eb8","FileName":"Tulips.jpg","FileType":"0","FilePath":"/file/PublishInfoAcc/Photo/776aab39-d382-480f-b5b6-0dc19b6c5c44.jpg","PublishID":"b8b70c22-7050-2335-200f-b9ea7fb439a9","UploadTime":"2018-05-04 11:50:06"}],removeSimpleAccessoryViews:null}
        try{
            JSONObject val1 = new JSONObject();
            String publishID = RandomCode.getCode();
            val1.put("ID",publishID );
            val1.put("Content",content);
            val1.put("PublishType","1");
            requestJson.put("publishInfo",val1);
            JSONObject val2 = new JSONObject();
            val2.put("NoticeType",0);
            val2.put("NoticeSource",0);
            val2.put("Noticers",new JSONArray());
            requestJson.put("noticeUserView",val2);
            JSONArray arr = new JSONArray();
            for (int k=0;k<selectedPhotoList.size();k++){
                JSONObject val3 = new JSONObject();
                val3.put("ID",RandomCode.getCode());
                val3.put("FileName",selectedPhotoList.get(k).getFileName()+"."+selectedPhotoList.get(k).getFileExt());
                val3.put("FileType",selectedPhotoList.get(k).getFileType()+"");
                val3.put("FilePath",selectedPhotoList.get(k).getFilePath());
                val3.put("PublishID",publishID);
                val3.put("UploadTime",timeStr);
                arr.put(val3);
            }
            requestJson.put("publishAccessoryInfos",arr);
            requestJson.put("removeSimpleAccessoryViews",new JSONObject());
        }catch (JSONException ex){
            Log.i("ShareActivity","json对象构造错误");
        }
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.MomentsSubmit(MyApplication.getCookieStr(),requestBody);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("body:" + response.body());
                if(response.body()!=null){
                    try{
                        //将字符串转换成jsonObject对象
                        Boolean result = new JSONObject(response.body()).getBoolean("d");
                        if (result==true){
                            //发布成功
                            Toast.makeText(MomentsEditActivity.this,"朋友圈信息发布成功！",Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            //发布失败
                            Toast.makeText(MomentsEditActivity.this,"朋友圈信息发布失败！",Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException ex){
                        Log.i("MomentsEditActivity","json对象构造错误");
                    }
                }else{
                    Toast.makeText(MomentsEditActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(MomentsEditActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {

            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            selectedPhotos.clear();

            if (photos != null) {

                selectedPhotos.addAll(photos);

            try {
                for (int i=0;i<photos.size();i++){
                    final MultipartBody body = fileToMultipartBody(new File(photos.get(i)));
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                upload(body);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            }
            photoAdapter.notifyDataSetChanged();
        }
    }
    //网络请求
    private void upload(MultipartBody body) {
        String type = "multipart/form-data; charset=utf-8; boundary="+body.boundary();
        //3、调用uploadImage上传图片
        PostRequest.Instance.request.updateImage(type,MyApplication.getCookieStr(),body).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null) {
                    try {
                        JSONObject jsonObj = new JSONObject(response.body());
                        Log.i("选择的图片3：",response.body());
                        if(jsonObj.getBoolean("result")==true){
                            JSONObject tempObj = new JSONObject(jsonObj.getString("acc"));
                            selectedPhotoList.add(new AccImageItem(tempObj));
                        }else{
                            Toast.makeText(MomentsEditActivity.this, jsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException ex) {
                        Log.i("Json解析错误:",ex.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(MomentsEditActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                Log.i("请求失败：",t.toString());
            }
        });


    }
    public static MultipartBody fileToMultipartBody(File file) {
        MultipartBody.Builder builder = new MultipartBody.Builder();

        //for (File file : files) {
            // TODO: 16-4-2  这里为了简单起见，没有判断file的类型
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), file);
        builder.addFormDataPart("file", file.getName(), requestBody);
        //}

        builder.setType(MultipartBody.FORM);
        MultipartBody multipartBody = builder.build();
        return multipartBody;
    }
}
