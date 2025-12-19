package net.ltgt.maven.nullaway;

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.util.List;

/**
 * Injects NullAway arguments into maven-compiler-plugin configuration.
 * <p>
 * This class manipulates the project's build configuration to add Error Prone
 * arguments to the maven-compiler-plugin's compilerArgs.
 */
public class CompilerConfigInjector {
    private static final String COMPILER_PLUGIN_GROUP_ID = "org.apache.maven.plugins";
    private static final String COMPILER_PLUGIN_ARTIFACT_ID = "maven-compiler-plugin";
    private static final String COMPILER_PLUGIN_KEY = COMPILER_PLUGIN_GROUP_ID + ":" + COMPILER_PLUGIN_ARTIFACT_ID;

    private final MavenProject project;

    public CompilerConfigInjector(MavenProject project) {
        this.project = project;
    }

    /**
     * Injects NullAway arguments into the maven-compiler-plugin configuration.
     *
     * @param nullawayArgs the NullAway Error Prone arguments to inject
     * @throws MojoExecutionException if maven-compiler-plugin is not found
     */
    public void injectCompilerArgs(List<String> nullawayArgs) throws MojoExecutionException {
        if (nullawayArgs.isEmpty()) {
            return;
        }

        Plugin compilerPlugin = findCompilerPlugin();
        if (compilerPlugin == null) {
            throw new MojoExecutionException(
                    "maven-compiler-plugin not found in project build plugins. " +
                            "Please add maven-compiler-plugin to your pom.xml.");
        }

        // Get or create configuration
        Xpp3Dom configuration = (Xpp3Dom) compilerPlugin.getConfiguration();
        if (configuration == null) {
            configuration = new Xpp3Dom("configuration");
            compilerPlugin.setConfiguration(configuration);
        }

        // Get or create compilerArgs
        Xpp3Dom compilerArgs = configuration.getChild("compilerArgs");
        if (compilerArgs == null) {
            compilerArgs = new Xpp3Dom("compilerArgs");
            configuration.addChild(compilerArgs);
        }

        // Add NullAway arguments
        for (String arg : nullawayArgs) {
            Xpp3Dom argNode = new Xpp3Dom("arg");
            argNode.setValue(arg);
            compilerArgs.addChild(argNode);
        }
    }

    /**
     * Finds the maven-compiler-plugin in the project's build plugins.
     *
     * @return the compiler plugin, or null if not found
     */
    private Plugin findCompilerPlugin() {
        if (project.getBuild() == null) {
            return null;
        }
        return project.getBuild().getPluginsAsMap().get(COMPILER_PLUGIN_KEY);
    }
}
