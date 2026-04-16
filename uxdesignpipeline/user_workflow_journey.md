# User Workflow Journey - BridgeNow Finance

## Project Overview
**Story ID:** VRVTEMP-387  
**Product:** BridgeNow Finance - Tasheel Finance Document  
**Context:** A new short-term finance product offering designed for customers who need immediate funding and tend to early close their loans.

---

## Experience Analysis

### Primary User Experience: **Quick Finance Access**
Customers seeking immediate short-term funding solutions with flexible repayment options.

### Key Scenarios Identified:
1. **New Customer Application Journey**
2. **Existing Customer Product Switch Journey**
3. **Sales Team Assisted Application Journey**
4. **Early Closure Journey**
5. **Top-Up Application Journey**

---

## Scenario 1: New Customer Application Journey

### Context & User Story
**Scenario:** Ahmed, a 42-year-old government employee earning SAR 12,000 monthly, needs immediate funding of SAR 10,000 to cover unexpected medical expenses. He wants a quick, hassle-free loan with no early closure penalties as he expects to repay within 6 months.

### Goals
**User Goal:** To quickly access immediate funding with transparent terms and no early closure penalties  
**Business Goal:** To capture customers who typically early close loans and provide them with a suitable product offering while maintaining profitability

### Workflow Design Variation 1A: Mobile App Primary Journey

#### Screen Flow:

**1.0 BridgeNow Landing Page**
- **Page Goal:** Build credibility and communicate key value proposition of BridgeNow Finance
- **Screen Description:**
  - Hero section highlighting "No Early Closure Charges" as primary selling point
  - Clear positioning: "Easy Salary Advance for your immediate needs"
  - Product benefits: Fixed 24-month tenure, up to 1x income (max SAR 30,000)
  - Fixed rate of 27% per annum for all profiles
  - Minimum loan amount SAR 4,000
  - CTA: "Apply Now" and "Learn More"
  - Discreet link to regular Personal Finance for higher amounts/different tenure
- **Design Problems:**
  - HMW communicate the unique value proposition without overwhelming the user?
  - HMW build trust immediately for a new product offering?
  - HMW clearly differentiate from existing Cash/Combo finance products?
- **Design Opportunities:**
  - What if we show personalized loan amount based on income data?
  - What if we provide a comparison table with traditional finance options?
  - What if we include customer testimonials specific to early closure scenarios?

**2.0 Eligibility Check Screen**
- **Page Goal:** Quickly assess customer eligibility and set expectations
- **Screen Description:**
  - Income verification through API pulls
  - Minimum income requirement check (SAR 4,000)
  - CITC and geolocation verification
  - Progress indicator showing application steps
  - Clear messaging about STP (Straight Through Processing)
- **Design Problems:**
  - HMW handle customers from unlisted companies gracefully?
  - HMW communicate technical verification processes in user-friendly terms?
  - HMW manage failed verification scenarios?
- **Design Opportunities:**
  - What if we provide real-time status updates during verification?
  - What if we offer alternative verification methods for edge cases?
  - What if we explain why each verification step is necessary?

**3.0 Loan Offer Screen**
- **Page Goal:** Present personalized loan offer and build confidence in the offering
- **Screen Description:**
  - Personalized loan amount (1x income, capped at SAR 30,000)
  - Fixed tenure: 24 months
  - Fixed rate: 27% per annum
  - Monthly installment calculation
  - Admin fee (0.5%) and VAT breakdown
  - Prominent "No Early Closure Fee" messaging
  - Accept/Decline options
  - Option to switch to regular Personal Finance if higher amount needed
- **Design Problems:**
  - HMW make the fixed amount offering feel personalized rather than restrictive?
  - HMW clearly communicate all fees and charges upfront?
  - HMW handle customers who want different amounts or tenure?
- **Design Opportunities:**
  - What if we show savings compared to early closure fees of traditional loans?
  - What if we provide a payment schedule preview?
  - What if we highlight the benefits of the fixed structure?

