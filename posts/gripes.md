---
title: "Git Gripes and Greybox Thinking"
---

Even though Git has been ubiqutous for a decade, blog posts are frequently published complaining that it is error-prone and has terrible UI. New users often report messing up their local state, requiring the help of an experienced developer to get themselves untangled.

Do these gripes have merit? Although beginners often have a hard time learning the tool, the claim that this indicates a deficiency in Git is based on an incorrect assumption — the idea that a tool like Git should be treated as a black box. Instead, I will argue, safe and confident use of Git, as well as other technologies, requires adopting what I will call a greybox stance.

---

There's no denying that a barrier of entry exists when learning Git. Concepts like remotes, branches or staging area easily confuse the beginner. The UX could be better. I, too, don't know why `git checkout` do two seemingly different things, and I get frustrated remembering the exact behavior between soft and hard `git reset`.

These are initial speed bumps, however, and experience Git users rarely make mistakes anymore, and if a problem happens, you know how to undo it. Like any tool, you get used to it and, with time, learn its sharp edges. Some of the frustration encountered by the novice, then, is the natural effect of learning unfamiliar concepts. And although I emphasize with the feeling, there's often an unfounded expectation that a new technology will be easy.

In reality, learning with the goal of understanding is rarely effortless. After all, learning new concepts requires thwarting your expectations and breaking down preconceived notions, much like building muscle requires breaking down and rebuilding existing muscle tissue. We instinctively know this, and yet we often get frustrated while learning anyway. Why does everything have to be so complicated? Just let commit my work. (Of course, weeks later you will realize, in a flash of sudden understanding, why the staging area works the way it does and, perhaps, does to _of necessity_)

When building tools developers often face a fundamental tradeoff between intuitiveness for a novice and long-term power and robustness. On this continuum, Git repesents a point closer to long-term power, even if over the years it has become easier to use. But in general Git favors sounds concepts over facility, and it does to because it optimizes for a long-term use. Like any tool that is used daily by professionals, the time that you're a novice — the first week or so — is dwarfed by the years and years of use you'll use. It would be a mistake to optimize for the first week, which only represents a small fraction of your time with Git over its entire lifespan.

---

You get over this speed bump by reading the documentation, asking your colleagues and, above all, using Git in anger for a while, learning how to get yourself out of a bind if you do get stuck. But over and above this, there's excellent advise out there: go read this blog post or that explaining the Git internals. Over the years, a number of resources have appeared that walk you through how you would go about implementing Git from scratch. More than anything else, these guides help you become proficient in using the tool properly.

In fact, a good command of Git requires a basic understanding of the data structures used to represent a repository. It may be argued that this represents some sort of deficiency. If the developers of Git made it impossible to use the tool safely without knowing its internals, have they failed at their job?

I think the unwillingness to engage with details is the deeper reason why people sometimes have trouble learning Git properly. It's also easy to see where this attitude is coming from. As computer users, we are used to taking the abstractions we're exposed to at face value. When we learn a new tool, we'll treat it as a blackbox — as a system defined purely by its inputs and outputs. In the case of git, we learn the different operations — checkout, add, commit and so forth — while ignoring what's happening under the hood when those commands are executed.

But with Git, peeling off the outer layer of abstraction and understanding the underlying data structures helps makes sense of why Git works the way it does, and why, given the choice of data structures, it has to work the way it does. Consider two examples:

- _Lightweight branches_: In Git a branch is simply a pointer to a commit. If you know this, you instantly now that switching creating a new branch with the state of the repository is near-instantaneous. After all, all that needs to happen is to write a new SHA hash to a file in the `.git` directory. No data is copied, and no network packets are sent over the wire.

- _Commits as snapshots_: When you use `git show` to show a commit, the output is a patch similar to the output of the hoary `diff -u` tool. So it's easy to conclude that a commit is written to disk as a patch. But contrary to this natural expectation, what Git actually does when creating a new commit is to store a complete snapshot of your repository in `.git`. Code diffs are never stored on disk. What you see in `git show` is generated on the fly by comparing the snapshot pointed to by the commit to its parent. 

When I learned the latter fact I was confused, because I had naively assumed that storing things in this way would be hopelessly inefficient, both in terms of disk space requirements and performance. But I turned out to be wrong on both counts. The diff algorithm is fast enough to make on-the-fly calculations feel instantaneous. And Git objects are stored efficiently through the use of persistent data structures and compression, making storage requirements essentially a non-issue.

