# NullAway Maven Plugin

A Maven plugin for configuring [NullAway](https://github.com/uber/NullAway) static analysis with [Error Prone](https://errorprone.info/).

NullAway is a fast annotation-based null-checking tool that helps eliminate `NullPointerException`s (NPEs) in Java code. This Maven plugin provides a convenient way to configure NullAway options in your Maven build.

## Features

- **Declarative Configuration**: Configure all NullAway options directly in your `pom.xml`
- **Feature Parity**: Supports all 33+ configuration options from the Gradle plugin
- **Validation**: Built-in configuration validation to catch errors early
- **Maven Integration**: Seamlessly integrates with maven-compiler-plugin and Error Prone

## Requirements

- Maven 3.6.3 or later
- Java 8 or later
- maven-compiler-plugin with Error Prone and NullAway configured

## Quick Start

### 1. Add Error Prone and NullAway to maven-compiler-plugin

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.14.1</version>
    <configuration>
        <compilerArgs>
            <arg>-XDcompilePolicy=simple</arg>
            <arg>-Xplugin:ErrorProne</arg>
        </compilerArgs>
        <annotationProcessorPaths>
            <path>
                <groupId>com.google.errorprone</groupId>
                <artifactId>error_prone_core</artifactId>
                <version>2.45.0</version>
            </path>
            <path>
                <groupId>com.uber.nullaway</groupId>
                <artifactId>nullaway</artifactId>
                <version>0.12.14</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

### 2. Add the NullAway Maven Plugin

```xml
<plugin>
    <groupId>net.ltgt.maven</groupId>
    <artifactId>nullaway-maven-plugin</artifactId>
    <version>1.0.0</version>
    <executions>
        <execution>
            <goals>
                <goal>configure</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <severity>ERROR</severity>
        <annotatedPackages>
            <package>com.example.myapp</package>
        </annotatedPackages>
    </configuration>
</plugin>
```

### 3. Build your project

```bash
mvn clean compile
```

## Configuration Options

The plugin supports all NullAway configuration options. Configure them in the `<configuration>` section of the plugin.

### Core Configuration

#### severity

The severity of the NullAway check. Valid values: `OFF`, `WARN`, `ERROR`, `DEFAULT`

- `OFF` - Disable NullAway
- `WARN` - Enable NullAway as warnings
- `ERROR` - Enable NullAway as errors (fails build)
- `DEFAULT` - Enable NullAway with default Error Prone severity

```xml
<severity>ERROR</severity>
```

#### annotatedPackages

**Required** (unless `onlyNullMarked` is set to `true`)

The list of packages that should be considered properly annotated according to the NullAway convention.

```xml
<annotatedPackages>
    <package>com.example.myapp</package>
    <package>com.example.library</package>
</annotatedPackages>
```

#### onlyNullMarked

Indicates that NullAway should only treat `@NullMarked` code as annotated, in accordance with the JSpecify specification.

If this is set to `true`, then `annotatedPackages` must be empty.

```xml
<onlyNullMarked>true</onlyNullMarked>
```

### Package and Class Exclusions

#### unannotatedSubPackages

A list of subpackages to be excluded from the `annotatedPackages` list.

```xml
<unannotatedSubPackages>
    <package>com.example.myapp.legacy</package>
    <package>com.example.myapp.generated</package>
</unannotatedSubPackages>
```

#### unannotatedClasses

A list of classes within annotated packages that should be treated as unannotated.

```xml
<unannotatedClasses>
    <class>com.example.myapp.LegacyClass</class>
</unannotatedClasses>
```

#### excludedClasses

A list of classes to be excluded from the nullability analysis.

```xml
<excludedClasses>
    <class>com.example.Generated</class>
</excludedClasses>
```

#### excludedClassAnnotations

A list of annotations that cause classes to be excluded from nullability analysis.

```xml
<excludedClassAnnotations>
    <annotation>com.example.NullAwayExcluded</annotation>
</excludedClassAnnotations>
```

#### excludedFieldAnnotations

A list of annotations that cause fields to be excluded from being checked for proper initialization.

```xml
<excludedFieldAnnotations>
    <annotation>javax.ws.rs.core.Context</annotation>
    <annotation>javax.inject.Inject</annotation>
</excludedFieldAnnotations>
```

### Initializers

#### knownInitializers

The fully qualified name of methods from third-party libraries that NullAway should treat as initializers.

```xml
<knownInitializers>
    <method>com.foo.Bar.init</method>
</knownInitializers>
```

#### customInitializerAnnotations

A list of annotations that should be considered equivalent to `@Initializer` annotations.

```xml
<customInitializerAnnotations>
    <annotation>com.example.Init</annotation>
</customInitializerAnnotations>
```

#### externalInitAnnotations

A list of annotations for classes that are "externally initialized."

```xml
<externalInitAnnotations>
    <annotation>com.example.ExternalInit</annotation>
</externalInitAnnotations>
```

### Behavioral Options

#### treatGeneratedAsUnannotated

If set to `true`, NullAway treats any class annotated with `@Generated` as if its APIs are unannotated.

```xml
<treatGeneratedAsUnannotated>true</treatGeneratedAsUnannotated>
```

#### acknowledgeRestrictiveAnnotations

If set to `true`, NullAway will acknowledge nullability annotations whenever they are available in unannotated code and also more restrictive than its optimistic defaults.

```xml
<acknowledgeRestrictiveAnnotations>true</acknowledgeRestrictiveAnnotations>
```

#### checkOptionalEmptiness

If set to `true`, NullAway will check for `.get()` accesses to potentially empty `Optional` values.

```xml
<checkOptionalEmptiness>true</checkOptionalEmptiness>
```

#### suggestSuppressions

If set to `true`, NullAway will use Error Prone's suggested fix functionality to suggest suppressing any warning that it finds.

```xml
<suggestSuppressions>true</suggestSuppressions>
```

#### assertsEnabled

If set to `true`, NullAway will handle assertions and use that to reason about the possibility of null dereferences.

This assumes that assertions will always be enabled at runtime (java run with `-ea` JVM argument).

```xml
<assertsEnabled>true</assertsEnabled>
```

#### exhaustiveOverride

If set to `true`, NullAway will check every method to see whether it overrides a method of a super-type, rather than relying only on the `@Override` annotation.

```xml
<exhaustiveOverride>true</exhaustiveOverride>
```

#### handleTestAssertionLibraries

If set to `true`, NullAway will handle assertions from test libraries, like `assertThat(...).isNotNull()`.

```xml
<handleTestAssertionLibraries>true</handleTestAssertionLibraries>
```

#### acknowledgeAndroidRecent

If set to `true`, treats `@RecentlyNullable` as `@Nullable`, and `@RecentlyNonNull` as `@NonNull`.

**Note**: Requires `acknowledgeRestrictiveAnnotations` to also be set to `true`.

```xml
<acknowledgeRestrictiveAnnotations>true</acknowledgeRestrictiveAnnotations>
<acknowledgeAndroidRecent>true</acknowledgeAndroidRecent>
```

#### checkContracts

If set to `true`, NullAway will check `@Contract` annotations.

```xml
<checkContracts>true</checkContracts>
```

#### jspecifyMode

If set to `true`, enables new checks based on JSpecify (like checks for generic types).

```xml
<jspecifyMode>true</jspecifyMode>
```

### Custom Classes and Methods

#### castToNonNullMethod

The fully qualified name of a method to be used for downcasting to a non-null value.

```xml
<castToNonNullMethod>com.example.Utils.requireNonNull</castToNonNullMethod>
```

#### checkOptionalEmptinessCustomClasses

A list of classes to be treated as `Optional` implementations (e.g., Guava's `com.google.common.base.Optional`).

```xml
<checkOptionalEmptinessCustomClasses>
    <class>com.google.common.base.Optional</class>
</checkOptionalEmptinessCustomClasses>
```

#### extraFuturesClasses

A list of classes to be treated equivalently to Guava `Futures` and `FluentFuture`.

```xml
<extraFuturesClasses>
    <class>com.example.CustomFuture</class>
</extraFuturesClasses>
```

### Custom Annotations

#### customContractAnnotations

A list of annotations that should be considered equivalent to `@Contract` annotations.

```xml
<customContractAnnotations>
    <annotation>com.example.Contract</annotation>
</customContractAnnotations>
```

#### customNullableAnnotations

A list of annotations that should be considered equivalent to `@Nullable` annotations.

```xml
<customNullableAnnotations>
    <annotation>com.example.Nullable</annotation>
    <annotation>org.custom.Nullable</annotation>
</customNullableAnnotations>
```

#### customNonnullAnnotations

A list of annotations that should be considered equivalent to `@NonNull` annotations.

```xml
<customNonnullAnnotations>
    <annotation>com.example.NonNull</annotation>
</customNonnullAnnotations>
```

#### customGeneratedCodeAnnotations

A list of annotations that should be considered equivalent to `@Generated` annotations.

```xml
<customGeneratedCodeAnnotations>
    <annotation>com.example.Generated</annotation>
</customGeneratedCodeAnnotations>
```

### Other Options

#### autoFixSuppressionComment

A comment that will be added alongside the `@SuppressWarnings("NullAway")` annotation when `suggestSuppressions` is set to `true`.

```xml
<autoFixSuppressionComment>TODO: fix nullability issue</autoFixSuppressionComment>
```

#### suppressionNameAliases

A list of names to suppress NullAway using a `@SuppressWarnings` annotation, similar to `@SuppressWarnings("NullAway")`.

This is useful when other warnings are already suppressed in the codebase.

```xml
<suppressionNameAliases>
    <name>DataFlowIssue</name>
</suppressionNameAliases>
```

## Common Usage Examples

### Basic Configuration

```xml
<plugin>
    <groupId>net.ltgt.maven</groupId>
    <artifactId>nullaway-maven-plugin</artifactId>
    <version>1.0.0</version>
    <executions>
        <execution>
            <goals>
                <goal>configure</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <severity>ERROR</severity>
        <annotatedPackages>
            <package>com.example.myapp</package>
        </annotatedPackages>
    </configuration>
</plugin>
```

### Strict Configuration with Suggestions

```xml
<configuration>
    <severity>ERROR</severity>
    <annotatedPackages>
        <package>com.example.myapp</package>
    </annotatedPackages>
    <suggestSuppressions>true</suggestSuppressions>
    <treatGeneratedAsUnannotated>true</treatGeneratedAsUnannotated>
    <checkOptionalEmptiness>true</checkOptionalEmptiness>
</configuration>
```

### JSpecify Mode

```xml
<configuration>
    <severity>ERROR</severity>
    <onlyNullMarked>true</onlyNullMarked>
    <jspecifyMode>true</jspecifyMode>
</configuration>
```

### With Legacy Code Exclusions

```xml
<configuration>
    <severity>ERROR</severity>
    <annotatedPackages>
        <package>com.example.myapp</package>
    </annotatedPackages>
    <unannotatedSubPackages>
        <package>com.example.myapp.legacy</package>
        <package>com.example.myapp.generated</package>
    </unannotatedSubPackages>
    <excludedFieldAnnotations>
        <annotation>javax.inject.Inject</annotation>
        <annotation>javax.ws.rs.core.Context</annotation>
    </excludedFieldAnnotations>
</configuration>
```

### Skip NullAway for Test Code

```xml
<executions>
    <execution>
        <id>main</id>
        <goals>
            <goal>configure</goal>
        </goals>
        <configuration>
            <severity>ERROR</severity>
            <annotatedPackages>
                <package>com.example.myapp</package>
            </annotatedPackages>
        </configuration>
    </execution>
    <execution>
        <id>test</id>
        <goals>
            <goal>configure</goal>
        </goals>
        <phase>process-test-classes</phase>
        <configuration>
            <severity>OFF</severity>
        </configuration>
    </execution>
</executions>
```

## Migrating from the Gradle Plugin

The Maven plugin provides the same functionality as the [Gradle plugin](https://github.com/tbroyer/gradle-nullaway-plugin). The main differences:

1. **Configuration syntax**: Maven uses XML instead of Groovy/Kotlin DSL
2. **Lists**: Use `<package>`, `<class>`, `<annotation>`, etc. tags instead of arrays
3. **Booleans**: Use `<option>true</option>` instead of `option = true`

### Gradle Configuration

```kotlin
nullaway {
    severity = CheckSeverity.ERROR
    annotatedPackages.add("com.example")
    treatGeneratedAsUnannotated = true
}
```

### Equivalent Maven Configuration

```xml
<configuration>
    <severity>ERROR</severity>
    <annotatedPackages>
        <package>com.example</package>
    </annotatedPackages>
    <treatGeneratedAsUnannotated>true</treatGeneratedAsUnannotated>
</configuration>
```

## Multi-Module Projects

Configure the plugin in the parent POM using `<pluginManagement>`:

```xml
<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <groupId>net.ltgt.maven</groupId>
                <artifactId>nullaway-maven-plugin</artifactId>
                <version>1.0.0</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>configure</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <severity>ERROR</severity>
                    <annotatedPackages>
                        <package>com.example</package>
                    </annotatedPackages>
                </configuration>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
```

Child modules inherit the configuration and can override as needed:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>net.ltgt.maven</groupId>
            <artifactId>nullaway-maven-plugin</artifactId>
            <configuration>
                <!-- Override parent configuration -->
                <severity>WARN</severity>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## Troubleshooting

### "maven-compiler-plugin not found"

Make sure you have maven-compiler-plugin configured in your POM:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.14.1</version>
</plugin>
```

### "Must specify either onlyNullMarked=true OR annotatedPackages"

NullAway requires either `onlyNullMarked` or `annotatedPackages` to be configured:

```xml
<!-- Option 1: Traditional annotation model -->
<annotatedPackages>
    <package>com.example</package>
</annotatedPackages>

<!-- Option 2: JSpecify @NullMarked model -->
<onlyNullMarked>true</onlyNullMarked>
```

### NullAway Not Running

1. Verify Error Prone is configured correctly in maven-compiler-plugin
2. Check that you have the correct annotationProcessorPaths
3. Enable Maven debug logging: `mvn -X compile`
4. Look for "NullAway configured with N arguments" in the output

### Build Fails with NullAway Errors

This is expected when `severity` is set to `ERROR`. To temporarily disable:

```bash
mvn compile -Dnullaway.severity=OFF
```

Or change configuration to `WARN` to see warnings without failing the build:

```xml
<severity>WARN</severity>
```

## Building from Source

```bash
cd maven-nullaway-plugin
mvn clean install
```

## License

Licensed under the Apache License, Version 2.0. See LICENSE file for details.

## Resources

- [NullAway GitHub](https://github.com/uber/NullAway)
- [NullAway Configuration Guide](https://github.com/uber/NullAway/wiki/Configuration)
- [Error Prone](https://errorprone.info/)
- [JSpecify](https://jspecify.dev/)
- [Gradle NullAway Plugin](https://github.com/tbroyer/gradle-nullaway-plugin)
