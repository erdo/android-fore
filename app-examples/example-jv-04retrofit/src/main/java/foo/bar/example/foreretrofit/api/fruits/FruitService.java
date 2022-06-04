package foo.bar.example.foreretrofit.api.fruits;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * These stubs are hosted at https://www.mocky.io/
 *
 * http://www.mocky.io/v2/59efa0132e0000ef331c5f9b
 * http://www.mocky.io/v2/59ef2a6c2e00002a1a1c5dea
 *
 */
public interface FruitService {

    @GET("59efa0132e0000ef331c5f9b/")
    Call<List<FruitPojo>> getFruitsSimulateOk(@Query("mocky-delay") String delayScalaDurationFormat);

    @GET("59ef2a6c2e00002a1a1c5dea/")
    Call<List<FruitPojo>> getFruitsSimulateNotAuthorised(@Query("mocky-delay") String delayScalaDurationFormat);

}
