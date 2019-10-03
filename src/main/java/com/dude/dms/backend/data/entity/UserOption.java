package com.dude.dms.backend.data.entity;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
public class UserOption extends DataEntity implements Diffable<UserOption> {

    @NotBlank
    @Size(max = 255)
    protected String key;

    @Size(max = 255)
    protected String value;

    public UserOption() {

    }

    public UserOption(@NotBlank @Size(max = 255) String key, @Size(max = 255) String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}