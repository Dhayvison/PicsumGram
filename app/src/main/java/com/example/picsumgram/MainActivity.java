package com.example.picsumgram;

import static com.example.picsumgram.presentation.adapter.PostAdapter.getScreenWidthInPixels;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.FixedPreloadSizeProvider;
import com.example.picsumgram.data.model.PostWithUser;
import com.example.picsumgram.presentation.adapter.PostAdapter;
import com.example.picsumgram.presentation.uistate.PostListState;
import com.example.picsumgram.presentation.viewmodel.PostViewModel;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private PostAdapter postAdapter;
    private RecyclerViewPreloader<PostWithUser> preloader;

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.posts_recyclerview);
        ShimmerFrameLayout loading = findViewById(R.id.posts_loading);
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView offlineIndicator = findViewById(R.id.offline_indicator);

        setSupportActionBar(toolbar);

        postAdapter = new PostAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postAdapter);

        PostViewModel postViewModel = new ViewModelProvider(this).get(PostViewModel.class);

        swipeRefreshLayout.setOnRefreshListener(postViewModel::loadPosts);

        postViewModel.getState().observe(this, state -> {
            if (state instanceof PostListState.Loading) {
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
                loading.setVisibility(View.GONE);
                loading.stopShimmer();
                swipeRefreshLayout.setRefreshing(false);

                PostListState.Success successState = (PostListState.Success) state;
                List<PostWithUser> posts = successState.getPosts();

                postAdapter.submitList(posts);

                if (preloader == null) {

                    PostAdapter.PostModelProvider provider = new PostAdapter.PostModelProvider(this, posts);

                    int screenWidth = getScreenWidthInPixels(this);

                    int height = (int) (screenWidth * 1.5);

                    FixedPreloadSizeProvider<PostWithUser> sizeProvider =
                            new FixedPreloadSizeProvider<>(screenWidth, height);

                    // Cria o ListPreloader
                    preloader = new RecyclerViewPreloader<PostWithUser>(
                            Glide.with(this),
                            provider,
                            sizeProvider,
                            10
                    );

                    recyclerView.addOnScrollListener(preloader);
                }
            } else if (state instanceof PostListState.Error) {

                loading.setVisibility(View.GONE);
                loading.stopShimmer();
                swipeRefreshLayout.setRefreshing(false);

                PostListState.Error errorState = (PostListState.Error) state;
                String errorMessage = errorState.getMessage();

                Log.e(TAG, "Estado: Erro de Rede: " + errorMessage);

                Intent intent = new Intent(MainActivity.this, ErrorActivity.class);

                intent.putExtra(ErrorActivity.EXTRA_ERROR_MESSAGE, errorMessage);

                startActivity(intent);

                finish();
            }
        });

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        connectivityManager.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                runOnUiThread(() -> offlineIndicator.setVisibility(View.GONE));
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                runOnUiThread(() -> offlineIndicator.setVisibility(View.VISIBLE));
            }
        });

        if (!isNetworkAvailable()) {
            offlineIndicator.setVisibility(View.VISIBLE);
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (layoutManager != null && layoutManager.findFirstVisibleItemPosition() > 0) {
                    recyclerView.smoothScrollToPosition(0);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }
}