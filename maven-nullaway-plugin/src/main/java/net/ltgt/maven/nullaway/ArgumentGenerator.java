package net.ltgt.maven.nullaway;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates Error Prone command-line arguments for NullAway configuration.
 * <p>
 * Ports the argument generation logic from the Gradle plugin's NullAwayOptions.asArguments() method.
 */
public class ArgumentGenerator {
    private final String severity;
    private final Boolean onlyNullMarked;
    private final List<String> annotatedPackages;
    private final List<String> unannotatedSubPackages;
    private final List<String> unannotatedClasses;
    private final List<String> knownInitializers;
    private final List<String> excludedClassAnnotations;
    private final List<String> excludedClasses;
    private final List<String> excludedFieldAnnotations;
    private final List<String> customInitializerAnnotations;
    private final List<String> externalInitAnnotations;
    private final Boolean treatGeneratedAsUnannotated;
    private final Boolean acknowledgeRestrictiveAnnotations;
    private final Boolean checkOptionalEmptiness;
    private final Boolean suggestSuppressions;
    private final Boolean assertsEnabled;
    private final Boolean exhaustiveOverride;
    private final String castToNonNullMethod;
    private final List<String> checkOptionalEmptinessCustomClasses;
    private final String autoFixSuppressionComment;
    private final Boolean handleTestAssertionLibraries;
    private final Boolean acknowledgeAndroidRecent;
    private final Boolean checkContracts;
    private final List<String> customContractAnnotations;
    private final List<String> customNullableAnnotations;
    private final List<String> customNonnullAnnotations;
    private final List<String> customGeneratedCodeAnnotations;
    private final Boolean jspecifyMode;
    private final List<String> extraFuturesClasses;
    private final List<String> suppressionNameAliases;

    public ArgumentGenerator(
            String severity,
            Boolean onlyNullMarked,
            List<String> annotatedPackages,
            List<String> unannotatedSubPackages,
            List<String> unannotatedClasses,
            List<String> knownInitializers,
            List<String> excludedClassAnnotations,
            List<String> excludedClasses,
            List<String> excludedFieldAnnotations,
            List<String> customInitializerAnnotations,
            List<String> externalInitAnnotations,
            Boolean treatGeneratedAsUnannotated,
            Boolean acknowledgeRestrictiveAnnotations,
            Boolean checkOptionalEmptiness,
            Boolean suggestSuppressions,
            Boolean assertsEnabled,
            Boolean exhaustiveOverride,
            String castToNonNullMethod,
            List<String> checkOptionalEmptinessCustomClasses,
            String autoFixSuppressionComment,
            Boolean handleTestAssertionLibraries,
            Boolean acknowledgeAndroidRecent,
            Boolean checkContracts,
            List<String> customContractAnnotations,
            List<String> customNullableAnnotations,
            List<String> customNonnullAnnotations,
            List<String> customGeneratedCodeAnnotations,
            Boolean jspecifyMode,
            List<String> extraFuturesClasses,
            List<String> suppressionNameAliases) {
        this.severity = severity;
        this.onlyNullMarked = onlyNullMarked;
        this.annotatedPackages = annotatedPackages;
        this.unannotatedSubPackages = unannotatedSubPackages;
        this.unannotatedClasses = unannotatedClasses;
        this.knownInitializers = knownInitializers;
        this.excludedClassAnnotations = excludedClassAnnotations;
        this.excludedClasses = excludedClasses;
        this.excludedFieldAnnotations = excludedFieldAnnotations;
        this.customInitializerAnnotations = customInitializerAnnotations;
        this.externalInitAnnotations = externalInitAnnotations;
        this.treatGeneratedAsUnannotated = treatGeneratedAsUnannotated;
        this.acknowledgeRestrictiveAnnotations = acknowledgeRestrictiveAnnotations;
        this.checkOptionalEmptiness = checkOptionalEmptiness;
        this.suggestSuppressions = suggestSuppressions;
        this.assertsEnabled = assertsEnabled;
        this.exhaustiveOverride = exhaustiveOverride;
        this.castToNonNullMethod = castToNonNullMethod;
        this.checkOptionalEmptinessCustomClasses = checkOptionalEmptinessCustomClasses;
        this.autoFixSuppressionComment = autoFixSuppressionComment;
        this.handleTestAssertionLibraries = handleTestAssertionLibraries;
        this.acknowledgeAndroidRecent = acknowledgeAndroidRecent;
        this.checkContracts = checkContracts;
        this.customContractAnnotations = customContractAnnotations;
        this.customNullableAnnotations = customNullableAnnotations;
        this.customNonnullAnnotations = customNonnullAnnotations;
        this.customGeneratedCodeAnnotations = customGeneratedCodeAnnotations;
        this.jspecifyMode = jspecifyMode;
        this.extraFuturesClasses = extraFuturesClasses;
        this.suppressionNameAliases = suppressionNameAliases;
    }

