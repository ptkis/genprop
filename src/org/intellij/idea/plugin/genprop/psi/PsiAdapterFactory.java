package org.intellij.idea.plugin.genprop.psi;

import com.intellij.openapi.application.ApplicationInfo;
import org.apache.log4j.Logger;

/**
 * Factory to get a PsiAdapter class compatible with the correct version of IDEA 3.x or 4.x/4.5x.
 *
 * @author Claus Ibsen
 * @see PsiAdapter
 */
public class PsiAdapterFactory {

    private static Logger log = Logger.getLogger(PsiAdapterFactory.class);
    private static PsiAdapter instance; // singleton instance

    /**
     * Gets a version of PsiAdapter that is compatible with IDEA 4.x/4.5x or IDEA 3.x.
     *
     * @return the PsiAdapter used for the current version of IDEA.
     */
    public static PsiAdapter getPsiAdapter() {
        if (instance == null) {

            logIDEAApplicationInfo(ApplicationInfo.getInstance());

            try {
                if (runningIdea3()) {
                    log.info("Running IDEA 3.x version");
                    Class clazz = Class.forName("org.intellij.idea.plugin.genprop.psi.idea3.PsiAdapterIdea3");
                    instance = (PsiAdapter) clazz.newInstance();
                } else {
                    log.info("Assuming to be running IDEA 4.x/4.5x version");
                    Class clazz = Class.forName("org.intellij.idea.plugin.genprop.psi.PsiAdapter");
                    instance = (PsiAdapter) clazz.newInstance();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        return instance;
    }

    /**
     * Check if running IDEA 3.x
     *
     * @return true if running IDEA 3.x
     */
    private static boolean runningIdea3() {
        ApplicationInfo info = ApplicationInfo.getInstance();
        return "3".equals(info.getMajorVersion());
    }

    /**
     * Logs the IDEA Application Info to the logger.
     *
     * @since 2.18
     */
    private static void logIDEAApplicationInfo(ApplicationInfo info) {
        if (log.isInfoEnabled()) {
            log.info("IDEA ApplicationInfo: MajorVersion='" + info.getMajorVersion() + "'");
            log.info("IDEA ApplicationInfo: MinorVersion='" + info.getMinorVersion() + "'");
            log.info("IDEA ApplicationInfo: VersionName='" + info.getVersionName() + "'");
            log.info("IDEA ApplicationInfo: BuildNumber='" + info.getBuildNumber() + "'");
            log.info("IDEA ApplicationInfo: BuildDate='" + info.getBuildDate().getTime() + "'");
        }
    }

}
