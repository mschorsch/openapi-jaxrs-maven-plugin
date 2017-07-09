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
public class Contact {

    /**
     * The identifying name of the contact person/organization.
     * https://swagger.io/specification/#contactObject
     */
    @Parameter
    private String name;

    /**
     * The URL pointing to the contact information. MUST be in the format of a
     * URL. https://swagger.io/specification/#contactObject
     */
    @Parameter
    private String url;

    /**
     * The email address of the contact person/organization. MUST be in the
     * format of an email address.
     * https://swagger.io/specification/#contactObject
     */
    @Parameter
    private String email;

    public Contact() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public io.swagger.models.Contact toSwaggerContact() {
        return new io.swagger.models.Contact()
                .name(name)
                .url(url)
                .email(email);
    }
}
