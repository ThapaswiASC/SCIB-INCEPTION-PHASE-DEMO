# User Workflow Journey Documentation

## Experience Overview

**User Type**: Healthcare Patient  
**Experience Context**: Digital Healthcare Platform - Patient Journey from Symptom Assessment to Treatment

This documentation outlines comprehensive user workflows for a digital healthcare platform, focusing on patient experiences from initial symptom assessment through appointment booking and management.

---

## Scenario 1: New Patient Symptom Assessment and Doctor Discovery

**Context**: Sarah, a 28-year-old working professional, has been experiencing persistent headaches for 3 days. She's never used the platform before and wants to understand her symptoms and find appropriate medical care quickly and efficiently.

### Workflow Variation 1A: Guided Symptom Assessment Path

**User Goal**: Help Sarah identify potential causes of her symptoms and connect her with appropriate medical professionals based on her assessment results.

**Business Goal**: Convert new users into engaged patients while building trust through accurate symptom assessment and seamless doctor matching.

#### Screen Flow:

**1.0 Homepage [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item
- NAVBAR PRESENCE: Yes
- Page Goal: Build credibility and guide new patients toward symptom assessment
- Screen Description:
  1. Patient can start a symptom assessment with prominent CTA
  2. Patient can search for doctors by specialty or condition
  3. Patient can view platform benefits and testimonials
  4. Patient can access emergency care information
  5. Patient can view health packages and preventive care options
- Design Problems:
  - HMW help patients quickly determine if they need immediate care vs. routine consultation?
  - HMW build trust with first-time users who may be anxious about their symptoms?
  - HMW communicate the platform's medical credibility without overwhelming users?
- Design Opportunities:
  - What if we could provide immediate triage guidance based on symptom severity?
  - What if we offered a virtual health assistant to guide nervous first-time users?
  - What if we could show real-time doctor availability for urgent cases?

**Pu.1 Symptom Assessment Modal [type: modal]**
- HOW IT IS REACHED: Click "Start Symptom Assessment" CTA button on Homepage
- NAVBAR PRESENCE: No
- Page Goal: Collect accurate symptom information to provide relevant doctor recommendations
- Screen Description:
  1. Multi-step symptom questionnaire with body diagram
  2. Severity and duration assessment
  3. Medical history and current medications input
  4. Urgency level determination
- Design Problems:
  - HMW make medical questionnaires feel approachable rather than clinical?
  - HMW ensure accuracy while keeping the assessment brief?
  - HMW handle users who may not know medical terminology?
- Design Opportunities:
  - What if we used conversational UI to make the assessment feel more natural?
  - What if we provided visual aids to help users describe symptoms accurately?
  - What if we could detect urgency and fast-track critical cases?

**2.0 Assessment Results & Doctor Recommendations [type: primary]**
- HOW IT IS REACHED: Complete symptom assessment modal
- NAVBAR PRESENCE: Yes
- Page Goal: Present assessment insights and connect patient with appropriate specialists
- Screen Description:
  1. Symptom assessment summary with severity indicators
  2. Recommended specialties and explanation
  3. Curated list of available doctors with ratings and availability
  4. Option to book immediate consultation or schedule appointment
  5. Educational content about the condition
- Design Problems:
  - HMW present medical recommendations without causing alarm?
  - HMW help patients choose between multiple qualified doctors?
  - HMW balance urgency with informed decision-making?
- Design Opportunities:
  - What if we could show doctor-patient compatibility scores?
  - What if we provided video introductions from recommended doctors?
  - What if we offered different consultation types (video, phone, in-person)?

**2.0-D Doctor Profile Detail [type: detail]**
- HOW IT IS REACHED: Click on doctor card from Assessment Results screen
- NAVBAR PRESENCE: No
- Page Goal: Provide comprehensive doctor information to support booking decision
- Screen Description:
  1. Doctor credentials, specializations, and experience
  2. Patient reviews and ratings with detailed feedback
  3. Available appointment slots with pricing
  4. Doctor's approach to treatment and communication style
  5. Clinic location and virtual consultation options
- Design Problems:
  - HMW help patients evaluate doctor quality beyond just ratings?
  - HMW present scheduling options clearly for different consultation types?
  - HMW address patient concerns about cost transparency?
- Design Opportunities:
  - What if we showed doctor response times and communication preferences?
  - What if we provided treatment outcome statistics for the doctor's specialty?
  - What if we offered patient testimonials specific to similar conditions?

**Pu.2 Appointment Booking Modal [type: modal]**
- HOW IT IS REACHED: Click "Book Appointment" CTA button on Doctor Profile Detail
- NAVBAR PRESENCE: No
- Page Goal: Complete appointment scheduling with all necessary information
- Screen Description:
  1. Available time slots with consultation type selection
  2. Patient information form (if new user)
  3. Insurance verification and payment options
  4. Appointment preferences and special requirements
  5. Confirmation and calendar integration
- Design Problems:
  - HMW streamline booking without sacrificing necessary information collection?
  - HMW handle insurance verification smoothly?
  - HMW accommodate patients with accessibility needs during booking?
- Design Opportunities:
  - What if we could auto-fill patient information from previous assessments?
  - What if we provided real-time insurance coverage verification?
  - What if we offered flexible rescheduling options upfront?

### Workflow Variation 1B: Direct Doctor Search Path

**User Goal**: Allow experienced patients to directly search and book with specific doctors or specialties without symptom assessment.

**Business Goal**: Provide efficient booking for returning patients while capturing their specific needs for personalized care.

#### Screen Flow:

**1.0 Homepage [type: primary]**
- Same as Variation 1A

**3.0 Doctor Search & Discovery [type: primary]**
- HOW IT IS REACHED: Click "Find a Doctor" navigation item or search CTA on Homepage
- NAVBAR PRESENCE: Yes
- Page Goal: Enable efficient doctor discovery through multiple search methods
- Screen Description:
  1. Advanced search filters (specialty, location, availability, insurance)
  2. Doctor grid with key information and availability indicators
  3. Map view for location-based search
  4. Sorting options (rating, distance, availability, price)
  5. Saved searches and doctor favorites for returning users
- Design Problems:
  - HMW help patients narrow down choices without overwhelming filter options?
  - HMW present location and virtual options clearly?
  - HMW accommodate different patient priorities (cost, convenience, expertise)?
- Design Opportunities:
  - What if we could learn patient preferences and suggest relevant filters?
  - What if we showed real-time availability to reduce booking friction?
  - What if we provided comparison tools for multiple doctors?

**3.0-D Doctor Profile Detail [type: detail]**
- Same as 2.0-D from Variation 1A

**Pu.2 Appointment Booking Modal [type: modal]**
- Same as Variation 1A

---

## Scenario 2: Existing Patient Appointment Management

**Context**: Michael, a 45-year-old patient with diabetes, has regular check-ups scheduled and needs to reschedule his upcoming appointment due to a work conflict. He's familiar with the platform and wants to manage this change quickly.

### Workflow Variation 2A: Dashboard-Centric Management

**User Goal**: Enable Michael to efficiently view, modify, and manage his appointments while accessing relevant health information.

**Business Goal**: Increase patient engagement and reduce no-shows through proactive appointment management and health tracking.

#### Screen Flow:

**4.0 Patient Dashboard [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item (default landing for logged-in users)
- NAVBAR PRESENCE: Yes
- Page Goal: Provide comprehensive overview of patient's health journey and immediate actions
- Screen Description:
  1. Upcoming appointments with quick action buttons
  2. Recent test results and health metrics
  3. Medication reminders and refill status
  4. Health goals progress and recommendations
  5. Quick access to emergency contacts and urgent care
- Design Problems:
  - HMW prioritize information for patients with multiple health conditions?
  - HMW make health data actionable rather than just informational?
  - HMW balance comprehensive overview with quick task completion?
- Design Opportunities:
  - What if we could predict which appointments patients are likely to miss?
  - What if we provided personalized health insights based on appointment history?
  - What if we integrated family member health information for caregivers?

**4.1 Appointment Management Section [type: primary]**
- HOW IT IS REACHED: Scroll to appointments section on Patient Dashboard
- NAVBAR PRESENCE: Yes
- Page Goal: Display appointment details with immediate management options
- Screen Description:
  1. Chronological list of upcoming appointments
  2. Past appointment history with outcomes
  3. Quick reschedule and cancel options
  4. Appointment preparation checklists
  5. Virtual consultation join links
- Design Problems:
  - HMW help patients prepare adequately for different types of appointments?
  - HMW make rescheduling feel effortless while maintaining doctor availability?
  - HMW communicate appointment importance for chronic condition management?
- Design Opportunities:
  - What if we could suggest optimal appointment times based on patient's health patterns?
  - What if we provided pre-appointment health metric collection?
  - What if we offered appointment outcome tracking for treatment effectiveness?

**Pu.3 Reschedule Appointment Modal [type: modal]**
- HOW IT IS REACHED: Click "Reschedule" button on appointment card
- NAVBAR PRESENCE: No
- Page Goal: Complete appointment rescheduling with minimal friction
- Screen Description:
  1. Current appointment details and reason for change
  2. Available alternative time slots with doctor availability
  3. Impact assessment (e.g., treatment timeline effects)
  4. Confirmation with calendar updates
  5. Notification preferences for appointment reminders
- Design Problems:
  - HMW help patients understand the medical implications of rescheduling?
  - HMW provide suitable alternatives without overwhelming choice?
  - HMW maintain continuity of care for chronic conditions?
- Design Opportunities:
  - What if we could automatically suggest the next best available slot?
  - What if we provided rescheduling credits for frequent changes?
  - What if we offered waitlist options for preferred times?

### Workflow Variation 2B: Calendar-Centric Management

**User Goal**: Provide Michael with a visual calendar interface to manage multiple appointments and health activities.

**Business Goal**: Improve appointment adherence through better scheduling visualization and integrated health planning.

#### Screen Flow:

**5.0 Health Calendar [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item
- NAVBAR PRESENCE: Yes
- Page Goal: Visualize all health-related activities in a unified calendar interface
- Screen Description:
  1. Monthly/weekly calendar view with appointments, medications, and health activities
  2. Color-coded categories (appointments, medications, exercise, symptoms)
  3. Drag-and-drop rescheduling capabilities
  4. Integration with personal calendar applications
  5. Health milestone and goal tracking
- Design Problems:
  - HMW prevent calendar overload while showing comprehensive health information?
  - HMW make medical appointments feel integrated with daily life?
  - HMW accommodate patients with varying levels of digital literacy?
- Design Opportunities:
  - What if we could sync with wearable devices for automatic health activity logging?
  - What if we provided calendar-based health pattern insights?
  - What if we offered family calendar sharing for coordinated care?

**Pu.4 Quick Appointment Actions Modal [type: modal]**
- HOW IT IS REACHED: Click on appointment in Health Calendar
- NAVBAR PRESENCE: No
- Page Goal: Provide immediate appointment management options from calendar view
- Screen Description:
  1. Appointment summary with doctor and location details
  2. Quick action buttons (reschedule, cancel, add to personal calendar)
  3. Preparation checklist and required documents
  4. Travel time and directions
  5. Related health activities and medication timing
- Design Problems:
  - HMW provide comprehensive appointment context without leaving calendar view?
  - HMW help patients coordinate appointment timing with other health activities?
  - HMW ensure patients don't miss important preparation steps?
- Design Opportunities:
  - What if we could automatically calculate optimal appointment spacing?
  - What if we provided location-based traffic and parking information?
  - What if we offered appointment outcome prediction based on preparation completeness?

---

## Scenario 3: Emergency Care Access and Triage

**Context**: Emma, a 35-year-old mother, notices her 8-year-old child has developed a high fever and rash. She's concerned about the severity and needs immediate guidance on whether to seek emergency care or schedule an urgent consultation.

### Workflow Variation 3A: Emergency Triage Assessment

**User Goal**: Help Emma quickly assess the urgency of her child's condition and connect her with appropriate immediate care options.

**Business Goal**: Provide reliable emergency triage to build trust while directing patients to appropriate care levels, reducing unnecessary ER visits.

#### Screen Flow:

**6.0 Emergency Care Hub [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item or emergency banner on any screen
- NAVBAR PRESENCE: Yes
- Page Goal: Provide immediate access to emergency assessment and care options
- Screen Description:
  1. Emergency triage questionnaire with severity indicators
  2. Direct access to emergency hotlines and 911
  3. Urgent care clinic locator with wait times
  4. Virtual emergency consultation availability
  5. Emergency contact information and medical history quick access
- Design Problems:
  - HMW help parents make critical decisions under stress?
  - HMW ensure medical accuracy while providing immediate guidance?
  - HMW balance urgency with thorough assessment?
- Design Opportunities:
  - What if we could provide real-time ER wait times and severity matching?
  - What if we offered immediate video triage with emergency nurses?
  - What if we could connect directly with pediatric specialists for child emergencies?

**Pu.5 Emergency Triage Assessment Modal [type: modal]**
- HOW IT IS REACHED: Click "Start Emergency Assessment" CTA button on Emergency Care Hub
- NAVBAR PRESENCE: No
- Page Goal: Rapidly assess symptom severity and provide immediate care recommendations
- Screen Description:
  1. Rapid symptom assessment with visual severity scales
  2. Age-specific questions for pediatric cases
  3. Immediate red flag symptom detection
  4. Real-time severity scoring with care recommendations
  5. Direct connection to emergency services if needed
- Design Problems:
  - HMW ensure parents can accurately describe symptoms under stress?
  - HMW provide clear guidance without replacing professional medical judgment?
  - HMW handle liability concerns while providing helpful triage?
- Design Opportunities:
  - What if we could use photo analysis for visible symptoms like rashes?
  - What if we provided immediate connection to poison control for ingestion cases?
  - What if we offered real-time vital sign monitoring guidance?

**7.0 Emergency Care Recommendations [type: primary]**
- HOW IT IS REACHED: Complete emergency triage assessment
- NAVBAR PRESENCE: Yes
- Page Goal: Provide clear, actionable care recommendations based on assessment
- Screen Description:
  1. Urgency level with clear next steps
  2. Recommended care setting (ER, urgent care, telehealth)
  3. Nearest appropriate facilities with directions and contact info
  4. Symptom monitoring guidance while seeking care
  5. Follow-up care recommendations
- Design Problems:
  - HMW communicate urgency levels without causing panic or complacency?
  - HMW help parents choose between multiple care options?
  - HMW ensure continuity between emergency care and regular healthcare?
- Design Opportunities:
  - What if we could provide real-time updates on facility capacity?
  - What if we offered direct admission coordination for serious cases?
  - What if we provided family notification systems for emergency situations?

### Workflow Variation 3B: Immediate Virtual Consultation

**User Goal**: Connect Emma with a healthcare provider immediately for real-time assessment and guidance.

**Business Goal**: Provide immediate care access to reduce anxiety and ensure appropriate care level selection.

#### Screen Flow:

**6.0 Emergency Care Hub [type: primary]**
- Same as Variation 3A

**Pu.6 Immediate Consultation Request Modal [type: modal]**
- HOW IT IS REACHED: Click "Connect Now" CTA button on Emergency Care Hub
- NAVBAR PRESENCE: No
- Page Goal: Facilitate immediate connection with available healthcare providers
- Screen Description:
  1. Brief symptom summary form
  2. Available provider queue with estimated wait times
  3. Insurance verification and emergency consultation fees
  4. Technical setup check for video consultation
  5. Emergency contact information collection
- Design Problems:
  - HMW minimize wait time while ensuring quality provider matching?
  - HMW handle technical issues during emergency consultations?
  - HMW manage expectations for virtual vs. in-person emergency assessment?
- Design Opportunities:
  - What if we could prioritize based on symptom severity automatically?
  - What if we provided backup communication methods if video fails?
  - What if we offered immediate nurse triage while waiting for doctor availability?

**8.0 Virtual Emergency Consultation [type: primary]**
- HOW IT IS REACHED: Provider accepts consultation request
- NAVBAR PRESENCE: Yes
- Page Goal: Conduct effective virtual emergency assessment and provide care guidance
- Screen Description:
  1. Video consultation interface with recording capabilities
  2. Symptom documentation and photo sharing tools
  3. Real-time prescription and referral generation
  4. Care plan creation with follow-up scheduling
  5. Emergency contact activation if needed
- Design Problems:
  - HMW ensure thorough assessment through virtual consultation?
  - HMW document emergency consultations for continuity of care?
  - HMW handle cases that require immediate in-person evaluation?
- Design Opportunities:
  - What if we could integrate with wearable devices for vital sign monitoring?
  - What if we provided AI-assisted symptom analysis to support provider assessment?
  - What if we offered immediate prescription delivery for urgent medications?

---

## Navigation Structure (Sidebar/Navbar - Primary Screens Only):

1.0 Homepage | 2.0 Assessment Results & Doctor Recommendations | 3.0 Doctor Search & Discovery | 4.0 Patient Dashboard | 5.0 Health Calendar | 6.0 Emergency Care Hub | 7.0 Emergency Care Recommendations | 8.0 Virtual Emergency Consultation

---

## User Journey Flows:

### Scenario 1A Flow:
1.0 Homepage → [click Start Symptom Assessment] → Pu.1 Symptom Assessment Modal → [complete assessment] → 2.0 Assessment Results & Doctor Recommendations → [click doctor card] → 2.0-D Doctor Profile Detail → [click Book Appointment] → Pu.2 Appointment Booking Modal → [submit] → 4.0 Patient Dashboard (confirmation state)

### Scenario 1B Flow:
1.0 Homepage → [click Find a Doctor] → 3.0 Doctor Search & Discovery → [click doctor card] → 3.0-D Doctor Profile Detail → [click Book Appointment] → Pu.2 Appointment Booking Modal → [submit] → 4.0 Patient Dashboard (confirmation state)

### Scenario 2A Flow:
4.0 Patient Dashboard → [scroll to appointments] → 4.1 Appointment Management Section → [click Reschedule] → Pu.3 Reschedule Appointment Modal → [submit] → 4.0 Patient Dashboard (updated state)

### Scenario 2B Flow:
5.0 Health Calendar → [click appointment] → Pu.4 Quick Appointment Actions Modal → [click reschedule] → Pu.3 Reschedule Appointment Modal → [submit] → 5.0 Health Calendar (updated state)

### Scenario 3A Flow:
6.0 Emergency Care Hub → [click Start Emergency Assessment] → Pu.5 Emergency Triage Assessment Modal → [complete assessment] → 7.0 Emergency Care Recommendations → [select care option] → External care facility or return to dashboard

### Scenario 3B Flow:
6.0 Emergency Care Hub → [click Connect Now] → Pu.6 Immediate Consultation Request Modal → [submit] → 8.0 Virtual Emergency Consultation → [complete consultation] → 4.0 Patient Dashboard (with care plan)

---

## Settings Screen

**9.0 Settings [type: settings]**
- HOW IT IS REACHED: Click settings icon in navigation header
- NAVBAR PRESENCE: No
- Page Goal: Centralize all user preferences and accessibility options
- Screen Description:
  1. **Account Settings**: Profile information, password, security
  2. **Notification Preferences**: Appointment reminders, health alerts, communication preferences
  3. **Accessibility Options**: 
     - Screen Reader Support (ON/OFF)
     - High Contrast Mode (ON/OFF)
     - Large Text Mode (ON/OFF)
     - Motor Accessibility (Reduced Motion ON/OFF)
     - Keyboard Navigation (Enhanced Focus ON/OFF)
  4. **Privacy Settings**: Data sharing, family access, insurance information
  5. **Display Preferences**: Theme selection, language, timezone
  6. **Health Data Settings**: Wearable device integration, data export options
- Design Problems:
  - HMW organize extensive settings without overwhelming users?
  - HMW make accessibility options discoverable for users who need them?
  - HMW ensure privacy settings are clear and actionable?
- Design Opportunities:
  - What if we could auto-detect accessibility needs and suggest relevant settings?
  - What if we provided settings recommendations based on user behavior?
  - What if we offered family member settings management for caregivers?

---

## Error and Empty States

**Er.1 No Internet Connection [type: state]**
- Condition of any screen when network connectivity is lost
- Shows offline capabilities and cached information
- Provides clear reconnection guidance

**Er.2 No Search Results [type: state]**
- Condition of 3.0 Doctor Search & Discovery when no doctors match criteria
- Suggests alternative search terms or expanded criteria
- Offers to save search for future notifications

**Er.3 No Appointments Scheduled [type: state]**
- Condition of 4.0 Patient Dashboard when user has no upcoming appointments
- Encourages preventive care scheduling
- Provides quick access to doctor search and symptom assessment

**Er.4 Emergency Services Unavailable [type: state]**
- Condition of 6.0 Emergency Care Hub when virtual emergency services are at capacity
- Provides alternative emergency resources and hotlines
- Offers to queue user for next available provider

---

## ACCESSIBILITY NOTES

**Keyboard Navigation:**
- All primary screens support full keyboard navigation with logical tab order
- Tab order: Header navigation → Main content → Action buttons → Footer
- Skip links provided to main content on all screens
- Modal dialogs trap focus and return to trigger element on close

**ARIA Labels and Landmarks:**
- Header: `role="banner"` with site navigation `role="navigation"`
- Main content: `role="main"` with descriptive headings structure
- Sidebar: `role="complementary"` for secondary navigation
- Forms: Proper labeling with `aria-describedby` for help text and errors
- Status updates: `aria-live="polite"` for appointment confirmations, `aria-live="assertive"` for emergency alerts

**Screen Reader Announcements:**
- Page changes announced with new page title and primary heading
- Form validation errors announced immediately
- Loading states announced with progress indicators
- Emergency triage results announced with urgency level
- Appointment booking confirmations announced with key details

**High Contrast Mode:**
- Toggled via Settings screen (9.0 Settings → Accessibility Options)
- Applies as CSS class `high-contrast` site-wide
- Ensures 7:1 contrast ratio for all text and interactive elements
- Maintains color coding for medical urgency with pattern alternatives

**Focus Indicators:**
- Visible focus rings on all interactive elements (3px solid outline)
- Enhanced focus mode available in settings for motor accessibility
- Focus indicators respect user's system preferences for reduced motion

**Minimum Touch Targets:**
- 44px minimum for all interactive elements across all screens
- Increased spacing in mobile layouts for easier touch interaction
- Swipe gestures available for calendar navigation and appointment management

---

## VIEWPORT BEHAVIOUR

**Desktop (1024px+):**
- Full sidebar navigation with expanded labels
- Multi-column layouts for dashboard and search results
- Hover states for enhanced interactivity
- Picture-in-picture video consultations with full feature set

**Tablet (768px–1023px):**
- Collapsible sidebar navigation with icon + label
- Two-column layouts adapt to single column for complex forms
- Touch-optimized interaction patterns
- Full-screen video consultations with floating controls

**Mobile (320px–767px):**
- Bottom tab navigation replaces sidebar
- Single-column layouts throughout
- Swipe gestures for navigation and management tasks
- Native mobile patterns for date/time selection
- Full-screen modal presentations
- Optimized emergency access with prominent emergency button

**Responsive Breakpoint Considerations:**
- Emergency care access prioritized on mobile with persistent emergency button
- Appointment cards stack vertically on mobile with swipe actions
- Search filters collapse into expandable sections on smaller screens
- Calendar view adapts from monthly to weekly to daily on progressively smaller screens
- Video consultations optimize for portrait orientation on mobile devices

---

*This documentation serves as the foundation for wireframe generation and development implementation, ensuring consistent user experience across all touchpoints while maintaining accessibility and scalability standards.*