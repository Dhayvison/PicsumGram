package com.example.seniormvvmproject.presentation.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.seniormvvmproject.R;
import com.example.seniormvvmproject.data.model.Post;

import java.util.Collections;
import java.util.List;

public class PostAdapter extends ListAdapter<Post, PostAdapter.PostViewHolder> {

    private static final DiffUtil.ItemCallback<Post> DIFF_CALLBACK = new DiffUtil.ItemCallback<Post>() {

        @Override
        public boolean areItemsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
            return oldItem.getId() == newItem.getId(); // Compara o ID (mesma entidade)
        }

        @Override
        public boolean areContentsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
            return oldItem.equals(newItem); // Compara o conteúdo (dados)
        }

        // 7. 💡 Geração do Payload (Chamado se areContentsTheSame retornar FALSE)
        @Override
        public @Nullable Object getChangePayload(@NonNull Post oldItem, @NonNull Post newItem) {
            Bundle diff = new Bundle();

            // Verifica as diferenças e adiciona apenas o campo que mudou ao Bundle
            if (!oldItem.getTitle().equals(newItem.getTitle())) {
                diff.putString("title", newItem.getTitle());
            }

            if (!oldItem.getBody().equals(newItem.getBody())) {
                diff.putString("body", newItem.getBody());
            }

            // Retorna o Bundle se houver mudanças, ou null para o fallback padrão do onBindViewHolder
            if (diff.size() == 0) {
                return null;
            }
            return diff;
        }
    };

    // 1. Construtor que passa a lógica de comparação para o ListAdapter
    public PostAdapter() {
        super(DIFF_CALLBACK);
    }

    public static int getScreenWidthInPixels(Context context) {
        // 1. Obtém o DisplayMetrics, que contém informações sobre o display.
        DisplayMetrics displayMetrics = new DisplayMetrics();

        // 2. Obtém o WindowManager a partir do Contexto.
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }

        // 4. Retorna a largura em pixels.
        return displayMetrics.widthPixels;
    }

    // 3. Criação do ViewHolder
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View postView = inflater.inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(postView);
    }

    // 4. Bind padrão (Chamado se o Payload for null ou em reciclagem normal)
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post currentPost = getItem(position);

        int screenWidth = getScreenWidthInPixels(holder.itemView.getContext());
        Glide.with(holder.itemView.getContext())
                .load("https://picsum.photos/id/" + currentPost.getId() + "/" + screenWidth + "/" + screenWidth)
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Adiciona o Caching
                .transition(DrawableTransitionOptions.withCrossFade()) // Efeito de transição
                .into(holder.imageViewPost);
        holder.titleTextView.setText(currentPost.getTitle());
        holder.bodyTextView.setText(currentPost.getBody());
    }

    // 5. ⚠️ Bind com Payload (Chamado apenas em caso de atualização de conteúdo)
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            // Se não há Payload (não é uma mudança de conteúdo), usa o bind padrão (acima)
            super.onBindViewHolder(holder, position, payloads);
            return;
        }

        // Se o payload não estiver vazio, sabemos que é o nosso Bundle de diferenças
        Bundle bundle = (Bundle) payloads.get(0);

        // Atualiza SÓ o que mudou, com base no que o DiffUtil calculou
        if (bundle.containsKey("title")) {
            holder.titleTextView.setText(bundle.getString("title"));
        }
        if (bundle.containsKey("body")) {
            holder.bodyTextView.setText(bundle.getString("body"));
        }
        //
    }

    // 2. ViewHolder (Armazena as referências de View)
    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView bodyTextView;
        ImageView imageViewPost;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_title);
            bodyTextView = itemView.findViewById(R.id.text_body);
            imageViewPost = itemView.findViewById(R.id.image_post);
        }
    }


    public static class PostModelProvider implements ListPreloader.PreloadModelProvider<Post> {

        public static int PRELOAD_AHEAD_ITEMS = 3;
        private final List<Post> posts;
        private final Context context;

        public PostModelProvider(Context context, List<Post> posts) {
            this.context = context;
            this.posts = posts;
        }

        @NonNull
        @Override
        public List<Post> getPreloadItems(int position) {
            if (posts.isEmpty()) {
                return Collections.emptyList();
            }

            int end = Math.min(position + PRELOAD_AHEAD_ITEMS, posts.size());

            return posts.subList(position, end);
        }

        @Nullable
        @Override
        public RequestBuilder<?> getPreloadRequestBuilder(@NonNull Post item) {
            int screenWidth = getScreenWidthInPixels(context);

            String imageUrl = "https://picsum.photos/id/" + item.getId() + "/" + screenWidth + "/" + screenWidth;

            return Glide.with(context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade());
        }
    }
}