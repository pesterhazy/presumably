---
title: "Reagent Mysteries"
subtitle: "Part 4: Children and Other Props"
uuid: 66088d3e-a7ef-4d86-a0fb-1c4a3b97e540
author: Paulus
date-published: 2017-01-20
---

ReactDOM.render — essentially React's single public API function — renders a root component based on a set of arguments, or props, that determine the component's look and behavior. Transforming its inputs and expanding sub-nodes recursively, render constructs a tree of elements ready to be mounted to the DOM. In React-land data flows, in the form of props, from parent to children.

Reagent follows the same principles but thereby hangs a tale. In React, every component receives as its single argument a JavaScript object called _props_. Each prop is a key associated with a (hopefully) immutable value. Props can be anything you wish: strings, numbers, objects or, just as commonly, callback functions. In plain React, props are typically passed by way of [JSX syntax](https://facebook.github.io/react/docs/jsx-in-depth.html), which looks like XHTML:

```
<MyUI name="Smith" age={72}}>
  <Child/>
  <Child/>
</MyUI>
```

Calling a component in Reagent works similarly:

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
[component {:prop1 :val1, :prop2 :val2 ...} child1 child2 ...]
```

React components always receive named props, but Reagent components are more flexible. A Reagent render function could be any ClojureScript function and, as such, can take any number of positional arguments.
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
    [:li "Taylor"]]])
```

To summarize, React distinguishes between regular props (passed like DOM attributes in JSX) and children (passed as sub-components in the tag body). The latter are available in JavaScript as `this.props.children`, a pseudo-array that contains the sub-components.

By default, Reagent handles props differently. All props, including the component's children, are held in a single React prop and accessible as function arguments to the render function. As a result, Reagent components aren't drop-in replacements for plain React components. However, interop with plain React is possible by using the  functions r/reactify-component, r/as-element and r/adapt-react-class (`[:> ...]`). In interop cases, as well as during non-render lifecycle methods, props and children will be accessible through the r/props and r/children helpers.

A final difference between React and Reagent is that in recent versions React has introduced the notion of PropTypes. If you specify a component's PropTypes, you can make props obligatory or optional, set default values or define the prop's value type. These runtime checks cannot be used when building Reagent components because of the differences in prop format noted above. But it's possible to encounter PropTypes when using third-party JavaScript, so it's useful to be aware of their existence.

## Further Reading

- Facebook's docs on [props](https://facebook.github.io/react/docs/components-and-props.html)
- [Props, Children & Component Lifecycle in Reagent](https://www.martinklepsch.org/posts/props-children-and-component-lifecycle-in-reagent.html)
  explores the implications of Reagent-style props for lifecycle methods.
