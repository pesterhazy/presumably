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
