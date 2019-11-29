package com.dude.dms.backend.data.docs;

import com.dude.dms.backend.data.DataEntity;
import com.dude.dms.backend.data.Tag;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class Attribute extends DataEntity {

    @OneToMany(mappedBy = "attribute")
    private Set<AttributeValue> attributeValues;

    @ManyToMany(mappedBy = "attributes", fetch = FetchType.EAGER)
    private Set<Tag> tags;

    @NotBlank
    protected String name;

    protected boolean required;

    @NotNull
    protected Type type;

    public enum Type {
        STRING, INT, FLOAT, DATE
    }

    public Attribute() {

    }

    public Attribute(@NotBlank String name, boolean required, @NotNull Type type) {
        this.name = name;
        this.required = required;
        this.type = type;
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

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Attribute{name='" + name + "', required=" + required + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Attribute attribute = (Attribute) o;
        return name.equals(attribute.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }
}