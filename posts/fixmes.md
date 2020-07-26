---
title: "On the effectiveness of FIXMEs"
date-published: 2020-07-27
---

Not too long ago, a friend and new contributor to a large codebase I work with was surprised to learn that the linter will block the merge of any pull request containing the string FIXME. "This rule is ill-considered", he cried out, exasperated. It's fair to say he didn't like the linter ruler. 

As his reaction shows, I didn't do a good job explaining the rationale behind our rule. Our treatment of FIXMEs is unorthodox, but we decided to adopt it for a reason. In fact, treating FIXMEs as merge blockers is among the most effective measures against cognitive overload that I know. In this post, I will explain how FIXMEs unclutter the mind, and, in doing so, I hope to show that automatically enforced FIXMEs can be an effective tool for teams working on hard problems.

# The meaning of codetags

Code comments are as old as programming itself. Codetags are also not a recent innovation. In the 1980s, programmers started attaching special significance to short tags like XXX, FIXME or TODO.[^xxx] Tags were added as a heads-up that the author wasn't entirely satisfied with a solution, both as a _note to self_ and for the benefit of other readers. The use of uppercase letters not only made tags stand out visually, it also makes it easy to grep for them.[^grep]

Codetags are in wide use use today, though semantics vary from team to team. While often initially vague, over time more specific meanings tend to materialize, much like emojis in Slack communities gradually acquire a more definite meaning. In my team, a clear line separates FIXMEs from TODOs. A TODO annotation means that the code in question could be optimized or cleaned up. Crucially, however, delaying the suggested improvement is acceptable, perhaps even preferable from a project-management perspective. A TODO is a gentle nudge. FIXMEs, on the other hand, signal that the code isn't ready to merge until the author addresses the problem. Our automated linter adds reality to this stringent judgment: FIXMEs fail the CI build, while TODOs are given a pass.[^linux]

How does this distinction help? On th one hand, tags with an agreed-upon meaning aid communication between team members while the pull request is in progress. "FIXME or TODO?" you might ask your colleague while pairing, requesting clarification of the status and priority of a less-than-satisfying solution. Similarly, during code review a FIXME left in the code signals that the line annotated requires close attention. It amounts to a cry for help: "I'm stuck! Can we talk about this tricky implementation issue?"

More interestingly, however, FIXMEs have an important role to play before code is ready to review.  When encountering a stumbling block - a consideration that will lead you away from your path - one thing you can do is to introduce a FIXME comment:

```
// FIXME: hardcoded user-id

// FIXME: O(n2) time complexity, check if fast enough
```

Some FIXMEs concern small detours, like retrieving a user-id from inside an existing function. Other issues are more substantial and will require serious thought to resolve. Either way fixing the issue demands mental bandwidth, which is in short supply. While it's tempting to jump into the FIXME right away, the best choice is often to leave it to one side for the moment.

It's worth going into detail on two maneuvers enabled by FIXMEs. The first, hardcoding, is a form of wishful thinking. When the structure of the code makes it awkward to use a value, a refactoring safari is often needed. The maneuver is to delay this step. Wishful thinking begins with the what-if question. What if we had access to the id here? Well, let's just pretend that we already do by hardcoding the value. This won't work except in a very specific case, of course, so it's not a generic solution - quite the opposite. But hardcoding an absurdly specific solution will allow you to make progress on the design while bracketing insignifant details. The generic solution will come later.

Stubbing, the second maneuver, helps when multiple cases need to be considered. You start with a stub, replacing the missing case with what an _active FIXME_:

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

With the partial implementation, you will still be able to experiment and run the code. If you hit the missing case, it will fail loudly, with an exception and a stacktrace. The FIXME is not only active at runtime, it will also ensure that you won't forget to fill in the missing case before finishing your branch.[^meta]

# Juggling concepts and virtual memory

To understand why deprioritizing a problem by converting it into a FIXME works so effectively, consider an analogy. Most people have a hard time managing their schedules. As unfinished work piles up, they develop a nagging doubt in the back of their mind, a fear that they may not remember something important, with all the bad consequences that entails. In his book Getting Things Done, David Allen famously recommends lists and regular review sessions as a remedy. Writing down a task into a notebook helps because it permits you to forget abut the task temporarily. The written list helps you relax about your obligations because instead of incessantly worrying about things falling through the cracks, the brain can rest assured that the task is stashed away in a secure place.

FIXMEs are powerful because programming is a cognitively demanding activity. There are limits to how many concepts we can grap at any given time. In juggling, the number of balls an average person can keep in the air is three.[^juggling] How many ideas can a person can keep in their head at the same time? Perhaps more than three, but the number of ideas we can juggle is depressingly low. To switch metaphors, the programmer's RAM or "working memory" is a scarce resource. Sure, there's a spectrum here: some people can retain more data in memory than others. But if not careful, even the best programmer quickly runs out of heap.

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

[^grep]: Our linter rule can be expressed in a single line of shell code: `git grep FIXME && exit 1`.

[^meta]: Note that FIXMEs can appear outside of code comments, in locations as diverse as exceptions, user-visible strings or SQL queries. Even writing documentation or blog posts (including this one) benefits from automatically enforced FIXMEs.

[^juggling]: The juggling analogy plays a central role in Rich Hickey's [Simple Made Easy](https://github.com/matthiasn/talk-transcripts/blob/master/Hickey_Rich/SimpleMadeEasy.md).
