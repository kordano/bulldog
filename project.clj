(defproject bulldog "0.1.0-SNAPSHOT"
  :description "Blogging platform"
  :url "https://github.com/kordano/bulldog"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/clj" "src/cljs" "test/clj"]
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.122"]
                 [org.clojure/core.async "0.2.371"]
                 [io.replikativ/konserve "0.3.0-beta2"]
                 [net.polyc0l0r/hasch "0.2.3"]
                 [http-kit "2.1.19"]
                 [compojure "1.4.0"]
                 [garden "1.3.0-SNAPSHOT"]
                 [prismatic/dommy "1.1.0"]
                 [endophile "0.1.2"]
                 [org.omcljs/om "0.9.0"  :exclusions [cljsjs/react]]
                 [sablono "0.3.6"  :exclusions [cljsjs/react]]
                 [jarohen/chord "0.6.0"]
                 [cljsjs/react "0.14.0-1"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [secretary "1.2.3"]]
  :min-lein-version "2.0.0"
  :profiles {:dev
             {:dependencies [[midje "1.8.1" :exclusions [org.clojure/clojure]]
                             [cljs-react-test "0.1.3-SNAPSHOT"]
                             [cljsjs/react-with-addons "0.14.0-1"]
                             [org.clojure/tools.nrepl "0.2.12"]
                             [com.cemerick/piggieback "0.2.1"]]
              :plugins [[lein-midje "3.1.3"]
                        [lein-figwheel "0.4.1"]
                        [cljs-react-test "0.1.3-SNAPSHOT"]
                        [lein-doo "0.1.5"]]
              :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
              :figwheel {:nrepl-port 7888
                         :css-dirs ["resources/public"]}}}
  :plugins [[lein-cljsbuild "1.1.0"]
            [lein-sassy "1.0.7"]]
  :sass {:src "resources/sass"
         :dst "resources/public"}
  :prep-tasks ["compile" ["cljsbuild" "once" "release"]]
  :main bulldog.core
  :uberjar-name "bulldog-standalone.jar"
  :clean-targets ^{:protect false} ["resources/public/js" "target" "out"]
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
               :release
               {:source-paths ["src/cljs"]
                :compiler {:main bulldog.core
                           :verbose true
                           :output-to "resources/public/js/main.js"
                           :optimizations :advanced
                           :pretty-print false}}}}
  
  )

