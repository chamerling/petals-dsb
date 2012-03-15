# Petals Distributed Service Bus

* To build the modules, just launch 'mvn install'
* To also build the distributions and the installer, add -Pdistributions to your maven command 'mvn install -Pdistributions'

# Petals 3.2 migration notes

- LoggingUtil changement de monolog vers Java Log. API change...
- org.ow2.petals.jbi.messaging.exchange.MessageExchange a disparu et est remplacé par org.ow2.petals.jbi.messaging.exchange.MessageExchangeImpl... Need an API!!!
-> Update de org.ow2.petals.jbi.messaging.exchange.MessageExchangeImpl dans tout les fichiers

- Update XMLUtil en XMLHelper et Transformers avec commons ebm
- !Utiliser des interfaces dans les interfaces!!! MessageExchangeImpl!

- MessageExchange ne contient plus la location! donc plus utilisée dans org.petalslink.dsb.transport.Adapter

- Transporter API sendSync ne retourne plus de message mais void

- Question : ou sont enregistrés les MBeans qui étés dans le PetalsServerIMpl? : MbeanHelper . Il y a eu un update du logger dedans et du router : Pas fait dans le DSB

- RouterMonitor and Persistance service are not used for now: set to optional in the router module. Note that this should be a router module and not a router dependency!

- TODO : implementer org.petalslink.dsb.transport.Adapter.createJBIMessageWrapper(MessageExchange)
- TODO : Ne plus utiliser les MEP de MessageExchanheImpl
- TODO : Check launcher qui n'est plus utilisé pareil!

- NS error for /org.petalslink.dsb.dsb-kernel/src/main/java/org/petalslink/dsb/kernel/ws/api/MasterConnectionService.java. NS updated with dsb NS but will broke master integration... Looks like now there is a real connector in the ESB kernel with org.ow2.petals.ws.DragonConnectionServiceImpl. cf https://gist.github.com/1885587 for error

- The CDK does not support onNotificationMessage on the listener anymore. The DSB CDK must support it (dsb-wsn-jbise)

- Need to delete /org.petalslink.dsb.dsb-xmlutils/src/main/java/org/petalslink/dsb/xmlutils/XMLHelper.java and use direclty utils


## New but not used
- org.ow2.petals.jbi.messaging.exchange.PersistedMessageExchangeWrapper dans TransporterNIO

## Upgrades to do on the ESB side
- API pour MessageExchange!
- PetalsServerImpl enregistre les MBeans via Helper : Pas accessible!

## Disappear
org.ow2.petals.jbi.messaging.endpoint.EndpointPropertiesService

## Deprecated in the DSB
And will be cleaned...

- Petals Oldies

<groupId>org.petalslink.dsb</groupId>
<artifactId>dsb-petals-oldies</artifactId>
<name>dsb-petals-oldies</name>

- Old monitoring

<groupId>org.petalslink.dsb</groupId>
<artifactId>dsb-monitoring</artifactId>
<packaging>jar</packaging>

-- @chamerling
