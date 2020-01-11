---
title: "What the F!GWH33L is a ClojureScript REPL?"
uuid: ec39917b-6828-4373-8552-380aba85a570
author: Paulus
date-published: 2019-01-11
---

Onboarding new developers to ClojureScript teams is a joy, mostly. You get to talk about all the things that make it fun to write web apps in a Lisp. But when we get to the topic of REPLs - arguably the most LISP-y of topics - there's often an awkard pause. While ClojureScript REPLs are well-supported these days, it's far from obvious how they work - and how to get them to work.

In this post, my goal is to explain CLJS repls in a way that I would have liked someone to explain them to me when I was starting out. But most pressing question on the newcomer's mind is how to set up. So while understanding the concepts is the goal, the path I'm taking in the post will be a guid to setting up a working REPL using figwheel main. 

## Getting started

```
clj -Sdeps '{:deps {seancorfield/clj-new {:mvn/version "0.8.4"}}}' -m clj-new.create figwheel-main playground.core -- --reagent

# Generating fresh figwheel-main project.
#    -->  To get started: Change into the 'playground.core' directory and run 'clojure -A:fig:build'
```

Well, that was easy enough!

```
mv playground.core playground # Strange name for a directory
cd playground
```

Let's do some chores

```
$ git init
Initialized empty Git repository in /Users/pe/prg/playground/.git/
$ git add .
$ git commit -m "Init"
$ tree
.
├── README.md
├── deps.edn
├── dev.cljs.edn
├── figwheel-main.edn
├── resources
│   └── public
│       ├── css
│       │   └── style.css
│       ├── index.html
│       └── test.html
├── src
│   └── playground
│       └── core.cljs
├── target
│   └── public
├── test
│   └── playground
│       ├── core_test.cljs
│       └── test_runner.cljs
└── test.cljs.edn
```
