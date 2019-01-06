---
title: "Monorepos and the Fallacy of Scale"
uuid: 9e53c9f8-8d97-4d36-9dbd-09ad725a1d29
author: Paulus
---

In a recent blog post entitled "Monorepos: Please don’t," Matt Klein [discusses](https://medium.com/@mattklein123/monorepos-please-dont-e9a279be011b) the pros and cons - as it turns out, mostly cons in his view - of monorepos. The long and short of it is that he doesn't like them, and he thinks that they are not a good fit for most teams. I've been thinking about this question for a few years and have come to the opposite conclusion. This blog post is my attempt to articulate my thoughts on the utility of monorepos.

While I will mostly focus on where I think Matt's argument goes off the rails, there is also a practical upside to this. In my opinion, monorepos really are a good choice for many, many teams. Hopefully the considerations laid out below will help you find the best repository layout. But more generally I hope to show that critical thinking about context and scale is the best tool available for making this choice for your team.

---

Let's begin from a clean slate. New companies usually start out with a single empty git repository. As more and more code gets written, the question inevitably comes up whether it makes sense to separate out part of the code - a component of the system or a modular piece of functionality - into a second repository. When talk about monorepos, we mean the principled choice not to split repositories. In companies adhering to this practice, all the code written by its employees is stored in a single monolithic repository.

Proponents of monorepos (among whom I count myself) argue that by keeping everything in one place and by removing artificial barriers, monorepos encourage sharing and simplify development and tooling. In his post, Matt argues that these benefits are illusionary. Second, he argues that, far from helping, monorepos actually lead to tight coupling of components. Let's take these two points in turn.

Does the practice of keeping all code together in one place lead to better collaboration? In my experience that’s clearly the case. The monorepofication efforts I’ve initiated at multiple organizations with split repositories in each case produced innumerable benefits. Some results were explicit goals, like ease of refactoring and a unified consistent snapshot of your project. Other synergies materialized in unexpected ways, like surprising opportunities for code reuse or intractable infrastructure problems involving crossing repo boundaries reduced to a few pragmatic lines of bash script.

Looking at it from the other side, could introducing near-insurmountable borders somehow make it easier to reuse a component? I think it's clear that it can only hurt your chances to perceive opportunities to use abstractions that cross boundaries.

If the suggestion for code sharing is to introduce a self-standing utility library shared by two components, that may well be the right thing to do in some cases, but just as easily it may not. The bottom line is that you should pick the right abstraction and the right place for a function or class, based on the individual merits of the case - and not driven by a decision about creating a new git repository made a long time ago.

We're now approaching the heart of the issue. We're right to argue about the right way to modularize code, because that's much of what we're paid to do as programmers, as well as one of the hardest parts of the job. But it is important to see that modularization doesn't entail separate repositories.

Is splitting up a repository the only way to introduce a module boundary? Of course not. We split up our code into functions, files, modules, classes or namespaces all the time. And of course, repository boundaries are not the only way to draw module boundaries. Often the most natural way to do that is also the simplest - putting components, subprojects or libraries in separate subdirectories.

Within a single repo, a pull request will raise the right questions during code review. Is this folder the right place to implement this feature? Is this consistent with how we do things elsewhere? Are you drawing the API boundaries around the most natural lines? Will the tests pass with this consistent snapshot of the project?

True, touching multiple subprojects in a single commit is not always desirable. For example, updating backend and frontend components in stages can be the better approach. But even so, it’s useful to have the option for simplicity or enforced coordination. We should have enough flexibility to ensure that cross-boundary changes are not _impossible_ if the need arises.

---

This brings us to Matt's second argument, the idea that monorepos lead to tight coupling between components. Reducing complexity by finding the right scope for classes and namespaces is crucial. So Matt is right to draw attention to worries about modularization. Our goal, after all, is to keep complexity at bay. But there’s no need to place modules in separate repositories. Subdirectories along with conventions (which can be broken if necessary) are a better way to meet this goal.

Splitting a codebase into sub-repos is a ham-fisted way to enforce ownership boundaries. Developers are not arguing children that need to be confined to separate rooms to prevent fights. With sufficient communication and good practices, a monorepo will allow you to avoid the question, which repo should this piece of code go? Instead of thinking about repo boundaries - effectively a distraction - a monorepo allows you to focus on the important question: where should we draw the boundaries between modules to keep the code maintainable, understandable and malleable in the light of changing requirements?

In fact splitting up repos carries a greater risk of calcifying layout decisions. As Agile has taught us, you can't reliably guess where the project is headed in the future. Similarly you can't predict where module boundaries will be properly drawn twelve months from now. By deciding to split up repositories now, you risk making the call to too early and making it unnecessarily difficult to reverse course. (Merging repositories, while possible, is cumbersome and an infrequent occurrence because it requires buy-in from, and coordination with, the whole team.)

I find it helpful to think of a company as a group of people engaged in a single project with a common mission. Even as the company pursues its mission through multiple subprojects, it helps to think of every decision taken and every code change introduced as a step towards its primary goal. The code base is the codification of a large chunk of the company's institutional knowledge about its goals and about what it considers the means to that end.

Looking at it from this perspective, a monorepo is the most natural way to express the fact that all team members are engaged in a single, if multi-faceted enterprise. So if you can keep all of this bundled up together, identified by a unique SHA hash and helpfully tracked through time by a VCS, why wouldn't you?

---

Well, if the benefits are real, why do companies reject monorepos? Matt's answer is that the approach doesn't scale. I think that this is a common assumption so I will focus on this - in my view ultimately misguided - justification in the rest of this post.

Matt points out that when he worked at Twitter, the introduction of a git-based monorepo caused significant performance issues and let to simple commands taking minutes to complete. I fully believe that these pain points are real. But we're not well-advised to conclude from such anecdotes that teams should reject monorepos because git won't scale.

It's worth breaking this idea into two parts. The first, that git doesn't in principle scale for projects with a few hundred participants over multiple years, is not generally plausible. In fact, the Linux kernel, git’s original raison d'être, can be cloned on the hopelessly underpowered broadband connection in this Berlin coffee shop in just a few minutes, and common repository operations are plenty fast. If Linux is not a successful poster child for collaboration in the large, then what is?

More generally, code repositories growth is steady but slow because change sets are Human Scale. At the end of the day, there's only so much furious typing that a few hundred developers can do over the course of a few years. Of course like any tool git needs to be used within its intended scope. But in a reasonable large team, unless you commit junk to the repository (I'm looking at you, Golang community) and only track files hand-written by the team (as opposed to generated code, third-party code and binaries), you'll probably be fine with a bit of discipline and prudent use of tooling.

