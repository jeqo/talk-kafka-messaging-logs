# Kafka: From Messaging To Event Logs

## Intro

This talks has been developed after a couple of years working with Apache Kafka.

The aim of this talk is to introduce you, as integration developer used to work with Databases and Message Queues, 
to a "new" way of manage and move data around your systems.

### Comparison between Messages Queues and Event Log Topics

I'd like to start reviewing some basic concepts about Messsaging Systems:

Going back to one of the key book in Systems Integration, 'Enteprise Integration Patterns' explore different ways to put messaging as the 
key mechanism in our Architecture. 

Let's review some of the basic concepts then: 

Messaging is recognized as an asynchronous communication mechanism that works as an intermediate between **Consumers** and **Producers**.

It offers **Channels**, known as Queues or Topics, that connect this program. Where a Sender or Producer program is the one that send messages 
writing them to the Channel, the Channel acknowledges that the message is saved. 

Then one, or several Consumers receive this messages, by reading and deleting it from the Channel.

Nothing special here, a technology capable of decoupling systems, **that store messages until are consumed**.

## Point-To-Point and Pub/Sub

In the case of traditional messaging systems, we have usually configurations: *Point-To-Point* where (commonly) one system produces and send messages 
to the Channel, and one Consumer receives this messages. 
To be more specific, the Broker is in charge of sending the message to the Consumer connected, and sometimes we use Filtering based on Headers to send 
messages to different Consumers, but the premise persist, one Consumer receives the message.

With Publish/Subscribe things change. In this case, several connected Consumers receive the same message. To ensure that all of them receives the same 
message is a bit harder, you have to deal with durable subscribers and TTL and so on.
 
As far as I know, people feel more confortable working with Queues than Topics, and better if you can put everything in one queue (at least with Oracle) 
because there are a lot of components around it that is a pain to be mantaining more queues, connection factories, and so on.

But why? If Topics, at least in theory, seems to be more complete? You can scale consumers, provide information for different systems with the same channel. 

It always depends in the context. Let's defined a couple of use cases to reason about.

### Messaging Challenges

I can mention some Messaging challenges with technologies like JMS but the idea of this presentation is not to say one is better than the 
other but to mention use-cases where one performs better than the other:

Bear with my drawings for a moment. The idea here is show some common issues with messaging:

More than out of order, is reprocessing what I try to point out here:

If you have a Queue, where you put messages of events related to some entity that change its status, you could end up with cases where your consumer 
fail to process one event for 'x' reason and then consumes the next message (because of parallel consumers) and retrying the previous message end up
updating the status with an old state. The same could happen if you use topics, and have several consumers, they could end up in an inconsistent state.

There are probably ways to tweak you broker configuration and send event in the same group, but **the aim here is to denote that Traditional Messaging is
not the best fit for Event messages**.

### Logs

Everyone has work with logs in almost a daily basis here, from checking you Web Server access, to review what went wrong in you application, 
we are use to check our log files to check what and in which order things happen.

Logs, been one of the most simple data structures (only a key and a value that could be anything), are a very powerful tool to have.  

It is ordered by nature and give this idea of when things happened or at least what happen before or after.

### Logs Everywhere

As I told you, logs are everywhere.

One of the key log usage, that actually inspires products like Kafka and stream processors, is Database Transaction Logs: This file is used by Databases
like Oracle to keep clusters or materialized views in sync. A very powerful idea that now is used as inspiration to think about replication and 
synchronization between different systems where we are not talking more about 2 nodes of the same database engine but different data systems as Oracle and 
Elasticsearch for instance, to have a Search Engine in sync with your relational database.

Another example is JTA transaction log that is persisted in you WebLogic domain to do distributed transactions, if this file get corrupted your transaction
could never be completed or you datasources could become inconsistent. 

**How powerful this data infrastructure should be to be used in this critical cases? What if we use it to not only scale your database but your entire architecture?**


### Log-Centric Architecture

As we have more and more influences in our system designs as Microservice Architectures or Reactive Systems, let's consider a new one. 

Kappa Architecture is an approach to put a Log a the hearth of your system, this external log will become your data pipeline between different systems.

Quoting Kappa Architecture: "If you assume an External Log is present, you're individual systems could rely on it and reduce their complexity"

### Solving Messaging Challenges with Logs

Basically what I want to show here is this Ordering/Reprocessing issue could be solved using a log where you consumer each record at the time. 

How to scale in this case? I will be restricted to the processing time, right? 
Well, that's what I mean where it always depends in the use case, but we will see how Kafka give you the tools to actually perform as you need, and adapt to 
different use cases. 

### Messaging use-case: Job Queues

To recap, Job-Queues is a use-case where usually traditional messaging systems work better. In a Job Queue you care about every message to be process or re-processed 
without caring much about the orden.

Remember that this system are Push based, the Broker is in charge of delivering messages to the consumers.

### Event Log use-case: Event Log

And with Event Logs, if you care about order and reprocessing, mainting status of you objects consistent, then Event Logs are better suited for this cases. 

Here we have a Pull based mechanism, where Consumers ask for more records, and they are in control. If they need to go back in time and reprocess, they can move 
along the log to do that.

