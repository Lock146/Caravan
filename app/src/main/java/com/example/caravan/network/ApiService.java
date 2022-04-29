package com.example.caravan.network;

import com.example.caravan.Constant.Constants;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

        @POST("send")
        Call<String> sendMessage(
                @HeaderMap HashMap<String, String> headers,
                @Body String messageBody
        );
    }


