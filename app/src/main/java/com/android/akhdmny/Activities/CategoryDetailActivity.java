package com.android.akhdmny.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.akhdmny.Adapter.CatDetailAdapter;
import com.android.akhdmny.Adapter.FourSquarAdapter;
import com.android.akhdmny.Adapter.ImagesAdapter;
import com.android.akhdmny.ApiResponse.AddToCart;
import com.android.akhdmny.ApiResponse.Categories.CategoryDetailsResponse;
import com.android.akhdmny.ApiResponse.Categories.Service;
import com.android.akhdmny.ApiResponse.MyChoice.FourSquare;
import com.android.akhdmny.ApiResponse.MyChoice.Venue;
import com.android.akhdmny.ErrorHandling.LoginApiError;
import com.android.akhdmny.Fragments.ServicesActivity;
import com.android.akhdmny.MainActivity;
import com.android.akhdmny.NetworkManager.NetworkConsume;
import com.android.akhdmny.R;
import com.android.akhdmny.Requests.CategoryDetailsRequest;
import com.android.akhdmny.Singletons.Cordinates;
import com.android.akhdmny.Utils.GPSActivity;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class CategoryDetailActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    ArrayList<Service> list;
    ArrayList<Venue> listFoureSquare;
    String currency;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.et_searchText)
    EditText et_searchText;
    @BindView(R.id.search_btn)
    Button search_btn;
    @BindView(R.id.topBar)
    LinearLayout topBar;
    @BindView(R.id.main_layout)
    LinearLayout main_layout;
    @BindView(R.id.imgTopBar)
    ImageView imgTopBar;
    @BindView(R.id.titleTopBar)
    TextView titleTopBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    TextView tvTitle;
    SharedPreferences prefs;
    GPSActivity gpsActivity;
    ImageView recordAudio ;
    ImageView playAudio ;
    ImageView AddImages;
    MediaPlayer mediaPlayer;
    RecyclerView recyclerViewPopup;
    EditText et_Description;
    SeekBar seekBar;

    AlertDialog alertDialog;
    private Handler mHandler;
    private Runnable mRunnable;
    private ArrayList<File> photos = new ArrayList<>();
    private ImagesAdapter imagesAdapter;
    public static final int RequestPermissionCode = 0;
    String ApiType = "";
    private static String AUDIO_FILE_PATH = "";


    String token = "Basic YWhsYW0tYXBwLWFuZHJvaWQ6NGQxNjNlZTgtMzJiZi00M2U2LWFlMzgtY2E1YmMwZjA0N2Nk";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_details);
        ButterKnife.bind(this);
        mHandler = new Handler();

        gpsActivity = new GPSActivity(this);
        setSupportActionBar(toolbar);
        tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvTitle.setText("Services");
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        list = new ArrayList<>();
        prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        try {
            String color = (NetworkConsume.getInstance().getDefaults("colour",this));
            topBar.setBackgroundColor(Color.parseColor(color));
        }catch (Exception e){

        }
        ApiType = NetworkConsume.getInstance().getDefaults("title",this);

        titleTopBar.setText(ApiType);
        Picasso.get().load(NetworkConsume.getInstance().getDefaults("image",this)).into(imgTopBar);

        if (ApiType.toLowerCase().equals("my choice")){
            FourSquareApi();
        }else {

            DetailApi("");
        }
        itemTouchLister();

    }

    private void FourSquareApi(){
        NetworkConsume.getInstance().ShowProgress(CategoryDetailActivity.this);
        NetworkConsume.getInstance().setAccessKey(prefs.getString("access_token","12"));
        NetworkConsume.getInstance().getAuthAPI().fourSquareApiCall(Cordinates.getInstance().model.getLatitude(),Cordinates.getInstance().model.getLongitude()).
                enqueue(new Callback<FourSquare>() {
                    @Override
                    public void onResponse(Call<FourSquare> call, Response<FourSquare> response) {
                        if (response.isSuccessful()) {
                            FourSquare square = response.body();
                            if ( square.getResponse().getResponse().getVenues().size() != 0) {
                                listFoureSquare = new ArrayList<>();
                                for (int i = 0; i < square.getResponse().getResponse().getVenues().size(); i++) {
                                    listFoureSquare.add(square.getResponse().getResponse().getVenues().get(i));
                                }
                                currency = square.getResponse().getCurrency();
                                FourSquarAdapter myAdapter = new FourSquarAdapter(CategoryDetailActivity.this, listFoureSquare,square.getResponse().getCurrency());
                                recyclerView.setAdapter(myAdapter);
                                NetworkConsume.getInstance().HideProgress(CategoryDetailActivity.this);
                            }else {
                                NetworkConsume.getInstance().SnackBarError(main_layout,CategoryDetailActivity.this,R.string.error_text);
                                NetworkConsume.getInstance().HideProgress(CategoryDetailActivity.this);
                            }
                        }else {
                            Gson gson = new Gson();
                            NetworkConsume.getInstance().HideProgress(CategoryDetailActivity.this);
                            LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                            Toast.makeText(CategoryDetailActivity.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<FourSquare> call, Throwable t) {
                        NetworkConsume.getInstance().HideProgress(CategoryDetailActivity.this);
                        Toast.makeText(CategoryDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    protected void initializeSeekBar(){
        seekBar.setMax(mediaPlayer.getDuration());

        mRunnable = new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                }
                mHandler.postDelayed(mRunnable,50);
            }
        };
        mHandler.postDelayed(mRunnable,50);
    }

    private void DetailApi(String address){
        NetworkConsume.getInstance().ShowProgress(CategoryDetailActivity.this);
        NetworkConsume.getInstance().setAccessKey(token);
        CategoryDetailsRequest categoryDetailsRequest = new CategoryDetailsRequest();
        categoryDetailsRequest.setAddress(address);
        categoryDetailsRequest.setCategory_id(NetworkConsume.getInstance().getDefaults("cat_id",CategoryDetailActivity.this));
        categoryDetailsRequest.setLat(Cordinates.getInstance().model.getLatitude());
        categoryDetailsRequest.setLongitude(Cordinates.getInstance().model.getLongitude());
        NetworkConsume.getInstance().getAuthAPI().CatDetails(categoryDetailsRequest).
                enqueue(new Callback<CategoryDetailsResponse>() {
            @Override
            public void onResponse(Call<CategoryDetailsResponse> call, Response<CategoryDetailsResponse> response) {
                if (response.isSuccessful()) {
                    CategoryDetailsResponse categoriesResponse = response.body();
                    if ( categoriesResponse.getResponse().getServices().size() != 0) {
                        list = new ArrayList<>();

                        for (int i = 0; i < categoriesResponse.getResponse().getServices().size(); i++) {
                            list.add(categoriesResponse.getResponse().getServices().get(i));
                        }
                        Cordinates.getInstance().currency = categoriesResponse.getResponse().getCurrency();
                        CatDetailAdapter myAdapter = new CatDetailAdapter(CategoryDetailActivity.this, list,categoriesResponse.getResponse().getCurrency());
                        recyclerView.setAdapter(myAdapter);
                        NetworkConsume.getInstance().HideProgress(CategoryDetailActivity.this);
                    }else {
                        NetworkConsume.getInstance().HideProgress(CategoryDetailActivity.this);
                        NetworkConsume.getInstance().SnackBarError(main_layout,CategoryDetailActivity.this,R.string.error);
                    }
                }else {
                    Gson gson = new Gson();
                    NetworkConsume.getInstance().HideProgress(CategoryDetailActivity.this);
                    LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                    Toast.makeText(CategoryDetailActivity.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<CategoryDetailsResponse> call, Throwable t) {
                NetworkConsume.getInstance().HideProgress(CategoryDetailActivity.this);
                Toast.makeText(CategoryDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
    private void itemTouchLister() {

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    DetailApi(et_searchText.getText().toString());

            }
        });
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ServicesActivity.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final AlertDialog.Builder ADD_Cart = new AlertDialog.Builder(CategoryDetailActivity.this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View viewCart = inflater.inflate(R.layout.add_cart, null);
                ImageView imageView = viewCart.findViewById(R.id.img_Resturaunt);
                Button btn = viewCart.findViewById(R.id.button_addToCart);
                LinearLayout linearLayout = viewCart.findViewById(R.id.bg_Popup);
                seekBar = viewCart.findViewById(R.id.seek_bar);
                TextView textViewTitle = viewCart.findViewById(R.id.Tv_title);
                et_Description = viewCart.findViewById(R.id.textDialog);
                recordAudio = viewCart.findViewById(R.id.recordAudio);
                playAudio = viewCart.findViewById(R.id.PlayAudio);
                AddImages = viewCart.findViewById(R.id.addImages);
                recyclerViewPopup = viewCart.findViewById(R.id.recycler_view);
                recyclerViewPopup.setLayoutManager(new LinearLayoutManager(CategoryDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
                recyclerViewPopup.setHasFixedSize(true);
                TextView textViewAddress = viewCart.findViewById(R.id.TV_Mob);
                TextView textViewprice = viewCart.findViewById(R.id.tv_email);
                if (ApiType.toLowerCase().equals("my choice")){
                    Picasso.get().load(R.drawable.place_holder).into(imageView);
                    textViewTitle.setText(listFoureSquare.get(position).getName());
                    textViewAddress.setText(String.valueOf(listFoureSquare.get(position).getLocation().getFormattedAddress().get(0)+","+listFoureSquare.get(position).getLocation().getDistance()+" km"));
                    textViewprice.setText(new DecimalFormat("##").format(listFoureSquare.get(position).getAmount())+" "+currency);

                }else {
                    Picasso.get().load(list.get(position).getImage()).into(imageView);
                    textViewTitle.setText(list.get(position).getTitle());
                    textViewAddress.setText(list.get(position).getAddress());
                    textViewprice.setText(list.get(position).getPrice().toString()+" "+Cordinates.getInstance().currency);
                }
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (et_Description.getText().toString().equals(""))
                        {
                            NetworkConsume.getInstance().SnackBarError(linearLayout,CategoryDetailActivity.this, R.string.error_text);
                        }else {
                            if (ApiType.toLowerCase().equals("my choice")){

                                addDataTOApi(listFoureSquare.get(position).getId(), listFoureSquare.get(position).getName(), 2,
                                        listFoureSquare.get(position).getLocation().getCountry(), listFoureSquare.get(position).getLocation().getDistance(),
                                        listFoureSquare.get(position).getAmount(), listFoureSquare.get(position).getLocation().getLat(),
                                        listFoureSquare.get(position).getLocation().getLng());
                            }
                            else {
                                addDataTOApi(list.get(position).getId().toString(), list.get(position).getTitle(), 1, list.get(position).getAddress(),
                                        list.get(position).getDistance(), list.get(position).getPrice(), list.get(position).getLat(), list.get(position).getLong());
                            }
                        }
                    }
                });
                recordAudio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(checkPermission()) {
                           AUDIO_FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/recorded_audio.wav";
                            AndroidAudioRecorder.with(CategoryDetailActivity.this)
                                    // Required
                                    .setFilePath(AUDIO_FILE_PATH)
                                    .setColor(ContextCompat.getColor(CategoryDetailActivity.this, R.color.colorPrimary))
                                    .setRequestCode(RequestPermissionCode)

                                    // Optional
                                    .setSource(AudioSource.MIC)
                                    .setChannel(AudioChannel.STEREO)
                                    .setSampleRate(AudioSampleRate.HZ_48000)
                                    .setAutoStart(false)
                                    .setKeepDisplayOn(true)

                                    // Start recording
                                    .record();



                        }else {
                            requestPermission();
                        }
                    }
                });
                playAudio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       bottomSheet();
                    }
                });
                AddImages.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onPickPhoto();
                    }
                });

                ADD_Cart.setView(viewCart);
                ADD_Cart.setCancelable(true);
                alertDialog = ADD_Cart.create();
                alertDialog.show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }
    private void bottomSheet(){
        BottomSheetMenuDialog dialog = new BottomSheetBuilder(CategoryDetailActivity.this, R.style.AppTheme_BottomSheetDialog)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .setMenu(R.menu.bottom_menu)
                .addDividerItem()
                .setItemBackground(R.drawable.drawer_item_bg)
                .setItemTextColorResource(R.color.colorAccent)
                .setBackgroundColorResource(R.color.colorPrimary)
                .setItemClickListener(new BottomSheetItemClickListener() {
                    @Override
                    public void onBottomSheetItemClick(MenuItem item) {
                        if (item.getTitle().equals("Listen")){
                            if(isPlaying()){
                                stopPlaying();
                            } else {
                                startPlaying(AUDIO_FILE_PATH);
                            }
                        }
                        if (item.getTitle().equals("Remove")){
                            recordAudio.setVisibility(View.VISIBLE);
                            playAudio.setVisibility(View.GONE);
                            seekBar.setProgress(0);
                            seekBar.setVisibility(View.GONE);
                        }
                        if (item.getTitle().equals("Record")){
                            if(checkPermission()) {


                                AndroidAudioRecorder.with(CategoryDetailActivity.this)
                                        // Required
                                        .setFilePath(AUDIO_FILE_PATH)
                                        .setColor(ContextCompat.getColor(CategoryDetailActivity.this, R.color.colorPrimary))
                                        .setRequestCode(RequestPermissionCode)

                                        // Optional
                                        .setSource(AudioSource.MIC)
                                        .setChannel(AudioChannel.STEREO)
                                        .setSampleRate(AudioSampleRate.HZ_48000)
                                        .setAutoStart(false)
                                        .setKeepDisplayOn(true)

                                        // Start recording
                                        .record();
                            }else {
                                requestPermission();
                            }
                        }
                    }
                })
                .createDialog();

        dialog.show();
    }

    private void onPickPhoto() {
        EasyImage.openChooserWithGallery(this,"Pick source",0);
    }
    private void startPlaying(String source){
        try {

            mediaPlayer = new MediaPlayer();
            seekBar.setVisibility(View.VISIBLE);
            mediaPlayer.setOnCompletionListener(CategoryDetailActivity.this);

            try {
                mediaPlayer.setDataSource(source);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.start();
            initializeSeekBar();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private void stopPlaying(){
        playAudio.setImageResource(R.drawable.ic_play);


        if(mediaPlayer != null){
            try {
                seekBar.setProgress(0);
                mediaPlayer.stop();
                mediaPlayer.reset();
                seekBar.setVisibility(View.GONE);
                if(mHandler!=null){
                    mHandler.removeCallbacks(mRunnable);
                }
            } catch (Exception e){ }
        }

    }
    private boolean isPlaying(){
        try {
            return mediaPlayer != null && mediaPlayer.isPlaying();
        } catch (Exception e){
            return false;
        }
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        stopPlaying();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                // Great! User has recorded and saved the audio file
                recordAudio.setVisibility(View.GONE);
                playAudio.setVisibility(View.VISIBLE);
            } else if (resultCode == RESULT_CANCELED) {
                // Oops! User has canceled the recording
            }
        }
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                e.printStackTrace();
            }

            @Override
            public void onImagesPicked(List<File> imageFiles, EasyImage.ImageSource source, int type) {
                onPhotosReturned(imageFiles);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(CategoryDetailActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }
    private void onPhotosReturned(List<File> returnedPhotos) {
        photos.addAll(returnedPhotos);

        imagesAdapter = new ImagesAdapter(this, photos);

        recyclerViewPopup.setAdapter(imagesAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this,
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(this,
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ServicesActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ServicesActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }


    }
    private void addDataTOApi(String id,String title,int type,String address,double distance,double amount,double lat, double longitude){
        NetworkConsume.getInstance().ShowProgress(CategoryDetailActivity.this);
        MultipartBody.Part voicePart = null;
        File AudioFile = new File(AUDIO_FILE_PATH);
        if (AudioFile.length() == 0){}
        else {
            RequestBody propertyImage = RequestBody.create(MediaType.parse("audio/*"), AudioFile);
            voicePart = MultipartBody.Part.createFormData("voice", AudioFile.getName(), propertyImage);
        }
        MultipartBody.Part[] ImagesParts = new MultipartBody.Part[photos.size()];
        for (int i = 0; i<photos.size();i++) {
            File addImageFile = new File(photos.get(i).getPath());
            RequestBody ImageBody = RequestBody.create(MediaType.parse("image/*"), addImageFile);
            ImagesParts[i] = MultipartBody.Part.createFormData("images["+i+"]",addImageFile.getName(),ImageBody);
        }
        NetworkConsume.getInstance().setAccessKey(prefs.getString("access_token","12"));

        RequestBody Description = RequestBody.create(MediaType.parse("text/plain"), et_Description.getText().toString());
        RequestBody Title = RequestBody.create(MediaType.parse("text/plain"),title);
       // RequestBody Address = RequestBody.create(MediaType.parse("text/plain"), address);
        NetworkConsume.getInstance().getAuthAPI().addToCart(Description,Title,type,address,amount,distance,lat,longitude,id,ImagesParts,voicePart)
                .enqueue(new Callback<AddToCart>() {
                    @Override
                    public void onResponse(Call<AddToCart> call, Response<AddToCart> response) {
                        if (response.isSuccessful()) {
                            AddToCart categoriesResponse = response.body();
                            photos = new ArrayList<>();
                            photos.clear();
                            et_Description.setText("");
                            et_Description.setHint(R.string.Message);
                            recordAudio.setVisibility(View.VISIBLE);
                            playAudio.setVisibility(View.GONE);
                            AUDIO_FILE_PATH = "";
                            NetworkConsume.getInstance().SnackBarSucccess(main_layout,CategoryDetailActivity.this,R.string.success_CartItem);
                           alertDialog.dismiss();
                            NetworkConsume.getInstance().HideProgress(CategoryDetailActivity.this);
                        }else {
                            NetworkConsume.getInstance().HideProgress(CategoryDetailActivity.this);
                            Gson gson = new Gson();
                            LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                            Toast.makeText(CategoryDetailActivity.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onFailure(Call<AddToCart> call, Throwable t) {
                        NetworkConsume.getInstance().HideProgress(CategoryDetailActivity.this);
                        Toast.makeText(CategoryDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_service, menu);
        MenuItem item = menu.findItem(R.id.button_item);
        Button btn = item.getActionView().findViewById(R.id.btn_MyCart);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CategoryDetailActivity.this, MyCart.class));
                overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
            }
        });

        return true;
    }


}
