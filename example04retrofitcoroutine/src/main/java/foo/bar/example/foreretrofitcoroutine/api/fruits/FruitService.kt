package foo.bar.example.foreretrofitcoroutine.api.fruits

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * These stubs are hosted at https://www.mocky.io/
 *
 * http://www.mocky.io/v2/59efa0132e0000ef331c5f9b
 * http://www.mocky.io/v2/59ef2a6c2e00002a1a1c5dea
 *
 */
interface FruitService {

    @GET("59efa0132e0000ef331c5f9b/")
    fun getFruitsSimulateOk(@Query("mocky-delay") delayScalaDurationFormat: String): Call<List<FruitPojo>>

    @GET("59ef2a6c2e00002a1a1c5dea/")
    fun getFruitsSimulateNotAuthorised(@Query("mocky-delay") delayScalaDurationFormat: String): Call<List<FruitPojo>>

}
