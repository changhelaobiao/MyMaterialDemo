package com.toby.mymaterialdemo.model;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.toby.mymaterialdemo.ui.activity.login.RegisterActivity;
import com.toby.mymaterialdemo.utils.LogUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by toby on 2015/12/23.
 */
public class SmsObserver extends ContentObserver {

    private Context context;
    private Handler handler;

    public SmsObserver(Context context, Handler handler) {
        super(handler);
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        LogUtils.d("----onChange--->" + uri.toString());
        String code = "";
        if (uri.toString().equals("content://sms/raw")) {
            // onChange会执行两次，第一次执行时短信内容还未入库
            return;
        }
        Uri inboxUri = Uri.parse("content://sms/inbox");
        Cursor cursor = context.getContentResolver().query(inboxUri, null, null, null, "date desc");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String address = cursor.getString(cursor.getColumnIndex("address"));
                String body = cursor.getString(cursor.getColumnIndex("body"));
                LogUtils.d("-----address---->" + address);
                LogUtils.d("----body---->" + body);

                Pattern pattern = Pattern.compile("(\\d{6})");
                Matcher matcher = pattern.matcher(body);
                if (matcher.find()) {
                    code = matcher.group(0);
                    LogUtils.d("----code---->" + code);
                    handler.obtainMessage(RegisterActivity.MSG_RECEIVED_CODE, code).sendToTarget();
                }
            }
            cursor.close();
        }
    }

}
