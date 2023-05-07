package com.oxcoding.movies.ui.SingleMovie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.oxcoding.movies.R
import com.oxcoding.movies.data.api.POSTER_BASE_URL
import com.oxcoding.movies.data.api.TheMovieDBClient
import com.oxcoding.movies.data.api.TheMovieDBInterface
import com.oxcoding.movies.data.repository.NetworkState
import com.oxcoding.movies.data.repository.Status
import com.oxcoding.movies.data.vo.MovieDetails
import com.oxcoding.movies.util.ConnectivityInterceptor
import kotlinx.android.synthetic.main.activity_single_movie.*
import java.text.NumberFormat
import java.util.*


class SingleMovie : AppCompatActivity() {

    private lateinit var viewModel: SingleMovieViewModel
    lateinit var movieRepository: MovieDetailsRepository



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_movie)

        val apiService : TheMovieDBInterface = TheMovieDBClient.getClient(ConnectivityInterceptor(this))
        movieRepository = MovieDetailsRepository(apiService)

        val movieId: Int = intent.getIntExtra("id",1)
        viewModel = getViewModel(movieId)

        viewModel.movieDetails.observe(this, Observer {
            bindUI(it)
        })

        viewModel.networkState.observe(this, Observer {
            progress_bar_popular.visibility = if (it == NetworkState.LOADING) View.VISIBLE else View.GONE
            txt_error.visibility = if (it.status == Status.FAILED) View.VISIBLE else View.GONE

        })

    }



    fun bindUI( it: MovieDetails){
        val formatCurrency = NumberFormat.getCurrencyInstance(Locale.US)

        movie_title.text = it.title
        movie_tagline.text = it.tagline
        movie_release_date.text = it.releaseDate
        movie_rating.text = it.rating
        movie_runtime.text = it.runtime.toString() + " minutes"
        movie_budget.text = formatCurrency.format(it.budget)
        movie_revenue.text = formatCurrency.format(it.revenue)
        movie_overview.text = it.overview

        val moviePosterURL = POSTER_BASE_URL + it.posterPath
        Glide.with(this)
                .load(moviePosterURL)
                .into(iv_movie_poster);
    }


    private fun getViewModel(movieId:Int): SingleMovieViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SingleMovieViewModel(movieRepository,movieId) as T
            }
        })[SingleMovieViewModel::class.java]
    }
}
