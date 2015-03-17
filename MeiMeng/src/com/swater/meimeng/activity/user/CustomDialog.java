package com.swater.meimeng.activity.user;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.meimeng.app.R;
import com.swater.meimeng.activity.reg.RegChoose;
import com.swater.meimeng.commbase.MeiMengApp;

/**
 * Created by shi-02 on 2015/3/13.
 */
public class CustomDialog extends Dialog {

    private Context context;

    private Button button;

    private Button registerButton;

    private TextView textView;

    private int id;
    public CustomDialog(Context context){
        super(context);
        this.context = context;
    }

    public CustomDialog(Context context, int theme,int id){
        super(context,theme);
        this.context = context;
        this.id = id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visitor_guide_dialog);

        button = (Button)findViewById(R.id.visitor_guide_dialog_cancel_button);
        registerButton = (Button)findViewById(R.id.visitor_guide_dialog_register_button);
        textView = (TextView)findViewById(R.id.dialog_text);
        //退出按钮
        if (id == 0){
            registerButton.setText("确定");
            textView.setText("是否登录？");
            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,UserLogin.class);
                    context.startActivity(intent);
                    dismiss();
                }
            });
        }else {
            registerButton.setText("注册");
            textView.setText("请先注册");
            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, RegChoose.class);
                    context.startActivity(intent);
                    dismiss();
                }
            });
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


    }
}


