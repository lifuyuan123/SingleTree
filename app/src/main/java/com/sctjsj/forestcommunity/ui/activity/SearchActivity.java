package com.sctjsj.forestcommunity.ui.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.baidu.speech.VoiceRecognitionService;
import com.sctjsj.basemodule.base.ui.act.BaseAppcompatActivity;
import com.sctjsj.basemodule.base.util.LogUtil;
import com.sctjsj.basemodule.base.util.StringUtil;
import com.sctjsj.forestcommunity.R;
import com.sctjsj.forestcommunity.adapter.HistoryInputAdapter;
import com.sctjsj.forestcommunity.ui.widget.Utils.Constant;
import com.sctjsj.forestcommunity.ui.widget.Utils.Eyes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import q.rorbin.qrefreshlayout.QRefreshLayout;

@Route(path = "/main/act/Search")
//搜索页面
public class SearchActivity extends BaseAppcompatActivity implements RecognitionListener {
    private static final int REQUEST_UI = 1;
    public static final int STATUS_None = 0;
    public static final int STATUS_WaitingReady = 2;
    public static final int STATUS_Ready = 3;
    public static final int STATUS_Speaking = 4;
    public static final int STATUS_Recognition = 5;
    @BindView(R.id.lin_search_NoData)
    LinearLayout linSearchNoData;
    private int status = STATUS_None;
    private long speechEndTime = -1;
    private static final int EVENT_ERROR = 11;
    private SpeechRecognizer speechRecognizer;
    @BindView(R.id.edt_search)
    EditText edtSearch;
    @BindView(R.id.lin_cancel)
    LinearLayout linCancel;
    @BindView(R.id.rv_search)
    RecyclerView rvSearch;
    @BindView(R.id.rl_search)
    QRefreshLayout rlSearch;
    @BindView(R.id.lv_search)
    ListView lvSearch;
    private static final int REQUEST_CODE = 123;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String s = (String) msg.obj;
                List<String> list = new ArrayList<>();
                for (String str : stringList) {
                    if (str.contains(s)) {
                        list.add(str);
                    }
                }
                adapter.setList(list);
            } else if (msg.what == 2) {
                rlSearch.setVisibility(View.GONE);
                lvSearch.setVisibility(View.VISIBLE);
                adapter.setList(stringList);
            } else if (msg.what == 3) {
                edtSearch.setText((String) msg.obj);
            }

            if(adapter.getList().size()>0){
                lvSearch.setVisibility(View.VISIBLE);
                linSearchNoData.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }else {
                lvSearch.setVisibility(View.GONE);
                linSearchNoData.setVisibility(View.VISIBLE);
            }

        }
    };

    private List<String> stringList = new ArrayList<>();
    private HistoryInputAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.setStatusBarLightMode(this, getResources().getColor(R.color.color_toolbar));
        initData();
        initView();
        initPermission();
    }

    private void initData() {
        stringList.add("定制家具");
        stringList.add("书桌");
        stringList.add("饭桌");
        stringList.add("椅子");
        stringList.add("衣柜");
    }

    private void initView() {
        adapter = new HistoryInputAdapter(stringList, this);
        lvSearch.setAdapter(adapter);
        rlSearch.setVisibility(View.GONE);
        lvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                edtSearch.setText(adapter.getItem(position).toString());
                //光标移到最后
                edtSearch.setSelection(adapter.getItem(position).toString().length());
            }
        });
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!StringUtil.isBlank(s.toString())) {
                    Message message = Message.obtain();
                    message.obj = s.toString();
                    message.what = 1;
                    handler.sendMessage(message);
                } else if (edtSearch.getText().toString().length() == 0 || edtSearch.getText().toString().equals("")) {
                    Message message = Message.obtain();
                    message.what = 2;
                    handler.sendMessage(message);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //百度语音
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this, new ComponentName(this, VoiceRecognitionService.class));
        speechRecognizer.setRecognitionListener(this);

    }

    @Override
    public int initLayout() {
        return R.layout.activity_search;
    }

    @Override
    public void reloadData() {

    }

    @OnClick({R.id.lin_search, R.id.lin_voice_search, R.id.lin_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //点击图标搜索
            case R.id.lin_search:
                if (!StringUtil.isBlank(edtSearch.getText().toString())) {
                    stringList.add(edtSearch.getText().toString());
                    lvSearch.setVisibility(View.GONE);
                }
                break;
            //声音输入搜索
            case R.id.lin_voice_search:
                startVoice();
                break;
            case R.id.lin_cancel:
                finish();
                break;
        }
    }

    private void startVoice() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SearchActivity.this);
        boolean api = sp.getBoolean("api", false);
        if (api) {
            switch (status) {
                case STATUS_None:
                    start();
//                    btn.setText("取消");
                    status = STATUS_WaitingReady;
                    break;
                case STATUS_WaitingReady:
                    cancel();
                    status = STATUS_None;
//                    btn.setText("开始");
                    break;
                case STATUS_Ready:
                    cancel();
                    status = STATUS_None;
//                    btn.setText("开始");
                    break;
                case STATUS_Speaking:
                    stop();
                    status = STATUS_Recognition;
//                    btn.setText("识别中");
                    break;
                case STATUS_Recognition:
                    cancel();
                    status = STATUS_None;
//                    btn.setText("开始");
                    break;
            }
        } else {
            start();
        }
    }


    @Override
    protected void onDestroy() {
        speechRecognizer.destroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            onResults(data.getExtras());
        }
    }

    public void bindParams(Intent intent) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean("tips_sound", true)) {
            intent.putExtra(Constant.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start);
            intent.putExtra(Constant.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);
            intent.putExtra(Constant.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
            intent.putExtra(Constant.EXTRA_SOUND_ERROR, R.raw.bdspeech_recognition_error);
            intent.putExtra(Constant.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);
        }
        if (sp.contains(Constant.EXTRA_INFILE)) {
            String tmp = sp.getString(Constant.EXTRA_INFILE, "").replaceAll(",.*", "").trim();
            intent.putExtra(Constant.EXTRA_INFILE, tmp);
        }
        if (sp.getBoolean(Constant.EXTRA_OUTFILE, false)) {
            intent.putExtra(Constant.EXTRA_OUTFILE, "sdcard/outfile.pcm");
        }
        if (sp.getBoolean(Constant.EXTRA_GRAMMAR, false)) {
            intent.putExtra(Constant.EXTRA_GRAMMAR, "assets:///baidu_speech_grammar.bsg");
        }
        if (sp.contains(Constant.EXTRA_SAMPLE)) {
            String tmp = sp.getString(Constant.EXTRA_SAMPLE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_SAMPLE, Integer.parseInt(tmp));
            }
        }
        if (sp.contains(Constant.EXTRA_LANGUAGE)) {
            String tmp = sp.getString(Constant.EXTRA_LANGUAGE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_LANGUAGE, tmp);
            }
        }
        if (sp.contains(Constant.EXTRA_NLU)) {
            String tmp = sp.getString(Constant.EXTRA_NLU, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_NLU, tmp);
            }
        }

        if (sp.contains(Constant.EXTRA_VAD)) {
            String tmp = sp.getString(Constant.EXTRA_VAD, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_VAD, tmp);
            }
        }
        String prop = null;
        if (sp.contains(Constant.EXTRA_PROP)) {
            String tmp = sp.getString(Constant.EXTRA_PROP, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_PROP, Integer.parseInt(tmp));
                prop = tmp;
            }
        }

        // offline asr
        {
            intent.putExtra(Constant.EXTRA_OFFLINE_ASR_BASE_FILE_PATH, "/sdcard/easr/s_1");
            if (null != prop) {
                int propInt = Integer.parseInt(prop);
                if (propInt == 10060) {
                    intent.putExtra(Constant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_Navi");
                } else if (propInt == 20000) {
                    intent.putExtra(Constant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_InputMethod");
                }
            }
            intent.putExtra(Constant.EXTRA_OFFLINE_SLOT_DATA, buildTestSlotData());
        }
    }

    private String buildTestSlotData() {
        JSONObject slotData = new JSONObject();
        JSONArray name = new JSONArray().put("李涌泉").put("郭下纶");
        JSONArray song = new JSONArray().put("七里香").put("发如雪");
        JSONArray artist = new JSONArray().put("周杰伦").put("李世龙");
        JSONArray app = new JSONArray().put("手机百度").put("百度地图");
        JSONArray usercommand = new JSONArray().put("关灯").put("开门");
        try {
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_NAME, name);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_SONG, song);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_ARTIST, artist);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_APP, app);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_USERCOMMAND, usercommand);
        } catch (JSONException e) {

        }
        return slotData.toString();
    }

    private void start() {
        edtSearch.setText("");
        print("点击了“开始”");
        Intent intent = new Intent();
        bindParams(intent);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        {

            String args = sp.getString("args", "");
            if (null != args) {
//                print("参数集：" + args);
                intent.putExtra("args", args);
            }
        }
        boolean api = sp.getBoolean("api", false);
        if (api) {
            speechEndTime = -1;
            speechRecognizer.startListening(intent);
        } else {
            intent.setAction("com.baidu.action.RECOGNIZE_SPEECH");
            startActivityForResult(intent, REQUEST_UI);
        }

        edtSearch.setText("");
    }

    private void stop() {
        speechRecognizer.stopListening();
        print("点击了“说完了”");
    }

    private void cancel() {
        speechRecognizer.cancel();
        status = STATUS_None;
        print("点击了“取消”");
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        status = STATUS_Ready;
        print("准备就绪，可以开始说话");
    }

    @Override
    public void onBeginningOfSpeech() {
        time = System.currentTimeMillis();
        status = STATUS_Speaking;
//        btn.setText("说完了");
        print("检测到用户的已经开始说话");
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        speechEndTime = System.currentTimeMillis();
        status = STATUS_Recognition;
        print("检测到用户的已经停止说话");
//        btn.setText("识别中");
    }

    @Override
    public void onError(int error) {
        time = 0;
        status = STATUS_None;
        StringBuilder sb = new StringBuilder();
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                sb.append("音频问题");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                sb.append("没有语音输入");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                sb.append("其它客户端错误");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                sb.append("权限不足");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                sb.append("网络问题");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                sb.append("没有匹配的识别结果");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                sb.append("引擎忙");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                sb.append("服务端错误");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                sb.append("连接超时");
                break;
        }
        sb.append(":" + error);
        print("识别失败：" + sb.toString());
