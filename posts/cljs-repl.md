---
title: "What the F!GWH33L is a ClojureScript REPL?"
uuid: ec39917b-6828-4373-8552-380aba85a570
author: Paulus
---

Onboarding new developers to ClojureScript teams is a joy, mostly. You get to talk about all the things that make it fun to write web apps in a Lisp. But when we get to the topic of REPLs - arguably the most LISP-y of topics - there's often an awkard pause. While ClojureScript REPLs are well-supported these days, it's far from obvious how they work - and how to get them to work.

In this post, my goal is to explain CLJS repls in a way that I would have liked someone to explain them to me when I was starting out. But most pressing question on the newcomer's mind is how to set up. So while understanding the concepts is the goal, the path I'm taking in the post will be a guid to setting up a working REPL using figwheel main. 

FIXME: what is a REPL?

=> REPL is not just a command prompt - a proper Eval step

## Getting started

```
$ clojure -Sdeps '{:deps {seancorfield/clj-new {:mvn/version "0.8.4"}}}' -m clj-new.create figwheel-main playground.core -- --reagent

Generating fresh figwheel-main project.
   -->  To get started: Change into the 'playground.core' directory and run 'clojure -A:fig:build'
$ mv playground.core playground # Strange name for a directory
$ cd playground
```

And let's do some chores:

```
$ git init
Initialized empty Git repository in /Users/pe/prg/playground/.git/
$ git add .
$ git commit -m "Init"
```

Well, that was easy enough!

FIXME: instructions how to install cli-tools

FIXME: `clj` vs `clojure`

Let's take a minute to talk about tool choices. If you haven't been following Clojure's development over the last two years, the two tools used here - `clojure` and Figwheel Main - may be new to you. Starting with the latter tool, Figwheel Main is the newer version of the beloved ClojureScript tool Figwheel, which gives you three things: access to the ClojureScript compiler; seemless live reloading; and (you guessed it) a browser REPL.

Other than being based on a more modern design, Figwheel Main is different from its predecessor in that while the original Figwheel was based on the Leiningen build tool, Figwheel Main builds on the Clojure CLI tools (henceforth cli tools) introduced back in 2018 (FIXME?). So what are the cli tools really? If Leiningen is a build tool and cli tools are in the same space, then surely cli tools are a build tool as well?

Well, not so fast. In typical clojurist fashion, Alex Miller, the author of cli tools, has decomplected a build tool into its basic componentes. Whereas Leiningen presents as a *eierlegende Wollmilchsau*, cli tools does only one job, and does it well: it's a classpath builder. You declare which external libraries your project depends on, and cli tools go out and fetch the jars from the default Maven repositories (think NPM for JVM langauages) for you, arranging them in a Java classpath (similar to the UNIX PATH environment variable). Finally, through the `-m` switch, cli tools allow you to specify optionally what code to run as an entry point once the JVM process is running.

A very simple example of this is just to run

```
clojure
```

which download Clojure for you and runs the clojure.main/repl for you. We also make use of classpath construction and entry point invocation in the snippet above: we first select which dependencies we need (in this case, the `clj-new` tool) and then invoke a Clojure namespace called `clj-new.create` (or more precisely, the `-main` function located in that namespace). If you're curious what the file looks like, you can take a look like this:

```
# unzip -q -c ~/.m2/repository/seancorfield/clj-new/0.8.4/clj-new-0.8.4.jar clj_new/create.clj

...
(defn -main
  "Bare bones entry point to create a new project from a template.

  May eventually support more options."
  [& [template-name project-name & args]]
...
```

While the cli tools are similar to Leiningen in constructing classpaths, they don't include any code to build Clojure jars. There are other tools that do that for Clojure. In our case, we're interested in running and building ClojureScript code, not clojure, of course, and the tool of our choice for this is Figwheel Main.

`clj-new` is a project template generator (think yeoman or create-react-app). We only need this dependency once in the lifetime of a project, so it was fine to specify it as a command-line parameter. But now `clj-new` has created an entire barebones ClojureScript project for us. Let's take a look

```
$ tree
.
├── README.md
├── deps.edn
├── dev.cljs.edn
├── figwheel-main.edn
├── resources
│   └── public
│       ├── css
│       │   └── style.css
│       ├── index.html
│       └── test.html
├── src
│   └── playground
│       └── core.cljs
├── target
│   └── public
├── test
│   └── playground
│       ├── core_test.cljs
│       └── test_runner.cljs
└── test.cljs.edn
```

