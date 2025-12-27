package org.example.demoescqrsaxon.commonapi.events;

import lombok.Getter;

public class AccountActivatedEvent extends BaseEvent<String> {
    @Getter
    private String status;

    public AccountActivatedEvent(String id, String status) {
        super(id);
        this.status = status;
    }
}
