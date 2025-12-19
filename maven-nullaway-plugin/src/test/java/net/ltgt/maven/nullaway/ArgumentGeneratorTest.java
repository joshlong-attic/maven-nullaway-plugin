package net.ltgt.maven.nullaway;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for ArgumentGenerator.
 */
public class ArgumentGeneratorTest {

    @Test
    public void testSeverityDefault() {
        ArgumentGenerator generator = createGenerator("DEFAULT");
        List<String> args = generator.generate();
        assertTrue("Should contain -Xep:NullAway", args.contains("-Xep:NullAway"));
    }

    @Test
    public void testSeverityNull() {
        ArgumentGenerator generator = createGenerator(null);
        List<String> args = generator.generate();
        assertTrue("Should contain -Xep:NullAway for null severity", args.contains("-Xep:NullAway"));
    }

    @Test
    public void testSeverityOff() {
        ArgumentGenerator generator = createGenerator("OFF");
        List<String> args = generator.generate();
        assertTrue("Should contain -Xep:NullAway:OFF", args.contains("-Xep:NullAway:OFF"));
    }

    @Test
    public void testSeverityWarn() {
        ArgumentGenerator generator = createGenerator("WARN");
        List<String> args = generator.generate();
        assertTrue("Should contain -Xep:NullAway:WARN", args.contains("-Xep:NullAway:WARN"));
    }

    @Test
    public void testSeverityError() {
        ArgumentGenerator generator = createGenerator("ERROR");
        List<String> args = generator.generate();
        assertTrue("Should contain -Xep:NullAway:ERROR", args.contains("-Xep:NullAway:ERROR"));
    }

    @Test
    public void testAnnotatedPackagesSingle() {
        ArgumentGenerator generator = createGeneratorBuilder()
                .annotatedPackages(Collections.singletonList("com.example"))
                .build();
        List<String> args = generator.generate();
        assertTrue("Should contain annotated packages",
                args.contains("-XepOpt:NullAway:AnnotatedPackages=com.example"));
    }

    @Test
    public void testAnnotatedPackagesMultiple() {
        ArgumentGenerator generator = createGeneratorBuilder()
                .annotatedPackages(Arrays.asList("com.example", "com.test"))
                .build();
        List<String> args = generator.generate();
        assertTrue("Should contain annotated packages with comma separator",
                args.contains("-XepOpt:NullAway:AnnotatedPackages=com.example,com.test"));
    }

    @Test
    public void testAnnotatedPackagesEmpty() {
        ArgumentGenerator generator = createGeneratorBuilder()
                .annotatedPackages(Collections.emptyList())
                .build();
        List<String> args = generator.generate();
        assertFalse("Should not contain annotated packages for empty list",
                containsOption(args, "AnnotatedPackages"));
    }

    @Test
    public void testAnnotatedPackagesNull() {
        ArgumentGenerator generator = createGeneratorBuilder()
                .annotatedPackages(null)
                .build();
        List<String> args = generator.generate();
        assertFalse("Should not contain annotated packages for null",
                containsOption(args, "AnnotatedPackages"));
    }

    @Test
    public void testOnlyNullMarkedTrue() {
        ArgumentGenerator generator = createGeneratorBuilder()
                .onlyNullMarked(true)
                .build();
        List<String> args = generator.generate();
        assertTrue("Should contain OnlyNullMarked=true",
                args.contains("-XepOpt:NullAway:OnlyNullMarked=true"));
    }

    @Test
    public void testOnlyNullMarkedFalse() {
        ArgumentGenerator generator = createGeneratorBuilder()
                .onlyNullMarked(false)
                .build();
        List<String> args = generator.generate();
        assertTrue("Should contain OnlyNullMarked=false",
                args.contains("-XepOpt:NullAway:OnlyNullMarked=false"));
    }

    @Test
    public void testOnlyNullMarkedNull() {
        ArgumentGenerator generator = createGeneratorBuilder()
                .onlyNullMarked(null)
                .build();
        List<String> args = generator.generate();
        assertFalse("Should not contain OnlyNullMarked for null",
                containsOption(args, "OnlyNullMarked"));
    }

