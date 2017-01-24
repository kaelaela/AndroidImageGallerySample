package me.kaelaela.gallerysample;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.CheckBox;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageListAdapter extends AbstractListAdapter<String> {

    private final float AFTER_ANIMATED_SIZE = 0.95f;
    private final float DEFAULT_SIZE = 1f;
    private final int DEFAULT_ANIM_DURATION = 300;
    private final int MAX_COUNT = 5;
    private final Interpolator INTERPOLATOR = new OvershootInterpolator(4);

    private OnItemClickListener listener;
    private final List<String> selectedUrls = new ArrayList<>();
    private final int thumbnailWidth;

    public ImageListAdapter(int displayWidth, int columnCount,  OnItemClickListener listener) {
        thumbnailWidth = displayWidth / columnCount;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ImageItemViewHolder(inflater.inflate(R.layout.item_thumbnail, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImageItemViewHolder) {
            ImageItemViewHolder viewHolder = (ImageItemViewHolder) holder;
            viewHolder.bind(items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void addItem(String item) {
        if (TextUtils.isEmpty(item)) {
            return;
        }
        items.add(item);
        notifyItemChanged(items.size());
    }

    @Override
    public void addAllItem(List<String> urls) {
        items.clear();
        items.addAll(urls);
    }

    @Override
    public void removeItem(String item) {
        Integer count = null;
        for (int i = 0; i < items.size(); i++) {
            String url = items.get(i);
            if (url.equals(item)) {
                count = i;
            }
        }
        if (count != null) {
            items.remove((int) count);
        }
    }

    @Override
    public void removeAllItem() {
        items.clear();
        notifyDataSetChanged();
    }

    @Override
    public void integrateItem() {
    }

    public interface OnItemClickListener {
        void onItemClick(int count);

        void onOverCount();
    }

    public class ImageItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final SimpleDraweeView thumbnail;
        final CheckBox check;
        private String url;

        public ImageItemViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            thumbnail = (SimpleDraweeView) itemView.findViewById(R.id.thumbnail);
            thumbnail.setOnClickListener(this);
            check = (CheckBox) itemView.findViewById(R.id.check);
            check.setOnClickListener(this);
            fixHeight();
        }

        private void fixHeight() {
            GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) itemView.getLayoutParams();
            params.height = thumbnailWidth;
            itemView.setLayoutParams(params);
        }

        public void bind(String url) {
            if (TextUtils.isEmpty(url)) {
                return;
            }
            this.url = url;
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithSource(Uri.fromFile(new File(url)))
                    .setProgressiveRenderingEnabled(true)
                    .setLocalThumbnailPreviewsEnabled(true)
                    .setResizeOptions(new ResizeOptions(thumbnailWidth, thumbnailWidth))
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(thumbnail.getController())
                    .setImageRequest(request)
                    .build();
            thumbnail.setController(controller);
            toggle(url);
        }

        private void toggle(String url) {
            boolean isContain = false;
            for (String s : selectedUrls) {
                if (s.equals(url)) {
                    isContain = true;
                }
            }
            check.setChecked(isContain);
            thumbnail.setScaleX(isContain ? AFTER_ANIMATED_SIZE : DEFAULT_SIZE);
            thumbnail.setScaleY(isContain ? AFTER_ANIMATED_SIZE : DEFAULT_SIZE);
            itemView.setBackground(isContain ? ContextCompat
                    .getDrawable(itemView.getContext(), R.drawable.shape_image_selected) : null);
        }

        public void bounceAnimation(final View view, boolean isChecked, AnimatorListenerAdapter adapter) {
            view.setEnabled(false);
            AnimatorSet animatorSet = new AnimatorSet();

            ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(view, "scaleX",
                    isChecked ? DEFAULT_SIZE : AFTER_ANIMATED_SIZE,
                    isChecked ? AFTER_ANIMATED_SIZE : DEFAULT_SIZE);
            bounceAnimX.setDuration(DEFAULT_ANIM_DURATION);
            bounceAnimX.setInterpolator(INTERPOLATOR);

            ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(view, "scaleY",
                    isChecked ? DEFAULT_SIZE : AFTER_ANIMATED_SIZE,
                    isChecked ? AFTER_ANIMATED_SIZE : DEFAULT_SIZE);
            bounceAnimY.setDuration(DEFAULT_ANIM_DURATION);
            bounceAnimY.setInterpolator(INTERPOLATOR);
            bounceAnimY.addListener(adapter);
            animatorSet.play(bounceAnimX).with(bounceAnimY);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setEnabled(true);
                }
            });
            animatorSet.start();
        }

        @Override
        public void onClick(final View v) {
            if (!selectedUrls.contains(url) && selectedUrls.size() == MAX_COUNT) {
                listener.onOverCount();
                return;
            }

            check.setChecked(!check.isChecked());
            bounceAnimation(thumbnail, check.isChecked(), new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    itemView.setBackground(ContextCompat
                            .getDrawable(itemView.getContext(), R.drawable.shape_image_selected));
                }
            });

            if (selectedUrls.contains(url)) {
                selectedUrls.remove(url);
            } else {
                selectedUrls.add(url);
            }
            listener.onItemClick(selectedUrls.size());
        }
    }
}
