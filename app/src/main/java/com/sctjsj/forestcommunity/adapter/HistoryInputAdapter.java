package com.sctjsj.forestcommunity.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sctjsj.forestcommunity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lifuy on 2017/8/16.
 */

public class HistoryInputAdapter extends BaseAdapter {

    private List<String> list=new ArrayList<>();
    private Context context;

    public HistoryInputAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.history_input_item,parent,false);
            viewHolder.textView= (TextView) convertView.findViewById(R.id.tv_history_item);
            viewHolder.linearLayout= (LinearLayout) convertView.findViewById(R.id.lin_history_item);
            convertView.setTag(viewHolder);
        }else {
           viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(list.get(position));
        return convertView;
    }


    class ViewHolder{
        TextView textView;
        LinearLayout linearLayout;
    }
}
