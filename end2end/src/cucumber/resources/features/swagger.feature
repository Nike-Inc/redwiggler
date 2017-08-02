@swagger
Feature: Parsing a swagger file

  Scenario: nullable string
    Given The swagger file 'nullable_string.swagger'
    When I parse the swagger
    And the test result 'response-nullable-string-1.json'
    When I validate based on the inputs
    Then I have 1 total results
    And I have 4 results with message 'schema violations found'
    And I have 1 results with message 'Fail: untested schema'
