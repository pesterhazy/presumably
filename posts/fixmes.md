---
title: "On the effectiveness of FIXMEs"
date-published: 2020-07-27
---

Not too long ago, a friend and new contributor to a large codebase I work with was surprised to learn that an automatic linter rule will block the merge of any pull request containing the string FIXME. "This rule is ill-considered", he cried out, exasperated.

Judging from his reaction, I didn't do a good job of explaining the rationale behind our rule. Our use of FIXMEs is unorthodox, but we decided to adopt it for a reason. In fact, in my opinion treating FIXMEs as merge blockers is among the most effective techniques for reducing cognitive overload. In this post, I will explain how FIXMEs unclutter the mind. My goal is to show that automatically enforced FIXMEs are an effective tool for teams working on hard problems.

## The meaning of codetags

Code comments are as old as programming itself. Codetags are also not a recent innovation. In the 1980s, programmers started attaching special significance to short tags like FIXME or TODO.[^xxx] Tags were added as a heads-up that the author wasn't entirely satisfied with a solution, both as a _note to self_ and for the benefit of other readers. The use of uppercase letters not only made tags stand out visually, it also makes it easy to grep for them.[^grep]

Codetags are in wide use today, though semantics vary from team to team. While often initially vague, over time more specific meanings tend to materialize, much like emojis in Slack communities gradually acquire a more definite meaning. In my team, a clear line separates FIXMEs from TODOs. A TODO annotation means that the code in question could be optimized or cleaned up. Crucially, however, delaying the suggested improvement is acceptable, perhaps even preferable from a project-management perspective. A TODO is a gentle nudge. FIXMEs, on the other hand, signal that the code isn't ready to merge until the author addresses the problem. Our automated linter adds reality to this stringent judgment: FIXMEs fail the CI build, while TODOs are given a pass.[^linux]

How does this distinction help? On the one hand, tags with an agreed-upon meaning aid communication between team members while the pull request is in progress. "FIXME or TODO?" you might ask your colleague while pairing, requesting clarification of the status and priority of a less-than-satisfying solution. Similarly, during code review a FIXME left in the code signals that the line annotated requires close attention. It amounts to a cry for help: "I'm stuck! Can we talk about this tricky implementation issue?"

More interestingly, FIXMEs also have an important role to play before code is ready to review, and that's what I want to focus on in this post. When encountering a stumbling block - a thought that will lead you away from your path - what you can do is to introduce a FIXME comment:

```
// FIXME: hardcoded user-id

// FIXME: O(n2) time complexity, check if fast enough
```

Some FIXMEs concern small detours, like retrieving a user-id from inside an existing function. Other issues are more substantial and will require serious thought to resolve. Either way fixing the issue demands mental bandwidth, which is in short supply. While it's tempting to jump into the FIXME right away, the best choice is often to leave it to one side for the moment.

## Two examples of using FIXMEs

Let's look at two maneuvers that are made possible by FIXMEs. The first, _hardcoding_, is a form of wishful thinking. When the structure of the code makes it awkward to use a value, a refactoring safari is often needed. The maneuver is to delay this step. Wishful thinking begins with the what-if question. What if we had access to the id here? Well, let's just pretend that we already do by hardcoding the value. This won't work except in a very specific case, of course, so it's not a generic solution - quite the opposite. But hardcoding an absurdly specific solution will allow you to make progress on the design while bracketing insignificant details. The generic solution will come later.

_Stubbing_, the second maneuver, helps when multiple cases need to be considered. You start with a stub, replacing the missing case with an active FIXME:

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

With the partial implementation in place, can now experiment with and get a feel for the code. When you hit the missing case, it will fail loudly and clearly, with an exception and a stacktrace. But the FIXME is not only active at runtime. It will also ensure that you won't forget to fill in the missing case before finishing your branch.[^meta]

## Juggling concepts and virtual memory

Pausing a train of thought by converting it into a FIXME improves the flow of deep work, and we can explain why by considering why so many of us struggle to manage our daily priorities. As unfinished work piles up, we develop a nagging doubt in the back of their minds, a fear that there's something important we're forgetting, with all the bad consequences that entails. As a solution, David Allen's Getting Things Done (GTD) system famously proposes a system of lists and weekly review sessions.[^gtd] Lists work because writing a task into a notebook permits you to forget about it temporarily. Keeping a written list of _IN_ tasks helps you relax about your obligations, so that instead of incessantly worrying about things falling through the cracks, you can be confident that the task is stashed away in a secure place. You know you'll return to the tasks in your review session, which has a pre-scheduled spot in your calendar. 

Let's apply this idea to coding. FIXMEs are powerful because programming is a cognitively demanding activity. There are limits to how many concepts we can grasp at any given time. In juggling, the number of balls an average person can keep in the air is three.[^juggling] How many ideas can a person actively keep in their head at the same time? Perhaps more than three, but by most counts the number of ideas we can juggle is depressingly low. To switch metaphors, the programmer's RAM or working memory is a scarce resource. Sure, there's a spectrum here – some people can retain more data in memory than others. But if not careful, even the best programmer quickly runs out of heap.

