import os
import sys
from reportlab.lib.pagesizes import A4
from reportlab.lib import colors
from reportlab.lib.units import inch
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, KeepTogether, HRFlowable
)
from reportlab.pdfgen import canvas

class NumberedCanvas(canvas.Canvas):
    """Canvas that performs a two-pass calculation to draw 'Page X of Y' on pages after the cover."""
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_page_decorations(num_pages)
            super().showPage()
        super().save()

    def draw_page_decorations(self, page_count):
        if self._pageNumber == 1:
            # Suppress headers and footers on Cover Page
            return

        self.saveState()
        self.setFont("Helvetica", 8)
        self.setFillColor(colors.HexColor("#64748B"))

        # Header (Only on page 2 and later)
        self.drawString(54, 842 - 36, "Online Course Enrollment System — Mini Project Report")
        self.drawRightString(595 - 54, 842 - 36, "Dept. of ECS | Pillai College of Engineering")
        self.setStrokeColor(colors.HexColor("#CBD5E1"))
        self.setLineWidth(0.5)
        self.line(54, 842 - 42, 595 - 54, 842 - 42)

        # Footer
        self.line(54, 45, 595 - 54, 45)
        self.drawString(54, 32, "Academic Year 2024 - 2025")
        page_text = f"Page {self._pageNumber} of {page_count}"
        self.drawRightString(595 - 54, 32, page_text)

        self.restoreState()


