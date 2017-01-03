tech

- error document
- add link to github source to about page
- add to planet clojure (requires clojure feed)
- try klipse for interactive code snippets: http://blog.klipse.tech/reagent/2016/12/31/reagent-in-klipse.html

posts

- How to set up a timer in a reagent component
  - different approaches (def atom, let binding)

- 3rd party React components
  - [:> nativeComp {:key "value"}]

- Datomic: or set
```
pesterhazy [12:36 PM]  
I'm looking into set intersection query ("give me all products with features a, b AND c")

[12:37]  
Here's what I could come up with: https://gist.github.com/pesterhazy/1e0ce9f18035b1693ee9e55241b23be6

pesterhazy [12:37 PM]  
shared a Clojure snippet: datomic-set-intersection.clj 
(defn make-intersection-q
  "Generate an intersection q for arbitrary sets of features"
  [n]
  (assert (pos? n))
  {:find '[[?product-slug ...]]
   :in ['$ (->> (range 1 (inc n)) (mapv #(symbol (str "?v" %))))]
   :where
   (into ['[?product :cat.product/slug ?product-slug]]
         (map (fn [n]
                ['?product :cat.product/features (symbol (str "?v" n))])
              (range 1 (inc n))))})
​
(defn feature-intersection [db feature-slugs]
  (d/q (make-intersection-q (count feature-slugs))
       db
       (map (fn [feature-slug] [:cat.feature/slug feature-slug])
            feature-slugs)))
Add Comment Collapse

pesterhazy [12:37 PM]  
Is there a  more elegant way than programmatically generating queries? (edited)

robert-stuttaford [12:53 PM]  
@pesterhazy entity ref collections are sets; i’d try filtering over all entities with :cat.product/features and doing set intersections with clojure.set/intersection on the set of entities for the slugs (edited)

robert-stuttaford [12:58 PM]  
something like this

[12:58]  
 ```(let [slugs    ...
      db       ...
      ent-fn   #(d/entity db %)
      features (into []
                     (comp (map (fn [slug]
                                  [:cat.feature/slug slug]))
                           (map ent-fn))
                     slugs)]
  (into []
        (comp (map ent-fn)
              (filter #(seq (clojure.set/intersection (:cat.product/features %) features))))
        (d/datoms db :aevt :cat.product/features)))
```

[1:00]  
you can make dynamic datalog queries, and it should cache its own pre-processing on the result of your `make-intersection-q` for each of the `n` values for future use. you might instead want to produce a value for http://docs.datomic.com/clojure/#datomic.api/query, which puts args and datalog syntax into a single structure (edited)

thegeez [1:08 PM]  
@pesterhazy perhaps build up a rule for the AND-ing of the slugs?

[1:09]  
 ```(let [rule [(into '[(slug-and [?e])] (map #(vector '?e :cat.feature/slug %) feature-slugs))]]
  (d/q
   '{:find [?e]
   :in [$ %]
   :where [(slug-and ?e)]}
   db
   rule))```

pesterhazy [1:10 PM]  
ah dynamically generating a rule, interesting!

[1:11]  
@robert-stuttaford wouldn't your solution be slower, as it has to generate the feature set for each product?

[1:12]  
> you might instead want to produce a value for http://docs.datomic.com/clojure/#datomic.api/query, which puts args and datalog syntax into a single structure

[1:12]  
what do you mean by that?

[1:12]  
ah there's a difference between q and query

[1:13]  
very interesting stuff

robert-stuttaford [1:43 PM]  
possibly, @pesterhazy - i suppose it would!

val_waeselynck [1:43 PM]  
@pesterhazy dynamic conjunction can be achieved using double negation and dynamic disjunction in Datalog

robert-stuttaford [1:44 PM]  
i like @thegeez ‘s idea

robert-stuttaford [1:51 PM]  
@val_waeselynck is there a code sample that illustrates your idea?

pesterhazy [1:51 PM]  
De Morgan's law?

val_waeselynck [1:51 PM]  
@robert-stuttaford working on it :slightly_smiling_face: not sure it's possible in this case because of the constraint that `not-join` must operate on one data source

[1:51]  
@pesterhazy yeah essentially

robert-stuttaford [1:52 PM]  
my autodidactism bites again. never did CS -pout-

val_waeselynck [1:52 PM]  
added a Clojure snippet 
[:find [?product-slug ...] :in $db $feats :where
  [?product :cat.product/slug ?product-slug]
  (not-join [?product]
    [$feats ?f]
    (not-join [?product ?f]
      [?product :cat.product/features ?f]))]
Add Comment Click to expand inline 6 lines

robert-stuttaford [1:52 PM]  
neat!

val_waeselynck [1:52 PM]  
but i don't think this one will work out of the box

[1:53]  
because of the fact that `not-join` works only on one data source

pesterhazy [1:53 PM]  
I'm in the same boat Robert

val_waeselynck [1:54 PM]  
the following may work

val_waeselynck [1:55 PM]  
added a Clojure snippet 
(d/q '[:find [?product-slug ...] :in $ ?feats-set :where
       [?product :cat.product/slug ?product-slug]
       (not-join [?product]
         [?f :features/slug ?fs]
         [(contains? ?feats-set ?fs)]
         (not-join [?product ?f]
           [?product :cat.product/features ?f]))]
  db #{"feature1" "feature2" "feature3"})
Add Comment Click to expand inline 8 lines

val_waeselynck [1:55 PM]  
not sure about the performance though :slightly_smiling_face:

pesterhazy [2:00 PM]  
Nice!!

val_waeselynck [2:01 PM]  
This is not the first time I see something like this come up - should probably be part of some best practices / tips and tricks section of the Datomic docs

val_waeselynck [2:06 PM]  
If this does not work-out performance-wise, then yeah, probably either generate a query or rule, or call a function in-query which performs the EAVT traversal, or a combination of both :slightly_smiling_face:
```

direnv as an ide
