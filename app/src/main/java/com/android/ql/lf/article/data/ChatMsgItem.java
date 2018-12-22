package com.android.ql.lf.article.data;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by lf on 18.11.7.
 *
 * @author lf on 18.11.7
 */
public class ChatMsgItem implements MultiItemEntity {

    public static final int FROM_MSG_FLAG = 0;
    public static final int SEND_MSG_FLAG = 1;

    private int mType = FROM_MSG_FLAG;

    @Override
    public int getItemType() {
        return mType;
    }

    public void setType(int mType) {
        this.mType = mType;
    }
}
