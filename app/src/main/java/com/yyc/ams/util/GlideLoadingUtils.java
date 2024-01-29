package com.yyc.ams.util;

import static com.bumptech.glide.load.resource.bitmap.VideoDecoder.FRAME_OPTION;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.tencent.smtt.sdk.ui.dialog.widget.RoundImageView;
import com.yyc.ams.R;

import java.io.File;
import java.security.MessageDigest;
import java.util.concurrent.ExecutionException;

/**
 * Created by edison on 2018/4/19.
 */

public class GlideLoadingUtils {



    public static void load(Context act, Object url, ImageView imageView, boolean isUser) {
        ld(act, url, imageView, isUser);
    }
     public static void load(Context act, Object url, RoundImageView imageView, boolean isUser) {
        ld(act, url, imageView, isUser);
    }
    public static void load(Context act, Object url, ImageView imageView) {
        ld(act, url, imageView, false);
    }


    private static void ld(Context act, Object url, ImageView imageView, boolean isUser){
        RequestOptions options = new RequestOptions();
        if (isUser){
            options.placeholder(R.mipmap.place_holder_user);
        }else {
//            options.placeholder(R.drawable.no_banner);
        }
        Glide.with(act).load(url).apply(options).into(imageView);
    }


    public static void loadRounded(Context act, Object url, ImageView imageView){
        //设置图片圆角角度
        RoundedCorners roundedCorners= new RoundedCorners(6);
        //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
        RequestOptions options=RequestOptions.bitmapTransform(roundedCorners).override(300, 300);
        Glide.with(act).load(url).apply(options).into(imageView);
    }

    public static void loadVideoScreenshot(final Context context, String uri, final ImageView imageView, long frameTimeMicros, final ImageView ivImg3) {
        final RequestOptions requestOptions = RequestOptions.frameOf(frameTimeMicros);
        requestOptions.set(FRAME_OPTION, MediaMetadataRetriever.OPTION_CLOSEST);
        requestOptions.transform(new BitmapTransformation() {
            @Override
            protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
                return toTransform;
            }
            @Override
            public void updateDiskCacheKey(MessageDigest messageDigest) {
                try {
                    messageDigest.update((context.getPackageName() + "RotateTransform").getBytes("utf-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Glide.with(context).load(uri).apply(requestOptions).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                ivImg3.setImageDrawable(resource);
                return false;
            }
        }).into(imageView);
    }

    /**
     *  计算单张图片 计算宽高
     */
    public static void loadMeasuring(Context act, Object url, ImageView imageView){
        int pWidth = ScreenUtils.getScreenWidth();
        Glide.with(act)
            .load(url)
            .listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    int w = resource.getIntrinsicWidth();
                    int h = resource.getIntrinsicHeight();

                    int newW;
                    int newH;
                    if (h > w * 3) {//h:w = 5:3
                        newW = pWidth / 2;
                        newH = newW * 5 / 3;
                    } else if (h < w) {//h:w = 2:3
                        newW = pWidth * 2 / 3;
                        newH = newW * 2 / 3;
                    } else {//newH:h = newW :w
                        newW = pWidth / 2;
                        newH = h * newW / w;
                    }
//                    imageView.setLayoutParams(new ViewGroup.LayoutParams(newW, newH));
//                    imageView.layout(0, 0, newW, newH);
                    ViewGroup.LayoutParams params = imageView.getLayoutParams();
                    params.height = newH;
                    params.width = newW;
                    imageView.setLayoutParams(params);
                    return false;
                }
            }).into(imageView);
    }


    public static void saveImage(final Context act, final String url, final String name){
        ToastUtils.showLong("正在下载，请稍后...");
        Glide.with(act)
                .asBitmap()//强制Glide返回一个Bitmap对象
                .load(url)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        final int width = resource.getWidth();
                        final int height = resource.getHeight();
                        new Thread(
                                () -> {
                                    try {
                                        File file = Glide.with(act)
                                                .load(url)
                                                .downloadOnly(width, height)
                                                .get();
                                        LogUtils.e(file);
//                                            FileSaveUtils.save(file, name);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                }
                        ).start();

                    }
                });
    }


    public static void loadImageViewListener(Context mContext, String path, ImageView mImageView, RequestListener<Drawable> requstlistener) {
        Glide.with(mContext).load(path).listener(requstlistener);

    }


}
