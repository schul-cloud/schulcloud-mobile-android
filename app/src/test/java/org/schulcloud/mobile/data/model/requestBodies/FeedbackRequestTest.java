package org.schulcloud.mobile.data.model.requestBodies;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class FeedbackRequestTest {
    private static final String SUBJECT = "FEEDBACK INCOMING";
    private static final String EMAIL = "localhost@lalala";
    private static final String TEXT = "YOU SHALL NOT PASS";

    private FeedbackRequest feedbackRequest;

    @Before
    public void setUp() {
        feedbackRequest = createFeedbackRequest();
    }

    @Test
    public void testGetProperties() {
        assertEquals(feedbackRequest.content.text, TEXT);
        assertEquals(feedbackRequest.subject, SUBJECT);
        assertEquals(feedbackRequest.email, EMAIL);
    }

    public static FeedbackRequest createFeedbackRequest() {
        FeedbackRequest feedbackRequest = new FeedbackRequest(TEXT, SUBJECT, EMAIL);

        return feedbackRequest;
    }


}