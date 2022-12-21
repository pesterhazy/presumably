---
title: "How to Do Things With Babashka"
uuid: d5724368-5890-4d73-b506-1c324be7df20
author: Paulus
date-published: 2022-12-21
---

It's time to move on from Bash.

Even though it's a fine interactive shell, Bash is inadequate for scripting. By default, Bash scripts swallow errors (and far from fixing this, the `set -e` option comes with its own set of footguns). Bash has arrays and other useful data structures, but they're [notoriously buggy](https://stackoverflow.com/a/61551944/239678) in Bash 3, which is what's preinstalled on macOS. And, finally, it lacks adequate means of abstraction to safely express complex logic - and by complex logic, I mean anything requiring a loop or function call.

Built on Clojure, Babashka is superior in all these respects. It has great support for safe concurrency and comes with batteries included, with support for [finding files](https://github.com/babashka/fs/blob/master/API.md#babashka.fs/glob), [starting subprocesses](https://github.com/babashka/process/blob/master/API.md#babashka.process/shell) and [reading and writing JSON](https://github.com/dakrone/cheshire). And the built-in Clojure standard library for transforming data structures is second to none.

What's the cost? Thanks to the magic of GraalVM, the startup penalty is barely noticeable despite the fact that Babashka is built on top of Java:

```
% time bb -e '(println (* 3 4))'
0.020 total  # that's 20 milliseconds
% bash -c "echo $(( 3 * 4 ))"
0.006 total
```

This document demonstrates how to approach common Bash scripting tasks in Babashka. With this Rosetta stone, you can translate Bash scripts to Babashka - and get results that are vastly more reliable, maintainable and fun to work with.

## Run a shell command

``` clojure
(require '[babashka.process :refer [shell]])
(shell "whoami")
```

Command output is visible to the user (stdout and stderr are inherited from the babashka process).

Bash equivalent:

``` shell
whoami
```

## Set environment variable for shell command

You can pass on extra environment variables to child processes:

``` clojure
(require '[babashka.process :refer [shell]])
(shell {:extra-env {"FOO" "bar"}} "printenv" "FOO")
```

Bash equivalent:

``` shell
FOO=bar printenv FOO
```

## Capture output of a shell command

``` clojure
(require '[babashka.process :refer [sh]])
(def myname (:out (sh ["whoami"])))
```

Bash equivalent:

``` shell
myname=$(whoami)
```

## Spawn a shell command in the background

``` clojure
(require '[babashka.process :refer [shell process]])

(let [p (process ["sh" "-c" "for i in `seq 3`; do date; sleep 1; done"]
                 {:out :inherit, :err :inherit})]
  (println "Waiting for result...")
  ;; dereference to wait for result
  @p)
```

Because of its Java heritage, Babashka has strong threading primitives. Clojure makes working with concurrency safe and easy.

Bash equivalent:

``` shell
sh -c 'for i in `seq 3`; do date; sleep 1; done' &
pid=$!
echo Waiting for result...
wait "$pid"
```

## Read command output line by line

``` clojure
(require '[babashka.process :as p :refer [process destroy-tree]]
         '[clojure.java.io :as io])

(let [stream (process
              {:err :inherit
               :shutdown destroy-tree}
              ["cat" "/etc/hosts"])]

  (with-open [rdr (io/reader (:out stream))]
    (binding [*in* rdr]
      (loop []
        (when-let [line (read-line)]
          (println (str "#" line))
          (recur)))))

  ;; kill the streaming bb process:
  (p/destroy-tree stream)
  nil)
```

This reads the command's stdout in a streaming fashion, making the approach suitable for large files. However, if you know you're not going to deal with large files, it's easier to read the file into memory:

``` clojure
(require '[babashka.process :as p :refer [shell destroy-tree]]
         '[clojure.java.io :as io])

(let [p (shell {:out :string} "cat" "/etc/hosts")]
  (doseq [line (clojure.string/split-lines (:out p))]
    (println (str "#" line)))

  nil)
```

Note that `cat` is used only as an example here. Use `slurp` to read a file efficiently.

Bash equivalent:

``` shell
cat /etc/hosts | sed 's/^/#/'
```

## Check if a file exists

``` clojure
(when (babashka.fs/exists? "/etc/hosts")
   (println "File exists"))
```

Bash equivalent:

``` shell
if [[ -f /etc/hosts ]]; then echo File exists; fi
```

## Duplicate an array

As a Bash programmer you may wonder how to duplicate an array in babashka. Well, you don't.

First, babashka typically uses vectors, not arrays. But more importantly, in babashka you don't need to make a copy of a thing to preserve the original, because vectors are immutable:

``` clojure
;; straightforward
(def my-args *command-line-args*)
```

Simple _and_ easy! In Bash however... oh boy:

``` shell
my_args="${@+"${@}"}"
my_args2="${my_args[@]+"${my_args[@]}"}" 
```

To the best of my knowledge, this is the only safe incantation to duplicate an array in Bash 3 (which is what ships with macOS).

## Find project folder

A typical pattern is to locate the folder of the code project containing a script, regardless of the current working directory.

Assuming your script is located in a top-level folder called `scripts/`, you can use this:

``` clojure
;; Note that the `*file* form has to be evaluated at the top level of your file,
;; i.e. not in the body a function.
;;
;; Here fs/parent works similarly to the "dirname" Unix command.

(def project-root (-> *file* babashka.fs/parent babashka.fs/parent))

;; Print root folder
(println (str project-root))

;; Print filename in root folder
(println (str (babashka.fs/file project-root "README.txt")))
```

Bash equivalent:

``` shell
project_root="$(dirname "${BASH_SOURCE[0]}")/.."

printf "%s\n" "$project_root"
printf "%s\n" "${project_root}/README.txt"
```

## Copy, move, delete, read and write files

``` clojure
(require '[babashka.fs :as fs])

(spit "world" "hello\n")
(fs/copy "world" "world2")
(spit "world2" "world\n" :append true)
(fs/delete "world")
(fs/move "world2" "world")
(print (slurp "world"))
```

Common operations like `spit` or `slurp` and functions in [babashka.fs](https://github.com/babashka/fs/blob/master/API.md) accept a string filename or, alternatively, an instance of java.io.File as returned by [babashka.fs/file](https://github.com/babashka/fs/blob/master/API.md#file-page_facing_up). So `(spit "out" "xxx")` and `(spit (babashka.fs/file "out") "xxx")` are interchangeable.

Bash equivalent:

``` shell
echo hello > world
cp world world2
echo "world" >> world2
rm world
mv world2 world
cat world
```

## Work with dates and times

The modern java.time API is available in Babashka. For the common "YYYY-MM-DD" pattern you can use a built-in formatter:

``` clojure
(defn iso-date
  []
  (-> (java.time.LocalDateTime/now)
      (.format (java.time.format.DateTimeFormatter/ISO_LOCAL_DATE))))

(def fname (str "backup-" (iso-date) ".zip"))
```

Bash equivalent:

``` shell
fname="backup-$(date '+%Y-%m-%d').zip"
```

If you need more control, you can specify your own [DateTimeFormatter](https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/time/format/DateTimeFormatter.html) pattern:

``` clojure
(defn iso-date-hm
  []
  (-> (java.time.LocalDateTime/now)
      (.format (java.time.format.DateTimeFormatter/ofPattern "yyyy-MM-dd---kk-mm"))))

(def fname (str "backup-" (iso-date-hm) ".zip"))
```

Bash equivalent:

``` shell
fname="backup-$(date '+%Y-%m-%d---%H-%M').zip"
```

## Read standard input (stdin)

Like Clojure, Babashka makes standard input available via the dynamic var [`*in*`](https://clojuredocs.org/clojure.core/*in*), which is a java.io.Reader. Often you just want to read the whole input into memory:

``` clojure
(println (clojure.string/upper-case (slurp *in*)))
```

Alternatively, you can process the input line by line:

``` clojure
;; For line-seq, we need a java.io.BufferedReader

(doseq [line (line-seq (clojure.java.io/reader *in*))]
  (println (clojure.string/upper-case line)))
```

Bash equivalent (note that this is not unicode-aware):

``` shell
tr a-z A-Z
```
