---
title: "Search and Replace with git"
subtitle: "Using git xargs and rpl"
uuid: d106120b-34de-4bc6-a1a8-16851474cc97
author: Paulus
draft: true
---

If you are like me and you see the command line as the ultimate developer
environment even as we're moving into 2017, chances are you also treat Git as a
trusty old friend. With most of my work under source control, one thing I find
myself reaching for frequently is a way to search and replace strings in the
entire project.

What could be simpler than going through all the text files under source control
and replacing one string with another? And yet the task turns out to be
surprisingly difficult and error-prone. Although editors or IDEs typically have
this feature, it's often unwiedly and, much worse, unpredictable.

So following the Unix philosophy, let's decompose the task into discrete steps.
The challenges usually involves

- selecting the right files and
- replacing the string without fuss.

Most of the time I just want to iterate over every file in my Git repository,
though sometimes I want to exclude some of files, either by path or extension.

Just use regular expressions, you say? Actually, no. Most of the time I don't
really need the flexibility they offer, and I don't want to remember the
confusing and inconsistent regex syntax and esacaping tricks required by the
different Unix tools. Mostly I replace fixed strings.

In fact some Unix tools are not Unix-y enough for my taste. A case in point, I
don't particularly like using sed or perl for string replacement, as regex
support is baked in, and I haven't found a way to opt out - believe me, I've
tried.

And then there's the fact that, by default, sed (and perl) only replace a single
occurrence of a search string per line. I often forget the `s/foo/bar/g` modifier.

Fortunately, there is a replacement for sed that operates on fixed strings called
[rpl(1)](https://linux.die.net/man/1/rpl). It's widely available (`brew install
rpl`, `apt-get install rpl`) and pretty much does one job, and does it well.

What about selecting files to work on? Git and the popular `xargs` tool help
here, but to combine the two easily I needed a new tool. So I built
[git xargs](https://gist.github.com/pesterhazy/65360ed980ae0c86a4150102ca6484a0),
which simply execute a utility on all files in your Git repository. It's tiny
enough to fit in a gist.

Here's how you use it (and remember to start with a clean `git status`):

```shell
git xargs rpl foo bar
# replaces "foo" with "bar" in all files in the current repository

git xargs '*.c' '*.cpp' -- rpl foo bar
# restrict to only files ending in .c and .cpp (note the double dash)

git xargs rpl '*hello*' '*goodbye*'
# no regex no problem (but you can specify -w to match only at word boundaries)
```

Of course, the beauty of revision control is that you can easily see what has
changed (`git diff`) and selectively apply or reject the changes (`git add -p`).
If something went wrong, just revert the working directory to its previous state (`git checkout --`).

Hopefully `git xargs` will prove useful to you too!
