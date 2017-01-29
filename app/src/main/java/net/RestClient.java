package net;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

// Creates singleton of the retrofit object
public class RestClient {

    // static instance to be used in entire app
    public static volatile Retrofit retrofit = null;
    // interface containing HTTP methods
    private static SteamApiService steamApiService;
    // create logger to be basic level of logging
    private static final HttpLoggingInterceptor logger = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
        @Override
        public void log(String message) {
            Log.d("MESSAGE", message);
        }
    }).setLevel(HttpLoggingInterceptor.Level.BASIC);
    // create custom client to extend timeout
    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(logger)
            .build();

    protected RestClient() {
        // Exists only to defeat instantiation
    }

    public static Retrofit getRetrofitInstance() {
        if(retrofit == null) {
            synchronized (RestClient.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(AppConstants.BASE_ADDRESS)
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(okHttpClient)
                            .build();
                }
            }
        }
        return retrofit;
    }

    public static SteamApiService getSteamApiInterface() {
        if(steamApiService == null) {
            synchronized (RestClient.class) {
                if(steamApiService == null)
                    steamApiService = getRetrofitInstance().create(SteamApiService.class);
            }
        }
        return steamApiService;
    }

}
