package myapp.lenovo.httpclient;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScoreFragment extends Fragment {
    private CourseFragment.PassValue passValue;

    private List<String> groupName;
    private Map<String,List<String[]>> childName;
    private ExpandableListView elv;

    private String linkURL;
    public static Handler handler;

    private static final String HOST_URL="http://222.24.62.120/";
    private static final String MAIN_URL="http://222.24.62.120/xs_main.aspx?xh=";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_score, container, false);
        elv= (ExpandableListView) view.findViewById(R.id.score_elv);

        groupName=new ArrayList<>();
        childName=new HashMap<>();

        Log.d("score","score");
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.arg1==10){
                    String viewState=msg.obj.toString();
                    MyScoreThread myScoreThread=new MyScoreThread(linkURL,viewState);
                    myScoreThread.start();
                }
                else if(msg.arg1==12){
                    MyBaseExpandableListAdapter ela=new MyBaseExpandableListAdapter(groupName
                            ,childName,getContext());
                    elv.setAdapter(ela);
                    ela.notifyDataSetChanged();
                    passValue.passData(1);
                }
                else if(msg.arg1==22){
                    Toast.makeText(getActivity(),"请求失败", Toast.LENGTH_SHORT).show();
                    if (MainActivity.myProgressBarDialog!=null
                            &&MainActivity.myProgressBarDialog.isShowing()){
                        MainActivity.myProgressBarDialog.dismiss();
                        Toast.makeText(getActivity(),"请求失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        linkURL=IOUtils.analyzeURL(MainActivity.loginResult,"成绩查询");
        IOUtils.getViewState(HOST_URL+linkURL,MAIN_URL+MainActivity.accountStr, MainActivity.cookie);

        return view;
    }

    class MyScoreThread extends Thread{
        private String linkURL;
        private String viewState;

        MyScoreThread(String linkURL,String viewState){
            this.linkURL=linkURL;
            this.viewState=viewState;
        }

        @Override
        public void run() {
            HttpClient httpClient=new DefaultHttpClient();
            String url=HOST_URL+linkURL;
            ArrayList<NameValuePair> pairs=new ArrayList<>();
            pairs.add(new BasicNameValuePair("__EVENTARGUMENT",""));
            pairs.add(new BasicNameValuePair("__EVENTTARGET",""));
            pairs.add(new BasicNameValuePair("__VIEWSTATE",viewState));
            pairs.add(new BasicNameValuePair("btn_zcj","%C0%FA%C4%EA%B3%C9%BC%A8"));
            pairs.add(new BasicNameValuePair("ddlXN",""));
            pairs.add(new BasicNameValuePair("ddlXQ",""));
            pairs.add(new BasicNameValuePair("ddl_kcxz",""));
            pairs.add(new BasicNameValuePair("hidLanguage",""));
            try {
                HttpEntity requestEntity=new UrlEncodedFormEntity(pairs);
                HttpPost scorePost=new HttpPost(url);
                scorePost.setEntity(requestEntity);
                scorePost.setHeader("Cookie",MainActivity.cookie);
                scorePost.setHeader("Referer",MAIN_URL+MainActivity.accountStr);
                HttpResponse httpResponse=httpClient.execute(scorePost);
                if(httpResponse.getStatusLine().getStatusCode()==200)
                {
                    HttpEntity httpEntity=httpResponse.getEntity();
                    String allScoreResult= EntityUtils.toString(httpEntity);
                    analyzeScore(allScoreResult);
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

    public void analyzeScore(String allScoreResult){
        String binaryString=MyBase64.decode(allScoreResult);
        Log.d("binaryString",binaryString);
        Elements td= Jsoup.parse(allScoreResult).select("td");
        ArrayList<String[]> scores=new ArrayList<>();
        for(int i=23;i<td.size();i=i+15){
            int j=i;
            if(td.get(j).text()==null||td.get(j).text().equals(""))
                break;
            String[] scoreInfo=new String[13];
            for(j=i;j<i+15;j++){
                if((j-23)%15!=13&&(j-23)%15!=14)
                    scoreInfo[(j-23)%15]=td.get(j).text();
            }
            scores.add(scoreInfo);
        }
        //for(int i=0;i<scores.size();i++){
        //    String[] scoreInfo=scores.get(i);
        //    for (String aScoreInfo : scoreInfo)
        //        Log.d(i + "", aScoreInfo);
        //}
        List<String[]> list=new ArrayList<>();
        for(int i=0;i<scores.size();i++){
            String[] scoreInfo=scores.get(i);
            if(i==0||scores.get(i)[1].equals(scores.get(i-1)[1]))
                list.add(scoreInfo);
            else {
                childName.put(list.get(0)[0]+list.get(0)[1],list);
                groupName.add(list.get(0)[0]+list.get(0)[1]);
                //list.clear();
                list=new ArrayList<>();
                list.add(scoreInfo);
            }
        }
        childName.put(list.get(0)[0]+list.get(0)[1],list);
        groupName.add(list.get(0)[0]+list.get(0)[1]);
        Message msg=handler.obtainMessage(1,"");
        msg.arg1=12;
        handler.sendMessage(msg);
    }

    @Override
    public void onAttach(Context context) {
        if(getActivity()instanceof CourseFragment.PassValue){
            passValue=(CourseFragment.PassValue) getActivity();
        }
        super.onAttach(context);
    }
}
