package com.fsociety.nirapotta_v3.Remote;


import com.fsociety.nirapotta_v3.Model.FCMResponse;
import com.fsociety.nirapotta_v3.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {

    @Headers({

            "Content-Type:application/json",
            "Authorization:key=AAAA_9iLsvs:APA91bEiNQIpOHw6TnLaxrBaNTLZ718z6X5oilDqfNUdQAN-At_kJ1asLaUBDGL9DWLsAi7O8xMiYoVIuqY-m1reli3eDasxX5rUNJL_BK3PbGKUJHp6XKbb7tb5XrFhtR27TxYJiioPQT1bbTdZvxDizd0otlpEgA"
    })
    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body Sender body);


}
