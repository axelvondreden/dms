package com.dude.dms.backend.data.docs;

import com.dude.dms.backend.data.DataEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class AttributeValue extends DataEntity {

    @NotNull
    @ManyToOne
    private Doc doc;

    @NotNull
    @ManyToOne
    private Attribute attribute;

    protected String value;

    public AttributeValue() {

    }

    public AttributeValue(@NotNull Doc doc, @NotNull Attribute attribute) {
        this.doc = doc;
        this.attribute = attribute;
    }

    public AttributeValue(@NotNull Doc doc, @NotNull Attribute attribute, String value) {
        this.doc = doc;
        this.attribute = attribute;
        this.value = value;
    }

    public Doc getDoc() {
        return doc;
    }

    public void setDoc(Doc doc) {
        this.doc = doc;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "AttributeValue{doc=" + doc + ", attribute=" + attribute + ", value='" + value + "'}";
    }
}