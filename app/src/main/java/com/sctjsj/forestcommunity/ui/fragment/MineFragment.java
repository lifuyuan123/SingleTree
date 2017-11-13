package com.sctjsj.forestcommunity.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.sctjsj.basemodule.base.HttpTask.XProgressCallback;
import com.sctjsj.basemodule.base.util.DpUtils;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.core.img_load.PicassoUtil;
import com.sctjsj.basemodule.core.router_service.impl.HttpServiceImpl;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.intf.MainUrl;
import com.sctjsj.forestcommunity.javabean.UserBean;
import com.sctjsj.forestcommunity.util.UserAuthUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by haohaoliu on 2017/8/9.
 * explain:我的fragment
 */

public class MineFragment extends Fragment {
    View rootView = null;
    @BindView(R.id.tv_min_integral)
    TextView tvMinIntegral;
    @BindView(R.id.iv_min_icon)
    CircleImageView ivMinIcon;
    @BindView(R.id.tv_min_name)
    TextView tvMinName;
    @BindView(R.id.tv_min_phone)
    TextView tvMinPhone;
    @BindView(R.id.mine_notLogin)
    LinearLayout mineNotLogin;
    @BindView(R.id.mine_isLogin)
    LinearLayout mineIsLogin;
    private HttpServiceImpl service;




    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frg_mine, null);
        unbinder = ButterKnife.bind(this, rootView);
        service= (HttpServiceImpl) ARouter.getInstance().build("/basemodule/service/http").navigation();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    //初始化数据
    private void init() {
        //判断用户是否登录
        if(UserAuthUtil.isUserLogin()){
            //用户已经登录
            getUserMessage();
            mineNotLogin.setVisibility(View.GONE);
            mineIsLogin.setVisibility(View.VISIBLE);
        }else {
            mineNotLogin.setVisibility(View.VISIBLE);
            mineIsLogin.setVisibility(View.GONE);
            PicassoUtil.getPicassoObject().load(R.drawable.icon_user_default)
                    .resize(DpUtils.dpToPx(getActivity(),80),DpUtils.dpToPx(getActivity(),80))
                    .error(R.drawable.icon_user_default).into(ivMinIcon);

        }


    }




    //获取用户信息
    private void getUserMessage() {
        HashMap<String,String> body=new HashMap<>();
        body.put("id", UserAuthUtil.getUserId()+"");
        body.put("ctype","user");
        body.put("jf","thumbnail");
        service.doCommonPost(null, MainUrl.UserbaseSingleQueryUrl, body, new XProgressCallback() {
            @Override
            public void onSuccess(String result) {
                if(!StringUtil.isBlank(result)){
                    try {
                        Log.e("getUserMessage",result.toString());
                        JSONObject object=new JSONObject(result);
                        if(object.getBoolean("result")){
                            //获取成功
                            JSONObject userJson=object.getJSONObject("data");
                            UserBean bean=new UserBean();
                            if(userJson.has("thumbnail")&&userJson.getJSONObject("thumbnail").has("url")){
                                bean.setUserIcon(userJson.getJSONObject("thumbnail").getString("url"));
                            }else {
                                bean.setUserIcon("sdf");
                            }
                            bean.setUserName(userJson.getString("realName"));
                            if(userJson.has("birthday")) {
                                bean.setBirthday(userJson.getString("birthday"));
                            }else {
                                bean.setBirthday("暂未设置");
                            }
                            bean.setPhone(userJson.getString("phone"));
                            bean.setUserExp(userJson.getInt("userExp"));
                            initView(bean);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("getUserMessage",e.toString());
                    }
                }
            }

            @Override
            public void onError(Throwable ex) {

            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current) {

            }
        });



    }
    //初始化View
    private void initView(UserBean bean) {
        if(null!=bean){
            tvMinIntegral.setText("积分"+bean.getUserExp());
            PicassoUtil.getPicassoObject().load(bean.getUserIcon())
                    .resize(DpUtils.dpToPx(getActivity(),80),DpUtils.dpToPx(getActivity(),80))
                    .error(R.drawable.icon_user_default).into(ivMinIcon);
            tvMinName.setText(bean.getUserName());
            tvMinPhone.setText(bean.getPhone());
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.lin_min_edit, R.id.lin_min_setting, R.id.lin_min_userData, R.id.lin_min_integral,
            R.id.lin_min_luckdraw, R.id.lin_min_card, R.id.lin_min_follow, R.id.lin_min_foot
            , R.id.lin_integral,R.id.mine_userlayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //编辑
            case R.id.lin_min_edit:
                break;
            //设置
            case R.id.lin_min_setting:
                ARouter.getInstance().build("/main/act/user/Setting").navigation();
                break;
            //个人资料
            case R.id.lin_min_userData:
                ARouter.getInstance().build("/main/act/user/UserData").navigation();
                break;
            //我的积分
            case R.id.lin_min_integral:
                ARouter.getInstance().build("/main/act/user/IntegralRecord").navigation();
                break;
            //抽奖
            case R.id.lin_min_luckdraw:
                ARouter.getInstance().build("/main/act/user/LuckDraw").navigation();
                break;
            //卡包
            case R.id.lin_min_card:
                ARouter.getInstance().build("/main/act/user/CardPackage").navigation();
                break;
            //关注
            case R.id.lin_min_follow:
//                ARouter.getInstance().build("/main/act/user/MyConcern").navigation();
                Toast.makeText(getActivity(), "暂未开通", Toast.LENGTH_SHORT).show();
                break;
            //足迹
            case R.id.lin_min_foot:
                ARouter.getInstance().build("/main/act/user/Footmark").navigation();
                break;
            //业主积分
            case R.id.lin_integral:
                ARouter.getInstance().build("/main/act/user/OwnerIntegral").navigation();
                break;
            case R.id.mine_userlayout:
                if(!UserAuthUtil.isUserLogin()){
                    //没有登录去登录
                    ARouter.getInstance().build("/main/act/login").navigation();
                }
                break;
        }
    }

}
