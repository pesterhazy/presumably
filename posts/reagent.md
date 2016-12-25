Reagent is a popular, practical ClojureScript wrapper for React, but is it
mysterious?. Its popularity is easily explained. It uses hiccup syntax as an
elegenat way to describe the DOM as a hierarchy of React components, where each
component is a simple ClojureScript function. Furthermore, to aid tracking the
efficient re-rendering of scenes, it introduces ratoms as an extension of
Clojure's atom abstraction, a powerful addition to React's props mechanism.

At the same time, Reagent is practical in that, wherever its model is too
restrictive, it gives the programmer the espace hatches and efficiency hacks to
build fast real-world applications.

Reagent is magical, and yet it's true that Reagent's abstractions, while useful
and often helpful, are sometimes also leaky. As a result, sometimes knowledge of
the implementation is helpful to find out how things work. Reagent may look
mysterious. In this series of blog posts, I will attempt to dispell this air of
mystery by explaining the underlying concepts.

## Building a table

This first installation tackles a simple question: how to build a table with
Reagent. React requires tables to be properly structured, including thead and
tbody elements, so we know what the hiccup representation of the DOM should
look like:

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

Next, we can build a component that takes the same arguments as print-table:

```
(defn table-ui [cols rel]
  [:table
   [:thead
    (map name cols)]
   [:tbody
    (map (partial row-ui cols) rel)]])

```

The -ui suffix is a hint that we're dealing with a render function. The render
function for individual rows is simple:

```clojure
(defn row-ui [cols m]
  [:tr (map (partial get m) cols)])
```

We can see the result in the browser:

FIXME: screenshot

## Vectors and sequences

So much for the code, but why does this work? Reagent adds to React the
convenience of expressing components as simple ClojureScript functions.
As table-ui does not deal with state, it contains only a render function. In
Reagent-speak this is a Form-1 component.

When the component is rendered, the hiccup values returned by the cljs function
is converted into hierarchy React elements, React's internal representation of
the DOM. To see how this works, we can call the function from the REPL. Here's
what it returns:

```
[:table
 [:thead ([:th "name"] [:th "country"] [:th "date"])]
 [:tbody
  ([:tr ([:td "Descartes"] [:td "France"] [:td 1596])]
   [:tr ([:td "Quine"] [:td "U.S.A."] [:td 1908])])]]
```

Notice that this is not quite the same as what we intended. Compare the header
to our original plan:

```
[:thead [:th "name"] [:th "country"] [:th "date"]]
```

The generated markup is wrapped in a pair of parentheses, which in Clojure
signifies a sequence (or list, which has similar properties). A quick look at
the code explains this easily: map returns a single value, a sequence. We
could modify table-ui to build the intended markup explicitly:

```
(into [:thead]
      (map (fn [col] [:th (name col)]) cols))
```

And yet, our initial, more concise attemp works. Why?

The answer is that Reagent (and before it, hiccup) anticipated this usage and,
in the course of transforming hiccup syntax to React elements, automatically
expands non-vector sequences into the enclosing elements.

So table-ui works as originally written. It also has the consequence that row-ui
does not actually function as a React component; it is evaluated and its result
spliced into the tbody element.

## Gotchas

This is a convenient shortcut, but like many conveniences, there's a price.
First, in Clojure vectors implement the sequence interface, and a vector can
often be exchanged transparently in place of sequences and vice versa. But here
you need to keep in mind that Reagent confers to vectors (but not to sequences)
the special significance of representing components or DOM elements.

So simply converting the sequence won't work:

```
(into [:thead]
      (vec (map (fn [col] [:th (name col)]) cols)))
```

Reagent will try to interpret a vector of vectors as a component. This cannot
work, as a vector is not a valid component specifier.

The second gotcha is that, while `into` explicitly realizes the lazy sequence,
leaving the sequence unrealized is dangerous when you rely on ratoms being
dereferenced in the course of its realization. The effect of this issue is that
everything will seem to work, but the component will not be rerendered when the
state changes.

Fortunately the fix is simply to wrap the `map` (or `for`) call in `doall`to
force its realization before the render function returns:

```
(into [:thead]
      (doall (map (fn [col] [:th (name col)]) cols)))
```

Happily, there's also a non-gotcha to report. Suppose you want to hide some
philosophers, based on their date of birth. A convenient shortcurt is simply to return
nil for rows to be ignored:

```
(defn row-ui [cols m]
  (when (>= date 1900)
    [:tr (map (fn [col] [:td (get m col)]) cols)]))
```

The upshot is that Reagent is a good sport and will ignore any child elements that
evaluate to nil, a feature that is turns out to be useful in building UIs. The
exception to this is when Reagent tries to interpret a vector whose first
element is nil; in this case it will try to call nil, with a predictable result.