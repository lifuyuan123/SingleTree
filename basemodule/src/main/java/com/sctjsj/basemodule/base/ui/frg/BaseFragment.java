package com.sctjsj.basemodule.base.ui.frg;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mayikang on 17/2/7.
 */

/**
 * 封装的 Fragment 基类
 */
public abstract class BaseFragment extends Fragment
{
    /**宿主的activity**/
    protected FragmentActivity mActivity;

    /**根view**/
    protected View mRootView;

    /**Fg是否对用户可见**/
    protected boolean mIsVisible;

    /**
     * 是否加载完成
     * 当执行完oncreatview方法后即为true，标识碎片加载完成
     */
    protected boolean mIsPrepare;

    /**
     * 碎片和 Activity 绑定
     * @param context
     */
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mActivity = getActivity();
    }

    /**
     * 加载碎片
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        //1.加载布局 XML 文件
        mRootView = inflater.inflate(setLayoutResouceId(), container, false);
        //2.接收从别的地方传过来的参数，如 Intent 跳转
        initData(getArguments());
        //3.绑定 Fragment中的控件
        initView();
        //4.设置碎片已加载完
        mIsPrepare = true;
        //5.懒加载数据
        onLazyLoad();
        //6.设置监听
        setListener();

        return mRootView;
    }



    /**
     * 设置监听事件
     */
    protected void setListener()
    {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        this.mIsVisible = isVisibleToUser;

        if (isVisibleToUser)
        {
            onVisibleToUser();
        }
    }

    /**
     * 用户可见时执行的操作
     */
    protected void onVisibleToUser() {
        //只有碎片加载完成和可见的时候，执行懒加载数据
        if (mIsPrepare && mIsVisible)
        {
            onLazyLoad();
        }
    }

    /**
     * 设置根布局资源id
     * @return
     */
    protected abstract int setLayoutResouceId();

    /**
     * 初始化数据
     * @param arguments 接收到的从其他地方传递过来的参数
     */
    protected abstract void initData(Bundle arguments);

    /**
     * 初始化View
     */
    protected abstract void initView();

    /**
     * 懒加载，仅当用户可见切view初始化结束后才会执行
     */
    protected abstract void onLazyLoad();

    @SuppressWarnings("unchecked")
    protected <T extends View> T findViewById(int id)
    {
        if (mRootView == null)
        {
            return null;
        }

        return (T) mRootView.findViewById(id);
    }


}