package uk.org.lsucs.lanvantracker.retrofit.van;


import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import uk.org.lsucs.lanvantracker.retrofit.models.VanLocation;
import uk.org.lsucs.lanvantracker.retrofit.models.VanStatus;

/**
 * Created by Zack Pollard on 11/11/17.
 */

public interface VanAPI {

    @GET("/api/v1/van/status")
    Observable<VanStatus> getVanStatus();

    @GET("/api/v1/van/location")
    Observable<VanLocation> getVanLocation();

    @PUT("/api/v1/van/location")
    Observable<VanLocation> setVanLocation(@Body VanLocation body);
}
