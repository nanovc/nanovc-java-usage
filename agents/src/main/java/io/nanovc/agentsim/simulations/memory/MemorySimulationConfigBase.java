package io.nanovc.agentsim.simulations.memory;

import com.fasterxml.jackson.databind.Module;
import io.nanovc.agentsim.SimulationConfigBase;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The base class for in-memory simulation configurations.
 */
public abstract class MemorySimulationConfigBase
    extends SimulationConfigBase
    implements MemorySimulationConfigAPI
{
    /**
     * Any additional mappings to use for serializing model items during the simulation.
     *
     * The key is the class of the item to augment with an mixin.
     * The value is the mixin class that we want to register.
     *
     * Use mixins to provide annotations for types that we don't control directly:
     * https://github.com/FasterXML/jackson-docs/wiki/JacksonMixInAnnotations
     *
     * For more complex serialization scenarios, consider using {@link #getSerializationModules()} instead.
     */
    public Map<Class<?>, Class<?>> serializationMixins = new LinkedHashMap<>();

    /**
     * Any additional mappings to use for serializing model items during the simulation.
     * <p>
     * The key is the class of the item to augment with an mixin.
     * The value is the mixin class that we want to register.
     * <p>
     * Use mixins to provide annotations for types that we don't control directly:
     * https://github.com/FasterXML/jackson-docs/wiki/JacksonMixInAnnotations
     *
     * For more complex serialization scenarios, consider using {@link #getSerializationModules()} instead.
     */
    @Override public Map<Class<?>, Class<?>> getSerializationMixins()
    {
        return serializationMixins;
    }


    /**
     * The list of additional serialization modules to register with Jackson.
     * This is used when serializing and deserializing types.
     * Use this when you want detailed control of serialization.
     * For simple cases, {@link #serializationMixins} might be enough.
     * Usually adding Java Date and Time support is useful:
     * {@link com.fasterxml.jackson.datatype.jsr310.JavaTimeModule}
     */
    public List<Class<? extends Module>> serializationModules = new ArrayList<>();

    /**
     * Gets the list of additional serialization modules to register with Jackson.
     * This is used when serializing and deserializing types.
     * Use this when you want detailed control of serialization.
     * For simple cases, {@link #serializationMixins} might be enough.
     * Usually adding Java Date and Time support is useful:
     * {@link com.fasterxml.jackson.datatype.jsr310.JavaTimeModule}
     */
    public List<Class<? extends Module>> getSerializationModules()
    {
        return serializationModules;
    }
}
