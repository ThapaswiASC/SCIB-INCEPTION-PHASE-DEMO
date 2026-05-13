# User Workflow Journey Documentation

## Experience Overview

**User Type:** Healthcare Patient  
**Experience Context:** Digital Healthcare Platform - Appointment Management and Medical Care Access

---

## Scenario 1: New Patient Booking First Appointment

### User Scenario
Sarah, a 28-year-old working professional, has been experiencing persistent headaches for the past week. She's new to the city and doesn't have a regular doctor. She needs to find a qualified healthcare provider and book an appointment quickly while managing her busy work schedule.

### User Goal
Successfully find and book an appointment with an appropriate healthcare provider within the next 2-3 days, with minimal friction and clear communication about the process.

### Business Goal
Convert new users into registered patients while capturing essential medical information and establishing trust in the platform's healthcare network.

### Workflow Design Variation 1: Guided Discovery Flow

#### Screen Breakdown:

**1.0 Homepage [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item
- NAVBAR PRESENCE: Yes
- Page Goal: Build credibility and guide new users toward appropriate healthcare solutions
- Screen Description:
  1. Hero section with symptom checker CTA
  2. Search functionality for doctors by specialty or condition
  3. Featured healthcare providers with ratings and availability
  4. Educational content about common health conditions
  5. Patient testimonials and platform benefits
  6. Quick access to emergency care information
- Design Problems:
  - HMW help new patients understand the platform's value proposition immediately?
  - HMW reduce anxiety for first-time healthcare seekers?
  - HMW balance comprehensive options with simplicity?
- Design Opportunities:
  - What if we could provide personalized healthcare recommendations based on symptoms?
  - What if we offered virtual consultations for non-urgent cases?
  - What if we integrated health insurance verification upfront?

**Pu.1 Symptom Assessment Modal [type: modal]**
- HOW IT IS REACHED: Clicking "Check Your Symptoms" CTA button on Homepage
- NAVBAR PRESENCE: No
- Page Goal: Gather symptom information to recommend appropriate care level and specialists
- Screen Description:
  1. Progressive symptom questionnaire with visual aids
  2. Severity assessment with clear indicators
  3. Duration and frequency tracking
  4. Recommendation engine for care urgency
  5. Specialist matching based on symptoms
- Design Problems:
  - HMW make medical questionnaires feel approachable and not intimidating?
  - HMW ensure accuracy without requiring medical expertise from users?
  - HMW provide helpful recommendations without diagnosing?
- Design Opportunities:
  - What if we used AI to ask follow-up questions based on initial responses?
  - What if we provided educational content about symptoms during assessment?
  - What if we offered immediate virtual triage for urgent cases?

**2.0 Doctor Search & Discovery [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item or from symptom assessment results
- NAVBAR PRESENCE: Yes
- Page Goal: Help users find and evaluate healthcare providers based on their specific needs
- Screen Description:
  1. Advanced search filters (specialty, location, availability, insurance)
  2. Doctor profile cards with key information and ratings
  3. Map view showing nearby providers
  4. Availability calendar integration
  5. Patient review summaries
  6. Insurance compatibility indicators
- Design Problems:
  - HMW help patients choose between multiple qualified providers?
  - HMW display complex scheduling information clearly?
  - HMW build trust in provider credentials and reviews?
- Design Opportunities:
  - What if we could show real-time availability across multiple providers?
  - What if we offered provider matching based on patient preferences and history?
  - What if we integrated video introductions from healthcare providers?

**2.0-D Doctor Profile Detail [type: detail]**
- HOW IT IS REACHED: Clicking on a doctor card from Doctor Search & Discovery screen
- NAVBAR PRESENCE: No
- Page Goal: Provide comprehensive information to help patients make informed provider selection
- Screen Description:
  1. Detailed provider biography and credentials
  2. Specializations and treatment approaches
  3. Patient reviews and ratings breakdown
  4. Available appointment slots calendar
  5. Office location and contact information
  6. Insurance and payment options
  7. Virtual consultation availability
- Design Problems:
  - HMW present extensive provider information without overwhelming users?
  - HMW help patients understand provider qualifications and approach?
  - HMW make scheduling feel seamless from the profile page?
- Design Opportunities:
  - What if we showed provider response times and communication style?
  - What if we offered virtual meet-and-greets before booking?
  - What if we displayed similar patient success stories?

**Pu.2 Appointment Booking Modal [type: modal]**
- HOW IT IS REACHED: Clicking "Book Appointment" CTA button on Doctor Profile Detail screen
- NAVBAR PRESENCE: No
- Page Goal: Capture appointment details and patient information efficiently
- Screen Description:
  1. Available time slot selection with duration indicators
  2. Appointment type selection (in-person, virtual, consultation)
  3. Reason for visit and symptom summary
  4. Patient information form (new patient registration)
  5. Insurance verification and payment setup
  6. Confirmation and calendar integration options
- Design Problems:
  - HMW minimize form abandonment during the booking process?
  - HMW collect necessary medical information without feeling invasive?
  - HMW handle insurance verification smoothly?
- Design Opportunities:
  - What if we could pre-populate information from symptom assessment?
  - What if we offered flexible payment options including payment plans?
  - What if we provided appointment preparation guidance?

**3.0 Appointment Confirmation [type: primary]**
- HOW IT IS REACHED: Direct navigation after successful booking or via sidebar nav item
- NAVBAR PRESENCE: Yes
- Page Goal: Confirm appointment details and provide pre-visit preparation information
- Screen Description:
  1. Appointment summary with all key details
  2. Provider contact information and office directions
  3. Pre-visit checklist and preparation instructions
  4. Document upload area for medical records
  5. Appointment modification options (reschedule/cancel)
  6. Calendar integration and reminder setup
- Design Problems:
  - HMW ensure patients have all necessary information for their visit?
  - HMW reduce no-shows through effective preparation?
  - HMW make appointment changes easy when needed?
- Design Opportunities:
  - What if we sent personalized preparation reminders based on appointment type?
  - What if we offered virtual check-in to reduce waiting room time?
  - What if we provided educational content relevant to the patient's condition?

#### Navigation Structure:
1.0 Homepage | 2.0 Doctor Search & Discovery | 3.0 Appointment Confirmation

#### User Journey Flow:
1.0 Homepage → [click "Check Your Symptoms"] → Pu.1 Symptom Assessment Modal [type: modal] → [complete assessment] → 2.0 Doctor Search & Discovery → [click doctor card] → 2.0-D Doctor Profile Detail [type: detail] → [click "Book Appointment"] → Pu.2 Appointment Booking Modal [type: modal] → [submit booking] → 3.0 Appointment Confirmation

### Workflow Design Variation 2: Direct Search Flow

#### Screen Breakdown:

**1.0 Homepage [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item
- NAVBAR PRESENCE: Yes
- Page Goal: Provide immediate access to healthcare provider search for users who know what they need
- Screen Description:
  1. Prominent search bar for provider/specialty lookup
  2. Quick category buttons for common specialties
  3. Location-based provider suggestions
  4. Featured urgent care and emergency options
  5. Platform benefits and trust indicators
  6. Recent patient success stories
- Design Problems:
  - HMW accommodate both guided and direct search preferences?
  - HMW ensure users don't miss important platform features?
  - HMW provide quick access without sacrificing thoroughness?
- Design Opportunities:
  - What if we offered smart search suggestions based on location and common needs?
  - What if we provided specialty education for users unsure of their needs?
  - What if we integrated insurance network filtering in the main search?

**2.0 Search Results [type: primary]**
- HOW IT IS REACHED: Direct navigation via search from Homepage or sidebar nav item
- NAVBAR PRESENCE: Yes
- Page Goal: Display relevant healthcare providers with key decision-making information
- Screen Description:
  1. Provider results with availability indicators
  2. Advanced filtering sidebar (insurance, rating, distance)
  3. Sort options (availability, rating, distance, price)
  4. Map toggle for geographic visualization
  5. Quick booking CTAs for immediate availability
  6. Comparison tool for multiple providers
- Design Problems:
  - HMW help users quickly identify the best match from multiple options?
  - HMW display complex provider information in scannable format?
  - HMW balance comprehensive information with quick decision-making?
- Design Opportunities:
  - What if we highlighted providers with immediate availability?
  - What if we showed estimated total cost including insurance?
  - What if we offered side-by-side provider comparisons?

**2.1 Quick Booking Flow [type: primary]**
- HOW IT IS REACHED: Clicking "Quick Book" CTA from Search Results
- NAVBAR PRESENCE: Yes
- Page Goal: Enable rapid appointment booking for users with clear preferences
- Screen Description:
  1. Streamlined appointment form with minimal required fields
  2. Available time slots for next 7 days
  3. Basic reason for visit selection
  4. Express patient registration
  5. Payment method selection
  6. Instant confirmation with calendar integration
- Design Problems:
  - HMW balance speed with collecting necessary information?
  - HMW ensure quality of care isn't compromised by quick booking?
  - HMW handle edge cases where additional information is needed?
- Design Opportunities:
  - What if we offered one-click booking for returning patients?
  - What if we provided appointment type recommendations based on reason for visit?
  - What if we offered flexible scheduling with provider approval?

#### Navigation Structure:
1.0 Homepage | 2.0 Search Results | 2.1 Quick Booking Flow

#### User Journey Flow:
1.0 Homepage → [search for provider/specialty] → 2.0 Search Results → [click "Quick Book"] → 2.1 Quick Booking Flow → [submit] → 3.0 Appointment Confirmation

---

## Scenario 2: Existing Patient Managing Upcoming Appointment

### User Scenario
Michael, a 45-year-old patient with diabetes, has a routine follow-up appointment scheduled for next week. He needs to reschedule due to a work conflict and wants to upload recent lab results before his new appointment. He's familiar with the platform but wants to ensure his provider has all necessary information.

### User Goal
Efficiently reschedule his appointment to a convenient time and ensure his healthcare provider has access to his latest medical information before the visit.

### Business Goal
Retain existing patients through seamless appointment management while improving care quality through better information sharing and reducing no-shows.

### Workflow Design Variation 1: Dashboard-Centric Management

#### Screen Breakdown:

**1.0 Patient Dashboard [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item (default landing for logged-in users)
- NAVBAR PRESENCE: Yes
- Page Goal: Provide comprehensive overview of patient's healthcare activity and upcoming needs
- Screen Description:
  1. Upcoming appointments with quick action buttons
  2. Recent medical activity and test results
  3. Medication reminders and refill status
  4. Health metrics tracking and trends
  5. Provider messages and notifications
  6. Quick access to common actions (book, reschedule, contact)
- Design Problems:
  - HMW prioritize information for patients with multiple health conditions?
  - HMW make the dashboard useful for both active and occasional users?
  - HMW ensure critical information doesn't get buried in the interface?
- Design Opportunities:
  - What if we provided personalized health insights based on patient data?
  - What if we offered proactive appointment suggestions based on health patterns?
  - What if we integrated family member health management for caregivers?

**1.0-D Appointment Detail [type: detail]**
- HOW IT IS REACHED: Clicking on appointment card from Patient Dashboard
- NAVBAR PRESENCE: No
- Page Goal: Display comprehensive appointment information with management options
- Screen Description:
  1. Complete appointment details (date, time, provider, location)
  2. Appointment preparation checklist and instructions
  3. Relevant medical history and previous visit notes
  4. Document upload area for lab results and records
  5. Pre-visit questionnaire if required
  6. Modification options (reschedule, cancel, add notes)
- Design Problems:
  - HMW ensure patients are properly prepared for their appointments?
  - HMW make appointment changes feel effortless?
  - HMW help patients understand what to expect during their visit?
- Design Opportunities:
  - What if we provided personalized preparation based on appointment type and patient history?
  - What if we offered virtual pre-screening for certain appointment types?
  - What if we enabled direct messaging with the provider's office?

**Pu.3 Reschedule Appointment Modal [type: modal]**
- HOW IT IS REACHED: Clicking "Reschedule" CTA button on Appointment Detail screen
- NAVBAR PRESENCE: No
- Page Goal: Enable quick appointment rescheduling with minimal disruption
- Screen Description:
  1. Available alternative time slots within preferred date range
  2. Reason for rescheduling (optional feedback)
  3. Notification preferences for confirmation
  4. Impact assessment (any preparation changes needed)
  5. Automatic calendar updates
  6. Provider notification system
- Design Problems:
  - HMW minimize the effort required to reschedule appointments?
  - HMW ensure rescheduling doesn't negatively impact care continuity?
  - HMW handle cases where preferred times aren't available?
- Design Opportunities:
  - What if we offered flexible scheduling with provider approval for urgent needs?
  - What if we provided rescheduling suggestions based on patient preferences?
  - What if we offered waitlist options for preferred time slots?

**2.0 Medical Records [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item
- NAVBAR PRESENCE: Yes
- Page Goal: Provide secure access to medical information with easy sharing capabilities
- Screen Description:
  1. Organized medical document library (lab results, imaging, reports)
  2. Upload functionality for new documents
  3. Sharing controls for provider access
  4. Document categorization and search
  5. Download and print options
  6. Version history and update notifications
- Design Problems:
  - HMW make medical records accessible without compromising security?
  - HMW help patients understand complex medical information?
  - HMW ensure providers have access to relevant information when needed?
- Design Opportunities:
  - What if we provided plain-language summaries of medical reports?
  - What if we offered automatic document categorization using AI?
  - What if we enabled secure family member access for caregiving situations?

#### Navigation Structure:
1.0 Patient Dashboard | 2.0 Medical Records

#### User Journey Flow:
1.0 Patient Dashboard → [click appointment card] → 1.0-D Appointment Detail [type: detail] → [click "Reschedule"] → Pu.3 Reschedule Appointment Modal [type: modal] → [select new time] → 1.0-D Appointment Detail (updated) → [navigate to] → 2.0 Medical Records → [upload lab results] → [share with provider] → 1.0 Patient Dashboard (confirmation)

### Workflow Design Variation 2: Quick Action Flow

#### Screen Breakdown:

**1.0 My Appointments [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item
- NAVBAR PRESENCE: Yes
- Page Goal: Provide focused appointment management with streamlined actions
- Screen Description:
  1. Chronological list of upcoming and past appointments
  2. Quick action buttons for each appointment (reschedule, cancel, prepare)
  3. Appointment status indicators and notifications
  4. Calendar view toggle for visual scheduling
  5. Bulk actions for multiple appointments
  6. Integration with external calendar systems
- Design Problems:
  - HMW make appointment management feel effortless for busy patients?
  - HMW provide enough context without cluttering the interface?
  - HMW handle complex scheduling scenarios (recurring appointments, multiple providers)?
- Design Opportunities:
  - What if we offered smart scheduling suggestions based on patient patterns?
  - What if we provided appointment impact analysis for rescheduling?
  - What if we enabled voice-activated appointment management?

**Pu.4 Quick Reschedule Modal [type: modal]**
- HOW IT IS REACHED: Clicking "Reschedule" quick action button on My Appointments screen
- NAVBAR PRESENCE: No
- Page Goal: Enable immediate appointment rescheduling with minimal steps
- Screen Description:
  1. Smart time slot suggestions based on patient preferences
  2. One-click rescheduling for suggested times
  3. Alternative date ranges if preferred times unavailable
  4. Automatic conflict detection with other appointments
  5. Instant confirmation and notification system
- Design Problems:
  - HMW make rescheduling feel instantaneous while ensuring accuracy?
  - HMW handle scheduling conflicts gracefully?
  - HMW provide enough flexibility without overwhelming choices?
- Design Opportunities:
  - What if we learned from patient rescheduling patterns to improve suggestions?
  - What if we offered automatic rescheduling based on predefined preferences?
  - What if we provided rescheduling impact assessment for care continuity?

#### Navigation Structure:
1.0 My Appointments

#### User Journey Flow:
1.0 My Appointments → [click "Reschedule" quick action] → Pu.4 Quick Reschedule Modal [type: modal] → [select new time] → 1.0 My Appointments (updated) → [click "Prepare" action] → 1.0-D Appointment Detail [type: detail] → [upload documents] → 1.0 My Appointments (confirmation)

---

## Scenario 3: Patient Seeking Urgent Care

### User Scenario
Jessica, a 32-year-old mother, notices her 5-year-old child has developed a high fever and rash late in the evening. She's concerned but unsure if this requires emergency room attention or if urgent care would be appropriate. She needs immediate guidance and potentially same-day or next-day care options.

### User Goal
Quickly assess the urgency of her child's condition and secure appropriate medical care within the next few hours, with clear guidance on care level needed.

### Business Goal
Provide appropriate care routing to reduce emergency room overcrowding while ensuring patient safety and capturing urgent care revenue opportunities.

### Workflow Design Variation 1: Triage-First Approach

#### Screen Breakdown:

**1.0 Urgent Care Hub [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item or emergency banner
- NAVBAR PRESENCE: Yes
- Page Goal: Provide immediate access to urgent care options with clear guidance
- Screen Description:
  1. Urgent symptom assessment tool with pediatric options
  2. Care level recommendations (emergency, urgent care, telehealth)
  3. Real-time availability for urgent care providers
  4. Emergency contact information and directions
  5. Telehealth immediate consultation options
  6. Urgent care preparation checklist
- Design Problems:
  - HMW help parents make appropriate care decisions under stress?
  - HMW provide immediate access without compromising safety?
  - HMW balance urgency with thorough assessment?
- Design Opportunities:
  - What if we offered immediate nurse triage via video call?
  - What if we provided real-time emergency room wait times?
  - What if we offered pediatric-specific urgent care guidance?

**Pu.5 Urgent Symptom Checker [type: modal]**
- HOW IT IS REACHED: Clicking "Check Symptoms Now" CTA button on Urgent Care Hub
- NAVBAR PRESENCE: No
- Page Goal: Rapidly assess symptom severity and provide appropriate care recommendations
- Screen Description:
  1. Age-specific symptom questionnaire with visual aids
  2. Severity indicators with clear escalation triggers
  3. Immediate care recommendations with reasoning
  4. Provider matching based on urgency and location
  5. Direct booking for urgent appointments
  6. Emergency services contact if needed
- Design Problems:
  - HMW provide accurate triage without medical training?
  - HMW ensure safety while avoiding unnecessary emergency visits?
  - HMW handle pediatric symptoms appropriately?
- Design Opportunities:
  - What if we integrated with poison control for specific symptoms?
  - What if we offered photo-based symptom assessment for rashes?
  - What if we provided multilingual support for urgent situations?

**2.0 Urgent Care Providers [type: primary]**
- HOW IT IS REACHED: Direct navigation from symptom checker results or sidebar nav item
- NAVBAR PRESENCE: Yes
- Page Goal: Display available urgent care options with real-time availability
- Screen Description:
  1. Urgent care centers with current wait times
  2. Telehealth providers available for immediate consultation
  3. Pediatric specialists with urgent availability
  4. Location-based sorting with driving directions
  5. Insurance verification for urgent care coverage
  6. Direct check-in options to reduce wait times
- Design Problems:
  - HMW help patients choose between multiple urgent care options quickly?
  - HMW provide accurate wait time information?
  - HMW ensure appropriate provider matching for pediatric cases?
- Design Opportunities:
  - What if we offered virtual queue management for urgent care centers?
  - What if we provided real-time provider availability updates?
  - What if we offered family-friendly urgent care center identification?

**Pu.6 Urgent Booking Modal [type: modal]**
- HOW IT IS REACHED: Clicking "Book Now" CTA button on Urgent Care Providers screen
- NAVBAR PRESENCE: No
- Page Goal: Enable rapid booking for urgent care with minimal friction
- Screen Description:
  1. Immediate availability slots for next 24 hours
  2. Streamlined patient information (child's details)
  3. Symptom summary from assessment
  4. Insurance quick verification
  5. Arrival time estimation and preparation instructions
  6. Emergency contact setup and notifications
- Design Problems:
  - HMW minimize booking time while collecting essential information?
  - HMW handle pediatric patient information appropriately?
  - HMW ensure parents have all necessary preparation information?
- Design Opportunities:
  - What if we offered family member booking with shared medical history?
  - What if we provided arrival notifications to reduce waiting anxiety?
  - What if we offered virtual check-in to minimize exposure time?

#### Navigation Structure:
1.0 Urgent Care Hub | 2.0 Urgent Care Providers

#### User Journey Flow:
1.0 Urgent Care Hub → [click "Check Symptoms Now"] → Pu.5 Urgent Symptom Checker [type: modal] → [complete assessment] → 2.0 Urgent Care Providers → [click "Book Now"] → Pu.6 Urgent Booking Modal [type: modal] → [submit booking] → 3.0 Urgent Care Confirmation

### Workflow Design Variation 2: Direct Access Flow

#### Screen Breakdown:

**1.0 Emergency & Urgent Care [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item or emergency access button
- NAVBAR PRESENCE: Yes
- Page Goal: Provide immediate access to all urgent care options without assessment barriers
- Screen Description:
  1. Emergency services contact (911) with clear indicators
  2. Urgent care center locations with real-time availability
  3. Telehealth urgent consultation immediate access
  4. Nurse hotline for triage support
  5. Pharmacy urgent medication access
  6. Transportation options for urgent care
- Design Problems:
  - HMW provide comprehensive urgent care access without overwhelming users?
  - HMW ensure appropriate care level selection?
  - HMW handle true emergencies appropriately?
- Design Opportunities:
  - What if we integrated with emergency services for seamless handoff?
  - What if we offered urgent care concierge services?
  - What if we provided real-time emergency room capacity information?

**1.1 Telehealth Urgent Care [type: primary]**
- HOW IT IS REACHED: Clicking "Start Video Consultation" from Emergency & Urgent Care screen
- NAVBAR PRESENCE: Yes
- Page Goal: Provide immediate virtual urgent care consultation
- Screen Description:
  1. Available providers for immediate consultation
  2. Video consultation setup and testing
  3. Symptom pre-screening form
  4. Payment and insurance verification
  5. Prescription and follow-up care coordination
  6. Emergency escalation options during consultation
- Design Problems:
  - HMW ensure video consultations are effective for urgent pediatric care?
  - HMW handle technical issues during urgent consultations?
  - HMW provide appropriate follow-up care coordination?
- Design Opportunities:
  - What if we offered multi-party consultations including specialists?
  - What if we provided real-time vital sign monitoring integration?
  - What if we offered immediate prescription delivery for urgent medications?

#### Navigation Structure:
1.0 Emergency & Urgent Care | 1.1 Telehealth Urgent Care

#### User Journey Flow:
1.0 Emergency & Urgent Care → [click "Start Video Consultation"] → 1.1 Telehealth Urgent Care → [connect with provider] → [consultation] → [receive care plan] → [follow-up scheduling if needed]

---

## Settings Screen

**4.0 Settings [type: settings]**
- HOW IT IS REACHED: Clicking settings icon/link in navigation or user menu
- NAVBAR PRESENCE: No
- Page Goal: Centralize all user preferences, accessibility options, and account management
- Screen Description:
  
  **Account Settings Section:**
  - Personal information management
  - Password and security settings
  - Two-factor authentication setup
  - Account deletion and data export
  
  **Notification Preferences Section:**
  - Appointment reminders (email, SMS, push)
  - Health alerts and medication reminders
  - Provider communication preferences
  - Marketing and promotional communications toggle
  
  **Accessibility Settings Section:**
  - Screen reader compatibility toggle
  - High contrast mode toggle
  - Font size adjustment
  - Motor accessibility options
  - Language and localization preferences
  
  **Privacy & Data Section:**
  - Medical record sharing permissions
  - Family member access controls
  - Data retention preferences
  - Third-party integration permissions
  
  **Display Preferences Section:**
  - Dashboard layout customization
  - Calendar view preferences
  - Appointment display options
  - Theme selection (light/dark mode)

- Design Problems:
  - HMW organize complex settings without overwhelming users?
  - HMW ensure accessibility settings are discoverable and effective?
  - HMW make privacy controls understandable and actionable?
- Design Opportunities:
  - What if we provided settings recommendations based on user behavior?
  - What if we offered guided accessibility setup for new users?
  - What if we enabled settings synchronization across family member accounts?

---

## Error States

**Er.1 Network Connection Error [type: state]**
- Condition of existing screens when network connectivity is lost
- Displays offline mode capabilities and cached information
- Provides retry mechanisms and offline form saving

**Er.2 Appointment Booking Failure [type: state]**
- Condition when appointment booking encounters system errors
- Offers alternative booking methods and provider contact information
- Saves user progress and provides recovery options

**Er.3 Payment Processing Error [type: state]**
- Condition when payment transactions fail during booking
- Provides alternative payment methods and manual processing options
- Ensures appointment is held while resolving payment issues

**Er.4 Provider Unavailability [type: state]**
- Condition when selected provider becomes unavailable
- Offers similar provider alternatives and waitlist options
- Provides notification setup for future availability

---

## ACCESSIBILITY NOTES

**Keyboard Navigation:**
- All primary screens support full keyboard navigation with logical tab order
- Skip links provided for main content areas on each screen
- Modal dialogs trap focus and return to trigger element on close
- Custom keyboard shortcuts for common actions (Ctrl+B for booking, Ctrl+S for search)

**ARIA Labels and Landmarks:**
- Main navigation marked with navigation landmark
- Content areas use main landmark with descriptive labels
- Form sections use fieldset and legend for grouping
- Dynamic content changes announced via aria-live regions
- Interactive elements have descriptive aria-labels

**Screen Reader Announcements:**
- Page title changes announced on navigation
- Form validation errors announced immediately
- Appointment confirmations and status changes announced
- Loading states and progress indicators announced
- Emergency and urgent care options clearly identified

**High Contrast Mode:**
- Toggled via Settings screen, applies as CSS class site-wide
- Maintains WCAG AAA contrast ratios (7:1 for normal text, 4.5:1 for large text)
- Preserves all visual hierarchy and interactive states
- Medical imagery and charts remain accessible with alternative descriptions

**Focus Indicators:**
- Visible focus rings on all interactive elements (2px solid, high contrast color)
- Focus indicators scale appropriately across viewport sizes
- Custom focus styles for complex components (calendars, modals)
- Focus management for dynamic content and single-page app navigation

**Minimum Touch Targets:**
- 44px minimum for all interactive elements across all screens
- Adequate spacing between adjacent interactive elements
- Larger touch targets for urgent care and emergency functions
- Gesture alternatives provided for complex interactions

---

## VIEWPORT BEHAVIOUR

**Desktop (1024px+):**
- Full sidebar navigation with expanded menu items
- Multi-column layouts for dashboard and search results
- Hover states and tooltips for enhanced information display
- Side-by-side modal layouts for complex forms
- Advanced filtering and sorting options visible by default

**Tablet (768px–1023px):**
- Collapsible sidebar navigation with icon + text labels
- Responsive grid layouts adapting to available space
- Touch-optimized interactive elements and spacing
- Simplified modal layouts with stacked form sections
- Contextual action menus for space efficiency

**Mobile (320px–767px):**
- Bottom navigation bar for primary actions
- Single-column layouts with vertical scrolling
- Full-screen modals for complex interactions
- Swipe gestures for navigation and actions
- Simplified information hierarchy with progressive disclosure
- Emergency and urgent care options prominently featured
- One-handed operation considerations for all critical functions

**Cross-Viewport Considerations:**
- Consistent information architecture across all breakpoints
- Progressive enhancement for advanced features on larger screens
- Touch-first design principles applied universally
- Performance optimization for mobile networks and devices
- Offline functionality maintained across all viewport sizes

---

*This documentation serves as the foundation for wireframe generation and represents a comprehensive user-centered design approach that balances patient needs with healthcare business objectives while ensuring accessibility and scalability across diverse user scenarios.*