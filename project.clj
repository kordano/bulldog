(defproject bulldog "0.1.0-SNAPSHOT"

  :description "Blogging platform"

  :url "https://github.com/kordano/bulldog"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/clj"]

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [org.clojure/core.async "0.2.374"]
                 [io.replikativ/konserve "0.3.2"]
                 [net.polyc0l0r/hasch "0.2.3" :exclusions [org.clojure/clojure]]
                 [http-kit "2.1.19"]
                 [compojure "1.4.0"]
                 [prismatic/dommy "1.1.0"]
                 [endophile "0.1.2"]
                 [org.omcljs/om "1.0.0-alpha22"]
                 [sablono "0.5.3"]
                 [cljsjs/react "0.14.3-0"]
                 [cljsjs/react-dom "0.14.3-1"]
                 [cljsjs/react-dom-server "0.14.3-0"]
                 [figwheel-sidecar "0.5.0-SNAPSHOT" :scope "test"]]

  :min-lein-version "2.0.0"
  
  :clean-targets ^{:protect false} ["resources/public/js" "target" "out"]
  
  )

