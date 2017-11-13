package com.sctjsj.forestcommunity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.javabean.MenuCityBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by XiaoHaoWit on 2017/8/17.
 * 选择销售状态的适配器
 */

public class TermListAdapter extends BaseAdapter {

    private List<MenuCityBean> data;
    private LayoutInflater inflater;
    private Context mContext;

    public TermListAdapter(List<MenuCityBean> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
        this.inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_rental_dropmenue2, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MenuCityBean bean = data.get(position);
        if (bean.isCheck()) {
            holder.itemRentalDropmTxt.setTextColor(mContext.getResources().getColor(R.color.home_RadioButton_check));
        } else {
            holder.itemRentalDropmTxt.setTextColor(mContext.getResources().getColor(R.color.text_color));
        }
        holder.itemRentalDropmTxt.setText(bean.getCyitName()+"");


        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.item_rental_dropm_txt)
        TextView itemRentalDropmTxt;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
