package me.kaelaela.gallerysample;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractListAdapter<I> extends RecyclerView.Adapter<ViewHolder> {
    List<I> items = new ArrayList<>();

    abstract public void addItem(I item);

    abstract public void addAllItem(List<I> receivedItems);

    abstract public void removeItem(I item);

    abstract public void removeAllItem();

    abstract public void integrateItem();
}
