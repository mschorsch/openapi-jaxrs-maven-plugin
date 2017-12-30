# Swagger JAX-RS Maven Plugin
This plugin generates a swagger file from JAX-RS resource annotations.

# Usage
```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github.mschorsch.plugins</groupId>
            <artifactId>openapi2-jaxrs-maven-plugin</artifactId>
            <version>1.0-SNAPSHOT</version>
            <configuration>
                <info>
                    <title>Fire,Fire,Fire!!</title>
                    <description>Description,Description</description>
                    <termsOfService>Always usable</termsOfService>
                    <contact>
                        <name>Hermann Mustermann</name>
                        <url>http://example.com</url>
                        <email>mustermann@example.com</email>
                    </contact>
                    <license>
                        <name>Apache 2.0</name>
                        <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>                            
                    </license>
                    <version>1.0</version>
                </info>
                <host>fire.example.com</host>
                <basePath>/api</basePath>
                <schemes>
                    <scheme>http</scheme>
                    <scheme>https</scheme>
                </schemes>
                <consumes>
                    <consume>application/json</consume>
                </consumes>
                <produces>
                    <produce>application/json</produce>
                </produces>
                <apiPackages>
                    <apiPackage>com.github.swagger.jaxrs.maven.test</apiPackage>
                </apiPackages>
                <apiSources>
                    <apiSource>com.github.swagger.jaxrs.maven.test.SwaggerInfo</apiSource>
                    <apiSource>com.github.swagger.jaxrs.maven.test.FireResource</apiSource>
                </apiSources>
                <generatedFormat>YAML</generatedFormat>
                <outputDirectory>${basedir}/target</outputDirectory>
                <filename>swagger.yaml</filename>
            </configuration>
            <executions>
                <execution>
                    <phase>compile</phase>
                    <goals>
                        <goal>generate</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>

<dependencies>
    <dependency>
        <groupId>io.swagger</groupId>
        <artifactId>swagger-jaxrs</artifactId>
        <version>1.5.15</version>
    </dependency>
</dependencies>
```