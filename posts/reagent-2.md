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

How to build a reloading workflow: tenzing or chestnut

## defonce and ratom

If state updates don't trigger re-renders, one common reason is that the var
actually refers to an atom, not a ratom.

Also defonce

## multimethods

## dereffing atoms

(get !state :foo)

(get @!state :foo)

exclamation mark convention

## root-component doesn't update

recommended:

```
(fn [] [root])
```

## on-jsload

## React exceptions
