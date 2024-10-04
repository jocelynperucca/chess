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
![Jocelyn Perucca Phase 2](https://github.com/user-attachments/assets/05156948-13fa-439a-aa60-12f9dcd4db4b)]
(https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIcDcuj3ZfF5vD6L9sgwr5iWw63O+nxPF+SwfgC5wFrKaooOUCAHjytJvqM2IJoYLphm6ZIUgaqEjuhoZEuGFocjA3K8gagrCjAoriq60pJpe8HlDR2iOs6yYwSWHHyJggH-kUbEwCUSAAGaWFU+ivEssL8cAswQe8GFylhPFAiWABqlCSYQ8SJNmuaCbxwmYeUnrekGs7oAp9raGp8EajhLLlKoIQoLGM6NnZ3m2WgJqkW65HlNGMD+b5aAkUyIUsSJTryhFNlRVxiaaamYUpXOplaSCcElFcr5EWOVbTgFbbFvlhTZD2MD9oOvTFSBowLuVUVtsupieN4fiBF4KDoHuB6+Mwx7pJkmC1ReCXXtIACiu7zfU83NC0D6qE+nTtTlVVskJ5Q7eguWpmyFkwEh9ijQG2XoE5iVYQSrmkjA5JgOESAcDUEk3XWUVBbFzGFJaMgoNwmQwEd0VMea8XnRwiRiJhnYHUYiMnbBBUlgjaBI1e1VdjkYB9gOaO42YnCrr1G6Qrau7QjAADio6suNp5TeezBY2mFSM8ta32KO3RQ3++1mYdt3RUJZ3qYh0LM6Mqi-T5c73c6T3BW5kNBuYBmZMrAUA2aEbAxRVG2iLxidjD7owJCYA+LcdoCpxNududilpY9MjPbbEk8hw9OxAraiwkbZGRhRjMUjAkPKq9o4AHIszFxvu7LCejEHWDI-FqOC1n0IYx2cP4+UpZ9AXajjJUVfJ6MsxVwAktIswMWojQHHt8XTcT9WkxUdejo3o4t8pCDAJYlAAIwGnc4+T5QvZz6OjQUyuPXroE2CO9g3DwLqEMhykE1nkTMvUNetQNALQvBJLQ5D6MXf42LeVXFDI+jPXKDQXlF8PUsofFAIcDZRS-igH+atEwa0Bi9acusQB6jAarVOEdTblHNtrP6OU3al1EpyXwvIRa51gcbIBXpMigKfigcOcUMERRtJnSBo4YCQDjggZhGg8EFXBHbCkjskjN2kF7FG4tmEt2LgTKqZJR4iO7qcXuJMnCmC6pvPqAQp7KggMkGAAApCAPImasICDoCeIAGwc3Ptzco1RKR3haFXYWD9eh70XlAOAEAkJQAgS3F+l9hKox6J-GAbip4eK8dAXx0g-6nV4UlAAVoYtAoCQlhMoJ47x0ToHexthLP0iDkFQzoUDdkmCA7YJVsdHhrFAGUSIRbSWojsKaxev7Tg1C5ElNhgwrBVchRhGEZoHQ1tfblEiA7J2x8HQ1ISnw6ZnFSE+1abbYANoqgT3CbCYRC9wmZKifRBy8hMQwAkt4YYF1kLQ19qFRhtp0mwHYSHYZoyVnlA2e4yGNoUC2ieaONBcV4kIRgB88JXyOA-LYRAYx4pc4ZWBOUJJPJIXCJgHcSGvIIASX9kQYAnCHn7KgFI9OATSg9B2aEzZGTIlQH8ccHunNlH6OST89eVMt4BC8JPLsXpYDAGwHvPWT4T7s17gAuai1lqrVaMYUW8KSwgG4HgBQyow7EqBZZJVUAVUIDVUsvJMBFW8p1WqvBDC4BLQUOECKu5dxMwUAAWXmmQAFpSQYWvmlam1dqFD2JYPUGoABpeaicXVmrKfAS11qFC2r5GQea4Qw03PwXUnGeNnJ53EWmqRKbsboyqgyomTLs2qMpkAA)
