package com.android.akhdmny.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.akhdmny.NetworkManager.NetworkConsume;
import com.android.akhdmny.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Add_tip extends AppCompatActivity {
    @BindView(R.id.tipLL)
    LinearLayout tipLL;


    @BindView(R.id.inputTip)
    EditText inputTip;


    @BindView(R.id.submitTip)
    Button submitTip;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bid_input);
        ButterKnife.bind(this);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(inputTip, InputMethodManager.SHOW_IMPLICIT);


        submitTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputTip.getText().toString().equals("")){
                    Toast.makeText(Add_tip.this, "Please enter the tip...", Toast.LENGTH_SHORT).show();
                }else {
                   NetworkConsume.getInstance().setDefaults("tip",inputTip.getText().toString(),Add_tip.this);
                   finish();
                }
            }
        });
        tipLL.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = tipLL.getRootView().getHeight() - tipLL.getHeight();
                if (heightDiff > dpToPx(Add_tip.this, 200)) { // if more than 200 dp, it's probably a keyboard...
                    // ... do something here
                }
            }
        });


    }
    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }
}
