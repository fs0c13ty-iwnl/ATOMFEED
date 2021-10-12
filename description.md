### ALL RIGHTS RESERVERD BY THIS GITHUB ACCOUNT ###

[IMPORTANT] DO NOT COPY OR USE ANY CODE FROM THIS FOR YOUR PERSONAL USE (#assignments, proejcts)

### THE PROJECT IS FOR DISPLAY ONLY ###

### Objective

To gain an understanding of what is required to build a client/server system, by building a simple system that aggregates and distributes ATOM feeds.

### Introduction

Information management and tracking becomes more difficult as the number of things to track increases. For most users, the number of web pages that they wish to keep track of is quite large and, if they had to remember to check everything manually, it's easy to forget a webpage or two when you're tired or busy. Enter *syndication*, a mechanism by which a website can publish summaries as a feed that you can sign up to, so that you can be notified when something new has happened and then, if it interests you, go and look at it. Initial efforts in the world of syndication included the development of the RSS family of protocols but these are, effectively, not standardised. The ATOM syndication protocol is a standards-based approach to try and provide a solid basis for syndication. You can see the ATOM RFC [here (Links to an external site.)](http://tools.ietf.org/html/rfc4287) although you won't be implementing all of it!

XML-based formats are easy to transport via Hypertext Transport Protocol (HTTP), the workhorse protocol of the Web, and it is increasingly common to work with a standard format for interchange between clients and servers, rather than develop a special protocol for one small group of clients and servers. Where, twenty years ago, we might have used byte-boundary defined patterns in transmitted data to communicate, it is far more common to use XML-based standards and existing HTTP mechanisms to shunt things around. This is socket-based communication between client and server and does not need to use the Java RMI mechanism to support it - as you would expect as you don't have to use an RMI client to access a web page! In this prac, you will take data and convert it into ATOM format and then send it to a server. The server will check it and then distribute a limited form of that data to every client who connects and asks for it. When you want to change the data in the server, you overwrite the existing file, which makes the update operation *idempotent* (you can do it as many times as you like and get the same result). The real test of your system will be that you can accept PUT and GET requests from other students on your server and your clients can talk to them. As always, don't share code.

### Syndication Servers

Syndication servers are web servers that serve XML documents which conform to the RSS or ATOM standards. On receipt of an HTTP GET, the server will respond with an XML response like this (from ["Creating an ATOM feed in PHP" (Links to an external site.)](http://www.ibm.com/developerworks/library/x-phpatomfeed/)):

<pre>&lt;?xml version=&#39;1.0&#39; encoding=&#39;iso-8859-1&#39; ?&gt;
&lt;feed xml:lang=&#34;en-US&#34; xmlns=&#34;http://www.w3.org/2005/Atom&#34;&gt;
        &lt;title&gt;Fishing Reports&lt;/title&gt;
        &lt;subtitle&gt;The latest reports from fishinhole.com&lt;/subtitle&gt;
        &lt;link href=&#34;http://www.fishinhole.com/reports&#34; rel=&#34;self&#34;/&gt;
        &lt;updated&gt;2015-07-03T16:19:54-05:00&lt;/updated&gt;
        &lt;author&gt;
                &lt;name&gt;NameOfYourBoss&lt;/name&gt;
                &lt;email&gt;nameofyourboss@fishinhole.com&lt;/email&gt;
        &lt;/author&gt;
        &lt;id&gt;tag:fishinhole.com,2008:http://www.fishinhole.com/reports&lt;/id&gt;
        &lt;entry&gt;
                &lt;title&gt;Speckled Trout In Old River&lt;/title&gt;
                &lt;link type=&#39;text/html&#39; href=&#39;http://www.fishinhole.com/reports/report.php?id=4&#39;/&gt;
                &lt;id&gt;tag:fishinhole.com,2008:http://www.fishinhole.com/reports/report.php?id=4&lt;/id&gt;
                &lt;updated&gt;2009-05-03T04:59:00-05:00&lt;/updated&gt;
                &lt;author&gt;
                        &lt;name&gt;ReelHooked&lt;/name&gt;
                &lt;/author&gt;
                &lt;summary&gt;Limited out by noon&lt;/summary&gt;
        &lt;/entry&gt;
        ...
&lt;/feed&gt;

</pre>

The server, once configured, will serve out this ATOM XML file to any client that requests it over HTTP. Usually, this would be part of a web-client but, in this case, you will be writing the aggregation server, the content servers and the read clients. The content server will PUT content on the server, while the read client will GET content from the server.

### Elements

The main elements of this assignment are:

* An ATOM server (or aggregation server) that responds to requests for feeds and also accepts feed updates from clients. The aggregation server will store feed information persistently, only removing it when the content server who provided it is no longer in contact, or when the feed item is not one of the most recent 20.
* A client that makes an HTTP GET request to the server and then displays the feed data, stripped of its XML information.
* A CONTENT SERVER that makes an HTTP PUT request to the server and then uploads a new version of the feed to the server, replacing the old one. This feed information is assembled into ATOM XML after being read from a file on the content server's local filesystem.

All code elements will be written in the Java programming language. Your clients are expected to have a thorough failure handling mechanism where they behave predictably in the face of failure, maintain consistency, are not prone to race conditions and recover reliably and predictably.

## Summary of this prac

In this assignment, you will build the aggregation system described below, including a failure management system to deal with as many of the possible failure modes that you can think of for this problem. This obviously includes client, server and network failure, but now you must deal with the following additional constraints (come back to these constraints after you read the description below):

1. Multiple clients may attempt to GET simultaneously and are required to GET the aggregated feed that is correct for the Lamport clock adjusted time if interleaved with any PUTs. Hence, if A PUT, a GET, and another PUT arrive in that sequence then the first PUT must be applied and the content server advised, then the GET returns the updated feed to the client then the next PUT is applied. In each case, the participants will be guaranteed that this order is maintained if they are using Lamport clocks.
2. Multiple content servers may attempt to simultaneously PUT. This must be serialised and the order maintained by Lamport clock timestamp.
3. Your aggregation server will expire and remove any content from a content server that it has not communicated within the last 12 seconds. You may choose the mechanism for this but you must consider efficiency and scale.
4. All elements in your assignment must be capable of implementing Lamport clocks, for synchronization and coordination purposes.
