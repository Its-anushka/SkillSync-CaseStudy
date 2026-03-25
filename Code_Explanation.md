# SkillSync Code Explanation (The Beginners Guide)

Welcome to your code! A microservices system is basically a giant restaurant. Instead of one chef doing everything (a "Monolith"), you have 10 specific chefs (Microservices). One handles chopping (Auth), one grills (Users), one plates (Sessions), etc.

They all talk to each other to fulfill a meal order. Because we use **Spring Boot**, every single chef's station is organized using the exact same standard pattern. If you learn how one service works, you instantly understand them all!

Let's go file-by-file and line-by-line through the **User Service** to understand the anatomy of your entire project.

---

## 1. The Entity (The Database Blueprint)
**File Location:** `user-service/src/main/java/com/user/entity/UserProfile.java`

**Why this file exists:** Java does not speak SQL natively. We need a way to turn a MySQL/Oracle database row into a Java Object we can read. This file explicitly maps the database columns to Java variables.

```java
// LINE 1-10: Imports and Packages. We import JPA (Jakarta Persistence API) tools here.

@Entity // WHY: This tells Spring Boot "Hey, this class isn't normal Java. This represents a Database Table!"
@Table(name = "user_profiles") // WHY: Specifies the exact name of the table in Oracle/MySQL.
@Data // WHY: This is a Lombok shortcut! It automatically writes getter and setter methods invisibly so we don't have to script 50 lines of getBio() and setBio().
public class UserProfile {

    @Id // WHY: Tells the database that the variable below is the Primary Key (the unique ID).
    @GeneratedValue(strategy = GenerationType.IDENTITY) // WHY: Tells the DB to auto-increment the ID (1, 2, 3...) whenever a new user is saved.
    private Long id;

    @Column(unique = true, nullable = false) // WHY: Forces the database to reject the save if the email is empty (nullable=false) or already exists (unique=true).
    private String email;

    private String fullName; // Normal columns get automatically created as VARCHAR(255) tables in the database.
    private String role;
    private String bio;
}
```

---

## 2. The Repository (The Database Librarian)
**File Location:** `user-service/src/main/java/com/user/repository/UserProfileRepository.java`

**Why this file exists:** If you want to grab User #5 from the database, you normally have to write `SELECT * FROM user_profiles WHERE id = 5`. That's annoying. Spring Data JPA writes the SQL for us through this interface.

```java
@Repository // WHY: Tells Spring this is the file responsible for database communication.
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    // WHY JpaRepository: By "extending" this built-in Spring class, we instantly inherit 50 invisible methods like .findAll(), .save(), and .deleteById(). We just get them for free!
    // The <UserProfile, Long> tells it: "You are fetching UserProfile entities, and their ID format is a Long number."

    // WHY this line?: JpaRepository doesn't have a way to search by email out of the box. 
    // By simply naming the function 'findByEmail', Spring Boot magically writes the SQL query `SELECT * ... WHERE email = ?` in the background for us!
    Optional<UserProfile> findByEmail(String email);
}
```

---

## 3. The DTO - Data Transfer Object (The Shipping Boxes)
**File Location:** `user-service/src/main/java/com/user/dto/request/CreateProfileRequest.java`

**Why this file exists:** If a hacker sends us an HTTP request containing `id=1`, we don't want them directly modifying our entity schemas. DTOs are temporary "shipping boxes" that strictly control exactly what data a user is allowed to send or view.

```java
@Data // WHY: Again, Lombok writes our getters and setters to keep the file clean.
public class CreateProfileRequest {
    // Notice there is no "ID" or "Role" here! 
    // WHY: Because when creating a profile from the frontend, a user is ONLY allowed to submit these 4 fields. We protect the system by omitting system-critical variables from this box.
    private String fullName;
    private String phone;
    private String bio;
    private String location;
}
```

---

## 4. The Controller (The Traffic Cop / Front Door)
**File Location:** `user-service/src/main/java/com/user/controller/UserController.java`

**Why this file exists:** The internet (Frontend, Postman, Mobile App) can only speak HTTP. The controller handles incoming web traffic (`HTTP POST, GET, PUT`) and matches the URL address to a specific piece of Java code.

