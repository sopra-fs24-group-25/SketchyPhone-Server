# Sketchy Phone

This project aims to synthesise two classical games, namely the telephone game and the Exquisite Corpse game. First, participating users need to create a text prompt. Afterwards, each user will be shown another user’s text prompt and presented with a blank canvas on which they can draw the received text prompt. Then, the drawings are redistributed to users, which they have to describe in words, thus creating a new text prompt. This completes one of many cycles, each perturbing the original text prompt in some way. At the end, the cycles are shown to all users, showing the development from start to finish. This project works well as a web-application. Benefits are easy accessibility through web-browsers, low- barrier for entry (as only internet access is required), multiplayer capabilities and real-time synchronisation through web-services. The application will leverage external APIs, namely Web Speech API, Web Audio API and Canvas API.

## Technologies used
- Spring Boot (Docs: https://docs.spring.io/spring-boot/docs/current/reference/html/index.html)
- Gradle (Docs: https://gradle.org/docs/)
- Google Cloud (Deployment)

## Launch & Deployment

Download your IDE of choice (e.g., [IntelliJ](https://www.jetbrains.com/idea/download/), [Visual Studio Code](https://code.visualstudio.com/), or [Eclipse](http://www.eclipse.org/downloads/)). Make sure Java 17 is installed on your system (for Windows, please make sure your `JAVA_HOME` environment variable is set to the correct version of Java).

#### IntelliJ
If you consider to use IntelliJ as your IDE of choice, you can make use of your free educational license [here](https://www.jetbrains.com/community/education/#students).
1. File -> Open... -> SoPra server template
2. Accept to import the project as a `gradle project`
3. To build right click the `build.gradle` file and choose `Run Build`

#### VS Code
The following extensions can help you get started more easily:
-   `vmware.vscode-spring-boot`
-   `vscjava.vscode-spring-initializr`
-   `vscjava.vscode-spring-boot-dashboard`
-   `vscjava.vscode-java-pack`

**Note:** You'll need to build the project first with Gradle, just click on the `build` command in the _Gradle Tasks_ extension. Then check the _Spring Boot Dashboard_ extension if it already shows `soprafs24` and hit the play button to start the server. If it doesn't show up, restart VS Code and check again.

### Building with Gradle
You can use the local Gradle Wrapper to build the application.
-   macOS: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

#### Build

```bash
./gradlew build
```

#### Run

```bash
./gradlew bootRun
```

You can verify that the server is running by visiting `localhost:8080` in your browser.

### Running the tests

```bash
./gradlew test
```

#### Development Mode
You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

### API Endpoint Testing with Postman
We recommend using [Postman](https://www.getpostman.com) to test your API Endpoints.

### Debugging
If something is not working and/or you don't know what is going on. We recommend using a debugger and step-through the process step-by-step.

To configure a debugger for SpringBoot's Tomcat servlet (i.e. the process you start with `./gradlew bootRun` command), do the following:

1. Open Tab: **Run**/Edit Configurations
2. Add a new Remote Configuration and name it properly
3. Start the Server in Debug mode: `./gradlew bootRun --debug-jvm`
4. Press `Shift + F9` or the use **Run**/Debug "Name of your task"
5. Set breakpoints in the application where you need it
6. Step through the process one step at a time

### Testing
Have a look here: https://www.baeldung.com/spring-boot-testing

### Deployment

By pushing your changes to the main branch three automated actions are ran:
- SonarCloud Analysis: Checking the quality of your code and test coverage.
- Deploying Project to App Engine / Test and Sonarqube: Checking if building works.
- Deploying Project to App Engine / Deploying to Google Cloud: Deploying Project. 

## Roadmap
New developers who want to contribute could add the following features to our project:
- Audio-Effects during gameplay.
- Voice-Chat or message-chat to allow users to talk to each other in real time.

## Authors

* **Xindi Liu**  [Cindylliu](https://github.com/Cindylliu)
* **Noah Isaak**  [guilloboi1917](https://github.com/guilloboi1917)
* **Noé Matumona**  [noematumona](https://github.com/noematumona)
* **Victor Cruz da Silva**  [vichcruz](https://github.com/vichcruz)

## License

This project is licensed under the GNU General Public License - see the [LICENSE.md](https://github.com/ansible/ansible/blob/devel/COPYING) file for details

