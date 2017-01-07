---
title: "Reagent Mysteries"
subtitle: "Part 4: Children and other props"
uuid: 66088d3e-a7ef-4d86-a0fb-1c4a3b97e540
author: Paulus
draft: true
---

ReactDOM.render -- essentially React's single public API function -- renders a component based on a set of props passed to it. After a pure transformation these inputs, expanding components recursively until it hits bedrock, it constructs a tree of elements ready to be rendered to the DOM. Through immutable props, data flows from parent to children. A simple outline, but thereby hangs a tale.

Every component receives as its single argument a JavaScript Object called
_props_. Each prop is a (usually) immutable value associated with a key. Props
can be anything you wish: strings, numbers, objects or, commonly, callback
functions.

Whereas a React component receives named props, Reagent components are more
flexible.

```clojure
(defn my-ui [name age]
  [:div "Mr. " name " is " age " years old"])

(defn root []
  [my-ui "Smith" age])
```

As you can see, a Reagent component is a simple ClojureScript function and, as
such, can take any arguments.
