Reagent is a popular, practical ClojureScript wrapper for React, but is it
mysterious?. Its popularity is easily explained. It uses Hiccup syntax as an
elegenat way to describe the DOM as a hierarchy of React components, where each
component is a simple ClojureScript function. Furthermore, to aid tracking the
efficient re-rendering of scenes, it introduces ratoms as an extension of
Clojure's atom abstraction, a powerful addition to React's props mechanism.

At the same time, Reagent is practical in that, wherever its model is too
restrictive, it gives the programmer the espace hatches and efficiency hacks to
build fast real-world applications.

And yet it's true: Reagent's abstractions, while useful and often helpful, are
sometimes leaky. As a result, sometimes knowledge of the implementation is
required to know what's going on. Reagent may look mysterious. In this series of
blog posts, I will attempt to dispell this air of mystery by explaining the
underlying concepts.