**4.0 Personal Information Collection**
- **Page Goal:** Collect necessary customer information efficiently
- **Screen Description:**
  - Pre-populated fields from API data
  - Salary date collection for auto-debit setup
  - Bank statement upload for unlisted company employees
  - Contact information verification
  - Employment details confirmation
- **Design Problems:**
  - HMW minimize data entry while ensuring accuracy?
  - HMW handle different employment types and verification needs?
  - HMW make salary date selection intuitive?
- **Design Opportunities:**
  - What if we use smart defaults based on employment sector?
  - What if we provide calendar integration for salary date selection?
  - What if we offer document scanning for bank statements?

**5.0 Debit Card Collection**
- **Page Goal:** Securely collect debit card information for auto-debit setup
- **Screen Description:**
  - Secure card input form
  - Auto-debit explanation and benefits
  - Payment schedule (salary date and 3 days after)
  - Security assurances and encryption messaging
  - Terms and conditions for auto-debit
- **Design Problems:**
  - HMW build trust for sharing sensitive financial information?
  - HMW clearly explain auto-debit process and timing?
  - HMW handle customers without debit cards?
- **Design Opportunities:**
  - What if we show visual timeline of payment schedule?
  - What if we provide alternative payment methods as backup?
  - What if we offer payment reminders and notifications?

**6.0 Document Upload & Verification**
- **Page Goal:** Complete document verification process efficiently
- **Screen Description:**
  - Required document checklist
  - Camera integration for document capture
  - Upload progress indicators
  - Document quality validation
  - Real-time verification status
- **Design Problems:**
  - HMW make document upload process seamless?
  - HMW handle poor quality or incorrect documents?
  - HMW provide clear guidance on document requirements?
- **Design Opportunities:**
  - What if we use AI for document quality checking?
  - What if we provide document templates or examples?
  - What if we offer multiple upload methods (camera, gallery, scan)?

**7.0 Application Review & Confirmation**
- **Page Goal:** Allow customer to review all information before final submission
- **Screen Description:**
  - Complete application summary
  - Loan terms recap
  - Payment schedule overview
  - Edit options for each section
  - Final terms and conditions
  - Digital signature/consent
  - 24-hour cooling period notification (SAMA requirement)
- **Design Problems:**
  - HMW present comprehensive information without overwhelming?
  - HMW ensure customer understands all terms and conditions?
  - HMW handle the mandatory cooling period requirement?
- **Design Opportunities:**
  - What if we use progressive disclosure for detailed terms?
  - What if we provide video explanations for complex terms?
  - What if we send summary via SMS/email for reference?

**8.0 Application Submitted**
- **Page Goal:** Confirm successful submission and set expectations for next steps
- **Screen Description:**
  - Confirmation message with application reference number
  - Expected processing timeline
  - Next steps explanation
  - Contact information for queries
  - Option to track application status
  - Cooling period reminder
- **Design Problems:**
  - HMW manage customer expectations during processing?
  - HMW provide adequate support during waiting period?
  - HMW handle the cooling period communication effectively?
- **Design Opportunities:**
  - What if we provide real-time application tracking?
  - What if we send proactive updates via multiple channels?
  - What if we offer educational content during waiting period?

**9.0 Approval & Disbursement**
- **Page Goal:** Communicate approval and facilitate disbursement
- **Screen Description:**
  - Approval notification
  - Final loan amount after admin fee and VAT deduction
  - Disbursement timeline
  - Account details confirmation
  - Contract download option
  - First payment date reminder
- **Design Problems:**
  - HMW clearly communicate final disbursement amount?
  - HMW ensure customer understands repayment obligations?
  - HMW provide easy access to loan documentation?
- **Design Opportunities:**
  - What if we provide digital wallet integration for disbursement?
  - What if we create a loan dashboard for ongoing management?
  - What if we offer financial planning tools post-disbursement?

### Workflow Design Variation 1B: Sales-Assisted Journey

#### Screen Flow:

**1.0 Sales Dashboard - BridgeNow Application**
- **Page Goal:** Enable sales team to efficiently create BridgeNow applications
- **Screen Description:**
  - Customer search and selection
  - BridgeNow product selection
  - Quick eligibility check
  - Application tagging as "non-STP"
  - Customer consent recording
