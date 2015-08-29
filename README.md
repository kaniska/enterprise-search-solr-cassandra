# README #

This README would normally document whatever steps are necessary to get your application up and running.

### What is this repository for? ###

* Quick summary
** demonstrates Solr Search Query patterns to find direct and correalted information from Mediscene documents
** Examples
(Query without boosting) http://localhost:8983/solr/collection1/select?q=indication:*skin*
(Query with boosting) http://localhost:8983/solr/collection1/select?q=indication:*skin*&defType=edismax&bq=indication:*Herpes*^0.5
Read about the request parameters being used : fl, qf,pf2,ps,mm and tie here: http://wiki.apache.org/solr/DisMaxQParserPlugin.

### How do I get set up? ###

* Summary of set up
** Solr Setup 
** Document Indexing
** Cassandra Setup
** Batch Scheduler 
** Query API
* Configuration
* Dependencies
* Database configuration
* How to run tests
* Deployment instructions

### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Who do I talk to? ###

* Repo owner or admin
* Other community or team contact

* [Learn Markdown](https://bitbucket.org/tutorials/markdowndemo) 
