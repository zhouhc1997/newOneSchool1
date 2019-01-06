package com.qinhu.oneschool.Adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.qinhu.oneschool.DB.Friend;
import com.qinhu.oneschool.DB.FriendManager;
import com.qinhu.oneschool.DB.MyUser;
import com.qinhu.oneschool.Litepal.LoaclUser;
import com.qinhu.oneschool.MyApplication;
import com.qinhu.oneschool.MyClass.CircleImageView;
import com.qinhu.oneschool.R;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

public class FriendManagerAdapter extends BaseAdapter {



    final static class ViewHolder {

        /**
         * 昵称
         */
        TextView tvTitle;
        /**
         * 头像
         */
        CircleImageView mImageView;
        /**
         * userid
         */
        TextView tvagain;
        TextView tvrefuse;
        TextView tvdelete;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        final FriendManager mContent = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.friend_manager, parent, false);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.friendname);
            viewHolder.tvagain = (TextView) convertView.findViewById(R.id.tvagain);
            viewHolder.mImageView = (CircleImageView) convertView.findViewById(R.id.frienduri);
            viewHolder.tvrefuse = (TextView) convertView.findViewById(R.id.tvrefuse);
            viewHolder.tvdelete=(TextView)convertView.findViewById(R.id.tvdelete) ;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        BmobQuery<MyUser> query=new BmobQuery<>();
        query.addWhereEqualTo("username",mContent.getAccountSend());
        query.setLimit(1);
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if(e==null){

                    Glide.with(MyApplication.getContext()).load(list.get(0).getAvatar()).into(viewHolder.mImageView);
                    viewHolder.tvTitle.setText(list.get(0).getNickname());
                }
            }
        });

        if(!mContent.getType().equals("add")){
            viewHolder.tvagain.setVisibility(View.GONE);
            viewHolder.tvrefuse.setVisibility(View.GONE);
            if(mContent.getType().equals("refuse")){
                viewHolder.tvdelete.setVisibility(View.VISIBLE);
                viewHolder.tvdelete.setText("已拒绝");
            }else if(mContent.getType().equals("delete")){
                viewHolder.tvdelete.setVisibility(View.VISIBLE);
                viewHolder.tvdelete.setText("已删除");
            }else if(mContent.getType().equals("again")){
                viewHolder.tvdelete.setVisibility(View.VISIBLE);
                viewHolder.tvdelete.setText("已同意");
            }
        }else{
            if(mContent.getType().equals("add")){
                viewHolder.tvagain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final FriendManager friend = new FriendManager();
                        friend.setType("again");
                        friend.setGet(true);
                        friend.update(mContent.getObjectId(), new UpdateListener() {

                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    Friend friend1=new Friend();
                                    friend1.setUser(mContent.getAccountSend());
                                    friend1.setFriendUser(mContent.getGetAccountSend());
                                    friend1.save(new SaveListener<String>() {
                                        @Override
                                        public void done(String s, BmobException e) {
                                            if(e==null){
                                                Friend friend1=new Friend();
                                                friend1.setFriendUser(mContent.getAccountSend());
                                                friend1.setUser(mContent.getGetAccountSend());
                                                friend1.save(new SaveListener<String>() {
                                                    @Override
                                                    public void done(String s, BmobException e) {
                                                        if(e==null){
                                                            FriendManager friendManager=new FriendManager();
                                                            friendManager.setGet(true);
                                                            friendManager.setType("again");
                                                            friendManager.setAccountSend(mContent.getGetAccountSend());
                                                            friendManager.setGetAccountSend(mContent.getAccountSend());
                                                            friendManager.setIsAndroid(false);
                                                            friendManager.save(new SaveListener<String>() {
                                                                @Override
                                                                public void done(String s, BmobException e) {

                                                                }
                                                            });

                                                            Toast.makeText(MyApplication.getContext(), "已同意申请", Toast.LENGTH_SHORT).show();
                                                            viewHolder.tvdelete.setVisibility(View.VISIBLE);
                                                            viewHolder.tvdelete.setText("已同意");
                                                            viewHolder.tvagain.setVisibility(View.GONE);
                                                            viewHolder.tvrefuse.setVisibility(View.GONE);

                                                            BmobQuery<MyUser> query=new BmobQuery<>();
                                                            query.addWhereEqualTo("username",mContent.getAccountSend());
                                                            query.setLimit(1);
                                                            query.findObjects(new FindListener<MyUser>() {
                                                                @Override
                                                                public void done(List<MyUser> list, BmobException e) {
                                                                    if(e==null){
                                                                        //储存至本地数据库
                                                                        MyUser myUser=list.get(0);
                                                                        LoaclUser loaclUser =new LoaclUser();
                                                                        loaclUser.setAvatar(myUser.getAvatar());
                                                                        loaclUser.setNickname(myUser.getNickname());
                                                                        loaclUser.setBeizhu("");
                                                                        loaclUser.setSex(myUser.getSex());
                                                                        loaclUser.setSignturn(myUser.getSignturn());
                                                                        loaclUser.setBirthday(myUser.getBirthday());
                                                                        loaclUser.setUpdateTime(myUser.getUpdatedAt());
                                                                        loaclUser.setUsername(myUser.getUsername());
                                                                        loaclUser.setAccount(mContent.getGetAccountSend());
                                                                        loaclUser.save();

                                                                    }else{

                                                                    }
                                                                }
                                                            });
                                                            /*
                                                            //将用户移除黑名单，
                                                            RongIM.getInstance().removeFromBlacklist(mContent.getAccountSend(), new RongIMClient.OperationCallback() {
                                                                @Override
                                                                public void onSuccess() {

                                                                }
                                                                @Override
                                                                public void onError(RongIMClient.ErrorCode errorCode) {

                                                                }
                                                            });
                                                            */
                                                            // 构造 TextMessage 实例
                                                            TextMessage myTextMessage = TextMessage.obtain("我们已经是好友了，快来一起聊天吧");


                                                            Message myMessage = Message.obtain(mContent.getAccountSend(), Conversation.ConversationType.PRIVATE, myTextMessage);

                                                            RongIM.getInstance().sendMessage(myMessage, null, null, new IRongCallback.ISendMessageCallback() {
                                                                @Override
                                                                public void onAttached(Message message) {
                                                                    //消息本地数据库存储成功的回调
                                                                }

                                                                @Override
                                                                public void onSuccess(Message message) {
                                                                    //消息通过网络发送成功的回调
                                                                }

                                                                @Override
                                                                public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                                                                    //消息发送失败的回调
                                                                }
                                                            });
                                                        }else{

                                                        }
                                                    }
                                                });
                                            }else{

                                            }
                                        }
                                    });
                                }else{

                                }
                            }
                        });
                    }
                });
                viewHolder.tvrefuse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final FriendManager friend = new FriendManager();
                        friend.setType("refuse");
                        friend.setGet(true);

                        friend.update( mContent.getObjectId(),new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    FriendManager friendManager=new FriendManager();
                                    friendManager.setGet(true);
                                    friendManager.setType("refuse");
                                    friendManager.setAccountSend(mContent.getGetAccountSend());
                                    friendManager.setAccountSend(mContent.getAccountSend());
                                    friendManager.setIsAndroid(false);
                                    friendManager.save(new SaveListener<String>() {
                                        @Override
                                        public void done(String s, BmobException e) {

                                        }
                                    });
                                    viewHolder.tvdelete.setVisibility(View.VISIBLE);
                                    viewHolder.tvdelete.setText("已拒绝");
                                    viewHolder.tvagain.setVisibility(View.GONE);
                                    viewHolder.tvrefuse.setVisibility(View.GONE);
                                }else{

                                }
                            }
                        });
                    }
                });
            }
        }
        return convertView;
    }
    private Context context;

    private List<FriendManager> list;

    public FriendManagerAdapter(Context context, List<FriendManager> list) {
        this.context = context;
        this.list = list;
    }
    @Override
    public int getCount() {
        if (list != null) return list.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (list == null)
            return null;

        if (position >= list.size())
            return null;

        return list.get(position);
    }
    public long getItemId(int position) {
        return position;
    }

}
