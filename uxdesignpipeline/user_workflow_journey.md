# User Workflow Journey Documentation

## Project Overview

This document outlines comprehensive user workflow scenarios for a digital healthcare platform, focusing on patient appointment management and healthcare service discovery. The workflows are designed to balance user needs with business objectives while ensuring accessibility and scalability.

## Experience Context

**Primary User**: Healthcare patients seeking medical services
**Experience Domain**: Healthcare Service Discovery and Appointment Management
**Platform**: Web and mobile responsive application

---

## SCENARIO 1: New Patient Appointment Booking

### User Scenario
Sarah, a 28-year-old working professional, has been experiencing persistent headaches for the past week. She's new to the city and doesn't have a regular doctor. She needs to find a qualified healthcare provider and book an appointment as quickly as possible while fitting her work schedule.

### User Goal
Find and book an appointment with a qualified healthcare provider that matches her symptoms, location preferences, and schedule availability.

### Business Goal
Convert new users into active patients while capturing comprehensive user data for personalized healthcare recommendations and building long-term patient relationships.

### Workflow Design Variation A: Symptom-First Approach

#### Screen Flow:

**1.0 Homepage [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item
- NAVBAR PRESENCE: Yes
- Page Goal: Build credibility and guide users toward appropriate healthcare services
- Screen Description:
  1. Hero section with symptom checker CTA
  2. Quick doctor search functionality
  3. Featured healthcare services and specialties
  4. Patient testimonials and trust indicators
  5. Emergency care contact information
- Design Problems:
  - HMW help new patients quickly identify the right type of healthcare provider?
  - HMW build immediate trust with users who are unfamiliar with the platform?
  - HMW balance comprehensive service offerings without overwhelming new users?
- Design Opportunities:
  - What if we could provide AI-powered symptom assessment to guide users?
  - What if we displayed real-time availability for immediate appointments?
  - What if we showed location-based provider recommendations?

**Pu.1 Symptom Assessment Modal [type: modal]**
- HOW IT IS REACHED: Click "Check Symptoms" CTA button on Homepage
- NAVBAR PRESENCE: No
- Page Goal: Gather symptom information to recommend appropriate healthcare providers
- Screen Description:
  1. Progressive symptom questionnaire with visual body map
  2. Severity assessment scale
  3. Duration and frequency inputs
  4. Previous medical history checkboxes
  5. Urgency level determination
- Design Problems:
  - HMW make medical questionnaires feel approachable and non-intimidating?
  - HMW ensure accurate symptom capture without overwhelming users?
  - HMW handle users who are unsure about their symptoms?
- Design Opportunities:
  - What if we used conversational UI to make the assessment feel more natural?
  - What if we provided educational content about symptoms during assessment?
  - What if we could detect urgency levels and fast-track emergency cases?

**2.0 Provider Search Results [type: primary]**
- HOW IT IS REACHED: Complete symptom assessment or direct navigation via sidebar
- NAVBAR PRESENCE: Yes
- Page Goal: Present relevant healthcare providers based on user needs and preferences
- Screen Description:
  1. Filtered list of recommended providers based on symptoms
  2. Provider profiles with specialties, ratings, and availability
  3. Map view showing provider locations
  4. Filter options (insurance, location, availability, rating)
  5. Sort options (distance, rating, earliest availability)
- Design Problems:
  - HMW help users compare providers effectively without information overload?
  - HMW display complex scheduling information clearly?
  - HMW handle cases where no providers match user criteria?
- Design Opportunities:
  - What if we could show real-time availability calendars?
  - What if we provided video introductions from healthcare providers?
  - What if we displayed patient reviews with verified appointment history?

**2.0-D Provider Profile Detail [type: detail]**
- HOW IT IS REACHED: Click on provider card from Provider Search Results
- NAVBAR PRESENCE: No
- Page Goal: Provide comprehensive provider information to support booking decision
- Screen Description:
  1. Provider photo, credentials, and specializations
  2. Detailed availability calendar
  3. Patient reviews and ratings breakdown
  4. Office location and contact information
  5. Insurance acceptance and pricing information
  6. "Book Appointment" CTA prominently displayed
- Design Problems:
  - HMW present complex provider information in a scannable format?
  - HMW help users understand insurance coverage and costs upfront?
  - HMW build confidence in provider selection?
- Design Opportunities:
  - What if we showed provider response times and communication preferences?
  - What if we displayed before/after patient success stories?
  - What if we provided virtual consultation options?

**Pu.2 Appointment Booking Modal [type: modal]**
- HOW IT IS REACHED: Click "Book Appointment" CTA button on Provider Profile Detail
- NAVBAR PRESENCE: No
- Page Goal: Capture appointment details and patient information efficiently
- Screen Description:
  1. Available time slot selection calendar
  2. Appointment type selection (in-person, telehealth)
  3. Patient information form (new patient registration)
  4. Insurance information capture
  5. Reason for visit and symptom summary
  6. Confirmation and payment processing
- Design Problems:
  - HMW minimize form abandonment during the booking process?
  - HMW handle insurance verification without delaying booking?
  - HMW accommodate users booking for family members?
- Design Opportunities:
  - What if we could pre-fill information from symptom assessment?
  - What if we provided estimated wait times and preparation instructions?
  - What if we offered flexible payment options and cost transparency?

**3.0 Appointment Confirmation [type: primary]**
- HOW IT IS REACHED: Complete appointment booking process
- NAVBAR PRESENCE: Yes
- Page Goal: Confirm appointment details and provide next steps
- Screen Description:
  1. Appointment confirmation details (date, time, provider, location)
  2. Calendar integration options
  3. Pre-appointment preparation checklist
  4. Contact information for changes or questions
  5. Navigation to patient dashboard
- Design Problems:
  - HMW ensure users have all necessary information for their appointment?
  - HMW reduce no-show rates through effective preparation?
  - HMW handle appointment changes and cancellations gracefully?
- Design Opportunities:
  - What if we sent personalized preparation reminders?
  - What if we provided virtual waiting room access?
  - What if we offered pre-appointment health questionnaires?

#### Navigation Structure (Sidebar/Navbar):
1.0 Homepage | 2.0 Provider Search | 3.0 Appointment Confirmation

#### User Journey Flow:
1.0 Homepage → [click Check Symptoms] → Pu.1 Symptom Assessment Modal → [complete assessment] → 2.0 Provider Search Results → [click provider card] → 2.0-D Provider Profile Detail → [click Book Appointment] → Pu.2 Appointment Booking Modal → [submit booking] → 3.0 Appointment Confirmation

### Workflow Design Variation B: Direct Search Approach

#### Screen Flow:

**1.0 Homepage [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item
- NAVBAR PRESENCE: Yes
- Page Goal: Provide immediate access to healthcare provider search
- Screen Description:
  1. Prominent search bar for provider/specialty/condition
  2. Quick access buttons for common specialties
  3. Location-based provider suggestions
  4. Featured healthcare packages and services
  5. Emergency contact and urgent care options
- Design Problems:
  - HMW help users who don't know what type of specialist they need?
  - HMW handle broad search queries effectively?
  - HMW provide guidance without forcing a specific path?
- Design Opportunities:
  - What if we provided search suggestions based on common conditions?
  - What if we showed trending health topics in the user's area?
  - What if we offered voice search for accessibility?

**2.0 Advanced Search [type: primary]**
- HOW IT IS REACHED: Click "Advanced Search" from Homepage or direct navigation
- NAVBAR PRESENCE: Yes
- Page Goal: Enable detailed provider filtering based on specific criteria
- Screen Description:
  1. Comprehensive filter panel (specialty, location, insurance, availability)
  2. Map-based provider selection
  3. Advanced criteria (gender preference, languages spoken, years of experience)
  4. Saved search functionality
  5. Search results with sorting options
- Design Problems:
  - HMW prevent filter overload while providing comprehensive options?
  - HMW handle cases where filters produce no results?
  - HMW make complex search criteria accessible to all users?
- Design Opportunities:
  - What if we could save and share search preferences?
  - What if we provided search result notifications for new providers?
  - What if we offered collaborative search for family healthcare planning?

**2.1 Search Results List [type: primary]**
- HOW IT IS REACHED: Execute search from Advanced Search screen
- NAVBAR PRESENCE: Yes
- Page Goal: Display comprehensive provider results with comparison capabilities
- Screen Description:
  1. Provider cards with key information and quick actions
  2. Side-by-side comparison functionality
  3. Availability preview and quick booking options
  4. Filter refinement panel
  5. Results pagination and sorting controls
- Design Problems:
  - HMW enable effective provider comparison without cognitive overload?
  - HMW display availability information clearly across multiple providers?
  - HMW handle large result sets efficiently?
- Design Opportunities:
  - What if we provided AI-powered provider matching scores?
  - What if we showed provider availability trends and best booking times?
  - What if we offered group booking for family appointments?

#### Navigation Structure (Sidebar/Navbar):
1.0 Homepage | 2.0 Advanced Search | 2.1 Search Results

#### User Journey Flow:
1.0 Homepage → [click Advanced Search] → 2.0 Advanced Search → [execute search] → 2.1 Search Results List → [click provider] → 2.0-D Provider Profile Detail → [book appointment] → Pu.2 Appointment Booking Modal → [confirm] → 3.0 Appointment Confirmation

---

## SCENARIO 2: Existing Patient Appointment Management

### User Scenario
Michael, a 45-year-old patient with diabetes, has been using the platform for 6 months. He needs to reschedule his upcoming endocrinologist appointment due to a work conflict and wants to review his appointment history to prepare for his next visit.

### User Goal
Efficiently manage existing appointments, access appointment history, and maintain continuity of care with preferred healthcare providers.

### Business Goal
Increase patient retention and engagement while reducing administrative overhead through self-service appointment management capabilities.

### Workflow Design Variation A: Dashboard-Centric Approach

#### Screen Flow:

**4.0 Patient Dashboard [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item (logged-in users)
- NAVBAR PRESENCE: Yes
- Page Goal: Provide comprehensive overview of patient's healthcare activity and upcoming needs
- Screen Description:
  1. Upcoming appointments with quick action buttons
  2. Recent appointment history and visit summaries
  3. Health metrics tracking and trends
  4. Prescription refill reminders
  5. Recommended preventive care based on patient history
  6. Quick access to frequently used features
- Design Problems:
  - HMW prioritize information for patients with multiple ongoing conditions?
  - HMW make complex medical information accessible and actionable?
  - HMW balance comprehensive information with quick task completion?
- Design Opportunities:
  - What if we provided personalized health insights based on appointment patterns?
  - What if we could predict and suggest optimal appointment scheduling?
  - What if we integrated with wearable devices for comprehensive health tracking?

**4.1 Appointment Management [type: primary]**
- HOW IT IS REACHED: Click "Manage Appointments" from Patient Dashboard or direct navigation
- NAVBAR PRESENCE: Yes
- Page Goal: Enable comprehensive appointment scheduling, rescheduling, and cancellation
- Screen Description:
  1. Calendar view of all scheduled appointments
  2. Appointment cards with provider information and quick actions
  3. Bulk rescheduling options for recurring appointments
  4. Appointment history with visit notes and outcomes
  5. Waitlist signup for preferred time slots
- Design Problems:
  - HMW handle complex rescheduling scenarios with multiple providers?
  - HMW prevent appointment conflicts and double-booking?
  - HMW accommodate patients with mobility or scheduling constraints?
- Design Opportunities:
  - What if we could automatically suggest optimal rescheduling options?
  - What if we provided appointment preparation reminders based on visit type?
  - What if we offered family calendar integration for coordinated care?

**Pu.3 Reschedule Appointment Modal [type: modal]**
- HOW IT IS REACHED: Click "Reschedule" button on appointment card
- NAVBAR PRESENCE: No
- Page Goal: Enable quick and efficient appointment rescheduling with minimal friction
- Screen Description:
  1. Current appointment details for reference
  2. Available alternative time slots with provider
  3. Conflict detection with existing appointments
  4. Reason for rescheduling (optional feedback)
  5. Confirmation and notification preferences
- Design Problems:
  - HMW minimize the steps required for rescheduling?
  - HMW handle cases where no alternative slots are available?
  - HMW communicate rescheduling policies and potential fees?
- Design Opportunities:
  - What if we could offer automatic rescheduling based on patient preferences?
  - What if we provided waitlist options for preferred times?
  - What if we offered telehealth alternatives when in-person slots are limited?

**4.0-D Appointment History Detail [type: detail]**
- HOW IT IS REACHED: Click on past appointment from Appointment Management or Dashboard
- NAVBAR PRESENCE: No
- Page Goal: Provide comprehensive appointment history and visit documentation
- Screen Description:
  1. Visit summary and provider notes
  2. Prescribed medications and treatment plans
  3. Follow-up recommendations and next steps
  4. Test results and lab reports
  5. Billing and insurance information
  6. Option to schedule follow-up appointments
- Design Problems:
  - HMW make complex medical information understandable to patients?
  - HMW ensure patient privacy while enabling information sharing?
  - HMW help patients track progress across multiple visits?
- Design Opportunities:
  - What if we provided visual progress tracking for chronic conditions?
  - What if we offered easy sharing options for family members or caregivers?
  - What if we integrated with personal health record systems?

#### Navigation Structure (Sidebar/Navbar):
4.0 Patient Dashboard | 4.1 Appointment Management

#### User Journey Flow:
4.0 Patient Dashboard → [click Manage Appointments] → 4.1 Appointment Management → [click Reschedule] → Pu.3 Reschedule Appointment Modal → [confirm new time] → 4.1 Appointment Management (updated) → [click past appointment] → 4.0-D Appointment History Detail

### Workflow Design Variation B: Calendar-First Approach

#### Screen Flow:

**5.0 Healthcare Calendar [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item
- NAVBAR PRESENCE: Yes
- Page Goal: Provide visual calendar interface for comprehensive appointment management
- Screen Description:
  1. Monthly/weekly calendar view with appointment blocks
  2. Color-coded appointments by provider or specialty
  3. Drag-and-drop rescheduling functionality
  4. Integration with personal calendar systems
  5. Appointment conflict detection and resolution
- Design Problems:
  - HMW make calendar interfaces accessible to users with visual impairments?
  - HMW handle complex recurring appointment patterns?
  - HMW prevent accidental appointment modifications?
- Design Opportunities:
  - What if we could sync with work calendars to suggest optimal appointment times?
  - What if we provided weather-based rescheduling suggestions for mobility-impaired patients?
  - What if we offered family calendar coordination for dependent care?

**5.1 Calendar Day View [type: primary]**
- HOW IT IS REACHED: Click on specific date from Healthcare Calendar
- NAVBAR PRESENCE: Yes
- Page Goal: Provide detailed daily schedule with appointment preparation information
- Screen Description:
  1. Detailed daily appointment schedule
  2. Appointment preparation checklists
  3. Travel time and directions to appointments
  4. Pre-appointment forms and questionnaires
  5. Provider contact information and office details
- Design Problems:
  - HMW help patients prepare effectively for multiple appointments in one day?
  - HMW handle transportation and logistics planning?
  - HMW accommodate patients with complex medical needs?
- Design Opportunities:
  - What if we provided personalized preparation timelines?
  - What if we offered ride-sharing integration for transportation?
  - What if we could coordinate care between multiple providers on the same day?

#### Navigation Structure (Sidebar/Navbar):
5.0 Healthcare Calendar | 5.1 Calendar Day View

#### User Journey Flow:
5.0 Healthcare Calendar → [click date] → 5.1 Calendar Day View → [drag appointment to new time] → Pu.3 Reschedule Confirmation Modal → [confirm] → 5.0 Healthcare Calendar (updated)

---

## SCENARIO 3: Emergency Care Access

### User Scenario
Jessica, a 32-year-old mother, notices her 5-year-old child has developed a high fever and rash late in the evening. She needs to quickly determine if this requires immediate emergency care or if it can wait until morning, and find the nearest appropriate care facility.

### User Goal
Quickly assess the urgency of a medical situation and access appropriate emergency or urgent care services with minimal delay.

### Business Goal
Provide reliable emergency care guidance while directing patients to appropriate care levels, reducing unnecessary emergency room visits and improving patient outcomes.

### Workflow Design Variation A: Triage-First Approach

#### Screen Flow:

**6.0 Emergency Care Hub [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item or emergency banner
- NAVBAR PRESENCE: Yes
- Page Goal: Provide immediate access to emergency care assessment and resources
- Screen Description:
  1. Prominent "Emergency Assessment" CTA for symptom triage
  2. Direct emergency services contact (911) with clear visibility
  3. Nearest emergency rooms and urgent care centers with wait times
  4. Common emergency symptoms and when to seek immediate care
  5. Poison control and crisis hotline information
- Design Problems:
  - HMW ensure users in genuine emergencies don't waste time with assessments?
  - HMW provide accurate triage without replacing professional medical judgment?
  - HMW handle users who are panicked or stressed?
- Design Opportunities:
  - What if we could provide real-time emergency room wait times?
  - What if we offered video triage with emergency nurses?
  - What if we could automatically contact emergency services when needed?

**Pu.4 Emergency Triage Assessment [type: modal]**
- HOW IT IS REACHED: Click "Emergency Assessment" CTA from Emergency Care Hub
- NAVBAR PRESENCE: No
- Page Goal: Quickly assess symptom severity and recommend appropriate care level
- Screen Description:
  1. Rapid symptom assessment with yes/no questions
  2. Age and medical history considerations
  3. Severity indicators and red flag symptoms
  4. Immediate care recommendations based on assessment
  5. Option to call emergency services directly from assessment
- Design Problems:
  - HMW ensure assessment is fast enough for genuine emergencies?
  - HMW handle liability concerns with medical triage recommendations?
  - HMW accommodate users with limited health literacy?
- Design Opportunities:
  - What if we could use voice input for hands-free assessment?
  - What if we provided multilingual support for emergency situations?
  - What if we could connect users directly with emergency medical professionals?

**6.1 Emergency Care Locations [type: primary]**
- HOW IT IS REACHED: Complete emergency assessment or direct navigation
- NAVBAR PRESENCE: Yes
- Page Goal: Help users find and navigate to appropriate emergency care facilities
- Screen Description:
  1. Map view of nearby emergency rooms and urgent care centers
  2. Real-time wait times and capacity information
  3. Facility specializations and capabilities
  4. Directions and transportation options
  5. Insurance acceptance and cost estimates
- Design Problems:
  - HMW provide accurate wait time information during high-demand periods?
  - HMW help users choose between emergency room and urgent care?
  - HMW accommodate users without transportation?
- Design Opportunities:
  - What if we could reserve spots in urgent care facilities?
  - What if we provided ambulance service integration?
  - What if we offered telemedicine emergency consultations?

#### Navigation Structure (Sidebar/Navbar):
6.0 Emergency Care Hub | 6.1 Emergency Care Locations

#### User Journey Flow:
6.0 Emergency Care Hub → [click Emergency Assessment] → Pu.4 Emergency Triage Assessment → [complete assessment] → 6.1 Emergency Care Locations → [select facility] → [external navigation/contact]

### Workflow Design Variation B: Location-First Approach

#### Screen Flow:

**7.0 Immediate Care Finder [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item
- NAVBAR PRESENCE: Yes
- Page Goal: Quickly locate nearest emergency and urgent care facilities
- Screen Description:
  1. Current location-based facility map
  2. Filter options for facility type and specializations
  3. One-tap calling and navigation to facilities
  4. Real-time availability and wait time indicators
  5. Emergency services direct contact integration
- Design Problems:
  - HMW ensure location accuracy in emergency situations?
  - HMW handle users who are traveling or in unfamiliar areas?
  - HMW provide reliable facility information during emergencies?
- Design Opportunities:
  - What if we could automatically detect user location and show nearest facilities?
  - What if we provided offline access to emergency facility information?
  - What if we could coordinate with emergency services for optimal facility routing?

#### Navigation Structure (Sidebar/Navbar):
7.0 Immediate Care Finder

#### User Journey Flow:
7.0 Immediate Care Finder → [select facility] → [call or navigate] → [external emergency care process]

---

## SCENARIO 4: Health Information Management

### User Scenario
Robert, a 55-year-old patient with multiple chronic conditions, needs to prepare for an upcoming specialist appointment. He wants to review his medical history, current medications, and recent test results to ensure he provides complete information to his new healthcare provider.

### User Goal
Access, organize, and share comprehensive health information to support informed healthcare decisions and effective provider communication.

### Business Goal
Improve care coordination and patient outcomes while reducing administrative burden on healthcare providers through comprehensive health information management.

### Workflow Design Variation A: Comprehensive Health Record

#### Screen Flow:

**8.0 Health Records Dashboard [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item
- NAVBAR PRESENCE: Yes
- Page Goal: Provide centralized access to all health information and records
- Screen Description:
  1. Health summary with key metrics and recent changes
  2. Quick access to medications, allergies, and conditions
  3. Recent test results and lab reports
  4. Upcoming appointments and care plan items
  5. Health document upload and organization tools
- Design Problems:
  - HMW organize complex medical information for easy comprehension?
  - HMW ensure health information privacy and security?
  - HMW accommodate users with varying levels of health literacy?
- Design Opportunities:
  - What if we could provide visual health trend analysis?
  - What if we offered AI-powered health insights and recommendations?
  - What if we could automatically import data from multiple healthcare providers?

**8.1 Medical History [type: primary]**
- HOW IT IS REACHED: Click "Medical History" from Health Records Dashboard or direct navigation
- NAVBAR PRESENCE: Yes
- Page Goal: Provide comprehensive chronological medical history with search and filter capabilities
- Screen Description:
  1. Timeline view of medical events and treatments
  2. Condition-based organization with severity tracking
  3. Provider notes and visit summaries
  4. Medication history and changes over time
  5. Family medical history integration
- Design Problems:
  - HMW present long medical histories in a digestible format?
  - HMW help users identify relevant information for specific appointments?
  - HMW handle conflicting or incomplete medical records?
- Design Opportunities:
  - What if we could generate automated health summaries for provider visits?
  - What if we provided condition-specific health education resources?
  - What if we could identify potential drug interactions or health risks?

**8.1-D Condition Detail View [type: detail]**
- HOW IT IS REACHED: Click on specific condition from Medical History
- NAVBAR PRESENCE: No
- Page Goal: Provide detailed information about specific health conditions and their management
- Screen Description:
  1. Condition overview with current status and severity
  2. Treatment history and medication changes
  3. Related appointments and provider notes
  4. Symptom tracking and progress indicators
  5. Educational resources and management tips
- Design Problems:
  - HMW help patients understand complex medical conditions?
  - HMW track condition progression over time?
  - HMW support patient self-advocacy in healthcare discussions?
- Design Opportunities:
  - What if we provided peer support connections for similar conditions?
  - What if we offered personalized condition management plans?
  - What if we could predict condition flare-ups based on historical data?

**8.2 Test Results [type: primary]**
- HOW IT IS REACHED: Click "Test Results" from Health Records Dashboard or direct navigation
- NAVBAR PRESENCE: Yes
- Page Goal: Organize and present laboratory and diagnostic test results with trend analysis
- Screen Description:
  1. Chronological list of all test results with normal/abnormal indicators
  2. Trend charts for key health metrics
  3. Provider interpretations and follow-up recommendations
  4. Comparison tools for tracking changes over time
  5. Integration with health monitoring devices
- Design Problems:
  - HMW make complex lab results understandable to patients?
  - HMW highlight concerning trends without causing unnecessary anxiety?
  - HMW help patients prepare questions for provider discussions?
- Design Opportunities:
  - What if we provided personalized explanations of test results?
  - What if we could predict health risks based on test result trends?
  - What if we offered automated sharing of results with healthcare providers?

**Pu.5 Share Health Information Modal [type: modal]**
- HOW IT IS REACHED: Click "Share" button from any health record screen
- NAVBAR PRESENCE: No
- Page Goal: Enable secure sharing of health information with healthcare providers or family members
- Screen Description:
  1. Selection of specific information to share
  2. Recipient selection (providers, family members, caregivers)
  3. Access duration and permission settings
  4. Secure sharing method options (portal, email, print)
  5. Sharing history and access logs
- Design Problems:
  - HMW ensure health information privacy while enabling necessary sharing?
  - HMW make sharing permissions clear and manageable?
  - HMW accommodate users with limited technical skills?
- Design Opportunities:
  - What if we could automatically share relevant information before appointments?
  - What if we provided emergency access options for critical health information?
  - What if we offered family caregiver coordination tools?

#### Navigation Structure (Sidebar/Navbar):
8.0 Health Records Dashboard | 8.1 Medical History | 8.2 Test Results

#### User Journey Flow:
8.0 Health Records Dashboard → [click Medical History] → 8.1 Medical History → [click condition] → 8.1-D Condition Detail View → [click Share] → Pu.5 Share Health Information Modal → [configure sharing] → 8.1 Medical History (updated)

### Workflow Design Variation B: Appointment-Focused Preparation

#### Screen Flow:

**9.0 Appointment Preparation [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item or from appointment reminders
- NAVBAR PRESENCE: Yes
- Page Goal: Help patients prepare comprehensive information for upcoming healthcare appointments
- Screen Description:
  1. Upcoming appointment details with preparation checklist
  2. Relevant health information summary for specific appointment type
  3. Questions to ask provider based on health history
  4. Symptom tracking tools for current concerns
  5. Insurance and administrative preparation items
- Design Problems:
  - HMW help patients prepare effectively without overwhelming them?
  - HMW customize preparation based on appointment type and provider?
  - HMW ensure patients bring all necessary information and questions?
- Design Opportunities:
  - What if we could generate personalized preparation checklists?
  - What if we provided appointment-specific health education?
  - What if we could coordinate preparation across multiple providers?

#### Navigation Structure (Sidebar/Navbar):
9.0 Appointment Preparation

#### User Journey Flow:
9.0 Appointment Preparation → [complete preparation checklist] → [external appointment attendance]

---

## UNIVERSAL SCREENS

**Settings Screen [type: settings]**
- HOW IT IS REACHED: Click settings icon/link from any screen
- NAVBAR PRESENCE: No
- Page Goal: Centralize all user preferences and account management options
- Screen Description:
  1. **Account Settings**: Profile information, contact details, emergency contacts
  2. **Privacy Settings**: Data sharing preferences, communication consent, marketing opt-outs
  3. **Notification Preferences**: Appointment reminders, health alerts, promotional messages
  4. **Accessibility Options**: Screen reader support toggle, high contrast mode toggle, font size preferences, motor accessibility options
  5. **Display Preferences**: Language selection, timezone settings, calendar view preferences
  6. **Security Settings**: Password management, two-factor authentication, login history
  7. **Integration Settings**: Calendar sync, health device connections, insurance information
- Design Problems:
  - HMW organize numerous settings without overwhelming users?
  - HMW make accessibility options discoverable and easy to use?
  - HMW ensure privacy settings are clear and comprehensive?
- Design Opportunities:
  - What if we could provide smart defaults based on user behavior?
  - What if we offered guided setup for complex integrations?
  - What if we provided privacy impact explanations for each setting?

**Error States [type: state]**

**Er.1 Network Connection Error**
- Condition of existing screens when network connectivity is lost
- Provides offline functionality where possible and clear reconnection guidance

**Er.2 Appointment Booking Error**
- Condition when appointment booking fails due to system issues or conflicts
- Offers alternative booking methods and provider contact information

**Er.3 Health Information Access Error**
- Condition when health records cannot be retrieved
- Provides alternative access methods and support contact information

**Er.4 Search No Results**
- Condition when provider or appointment searches return no results
- Offers search refinement suggestions and alternative search strategies

---

## ACCESSIBILITY NOTES

Accessibility requirements apply across all primary screens and interactions:

**Keyboard Navigation:**
- All interactive elements accessible via keyboard with logical tab order
- Skip links provided for main content areas on all primary screens
- Keyboard shortcuts for common actions (search, book appointment, emergency access)
- Focus indicators clearly visible with 3:1 contrast ratio minimum

**ARIA Labels and Landmarks:**
- Main navigation marked with navigation landmark
- Content areas marked with main landmark
- Search functionality marked with search landmark
- Form sections properly labeled with fieldset and legend elements
- Dynamic content changes announced to screen readers

**Screen Reader Announcements:**
- Page title changes announced when navigating between screens
- Form validation errors announced immediately
- Appointment booking confirmations announced
- Emergency triage results announced with appropriate urgency
- Loading states and progress indicators announced

**High Contrast Mode:**
- Toggled via Settings screen under Accessibility Options
- Applies as CSS class site-wide affecting all screens
- Maintains 7:1 contrast ratio for text and 3:1 for interactive elements
- Preserves all functionality while improving visual accessibility

**Motor Accessibility:**
- Minimum touch targets of 44px on all interactive elements
- Drag-and-drop functionality includes keyboard alternatives
- Time-sensitive actions (emergency triage) include extension options
- Voice input support for search and form completion

**Cognitive Accessibility:**
- Clear, consistent navigation patterns across all screens
- Progress indicators for multi-step processes
- Plain language used throughout with medical term explanations
- Error messages provide clear correction guidance
- Timeout warnings with extension options for complex forms

---

## VIEWPORT BEHAVIOUR

Viewport adaptations apply to all primary screens:

**Desktop (1024px+):**
- Full sidebar navigation with expanded menu items
- Multi-column layouts for dashboard and search results
- Hover states for interactive elements
- Modal dialogs centered with backdrop overlay
- Calendar views show full month with detailed appointment information

**Tablet (768px–1023px):**
- Collapsible sidebar navigation with icon + text labels
- Two-column layouts where appropriate, single column for complex forms
- Touch-optimized interactive elements with increased spacing
- Modal dialogs adapt to available screen space
- Calendar views show week view as default with month option

**Mobile (320px–767px):**
- Bottom navigation bar replaces sidebar for primary screens
- Single-column layouts throughout
- Full-screen modal dialogs and forms
- Swipe gestures for calendar navigation and appointment management
- Simplified search interfaces with progressive disclosure
- Emergency access prominently featured in navigation

**Responsive Breakpoint Considerations:**
- Provider search results adapt from grid to list view on smaller screens
- Appointment booking forms use progressive disclosure on mobile
- Health record timelines switch to vertical card layout on mobile
- Emergency triage assessment optimized for one-handed mobile use
- Settings screen uses accordion layout on mobile devices

---

## CONCLUSION

This comprehensive user workflow documentation provides systematic, user-centered design approaches that balance patient needs with healthcare business objectives. The scenarios cover the complete patient journey from initial healthcare discovery through ongoing care management, with particular attention to accessibility and emergency care access.

Each workflow variation offers different approaches to common healthcare tasks, allowing for user preference accommodation and A/B testing opportunities. The detailed screen specifications provide clear guidance for wireframe generation while maintaining focus on user goals and business outcomes.

The accessibility and viewport behavior specifications ensure that all users can effectively access healthcare services regardless of their abilities or device preferences, supporting the platform's goal of inclusive healthcare access.