---
title: "Monorepos and the Fallacy of Scale"
uuid: 9e53c9f8-8d97-4d36-9dbd-09ad725a1d29
author: Paulus
date-published: 2018-01-07
---

In a recent [blog post entitled "Monorepos: Please don’t,"](https://medium.com/@mattklein123/monorepos-please-dont-e9a279be011b) Matt Klein discusses the pros and cons - as it turns out, mostly cons in his view - of monorepos. The long and short of it is that he thinks that they are not a good fit for most teams. After thinking about this question for a few years I have come to the opposite conclusion.

In this reply I will try to articulate my thoughts on monorepos. While I will focus on where I think Matt's argument goes off the rails, I also want to stress that monorepos really are a good choice for many, many teams. Hopefully the reasons laid out below will help you find a good repository layout. But most of all I hope to show that critical thinking about context and scale is the best tool available for making this choice for your organization.

## Benefits of monorepos

In the beginning was the initial commit. New companies usually start out with a single empty git repository. As more and more code gets written, the question invariably comes up whether it makes sense to separate out part of the code - a component of the system or a modular piece of functionality - into a second repository. When we talk about monorepos, we mean the principled choice not to do that, ie. not to split repositories. In companies adhering to this practice, all the code written by employees is stored in a single monolithic repository.

Proponents of monorepos (among whom I count myself) claim that by keeping everything in one place and by removing artificial barriers, monorepos encourage code sharing and simplify development and tooling. In his post, Matt argues that these benefits are illusionary. Second, he argues that, far from helping, monorepos actually lead to tight coupling of components. I’ll start with the first point and discuss the point about coupling further down.

Does the practice of keeping all code together in one place lead to better code sharing? In my experience that’s clearly the case. The monorepofication efforts I’ve initiated at multiple organizations with split repositories in each case produced innumerable benefits. Some results were explicit goals, like better greppability, ease of refactoring and a unified consistent snapshot of the project. Other synergies materialized in unexpected ways, like surprising opportunities for code reuse or intractable infrastructure problems straddling repo boundaries reduced to a few pragmatic lines of bash.

Looking at it from the other side, could introducing strict borders somehow make it easier to reuse logic? I think it's clear that borders can only take away from your ability to perceive opportunities to use abstractions or to unify code.

Perhaps the suggestion for code sharing is to introduce separate utility library shared by two components. Although that may well be the right thing to do in some cases, it may just as easily not be the right way to go in others. The bottom line is that you should pick the right abstraction and the right place for a function or class based on the individual merits of the case - and not driven by facts about repos created a long time ago.

## Modularization

We're now approaching the heart of the issue. It is indubitably a good thing to explores various ways to modularize code, because finding the right answer is crucial to maintainable code, as well as one of the hardest parts of our job as programmers. But it is equally important to see that effective modularization doesn't entail separate repositories.

Is a repository split the only possible module boundary? Of course not. We divide up our code into functions, files, modules, classes or namespaces all the time. And often the most natural way to do draw module boundaries at the highest level is also the simplest: keeping components, subprojects or libraries in separate subdirectories.

Within a unified repo, a pull request will raise relevant questions during code review. Is this folder the right place to implement this feature? Is this consistent with how we do things elsewhere? Are we drawing the API boundaries along the most natural lines? Will the tests pass with this consistent snapshot of the project?

True, touching multiple subprojects in a single commit is not always desirable. For example, updating backend and frontend components incrementally in backward-compatible ways can be the better approach. But even so, it’s useful to retain the option of cross-boundary commits for many reasons including simplicity and enforced coordination. We should have enough flexibility to ensure that such changes are not _impossible_ if the need arises.

## Monorepos as the embodiment of a unified project

This leads us to Matt's second argument, the idea that monorepos cause tight coupling between components. Reducing complexity by reducing dependencies classes and namespaces is essential. So Matt is right to draw attention to worries about modularization - our goal, after all, is to keep complexity at bay. But to achieve this, there’s no need to place modules in separate repositories. Subdirectories along with conventions (which can be broken if necessary) are a better way to meet this goal.

If you think about it, splitting a codebase into sub-repos is a ham-fisted way to enforce ownership boundaries. Developers are not arguing children that need to be confined to separate rooms to prevent fights. With sufficient communication and good practices, a monorepo will allow you to avoid the question “which repo does this piece of code belong to?” Instead of thinking about repo boundaries - effectively a distraction - a monorepo allows you to focus on the important question: where should we draw the boundaries between modules to keep the code maintainable, understandable and malleable in the light of changing requirements?

In fact splitting up repos carries a greater risk of calcifying layout decisions. As Agile has taught us, you can't reliably guess where the project is headed in the future. Similarly you can't predict where module boundaries will be properly drawn twelve months from now. By deciding to split up repositories now, you risk making the call hastily, making it unnecessarily difficult to reverse course. (Merging repositories, while possible, is cumbersome and an infrequent occurrence because it requires buy-in from, and coordination with, the whole team.)

I find it helpful to think of a company as a group of people engaged in a single project with a common mission. Even as the company pursues its mission through multiple subprojects, every decision taken and every code change introduced is a step towards its primary goal. The code base is the codification of a large chunk of the company's institutional knowledge about its mission and about what it considers the best means to that end.

Looking at it from this perspective, a monorepo can be seen as the most natural expression of the fact that all team members are engaged in a single, if multi-faceted enterprise. So if you can keep all of this bundled up together, identified by a unique SHA hash and helpfully tracked through time by a VCS, why wouldn't you?

## But does it scale?

Well, if the benefits are real, why do companies reject monorepos? Matt's answer is that the approach won’t scale. I think that this - in my view ultimately misguided - assumption is common so I will focus on it in the rest of this post.

Matt points out that when he worked at Twitter, the introduction of a git-based monorepo caused significant performance issues and let to simple commands taking minutes to complete. I fully believe that these pain points are real. But we're not well-advised to conclude from such anecdotes that teams should reject monorepos because git won't scale.

It's worth breaking this idea into two parts. The first, that git doesn't in principle scale for projects with a few hundred participants over multiple years, is not generally plausible. In fact, the Linux kernel, git’s original raison d'être, can be cloned in just a few minutes on the hopelessly underpowered broadband of the Berlin coffee shop where I’m writing this post, and common repository operations are plenty fast. If Linux is not a successful poster child for collaboration in the large, then what is?

More generally, code repositories growth is steady but slow because change sets are Human Scale. At the end of the day, there's only so much furious typing that a few hundred developers can do over the course of a few years. Of course like any tool git needs to be used within its intended parameters. But in a reasonably-sized team, unless you commit junk to the repository (I'm looking at you, Golang community) and only track files hand-written by the team (as opposed to generated code, third-party code and binaries), you'll probably be fine with a bit of discipline and prudent use of tooling.