    @Test
    public void testBooleanOptions() {
        ArgumentGenerator generator = createGeneratorBuilder()
                .treatGeneratedAsUnannotated(true)
                .acknowledgeRestrictiveAnnotations(false)
                .checkOptionalEmptiness(true)
                .suggestSuppressions(false)
                .assertsEnabled(true)
                .build();
        List<String> args = generator.generate();

        assertTrue("Should contain TreatGeneratedAsUnannotated=true",
                args.contains("-XepOpt:NullAway:TreatGeneratedAsUnannotated=true"));
        assertTrue("Should contain AcknowledgeRestrictiveAnnotations=false",
                args.contains("-XepOpt:NullAway:AcknowledgeRestrictiveAnnotations=false"));
        assertTrue("Should contain CheckOptionalEmptiness=true",
                args.contains("-XepOpt:NullAway:CheckOptionalEmptiness=true"));
        assertTrue("Should contain SuggestSuppressions=false",
                args.contains("-XepOpt:NullAway:SuggestSuppressions=false"));
        assertTrue("Should contain AssertsEnabled=true",
                args.contains("-XepOpt:NullAway:AssertsEnabled=true"));
    }

    @Test
    public void testStringOptionNonEmpty() {
        ArgumentGenerator generator = createGeneratorBuilder()
                .castToNonNullMethod("com.example.Utils.requireNonNull")
                .build();
        List<String> args = generator.generate();
        assertTrue("Should contain CastToNonNullMethod",
                args.contains("-XepOpt:NullAway:CastToNonNullMethod=com.example.Utils.requireNonNull"));
    }

    @Test
    public void testStringOptionEmpty() {
        ArgumentGenerator generator = createGeneratorBuilder()
                .castToNonNullMethod("")
                .build();
        List<String> args = generator.generate();
        assertFalse("Should not contain CastToNonNullMethod for empty string",
                containsOption(args, "CastToNonNullMethod"));
    }

    @Test
    public void testStringOptionNull() {
        ArgumentGenerator generator = createGeneratorBuilder()
                .castToNonNullMethod(null)
                .build();
        List<String> args = generator.generate();
        assertFalse("Should not contain CastToNonNullMethod for null",
                containsOption(args, "CastToNonNullMethod"));
    }

    @Test
    public void testListOptions() {
        ArgumentGenerator generator = createGeneratorBuilder()
                .unannotatedSubPackages(Arrays.asList("com.example.legacy"))
                .excludedClasses(Arrays.asList("com.example.Generated", "com.example.Test"))
                .knownInitializers(Collections.singletonList("com.example.Builder.init"))
                .build();
        List<String> args = generator.generate();

        assertTrue("Should contain UnannotatedSubPackages",
                args.contains("-XepOpt:NullAway:UnannotatedSubPackages=com.example.legacy"));
        assertTrue("Should contain ExcludedClasses",
                args.contains("-XepOpt:NullAway:ExcludedClasses=com.example.Generated,com.example.Test"));
        assertTrue("Should contain KnownInitializers",
                args.contains("-XepOpt:NullAway:KnownInitializers=com.example.Builder.init"));
    }

