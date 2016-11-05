package apidez.com.myapplication.api;

import apidez.com.myapplication.model.ImageResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by nongdenchet on 11/5/16.
 */

public interface ImgurApi {

    @Multipart
    @POST("image")
    Call<ImageResponse> create(
            @Part MultipartBody.Part image,
            @Part("name") RequestBody name
    );
}
