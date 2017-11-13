package com.sctjsj.forestcommunity.ui.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.util.DpUtils;
import com.sctjsj.basemodule.core.img_load.PicassoUtil;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.javabean.RentalCenter;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = "/main/act/RentalDetails")
//租售详情
public class RentalDetailsActivity extends BaseAppcompatActivity {
    @Autowired(name = "bean")
    RentalCenter bean;
    @BindView(R.id.tv_rental_title)
    TextView tvRentalTitle;
    @BindView(R.id.iv_rental_detail)
    ImageView ivRentalDetail;
    @BindView(R.id.tv_houseType)
    TextView tvHouseType;
    @BindView(R.id.tv_acreage)
    TextView tvAcreage;
    @BindView(R.id.tv_Orientation)
    TextView tvOrientation;
    @BindView(R.id.tv_rental_time)
    TextView tvRentalTime;
    @BindView(R.id.tv_rental_address)
    TextView tvRentalAddress;
    @BindView(R.id.tv_rental_describe)
    TextView tvRentalDescribe;
    private HttpServiceImpl service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        service = (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        initData();
        initView();
    }

    private void initView() {
        tvRentalTitle.setText(bean.getAdress());
        tvAcreage.setText(bean.getAcreage());
        tvOrientation.setText(bean.getOrientation());
        tvRentalAddress.setText(bean.getAdress());
        tvRentalDescribe.setText(bean.getTitle());
        PicassoUtil.getPicassoObject().load(bean.getUrl())
                .resize(DpUtils.dpToPx(this, 80), DpUtils.dpToPx(this, 80))
                .into(ivRentalDetail);
    }

    private void initData() {

    }

    @Override
    public int initLayout() {
        return R.layout.activity_rental_details;
    }

    @Override
    public void reloadData() {

    }

    @OnClick(R.id.linear_back)
    public void onViewClicked() {
        finish();
    }

}
