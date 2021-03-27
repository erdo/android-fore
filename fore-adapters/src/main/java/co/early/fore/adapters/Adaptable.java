package co.early.fore.adapters;

public interface Adaptable<T> {
    T getItem(int index);
    int getItemCount();
}
