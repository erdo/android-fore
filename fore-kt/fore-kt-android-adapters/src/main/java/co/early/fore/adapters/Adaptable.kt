package co.early.fore.adapters

interface Adaptable<T> {
    fun getItem(index: Int): T
    fun getItemCount(): Int
}