- **Design Problems:**
  - HMW ensure sales team understands BridgeNow specifics?
  - HMW maintain compliance in assisted applications?
  - HMW track non-STP applications effectively?
- **Design Opportunities:**
  - What if we provide sales team with customer insights?
  - What if we offer guided selling prompts?
  - What if we integrate with CRM for better customer context?

**2.0 Customer Information Input**
- **Page Goal:** Allow sales team to input customer information accurately
- **Screen Description:**
  - Customer details form
  - Income verification options
  - Document collection checklist
  - Real-time validation
  - Customer consent checkboxes
- **Design Problems:**
  - HMW ensure data accuracy in manual entry?
  - HMW maintain customer privacy during assisted process?
  - HMW handle incomplete information scenarios?
- **Design Opportunities:**
  - What if we provide auto-complete suggestions?
  - What if we offer customer verification via OTP?
  - What if we integrate with existing customer database?

**3.0 Product Switch Option**
- **Page Goal:** Enable easy switching between BridgeNow and regular Personal Finance
- **Screen Description:**
  - Product comparison table
  - Switch confirmation dialog
  - New application creation process
  - Original application status update
  - Customer notification options
- **Design Problems:**
  - HMW make product switching seamless for sales team?
  - HMW ensure customer is informed about product changes?
  - HMW maintain application audit trail?
- **Design Opportunities:**
  - What if we provide recommendation engine for product selection?
  - What if we offer side-by-side benefit comparison?
  - What if we automate customer communication about switches?

---

## Scenario 2: Existing Customer Product Switch Journey

### Context & User Story
**Scenario:** Fatima, an existing Tasheel customer with a current Cash Finance loan, discovers BridgeNow Finance and wants to switch to take advantage of no early closure fees for her next loan application.

### Goals
**User Goal:** To easily switch to BridgeNow Finance and understand the benefits compared to current product  
**Business Goal:** To retain existing customers by offering suitable product alternatives and increase customer satisfaction

### Workflow Design Variation 2A: In-App Product Discovery

#### Screen Flow:

**1.0 Customer Dashboard**
- **Page Goal:** Showcase BridgeNow as a relevant option for existing customers
- **Screen Description:**
  - Current loan status overview
  - Personalized BridgeNow recommendation banner
  - "Explore New Products" section
  - Quick comparison with current product
  - Early closure calculator for current loan
- **Design Problems:**
  - HMW introduce new product without disrupting current loan management?
  - HMW personalize recommendations based on customer behavior?
  - HMW avoid overwhelming customers with too many options?
- **Design Opportunities:**
  - What if we use predictive analytics to identify potential switchers?
  - What if we show potential savings with BridgeNow?
  - What if we offer seamless transition planning?

**2.0 Product Comparison Screen**
- **Page Goal:** Help customer understand differences between current and BridgeNow products
- **Screen Description:**
  - Side-by-side feature comparison
  - Benefit highlights for BridgeNow
  - Cost comparison calculator
  - Eligibility check for BridgeNow
  - FAQ section addressing common concerns
- **Design Problems:**
  - HMW present complex product differences simply?
  - HMW avoid cannibalizing existing profitable products?
  - HMW handle customers who don't qualify for BridgeNow?
- **Design Opportunities:**
  - What if we provide interactive calculators for different scenarios?
  - What if we offer personalized recommendations based on usage patterns?
  - What if we create video explanations for product differences?

### Workflow Design Variation 2B: Proactive Outreach Journey

#### Screen Flow:

**1.0 Targeted Communication Landing**
- **Page Goal:** Engage customers identified as potential BridgeNow candidates
- **Screen Description:**
  - Personalized message based on early closure history
  - BridgeNow benefits specific to their profile
  - Limited-time offer or incentive
  - Direct application link
  - Option to learn more or compare products
- **Design Problems:**
  - HMW create compelling messaging without being pushy?
  - HMW ensure relevance to customer's current situation?
  - HMW handle customers who are not interested?
- **Design Opportunities:**
  - What if we use behavioral triggers for timing outreach?
  - What if we offer exclusive benefits for existing customers?
  - What if we provide consultation with financial advisors?

