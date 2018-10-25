package com.android.akhdmny.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.akhdmny.Activities.Profile;
import com.android.akhdmny.ApiResponse.LoginInsideResponse;
import com.android.akhdmny.Authenticate.login;
import com.android.akhdmny.LocaleHelper;
import com.android.akhdmny.MainActivity;
import com.android.akhdmny.NetworkManager.NetworkConsume;
import com.android.akhdmny.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentSettings extends Fragment {
    @BindView(R.id.relative_layout_Profile)
    RelativeLayout relativeLayout;

    @BindView(R.id.img_Resturaunt)
    CircleImageView img_Resturaunt;

    @BindView(R.id.Tv_name)
    TextView Tv_name;
    @BindView(R.id.TV_Mob)
    TextView TV_Mob;
    @BindView(R.id.tv_email)
    TextView tv_email;



    @BindView(R.id.logoutLayout)
    LinearLayout logoutLayout;
    @BindView(R.id.Lang_Laoyout)
    RelativeLayout Lang_Laoyout;



    public FragmentSettings() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);
        ButterKnife.bind(this,view);

        try {
            Gson gson = new Gson();
            String jsonLoin = NetworkConsume.getInstance().getDefaults("login",getActivity());
            LoginInsideResponse response = gson.fromJson(jsonLoin,LoginInsideResponse.class);
            Picasso.get().load(response.getAvatar()).into(img_Resturaunt);
            String splitter = response.getName();
            String[] strings = splitter.split(" ");

            Tv_name.setText(response.getName());
            TV_Mob.setText(response.getPhone());
            tv_email.setText(response.getEmail());

        }catch (Exception e){}

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Profile.class));
                getActivity().overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
            }
        });

        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogue();
            }
        });
        Lang_Laoyout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocaleHelper.languageSwitcher.showChangeLanguageDialog((FragmentActivity) getActivity());

            }
        });

        return view;
    }

    private void dialogue(){
        new AlertDialog.Builder(getActivity(), R.style.alert_dialog_theme)
                .setTitle("Exit!")
                .setMessage("Do you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
                        prefs.edit().putString("access_token", "")
                                .putString("avatar","")
                                .putString("login","").commit();

                       startActivity( new Intent(getActivity(),login.class));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                })
                .show();
    }
}
