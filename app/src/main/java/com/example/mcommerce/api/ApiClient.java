package com.example.mcommerce.api;


import android.content.Context;


import com.example.mcommerce.BaseActivity;
import com.example.mcommerce.models.response.ResAlert;
import com.example.mcommerce.models.response.ResCategories;
import com.example.mcommerce.models.response.ResLogin;
import com.example.mcommerce.models.response.ResProducts;
import com.example.mcommerce.models.response.ResShop;
import com.example.mcommerce.models.response.ResTotalPoint;
import com.example.mcommerce.models.response.ResTransactions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.cert.CertificateException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;


public class ApiClient {

    public static ApiInterface apiMainService;

    public static ApiInterface getApiClient(Context context) {
        String api_root = ((BaseActivity)context).mPrefs.getServerUrl() + "/virox_mcommerce/api/v1/";

        if (apiMainService == null) {

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = getUnsafeOkHttpClient();

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(api_root)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
            apiMainService = retrofit.create(ApiInterface.class);
        }

        return apiMainService;
    }

    private static OkHttpClient getUnsafeOkHttpClient() {

        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.readTimeout(60, TimeUnit.SECONDS);
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.writeTimeout(30, TimeUnit.SECONDS);
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    public interface ApiInterface {
        @Multipart
        @POST("signup")
        Call<ResLogin> signUpAPI(@Part MultipartBody.Part body, @PartMap Map<String, RequestBody> params);


        @FormUrlEncoded
        @POST("signin")
        Call<ResLogin> signInAPI(@Field("email") String email, @Field("password") String password, @Field("fcm_token") String fcm_token);


        @FormUrlEncoded
        @POST("getProductList")
        Call<ResProducts> getProductsAPI(@FieldMap Map<String, String> maps);

        @FormUrlEncoded
        @POST("getShopList")
        Call<ResShop> getShopsAPI(@FieldMap Map<String, String> maps);

        @FormUrlEncoded
        @POST("getTransactions")
        Call<ResTransactions> getTransactionsAPI(@FieldMap Map<String, String> maps);

        @FormUrlEncoded
        @POST("getTotalPoint")
        Call<ResTotalPoint> getTotalPointAPI(@Field("user_id") String user_id);

        @GET("getProductCategories")
        Call<ResCategories> getProductCategories();

        @FormUrlEncoded
        @POST("getAlerts")
        Call<ResAlert> getAlertsAPI(@Field("pageNum") int pageNum, @Field("searchKey") String searchKey);

    }

}
