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

import org.apache.maven.plugins.annotations.Parameter;

/**
 *
 * @author Matthias Schorsch
 */
public class License {

    /**
     * Required. The license name used for the API.
     * https://swagger.io/specification/#licenseObject
     */
    @Parameter
    private String name;

    /**
     * A URL to the license used for the API. MUST be in the format of a URL.
     * https://swagger.io/specification/#licenseObject
     */
    @Parameter
    private String url;

    public License() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public io.swagger.models.License toSwaggerLicense() {
        return new io.swagger.models.License()
                .name(name)
                .url(url);
    }
}
