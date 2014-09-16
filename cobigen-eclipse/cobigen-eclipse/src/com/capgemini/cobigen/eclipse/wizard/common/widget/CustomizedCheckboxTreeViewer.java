/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.eclipse.wizard.common.widget;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

/**
 * Customized {@link CheckboxTreeViewer} which fires changed events if setting an array of checked elements
 * @author mbrunnli (15.03.2013)
 */
public class CustomizedCheckboxTreeViewer extends CheckboxTreeViewer {

    /**
     * Creates a new {@link CustomizedCheckboxTreeViewer} with {@link SWT#BORDER} style
     * @param parent
     *            of this viewer
     * @author mbrunnli (15.03.2013)
     */
    public CustomizedCheckboxTreeViewer(Composite parent) {
        super(parent, SWT.BORDER);
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (26.03.2013)
     */
    @Override
    public boolean getChecked(Object element) {
        Widget widget = internalExpand(element, false);
        if (widget instanceof TreeItem) {
            return ((TreeItem) widget).getChecked();
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (15.03.2013)
     */
    @Override
    public void setCheckedElements(Object[] elements) {
        super.setCheckedElements(elements);
        fireCheckStateChanged(new CheckStateChangedEvent(this, elements, true));
    }

    /**
     * {@inheritDoc}
     * @author trippl (18.04.2013)
     */
    @Override
    public boolean setChecked(Object element, boolean value) {
        boolean returnValue = super.setChecked(element, value);
        if (returnValue) fireCheckStateChanged(new CheckStateChangedEvent(this, element, value));
        return returnValue;
    }

}
