package com.example.miic.oa.common.gifView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.FloatRange;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.miic.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by XuKe on 2018/3/13.
 */

public class GifImageView extends AppCompatImageView {

    private static final int DEFAULT_DURATION = 1000;
    private float mScaleW = 1.0f;
    private float mScaleH = 1.0f;
    private float mScale = 1.0f;
    private Movie movie;
    //播放开始时间点
    private long mMovieStart;
    //播放暂停时间点
    private long mMoviePauseTime;
    //播放暂停时间
    private long offsetTime;
    //播放完成进度
    @FloatRange(from = 0, to = 1.0f)
    float percent;
    //播放次数，-1为循环播放
    private int counts = -1;

    private volatile boolean reverse = false;
    private volatile boolean mPaused;
    private volatile boolean hasStart;
    private Context context;
    private boolean mVisible = true;

    private OnPlayListener mOnPlayListener;
    private int movieDuration;
    private boolean endLastFrame = false;

    private String DOWNLOAD_ADDR = "";
    private int mViewWidth;
    private int mViewHeight;

    public GifImageView(Context context) {
        this(context, null);
        this.context = context;
    }

    public GifImageView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
        this.context = context;
    }

    public GifImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setViewAttributes(context, attrs, defStyle);
    }

    private void setViewAttributes(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.GifImageView, defStyle, 0);

        int srcID = a.getResourceId(R.styleable.GifImageView_gif_src, 0);
        boolean authPlay = a.getBoolean(R.styleable.GifImageView_auth_play, true);
        counts = a.getInt(R.styleable.GifImageView_play_count, -1);
        endLastFrame = a.getBoolean(R.styleable.GifImageView_end_last_frame, false);
        if (srcID > 0) {
            setGifResource(srcID, null);
            if (authPlay) play(counts);
        }
        a.recycle();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public void setGifResource(int movieResourceId, OnPlayListener onPlayListener) {
        if (onPlayListener != null) {
            mOnPlayListener = onPlayListener;
        }
        reset();
        movie = Movie.decodeStream(getResources().openRawResource(movieResourceId));
        if (movie == null) {
            //如果movie为空，那么就不是gif文件，尝试转换为bitmap显示
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), movieResourceId);
            if (bitmap != null) {
                setImageBitmap(bitmap);
                return;
            }
        }
        movieDuration = movie.duration() == 0 ? DEFAULT_DURATION : movie.duration();
        requestLayout();
    }

    public void setGifResource(int movieResourceId) {
        setGifResource(movieResourceId, null);
    }

    public void setGifResource(final String path, OnPlayListener onPlayListener) {
        movie = Movie.decodeFile(path);
        //setUrl(path);
        mOnPlayListener = onPlayListener;
        reset();
        if (movie == null) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if (bitmap != null) {
                setImageBitmap(bitmap);
                return;
            }
        }
        movieDuration = movie.duration() == 0 ? DEFAULT_DURATION : movie.duration();
        requestLayout();
        if (mOnPlayListener != null) {
            mOnPlayListener.onPlayStart();
        }
    }

    //从新开始播放
    public void playOver() {
        if (movie != null) {
            play(-1);
        }
    }

    //倒叙播放
    public void playReverse() {
        if (movie != null) {
            reset();
            reverse = true;
            if (mOnPlayListener != null) {
                mOnPlayListener.onPlayStart();
            }
            invalidate();
        }
    }

    public void play(int counts) {
        this.counts = counts;
        reset();
        if (mOnPlayListener != null) {
            mOnPlayListener.onPlayStart();
        }
        invalidate();
    }

    private void reset() {
        reverse = false;
        mMovieStart = SystemClock.uptimeMillis();
        mPaused = false;
        hasStart = true;
        mMoviePauseTime = 0;
        offsetTime = 0;
    }

    public void play() {
        if (movie == null)
            return;
        if (hasStart) {
            if (mPaused && mMoviePauseTime > 0) {
                mPaused = false;
                offsetTime = offsetTime + SystemClock.uptimeMillis() - mMoviePauseTime;
                invalidate();
                if (mOnPlayListener != null) {
                    mOnPlayListener.onPlayRestart();
                }
            }
        } else {
            play(-1);
        }
    }

    public void pause() {
        if (movie != null && !mPaused && hasStart) {
            mPaused = true;
            invalidate();
            mMoviePauseTime = SystemClock.uptimeMillis();
            if (mOnPlayListener != null) {
                mOnPlayListener.onPlayPause(true);
            }
        } else {
            if (mOnPlayListener != null) {
                mOnPlayListener.onPlayPause(false);
            }
        }
    }

    private int getCurrentFrameTime() {
        if (movieDuration == 0)
            return 0;
        long now = SystemClock.uptimeMillis() - offsetTime;
        int nowCount = (int) ((now - mMovieStart) / movieDuration);
        if (counts != -1 && nowCount >= counts) {
            hasStart = false;
            if (mOnPlayListener != null) {
                mOnPlayListener.onPlayEnd();
            }
            return endLastFrame ? movieDuration : 0;
        }
        float currentTime = (now - mMovieStart) % movieDuration;
        percent = currentTime / movieDuration;
        if (mOnPlayListener != null && hasStart) {
            BigDecimal mData = new BigDecimal(percent).setScale(2, BigDecimal.ROUND_HALF_UP);
            double f1 = mData.doubleValue();
            f1 = f1 == 0.99 ? 1.0 : f1;
            mOnPlayListener.onPlaying((float) f1);
        }
        return (int) currentTime;
    }

    public void setPercent(float percent) {
        if (movie != null && movieDuration > 0) {
            this.percent = percent;
            movie.setTime((int) (movieDuration * percent));
            invalidateView();
            if (mOnPlayListener != null) {
                mOnPlayListener.onPlaying(percent);
            }
        }

    }

    public boolean isPaused() {
        return this.mPaused;
    }

    public boolean isPlaying() {
        return !this.mPaused && hasStart;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (movie != null) {
            if (!mPaused && hasStart) {
                if (reverse) {
                    movie.setTime(movieDuration - getCurrentFrameTime());
                } else {
                    movie.setTime(getCurrentFrameTime());
                }
                drawMovieFrame(canvas);
                invalidateView();
            } else {
                drawMovieFrame(canvas);
            }
        }
    }

    /**
     * 画出gif帧
     */
    private void drawMovieFrame(Canvas canvas) {
        canvas.save();
        canvas.scale(1 / mScale, 1 / mScale);
        movie.draw(canvas, 0.0f, 0.0f);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (movie != null) {
            int movieWidth = movie.width();
            int movieHeight = movie.height();
            if (widthMode == MeasureSpec.EXACTLY) {
                mScaleW = ((float) movieWidth) / sizeWidth;
            }
            if (heightMode == MeasureSpec.EXACTLY) {
                mScaleH = ((float) movieHeight) / sizeHeight;
            }
            mScale = Math.max(mScaleW, mScaleH);
            setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? sizeWidth
                    : movieWidth, (heightMode == MeasureSpec.EXACTLY) ? sizeHeight
                    : movieHeight);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }


    private void invalidateView() {
        if (mVisible) {
            postInvalidateOnAnimation();
        }
    }

    public int getDuration() {
        if (movie != null) {
            return movie.duration();
        } else return 0;
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        mVisible = screenState == SCREEN_STATE_ON;
        invalidateView();
    }


    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        mVisible = visibility == View.VISIBLE;
        invalidateView();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mVisible = visibility == View.VISIBLE;
        invalidateView();
    }


    public interface OnPlayListener {
        void onPlayStart();

        void onPlaying(@FloatRange(from = 0f, to = 1.0f) float percent);

        void onPlayPause(boolean pauseSuccess);

        void onPlayRestart();

        void onPlayEnd();
    }



    /**
     * 设置gif图片的url，开始播放gif
     * @param url
     */
    public void setUrl(String url) {
        DOWNLOAD_ADDR = url;
        //先从本地读取，如果本地没有，在从网络上获取
        movie = readGifFormLoacl();
        if (movie == null) {
            //先下载只本地
            gifDownload();
            readGifFormNet();
        }
    }
    /**
     * 加载本地图片
     */
    public Movie readGifFormLoacl() {
        try {
            File dir = context.getCacheDir().getAbsoluteFile();
            String fileName = DOWNLOAD_ADDR.substring(
                    DOWNLOAD_ADDR.lastIndexOf("/"), DOWNLOAD_ADDR.length());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName);
            return movie = Movie.decodeFile(file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 下载网络图片
     */
    private void readGifFormNet() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                httpTest();
                return null;
            }
            @Override
            protected void onPostExecute(Void result) {
                mHandler.sendEmptyMessage(0);
            }

        }.execute();
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    invalidate();
                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    /**
     * 下载网络图片到本地
     */
    public void gifDownload() {
        new Thread() {
            public void run() {
                InputStream in = null;
                FileOutputStream fos = null;
                try {
                    URL url = new URL(DOWNLOAD_ADDR);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    int size = connection.getContentLength();
                    Log.e(TAG, "size = " + size);
                    in = connection.getInputStream();
                    File dir = context.getCacheDir().getAbsoluteFile();
                    String fileName = DOWNLOAD_ADDR.substring(
                            DOWNLOAD_ADDR.lastIndexOf("/"),
                            DOWNLOAD_ADDR.length());
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(dir, fileName);
                    Log.d("info", "file->" + file.getAbsolutePath());
                    fos = new FileOutputStream(file);
                    int len = -1;
                    byte[] buffer = new byte[1024 * 8];
                    while ((len = in.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                        fos.flush();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
        }.start();
    }
    /**
     * 下载网络图片
     */
    private void httpTest() {
        try {
            URL url = new URL(DOWNLOAD_ADDR);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            // connection.setRequestMethod("GET");
            int size = connection.getContentLength();
            Log.e(TAG, "size = " + size);
            InputStream in = connection.getInputStream();
            byte[] array = streamToBytes(in);
            movie = Movie.decodeByteArray(array, 0, array.length);
            // 得到图片宽高
            if (movie != null) {
                mViewWidth = movie.width();
                mViewHeight = movie.height();
            }
            in.close();
        } catch (IOException e) {
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
}
