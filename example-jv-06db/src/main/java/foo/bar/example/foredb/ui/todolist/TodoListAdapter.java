package foo.bar.example.foredb.ui.todolist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.early.fore.adapters.Notifyable;
import co.early.fore.adapters.NotifyableImp;
import co.early.fore.core.Affirm;
import foo.bar.example.foredb.R;
import foo.bar.example.foredb.feature.todoitems.TodoItem;
import foo.bar.example.foredb.feature.todoitems.TodoListModel;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;


public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.ViewHolder> implements Notifyable<TodoListAdapter.ViewHolder> {

    private final TodoListModel todoListModel;
    private final NotifyableImp<ViewHolder> notifyableImp;

    public TodoListAdapter(final TodoListModel todoListModel) {
        this.todoListModel = Affirm.notNull(todoListModel);
        this.notifyableImp = new NotifyableImp<>(this, todoListModel);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_todolist_listitem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.itemView.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final TodoItem item = todoListModel.get(position);

        holder.done.setChecked(item.isDone());
        holder.done.setOnCheckedChangeListener((buttonView, isChecked) -> {
            //if you tap very fast on different rows removing them
            //while you are using adapter animations you will crash unless
            //you check for this
            int betterPosition = holder.getAdapterPosition();
            if (betterPosition != NO_POSITION) {
                todoListModel.setDone(isChecked, betterPosition);
            }
        });

        holder.description.setText("" + item.getLabel());
    }

    @Override
    public int getItemCount() {
        return todoListModel.size();
    }

    @Override
    public void notifyDataSetChangedAuto() {
        notifyableImp.notifyDataSetChangedAuto();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.todolist_description_text)
        protected TextView description;

        @BindView(R.id.todolist_done_checkbox)
        protected CheckBox done;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