//        btn.setText("开始");
    }

    @Override
    public void onResults(Bundle results) {
        long end2finish = System.currentTimeMillis() - speechEndTime;
        status = STATUS_None;
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        //识别成功
//        print("识别成功：" + Arrays.toString(nbest.toArray(new String[nbest.size()])));
        //设置得到的结果
        edtSearch.setText(nbest.toArray(new String[nbest.size()]).toString());
        LogUtil.e("识别成功", nbest.toArray(new String[nbest.size()]).toString());
        //json数据
        String json_res = results.getString("origin_result");
        try {
            print("origin_result=\n" + new JSONObject(json_res).toString(4));
        } catch (Exception e) {
            print("origin_result=[warning: bad json]\n" + json_res);
        }
//        btn.setText("开始");
        String strEnd2Finish = "";
        if (end2finish < 60 * 1000) {
            strEnd2Finish = "(waited " + end2finish + "ms)";
        }
        edtSearch.setText(nbest.get(0) + strEnd2Finish);
        time = 0;
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        ArrayList<String> nbest = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (nbest.size() > 0) {
            print("~临时识别结果：" + Arrays.toString(nbest.toArray(new String[0])));
            edtSearch.setText(nbest.get(0));
        }
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        switch (eventType) {
            case EVENT_ERROR:
                String reason = params.get("reason") + "";
                print("EVENT_ERROR, " + reason);
                break;
            case VoiceRecognitionService.EVENT_ENGINE_SWITCH:
                int type = params.getInt("engine_type");
                print("*引擎切换至" + (type == 0 ? "在线" : "离线"));
                break;
        }
    }

    long time;

    private void print(String msg) {
        long t = System.currentTimeMillis() - time;
        if (t > 0 && t < 100000) {
//            edtSearch.append(t + "ms, " + msg + "\n");
        } else {
//            edtSearch.append("" + msg + "\n");
        }
//        ScrollView sv = (ScrollView) txtLog.getParent();
//        sv.smoothScrollTo(0, 1000000);
        Log.e("mag", "-----" + msg);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //todo
            } else {
                // Permission Denied
                Toast.makeText(SearchActivity.this, "没有权限", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void initPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkAUDIOPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO);
            if (checkAUDIOPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO}, 123);
                return;
            }
        }
    }
}


