✅ TDD - Test Driven Development
▶️ What is TDD?
TDD means:

Write Tests First → Before writing the actual logic

Run tests → They fail initially

Write the minimum logic to make tests pass

Refactor if needed

Repeat

▶️ TDD Example (Java)
Suppose you're creating a Calculator:

Step 1: Write Test First

java
Copy
Edit
@Test
void testAddition() {
    Calculator calc = new Calculator();
    assertEquals(5, calc.add(2, 3));
}
Step 2: Run Test → Fails (add() not implemented)

Step 3: Implement the Method

java
Copy
Edit
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
}
Step 4: Run Test → Passes

✅ BDD - Behavior Driven Development
▶️ What is BDD?
BDD focuses on system behavior from the user perspective, often written in natural language:

Uses Given-When-Then structure

Helps collaboration between developers, testers, and business people

Often implemented with tools like Cucumber, JBehave, etc.

▶️ BDD Example with Cucumber
Feature File:

gherkin
Copy
Edit
Feature: Calculator Addition

  Scenario: Add two numbers
    Given I have a calculator
    When I add 2 and 3
    Then the result should be 5
Step Definitions (Java):

java
Copy
Edit
Calculator calc;
int result;

@Given("I have a calculator")
public void i_have_a_calculator() {
    calc = new Calculator();
}

@When("I add {int} and {int}")
public void i_add_and(int a, int b) {
    result = calc.add(a, b);
}

@Then("the result should be {int}")
public void the_result_should_be(int expected) {
    assertEquals(expected, result);
}
✅ TDD vs BDD Quick View
Aspect	TDD	BDD
Tests written	In code (JUnit/TestNG)	Natural language (Given-When-Then)
Focus	Code correctness	System behavior (user perspective)
Tools	JUnit, Mockito	Cucumber, JBehave, SpecFlow

✅ Parameterized Tests
▶️ What are Parameterized Tests?
Run the same test multiple times with different inputs

Avoid code duplication

Useful for edge cases and varying data

▶️ Parameterized Test Example (JUnit 5)
java
Copy
Edit
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    Calculator calc = new Calculator();

    @ParameterizedTest
    @CsvSource({
        "2, 3, 5",
        "10, 5, 15",
        "-2, 4, 2"
    })
    void testAddition(int a, int b, int expected) {
        assertEquals(expected, calc.add(a, b));
    }

Output: Runs 3 times with different inputs:

add(2, 3) → 5

add(10, 5) → 15

add(-2, 4) → 2

▶️ Other Options for Parameterized Tests
@ValueSource: Single input values

@CsvSource: Multiple values per run

@MethodSource: Custom data providers

@EnumSource: For enums