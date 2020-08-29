package foo.bar.example.foredb.api.todoitems;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * These stubs are hosted at https://www.mocky.io/
 *
 * http://www.mocky.io/v2/5b1a81703300005400fb14da
 *
 */
public interface TodoItemService {

    @GET("5b1a81703300005400fb14da/")
    Call<List<TodoItemPojo>> getTodoItems(@Query("mocky-delay") String delayScalaDurationFormat);

}
