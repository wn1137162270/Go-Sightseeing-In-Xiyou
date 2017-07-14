package myapp.lenovo.httpclient;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Lenovo on 2017/2/8.
 */

public class MyCourseDialog extends Dialog {
    private Button confirm;

    public MyCourseDialog(Context context, String titleStr,String contentStr) {
        super(context,R.style.myCourseDialog);

        View view= LayoutInflater.from(getContext()).inflate(R.layout.dialog_course_my,null);
        TextView title = (TextView) view.findViewById(R.id.title_tv);
        TextView content = (TextView) view.findViewById(R.id.content_tv);
        confirm= (Button) view.findViewById(R.id.confirm_btn);

        title.setText(titleStr);
        content.setText(contentStr);
        super.setContentView(view);
    }

    public void setOnConfirmClickListener(View.OnClickListener onCloseClickListener){
        confirm.setOnClickListener(onCloseClickListener);
    }

}
