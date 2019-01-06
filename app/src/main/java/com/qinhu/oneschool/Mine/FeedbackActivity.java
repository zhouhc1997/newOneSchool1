package com.qinhu.oneschool.Mine;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.qinhu.oneschool.DB.Device;
import com.qinhu.oneschool.DB.MyUser;
import com.qinhu.oneschool.MyClass.ShapeCornerBgView;
import com.qinhu.oneschool.R;
import com.qinhu.oneschool.Utils.StatusBarUtil;
import com.qinhu.oneschool.Utils.Tools;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

public class FeedbackActivity extends AppCompatActivity {
    private GridView gv;
    private EditText ed;
    private TextView tv;
    private ProgressDialog progressDialog;
    private ShapeCornerBgView punish;

    private String[] pics = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            StatusBarUtil.setStatusBarColor(FeedbackActivity.this, R.color.white); }

        TextView title = findViewById(R.id.id_mheader_title);
        title.setText("意见反馈");
        ImageView backbtn = (ImageView) findViewById(R.id.id_mheader_backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 hintKbTwo();
                 finish();
             }
         });

        ed = (EditText) findViewById(R.id.id_feedback_edit);
        punish = findViewById(R.id.id_feedback_punish);
        punish.setClickable(false);
        tv = findViewById(R.id.id_feedback_num);
        gv = (GridView) findViewById(R.id.id_feedback_gridview);
        //为GridView设置适配器
        gv.setAdapter(new MyAdapter(this));
        //注册监听事件
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                if (position == 0){
                    PictureSelector.create(FeedbackActivity.this)
                            .openGallery(PictureMimeType.ofImage())
                            .maxSelectNum(5)
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                }else {
                    pics = remove(pics,position - 1);
                }
                gv.setAdapter(new MyAdapter(FeedbackActivity.this));
            }
        });

        ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @SuppressLint("NewApi")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (ed.getText().length()>0){
                    punish.setClickable(true);
                    punish.setBgColor(getColor(R.color.selected));
                }else {
                    punish.setClickable(false);
                    punish.setBgColor(getColor(R.color.no_selected));
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                tv.setText(String.valueOf(editable.length()) + "/200");
            }
        });

        punish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFeedback();
            }
        });
    }

    private void uploadFeedback() {
        if (ed.getText().toString().isEmpty()){
            Tools.warntoast(FeedbackActivity.this,"意见反馈内容不能为空!");
        }else if (ed.getText().toString().length() > 200){
            Tools.warntoast(FeedbackActivity.this,"反馈内容字数不能超过200!");
        }else if (pics.length == 0){
            Tools.warntoast(FeedbackActivity.this,"请至少上传一张图片哟！");
        }else {
            ShowProgress();
            uploadImages();
        }
    }

    private void uploadImages() {
        BmobFile.uploadBatch(pics, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> files, List<String> urls) {
                if(urls.size()==pics.length){//如果数量相等，则代表文件全部上传完成
                    //do something
                    Device device = new Device();
                    device.setContent(ed.getText().toString());
                    device.setImgPath(urls);
                    device.setUser(BmobUser.getCurrentUser(MyUser.class));
                    device.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null){
                                CloseProgress();
                                Toast.makeText(FeedbackActivity.this,"感谢您的反馈！",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                }
            }
            @Override
            public void onError(int statuscode, String errormsg) {
                CloseProgress();
            }

            @Override
            public void onProgress(int curIndex, int curPercent, int total,int totalPercent) {
                //1、curIndex--表示当前第几个文件正在上传
                //2、curPercent--表示当前上传文件的进度值（百分比）
                //3、total--表示总的上传文件数
                //4、totalPercent--表示总的上传进度（百分比）
            }
        });
    }


    // 图片、视频、音频选择结果回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    pics =new String[selectList.size()];
                    for(int i=0,size=selectList.size();i<size;i++)
                        pics[i]=selectList.get(i).getPath();
                    gv.setAdapter(new MyAdapter(FeedbackActivity.this));
                    break;
            }
        }
    }

    //自定义适配器
    class MyAdapter extends BaseAdapter {
        //上下文对象
        private Context context;
        //图片数组

        MyAdapter(Context context) {
            this.context = context;
        }

        public int getCount() {
            return pics.length + 1;
        }

        public Object getItem(int item) {
            return item;
        }

        public long getItemId(int id) {
            return id;
        }

        //创建View方法
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(FeedbackActivity.this);
                imageView.setLayoutParams(new GridView.LayoutParams(len(),  len()));//设置ImageView对象布局
                imageView.setAdjustViewBounds(false);//设置边界对齐
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//设置刻度的类型
            } else {
                imageView = (ImageView) convertView;
            }
            if (position == 0){
                Glide.with(FeedbackActivity.this).load(R.drawable.camerabox).into(imageView);
            }else {
                Glide.with(FeedbackActivity.this).load(pics[position - 1]).into(imageView);
            }
            return imageView;
        }

    }

    //根据下标删除字符串数组中的一个元素
    private String[] remove(String[] arr, int num) {
        String[] tmp = new String[arr.length - 1];

        int idx = 0;
        boolean hasRemove = false;
        for (int i = 0,len = arr.length; i < len; i++) {
            if (!hasRemove && i == num) {
                hasRemove = true;
                continue;
            }
            tmp[idx++] = arr[i];
        }
        return tmp;
    }

    private int len() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        return (width-70)/4;
    }

    //此方法只是关闭软键盘 可以在finish之前调用一下
    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&getCurrentFocus()!=null){
            if (getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public void ShowProgress(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(FeedbackActivity.this);
            progressDialog.setMessage("正在上传，请稍后....");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    public void CloseProgress(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

}