---

## Scenario 3: Early Closure Journey

### Context & User Story
**Scenario:** Omar, a BridgeNow customer, receives an unexpected bonus and wants to close his loan early to reduce his financial obligations, taking advantage of the no early closure fee benefit.

### Goals
**User Goal:** To easily close the loan early without penalties and understand the exact settlement amount  
**Business Goal:** To provide seamless early closure experience that reinforces the product's key value proposition

### Workflow Design Variation 3A: Self-Service Early Closure

#### Screen Flow:

**1.0 Loan Management Dashboard**
- **Page Goal:** Provide easy access to early closure option
- **Screen Description:**
  - Current loan status and balance
  - "Close Loan Early" prominent CTA
  - Benefits reminder (no early closure fee)
  - Payment history overview
  - Outstanding amount calculator
- **Design Problems:**
  - HMW make early closure option discoverable without promoting it too heavily?
  - HMW ensure customers understand the settlement process?
  - HMW handle partial payments vs. full closure?
- **Design Opportunities:**
  - What if we show savings compared to traditional loans?
  - What if we offer flexible closure options (partial/full)?
  - What if we provide financial impact analysis?

**2.0 Early Closure Calculator**
- **Page Goal:** Provide transparent calculation of settlement amount
- **Screen Description:**
  - Outstanding principal amount
  - Accrued profit calculation
  - Total settlement amount
  - Confirmation of zero early closure fee
  - Payment method selection
  - Settlement date options
- **Design Problems:**
  - HMW ensure calculation transparency and accuracy?
  - HMW handle different settlement date scenarios?
  - HMW provide multiple payment options?
- **Design Opportunities:**
  - What if we provide detailed breakdown of calculations?
  - What if we offer installment options for settlement?
  - What if we show comparison with original payment schedule?

### Workflow Design Variation 3B: Assisted Early Closure

#### Screen Flow:

**1.0 Customer Service Request**
- **Page Goal:** Facilitate early closure request through customer service
- **Screen Description:**
  - Contact options (call, chat, email)
  - Early closure request form
  - Required information checklist
  - Expected processing timeline
  - Callback scheduling option
- **Design Problems:**
  - HMW provide efficient customer service for early closure?
  - HMW ensure consistent information across channels?
  - HMW handle high volume of early closure requests?
- **Design Opportunities:**
  - What if we provide AI-powered chat assistance?
  - What if we offer video call support for complex cases?
  - What if we create self-service options to reduce service load?

---

## Scenario 4: Top-Up Application Journey

### Context & User Story
**Scenario:** Khalid, a BridgeNow customer with good payment history, needs additional funding and wants to apply for a top-up, understanding it will be under regular Cash Finance terms.

### Goals
**User Goal:** To easily access additional funding while understanding the change in product terms  
**Business Goal:** To provide additional lending opportunities while transitioning customers to regular products for higher amounts

### Workflow Design Variation 4A: In-App Top-Up Flow

#### Screen Flow:

**1.0 Top-Up Eligibility Screen**
- **Page Goal:** Assess and communicate top-up eligibility
- **Screen Description:**
  - Current BridgeNow loan status
  - Top-up eligibility criteria
  - Available top-up amount
  - Product transition explanation (BridgeNow to Cash Finance)
  - Terms comparison table
- **Design Problems:**
  - HMW clearly communicate product transition implications?
  - HMW ensure customer understands different terms for top-up?
  - HMW handle customers who don't qualify for top-up?
- **Design Opportunities:**
  - What if we provide seamless product education during transition?
  - What if we offer grandfathered benefits for loyal customers?
  - What if we create a unified loan management experience?

**2.0 Top-Up Application Form**
- **Page Goal:** Collect necessary information for top-up assessment
- **Screen Description:**
  - Pre-populated customer information
  - Updated income verification
  - Additional funding requirement
  - Combined loan terms preview
  - Risk assessment indicators
- **Design Problems:**
  - HMW minimize re-entry of existing customer data?
  - HMW assess combined loan risk appropriately?
  - HMW handle changes in customer circumstances?
