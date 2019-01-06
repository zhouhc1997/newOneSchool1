package com.qinhu.oneschool.MyClass;


import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class JiaowuLogin {

    public static final String WustIpString(){
        return "http://jwxt.wust.edu.cn/whkjdx/";
    }

    public static String getString(String httpUrl, String lastJSESSIONID) {
        String msg = ""; //服务器返回结果
        try {
            //创建URL对象
            URL url = new URL(httpUrl);
            //创建HttpURLConnection对象
            HttpURLConnection httpURLConnection = (HttpURLConnection)
                    url.openConnection();
            //设置连接相关属性
            httpURLConnection.setConnectTimeout(5000); //设置连接超时为5秒
            httpURLConnection.setRequestMethod("GET"); //设置请求方式(默认为get)
            httpURLConnection.setRequestProperty("Cookie", lastJSESSIONID);
            //建立到连接(可省略)
            httpURLConnection.connect();
            //获得服务器反馈状态信息
            //200：表示成功完成(HTTP_OK)， 404：请求资源不存在(HTTP_NOT_FOUND)
            int code = httpURLConnection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                //接收服务器返回的信息（输入流）
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(httpURLConnection.getInputStream()));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    msg+=line+"\n";
                }
//关闭缓冲区和连接
                bufferedReader.close();
                httpURLConnection.disconnect();
            }
            return msg;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }


    public static String login(String httpUrl, String username, String password, String vcode, String lastJSESSIONID) {
        if(lastJSESSIONID == null){
            return "0";
        }

        String msg = "0"; //服务器返回结果
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection)
                    url.openConnection();
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Cookie", lastJSESSIONID);
            httpURLConnection.connect();
            //post请求传递参数
            String data = "PASSWORD=" + password + "&USERNAME=" + username +
                    "&useDogCode=&x=55&y=17&RANDOMCODE="+ vcode.toLowerCase() ; //参数之间用&连接
            //向服务器发送数据(输出流)
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8"));
            writer.write(data);
            writer.close();
            int code = httpURLConnection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {

                String str = "";
                //接收服务器返回的信息（输入流）
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(httpURLConnection.getInputStream()));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    str+=line+"\n";
                }
                    //关闭缓冲区和连接
                bufferedReader.close();
                httpURLConnection.disconnect();
                org.jsoup.nodes.Document doc = Jsoup.parse(str);
                if (doc.title().equals("武汉科技大学数字化校园平台-强智科技大学")){
                    msg = doc.getElementById("errorinfo").text();
                }else {
                    try {
                        HttpURLConnection httpURLConnection1 = (HttpURLConnection)
                                new URL(WustIpString() + "Logon.do?method=logonBySSO").openConnection();
                        httpURLConnection1.setConnectTimeout(5000);
                        httpURLConnection1.setRequestMethod("GET");
                        httpURLConnection1.setRequestProperty("Cookie", lastJSESSIONID);
                        httpURLConnection1.connect();
                        if (httpURLConnection1.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            msg = "1";
                            httpURLConnection.disconnect();
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }


    public static String crawler(String httpUrl, String d,String lastJSESSIONID) {

        if(lastJSESSIONID == null){
            return "1";
        }

        String msg = ""; //服务器返回结果
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection)
                    url.openConnection();
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Cookie", lastJSESSIONID);
            httpURLConnection.connect();
            //post请求传递参数
            String data = d; //参数之间用&连接
            //向服务器发送数据(输出流)
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8"));
            writer.write(data);
            writer.close();
            int code = httpURLConnection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
//接收服务器返回的信息（输入流）
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(httpURLConnection.getInputStream()));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    msg+=line+"\n";
                }
//关闭缓冲区和连接
                bufferedReader.close();
                httpURLConnection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;

    }
}
