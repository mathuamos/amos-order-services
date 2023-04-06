# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

## Project Description
Is a project that  manage the orders of the &quot;pilotes&quot; through some
API. Pilotes of the great Miquel Montoro are to order Majorcan recipe

### Guides

Instructions
1. Run mvn clean install to install all the dependency
2. Where to change db connection 
   Access application properties and changes url port username and password

Test user credentions username **amos@gmail.com** password **1234**
   
4. To run project  cd   mvn package

### Kindly note
After project start some dummy data are inserted to the in memory database as required neccesary data for system user i,e

Users and roles and some test recipes


Follow the instruction in the Api document to run the api calls

## Entities
1.Cart -> stores cart recipe of a user before submitting to create order
2. CartItems -> contains cart items
3. Orders -> stores users order
4. Recipes -> contains Majorcan recipe
5. Role -> stores role of users of the system
6. Users -< stores users


OrdersController is used to manage orders api
1.   **/api/v1/myorders** -> used to get orders of a specific user
2. **/api/v1/checkout**  -> used to checkout cart in order to make order
3. **/api/v1/reactivate-cart**  -> used to reactive order for editing purpose
4. **/api/v1/update-order** -> used to update order after editing
5. **/api/v1/cancel-order** -> used to cancel order 


CartController is used to manage recipes and cart  apis
1. **/api/v1/recipes** -> it gets all active available  Majorcan recipes
2. **/api/v1/add-to-cart** -> used to add recipe to cart
3. **api/v1/get-cart-details** -> used to get cart details
4. **/api/v1/remove-recipe** -> used to remove item in cart
5. **/api/v1/modify-cart-item**  -> used to update/edit cart item


## Apis
Kindly use swagger for api reference
http://localhost:8080/swagger-ui/index.html#/



