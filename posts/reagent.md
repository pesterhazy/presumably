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

## Building a table

How do you build a table with Reagent? React requires tables to be properly
structured, including `thead` and `tbody` elements, so we know what the hiccup
representation of the DOM should look like:

```clojure
[:table
 [:thead
  [:th "name"] [:th "country"] [:th "date"]]
 [:tbody
  [:tr
   [:td "Descartes"] [:td "France"] [:td "1596"]]
  [:td
   ;; ...
   ]]]
```

In good Clojure tradition, we start with a data structure:

```clojure
(def philosopher-cols
  [:name :country :date])

(def philosophers
  [{:name "Descartes"
    :country "France"
    :date 1596}
   {:name "Quine"
    :country "U.S.A."
    :date 1908}])
```

Each row is a map of attribute to value. Because we chose a common format, we
can print this structure in the cljs repl using `clojure.core/print-table`:

```
example.table=> (clojure.pprint/print-table philosopher-cols philosophers)

|     :name | :country | :date |
|-----------+----------+-------|
| Descartes |   France |  1596 |
|     Quine |   U.S.A. |  1908 |
```

Next, we can build a component that takes the same arguments as `print-table`:

```
(defn table-ui [cols rel]
  [:table
   [:thead
    (map name cols)]
   [:tbody
    (map (partial row-ui cols) rel)]])

```

The `-ui` suffix is a hint that we're dealing with a render function. The render
function for individual rows is simple:

```clojure
(defn row-ui [cols m]
  [:tr (map (partial get m) cols)])
```

We can see the result in the browser:

FIXME: screenshot

## Vectors and sequences

So much for the code, but why does this work?
