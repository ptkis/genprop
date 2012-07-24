package org.intellij.idea.plugin.genprop.velocity;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.SimpleLog4JLogSystem;

/**
 * Velocity factory. <p/> Creating instances of the VelocityEngine.
 *
 * @author Claus Ibsen
 * @since 2.19
 */
public class VelocityFactory {

    private VelocityFactory() {
    }

    /**
     * Returns a new instance of the VelocityEngine. <p/> The engine is initialized and outputs its logging to IDEA
     * logging.
     *
     * @return a new velocity engine that is initialized.
     * @throws Exception error creating the VelocityEngine.
     */
    public static VelocityEngine newVeloictyEngine()
            throws Exception {
        ExtendedProperties prop = new ExtendedProperties();
        prop.addProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS, SimpleLog4JLogSystem.class.getName());
        prop.addProperty("runtime.log.logsystem.log4j.category", "GenerateProperty");
        VelocityEngine velocity = new VelocityEngine();
        velocity.setExtendedProperties(prop);
        velocity.init();
        return velocity;
    }

}