- **Design Opportunities:**
  - What if we use existing customer data to streamline process?
  - What if we provide real-time risk assessment feedback?
  - What if we offer flexible top-up structuring options?

### Workflow Design Variation 4B: Consolidated Loan Management

#### Screen Flow:

**1.0 Loan Consolidation Option**
- **Page Goal:** Offer loan consolidation as alternative to top-up
- **Screen Description:**
  - Current BridgeNow loan details
  - Proposed consolidated loan terms
  - Benefits and drawbacks comparison
  - New payment schedule
  - Consolidation process timeline
- **Design Problems:**
  - HMW present consolidation as beneficial option?
  - HMW handle complex calculations and comparisons?
  - HMW ensure customer understands long-term implications?
- **Design Opportunities:**
  - What if we provide financial planning tools for decision making?
  - What if we offer consultation with financial advisors?
  - What if we create personalized recommendations based on customer profile?

---

## Scenario 5: Failed Application Recovery Journey

### Context & User Story
**Scenario:** Nasser's BridgeNow application fails during CITC check or geolocation verification, but he's still interested in obtaining finance through alternative channels.

### Goals
**User Goal:** To understand why application failed and explore alternative options  
**Business Goal:** To recover potentially lost customers through alternative fulfillment channels

### Workflow Design Variation 5A: Automated Recovery Flow

#### Screen Flow:

**1.0 Application Status - Failed**
- **Page Goal:** Communicate failure reason and provide next steps
- **Screen Description:**
  - Clear explanation of failure reason
  - Alternative options available
  - Contact information for assistance
  - Option to retry with additional documentation
  - Switch to regular Personal Finance option
- **Design Problems:**
  - HMW communicate technical failures in user-friendly terms?
  - HMW maintain customer confidence after failure?
  - HMW provide clear path forward?
- **Design Opportunities:**
  - What if we provide specific guidance for each failure type?
  - What if we offer immediate callback from sales team?
  - What if we create alternative verification methods?

**2.0 Alternative Verification Path**
- **Page Goal:** Provide alternative methods to complete application
- **Screen Description:**
  - Alternative document upload options
  - Manual verification process explanation
  - Sales team assistance option
  - Expected timeline for manual processing
  - Status tracking for alternative path
- **Design Problems:**
  - HMW maintain user experience quality in manual processes?
  - HMW ensure security in alternative verification?
  - HMW manage customer expectations for longer processing?
- **Design Opportunities:**
  - What if we provide real-time updates during manual processing?
  - What if we offer video verification calls?
  - What if we create expedited processing for certain failure types?

### Workflow Design Variation 5B: Sales Team Intervention

#### Screen Flow:

**1.0 Sales Team Dashboard - Failed Applications**
- **Page Goal:** Enable sales team to identify and assist failed applications
- **Screen Description:**
  - Failed application queue
  - Failure reason categorization
  - Customer contact information
  - Previous interaction history
  - Recovery action options
- **Design Problems:**
  - HMW prioritize failed applications for sales team attention?
  - HMW provide sales team with adequate context?
  - HMW track recovery success rates?
- **Design Opportunities:**
  - What if we use AI to predict recovery likelihood?
  - What if we provide sales team with customer insights?
  - What if we automate initial recovery outreach?

---

## Error States and Edge Cases

### Error State Screens:

**Er.1 Network Connection Error**
- Clear error message with retry option
- Offline capability explanation
- Contact information for technical support

**Er.2 Document Upload Failure**
- Specific error reason (file size, format, quality)
- Alternative upload methods
- Help documentation links

**Er.3 Payment Processing Error**
- Transaction failure explanation
- Alternative payment methods
- Customer service contact options

**Er.4 System Maintenance**
- Maintenance schedule information
- Expected resolution time
- Alternative contact methods

### Pop-up States:

**Pu.1 Product Switch Confirmation**
- Clear explanation of implications
- Benefit comparison
- Confirmation and cancellation options

**Pu.2 Early Closure Confirmation**
- Settlement amount confirmation
- Payment method selection
- Final confirmation step

**Pu.3 Application Abandonment Warning**
- Progress save option
- Resume later functionality
- Contact information for assistance

