package com.techshroom.unplanned.gui.model;

public class Label extends GuiElementBase implements Labeled {

    private String text;

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

}
