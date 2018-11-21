package com.android.akhdmny.Authenticate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.akhdmny.ApiResponse.LoginApiResponse;
import com.android.akhdmny.ApiResponse.RegisterResponse;
import com.android.akhdmny.ErrorHandling.LoginApiError;
import com.android.akhdmny.MainActivity;
import com.android.akhdmny.NetworkManager.NetworkConsume;
import com.android.akhdmny.R;
import com.android.akhdmny.Requests.LoginRequest;
import com.android.akhdmny.Requests.SignInRequest;
import com.android.akhdmny.Utils.UserDetails;
import com.android.akhdmny.Utils.Validator;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.victor.loading.rotate.RotateLoading;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class login extends AppCompatActivity {

    @BindView(R.id.et_Mobile)
    EditText et_Mobile;
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.btn_register)
    Button btn_register;
    @BindView(R.id.btn_login)
    Button btn_login;
    @BindView(R.id.rotateloading)
    RotateLoading rotateLoading;
    @BindView(R.id.btn_forgot_password)
    Button btn_forgot_password;
    @BindView(R.id.btn_skip)
    Button btn_skip;
    @BindView(R.id.ccp_getFullNumber)
    CountryCodePicker ccp_getFullNumber;
    String token = "Basic YWhsYW0tYXBwLWFuZHJvaWQ6NGQxNjNlZTgtMzJiZi00M2U2LWFlMzgtY2E1YmMwZjA0N2Nk";
    private Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ButterKnife.bind(this);
        ClickEvent();

    }

    private void ClickEvent(){
        mActivity = this;
        ccp_getFullNumber.registerCarrierNumberEditText(et_Mobile);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this,Registration.class));

            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });
    }

    private void Login(){


        Validator validator = new Validator(login.this, true);

        validator
                .setRules(Validator.Rules.REQUIRED, Validator.Rules.MIN)
                .validate(ccp_getFullNumber.getFullNumberWithPlus(), et_Mobile, 3)
                .validate(et_password.getText().toString(), et_password, 3);

        if (validator.fails()) {

            return;
        }

        NetworkConsume.getInstance().setAccessKey("Basic "+token);
        LoginRequest request = new LoginRequest();
        request.setPhone(ccp_getFullNumber.getFullNumberWithPlus());
        request.setPassword(et_password.getText().toString());
        NetworkConsume.getInstance().ShowProgress(login.this);
        NetworkConsume.getInstance().getAuthAPI().LoginApi(request).enqueue(new Callback<LoginApiResponse>() {
            @Override
            public void onResponse(Call<LoginApiResponse> call, Response<LoginApiResponse> response) {
                if (response.isSuccessful()) {
                    LoginApiResponse apiResponse = response.body();
                    UserDetails.username = apiResponse.getResponse().getId();
                    Gson gson = new Gson();
                    String json = gson.toJson(apiResponse.getResponse());
                    NetworkConsume.getInstance().setDefaults("login",json,login.this);
                    NetworkConsume.getInstance().setDefaults("id",String.valueOf(apiResponse.getResponse().getId()),login.this);
                    SharedPreferences prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
                    prefs.edit().putString("access_token", apiResponse.getResponse().getAccessToken())
                            .putString("avatar",apiResponse.getResponse().getAvatar()).commit();
                    startActivity(new Intent(login.this,MainActivity.class));
//                    rotateLoading.stop();

                    finish();
                    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
                  NetworkConsume.getInstance().HideProgress(login.this);

                }else {
                   // rotateLoading.stop();
                    NetworkConsume.getInstance().HideProgress(login.this);
                    Gson gson = new Gson();
                    LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                    Toast.makeText(mActivity, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<LoginApiResponse> call, Throwable t) {
                NetworkConsume.getInstance().HideProgress(login.this);
                Toast.makeText(login.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
        });
    }

}
