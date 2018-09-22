package foo.bar.example.foredb.ui.todolist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.early.fore.adapters.ChangeAwareAdapter;
import co.early.fore.core.Affirm;
import co.early.fore.core.logging.Logger;
import foo.bar.example.foredb.R;
import foo.bar.example.foredb.feature.todoitems.TodoItem;
import foo.bar.example.foredb.feature.todoitems.TodoListModel;


public class TodoListAdapter extends ChangeAwareAdapter<TodoListAdapter.ViewHolder> {

    private static final String TAG = TodoListAdapter.class.getSimpleName();

    private final TodoListModel todoListModel;
    private final Logger logger;

    public TodoListAdapter(final TodoListModel todoListModel, Logger logger) {
        super(todoListModel);
        this.todoListModel = Affirm.notNull(todoListModel);
        this.logger = Affirm.notNull(logger);
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
            //yuk, can't find a way around this, without checking
            //here you will occasionally get outofindex errors
            //if you tap very fast on different rows removing them
            //while you are using adapter animations
            int betterPosition = holder.getAdapterPosition();
            if (betterPosition!=-1) {
                todoListModel.setDone(isChecked, betterPosition);
            }
        });

        holder.description.setText("" + item.getLabel());
    }

    @Override
    public int getItemCount() {
        return todoListModel.size();
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
