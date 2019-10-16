---
title: "Double Bundle"
subtitle: "Integrating NPM dependencis into Clojurescript projects"
uuid: a393ce8b-6032-47af-9e4a-2834e1a14cbf
author: Paulus
---
Lately the Clojuresphere has been abuzz with efforts to make it easy to integrate NPM dependencies in ClojureScript projects. The reason is hardly a mystery. The NPM package repository, warts and all, contains a large amount of high-quality libraries. Access to these libraries instantly gives you leverage.

NPM's original home is server-side development. These days access to NPM is important even - perhaps especially - when your ClojureScript code is targeting the web browser. Toolkits like Reagent, Rum and Om make it easy, if not trivial, to include pre-baked Javascript React components in your ClojureScript projects. Access to battle-tested components like [react-select](https://github.com/JedWatson/react-select), [react-datetime](https://github.com/YouCanBookMe/react-datetime) or frameworks like [reactstrap](https://reactstrap.github.io/) can significantly accelerate frontend development and keeps the incidental complexity of working with the DOM at bay.

So what's the best way to integrate 3rd party Javascript in a ClojureScript project? The default answer is to integrate these dependencies using Maven coordinates from CLJSJS. CLJSJS is a wonderful community effort to re-package common Javascript (and React) libraries in Jar for, for consumption by leiningen or boot. Although CLJSJS is fantastic at what it does, in this post I will describe a different way, which I'll call the "double bundle" approach.

Modern websites usually include their code in the form of a bundle, essentially a concatenation of individual libraries and modules along with some glue code. For ClojureScript, the Google Closure compiler is responsible for generating this bundle. Requiring a cljsjs namespace (like `cljsjs.react`) causes the React.js to be included in the output bundle. Because the library is included as a foreign lib, Closure will not attempt to minify or shrink its contents.

Essentially a convenient bridge between the Maven and NPM world, CLJSJS allows you to specify all your dependencies in a single file (project.clj or build.boot). However, the approach also has drawbacks:

- Every NPM dependency used, and every update, requires manual effort to be re-packaged as a CLJSJS package.
- CLJSJS builds separate mini-bundles in a potentially error-prone process. Care needs to be taken that every library used is marked as "external" and imported from the global namespace (window).
- Inter-library dependencies are handled via the global window object, so if the CLJSJS dependency graph isn't specified correctly, load order error may occurs, often manifested in inscrutable errors like "window.React is undefined".

Of course CLJSJS packages aren't magic, so how do they work? Essentially what they do is to build a single bundle containing the library, including all its internal dependencies. The bundle's outgoing interface is to expose a global var - for react, `window.React`, for react-datetime `window.ReactDatetime`. On the inbound side, the library can also be a consumer of external dependencies. For example, react-datetime assumes the existence of a preloaded `window.React` object.

Many if not all CLJSJS libraries consist of a bundle built using the webpack packager. Webpack is a powerful tool in the Javascript world, perhaps the most popular of its sort (the others in the motely crew are browserify, rollup, Google Closure, and the react-native packager). The packages relies on a [webpack.config.js](https://github.com/cljsjs/packages/blob/297ecba948bf9fe1c0f85f148e641f8c7269b796/react-highlight/resources/webpack.config.js) that defines both inbound ("external") and outbound ("output") dependencies. The result is a `bundle.js` that defines a single global object.

This raises the question - why not rely on webpack to orchestrate NPM dependencies altogether? This is the strategy I will propose in the remainder of this post.

The [double bundle](https://github.com/pesterhazy/double-bundle) example projects demonstrates how to use webpack directly to use NPM dependencies in your Clojurescript project. The net effect is that the project's index.html references two separate bundles, one built by webpack and one built by the Clojurescript compiler and Google Closure compiler combo.

Webpack supports exporting a single var, but we actually need to export multiple libraries as multiple vars. For this reason, the webpack conig uses a special entry point, `library.js`. For each dependency that needs to be visible from Clojurescript, the file contains a line assigning the required module to a global Javascript variable. This is not elegant, but the effect is similar to how CLJSJS works: all dependencies are available as global names, e.g. `js/window.React` or `js/window.ReactDOM`.

With this setup, adding a new NPM dependency is as easy running `yarn add react-datetime`, adding `window.ReactDatetime = require("react-datetime")` to `library.js` and rebuilding the bundle using `yarn build`. Having done this, you can access the library using `(goog.object/get js/window "ReactDatetime")`.

What's more, any library available on NPM should work. Inter-library dependencies will be handled automatically by webpack. Because webpack is popular, chances are getting things to work will require little additional work - and problems will be easy to google.

With the double-bundle approach, a few things need to be kept in mind:

- The two script tags in index.html need to be in the right order - npm-bundle.js first and clojurescript-bundle.js last. For production builds, of course, there's nothing to stop you from concatenating the two bundles into a single bundle.
- To avoid including multiple versions of react and react-dom, these two cljsjs packages should be added to the project-wide global exclusions in project.clj. (Similarly, build.boot also supports global exclusions.)
- By removing the `cljsjs/react` dependency, we also lose the `cljsjs.react` namespace. However, libraries like Reagent require this namespace. The solution is to mock out this namespace with an empty cljs file. This is admittedly somewhat inelegant.

## Summary

Using the double bundle approach is powerful, as it taps into the ecosystem and accumulated wisdom of the NPM community.

## more

- link to package.json and webpack.config.js

## caveats

- global exclusions for react
- externs, not necessary for React, components can be accessed using goog.object/get
- related to npm-deps? no
- two bundles but they can be concatenated

## todo

- reference to baking-soda
- properly attribute cljsjs
