package org.schulcloud.mobile.data.model.requestBodies;

import org.schulcloud.mobile.data.model.Content;

public class FeedbackRequest {

    public Content content = new Content();
    public String subject;
    public String email;

    public FeedbackRequest(String text, String subject, String email) {
        this.content.text = text;
        this.subject = subject;
        this.email = email;
    }
}
