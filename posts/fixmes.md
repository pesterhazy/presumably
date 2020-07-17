---
title: "In Defense of FIXMEs (and Pair Programming)"
date-published: 2020-07-13
---

Not long ago, a new contributor to the Pitch codebase was surprised to learn that our linter blocks merges of pull requests containing the word _FIXME_. "This rule is ill-considered", he cried, exasperated, "We are lying to ourselves - these things still need to be fixed!" He clearly felt that the CI rule was curtailing his freedom. And it's never a bad idea to ask for justification of any convention, especially if, like ours, it's unusual and enforced by CI. In any case, I'll take his comments as an opportunity to attempt a defendse of FIXMEs.

Since the dawn of programming code has included comments, and for decades - at least since the 1980s - programmers have lent significance to tags like XXX, TODO or FIXME. Mostly tags are added to express that the author is not satisfied with a solution, both as a _note to self_ and for the benefit of other readers. The use of uppercase letters not only makes tags jump out visually, it also makes them easy to grep for. The contentious linter rule requires no more than a single line of shell code: `git grep FIXME && exit 1`.

Annotating code with tags is common enough, but semantics vary. With time codebases tend to assign specific meanings to individual tags, much like emojis in Slack workspaces gradually acquire a definite meaning. At Pitch we tag comments as TODO when code  _should_ be changed eventually, but not necessarily today. It is considered acceptable, perhaps even desirable to wait to make the improvement suggested. By contrast, a FIXME is more stringent: code is unfit for merge until the author addresses the problem. Our automated linter codifies this judgment priority: FIXMEs fail the build, but TODOs are given a pass.

# Learning to Let It Go

Tags with an agreed-upon meaning aid communication with team members while the pull request is in progress. "Is this a FIXME or a TODO?" you might ask your colleague, requesting a judgment of the status and priority of the less-than-satisfying implementation. Similarly, during code review, FIXMEs signal that the function annotated requires attention. The comment screams out, "Help! Ask me about this issue I can't make progress on!"

FIXMEs help the author as much as the reviewer, and that's where things get interesting. As David Allen has pointed out (in Getting Things Done) the brain does a bad job of letting go of information it considers important. Allen recommends making lists. When you write down a task on a list, you silence the nagging doubt in the back of your mind that you may not remember it. The list helps you relax about your work schedule because instead of incessantly worrying about things falling through the cracks, the brain can rest assured that the task is stored in a secure place. Making a list allows you to forget abut the task now, temporarily.

While working on a branch, I use FIXMEs in a similar way. When I encounter a stumbling block - a problem that will lead me away from my current path - I'll add a FIXME comment to the problematic line:

```
// FIXME: hardcoded user-id

// FIXME: time cmplexity is O(n2), check if fast enough
```

Sometimes FIXMEs concern trivial matters, like the question of how to get a value, like a user-id, from inside the context of a function. But even trivialities require bandwidth, and I might just not have enough of that to deal with the issue in addition to the problem I'm trying to solve. Other times the issue is more substantial and will require serious thought to resolve. But the issue, while interesting, is off-topic relative to my current interest, and although I will need to come back to this, I'll want leave it on the side for now.

Of the various code-wrangling maneuvers made possible by FIXMEs, hardcoding, a form of wishful thinking, is particularly useful. I frequently find while writing a function that a dependency value is awkward to retrieve without going on an extended refactoring safari. Wishful thinking, one of the most undervalued techniques in software engineering, beings with a what-if question. What if we had access to the user's id here? Well, let's just pretend that we already do by hardcoding the value here. This will only work for one particular user, of course, so it's not a generic solution - quite the opposite. But hardcoding a laughably specific "solution" will help you explore the problem space now and make progress on the design without worrying about specifics - a generic solution will come later.

# Pair Programming and Virtual Memory

Human minds are finite, almost painfully so. It has been said that, like the balls a juggler can keep in the air, the number of concurrent ideas a person can keep in their head at the same time is 7, or perhaps 3. But regardless of what estimate you choose to believe, the number is depressingly low. To switch metaphors, while programming the RAM or "working memory" is our scracest resource. True, there's a spectrum here: some people can read more values into memory than others. But even the best programmer quickly runs out of heap.

Pair programming is a huge help when facing a hard problem because your partner temporarily expandse your working memory by temporarily lending you part of their cognitive capacity. Virtual memory allows you to double your RAM by making use of hard drive. By writing down FIXMEs, you can achieve a similar effect. You temporarily extend your cognitive capacity by "swapping to disk": you remove an idea from your the working memory, with the intention to page it back in when needed, usually at the end of the sesion or, even better, the next day. (Incidentally, I find writing down FIXMEs especially valuable while pairing, when you say to your partner "Let's get back to this FIXME", making the priority an explicit topic of conversation.)

The net effect of swapping out inessentials is that you can more easily focus on the essential problem, the inherent complexity of the problem. Accidental complexity has a talismanic quality: we have an understandable but unfortunate tendency to be distracted by details of how to express an idea in code. It is true that eventually a decision will need to be made about the accidental details, but it's better to tackle the essential complexity first. The rest is FIXMEs.

The act of explicitly deprioritizing problems by converting them into FIXMEs can feel liberating. Like explained in Getting Things Done, this works well because your brain is prone to worrying: there's this nagging feeling that you may after all forget about the hardcoded value you left in the code or the quadratic complexity that might blow up with real data. And then people will be angry at you, or, what could be worse, you'll blame yourself. Emotions and other sub-conscious processes play a big role. The unconscious mind will only allow itself to stop worrying if it knows that the task is written down neatly in a secure place, and you will get back to it in time. Let it go, brain, the CI job has got your back!

# Three ways of eliminating FIXMEs

After swapping out the distraction, you can return to applying your full mental capacity to the core problem. It will still be hard, of course, but you'll have a better shot at seeing the solution if your RAM isn't cluttered with inessential details. Eventually of course, after you've implemented the main thrust of the logic (but before your branch is merged) you will need to go back to the problems you snoozed. At that point, there are three ways to eliminate the FIXME. First, you can face the problem head-on. Refactor the code so you can access the user-id in your function, or rewrite the algorithm to be O(n) instead of O(n2).

Second - and this happens surprisingly often - you may be able to simply delete the FIXME at this point. After the bulk of the implementation is done, you may have already solved the issue without noticing it, as a fallout of your implementation strategy. You may know more about the problem now: given that in practice n=3, O(n2) complexity is actually not a problem at all. Or you realize that, with the confidence gained from your implementation, you were really worrying about something that wasn't in retrospect worth worrying about.

Finally, if all else fails, you can choose to demote the FIXME to TODO status. After considering the matter, you decide that, while the problem you identified is real, it's not a merge blocker. A TODO can remind you that this is something that should be addressed at some point. Often, filing a follow-up issue in your issue tracker will help make sure that the matter is not forgotten. Filing a follow-up for later in the project, while not always what we prefer, is a valuable tool for larger teams, where it's desirable to merge branches earlier rather than later. Excessively long-running feature branches cause unnecessary additional integration work and merge conflicts. Whether demotion is the right call will depends on the specifics of your project. But when applied well, demotiing FIXMEs is a useful tool for managing priorities and focusing on the essential.

That's why I love FIXMEs. It's true that a linter rule preventing merges of code containing the word FIXME limits your freedom of expression. But constraints like this can help creativity. FIXME-driven development gives you autonomy by helping you focus on essentials first and manage what is arguably our most precious resources - our congitive capacity.