    @Test
    public void testAllOptionsConfigured() {
        ArgumentGenerator generator = createGeneratorBuilder()
                .annotatedPackages(Collections.singletonList("com.example"))
                .unannotatedSubPackages(Collections.singletonList("com.example.legacy"))
                .excludedClasses(Collections.singletonList("com.example.Generated"))
                .treatGeneratedAsUnannotated(true)
                .suggestSuppressions(true)
                .castToNonNullMethod("com.example.Utils.requireNonNull")
                .jspecifyMode(true)
                .build();
        List<String> args = generator.generate();

        // Should have severity + 7 options = 8 arguments
        assertEquals("Should have 8 arguments", 8, args.size());

        // Verify all are present
        assertTrue("Should contain severity", args.contains("-Xep:NullAway"));
        assertTrue("Should contain AnnotatedPackages",
                args.contains("-XepOpt:NullAway:AnnotatedPackages=com.example"));
        assertTrue("Should contain UnannotatedSubPackages",
                args.contains("-XepOpt:NullAway:UnannotatedSubPackages=com.example.legacy"));
        assertTrue("Should contain ExcludedClasses",
                args.contains("-XepOpt:NullAway:ExcludedClasses=com.example.Generated"));
        assertTrue("Should contain TreatGeneratedAsUnannotated",
                args.contains("-XepOpt:NullAway:TreatGeneratedAsUnannotated=true"));
        assertTrue("Should contain SuggestSuppressions",
                args.contains("-XepOpt:NullAway:SuggestSuppressions=true"));
        assertTrue("Should contain CastToNonNullMethod",
                args.contains("-XepOpt:NullAway:CastToNonNullMethod=com.example.Utils.requireNonNull"));
        assertTrue("Should contain JSpecifyMode",
                args.contains("-XepOpt:NullAway:JSpecifyMode=true"));
    }

    @Test
    public void testMinimalConfiguration() {
        ArgumentGenerator generator = createGeneratorBuilder().build();
        List<String> args = generator.generate();

        // Should only have severity
        assertEquals("Should have 1 argument (severity only)", 1, args.size());
        assertTrue("Should contain severity", args.contains("-Xep:NullAway"));
    }

    @Test
    public void testCustomAnnotations() {
        ArgumentGenerator generator = createGeneratorBuilder()
                .customNullableAnnotations(Arrays.asList("com.example.Nullable", "org.custom.Nullable"))
                .customNonnullAnnotations(Collections.singletonList("com.example.NonNull"))
                .customContractAnnotations(Collections.singletonList("com.example.Contract"))
                .build();
        List<String> args = generator.generate();

        assertTrue("Should contain CustomNullableAnnotations",
                args.contains("-XepOpt:NullAway:CustomNullableAnnotations=com.example.Nullable,org.custom.Nullable"));
        assertTrue("Should contain CustomNonnullAnnotations",
                args.contains("-XepOpt:NullAway:CustomNonnullAnnotations=com.example.NonNull"));
        assertTrue("Should contain CustomContractAnnotations",
                args.contains("-XepOpt:NullAway:CustomContractAnnotations=com.example.Contract"));
    }

    // Helper methods

    private ArgumentGenerator createGenerator(String severity) {
        return new ArgumentGenerator(
                severity, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null
        );
    }

    private GeneratorBuilder createGeneratorBuilder() {
        return new GeneratorBuilder();
    }