Lightweight branches and commits as snapshots are not immediately intuitive. But they turn out to be a beautiful design, which simplifies the user's reasoning about Git's behavior. But to leverage these elements of the design as a user, you need to know about them — you need to gently lift the lid on the abstraction. The reward is a deeper understanding.

---

The argument, then, is that efforts to learn Git are hampered by too much emphasis on Git's "porceallain", while ignoring its plumbing. I'd argue that you can't use Git with confidence without some level of understanding of its underpinnings and data structures.

Nor is Git unique in this regard. Users of relational databases like Postgres often find it tedious or difficult to learn about how indexes work. This makes it easy to shoot yourself in the foot, because indexes are at the core of how relational databases work. It turns out to be illuminating to understand the data structure used to represent indexes on disk. B-Trees have powered all SQL databases since the 70's.

Understanding this makes you realize what operations can be done efficiently, ideally in constant time. Essentially, B-Trees allow you to locate rows quickly by looking up finding a node in the tree matching the search criteria. As a result, an index can be used in WHERE clauses, but crucially it also powers JOINs whose ON expressions are covered by indexes. Because range queries can use the structure of a B-Tree, the conditions can be inequalities like `>` or `>=` as well as equalities `=`.

Similarly, multi-column indexes (A, B) can speed up row selection. But this is true only if all columns starting from the left are specified, and all but the rightmost column use equalities rather than inequalities. So an index covering `A, B` can speed up a query selecting `WHERE A=3 and B>1000` but will be useless for `WHERE B<1000` in isolation. If you visualize the structure on disk as a tree, with nodes ordered by columns, it is easy to see why this is so. Peeling back the outer layer of abstraction of SQL makes this immediately evident.

In other words, just as with Git, it helps to go beyond treating Postgres as a blackbox. The story of software is the story of abstraction and again there's a continuum here. On the on extreme, relational calculus is undoubtedly a useful abstraction — some would say the most successful abstraction in computer science. And indeed, when SQL was introduced in the 70's, raising the level of abstraction was what made it so successful. The dominant model that came before the relational model - network databases - required the developer to write queries in a language inexorably tied to its implementation.

Essentially, before SQL came along, databases required the user to look at the system as a whitebox by writing queries as "walk this index, then find all the matching rows, then use that other index". This was bad news both for the user (who suffered cognitive overhhead due to unnecessary detail) and the databse vendor (who was unable to improve the implementation without breaking user queries). The power of SQL was that it hid the knowledge of irrelevant details, allowing the user to focus on the explicit interface exposed through the language, treating the system as a blackbox.

---

Databases before SQL were deficient because they lacked the power of abstraction of relational algebra, forcing the user to treat the system as a whitebox. This was bad. But it's also not optimal to take a pure blackbox approach, ignoring data structures completely. As it turns out, with powerful systems like Postgres, the best stance to take is to treat the system as a greybox, relying on the correctness guarantees provided by the blackbox abstraction while also taking into account charactersistcs that require peeling back the outer layer of abstraction just so much to be more confident in predicting the behavior of the system.

The same is true for Git. The best way to understand Git is to combine a grasp of its external interface (the procellain) with some level of understanding of the underlying data structures. Fortunately for those of us who want to learn about Git's plumbing, wears its heart on its sleeve. Inspecting its data structures is as easy as looking at the files contained in the `.git` subdirectory. Some data is stored as plain text. For example, `cat .git/HEAD` reveals what revision is currently checked out (and if it's in the "detached head" state). Objects, which are stored in a compact format, are easily uncovered using `git cat-file`.

The graybox approach to learning Git (and many other complex technologies worth learning) is successful, both in making you more proficient in the tool you're using and in becoming a better hacker. In the words of Git's creator:

> I’m a huge proponent of designing your code around the data, rather than the other way around, and I think it’s one of the reasons git has been fairly successful… I will, in fact, claim that the difference between a bad programmer and a good one is whether he considers his code or his data structures more important. Bad programmers worry about the code. Good programmers worry about data structures and their relationships (Linus Torvalds, 2006)

_Thanks to Arthur Caillau and James Mintram for discussing earlier drafts of this post_
