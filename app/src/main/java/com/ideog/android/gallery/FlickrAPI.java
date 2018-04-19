package com.ideog.android.gallery;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlickrAPI {
    @GET("/services/rest/?method=flickr.photos.search")
    Observable<FlickrResultModel> getPhotos(
            @Query("api_key") String apiKey,
            @Query("text") String text,
            @Query("sort") String sort,
            @Query("nojsoncallback") String nojsoncallback,
            @Query("format") String format,
            @Query("extras") String extras
    );
}
