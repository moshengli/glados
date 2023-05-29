package com.mosheng.glados.demos;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

@EnableScheduling
@Component
public class Core {
    @Autowired
    public JavaMailSender mailSender;
    public static String sendPostWithJson(String url, String jsonString) throws Exception {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonString);
        Request request = new Request.Builder()
                .addHeader("cookie","xxx")//按f12抓一下点击签到的包就行
                .addHeader("user-agent","xxx")//尽量加上这个标头，避免被识别爬虫，但是glados也不检查哈哈
                .addHeader("authorization","xxx")//尽量加上这个标头，避免被识别爬虫，但是glados也不检查哈哈
                .addHeader("Connection","close")//这个得加上，不加的话okhttp3这个线程不会停止
                .post(body)
                .url(url)
                .build();
        Call call = client.newCall(request);
        //返回请求结果
        Response response = call.execute();
        String string=response.body().string();
        return string;
    }
    public void sendEmail(String msg) throws Exception{
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        message.setSubject("GLaDOS");
        helper.setFrom("xxxxx@qq.com");
        helper.setTo("xxxxxx@qq.com");
        helper.setText(msg,true);
        mailSender.send(message);
    }
    @Scheduled(cron = "0/5 * * * * ?")//每五秒执行一次，便于测试，大家可以改成你想要的时间
    public void main() throws Exception{
        String url = "https://glados.rocks/api/user/checkin";

        String jsonString = "{\"token\":\"glados.network\"}";

        String message="";

        int start=0;

        int end=0;
        message=sendPostWithJson(url, jsonString);

        start=message.indexOf("message")+10;
        end=message.indexOf("list")-3;

        message=message.substring(start,end);
        message="<h1>"+message+"<h1>";
        sendEmail(message);
    }
}
