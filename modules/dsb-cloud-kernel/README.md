# Move the Service Bus to the Cloud

## Petals ESB problems
### Configuration
- The configuration is heavily based on server.properties and topology.xml...

## Evolutions
### Topology
- The topology is dynamic and uses the IaaS features to be build. @see edelweiss topology service implementation.

## Added
- CloudConfiguration: Will search the csb.properties file in the classpath and in the $HOME/.edelweiss folder. This file needs to contain all the required cloud information to be used by edelweiss such as compute and storage providers.