But even if you expect to produce more code than the kernel team in the next five years, does that mean that you should reject a monorepo today? This is the second part of the assumption I mentioned above. I think that the conclusion doesn't follow.

Most readers of this post, of course, are not in a position to pick a repository layout for very large teams. Nor is most code produced today written by very large teams consisting of hundreds of individual contributors. Many of us, especially in the world of startups, work in smaller teams - let's say less than 100 developers. And although some of our code will live on in near-perpetuity, a lot of it will enjoy only a relatively short lifespan (and sadly we don't typically know ahead of time whether that will be the case for any individual module). For the many developers working in small teams, practical problems of repository scalability are not likely to come up in the near term. (If the monorepofication czar at Twitter is reading this, hello there! What follows may not apply to your team, as I'm now talking about the non-unicorn rest of us.)

## The Fallacy of Scale

The truth is that even though we can in some areas learn from the experience of giants like Twitter, we don't necessarily have their scale in terms of either employees or users and we may never reach it. And yet we often make choices based on the idea that we absolutely _must_ scale to be successful and that we need to make what we take to be the _most scalable decision_ today.

This is what I call the **Fallacy of Scale**. It’s a fallacy because large teams are qualitatively different from small teams in countless ways. Context matters: what works for Twitter, a twelve-year old billion-dollar business, won't necessarily be right for your startup. What's more, the tooling that works for Twitter in 2019 wouldn’t have been appropriate for Twitter itself in its early years, effectively a different company.

A better way to think about code layout and other architecture decisions relating to scale is to find a solution that works for your team today and in the next six months. Without feigning ignorance about the near-term, you should not to predict the future. More concretely, when - or more realistically, if - you outscale a monorepo, that’s the right time to adjust course. Before you actually reach that point, you will not know reliably what scaling issue will come up.

Once you do encounter a real scalability hiccup, you can take action based on concrete pain points by introducing tooling to fix specific problems or by switching to a multi-repository setup. But don't make the cardinal mistake of startups: worrying about crossing a bridge before you get to it.

Thanks to Michael Reitzenstein, Ben Lovell, Misha Karpenko and Anke Breunig for comments on earlier drafts of this post.
