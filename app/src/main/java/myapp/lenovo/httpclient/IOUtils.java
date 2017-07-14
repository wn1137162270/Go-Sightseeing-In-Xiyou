package myapp.lenovo.httpclient;

import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by Lenovo on 2017/1/25.
 */

public class IOUtils {
    //获取--VIEWSTATE的值，发送get请求
    public static void getViewState(final String url,final String referer,final String cookie) {
        final String[] viewState = new String[1];
        new Thread(){
            @Override
            public void run() {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet viewStateGet = new HttpGet(url);
                viewStateGet.addHeader("Cookie", cookie);
                viewStateGet.addHeader("Referer", referer);
                try {
                    HttpResponse httpResponse = httpClient.execute(viewStateGet);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    String viewStateResult = EntityUtils.toString(httpEntity);
                    viewState[0] = Jsoup.parse(viewStateResult).select("input[name=__VIEWSTATE]").val();
                    Message msg = ScoreFragment.handler.obtainMessage(1, "");
                    msg.arg1 = 10;
                    msg.obj= viewState[0];
                    ScoreFragment.handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                super.run();
            }
        }.start();
    }

    //发送请求的URL是从登录成功后返回的main页面解析出来的
    public static String analyzeURL(String loginResult,String searchItem){
        String linkURL="";
        Document doc=Jsoup.parse(loginResult);
        Elements links=doc.select("a[href]");

        for(Element link:links){
            if(link.text().equals(searchItem))
                linkURL=link.attr("href");
        }
        return linkURL;
    }
}
