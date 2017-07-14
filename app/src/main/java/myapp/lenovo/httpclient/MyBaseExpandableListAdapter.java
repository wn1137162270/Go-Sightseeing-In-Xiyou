package myapp.lenovo.httpclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by Lenovo on 2017/1/25.
 */

public class MyBaseExpandableListAdapter extends BaseExpandableListAdapter {
    private List<String> groupName;
    private Map<String,List<String[]>> childName;
    private Context context;
    private LayoutInflater inflater;

    MyBaseExpandableListAdapter(List<String> groupName, Map<String,List<String[]>> childName
            , Context context){
        this.groupName=groupName;
        this.childName=childName;
        this.context=context;
        this.inflater=LayoutInflater.from(this.context);
    }

    @Override
    public int getGroupCount() {
        return groupName.size();
    }

    @Override
    public int getChildrenCount(int i) {
        String group=groupName.get(i);
        List<String[]> childList=childName.get(group);
        return childList.size();
    }

    @Override
    public Object getGroup(int i) {
        return groupName.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        String group=groupName.get(i);
        return childName.get(group).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.group_item, null);
        }
        ImageView pd = (ImageView) view.findViewById(R.id.pull_down_iv);
        TextView gnm = (TextView) view.findViewById(R.id.group_name_tv);

        pd.setImageResource(R.drawable.pull_down_off);
        if (b) {
            pd.setImageResource(R.drawable.pull_down_on);
        }
        String gn=groupName.get(i);
        String year=gn.substring(0,gn.length()-1);
        String sem=gn.substring(gn.length()-1);
        gnm.setText(year+"第"+sem+"学期");

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.child_item, null);
        }
        TextView cn = (TextView) view.findViewById(R.id.child_name_tv);
        TextView cp = (TextView) view.findViewById(R.id.child_property_tv);
        TextView cs = (TextView) view.findViewById(R.id.child_score_tv);

        String gn=groupName.get(i);
        cn.setText(childName.get(gn).get(i1)[3]);
        cp.setText(childName.get(gn).get(i1)[4]);
        cs.setText(childName.get(gn).get(i1)[8]);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
