package com.example.arena;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

    public class SupabaseClient {

        // Supabase REST API bağlantı noktamız
        private static final String BASE_URL = "https://cyqjgbkazisikspdbbwn.supabase.co/rest/v1/";

        // Not defterine kopyaladığın sb_publishable_... ile başlayan uzun şifreyi buraya yapıştır
        private static final String API_KEY = "sb_publishable_iOAWK_PHA1rkegWNcMr2_w_vgtFRGaE";

        private static Retrofit retrofit = null;

        public static Retrofit getClient() {
            if (retrofit == null) {
                // Bu interceptor, Supabase'e giden her isteğe otomatik olarak API anahtarımızı ekler
                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("apikey", API_KEY)
                                .addHeader("Authorization", "Bearer " + API_KEY)
                                .build();
                        return chain.proceed(newRequest);
                    }
                }).build();

                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        }
    }
