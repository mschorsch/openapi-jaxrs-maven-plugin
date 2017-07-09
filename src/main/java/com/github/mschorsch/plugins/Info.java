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
public class Info {
    
    /**
     * The title of the application.
     * https://swagger.io/specification/#infoObject
     */
    @Parameter
    private String title;

    /**
     * A short description of the application.
     * GFM syntax can be used for rich text representation.
     * https://swagger.io/specification/#infoObject
     */
    @Parameter
    private String description;

     /**
     * The Terms of Service for the API.
     * https://swagger.io/specification/#infoObject
     */
    @Parameter
    private String termsOfService;

     /**
     * The contact information for the exposed API.
     * https://swagger.io/specification/#infoObject
     */
    @Parameter
    private Contact contact;

     /**
     * 	The license information for the exposed API.
     * https://swagger.io/specification/#infoObject
     */
    @Parameter
    private License license;

     /**
     * Provides the version of the application API (not to be confused with the specification version).
     * https://swagger.io/specification/#infoObject
     */
    @Parameter
    private String version;
    
    public Info() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTermsOfService() {
        return termsOfService;
    }

    public void setTermsOfService(String termsOfService) {
        this.termsOfService = termsOfService;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    
    public io.swagger.models.Info toSwaggerInfo() {
        return new io.swagger.models.Info()
                .title(title)
                .description(description)
                .termsOfService(termsOfService)
                .contact(contact != null ? contact.toSwaggerContact() : null)
                .license(license != null ? license.toSwaggerLicense() : null)
                .version(version);
    }
}
