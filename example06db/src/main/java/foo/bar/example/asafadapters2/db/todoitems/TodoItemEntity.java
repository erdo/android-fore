package foo.bar.example.asafadapters2.db.todoitems;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

/**
 * Room Entity, the database functionality is all managed by the
 * associated model, see the feature package
 */
@Entity
public class TodoItemEntity {

    public static final String TABLE_NAME = "TodoItemEntity";//must be the name of the Entity class
    public static final String COLUMN_CREATE_TIMESTAMP = "create_timestamp";
    public static final String COLUMN_LABEL = "label";
    public static final String COLUMN_DONE = "done";


    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = COLUMN_CREATE_TIMESTAMP, index = true)
    private long creationTimestamp;

    @ColumnInfo(name = COLUMN_LABEL)
    private String label;

    @ColumnInfo(name = COLUMN_DONE, index = true)
    private boolean done;


    //for Room to use
    public TodoItemEntity() {
    }

    @Ignore
    public TodoItemEntity(long creationTimestamp, String label) {
        this.creationTimestamp = creationTimestamp;
        this.label = label;
        this.done = false;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