def build_pdf(filename="d:/Microproj_EXTC/Java/Online_Course_Enrollment_System_Report.pdf"):
    doc = SimpleDocTemplate(
        filename,
        pagesize=A4,
        leftMargin=54,
        rightMargin=54,
        topMargin=54,
        bottomMargin=54
    )

    styles = getSampleStyleSheet()

    # Custom Palette
    NAVY = colors.HexColor("#1E3A8A")
    BLUE = colors.HexColor("#2563EB")
    DARK_TEXT = colors.HexColor("#1E293B")
    MUTED_TEXT = colors.HexColor("#475569")
    LIGHT_BG = colors.HexColor("#F8FAFC")
    BORDER_COLOR = colors.HexColor("#CBD5E1")

    # Typography Styles
    title_style = ParagraphStyle(
        'CoverTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=24,
        leading=28,
        alignment=1, # Center
        textColor=NAVY,
        spaceAfter=12
    )

    subtitle_style = ParagraphStyle(
        'CoverSubtitle',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=12,
        leading=16,
        alignment=1,
        textColor=MUTED_TEXT,
        spaceAfter=8
    )

    h1_style = ParagraphStyle(
        'Heading1_Custom',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=16,
        leading=20,
        textColor=NAVY,
        spaceBefore=16,
        spaceAfter=10,
        keepWithNext=True
    )

    h2_style = ParagraphStyle(
        'Heading2_Custom',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=12,
        leading=16,
        textColor=BLUE,
        spaceBefore=12,
        spaceAfter=6,
        keepWithNext=True
    )

    body_style = ParagraphStyle(
        'Body_Custom',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9.5,
        leading=13.5,
        textColor=DARK_TEXT,
        spaceAfter=8
    )

    bullet_style = ParagraphStyle(
        'Bullet_Custom',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9.5,
        leading=13.5,
        textColor=DARK_TEXT,
        leftIndent=15,
        firstLineIndent=-10,
        spaceAfter=5
    )

    code_style = ParagraphStyle(
        'Code_Custom',
        parent=styles['Normal'],
        fontName='Courier',
        fontSize=8,
        leading=11,
        textColor=colors.HexColor("#0F172A"),
        spaceBefore=4,
        spaceAfter=8
    )

    story = []

    # ==========================================
    # PAGE 1: COVER PAGE
    # ==========================================
    story.append(Spacer(1, 20))
    story.append(Paragraph("<b>Java Programming Lab I</b>", ParagraphStyle('H', parent=subtitle_style, fontSize=14, textColor=NAVY)))
    story.append(Paragraph("Mini Project Report On", subtitle_style))
    story.append(Spacer(1, 15))
    story.append(Paragraph("ONLINE COURSE ENROLLMENT SYSTEM", title_style))
    story.append(HRFlowable(width="60%", thickness=2, color=BLUE, spaceAfter=25, spaceBefore=10))

    story.append(Paragraph("Submitted in partial fulfilment of the requirement<br/>for the Degree of", subtitle_style))
    story.append(Paragraph("<b>Bachelor of Technology</b><br/>In<br/><b>Electronics & Computer Science</b>", ParagraphStyle('Deg', parent=subtitle_style, fontName='Helvetica-Bold', fontSize=12, leading=16, textColor=DARK_TEXT)))
    story.append(Spacer(1, 25))

    story.append(Paragraph("<b>Submitted By:</b>", ParagraphStyle('SubBy', parent=subtitle_style, fontName='Helvetica-Bold', fontSize=11, textColor=NAVY)))
    story.append(Paragraph("<b>Purnima Nalla:</b> ECSB439<br/><b>Malkit Singh:</b> ECSB442<br/><b>Chandrakant Varande:</b> ECSB443", ParagraphStyle('Names', parent=subtitle_style, fontSize=11, leading=16, textColor=DARK_TEXT)))
    story.append(Spacer(1, 20))

    story.append(Paragraph("<b>Supervisor:</b>", ParagraphStyle('SupBy', parent=subtitle_style, fontName='Helvetica-Bold', fontSize=11, textColor=NAVY)))
    story.append(Paragraph("<b>Dr. Ravi Biradar</b>", ParagraphStyle('SupName', parent=subtitle_style, fontSize=12, textColor=DARK_TEXT)))
    story.append(Spacer(1, 35))

    story.append(Paragraph("<b>DEPARTMENT OF ELECTRONICS & COMPUTER SCIENCE</b><br/><b>PILLAI COLLEGE OF ENGINEERING</b><br/>New Panvel – 410206<br/><b>Academic Year 2024 - 25</b>", ParagraphStyle('Dept', parent=subtitle_style, fontSize=10.5, leading=15, textColor=NAVY)))
    story.append(PageBreak())

    # ==========================================
    # PAGE 2: CERTIFICATE
    # ==========================================
    story.append(Spacer(1, 20))
    story.append(Paragraph("<b>DEPARTMENT OF ELECTRONICS & COMPUTER SCIENCE</b>", ParagraphStyle('CD', parent=subtitle_style, fontSize=13, textColor=NAVY)))
    story.append(Paragraph("Pillai College of Engineering<br/>New Panvel – 410206", subtitle_style))
    story.append(Spacer(1, 15))
    story.append(Paragraph("<b>CERTIFICATE</b>", title_style))
    story.append(HRFlowable(width="40%", thickness=1.5, color=NAVY, spaceAfter=20, spaceBefore=5))

    story.append(Paragraph("This is to certify that the requirements for the Java Programming Lab I mini-project entitled <b>'Online Course Enrollment System'</b> have been successfully completed by the following students:", body_style))
    story.append(Spacer(1, 10))

    cert_table_data = [
        [Paragraph("<b>Student Name</b>", ParagraphStyle('TH', parent=body_style, textColor=colors.white)),
         Paragraph("<b>Roll No.</b>", ParagraphStyle('TH', parent=body_style, textColor=colors.white))],
        [Paragraph("Purnima Nalla", body_style), Paragraph("ECSB439", body_style)],
        [Paragraph("Malkit Singh", body_style), Paragraph("ECSB442", body_style)],
        [Paragraph("Chandrakant Varande", body_style), Paragraph("ECSB443", body_style)],
    ]
    t_cert = Table(cert_table_data, colWidths=[3.2*inch, 2.5*inch])
    t_cert.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), NAVY),
        ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
        ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 6),
        ('TOPPADDING', (0, 0), (-1, -1), 6),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, LIGHT_BG]),
        ('GRID', (0, 0), (-1, -1), 0.5, BORDER_COLOR),
    ]))
    story.append(t_cert)
    story.append(Spacer(1, 15))

    story.append(Paragraph("in partial fulfillment of <b>Bachelor of Technology in Electronics & Computer Science</b> at Pillai College of Engineering, New Panvel – 410206 during the Academic Year 2024 – 2025.", body_style))
    story.append(Spacer(1, 60))

    sig_data = [
        [Paragraph("___________________________<br/><b>Dr. Ravi Biradar</b><br/>Project Supervisor", body_style),
         Paragraph("___________________________<br/><b>Head of Department</b><br/>Dept. of Electronics & Computer Science", body_style)]
    ]
    t_sig = Table(sig_data, colWidths=[3.2*inch, 3.2*inch])
    t_sig.setStyle(TableStyle([
        ('ALIGN', (0, 0), (-1, -1), 'CENTER'),
        ('VALIGN', (0, 0), (-1, -1), 'TOP'),
    ]))
    story.append(t_sig)
    story.append(PageBreak())

    # ==========================================
    # CHAPTER 3: ABSTRACT
    # ==========================================
    story.append(Paragraph("CHAPTER 3: ABSTRACT", h1_style))
    story.append(HRFlowable(width="100%", thickness=1, color=NAVY, spaceAfter=12, spaceBefore=4))
    story.append(Paragraph("The <b>Online Course Enrollment System</b> is a desktop-native application developed in <b>Java (Java 17+)</b> designed to automate course offerings, user authentication, and student registrations. Designed around core <b>Object-Oriented Programming (OOP)</b> principles—including inheritance, abstraction, encapsulation, and polymorphism—the application manages distinct user roles (<b>Student</b> and <b>Instructor</b>) and polymorphic course specializations (<b>ProgrammingCourse</b> and <b>DesignCourse</b>).", body_style))
    story.append(Paragraph("The graphical user interface (GUI) is implemented using <b>Java Swing</b> modernized with the <b>FlatLaf Look and Feel</b>, offering an intuitive dark/light-capable theme, responsive card layouts, and smooth single-frame transitions using <code>CardLayout</code>. Rather than traditional relational databases and raw SQL queries, the system uses a high-performance <b>MongoDB</b> document database via the official <b>MongoDB Java Driver (<code>mongodb-driver-sync</code>)</b>. Advanced MongoDB <b>Aggregation Pipelines (<code>$lookup</code>)</b> are implemented to join student enrollments with course details in real time. For security, user credentials are encrypted using <b>BCrypt cryptographic hashing</b> (<code>jbcrypt</code>), eliminating plaintext password vulnerabilities.", body_style))
    story.append(Paragraph("The primary purpose of this project is to demonstrate the practical integration of modern desktop GUI design, NoSQL database engineering, aggregation pipelines, event-driven architectures, and core software design patterns in a real-world scenario.", body_style))
    story.append(PageBreak())

    # ==========================================
    # CHAPTER 4: TABLE OF CONTENTS
    # ==========================================
    story.append(Paragraph("CHAPTER 4: TABLE OF CONTENTS", h1_style))
    story.append(HRFlowable(width="100%", thickness=1, color=NAVY, spaceAfter=12, spaceBefore=4))

    toc_data = [
        [Paragraph("<b>Chapter</b>", ParagraphStyle('TH', parent=body_style, textColor=colors.white)),
         Paragraph("<b>Title</b>", ParagraphStyle('TH', parent=body_style, textColor=colors.white)),
         Paragraph("<b>Description / Sub-sections</b>", ParagraphStyle('TH', parent=body_style, textColor=colors.white))],
        [Paragraph("Chapter 1", body_style), Paragraph("Front Page", body_style), Paragraph("Title, Authors, Affiliation", body_style)],
        [Paragraph("Chapter 2", body_style), Paragraph("Certificate", body_style), Paragraph("Departmental Certification", body_style)],
        [Paragraph("Chapter 3", body_style), Paragraph("Abstract", body_style), Paragraph("Executive Summary", body_style)],
        [Paragraph("Chapter 4", body_style), Paragraph("Table of Contents", body_style), Paragraph("Document Structure", body_style)],
        [Paragraph("Chapter 5", body_style), Paragraph("Introduction", body_style), Paragraph("5.1 Purpose, 5.2 Problem Statement, 5.3 Objectives", body_style)],
        [Paragraph("Chapter 6", body_style), Paragraph("Tools & Technology", body_style), Paragraph("6.1 Frontend, 6.2 Backend (OOP), 6.3 MongoDB, 6.4 Tools", body_style)],
        [Paragraph("Chapter 7", body_style), Paragraph("System Design", body_style), Paragraph("7.1 3-Tier Model, 7.2 Architecture, 7.3 Data Flow", body_style)],
        [Paragraph("Chapter 8", body_style), Paragraph("Implementation", body_style), Paragraph("8.1 UI Panels, 8.2 DAOs & BCrypt, 8.3 Aggregations", body_style)],
        [Paragraph("Chapter 9", body_style), Paragraph("Output & Weekly Logs", body_style), Paragraph("9.1 Screens, 9.2 MongoDB BSON, Weekly Logs (W1-W3)", body_style)],
        [Paragraph("Chapter 10", body_style), Paragraph("Testing & Debugging", body_style), Paragraph("Comprehensive JUnit 5 Test Matrix", body_style)],
        [Paragraph("Chapter 11", body_style), Paragraph("Conclusion & Future Work", body_style), Paragraph("11.1 Conclusion, 11.2 Future Enhancements", body_style)],
    ]
    t_toc = Table(toc_data, colWidths=[1.1*inch, 2.3*inch, 3.2*inch])
    t_toc.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), NAVY),
        ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 5),
        ('TOPPADDING', (0, 0), (-1, -1), 5),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, LIGHT_BG]),
        ('GRID', (0, 0), (-1, -1), 0.5, BORDER_COLOR),
    ]))
    story.append(t_toc)
    story.append(PageBreak())

    # ==========================================
    # CHAPTER 5: INTRODUCTION
    # ==========================================
    story.append(Paragraph("CHAPTER 5: INTRODUCTION", h1_style))
    story.append(HRFlowable(width="100%", thickness=1, color=NAVY, spaceAfter=10, spaceBefore=4))

    story.append(Paragraph("5.1 Purpose of the Project:", h2_style))
    story.append(Paragraph("The <b>Online Course Enrollment System</b> is designed to provide a comprehensive and automated solution for managing digital learning paths, course offerings, and student registrations. Built in Java, it replaces manual tracking sheets with an interactive, reliable desktop platform.", body_style))
    story.append(Paragraph("The system features a modern FlatLaf user interface, single-frame CardLayout screen navigation, NoSQL persistence with MongoDB, and cryptographic password protection via BCrypt. It demonstrates the practical implementation of Object-Oriented design, DAOs, and document aggregations.", body_style))

    story.append(Paragraph("5.2 Problem Statement:", h2_style))
    story.append(Paragraph("In many educational departments and training setups, course management is handled manually or through basic disconnected applications. This creates key problems:", body_style))
    story.append(Paragraph("• <b>Manual Inefficiencies:</b> Tracking registrations via paper logs or spreadsheets is prone to data loss, human error, and record duplication.", bullet_style))
    story.append(Paragraph("• <b>Lack of Real-Time Sync:</b> Course availability and enrollment statuses are not updated dynamically across users.", bullet_style))
    story.append(Paragraph("• <b>Poor User Experience:</b> Default Java Swing interfaces often look cluttered and suffer from excessive pop-up windows.", bullet_style))
    story.append(Paragraph("• <b>Lack of Role Separation:</b> Many legacy systems hardcode all accounts as students, omitting instructor privileges like course creation and student roster inspection.", bullet_style))
    story.append(Paragraph("• <b>Security Risks:</b> Plaintext passwords in database tables represent serious security vulnerabilities.", bullet_style))

    story.append(Paragraph("5.3 Objectives of the Project:", h2_style))
    objs = [
        "<b>1. Interactive GUI:</b> Build a responsive interface with FlatLaf dark/light theming and single-frame CardLayout navigation.",
        "<b>2. Secure Authentication:</b> Hash all passwords with 12-round BCrypt before database storage.",
        "<b>3. Role-Based Structure:</b> Separate Student and Instructor accounts using OOP inheritance.",
        "<b>4. Polymorphic Course Catalog:</b> Manage Programming and Design courses with custom badges and tags.",
        "<b>5. Real-Time Enrollment:</b> Enable 1-click enrollments with instant button state updates and duplicate rejection.",
        "<b>6. MongoDB Aggregations:</b> Query enrollments using <code>$lookup</code> aggregation pipelines.",
        "<b>7. Cascading Account Cleanup:</b> Cascade-delete all enrollments and courses upon account deletion.",
        "<b>8. Automated Seeder:</b> Populate sample demo accounts and courses on startup if the database is empty.",
        "<b>9. Live Theme Toggle:</b> Switch dynamically between Dark and Light mode at runtime.",
        "<b>10. Software Engineering Standards:</b> Adopt Maven dependency management and the Data Access Object (DAO) pattern."
    ]
    for o in objs:
        story.append(Paragraph(f"• {o}", bullet_style))
    story.append(PageBreak())

    # ==========================================
    # CHAPTER 6: TOOLS AND TECHNOLOGY USED
    # ==========================================
    story.append(Paragraph("CHAPTER 6: TOOLS AND TECHNOLOGY USED", h1_style))
    story.append(HRFlowable(width="100%", thickness=1, color=NAVY, spaceAfter=10, spaceBefore=4))

    story.append(Paragraph("6.1 Frontend (User Interface):", h2_style))
    story.append(Paragraph("• <b>Java Swing:</b> Core desktop GUI toolkit (<code>JFrame</code>, <code>JPanel</code>, <code>JLabel</code>, <code>JTextField</code>, <code>JPasswordField</code>, <code>JProgressBar</code>, <code>JTable</code>).", bullet_style))
    story.append(Paragraph("• <b>FlatLaf (<code>com.formdev:flatlaf</code>):</b> Modern look-and-feel library enabling smooth dark/light themes, rounded corners (<code>Arc: 12-16</code>), and clean typography.", bullet_style))
    story.append(Paragraph("• <b>Custom Components:</b> <code>RoundedPanel</code>, <code>CustomButton</code> (semantic variants), <code>StatCard</code>, <code>ToastNotification</code>, and <code>WrapLayout</code>.", bullet_style))

    story.append(Paragraph("6.2 Backend (Application Logic):", h2_style))
    story.append(Paragraph("• <b>Core Java (Java 17+ / Java 25):</b> Controls application logic, threading via <code>SwingWorker</code>, and object mappings.", bullet_style))
    story.append(Paragraph("• <b>OOP Concepts:</b> Inheritance (<code>User</code> → <code>Student</code>, <code>Instructor</code>), Abstraction (<code>Course</code> abstract methods), Polymorphism (<code>ProgrammingCourse</code>, <code>DesignCourse</code>), Encapsulation (private fields & getters/setters).", bullet_style))
    story.append(Paragraph("• <b>BCrypt Hashing (<code>org.mindrot:jbcrypt</code>):</b> 12-round salted hashing algorithm securing authentication.", bullet_style))

    story.append(Paragraph("6.3 Database (MongoDB NoSQL):", h2_style))
    story.append(Paragraph("• <b>MongoDB Engine:</b> Local NoSQL document store running on <code>localhost:27017</code>, database <code>online_course_db</code>.", bullet_style))
    story.append(Paragraph("• <b>Collections:</b> <code>users</code> (user identity & hashes), <code>courses</code> (curriculum details), <code>enrollments</code> (student-course associations).", bullet_style))
    story.append(Paragraph("• <b>Driver:</b> Official MongoDB Synchronous Java Driver (<code>org.mongodb:mongodb-driver-sync:5.1.0</code>).", bullet_style))
    story.append(Paragraph("• <b>Aggregation Pipelines:</b> Multi-stage <code>$match</code> → <code>$lookup</code> → <code>$unwind</code> → <code>$sort</code> operations replacing relational joins.", bullet_style))

    story.append(Paragraph("6.4 Tools & Build Automation:", h2_style))
    story.append(Paragraph("• <b>Apache Maven:</b> Manages dependencies, compiler configurations, and lifecycle via <code>pom.xml</code>.", bullet_style))
    story.append(Paragraph("• <b>Maven Shade Plugin:</b> Creates executable Fat JARs with bundled dependencies.", bullet_style))
    story.append(Paragraph("• <b>JUnit 5:</b> Automated testing suite for DAO logic and aggregations.", bullet_style))
    story.append(Paragraph("• <b>MongoDB Compass & mongosh:</b> Database management and schema validation.", bullet_style))
    story.append(PageBreak())

    # ==========================================
    # CHAPTER 7: SYSTEM DESIGN
    # ==========================================
    story.append(Paragraph("CHAPTER 7: SYSTEM DESIGN", h1_style))
    story.append(HRFlowable(width="100%", thickness=1, color=NAVY, spaceAfter=10, spaceBefore=4))

    story.append(Paragraph("7.1 System Architecture (3-Tier Model):", h2_style))
    story.append(Paragraph("The system is architected into three distinct layers to ensure separation of concerns, scalability, and maintainability:", body_style))
    story.append(Paragraph("• <b>1. Presentation Layer (GUI):</b> Java Swing modernized with FlatLaf, executing inside a single <code>MainFrame</code> with <code>CardLayout</code> navigation.", bullet_style))
    story.append(Paragraph("• <b>2. Application Layer (Backend):</b> Core Java business logic, polymorphic domain models, BCrypt cryptographic services, and Data Access Objects (<code>UserDAO</code>, <code>CourseDAO</code>, <code>EnrollmentDAO</code>).", bullet_style))
    story.append(Paragraph("• <b>3. Data Layer (Database):</b> MongoDB document collections (<code>users</code>, <code>courses</code>, <code>enrollments</code>) with compound indexes.", bullet_style))

    story.append(Paragraph("7.2 System Block Diagram:", h2_style))
    dia_text = """+-------------------------------------------------------------+
|                 PRESENTATION LAYER (GUI)                    |
|   Java Swing + FlatLaf Theme (Dark/Light) + CardLayout      |
|   [Splash]  [Login/Signup]  [Dashboard]  [CourseCatalog]    |
+------------------------------+------------------------------+
                               | ActionEvents / Listeners
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
    
    t_dia = Table([[Paragraph(f"<pre>{dia_text}</pre>", code_style)]], colWidths=[6.6*inch])
    t_dia.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), LIGHT_BG),
        ('BOX', (0,0), (-1,-1), 0.5, BORDER_COLOR),
        ('TOPPADDING', (0,0), (-1,-1), 6),
        ('BOTTOMPADDING', (0,0), (-1,-1), 6),
    ]))
    story.append(t_dia)
    story.append(Spacer(1, 10))

    story.append(Paragraph("7.3 Step-by-Step Data Flow:", h2_style))
    story.append(Paragraph("<b>1. Application Launch:</b> <code>App.java</code> initializes FlatLaf, verifies MongoDB, seeds default data if empty, and launches <code>SplashPanel</code> which auto-advances to Auth in 2s.", bullet_style))
    story.append(Paragraph("<b>2. User Registration:</b> User submits details. <code>UserDAO.registerUser()</code> generates a 12-round BCrypt hash and inserts document into <code>users</code>.", bullet_style))
    story.append(Paragraph("<b>3. User Login:</b> <code>UserDAO.authenticate()</code> checks credentials against stored hash via <code>BCrypt.checkpw()</code>. On success, <code>WelcomeTransitionPanel</code> appears for 1s before showing Dashboard.", bullet_style))
    story.append(Paragraph("<b>4. Course Management:</b> Instructors add courses via modal dialog. <code>CourseDAO.createCourse()</code> saves document. <code>CoursePanel</code> displays cards with search and category filtering.", bullet_style))
    story.append(Paragraph("<b>5. Enrollment & Lookup:</b> Student enrolls $\\rightarrow$ record saved in <code>enrollments</code>. 'My Enrollments' runs a MongoDB <code>$lookup</code> pipeline joining course details.", bullet_style))
    story.append(Paragraph("<b>6. Cascading Account Cleanup:</b> In <code>ProfilePanel</code>, 'Delete My Account' cascade-removes the user, all their enrollments, and instructor courses.", bullet_style))
    story.append(PageBreak())

    # ==========================================
    # CHAPTER 8: IMPLEMENTATION
    # ==========================================
    story.append(Paragraph("CHAPTER 8: IMPLEMENTATION", h1_style))
    story.append(HRFlowable(width="100%", thickness=1, color=NAVY, spaceAfter=10, spaceBefore=4))

    story.append(Paragraph("8.1 Frontend Development & Key Panels:", h2_style))
    story.append(Paragraph("• <b>SplashPanel:</b> Animated graduation logo, project title, and progress bar with background connection testing.", bullet_style))
    story.append(Paragraph("• <b>LoginSignupPanel:</b> Tabbed auth card with email validation, password show/hide, role toggle (Student/Instructor), and 1-click Demo Account buttons.", bullet_style))
    story.append(Paragraph("• <b>WelcomeTransitionPanel:</b> Personalized greeting card ('Hello, <Name>!') displayed for 1 second upon login.", bullet_style))
    story.append(Paragraph("• <b>DashboardPanel:</b> Sidebar with branding, user info, nav buttons, live theme switcher, and role-based tab routing.", bullet_style))
    story.append(Paragraph("• <b>CoursePanel & CourseCard:</b> Live keyword search, category pills ('Programming', 'Design'), responsive card grid, and course creation modal.", bullet_style))
    story.append(Paragraph("• <b>MyEnrollmentsPanel:</b> Student view displaying enrolled courses via <code>$lookup</code> joins with a 'Drop Course' action.", bullet_style))
    story.append(Paragraph("• <b>InstructorEnrollmentsPanel:</b> Instructor view featuring a table of enrolled students across published courses.", bullet_style))
    story.append(Paragraph("• <b>ProfilePanel:</b> User metadata card and Danger Zone for cascading account deletion.", bullet_style))

    story.append(Paragraph("8.2 Backend DAO & BCrypt Implementation:", h2_style))
    code_auth = """// BCrypt Registration & Login in UserDAO.java
