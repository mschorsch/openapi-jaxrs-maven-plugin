/*
 * Copyright 2017 Matthias Schorsch.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mschorsch.plugins;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import io.swagger.jaxrs.Reader;
import io.swagger.models.Swagger;
import io.swagger.util.Json;
import io.swagger.util.Yaml;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.COMPILE,
        requiresDependencyResolution = ResolutionScope.RUNTIME)
public class SwaggerGeneratorMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    //**************************************************************************
    /**
     * Provides metadata about the API. The metadata can be used by the clients
     * if needed. https://swagger.io/specification/#infoObject
     */
    @Parameter
    private Info info;

    /**
     * The host (name or ip) serving the API. This MUST be the host only and
     * does not include the scheme nor sub-paths. It MAY include a port. If the
     * host is not included, the host serving the documentation is to be used
     * (including the port). The host does not support path templating.
     * https://swagger.io/specification
     */
    @Parameter
    private String host;

    /**
     * The base path on which the API is served, which is relative to the host.
     * If it is not included, the API is served directly under the host. The
     * value MUST start with a leading slash (/). The basePath does not support
     * path templating. https://swagger.io/specification
     */
    @Parameter
    private String basePath;

    /**
     * The transfer protocol of the API. Values MUST be from the list: "http",
     * "https", "ws", "wss". If the schemes is not included, the default scheme
     * to be used is the one used to access the Swagger definition itself.
     * https://swagger.io/specification
     */
    @Parameter
    private List<Scheme> schemes;

    /**
     * A list of MIME types the APIs can consume. This is global to all APIs but
     * can be overridden on specific API calls. Value MUST be as described under
     * Mime Types. https://swagger.io/specification
     * https://swagger.io/specification/#mimeTypes
     */
    @Parameter
    private List<String> consumes;

    /**
     * A list of MIME types the APIs can produce. This is global to all APIs but
     * can be overridden on specific API calls. Value MUST be as described under
     * Mime Types. https://swagger.io/specification
     * https://swagger.io/specification/#mimeTypes
     */
    @Parameter
    private List<String> produces;

    // paths
    // definitions
    // parameters
    // responses
    // securityDefinitions
    // security
    // tags
    // externalDocs
    //**************************************************************************
    /**
     * List of JAX-RS resource classes. Annotated with @Api.
     *
     * Example:
     *
     * <pre>
     * &lt;apiSources&gt;
     *  &lt;apiSource&gt;com.example.MyClass&lt;/apiSource&gt;
     *  &lt;apiSource&gt;...&lt;/apiSource&gt;
     * &lt;/apiSources&gt;
     * </pre>
     */
    @Parameter
    private List<String> apiSources = new ArrayList<>();

    /**
     * List of JAX-RS resource packages.
     *
     * Example:
     *
     * <pre>
     * &lt;apiPackages&gt;
     *  &lt;apiPackage&gt;com.example&lt;/apiPackage&gt;
     *  &lt;apiPackage&gt;...&lt;/apiPackage&gt;
     * &lt;/apiPackages&gt;
     * </pre>
     */
    @Parameter
    private List<String> apiPackages = new ArrayList<>();

    /**
     * Generated format (YAML,JSON).
     */
    @Parameter(defaultValue = "YAML")
    private FileFormat generatedFormat;

    /**
     * Generated filename.
     */
    @Parameter
    private String filename;

    /**
     * Output directory (Default: "${basedir}/target")
     */
    @Parameter(defaultValue = "${basedir}/target")
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException {
        final Set<Class<?>> classes = new HashSet<>();

        // 1. Fixed Fields (https://swagger.io/specification)
        Swagger swagger = createDefaultSwagger();

        // 2. Read api classes
        getLog().info("Reading api classes ...");
        classes.addAll(initApiClasses());

        if (classes.isEmpty()) {
            getLog().info("No api classes defined.");

        } else {
            // 3. Parse classes
            getLog().info("Parsing api classes ...");
            final Reader apiClazzReader = new Reader(swagger);
            swagger = apiClazzReader.read(classes);
        }

        // 4. Flush out
        getLog().info("Creating swagger file ...");
        writeSwaggerFile(swagger);

        getLog().info("Swagger file creation successfull.");
    }

    private Swagger createDefaultSwagger() {
        return new Swagger()
                .info(info != null ? info.toSwaggerInfo() : null)
                .host(host)
                .basePath(basePath)
                .schemes(schemes != null ? getSwaggerSchemes() : null)
                .consumes(consumes)
                .produces(produces);
    }

    private List<io.swagger.models.Scheme> getSwaggerSchemes() {
        final List<io.swagger.models.Scheme> ret = new ArrayList<>();
        for (Scheme scheme : schemes) {
            ret.add(scheme.toSwaggerScheme());
        }
        return ret;
    }

    private Set<Class<?>> initApiClasses() throws MojoExecutionException {
        final Set<Class<?>> ret = new HashSet<>();

        try {
            final URLClassLoader urlClassLoaderloader = initCLassLoader();

            for (String apiSource : apiSources) {
                getLog().info("-> Found '" + apiSource + "'");
                ret.add(urlClassLoaderloader.loadClass(apiSource));
            }

            for (String apiPackage : apiPackages) {
                final ImmutableSet<ClassInfo> classInfos = ClassPath.from(urlClassLoaderloader).getTopLevelClasses(apiPackage);
                for (ClassInfo classInfo : classInfos) {
                    getLog().info("-> Found '" + classInfo.getSimpleName() + "' in package " + apiPackage);
                    ret.add(classInfo.load());
                }
            }

        } catch (IOException | ClassNotFoundException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        } catch (DependencyResolutionRequiredException ex) {
            throw new MojoExecutionException("Dependency resolution failed", ex);
        }

        return ret;
    }

    private URLClassLoader initCLassLoader() throws DependencyResolutionRequiredException, MojoExecutionException {
        final List<URL> projectClasspathList = new ArrayList<>();
        for (String runtimeElement : project.getRuntimeClasspathElements()) {
            try {
                projectClasspathList.add(new File(runtimeElement).toURI().toURL());
            } catch (MalformedURLException e) {
                throw new MojoExecutionException(runtimeElement + " is an invalid classpath element", e);
            }
        }

        for (String compileElement : project.getCompileClasspathElements()) {
            try {
                projectClasspathList.add(new File(compileElement).toURI().toURL());
            } catch (MalformedURLException e) {
                throw new MojoExecutionException(compileElement + " is an invalid classpath element", e);
            }
        }

        return new URLClassLoader(projectClasspathList.toArray(new URL[0]),
                Thread.currentThread().getContextClassLoader());
    }

    private void writeSwaggerFile(Swagger swagger) throws MojoExecutionException {
        if (outputDirectory == null) {
            throw new MojoExecutionException("'outputDirectory' not set");
        } else if (!outputDirectory.isDirectory()) {
            throw new MojoExecutionException(String.format("'%s' is not a directory", outputDirectory));
        }

        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdir()) {
                throw new MojoExecutionException(String.format("Could not create outputDirectory '%s'", outputDirectory));
            }
        }

        try {
            switch (generatedFormat) {
                case YAML: {
                    final File yamlFile = new File(outputDirectory.getAbsolutePath(), MoreObjects.firstNonNull(filename, "swagger.yaml"));
                    Yaml.mapper().writeValue(yamlFile, swagger);
                    break;
                }

                case JSON: {
                    final File jsonFile = new File(outputDirectory.getAbsolutePath(), MoreObjects.firstNonNull(filename, "swagger.json"));
                    Json.mapper().writeValue(jsonFile, swagger);
                    break;
                }

                default: {
                    throw new MojoExecutionException(String.format("unknown file format '%s'", generatedFormat));
                }
            }
        } catch (IOException ex) {
            throw new MojoExecutionException("Could not generate swagger file.", ex);
        }
    }
}
