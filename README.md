# auction-service
This service caters to auction events

You can use Login and Signup requests on the attached postman file (Melih-v1.postman_collection.json), located under root directory 
After signup with user, and get token, you can consume Auction Service endpoints based on the role of the user. Check user-role service Readme file for further info.

Default Port: 8082 Also, swagger is available on http://localhost:8082/swagger-ui/index.html

Api version is v1, and it is provided by Accept header "application/vnd.melih.api.v1+json"

H2 database is available on http://localhost:8082/h2-console/ via username:sa and password:password

Authorization header must be added to request, and it must include a JWT token, signed by the user-service

There are 2 locales, Turkish and English(default). If you want to change the locale, simply add Locale(en or tr) header to your requests.

Start with Auction Create request to create an auction (Only permitted to SELLER role)
use Auction Bid request for bidding (Only permitted to BUYER role)
Users can list Auctions, but only SELLER can display maxBid and maxBidHolder 
When SELLER user ends auction with Auction End endpoint, s/he will get a response with maxBid and maxBidOwner info of that auction.
