---
title: "Monrepos and the Fallacy of Scale"
uuid: 9e53c9f8-8d97-4d36-9dbd-09ad725a1d29
author: Paulus
---

In a recent blog post, Matt Klein discusses the pros and cons - mostly cons, in his mind - of monorepos. In short: he doesn't like them, and he thinks that they are a terrible fit for most teams. This blog post is my attempt to articulate the (many) reasons I think this argument is misguided.

While I will mostly discuss why I think the argument goes off the rails, there is also a practical upside to this. In my opinion, monorepos really are a good choice for many, many teams. I hope that the considerations laid out below will help you find the best repository layout for your team. More generally, I hope to show that critical thinking will help you make the best choices for your team.

---

New companies usually start out with a single git repository. As more and more code is written, the question inevitably comes up whether it makes sense to separate out part of the code - a component of the system or a piece of functionaltiy that is autonomous - into a second repository. Monorepo refers to making the conscious choice not to do that. In companies adhering to this practice, all or most of its code is stored in only one repository.

Proponents of monorepos (among whom I count myself) argue that by keeping everything in one place and by removing barriers, Monorepos encourage sharing and simplify development. In his post, Matt argues that these benefits are merely an illusion. And he argues that they lead to tight coupling of components. Let's take these two points in turn.

Does the practice of keeping all code together in one place lead to better collaboration? My experience clearly bears out that claim. I've led monorepoification efforts a few times in the past, and each time the beneifts have been innumerable. Some benefits, like easier refactoring and always having a single consistent snapshot of your view of the project, were explicit goals. But other synergy materialized in unexpected ways, like suprising instances of code reuse or infrastructure problems being reduce from hard when crossing repo boundaries to doable with a few pragmatic lines of code.

Looking at it from the other side, could introducing near-insurmountable borders somehow make it easier to reuse a component? I think it's clear that it can only hurt your chances to preceive opportunities to use abstractions that cross boundaries.

If the suggestion for code sharing is to introduce a self-standing utility library that two code parts share, that may well be the right thing to do in some cases, but just as easily it may not. The bottom line is that you should pick the right abstraction and the right place for a function or class, based on the individual merits of the case - and not driven by a decision about creating a new git repository made a long time ago.

We're now approaching the heart of the issue. We're right to argue about the right way to modularize our code, because that's a large part of what we do as programmers, as well as one of the hardest. But it is important to see that modularization doesn't necessitate separate repositories. Is splitting up a repository the only way to introduce a module boundary? Of course not. We split up our code into functions, files, modules, classes or namespaces all the time.

And of course, repository boundaries are not the only way to draw module boundaries. Often the most natural way to do that is also the simplest - putting componnents, subprojects or libraries in subdirectories.

Within a single repo, a pull request will raise the right questions during code review. Is this folder the right place to implement this feature? Is this consistent with how do things elsewhere? Are you drawing the API boundaries around the most natural fault lines? Will the tests pass with this consistent snapshot of the project? Touching multiple subproject in a single commit is not always desirable, but it should be _possible_ if the need arises.

---

This brings us to the Matt's second argument that monorepos lead to tight coupling in the code base. He is of course right that reducing complexity by finding the right scope for classes and namespaces is crucial. But modules needn't be placed in separate repositories to achieve this goal. Subdirectories along with conventions (broken if necessary) are just as good a way to achieve this goal.

Polyrepofication is a ham-fisted way to enforce ownership boundaries. Developers are not arguing children that need to be confined to separate rooms to prevent arguments. With sufficient communication and good practices, a monorepo will allow you to escape the red herring (which repo should this go to?) and focus on the truly important question: where should we draw the module boundaries to keep this code maintainable, understandable and changeable in the light of future requirements?

In fact polyrepos have a much greater risk of making layout decisions too early. Just as you can't predict the future, you can't predict where the borders, the module boundaries, will be properly drawn 6 months from now. By making a decision now to split up repositories, you make risk making the call to too early and making it hard for yourself to reverse course.
