package com.example.seniormvvmproject;

import static com.example.seniormvvmproject.presentation.adapter.PostAdapter.PostModelProvider.PRELOAD_AHEAD_ITEMS;
import static com.example.seniormvvmproject.presentation.adapter.PostAdapter.getScreenWidthInPixels;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.FixedPreloadSizeProvider;
import com.example.seniormvvmproject.data.model.Post;
import com.example.seniormvvmproject.presentation.adapter.PostAdapter;
import com.example.seniormvvmproject.presentation.uistate.PostListState;
import com.example.seniormvvmproject.presentation.viewmodel.PostViewModel;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private PostAdapter postAdapter;
    private RecyclerViewPreloader<Post> preloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.posts_recyclerview);
        ShimmerFrameLayout loading = findViewById(R.id.posts_loading);
        TextView textViewError = findViewById(R.id.textViewError);
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        postAdapter = new PostAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postAdapter);

        PostViewModel postViewModel = new ViewModelProvider(this).get(PostViewModel.class);

        swipeRefreshLayout.setOnRefreshListener(postViewModel::loadPosts);

        postViewModel.getState().observe(this, state -> {
            if (state instanceof PostListState.Loading) {
                textViewError.setVisibility(View.GONE);

                if (postAdapter.getCurrentList().isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    loading.setVisibility(View.VISIBLE);
                    loading.startShimmer();
                } else {
                    loading.setVisibility(View.GONE);
                }

                postAdapter.submitList(null);
            } else if (state instanceof PostListState.Success) {
                recyclerView.setVisibility(View.VISIBLE);
                textViewError.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
                loading.stopShimmer();
                swipeRefreshLayout.setRefreshing(false);

                PostListState.Success successState = (PostListState.Success) state;
                List<Post> posts = successState.getPosts();

                postAdapter.submitList(posts);

                if (preloader == null) {

                    PostAdapter.PostModelProvider provider = new PostAdapter.PostModelProvider(this, posts);

                    int screenWidth = getScreenWidthInPixels(this);

                    FixedPreloadSizeProvider<Post> sizeProvider =
                            new FixedPreloadSizeProvider<>(screenWidth, screenWidth);

                    // Cria o ListPreloader
                    preloader = new RecyclerViewPreloader<>(
                            Glide.with(this),
                            provider,
                            sizeProvider,
                            PRELOAD_AHEAD_ITEMS
                    );

                    recyclerView.addOnScrollListener(preloader);
                }
            } else if (state instanceof PostListState.Error) {
                recyclerView.setVisibility(View.GONE);
                textViewError.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
                loading.stopShimmer();
                swipeRefreshLayout.setRefreshing(false);

                PostListState.Error errorState = (PostListState.Error) state;
                Toast.makeText(MainActivity.this, errorState.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Estado: Erro de Rede: " + errorState.getMessage());
            }
        });
    }
}