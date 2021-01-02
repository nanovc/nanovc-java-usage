import io.nanovc.NanoVersionControl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the {@link NanoVersionControl} API.
 */
public class NanoVersionControlTests
{
    @Test
    public void testNanoVersionControlURL()
    {
        assertEquals("https://nanovc.io", NanoVersionControl.URL);
    }
}