### Email Communications:

**Em.1 Application Confirmation Email**
- Application reference number
- Next steps timeline
- Contact information
- Document checklist

**Em.2 Approval Notification Email**
- Loan terms summary
- Disbursement timeline
- Contract attachment
- Customer service contacts

**Em.3 Early Closure Confirmation Email**
- Settlement amount breakdown
- Payment confirmation
- Loan closure certificate
- Feedback request

---

## Accessibility Considerations

### Design Standards:
- WCAG 2.1 AA compliance
- Screen reader compatibility
- High contrast mode support
- Keyboard navigation support
- Multiple language support (Arabic/English)
- Font size adjustment options
- Voice input capabilities
- Simplified language options

### Inclusive Design Features:
- Cultural sensitivity in messaging
- Multiple authentication methods
- Flexible input formats
- Error prevention and recovery
- Progressive disclosure of information
- Multiple communication channels

---

## Scalability Framework

### Technical Scalability:
- API-first architecture
- Microservices design
- Cloud-native deployment
- Auto-scaling capabilities
- Performance monitoring
- A/B testing framework

### Business Scalability:
- Multi-channel support
- White-label capabilities
- Configurable business rules
- Automated decision engines
- Analytics and reporting
- Integration capabilities

### User Experience Scalability:
- Modular design system
- Responsive design patterns
- Progressive web app capabilities
- Offline functionality
- Multi-device synchronization
- Personalization engine

---

## Screen Sequence Summary

### Scenario 1A - Mobile App Primary Journey:
1.0 BridgeNow Landing Page → 2.0 Eligibility Check Screen → 3.0 Loan Offer Screen → 4.0 Personal Information Collection → 5.0 Debit Card Collection → 6.0 Document Upload & Verification → 7.0 Application Review & Confirmation → 8.0 Application Submitted → 9.0 Approval & Disbursement

### Scenario 1B - Sales-Assisted Journey:
1.0 Sales Dashboard - BridgeNow Application → 2.0 Customer Information Input → 3.0 Product Switch Option

### Scenario 2A - In-App Product Discovery:
1.0 Customer Dashboard → 2.0 Product Comparison Screen

### Scenario 2B - Proactive Outreach Journey:
1.0 Targeted Communication Landing

### Scenario 3A - Self-Service Early Closure:
1.0 Loan Management Dashboard → 2.0 Early Closure Calculator

### Scenario 3B - Assisted Early Closure:
1.0 Customer Service Request

### Scenario 4A - In-App Top-Up Flow:
1.0 Top-Up Eligibility Screen → 2.0 Top-Up Application Form

### Scenario 4B - Consolidated Loan Management:
1.0 Loan Consolidation Option

### Scenario 5A - Automated Recovery Flow:
1.0 Application Status - Failed → 2.0 Alternative Verification Path

### Scenario 5B - Sales Team Intervention:
1.0 Sales Team Dashboard - Failed Applications

---

## Success Metrics and KPIs

### User Experience Metrics:
- Application completion rate
- Time to complete application
- Customer satisfaction scores
- Support ticket volume
- User retention rates

### Business Metrics:
- STP application percentage
- Early closure rates
- Customer acquisition cost
- Loan disbursement volume
- Revenue per customer

### Technical Metrics:
- System uptime
- API response times
- Error rates
- Mobile app performance
- Security incident rates

---

## Implementation Roadmap

### Phase 1: Core Application Journey
- Mobile app primary journey
- Basic eligibility and offer screens
- Document upload and verification
- Application submission and tracking

### Phase 2: Sales and Support Features
- Sales team dashboard
- Customer service tools
- Product switching capabilities
- Error handling and recovery

### Phase 3: Advanced Features
- Early closure self-service
- Top-up functionality
- Personalization engine
- Advanced analytics

### Phase 4: Optimization and Scale
- Performance optimization
- Advanced accessibility features
- Multi-language support
- Integration with external systems

This comprehensive user workflow journey provides a systematic approach to designing the BridgeNow Finance experience, ensuring all user scenarios are addressed while maintaining focus on business objectives, accessibility, and scalability requirements.