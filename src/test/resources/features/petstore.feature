Feature: Pet Store API

  @Petstore
  Scenario: Verify that a pet can be added and then retrieved
    Given get the scheme "petstoe"
    And update the json path "status" with value "available"
    When post request send with endpoint "pet"
    Then the response status code should be 200
    And the json path "name" should be "Rocky"
    And the json path "status" should be "available"