```java
@RestController // WHY: Tells Spring "This class listens to the internet and responds with JSON data."
@RequestMapping("/users") // WHY: Sets the base URL for every command in this file to start with http://localhost:8080/users
public class UserController {

    @Autowired // WHY "Dependency Injection": Instead of creating a NEW Service every time with `new UserServiceImpl()`, Spring Boot lends us a shared clone.
    private UserService userService;

    // WHY PostMapping: If a request comes in using the POST method to "/users/profile", trigger this function.
    @PostMapping("/profile") 
    // @RequestHeader grabs the JWT Token.
    // @RequestBody grabs the JSON data the user typed in Postman and packs it into our DTO shipping box!
    public ResponseEntity<UserProfileResponse> createProfile(
            @RequestHeader("Authorization") String token, 
            @RequestBody CreateProfileRequest request) {

        // WHY: The Controller shouldn't do logic. It acts as a traffic cop and passes the data directly to the chef (The Service layer).
        String email = extractEmailFromToken(token);
        String role = extractRoleFromToken(token);
        
        // Tells the service layer to do the heavy lifting, then returns HTTP 200 (OK) to the internet!
        return ResponseEntity.ok(userService.createProfile(email, role, request)); 
    }
}
```

---

## 5. The Service Layer (The Brains / Chef)
**File Location:** `user-service/src/main/java/com/user/service/UserServiceImpl.java`

**Why this file exists:** This is where the actual business logic lives. Calculations, mapping, error throwing, and database saving occur here.

```java
@Service // WHY: Tells Spring this file handles heavy logic.
public class UserServiceImpl implements UserService {

    private final UserProfileRepository repository;

    // WHY: We inject the repository (our Database Librarian) so we can ask it to save things.
    public UserServiceImpl(UserProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserProfileResponse createProfile(String email, String role, CreateProfileRequest request) {

        // 1. BUSINESS LOGIC: Ask the database if this email already exists.
        if (repository.findByEmail(email).isPresent()) {
            throw new DuplicateResourceException("Profile already exists"); // WHY: Stop the program if they are trying to duplicate themselves!
        }

        // 2. MAPPING: We create an empty Database Entity (Spreadsheet row).
        UserProfile profile = new UserProfile();
        
        // We unpack the DTO (Shipping box) and pour the data into the Database Entity.
        profile.setEmail(email);
        profile.setRole(role);
        profile.setFullName(request.getFullName());
        
        // 3. SAVING: We hand the filled Database Entity to the Repository librarian to save it permanently to MySQL/Oracle.
        UserProfile savedProfile = repository.save(profile);

        // 4. RETURN: We map the saved entity back into a new DTO shipping box (UserProfileResponse) to send safely back to the user's screen.
        return mapToResponse(savedProfile);
    }
}
```

---

## Global Concepts You Need to Know

### 1. What is Feign Client? (`user-service/client/MentorClient.java`)
**The Problem:** Microservices run on different physical machines. How does a Session Service talk to the Mentor Service?
**The Solution (Feign):** It allows Microservice A to make a direct HTTP call to Microservice B invisibly. It pretends it's just normal Java code, but behind the scenes, it shoots an internet request to the other microservice to get data.

### 2. What is RabbitMQ / Event Publishers?
**The Problem:** Normal Feign calls make the user wait. If you book a Session, the system has to send 4 emails. If the server waits for the emails to finish, the user clicks "Book" and stares at a loading screen for 10 seconds.
**The Solution (RabbitMQ):** Asynchronous Events. The Session Service finishes booking in 0.1 seconds, yells "SOMEONE BOOKED A TICKET!" into a RabbitMQ loudspeaker tube, and immediately tells the user "Success!". 
Later, the Notification Service (listening on the other end of the tube) hears the loudspeaker, picks up the event, and takes its sweet time sending the emails in the background.

### 3. What is Eureka? (`eureka-server`)
**The Problem:** Every microservice has a different IP address (e.g., Session is on `7076`, User is on `7072`). Hardcoding these numbers is a nightmare if servers restart.
**The Solution:** Eureka is a "Phonebook". When a service turns on, it calls Eureka and says "Hey! My name is SESSION-SERVICE and I am on port 7076!". Now, when the API Gateway wants to route traffic, it asks Eureka where things are dynamically.

### 4. What is the API Gateway? (`api-gateway`)
**The Problem:** We don't want the frontend React app to memorize 10 different Microservice ports (7071, 7072, 7073...). Also, having to verify security identity locally on all 10 services is annoying.
**The Solution:** A unified bouncer. The API Gateway sits at Port `8080`. 
1. The user asks the gateway at `8080` for `/session-service`.
2. The Gateway checks the JWT Token using `AuthenticationFilter.java` (The bouncer checks the ID).
3. If valid, the Gateway asks Eureka where the Session code lives.
4. The Gateway seamlessly forwards the traffic to `7076`, completely hiding the ugly backend network logic from the user.
