---
title: "Monorepos and the Fallacy of Scale"
uuid: 9e53c9f8-8d97-4d36-9dbd-09ad725a1d29
author: Paulus
---

In a recent blog post, Matt Klein discusses the pros and cons - but frankly mostly cons, in his mind - of monorepos. The long and short of it is that he doesn't like them, and he thinks that they are not a good fit for most teams. I've been thinking about this question for a few years and have come to the opposite conclusion. This blog post is my attempt to articulate my thoughts on the utility of monorepos.

While I will mostly discuss why I think Matt's argument goes off the rails, there is also a practical upside to this. In my opinion, monorepos really are a good choice for many, many teams. I hope that the considerations laid out below will help you find the best repository layout for your team. More generally, critical thinking about context and scale is the best tool for making this choices for your team.

---

Let's begin with an empty slate. New companies usually start out with a single, empty git repository. As more and more code gets written, the question inevitably comes up whether it makes sense to separate out part of the code - a component of the system or a piece of functionaltiy that is autonomous - into a second repository. Monorepo refers to making the principled choice not to do that. In companies adhering to this practice, all or most of the code written by team members is stored in a single monolithical repository.

Proponents of monorepos (among whom I count myself) argue that by keeping everything in one place and by removing articial barriers, Monorepos encourage sharing and simplify development and tooling. In his post, Matt argues that these benefits are illusionary. Second, he argues that, far from helping, monorepos actually lead to tight coupling of components. Let's take these two points in turn.

Does the practice of keeping all code together in one place lead to better collaboration? My experience clearly bears out that claim. I've led monorepoification efforts a few times in the past, and each time the beneifts have been innumerable. Some benefits, like easier refactoring and always having a single consistent snapshot of your view of the project, were explicit goals. But other synergy materialized in unexpected ways, like suprising instances of code reuse or infrastructure problems being reduce from hard when crossing repo boundaries to doable with a few pragmatic lines of code.

Looking at it from the other side, could introducing near-insurmountable borders somehow make it easier to reuse a component? I think it's clear that it can only hurt your chances to preceive opportunities to use abstractions that cross boundaries.

If the suggestion for code sharing is to introduce a self-standing utility library that two code parts share, that may well be the right thing to do in some cases, but just as easily it may not. The bottom line is that you should pick the right abstraction and the right place for a function or class, based on the individual merits of the case - and not driven by a decision about creating a new git repository made a long time ago.

We're now approaching the heart of the issue. We're right to argue about the right way to modularize our code, because that's a large part of what we do as programmers, as well as one of the hardest. But it is important to see that modularization doesn't necessitate separate repositories. Is splitting up a repository the only way to introduce a module boundary? Of course not. We split up our code into functions, files, modules, classes or namespaces all the time.

And of course, repository boundaries are not the only way to draw module boundaries. Often the most natural way to do that is also the simplest - putting componnents, subprojects or libraries in subdirectories.

Within a single repo, a pull request will raise the right questions during code review. Is this folder the right place to implement this feature? Is this consistent with how do things elsewhere? Are you drawing the API boundaries around the most natural fault lines? Will the tests pass with this consistent snapshot of the project? Touching multiple subproject in a single commit is not always desirable, but it should be _possible_ if the need arises.

---

This brings us to the Matt's second argument that monorepos lead to tight coupling in the code base. He is of course right that reducing complexity by finding the right scope for classes and namespaces is crucial. But modules needn't be placed in separate repositories to achieve this goal. Subdirectories along with conventions (broken if necessary) are just as good a way to achieve this goal.

Polyrepofication is a ham-fisted way to enforce ownership boundaries. Developers are not arguing children that need to be confined to separate rooms to prevent arguments. With sufficient communication and good practices, a monorepo will allow you to escape the red herring (which repo should this go to?) and focus on the truly important question: where should we draw the module boundaries to keep this code maintainable, understandable and changeable in the light of future requirements?

