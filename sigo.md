# Introduction to Apache Kafka

A couple of weeks ago we have joined as Partners to Confluent.

Confluent is the company from the creators of Apache Kafka, that has build a platform on top of this product to make it enterprise-ready.

Today, we will talk about Apache Kafka: a project I have enjoyed a lot working with, contributing to and 
now I am really excited about this partnership and been able to offer it to our clients.

I will try something new to me, there is almost no slides, full live demo, so, please bear with me :)

## Comparing Kafka with a Traditional Messaging System (JMS)

The most common question about Kafka is: What is different from something like JMS? Why it is better?

To give an answer to that let's classify first JMS as a traditional messaging system. 

We will talk about broker based messsaging systems, different from brokerless like Vertx or CDI that share an event bus between all the components.

This kind of systems are known for its ability to decouple systems, manage messages send from some producers to some consumers.

The behavior I want to highlight from messaging systems is that it receives message from producers, stores them, send them to consumers and finally removes them from its persistence layer.

Let's start a JMS server to see how it works.

-- Start ActiveMQ server

-- Go to ActiveMQ Web Console

Here we can see the 2 types of channels: Queues and Topics. 

I would say there is a preference for Queues. Let's see why

-- Create queue: queue1

We will create a queue1 and create a message, this message will be stored by the broker until a consumer is connected and receives the message.

-- Run consumer

Once message is received, broker deletes the message. In case of error before commit, broker will resend the message, or after some retries, put it into a deadletter queue. 

I want to highlight a couple of points here: Broker is in charge of the delivery, more load on the broker as it has to be aware of consumers and how they react to the messages. 
Also they manage state and that makes them more difficult to scale.

What was the message produced? What if I want to reprocess the message for some reason? In this cases we have to go to the producer and regenerate the message, ok if you manage it, 
more difficult if it is out of your context: External service like Twitter, SAP, etc. it could involve cost, etc.


Now let's try with Topics.

This are used when you want multiple different consumers, potentially different systems, that consume messages from the same channel. 

-- Create topic

If we create a simple consumer and messages will be dequeued by consumer.

-- Create first consumer

If we create another one, it will miss messages already consumed for the first one, maybe not a problem if it is the first time that you have started, but if it die and starts again could be an issue.

-- Create another and simulate fail

In those cases we can create a "Durable Subscriber" 

-- Create durable subscriber

As you can see messages counts start getting more complex to follow. If this traditional behavior fit with your integrations there is no problem

**But as we can see, ordering and reprocessing could be an issue with traditional messaging**

Let's started a Kafka broker to check how different it behaves.

-- Start broker

First of all, we can see that we need Zookeeper, that it is use as internal coordinator.

-- Start simple consumer

As with JMS, when you create a consumer, a topic is created (with options by default - you can avoid this to force create topics first), and it will start polling messages. 

Let's be clear here that the Consumer is in charge of polling. This could look similar to what a DbAdapter do in SOA Suite or OSB.

Now let's create a partitioned topic, this will mean that a log will be splitted in different partition and each partition could potentially be consumed individually.

-- Create topic with 3 partitions

Partitions will be the unit of paralellism, this meaning that if you have 20 partition you can consume them with 20 consumer instance from the same consumer group, in parallel.

-- Start 1, then 2 and 3 instances of the same consumer group.

And if we started another consumer group, it will handle its consumption independently. 

With this I want to let clear the use cases where JMS like systems and Kafka ones differ: Job Queues are enough with JMS and Event Logs are more appropiate with Kafka.

## Kafka Cluster

Now let's move to the next part: Let's focus a moment in the Cluster side.

The first point here is to focus in the relation with Zookeeper. 

Why do we need Zookeeper for? 

At first view one would say that having another process to manage is more complicated. This is partially true.

It is more complex to manage, but at the same time it handle complex tasks to deal when you are talking about distributed systems. For instance: in which broker store a topic? which broker contains the leader?

Let's see for instance how it manage replica leader and at the same time undestand better basic topic concepts like: partitions and replicas.

Thinking about CAP theorem, Partitions obviously handles Partitioning, and Replicas manage Availability.

-- Start Kafka cluster

-- Create simple topic

-- Connect to Zookeeper and check data structure

-- Scale brokers

-- Create a topic with 5 partitions and 3 replication factor

Here we have a topic with 15 partition replicas, 3 per partition.

With more partitions, less consistency between partitions, but more throughput. 

With more replicas, more availability, but less throughput.

A good idea here is to define use cases, and create templates for topics: balance the options depending on the use case.

## Kafka Cleanup Policy

Another key concepts to manage is how Kafka manage cleaning your log.

As with your application logs, you need to rotate or eliminate files right? Kafka internally manage segments per partition. This means that writes will be on the most recent segment.

But how to delete files? depends on the use case.

If you don't want to delete files, you can use compaction (that comes enabled by default)

You have to choose or delete or compact your log.

If you choose deletion, you can choose between by time or by size. This mean how old should be your records to be deleted. Or how big is your log to start deleting files.

If you choose compation, this means that you logs will be stored "for ever" but Kafka will try to save space by compacting records with the same key value. 

The cleaning and compacting process will run through the "old" segments, so writing performance won't get affected.

-- Show how topics are created to enable this.

## Log Records

Another important point is that Kafka records are stored as Key Value. 

You can serialize and deserialize them as you need: as Int, String, bytes, JSON, etc.

One main issue here: think about how to manage schema evolution. Avro project works on this topic, and the Confluent platform relies on this.
We will see more on this later.

## Kafka APIs

Finally I want to talk a bit about the APIs, there are 4 main APIs:

2 basic APIs: Consumer and Producer
and 
2 more specialized: Streams and Connector.

We can go deeper on consistency options as exactly once processing, batching and more, but I will let my presentation in OUGN to check those details.

I want to give a better idea about each API seing them in a couple of Demos:

## Demo 1: Twitter to your App

## Demo 2: Infosak CQRS
