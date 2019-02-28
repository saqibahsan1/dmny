package com.android.akhdmny.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.akhdmny.Adapter.BottomSheetAdapter;
import com.android.akhdmny.MainActivity;
import com.android.akhdmny.Models.CancelReasonModel;
import com.android.akhdmny.Models.Reason;
import com.android.akhdmny.NetworkManager.NetworkConsume;
import com.android.akhdmny.R;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("ValidFragment")
public class CancelFragment extends BottomSheetDialogFragment {

    BottomSheetAdapter.DetectReasonSelected detectReasonSelected;
    public CancelFragment(BottomSheetAdapter.DetectReasonSelected selected){
        detectReasonSelected=selected;
    }

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    SharedPreferences prefs;
    ArrayList<Reason> reasonArrayList;
    BottomSheetAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cancel_fragment, container, false);
        ButterKnife.bind(this,view);
        reasonArrayList = new ArrayList<>();
        prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
        cancelApi();
        adapter = new BottomSheetAdapter(getActivity(),reasonArrayList,detectReasonSelected);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void cancelApi(){
        NetworkConsume.getInstance().ShowProgress(getActivity());
        NetworkConsume.getInstance().setAccessKey(prefs.getString("access_token","12"));
        NetworkConsume.getInstance().getAuthAPI().cancelApi().enqueue(new Callback<CancelReasonModel>() {
            @Override
            public void onResponse(Call<CancelReasonModel> call, Response<CancelReasonModel> response) {
                if (response.isSuccessful()){
                    CancelReasonModel model = response.body();
                    if (model != null) {
                        reasonArrayList.addAll(model.getResult().getReasons());
                        adapter.notifyDataSetChanged();
                    }
                    NetworkConsume.getInstance().HideProgress(getActivity());
                }else {
                    NetworkConsume.getInstance().HideProgress(getActivity());
                    Toast.makeText(getActivity(), "Something Went Wrong!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CancelReasonModel> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                NetworkConsume.getInstance().HideProgress(getActivity());
            }
        });
    }


}
