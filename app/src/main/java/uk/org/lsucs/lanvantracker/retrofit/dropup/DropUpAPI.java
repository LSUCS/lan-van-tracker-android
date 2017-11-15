package uk.org.lsucs.lanvantracker.retrofit.dropup;


import java.util.LinkedList;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import uk.org.lsucs.lanvantracker.retrofit.models.CurrentDropUp;
import uk.org.lsucs.lanvantracker.retrofit.models.DropUp;

/**
 * Created by Zack Pollard on 11/11/17.
 */

public interface DropUpAPI {

    @GET("/api/v1/dropups")
    Observable<LinkedList<DropUp>> getDropUps();

    @PUT("/api/v1/dropups")
    @Multipart
    Observable<LinkedList<DropUp>> setDropUps(@Part MultipartBody.Part file);

    @GET("/api/v1/dropups/current")
    Observable<CurrentDropUp> getCurrentDropUp();

    @PUT("/api/v1/dropups/current")
    Observable<CurrentDropUp> setCurrentDropUp(@Body CurrentDropUp body);
}
