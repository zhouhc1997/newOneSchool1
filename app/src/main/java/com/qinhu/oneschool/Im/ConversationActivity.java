package com.qinhu.oneschool.Im;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qinhu.oneschool.MyClass.AndroidBug5497Workaround;
import com.qinhu.oneschool.R;
import com.qinhu.oneschool.Public.UserActivity;
import com.qinhu.oneschool.Utils.StatusBarUtil;


public class ConversationActivity extends FragmentActivity {
    private TextView name;
    private ImageView imageView;
    private ImageView information;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        AndroidBug5497Workaround.assistActivity(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            StatusBarUtil.setStatusBarColor(ConversationActivity.this,R.color.noColor); }

        String sName = getIntent().getData().getQueryParameter("title");//获取昵称
        name=(TextView)findViewById(R.id.name);
        imageView=(ImageView)findViewById(R.id.id_conversation_backbtn);
        information=(ImageView)findViewById(R.id.id_conversation_information);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /*
        Uri uri=getIntent().getData();
        Toast.makeText(getApplicationContext(),getIntent().getData().getQueryParameter("targetId"),Toast.LENGTH_SHORT).show();
        */
        information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ConversationActivity.this, UserActivity.class);
                intent.putExtra("account", getIntent().getData().getQueryParameter("targetId"));
                intent.putExtra("type","friend");
                startActivity(intent);
            }
        });
        name.setText(sName);

        /*
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        String sName = getIntent().getData().getQueryParameter("title");//获取昵称
        name=(TextView)findViewById(R.id.name);
        imageView=(ImageView)findViewById(R.id.back);
        //imageView.setImageResource(R.drawable.back);
        name.setText(sName);
        */
    }
}
