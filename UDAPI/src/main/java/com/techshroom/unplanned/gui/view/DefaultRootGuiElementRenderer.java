package com.techshroom.unplanned.gui.view;

import com.techshroom.unplanned.gui.model.Label;

/**
 * Extension of {@link SimpleRootGuiElementRenderer} to automatically add
 * renderers for all UDAPI built-ins.
 */
public class DefaultRootGuiElementRenderer extends SimpleRootGuiElementRenderer {

    {
        addRenderer(new LabelRenderer(), Label.class);
    }

}
