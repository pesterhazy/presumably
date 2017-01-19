---
title: "Reagent Mysteries"
subtitle: "Part 4: Children and Other Props"
uuid: 66088d3e-a7ef-4d86-a0fb-1c4a3b97e540
author: Paulus
draft: true
---

ReactDOM.render — essentially React's single public API function — renders a root component based on a set of arguments, or props, that determine the component's look and behavior. Transforming its inputs and expanding sub-nodes recursively, render constructs a tree of elements ready to be mounted to the DOM. In React-land data flows, in the form of props, from parent to children.

Reagent follow the same principles but thereby hangs a tale. In React, every component receives as its single argument a JavaScript object called _props_. Each prop is a key associated with a (hopefully) immutable value. Props can be anything you wish: strings, numbers, objects or, just as commonly, callback functions. In plain React, props are typicalled passed using the [JSX syntax](https://facebook.github.io/react/docs/jsx-in-depth.html), which looks like HTML:

```
<MyUI name="Smith" age=72}>
```

Reagent's equivalent looks similar:

```clojure
(defn root []
  [my-ui {:name "Smith" :age 72}])
```

Here's one way to define the component:

```clojure
(defn my-ui [{:keys [name age]}]
  [:div "Mr. " name " is " age " years old"])

```

In fact, if you are working with a plain React component, perhaps from a third-party library, this is the only way to instantiate a component:

```clojure
[component {:prop1 :val1, :prop2 :val2 ...} child1 child2 ...}]
```

React component always receive named props, but Reagent components are more flexible. A Reagent render function could be any ClojureScript function and, as such, can take any number of positional arguments.
```clojure
(defn my-ui* [name age]
  [:div "Mr. " name " is " age " years old"])

(defn root []
  [my-ui* "Smith" 72])
```

If we peek under the covers, we can see that Reagent implements this feature by storing the _entire_ list of props passed to the Reagent component in a single prop called `argv`. Note that `argv` contains the entire Hiccup vector. Its first element is the function representing the component (in our case, my-ui*), followed by each argument ("Smith" and 72).

It is often a good idea to follow the React convention of passing a map of attributes (or props) as the first argument, followed by children (if any). Here is such an example:

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

Another point in favor of this format is that Reagent comes with convenience functions that allow you to access the props map as `(r/props (r/current-component))` and the children as `(r/children (r/current-component))` from inside the render function. r/children, in particular, will only work if the props-first convention is followed.

Conversely r/children also works when you're implementing a plain React component in ClojureScript using reactify-component. This is sometimes useful when interoperating with Reagent components. To see how this works, we can create a plain React component and instantiate it using the [`:>` shorcut](https://reagent-project.github.io/news/news060-alpha.html) introduced in Reagent 0.6.0:

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

## Further Reading

- Facebook's docs on [props](https://facebook.github.io/react/docs/components-and-props.html)
- [Props, Children & Component Lifecycle in Reagent](https://www.martinklepsch.org/posts/props-children-and-component-lifecycle-in-reagent.html)
  explores the implications of Reagent-style props for lifecycle methods.
