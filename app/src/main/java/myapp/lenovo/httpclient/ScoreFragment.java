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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScoreFragment extends Fragment {
    private CourseFragment.PassValue passValue;

    private List<String> groupName;
    //private Map<String,List<String[]>> childName;
    private Map<String,List<Score>> childName;
    private String currentSemester;
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
        currentSemester="";

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

    private void analyzeScore(String allScoreResult){
        org.jsoup.nodes.Document doc=Jsoup.parse(allScoreResult);
        String viewState=doc.select("input[name=__VIEWSTATE]").val();
        analyzeDailyFinalScore(viewState);
        /*Elements td=doc.select("td");
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
        groupName.add(list.get(0)[0]+list.get(0)[1]);*/
    }

    private void analyzeDailyFinalScore(String viewState) {
        String decodedMiddleString;
        String decodedFinalString=null;
        try {
            decodedMiddleString = new String(MyBase64.decode(viewState));
            decodedFinalString=new String(MyBase64.decode(decodedMiddleString),"utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Log.d("decodedFinalString0",decodedFinalString);
        decodedFinalString=decodedFinalString.substring(decodedFinalString.indexOf("<?xml")
                ,decodedFinalString.indexOf("ram>"))+"ram>";
        decodedFinalString=decodedFinalString.replace
                (decodedFinalString.substring(decodedFinalString.indexOf("<xs:schema")
                        ,decodedFinalString.indexOf("<diffgr"))," ");
        decodedFinalString=decodedFinalString.replace("utf-16","utf-8");
        //Log.d("decodedFinalString1",decodedFinalString);
        InputStream is=new ByteArrayInputStream(decodedFinalString.getBytes());
        DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        org.w3c.dom.Document doc=null;
        try {
            db=dbf.newDocumentBuilder();
            doc=db.parse(is);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        NodeList tables=doc.getElementsByTagName("Table");
        String[] nodeNames={"KCMC","KCXZ","PSCJ","QMCJ","CJ"};
        List<Score> scores=new ArrayList<>();
        for(int i=0;i<tables.getLength();i++){
            Score score=new Score();
            NodeList nodes=tables.item(i).getChildNodes();
            String year=null,semester=null;
            for(int j=0;j<nodes.getLength();j++){
                Node node=nodes.item(j);
                String nodeName=node.getNodeName();
                for(int k=0;k<5;k++){
                    if(nodeName.equals(nodeNames[k])){
                        addScore(score,k,node.getTextContent());
                    }
                }
                if(nodeName.equals("XN")){
                    year=node.getTextContent();
                }
                else if(nodeName.equals("XQ")){
                    semester=node.getTextContent();
                }
            }
            if(!currentSemester.equals(year+semester)){
                if(scores.size()>0){
                    childName.put(currentSemester,scores);
                }
                currentSemester=year+semester;
                groupName.add(currentSemester);
                scores=new ArrayList<>();
                scores.add(score);
            }
            else{
                scores.add(score);
            }
        }
        childName.put(currentSemester,scores);
//        for(int i=0;i<groupName.size();i++){
//            Log.d("groupName",i+"---"+groupName.get(i));
//        }
//        List<Score> list0=childName.get("2015-20161");
//        for(int i=0;i<list0.size();i++){
//            Log.d("list0",i+"---"+list0.get(i));
//        }
//        List<Score> list1=childName.get("2015-20162");
//        for(int i=0;i<list1.size();i++){
//            Log.d("list1",i+"---"+list1.get(i));
//        }
//        List<Score> list2=childName.get("2016-20171");
//        for(int i=0;i<list2.size();i++){
//            Log.d("list2",i+"---"+list2.get(i));
//        }
//        List<Score> list3=childName.get("2016-20172");
//        for(int i=0;i<list3.size();i++){
//            Log.d("list3",i+"---"+list3.get(i));
//        }
        Message msg=handler.obtainMessage(1,"");
        msg.arg1=12;
        handler.sendMessage(msg);
    }

    private void addScore(Score score,int index,String content){
        switch (index){
            case 0:score.setCourseName(content);break;
            case 1:score.setCourseType(content);break;
            case 2:score.setDailyScore(content);break;
            case 3:score.setFinalScore(content);break;
            case 4:score.setScore(content);break;
            default:break;
        }
    }

    @Override
    public void onAttach(Context context) {
        if(getActivity()instanceof CourseFragment.PassValue){
            passValue=(CourseFragment.PassValue) getActivity();
        }
        super.onAttach(context);
    }
}
