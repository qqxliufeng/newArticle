package com.android.ql.lf.baselibaray.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.ql.lf.baselibaray.application.BaseApplication;
import com.android.ql.lf.baselibaray.component.DaggerApiServerComponent;
import com.android.ql.lf.baselibaray.data.BaseNetResult;
import com.android.ql.lf.baselibaray.interfaces.INetDataPresenter;
import com.android.ql.lf.baselibaray.present.GetDataFromNetPresent;
import com.android.ql.lf.baselibaray.ui.activity.FragmentContainerActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;


import javax.inject.Inject;

import rx.Subscription;

/**
 * @author Administrator
 * @date 2017/10/17 0017
 */

public abstract class BaseNetWorkingFragment extends BaseFragment implements INetDataPresenter {

    public final String RESULT_CODE = "code";
    public final String RESULT_OBJECT = "result";
    public final String MSG_FLAG = "msg";
    public final String SUCCESS_CODE = "200";

    @Inject
    public GetDataFromNetPresent mPresent;

    public ProgressDialog progressDialog;

    protected Subscription logoutSubscription;


//    public void registerLoginSuccessBus() {
//        subscription = RxBus.getDefault().toObservable(UserInfo.class).subscribe(new Action1<UserInfo>() {
//            @Override
//            public void call(UserInfo userInfo) {
//                onLoginSuccess(userInfo);
//            }
//        });
//    }
//
//    public void registerLogoutSuccessBus() {
//        logoutSubscription = RxBus.getDefault().toObservable(String.class).subscribe(new Action1<String>() {
//            @Override
//            public void call(String logout) {
//                if (Objects.equals(logout, UserInfo.LOGOUT_FLAG)) {
//                    onLogoutSuccess(logout);
//                }
//            }
//        });
//    }
//
//    public void onLoginSuccess(UserInfo userInfo) {
//    }
//
//    public void onLogoutSuccess(String logout) {
//    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentContainerActivity) {
            if (getParentFragment() == null) {
                GetDataFromNetPresent present = ((FragmentContainerActivity) context).getPresent();
                if (present != null) {
                    this.mPresent = present;
                } else {
                    DaggerApiServerComponent.builder().appComponent(BaseApplication.getInstance().getAppComponent()).build().inject(this);
                }
            } else {
                DaggerApiServerComponent.builder().appComponent(BaseApplication.getInstance().getAppComponent()).build().inject(this);
            }
        } else {
            DaggerApiServerComponent.builder().appComponent(BaseApplication.getInstance().getAppComponent()).build().inject(this);
        }
        if (mPresent != null) {
            this.mPresent.setNetDataPresenter(this);
        }
    }

    public void getFastProgressDialog(String message) {
        progressDialog = ProgressDialog.show(mContext, null, message);
    }

    @Override
    public void onRequestStart(int requestID) {
    }

    @Override
    public void onRequestFail(int requestID, @NotNull Throwable e) {
        Log.e("TAG", "message --->  " + e.getMessage());
        if (e instanceof RequestException && !TextUtils.isEmpty(e.getMessage())) {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, showFailMessage(requestID), Toast.LENGTH_SHORT).show();
        }
    }

    public String showFailMessage(int requestID) {
        return "请求失败";
    }

    @Override
    public <T> void onRequestSuccess(int requestID, T result) {
        handleSuccess(requestID, result);
    }

    @Override
    public void onRequestEnd(int requestID) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public <T> BaseNetResult checkResultCode(T json) {
        try {
            if (json != null) {
                JSONObject jsonObject = new JSONObject(json.toString());
                return new BaseNetResult(jsonObject.optString(RESULT_CODE), jsonObject);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public <T> void handleSuccess(int requestID, T result) {
        try {
            BaseNetResult check = checkResultCode(result);
            if (check != null) {
                if (check.code.equals(SUCCESS_CODE)) {
                    onHandleSuccess(requestID, ((JSONObject) check.obj).opt(RESULT_OBJECT));
                } else {
                    onRequestFail(requestID, new RequestException(((JSONObject) check.obj).optString(MSG_FLAG)));
                    onRequestEnd(requestID);
                }
            } else {
                onRequestFail(requestID, new RequestException());
                onRequestEnd(requestID);
            }
        } catch (Exception e) {
            onRequestFail(requestID, e);
            onRequestEnd(requestID);
        }
    }

    public void onHandleSuccess(int requestID, Object obj) {
    }

    public boolean checkedObjType(Object obj) {
        return obj instanceof JSONObject;
    }

    @Override
    public void onDestroyView() {
        unsubscribe(logoutSubscription);
        super.onDestroyView();
        if (mPresent != null) {
            mPresent.unSubscription();
            mPresent = null;
        }
    }

    public static class RequestException extends Exception {

        public RequestException() {
            super();
        }

        public RequestException(String message) {
            super(message);
        }

        @Override
        public String getMessage() {
            return "提示：" + super.getMessage();
        }
    }
}
