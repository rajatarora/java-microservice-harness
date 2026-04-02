This project includes a multistage Dockerfile to build and run the application as a JVM JAR.

- `Dockerfile` — builds a runnable JAR using the Gradle wrapper and packages it into an Alpine-based JRE image.

Build the JAR image:

```bash
docker build -t harness:jar -f Dockerfile .
docker run --rm -p8080:8080 harness:jar
```
