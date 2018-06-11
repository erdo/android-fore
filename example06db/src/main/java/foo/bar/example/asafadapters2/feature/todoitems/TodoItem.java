package foo.bar.example.asafadapters2.feature.todoitems;

import co.early.asaf.adapters.DiffComparator;
import co.early.asaf.core.Affirm;
import foo.bar.example.asafadapters2.db.todoitems.TodoItemEntity;

/**
 * Encapsulates a TodoItem, holds a reference to its equivalent in the database layer {@link TodoItemEntity}
 * <p>
 * TodoItem objects are tied to a database entry{@link TodoItemEntity}, and any calls on the setters
 * here will cause the instance to become dirty. A list of these items will be refreshed automatically
 * as long as the updates are saved to the database (see the model class in the features package).
 * The dirty flag is used by DiffUtil so that it can detect a change to the item when it compares
 * it with a fresh instance taken from the database. Once an item becomes dirty it remains that way
 * until it is replaced by a db copy.
 */
public class TodoItem implements DiffComparator<TodoItem> {

    private final TodoItemEntity todoItemEntity;
    private boolean dirty = false;

    public TodoItem(long creationTimestamp, String label) {
        this(new TodoItemEntity(creationTimestamp, label));
    }

    public TodoItem(TodoItemEntity todoItemEntity) {
        this.todoItemEntity = Affirm.notNull(todoItemEntity);
    }


    public String getLabel() {
        return todoItemEntity.getLabel();
    }

    public void setLabel(String label) {
        todoItemEntity.setLabel(label);
        dirty = true;
    }

    public boolean isDone() {
        return todoItemEntity.isDone();
    }

    public void setDone(boolean done) {
        todoItemEntity.setDone(done);
        dirty = true;
    }

    public long getCreationTimestamp() {
        return todoItemEntity.getCreationTimestamp();
    }

    public boolean isDirty() {
        return dirty;
    }

    TodoItemEntity getEntity(){
        return todoItemEntity;
    }

    /**
     * Used by {@see DiffUtil}
     * <p>
     * Do the two instances represent the same real world item? even though they maybe
     * different instances. For example, one could be its representation in a list view, the other
     * could be its representation in a database entity, but if they represent the same item
     * conceptually then this method should return true
     *
     * @param other
     * @return true if the items represent the same real world / conceptual item
     */
    @Override
    public boolean itemsTheSame(TodoItem other) {
        return other != null
               // && getCreationTimestamp() == other.getCreationTimestamp()
                && getEntity().getId() == other.getEntity().getId();
    }

    /**
     * Used by {@see DiffUtil}
     * <p>
     * Note this really means: do they look the same in a list on the display. As such is usually
     * related to a particular view.
     * <p>
     * This only gets called if {@link#itemsTheSame()} already returns true
     *
     * @param other
     * @return
     */
    @Override
    public boolean contentsTheSame(TodoItem other) {
        if (isDirty()){
            return false;
        } else if (isDone() != other.isDone()){
            return false;
        } else {
            return true;
        }
    }

}
