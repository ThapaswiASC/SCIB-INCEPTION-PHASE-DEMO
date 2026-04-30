# Youth Account Management - User Workflow Journey

## Experience Overview

Parents and guardians need to manage their children's financial education through digital banking tools. This experience encompasses fund allocation, spending oversight, limit management, and activity monitoring to teach financial responsibility while maintaining parental control.

---

## Scenario 1: Parent Accessing and Managing Youth Account Dashboard

**Context:** Sarah, a working mother of a 16-year-old daughter, wants to check her daughter's account balance and recent spending activity during her lunch break to ensure her daughter is managing money responsibly.

**User Goal:** Quickly access comprehensive youth account information to monitor financial activity and make informed decisions about fund allocation.

**Business Goal:** Increase parent engagement with youth banking services and build trust through transparent account management tools.

### Workflow Variation A: Quick Dashboard Review

**Screens:**

**1.0 Youth Account Dashboard [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item "Youth Accounts"
- NAVBAR PRESENCE: Yes
- Page Goal: Provide comprehensive overview of youth account status and enable quick management actions
- Screen Description:
  1. Display current youth account balance prominently
  2. Show recent transaction summary (last 5 transactions)
  3. Display active spending limits and usage
  4. Provide quick action buttons for fund transfer and limit management
  5. Show account holder information and account status
  6. Display spending analytics (weekly/monthly trends)
- Design Problems:
  - HMW present complex financial data in a scannable format for busy parents?
  - HMW balance detailed information with quick actionability?
  - HMW communicate spending patterns without overwhelming the parent?
- Design Opportunities:
  - What if the dashboard could predict when funds might run low?
  - What if we could show spending categorization to help parents understand habits?
  - What if parents could set up automated fund transfers based on balance thresholds?

**Pu.1 Fund Transfer Modal [type: modal]**
- HOW IT IS REACHED: Clicking "Add Funds" CTA button on dashboard
- NAVBAR PRESENCE: No
- Page Goal: Enable quick and secure fund transfer from parent to youth account
- Screen Description:
  1. Source account selector with available balances
  2. Transfer amount input with validation
  3. Transfer purpose/note field (optional)
  4. Real-time balance preview after transfer
  5. Confirmation step with transfer summary
- Design Problems:
  - HMW prevent accidental large transfers while maintaining ease of use?
  - HMW communicate transfer limits and validation errors clearly?
- Design Opportunities:
  - What if we could suggest transfer amounts based on spending patterns?
  - What if parents could schedule recurring transfers?

**1.1 Dashboard with Updated Balance [type: primary]**
- HOW IT IS REACHED: Automatic return after successful fund transfer
- NAVBAR PRESENCE: Yes
- Page Goal: Confirm successful transfer and show updated account status
- Screen Description:
  1. Updated youth account balance reflecting new transfer
  2. Success notification with transfer details
  3. Updated transaction history showing new transfer
  4. Refreshed spending limit usage calculations
- Design Problems:
  - HMW ensure parents feel confident the transfer was successful?
- Design Opportunities:
  - What if we could show the impact of the transfer on spending runway?

### Workflow Variation B: Detailed Account Analysis

**Screens:**

**1.0 Youth Account Dashboard [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item "Youth Accounts"
- NAVBAR PRESENCE: Yes
- Page Goal: Provide comprehensive overview with emphasis on detailed analytics
- Screen Description:
  1. Enhanced balance display with trend indicators
  2. Expanded recent activity section (last 10 transactions)
  3. Spending category breakdown chart
  4. Monthly spending comparison
  5. Goal tracking section (if savings goals are set)
  6. Detailed spending limit status with time remaining
- Design Problems:
  - HMW present detailed analytics without creating cognitive overload?
  - HMW help parents identify concerning spending patterns quickly?
- Design Opportunities:
  - What if we could provide personalized insights about spending behavior?
  - What if parents could compare their child's spending to anonymized peer data?

**1.0-D Transaction Detail View [type: detail]**
- HOW IT IS REACHED: Clicking on any transaction row in the activity list
- NAVBAR PRESENCE: No
- Page Goal: Provide complete transaction context for parental review
- Screen Description:
  1. Complete transaction details (merchant, location, time)
  2. Transaction category and tags
  3. Remaining balance after transaction
  4. Option to flag transaction for discussion
  5. Related transactions from same merchant
- Design Problems:
  - HMW provide sufficient detail without making the interface feel invasive?
- Design Opportunities:
  - What if parents could add private notes about transactions for future reference?

**NAVIGATION STRUCTURE:** 1.0 Youth Account Dashboard

**USER JOURNEY FLOW:**
- Variation A: 1.0 Youth Account Dashboard → [click Add Funds] → Pu.1 Fund Transfer Modal → [submit] → 1.1 Dashboard with Updated Balance
- Variation B: 1.0 Youth Account Dashboard → [click transaction] → 1.0-D Transaction Detail View → [back] → 1.0 Youth Account Dashboard

---

## Scenario 2: Parent Setting Up Spending Controls and Limits

**Context:** Mark, a father of twin 14-year-olds, wants to establish weekly spending limits for both children's accounts after noticing they spent their entire monthly allowance in the first week.

**User Goal:** Configure appropriate spending controls that teach financial discipline while allowing reasonable flexibility for teenage needs.

**Business Goal:** Increase feature adoption of spending controls and reduce customer service calls related to overspending concerns.

### Workflow Variation A: Simple Limit Configuration

**Screens:**

**2.0 Spending Limits Management [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item "Spending Controls"
- NAVBAR PRESENCE: Yes
- Page Goal: Enable parents to configure and manage all spending-related controls in one location
- Screen Description:
  1. Current spending limit overview for all youth accounts
  2. Quick limit adjustment controls (weekly/monthly toggles)
  3. Limit usage visualization (progress bars)
  4. Emergency override options
  5. Notification preferences for limit breaches
  6. Historical limit effectiveness analytics
- Design Problems:
  - HMW help parents choose appropriate limit amounts for different age groups?
  - HMW balance control with teaching financial independence?
- Design Opportunities:
  - What if the system could recommend limits based on the child's age and spending history?
  - What if parents could set different limits for different spending categories?

**Pu.2 Limit Configuration Modal [type: modal]**
- HOW IT IS REACHED: Clicking "Edit Limits" CTA button
- NAVBAR PRESENCE: No
- Page Goal: Provide guided limit setting with contextual help
- Screen Description:
  1. Limit amount input with suggested ranges
  2. Limit period selector (daily/weekly/monthly)
  3. Category-specific limits (optional)
  4. Override conditions configuration
  5. Preview of how limits will affect current spending patterns
- Design Problems:
  - HMW prevent parents from setting unrealistic limits that frustrate children?
- Design Opportunities:
  - What if we could show the impact of different limit scenarios?

### Workflow Variation B: Advanced Control Configuration

**Screens:**

**2.0 Spending Limits Management [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item "Spending Controls"
- NAVBAR PRESENCE: Yes
- Page Goal: Provide comprehensive spending control management with advanced features
- Screen Description:
  1. Multi-account limit management interface
  2. Category-based spending controls
  3. Time-based restrictions (school hours, weekends)
  4. Merchant category blocking/allowing
  5. Graduated limit increases based on responsible spending
  6. Family spending goals and challenges
- Design Problems:
  - HMW make advanced controls accessible without overwhelming less tech-savvy parents?
- Design Opportunities:
  - What if children could request temporary limit increases for special occasions?

**2.0-D Limit History and Analytics [type: detail]**
- HOW IT IS REACHED: Clicking "View Limit History" link
- NAVBAR PRESENCE: No
- Page Goal: Show effectiveness of spending limits over time
- Screen Description:
  1. Historical limit changes and their impact
  2. Spending pattern analysis before/after limit implementation
  3. Limit breach incidents and resolutions
  4. Recommendations for limit adjustments
- Design Problems:
  - HMW present historical data in a way that guides future decisions?
- Design Opportunities:
  - What if we could show correlation between limits and financial learning outcomes?

**NAVIGATION STRUCTURE:** 2.0 Spending Limits Management

**USER JOURNEY FLOW:**
- Variation A: 2.0 Spending Limits Management → [click Edit Limits] → Pu.2 Limit Configuration Modal → [save] → 2.0 Spending Limits Management (updated)
- Variation B: 2.0 Spending Limits Management → [click View Limit History] → 2.0-D Limit History and Analytics → [back] → 2.0 Spending Limits Management

---

## Scenario 3: Parent Reviewing Youth Spending Activity and Patterns

**Context:** Jennifer, a single mother, receives a notification that her 17-year-old son has made several large purchases this week. She wants to review his spending activity to understand if this is concerning behavior or normal teenage spending.

**User Goal:** Analyze spending patterns to make informed decisions about financial guidance and account management.

**Business Goal:** Provide insights that help parents feel confident in their oversight while encouraging continued use of youth banking services.

### Workflow Variation A: Quick Activity Review

**Screens:**

**3.0 Activity Monitor [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item "Activity Monitor"
- NAVBAR PRESENCE: Yes
- Page Goal: Provide comprehensive spending activity overview with filtering and analysis tools
- Screen Description:
  1. Transaction timeline with visual spending patterns
  2. Filter controls (date range, amount, category, merchant)
  3. Spending velocity indicators (unusual activity alerts)
  4. Category breakdown with percentage changes
  5. Comparison to previous periods
  6. Export options for detailed analysis
- Design Problems:
  - HMW help parents distinguish between normal and concerning spending patterns?
  - HMW present transaction data without feeling like surveillance?
- Design Opportunities:
  - What if we could highlight transactions that deviate from normal patterns?
  - What if parents could set up custom alerts for specific spending behaviors?

**3.0-D Individual Transaction Analysis [type: detail]**
- HOW IT IS REACHED: Clicking on any transaction in the activity timeline
- NAVBAR PRESENCE: No
- Page Goal: Provide complete context for individual transactions
- Screen Description:
  1. Full transaction details with location and time context
  2. Spending pattern analysis for this merchant/category
  3. Balance impact and remaining funds visualization
  4. Option to discuss transaction with youth
  5. Similar transaction history
- Design Problems:
  - HMW provide detailed oversight without damaging parent-child trust?
- Design Opportunities:
  - What if transactions could include photos or notes from the youth?

### Workflow Variation B: Pattern Analysis and Insights

**Screens:**

**3.0 Activity Monitor [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item "Activity Monitor"
- NAVBAR PRESENCE: Yes
- Page Goal: Focus on spending insights and pattern recognition
- Screen Description:
  1. AI-powered spending insights and recommendations
  2. Behavioral pattern identification (impulse buying, saving streaks)
  3. Peer comparison data (anonymized)
  4. Financial milestone tracking
  5. Predictive spending forecasts
  6. Educational content recommendations based on spending patterns
- Design Problems:
  - HMW provide meaningful insights without being judgmental about spending choices?
- Design Opportunities:
  - What if the system could suggest conversation starters for financial discussions?

**Pu.3 Spending Alert Configuration [type: modal]**
- HOW IT IS REACHED: Clicking "Set Up Alerts" CTA button
- NAVBAR PRESENCE: No
- Page Goal: Allow parents to configure custom monitoring alerts
- Screen Description:
  1. Alert threshold settings (amount, frequency, category)
  2. Notification method preferences
  3. Alert severity levels
  4. Temporary alert suspension options
- Design Problems:
  - HMW help parents set appropriate alert thresholds that aren't too sensitive?
- Design Opportunities:
  - What if alerts could include suggested responses or conversation guides?

**NAVIGATION STRUCTURE:** 3.0 Activity Monitor

**USER JOURNEY FLOW:**
- Variation A: 3.0 Activity Monitor → [click transaction] → 3.0-D Individual Transaction Analysis → [back] → 3.0 Activity Monitor
- Variation B: 3.0 Activity Monitor → [click Set Up Alerts] → Pu.3 Spending Alert Configuration → [save] → 3.0 Activity Monitor (updated)

---

## Scenario 4: Parent Handling Insufficient Funds and Transfer Errors

**Context:** David attempts to transfer $500 to his daughter's youth account for a school trip, but his checking account only has $300 available. He needs to understand the error and find alternative funding solutions quickly.

**User Goal:** Resolve funding issues efficiently while understanding available options and maintaining the planned transfer timeline.

**Business Goal:** Reduce transfer abandonment rates and guide users toward successful completion of funding transactions.

### Workflow Variation A: Error Resolution with Alternative Sources

**Screens:**

**Er.1 Insufficient Funds Error State [type: state]**
- HOW IT IS REACHED: Automatic display when transfer amount exceeds available balance
- NAVBAR PRESENCE: No
- Page Goal: Clearly communicate the error and provide immediate resolution options
- Screen Description:
  1. Clear error message explaining insufficient funds
  2. Available balance display for selected source account
  3. Alternative funding source suggestions
  4. Option to modify transfer amount
  5. Link to account funding options
  6. Save transfer for later option
- Design Problems:
  - HMW communicate funding errors without causing embarrassment or frustration?
  - HMW guide users toward successful resolution quickly?
- Design Opportunities:
  - What if we could suggest optimal transfer amounts based on available funds?
  - What if users could set up automatic funding from other accounts?

**Pu.4 Alternative Funding Sources Modal [type: modal]**
- HOW IT IS REACHED: Clicking "View Other Funding Options" from error state
- NAVBAR PRESENCE: No
- Page Goal: Present all available funding alternatives in one interface
- Screen Description:
  1. List of all eligible parent accounts with balances
  2. External funding options (linked accounts, credit lines)
  3. Partial transfer options with scheduling for remainder
  4. Account funding shortcuts (external transfers)
- Design Problems:
  - HMW help parents choose the best funding source for their situation?
- Design Opportunities:
  - What if we could recommend the most cost-effective funding option?

### Workflow Variation B: Scheduled Transfer Resolution

**Screens:**

**Er.1 Insufficient Funds Error State [type: state]**
- HOW IT IS REACHED: Automatic display when transfer amount exceeds available balance
- NAVBAR PRESENCE: No
- Page Goal: Focus on scheduling and planning solutions for funding shortfalls
- Screen Description:
  1. Error explanation with timeline impact assessment
  2. Scheduled transfer options based on expected deposits
  3. Partial immediate transfer with remainder scheduling
  4. Notification setup for when funds become available
  5. Youth account impact preview (if transfer is delayed)
- Design Problems:
  - HMW help parents maintain their intended financial support timeline despite temporary shortfalls?
- Design Opportunities:
  - What if we could predict when sufficient funds will be available based on deposit patterns?

**Pu.5 Transfer Scheduling Modal [type: modal]**
- HOW IT IS REACHED: Clicking "Schedule Transfer" from error state
- NAVBAR PRESENCE: No
- Page Goal: Enable parents to plan future transfers when funds become available
- Screen Description:
  1. Calendar interface for selecting transfer date
  2. Automatic transfer setup when balance threshold is met
  3. Notification preferences for scheduled transfers
  4. Option to modify or cancel scheduled transfers
- Design Problems:
  - HMW ensure scheduled transfers don't create future insufficient fund situations?
- Design Opportunities:
  - What if scheduled transfers could automatically adjust based on available funds?

**NAVIGATION STRUCTURE:** N/A (Error states and modals only)

**USER JOURNEY FLOW:**
- Variation A: Er.1 Insufficient Funds Error State → [click View Other Funding Options] → Pu.4 Alternative Funding Sources Modal → [select source] → Return to transfer flow
- Variation B: Er.1 Insufficient Funds Error State → [click Schedule Transfer] → Pu.5 Transfer Scheduling Modal → [schedule] → Confirmation of scheduled transfer

---

## Scenario 5: Parent Managing Multiple Youth Accounts

**Context:** Lisa has three children (ages 13, 15, and 17) with different financial needs and maturity levels. She needs to manage all three accounts efficiently while applying age-appropriate controls and funding strategies.

**User Goal:** Efficiently manage multiple youth accounts with different requirements while maintaining individual attention to each child's financial development.

**Business Goal:** Increase account retention and cross-selling opportunities by demonstrating value in managing multiple youth accounts.

### Workflow Variation A: Unified Multi-Account Dashboard

**Screens:**

**4.0 Multi-Account Overview [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item "All Youth Accounts"
- NAVBAR PRESENCE: Yes
- Page Goal: Provide comprehensive overview of all youth accounts with comparative analysis
- Screen Description:
  1. Side-by-side account comparison cards
  2. Combined spending analytics across all accounts
  3. Bulk action capabilities (mass transfers, limit updates)
  4. Individual account quick actions
  5. Family spending goals and progress
  6. Age-appropriate milestone tracking for each child
- Design Problems:
  - HMW present multiple accounts without creating overwhelming complexity?
  - HMW enable efficient management while maintaining individual account focus?
- Design Opportunities:
  - What if we could show spending patterns that indicate sibling influence?
  - What if parents could set up automatic rebalancing between accounts?

**4.0-D Individual Account Deep Dive [type: detail]**
- HOW IT IS REACHED: Clicking on any account card in the multi-account overview
- NAVBAR PRESENCE: No
- Page Goal: Provide focused management for individual youth account within multi-account context
- Screen Description:
  1. Individual account dashboard with full functionality
  2. Sibling comparison context (when appropriate)
  3. Age-specific recommendations and insights
  4. Quick navigation to other sibling accounts
  5. Account-specific goals and milestones
- Design Problems:
  - HMW maintain individual focus while leveraging multi-account insights?
- Design Opportunities:
  - What if we could suggest learning opportunities based on sibling successes?

### Workflow Variation B: Account-by-Account Management

**Screens:**

**4.0 Multi-Account Overview [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item "All Youth Accounts"
- NAVBAR PRESENCE: Yes
- Page Goal: Facilitate sequential management of individual accounts with easy navigation
- Screen Description:
  1. Account selection interface with status indicators
  2. Recent activity summary for each account
  3. Priority alerts and action items per account
  4. Quick account switching navigation
  5. Bulk operations for common tasks
- Design Problems:
  - HMW help parents prioritize attention across multiple accounts?
- Design Opportunities:
  - What if we could suggest which accounts need attention most urgently?

**Pu.6 Bulk Operations Modal [type: modal]**
- HOW IT IS REACHED: Clicking "Bulk Actions" CTA button
- NAVBAR PRESENCE: No
- Page Goal: Enable efficient management of common tasks across multiple accounts
- Screen Description:
  1. Multi-account fund distribution interface
  2. Bulk limit updates with individual overrides
  3. Mass notification settings
  4. Coordinated goal setting across accounts
- Design Problems:
  - HMW enable bulk operations while respecting individual account differences?
- Design Opportunities:
  - What if bulk operations could automatically adjust for age-appropriate differences?

**NAVIGATION STRUCTURE:** 4.0 Multi-Account Overview

**USER JOURNEY FLOW:**
- Variation A: 4.0 Multi-Account Overview → [click account card] → 4.0-D Individual Account Deep Dive → [back] → 4.0 Multi-Account Overview
- Variation B: 4.0 Multi-Account Overview → [click Bulk Actions] → Pu.6 Bulk Operations Modal → [execute] → 4.0 Multi-Account Overview (updated)

---

## Settings and Configuration

**5.0 Settings [type: settings]**
- HOW IT IS REACHED: Clicking settings icon in navigation or user menu
- NAVBAR PRESENCE: No
- Page Goal: Centralize all user preferences and system configuration options
- Screen Description:
  1. **Notification Preferences**
     - Transaction alerts (amount thresholds, frequency)
     - Limit breach notifications
     - Account activity summaries
     - Email vs. SMS vs. in-app preferences
  2. **Display Options**
     - Currency display format
     - Date and time format preferences
     - Dashboard layout customization
     - Chart and graph preferences
  3. **Security Settings**
     - Two-factor authentication setup
     - Login notification preferences
     - Session timeout settings
     - Trusted device management
  4. **Accessibility Options**
     - Screen reader compatibility toggle
     - High contrast mode toggle
     - Font size adjustment
     - Motion reduction preferences
  5. **Account Management**
     - Profile information updates
     - Linked account management
     - Youth account permissions
     - Data export and privacy controls
- Design Problems:
  - HMW organize diverse settings without creating a overwhelming interface?
  - HMW ensure critical accessibility settings are discoverable?
- Design Opportunities:
  - What if settings could be automatically optimized based on usage patterns?
  - What if we could provide guided setup for new users?

---

## ACCESSIBILITY NOTES

**Keyboard Navigation:**
- All primary screens support full keyboard navigation with logical tab order
- Tab order follows visual hierarchy: header navigation → main content → sidebar → footer
- Skip links provided for main content and navigation sections
- Modal dialogs trap focus and return to trigger element on close

**ARIA Labels and Landmarks:**
- Main navigation marked with role="navigation" and aria-label="Main navigation"
- Content areas use appropriate landmark roles (main, aside, complementary)
- Form controls have associated labels and error messages
- Dynamic content updates announced via aria-live regions
- Data tables include proper headers and scope attributes

**Screen Reader Announcements:**
- Balance updates announced when funds are transferred
- Error states announced immediately when validation fails
- Success confirmations announced after completed actions
- Loading states communicated during data fetching
- Navigation changes announced when moving between screens

**High Contrast Mode:**
- Toggled via Settings screen under Accessibility Options
- Applies as CSS class site-wide affecting all screens
- Maintains minimum 4.5:1 contrast ratio for normal text
- Maintains minimum 3:1 contrast ratio for large text and UI elements

**Focus Indicators:**
- Visible focus indicators on all interactive elements
- Focus indicators use 2px solid border with high contrast color
- Focus indicators never rely solely on color changes
- Custom focus styles for complex components like data tables

**Minimum Touch Targets:**
- All interactive elements minimum 44px touch target
- Adequate spacing between adjacent interactive elements
- Applies to buttons, links, form controls, and custom interactive components

---

## VIEWPORT BEHAVIOUR

**Desktop (1024px+):**
- Full sidebar navigation with expanded menu items
- Multi-column layouts for dashboard cards and data tables
- Hover states and tooltips for enhanced interaction
- Side-by-side modal layouts for complex forms

**Tablet (768px–1023px):**
- Collapsible sidebar navigation with icon + text labels
- Responsive grid layouts that stack at narrower widths
- Touch-optimized interaction targets and spacing
- Modal dialogs adapt to available screen space

**Mobile (320px–767px):**
- Bottom navigation bar replaces sidebar
- Single-column layouts with vertical card stacking
- Full-screen modal presentations
- Swipe gestures for navigation between related screens
- Condensed data tables with horizontal scrolling or accordion patterns
- Priority-based content showing most important information first

---

*This workflow documentation serves as the foundation for wireframe generation and represents the complete user journey for youth account management functionality.*