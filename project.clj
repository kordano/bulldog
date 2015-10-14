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
                 [net.polyc0l0r/hasch "0.2.3"]
                 [prismatic/dommy "1.1.0"]
                 [org.omcljs/om "0.9.0" :exclusions [cljsjs/react]]
                 [midje "1.6.3"]
                 [sablono "0.3.6"]
                 [http-kit "2.1.18"]
                 [compojure "1.4.0"]]
  :profiles {:dev
             {:dependencies [[midje "1.6.3" :exclusions [org.clojure/clojure]]
                              [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                             [org.omcljs/om "0.9.0" :exclusions [cljsjs/react]]
                             [cljs-react-test "0.1.3-SNAPSHOT"]
                             [cljsjs/react-with-addons "0.13.3-0"]
                             [org.clojure/tools.nrepl "0.2.11"]
                             [secretary "1.2.3"]
                             [jarohen/chord "0.6.0"]
                             [com.cemerick/piggieback "0.2.1"]]
              :plugins [[lein-midje "3.1.3"]
                        [lein-figwheel "0.4.1"]
                        [cljs-react-test "0.1.3-SNAPSHOT"]
                        [lein-doo "0.1.5"]]
              :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
              :figwheel {:nrepl-port 7888}}}
  :plugins [[lein-cljsbuild "1.1.0"]]
  :main bulldog.core
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

