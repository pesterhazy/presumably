---
title: "A plea for FIXMEs"
date-published: 2020-07-13
---

Not too long ago, a new contributor to the Pitch codebase was surprised to learn that our linter will block the merge of any pull request containing the word _FIXME_. "This rule is ill-considered", he cried out, exasperated, "We are lying to ourselves - these things still need to be fixed!"

He clearly felt that the CI rule was curtailing his freedom, and it's true that our practice of blocking merges on FIXMEs is unorthodox. But the judicious use of this simple convention can make a big impact both on the team and individual level. In this post, I will make the case for automatically enforced FIXMEs - a tool that has helped me improve as a programmer.

# The meaning of code tags

Code tags are not a recent invention. Since the dawn of programming, code has been interwoven with comments, and since the 1980s programmers have lent significance to tags like XXX, TODO or FIXME. Most tags are added with the intention of flagging that the author is not satisfied with a solution, both as a _note to self_ and for the benefit of other readers. The use of uppercase letters not only makes tags jump out visually, it also makes them easy to grep for. The contentious linter rule requires no more than a single line of shell code: `git grep FIXME && exit 1`. And many IDEs give you a convenient way to browse tags.

While annotating code with tags is common enough, semantics vary from team to team. With time teams tend to assign specific meanings to individual tags, much like emojis in Slack workspaces gradually acquire a definite meaning. At Pitch we tag comments as TODO when code _should_ be changed eventually, though not necessarily today. It is considered acceptable, perhaps even desirable to delay the improvement suggested. A TODO is a soft sign for the future. By contrast, FIXMEs are stringent: the code is unfit for merge until the author addresses the problem. Our automated linter codifies this judgment: FIXMEs fail the build, but TODOs are given a pass.

Tags with an agreed-upon meaning aid communication between team members while the pull request is in progress. "Is this a FIXME or a TODO?" you might ask your colleague while pairing, requesting a judgment and clarification of the status and priority of a solution considered less-than-satisfying. Similarly, during code review an oustanding FIXME signals that the function annotated requires close attention. The comment screams out, as it were, "Help, I'm stuck! Ask me about this issue I encountered!"

# Code-wrangling maneuvers

FIXMEs are useful during review, but they can help the author as much as the reviewer, and that's where things get more interesting. During the code-wrangling phase, the phase when I'm actively implementing, trying things out and making small incremental improvements, the work is split up into a series of small changes, each resulting in a commit. Changes such as Inline Function and Rename Variable (and other refactorings) are instances of a maneuver, a generic pattern that, when applied on your code, gets you one step closer to your goal.

FIXMEs figure prominently in my repertoire of code-wrangling maneuvers. When I encounter a stumbling block, a consideration that will lead me away from my current path, I'll go in and add a FIXME comment to the worrisome line:

```
// FIXME: hardcoded user-id

// FIXME: time cmplexity is O(n2), check if fast enough
```

Sometimes FIXMEs concern trivial matters, like the question of how to get a user-id from inside the context of an existing function. But even trivialities require attention, and I often lack the mental bandwidth to deal with the issue in addition to the main problem I'm trying to solve. Other issues are more substantial and will require serious thought to resolve. But although doing this work may be interesting, it is still off-topic relative to my current focus, and although I will need to come back to this, it's often best to leave the matter to one side for now.

