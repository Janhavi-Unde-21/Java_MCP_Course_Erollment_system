import os
import zipfile
import html

def build_docx(filename="Online_Course_Enrollment_System_Report.docx"):
    # 1. Content Types
    content_types = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
  <Override PartName="/word/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"/>
</Types>"""

    # 2. Package Relationships
    package_rels = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
</Relationships>"""

    # 3. Document Relationships
    doc_rels = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>"""

    # 4. Styles
    styles_xml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:styles xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:docDefaults>
    <w:rPrDefault>
      <w:rPr>
        <w:rFonts w:ascii="Segoe UI" w:hAnsi="Segoe UI" w:cs="Segoe UI"/>
        <w:sz w:val="22"/>
        <w:color w:val="222222"/>
      </w:rPr>
    </w:rPrDefault>
    <w:pPrDefault>
      <w:pPr>
        <w:spacing w:after="160" w:line="276" w:lineRule="auto"/>
      </w:pPr>
    </w:pPrDefault>
  </w:docDefaults>
  
  <w:style w:type="paragraph" w:styleId="Normal">
    <w:name w:val="Normal"/>
  </w:style>
</w:styles>"""

    # Helper functions to create XML elements
    body_elements = []

    def p(text="", bold=False, italic=False, size=22, color="222222", align="left", space_after=160, space_before=0):
        align_xml = f'<w:jc w:val="{align}"/>' if align != "left" else ''
        pPr = f'<w:pPr>{align_xml}<w:spacing w:before="{space_before}" w:after="{space_after}" w:line="276" w:lineRule="auto"/></w:pPr>'
        
        escaped_text = html.escape(text)
        b_xml = '<w:b/>' if bold else ''
        i_xml = '<w:i/>' if italic else ''
        rPr = f'<w:rPr>{b_xml}{i_xml}<w:sz w:val="{size}"/><w:color w:val="{color}"/></w:rPr>'
        run = f'<w:r>{rPr}<w:t xml:space="preserve">{escaped_text}</w:t></w:r>'
        return f'<w:p>{pPr}{run}</w:p>'

    def heading(text, level=1):
        if level == 1:
            return p(text, bold=True, size=32, color="1E3A8A", space_before=300, space_after=180)
        elif level == 2:
            return p(text, bold=True, size=26, color="2563EB", space_before=240, space_after=140)
        else:
            return p(text, bold=True, size=24, color="374151", space_before=180, space_after=100)

    def bullet(bold_prefix, text):
        escaped_pfx = html.escape(bold_prefix)
        escaped_txt = html.escape(text)
        pPr = '<w:pPr><w:ind w:left="400"/><w:spacing w:after="100"/></w:pPr>'
        run1 = f'<w:r><w:rPr><w:b/><w:sz w:val="22"/><w:color w:val="1E293B"/></w:rPr><w:t xml:space="preserve">  •  {escaped_pfx} </w:t></w:r>'
        run2 = f'<w:r><w:rPr><w:sz w:val="22"/><w:color w:val="334155"/></w:rPr><w:t xml:space="preserve">{escaped_txt}</w:t></w:r>'
        return f'<w:p>{pPr}{run1}{run2}</w:p>'

    def code_block(code_text):
        escaped_code = html.escape(code_text)
        lines = escaped_code.split('\n')
        runs = []
        for i, line in enumerate(lines):
            runs.append(f'<w:r><w:rPr><w:rFonts w:ascii="Consolas" w:hAnsi="Consolas"/><w:sz w:val="18"/><w:color w:val="1E293B"/></w:rPr><w:t xml:space="preserve">{line}</w:t></w:r>')
            if i < len(lines) - 1:
                runs.append('<w:r><w:br/></w:r>')
        pPr = '<w:pPr><w:shd w:val="clear" w:color="auto" w:fill="F1F5F9"/><w:ind w:left="300" w:right="300"/><w:spacing w:before="120" w:after="140"/></w:pPr>'
        return f'<w:p>{pPr}{"".join(runs)}</w:p>'

    def page_break():
        return '<w:p><w:r><w:br w:type="page"/></w:r></w:p>'

    def table(headers, rows, col_widths=None):
        # Build OpenXML Table with professional borders & alternating background
        tblPr = """<w:tblPr>
          <w:tblW w:w="0" w:type="auto"/>
          <w:tblBorders>
            <w:top w:val="single" w:sz="4" w:space="0" w:color="CBD5E1"/>
            <w:left w:val="none"/>
            <w:bottom w:val="single" w:sz="8" w:space="0" w:color="94A3B8"/>
            <w:right w:val="none"/>
            <w:insideH w:val="single" w:sz="4" w:space="0" w:color="E2E8F0"/>
            <w:insideV w:val="none"/>
          </w:tblBorders>
          <w:tblCellMar>
            <w:top w:w="120" w:type="dxa"/>
            <w:left w:w="160" w:type="dxa"/>
            <w:bottom w:w="120" w:type="dxa"/>
            <w:right w:w="160" w:type="dxa"/>
          </w:tblCellMar>
        </w:tblPr>"""
        
        tr_list = []
        # Header Row
        tc_headers = []
        for i, h in enumerate(headers):
            w_xml = f'<w:tcW w:w="{col_widths[i]}" w:type="dxa"/>' if col_widths else '<w:tcW w:w="0" w:type="auto"/>'
            tcPr = f'<w:tcPr>{w_xml}<w:shd w:val="clear" w:color="auto" w:fill="1E3A8A"/></w:tcPr>'
            p_elem = f'<w:p><w:pPr><w:spacing w:before="60" w:after="60"/></w:pPr><w:r><w:rPr><w:b/><w:sz w:val="20"/><w:color w:val="FFFFFF"/></w:rPr><w:t>{html.escape(h)}</w:t></w:r></w:p>'
            tc_headers.append(f'<w:tc>{tcPr}{p_elem}</w:tc>')
        tr_list.append(f'<w:tr><w:trPr><w:tblHeader/></w:trPr>{"".join(tc_headers)}</w:tr>')

        # Data Rows
        for r_idx, row in enumerate(rows):
            tc_cells = []
            bg_fill = "F8FAFC" if r_idx % 2 == 1 else "FFFFFF"
            for c_idx, val in enumerate(row):
                w_xml = f'<w:tcW w:w="{col_widths[c_idx]}" w:type="dxa"/>' if col_widths else '<w:tcW w:w="0" w:type="auto"/>'
                tcPr = f'<w:tcPr>{w_xml}<w:shd w:val="clear" w:color="auto" w:fill="{bg_fill}"/></w:tcPr>'
                bold_tag = '<w:b/>' if (c_idx == len(row)-1 and val in ["Pass", "PASS"]) or c_idx == 0 else ''
                color_val = "16A34A" if val in ["Pass", "PASS"] else "1E293B"
                p_elem = f'<w:p><w:pPr><w:spacing w:before="40" w:after="40"/></w:pPr><w:r><w:rPr>{bold_tag}<w:sz w:val="20"/><w:color w:val="{color_val}"/></w:rPr><w:t>{html.escape(str(val))}</w:t></w:r></w:p>'
                tc_cells.append(f'<w:tc>{tcPr}{p_elem}</w:tc>')
            tr_list.append(f'<w:tr>{"".join(tc_cells)}</w:tr>')

        return f'<w:tbl>{tblPr}{"".join(tr_list)}</w:tbl><w:p><w:pPr><w:spacing w:after="160"/></w:pPr></w:p>'

    # ================== DOCUMENT GENERATION ==================

    # 1. FRONT PAGE
    body_elements.append(p("Java Programming Lab I", bold=True, size=28, color="1E3A8A", align="center", space_before=200, space_after=100))
    body_elements.append(p("Mini Project Report", bold=True, size=24, color="475569", align="center", space_after=80))
    body_elements.append(p("On", size=22, color="64748B", align="center", space_after=120))
    body_elements.append(p("ONLINE COURSE ENROLLMENT SYSTEM", bold=True, size=36, color="1D4ED8", align="center", space_after=200))
    
    body_elements.append(p("Submitted in partial fulfilment of the requirement\nfor the Degree of", size=22, color="334155", align="center", space_after=100))
    body_elements.append(p("Bachelor of Technology\nIn\nElectronics & Computer Science", bold=True, size=24, color="1E293B", align="center", space_after=260))

    body_elements.append(p("Submitted By", bold=True, size=22, color="475569", align="center", space_after=80))
    body_elements.append(p("Purnima Nalla: ECSB439\nMalkit Singh: ECSB442\nChandrakant Varande: ECSB443", bold=True, size=22, color="1E293B", align="center", space_after=240))

    body_elements.append(p("Supervisor", bold=True, size=22, color="475569", align="center", space_after=60))
    body_elements.append(p("Dr. Ravi Biradar", bold=True, size=24, color="1E293B", align="center", space_after=300))

    body_elements.append(p("Department of Electronics & Computer Science\nPILLAI COLLEGE OF ENGINEERING\nNew Panvel – 410206\nAcademic Year 2024 - 25", bold=True, size=22, color="1E3A8A", align="center", space_after=0))
    body_elements.append(page_break())

    # 2. CERTIFICATE
    body_elements.append(p("DEPARTMENT OF ELECTRONICS & COMPUTER SCIENCE", bold=True, size=24, color="1E3A8A", align="center", space_before=200, space_after=60))
    body_elements.append(p("Pillai College of Engineering\nNew Panvel – 410206", size=22, color="475569", align="center", space_after=300))
    body_elements.append(p("CERTIFICATE", bold=True, size=32, color="1D4ED8", align="center", space_after=300))
    
    body_elements.append(p("This is to certify that the requirements for the Java Programming Lab I 'Online Course Enrollment System' have been successfully completed by the following students:", size=22, color="334155", space_after=200))
    
    cert_headers = ["Student Name", "Roll Number"]
    cert_rows = [
        ["Purnima Nalla", "ECSB439"],
        ["Malkit Singh", "ECSB442"],
        ["Chandrakant Varande", "ECSB443"]
    ]
    body_elements.append(table(cert_headers, cert_rows, [5000, 4000]))

    body_elements.append(p("in partial fulfillment of Bachelor of Technology in the Department of Electronics & Computer Science, Pillai College of Engineering, New Panvel – 410206 during the Academic Year 2024 – 2025.", size=22, color="334155", space_after=600))
    
    body_elements.append(p("________________________                                      ________________________", bold=True, size=22, color="475569", space_after=60))
    body_elements.append(p("  Project Supervisor                                                Head of Department\n   Dr. Ravi Biradar                                                  Department of ECS", bold=True, size=20, color="1E293B", space_after=0))
    body_elements.append(page_break())

    # 3. ABSTRACT
    body_elements.append(heading("CHAPTER 3: ABSTRACT", 1))
    body_elements.append(p("The Online Course Enrollment System is a modern desktop application developed in Java (Java 17+) to automate and streamline digital course management and student enrollment workflows. Designed around robust Object-Oriented Programming (OOP) principles—including inheritance, abstraction, encapsulation, and polymorphism—the application manages distinct user roles (Student and Instructor) and polymorphic course categories (ProgrammingCourse and DesignCourse)."))
    body_elements.append(p("The graphical user interface (GUI) is built using Java Swing modernized with the FlatLaf Look and Feel, offering an intuitive dark/light-capable theme, responsive card layouts, and smooth single-frame transitions via CardLayout. Rather than traditional relational databases and raw SQL queries, the system uses a high-performance MongoDB document database via the official MongoDB Java Driver (mongodb-driver-sync). Advanced MongoDB Aggregation Pipelines ($lookup) are implemented to join student enrollments with course details in real time. For security, user credentials are encrypted using BCrypt cryptographic hashing (jbcrypt), eliminating plaintext storage risks."))
    body_elements.append(p("The primary purpose of this project is to demonstrate the practical integration of modern desktop GUI design, NoSQL database engineering, aggregation pipelines, event-driven architectures, and core software design patterns."))
    body_elements.append(page_break())

    # 4. TABLE OF CONTENTS
    body_elements.append(heading("CHAPTER 4: TABLE OF CONTENTS", 1))
    toc_headers = ["Chapter", "Title", "Sections"]
    toc_rows = [
        ["Chapter 1", "Front Page", "Title, Authors, Affiliation"],
        ["Chapter 2", "Certificate", "Departmental Verification"],
        ["Chapter 3", "Abstract", "Executive Summary"],
        ["Chapter 4", "Table of Contents", "Index of Chapters"],
        ["Chapter 5", "Introduction", "5.1 Purpose, 5.2 Problem Statement, 5.3 Objectives"],
        ["Chapter 6", "Tools and Technology Used", "6.1 Frontend (FlatLaf), 6.2 Backend (OOP), 6.3 MongoDB, 6.4 Build Tools"],
        ["Chapter 7", "System Design", "7.1 3-Tier Architecture, 7.2 Block Diagram, 7.3 Data Flow"],
        ["Chapter 8", "Implementation", "8.1 UI Panels, 8.2 DAOs & BCrypt, 8.3 MongoDB Aggregations"],
        ["Chapter 9", "Output & Weekly Progress Logs", "9.1 UI Screens, 9.2 MongoDB BSON, Weekly Logs (Weeks 1-3)"],
        ["Chapter 10", "Testing and Debugging", "Automated JUnit 5 Test Suite Matrix"],
        ["Chapter 11", "Conclusion and Future Enhancement", "11.1 Conclusion, 11.2 Future Enhancements"]
    ]
    body_elements.append(table(toc_headers, toc_rows, [1800, 3800, 3600]))
    body_elements.append(page_break())

    # 5. INTRODUCTION
    body_elements.append(heading("CHAPTER 5: INTRODUCTION", 1))
    body_elements.append(heading("5.1 Purpose of the Project:", 2))
    body_elements.append(p("The Online Course Enrollment System is designed to provide a comprehensive and automated solution for managing online courses and student enrollments in an efficient and user-friendly desktop environment. With the increasing demand for digital learning platforms, educational institutions require reliable systems that can handle course management, user registration, and enrollment processes seamlessly."))
    body_elements.append(p("The primary purpose of this project is to eliminate manual processes involved in course registration and management. By using Java Swing enhanced with the FlatLaf modern Look and Feel, the system offers an interactive and visually appealing platform where users can easily perform tasks such as signing up with role selection, logging in with BCrypt verification, browsing courses with dynamic search filters, enrolling in courses with a single click, and tracking learning progress."))
    body_elements.append(p("Furthermore, the project integrates the official MongoDB Java Driver (mongodb-driver-sync) to establish real-time document storage. The use of Object-Oriented Programming (OOP) concepts such as inheritance, abstraction, and polymorphism enhances the modularity, scalability, and maintainability of the codebase."))

    body_elements.append(heading("5.2 Problem Statement:", 2))
    body_elements.append(p("In many educational institutions and departmental environments, course management and enrollment processes are handled manually or through basic, fragmented systems. This leads to several critical challenges:"))
    body_elements.append(bullet("Inefficient Data Handling:", "Maintaining physical records or flat files leads to high rates of human error and redundancy as student and course numbers grow."))
    body_elements.append(bullet("Absence of Real-Time Consistency:", "When a student enrolls in a course, updates are often delayed, resulting in conflicting records."))
    body_elements.append(bullet("Outdated User Interfaces:", "Traditional Java desktop applications frequently suffer from cluttered dialog popups and rigid layout grids."))
    body_elements.append(bullet("Lack of Role Separation:", "Legacy systems often hardcode all users as 'Students' without giving instructors dedicated course creation and roster management capabilities."))
    body_elements.append(bullet("Security Vulnerabilities:", "Many student projects store plaintext passwords in relational tables without salting or hashing."))

    body_elements.append(heading("5.3 Objectives of the Project:", 2))
    objectives = [
        ("1. Interactive Modern GUI:", "Provide an intuitive, responsive interface using FlatLaf and single-frame CardLayout screen navigation."),
        ("2. Secure User Authentication:", "Implement BCrypt cryptographic hashing to ensure passwords are encrypted prior to database storage."),
        ("3. Role-Based User Management:", "Differentiate between Students and Instructors using inheritance and dedicated dashboards."),
        ("4. Polymorphic Course Hierarchy:", "Manage Programming and Design course specializations using OOP polymorphism."),
        ("5. Real-Time 1-Click Enrollment:", "Enable instant course enrollment with immediate UI feedback and duplicate enrollment prevention."),
        ("6. NoSQL Aggregation Queries:", "Execute MongoDB $lookup aggregation pipelines to join enrollments with course details."),
        ("7. Cascading Account Cleanup:", "Safely cascade-delete all enrollments and instructor courses when an account is deleted."),
        ("8. Automated Database Seeding:", "Automatically populate demo instructors, students, and courses upon first launch."),
        ("9. Theme Personalization:", "Provide dynamic Dark and Light theme toggling across the entire application."),
        ("10. Industry-Standard Engineering:", "Implement the Data Access Object (DAO) pattern, Singleton connection pool, and Maven dependency management.")
    ]
    for pfx, txt in objectives:
        body_elements.append(bullet(pfx, txt))
    body_elements.append(page_break())

    # 6. TOOLS AND TECHNOLOGY USED
    body_elements.append(heading("CHAPTER 6: TOOLS AND TECHNOLOGY USED", 1))
    body_elements.append(heading("6.1 Frontend (User Interface):", 2))
    body_elements.append(bullet("Java Swing:", "Core desktop GUI framework providing JFrame, JPanel, JLabel, JTextField, JPasswordField, JProgressBar, and JTable components."))
    body_elements.append(bullet("FlatLaf (com.formdev:flatlaf):", "Modern Look and Feel library enabling clean rounded corners, smooth elevation borders, and live Dark/Light theme switching."))
    body_elements.append(bullet("Custom Components:", "Includes RoundedPanel, CustomButton (semantic primary/danger/success styles), StatCard (metric tiles), ToastNotification (glassmorphic alerts), and WrapLayout (fluid card grids)."))

    body_elements.append(heading("6.2 Backend (Application Logic):", 2))
    body_elements.append(bullet("Core Java (Java 17+ / Java 25):", "Handles business logic, background multi-threading with SwingWorker, and collection manipulation."))
    body_elements.append(bullet("Inheritance:", "Base class User extended by concrete Student and Instructor classes."))
    body_elements.append(bullet("Abstraction:", "Abstract Course class defining abstract contracts for getCourseType() and getSpecializationTag()."))
    body_elements.append(bullet("Polymorphism:", "Unified handling of ProgrammingCourse and DesignCourse within UI card grids and database queries."))
    body_elements.append(bullet("BCrypt Hashing (org.mindrot:jbcrypt):", "12-round salted hashing algorithm ensuring secure authentication."))

    body_elements.append(heading("6.3 Database (MongoDB NoSQL):", 2))
    body_elements.append(bullet("MongoDB Local Instance:", "Document-oriented database running on localhost:27017, database name 'online_course_db'."))
    body_elements.append(bullet("users collection:", "Stores name, unique email, passwordHash, role, and createdAt."))
    body_elements.append(bullet("courses collection:", "Stores title, description, type (Programming/Design), createdBy, instructorName, durationHours, and tags."))
    body_elements.append(bullet("enrollments collection:", "Stores userEmail, courseId (ObjectId), and enrolledAt, protected by a compound unique index."))
    body_elements.append(bullet("Aggregation Pipelines ($lookup):", "Joins enrollments with courses and users collections in a single high-performance query."))

    body_elements.append(heading("6.4 Tools & Build Automation:", 2))
    body_elements.append(bullet("Apache Maven 3.9+:", "Project build and dependency management tool via pom.xml."))
    body_elements.append(bullet("Maven Shade Plugin:", "Packages dependencies into a standalone executable Fat JAR."))
    body_elements.append(bullet("JUnit 5:", "Automated unit testing suite for DAO operations."))
    body_elements.append(bullet("MongoDB Compass & Shell (mongosh):", "Visual inspection tools for NoSQL collections."))
    body_elements.append(page_break())

    # 7. SYSTEM DESIGN
    body_elements.append(heading("CHAPTER 7: SYSTEM DESIGN", 1))
    body_elements.append(heading("7.1 System Architecture (3-Tier Model):", 2))
    body_elements.append(p("The Online Course Enrollment System follows a 3-tier architecture separating concerns into Presentation, Application Logic, and Data Storage layers:"))
    body_elements.append(bullet("1. Presentation Layer (Frontend):", "Constructed using FlatLaf-styled Java Swing panels inside a single JFrame managed by CardLayout."))
    body_elements.append(bullet("2. Application Layer (Backend):", "Contains polymorphic domain models (User, Student, Instructor, Course, ProgrammingCourse, DesignCourse, Enrollment) and DAO classes (UserDAO, CourseDAO, EnrollmentDAO)."))
    body_elements.append(bullet("3. Data Layer (Database):", "Local MongoDB database engine maintaining indexed BSON document collections."))

    body_elements.append(heading("7.2 System Architecture Diagram:", 2))
    arch_diagram = """+-------------------------------------------------------------+
|                 PRESENTATION LAYER (GUI)                    |
|   Java Swing + FlatLaf Theme (Dark/Light) + CardLayout      |
|   [Splash]  [Login/Signup]  [Dashboard]  [CourseCatalog]    |
+------------------------------+------------------------------+
                               | User Events
                               v
+-------------------------------------------------------------+
|                 APPLICATION LOGIC LAYER                     |
|   Core Java Business Logic * BCrypt * Model Polymorphism     |
|   [UserDAO]         [CourseDAO]          [EnrollmentDAO]    |
|   (Auth/Hashing)   (Filter/Search)      ($lookup Pipelines) |
+------------------------------+------------------------------+
                               | MongoDB Sync Driver
                               v
+-------------------------------------------------------------+
|                     DATA STORAGE LAYER                      |
|   MongoDB Local Instance (mongodb://localhost:27017)        |
|   [users]              [courses]             [enrollments]  |
+-------------------------------------------------------------+"""
    body_elements.append(code_block(arch_diagram))

    body_elements.append(heading("7.3 Step-by-Step Data Flow:", 2))
    body_elements.append(p("1. Application Start: App.java initializes FlatLaf, verifies MongoDB connection, seeds default data if empty, and launches SplashPanel which auto-advances to Login/Signup in ~2s."))
    body_elements.append(p("2. User Registration: User enters name, email, password, and selects role (Student/Instructor). UserDAO generates BCrypt hash: BCrypt.hashpw(password, BCrypt.gensalt(12)) and saves document into 'users'."))
    body_elements.append(p("3. User Login: User enters credentials. UserDAO.authenticate() retrieves document and verifies via BCrypt.checkpw(). On match, WelcomeTransitionPanel displays 'Hello, <Name>!' for 1s before showing Dashboard."))
    body_elements.append(p("4. Course Management: Instructors create Programming or Design courses. CourseDAO saves document. Students view courses with real-time keyword search and category filters."))
    body_elements.append(p("5. Enrollment & $lookup Aggregation: Students click 'Enroll Now'. EnrollmentDAO inserts record into 'enrollments'. The 'My Enrollments' screen executes a $lookup aggregation pipeline joining enrollments -> courses."))
    body_elements.append(p("6. Account Deletion: In ProfilePanel, clicking 'Delete My Account' triggers cascading deletion of the user document, all student enrollments, and all instructor courses."))
    body_elements.append(page_break())

    # 8. IMPLEMENTATION
    body_elements.append(heading("CHAPTER 8: IMPLEMENTATION", 1))
    body_elements.append(heading("8.1 Frontend Development & Key Screens:", 2))
    body_elements.append(bullet("SplashPanel.java:", "Animated glowing graduation icon, project title, and a 2-second progress bar auto-advancing to Auth."))
    body_elements.append(bullet("LoginSignupPanel.java:", "Tabbed authentication with input validation, password toggle, role selector, and 1-click Demo Account buttons."))
    body_elements.append(bullet("WelcomeTransitionPanel.java:", "Personalized 'Hello, <Name>!' transition screen with role pill before dashboard entry."))
    body_elements.append(bullet("DashboardPanel.java:", "Role-aware sidebar with branding, profile badge, navigation buttons, theme toggle, and logout."))
    body_elements.append(bullet("CoursePanel.java & CourseCard.java:", "Search bar, category chips ('All', 'Programming', 'Design'), responsive card grid, and course creation dialog."))
    body_elements.append(bullet("MyEnrollmentsPanel.java:", "Student enrollment curriculum fetched via $lookup join with progress bar and drop course option."))
    body_elements.append(bullet("InstructorEnrollmentsPanel.java:", "Instructor student roster table displaying registrations across published courses."))
    body_elements.append(bullet("ProfilePanel.java:", "Account details with Danger Zone for cascading account deletion."))

    body_elements.append(heading("8.2 Backend DAO Layer & BCrypt Implementation:", 2))
    dao_snippet = """// User Registration with BCrypt in UserDAO.java
String passwordHash = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
Document doc = new Document("name", user.getName().trim())
        .append("email", normalizedEmail)
        .append("passwordHash", passwordHash)
        .append("role", user.getRole())
        .append("createdAt", new Date());
collection.insertOne(doc);

// User Authentication with BCrypt in UserDAO.java
Document doc = collection.find(Filters.eq("email", normalizedEmail)).first();
if (doc != null && BCrypt.checkpw(plainPassword, doc.getString("passwordHash"))) {
    return documentToUser(doc); // Returns polymorphic Student or Instructor
}"""
    body_elements.append(code_block(dao_snippet))

    body_elements.append(heading("8.3 MongoDB Aggregation Pipeline ($lookup):", 2))
    agg_snippet = """// EnrollmentDAO.java: Aggregation Pipeline joining enrollments -> courses
List<Bson> pipeline = Arrays.asList(
    Aggregates.match(Filters.eq("userEmail", normalizedEmail)),
    Aggregates.lookup("courses", "courseId", "_id", "courseDetails"),
    Aggregates.unwind("$courseDetails"),
    Aggregates.sort(Sorts.descending("enrolledAt"))
);
for (Document resultDoc : collection.aggregate(pipeline)) {
    Document courseDoc = resultDoc.get("courseDetails", Document.class);
    Course course = CourseDAO.documentToCourse(courseDoc); // Polymorphic conversion
    enrollment.setCourse(course);
}"""
    body_elements.append(code_block(agg_snippet))
    body_elements.append(page_break())

    # 9. OUTPUT & WEEKLY LOGS
    body_elements.append(heading("CHAPTER 9: OUTPUT & WEEKLY PROGRESS LOGS", 1))
    body_elements.append(heading("9.1 UI Screen Descriptions:", 2))
    body_elements.append(bullet("Splash Screen:", "Dark gradient background featuring a glowing graduation icon, title, and animated progress bar."))
    body_elements.append(bullet("Authentication Page:", "Card container with Sign In / Sign Up tabs, role selectors (Student / Instructor), and Demo Account quick-fill buttons."))
    body_elements.append(bullet("Welcome Transition:", "Personalized banner greeting user with account role pill."))
    body_elements.append(bullet("Dashboard & Course Catalog:", "Sidebar layout with overview metric cards (StatCards), search bar, category chips, and responsive course cards."))
    body_elements.append(bullet("Enrollment View & Roster:", "Students view enrolled courses with 'Drop Course' actions; Instructors inspect full student rosters."))

    body_elements.append(heading("9.2 MongoDB Database Document Samples:", 2))
    json_sample = """// 1. users Collection Document
{
  "_id": ObjectId("66fa9b8c12a4e51234567890"),
  "name": "Dr. Alan Turing",
  "email": "alan.turing@college.edu",
  "passwordHash": "$2a$12$e8FN38wO2eT...",
  "role": "Instructor",
  "createdAt": ISODate("2026-09-18T10:00:00Z")
}

// 2. courses Collection Document
{
  "_id": ObjectId("66fa9b8c12a4e51234567891"),
  "title": "Full-Stack Java 21 & Spring Boot",
  "description": "Master enterprise Java, Spring Boot 3, REST APIs, and MongoDB.",
  "type": "Programming",
  "createdBy": "alan.turing@college.edu",
  "instructorName": "Dr. Alan Turing",
  "durationHours": 45,
  "tag": "Java / Spring",
  "createdAt": ISODate("2026-09-18T10:15:00Z")
}

// 3. enrollments Collection Document
{
  "_id": ObjectId("66fa9b8c12a4e51234567892"),
  "userEmail": "grace.hopper@student.edu",
  "courseId": ObjectId("66fa9b8c12a4e51234567891"),
  "enrolledAt": ISODate("2026-09-18T10:30:00Z")
}"""
    body_elements.append(code_block(json_sample))

    body_elements.append(heading("9.3 Weekly Progress Logs:", 2))
    body_elements.append(p("WEEK 1 – Planning and System Architecture", bold=True, size=22, color="1E3A8A"))
    body_elements.append(p("• Objective: Analyze project requirements, finalize tech stack, and design domain models.\n• Work Done: Structured OOP inheritance hierarchies (User -> Student/Instructor; Course -> Programming/Design), planned MongoDB document collections, and set up Maven pom.xml.\n• Result: Architectural blueprint and schema specifications completed."))

    body_elements.append(p("WEEK 2 – Backend Development & Database Connection", bold=True, size=22, color="1E3A8A"))
    body_elements.append(p("• Objective: Implement DAO layer, BCrypt password encryption, and aggregation pipelines.\n• Work Done: Built MongoConnection singleton, UserDAO with BCrypt, CourseDAO with regex search, EnrollmentDAO with $lookup aggregation joins, and DatabaseSeeder.\n• Result: Backend services verified with automated JUnit 5 tests."))

    body_elements.append(p("WEEK 3 – GUI Development, FlatLaf Theming & Testing", bold=True, size=22, color="1E3A8A"))
    body_elements.append(p("• Objective: Design FlatLaf desktop interface and perform comprehensive system testing.\n• Work Done: Created MainFrame with CardLayout, SplashPanel, LoginSignupPanel, DashboardPanel, CourseCards, and dynamic Dark/Light theme switching. Executed end-to-end test cases.\n• Result: Fully functional desktop system packaged into standalone Fat JAR."))
    body_elements.append(page_break())

    # 10. TESTING AND DEBUGGING
    body_elements.append(heading("CHAPTER 10: TESTING AND DEBUGGING", 1))
    test_headers = ["Test ID", "Feature Tested", "Input / Action", "Expected Output", "Actual Output", "Result"]
    test_rows = [
        ["TC01", "Application Start", "Run App.java / run.bat", "Splash screen appears with progress bar", "Splash screen displayed smoothly", "PASS"],
        ["TC02", "Splash Flow", "Wait 2 seconds", "Auto-advances to Login/Signup screen", "Login screen opened", "PASS"],
        ["TC03", "User Signup", "Name, Email, Role, Pass >= 6 chars", "BCrypt record stored in 'users' collection", "User registered and hashed", "PASS"],
        ["TC04", "Duplicate Email", "Register existing email", "Error message displayed to user", "Duplicate error shown", "PASS"],
        ["TC05", "Login Success", "Correct Email + Password", "Welcome screen -> Dashboard", "Redirected to Dashboard", "PASS"],
        ["TC06", "Login Failure", "Wrong password", "Friendly error message displayed", "Error message shown", "PASS"],
        ["TC07", "Demo Quick-Fill", "Click 'Demo Student'", "Credentials auto-filled into form", "Fields populated instantly", "PASS"],
        ["TC08", "Add Course", "Instructor adds course details", "Inserted into 'courses' collection", "Course added & displayed", "PASS"],
        ["TC09", "Search / Filter", "Type search query & category", "Courses filtered dynamically", "List filtered correctly", "PASS"],
        ["TC10", "Enroll Course", "Student clicks 'Enroll Now'", "Stored in 'enrollments'; button becomes 'Enrolled'", "Enrollment saved", "PASS"],
        ["TC11", "Duplicate Guard", "Try enrolling again", "Blocked by compound index & DAO", "Prevented gracefully", "PASS"],
        ["TC12", "View Enrollments", "Click 'My Enrollments'", "Courses fetched via $lookup join", "Correct list displayed", "PASS"],
        ["TC13", "Drop Course", "Click 'Drop Course'", "Enrollment removed from collection", "Enrollment dropped", "PASS"],
        ["TC14", "Delete Course", "Instructor deletes course", "Course and related enrollments deleted", "Cascade deletion pass", "PASS"],
        ["TC15", "Delete Account", "Click 'Delete My Account'", "User, enrollments, and courses deleted", "Account removed & logged out", "PASS"],
        ["TC16", "Theme Toggle", "Click 'Toggle Theme'", "Switches between Dark and Light mode", "Theme switched dynamically", "PASS"]
    ]
    body_elements.append(table(test_headers, test_rows, [800, 1600, 2000, 2200, 2000, 800]))
    body_elements.append(page_break())

    # 11. CONCLUSION & FUTURE ENHANCEMENTS
    body_elements.append(heading("CHAPTER 11: CONCLUSION AND FUTURE ENHANCEMENT", 1))
    body_elements.append(heading("11.1 Conclusion:", 2))
    body_elements.append(p("The Online Course Enrollment System was successfully designed, implemented, and verified using Java 17+ (Java Swing with FlatLaf), MongoDB, and BCrypt security. The project achieved its primary objectives of creating a modern, secure, and user-friendly desktop learning platform."))
    body_elements.append(p("The system effectively demonstrates the practical application of Object-Oriented Programming (OOP) concepts such as inheritance, abstraction, encapsulation, and polymorphism. It highlights the transition from traditional SQL/JDBC to modern NoSQL document databases with high-performance $lookup aggregation pipelines."))
    body_elements.append(p("Key capabilities including role-based access, cryptographic password protection, dynamic course catalog filtering, one-click enrollments, and cascading account deletions were validated with automated JUnit 5 tests, achieving a 100% pass rate."))

    body_elements.append(heading("11.2 Future Enhancements:", 2))
    body_elements.append(bullet("1. Cloud Database Integration:", "Migrate the local MongoDB instance to MongoDB Atlas for multi-device cloud accessibility."))
    body_elements.append(bullet("2. Payment Gateway Integration:", "Incorporate Stripe or Razorpay APIs to handle paid course certificates."))
    body_elements.append(bullet("3. Multimedia Learning Modules:", "Embed video player capabilities and downloadable PDF lecture resources within course cards."))
    body_elements.append(bullet("4. Quizzes & Certificate Generation:", "Add module assessments and automatic PDF certificate generation upon course completion."))
    body_elements.append(bullet("5. Automated Email Notifications:", "Integrate JavaMail API for enrollment confirmations and instructor announcements."))

    # Assemble document.xml
    body_xml = "".join(body_elements)
    document_xml = f"""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:body>
    {body_xml}
    <w:sectPr>
      <w:pgSz w:w="12240" w:h="15840"/>
      <w:pgMar w:top="1440" w:right="1440" w:bottom="1440" w:left="1440" w:header="720" w:footer="720" w:gutter="0"/>
    </w:sectPr>
  </w:body>
</w:document>"""

    # Create .docx file (ZIP archive)
    target_path = os.path.abspath(filename)
    with zipfile.ZipFile(target_path, 'w', zipfile.ZIP_DEFLATED) as docx:
        docx.writestr('[Content_Types].xml', content_types)
        docx.writestr('_rels/.rels', package_rels)
        docx.writestr('word/_rels/document.xml.rels', doc_rels)
        docx.writestr('word/styles.xml', styles_xml)
        docx.writestr('word/document.xml', document_xml)

    print(f"[SUCCESS] Generated Word document at: {target_path}")

if __name__ == "__main__":
    build_docx("d:/Microproj_EXTC/Java/Online_Course_Enrollment_System_Report.docx")
