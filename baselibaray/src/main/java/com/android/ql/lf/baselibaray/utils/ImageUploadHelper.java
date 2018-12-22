package com.android.ql.lf.baselibaray.utils;


import com.android.ql.lf.baselibaray.application.BaseApplication;
import com.android.ql.lf.baselibaray.data.ImageBean;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;

/**
 * Created by lf on 2017/11/24 0024.
 *
 * @author lf on 2017/11/24 0024
 */

public class ImageUploadHelper {

    private OnImageUploadListener onImageUploadListener;

    public static MultipartBody.Builder createMultipartBody() {
        return new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("token", BaseConfig.md5Token);
    }

    public ImageUploadHelper(OnImageUploadListener onImageUploadListener) {
        this.onImageUploadListener = onImageUploadListener;
    }



    public void upload(final ArrayList<ImageBean> list) {
        if (list != null && !list.isEmpty()) {
            File dir = new File(BaseConfig.IMAGE_PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            ArrayList<String> tempPath = new ArrayList<>();
            for (ImageBean imageBean : list) {
                tempPath.add(imageBean.getUriPath());
            }
            Observable.just(tempPath).map(new Func1<ArrayList<String>, ArrayList<File>>() {
                @Override
                public ArrayList<File> call(ArrayList<String> list) {
                    try {
                        return (ArrayList<File>) Luban
                                .with(BaseApplication.getInstance())
                                .ignoreBy(100)
                                .setTargetDir(BaseConfig.IMAGE_PATH)
                                .load(list)
                                .get();
                    } catch (IOException e) {
                        return null;
                    }
                }
            }).subscribeOn(Schedulers.io())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            if (onImageUploadListener != null) {
                                onImageUploadListener.onActionStart();
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ArrayList<File>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (onImageUploadListener != null) {
                                onImageUploadListener.onActionFailed();
                            }
                        }

                        @Override
                        public void onNext(ArrayList<File> files) {
                            if (onImageUploadListener != null && files != null) {
                                MultipartBody.Builder builder = ImageUploadHelper.createMultipartBody();
                                for (int i = 0; i < files.size(); i++) {
                                    File file = files.get(i);
                                    builder.addFormDataPart("img", file.getName(), RequestBody.create(MultipartBody.FORM, file));
                                }
                                onImageUploadListener.onActionEnd(builder);
                            } else {
                                if (onImageUploadListener != null) {
                                    onImageUploadListener.onActionFailed();
                                }
                            }
                        }
                    });
        }
    }

    public void uploadMultiImage(final ArrayList<ImageBean> list) {
        if (list != null && !list.isEmpty()) {
            File dir = new File(BaseConfig.IMAGE_PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            ArrayList<String> tempPath = new ArrayList<>();
            for (ImageBean imageBean : list) {
                tempPath.add(imageBean.getUriPath());
            }
            Observable.just(tempPath).map(new Func1<ArrayList<String>, ArrayList<File>>() {
                @Override
                public ArrayList<File> call(ArrayList<String> list) {
                    try {
                        return (ArrayList<File>) Luban
                                .with(BaseApplication.getInstance())
                                .ignoreBy(100)
                                .setTargetDir(BaseConfig.IMAGE_PATH)
                                .load(list)
                                .get();
                    } catch (IOException e) {
                        return null;
                    }
                }
            }).subscribeOn(Schedulers.io())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            if (onImageUploadListener != null) {
                                onImageUploadListener.onActionStart();
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ArrayList<File>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (onImageUploadListener != null) {
                                onImageUploadListener.onActionFailed();
                            }
                        }

                        @Override
                        public void onNext(ArrayList<File> files) {
                            if (onImageUploadListener != null && files != null) {
                                MultipartBody.Builder builder = ImageUploadHelper.createMultipartBody();
                                for (int i = 0; i < files.size(); i++) {
                                    File file = files.get(i);
                                    builder.addFormDataPart("img[]", file.getName(), RequestBody.create(MultipartBody.FORM, file));
                                }
                                onImageUploadListener.onActionEnd(builder);
                            } else {
                                if (onImageUploadListener != null) {
                                    onImageUploadListener.onActionFailed();
                                }
                            }
                        }
                    });
        }
    }

    public void upload(final ArrayList<ImageBean> list, final ArrayList<String> keys) {
        if (list != null && !list.isEmpty()) {
            File dir = new File(BaseConfig.IMAGE_PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            ArrayList<String> tempPath = new ArrayList<>();
            for (ImageBean imageBean : list) {
                tempPath.add(imageBean.getUriPath());
            }
            Observable.just(tempPath).map(new Func1<ArrayList<String>, ArrayList<File>>() {
                @Override
                public ArrayList<File> call(ArrayList<String> list) {
                    try {
                        return (ArrayList<File>) Luban
                                .with(BaseApplication.getInstance())
                                .ignoreBy(100)
                                .setTargetDir(BaseConfig.IMAGE_PATH)
                                .load(list)
                                .get();
                    } catch (IOException e) {
                        return null;
                    }
                }
            }).subscribeOn(Schedulers.io())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            if (onImageUploadListener != null) {
                                onImageUploadListener.onActionStart();
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ArrayList<File>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (onImageUploadListener != null) {
                                onImageUploadListener.onActionFailed();
                            }
                        }

                        @Override
                        public void onNext(ArrayList<File> files) {
                            if (onImageUploadListener != null && files != null && keys!=null && keys.size() == files.size()) {
                                MultipartBody.Builder builder = ImageUploadHelper.createMultipartBody();
                                for (int i = 0; i < files.size(); i++) {
                                    File file = files.get(i);
                                    builder.addFormDataPart(keys.get(i), file.getName(), RequestBody.create(MultipartBody.FORM, file));
                                }
                                onImageUploadListener.onActionEnd(builder);
                            } else {
                                if (onImageUploadListener != null) {
                                    onImageUploadListener.onActionFailed();
                                }
                            }
                        }
                    });
        }
    }



    public interface OnImageUploadListener {

        public void onActionStart();

        public void onActionEnd(MultipartBody.Builder builder);

        public void onActionFailed();
    }

}
