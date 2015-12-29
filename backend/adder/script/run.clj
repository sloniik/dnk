(use 'ring.adapter.jetty)
(use 'cabinet.web)

(run-jetty #'cabinet.web/app {:port 8080})
;(run-jetty #'web/app {:port 8080})