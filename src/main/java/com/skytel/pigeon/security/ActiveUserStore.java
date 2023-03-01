package com.skytel.pigeon.security;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActiveUserStore {

    public List<String> users;
    public ActiveUserStore() {
        users = new ArrayList<>();
    }
}
