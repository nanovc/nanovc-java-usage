package io.nanovc.agentsim;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeAll;

/**
 * The base class for that helps with testing {@link EnvironmentModel}'s.
 */
public class EnvironmentModelTestsBase extends AgentSimTestsBase
{
}