public boolean registerUser(User user, String plainPassword) {
    String hash = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    Document doc = new Document("name", user.getName())
        .append("email", user.getEmail().toLowerCase())
        .append("passwordHash", hash)
        .append("role", user.getRole())
        .append("createdAt", new Date());
    collection.insertOne(doc);
    return true;
}

public User authenticate(String email, String plainPassword) {
    Document doc = collection.find(Filters.eq("email", email.toLowerCase())).first();
    if (doc != null && BCrypt.checkpw(plainPassword, doc.getString("passwordHash"))) {
        return documentToUser(doc); // Returns Student or Instructor
    }
    return null;
}"""
    t_code1 = Table([[Paragraph(f"<pre>{code_auth}</pre>", code_style)]], colWidths=[6.6*inch])
    t_code1.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), LIGHT_BG),
        ('BOX', (0,0), (-1,-1), 0.5, BORDER_COLOR),
        ('TOPPADDING', (0,0), (-1,-1), 4),
        ('BOTTOMPADDING', (0,0), (-1,-1), 4),
    ]))
    story.append(t_code1)
    story.append(Spacer(1, 8))

    story.append(Paragraph("8.3 MongoDB Aggregation Pipeline ($lookup):", h2_style))
    code_agg = """// EnrollmentDAO.java: Aggregation Pipeline joining enrollments -> courses