    /**
     * Generates Error Prone command-line arguments for NullAway configuration.
     * <p>
     * Returns a list of arguments in the format:
     * <ul>
     *   <li>{@code -Xep:NullAway[:SEVERITY]} for check severity</li>
     *   <li>{@code -XepOpt:NullAway:PropertyName=value} for all other options</li>
     * </ul>
     *
     * @return list of Error Prone arguments
     */
    public List<String> generate() {
        List<String> args = new ArrayList<>();

        // Add severity (always present)
        args.add(formatSeverity());

        // Add each option if present (matching the order from NullAwayOptions.asArguments())
        addIfNotNull(args, listOption("AnnotatedPackages", annotatedPackages));
        addIfNotNull(args, booleanOption("OnlyNullMarked", onlyNullMarked));
        addIfNotNull(args, listOption("UnannotatedSubPackages", unannotatedSubPackages));
        addIfNotNull(args, listOption("UnannotatedClasses", unannotatedClasses));
        addIfNotNull(args, listOption("KnownInitializers", knownInitializers));
        addIfNotNull(args, listOption("ExcludedClassAnnotations", excludedClassAnnotations));
        addIfNotNull(args, listOption("ExcludedClasses", excludedClasses));
        addIfNotNull(args, listOption("ExcludedFieldAnnotations", excludedFieldAnnotations));
        addIfNotNull(args, listOption("CustomInitializerAnnotations", customInitializerAnnotations));
        addIfNotNull(args, listOption("ExternalInitAnnotations", externalInitAnnotations));
        addIfNotNull(args, booleanOption("TreatGeneratedAsUnannotated", treatGeneratedAsUnannotated));
        addIfNotNull(args, booleanOption("AcknowledgeRestrictiveAnnotations", acknowledgeRestrictiveAnnotations));
        addIfNotNull(args, booleanOption("CheckOptionalEmptiness", checkOptionalEmptiness));
        addIfNotNull(args, booleanOption("SuggestSuppressions", suggestSuppressions));
        addIfNotNull(args, booleanOption("AssertsEnabled", assertsEnabled));
        addIfNotNull(args, booleanOption("ExhaustiveOverride", exhaustiveOverride));
        addIfNotNull(args, stringOption("CastToNonNullMethod", castToNonNullMethod));
        addIfNotNull(args, listOption("CheckOptionalEmptinessCustomClasses", checkOptionalEmptinessCustomClasses));
        addIfNotNull(args, stringOption("AutoFixSuppressionComment", autoFixSuppressionComment));
        addIfNotNull(args, booleanOption("HandleTestAssertionLibraries", handleTestAssertionLibraries));
        addIfNotNull(args, booleanOption("AcknowledgeAndroidRecent", acknowledgeAndroidRecent));
        addIfNotNull(args, booleanOption("CheckContracts", checkContracts));
        addIfNotNull(args, listOption("CustomContractAnnotations", customContractAnnotations));
        addIfNotNull(args, listOption("CustomNullableAnnotations", customNullableAnnotations));
        addIfNotNull(args, listOption("CustomNonnullAnnotations", customNonnullAnnotations));
        addIfNotNull(args, listOption("CustomGeneratedCodeAnnotations", customGeneratedCodeAnnotations));
        addIfNotNull(args, booleanOption("JSpecifyMode", jspecifyMode));
        addIfNotNull(args, listOption("ExtraFuturesClasses", extraFuturesClasses));
        addIfNotNull(args, listOption("SuppressionNameAliases", suppressionNameAliases));

        return args;
    }

    /**
     * Formats the severity argument.
     * <p>
     * Maps severity values to Error Prone check syntax:
     * <ul>
     *   <li>OFF → {@code -Xep:NullAway:OFF}</li>
     *   <li>WARN → {@code -Xep:NullAway:WARN}</li>
     *   <li>ERROR → {@code -Xep:NullAway:ERROR}</li>
     *   <li>DEFAULT or null → {@code -Xep:NullAway}</li>
     * </ul>
     *
     * @return formatted severity argument
     */
    private String formatSeverity() {
        if (severity == null || "DEFAULT".equals(severity)) {
            return "-Xep:NullAway";
        }
        return "-Xep:NullAway:" + severity;
    }

    /**
     * Formats a list option as a comma-separated Error Prone argument.
     *
     * @param name   the option name
     * @param values the list of values
     * @return formatted argument, or null if the list is null or empty
     */
    private String listOption(String name, List<String> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return "-XepOpt:NullAway:" + name + "=" + String.join(",", values);
    }

    /**
     * Formats a boolean option as an Error Prone argument.
     *
     * @param name  the option name
     * @param value the boolean value
     * @return formatted argument, or null if the value is null
     */
    private String booleanOption(String name, Boolean value) {
        if (value == null) {
            return null;
        }
        return "-XepOpt:NullAway:" + name + "=" + value;
    }

    /**
     * Formats a string option as an Error Prone argument.
     *
     * @param name  the option name
     * @param value the string value
     * @return formatted argument, or null if the value is null or empty
     */
    private String stringOption(String name, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return "-XepOpt:NullAway:" + name + "=" + value;
    }

    /**
     * Adds an argument to the list if it's not null.
     *
     * @param args the arguments list
     * @param arg  the argument to add (may be null)
     */
    private void addIfNotNull(List<String> args, String arg) {
        if (arg != null) {
            args.add(arg);
        }
    }
}
