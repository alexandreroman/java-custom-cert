/*
 * Copyright (c) 2023 VMware, Inc. or its affiliates
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

package com.vmware.tanzu.demos.javacustomcert;

import nl.altindag.ssl.SSLFactory;
import nl.altindag.ssl.util.PemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException {
        LOGGER.info("Loading custom certificate");
        // Load the custom certificate from a file.
        final var tm = PemUtils.loadTrustMaterial(Main.class.getResourceAsStream("/httpbin.org.pem"));
        // Create a SSLContext using the custom certificate.
        final var sslContext = SSLFactory.builder()
                .withTrustMaterial(tm)
                .build()
                .getSslContext();

        // Create a HTTP client.
        final var client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(30))
                .followRedirects(HttpClient.Redirect.NORMAL)
                // Enable the custom certificate through SSLContext.
                .sslContext(sslContext)
                .build();

        LOGGER.info("Connecting to httpbin.org");
        final var req = HttpRequest.newBuilder().uri(URI.create("https://httpbin.org/status/200")).build();
        final var resp = client.send(req, HttpResponse.BodyHandlers.discarding());
        LOGGER.info("Received HTTP response: {}", resp.statusCode());
    }
}
