# How to do things with babashka

## Run a shell command

```
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

```
(require '[babashka.process :refer [shell]])
(shell {:extra-env {"FOO" "bar"}} "printenv" "FOO")
```

Bash equivalent:

```
FOO=bar printenv FOO
```

## Capture output of a shell command

```
(require '[babashka.process :refer [sh]])
(def myname (:out (sh ["whoami"])))
```

Bash equivalent:

```
myname=$(whoami)
```

## Spawn a shell command in the background

```
(require '[babashka.process :refer [shell process]])

(let [p (process ["sh" "-c" "for i in `seq 3`; do date; sleep 1; done"]
                 {:out :inherit, :err :inherit})]
  (println "Waiting for result...")
  ;; dereference to wait for result
  @p)
```

Because of its heritage, Babashka has strong threading primitives. Clojure makes working with concurrency easy.

Bash equivalent:

```
sh -c 'for i in `seq 3`; do date; sleep 1; done' &
pid=$!
echo Waiting for result...
wait "$pid"
```

## Check if a file exists

```
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

```
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

```
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
