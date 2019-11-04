package com.dude.dms.backend.data.docs;

import com.dude.dms.backend.data.DataEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class AttributeValue extends DataEntity {

    @NotNull
    @ManyToOne
    private Doc doc;

    @NotNull
    @ManyToOne
    private Attribute attribute;

    protected String stringValue;

    protected Integer intValue;

    protected Float floatValue;

    protected LocalDate dateValue;

    public AttributeValue() {

    }

    public AttributeValue(@NotNull Doc doc, @NotNull Attribute attribute) {
        this.doc = doc;
        this.attribute = attribute;
    }

    public AttributeValue(@NotNull Doc doc, @NotNull Attribute attribute, String stringValue) {
        this.doc = doc;
        this.attribute = attribute;
        this.stringValue = stringValue;
    }

    public AttributeValue(@NotNull Doc doc, @NotNull Attribute attribute, Integer intValue) {
        this.doc = doc;
        this.attribute = attribute;
        this.intValue = intValue;
    }

    public AttributeValue(@NotNull Doc doc, @NotNull Attribute attribute, Float floatValue) {
        this.doc = doc;
        this.attribute = attribute;
        this.floatValue = floatValue;
    }

    public AttributeValue(@NotNull Doc doc, @NotNull Attribute attribute, LocalDate dateValue) {
        this.doc = doc;
        this.attribute = attribute;
        this.dateValue = dateValue;
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

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }

    public Float getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(Float floatValue) {
        this.floatValue = floatValue;
    }

    public LocalDate getDateValue() {
        return dateValue;
    }

    public void setDateValue(LocalDate dateValue) {
        this.dateValue = dateValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AttributeValue that = (AttributeValue) o;
        return doc.equals(that.doc) && attribute.equals(that.attribute);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), doc, attribute);
    }
}