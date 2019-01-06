package com.qinhu.oneschool.Home.Question;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.qinhu.oneschool.DB.Answer;
import com.qinhu.oneschool.DB.MyUser;
import com.qinhu.oneschool.MyClass.ShapeCornerBgView;
import com.qinhu.oneschool.R;
import com.qinhu.oneschool.Utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

@SuppressLint("SimpleDateFormat")
public class WriteAnswerActivity extends FragmentActivity implements View.OnClickListener{
    private static final int REQUEST_CODE_PICK_IMAGE = 1023;
    private RichTextEditor editor;
    private View picture;
    private ProgressDialog progressDialog;
    private ShapeCornerBgView submitbtn;
    private  String str = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_answer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            StatusBarUtil.setStatusBarColor(WriteAnswerActivity.this,R.color.noColor); }

        editor = (RichTextEditor) findViewById(R.id.richEditor);
        ImageView backbtn = findViewById(R.id.id_writeanswer_backbtn);
        backbtn.setOnClickListener(this);
        submitbtn = findViewById(R.id.id_writeanswer_submitbtn);
        submitbtn.setOnClickListener(this);
        picture = findViewById(R.id.button1);
        picture.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        editor.hideKeyBoard();
        switch (v.getId()){
            case R.id.button1:
                // 打开系统相册
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");// 相片类型
                startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
                break;
            case R.id.id_writeanswer_submitbtn:
                List<RichTextEditor.EditData> editList = editor.buildEditData();
                //Log.d("Editor", editList.size() + "+++" + editList.get(0).inputStr);
                if (editList.size()==1&&editList.get(0).inputStr.isEmpty()){
                    Toast.makeText(WriteAnswerActivity.this,"回答不能为空！",Toast.LENGTH_SHORT).show();
                }else {
                    dealEditData(editList);
                    ShowProgress();
                }
                break;
            case R.id.id_writeanswer_backbtn:
                finish();
                break;
            default:break;
        }
    }

    /**
     * 负责处理编辑数据提交等事宜，请自行实现
     */

    private void dealEditData(List<RichTextEditor.EditData> editList){
        str = "";
        final List<String> imgPath = new ArrayList<>();
        for (RichTextEditor.EditData editData : editList){
            if (editData.inputStr != null) {
                str += "<div>" + editData.inputStr + "</div>";
            }else if (editData.imagePath != null){
                str += "<img src='" + editData.imagePath + "' />";
                imgPath.add(editData.imagePath);
            }
        }
        if (imgPath.size() == 0){
            upLoad(str);
            //Log.d("RichEditor", str);
        }else {
            final String[] filePaths = new String[10];
            for (int a = 0; a < imgPath.size();a++){
                filePaths[a] = imgPath.get(a);
            }
            BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
                @Override
                public void onSuccess(List<BmobFile> list, List<String> list1) {
                    if(list1.size() == imgPath.size()){//如果数量相等，则代表文件全部上传完成
                        for (int i = 0; i < list1.size();i++){
                            String oldString = imgPath.get(i).toString();
                            String newString = list1.get(i).toString();
                            str = str.replaceAll(oldString,newString);
                            if (i==list1.size()-1){
                                upLoad(str);
                                //Log.d("RichEditor", str);
                            }
                        }
                    }
                }
                @Override
                public void onProgress(int i, int i1, int i2, int i3) {
                }
                @Override
                public void onError(int i, String s) {
                }
            });
        }
    }

    private void upLoad(String string){
        Answer answer = new Answer();
        answer.setContent("<div id='a'>" + string+ "</div>");
        answer.setQuestion(getIntent().getStringExtra("questionId"));
        answer.setUser(BmobUser.getCurrentUser(MyUser.class));
        answer.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null){
                    Toast.makeText(WriteAnswerActivity.this,"回答成功！",Toast.LENGTH_SHORT).show();
                    CloseProgress();
                    finish();
                }else {
                    Toast.makeText(WriteAnswerActivity.this,"回答失败！" + e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_PICK_IMAGE) {
            Uri uri = data.getData();
            insertBitmap(getRealFilePath(uri));
        }
    }

    /**
     * 添加图片到富文本剪辑器
     *
     * @param imagePath
     */
    private void insertBitmap(String imagePath) {
        editor.insertImage(imagePath);
    }

    /**
     * 根据Uri获取图片文件的绝对路径
     */
    public String getRealFilePath(final Uri uri) {
        if (null == uri) {
            return null;
        }

        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = getContentResolver().query(uri,
                    new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


    public void ShowProgress(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(WriteAnswerActivity.this);
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
