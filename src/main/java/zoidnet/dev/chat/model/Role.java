package zoidnet.dev.chat.model;

import static zoidnet.dev.chat.model.Authority.ROLE_ADMIN;
import static zoidnet.dev.chat.model.Authority.ROLE_USER;

public enum Role {

    USER (ROLE_USER.name()),

    ADMIN (ROLE_ADMIN.name());

    private final String authority;


    Role(String authority) {
        this.authority = authority;
    }

    public String asAuthority() {
        return authority;
    }

}
