package com.android.akhdmny.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.akhdmny.ApiResponse.AcceptModel.Driver;
import com.android.akhdmny.MainActivity;
import com.android.akhdmny.NetworkManager.NetworkConsume;
import com.android.akhdmny.R;
import com.fuzzproductions.ratingbar.RatingBar;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Driver_Ratings extends AppCompatActivity {

    @BindView(R.id.driver_img)
    ImageView driver_img;

    @BindView(R.id.driver_name)
    TextView driver_name;

    @BindView(R.id.driver_rate)
    TextView driver_rate;

    @BindView(R.id.carName)
    TextView carName;

    @BindView(R.id.carnumber)
    TextView carnumber;

    @BindView(R.id.et_driver_review)
    EditText et_driver_review;

    @BindView(R.id.ratingBar)
    RatingBar ratingBar;

    @BindView(R.id.Done_review_btn)
    Button Done_review_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_rating);
        ButterKnife.bind(this);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    ratingBar.setRating(rating);
            }
        });


        try{
            String driver = NetworkConsume.getInstance().getDefaults("D_model",Driver_Ratings.this);
            Gson gson = new Gson();
            Driver obj = gson.fromJson(driver, Driver.class);
            driver_name.setText(obj.getName());
            carName.setText(obj.getCarCompany());
            carnumber.setText(obj.getCarModel());


        }catch (Exception e){

        }
        Done_review_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkConsume.getInstance().setDefaults("D_model","",Driver_Ratings.this);
                NetworkConsume.getInstance().setDefaults("O_model","",Driver_Ratings.this);
                NetworkConsume.getInstance().setDefaults("U_model","",Driver_Ratings.this);
                NetworkConsume.getInstance().setDefaults("orderId","",Driver_Ratings.this);
                Intent start = new Intent(Driver_Ratings.this,MainActivity.class);
                start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(start);
                finish();
            }
        });
    }
}
