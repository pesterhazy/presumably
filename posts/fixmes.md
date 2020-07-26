---
title: "On the effectiveness of FIXMEs"
date-published: 2020-07-13
---

Not too long ago, a friend and new contributor to a large codebase I work with was surprised to learn that the linter will block the merge of any pull request containing the string FIXME. "This rule is ill-considered", he cried out, exasperated. It's fair to say he didn't like the linter ruler. 

I obviously didn't do a good job explaining the reasons behind our rule. Our treatment of FIXMEs is unorthodox, but there's a story behind it. In my experience, treating FIXMEs as merge blockers is an effective measure against cognitive overload. In this post, I will make another attempt at explaining why automatically enforced FIXMEs are so effective.

# The meaning of codetags

Comments are as old as programming itself and have served a [variety of purposes](https://en.wikipedia.org/wiki/Comment_%28computer_programming%29#Stress_relief). Codetags are not a recent innovation either. In the 1980s, programmers started attaching special significance to tags like XXX, FIXME or TODO.[^xxx] Then as now, tags are added as a heads-up that the author is not satisfied with a solution, both as a _note to self_ and for the benefit of other readers. The use of uppercase letters not only makes tags stand out visually, it also makes it stupid easy to grep for them. Our linter rule can be expressed in a single line of shell code: `git grep FIXME && exit 1`.

Codetag semantics vary from team to team. While often initially vague, over time more specific meanings tend to materialize, much like emojis in Slack workspaces gradually acquire a more and more definite meaning. In my team, the TODO tag means that the code in question could be optimized or cleaned up. Crucially, however, it is acceptable, perhaps even preferable from a project-management perspective, to delay the suggested improvement, sometimes indefinitely. A TODO is a gentle nudge. A FIXME, on the other hand, is stringent: the code isn't ready to merge until the author addresses the problem. Our automated linter adds reality to this judgment: FIXMEs fail the CI build, while TODOs are given a pass.[^linux]

Tags with an agreed-upon meaning aid communication between team members while the pull request is in progress. "FIXME or TODO?" you might ask your colleague while pairing, requesting clarification of the status and priority of a less-than-satisfying solution. Similarly, during code review a FIXME left in the code signals that the line annotated requires close attention. It amounts to a cry for help: "I'm stuck! Can we talk about this tricky implementation issue?"

# Hardcoding values and runtime FIXMEs

FIXMEs are useful during review, but they can help the author as much as the reviewer, and that's where things get more interesting. During the implementation phase - when I'm actively writing code, trying out approaches and making small incremental improvements - the work is split up into a series of small changes, each resulting in a commit. It helps to think of each step as an instance of a generic pattern or maneuver. 

Refactorings such as Inline Function and Rename Variable are examples, but so is the introduction of a FIXME. When I encounter a stumbling block, a consideration that will lead me away from my current path, I'll stare the problem in the face and walk away. But before that, I'll go in and add a comment to the worrisome section:

```
// FIXME: hardcoded user-id

// FIXME: O(n2) time complexity, check if fast enough
```

Some FIXMEs concern small hurdles, like the question of how to get a user-id from inside the context of an existing function. But even trivial refactorings require attention, and I often lack the mental bandwidth to deal with the issue in addition to the main problem at hand. Other issues are more substantial and will require serious thought to resolve. But although doing this work may be tempting, it is still off-topic relative to my current focus, and while I know I will need to come back to the issue, the best choice is often to leave it to one side for the moment.

My favorite of all the maneuvers made possible by FIXMEs is hardcoding, a form of wishful thinking. I frequently find while writing a function that a dependency value is awkward to retrieve without going on a refactoring safari. Wishful thinking begins with a what-if question. What if we had access to the id here? Well, let's just pretend that we already do by hardcoding the value here. This will only work for one particular user, of course, so it's not a generic solution - quite the opposite. But hardcoding a laughably specific solution will help you make progress on the design without worrying about details - a generic solution will come later.

Another maneuver that can help is the partial implementation. If there are multiple cases to consider, I often find myself beginning with a stub and replacing the missing case with what could be called an active FIXME:

```
switch (v) {
   case 1:
   // do something
   break;
   case 2:
   // do something
   break;
   default:
   throw new Error("FIXME: Not implemented");
}
```

You will be able to run the code so long as you don't encounter the missing case. If you do, you'll get an exception and a stacktrace. By relying on CI, the active FIXME will ensure that you won't forget about filling in the missing case before finishing your branch. Note that FIXMEs can appear outside of code comments, in locations as diverse as exceptions, strings, SQL queries or even documentation.

# Pair programming and fixmification

What introducing a FIXME boils down is deprioritizing a problem by converting it into a FIXME. So why does fixmification work so effectively? The reason is that our brain does a terrible job at letting go of important information. In Getting Things Done, David Allen famously recommends building lists and scheduling regular review sessions as a remedy. Unfinished tasks build up as a nagging doubt in the back of your mind, a fear that you may not remember, with all the bad consequences this entails. Writing down a task in a notebbok helps allay this doubt. By allowing you to forget abut the task now, at least temporarily, the written list helps you relax about how you organize your work because instead of incessantly worrying about things falling through the cracks, the brain can rest assured that the task is stashed away in a secure place.

FIXMEs serve a similar purpose, and here's why. It has been said that, like the number of balls a juggler can keep in the air, the number of concurrent ideas a person can keep in their head at the same time is somewhere between 3 and 7. The number is depressingly low. To switch metaphors, while programming the RAM or "working memory" is a scarce resource. True, there's a spectrum here: some people can read more values into memory than others. But even the best programmer quickly runs out of heap.

Consider how effective pair programming can be when working through a hard problem. The pairing partner temporarily expands your working memory by giving you access to part of their cognitive capacity. The Linux virtual memory system allows you to double your RAM by making use of your hard drive capacity. By writing down FIXMEs, you can achieve a similar effect. You temporarily extend your cognitive capacity by swapping to disk, i.e. by removing an idea from your working memory, with the intention to page it back in when needed, usually at the end of the session. What's even better is to come back to the issue the next day with a clear mind.

The net effect of temporarily transfering inessentials to swap space is that you can focus on the heart of the issue, the inherent complexity of the business problem, the solution to which is your ultimate goal. Accidental complexity has a talismanic quality. We have an unfortunate but understandable tendency to be distracted by details of how to express an idea in code. It is true that eventually we will need a decision about the accidental details, but it's almost always better to tackle the essential complexity first. The rest is FIXMEs.

Your brain is prone to worrying that you may after all forget about the hardcoded value left in the code or the quadratic complexity that might blow up in production. People will be angry at you, or, what may be worse, you'll blame yourself. In truth, emotions and other sub-conscious processes play a big role in our ability to focus. The unconscious mind will only allow itself to stop worrying about a task until it is written down, with a systems in place ensuring you will circle back to the topic without risking emotional backlash. The real value of fixmification is in its liberating effect. Let it go, brain, the CI job has got your back!

# Three ways of eliminating FIXMEs

After moving the distraction out of the way, you can return to the core problem. Now that your RAM isn't cluttered with inessential details, the clearer view gives you a better shot at seeing the solution. At some point, however, you will need to go back to the problem you snoozed. Having reached this point, eliminating the FIXME can take one of three possible forms, the most straightforward of which is to face the problem head-on. What this boils down to is to actually perform the task you anticipated initially, for example by refactoring the code so you have access to the user-id you needed or by rewriting the algorithm to require O(n) instead of O(n2) iterations. Frequently, a test case needs to be added as well.

But doing chores is not the only way forward. Perhaps surprisingly, you may be able to simply delete the FIXME at this point. Sometimes things fall into place. In the process of implementing the main logic, you often end up solving the incidental issue as a consequence of the approach chosen, perhaps without even realizing it. Additionally, addressing the essential complexity puts you in a better position to understand the original incidental problem. With this knowledge, it often turns out there's nothing left that needs fixing. Perhaps given that in practice n=3, O(n2) complexity is prefectly fine. Or you recognize that, with the confidence gained from completing an implementation, your worry was about something that wasn't worth worrying about in the first place.

Finally you can choose to demote the FIXME to TODO status. While the problem you identified is real, it's not a merge blocker *here and now*. A TODO can remind you that the concern should be addressed at some point in the future. Often, filing a follow-up issue in your issue tracker will help make sure that the matter is not forgotten. (From what I hear, Google requires TODOs to include a ticket number). Filing a follow-up for later in the project, while not always what we would prefer, is a valuable tool when working in teams. Long-running feature branches cause unnecessary integration work, so it's desirable to merge branches as soon as possible. Whether demotion is the right call will depends on the specifics of your project's goals and values. But when applied with caution, FIXME demotion is a valuable tool for managing priorities and focusing on the essential.

That's why I've grown to love FIXMEs. It's true that a linter rule preventing merges of code containing a word limits your freedom of expression. But as in art, constraints can help creativity. FIXME-driven development adds to our autonomy, rather than substracting from it, because it encourages us to focus on what matters and because it optimizes for what is arguably our most precious resource - our congitive capacity.

[^xxx]: There's evidence that, like many conventions, significant codetags were first introduced by Unix hackers in the BSD codebase. Possibly the earliest example of the codetag XXX (used more or less synonymously with FIXME) is a [1981 commit](https://github.com/dspinellis/unix-history-repo/commit/9e295a2f65c046125ece0ad68f142f59df4c3400) by Bill Joy, the author of vi. FIXME seems like it became popular later. When 4.4BSD came around, its source contained 1500 instances of `/* XXX`, compare to only 332 of `/* FIXME`, mostly in gcc and gdb. For more software archeology, see [Juho Snellman's fascinating investigation](https://www.snellman.net/blog/archive/2017-04-17-xxx-fixme/).

[^linux]: The Linux kernel repository contains 4041 FIXMEs and 5720 TODOs. It clearly doesn't follow the rule proposed in this post.
