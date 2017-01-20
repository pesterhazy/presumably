---
title: "Reagent Mysteries"
subtitle: "Part 5: Interop"
uuid: c8de392c-93b3-4c86-bf3b-19971f710dfc.
author: Paulus
draft: true
---

What's the difference between (first one doesn't work, but the other does work):
```(r/adapt-react-class (.-DataSource (.-ListView ReactNative)))
;; vs.
(.-DataSource (.-ListView ReactNative)))
````

Why do I have to use `#js`, e.g.:
```(data-source. #js {:rowHasChanged #(not= %1 %2)})
````

Why do I have to use `clj->js`, e.g.:
```{:dataSource (.cloneWithRows ds (clj->js ["John", "Joel", "James", "Jimmy", "Jackson", "Jillian", "Julie", "Devin"]))}
```

And why `/as-element` was even necessary for the render, e.g.:
```:render-row #(r/as-element [text %])
````

pesterhazy [6:33 PM]
ad 1) "ListView" is a React component. To use it in reagent, you need to "adapt" it into a Reagent component. (edited)

[6:33]
DataSource, on the other hand, is just a JavaScript class (the `.` means instantiation), so no adaptation necessary.

[6:36]
ad 2) you're passing a value to the DataSource constructor. `{:a :b}` is a PersistentHashMap, which a JavaScript lib won't understand. So you need to create a JavaScript object. `#js` is a literal to do that. `(js-obj :a :b :c :d)` is more explicit.

[6:37]
ad 3, same issue. `[]` creates a PersistentVector, which is _not_  a js array. `clj->js` is one way to do that, better ways would be `into-array` or `#js ["john" ...]`

[6:39]
ad 4, as-element is necessary for the inverse reason why adapt-react-class exists. render-row expects a React render fn, i.e. a fn that returns a React element (in JS usually generated through JSX)

[6:39]
if you just returned `[text "asdf"]`, that would be a PersistentVector, which JS has no clue about

[6:39]
makes sense?

[6:40]
btw these are all reagent or js interop issues, nothing particularly to do with react-native

[6:41]
(although I realize react-native shows these issues more than react-web)
