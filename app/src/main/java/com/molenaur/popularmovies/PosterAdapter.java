package com.molenaur.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.molenaur.popularmovies.utilities.TheMovieDatabaseUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by jmolenaur on 7/9/2017.
 */

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.ViewHolder> {
    private List<PosterData> posters;
    private Context context;
    private PosterItemClickListener posterItemClickListener;

    public PosterAdapter(Context context, List<PosterData> posters, PosterItemClickListener listener) {
        this.context = context;
        this.posters = posters;
        posterItemClickListener = listener;
    }

    public interface PosterItemClickListener {
        void onPosterItemClick(int posterIndex);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.poster_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PosterData posterData = posters.get(position);
        holder.bind(posterData);
    }

    @Override
    public int getItemCount() {
        return posters.size();
    }

    public void addPosterData(List<PosterData> posters) {
        this.posters.addAll(posters);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView posterImage;
        private TextView posterTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            posterImage = (ImageView) itemView.findViewById(R.id.iv_poster);
            posterTitle = (TextView) itemView.findViewById(R.id.tv_movie_title);
            itemView.setOnClickListener(this);
        }

        public void bind(PosterData posterData) {
            if (posterData.getPosterPath() == null ||
                    posterData.getPosterPath().isEmpty() ||
                    posterData.getPosterPath().equalsIgnoreCase("null")) {
                // sometimes the poster path comes back as "null" - in this case, just display the title
                posterImage.setVisibility(GONE);
                posterTitle.setText(posterData.getTitle());
                posterTitle.setVisibility(View.VISIBLE);
            } else {
                URL posterUrl = TheMovieDatabaseUtils.getMoviePosterUrl(posterData.getPosterPath());
                posterTitle.setVisibility(GONE);
                Picasso.with(context).load(posterUrl.toString()).into(posterImage);
                posterImage.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View view) {
            int clickPosition = getAdapterPosition();
            posterItemClickListener.onPosterItemClick(clickPosition);
        }
    }
}
