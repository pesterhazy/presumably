---
title: "Reagent Mysteries"
subtitle: "Part 4: Children and other props"
uuid: 66088d3e-a7ef-4d86-a0fb-1c4a3b97e540
author: Paulus
draft: true
---

ReactDOM.render -- essentially React's single public API function -- renders a
root component based on a set of props passed to it. After a series of
transformation of these inputs and expanding components recursively until it hits bedrock, render constructs a tree of elements ready to be mounted in the DOM. Through immutable props, data flows from parent to children. A simple outline, but thereby hangs a tale.

Every component receives as its single argument a JavaScript Object called
_props_. Each prop is a (usually) immutable value associated with a key. Props
can be anything you wish: strings, numbers, objects or, commonly, callback
functions. Plain React uses [JSX syntax](https://facebook.github.io/react/docs/jsx-in-depth.html) to pass props:

```
<MyUI {name: "Smith", age: 72}>
```

The Reagent equivalent is just as readable: 

```clojure
(defn root []
  [my-ui {:name "Smith" :age 72}])
```

Here's one way to define the component:

```clojure
(defn my-ui [{:keys [name age]}]
  [:div "Mr. " name " is " age " years old"])

```

In fact, if you instantiate a plain React component, this is the only way to
instantiate a component:

```clojure
[component {:prop1 :val1, :prop2 :val2 ...} child1 child2 ...}]
```

Whereas a React component always receives named props, Reagent components are
more flexible. A Reagent component is a simple ClojureScript function and, as such, can take any positional arguments.

```clojure
(defn my-ui* [name age]
  [:div "Mr. " name " is " age " years old"])

(defn root []
  [my-ui* "Smith" 72])
```

If we peek under the covers, we can see that Reagent implements this by storing
the _entire_ list of props passed to the Reagent component in a single prop
called `argv`. Note that `argv` contains the entire Hiccup vector. Its first
element is the function representing the component (in our case, my-ui*),
followed by each argument ("Smith" and 72).

Generally it is a good idea to follow the React convention of passing a map of
attributes (or props) as the first argument, followed by children (if any).
Here's an example with children:

```clojure
(defn title-ul-ui [{:keys [title]} & children]
  [:section
   [:h3 title]
   (into [:ul children])])

(defn root []
  (title-ul-ui
   {:title "people"}
   [:li {:key 0} "Smith"]
   [:li {:key 1} "Schmidt"]))
```

This argument format is all but suggested Reagent, which comes with convenience functions that allow you to access the props map as `(r/props (r/current-component))` and the children as `(r/children (r/current-component))` from inside the render fn. These functions will only work if the React convention is followed.

The upside is that r/children also works when you're implementing a plain React
component using Reagent using reactify-component. This is sometimes useful
when interoperating with Reagent components. We can simulate this with the
[`:>` shorcut](https://reagent-project.github.io/news/news060-alpha.html)
introduced in Reagent 0.6.0:

```clojure
(defn title-ul-ui [{:keys [title]}]
  [:div
   [:section
    [:h3 title]
    (into [:ul] (r/children (r/current-component)))]])

(def title-ul-ui* (r/reactify-component title-ul-ui))

(defn root []
  [:div
  [:> title-ul-ui* {:title "people"}
    [:li "Smith"]
    [:li "Hinz"]]])
```
