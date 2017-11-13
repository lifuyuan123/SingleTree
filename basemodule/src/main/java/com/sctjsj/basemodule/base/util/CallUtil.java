package com.sctjsj.basemodule.base.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.sctjsj.basemodule.base.constant.code;
import com.sctjsj.basemodule.base.util.permission.EasyPermissionsEx;


/**
 * Created by mayikang on 17/5/12.
 */

public class CallUtil {


    public static  void makeCall(Context context, String num) {

        if (StringUtil.isBlank(num)) {
            Toast.makeText(context, "要拨打的号码为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (EasyPermissionsEx.hasPermissions(context, Manifest.permission.CALL_PHONE)) {
            //已经授权
            Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + num));
            context.startActivity(intent);
        }else {
            EasyPermissionsEx.requestPermissions(context,"拨打电话需要授予拨号权限", code.REQUEST_CALL_PERMISSION,Manifest.permission.CALL_PHONE);
        }


    }

}
