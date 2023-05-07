package com.oxcoding.movies.ui.popular

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oxcoding.movies.R
import com.oxcoding.movies.data.api.POSTER_BASE_URL
import com.oxcoding.movies.data.vo.Movie
import kotlinx.android.synthetic.main.movie_list_item.view.*
import com.oxcoding.movies.data.repository.NetworkState
import com.oxcoding.movies.data.repository.Status
import com.oxcoding.movies.ui.SingleMovie.SingleMovie
import com.oxcoding.movies.ui.SingleMovie.SingleMovieViewModel
import kotlinx.android.synthetic.main.network_state_item.view.*


class PopularMoviePagedListAdapter (public val context: Context) :
    PagedListAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffCallback()) {

    val DATA_VIEW_TYPE = 1
    val NETWORK_VIEW_TYPE = 2

    private var networkState: NetworkState? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View
        if (viewType == DATA_VIEW_TYPE) {
            view = layoutInflater.inflate(R.layout.movie_list_item, parent, false)
            return MovieItemViewHolder(view)
        } else {
            view = layoutInflater.inflate(R.layout.network_state_item, parent, false)
            return NetworkStateItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == DATA_VIEW_TYPE) {
            (holder as MovieItemViewHolder).bind(getItem(position),context)
        }
        else {
            (holder as NetworkStateItemViewHolder).bind(networkState)
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    private fun hasExtraRow(): Boolean {
        return networkState != null && networkState != NetworkState.LOADED
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            NETWORK_VIEW_TYPE
        } else {
            DATA_VIEW_TYPE
        }
    }

    class MovieItemViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        fun bind(movie: Movie?,context:Context) {
            itemView.cv_movie_title.text = movie?.title
//            if(movie?.releaseDate.toString().length > 4){
//                itemView.cv_movie_release_date.text =  movie?.releaseDate?.substring(0,4)
//            }

            itemView.cv_movie_release_date.text =  movie?.releaseDate
            val moviePosterURL = POSTER_BASE_URL + movie?.posterPath
            Glide.with(itemView.context)
                    .load(moviePosterURL)
                    .into(itemView.cv_iv_movie_poster);

            itemView.setOnClickListener{
                val intent = Intent(context, SingleMovie::class.java)
                intent.putExtra("id", movie?.id)
                context.startActivity(intent)
            }
        }


    }


    class NetworkStateItemViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        fun bind(networkState: NetworkState?) {
            if (networkState != null && networkState.status == Status.RUNNING) {
                itemView.progress_bar.visibility = View.VISIBLE;
            } else {
                itemView.progress_bar.visibility = View.GONE;
            }

            if (networkState != null && networkState.status == Status.FAILED) {
                itemView.error_msg.visibility = View.VISIBLE;
                itemView.error_msg.setText(networkState.msg);
            } else {
                itemView.error_msg.visibility = View.GONE;
            }

        }
    }


    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }

    }


    fun setNetworkState(newNetworkState: NetworkState) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

}