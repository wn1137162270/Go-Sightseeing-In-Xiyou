package myapp.lenovo.httpclient;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
public class CourseFragment extends Fragment {
    private PassValue passValue;

    private MyProgressBarDialog myProgressBarDialog;

    private CourseTableView courseTableView;
    private List<Course> list;

    private String linkURL;
    private String courseViewState;
    private Handler handler;

    private List<String> chooseList;
    private String chooseSelectedStr;
    private String currentSelectedStr;
    private boolean isFirst=true;

    private static final String HOST_URL="http://222.24.62.120/";
    private static final String MAIN_URL="http://222.24.62.120/xs_main.aspx?xh=";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_course, container, false);
        courseTableView= (CourseTableView) view.findViewById(R.id.course_table_view);
        list=new ArrayList<>();
        chooseList=new ArrayList<>();
        getActivity().invalidateOptionsMenu();

        Log.d("course","course");
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.arg1==13){
                    passValue.passData(1);
                    courseTableView.updateCourseData(list);
                    getActivity().invalidateOptionsMenu();
                }
                else if(msg.arg1==14) {
                    courseTableView.updateCourseData(list);
                    getActivity().invalidateOptionsMenu();
                    myProgressBarDialog.dismiss();
                }
                else if(msg.arg1==22){
                    if(isFirst){
                        if (MainActivity.myProgressBarDialog!=null
                                &&MainActivity.myProgressBarDialog.isShowing()){
                            MainActivity.myProgressBarDialog.dismiss();
                            Toast.makeText(getActivity(),"请求失败", Toast.LENGTH_SHORT).show();
                        }
                        isFirst=false;
                    }
                    else if (myProgressBarDialog!=null&&myProgressBarDialog.isShowing()){
                        myProgressBarDialog.dismiss();
                        Toast.makeText(getActivity(),"请求失败", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getActivity(),"请求失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        linkURL=IOUtils.analyzeURL(MainActivity.loginResult,"学生个人课表");
        MyFirstCourseThread myFirstCourseThread=new MyFirstCourseThread(linkURL);
        myFirstCourseThread.start();

        return view;
    }

    class MyFirstCourseThread extends Thread{
        private String linkURL;

        MyFirstCourseThread(String linkURL){
            this.linkURL=linkURL;
        }

        @Override
        public void run() {
            HttpClient httpClient=new DefaultHttpClient();
            String url=HOST_URL+linkURL;
            HttpGet courseFirstGet=new HttpGet(url);
            courseFirstGet.setHeader("Cookie",MainActivity.cookie);
            courseFirstGet.setHeader("Referer",MAIN_URL+MainActivity.accountStr);
            try {
                HttpResponse httpResponse=httpClient.execute(courseFirstGet);
                if(httpResponse.getStatusLine().getStatusCode()==200)
                {
                    HttpEntity httpEntity=httpResponse.getEntity();
                    String courseFirstResult=EntityUtils.toString(httpEntity);
                    //Log.d("courseFirstResult",courseFirstResult);
                    if(isFirst){
                        analyzeFirstCourse(courseFirstResult);
                    }
                    else{
                        analyzeCourse(courseFirstResult);
                        Message msg=handler.obtainMessage(1,"");
                        msg.arg1=14;
                        handler.sendMessage(msg);
                    }
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

    class MyCourseThread extends Thread{
        private String linkURL;
        private String year;
        private String semester;
        private String yos;

        MyCourseThread(String linkURL,String year,String semester,String yos){
            this.linkURL=linkURL;
            this.year=year;
            this.semester=semester;
            this.yos=yos;
        }

        @Override
        public void run() {
            HttpClient httpClient=new DefaultHttpClient();
            String url=HOST_URL+linkURL;
            ArrayList<NameValuePair> pairs=new ArrayList<>();
            pairs.add(new BasicNameValuePair("__EVENTARGUMENT",""));
            pairs.add(new BasicNameValuePair("__EVENTTARGET",yos));
            pairs.add(new BasicNameValuePair("__VIEWSTATE",courseViewState));
            pairs.add(new BasicNameValuePair("xnd",year));
            pairs.add(new BasicNameValuePair("xqd",semester));
            try {
                HttpEntity requestEntity=new UrlEncodedFormEntity(pairs);
                HttpPost coursePost=new HttpPost(url);
                coursePost.setEntity(requestEntity);
                coursePost.setHeader("Cookie",MainActivity.cookie);
                coursePost.setHeader("Referer",MAIN_URL+MainActivity.accountStr);
                HttpResponse httpResponse=httpClient.execute(coursePost);
                if(httpResponse.getStatusLine().getStatusCode()==200)
                {
                    HttpEntity httpEntity=httpResponse.getEntity();
                    String courseResult= EntityUtils.toString(httpEntity);
                    //Log.d("courseResult",courseResult);
                    analyzeCourse(courseResult);
                    Message msg=handler.obtainMessage(1,"");
                    msg.arg1=14;
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

    public void analyzeFirstCourse(String firstCourseResult){
        Document doc=Jsoup.parse(firstCourseResult);
        courseViewState =doc.select("input[name=__VIEWSTATE]").val();
        Elements optionSelected=doc.select("option[selected]");
        Elements alerts=doc.select("script[language]");
        if(alerts.get(0).data().contains("你还没有进行本学期的课堂教学质量评价")){
            Log.d("alerts","alerts");
            Intent intent=new Intent(getActivity(),LoginActivity.class);
            intent.putExtra("isLogout",false);
            startActivity(intent);
            getActivity().finish();
        }
        else {
            currentSelectedStr=optionSelected.get(0).text()+optionSelected.get(1).text();
            Elements options=doc.select("option");
            for(int i=options.size()-1;i>=0;i--){
                String year=options.get(i).text();
                if(year.contains("-")){
                    chooseList.add(year+"1");
                    chooseList.add(year+"2");
                }
            }
            analyzeCourse(firstCourseResult);
            Message msg=handler.obtainMessage(1,"");
            msg.arg1=13;
            handler.sendMessage(msg);
            isFirst=false;
        }
    }

    public void analyzeCourse(String courseResult){
        Document doc=Jsoup.parse(courseResult);
        Elements optionSelected=doc.select("option[selected]");
        chooseSelectedStr=optionSelected.get(0).text()+optionSelected.get(1).text();
        Elements td=doc.select("td");
        //for(int i=0;i<td.size();i++){
        //    Log.d("td:"+i,td.get(i).text());
        //}
        ArrayList<String[]> practiceList=new ArrayList<>();
        ArrayList<String[]> fieldTripList=new ArrayList<>();
        ArrayList<String[]> notArrangedList=new ArrayList<>();
        list.clear();
        for(int i=0;i<td.size();i++){
            String tdStr=td.get(i).text();
            if(tdStr.equals("第1节")||tdStr.equals("第3节")||tdStr.equals("第5节")||
                    tdStr.equals("第7节")||tdStr.equals("第9节")){
                int k=0;
                for(int j=i+1;j<=i+7;j++){
                    String ts=td.get(j).text();
                    if(ts.length()>1) {
                        int end = ts.indexOf(' ');
                        Course course = new Course();
                        course.setDay(k + 1);
                        course.setSection(Integer.parseInt(tdStr.substring(1, 2)));
                        course.setCourseName(ts.substring(0, end));
                        course.setCourseDetail(ts);
                        if (!td.get(j).attr("rowspan").equals("2"))
                            course.setCourseLong(Integer.parseInt(td.get(j).attr("rowspan")));
                        list.add(course);
                    }
                    k++;
                }
                i=i+7;
            }
            else if(tdStr.equals("实践课(或无上课时间)信息：")){
                int j=i+8;
                while(!td.get(j).text().equals("实习课信息：")){
                    String[] practices=new String[6];
                    int w=0,k;
                    for(k=j;k<=j+5;k++){
                        practices[w]=td.get(k).text();
                        w++;
                    }
                    practiceList.add(practices);
                    j=k;
                }
                i=j-1;
            }
            else if(tdStr.equals("实习课信息：")){
                int j=i+9;
                while(!td.get(j).text().equals("未安排上课时间的课程：")){
                    String[] fieldTrips=new String[7];
                    int w=0,k;
                    for(k=j;k<=j+6;k++){
                        fieldTrips[w]=td.get(k).text();
                        w++;
                    }
                    fieldTripList.add(fieldTrips);
                    j=k;
                }
                i=j-1;
            }
            else if(tdStr.equals("未安排上课时间的课程：")){
                int j=i+7;
                while(j<td.size()){
                    String[] notArrangeds=new String[5];
                    int w=0,k;
                    for(k=j;k<=j+4;k++){
                        notArrangeds[w]=td.get(k).text();
                        w++;
                    }
                    notArrangedList.add(notArrangeds);
                    j=k;
                }
                i=j-1;
            }
        }
        /*for(int i=0;i<courseList.size();i++){
            String[] courses=courseList.get(i);
            for(String acourse:courses){
                Log.d("acourse"+i,acourse);
                Log.d("length",acourse.length()+"");
            }
        }
        for(int i=0;i<practiceList.size();i++){
            String[] practices=practiceList.get(i);
            for(String apractice:practices)
                Log.d("apractice"+i,apractice);
        }
        for(int i=0;i<fieldTripList.size();i++){
            String[] fieldTrips=fieldTripList.get(i);
            for(String afieldTrip:fieldTrips)
                Log.d("afieldTrip"+i,afieldTrip);
        }
        for(int i=0;i<notArrangedList.size();i++){
            String[] notArrangeds=notArrangedList.get(i);
            for(String anotArranged:notArrangeds)
                Log.d("anotArranged"+i,anotArranged);
        }*/
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        int base=Menu.FIRST;
        for(int i=0;i<chooseList.size();i++) {
            String ci=chooseList.get(i);
            MenuItem mi=menu.add(base, base + i + 1, base + i + 1,
                    ci.substring(0, 9) + "第" + ci.substring(9) + "学期");
            mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            if(ci.equals(chooseSelectedStr))
                mi.setVisible(false);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()>Menu.FIRST){
            myProgressBarDialog =new MyProgressBarDialog(getContext(),"数据加载中…");
            myProgressBarDialog.show();
            myProgressBarDialog.setCancelable(false);
            String ci=item.getTitle().toString();
            String chooseYear=ci.substring(0, 9);
            String chooseSemester=ci.substring(10,11);
            if(currentSelectedStr.substring(9).equals(chooseSemester)){
                if(currentSelectedStr.substring(0,9).equals(chooseYear)){
                    Log.d("get","get");
                    MyFirstCourseThread myFirstCourseThread=new MyFirstCourseThread(linkURL);
                    myFirstCourseThread.start();
                }
                else {
                    MyCourseThread myCourseThread=new MyCourseThread(linkURL,chooseYear,chooseSemester,"xnd");
                    Log.d("xnd",chooseYear+"-----"+chooseSemester);
                    myCourseThread.start();
                }
            }
            else {
                MyCourseThread myCourseThread=new MyCourseThread(linkURL,chooseYear,chooseSemester,"xqd");
                Log.d("xqd",chooseYear+"-----"+chooseSemester);
                myCourseThread.start();
            }
        }
        return true;
    }

    public interface PassValue{
        void passData(int count);
    }

    @Override
    public void onAttach(Context context) {
        if(getActivity()instanceof PassValue){
            passValue=(PassValue) getActivity();
        }
        super.onAttach(context);
    }
}
