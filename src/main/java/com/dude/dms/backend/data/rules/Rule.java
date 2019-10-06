package com.dude.dms.backend.data.rules;

import com.dude.dms.backend.data.DataEntity;
import com.dude.dms.backend.data.base.Tag;

import javax.persistence.MappedSuperclass;
import java.util.Set;

@MappedSuperclass
public abstract class Rule extends DataEntity {

    protected Boolean active;

    public abstract boolean validate(String line);

    public abstract Set<Tag> getTags();

    public abstract void setTags(Set<Tag> tags);

    protected Rule() {
    }

    protected Rule(Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}