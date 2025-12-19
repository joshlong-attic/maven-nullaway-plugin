package net.ltgt.maven.nullaway;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.List;

/**
 * Maven plugin for configuring NullAway static analysis with Error Prone.
 * <p>
 * This plugin reads NullAway configuration from the POM and injects the appropriate
 * Error Prone command-line arguments into the maven-compiler-plugin configuration.
 * <p>
 * The plugin executes in the {@code initialize} phase (before compilation) to ensure
 * the arguments are available when javac runs.
 */
@Mojo(
        name = "configure",
        defaultPhase = LifecyclePhase.INITIALIZE,
        threadSafe = true
)
public class NullAwayMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    // ========== Core Configuration ==========

    /**
     * The severity of the NullAway check.
     * <p>
     * Valid values: OFF, WARN, ERROR, DEFAULT
     * <ul>
     *   <li>OFF - Disable NullAway</li>
     *   <li>WARN - Enable NullAway as warnings</li>
     *   <li>ERROR - Enable NullAway as errors (fails build)</li>
     *   <li>DEFAULT - Enable NullAway with default Error Prone severity</li>
     * </ul>
     */
    @Parameter(property = "nullaway.severity", defaultValue = "DEFAULT")
    private String severity;

    /**
     * Indicates that the annotatedPackages flag has been deliberately omitted, and that NullAway
     * can proceed with only treating @NullMarked code as annotated, in accordance with the JSpecify specification.
     * <p>
     * If this option is set to true, then annotatedPackages must be empty.
     * Note that even if this flag is omitted (and annotatedPackages is non-empty),
     * any @NullMarked code will still be treated as annotated.
     */
    @Parameter(property = "nullaway.onlyNullMarked")
    private Boolean onlyNullMarked;

    /**
     * The list of packages that should be considered properly annotated according to the NullAway convention.
     * <p>
     * Either this or onlyNullMarked must be specified (but not both).
     */
    @Parameter(property = "nullaway.annotatedPackages")
    private List<String> annotatedPackages;

    // ========== Package and Class Exclusions ==========

    /**
     * A list of subpackages to be excluded from the annotatedPackages list.
     */
    @Parameter(property = "nullaway.unannotatedSubPackages")
    private List<String> unannotatedSubPackages;

    /**
     * A list of classes within annotated packages that should be treated as unannotated.
     */
    @Parameter(property = "nullaway.unannotatedClasses")
    private List<String> unannotatedClasses;

    /**
     * A list of classes to be excluded from the nullability analysis.
     */
    @Parameter(property = "nullaway.excludedClasses")
    private List<String> excludedClasses;

    /**
     * A list of annotations that cause classes to be excluded from nullability analysis.
     */
    @Parameter(property = "nullaway.excludedClassAnnotations")
    private List<String> excludedClassAnnotations;

    /**
     * A list of annotations that cause fields to be excluded from being checked for proper initialization.
     */
    @Parameter(property = "nullaway.excludedFieldAnnotations")
    private List<String> excludedFieldAnnotations;

    // ========== Initializers ==========

    /**
     * The fully qualified name of those methods from third-party libraries that NullAway should treat as initializers.
     */
    @Parameter(property = "nullaway.knownInitializers")
    private List<String> knownInitializers;

    /**
     * A list of annotations that should be considered equivalent to @Initializer annotations,
     * and thus mark methods as initializers.
     */
    @Parameter(property = "nullaway.customInitializerAnnotations")
    private List<String> customInitializerAnnotations;

    /**
     * A list of annotations for classes that are "externally initialized."
     */
    @Parameter(property = "nullaway.externalInitAnnotations")
    private List<String> externalInitAnnotations;

    // ========== Behavioral Options ==========

    /**
     * If set to true, NullAway treats any class annotated with @Generated as if its APIs are
     * unannotated when analyzing uses from other classes.
     */
    @Parameter(property = "nullaway.treatGeneratedAsUnannotated")
    private Boolean treatGeneratedAsUnannotated;

    /**
     * If set to true, NullAway will acknowledge nullability annotations whenever they are available
     * in unannotated code and also more restrictive than its optimistic defaults.
     */
    @Parameter(property = "nullaway.acknowledgeRestrictiveAnnotations")
    private Boolean acknowledgeRestrictiveAnnotations;

    /**
     * If set to true, NullAway will check for .get() accesses to potentially empty Optional values,
     * analogously to how it handles dereferences to @Nullable values.
     */
    @Parameter(property = "nullaway.checkOptionalEmptiness")
    private Boolean checkOptionalEmptiness;

    /**
     * If set to true, NullAway will use Error Prone's suggested fix functionality to suggest
     * suppressing any warning that it finds.
     */
    @Parameter(property = "nullaway.suggestSuppressions")
    private Boolean suggestSuppressions;

    /**
     * If set to true, NullAway will handle assertions, and use that to reason about the possibility
     * of null dereferences in the code that follows these assertions.
     * <p>
     * This assumes that assertions will always be enabled at runtime (java run with -ea JVM argument).
     */
    @Parameter(property = "nullaway.assertsEnabled")
    private Boolean assertsEnabled;

    /**
     * If set to true, NullAway will check every method to see whether or not it overrides a method
     * of a super-type, rather than relying only on the @Override annotation.
     */
    @Parameter(property = "nullaway.exhaustiveOverride")
    private Boolean exhaustiveOverride;

    /**
     * If set to true, NullAway will handle assertions from test libraries, like assertThat(...).isNotNull(),
     * and use that to reason about the possibility of null dereferences in the code that follows these assertions.
     */
    @Parameter(property = "nullaway.handleTestAssertionLibraries")
    private Boolean handleTestAssertionLibraries;

    /**
     * If set to true, treats @RecentlyNullable as @Nullable, and @RecentlyNonNull as @NonNull.
     * <p>
     * Requires that acknowledgeRestrictiveAnnotations is also set to true.
     */
    @Parameter(property = "nullaway.acknowledgeAndroidRecent")
    private Boolean acknowledgeAndroidRecent;

    /**
     * If set to true, NullAway will check @Contract annotations.
     */
    @Parameter(property = "nullaway.checkContracts")
    private Boolean checkContracts;

    /**
     * If set to true, enables new checks based on JSpecify (like checks for generic types).
     */
    @Parameter(property = "nullaway.jspecifyMode")
    private Boolean jspecifyMode;

    // ========== Custom Classes and Methods ==========

    /**
     * The fully qualified name of a method to be used for downcasting to a non-null value
     * rather than standard suppressions in some instances.
     */
    @Parameter(property = "nullaway.castToNonNullMethod")
    private String castToNonNullMethod;

    /**
     * A list of classes to be treated as Optional implementations
     * (e.g. Guava's com.google.common.base.Optional).
     */
    @Parameter(property = "nullaway.checkOptionalEmptinessCustomClasses")
    private List<String> checkOptionalEmptinessCustomClasses;

    /**
     * A list of classes to be treated equivalently to Guava Futures and FluentFuture.
     * <p>
     * This special support will likely be removed once NullAway's JSpecify support is more complete.
     */
    @Parameter(property = "nullaway.extraFuturesClasses")
    private List<String> extraFuturesClasses;

    // ========== Custom Annotations ==========

    /**
     * A list of annotations that should be considered equivalent to @Contract annotations.
     */
    @Parameter(property = "nullaway.customContractAnnotations")
    private List<String> customContractAnnotations;

    /**
     * A list of annotations that should be considered equivalent to @Nullable annotations.
     */
    @Parameter(property = "nullaway.customNullableAnnotations")
    private List<String> customNullableAnnotations;

    /**
     * A list of annotations that should be considered equivalent to @NonNull annotations,
     * for the cases where NullAway cares about such annotations.
     */
    @Parameter(property = "nullaway.customNonnullAnnotations")
    private List<String> customNonnullAnnotations;

    /**
     * A list of annotations that should be considered equivalent to @Generated annotations,
     * for the cases where NullAway cares about such annotations.
     */
    @Parameter(property = "nullaway.customGeneratedCodeAnnotations")
    private List<String> customGeneratedCodeAnnotations;

    // ========== Other Options ==========

    /**
     * A comment that will be added alongside the @SuppressWarnings("NullAway") annotation
     * when suggestSuppressions is set to true.
     */
    @Parameter(property = "nullaway.autoFixSuppressionComment")
    private String autoFixSuppressionComment;

    /**
     * A list of names to suppress NullAway using a @SuppressWarnings annotation,
     * similar to @SuppressWarnings("NullAway").
     * <p>
     * This is useful when other warnings are already suppressed in the codebase and NullAway
     * should be suppressed as well, such as with JetBrains' DataFlowIssue inspection.
     */
    @Parameter(property = "nullaway.suppressionNameAliases")
    private List<String> suppressionNameAliases;

    @Override
    public void execute() throws MojoExecutionException {
        // Validate configuration
        validateConfiguration();

        // Generate Error Prone arguments
        ArgumentGenerator generator = new ArgumentGenerator(
                severity,
                onlyNullMarked,
                annotatedPackages,
                unannotatedSubPackages,
                unannotatedClasses,
                knownInitializers,
                excludedClassAnnotations,
                excludedClasses,
                excludedFieldAnnotations,
                customInitializerAnnotations,
                externalInitAnnotations,
                treatGeneratedAsUnannotated,
                acknowledgeRestrictiveAnnotations,
                checkOptionalEmptiness,
                suggestSuppressions,
                assertsEnabled,
                exhaustiveOverride,
                castToNonNullMethod,
                checkOptionalEmptinessCustomClasses,
                autoFixSuppressionComment,
                handleTestAssertionLibraries,
                acknowledgeAndroidRecent,
                checkContracts,
                customContractAnnotations,
                customNullableAnnotations,
                customNonnullAnnotations,
                customGeneratedCodeAnnotations,
                jspecifyMode,
                extraFuturesClasses,
                suppressionNameAliases
        );

        List<String> arguments = generator.generate();

        // Skip if NullAway is disabled
        if ("OFF".equals(severity)) {
            getLog().info("NullAway is disabled (severity=OFF)");
            return;
        }

        // Inject into maven-compiler-plugin
        CompilerConfigInjector injector = new CompilerConfigInjector(project);
        injector.injectCompilerArgs(arguments);

        getLog().info("NullAway configured with " + arguments.size() + " arguments");
        if (getLog().isDebugEnabled()) {
            getLog().debug("NullAway arguments:");
            for (String arg : arguments) {
                getLog().debug("  " + arg);
            }
        }
    }

    /**
     * Validates the configuration for common errors.
     *
     * @throws MojoExecutionException if configuration is invalid
     */
    private void validateConfiguration() throws MojoExecutionException {
        // Validate onlyNullMarked vs annotatedPackages
        boolean hasOnlyNullMarked = onlyNullMarked != null && onlyNullMarked;
        boolean hasAnnotatedPackages = annotatedPackages != null && !annotatedPackages.isEmpty();

        if (hasOnlyNullMarked && hasAnnotatedPackages) {
            throw new MojoExecutionException(
                    "Cannot specify both onlyNullMarked=true and annotatedPackages. " +
                            "Use onlyNullMarked for JSpecify @NullMarked only, " +
                            "or use annotatedPackages for the traditional NullAway annotation model.");
        }

        if (!hasOnlyNullMarked && !hasAnnotatedPackages && !"OFF".equals(severity)) {
            throw new MojoExecutionException(
                    "Must specify either onlyNullMarked=true OR annotatedPackages when NullAway is enabled. " +
                            "See: https://github.com/uber/NullAway/wiki/Configuration");
        }

        // Validate acknowledgeAndroidRecent requires acknowledgeRestrictiveAnnotations
        if (Boolean.TRUE.equals(acknowledgeAndroidRecent) &&
                !Boolean.TRUE.equals(acknowledgeRestrictiveAnnotations)) {
            throw new MojoExecutionException(
                    "acknowledgeAndroidRecent requires acknowledgeRestrictiveAnnotations=true");
        }
    }
}
