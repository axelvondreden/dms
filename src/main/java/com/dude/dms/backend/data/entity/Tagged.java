package com.dude.dms.backend.data.entity;

import java.util.Set;

public interface Tagged {

    Set<Tag> getTags();

    void setTags(Set<Tag> tags);

}
