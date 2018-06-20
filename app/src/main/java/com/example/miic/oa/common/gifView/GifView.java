package com.example.miic.oa.common.gifView;

/**
 * Created by XuKe on 2018/3/13.
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import static java.lang.Thread.sleep;


public class GifView extends View
{
    private static final String TAG = GifView.class.getSimpleName();
    private Movie mMovie;
    //private Bitmap mBmp;
    private long mPlayMovieTime;
    private static  String DOWNLOAD_ADDR = "http://img3.3lian.com/2006/013/08/20051105155416881.gif";
    private Context context;
    public GifView(Context context)
    {
        super(context);
        this.context = context;
        //readGifFormNative();//播放本地的gif格式图片
        //readGifFormNet();//播放网络的gif格式图片
    }

    public GifView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        //readGifFormNet();
        //setFocusable(true);
        //readGifFormNative();
    }


    private void readGifFormNative(int id)
    {
        InputStream in;
        in = context.getResources().openRawResource(id);
        mMovie = Movie.decodeStream(in);
    }


    public void readGifFormNet(String path)
    {

        DOWNLOAD_ADDR = path;
        new Thread(new Runnable()
        {


            @Override
            public void run()
            {
                httpTest();
                mHandler.sendEmptyMessage(0);

            }
        }).start();
    }


    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 0:
                    invalidate();
                    break;


                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    private void httpTest()
    {
        Bitmap bitmap = null;
        try
        {
            URL url = new URL(DOWNLOAD_ADDR);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            // connection.setRequestMethod("GET");
            int size = connection.getContentLength();
            Log.e(TAG, "size = " + size);
            InputStream in = connection.getInputStream();
            if (in!=null){
//                byte[] array = streamToBytes(in);
//                mMovie = Movie.decodeByteArray(array, 0, array.length);
                mMovie = Movie.decodeStream(in);
                bitmap = Bitmap.createBitmap(mMovie.width(), mMovie.height(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                mMovie.draw(canvas, 0, 0);
                canvas.save();
            }
            //mMovie = Movie.decodeStream(in);
            // mBmp = BitmapFactory.decodeStream(in);
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }



    private static byte[] streamToBytes(InputStream is) {
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = is.read(buffer)) >= 0) {
                os.write(buffer, 0, len);
            }
        } catch (java.io.IOException e) {
        }
        return os.toByteArray();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        Paint p = new Paint();
        p.setAntiAlias(true);
        setLayerType(LAYER_TYPE_SOFTWARE, p);
        if (mMovie != null) {

            long now = android.os.SystemClock.uptimeMillis();
            if (mPlayMovieTime == 0) {   // first time
                mPlayMovieTime = now;
            }
            int dur = mMovie.duration();
            if (dur == 0) {
                dur = 1000;
            }
            int relTime = (int)((now - mPlayMovieTime) % dur);
            mMovie.setTime(relTime);
            mMovie.draw(canvas, getWidth() - mMovie.width(),
                    getHeight() - mMovie.height());
            invalidate();
        }
    }
}
