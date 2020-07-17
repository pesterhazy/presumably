---
title: "In Defense of FIXMEs (and Pair Programming)"
date-published: 2020-07-13
---

Not long ago, a new contributor to the Pitch codebase was surprised to learn that our linter blocks merges of pull requests containing the word _FIXME_. "This rule is ill-considered", he cried, exasperated, "We are lying to ourselves - these things still need to be fixed!" He clearly felt that the CI rule was curtailing his freedom. And it's never a bad idea to ask for justification of any convention, especially if, like ours, it's unusual and enforced by CI. Here are the reasons why I think the convention is justified.

Since the dawn of programming code has included comments, and for decades - at least since the 1980s - it has assing significance to tags like XXX, TODO or FIXME. Mostly tags are added to express that the author is not satisfied with a solution, both as a _note to self_ and for the benefit of other readers. The use of uppercase not only makes tags jump out visually, it also makes them easy to grep, meaning that the contentious linter rule requires no more than a single line of shell code: `git grep FIXME && exit 1`.

Annotating code with tags is a common practice, but semantics vary. With time codebases tend to assign specific meanings to individual tags, much like emojis in Slack workspaces gradually acquire a definite meaning. At Pitch we tag comments as TODO when code  _should_ be changed eventually, but not necessarily today. It is considered acceptable, perhaps even desirable to wait to make the improvement suggested. By contrast, a FIXME is more stringent: code is unfit for merge until the author addresses the problem. Our automated linter codifies this judgment priority: FIXMEs fail the build, but TODOs are given a pass.

# How tags help

Conventions aid communication with team members while the pull request is in progress. "Is this a FIXME or a TODO?" you might ask your colleague, requesting a judgment of the status and priority of the difficulty. And during code review, FIXMEs are a signal that the function annotated requires attention. They scream, "Help! Ask me about this issue I encountered!"

But the impact of FIXMEs goes deeper than that. And the reason can, I think, be found in how our brain works. As David Allen has pointed out (in Getting Things Done) the brain has considerable difficulty letting go of information it considers important, even just for a moment. That's why Allen recommends making lists - avoiding the nagging feeling in the back of your mind that you might be missing something, it helps you be relaxed about your work because instead of incessantly worrying about things you might need to do but have forgotten, the brain can be sure that the task is stored in a secure place. Making a list allows you to forget abut the task now, temporarily.

While working on a branch, I use FIXMEs in a similar way. When I encounter a stumbling block - a problem that will lead me away from the path I'm following - I'll add a FIXME comment to the problematic function:

```
// FIXME: hardcoded user-id

// FIXME: time cmplexity is O(n2), check if fast enough
```

Sometimes FIXMEs concern trivial matters, like the question of how to get a value, like a user-id, from inside the context of a function. But even trivialities require bandwidth, and I might just not have enough of that to deal with the issue in addition to the problem I'm trying to solve. Other times the issue is more substantial and will require serious thought to resolve. But the issue, while interesting, is off-topic relative to my current interest, and although I will need to come back to this, I'll want leave it on the side for now.

Hardcoding is useful enough that it deserves emphasis. Frequently what I find while writing a function is that a dependency value is awkward to retrieve without going on an extended refactoring safari. Wishful thinking, one of the most undervalued techniques in software engineering, beings with a what-if question. What if we had access to the user's id here? Well, let's just pretend that we do by hardcoding the value here. This will only work for one particular user, of course, so it's not a generic solution - quite the opposite. But hardcoding a laughably specific "solution" will help you explore the problem space now and make progress on the design without worrying about specifics - a generic solution will come later.

The bandwidth problem is real. It has been said that, like the balls a juggler can keep in the air, the number of ideas a person can keep in their head at the same time is 7, or perhaps 3. But regardless of what estimate you choose to believe, the number is depressingly low. To switch metaphors, while programming the RAM or "working memory" is our scracest resource. True, there's a spectrum here: some people have more capacity than others. But even the best programmers blow their stack pretty quickly.

I think this is one reason why pair programming works so well when dealing with hard problems: it allows you to temporarily expand your working memory because your pairing partner lends you part of their cognitive capacity. By writing down FIXMEs, you can achieve a similar "virtual memory" effect. You can temporarily extend your cognitive capacity by "swapping to disk": you remove an idea out of the working memory, with the intention to page it in when it's needed again - usually at the end of the sesion or, even better, the next day. (Incidentally, I find writing down FIXMEs especially valuable while pairing, when you say to your partner "Let's get back to this FIXME", making the priority an explicit topic of conversation.)

The net effect of swapping out inessentials is that you can more easily focus on the essential problem, the inherent complexity of the problem. Accidental complexity has a talismanic quality: we have an understandable but unfortunate tendency to be distracted by details of how to express an idea in code. It is true that eventually a decision has to be made about the accidental details, but it's better to tackle the essential complexity first. The rest is FIXMEs.

I find the act of explicitly deprioritizing problems by turning them into FIXMEs liberating. Like explained Getting Things Done, this works well because your brain is prone to worrying: there's this nagging feeling that maybe you'll forget about the hardcoded value, or the quadratic complexity, and then people will be mad at you (or maybe you'll be upset about yourself). The unconscious mind will only stop worrying if it knows that the task is written down somewhere in a secure place, and you will get back to it early enough. Let it go, brain, the CI job has got your back!

After swapping out the distraction, you can return to applying your full mental capacity to the core problem. It will still be hard, of course, but you'll have a better shot at seeing the solution if your RAM isn't cluttered with inessential details. 

Eventually of course, after you've implemented the bulk of the logic but before your branch is merged, you do need get back to the problems you snoozed so successfully. When the time comes, there are three ways to eliminate the FIXME. First, you can face the problem head-on. Refactor the code so you can access the user-id in your function, or rewrite the algorithm to be O(n) instead of O(n2).

Second - and this happens surprisingly often - you may be able to simply delete the FIXME at this point. After the bulk of the implementation is done, you may have already solved the issue without noticing it, as a fallout of your implementation strategy. You may know more about the problem now: given that in practice n=3, O(n2) complexity is actually not a problem at all. Or you realize that, with the confidence gained from your implementation, you were really worrying about something that wasn't in retrospect worth worrying about.

Finally, if all else fails, you can choose to replace the FIXME with a TODO. After considering the matter, you decide that, while the problem you identified is real, it's not a merge blocker. A TODO can remind you that this is something that should be addressed at some point. Often, filing a follow-up issue in your issue tracker will help make sure that the matter is not forgotten. Filing a follow-up for later in the project, while not always what we prefer, is a valuable tool for larger teams, where it's desirable to merge branches earlier rather than later. Excessively long-running feature branches cause unnecessary additional integration work and merge conflicts. Whather TODO-ification is an acceptable outcome will be a judgment call that depends on the specifics of your project, but when applied well, it helps you manage priorities and focus on the essentials.

That's my defense then: a linter rule preventing merges of code containing the word FIXME limits your freedom of expression. But constraints help creativity. FIXME-driven development gives you freedom by helping you focus on essentials first and manage your congitive capacity, arguably our most precious resource.
