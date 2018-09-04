package com.bersama.uploadimage.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {
    private static Retrofit retro;
    private static final String base_url = "http://192.168.1.8/api/uploadimage/";


    private static Retrofit getClient()
    {
        if (retro == null)
        {
            retro = new Retrofit.Builder()
                    .baseUrl(base_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retro;
    }
    public static ApiServices getApiServices()
    {
        return getClient().create(ApiServices.class);
    }
}
