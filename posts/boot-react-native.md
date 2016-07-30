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

* Install `npm`
* Install `react-native-cli`

You'll also need a development setup specific to the platform you want to target

For iOS:

* XCode

For Android:

* Android dev set up
* Genymotion device emulator

All of these steps are explained in the React Native documentation.

To work with cljsrn, you'll need to install Java and Boot.

Next install Boot React Native:

```
git clone https://github.com/mjmeintjes/boot-react-native.git
cd boot-react-native
boot inst
```

This installs latest `master` into your local maven repository.

Boot React Native also comes with an example application called, logically
enough, *SimpleExampleApp*. The app has the following basic structure:

```
 example
├── app
│   ├── android
│   │   └── ... android-specific files
│   ├── build
│   │   └── ... build output
│   ├── index.android.js
│   ├── index.ios.js
│   ├── init.js
│   ├── ios
│   │   └── ... iOS-specific files
│   ├── node_modules
│   │   └── ... react native dependencies
│   └── package.json
├── build.boot
├── node_modules
├── resources
│   ├── dist.cljs.edn
│   ├── externs.js
│   ├── react.ext.js
│   └── react.native.ext.js
├── rn-goog-require.patch
└── src
    └── mattsum
        └── simple_example/core.cljs
```

The `example/` directory includes all files relating to the ClojureScript
project. The subdirectory `app/` contains the React Native portion of the
infrastructure required. Again, the `ios/` and `android/` subfolders contain the
ObjectiveC and Java code that forms the "native" shell around the React Native
application. Hopefully you'll only need to interact with these native parts in
rare situations, so you can focus your attention on the ClojureScript-based
application code.

Next, let's set up *SimpleExampleApp*.

```
$ cd example/app
$ npm install
```

This installs `react-native 0.30.0` and all its dependencies. Unfortunately,
currently Boot React Native requires a small patch to enable its live-reloading
functionality. To apply this patch to *SimpleExampleApp*, type:

```
$ cd example
$ patch -d app/node_modules/react-native -p1 < rn-goog-require.patch
patching file packager/react-packager/src/JSTransformer/worker/extract-dependencies.js
```

You'll need to repeat this step every time you re-install react-native. This
step is a bit annoying, but will hopefully be replaced by a more automatic
siutation in the future.

# Starting the app


# Further reading

Please check out boot-react-native. If you run into issues, check out the
[troubleshooting guide](https://github.com/mjmeintjes/boot-react-native/wiki/Troubleshooting).
