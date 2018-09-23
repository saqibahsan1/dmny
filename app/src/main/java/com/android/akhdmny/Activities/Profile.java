package com.android.akhdmny.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.akhdmny.R;
import com.hbb20.CountryCodePicker;
import com.raywenderlich.android.validatetor.ValidateTor;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    @BindView(R.id.user_profile_Image)
    CircleImageView user_profile_Image;

    @BindView(R.id.Et_firstName)
    EditText Et_firstName;

    @BindView(R.id.et_lastName)
    EditText et_lastName;

    @BindView(R.id.et_Mobile)
    EditText et_Mobile;

    @BindView(R.id.et_password)
    EditText et_password;

    @BindView(R.id.et_Confirm_password)
    EditText et_Confirm_password;

    @BindView(R.id.et_address)
    EditText et_address;

    @BindView(R.id.et_gender)
    EditText et_gender;

    @BindView(R.id.Btn_save)
    Button Btn_save;

    @BindView(R.id.ccp_getFullNumber)
    CountryCodePicker ccp_getFullNumber;
    ValidateTor validateTor = new ValidateTor();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");
    }

    private void Validate(){
        View focusView = null;
        Boolean cancel = false;
        if (validateTor.isEmpty(Et_firstName.getText().toString())){
            Et_firstName.setError("Please enter your First name");
            focusView = Et_firstName;
            cancel = true;

        }if (validateTor.isEmpty(et_lastName.getText().toString())){
            et_lastName.setError("Please enter your last name");
            focusView = et_lastName;
            cancel = true;
        }if (validateTor.isEmpty(et_Mobile.getText().toString())){
            et_Mobile.setError("Please enter your number");
            focusView = et_Mobile;
            cancel = true;
        }if (validateTor.isEmpty(et_gender.getText().toString())){
            et_gender.setError("Please select your gender");
            focusView = et_gender;
            cancel = true;
        }
        if (validateTor.isEmpty(et_address.getText().toString())){
            et_address.setError("Please select your address");
            focusView = et_address;
            cancel = true;
        }
        if (validateTor.isAtleastLength(et_password.getText().toString(),6)
                && et_password.getText().toString().equals(et_Confirm_password.getText().toString()) )
        {
            cancel = false;

        }else {
            cancel = true;
            focusView = et_password;
            et_password.setError("Password did not matched");
        }
        if (cancel){
            if (focusView!=null){
                focusView.requestFocus();
            }
        }else {

        }
    }

    private void UpdateProfileApi(){

    }
}
