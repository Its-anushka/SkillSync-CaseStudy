# SkillSync Platform Endpoints API

Welcome to the **SkillSync Platform**. This document provides an exhaustive list of all RESTful API endpoints exposed across the microservices ecosystem.

All traffic should preferably be routed through the centralized **API Gateway** (running on port `8080`), appending the context paths below. All requests (except explicit auth overrides) demand an `Authorization: Bearer <token>` header, parsed by the global JWT filter.

---

## 1. Auth Service (`authentication-service`)
Handles user registration, identity authentication, and JWT lifecycle management.

* `POST /api/auth/register` : Register a new user and map them to a specified role.
* `POST /api/auth/login` : Authenticate credentials and receive a fresh JWT payload.
* `POST /api/auth/refresh` : Exchange a valid (but potentially near-expired) bearer token for a newly signed JWT.
* `GET /api/auth/admin/test` : Diagnostic token validation (Restricted: ADMIN only).
* `GET /api/auth/user/test` : Diagnostic token validation (Restricted: USER only).

## 2. User Service (`user-service`)
Handles comprehensive user profiles including personal info, bios, location, and global user retrieval.

* `POST /users/profile` : Initialize a profile for a newly registered user (commonly called internally by Auth Service).
* `PUT /users/profile` : Update the currently authenticated user's profile details.
* `GET /users/me` : Retrieve the profile associated with the currently authenticated token.
* `GET /users/{id}` : Fetch the profile information for a specific user ID.
* `GET /users` : Discover and list all public user profiles in the system.
* `PUT /users/{id}` : Selectively update a specific user's profile by ID.
* `PUT /users/admin/role` : Upgrade or demote an internal access role for a specific user (Restricted: ADMIN only).

## 3. Mentor Service (`Mentor-Service`)
Handles mentorship applications, global mentor searches, availability, and specific mentor bio configurations.

* `POST /mentors/apply` : Submit a learner's account for elevation into a mentor role.
* `GET /mentors/public` : Fetch all activated mentors in the platform, typically used for the discovery page.
* `GET /mentors/public/{id}` : Retrieve deep profile specifics encompassing experience and ratings for a target mentor.
* `PUT /mentors/{id}/availability` : Toggle or update the scheduling availability state for a specific mentor.

## 4. Skill Service (`Skill-Service`)
Maintains the centralized system catalog for all recognized peer learning skills and tags.

* `POST /skills` : Inject a new unique skill into the taxonomy (e.g., "Java", "Machine Learning").
* `GET /skills` : Discover the entire platform-wide array of cataloged skills.
* `GET /skills/{id}` : Identify domain specifics belonging to a single unique skill.

## 5. Session Service (`Session-Service`)
Handles the booking, state machine transitions, and history tracking for 1-on-1 mentorship sessions.

* `POST /sessions` : A learner requests a fresh mentorship block with a selected mentor on a specific date.
* `PUT /sessions/{id}/accept` : A targeted mentor formally accepts a requested session block.
* `PUT /sessions/{id}/reject` : A targeted mentor formally rejects an incoming session booking.
* `PUT /sessions/{id}/cancel` : Aborts an existing session entirely (can be triggered by either party).
* `PUT /sessions/{id}/complete` : A mentor resolves an accepted session to finalize it and open the floor for reviews.
* `GET /sessions/user/{userId}` : Retrieves a comprehensive chronological list of every historical or upcoming session linked to a specific user.

## 6. Group Service (`group-service`)
Facilitates peer-led study groups, collaborative hubs, and roster configurations.

* `POST /groups` : Create a distinct peer learning or study group node.
* `GET /groups` : Fetch a system-wide directory of every available peer learning group.
* `GET /groups/{id}` : Pull descriptive specifics and member capacities of a given group.
* `GET /groups/my` : Retrieve the immediate group enrollment matrix for the authenticated operator.
* `POST /groups/{id}/join` : Insert the currently authenticated user into an active group roster.
* `POST /groups/{id}/leave` : Disengage the currently authenticated user from a group they are part of.
* `DELETE /groups/{groupId}/remove/{email}` : Administratively evict a specific user from a managed group.

## 7. Review Service (`Review-Service`)
Handles empirical feedback, numerical ratings, and testimonials tied to completed mentorship sessions.

* `POST /reviews` : Securely dispatch a 1-5 rating accompanied by a written testimonial for a mentor.
* `GET /reviews/mentor/{mentorId}` : Query the entire historical aggregation of ratings and reviews attached to a specific mentor.

---

### Auxiliary Infrastructure
* **Notification Service** (`Notification-Service`): Consumes silent asynchronous queue bursts routed through RabbitMQ (e.g., `SESSION_BOOKED`) and maps them to email or application-layer popups. Exposes no explicit REST controllers.
* **API Gateway** (`api-gateway`): Sits as the ingress point evaluating tokens via `AuthenticationFilter`. Operates port `8080`.
* **Eureka** (`Eureka-Server`): The central load-balancing and naming registry routing inter-service Feign calls locally. Operates port `8761`.
