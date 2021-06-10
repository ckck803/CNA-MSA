#!/usr/bin/env bash
#

http POST http://localhost:8080/reservations bookId="B1001" customerId="C1001" movieId="M1001"
http POST http://localhost:8080/reservations bookId="B1002" customerId="C1002" movieId="M1002"
http POST http://localhost:8080/reservations bookId="B1003" customerId="C1003" movieId="M1003"
http GET http://localhost:8080/reservations

http DELETE http://localhost:8080/reservations/B1001
http GET http://localhost:8080/reservations

http http://localhost:8080/approvals 
http http://localhost:8080/movieSeats
http http://localhost:8080/movieManagements 


kubectl run multitool --image=praqma/network-multitool
kubectl exec -it pod/multitool -n mall-ns -- /bin/sh