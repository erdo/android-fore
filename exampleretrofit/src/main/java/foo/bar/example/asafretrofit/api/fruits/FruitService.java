package foo.bar.example.asafretrofit.api.fruits;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface FruitService {

    @GET("fruits/")
    Call<List<FruitPojo>> getFruitsSimulateOk();

    @GET("fruitsnotauthorized/")
    Call<List<FruitPojo>> getFruitsSimulateNotAuthorised();

}