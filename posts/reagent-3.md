---
title: "Reagent Mysteries"
subtitle: "Part 3: Manipulating the DOM"
uuid: e2a369b7-b5ef-4206-a85b-751834440dc2
author: Paulus
draft: true
---

So you're enjoying React with its functional abstractions and
uni-directional data flow. And yet you still crave direct DOM manipulation. You
ignore the warnings, you know it will mess with your karma. Now what?


In truth, of course, there are legitimate reasons for reaching into the DOM.
Sometimes you need to circumvent React for performance reasons. But a more
common reason is that some DOM elements have an inherently imperative API. For
example, the HTML5 `<video>` element has methods, `.play` and `.pause`, that
change playback status.

These are methods of the DOM element. However, when you're building a component
in React, you don't usually touch the DOM itself. Instead, React components
figure in an internal data structure that mirrors the hierarchy of the DOM but
shields you from its details.

This hierarchy of React elements, containing only immutable values, is what
makes React's so simple to reason about. Still there's no denying
that pausing a video will inevitably involve mutating state in some form.

React's core abstraction is the component. A component class, of course, can be
instantiated many times on any given page. The concrete representation of the
page element on the JavaScript heap is called the component's backing instance.
For custom components written in JavaScript (or ClojureScript), this instance is
a JavaScript object, which can have methods as well as props and state. But not
everything is a custom component, as otherwise nothing would ever be displayed in
your browser window. For native components such as `<div>`s or `<video>`s, the
backing instance is the DOM node associated with the component.

If all we ever see in React-land is elements and components, how can we access
a video's backing instance? The answer is to attach a callback ref to the
element:

```clojure
(defn video-ui []
  (let [!video (atom nil)] ;; clojure.core/atom
    (function [{:keys [src]}]
      [:div
       [:div
        [:video {:src src
                 :style {:width 400}
                 :ref (function [el]
                        (reset! !video el))}]]
       [:div
        [:button {:on-click (function []
                              (when-let [video @!video] ;; not nil?
                                (if (.-paused video)
                                  (.play video)
                                  (.pause video))))}
         "Toogle"]]])))
```

When the rendering function is called initially, the video DOM node has not been
created yet. By passing the special `ref` prop to a component, you tell React
that you want to be notified once the DOM node has been created. Conveniently,
the anonymous function receives as its argument the backing instance. A useful
pattern is to store the reference to the node in an atom for later use.

Note that the ref callback is called twice in the component's lifetime, when the
DOM element is created but also when it is destroyed. In the second case, React
simply passes nil to the callback. For this reason it's good practice to check
the ref before accessing its attributes and methods, a precaution that prevents
TypeErrors. After performing this check, the button's on-click handler calls the
appropriate method on the DOM node.

A few notes on the implementation:

- We use a clojure.core/atom instead of a ratom to store the ref, as we don't
  deref the atom in the render function or want the component to re-render when
  the video node is created.

- The component is implemented as a
  [Form-2 component](https://github.com/Day8/re-frame/wiki/Creating-Reagent-Components#form-2--a-function-returning-a-function).
  In this style, props need to be specified as arguments to the inner
  (rendering) function. The outer (component-creating) function often does not
  need to see the props. Using the fact that JavaScript allows you to call
  functions with the wrong arity, we simply leave out the `src` prop in the
  declaration.

- Earlier versions of React only supported string refs. Callback refs,
  introduced in recent versions of React, are elegant and a better fit for
  reagent, so you should prefer them where you can. String refs and the related
  [findDOMNode](https://facebook.github.io/react/docs/react-dom.html#finddomnode)
  or
  [r/dom-node](http://blog.ducky.io/reagent-docs/0.6.0-alpha2/reagent.core.html#var-dom-node)
  API is now
  [considered an anti-pattern](https://news.ycombinator.com/item?id=12089685).

- The next step in building a video player could be to wrap this stateful,
  mutating beast in a clean React component. Playback state could be represented
  using a `playing?` prop. This is a powerful React pattern: encapsulate messy
  DOM-manipulation in a reusable component with a clean, functional interface. This
  exercise is left to the reader.

## Further Reading

The code for this example is available [on github](https://github.com/pesterhazy/presumably/blob/master/example-src/example/refs.cljs#L5).

React comes with good documentation on [refs](https://facebook.github.io/react/docs/refs-and-the-dom.html) and
[backing instances](https://facebook.github.io/react/blog/2015/12/18/react-components-elements-and-instances.html).

For an in-depth explanation of React component from a ClojureScript perspective,
the [Lambda Island
episode on React](https://lambdaisland.com/episodes/react-app-clojurescript) is
excellent (paywalled but with a free trial available).

[React From Zero](https://github.com/kay-is/react-from-zero) is a useful step-by-step
guide to React's concepts in JavaScript

