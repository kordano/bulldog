(defproject bulldog "0.1.0-SNAPSHOT"

  :description "Blogging platform"

  :url "https://github.com/kordano/bulldog"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/clj"]

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [org.clojure/core.async "0.2.374"]
                 [org.clojure/core.cache "0.6.4"]
                 [org.clojure/core.memoize "0.5.8" :exclusions [org.clojure/core.cache]]
                 
                 [io.replikativ/konserve "0.3.3"]
                 [io.replikativ/replikativ "0.1.0"]
                 [io.replikativ/incognito "0.2.0-beta1"]
                 
                 [net.polyc0l0r/hasch "0.2.3" :exclusions [org.clojure/clojure]]
                 [http-kit "2.1.19"]
                 [compojure "1.4.0"]
                 [endophile "0.1.2"]
                 
                 [org.omcljs/om "1.0.0-alpha22"]
                 [prismatic/dommy "1.1.0"]
                 [sablono "0.5.3"]
                 [secretary "1.2.3"]
                 
                 [cljsjs/react "0.14.3-0"]
                 [cljsjs/react-dom "0.14.3-1"]
                 [cljsjs/react-dom-server "0.14.3-0"]
                 
                 [figwheel-sidecar "0.5.0-SNAPSHOT" :scope "test"]]

  :min-lein-version "2.0.0"
  
  :clean-targets ^{:protect false} ["resources/public/js" "target" "out"]
  
  )

