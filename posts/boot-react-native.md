# Getting your feet wet with boot-react-native

Mobile applications is ClojureScript's next frontier. As a compile-to-javascript
language, ClojureScript can run on mobile devices, targetting Facebook's
wonderful React Native framework. The combination is powerful: React Native is
comprehensive and performant, and ClojureScript adds a powerful abstractions and
great suport for interactive development.

This last point cannot be stressed enough. Sophisticated UIs, as required in
mobile applications, are best built interactively, with a short feedback cycle.
As a LISP, ClojureScript is in a great position to make programmers creative and
productive for app development.

So can we build React Native apps in ClojureScript today? The good news
is that most of the parts of the CLJSRN puzzle exist and work well. The bad news:
building apps for iOS and Android with interactive reloading brings with it
significant tooling challenges.

Boot React Native is a tool that intends to make it easy for
developers to get started. In this post, I'll explain how to get a basic CLJSRN
setup running.

# First steps

As CLJSRN is built on React Native, the first step is to set up the React Native
development environment:

* Install `react-native-cli`

* Install the tools required

* Set up a simulator


# Further reading

Please check out boot-react-native. If you run into issues, check out the
[troubleshooting guide](https://github.com/mjmeintjes/boot-react-native/wiki/Troubleshooting).
