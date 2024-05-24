# Sketchy Phone

This project aims to blend two classic games: the Telephone game and the Exquisite Corpse game. We allow players to sign up as persistent users or play as guest users. The primary difference is that persistent users can save and view their playing history. For the best experience, we recommend playing the game during a call with friends! Each game begins with players creating a text prompt. These prompts are then distributed to other players, who draw their interpretation of the received prompt. Next, these drawings are redistributed, and players must describe the drawing in words, forming a new text prompt. This completes one cycle of the game. The game progresses through multiple cycles, each one altering the original text prompt in unique ways. At the end, all cycles are displayed to the players, showing the evolution from start to finish. Additionally, we narrate text prompts to add humorous effects to the presentation at the of the game. Players can upvote text prompts and drawings. At the end of the game, the best text prompt writer and artist are revealed based on the votes.

## Technologies used
- Spring Boot (Docs: https://docs.spring.io/spring-boot/docs/current/reference/html/index.html)
- Gradle (Docs: https://gradle.org/docs/)
- Google Cloud (Deployment)

## High-level components
1. **User Service**
   - **Role:** Handles user registration, authentication, and profile management. This component ensures that users can securely sign up, log in, and manage their profiles.
   - **Correlation:** Interacts with the Game Service to authenticate users and manage user data during gameplay.
   - **Reference:** `UserService.java` - [UserService.java](https://github.com/sopra-fs24-group-25/SketchyPhone-Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/UserService.java)

2. **Game Service**
   - **Role:** Manages the creation, updating, and retrieval of game instances. This service allows users to join existing game rooms or create new ones. This component is responsible for the game lifecycle, including setting game parameters and tracking game status. This service also handles the main game logic. 
   - **Correlation:** Works closely with both the User Service for user management.
   - **Reference:** `GameService.java` - [GameService.java](https://github.com/sopra-fs24-group-25/SketchyPhone-Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/Game/GameService.java)

3. **Game Controller**
   - **Role:** Manages all Rest Endpoints concerning the game logic.
   - **Correlation:** When Client sends http-requests concerning the game they go through game controller where the actual methods in the game service are called.
   - **Reference:** `GameController.java` - [GameController.java](https://github.com/sopra-fs24-group-25/SketchyPhone-Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/controller/GameController.java)

4. **User Controller**
   - **Role:** Manages all Rest Endpoints concerning the user managment.
   - **Correlation:** When Client sends http-requests concerning the user managment they go through game controller where the actual methods in the user service are called.
   - **Reference:** `UserController.java` - [UserController.java](https://github.com/sopra-fs24-group-25/SketchyPhone-Server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/controller/UserController.java)

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
- Implement microphone feature for all users in the game
- Allow users to spend their accumulated votes to manipulate other user's microphone output with humorous effects.
- Provide users with a account recovery service, in case they lost their account credentials.
- Implement Websockets for better traffic control and bidirectional communication

## Authors

* **Xindi Liu**  [Cindylliu](https://github.com/Cindylliu)
* **Noah Isaak**  [guilloboi1917](https://github.com/guilloboi1917)
* **Noé Matumona**  [noematumona](https://github.com/noematumona)
* **Victor Cruz da Silva**  [vichcruz](https://github.com/vichcruz)

* ## Acknowledgements
- [Template Server](https://github.com/HASEL-UZH/sopra-fs24-template-server)

## License

This project is licensed under the Apache License - see the [LICENSE.md](LICENSE) file for details

