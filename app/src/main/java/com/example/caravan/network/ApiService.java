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

        @Headers({"Authorization: key=AAAAAQUSFTc:APA91bEqVttlQUxYkjfSPD_X2IpXXRxGR4yk3qSsxm-1mlHg3sfgKXeXGOA-wp5_-Oe1VDEtnGhrHDv24bCcly_eHI3HTswPzeXCcWyqm-V5HKciGf1ws-9DQcP5HPTO4K3an4r-z_Su",
                "Content-Type: application/json"})
        @POST("fcm/send")
        Call<String> sendMessage(
                @HeaderMap HashMap<String, String> headers,
                @Body String messageBody
        ); //Response<ResponseBody>
    }


