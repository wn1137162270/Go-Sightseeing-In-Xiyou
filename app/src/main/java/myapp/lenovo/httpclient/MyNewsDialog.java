package myapp.lenovo.httpclient;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Lenovo on 2017/2/7.
 */

public class MyNewsDialog extends Dialog {
    private TextView content;
    private ImageButton close;
    private Button next;

    public MyNewsDialog(Context context,String contentStr) {
        super(context,R.style.myNewsDialog);

        View view= LayoutInflater.from(getContext()).inflate(R.layout.dialog_news_my,null);
        content = (TextView) view.findViewById(R.id.content_tv);
        close= (ImageButton) view.findViewById(R.id.close_ib);
        next= (Button) view.findViewById(R.id.next_ib);

        content.setText(contentStr);
        super.setContentView(view);
    }

    public void setOnCloseClickListener(View.OnClickListener onCloseClickListener){
        close.setOnClickListener(onCloseClickListener);
    }

    public void setOnNextClickListener(View.OnClickListener onNextClickListener){
        next.setOnClickListener(onNextClickListener);
    }

    public TextView getContent() {
        return content;
    }
}
