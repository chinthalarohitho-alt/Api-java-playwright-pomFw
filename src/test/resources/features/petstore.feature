Feature: Pet Store API Comprehensive Tests

  @Pet
  Scenario: 01. Add a new pet to the store
    Given get the scheme "petstore"
    And update the json path "id" with a random numeric value
    And update the json path "name" with value "Buddy"
    And update the json path "status" with value "available"
    When post request send with endpoint "pet"
    Then the response status code should be 200
    And the response field "id" should be of type "number"
    And the response field "name" should be of type "string"
    And the response field "name" should be "Buddy"

  @Pet
  Scenario: 02. Update an existing pet's status
    Given get the scheme "updatePet"
    And update the json path "id" with a random numeric value
    And update the json path "status" with value "sold"
    When put request send with endpoint "pet"
    Then the response status code should be 200
    And the response field "status" should be of type "string"
    And the response field "status" should be "sold"

  @Pet
  Scenario: 03. Find pets by status - available
    When get request send with endpoint "pet/findByStatus?status=available"
    Then the response status code should be 200
    And the response array "$" should not be empty
    And the response field "[0].status" should be "available"

  @Pet
  Scenario: 04. Find pets by status - pending
    When get request send with endpoint "pet/findByStatus?status=pending"
    Then the response status code should be 200
    And the response field "[0].status" should be "pending"

  @Pet
  Scenario: 05. Find pets by status - sold
    When get request send with endpoint "pet/findByStatus?status=sold"
    Then the response status code should be 200
    And the response field "[0].status" should be "sold"

  @Pet @Smoke
  Scenario: 06. Get pet by ID
    Given get the scheme "petstore"
    And update the json path "id" with value "987654321"
    When post request send with endpoint "pet"
    And get request send with endpoint "pet/987654321"
    Then the response status code should be 200
    And the response field "id" should be 987654321
    And the response field "name" should be of type "string"

  @Pet
  Scenario: 07. Get pet by non-existent ID
    When get request send with endpoint "pet/0"
    Then the response status code should be 404

  @Pet
  Scenario: 08. Update pet with form data
    Given get the scheme "petstore"
    And update the json path "id" with value "987654322"
    When post request send with endpoint "pet"
    When post request send with endpoint "pet/987654322" and with the payload "name=Max&status=pending"
    Then the response status code should be 200
    And the json path "message" should be "987654322"

  @Pet
  Scenario: 09. Delete a pet
    Given get the scheme "petstore"
    And update the json path "id" with value "987654323"
    When post request send with endpoint "pet"
    When delete request send with endpoint "pet/987654323"
    Then the response status code should be 200
    And the response field "code" should be 200
    And the response field "message" should be "987654323"

  @Pet
  Scenario: 10. Verify deleted pet is not found
    Given get the scheme "petstore"
    And update the json path "id" with value "987654324"
    When post request send with endpoint "pet"
    And delete request send with endpoint "pet/987654324"
    And get request send with endpoint "pet/987654324"
    Then the response status code should be 404

  @Store
  Scenario: 11. Returns pet inventories by status
    When get request send with endpoint "store/inventory"
    Then the response status code should be 200
    And the response field "$" should be of type "object"

  @Store
  Scenario: 12. Place an order for a pet
    Given get the scheme "storeOrder"
    And update the json path "id" with a random numeric value
    And update the json path "petId" with value "101"
    And update the json path "quantity" with value "1"
    When post request send with endpoint "store/order"
    Then the response status code should be 200
    And the response field "petId" should be 101
    And the response field "quantity" should be 1
    And the response field "complete" should be of type "boolean"

  @Store
  Scenario: 13. Find purchase order by ID
    Given get the scheme "storeOrder"
    And update the json path "id" with value "101010"
    When post request send with endpoint "store/order"
    And get request send with endpoint "store/order/101010"
    Then the response status code should be 200
    And the response field "id" should be 101010

  @Store
  Scenario: 14. Delete purchase order by ID
    Given get the scheme "storeOrder"
    And update the json path "id" with value "101011"
    When post request send with endpoint "store/order"
    And delete request send with endpoint "store/order/101011"
    Then the response status code should be 200

  @Store
  Scenario: 15. Delete non-existent order
    When delete request send with endpoint "store/order/0"
    Then the response status code should be 404

  @User
  Scenario: 16. Create user
    Given get the scheme "createUser"
    And update the json path "username" with a random string value
    When post request send with endpoint "user"
    Then the response status code should be 200
    And the response field "message" should match pattern "^[0-9]+$"

  @User
  Scenario: 17. Get user by username
    Given get the scheme "createUser"
    And update the json path "username" with value "portfolio_parallel_user"
    When post request send with endpoint "user"
    And get request send with endpoint "user/portfolio_parallel_user"
    Then the response status code should be 200
    And the response field "username" should be "portfolio_parallel_user"
    And the response field "userStatus" should be of type "number"

  @User
  Scenario: 18. Update user
    Given get the scheme "createUser"
    And update the json path "username" with value "portfolio_parallel_user_update"
    When post request send with endpoint "user"
    And update the json path "firstName" with value "Jane"
    When put request send with endpoint "user/portfolio_parallel_user_update"
    Then the response status code should be 200

  @User
  Scenario: 19. User login
    Given get the scheme "createUser"
    And update the json path "username" with value "login_user"
    And update the json path "password" with value "Pass123"
    When post request send with endpoint "user"
    And get request send with endpoint "user/login?username=login_user&password=Pass123"
    Then the response status code should be 200
    And the response field "message" should contain "logged in user session"

  @User
  Scenario: 20. User logout
    When get request send with endpoint "user/logout"
    Then the response status code should be 200

  @User
  Scenario: 21. Create list of users with array
    When post request send with endpoint "user/createWithArray" and with the payload "[{\"id\": 10, \"username\": \"u10\"}, {\"id\": 11, \"username\": \"u11\"}]"
    Then the response status code should be 200

  @User
  Scenario: 22. Create list of users with list
    When post request send with endpoint "user/createWithList" and with the payload "[{\"id\": 12, \"username\": \"u12\"}, {\"id\": 13, \"username\": \"u13\"}]"
    Then the response status code should be 200

  @User
  Scenario: 23. Delete user
    Given get the scheme "createUser"
    And update the json path "username" with value "delete_user"
    When post request send with endpoint "user"
    And delete request send with endpoint "user/delete_user"
    Then the response status code should be 200

  @User
  Scenario: 24. Delete non-existent user
    When delete request send with endpoint "user/unknown_99999"
    Then the response status code should be 404

  @Negative
  Scenario: 25. Add pet with truly invalid body
    When post request send with endpoint "pet" and with the payload "{ broken: "
    Then the response status code should be 400

  @Negative
  Scenario: 26. Get pet with invalid ID format
    When get request send with endpoint "pet/abc_def"
    Then the response status code should be 404

  @Negative
  Scenario: 27. Get pet that does not exist
    When get request send with endpoint "pet/0"
    Then the response status code should be 404

  @Boundary
  Scenario: 28. Place order with maximum quantity
    Given get the scheme "storeOrder"
    And update the json path "id" with a random numeric value
    And update the json path "quantity" with value "100"
    When post request send with endpoint "store/order"
    Then the response status code should be 200
    And the response field "quantity" should be 100

  @Security
  Scenario: 29. Try to get non-existent user
    When get request send with endpoint "user/non_existent_12345"
    Then the response status code should be 404

  @Smoke
  Scenario: 30. Check API health/version
    When get request send with endpoint "pet/findByStatus?status=available"
    Then the response status code should be 200
    And the response array "$" should not be empty
