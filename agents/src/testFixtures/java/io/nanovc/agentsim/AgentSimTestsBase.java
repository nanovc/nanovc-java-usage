package io.nanovc.agentsim;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeAll;

/**
 * The base class for tests across the Agent Sim framework.
 */
public class AgentSimTestsBase
{
    protected static JsonMapper jsonMapper;

    @BeforeAll
    public static void createJSONMapper()
    {
        // Create the JSON mapper:
        jsonMapper = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .serializationInclusion(JsonInclude.Include.NON_DEFAULT)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .build();

        // Configure the pretty printer to use a new line character only. This helps with cross platform unit tests:
        DefaultIndenter newLineOnlyIndenter = new DefaultIndenter("  ", "\n");
        jsonMapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter().withObjectIndenter(newLineOnlyIndenter).withArrayIndenter(newLineOnlyIndenter));

        // Use mixins to provide annotations for types that we don't control directly:
        // https://github.com/FasterXML/jackson-docs/wiki/JacksonMixInAnnotations
        jsonMapper.addMixIn(AgentConfigAPI.class, AgentConfigMixin.class);
        jsonMapper.addMixIn(ModelAPI.class, ModelMixin.class);
    }

    /**
     * Gets the JSON representation of the given value.
     *
     * @param value The object to get as JSON.
     * @return The JSON representation of the given value.
     * @throws JsonProcessingException If there is an error creating the JSON representation of the given value.
     */
    protected String getJSON(Object value) throws JsonProcessingException
    {
        return jsonMapper.writeValueAsString(value);
    }

    /**
     * This is a mixin class for Jackson which allows us to configure how we want these classes serialized without polluting the models.
     * https://github.com/FasterXML/jackson-docs/wiki/JacksonMixInAnnotations
     */
    @JsonTypeInfo(use= JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="type")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public static abstract class TypedMixin
    {
    }

    public static abstract class AgentConfigMixin extends TypedMixin
    {
        @JsonInclude(JsonInclude.Include.ALWAYS)
        public abstract boolean isEnabled();
    }

    public static abstract class ModelMixin extends TypedMixin
    {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public abstract String getName();
    }

}
