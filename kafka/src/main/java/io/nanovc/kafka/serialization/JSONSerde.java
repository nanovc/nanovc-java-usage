package io.nanovc.kafka.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * JSON Serialization and Deserialization (Serde).
 *
 * @param <T> The specific type of item to serialize.
 */
public class JSONSerde<T> implements Serde<T>, Serializer<T>, Deserializer<T>
{
    /**
     * The specific class to serialize.
     */
    protected Class<T> classToSerialize;

    /**
     * The {@link ObjectMapper} to use for serialization to JSON.
     */
    protected ObjectMapper objectMapper;

    /**
     * This is a lambda to the logic to configure the type validator to use.
     * If this is provided then we enable default typing.
     * This is needed as a firewall of polymorphic types that we allow.
     * This is described in:
     * https://cowtowncoder.medium.com/on-jackson-cves-dont-panic-here-is-what-you-need-to-know-54cd0d6e8062
     */
    protected Consumer<BasicPolymorphicTypeValidator.Builder> typeValidatorConfigurer;

    /**
     * The map of target classes (keys) that should have serialization annotations mixed in from the source classes (values).
     * This allows us to keep the original classes free from Jackson serialization annotations
     * while allowing us to control serialization with the convenience of annotations on other surrogate classes.
     */
    protected Map<Class<?>, Class<?>> targetToSourceMixinMap = new LinkedHashMap<>();

    /**
     * Creates a new serializer and deserializer (Serde) that converts arbitrary objects to JSON.
     *
     * @param classToSerialize The specific class to serialize.
     */
    public JSONSerde(Class<T> classToSerialize)
    {
        this(classToSerialize, null, null);
    }

    /**
     * Creates a new serializer and deserializer (Serde) that converts arbitrary objects to JSON.
     * This constructor allows you to define mixins that are useful for controlling serialization details without modifying the actual classes being serialized.
     *
     * @param classToSerialize        The specific class to serialize.
     * @param targetToSourceMixinMap  The map of target classes (keys) that should have serialization annotations mixed in from the source classes (values).
     *                                This allows us to keep the original classes free from Jackson serialization annotations
     *                                while allowing us to control serialization with the convenience of annotations on other surrogate classes.
     * @param typeValidatorConfigurer This is a lambda to the logic to configure the type validator to use.
     *                                If this is provided then we enable default typing.
     *                                This is needed as a firewall of polymorphic types that we allow.
     *                                This is described in:
     *                                https://cowtowncoder.medium.com/on-jackson-cves-dont-panic-here-is-what-you-need-to-know-54cd0d6e8062
     */
    public JSONSerde(
        Class<T> classToSerialize,
        Map<Class<?>, Class<?>> targetToSourceMixinMap,
        Consumer<BasicPolymorphicTypeValidator.Builder> typeValidatorConfigurer
    )
    {
        this.classToSerialize = classToSerialize;
        if (targetToSourceMixinMap != null)
        {
            this.targetToSourceMixinMap.putAll(targetToSourceMixinMap);
        }
        this.typeValidatorConfigurer = typeValidatorConfigurer;
    }

    /**
     * Configure this class, which will configure the underlying serializer and deserializer.
     *
     * @param configs configs in key/value pairs
     * @param isKey   whether is for key or value
     */
    @Override public void configure(Map<String, ?> configs, boolean isKey)
    {
        // Create the object mapper:
        this.objectMapper = new ObjectMapper();

        // Configure the object mapper:
        configureObjectMapper(this.objectMapper, configs);
    }

    /**
     * Configures the object mapper for serialization.
     *
     * @param objectMapper The object mapper to configure.
     * @param configs      The configuration settings that were passed into the serializer.
     */
    protected void configureObjectMapper(ObjectMapper objectMapper, Map<String, ?> configs)
    {
        // Configure settings:
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true);
        objectMapper.configure(MapperFeature.AUTO_DETECT_FIELDS, true);


        // Check whether we want to enable default typing:
        if (this.typeValidatorConfigurer != null)
        {
            // Allow default typing for all types:
            // BE CAREFUL: https://cowtowncoder.medium.com/on-jackson-cves-dont-panic-here-is-what-you-need-to-know-54cd0d6e8062
            BasicPolymorphicTypeValidator.Builder builder = BasicPolymorphicTypeValidator.builder();
            this.typeValidatorConfigurer.accept(builder);
            objectMapper.activateDefaultTyping(
                builder.build()
            );
        }

        // Make sure that the pretty printer only uses new lines and not carriage returns:
        // https://stackoverflow.com/a/53325273/231860
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        DefaultPrettyPrinter.Indenter indenter = new DefaultIndenter("  ", "\n");
        prettyPrinter.indentArraysWith(indenter);
        prettyPrinter.indentObjectsWith(indenter);
        this.objectMapper.setDefaultPrettyPrinter(prettyPrinter);

        // Register all the mixins that were specified:
        for (Map.Entry<Class<?>, Class<?>> entry : this.targetToSourceMixinMap.entrySet())
        {
            objectMapper.addMixIn(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Convert {@code data} into a byte array.
     *
     * @param topic topic associated with data
     * @param data  typed data
     * @return serialized bytes
     */
    @Override public byte[] serialize(String topic, T data)
    {
        try
        {
            return objectMapper.writeValueAsBytes(data);
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deserialize a record value from a byte array into a value or object.
     *
     * @param topic topic associated with the data
     * @param data  serialized bytes; may be null; implementations are recommended to handle null by returning a value or null rather than throwing an exception.
     * @return deserialized typed data; may be null
     */
    @Override public T deserialize(String topic, byte[] data)
    {
        try
        {
            return this.objectMapper.readValue(data, this.classToSerialize);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Close this serde class, which will close the underlying serializer and deserializer.
     * <p>
     * This method has to be idempotent because it might be called multiple times.
     */
    @Override public void close()
    {
    }

    /**
     * Gets this serializer.
     *
     * @return This serializer.
     */
    @Override public Serializer<T> serializer()
    {
        return this;
    }

    /**
     * Gets this deserializer.
     *
     * @return This deserializer.
     */
    @Override public Deserializer<T> deserializer()
    {
        return this;
    }
}
