package io.nanovc.agentsim.simulations.memory;

import io.nanovc.agentsim.SimulationConfigBase;

import java.util.LinkedHashMap;
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
     */
    @Override public Map<Class<?>, Class<?>> getSerializationMixins()
    {
        return serializationMixins;
    }
}
