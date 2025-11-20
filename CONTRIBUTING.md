# Contributing to EJB2 Spring Integration

We love your input! We want to make contributing to EJB2 Spring Integration as easy and transparent as possible, whether it's:

- Reporting a bug
- Discussing the current state of the code
- Submitting a fix
- Proposing new features
- Becoming a maintainer

## We Develop with GitHub

We use GitHub to host code, to track issues and feature requests, as well as accept pull requests.

## We Use [GitHub Flow](https://guides.github.com/introduction/flow/index.html)

Pull requests are the best way to propose changes to the codebase:

1. Fork the repo and create your branch from `main`.
2. If you've added code that should be tested, add tests.
3. If you've changed APIs, update the documentation.
4. Ensure the test suite passes.
5. Make sure your code lints.
6. Issue that pull request!

## Any contributions you make will be under the MIT Software License

In short, when you submit code changes, your submissions are understood to be under the same [MIT License](LICENSE) that covers the project. Feel free to contact the maintainers if that's a concern.

## Report bugs using GitHub's [issue tracker](https://github.com/sleroy/ejb2-spring-integration/issues)

We use GitHub issues to track public bugs. Report a bug by [opening a new issue](https://github.com/sleroy/ejb2-spring-integration/issues/new).

## Write bug reports with detail, background, and sample code

**Great Bug Reports** tend to have:

- A quick summary and/or background
- Steps to reproduce
  - Be specific!
  - Give sample code if you can
- What you expected would happen
- What actually happens
- Notes (possibly including why you think this might be happening, or stuff you tried that didn't work)

## Development Process

### Prerequisites

- Java 17+
- Maven 3.6+
- Git

### Setup

```bash
# Clone your fork
git clone https://github.com/YOUR_USERNAME/ejb2-spring-integration.git
cd ejb2-spring-integration

# Add upstream remote
git remote add upstream https://github.com/sleroy/ejb2-spring-integration.git

# Create a branch
git checkout -b feature/my-feature
```

### Build

```bash
# Build the project
mvn clean install

# Run tests
mvn test

# Check code style
mvn checkstyle:check
```

### Code Style

- Follow standard Java coding conventions
- Use meaningful variable and method names
- Add Javadoc comments for public APIs
- Keep methods focused and small
- Write unit tests for new features

### Commit Messages

- Use the present tense ("Add feature" not "Added feature")
- Use the imperative mood ("Move cursor to..." not "Moves cursor to...")
- Limit the first line to 72 characters or less
- Reference issues and pull requests liberally after the first line

Example:
```
Add retry mechanism to ExceptionHandlingEjbMethodInvoker

- Implement exponential backoff
- Add configuration for max retries
- Update documentation

Fixes #123
```

### Pull Request Process

1. Update the README.md with details of changes to the interface, if applicable.
2. Update the documentation in the `docs/` directory with any relevant changes.
3. Add tests to cover your changes.
4. Ensure all tests pass and the build succeeds.
5. Update the CHANGELOG.md with a note describing your changes.
6. The PR will be merged once you have the sign-off of at least one maintainer.

## Testing

### Unit Tests

Write unit tests for all new functionality:

```java
@Test
public void testTransactionCommitOnSuccess() throws Exception {
    // Arrange
    EjbMethodInvoker mockDelegate = mock(EjbMethodInvoker.class);
    when(mockDelegate.invoke(any(), any())).thenReturn("result");
    
    Connection mockConnection = mock(Connection.class);
    
    TransactionEjbMethodInvoker invoker = new TransactionEjbMethodInvoker(
        mockDelegate, 
        TransactionPolicy.SUCCESS
    );
    
    // Act
    Object result = invoker.invoke("request", mockConnection);
    
    // Assert
    assertEquals("result", result);
    verify(mockConnection).commit();
    verify(mockConnection, never()).rollback();
}
```

### Integration Tests

For integration tests that require a database:

```java
@SpringBootTest
@Testcontainers
public class EjbIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
    
    @Test
    public void testFullIntegration() {
        // Test with real database
    }
}
```

## Documentation

### Code Documentation

- Add Javadoc to all public classes and methods
- Include `@param`, `@return`, and `@throws` tags
- Provide usage examples in class-level Javadoc

Example:
```java
/**
 * Decorator that adds transaction management to EJB method invocations.
 * 
 * <p>Example usage:</p>
 * <pre>
 * EjbMethodInvoker invoker = new TransactionEjbMethodInvoker(
 *     baseInvoker,
 *     TransactionPolicy.SUCCESS
 * );
 * </pre>
 *
 * @author EJB2Spring Framework
 * @see TransactionPolicy
 * @see EjbMethodInvoker
 */
public class TransactionEjbMethodInvoker implements EjbMethodInvoker {
    // ...
}
```

### README Updates

When adding new features:
1. Update the Features section
2. Add examples to the Examples section
3. Update the API Reference if needed

### Additional Documentation

Consider adding:
- Architecture Decision Records (ADRs) in `docs/adr/`
- Tutorial guides in `docs/tutorials/`
- Migration guides for specific scenarios

## Code Review Process

All submissions require review. We use GitHub pull requests for this purpose.

### What We Look For

- **Correctness**: Does the code do what it claims to do?
- **Tests**: Are there adequate tests?
- **Documentation**: Is the code well-documented?
- **Style**: Does it follow our coding standards?
- **Design**: Is the design sound and maintainable?
- **Performance**: Are there any performance concerns?

## Community

- Be respectful and inclusive
- Provide constructive feedback
- Help others learn and grow
- Celebrate contributions, no matter how small

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

## Questions?

Feel free to ask questions by:
- Opening an issue
- Starting a discussion on GitHub Discussions
- Contacting the maintainers

Thank you for contributing to EJB2 Spring Integration! 🎉
