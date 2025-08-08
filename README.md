# spring-authentication
### Test Actuator
GET http://localhost:8080/actuator/health → No auth needed
GET http://localhost:8080/actuator/info → No auth needed
GET http://localhost:8080/actuator/admin → Needs ADMIN role (admin/admin123)

### Test API
GET http://localhost:8080/api/public → No auth needed
GET http://localhost:8080/api/secure → Needs auth (admin/admin123)
