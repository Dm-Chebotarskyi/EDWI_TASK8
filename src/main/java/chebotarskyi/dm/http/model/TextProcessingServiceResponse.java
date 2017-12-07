package chebotarskyi.dm.http.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

@Immutable
@JsonSerialize(as = ImmutableTextProcessingServiceResponse.class)
@JsonDeserialize(as = ImmutableTextProcessingServiceResponse.class)
public interface TextProcessingServiceResponse {

    @Parameter
    public String label();

    @Parameter
    public TextProcessingProbability probability();

}
