# Auction Service

This service caters to auction events.

## Postman Collection
You can use the Login and Signup requests provided in the attached Postman file (`Melih-v1.postman_collection.json`), located under the root directory. After signing up with a user and obtaining a token, you can consume Auction Service endpoints based on the user's role. Check the `user-role` service README file for further information.

## Default Port
Default Port: 8082

## Swagger
Swagger is available on [http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html)

## API Version
Api version is v1, and it is provided by the Accept header `application/vnd.melih.api.v1+json`.

## H2 Database
H2 database is available on [http://localhost:8082/h2-console/](http://localhost:8082/h2-console/) with the following credentials:
- Username: sa
- Password: password

## Authorization
Authorization header must be added to the request, and it must include a JWT token signed by the user-service.

## Locales
There are 2 locales: Turkish and English (default). To change the locale, simply add a `Locale` header to your requests with values `en` or `tr`.

## Getting Started
Start with the Auction Create request to create an auction (Only permitted to SELLER role). Auction Create body takes 2 arguments: `productId` and `minBid`. There is no product entity implemented, so put a casual identifier instead.

## Bidding
Use the Auction Bid request for bidding (Only permitted to BUYER role).

## Auction Listing
Users can list Auctions, but only SELLER can display `maxBid` and `maxBidHolder` if it is still active. If not, both USER and SELLER can display.

## Auction End
When a SELLER user ends an auction with the Auction End endpoint, they will receive a response with `maxBid` and `maxBidOwner` info of that auction.