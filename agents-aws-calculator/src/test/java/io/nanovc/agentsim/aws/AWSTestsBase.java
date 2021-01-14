package io.nanovc.agentsim.aws;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.nanovc.agentsim.AgentConfigAPI;
import io.nanovc.agentsim.SimulationHandlerTestsBase;
import io.nanovc.agentsim.aws.organizations.OrganizationalUnit;
import org.junit.jupiter.api.BeforeAll;

/**
 * The base class for AWS tests.
 */
public class AWSTestsBase extends SimulationHandlerTestsBase
{
    @BeforeAll
    public static void configureJsonMapper()
    {
        // Use mixins to provide annotations for types that we don't control directly:
        // https://github.com/FasterXML/jackson-docs/wiki/JacksonMixInAnnotations
        jsonMapper.addMixIn(OrganizationalUnit.class, OrganizationalUnitMixin.class);
    }

    @JsonPropertyOrder({"organizationalUnitName"})
    public static abstract class OrganizationalUnitMixin extends TypedMixin
    {
    }
}
