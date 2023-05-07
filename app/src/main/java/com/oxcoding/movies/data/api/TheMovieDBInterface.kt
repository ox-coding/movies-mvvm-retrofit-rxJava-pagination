package com.oxcoding.movies.data.api

import com.oxcoding.movies.data.vo.MovieDetails
import com.oxcoding.movies.data.vo.MovieResponse
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDBInterface {

    @GET("movie/popular")  //upcoming , popular
    fun getPopularMovie(
        @Query("page") page: Int
        ): Single<MovieResponse>


    @GET("movie/{movie_id}")  //upcoming , popular
    fun getMovieDetails(
        @Path("movie_id") id: Int
    ): Single<MovieDetails>

}