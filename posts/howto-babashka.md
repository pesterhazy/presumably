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

## Run a shell command, capturing its output

```
(require '[babashka.process :refer [sh]])
(def myname (:out (sh ["whoami"])))
```

Bash equivalent:

```
myname=$(whoami)
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

