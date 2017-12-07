package chebotarskyi.dm.http.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;


@Immutable
@JsonSerialize(as = ImmutableTextProcessingProbability.class)
@JsonDeserialize(as = ImmutableTextProcessingProbability.class)
public interface TextProcessingProbability {


    @Parameter
    public double neg();

    @Parameter
    public double neutral();

    @Parameter
    public double pos();

}
