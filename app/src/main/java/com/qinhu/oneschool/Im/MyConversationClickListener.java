package com.qinhu.oneschool.Im;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.qinhu.oneschool.DB.MyUser;
import com.qinhu.oneschool.Litepal.LoaclUser;
import com.qinhu.oneschool.Public.UserActivity;

import org.litepal.crud.DataSupport;

import java.util.List;

import cn.bmob.v3.BmobUser;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.LocationMessage;
import io.rong.message.RichContentMessage;

public class MyConversationClickListener implements RongIM.ConversationClickListener {

    @Override
    public boolean onMessageLongClick(Context context, View view, Message message) {
        return false;
    }

    @Override
    public boolean onMessageClick(Context context, View view, Message message) {
        //点击消息处理事件，示例代码展示了如何获得消息内容
        if (message.getContent() instanceof LocationMessage) {
            Intent intent = new Intent(context,ConversationActivity.class);
            intent.putExtra("location", message.getContent());
            context.startActivity(intent);

        }else  if(message.getContent() instanceof RichContentMessage){
            RichContentMessage mRichContentMessage = (RichContentMessage) message.getContent();
            Log.d("Begavior",  "extra:"+mRichContentMessage.getExtra());

        }

        Log.d("Begavior", message.getObjectName() + ":" + message.getMessageId());

        return false;

    }

    @Override
    public boolean onMessageLinkClick(Context context, String s, Message message) {
        return false;
    }

    @Override
    public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo, String s) {
        MyUser myUser= BmobUser.getCurrentUser(MyUser.class);

        List<LoaclUser> list= DataSupport.where("account=?",myUser.getUsername()).find(LoaclUser.class);

        for(LoaclUser loaclUser :list) {

            if(loaclUser.getUsername().equals(userInfo.getUserId())) {
                Intent in = new Intent(context, UserActivity.class);
                in.putExtra("account", userInfo.getUserId());
                in.putExtra("type", "friend");
                context.startActivity(in);
                break;
            }
        }
        return false;


    }

    @Override
    public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo, String s) {
        return false;
    }
}