(ns site.title-slug
  (:require [io.perun.meta :as pm]
            [io.perun.core :as perun]
            [clojure.string :refer [split trim lower-case join]]
            [boot.core :refer [deftask with-pre-wrap]]))

(defn- decompose
  [raw]
  (let [normalized (java.text.Normalizer/normalize raw java.text.Normalizer$Form/NFKD)]
    (.replaceAll normalized "\\p{InCombiningDiacriticalMarks}+"  "")))

(defn- compact-spaces
  [raw]
  (join " "
        (split
         (trim raw) #"\s+")))

(defn slugify
  "Slugifies a given string"
  [raw]
  (-> raw
      clojure.string/lower-case
      decompose
      compact-spaces
      (clojure.string/replace " " "-")
      (clojure.string/replace #"[^a-z0-9-]" "")))

(defn update-file [{:keys [slug title subtitle] :as fi}]
  (let [full-title (str title (when subtitle (str ". " subtitle)))
        new-slug (-> full-title clojure.string/trim slugify)]
    (boot.util/dbug "%s -> %s\n" (pr-str full-title) new-slug)
    (assoc fi
           :slug (or slug new-slug)
           :full-title full-title)))

(deftask title-slug
  []
  (with-pre-wrap fileset
    (let [files (filter identity (pm/get-meta fileset))
          updated-files (->> files
                             (map update-file))]
      (perun/report-debug "slug" "generated slugs" (map :slug updated-files))
      (perun/report-info "slug" "added slugs to %s files" (count updated-files))
      (pm/set-meta fileset updated-files))))
