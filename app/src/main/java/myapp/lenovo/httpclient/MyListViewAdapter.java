package myapp.lenovo.httpclient;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Lenovo on 2017/2/2.
 */

public class MyListViewAdapter extends BaseAdapter {
    private List<String[]> planList;
    private LayoutInflater inflater;

    MyListViewAdapter(List<String[]> planList, Context context){
        this.planList=planList;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return planList.size();
    }

    @Override
    public Object getItem(int i) {
        return planList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null)
            view=inflater.inflate(R.layout.list_item,null);
        TextView order= (TextView) view.findViewById(R.id.order_tv);
        TextView name= (TextView) view.findViewById(R.id.name_tv);
        TextView necessity= (TextView) view.findViewById(R.id.necessity_tv);
        TextView credit= (TextView) view.findViewById(R.id.credit_content_tv);
        TextView period= (TextView) view.findViewById(R.id.period_content_tv);
        TextView check= (TextView) view.findViewById(R.id.check_content_tv);
        TextView category= (TextView) view.findViewById(R.id.category_content_tv);
        TextView code= (TextView) view.findViewById(R.id.code_content_tv);
        TextView week= (TextView) view.findViewById(R.id.week_content_tv);

        order.setText(i+1+".");
        for(int j=0;j<=6;j++)
            if(planList.get(i)[j].length()<=1)
                planList.get(i)[j]="无";
        if(planList.get(i)[14].length()<=1)
            planList.get(i)[14]="无";
        name.setText(planList.get(i)[1]);
        necessity.setText(planList.get(i)[5]);
        credit.setText(planList.get(i)[2]);
        period.setText(planList.get(i)[3]);
        check.setText(planList.get(i)[4]);
        category.setText(planList.get(i)[6]);
        code.setText(planList.get(i)[0]);
        week.setText(planList.get(i)[14]);

        String cn=planList.get(i)[5];
        //Log.d("cn"+i,cn);
        if(cn.equals("必修课"))
            necessity.setTextColor(R.color.colorBlack);
        else
            necessity.setTextColor(R.color.colorDarkGray);

        return view;
    }
}