My favorite of all the various code-wrangling maneuvers made possible by FIXMEs is hardcoding, a form of wishful thinking. I frequently find while writing a function that a dependency value is awkward to retrieve without going on a refactoring safari. Wishful thinking begins with a what-if question. What if we had access to the id here? Well, let's just pretend that we already do by hardcoding the value here. This will only work for one particular user, of course, so it's not a generic solution - quite the opposite. But hardcoding a laughably specific solution will help you make progress on the design without worrying about details - a generic solution will come later.

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
   throw Error("FIXME: Not implemented");
}
```

You will be able to run the code so long as you don't encounter the missing case. If you do, you'll get an exception and a stacktrace. By relying on CI, the active FIXME will ensure that you won't forget about adding the missing case before finishing your branch.

Note that FIXMEs don't need to appear in code comments. They can appear in strings, SQL queries or even markdown files. 

# Pair Programming and Virtual Memory

As David Allen has pointed out (in Getting Things Done) the brain does a bad job of letting go of information it considers important. Allen recommends making lists. When you write down a task on a list, you silence the nagging doubt in the back of your mind that you may not remember it. The list helps you relax about your work schedule because instead of incessantly worrying about things falling through the cracks, the brain can rest assured that the task is stored in a secure place. Making a list allows you to forget abut the task now, temporarily.

Human minds are finite, almost painfully so. It has been said that, like the balls a juggler can keep in the air, the number of concurrent ideas a person can keep in their head at the same time is 7, or perhaps 3. But regardless of what estimate you choose to believe, the number is depressingly low. To switch metaphors, while programming the RAM or "working memory" is our scracest resource. True, there's a spectrum here: some people can read more values into memory than others. But even the best programmer quickly runs out of heap.

Pair programming is a huge help when facing a hard problem because your partner temporarily expands your working memory by lending you part of their cognitive capacity, just like the Linux virtual memory system allows you to double your RAM by making use of your hard drive. By writing down FIXMEs, you can achieve a similar effect. You temporarily extend your cognitive capacity by _swapping to disk_: you remove an idea from your working memory, with the intention to page it back in when needed, usually at the end of the sesion or, even better, the next day. (Incidentally, I find writing down FIXMEs especially valuable while pairing, when you say to your partner "Let's get back to this FIXME", making the priority an explicit topic of conversation.)

The net effect of temporarily transfering inessentials to swap space is that you can focus on the essential problem, the inherent complexity of the business problem you're being paid to solve. Accidental complexity has a talismanic quality. We have an understandable but unfortunate tendency to be distracted by details of how to express an idea in code. It is true that eventually we will need a decision about the accidental details, but it's almost always better to tackle the essential complexity first. The rest is FIXMEs.

Fixmification, i.e. deprioritizing problems by converting them into FIXMEs, can feel liberating. Like explained in Getting Things Done, this works well because your brain is prone to worrying: there's this nagging feeling that you may after all forget about the hardcoded value you left in the code or the quadratic complexity that might blow up with real data. And then people will be angry at you, or, what could be worse, you'll blame yourself. Emotions and other sub-conscious processes play a big role. The unconscious mind will only allow itself to stop worrying if it knows that the task is written down neatly in a secure place, and you will get back to it in time. Let it go, brain, the CI job has got your back!

# Three ways of eliminating FIXMEs

After moving the distraction out of the way, you can return to applying your full mental capacity to the core of the problem. With a clear view, you'll have a better shot at seeing the solution, because your RAM isn't cluttered with inessential details. At some point, after you've implemented the main thrust of the logic, you will need to go back to the problem you snoozed. Having reached this point, eliminating the FIXME can take one of three possible forms, the most straightforward of which is to face the problem head-on. What this boils down to is to do the work you anticipated initially, for example by refactoring the code so you have access to the user-id you needed or by rewriting the algorithm to require O(n) instead of O(n2) iterations.

But doing the work is not the only way forward. More interestingly, you may be able to simply delete the FIXME at this point. In my experience, this option presents itself surprisingly often. Having done the bulk of the implementation work, you may have already solved the original issue as a side-effect of the approach you ended up using - perhaps without even noticing that that's what you were doing. Sometimes things simply fall into place. Similarly, you may now in a better position to understand the problem. With this knowledge, there's nothing left to fix; perhaps given that in practice n=3, O(n2) complexity is actually not a problem at all. Or you realize that, with the confidence gained from having completed the implementation, you were really worrying about something that wasn't in retrospect worth worrying about.

Finally, if all else fails, you can choose to demote the FIXME to TODO status. While the problem you identified is real, it's not a merge blocker *here and now*. A TODO can remind you that the concern should be addressed at some point in the future. Often, filing a follow-up issue in your issue tracker will help make sure that the matter is not forgotten. (From what I hear, Google requires TODOs to include a ticket number). Filing a follow-up for later in the project, while not always what we would prefer, is a valuable tool when working in teams. Long-running feature branches cause unnecessary integration work, so it's desirable to merge branches as soon as possible. Whether demotion is the right call will depends on the specifics of your project's goals and values. But when applied well, FIXME demotion is a valuable tool for managing priorities and focusing on the essential.

That's why I've grown to love FIXMEs. It's true that a linter rule preventing merges of code containing the word FIXME limits your freedom of expression. But constraints like this can help creativity. FIXME-driven development gives you autonomy by helping you focus on essentials first and manage what is arguably our most precious resources - our congitive capacity.
