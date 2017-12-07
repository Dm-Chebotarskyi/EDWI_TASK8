package chebotarskyi.dm.http;

import chebotarskyi.dm.http.model.TextProcessingServiceResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TextProcessingService {

    @POST("sentiment/")
    @FormUrlEncoded
    Call<TextProcessingServiceResponse> sentimentAnalysis(@Field("text") String text);

}
