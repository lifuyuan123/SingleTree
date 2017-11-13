package com.sctjsj.forestcommunity.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.util.SPFUtil;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.basemodule.core.config.Tag;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.adapter.MyBaseAdapter;
import com.sctjsj.forestcommunity.adapter.SortAdapter;
import com.sctjsj.forestcommunity.db.RegionDAO;
import com.sctjsj.forestcommunity.event.ChooseCityEvent;
import com.sctjsj.forestcommunity.javabean.RegionInfo;
import com.sctjsj.forestcommunity.javabean.SortModel;

import com.sctjsj.forestcommunity.util.CharacterParser;
import com.sctjsj.forestcommunity.util.ClearEditText;
import com.sctjsj.forestcommunity.util.PinyinComparator;
import com.sctjsj.forestcommunity.widget.SideBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Route(path = "/main/act/ChoiceCity")
//选择城市
public class ChoiceCityActivity extends BaseAppcompatActivity {

    
    private List<RegionInfo> provinceList;
    private List<RegionInfo> citysList;
    private List<String> provinces;
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;
    private ClearEditText mClearEditText;
    private List<RegionInfo> mReMenCitys;//热门城市列表
    private MyBaseAdapter gvAdapter;
    private GridView mGridView;
    private RelativeLayout iv_left;
    
    @Autowired(name = "type")
    int type=0;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        initViews();
        init();


    }

    private void init() {

        if(type==2){
            //从租售中心跳转过来的
            mClearEditText.setVisibility(View.GONE);
            mGridView.setVisibility(View.GONE);
        }

    }

    @Override
    public int initLayout() {
        return R.layout.activity_choice_city;
    }

    @Override
    public void reloadData() {

    }


    private void initData() {
        provinceList = RegionDAO.getProvencesOrCity(1);
        provinceList.addAll(RegionDAO.getProvencesOrCity(2));
        citysList = new ArrayList<RegionInfo>();
        mReMenCitys = new ArrayList<RegionInfo>();
        provinces = new ArrayList<String>();
        for (RegionInfo info : provinceList) {
            provinces.add(info.getName().trim());
            Log.e("initData",info.getName());
        }

        mReMenCitys.add(new RegionInfo(2, 1,"北京"));
        mReMenCitys.add(new RegionInfo(25,1, "上海"));
        mReMenCitys.add(new RegionInfo(77,6, "深圳"));
        mReMenCitys.add(new RegionInfo(76, 6,"广州"));
        mReMenCitys.add(new RegionInfo(197, 14,"长沙"));
        mReMenCitys.add(new RegionInfo(343, 1,"天津"));

    }

    private void initViews() {
      /*  iv_left = (RelativeLayout) findViewById(R.id.iv_left);*/
        View view = View.inflate(this, R.layout.head_city_list, null);
        mGridView = (GridView) view.findViewById(R.id.id_gv_remen);
        gvAdapter = new MyGridViewAdapter(this,mReMenCitys);
        mGridView.setAdapter(gvAdapter);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                if(s.equals("热门")){
                    sortListView.setSelection(0);
                }
                int position = adapter.getPositionForSection(s.charAt(0));
                if(position != -1){
                    sortListView.setSelection(position);
                }

            }
        });

        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        sortListView.addHeaderView(view);
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //这里要利用adapter.getItem(position)来获取当前position所对应的对象
                Toast.makeText(getApplication(), ((SortModel)adapter.getItem(position-1)).getName(), Toast.LENGTH_SHORT).show();
                hideSoftInput(mClearEditText.getWindowToken());

                //保存选中的值
                String city=((SortModel)adapter.getItem(position-1)).getName().toString();
                if(!StringUtil.isBlank(city)){
                    SPFUtil.put(Tag.TAG_CITY,city);
                    EventBus.getDefault().post(new ChooseCityEvent());

                }
                Toast.makeText(ChoiceCityActivity.this, ((SortModel)adapter.getItem(position-1)).getName(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

//		SourceDateList = filledData(getResources().getStringArray(R.array.date));
        SourceDateList = filledData(provinceList);

        // 根据a-z进行排序源数据43

        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new SortAdapter(this, SourceDateList);
        sortListView.setAdapter(adapter);


        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


       /* iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput(mClearEditText.getWindowToken());
                CitySelecterActivity.this.finish();
            }
        });*/

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//				String cityName = mReMenCitys.get(position).getName();
//				hideSoftInput(mClearEditText.getWindowToken());
//				Intent data = new Intent();
//			    data.putExtra("cityName", cityName);
//				setResult(1110, data);
//				CitySelecterActivity.this.finish();
            }
        });
    }


    /**
     * 为ListView填充数据
     * @param date
     * @return
     */
    private List<SortModel> filledData(List<RegionInfo> date){
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for(int i=0; i<date.size(); i++){
            SortModel sortModel = new SortModel();
            sortModel.setName(date.get(i).getName());
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(date.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if(sortString.matches("[A-Z]")){
                sortModel.setSortLetters(sortString.toUpperCase());
            }else{
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * @param filterStr
     */
    private void filterData(String filterStr){
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if(TextUtils.isEmpty(filterStr)){
            filterDateList = SourceDateList;
        }else{
            if(!provinces.contains(filterStr)){
                filterDateList.clear();
                for(SortModel sortModel : SourceDateList){
                    String name = sortModel.getName();
                    if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
                        filterDateList.add(sortModel);
                    }
                }
            }else{
                filterDateList.clear();
                for(int i = 0;i<provinceList.size();i++){
                    String name = provinceList.get(i).getName();
                    if(name.equals(filterStr)){
                        filterDateList.addAll(filledData(RegionDAO.getProvencesOrCityOnParent(provinceList.get(i).getId())));
                    }
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }
    private class MyGridViewAdapter extends MyBaseAdapter<RegionInfo, GridView> {
        private LayoutInflater inflater;
        public MyGridViewAdapter(Context ct, List<RegionInfo> list) {
            super(ct, list);
            inflater = LayoutInflater.from(ct);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView==null){
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_remen_city, null);
                holder.id_tv_cityname = (TextView) convertView.findViewById(R.id.id_tv_cityname);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            RegionInfo info = mReMenCitys.get(position);
            holder.id_tv_cityname.setText(info.getName());
            return convertView;
        }
        class ViewHolder{
            TextView id_tv_cityname;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    protected void hideSoftInput(IBinder token)
    {
        if (token != null)
        {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
