package org.schulcloud.mobile.data.model.responseBodies;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class FeedbackResponseTest {
    private static final String MESSAGEID = "LUKE";
    private static final String RESPONSE = "VADER";
    private static final Envelope ENVELOPE = new Envelope();

    private FeedbackResponse feedbackResponse;

    @Before
    public void setUp() throws Exception {
        feedbackResponse = createFeedbackResponse();
    }

    @Test
    public void testGetProperties() {
        assertEquals(feedbackResponse.messageId, MESSAGEID);
        assertEquals(feedbackResponse.response, RESPONSE);
        assertEquals(feedbackResponse.envelope, ENVELOPE);
    }

    public static FeedbackResponse createFeedbackResponse() {
        FeedbackResponse feedbackResponse = new FeedbackResponse();
        feedbackResponse.messageId = MESSAGEID;
        feedbackResponse.response = RESPONSE;
        feedbackResponse.envelope = ENVELOPE;

        return feedbackResponse;
    }

}