List<Bson> pipeline = Arrays.asList(
    Aggregates.match(Filters.eq("userEmail", normalizedEmail)),
    Aggregates.lookup("courses", "courseId", "_id", "courseDetails"),
    Aggregates.unwind("$courseDetails"),
    Aggregates.sort(Sorts.descending("enrolledAt"))
);
for (Document res : collection.aggregate(pipeline)) {
    Document cDoc = res.get("courseDetails", Document.class);
    Course course = CourseDAO.documentToCourse(cDoc); // Polymorphic conversion
    enrollment.setCourse(course);
}"""
    t_code2 = Table([[Paragraph(f"<pre>{code_agg}</pre>", code_style)]], colWidths=[6.6*inch])
    t_code2.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), LIGHT_BG),
        ('BOX', (0,0), (-1,-1), 0.5, BORDER_COLOR),
        ('TOPPADDING', (0,0), (-1,-1), 4),
        ('BOTTOMPADDING', (0,0), (-1,-1), 4),
    ]))
    story.append(t_code2)
    story.append(PageBreak())

    # ==========================================
    # CHAPTER 9: OUTPUT & WEEKLY LOGS
    # ==========================================
    story.append(Paragraph("CHAPTER 9: OUTPUT & WEEKLY PROGRESS LOGS", h1_style))
    story.append(HRFlowable(width="100%", thickness=1, color=NAVY, spaceAfter=10, spaceBefore=4))

    story.append(Paragraph("9.1 MongoDB Database Documents:", h2_style))
    json_text = """// 1. users Collection Document
{
  "_id": ObjectId("66fa9b8c12a4e51234567890"),
  "name": "Dr. Alan Turing",
  "email": "alan.turing@college.edu",
  "passwordHash": "$2a$12$e8FN38wO2eT...BCryptHash",
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
    t_json = Table([[Paragraph(f"<pre>{json_text}</pre>", code_style)]], colWidths=[6.6*inch])
    t_json.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), LIGHT_BG),
        ('BOX', (0,0), (-1,-1), 0.5, BORDER_COLOR),
        ('TOPPADDING', (0,0), (-1,-1), 4),
        ('BOTTOMPADDING', (0,0), (-1,-1), 4),
    ]))
    story.append(t_json)
    story.append(Spacer(1, 10))

    story.append(Paragraph("9.2 Weekly Progress Logs:", h2_style))
    story.append(Paragraph("<b>WEEK 1 – Planning and System Architecture:</b>", h2_style))
    story.append(Paragraph("• <i>Objective:</i> Understand system requirements, choose modern tech stack, and structure OOP models.<br/>• <i>Work Done:</i> Designed User and Course class hierarchies, created MongoDB document schemas, and configured Maven pom.xml.<br/>• <i>Result:</i> Project architecture and database schemas completed successfully.", body_style))

    story.append(Paragraph("<b>WEEK 2 – Backend Development & MongoDB Connection:</b>", h2_style))
    story.append(Paragraph("• <i>Objective:</i> Implement DAO layer, BCrypt password hashing, and aggregation pipelines.<br/>• <i>Work Done:</i> Created MongoConnection singleton, UserDAO with BCrypt, CourseDAO with regex search, EnrollmentDAO with $lookup join, and DatabaseSeeder.<br/>• <i>Result:</i> Backend logic verified with automated JUnit tests.", body_style))

    story.append(Paragraph("<b>WEEK 3 – GUI Development, FlatLaf Theming & Testing:</b>", h2_style))
    story.append(Paragraph("• <i>Objective:</i> Design FlatLaf desktop interface and perform comprehensive system testing.<br/>• <i>Work Done:</i> Created MainFrame with CardLayout, SplashPanel, LoginSignupPanel, DashboardPanel, CourseCards, and dynamic theme switcher. Completed end-to-end testing.<br/>• <i>Result:</i> Fully functional system packaged into executable Fat JAR.", body_style))
    story.append(PageBreak())

    # ==========================================
    # CHAPTER 10: TESTING AND DEBUGGING
    # ==========================================
    story.append(Paragraph("CHAPTER 10: TESTING AND DEBUGGING", h1_style))
    story.append(HRFlowable(width="100%", thickness=1, color=NAVY, spaceAfter=10, spaceBefore=4))

    story.append(Paragraph("The system was validated using automated JUnit 5 test suites (<code>DAOTest.java</code>) and manual test cases covering all user workflows:", body_style))
    story.append(Spacer(1, 4))

    test_table_data = [
        [Paragraph("<b>ID</b>", ParagraphStyle('TH', parent=body_style, textColor=colors.white, fontSize=8.5)),
         Paragraph("<b>Feature Tested</b>", ParagraphStyle('TH', parent=body_style, textColor=colors.white, fontSize=8.5)),
         Paragraph("<b>Input / Action</b>", ParagraphStyle('TH', parent=body_style, textColor=colors.white, fontSize=8.5)),
         Paragraph("<b>Expected Output</b>", ParagraphStyle('TH', parent=body_style, textColor=colors.white, fontSize=8.5)),
         Paragraph("<b>Actual Output</b>", ParagraphStyle('TH', parent=body_style, textColor=colors.white, fontSize=8.5)),
         Paragraph("<b>Result</b>", ParagraphStyle('TH', parent=body_style, textColor=colors.white, fontSize=8.5))],
        [Paragraph("TC01", body_style), Paragraph("App Launch", body_style), Paragraph("Run App.java", body_style), Paragraph("Splash screen with progress bar", body_style), Paragraph("Displayed smoothly", body_style), Paragraph("<font color='#16A34A'><b>PASS</b></font>", body_style)],
        [Paragraph("TC02", body_style), Paragraph("Splash Flow", body_style), Paragraph("Wait ~2 seconds", body_style), Paragraph("Auto-advance to Login", body_style), Paragraph("Navigated to Auth", body_style), Paragraph("<font color='#16A34A'><b>PASS</b></font>", body_style)],
        [Paragraph("TC03", body_style), Paragraph("User Signup", body_style), Paragraph("Name, Email, Role, Pass", body_style), Paragraph("BCrypt doc saved in users", body_style), Paragraph("Data inserted & hashed", body_style), Paragraph("<font color='#16A34A'><b>PASS</b></font>", body_style)],
        [Paragraph("TC04", body_style), Paragraph("Duplicate Email", body_style), Paragraph("Register existing email", body_style), Paragraph("Registration rejected", body_style), Paragraph("Error prompt shown", body_style), Paragraph("<font color='#16A34A'><b>PASS</b></font>", body_style)],
        [Paragraph("TC05", body_style), Paragraph("Login Success", body_style), Paragraph("Valid credentials", body_style), Paragraph("Welcome screen → Dashboard", body_style), Paragraph("Logged in successfully", body_style), Paragraph("<font color='#16A34A'><b>PASS</b></font>", body_style)],
        [Paragraph("TC06", body_style), Paragraph("Login Failure", body_style), Paragraph("Wrong password", body_style), Paragraph("Error toast displayed", body_style), Paragraph("Error message shown", body_style), Paragraph("<font color='#16A34A'><b>PASS</b></font>", body_style)],
        [Paragraph("TC07", body_style), Paragraph("Demo Quick-Fill", body_style), Paragraph("Click 'Demo Student'", body_style), Paragraph("Auto-fills demo account", body_style), Paragraph("Fields filled instantly", body_style), Paragraph("<font color='#16A34A'><b>PASS</b></font>", body_style)],
        [Paragraph("TC08", body_style), Paragraph("Add Course", body_style), Paragraph("Enter title, type, desc", body_style), Paragraph("Saved in courses collection", body_style), Paragraph("Published in catalog", body_style), Paragraph("<font color='#16A34A'><b>PASS</b></font>", body_style)],
        [Paragraph("TC09", body_style), Paragraph("Search / Filter", body_style), Paragraph("Type keyword & category", body_style), Paragraph("Live card grid filtering", body_style), Paragraph("Filtered accurately", body_style), Paragraph("<font color='#16A34A'><b>PASS</b></font>", body_style)],
        [Paragraph("TC10", body_style), Paragraph("Enroll Course", body_style), Paragraph("Click 'Enroll Now'", body_style), Paragraph("Stored in enrollments", body_style), Paragraph("Button: '✓ Enrolled'", body_style), Paragraph("<font color='#16A34A'><b>PASS</b></font>", body_style)],
        [Paragraph("TC11", body_style), Paragraph("Duplicate Guard", body_style), Paragraph("Try duplicate enroll", body_style), Paragraph("Prevented by compound index", body_style), Paragraph("Prevented gracefully", body_style), Paragraph("<font color='#16A34A'><b>PASS</b></font>", body_style)],
        [Paragraph("TC12", body_style), Paragraph("View Enrolled", body_style), Paragraph("Open 'My Enrollments'", body_style), Paragraph("Fetched via $lookup join", body_style), Paragraph("Cards displayed properly", body_style), Paragraph("<font color='#16A34A'><b>PASS</b></font>", body_style)],
        [Paragraph("TC13", body_style), Paragraph("Drop Course", body_style), Paragraph("Click 'Drop Course'", body_style), Paragraph("Removed from collection", body_style), Paragraph("Enrollment dropped", body_style), Paragraph("<font color='#16A34A'><b>PASS</b></font>", body_style)],
        [Paragraph("TC14", body_style), Paragraph("Delete Course", body_style), Paragraph("Instructor deletes course", body_style), Paragraph("Cascade delete enrollments", body_style), Paragraph("Cleaned up & removed", body_style), Paragraph("<font color='#16A34A'><b>PASS</b></font>", body_style)],
        [Paragraph("TC15", body_style), Paragraph("Delete Account", body_style), Paragraph("Click 'Delete My Account'", body_style), Paragraph("Cascade remove user data", body_style), Paragraph("Removed & logged out", body_style), Paragraph("<font color='#16A34A'><b>PASS</b></font>", body_style)],
        [Paragraph("TC16", body_style), Paragraph("Theme Switch", body_style), Paragraph("Click 'Toggle Theme'", body_style), Paragraph("Switches Dark/Light UI", body_style), Paragraph("Theme updated smoothly", body_style), Paragraph("<font color='#16A34A'><b>PASS</b></font>", body_style)],
    ]

    t_test = Table(test_table_data, colWidths=[0.5*inch, 1.1*inch, 1.3*inch, 1.5*inch, 1.4*inch, 0.8*inch])
    t_test.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), NAVY),
        ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 3),
        ('TOPPADDING', (0, 0), (-1, -1), 3),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, LIGHT_BG]),
        ('GRID', (0, 0), (-1, -1), 0.5, BORDER_COLOR),
    ]))
    story.append(t_test)
    story.append(PageBreak())

    # ==========================================
    # CHAPTER 11: CONCLUSION & FUTURE WORK
    # ==========================================
    story.append(Paragraph("CHAPTER 11: CONCLUSION AND FUTURE ENHANCEMENT", h1_style))
    story.append(HRFlowable(width="100%", thickness=1, color=NAVY, spaceAfter=10, spaceBefore=4))

    story.append(Paragraph("11.1 Conclusion:", h2_style))
    story.append(Paragraph("The <b>Online Course Enrollment System</b> was successfully designed, implemented, and tested using <b>Java 17+ (Java Swing + FlatLaf)</b>, <b>MongoDB</b>, and <b>BCrypt security</b>. The project achieved all core design goals:", body_style))
    story.append(Paragraph("• <b>Modern User Experience:</b> Transformed traditional Swing UI with FlatLaf styling, rounded cards, and single-frame <code>CardLayout</code> transitions.", bullet_style))
    story.append(Paragraph("• <b>Object-Oriented Design:</b> Applied inheritance, abstraction, and polymorphism across user and course hierarchies.", bullet_style))
    story.append(Paragraph("• <b>NoSQL Efficiency:</b> Implemented MongoDB document storage and <code>$lookup</code> aggregation pipelines.", bullet_style))
    story.append(Paragraph("• <b>Security:</b> Protected user credentials using 12-round BCrypt cryptographic hashing.", bullet_style))
    story.append(Paragraph("• <b>Reliability:</b> Validated end-to-end functionality with automated JUnit 5 tests, achieving a 100% pass rate.", bullet_style))

    story.append(Paragraph("11.2 Future Enhancements:", h2_style))
    story.append(Paragraph("• <b>1. MongoDB Atlas Cloud:</b> Migrate local database to cloud-hosted MongoDB Atlas for multi-device network access.", bullet_style))
    story.append(Paragraph("• <b>2. Payment Integration:</b> Add Stripe or Razorpay APIs to support premium paid certifications.", bullet_style))
    story.append(Paragraph("• <b>3. Multimedia Modules:</b> Embed video lectures and downloadable course PDF resources.", bullet_style))
    story.append(Paragraph("• <b>4. Assessment & Certificates:</b> Include module quizzes and automated completion certificate generation.", bullet_style))
    story.append(Paragraph("• <b>5. Automated Notifications:</b> Integrate JavaMail API to send course announcements and enrollment confirmations.", bullet_style))

    # Build Document with NumberedCanvas
    doc.build(story, canvasmaker=NumberedCanvas)
    print(f"[SUCCESS] Generated PDF Report at: {filename}")

if __name__ == "__main__":
    build_pdf()
