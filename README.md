# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```


Phase 2 Sequence Diagram: 
![JocelynPeruccaPhase2Redo](https://github.com/user-attachments/assets/7b0552da-0a3f-405a-804b-49307d8f90e2)

[Phase 2 Diagram Jocelyn Perucca](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIcDcuj3ZfF5vD6L9sgwr5iWw63O+nxPF+SwfgC5wFrKaooOUCAHjytJvqM2IJoYLphm6ZIUgaqEjuhoZEuGFocjA3K8gagrCjAoriq60pJpe8HlDR2iYIB-5FGxMAlEgABmlhVPorxLLCHHyLMEHvBhcpYcmMElgAapQQmEPEiTZrmpiYRqOEsuUnrekGjKwrGM6NugswMSgJqkW65HlNGMCWQ6THmixvFOvKblmZx+nedxLkBfIXHKSCcElFcr5EWOVbTrO6BtsWUWFNkPYwP2g69HFIGjAuSXWUkfRLpwpieN4fiBF4KApfu9i+Mwx7pJkmCZRePnXtIACiu69fUvXNC0D6qE+nTFXOf5siFMBTegEVAh20XgjASFNb6AZBslaDyfBBmOUZ81BuYmmZNtdYlQ5TJOZGFFUbaC1oJoOidp57rpmAPi3CdcacR9naYaF-3hUFBKGaSMDkmA4RIBwNSCZdwbaDdZoRoUloyCg3CZH9KPhYD3nA9lA6Os6SnLWmOVLamQNXtTZNpZ2nVgH2ZPLqu1UbpCtq7tCMAAOKjqyrWnh157MNF16C4NI32KO3TPTNlOpuUyvcWyJPILEwujKoyO7ftvlYRDR1Q9OZ0gHqhvXSRt3MZjD08k9O0la972Q59kLfb97kA179N8f7YMKYdDtQ4JLv87rIuwmjZH3eUgsUjA83KtDo4AHIi-b6NBybZKjjHWBBarwJF6MJe07B0tpqWfQK-r4yVE3KA56MsxtwAktItloWojQHMz3ms+UFQ5c0PRTG3qgtxU-Q99ILcAIy9gAzAALE8J6ZAaFYTF8OgIKADb76BfRfG3HcIZfewwEPZgVVV66BNgP3YNw8C6njeuGGL7VWZawZuPWoDR5aK2CG7OcQ5r6jmHgzWakUrjPS7tneBNcVqsULjAEymQ-62znGg0YN9jbOjNhHT6lsMDnRQIQ9ACc7pO3KI9fGu086J1Wn5TkvheTK3BjIL2xkf4oAIXA4iRNmFuRtJnEho4YCQHTggWRagOF3S4QhGAPsfpJCXuTRM5cSx6M1sTEBpYl4IOoCtU4Y9KiT1LDPUcvdV4b03jAA4T8VwvxqgESwOMkLJBgAAKQgDyIW8iAjH1PhLHIUtsHXiqJSO8LQ25K2gegIcn9gB+KgHACASEoDEJQL3SxxxDFph6KgmAWScl5IKUUkpmCeIkwAFahLQAQqpNTKB1OgA06QZDEwUPRurU6NDrYXWeowx27IWEuzYe7ImGiWG8NdldaaAiPrlCjpwMRTiBlqJmVjVhbchRhD0cYT25tPqRF9kkP+gjCaB1MXxB58Yw7YWuerG0VQT45NhEvGSfyen5L6fRe0qMYCCW8MMdayEXqSNmdI203TYCKL-h7byWyYC-OyR2YANoUC2nRaOQ5Xllk4uBbAAlHAiUKIgOE8UZcALINJk4JpBcSw0xHjYyW7N2Wc28RuLw2SuxempdgT+50nwpDameWJwCrFpgqH1AaQ0RrGBViyqmxluB4AUMqeOHKKUgD1VAA1CAjWbKEbgs1f8DbTK8lIuAA0FDhDcruXcQsFAAFlepkExZ8yhurxV0tnmSiMJqzVhtztar5trxWWV2g6iNzl4CuvdQoT1bkkksHqDUAA0r1LOAbLlYptaa0NayrJzg0Es7Ba1K14Dpc9DQcbg0JrwAaFNiKsYut6m6j1XrKRkF6uEUtb1y3xqbbzcFlA63PKjVWudUA20fPKfyppLyYpsr-KPPldiBxTw8Z4zAQA)

