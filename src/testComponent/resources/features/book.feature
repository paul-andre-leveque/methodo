Scenario: L'utilisateur crée deux livres et les récupère
When the user creates the book "Kotlin" written by "Dmitry"
And the user creates the book "Java" written by "Joshua"
And the user retrieves all books
Then the list should contain the following books in the same order
| title  | author  |
| Kotlin | Dmitry  |
| Java   | Joshua  |

Scenario: L'utilisateur réserve un livre
Given the following books exist:
| title  | author  | isReserved |
| Kotlin | Dmitry  | false      |
| Java   | Joshua  | false      |
When the user reserves the book "Kotlin"
And the user retrieves all books
Then the book "Kotlin" should have isReserved set to true

Scenario: L'utilisateur tente de réserver un livre déjà réservé
Given the following books exist:
| title  | author  | isReserved |
| Kotlin | Dmitry  | true       |
| Java   | Joshua  | false      |
When the user reserves the book "Kotlin"
Then the user should receive an error message "Book already reserved or unavailable"