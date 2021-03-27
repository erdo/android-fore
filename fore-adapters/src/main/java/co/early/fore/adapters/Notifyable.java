package co.early.fore.adapters;

import androidx.recyclerview.widget.RecyclerView;

public interface Notifyable<T extends RecyclerView.ViewHolder> {
    void notifyDataSetChangedAuto();
}
