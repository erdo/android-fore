package foo.bar.example.foredb.db.todoitems;

import android.app.Application;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import co.early.fore.core.Affirm;
import co.early.fore.core.WorkMode;


/**
 * Room Database class, the app shouldn't be accessing this class directly, its all wrapped up by
 * the associated model, see the feature package
 */
@Database(entities = {TodoItemEntity.class}, exportSchema = false, version = 1)
public abstract class TodoItemDatabase extends RoomDatabase {


    private static TodoItemDatabase instance;


    public static TodoItemDatabase getInstance(Application application, boolean inMemoryDb, WorkMode workMode) {

        Affirm.notNull(application);
        Affirm.notNull(workMode);

        if (instance == null) {
            instance = buildInstance(application, inMemoryDb, workMode);
        }

        return instance;
    }

    private static TodoItemDatabase buildInstance(Application application, boolean inMemoryDb, WorkMode workMode) {

        RoomDatabase.Builder<TodoItemDatabase> builder;

        if (inMemoryDb) {
            builder = Room.inMemoryDatabaseBuilder(application, TodoItemDatabase.class);
        } else {
            builder = Room.databaseBuilder(application, TodoItemDatabase.class,
                    TodoItemDatabase.class.getSimpleName() + "DB");
        }

        // addMigrations(builder);

        if (workMode == WorkMode.SYNCHRONOUS) {
            builder.allowMainThreadQueries();
        }

        return builder.build();
    }

    public static void destroyInstance() {
        instance = null;
    }

    public abstract TodoItemDao todoItemDao();

}
