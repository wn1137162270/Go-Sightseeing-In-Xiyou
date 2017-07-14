package myapp.lenovo.httpclient;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CourseTableView extends RelativeLayout {
    private int weekNum;
    private int[] weekNumsUS={7,1,2,3,4,5,6};
    private String[] monthDate;
    private String month;
    private int totalWeekNum;
    private int totalSectionNum;

    private int firstRow;
    private int notFirstRow;
    private int firstColumn;
    private int notFirstColumn;

    private TextView firstTextView;
    private int padding0;
    private int padding1;
    private String[] weekDays={"周一","周二","周三","周四","周五","周六","周日"};
    private FrameLayout frameLayout;

    private List<? extends Course> courseDate;
    private List<View> myCacheView=new ArrayList<>();

    private static final int FIRST_TEXT_VIEW_ID = 555;
    private static final int FIRST_REST_NUM=3;
    private static final int[] COURSE_COLOR={R.drawable.course_info_light_blue, R.drawable.course_info_red,
            R.drawable.course_info_dark_green, R.drawable.course_info_orange,
            R.drawable.course_info_purple, R.drawable.course_info_green_blue,
            R.drawable.course_info_yellow, R.drawable.course_info_blue,
            R.drawable.course_info_pink, R.drawable.course_info_green};

    public CourseTableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray=context.getTheme().obtainStyledAttributes(attrs,R.styleable
        .CourseTable,defStyleAttr,0);
        totalWeekNum=typedArray.getInt(R.styleable.CourseTable_totalWeek,7);
        totalSectionNum=typedArray.getInt(R.styleable.CourseTable_totalSection,12);
        typedArray.recycle();
        init();
    }

    public CourseTableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CourseTableView(Context context) {
        this(context, null);
    }

    public void init(){
        Log.d("init0","init0");
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        weekNum=calendar.get(Calendar.DAY_OF_WEEK)-1;
        monthDate=getMonthDateOfWeek();
        drawFrame();
    }

    public String[] getMonthDateOfWeek(){
        Calendar calendar=Calendar.getInstance();
        String[] dates=new String[totalWeekNum];
        int wn=weekNumsUS[weekNum];
        if(wn!=7)
            calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
        else {
            calendar.add(Calendar.DAY_OF_MONTH,-1);
            calendar.set(Calendar.DAY_OF_WEEK,2);
        }
        int md=0;
        for(int i=1;i<totalWeekNum;i++){
            if(i==1){
                md=calendar.get(Calendar.DAY_OF_MONTH);
                dates[i-1]=md+"";
                month=(calendar.get(Calendar.MONTH)+1)+"月";
            }
            calendar.add(Calendar.DATE,1);
            if(calendar.get(Calendar.DAY_OF_MONTH)<md){
                dates[i]=(calendar.get(Calendar.MONTH)+1)+"月";
                md=calendar.get(Calendar.DAY_OF_MONTH);
            }
            else {
                dates[i]=calendar.get(Calendar.DAY_OF_MONTH)+"";
            }
        }
        return dates;
    }

    public void drawFrame(){
        initSize();
        drawFirstRow();
        drawRestRow();
    }

    public void initSize(){
        int screenHeight;
        int screenWidth;
        screenHeight=DensityUtils.getScreenHeight(getContext());
        screenWidth=DensityUtils.getScreenWidth(getContext());
        firstRow=DensityUtils.dipToPx(getContext(),40);
        notFirstColumn=screenWidth*2/(2*totalWeekNum+1);
        firstColumn=notFirstColumn/2;
        notFirstRow=notFirstColumn;
        //notFirstRow=(screenHeight-firstRow)/totalSectionNum+DensityUtils.dipToPx(getContext(),5);
    }

    public void drawFirstRow(){
        initFirstTextView();
        initRestTextView();
    }

    public void initFirstTextView(){
        firstTextView=new TextView(getContext());
        padding0 = DensityUtils.dipToPx(getContext(), 1);
        padding1=DensityUtils.dipToPx(getContext(),2);
        firstTextView.setId(FIRST_TEXT_VIEW_ID);
        RelativeLayout.LayoutParams lp=new LayoutParams(firstColumn,firstRow);
        firstTextView.setLayoutParams(lp);
        firstTextView.setText(month);
        firstTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
        firstTextView.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
        firstTextView.setBackgroundResource(R.drawable.course_table_bg);
        firstTextView.setPadding(padding0,padding1*2, padding0,padding1*2);
        addView(firstTextView);
    }

    public void initRestTextView(){
        LinearLayout ll;
        RelativeLayout.LayoutParams lp;
        TextView tvMonthDate;
        TextView tvWeekDay;
        for(int i=0;i<totalWeekNum;i++){
            ll=new LinearLayout(getContext());
            ll.setId(FIRST_REST_NUM+i);
            lp=new LayoutParams(notFirstColumn,firstRow);
            ll.setOrientation(LinearLayout.VERTICAL);
            if(i==0)
                lp.addRule(RelativeLayout.RIGHT_OF,firstTextView.getId());
            else
                lp.addRule(RelativeLayout.RIGHT_OF,FIRST_REST_NUM+i-1);
            ll.setLayoutParams(lp);
            ll.setBackgroundResource(R.drawable.course_table_bg);
            LinearLayout.LayoutParams llp=new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tvMonthDate=new TextView(getContext());
            tvMonthDate.setLayoutParams(llp);
            tvMonthDate.setText(monthDate[i]);
            tvMonthDate.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
            tvMonthDate.setGravity(Gravity.CENTER);
            tvMonthDate.setPadding(padding1,padding1,padding1,padding1);
            ll.addView(tvMonthDate);
            llp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT);
            tvWeekDay=new TextView(getContext());
            tvWeekDay.setLayoutParams(llp);
            tvWeekDay.setText(weekDays[i]);
            tvWeekDay.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
            tvWeekDay.setGravity(Gravity.CENTER|Gravity.BOTTOM);
            tvWeekDay.setPadding(padding1,0,padding1,padding1*2);
            ll.addView(tvWeekDay);
            if(weekNumsUS[weekNum]-1==i){
                ll.setBackgroundColor(0x77069ee9);
                tvMonthDate.setTextColor(Color.WHITE);
                tvWeekDay.setTextColor(Color.WHITE);
            }
            addView(ll);
        }
    }

    public void drawRestRow(){
        ScrollView sv=new ScrollView(getContext());
        RelativeLayout.LayoutParams rlp=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        rlp.addRule(RelativeLayout.BELOW,firstTextView.getId());
        sv.setLayoutParams(rlp);
        sv.setVerticalScrollBarEnabled(false);

        LinearLayout ll=new LinearLayout(getContext());
        ViewGroup.LayoutParams vlp=new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setLayoutParams(vlp);

        LinearLayout lll=new LinearLayout(getContext());
        LinearLayout.LayoutParams llp=new LinearLayout.LayoutParams(firstColumn
                , LinearLayout.LayoutParams.WRAP_CONTENT);
        lll.setLayoutParams(llp);
        lll.setOrientation(LinearLayout.VERTICAL);

        initLeftTextView(lll);
        ll.addView(lll);

        frameLayout=new FrameLayout(getContext());
        LinearLayout.LayoutParams llp2=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , LinearLayout.LayoutParams.MATCH_PARENT);
        frameLayout.setLayoutParams(llp2);
        initRightFrame();
        ll.addView(frameLayout);

        sv.addView(ll);

        addView(sv);
    }

    public void initLeftTextView(LinearLayout lll){
        LinearLayout.LayoutParams llp=new LinearLayout.LayoutParams(firstColumn,notFirstRow);
        TextView tv;
        for(int i=0;i<totalSectionNum;i++){
            tv=new TextView(getContext());
            tv.setLayoutParams(llp);
            tv.setText(""+(i+1));
            tv.setTextColor(Color.GRAY);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundResource(R.drawable.course_table_bg);
            lll.addView(tv);
        }
    }

    public void initRightFrame(){
        FrameLayout fl;
        FrameLayout.LayoutParams flp;
        for(int i=0;i<totalSectionNum*totalWeekNum;i++){
            int row=i/totalWeekNum;
            int col=i%totalWeekNum;
            fl=new FrameLayout(getContext());
            flp=new FrameLayout.LayoutParams(notFirstColumn,notFirstRow);
            flp.setMargins(col*notFirstColumn,row*notFirstRow,0,0);
            fl.setLayoutParams(flp);
            fl.setBackgroundResource(R.drawable.course_table_bg);
            frameLayout.addView(fl);
        }
    }

    public void updateCourseData(List<? extends Course> courseDate){
        this.courseDate=courseDate;
        updataCourseView();
    }

    public void updateCourseData(){
        updataCourseView();
    }

    public void updataCourseView(){
        clearCourseView();
        FrameLayout fl;
        FrameLayout.LayoutParams flp;
        TextView tv;
        int k=0;
        for(int i=0;i<courseDate.size();i++){
            int flag=0;
            final Course course=courseDate.get(i);
            int section= course.getSection();
            int day=course.getDay();
            fl=new FrameLayout(getContext());
            flp=new FrameLayout.LayoutParams(notFirstColumn,notFirstRow*course.getCourseLong());
            flp.setMargins(notFirstColumn*(day-1),notFirstRow*(section-1),0,0);
            fl.setLayoutParams(flp);
            fl.setPadding(padding1,padding1,padding1,padding1);

            tv=new TextView(getContext());
            flp=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT);
            tv.setLayoutParams(flp);
            tv.setText(course.getCourseName());
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
            tv.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
            tv.setPadding(padding0,padding0,padding0,padding0);
            tv.setEllipsize(TextUtils.TruncateAt.END);
            tv.setLines(7);
            for(int j=0;j<i;j++){
                if(courseDate.get(j).getCourseName().equals(course.getCourseName())){
                    course.setColor(courseDate.get(j).getColor());
                    tv.setBackgroundResource(courseDate.get(j).getColor());
                    flag=1;
                    break;
                }
            }
            if(flag==0){
                if(k==10)
                    k=0;
                course.setColor(COURSE_COLOR[k]);
                tv.setBackgroundResource(COURSE_COLOR[k]);
                k++;
            }
            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    final MyCourseDialog myCourseDialog=new MyCourseDialog(getContext(),
                            course.getCourseName(),course.getCourseDetail());
                    myCourseDialog.setOnConfirmClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            myCourseDialog.dismiss();
                        }
                    });
                    myCourseDialog.show();
                }
            });
            fl.addView(tv);
            myCacheView.add(fl);
            frameLayout.addView(fl);
        }
    }

    public void clearCourseView(){
        if(myCacheView==null||myCacheView.isEmpty())
            return;
        for(int i=myCacheView.size()-1;i>=0;i--){
            frameLayout.removeView(myCacheView.get(i));
            myCacheView.remove(i);
        }
    }

}
