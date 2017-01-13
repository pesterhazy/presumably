---
title: "Search and Replace with git"
subtitle: "Introducing git xargs"
uuid: d106120b-34de-4bc6-a1a8-16851474cc97
author: Paulus
draft: true
---

If, like me, even in 2017 you see the command line as the ultimate developer
environment, chances are you also see Git as your best friend. With `git` most of my work under source control, one thing I find myself reaching for frequently is a way to search and replace strings in your entire project.

What could be simpler than going through all the text files and replacing one
string with another? And yet it's surprisingly difficult and error-prone.
Although editors or IDEs typically have this feature, it's often hard to use
and often unpredictable.

Following the Unix philosophy, we should decompose into discrete steps. The
challenges usually involve

- selecting the right files and
- replacing the string without fuss.

Usually I just want to go through all files in my Git repository, though
sometimes I want to exclude some files.

Just use regular expressions, you say? Actually, no. Most of the time I don't
really need the power they offer, and I don't want to remember the -- confusing
and inconsistent -- regex syntax of different tools. Mostly I want to replace
fixed strings.

So even though Unix is great, I don't particularly like using sed or perl for
this job, as regex support is baked in (and I haven't found a way to opt out --
believe me, I've tried).
