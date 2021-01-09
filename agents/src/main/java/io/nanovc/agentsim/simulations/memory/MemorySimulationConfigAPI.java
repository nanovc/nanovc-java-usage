package io.nanovc.agentsim.simulations.memory;

import io.nanovc.agentsim.SimulationConfigAPI;

import java.util.Map;

/**
 * The API for in-memory simulation configurations.
 */
public interface MemorySimulationConfigAPI
    extends SimulationConfigAPI
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
    Map<Class<?>, Class<?>> getSerializationMixins();
}
