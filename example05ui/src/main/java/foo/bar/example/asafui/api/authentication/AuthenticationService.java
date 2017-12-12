package foo.bar.example.asafui.api.authentication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * These stubs are hosted at https://www.mocky.io/
 *
 * http://www.mocky.io/v2/59efa0132e0000ef331c5f9b
 * http://www.mocky.io/v2/59ef2a6c2e00002a1a1c5dea
 *
 */
public interface AuthenticationService {

    @POST("59efa0132e0000ef331c5f9b/")
    Call<SessionResponsePojo> getSessionToken(@Body SessionRequestPojo sessionRequestPojo,
                                              @Query("mocky-delay") String delayScalaDurationFormat);

}