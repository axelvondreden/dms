package com.dude.dms.backend.data.base;

import com.dude.dms.backend.data.DataEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class Word extends DataEntity {

    @NotNull
    @ManyToOne
    private Doc doc;

    @NotNull
    protected String text;

    @NotNull
    protected Float x;

    @NotNull
    protected Float y;

    @NotNull
    protected Float width;

    @NotNull
    protected Float height;

    public Word() {

    }

    public Word(@NotNull String text, @NotNull Float x, @NotNull Float y, @NotNull Float width, @NotNull Float height) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Word(@NotNull Doc doc, @NotNull String text, @NotNull Float x, @NotNull Float y, @NotNull Float width, @NotNull Float height) {
        this.doc = doc;
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Doc getDoc() {
        return doc;
    }

    public void setDoc(Doc doc) {
        this.doc = doc;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public Float getWidth() {
        return width;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "Word{text='" + text + "', x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + '}';
    }
}