package com.android.ql.lf.article.wxapi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.ql.lf.baselibaray.utils.BaseConfig;
import com.android.ql.lf.baselibaray.utils.RxBus;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


/**
 * Created by lf on 2017/11/28 0028.
 *
 * @author lf on 2017/11/28 0028
 */

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private IWXAPI iwxapi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iwxapi = WXAPIFactory.createWXAPI(this, BaseConfig.WX_APP_ID, true);
        iwxapi.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (baseResp instanceof SendAuth.Resp) {
                    RxBus.getDefault().post(baseResp);
                } else if (baseResp instanceof SendMessageToWX.Resp) {
                    Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show();
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (baseResp instanceof SendMessageToWX.Resp) {
                    Toast.makeText(this, "分享取消", Toast.LENGTH_SHORT).show();
                }
                //发送取消
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Toast.makeText(this, "用户拒绝", Toast.LENGTH_SHORT).show();
                //发送被拒绝
                break;
            default:
                //发送返回
                break;
        }
        finish();
    }
}
