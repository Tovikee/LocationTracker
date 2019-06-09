package com.example.locationtracker.Remote;

import com.example.locationtracker.Models.MyResponse;
import com.example.locationtracker.Models.Request;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
        "Content-Type:application/json",
            "Authorization:key=AAAAZZ_gfNQ:APA91bHDP-fZS5a5KjYzVfy-DtZSolsxJhhAdRSX9-bfVRERTQC7QpRUXovJc9ubUl68sQUEiHruv01-Ln--KNrk1eVPxqutkqNaRf-TvHv9wzkNXeHExtx4CDJTKp_aC4wfg3Edmdod"
    })
    @POST("fcm/send")
    Observable<MyResponse> sendFriendRequestToUser(@Body Request body);

}