Rather than passing dependencies on the command line, this project contains a proper `deps.edn` file, which looks like this:

```
$ cat deps.edn
{:deps {org.clojure/clojure {:mvn/version "1.9.0"}
        org.clojure/clojurescript {:mvn/version "1.10.520"}
        reagent {:mvn/version "0.8.1"}}
 :paths ["src" "resources"]
 :aliases {:fig {:extra-deps
                  {com.bhauman/rebel-readline-cljs {:mvn/version "0.1.4"}
                   com.bhauman/figwheel-main {:mvn/version "0.2.3"}}
                 :extra-paths ["target" "test"]}
           :build {:main-opts ["-m" "figwheel.main" "-b" "dev" "-r"]}
           :min   {:main-opts ["-m" "figwheel.main" "-O" "advanced" "-bo" "dev"]}
           :test  {:main-opts ["-m" "figwheel.main" "-co" "test.cljs.edn" "-m" "playground.test-runner"]}}}
```

There are a bunch of things going here - including aliases - but we can recognize the two aspects we mentioned before:

- Runtime dependencies from Maven include Reagent and a specific version of the ClojureScript compiler. Additionally we specify two development-time dependencies (aliased as "fig"): Figwheel Main and rebel-readline-cljs, which provdes a fancier terminal REPL prompt.
- As entry points, we specify commands that call out to the figwheel.main namespace.

As the autogenerated README tells us, we can now start a development enviroment as follows:

```
$ clojure -A:fig:build
** SNIP **
Opening URL http://localhost:9500
ClojureScript 1.10.520
cljs.user=>
```

After the "Opening URL http://localhost:9500" line, Figwheel will attempt to start a browser pointed at the local URL, which make take a few seconds. If this step fails, you can open the URL manually in a browser of you choice (Chrome, Firefox and Safari should all work). Although it's possible to open the URL in multiple browsers or tabs at once, at the beginning it's best to make sure only one tab opening the URL is open at the same time.

Now Figwheel gives you two exciting ways of getting changes into your browsers without hitting the reload button in your browser. The first way is to change the source file and to save it. You could open src/playground/core.cljs in your favorite editor and change the hello-world. Or you could do it in an unnecessarily complicated way:

```
$ cat > rocks.patch
diff --git a/src/playground/core.cljs b/src/playground/core.cljs
index 237b5a9..ff8143f 100644
--- a/src/playground/core.cljs
+++ b/src/playground/core.cljs
@@ -10,21 +10,21 @@

 ;; define your app data so that it doesn't get over-written on reload
 (defonce app-state (atom {:text "Hello world!"}))

 (defn get-app-element []
   (gdom/getElement "app"))

 (defn hello-world []
   [:div
    [:h1 (:text @app-state)]
-   [:h3 "Edit this in src/playground/core.cljs and watch it change!"]])
+   [:h3 "Live reload rocks"]])

 (defn mount [el]
   (reagent/render-component [hello-world] el))

 (defn mount-app-element []
   (when-let [el (get-app-element)]
     (mount el)))

 ;; conditionally start your application based on the presence of an "app" element
 ;; this is particularly helpful for testing this ns without launching the app
```

And now apply the patch

```
$ patch -p1 < rocks.patch
patching file src/playground/core.cljs
```

You should see the string "Live reload rocks" appear on your screen. When you reload your browser, you will also see the new string.

But while saving waiting for your namespace to reload is useful, it is not the only way to update your browser window. There's also REPL evaluation.

When you see `cljs.user=>`, you can start typing Clojure forms into the prompt. Try this and watch the background color of the page change:

```
cljs.user=> (set! (.-backgroundColor (.-style (js/document.querySelector "body"))) "#decade")
"#decade"
```

From the prompt you can also interact with the code in your application. The following form will update what you see in your browser window:

```
cljs.user=> (swap! playground.core/app-state update :text clojure.string/upper-case)
{:text "HELLO WORLD!"}
```

The `cljs.user` string you're seeing is the namespace you're in. You can switch the namespace and evaluate functions without having to type in the full namespace every time:

```
cljs.user=> (in-ns 'playground.core)

playground.core=> (hello-world)
[:div [:h1 "HELLO WORLD!"] [:h3 "Live reload rocks"]]
```

## What's going on here?

We got to a working browser REPL fairly quickly, but let's pause for a minute and reflect on what just happened.

