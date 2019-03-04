# MapScanner
This is a middleware service for Android mobile apps. Its goal is to return places of interests (best coffee shops, nearest cinema...) by requesting the data for the applications bound to it. The user will enter his filter on an app (only restaurant near my location, with more than four stars for example). This filter will be forwarded by the app to the middleware. The middleware will be in charge of doing the business logic and returning the tailored data.

The middleware hides the complexity of requesting data asynchronously to a REST Web Service. It will actually request, through HTTP calls to the Foursquare's API, data about the user' surroundings.

### Developed from Febuary 2019 - now
