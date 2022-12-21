# How to do things with babashka

## Run a shell command

``` clojure
(require '[babashka.process :refer [shell]])
(shell "whoami")
```

Command output is visible to the user (stdout and stderr are inherited from the babashka process).

Bash equivalent:

```
whoami
```

## Set environment variable for shell command

You can pass on extra environment variables to child processes:

``` clojure
(require '[babashka.process :refer [shell]])
(shell {:extra-env {"FOO" "bar"}} "printenv" "FOO")
```

Bash equivalent:

```
FOO=bar printenv FOO
```

## Capture output of a shell command

``` clojure
(require '[babashka.process :refer [sh]])
(def myname (:out (sh ["whoami"])))
```

Bash equivalent:

```
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

```
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

```
cat /etc/hosts | sed 's/^/#/'
```

## Check if a file exists

``` clojure
(when (babashka.fs/exists? "/etc/hosts")
   (println "File exists"))
```

Bash equivalent:

```
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

```
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

```
project_root="$(dirname "${BASH_SOURCE[0]}")/.."

printf "%s\n" "$project_root"
printf "%s\n" "${project_root}/README.txt"
```

## Copying, moving, deleting, reading and writing files

```
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

```
echo hello > world
cp world world2
echo "world" >> world2
rm world
mv world2 world
cat world
```

## Working with dates and times

The modern java.time API is available in Babashka. For the common "YYYY-MM-DD" pattern you can use a built-in formatter:

```
(defn iso-date
  []
  (-> (java.time.LocalDateTime/now)
      (.format (java.time.format.DateTimeFormatter/ISO_LOCAL_DATE))))

(str "backup-" (iso-date) ".zip")
```

Bash equivalent:

```
fname="backup-$(date '+%Y-%m-%d').zip"
```

If you need more control, you can specify your own [DateTimeFormatter](https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/time/format/DateTimeFormatter.html) pattern:

```
(defn iso-date-hm
  []
  (-> (java.time.LocalDateTime/now)
      (.format (java.time.format.DateTimeFormatter/ofPattern "yyyy-MM-dd---kk-mm"))))

(str "backup-" (iso-date-hm) ".zip")
```

Bash equivalent:

```
fname="backup-$(date '+%Y-%m-%d---%H-%M').zip"
```
