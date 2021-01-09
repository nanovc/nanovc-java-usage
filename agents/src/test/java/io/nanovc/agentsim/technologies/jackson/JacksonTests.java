package io.nanovc.agentsim.technologies.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.nanovc.agentsim.technologies.junit.TestDirectory;
import io.nanovc.agentsim.technologies.junit.TestDirectoryExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the excellent Jackson serialization library.
 * https://github.com/FasterXML/jackson-databind/
 */
@ExtendWith(TestDirectoryExtension.class)
public class JacksonTests
{
    /**
     * 1 minute tutorial: POJOs to JSON and back
     * https://github.com/FasterXML/jackson-databind/#1-minute-tutorial-pojos-to-json-and-back
     */
    @Test
    public void test_1_Minute_Tutorial(@TestDirectory Path testPath) throws IOException
    {
        // The most common usage is to take piece of JSON,
        // and construct a Plain Old Java Object ("POJO") out of it.
        // So let's start there.
        //
        // With simple 2-property POJO like this:
        // // Note: can use getters/setters as well; here we just use public fields directly:
        /* public class MyValue {
           public String name;
           public int age;
           // NOTE: if using getters/setters, can keep fields `protected` or `private`
         }
         */

        // we will need a com.fasterxml.jackson.databind.ObjectMapper instance,
        // used for all data-binding, so let's construct one:
        ObjectMapper mapper = new ObjectMapper(); // create once, reuse

        // The default instance is fine for our use
        // -- we will learn later on how to configure mapper instance if necessary.
        // Usage is simple:
        //MyValue value = mapper.readValue(new File("data.json"), MyValue.class);
        // or:
        //value = mapper.readValue(new URL("http://some.com/api/entry.json"), MyValue.class);
        // or:
        MyValue value = mapper.readValue("{\"name\":\"Bob\", \"age\":13}", MyValue.class);

        // And if we want to write JSON, we do the reverse:
        MyValue myResultObject = value;
        mapper.writeValue(testPath.resolve("result.json").toFile(), myResultObject);
        // or:
        byte[] jsonBytes = mapper.writeValueAsBytes(myResultObject);
        assertEquals("{\"name\":\"Bob\",\"age\":13}", new String(jsonBytes, StandardCharsets.UTF_8));
        // or:
        String jsonString = mapper.writeValueAsString(myResultObject);
        assertEquals("{\"name\":\"Bob\",\"age\":13}", jsonString);
    }

    // Note: can use getters/setters as well; here we just use public fields directly:
    public static class MyValue
    {
        public String name;

        public int age;
        // NOTE: if using getters/setters, can keep fields `protected` or `private`
    }

    /**
     * 3 minute tutorial: Generic collections, Tree Model
     * https://github.com/FasterXML/jackson-databind/#3-minute-tutorial-generic-collections-tree-model
     */
    @Test
    public void test_3_Minute_Tutorial(@TestDirectory Path testPath) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper(); // create once, reuse

        // Beyond dealing with simple Bean-style POJOs, you can also handle JDK Lists, Maps:
        Map<String, Integer> scoreByName = mapper.readValue("{\"a\":1,\"b\":2}", Map.class);
        List<String> names = mapper.readValue("[\"cat\", \"dog\"]", List.class);

        // and can obviously write out as well
        mapper.writeValue(testPath.resolve("names.json").toFile(), names);

        // as long as JSON structure matches, and types are simple.
        // If you have POJO values, you need to indicate actual type
        // (note: this is NOT needed for POJO properties with List etc types):

        Map<String, ResultValue> results = mapper.readValue("{\"person1\": {\"name\":\"Bob\",\"age\":13} }",
                                                            new TypeReference<Map<String, ResultValue>>() {}
        );
        // why extra work? Java Type Erasure will prevent type detection otherwise

        // (note: no extra effort needed for serialization, regardless of generic types)

        // But wait! There is more!

        // While dealing with Maps, Lists and other "simple" Object types
        // (Strings, Numbers, Booleans) can be simple, Object traversal can be cumbersome.
        // This is where Jackson's Tree model (https://github.com/FasterXML/jackson-databind/wiki/JacksonTreeModel)
        // can come in handy:
        // can be read as generic JsonNode, if it can be Object or Array; or,
        // if known to be Object, as ObjectNode, if array, ArrayNode etc:
        ObjectNode root = (ObjectNode) mapper.readTree("{\"name\":\"Bob\",\"age\":13}");
        String name = root.get("name").asText();
        assertEquals("Bob", name);
        int age = root.get("age").asInt();
        assertEquals(13, age);

        // can modify as well: this adds child Object as property 'other', set property 'type'
        root.with("other").put("type", "student");
        String json = mapper.writeValueAsString(root);

        // with above, we end up with something like as 'json' String:
        // {
        //   "name" : "Bob", "age" : 13,
        //   "other" : {
        //      "type" : "student"
        //   }
        // }
        assertEquals("{\"name\":\"Bob\",\"age\":13,\"other\":{\"type\":\"student\"}}", json);

