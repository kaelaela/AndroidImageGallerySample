package me.kaelaela.gallerysample;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MessengerImageListAdapter extends AbstractListAdapter<String> {

    private OnItemClickListener mListener;
    private List<String> mSelectedUrls = new ArrayList<>(5);

    public MessengerImageListAdapter(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new MessengerImageViewHolder(inflater.inflate(R.layout.item_messenger_thumbnail, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MessengerImageViewHolder) {
            MessengerImageViewHolder viewHolder = (MessengerImageViewHolder) holder;
            viewHolder.bind(items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void addItem(String item) {
        items.add(item);
        notifyItemInserted(items.size());
    }

    @Override
    public void addAllItem(List<String> receivedItems) {
        items.clear();
        items.addAll(receivedItems);
        notifyDataSetChanged();
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
    }

    @Override
    public void integrateItem() {

    }

    public interface OnItemClickListener {
        void onItemClick();
    }

    public class MessengerImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final int THUMBNAIL_SIZE;
        private String url;
        private View shadow;
        private Button sendButton;
        private SimpleDraweeView thumbnail;

        public MessengerImageViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            thumbnail = (SimpleDraweeView) itemView.findViewById(R.id.thumbnail);
            thumbnail.setOnClickListener(this);
            sendButton = (Button) itemView.findViewById(R.id.send_button);
            sendButton.setOnClickListener(this);
            shadow = itemView.findViewById(R.id.shadow);
            THUMBNAIL_SIZE = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.messenger_thumbnail_side);
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
                    .setResizeOptions(new ResizeOptions(THUMBNAIL_SIZE, THUMBNAIL_SIZE))
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(thumbnail.getController())
                    .setImageRequest(request)
                    .build();
            thumbnail.setController(controller);
            toggle();
        }

        private void toggle() {
            boolean isContain = false;
            for (String s : mSelectedUrls) {
                if (s.equals(url)) {
                    isContain = true;
                }
            }
            sendButton.setVisibility(isContain ? View.VISIBLE : View.GONE);
            shadow.setVisibility(isContain ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {
            if (v == sendButton) {
                Toast.makeText(v.getContext(), "送信しました", Toast.LENGTH_SHORT).show();
                sendButton.setVisibility(View.GONE);
                shadow.setVisibility(View.GONE);
                mListener.onItemClick();
            } else {
                shadow.setVisibility(sendButton.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                sendButton.setVisibility(sendButton.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }

            if (mSelectedUrls.contains(url)) {
                mSelectedUrls.remove(url);
            } else {
                mSelectedUrls.add(url);
            }
        }
    }
}