But even if you expect to produce more code than the kernel team in the next five years, does that mean that you should reject a monorepo now? This is the second component I mentioned above. I think that this doesn't follow.

Most of the readers of this post are not in a position to pick a repository layout for very large teams. Nor is most code produced today written in the context of very large teams with hundreds of individual contributors. Most of us, especially in the world of startups, work in smaller teams - let's define a small team as less than 100 developers. And although some of our code will live on in near-perpetuity, a lot of our code will enjoy only a relatively short lifespan (and sadly we don't typically know ahead of time whether that will be the case for any individual module). For the many developers working in small teams, practical problems of repository scalability are not likely to come up in the near term. (If the monorepofication czar at Twitter is reading this, hello there! What follows may not apply to your team, as I'm now talking about the non-unicorn rest of us.)

The truth is that even though we can in some areas learn from the experience of giants like Twitter, we don't have their scale of employees or users and we may never reach it. And yet we often make choices based on the idea that we absolutely _must_ scale to be successful and so we might as well make what we take to be the more scalable decision today.

This is what I call the **Fallacy of Scale**. Very large teams are qualitatively different from small teams in countless ways. Context matters: what works for Twitter, a twelve-year old billion-dollar business, won't necessarily be right for your startup. What's more, the tooling that works for Twitter in 2019 wouldn’t have been appropriate for Twitter itself in its early years, effectively a different company.

A better way to think about code layout and other architecture decisions relating to scale is to find a solution that works for your team today and in the next six months. Without feigning ignorance about the near-term, try not to predict the future. More concretely, when - or more realistically, if - you outscale a monorepo, that’s the right time to adjust course. Before you reach that point, you will not know reliably what scaling issue will come up.

Once you do encounter a real scalability hiccup you can take action based on concrete pain points by introducing tooling to fix specific problems or by switching to a multi-repository setup. But don't make the cardinal mistake of startups: worrying about crossing a bridge before you get to it.

Thanks to Michael Reitzenstein, Ben Lovell, Misha Karpenko and Anke Breunig for reading earlier drafts of this post.
