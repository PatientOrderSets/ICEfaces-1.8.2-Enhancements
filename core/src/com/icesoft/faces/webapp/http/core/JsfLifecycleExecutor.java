package com.icesoft.faces.webapp.http.core;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import com.icesoft.faces.util.CoreUtils;

public class JsfLifecycleExecutor extends LifecycleExecutor {
    private final LifecycleFactory LIFECYCLE_FACTORY =
            (LifecycleFactory) FactoryFinder.getFactory(
                    FactoryFinder.LIFECYCLE_FACTORY);
    private Lifecycle lifecycle = LIFECYCLE_FACTORY.getLifecycle(
            LIFECYCLE_FACTORY.DEFAULT_LIFECYCLE);

    public void apply(FacesContext facesContext) {
        CoreUtils.addAuxiliaryContexts(facesContext);
        lifecycle.execute(facesContext);
        lifecycle.render(facesContext);
    }
}