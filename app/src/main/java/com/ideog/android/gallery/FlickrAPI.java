package com.ideog.android.gallery;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlickrAPI {
    @GET("/services/rest/?method=flickr.photos.search")
    Call<FlickrResultModel> getPhotos(
            @Query("api_key") String apiKey,
            @Query("text") String text,
            @Query("sort") String sort,
            @Query("nojsoncallback") String nojsoncallback,
            @Query("format") String format,
            @Query("extras") String extras
    );
}
