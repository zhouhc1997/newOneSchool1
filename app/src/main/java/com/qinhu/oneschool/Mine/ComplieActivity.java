package com.qinhu.oneschool.Mine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.qinhu.oneschool.DB.MyUser;
import com.qinhu.oneschool.Litepal.LoaclUser;
import com.qinhu.oneschool.MyClass.BaseDialog;
import com.qinhu.oneschool.MyClass.CircleImageView;
import com.qinhu.oneschool.MyClass.PickerView;
import com.qinhu.oneschool.R;
import com.qinhu.oneschool.Utils.StatusBarUtil;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class ComplieActivity extends Activity implements View.OnClickListener{
    private ImageView backbnt;
    private CircleImageView avatar;
    private LinearLayout nicknamebox;
    private LinearLayout avatarbox;
    private LinearLayout birthdaybox;
    private LinearLayout emailbox;
    private LinearLayout sexbox;
    private LinearLayout signbox;

    private TextView nickname;
    private TextView birthday;
    private TextView email;
    private TextView sex;
    private TextView signture;

    private String year;
    private String month;
    private String day;

    private BmobFile bmobFile;

    private MyUser myUser;
    private BaseDialog dialog;

    private List<String> YearData = new ArrayList<String>();
    private List<String> MonthData = new ArrayList<String>();
    private List<String> DayData = new ArrayList<String>();
    private PickerView DayView;

    /* 请求识别码 */
    private static final int PHOTO_REQUEST_GALLERY = 1;// 从相册中选择

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complie);
        StatusBarUtil.noColorBar(ComplieActivity.this);

        initView();
        initEvent();
    }
    private void initView() {
        avatar = (CircleImageView) findViewById(R.id.id_complie_avatar);
        nickname = (TextView) findViewById(R.id.id_complie_nickname);
        birthdaybox = (LinearLayout) findViewById(R.id.id_complie_birthdaybox);
        birthday = (TextView) findViewById(R.id.id_complie_birthday);
        email = (TextView) findViewById(R.id.id_complie_mail);
        sex = (TextView) findViewById(R.id.id_complie_sex);
        signture = (TextView)findViewById(R.id.id_complie_sign);

        nicknamebox = findViewById(R.id.id_complie_nicknamebox);
        avatarbox = findViewById(R.id.id_complie_avatarbox);
        emailbox = findViewById(R.id.id_complie_emailbox);
        backbnt = findViewById(R.id.id_complie_backbtn);
        sexbox = findViewById(R.id.id_complie_sexbox);
        signbox = findViewById(R.id.id_complie_signbox);

        myUser = BmobUser.getCurrentUser(MyUser.class);
        sex.setText(myUser.getSex());
        nickname.setText(myUser.getNickname());
        birthday.setText(myUser.getBirthday());
        email.setText(myUser.getEmail());
        signture.setText(myUser.getSignturn());
        Glide.with(ComplieActivity.this)
                            .load(myUser.getAvatar())
                            .into(avatar);

    }

    private void initEvent() {
        avatar.setOnClickListener(this);
        backbnt.setOnClickListener(this);
        avatarbox.setOnClickListener(this);
        sexbox.setOnClickListener(this);
        birthdaybox.setOnClickListener(this);
        nicknamebox.setOnClickListener(this);
        emailbox.setOnClickListener(this);
        signbox.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.id_complie_backbtn:
                finish();
                break;
            case R.id.id_complie_avatarbox:
                gallery();
                break;
            case R.id.id_complie_nicknamebox:
                //提示弹窗
                initNicknameEditDialog();
                break;
            case R.id.id_complie_sexbox:
                //提示弹窗
                initSexEditDialog();
                break;
            case R.id.id_complie_birthdaybox:
                initWheelYearMonthDayDialog();
                break;
            case R.id.id_complie_emailbox:
                initEmailEditDialog();
                break;
            case R.id.id_complie_signbox:
                initSignEditDialog();
                break;
             default:break;
        }
    }

    /*
     * 从相册获取
     */
    public void gallery() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            Glide.with(ComplieActivity.this).load(data.getData()).into(avatar);
            if (data != null) {
                Uri uri = data.getData();
                initUCrop(uri);
            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            if (data != null) {
                final MyUser user= BmobUser.getCurrentUser(MyUser.class);
                avatar.setImageURI(UCrop.getOutput(data));
                bmobFile = new BmobFile(uri2File(UCrop.getOutput(data)));
                bmobFile.uploadblock(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            MyUser user1=new MyUser();
                            user1.setAvatar(bmobFile.getFileUrl());
                            user1.update(user.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        user.setAvatar(bmobFile.getFileUrl());
                                        LoaclUser loaclUser=new LoaclUser();
                                        loaclUser.setAvatar(bmobFile.getFileUrl());
                                        loaclUser.updateAll("account=? and username=?",user.getUsername(),user.getUsername());
                                        Toast.makeText(getApplicationContext(),"修改成功",Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getApplicationContext(),"上传失败,请检查网络并重试",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(getApplicationContext(),"上传失败,请检查网络并重试",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onProgress(Integer value) {
                        // 返回的上传进度（百分比）
                    }
                });
                //检查是否拍了照片，如果拍了则删除。
               // deleteIcon(imgPath);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private File uri2File(Uri uri) {
        String img_path;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = managedQuery(uri, proj, null,
                null, null);
        if (actualimagecursor == null) {
            img_path = uri.getPath();
        } else {
            int actual_image_column_index = actualimagecursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            img_path = actualimagecursor
                    .getString(actual_image_column_index);
        }
        File file = new File(img_path);
        return file;
    }

    private void initUCrop(Uri uri) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        long time = System.currentTimeMillis();
        String imageName = timeFormatter.format(new Date(time));

        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), imageName + ".jpeg"));

        UCrop.Options options = new UCrop.Options();
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        options.setToolbarColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        options.setStatusBarColor(ActivityCompat.getColor(this, R.color.colorPrimaryDark));

        options.setMaxScaleMultiplier(5);
        options.setImageToCropBoundsAnimDuration(666);
        options.setCircleDimmedLayer(true);

        UCrop.of(uri, destinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(200, 200)
                .withOptions(options)
                .start(this);
    }

    private void initSexEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ComplieActivity.this);
        //    指定下拉列表的显示数据
        final String[] sexs = {"男", "女", "保密"};
        //    设置一个下拉的列表选择项
        builder.setItems(sexs, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                sex.setText(sexs[which]);
                refreshSex(sexs[which]);
            }
        });
        AlertDialog alertDialog = builder.show();
        alertDialog.setCanceledOnTouchOutside(true);
    }

    private void initWheelYearMonthDayDialog() {
        View view = getLayoutInflater().inflate(R.layout.date_picker_layout, null);
        PickerView YearView = (PickerView) view.findViewById(R.id.id_pickerview_year);
        PickerView MonthView = (PickerView) view.findViewById(R.id.id_pickerview_month);
        DayView = (PickerView) view.findViewById(R.id.id_pickerview_day);

        year = "1998";
        month = "1";
        day = "1";

        YearData.clear();
        MonthData.clear();
        DayData.clear();

        for (int y = 1990;y <= 2005;y++){
            for (int m = 1;m <= 12;m++){
                for (int d = 1; d <= 31;d++){
                    DayData.add(""+ d);
                }
                MonthData.add("" + m);
            }
            YearData.add("" + y);
        }

        YearView.setData(YearData,Integer.parseInt(year)-1990);
        DayView.setData(DayData,Integer.parseInt(day)-1);
        YearView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                year = text;
                DayData.clear();
                int n = Integer.parseInt(day)-1;
                for (int d = 1, cal = calculate(Integer.parseInt(year),Integer.parseInt(month)); d <= cal; d++){
                    DayData.add(""+ d);
                }
                if (DayData.size() <= n){
                    n = DayData.size()-1;;
                }
                DayView.setData(DayData,n);
            }
        });
        MonthView.setData(MonthData,Integer.parseInt(month)-1);
        MonthView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                month = text;
                DayData.clear();
                int n = Integer.parseInt(day)-1;
                for (int d = 1, cal = calculate(Integer.parseInt(year),Integer.parseInt(month)); d <= cal; d++){
                    DayData.add(""+ d);
                }
                if (DayData.size() <= n){
                    n = DayData.size()-1;
                }
                DayView.setData(DayData,n);
            }
        });

        DayView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                    day = text;
            }
        });

        dialog = new BaseDialog.Builder(this).setTitle("编辑生日")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       final String str = year + "/" + month + "/" + day;
                        if (!str.isEmpty()){
                            MyUser p2 = new MyUser();
                            p2.setBirthday(str);
                            p2.update(myUser.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        birthday.setText(str);
                                    } else {
                                        Toast.makeText(ComplieActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        dialog.dismiss();
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {

                    }
                }).setView(view).create();

        dialog.show();
    }

    private int calculate(int y, int m) {
        if(m == 1 || m == 3 || m ==5 || m==7 || m==8 || m == 10 || m == 12){
            return 31;
        }else if ( m == 2){
            if( isRunYear(y) ){
                return 29;
            }else {
                return 28;
            }
        }else {
            return 30;
        }
    }

    private boolean isRunYear(int y) {
        if((y%4==0&&y%100!=0)||y%400==0) {
            return true;
        }else {
            return false;
        }
    }

    private void initNicknameEditDialog(){
        View view = getLayoutInflater().inflate(R.layout.dialog_input_amount, null);
        final EditText amountEdit = (EditText) view.findViewById(R.id.dialog_et_amount);
        amountEdit.setHint("请输入");
        dialog = new BaseDialog.Builder(this).setTitle("编辑昵称")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String str = amountEdit.getText().toString();
                        if (str.isEmpty()){

                        }else {
                            refreshNickname(str);
                        }
                        dialog.dismiss();
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {

                    }
                }).setView(view).create();
        dialog.show();
    }

    private void initEmailEditDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_input_amount, null);
        final EditText amountEdit = (EditText) view.findViewById(R.id.dialog_et_amount);
        amountEdit.setHint("请输入");
        dialog = new BaseDialog.Builder(this).setTitle("编辑邮箱")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String str = amountEdit.getText().toString();
                        if (str.isEmpty()){

                        }else {
                            refreshEmail(str);
                        }
                        dialog.dismiss();
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {

                    }
                }).setView(view).create();
        dialog.show();
    }

    private void initSignEditDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_input_amount, null);
        final EditText amountEdit = (EditText) view.findViewById(R.id.dialog_et_amount);
        amountEdit.setHint("请输入");
        dialog = new BaseDialog.Builder(this).setTitle("编辑个性签名")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String sign = amountEdit.getText().toString();
                        refreshSign(sign);
                        dialog.dismiss();
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {

                    }
                }).setView(view).create();
        dialog.show();
    }

    public  void refreshEmail(final String _email){
        final MyUser myUser= BmobUser.getCurrentUser(MyUser.class);
        if(!_email.equals(myUser.getAvatar())) {
            MyUser myUser1 = new MyUser();
            myUser1.setEmail(_email);
            myUser1.update(myUser.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        List<LoaclUser> loaclUserList = DataSupport.where("account=? and username=?", myUser.getUsername(), myUser.getUsername()).find(LoaclUser.class);
                        if (loaclUserList != null && loaclUserList.size() != 0) {
                            email.setText(_email);
                            LoaclUser loaclUser = new LoaclUser();
                            loaclUser.setEmail(_email);
                            loaclUser.updateAll("account=? and username=?", myUser.getUsername(), myUser.getUsername());
                        }
                    } else {
                        if (e.getErrorCode() == 301) {
                            Toast.makeText(ComplieActivity.this, "邮箱格式不正确", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ComplieActivity.this, "抱歉，出现了人类无法理解的错误！", Toast.LENGTH_LONG).show();
                        }
                    }

                }

            });
        }

    }

    public void refreshNickname(final String _nickname){
        final MyUser myUser= BmobUser.getCurrentUser(MyUser.class);
        if(!_nickname.equals(myUser.getNickname())) {
            nickname.setText(_nickname);
            MyUser myUser1 = new MyUser();
            myUser1.setNickname(_nickname);
            myUser1.update(myUser.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        List<LoaclUser> loaclUserList = DataSupport.where("account=? and username=?", myUser.getUsername(), myUser.getUsername()).find(LoaclUser.class);
                        if (loaclUserList != null && loaclUserList.size() != 0) {
                            LoaclUser loaclUser = new LoaclUser();
                            loaclUser.setNickname(_nickname);
                            loaclUser.updateAll("account=? and username=?", myUser.getUsername(), myUser.getUsername());
                        }
                    } else {
                    }
                }

            });
        }


    }

    public void refreshSign(final String _sign){
        final MyUser myUser= BmobUser.getCurrentUser(MyUser.class);
        if(!_sign.equals(myUser.getSignturn())) {
            signture.setText(_sign);
            MyUser myUser1 = new MyUser();
            myUser1.setSignturn(_sign);
            myUser1.update(myUser.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        List<LoaclUser> loaclUserList = DataSupport.where("account=? and username=?", myUser.getUsername(), myUser.getUsername()).find(LoaclUser.class);
                        if (loaclUserList != null && loaclUserList.size() != 0) {
                            LoaclUser loaclUser = new LoaclUser();
                            loaclUser.setSignturn(_sign);
                            loaclUser.updateAll("account=? and username=?", myUser.getUsername(), myUser.getUsername());
                        }
                    } else {

                    }

                }

            });
        }

    }

    public void refreshSex(final String sex){
        final MyUser myUser= BmobUser.getCurrentUser(MyUser.class);
        if(!sex.equals(myUser.getSex())) {
            MyUser myUser1 = new MyUser();
            myUser1.setSex(sex);
            myUser1.update(myUser.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        List<LoaclUser> loaclUserList = DataSupport.where("account=? and username=?", myUser.getUsername(), myUser.getUsername()).find(LoaclUser.class);
                        if (loaclUserList != null && loaclUserList.size() != 0) {
                            LoaclUser loaclUser = new LoaclUser();
                            loaclUser.setSex(sex);
                            loaclUser.updateAll("account=? and username=?", myUser.getUsername(), myUser.getUsername());
                        }
                    } else {

                    }

                }

            });
        }

    }
}