    private boolean containsOption(List<String> args, String optionName) {
        String prefix = "-XepOpt:NullAway:" + optionName + "=";
        for (String arg : args) {
            if (arg.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Builder for ArgumentGenerator to make tests more readable.
     */
    private static class GeneratorBuilder {
        private String severity = "DEFAULT";
        private Boolean onlyNullMarked;
        private List<String> annotatedPackages;
        private List<String> unannotatedSubPackages;
        private List<String> unannotatedClasses;
        private List<String> knownInitializers;
        private List<String> excludedClassAnnotations;
        private List<String> excludedClasses;
        private List<String> excludedFieldAnnotations;
        private List<String> customInitializerAnnotations;
        private List<String> externalInitAnnotations;
        private Boolean treatGeneratedAsUnannotated;
        private Boolean acknowledgeRestrictiveAnnotations;
        private Boolean checkOptionalEmptiness;
        private Boolean suggestSuppressions;
        private Boolean assertsEnabled;
        private Boolean exhaustiveOverride;
        private String castToNonNullMethod;
        private List<String> checkOptionalEmptinessCustomClasses;
        private String autoFixSuppressionComment;
        private Boolean handleTestAssertionLibraries;
        private Boolean acknowledgeAndroidRecent;
        private Boolean checkContracts;
        private List<String> customContractAnnotations;
        private List<String> customNullableAnnotations;
        private List<String> customNonnullAnnotations;
        private List<String> customGeneratedCodeAnnotations;
        private Boolean jspecifyMode;
        private List<String> extraFuturesClasses;
        private List<String> suppressionNameAliases;

        GeneratorBuilder severity(String severity) {
            this.severity = severity;
            return this;
        }

        GeneratorBuilder onlyNullMarked(Boolean onlyNullMarked) {
            this.onlyNullMarked = onlyNullMarked;
            return this;
        }

        GeneratorBuilder annotatedPackages(List<String> annotatedPackages) {
            this.annotatedPackages = annotatedPackages;
            return this;
        }

        GeneratorBuilder unannotatedSubPackages(List<String> unannotatedSubPackages) {
            this.unannotatedSubPackages = unannotatedSubPackages;
            return this;
        }

        GeneratorBuilder unannotatedClasses(List<String> unannotatedClasses) {
            this.unannotatedClasses = unannotatedClasses;
            return this;
        }

        GeneratorBuilder knownInitializers(List<String> knownInitializers) {
            this.knownInitializers = knownInitializers;
            return this;
        }

        GeneratorBuilder excludedClasses(List<String> excludedClasses) {
            this.excludedClasses = excludedClasses;
            return this;
        }

        GeneratorBuilder treatGeneratedAsUnannotated(Boolean treatGeneratedAsUnannotated) {
            this.treatGeneratedAsUnannotated = treatGeneratedAsUnannotated;
            return this;
        }

        GeneratorBuilder acknowledgeRestrictiveAnnotations(Boolean acknowledgeRestrictiveAnnotations) {
            this.acknowledgeRestrictiveAnnotations = acknowledgeRestrictiveAnnotations;
            return this;
        }

        GeneratorBuilder checkOptionalEmptiness(Boolean checkOptionalEmptiness) {
            this.checkOptionalEmptiness = checkOptionalEmptiness;
            return this;
        }

        GeneratorBuilder suggestSuppressions(Boolean suggestSuppressions) {
            this.suggestSuppressions = suggestSuppressions;
            return this;
        }

        GeneratorBuilder assertsEnabled(Boolean assertsEnabled) {
            this.assertsEnabled = assertsEnabled;
            return this;
        }

        GeneratorBuilder castToNonNullMethod(String castToNonNullMethod) {
            this.castToNonNullMethod = castToNonNullMethod;
            return this;
        }

        GeneratorBuilder jspecifyMode(Boolean jspecifyMode) {
            this.jspecifyMode = jspecifyMode;
            return this;
        }

        GeneratorBuilder customNullableAnnotations(List<String> customNullableAnnotations) {
            this.customNullableAnnotations = customNullableAnnotations;
            return this;
        }

        GeneratorBuilder customNonnullAnnotations(List<String> customNonnullAnnotations) {
            this.customNonnullAnnotations = customNonnullAnnotations;
            return this;
        }

        GeneratorBuilder customContractAnnotations(List<String> customContractAnnotations) {
            this.customContractAnnotations = customContractAnnotations;
            return this;
        }

        ArgumentGenerator build() {
            return new ArgumentGenerator(
                    severity, onlyNullMarked, annotatedPackages, unannotatedSubPackages,
                    unannotatedClasses, knownInitializers, excludedClassAnnotations,
                    excludedClasses, excludedFieldAnnotations, customInitializerAnnotations,
                    externalInitAnnotations, treatGeneratedAsUnannotated,
                    acknowledgeRestrictiveAnnotations, checkOptionalEmptiness,
                    suggestSuppressions, assertsEnabled, exhaustiveOverride,
                    castToNonNullMethod, checkOptionalEmptinessCustomClasses,
                    autoFixSuppressionComment, handleTestAssertionLibraries,
                    acknowledgeAndroidRecent, checkContracts, customContractAnnotations,
                    customNullableAnnotations, customNonnullAnnotations,
                    customGeneratedCodeAnnotations, jspecifyMode, extraFuturesClasses,
                    suppressionNameAliases
            );
        }
    }
}
