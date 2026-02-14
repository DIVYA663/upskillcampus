import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.*;
import java.util.*;

// Main class
@SpringBootApplication
@RestController
public class StudentApp {

    public static void main(String[] args) {

        // setting database properties inside code
        SpringApplication app = new SpringApplication(StudentApp.class);

        Map<String, Object> map = new HashMap<>();
        map.put("spring.datasource.url", "jdbc:h2:mem:testdb");
        map.put("spring.datasource.driverClassName", "org.h2.Driver");
        map.put("spring.jpa.database-platform", "org.hibernate.dialect.H2Dialect");
        map.put("spring.jpa.hibernate.ddl-auto", "update");

        app.setDefaultProperties(map);
        app.run(args);
    }

    // connect repository
    @Autowired
    StudentRepo repo;

    // ================= FRONTEND =================

    @GetMapping("/")
    public String homePage() {

        // returning simple html page
        return "<html>" +
                "<head><title>Student Project</title></head>" +
                "<body style='font-family:Arial'>" +

                "<h2>Student Management</h2>" +

                "Name: <input type='text' id='name'><br><br>" +
                "Course: <input type='text' id='course'><br><br>" +
                "<button onclick='addStudent()'>Add</button>" +

                "<h3>Student List</h3>" +
                "<ul id='list'></ul>" +

                "<script>" +

                "function loadStudents(){" +
                " fetch('/students')" +
                " .then(res => res.json())" +
                " .then(data => {" +
                "   let list = document.getElementById('list');" +
                "   list.innerHTML='';" +
                "   data.forEach(s => {" +
                "     list.innerHTML += '<li>' + s.name + ' - ' + s.course +" +
                "     \" <button onclick=deleteStudent('\"+s.id+\"')>Delete</button></li>\";" +
                "   });" +
                " });" +
                "}" +

                "function addStudent(){" +
                " let student = {" +
                "   name: document.getElementById('name').value," +
                "   course: document.getElementById('course').value" +
                " };" +
                " fetch('/students', {" +
                "   method: 'POST'," +
                "   headers: {'Content-Type':'application/json'}," +
                "   body: JSON.stringify(student)" +
                " }).then(() => loadStudents());" +
                "}" +

                "function deleteStudent(id){" +
                " fetch('/students/' + id, {method:'DELETE'})" +
                " .then(() => loadStudents());" +
                "}" +

                "loadStudents();" +

                "</script>" +
                "</body></html>";
    }

    // ================= BACKEND =================

    @GetMapping("/students")
    public List<Student> getStudents() {
        return repo.findAll();
    }

    @PostMapping("/students")
    public Student saveStudent(@RequestBody Student s) {
        return repo.save(s);
    }

    @DeleteMapping("/students/{id}")
    public void removeStudent(@PathVariable Long id) {
        repo.deleteById(id);
    }
}


// ================= ENTITY CLASS =================

@Entity
class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String course;

    public Student() {
    }

    public Student(String name, String course) {
        this.name = name;
        this.course = course;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCourse() {
        return course;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}


// ================= REPOSITORY =================

interface StudentRepo extends JpaRepository<Student, Long> {
}
