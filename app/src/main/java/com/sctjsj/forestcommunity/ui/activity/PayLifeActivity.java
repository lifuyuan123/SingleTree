package com.sctjsj.forestcommunity.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;

import butterknife.OnClick;

@Route(path = "/main/act/PayLife")
//生活缴费
public class PayLifeActivity extends BaseAppcompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
    }

    @Override
    public int initLayout() {
        return R.layout.activity_pay_life;
    }

    @Override
    public void reloadData() {

    }

    @OnClick({R.id.linear_back, R.id.lin_water, R.id.lin_electric, R.id.lin_gas, R.id.lin_tv, R.id.lin_fixed_phone, R.id.lin_wifi,R.id.lin_property})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.linear_back:
                finish();
                break;
            //水费
            case R.id.lin_water:
                Toast.makeText(this, "水费", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("alipays://platformapi/startapp?appId=20000193");
                intent.setData(content_url);
                startActivity(intent);
                break;
            //电费
            case R.id.lin_electric:
                Toast.makeText(this, "电费", Toast.LENGTH_SHORT).show();
                break;
            //燃气费
            case R.id.lin_gas:
                Toast.makeText(this, "燃气费", Toast.LENGTH_SHORT).show();
                break;
            //有线电视
            case R.id.lin_tv:
                Toast.makeText(this, "有线电视", Toast.LENGTH_SHORT).show();
                break;
            //固话
            case R.id.lin_fixed_phone:
                Toast.makeText(this, "固话", Toast.LENGTH_SHORT).show();
                break;
            //宽带
            case R.id.lin_wifi:
                Toast.makeText(this, "宽带", Toast.LENGTH_SHORT).show();
                break;
            //物业费
            case R.id.lin_property:
                Toast.makeText(this, "物业费", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
