package myapp.lenovo.httpclient;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment {
    private CourseFragment.PassValue passValue;

    private ImageView sculpture;
    private TextView name;
    private TextView account;
    private TextView major;
    private TextView className;
    private Handler handler;

    private String sex;
    private String nameStr;
    private String majorStr;
    private String classStr;

    private String contentResult;
    private List<String> newsURL;
    private String newsContent;
    private int count;

    private MyNewsDialog myNewsDialog;

    private static final String HOST_URL="http://222.24.62.120/";
    private static final String MAIN_URL="http://222.24.62.120/xs_main.aspx?xh=";
    private static final String CONTENT_URL="content.aspx";
    private static final String ABOUT_URL="https://www.xiyoumobile.com";
    private static final String logoutViewState="dDwxMjg4MjkxNjE4Ozs+mC/tArt0u1jmP1rzm5PGO18pVC4=";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_me, container, false);
        sculpture= (ImageView) view.findViewById(R.id.sculpture_iv);
        name= (TextView) view.findViewById(R.id.name_tv);
        account= (TextView) view.findViewById(R.id.account_tv);
        major= (TextView) view.findViewById(R.id.major_tv);
        className= (TextView) view.findViewById(R.id.class_tv);
        LinearLayout news = (LinearLayout) view.findViewById(R.id.news_layout);
        LinearLayout indirection = (LinearLayout) view.findViewById(R.id.indirection_layout);
        LinearLayout question = (LinearLayout) view.findViewById(R.id.question_layout);
        LinearLayout about = (LinearLayout) view.findViewById(R.id.about_layout);
        LinearLayout logout = (LinearLayout) view.findViewById(R.id.logout_layout);

        MyOnClickListener myOnClickListener=new MyOnClickListener();
        news.setOnClickListener(myOnClickListener);
        indirection.setOnClickListener(myOnClickListener);
        question.setOnClickListener(myOnClickListener);
        about.setOnClickListener(myOnClickListener);
        logout.setOnClickListener(myOnClickListener);

        Log.d("me","me");
        newsURL=new ArrayList<>();
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.arg1==15){
                    if(sex.equals("女"))
                        sculpture.setImageResource(R.drawable.woman);
                    else if(sex.equals("男"))
                        sculpture.setImageResource(R.drawable.man);
                    name.setText(nameStr);
                    account.setText(MainActivity.accountStr);
                    major.setText(majorStr);
                    className.setText(classStr);
                    passValue.passData(1);
                }
                else if(msg.arg1==16){
                    Intent intent=new Intent(getActivity(),LoginActivity.class);
                    intent.putExtra("isLogout",true);
                    startActivity(intent);
                    getActivity().finish();
                }
                else if(msg.arg1==17){
                    Toast.makeText(getContext(),"退出失败",Toast.LENGTH_SHORT).show();
                }
                else if(msg.arg1==18){
                    count=0;
                    MyNewsThread myNewsThread=new MyNewsThread(count);
                    myNewsThread.start();
                }
                else if(msg.arg1==19){
                    if(count==0) {
                        showNewsDialog();
                    }
                    else{
                        TextView contentView=myNewsDialog.getContent();
                        contentView.setText(newsContent);
                    }
               }
                else if(msg.arg1==22){
                    Toast.makeText(getActivity(),"请求失败", Toast.LENGTH_SHORT).show();
                }
                else if(msg.arg1==23){
                    if (MainActivity.myProgressBarDialog!=null
                            &&MainActivity.myProgressBarDialog.isShowing()){
                        MainActivity.myProgressBarDialog.dismiss();
                        Toast.makeText(getActivity(),"请求失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        String linkURL=IOUtils.analyzeURL(MainActivity.loginResult,"个人信息");
        MyInfoThread myInfoThread=new MyInfoThread(linkURL);
        myInfoThread.start();

        return view;
    }

    class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.news_layout:
                    if(newsURL.size()==0){
                        MyContentThread myContentThread=new MyContentThread();
                        myContentThread.start();
                    }
                    else{
                        showNewsDialog();
                    }
                    break;
                case R.id.indirection_layout:break;
                case R.id.question_layout:
                    Intent questionIntent=new Intent(Intent.ACTION_SENDTO);
                    questionIntent.setData(Uri.parse("mailto:1137162270@qq.com"));
                    questionIntent.putExtra(Intent.EXTRA_SUBJECT, "游西邮问题反馈");
                    startActivity(questionIntent);break;
                case R.id.about_layout:
                    Intent aboutIntent=new Intent(Intent.ACTION_VIEW,Uri.parse(ABOUT_URL));
                    aboutIntent.setClassName("com.android.browser","com.android.browser.BrowserActivity");
                    startActivity(aboutIntent);
                    break;
                case R.id.logout_layout:
                    MyLogoutThread myLogoutThread=new MyLogoutThread();
                    myLogoutThread.start();
                    break;
            }
        }
    }

    class MyContentThread extends Thread{

        @Override
        public void run() {
            HttpClient httpClient=new DefaultHttpClient();
            String url=HOST_URL+CONTENT_URL;
            HttpGet contentGet=new HttpGet(url);
            contentGet.setHeader("Cookie",MainActivity.cookie);
            contentGet.setHeader("Referer",MAIN_URL+MainActivity.accountStr);
            try {
                HttpResponse httpResponse=httpClient.execute(contentGet);
                if(httpResponse.getStatusLine().getStatusCode()==200)
                {
                    HttpEntity httpEntity=httpResponse.getEntity();
                    contentResult= EntityUtils.toString(httpEntity);
                    //Log.d("contentResult",contentResult);
                    analyzeContent();
                    Message msg=handler.obtainMessage(1,"");
                    msg.arg1=18;
                    handler.sendMessage(msg);
                }
            } catch (IOException e) {
                Message msg=handler.obtainMessage(1,"");
                msg.arg1=22;
                handler.sendMessage(msg);
                e.printStackTrace();
            }
            super.run();
        }
    }

    class MyNewsThread extends Thread{
        private int count;

        MyNewsThread(int count){
            this.count=count;
        }

        @Override
        public void run() {
            HttpClient httpClient=new DefaultHttpClient();
            String url=HOST_URL+newsURL.get(count);
            url=url.replace(" ","%20");
            HttpGet newsGet=new HttpGet(url);
            newsGet.setHeader("Cookie",MainActivity.cookie);
            try {
                HttpResponse httpResponse=httpClient.execute(newsGet);
                if(httpResponse.getStatusLine().getStatusCode()==200)
                {
                    HttpEntity httpEntity=httpResponse.getEntity();
                    String newsResult= EntityUtils.toString(httpEntity);
                    //Log.d("newsResult",newsResult);
                    newsContent=Jsoup.parse(newsResult).select("textarea").text();
                    newsContent=newsContent.substring(newsContent.indexOf('>')+1,
                            newsContent.lastIndexOf('<'));
                    Log.d("newsContent",newsContent);
                    Message msg=handler.obtainMessage(1,"");
                    msg.arg1=19;
                    handler.sendMessage(msg);
                }
            } catch (IOException e) {
                Message msg=handler.obtainMessage(1,"");
                msg.arg1=22;
                handler.sendMessage(msg);
                e.printStackTrace();
            }
            super.run();
        }
    }

    class MyInfoThread extends Thread{
        private String linkURL;

        MyInfoThread(String linkURL){
            this.linkURL=linkURL;
        }

        @Override
        public void run() {
            HttpClient httpClient=new DefaultHttpClient();
            String url=HOST_URL+linkURL;
            HttpGet infoGet=new HttpGet(url);
            infoGet.setHeader("Cookie",MainActivity.cookie);
            infoGet.setHeader("Referer",MAIN_URL+MainActivity.accountStr);
            try {
                HttpResponse httpResponse=httpClient.execute(infoGet);
                if(httpResponse.getStatusLine().getStatusCode()==200)
                {
                    HttpEntity httpEntity=httpResponse.getEntity();
                    String infoResult= EntityUtils.toString(httpEntity);
                    //Log.d("infoResult",infoResult);
                    Elements td= Jsoup.parse(infoResult).select("td");
                    //for(int i=0;i<td.size();i++){
                    //    Log.d(i+"",td.get(i).text());
                    //}
                    sex=td.get(20).text();
                    nameStr=td.get(8).text();
                    majorStr=td.get(78).text();
                    classStr=td.get(90).text();
                    Message msg=handler.obtainMessage(1,"");
                    msg.arg1=15;
                    handler.sendMessage(msg);
                }
            } catch (IOException e) {
                Message msg=handler.obtainMessage(1,"");
                msg.arg1=22;
                handler.sendMessage(msg);
                e.printStackTrace();
            }
            super.run();
        }
    }

    class MyLogoutThread extends Thread{

        @Override
        public void run() {
            HttpClient httpClient=new DefaultHttpClient();
            HttpPost logoutPost=new HttpPost(MAIN_URL+MainActivity.accountStr);
            logoutPost.setHeader("Cookie", MainActivity.cookie);
            logoutPost.setHeader("Referer",MAIN_URL+MainActivity.accountStr);
            ArrayList<NameValuePair> pairs=new ArrayList<>();
            pairs.add(new BasicNameValuePair("__EVENTARGUMENT",""));
            pairs.add(new BasicNameValuePair("__EVENTTARGET","likTc"));
            pairs.add(new BasicNameValuePair("__VIEWSTATE", logoutViewState));
            try {
                HttpEntity requestEntity=new UrlEncodedFormEntity(pairs, HTTP.UTF_8);
                logoutPost.setEntity(requestEntity);
                HttpResponse httpResponse=httpClient.execute(logoutPost);
                if(httpResponse.getStatusLine().getStatusCode()==200)
                {
                    HttpEntity httpEntity=httpResponse.getEntity();
                    String logoutResult= EntityUtils.toString(httpEntity);
                    //Log.d("logoutResult",logoutResult);
                    Document doc=Jsoup.parse(logoutResult);
                    Elements title=doc.select("title");
                    Message msg=handler.obtainMessage(1,"");
                    if(title.get(0).text().contains("请登录"))
                        msg.arg1=16;
                    else
                        msg.arg1=17;
                    handler.sendMessage(msg);
                }
            } catch (IOException e) {
                Message msg=handler.obtainMessage(1,"");
                msg.arg1=22;
                handler.sendMessage(msg);
                e.printStackTrace();
            }
            super.run();
        }
    }

    public void showNewsDialog(){
        myNewsDialog = new MyNewsDialog(getContext(), newsContent);
        myNewsDialog.setOnCloseClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myNewsDialog.dismiss();
            }
        });
        myNewsDialog.setOnNextClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count==newsURL.size()-1)
                    Toast.makeText(getContext(),"已是最后一个",Toast.LENGTH_SHORT).show();
                else{
                    count++;
                    MyNewsThread myNewsThread=new MyNewsThread(count);
                    myNewsThread.start();
                }
            }
        });
        myNewsDialog.show();
    }

    public void analyzeContent(){
        Document doc=Jsoup.parse(contentResult);
        Elements td= doc.select("td");
        //for(int i=0;i<td.size();i++){
        //    Log.d(i+"",td.get(i).text());
        //}
        Elements a=doc.select("a[onclick]");
        for(int i=0;i<a.size();i++){
            String onclick=a.get(i).attr("onclick");
            String[] s=onclick.split("'");
            newsURL.add(s[1].substring(0,s[1].length()-1));
        }
        //newsURL=s[1].substring(0,s[1].length()-1);
    }

    @Override
    public void onAttach(Context context) {
        if(getActivity()instanceof CourseFragment.PassValue){
            passValue=(CourseFragment.PassValue) getActivity();
        }
        super.onAttach(context);
    }

}

