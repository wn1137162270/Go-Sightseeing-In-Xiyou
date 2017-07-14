package myapp.lenovo.httpclient;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Lenovo on 2017/2/6.
 */

public class MyProgressBarDialog extends Dialog {

    public MyProgressBarDialog(Context context,String wordStr) {
        super(context,R.style.myProgressBarDialog);

        View view=LayoutInflater.from(getContext()).inflate(R.layout.dialog_progress_bar_my,null);
        TextView word = (TextView) view.findViewById(R.id.word_tv);
        word.setText(wordStr);
        super.setContentView(view);
    }
}
