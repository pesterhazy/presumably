---
title: "Reagent mysteries"
subtitle: "Part 2: Reloading?"
author: Paulus
published: Dec 30, 2016
---

One of Reagent's best features is that it enables a reloading-enabled workflow.
After opening your app in the browser, you can edit a source file and, after a
few seconds, explore the change in the running browser session. Importantly, the
app's state is preserved across reloads. Especially when building UIs, this can
be a powerful development tool:

> Creators need an immediate connection to what they're creating. [Bret Victor,
> Inventing on Principle](http://blog.ezyang.com/2012/02/transcript-of-inventing-on-principleb/)

In general, there are two goals:

- Reloading should be *reliable*. After making a change, you need to be able
  rely on those changes taking effect. If there are any errors or warnings along
  the way, you should be notified clearly.

- The edit/compile/test cycle should be *fast*. You want to wait for changes
  for a few seconds at a maximum. Any delays threaten to break Victor's Immediate
  Feedback principle.

Getting started, the first hurdle to overcome is tooling. Often when newcomers
experience issues with ClojureScript, tooling problems are the cause. Fortunately,
these days it's relatively straightforward to get started with a project
template. Working with reagent, I recommend two projects that have reloading
enabled out of the box:

- [tenzing](https://github.com/martinklepsch/tenzing), based on boot and
  boot-reload (which displays figwheel-like in-browser warnings)

   ```
boot -d seancorfield/boot-new new -t tenzing -a +reagent -n my-test
   ```

- [chestnut](https://github.com/plexus/chestnut), based on leiningen and figwheel

   ```
lein new chestnut my-test --reagent
   ```

Either of these projects should provide you with a reliable and fast reloading
mechanism. But what if things go wrong? Below is a list of the common issues.

## Code changes don't have any effect

You're making a change to a render function, and yet your web app doesn't
update? The reason may be that React doesn't get notified of the changes. Reagent
rerenders components when:

- component state changes
- props change
- a ratom changes

But if you only change code, neither of those events occur. Consequently you
need to re-render the scene manually. With Reagent, as with React, this is done
simply by re-mounting the root component, i.e. by re-running `r/render`.

With boot and boot-reload,
[set the on-jsload property](https://github.com/martinklepsch/tenzing/blob/242a30595f63a541b8ada8bd7be0b489ccf522a2/resources/leiningen/new/tenzing/build.boot#L38)
of the `reload` task to a function that [re-mounts the root](https://github.com/martinklepsch/tenzing/blob/b9fb8f596005d5dce36468f4880453f74fecf421/resources/leiningen/new/tenzing/app.cljs#L3).

With leiningen and figwheel, you can do the same by simply rendering the component
[inside the core namespace](https://github.com/plexus/chestnut/blob/fa2764cfeb3bd0df80a244dbb8cc47f29b903c2d/src/leiningen/new/chestnut/src/cljs/chestnut/core_reagent.cljs#L11).


## The root-component doesn't update

Code changes in your root component are sometimes not picked up after reloading.
The solution is simple. If your code looks like this:

```clojure
(defn root []
   [:div "Where the magic happens"])

(r/render [root] (.getElementById js/document "container")))
```

wrap the component in an anonymous function instead:

```clojure
(r/render (fn [] [root])
          (.getElementById js/document "container")))
```

## Dereffing atoms

If state updates don't trigger re-renders, one common reason is that you're
accessing the ratom instead of its contents.

This problem is compounded by the fact that `get` in Clojure(Script) doesn't
throw if you pass it an atom. Compare:

```
cljs.user=> (defonce !state (r/atom {:loading true}))
#'cljs.user/!state

cljs.user=> (get !state :loading)
nil ;; D'OH!

cljs.user=> (get @!state :loading)
true
```

Call this a design choice, or call it an oversight that cannot be fixed without
introducing a breaking a change. In any case, if state doesn't propagate, check
every usage of your state atom -- may you forgot the `@` symbol.

One typographic convention that can help here is always to prefix variables
names for atoms with an exclamation mark:

```clojure
(let [state @!state] ...)
```

If you follow this convention, any use of `!state` not in the vicinity of @,
deref, swap! or reset! looks suspicious.

## Ratoms and defonce

Speaking of state, did you check that your `!state` is actually contained in a
ratom? Remember, a ratom is like a clojure.core/atom, except that it
automatically remembers whether it was dereffed in each render function and, if
so, marks that function as requiring a re-render when the ratom is swapped.

I often accidentally write

```clojure
(defonce !state (atom {}))
```

and forget to refer `reagent.core/atom` as `atom` in the namespace declaration.
For this reason, I prefer explicitly specifying the qualified function
name `r/atom`:

```clojure
(ns my-test.core
  (:require [reagent.core :as r]))

(defonce !state (r/atom {}))
```

Finally, make sure you're using defonce, not def, to define the variable.
Otherwise, each reload will reset the atom to its initial state.

## Laziness

When returning sequences, be careful to
[realize any lazy sequences](reagent.html#gotchas) you create, using e.g. map or
for.

## Exceptions in React

Sometimes things go wrong. Like any dynamic language, ClojureScript has Null
Pointer Exceptions in the shape of the inimitable truism `undefined in not a
function`. Normally, you'll just fix the bug, save your file and expect the
issue to be fixed in the running browser window.

Unfortunately, if an exception
[occurs in a Reagent](https://github.com/reagent-project/reagent/issues/272) (or
React) render function, React messes up, essentially leaving the entire
component tree in a corrupted state. The reasons behind this are ultimately due
to issues with JavaScript exception handling, but this behavior is obviously
annoying in a reloading-based workflow.

The good news is that the React developers are aware of
[this issue](https://github.com/facebook/react/issues/2461) and have introduced
error barriers as a new feature to keep render exception from corrupting the
tree. If you want to try this experimental feature introduced in React 0.15 in a
Reagent project, try
the code in [this gist](https://gist.github.com/pesterhazy/d163a8b3f1f1c6a0dac235858776c14b).
