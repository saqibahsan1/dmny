package com.android.akhdmny.Service;

import com.android.akhdmny.ApiResponse.AcceptModel.AcceptOrderApiModel;
import com.android.akhdmny.ApiResponse.AddComplaintResponse;
import com.android.akhdmny.ApiResponse.AddToCart;
import com.android.akhdmny.ApiResponse.CartApi.CartApiResp;
import com.android.akhdmny.ApiResponse.CartOrder;
import com.android.akhdmny.ApiResponse.DriverList;
import com.android.akhdmny.ApiResponse.FourSquare;
import com.android.akhdmny.ApiResponse.OrderConfirmation;
import com.android.akhdmny.ApiResponse.CategoriesDetailResponse;
import com.android.akhdmny.ApiResponse.CategoriesResponse;
import com.android.akhdmny.ApiResponse.ComplaintHistoryResponse;
import com.android.akhdmny.ApiResponse.LoginApiResponse;
import com.android.akhdmny.ApiResponse.OrderId;
import com.android.akhdmny.ApiResponse.ParcelApiResponse;
import com.android.akhdmny.ApiResponse.ProfileResponse;
import com.android.akhdmny.ApiResponse.RegisterResponse;
import com.android.akhdmny.ApiResponse.UpdateTokenResponse;
import com.android.akhdmny.ApiResponse.createOrder.CreateOrderResp;
import com.android.akhdmny.Requests.CreateOrderRequest;
import com.android.akhdmny.Requests.LoginRequest;
import com.android.akhdmny.Requests.RequestOrder;
import com.android.akhdmny.Requests.SignInRequest;
import com.android.akhdmny.Requests.VerificationReguest;
import com.android.akhdmny.Requests.requestOrder;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface AuthService {

    @PUT("/akhdmny/public/api/user/register")
    Call<RegisterResponse> register(@Body SignInRequest request);

    @POST("/akhdmny/public/api/user/login")
    Call<LoginApiResponse> LoginApi(@Body LoginRequest request);


    @POST("/akhdmny/public/api/user/create-order")
    Call<CreateOrderResp> CreateOrder(@Body CreateOrderRequest request);

    @POST("/akhdmny/public/api/user/verification")
    Call<LoginApiResponse> VerificationApi(@Body VerificationReguest request);

    @GET("/akhdmny/public/api/user/categories")
    Call<CategoriesResponse> CategoryApi();

    @GET("/akhdmny/public/api/user/cart_item")
    Call<CartApiResp> CartOrders(@Query("lat") double lat, @Query("long") double longitude);

    @GET("/akhdmny/public/api/user/cart_item/destroy")
    Call<CartOrder> RemoveCartOrders(@Query("cart_id") int userId, @Query("lat") double lat, @Query("long") double longi);

    @GET("/akhdmny/public/api/user/services")
    Call<CategoriesDetailResponse> CatDetails(@Query("category_id") int id, @Query("lat") double lat,@Query("long") double longitude,
                                              @Query("address") String address);

    @GET("/akhdmny/public/api/user/problem")
    Call<ComplaintHistoryResponse> History();

    @GET("/akhdmny/public/api/user/foursquare")
    Call<FourSquare> fourSquareApiCall(@Query("lat") double lat, @Query("long") double longi);

    @GET("/akhdmny/public/api/user/accept-bid")
    Call<AcceptOrderApiModel> AcceptBidApi(@Query("order_id") int OrderId, @Query("bid") int Bid, @Query("driver_id") int DriverId);

    @POST("/createOrder")
    Call<OrderId> OrderRequest(@Body RequestOrder order);

    @GET("/updateToken")
    Call<UpdateTokenResponse> Token(@Query("id") int id,@Query("token") String token);

    @GET("/acceptedDrivers")
    Call<DriverList> BidApi(@Query("orderId") String orderId);

    @Multipart
    @POST("/akhdmny/public/api/user/problem?lat=24.9070714&long=67.1124509")
    Call<AddComplaintResponse> AddComplaint(@Part("description") RequestBody description, @Part("title") RequestBody title,
                                            @Part MultipartBody.Part[] Images, @Part MultipartBody.Part Sound);
    @Multipart
    @POST("/akhdmny/public/api/user/cart/store")
    Call<AddToCart> addToCart(@Part("description") RequestBody description,@Part("title") RequestBody title,
                              @Part("type") int type,
                              @Part("address") String address,@Part("amount") double amount,
                              @Part("distance") double distance,@Part("lat") double lat,@Part("long") double longitude,
                              @Part("service_id") String service_id,
                              @Part MultipartBody.Part[] Images, @Part MultipartBody.Part Sound);

    @Multipart
    @POST("/akhdmny/public/api/user/parcel")
    Call<ParcelApiResponse> ParcelApi(@Part("from_lat") double lat, @Part("from_long") double longitude,
                                      @Part("to_lat") double to_lat, @Part("to_long") double to_longitude,
                                      @Part("description") String description, @Part("amount") int amount, @Part("distance") String distance,
                                      @Part MultipartBody.Part[] Images, @Part MultipartBody.Part Sound);

    @Multipart
    @POST("/akhdmny/public/api/user/update/profile")
    Call<ProfileResponse> UpdateProfile(@Part("first_name") String firstNme, @Part("last_name") String last_name,
                                        @Part("password") String password, @Part("email") String email,
                                        @Part("address") String address, @Part("gender") String gender, @Part MultipartBody.Part[] Image);
}
