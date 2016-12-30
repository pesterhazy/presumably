---
title: "Reagent mysteries"
subtitle: "Part 2: Why doesn't the page reload?"
author: Paulus
draft: true
---

One of the cool features about Reagent is that it enables a reloading-enabled
workflow. After starting the web app, you can edit a source file and, after a
few seconds, explore the change in the running browser session. Importantly, the
app's state is preserved accross reloads. Especially when building UIs, this can
be a powerful development tool.

> Creators need an immediate connection to what they're creating. [Bret Victor,
> Inventing on Principle](http://blog.ezyang.com/2012/02/transcript-of-inventing-on-principleb/)

We have two goals:

- Relaoding should be *reliable*. After making a change, you need to be able
  rely on those changes taking effect. If there are any errors or warnings along
  the way, you should be notified clearly.

- The edit/compile/test cycle needs to be *fast*. You want to wait for changes
  for a few seconds, maximum. Any delays threaten to break the Victor's Immediate
  Feedback principle.

Getting started, the first hurdle to overcome is tooling. Often when people have
issues with ClojureScript, tooling may be the issue. Fortunately, these days
it's relatively straight-forward to get started with a project template. Working
with reagent, I recommend two projects that have reloading enabled out of the box:

- [tenzing](https://github.com/martinklepsch/tenzing), based on boot and
  `boot-reload` (which displays figwheel-like in-browser warnings)

   ```
boot -d seancorfield/boot-new new -t tenzing -a +reagent -n my-test
   ```

- [chestnut](https://github.com/plexus/chestnut), based on leiningen and figwheel

   ```
lein new chestnut my-test +reagent
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

Code changes in your root component are somtimes not picked up after reloading.
The solution is simple. If your code looks like this:

```
(defn root []
   [:div "Where the magic happens"])

(r/render [root]
                    (.getElementById js/document "container")))
```

wrap the component in an anonymous function instead:

```
(r/render (fn [] [root])
                    (.getElementById js/document "container")))
```

## dereffing atoms

If state updates don't trigger re-renders, one common reason is that you're
accessing the atom instead of its contents.

This problem is compounded by the fact that `get` in Clojure(Script) doesn't
throw if you pass it an atom. Compare:

```
cljs.user=> (def !state (atom {:loading true}))
#'cljs.user/!state

cljs.user=> (get !state :loading)
nil ;; D'OH!

cljs.user=> (get @!state :loading)
true
```

Call this a design choice, or call it an oversight that cannot be fixed without
introducing a breaking a change. In any case, if state doesn't propagate, check
every usage of your state atom -- may you forgot the `@` symbol.

One typographic convention that can help here -- admittedly not a very common
one -- is to always prefix variables names for atoms with an exclamation mark:

```(let [state @!state] ...)```

If you follow this convention, any use of `!state` not in the context of `@`
(`deref`), `swap!` or `reset!` looks suspicious.

## defonce and ratom

Also defonce

(get !state :foo)

(get @!state :foo)

exclamation mark convention

## React exceptions

## multimethods
