# Spring Boot Actuator and aggregating health
**I have no idea if this is codded correctly, I could not find any examples of combining multple services into a single health state. So this is my WAG at solving the problem.**

## Problem
I was adding 'Actuators' to my Spring Boot application per this [Baeldung Article](https://www.baeldung.com/spring-boot-actuators). In my application I had a service which has multiple sub-components, as long as **one** of these sub-components is up then the service itself is considered up. but using the information in the article above each of my services had their own _HealthIndicator_ which then got aggregated with the entire application health. As soon as one of the sub-components went down the application indicated it was down. Apon further reading I noticed section 4.4 which states:
>A handy feature of health indicators is that we can aggregate them as part of a hierarchy.

Unfortunately it doesn't delve into doing this. My first attempt was to make my own aggregator class (based on _AbstractHealthAggregator_) but doing this made the entire system use my aggregator. This would probably be OK if my application is the only one running, but if you have other applications (or services) running then this is not good because I don't want the server to indicate that it is up and running even if some other service is down.

I looked on [StackOverflow](https://stackoverflow.com/questions/51861320/how-to-aggregate-health-indicators-in-spring-boot) but didn't find a lot of answers and the answers I got I had either tried or didn't really didn't apply.

## Solution
I came up with the solution in this tree through frustration and trail and error. This appears to work but I **have absolutely no idea** if this is the _CORRECT_ way to use this functionality.

## Code
This tree contains a simple Spring Boot Actuator with 5 classes

_DemoApplication_ is the Spring Boot application

_MySpringBootController_ is the controller (has 1 handler in it), it also doubles as a _CompositeHealthIndicator_ it also has an embedded _HealthAggregator_ class. When the class is instantiated it creates the aggregator and calls the super classes CTOR. It then calls _addHealthIndicator_ for the 2 services. In my actual application the _CompositeHealthIndicator_ subclass is actually a seperate class which uses the **@Component** annotation.

_Service1_ and _Service2_ These are classes that implement the _HealthIndicator_ interface, it returns a health status based on a random boolean which is set via the a set function.  I found the important thing with these classes is that you **DO NOT** use any of the **@Component** annotations as you do not want these instances created by Spring - otherwise they get sucked into the main health aggregator as well as your own service.

## Running the actuator
The code is sewtup so that when it is started _Service1_ is down and _Service2_ is up, you can see if you go to http://[host]:[port]/actuator/health that this allows the application health to be "Up":

> {"status":"UP","details":**{"myHelloWorldController":{"status":_"UP"_,"details":{"Service 1":{"status":_"DOWN"_,"details":{"Detail1":"Hello"}},"Service 2":{"status":_"UP"_,"details":{"Detail2":"World"}}}}**,"diskSpace":{"status":"UP","details":{"total":499963170816,"free":423310401536,"threshold":10485760}}}}

You can see that the application is UP, and the 'myHelloWorldController' is also up even though "Service 1" is down. Accessing the _'/helo'_ url will randomly set the services to up or down.