We can push the analogy even further. Think of how effective pair programming is when working through a hard problem. The Linux virtual memory system allows the user to double their RAM by making use of spare hard drive capacity. Similarly, the pairing partner temporarily expands the programmer's working memory by allowing them access to part of their cognitive capacity. Writing down FIXMEs achieves a similar effect. Whereas in a pairing session you extend your cognitive capacity with help from your partner, FIXMEs allow you to swap to disk. You remove an idea from your working memory, with the intention of paging it back in as needed, usually at the end of the session.[^better]

## Focus on the inherent complexity

The net effect of temporarily transferring inessential questions to swap space is improved focus on the inherent complexity of the problem, the solution to which is the ultimate goal. Accidental complexity has a talismanic quality. We have an unfortunate but understandable tendency to get distracted by mere details of how to express an idea in code. It is true that eventually we will need a decision about the accidental aspects of the problem, but as a rule it is better to tackle the essential complexity first. The rest is FIXMEs.

Your brain is prone to worrying that you may after all forget about the hardcoded value left in the code or the quadratic complexity that might blow up in production. People will be angry at you, or, what may be worse, you'll blame yourself. Whether we like it or not, emotions and other sub-conscious processes play a big role in our ability to focus. The unconscious mind will not allow itself to stop worrying about a task unless it is written down, with a system in place ensuring you will circle back to the topic without risking emotional backlash. Writing down FIXMEs has a liberating effect. With CI as a safety net, the brain can relax.

## Three ways of eliminating FIXMEs

After moving the distraction out of the way, you can focus on the core problem. When your RAM isn't cluttered with inessential details, the clearer view gives you a better shot at seeing the solution. At some point, of course, you will need to go back to the problem you snoozed. Having reached this point, eliminating the FIXME can take one of three possible forms.

1. The most straightforward resolution is to face the problem head-on. In other words, you perform the task you anticipated initially, for instance by refactoring the code to get access to the user-id you needed or by rewriting the algorithm to require O(n) iterations instead of O(n2). Frequently, a test case needs to be added as well.

2. Perhaps surprisingly, you may be able to simply delete the FIXME at this point. Sometimes things fall into place. In the process of implementing the main logic, you often end up solving the incidental issue as a consequence of the approach chosen, perhaps without even realizing it. Additionally, addressing the essential complexity puts you in a better position to understand the original incidental problem. Equipped with this knowledge, you may realize there's nothing left to fix. Perhaps in practice n is always 3, so O(n2) complexity is perfectly fine. Or you recognize that, with the confidence gained from completing an implementation, your worry was about something that wasn't worth worrying about in the first place.

3. Finally you can choose to downgrade the FIXME to TODO status. A TODO can remind you that the concern should be addressed at some point in the future. Often, filing a follow-up issue in your issue tracker will help make sure that the matter is not forgotten.[^google] Filing a follow-up is not always what we would prefer, but it's a valuable tool, especially when working in teams. Long-running feature branches cause unnecessary integration work, so it's desirable to merge branches as soon as possible. Whether downgrading is the right call depends on the specifics of your project's goals and values. But when applied with caution, it's a valuable tool for managing priorities and focusing on the essential.

In a nutshell, that's why I'm a fan of FIXMEs automatically enforced by CI. FIXME-driven development adds to our autonomy, rather than subtracting from it, because it guides us towards what matters and because it optimizes for what is arguably our most precious resource - our cognitive capacity.

[^xxx]: There is evidence that, like many coding conventions in use today, significant codetags were first introduced by Unix hackers in the BSD codebase. Possibly the earliest example of the codetag XXX (used more or less synonymously with FIXME today) is a [1981 commit](https://github.com/dspinellis/unix-history-repo/commit/9e295a2f65c046125ece0ad68f142f59df4c3400) by Bill Joy, the author of vi. FIXME seems like it became popular later. When 4.4BSD came out, its source contained 1500 instances of `XXX`, compared to only 332 of `FIXME` (mostly in gcc and gdb). For more software archaeology, see [Juho Snellman's fascinating investigation](https://www.snellman.net/blog/archive/2017-04-17-xxx-fixme/).

[^linux]: The Linux kernel repository contains 4041 FIXMEs and 5720 TODOs. It clearly doesn't adhere to the rule proposed in this post.

[^grep]: Our linter rule can be expressed in a single line of shell code: `git grep FIXME && exit 1`.

[^meta]: Note that FIXMEs can appear outside of code comments, in locations as diverse as exceptions, user-visible strings or SQL queries. Even writing documentation or blog posts (including this one) benefits from automatically enforced FIXMEs.

[^juggling]: The juggling analogy plays a central role in Rich Hickey's [Simple Made Easy](https://github.com/matthiasn/talk-transcripts/blob/master/Hickey_Rich/SimpleMadeEasy.md).

[^better]: What's even better is to come back to the issue the next day with a clear mind.

[^google]: Google and other organizations require TODOs to include a ticket number.

[^gtd]: [GTD in 15 minutes](https://hamberg.no/gtd) is a concise and well-written summary of Allen's 2001 book, Getting Things Done.
