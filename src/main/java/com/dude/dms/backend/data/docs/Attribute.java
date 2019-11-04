package com.dude.dms.backend.data.docs;

import com.dude.dms.backend.data.DataEntity;
import com.dude.dms.backend.data.Tag;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class Attribute extends DataEntity {

    @OneToMany(mappedBy = "attribute")
    private Set<AttributeValue> attributeValues;

    @ManyToMany(mappedBy = "attributes")
    private Set<Tag> tags;

    @NotBlank
    protected String name;

    protected boolean required;

    public Attribute() {

    }

    public Attribute(@NotBlank String name, boolean required) {
        this.name = name;
        this.required = required;
    }

    public Set<AttributeValue> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(Set<AttributeValue> attributeValues) {
        this.attributeValues = attributeValues;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public String toString() {
        return "Attribute{name='" + name + "', required=" + required + '}';
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}