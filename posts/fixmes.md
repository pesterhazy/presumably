---
title: "A defense of FIXMEs"
---

Not long ago, a friend noticed that in the codebase I'm currently contributing to, it isn't possible to merge pull requests that contain the word "FIXME". "Why would you set up your CI that way?", he asked. My guess is that he felt that the CI rule constrained his freedom of expression. Whatever his reason, I think it's fair to ask for an explanation for this practice. Such an explanation is what I'm going to attempt here.

Programmers have been peppering their code with special tags like XXX, FIXME and TODO for decades, at least since the 1980s. Special comments explain reasons behind the code, for the benefit of future readers (which may include the author). Uppercse tags also stand out visually and are easily grepable; in fact, the CI rule mentioned boils down to a simple `git grep -q FIXME && exit 1`.

Much like many Slack workspaces form a private mini-language out of emojis, every codebase assigns specific meaning to tags. In our case, we use TODO for code that _should_ be changed eventually, but not necessarily today. It is considered acceptable, perhaps even desirable to wait to make the improvement suggested. By contrast, a FIXME is mandatory: we consider the code unfit for merge until the problem is addressed. Our linter reflects this judgment: TODOs are given a pass, while FIXMEs fail the build.

What does this convention have to recommend itself? It eases communication during the genesis of the code. "Is this a FIXME or a TODO?" you might ask, making a judgment as to the status and priority of the problem in question. And during code review, FIXMEs jump out and scream, "Ask me about this question!"

But the impact of FIXMEs run deeper. And the reason can, I think, be found in how our brain works. As David Allen has pointed out (in Getting Things Done) the brain finds it hard to let go of important information. That's why Allen recommends making lists - it helps you be relaxed about your work because instead of incessantly worrying about things you might need to do but have forgotten, the brain can be sure that the task is stored in a secure place. You allow yourself to forget abut the task now, temporarily.

While working on a branch, I use FIXMEs in a similar way. When I encounter a question or a potential issue with my approach, if I can't solve it within 5 minutes or so, I'll write it down as a FIXME:

```
// FIXME: hardcoded user-id

// FIXME: time cmplexity is O(n2), check if fast enough
```

Sometimes the FIXME will concerns trivial problems - how do I get at the user-id from this function without breaking the function signature - that I just don't have the bandwith to deal with right now. Other times the issue is more substantial and will take serious thought to resolve. But later, not now - I don't want to deal with this right now.

Hardcoding a value or piece of logic that is awkward to get at without refactoring is such a useful trick that it deserves emphasis. This is one of the most powerful techniques we have: wishful thinking. What if we had access to the user-id here? Well, let's just pretend that we do and hardcode the value here. This will only work for one particular user, of course, so it's far from a generic solution. But that's the point. Hardcoding a laughably specific solution will help you make progress now and explore the problem space - a generic solution will come later.

The bandwidth problem is real. It has been said that, like the balls a juggler can keep in the air, the number of ideas a person can keep in their head at the same time is 7, or perhaps 3. But regardless of what estimate you choose to believe, the number is depressingly low. To switch metaphors, while programming the RAM or "working memory" is our scracest resource. There's a spectrum here: some people have more capacity than others. But even the best programmers throw OutOfMemoryExceptions pretty quickly.

I think this is one reason why pair programming works so well when dealing with hard problems: it allows you to temporarily expand your working memory because your pairing partner lends you part of their cognitive capacity. By writing down FIXMEs, you can achieve a similar "virtual memory" effect. You can temporarily extend your cognitive capacity by "swapping to disk": you remove an idea out of the working memory, with the intention to page it in when it's needed again - usually at the end of the sesion or, even better, the next day. (Incidentally, I find writing down FIXMEs especially valuable while pairing, when you say to your partner "Let's get back to this FIXME", making the priority an explicit topic of conversation.)

The net effect of swapping out inessentials is that you can more easily focus on the essential problem, the inherent complexity of the problem. Accidental complexity has a talismanic quality: we have an understandable but unfortunate tendency to be distracted by details of how to express an idea in code. It is true that eventually a decision has to be made about the accidental details, but it's better to tackle the essential complexity first. The rest is FIXMEs.

I find the act of explicitly deprioritizing problems by turning them into FIXMEs liberating. Like explained Getting Things Done, this works well because your brain is prone to worrying: there's this nagging feeling that maybe you'll forget about the hardcoded value, or the quadratic complexity, and then people will be mad at you (or maybe you'll be upset about yourself). The unconscious mind will only stop worrying if it knows that the task is written down somewhere in a secure place, and you will get back to it early enough. Let it go, brain, the CI job has got your back!

After swapping out the distractions, you can use your full capacity at the core of the problem you're dealing with. It will still be hard, of course, but you'll have a better shot at seeing the solution if you RAM isn't cluttered with inessential details. 

Eventually of course, after you've implemented the bulk of the logic but before your branch is merged, you do need get back to the problems you snoozed so successfully. When the time comes, there are three ways to eliminate the FIXME. First, you can face the problem head-on. Refactor the code so you can access the user-id in your function, or rewrite the algorithm to be O(n) instead of O(n2).

Second - and this happens surprisingly often - you may be able to simply delete the FIXME at this point. After the bulk of the implementation is done, you may have already solved the issue without noticing it, as a fallout of your implementation strategy. You may know more about the problem now: given that in practice n=3, O(n2) complexity is actually not a problem at all. Or you realize that, with the confidence gained from your implementation, you were really worrying about something that wasn't in retrospect worth worrying about.

Finally, if all else fails, you can choose to replace the FIXME with a TODO. After considering the matter, you decide that, while the problem you identified is real, it's not a merge blocker. A TODO can remind you that this is something that should be addressed at some point. Often, filing a follow-up issue in your issue tracker will help make sure that the matter is not forgotten. Filing a follow-up for later in the project, while not always what we prefer, is a valuable tool for larger teams, where it's desirable to merge branches earlier rather than later. Excessively long-running feature branches cause unnecessary additional integration work and merge conflicts. Whather TODO-ification is an acceptable outcome will be a judgment call that depends on the specifics of your project, but when applied well, it helps you manage priorities and focus on the essentials.

That's my defense of a linter rule preventing merges of code containing FIXME. FIXME-driven development helps you focus on the essentials first and conserve your congitive capacity, arguably our most precious resource.
