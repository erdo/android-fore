package foo.bar.example.asafadapters2.db.todoitems;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import java.util.List;
import java.util.Random;

import co.early.asaf.core.time.SystemTimeWrapper;

import static foo.bar.example.asafadapters2.db.todoitems.TodoItemEntity.COLUMN_CREATE_TIMESTAMP;
import static foo.bar.example.asafadapters2.db.todoitems.TodoItemEntity.COLUMN_DONE;
import static foo.bar.example.asafadapters2.db.todoitems.TodoItemEntity.TABLE_NAME;


/**
 * Data Access class, the app shouldn't be accessing this class directly, its all wrapped up in
 * the associated model class in the feature package which handles threading and notifications
 * for you.
 */
@Dao
public abstract class TodoItemDao {

    @Insert
    public abstract long insertTodoItem(TodoItemEntity todoItemEntity);

    @Insert
    public abstract void insertManyTodoItems(List<TodoItemEntity> todoItemEntities);

    @Update
    public abstract int updateTodoItem(TodoItemEntity logEntry);

    @Delete
    public abstract int deleteTodoItem(TodoItemEntity logEntry);

    @Query("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_CREATE_TIMESTAMP + " DESC, id")
    public abstract List<TodoItemEntity> getAllTodoItems();

    @Query("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_DONE + " = :done " + " ORDER BY " + COLUMN_CREATE_TIMESTAMP + " DESC, id")
    public abstract List<TodoItemEntity> getTodoItems(boolean done);

    @Query("SELECT COUNT(*) FROM " + TABLE_NAME)
    public abstract Integer getRowCount();

    @Query("SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + COLUMN_DONE + " = 1")
    public abstract Integer getDoneRowCount();

    @Query("DELETE FROM " + TABLE_NAME)
    public abstract int clear();

    @Transaction
    public int addRandom(int numberOfEntriesToAdd, SystemTimeWrapper systemTimeWrapper) {
        // Anything inside this method runs in a single transaction.

        for (int i = 0; i < numberOfEntriesToAdd; i++) {
            insertTodoItem(RandomTodoCreator.create(systemTimeWrapper.currentTimeMillis()));
        }

        return numberOfEntriesToAdd;
    }

    @Transaction
    public int deleteRandomXPercent(int percent, SystemTimeWrapper systemTimeWrapper) {
        // Anything inside this method runs in a single transaction.

        Random random = new Random();
        int deleted = 0;

        for (TodoItemEntity todoItemEntity : getAllTodoItems()) {
            if (random.nextInt(101) < percent+1) {
                deleteTodoItem(todoItemEntity);
                deleted++;
            }
        }

        return deleted;
    }

    @Transaction
    public int doRandomXPercent(int percent, SystemTimeWrapper systemTimeWrapper) {
        // Anything inside this method runs in a single transaction.

        Random random = new Random();
        int updated = 0;

        for (TodoItemEntity todoItemEntity : getTodoItems(false)) {
            if (random.nextInt(101) < percent+1) {
                todoItemEntity.setDone(true);
                updateTodoItem(todoItemEntity);
                updated++;
            }
        }

        return updated;
    }

}