In fact polyrepos have a much greater risk of calcifying layout decisions too early. Just as you can't predict the future, you can't predict where the borders, the module boundaries, will be properly drawn 6 months from now. By making a decision now to split up repositories, you risk making the call to too early and making it hard for yourself to reverse course.

Remember we're talking about working in a team in a company. Think of the company as a single enterprise with a mission. Even as the companies pursues the mission using multiple projects, it helps to think of the every decision and every code change as a step towards the primary goal. The code base is the codification of (a large part of) the company's institutional knowledge about its project and about (what it thinks is) the best way to acheive this project.

Looking at it from this perspective, a monorepo is the natural way to express the fact that all team members are collaborating on a single, if multi-faceted project. So if you can keep all of this bundled up together, identied by a SHA hash and complete with a single history, why wouldn't you?

---

Well, why wouldn't you be excited about monorepos? Matt's answer is that the approach doesn't scale. I think that this is a very common thought so I will focus on this - in my view fallacious - justification in the rest of this post.

Matt points out that at Twitter, the introduction of a monorepo based on Git has caused significant performance issues for team members, with simple commands taking minutes to complete. I fully believe that these pain points are real. But does this mean that teams should reject monorepos because Git won't scale?

It's worth breaking this idea into two components. The first, that Git doesn't in principle scale for projects with 100s of participants over multiple years, is not very plausible. In fact, the Linux kernel - the project that Git was developed for initially - can be easily cloned on hopelessly underpowered German cafe broadband in a few minutes, and common repository operations are sufficiently fast. If Linux is not a successful poster child for collaborative work in a large, diverse team, what is?

More generally, code repositories grow steadily but slowly because change sets are Human Scale. At the end of the day, there's only so much furious typing that a few hundered developers can do over the course of a few years. Of course like any tool Git needs to be used within its intended scope. But in a reasonable large team, unless you commit junk to the repository (I'm looking at you, Golang community) and only track files hand-written by the team (as opposed to generated code, third-party code and binaries), you'll probably be fine with a bit of discipline and prudent use of tooling.

But even if you expect to produce more code than the kernel team in the next five years, does that mean that you should reject a monorepo now? This is the second component alluded to above. I think that this doesn't follow.

Most of the readers of this post are not in a position to pick a repository layout for very large teams. Nor is most code produced today written in the context of very large teams with 100s of individual contributors. Most of us, especially in the world of startups, work in smaller teams - let's define a small team as less than 100 developers - and although some of our code will live on in near-perpetuity, a lot of our code will enjoy a relatively short lifespan (and sadly we don't usually know whether that's the case for any individual module). For the many developers working in small teams, practical problems of repository scalability are not likely to come up any time soon. (If the monorepofication czar at Twitter is reading this, hello there! Please stop reading now as I'm talking about the rest of us.)

The truth is that even though we can in some areas learn from the experience of giants like Twitter, we don't have their scale of employees or users and we may never reach it. And yet we often make choices based on the idea that we _must_ scale to be successful and so we might as well make the scalable decision today.

This is a Fallacy of Scale. Very large teams are qualitatively different from small teams in many ways. This hardly needs spelling out but context really does matter - what works for Twitter, a twelve-year old company with billion dollar revenue, won't necessarily be right for your startup. What's more, the tooling that works for Twitter as of 2019 are likely not be appropriate even for Twitter itself - the same company in the first years of its existance.

The right way to think about architecture decision relating to scale is to make the decisions that work for your team today and in the next 12 months. Try not to be foolishly ignorant about the future, but don't try to predict it. When (if!) you get to the point where a monorepo doesn't scale anymore, you'll know it but you may not know it before you reach that point. When you get there you may decide to introduce tooling to fix specific problems, or change to a multi-repository layout. But don't make the cardinal mistake of all startups - worrying about crossing that bridge before you get to it.

Thanks to Michael Reitzenstein and Ben Lovell for reading earlier drafts of this post.
