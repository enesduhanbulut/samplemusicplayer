package com.duh.samplemusicplayer.view.adapter;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public abstract class AbstractRecyclerViewAdapter<T, S extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<S> {
    private List<T> items = new ArrayList<>();

    abstract boolean areItemsSame(T object, T object2);

    abstract boolean areContentsTheSame(T object, T object2);

    public void addItem(T item) {
        items.add(item);
    }

    public void addItemWithUpdate(T item) {
        items.add(item);
        List<T> tempList = new ArrayList<>(items);
        updateList(tempList);
    }

    public T getItem(int pos) {
        return items.get(pos);
    }

    public void removeItem(T item) {
        items.remove(Observable.fromIterable(items)
                .filter(listItem -> areItemsSame(listItem, item))
                .blockingSingle());
    }

    public void updateList(List<T> updateList) {
        DiffUtil.Callback callback = new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return getItemCount();
            }

            @Override
            public int getNewListSize() {
                return updateList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return areItemsSame(getItem(oldItemPosition), updateList.get(newItemPosition));
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return AbstractRecyclerViewAdapter.this.areContentsTheSame(getItem(oldItemPosition), updateList.get(newItemPosition));
            }
        };
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);
        this.items = updateList;
        diffResult.dispatchUpdatesTo(AbstractRecyclerViewAdapter.this);
    }


    public int getItemCount() {
        return items.size();
    }

    public List<T> getItems() {
        return items;
    }

}
