# Triaforce

A simplistic, fast, and lightweight trial manager service that exposes a simple HTTP API for clients
to use. It's written in Kotlin using Quarkus as the server framework and RocksDB for data storage.

## Endpoint

- A *project* is a unique, alphanumeric identifier that determines a project that a trial is for.
- An *ID* is a unique identifier that determines the client that has a trial.

Triaforce exposes a single primary endpoint returning JSON at `/status/PROJECT/ID`, replacing
*PROJECT* with the project and *ID* with the client ID. This returns a JSON response looking
like this:

```json
{
  "active": true,
  "expiration": "2020-01-01T00:00:00"
}
```

*active* determines if the trial is active (has not expired), and *expiration* is a date
in ISO 8601 format, without an explicit time zone. (The time is always in UTC.)

Therefore, to use the API, simply choose a project name and gather some proper, unique ID from the
client (e.g. `Settings.Secure.ANDROID_ID`), then call the endpoint. If a trial was not active for
the particular client, it will immediately be started.

## Building

A Dockerfile is available in the root directory that will build and run Triaforce using GraalVM.
If you want to build it from scratch, follow the commands the Dockerfile runs, as well as the
[standard Quarkus native binary directions](https://quarkus.io/guides/building-native-image). To
summarize:

- Set `GRAALVM_HOME=` to the GraalVM installation directory.
- Run `./mvnw package -Pnative -Dmaven.test.skip=true`. The output binary will be in the `target/`
  directory and end with `-runner`.
- Grab the RocksDB shared binary from the JAR file (see the Dockerfile's `unzip` command),
  and make sure it's in the same folder as the native server binary.
  
## Configuration

Create a `config/` directory inside the same directory as the application binary (this should
be `/app/config` for the Docker image) and create a file inside named `application.properties`.
These are the properties that are most relevant (default values are included):

```properties
# Path to a folder to store the RocksDB database files.
database.path=database
# Duration of the trial, in Java's Period string format:
# https://docs.oracle.com/javase/8/docs/api/java/time/Period.html#parse-java.lang.CharSequence-
trial.duration=P2D
# Comma-separated list of project names; any project names used not in this list will
# result in a 404 error.
trial.projects=default
# Port to run the web server on.
quarkus.http.port=8080
```

You can also

## Development

You can run the application in dev mode, which also supports hot reload, for development:

```
./mvnw quarkus:dev
```

Development is done in IntelliJ IDEA, which has the code styles all set inside.

