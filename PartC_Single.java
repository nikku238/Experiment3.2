package com.example.c;

import java.util.Properties;
import javax.sql.DataSource;
import java.util.List;

import com.example.c.PartC_Single.Student;
import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.Transaction;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration as SpringConfiguration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

// Single-file consolidated Part C. Note: uses Spring annotations; the file groups classes below.
public class PartC_Single {
    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext(AppConfig.class, StudentRepository.class, StudentService.class);
        StudentService svc = ctx.getBean(StudentService.class);

        Student s1 = new Student("Harsh", "harsh@example.com", 21);
        Long id = svc.createStudent(s1);
        System.out.println("Saved ID: " + id);

        Student loaded = svc.getStudent(id);
        System.out.println("Loaded: " + loaded);

        ctx.close();
    }

    @Entity
    @Table(name = "students")
    public static class Student {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;
        private String email;
        private int age;

        public Student() {}
        public Student(String name, String email, int age) { this.name = name; this.email = email; this.age = age; }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }

        @Override
        public String toString() {
            return "Student{" + "id=" + id + ", name='" + name + '\'' + ", email='" + email + '\'' + ", age=" + age + '}';
        }
    }
}

// Due to Java syntax and annotation imports, create supportive classes outside public class
@SpringConfiguration
@EnableTransactionManagement
class AppConfig {
    @Bean
    public DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3306/testdb?useSSL=false&serverTimezone=UTC");
        ds.setUsername("root");
        ds.setPassword("root");
        return ds;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory(DataSource ds) {
        LocalSessionFactoryBean lsfb = new LocalSessionFactoryBean();
        lsfb.setDataSource(ds);
        lsfb.setAnnotatedClasses(PartC_Single.Student.class);
        Properties props = new Properties();
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.show_sql", "true");
        lsfb.setHibernateProperties(props);
        return lsfb;
    }

    @Bean
    public PlatformTransactionManager transactionManager(SessionFactory sf) {
        return new HibernateTransactionManager(sf);
    }
}

@Repository
class StudentRepository {
    private final SessionFactory sessionFactory;
    public StudentRepository(SessionFactory sf) { this.sessionFactory = sf; }

    private Session currentSession() { return sessionFactory.getCurrentSession(); }

    public Long save(PartC_Single.Student s) { return (Long) currentSession().save(s); }
    public PartC_Single.Student findById(Long id) { return currentSession().get(PartC_Single.Student.class, id); }
    public List<PartC_Single.Student> findAll() { return currentSession().createQuery("from PartC_Single$Student", PartC_Single.Student.class).list(); }
    public void update(PartC_Single.Student s) { currentSession().update(s); }
    public void delete(PartC_Single.Student s) { currentSession().delete(s); }
}

@Service
class StudentService {
    private final StudentRepository repo;
    public StudentService(StudentRepository repo) { this.repo = repo; }

    @Transactional
    public Long createStudent(PartC_Single.Student s) { return repo.save(s); }

    @Transactional(readOnly = true)
    public PartC_Single.Student getStudent(Long id) { return repo.findById(id); }

    @Transactional(readOnly = true)
    public List<PartC_Single.Student> getAll() { return repo.findAll(); }

    @Transactional
    public void updateStudent(PartC_Single.Student s) { repo.update(s); }

    @Transactional
    public void deleteStudent(PartC_Single.Student s) { repo.delete(s); }

    @Transactional
    public void createTwoStudentsAtomically(PartC_Single.Student a, PartC_Single.Student b) {
        repo.save(a);
        repo.save(b);
    }
}
