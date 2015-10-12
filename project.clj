(defproject bulldog "0.1.0-SNAPSHOT"
  :description "Blogging platform"
  :url "https://github.com/kordano/bulldog"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/clj" "src/cljs" "test/clj" "test/cljs"]
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.122"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [io.replikativ/konserve "0.3.0-beta2"]
                 [midje "1.6.3"]
                 [http-kit "2.1.18"]
                 [compojure "1.4.0"]]
  :profiles {:dev
             {:dependencies [[midje "1.6.3" :exclusions [org.clojure/clojure]]
                             [org.clojure/tools.nrepl "0.2.11"]
                             [com.cemerick/piggieback "0.2.1"]]
              :plugins [[lein-midje "3.1.3"]
                        [lein-figwheel "0.4.1"]
                        [lein-doo "0.1.5"]]
              :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
              :figwheel {:nrepl-port 7888}}}
  :plugins [[lein-cljsbuild "1.1.0"]]
  :clean-targets ^{:protect false} ["resources/public/js"]
  :cljsbuild {:builds
              {:dev
               {:source-paths ["src/cljs"]
                :figwheel true
                :compiler {:main bulldog.core
                           :output-to "resources/public/js/main.js"
                           :asset-path "js/out"
                           :optimizations :none
                           :source-map true}}
               :test
               {:source-paths ["src/cljs" "test/cljs"]
                :compiler {:output-to "resources/public/js/test/main.js"
                           :main bulldog.test-runner
                           :optimizations :none}}
               :prod
               {:source-paths ["src/cljs"]
                :compiler {:main bulldog.core
                           :output-to "resources/public/js/bulldog.js"
                           :cache-analysis true
                           :optimizations :advanced}}}}
  
  )
