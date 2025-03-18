package foo.bar.example.foredb.feature.todoitems;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import co.early.fore.core.WorkMode;
import co.early.fore.core.callbacks.SuccessCallbackWithPayload;
import co.early.fore.core.logging.Logger;
import co.early.fore.core.logging.SystemLogger;
import co.early.fore.core.time.SystemTimeWrapper;
import foo.bar.example.foredb.db.todoitems.TodoItemDatabase;

import static co.early.fore.core.testhelpers.CountDownLatchWrapper.runInBatch;

/**
 * Integration test which demonstrates how to test db driven models
 * using a real in memory database rather than a mock
 *
 * We need to use count down latches here because Room's invalidation tracker
 * always fires in a different thread
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class TodoItemsModelTest {

    @Mock
    private SystemTimeWrapper mockSystemTimeWrapper;
    @Mock
    private SuccessCallbackWithPayload<Long> mockSuccessCallback;

    private TodoItemDatabase todoItemDatabase;
    private WorkMode workMode = WorkMode.SYNCHRONOUS;
    private Logger logger = new SystemLogger();

    private static final TodoItem TODO_ITEM_0 = new TodoItem(0, "buy rice");
    private static final TodoItem TODO_ITEM_1 = new TodoItem(1, "get hair cut");
    private static final TodoItem TODO_ITEM_2 = new TodoItem(2, "invest in bitcoin");

    private static final String NEW_LABEL = "learn to cook";

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        //real in memory db which allows main thread queries
        todoItemDatabase = TodoItemDatabase.getInstance(RuntimeEnvironment.application, true, workMode);
    }

    @After
    public void tearDown() {
        if (todoItemDatabase.isOpen()) {
            todoItemDatabase.getOpenHelper().close();
        }
        todoItemDatabase.destroyInstance();
    }

    @Test
    public void whenInitialised_withNoData_stateIsCorrect() throws Exception {

        //arrange
        TodoListModel todoListModel = new TodoListModel(todoItemDatabase, logger, mockSystemTimeWrapper, workMode);

        //act

        //assert
        Assert.assertEquals(0, todoListModel.size());
    }

    @Test
    public void whenQueryingTodoItems_withTodoItemsAdded_todoItemsAreCorrect() throws Exception {

        //arrange
        TodoListModel todoListModel = new TodoListModel(todoItemDatabase, logger, mockSystemTimeWrapper, workMode);

        //the Room invalidation tracker fires in a different thread
        CountDownLatch latchForRoomInvalidationTracker = new CountDownLatch(3);
        todoListModel.addObserver(() -> latchForRoomInvalidationTracker.countDown());


        //act
        todoListModel.add(TODO_ITEM_0);
        todoListModel.add(TODO_ITEM_1);
        todoListModel.add(TODO_ITEM_2);


        //Try to ensure all the invalidation trackers have been fired before we continue.
        //In reality, Room batches up the invalidation trackers so we can't be deterministic
        //about how many we will receive, hence the 2s timeout
        latchForRoomInvalidationTracker.await(2, TimeUnit.SECONDS);


        //assert
        Assert.assertEquals(3, todoListModel.size());
        Assert.assertEquals(2, todoListModel.get(0).getCreationTimestamp());
        Assert.assertEquals(1, todoListModel.get(1).getCreationTimestamp());
        Assert.assertEquals(0, todoListModel.get(2).getCreationTimestamp());
    }

    @Test
    public void whenQueryingTodoItems_withTodoItemsAddedAndRemoved_todoItemsAreCorrect() throws Exception {

        //arrange
        TodoListModel todoListModel = new TodoListModel(todoItemDatabase, logger, mockSystemTimeWrapper, workMode);


        //act
        runInBatch(2, todoListModel, () -> {
            todoListModel.add(TODO_ITEM_0);
            todoListModel.add(TODO_ITEM_1);
        });

        //act
        runInBatch(1, todoListModel, () -> {
            todoListModel.remove(todoListModel.get(0));
        });


        //assert
        Assert.assertEquals(1, todoListModel.size());
        Assert.assertEquals(0, todoListModel.get(0).getCreationTimestamp());
    }

    @Test
    public void whenQueryingTodoItems_withTodoItemsAddedAndChanged_todoItemsAreCorrect() throws Exception {

        //arrange
        TodoListModel todoListModel = new TodoListModel(todoItemDatabase, logger, mockSystemTimeWrapper, workMode);


        //act
        runInBatch(1, todoListModel, () -> {
            todoListModel.add(TODO_ITEM_0);
        });

        //act
        runInBatch(1, todoListModel, () -> {
            TodoItem todoItem = todoListModel.get(0);
            todoItem.setLabel(NEW_LABEL);
            todoListModel.update(todoItem);
        });


        //assert
        Assert.assertEquals(1, todoListModel.size());
        Assert.assertEquals(0, todoListModel.get(0).getCreationTimestamp());
        Assert.assertEquals(NEW_LABEL, todoListModel.get(0).getLabel());
    }

    @Test
    public void whenQueryingTodoItems_withTodoItemsAddedAndCleared_todoItemsAreCorrect() throws Exception {

        //arrange
        TodoListModel todoListModel = new TodoListModel(todoItemDatabase, logger, mockSystemTimeWrapper, workMode);


        //act
        runInBatch(2, todoListModel, () -> {
            todoListModel.add(TODO_ITEM_0);
            todoListModel.add(TODO_ITEM_1);
        });


        //act
        runInBatch(1, todoListModel, () -> {
            todoListModel.clear();
        });


        //assert
        Assert.assertEquals(0, todoListModel.size());
    }

    @Test
    public void whenTodoItemIsMarkedAsDone__todoItemsIsRemovedFromList() throws Exception {

        //arrange
        TodoListModel todoListModel = new TodoListModel(todoItemDatabase, logger, mockSystemTimeWrapper, workMode);


        //act
        runInBatch(3, todoListModel, () -> {
            todoListModel.add(TODO_ITEM_0);
            todoListModel.add(TODO_ITEM_1);
            todoListModel.add(TODO_ITEM_2);
        });

        //act
        runInBatch(1, todoListModel, () -> {
            TodoItem todoItem = todoListModel.get(1);
            todoItem.setDone(true);
            todoListModel.update(todoItem);
        });


        //assert
        Assert.assertEquals(2, todoListModel.size());
        Assert.assertEquals(2, todoListModel.get(0).getCreationTimestamp());
        Assert.assertEquals(0, todoListModel.get(1).getCreationTimestamp());
    }
}
