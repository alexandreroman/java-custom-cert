# Trusting a custom TLS certificate in plain Java

This project shows how to trust a custom TLS certificate in plain Java when
connecting to a remote service.

This app actually relies on the open source library
[SSLContext Kickstart](https://github.com/Hakky54/sslcontext-kickstart)
to load a custom certificate (`.pem` format), and a create a
[SSLContext](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/javax/net/ssl/class-use/SSLContext.html)
instance you can use with your HTTP client.

## How does it work?

Using SSLContext Kickstart, a few lines are required to load a custom certificate
and configure the JVM runtime:

```java
// Load the custom certificate from a file.
var tm = PemUtils.loadTrustMaterial(Main.class.getResourceAsStream("/httpbin.org.pem"));
// Create a SSLContext using the custom certificate.
var sslContext = SSLFactory.builder()
    .withTrustMaterial(tm).build().getSslContext();
```

At this point, you can use the `SSLContext` instance with your HTTP client:

```java
var client = HttpClient.newBuilder()
    .version(HttpClient.Version.HTTP_1_1)
    .connectTimeout(Duration.ofSeconds(30))
    .followRedirects(HttpClient.Redirect.NORMAL)
    // Enable the custom certificate through SSLContext.
    .sslContext(sslContext)
    .build();
```

## How to use it?

Build the app with Java 17+:

```shell
./mvnw package
```

The JAR file is available in the directory `target`.

You can now run the app:

```shell
java -jar target/java-custom-cert-1.0.0-SNAPSHOT.jar
```

You should get this command output:

```shell
18:06:04.204 [main] INFO com.vmware.tanzu.demos.javacustomcert.Main -- Loading custom certificate
18:06:04.373 [main] INFO com.vmware.tanzu.demos.javacustomcert.Main -- Connecting to httpbin.org
18:06:06.994 [main] INFO com.vmware.tanzu.demos.javacustomcert.Main -- Received HTTP response: 200
```

## Contribute

Contributions are always welcome!

Feel free to open issues & send PR.

## License

Copyright &copy; 2023 [VMware, Inc. or its affiliates](https://vmware.com).

This project is licensed under the [Apache Software License version 2.0](https://www.apache.org/licenses/LICENSE-2.0).
