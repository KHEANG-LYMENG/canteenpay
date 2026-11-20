# Lab-01: Environment and Tool Versions

## Student A (Primary Machine)
- **Operating System**: macOS (Darwin 27.2.0 arm64, Apple Silicon)
- **Java Runtime**: Oracle JDK 27.0.1.0.3 (build 27.0.1.0.3+1-2)
  - Java Home: `/Library/Java/JavaVirtualMachines/jdk-27.jdk/Contents/Home`
- **Apache Maven**: 3.9.9
- **Git Version**: 2.54.0 (Apple Git-157)
- **IDE / Environment**: Antigravity / Terminal

## Student B (Partner Machine)
- **Operating System**: [To be recorded by Student B]
- **Java Runtime**: [e.g. JDK 25 / 27]
- **Apache Maven**: [e.g. 3.9.x]
- **Git Version**: [e.g. 2.x]
- **IDE / Environment**: [e.g. IntelliJ IDEA / VS Code / Eclipse]

## Verification Status
- `mvn -q compile` ran cleanly with exit code 0.
- `mvn verify` executed 1 test with 0 failures, 0 errors.
- Intentional failure test executed: verified AssertionFailedError (`expected: "CP-20270001-8" but was: "CP-20270001-9"`).
- Test restored and confirmed passing with `BUILD SUCCESS`.
