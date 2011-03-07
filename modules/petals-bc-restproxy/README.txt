# Component
Embeds a component.wsdl to automatically activate a JBI endpoint ie no SUs are needed!

# Proxify REST requests

http://HOST:PORT/PATH/URL

where
HOST, PORT and PATH are defined in the component jbi.xml.
Default configuration generates 'http://localhost:8989/petals/rest/proxy/'

If you want to proxify a REST service located at http://weather.yahooapis.com/forecastrss?w=2442047&u=c
call
http://localhost:8989/petals/rest/proxy/http://weather.yahooapis.com/forecastrss?w=2442047&u=c

