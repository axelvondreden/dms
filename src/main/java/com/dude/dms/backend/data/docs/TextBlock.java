package com.dude.dms.backend.data.docs;

import com.dude.dms.backend.data.DataEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class TextBlock extends DataEntity {

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

    @NotNull
    protected Float fontSize;

    @NotNull
    protected Float pageWidth;

    @NotNull
    protected Float pagHeight;

    public TextBlock() {

    }

    public TextBlock(@NotNull Doc doc, @NotNull String text, @NotNull Float x, @NotNull Float y, @NotNull Float width, @NotNull Float height, @NotNull Float fontSize, @NotNull Float pageWidth, @NotNull Float pagHeight) {
        this.doc = doc;
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.fontSize = fontSize;
        this.pageWidth = pageWidth;
        this.pagHeight = pagHeight;
    }

    public TextBlock(@NotNull String text, @NotNull Float x, @NotNull Float y, @NotNull Float width, @NotNull Float height, @NotNull Float fontSize, @NotNull Float pageWidth, @NotNull Float pagHeight) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.fontSize = fontSize;
        this.pageWidth = pageWidth;
        this.pagHeight = pagHeight;
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

    public Float getFontSize() {
        return fontSize;
    }

    public void setFontSize(Float fontSize) {
        this.fontSize = fontSize;
    }

    public Float getPageWidth() {
        return pageWidth;
    }

    public void setPageWidth(Float pageWidth) {
        this.pageWidth = pageWidth;
    }

    public Float getPagHeight() {
        return pagHeight;
    }

    public void setPagHeight(Float pagHeight) {
        this.pagHeight = pagHeight;
    }

    @Override
    public String toString() {
        return "TextBlock{text='" + text + "', x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + '}';
    }
}