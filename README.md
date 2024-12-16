#### Software Architecture and Platforms - a.y. 2024-2025

## Lab #10-20241213 (+addendum on 20241216)

In this lab we have a look (just a look) at two technologies: [Apache Kafka](https://kafka.apache.org/), as main example of middleware for Event-Driven Microservices and Architectures, and  [JaCaMo](https://jacamo-lang.github.io/), as main example of research platform for programming agents and multi-agent systems, based on the BDI (Belief-Desire-Intention) architecture on the agent side, the A&A (Agents and Artifacts) on the environment side, and Moise on the organisation side.

- [Apache Kafka](https://kafka.apache.org/) - "An open-source distributed event streaming platform"
  - [Background and Context](https://developer.confluent.io/faq/apache-kafka/architecture-and-terminology/)
  - [Kafka Intro](https://kafka.apache.org/intro)
  - [Kafka Documentation](https://kafka.apache.org/documentation/)
    - [Architecture](https://kafka.apache.org/39/documentation/streams/architecture)
  - Kafka [Quick start](https://kafka.apache.org/quickstart)
    - [Setting up Kafka Using Docker](https://docs.google.com/document/d/1sGcs2UHeAx8lrca5PuMeGTZVGq7NIBm_oFyQhe5jFuc/edit?usp=sharing)
      - using Docker Compose with `kafka-deplo.yaml` config file
  - Working with Kafka - Kafka clients
    - [Kafka clients in Java](https://docs.confluent.io/kafka-clients/java/current/overview.html)
    - A simple Kafka producer and consumer in Java (sources in `sap.kafka` package)
  - Specifying API in Event-Driven Architectures: Recalling the [AsyncAPI](https://www.asyncapi.com/) initiative	

- [JaCaMo](https://jacamo-lang.github.io/) - A plaform for programming multi-agent systems 
  - Background materials -- see "Agents and MAS" subfolder in "Materials" folder on the [course web site](https://virtuale.unibo.it/course/view.php?id=60131)
    - in particular: AOP and MAP tutorial
  - [Getting Started](https://jacamo-lang.github.io/getting-started)
  - [Playground on GitPod](https://gitpod.io/#https://github.com/jacamo-lang/template)

- **Microservices+agents integration** -- the repo contains a simple example related to the smart-room case study (see previous labs), in which a simple smart thermostat is implemented using agent technologies and microservices. In particular:
  - the smart-thermostat agent is implemented in Jason, and integrated inside a Java-based microservice designed using hexagonal/clear architecture using CArtAgO artifacts for bridging domain layer and infrastructure layer;     
  - a room-controller microservice is provided, simulating the room to be controlled, providing a REST API to control a HVAC.
  - To run the example:
    - first run the room-controller - the main class is: `room_controller.LaunchRoomControllerService`
    - then run the agent-based microservice - the main class is: `thermostat.LaunchSmartThermostat`
      - this is based on maven dependencies + local jars included in `lib` folder
      - cannot be run using Maven only (pom file to be fixed)
       

	
   
