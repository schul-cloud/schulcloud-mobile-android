package org.schulcloud.mobile.data.model.responseBodies;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class EnvelopeTest {
    private static final String FROM = "LUKE";
    private static final String[] TO = new String[]{"VADER"};

    private Envelope envelope;

    @Before
    public void setUp() throws Exception {
        envelope = createEnvelope();
    }

    @Test
    public void testGetProperties() {
        assertEquals(envelope.from, FROM);
        assertEquals(envelope.to, TO);
    }

    public static Envelope createEnvelope() {
        Envelope envelope = new Envelope();
        envelope.from = FROM;
        envelope.to = TO;

        return envelope;
    }

}