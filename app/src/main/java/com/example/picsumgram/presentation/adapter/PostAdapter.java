package com.example.picsumgram.presentation.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import com.example.picsumgram.R;
import com.example.picsumgram.data.model.PostWithUser;

import java.util.Collections;
import java.util.List;

public class PostAdapter extends ListAdapter<PostWithUser, PostAdapter.PostViewHolder> {

    private static final DiffUtil.ItemCallback<PostWithUser> DIFF_CALLBACK = new DiffUtil.ItemCallback<PostWithUser>() {

        @Override
        public boolean areItemsTheSame(@NonNull PostWithUser oldItem, @NonNull PostWithUser newItem) {
            return oldItem.getPost().getId() == newItem.getPost().getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull PostWithUser oldItem, @NonNull PostWithUser newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public @Nullable Object getChangePayload(@NonNull PostWithUser oldItem, @NonNull PostWithUser newItem) {
            Bundle diff = new Bundle();

            if (!oldItem.getPost().getTitle().equals(newItem.getPost().getTitle())) {
                diff.putString("title", newItem.getPost().getTitle());
            }

            if (!oldItem.getPost().getBody().equals(newItem.getPost().getBody())) {
                diff.putString("body", newItem.getPost().getBody());
            }

            if (diff.isEmpty()) {
                return null;
            }

            return diff;
        }
    };

    public PostAdapter() {
        super(DIFF_CALLBACK);
    }

    public static int getScreenWidthInPixels(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }

        return displayMetrics.widthPixels;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View postView = inflater.inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(postView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostWithUser currentPost = getItem(position);


        if (currentPost.getUser() != null) {
            String name = currentPost.getUser().getName();
            int userId = currentPost.getUser().getId();

            Bitmap avatarBitmap = createAvatarBitmap(getInitials(name), getColorForUser(userId));

            Glide.with(holder.itemView.getContext())
                    .load(avatarBitmap)
                    .circleCrop()
                    .into(holder.avatarImageView);

            holder.authorNameTextView.setText(currentPost.getUser().getName());
            holder.authorEmailTextView.setText(currentPost.getUser().getEmail());
        }


        int screenWidth = getScreenWidthInPixels(holder.itemView.getContext());
        Glide.with(holder.itemView.getContext())
                .load("https://picsum.photos/id/" + currentPost.getPost().getId() + "/" + screenWidth + "/" + screenWidth)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageViewPost);

        holder.titleTextView.setText(currentPost.getPost().getTitle());
        holder.bodyTextView.setText(currentPost.getPost().getBody());
    }

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

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "??";

        String[] parts = name.split(" ");
        if (parts.length > 1) {
            // Pega a primeira letra do primeiro e do último nome
            return (parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0)).toUpperCase();
        } else {
            // Se for um nome só, pega as duas primeiras letras
            return name.length() >= 2 ? name.substring(0, 2).toUpperCase() : name.toUpperCase();
        }
    }

    private Bitmap createAvatarBitmap(String initials, int userId) {
        int size = 120; // Tamanho do avatar em pixels
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // 1. Definir a cor de fundo (baseada no userId)
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(getColorForUser(userId));
        backgroundPaint.setAntiAlias(true);

        // 2. Desenhar o círculo 🔵
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, backgroundPaint);

        // 3. Configurar o texto (as iniciais) ✍️
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(48);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // Centralizar o texto verticalmente
        float xPos = size / 2f;
        float yPos = (size / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f);

        canvas.drawText(initials, xPos, yPos, textPaint);

        return bitmap;
    }

    private int getColorForUser(int userId) {
        int[] colors = {
                Color.parseColor("#E91E63"), // Rosa
                Color.parseColor("#9C27B0"), // Roxo
                Color.parseColor("#3F51B5"), // Azul
                Color.parseColor("#009688"), // Teal
                Color.parseColor("#FF9800"), // Laranja
                Color.parseColor("#795548")  // Marrom
        };

        int index = Math.abs(userId) % colors.length;

        return colors[index];
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView bodyTextView;
        ImageView imageViewPost;

        TextView authorNameTextView;
        TextView authorEmailTextView;
        ImageView avatarImageView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_title);
            bodyTextView = itemView.findViewById(R.id.text_body);
            imageViewPost = itemView.findViewById(R.id.image_post);
            authorNameTextView = itemView.findViewById(R.id.text_author_name);
            authorEmailTextView = itemView.findViewById(R.id.text_author_email);
            avatarImageView = itemView.findViewById(R.id.image_avatar);
        }
    }

    public static class PostModelProvider implements ListPreloader.PreloadModelProvider<PostWithUser> {

        public static int PRELOAD_AHEAD_ITEMS = 3;
        private final List<PostWithUser> posts;
        private final Context context;

        public PostModelProvider(Context context, List<PostWithUser> posts) {
            this.context = context;
            this.posts = posts;
        }

        @NonNull
        @Override
        public List<PostWithUser> getPreloadItems(int position) {
            if (posts.isEmpty()) {
                return Collections.emptyList();
            }

            int end = Math.min(position + PRELOAD_AHEAD_ITEMS, posts.size());

            return posts.subList(position, end);
        }

        @Nullable
        @Override
        public RequestBuilder<?> getPreloadRequestBuilder(@NonNull PostWithUser item) {
            int screenWidth = getScreenWidthInPixels(context);

            String imageUrl = "https://picsum.photos/id/" + item.getPost().getId() + "/" + screenWidth + "/" + screenWidth;

            return Glide.with(context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade());
        }
    }
}