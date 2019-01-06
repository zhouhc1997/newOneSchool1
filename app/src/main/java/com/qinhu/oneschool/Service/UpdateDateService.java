package com.qinhu.oneschool.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;


import com.qinhu.oneschool.DB.Friend;
import com.qinhu.oneschool.DB.MyUser;
import com.qinhu.oneschool.Litepal.LoaclUser;

import org.litepal.crud.DataSupport;

import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class UpdateDateService extends Service {


    private UpdateTime updateTime=new UpdateTime();

    public UpdateDateService() {
    }


    public class UpdateTime extends Binder {
        public void Update(){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final MyUser user= BmobUser.getCurrentUser(MyUser.class);
                    //刷新好友
                    BmobQuery<Friend> query=new BmobQuery<>();
                    query.addWhereEqualTo("user",user.getUsername());
                    query.setLimit(500);
                    query.order("-friendUser").findObjects(new FindListener<Friend>() {
                        @Override
                        public void done(final List<Friend> friendList, BmobException e) {
                            if(e==null){
                                final List<LoaclUser> loaclUserList = DataSupport.where("account=?",user.getUsername()).find(LoaclUser.class);
                                String [] objectIdList=new String[friendList.size()+1];
                                for(int i=0;i<friendList.size();i++){
                                    objectIdList[i]=friendList.get(i).getFriendUser();
                                }
                                objectIdList[friendList.size()]=user.getUsername();//将用户信息缓存至本地

                                BmobQuery<MyUser>  query=new BmobQuery<MyUser>();
                                query.addWhereContainedIn("username", Arrays.asList(objectIdList));
                                query.setLimit(500);
                                query.order("-username").findObjects(new FindListener<MyUser>() {
                                    @Override
                                    public void done(List<MyUser> list, BmobException e) {
                                        if(e==null){
                                            boolean isSava;//是否已经写入内存
                                            boolean []isdelete=new boolean[loaclUserList.size()];
                                            for(int i=0;i<isdelete.length;i++){//初始化删除数组。删除云端不存在但是本地存在的好友
                                                isdelete[i]=true;
                                            }
                                            for(int i=0;i<list.size();i++){
                                                MyUser myUser=list.get(i);
                                                isSava=false;//标记为未写入
                                                for(LoaclUser loaclUser : loaclUserList){
                                                    if(loaclUser.getUsername().equals(myUser.getUsername())){
                                                        isSava=true;//已经写入内存，只需要修改
                                                        isdelete[i]=false;
                                                        if(!loaclUser.getUpdateTime().equals(myUser.getUpdatedAt())){//最后更新时间不相等
                                                            loaclUser.setAvatar(myUser.getAvatar());
                                                            loaclUser.setUpdateTime(myUser.getUpdatedAt());
                                                            loaclUser.setBirthday(myUser.getBirthday());
                                                            loaclUser.setSignturn(myUser.getSignturn());
                                                            loaclUser.setSex(myUser.getSex());
                                                            loaclUser.setEmail(myUser.getEmail());
                                                            loaclUser.setNickname(myUser.getNickname());
                                                            if(i<friendList.size()) {//防止出现list越界
                                                                for(Friend friend:friendList){
                                                                    if(friend.getFriendUser().equals(myUser.getUsername())) {
                                                                        loaclUser.setBeizhu(friend.getNickName());//获取备注
                                                                        break;
                                                                    }

                                                                }
                                                            }
                                                            loaclUser.updateAll("username=? and account=?", loaclUser.getUsername(),user.getUsername());
                                                        }
                                                        break;
                                                    }
                                                }
                                                if(!isSava){
                                                    LoaclUser loaclUser =new LoaclUser();
                                                    loaclUser.setUsername(myUser.getUsername());
                                                    loaclUser.setAccount(user.getUsername());
                                                    loaclUser.setAvatar(myUser.getAvatar());
                                                    loaclUser.setUpdateTime(myUser.getUpdatedAt());
                                                    loaclUser.setBirthday(myUser.getBirthday());
                                                    loaclUser.setSignturn(myUser.getSignturn());
                                                    loaclUser.setSex(myUser.getSex());
                                                    loaclUser.setEmail(myUser.getEmail());
                                                    loaclUser.setNickname(myUser.getNickname());
                                                    if(i<friendList.size()) {//防止出现list越界
                                                        for(Friend friend:friendList){

                                                            if(friend.getFriendUser().equals(myUser.getUsername())) {

                                                                loaclUser.setBeizhu(friend.getNickName());//获取备注

                                                                break;
                                                            }

                                                        }
                                                    }
                                                    loaclUser.save();
                                                }
                                            }
                                            for(int i = 0; i< loaclUserList.size(); i++){
                                                if(isdelete[i]){//删除该好友信息;
                                                    LoaclUser loaclUser = loaclUserList.get(i);
                                                    loaclUser.delete();//删除
                                                }
                                            }
                                            stopSelf();//线程停止
                                        }else{
                                            stopSelf();//线程停止

                                        }
                                    }
                                });
                            }else{
                                stopSelf();//线程停止

                            }
                        }
                    });
                }
            }).start();


        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return updateTime;
    }
}