        // Tree Model can be more convenient than data-binding,
        // especially in cases where structure is highly dynamic,
        // or does not map nicely to Java classes.
    }

    public static class ResultValue
    {
        public String name;

        public int age;
        // NOTE: if using getters/setters, can keep fields `protected` or `private`
    }

    @Test
    public void testJavaTimeModule() throws JsonProcessingException
    {
        // Create the models:
        Instant instant = Instant.ofEpochSecond(1234567890);

        // Create the object mapper:
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Register Java 8 Types:
        // https://github.com/FasterXML/jackson-modules-java8
        // This is so that we handle Instant correctly.
        objectMapper.registerModule(new JavaTimeModule());

        // Get the JSON for the model:
        String json = objectMapper.writeValueAsString(instant);
        String expectedJSON =
            "1234567890.000000000";
        assertEquals(expectedJSON, json.replace(System.lineSeparator(), "\n"));
    }

    @Test
    public void SurrogateSerializationTests() throws JsonProcessingException
    {
        // Create the models:
        SimpleModel simpleModel = new SimpleModel();
        simpleModel.name = "Luke";
        simpleModel.age = 38;

        ComplexModel complexModel = new ComplexModel("Luke", 38);

        // Add both models to the parent object:
        ModelRoot modelRoot = new ModelRoot();
        modelRoot.models.add(simpleModel);
        modelRoot.models.add(complexModel);

        // Create the object mapper:
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        SimpleModule module = new SimpleModule();
        objectMapper.registerModule(module);

        // Use mixins to provide annotations for types that we don't control directly:
        // https://github.com/FasterXML/jackson-docs/wiki/JacksonMixInAnnotations
        objectMapper.addMixIn(ModelBase.class, ModelBaseMixin.class);
        objectMapper.addMixIn(SimpleModel.class, SimpleModelMixin.class);
        objectMapper.addMixIn(ComplexModel.class, ComplexModelMixin.class);

        // Get the JSON for the models:
        String json = objectMapper.writeValueAsString(modelRoot);
        String expectedJSON =
            "{\n" +
            "  \"models\" : [ {\n" +
            "    \"type\" : \"io.nanovc.agentsim.technologies.jackson.JacksonTests$SimpleModel\",\n" +
            "    \"keepThis\" : 42,\n" +
            "    \"name\" : \"Luke\",\n" +
            "    \"oldness\" : 38\n" +
            "  }, {\n" +
            "    \"type\" : \"io.nanovc.agentsim.technologies.jackson.JacksonTests$ComplexModel\",\n" +
            "    \"name\" : \"Luke\",\n" +
            "    \"age\" : 38,\n" +
            "    \"keepThis\" : 42\n" +
            "  } ]\n" +
            "}";
        assertEquals(expectedJSON, json.replace(System.lineSeparator(), "\n"));
    }

    public static class ModelRoot
    {
        public ModelCollection models = new ModelCollection();
    }

    public static class ModelCollection extends ArrayList<ModelBase>
    {
    }

    /**
     * A base class for the example models.
     */
    // NOTE: We don't annotate this class to show type information in the JSON. The mixin does that.
    public static abstract class ModelBase
    {
        /**
         * This is something that we want all sub classes to have.
         */
        public int keepThis = 42;

        /**
         * Something that we don't want serialized.
         */
        public int ignoreThis;
    }

    /**
     * A mixin that allows us to control the serialization of the {@link ModelBase} class.
     * This example is to demonstrate that a simple model that doesn't have any
     * Jackson annotations can have a {@link ModelBaseMixin} which provides
     * all the additional Jackson annotations without polluting the {@link ModelBase}.
     * https://github.com/FasterXML/jackson-docs/wiki/JacksonMixInAnnotations
     */
    @JsonTypeInfo(use= JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="type")
    public static abstract class ModelBaseMixin
    {
        /**
         * Ignore a field on the model.
         */
        @JsonIgnore int ignoreThis;
    }


    /**
     * A model that we don't want to pollute with annotations.
     * This example is to demonstrate that a simple model that doesn't have any
     * Jackson annotations can have a {@link SimpleModelMixin} which provides
     * all the additional Jackson annotations without polluting the {@link SimpleModel}.
     * https://github.com/FasterXML/jackson-docs/wiki/JacksonMixInAnnotations
     */
    public static class SimpleModel extends ModelBase
    {
        public String name;
        public int age;
    }

    /**
     * A mixin that allows us to control the serialization of the {@link SimpleModel} class.
     * This example is to demonstrate that a simple model that doesn't have any
     * Jackson annotations can have a {@link SimpleModelMixin} which provides
     * all the additional Jackson annotations without polluting the {@link SimpleModel}.
     * https://github.com/FasterXML/jackson-docs/wiki/JacksonMixInAnnotations
     */
    public static abstract class SimpleModelMixin
    {
        @JsonProperty("oldness") public int age;
    }

    /**
     * A model that we don't want to pollute with annotations.
     * This example is to demonstrate that a complex model can be substituted with a surrogate model.
     */
    public static class ComplexModel extends ModelBase
    {
        public ComplexModel(String name, int age)
        {
            this.name = name;
            this.age = age;
        }

        public String name;
        public int age;
    }

    /**
     * A mixin that allows us to control the serialization of the {@link SimpleModel} class.
     * This example is to demonstrate that a complex model can be substituted with a surrogate model.
     */
    @JsonSerialize(as = ComplexModelSurrogate.class)
    @JsonDeserialize(as = ComplexModelSurrogate.class)
    public static abstract class ComplexModelMixin
    {

    }

    /**
     * The surrogate to use instead of the complex model.
     * This example is to demonstrate that a complex model can be substituted with a surrogate model.
     */
    public static class ComplexModelSurrogate extends ComplexModel
    {
        @JsonCreator
        public ComplexModelSurrogate(@JsonProperty("name") String name, @JsonProperty("age") int age)
        {
            super(name, age);
        }
    }
}
