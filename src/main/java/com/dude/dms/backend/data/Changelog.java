package com.dude.dms.backend.data;

import com.dude.dms.backend.data.DataEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class Changelog extends DataEntity {

    protected LocalDateTime published;

    @Size(max = 99999999)
    protected String body;

    @NotNull
    protected String version;

    public Changelog() {

    }

    public Changelog(LocalDateTime published, @Size(max = 99999999) String body, @NotNull String version) {
        this.published = published;
        this.body = body;
        this.version = version;
    }

    public LocalDateTime getPublished() {
        return published;
    }

    public void setPublished(LocalDateTime published) {
        this.published = published;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}