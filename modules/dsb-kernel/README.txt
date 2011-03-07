Services Description
--------------------

* communication :
WebServiceRemoteCheckerClient : WS implementation of the service used to check that a remote host is here. This is used each time a message is sent to the remote node.

* management/binder:
Bind services to the bus. It has been cutted in submodules in order to get binders for each protocol. The binders are activated by Fractal configuration.

* management/component :
- Start some component which are available in the artifact directory N seconds after the node is started. This is configured in the soa4all configuration file

* management/cron :
- ServciePoller = looks at the service registry for new endpoints and expose them if needed (configuration)

* management/protocol :
- Not used

* management/messaging :
- Not used

* monitor : Old monitoring for studio which stores N messages in a Map and expose them as WS

* monitoring : Not used anymore

* ws : Expose some services as Web Services

IMPORTANT
------------
NEED ASM 3.0 to work, exclude all CXF dependencies from ALL dependencies!!!