## Apache Kafka

So let's get more practical now. Given that we understand now what is that difference between messaging and event logs, let's move to the most popular log system, 
now called distributed streaming platform.

I really like todays Kafka documentation, put it really simple to know what it is:

3 capabilitites:

- Let you publish and subscribe to stream of records. (similar to message systems)
- Let you store streams of records in a fault-tolerant way.
- Let you process streams of records as they occur

So, you have persistent pub/sub + stream processing

Stream of records: unbounded set of records.

Good for 2 things:

- Streaming data pipelines that reliably get data between systems and applications.
- Real-time streaming application that transform or react to streams of data.


### Facts

Before we go deep into Kafka, let's check some facts first.

This project born to solve a data pipeline problem (similar to what we have in "Rampe" - client system) at LinkedIn. 

It was open-sourced on 2010, become an Apache project in 2011, gratuated from incubator in 2012 and its creator found Confluent, now our partner :)

### Use Cases

There are several use-cases where Kafka has become its key components, for instance an interesting one CDC, that is about capturing changes in Databases
and them publish them as Events. This is used with Traditional databases given that new ones already have some event publishing capability.

Or Cloud adoption, to be used as a pipeline between Cloud and On-Premise systems.

### Kafka as Batch, Database and Messaging

Not sure how accurate this image is, but I like it because it gives the idea of how functional Kafka is, and depending on how you configure it, you can move 
it close to a Batch processor, or to a Database, or to a Messaging system.

### Tour

To give a first view about its components:

Kafka runs as a Cluster of 1 or more brokers.

Where stream of records are categorized as Topics.

This records has Key, Value and Timestamp.

Then, we have 4 key APIs:

Consumer and Producer APIs

Streams API

and Connector API.

We will see from scratch how each component of this Streaming Platform works with some use cases. 


## Kafka Core

In the core of Kafka is the Cluster and how it manage Records.

Let's begin with the Kafka Cluster.

### Zookeeper

Kafka relies in Apache Zookeeper as its coordinator. It is in charge of maintaining consensus between different servers, ensure all replicas are in sync, and 
validating the presence of servers as part of the cluster.

It is the Kafka internal source of truth.

We will see how it works in the first demo.

### Kakfa Topics

As I mentioned, records are categorized in Topics. 

This Topics has 2 properties we have to be very aware of: Partitions and Replicas.

This properties define, as their names said, how partitioned you Topic will be, and how many replicas each partition will have. 

Take into account that order is only ensured by Partition. This means that is possible that one message B, produced after message A, could arrive first to Partition 2 than message A to partition 1. 

And also one Replica is the master, and others are followers that should be in sync to become the master. Zookeeper ensures this.

Retention and Compaction are key aspects, defines how long to store records. You can specify a size limit or a time limit to retain records, and to make it more powerful, 
you can add Compation to remove old messages with the same Key, and reduce more your space usage.

### Availability and Consistency

To understand better this concepts lets check a couple of use cases, the first one focused on activity tracking (imaging Google Analytics), 
basically receving events about clicks from users.Given that this is not critical data, you can afford lossing some messages, you could retein record for 3 days. 
This means a record after 3 days could be removed by the internal cleaner (garbage collector), and you can reset you position to 3 days back to reprocess messages. 
Adding more partitions means you can improve your production and consumption throughput, more partitions more paralellism.
With less replicas of each partition you avoid message passing, but increase the risk to loose messages if enough replicas are lost.
In this case you are priorizing availability.

In the other case, Inventory Management, is much more critical data, you could have prices and description that if you loose them can impact directly your business.
Less partitions means more consistency (order) in the data you are saving in Kafka, and enough paralellism to have a performant system.
More replicas means more reliability against partitions lost. 
Consistency is more important here. 

### Topics to Partitions

Now that we understand the concepts of Topics, Partitions and Replicas, let's go deeper in the Topic Anatomy.

We know that Topics are partitioned in Partitions, each partition has its own records, and this means that each could have different amount of records.

From offsets zero, each partition store records identifying them with an offset that is the position in the log.

### From Partitions to Segments

Then each Partition is divided in Segments. Segments are the administrative unit use by Kafka to group Records. 
Each Segment will have its own Offset Index and its Timestamp Index. 
We can configure the size of the Segment.

### From Segments to Records

Inside a Segment, you have the Records, also known as Messages. 

We have an active segment list that defines from which initial offset to what offset is part of a segmnet. 
Then, deletes are executed on older segments and appends on the most recent segment.
That's why this is so performant. 

### Log Records.

Finally, the Records. This is how Kafka stores records on disk. One of the important things to observe here is how Kafka tries to keep 
compatibility with previous versions. And in new releases there is a new feature: Headers will be added to the message.

### Avro

Important note: Always be aware of Schemas. Remember the old days of canonical models and XSD libraries. Well, Avro is a tool to design and implement serialization based on schemas. 
Having them centralized is very important to share event schemas and validate everyone is working with the same version.

One main benefit is that Avro offer ways to support Forward and Backward compatibility. This is key when you share events with other departments and external clients.

### Demo 1

Scale cluster

Partitions, Replicas and ISR


