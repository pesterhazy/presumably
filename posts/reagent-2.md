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
mechanism. But what if things go wrong? Below is a list of the common issues:

## root-component doesn't update

recommended:

```
(fn [] [root])
```

## defonce and ratom

If state updates don't trigger re-renders, one common reason is that the var
actually refers to an atom, not a ratom.

Also defonce

## dereffing atoms

(get !state :foo)

(get @!state :foo)

exclamation mark convention

## on-jsload

## React exceptions

## multimethods
