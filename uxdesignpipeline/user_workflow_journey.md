# User Workflow Journey Documentation

## Experience Overview

**User Type**: Digital Product Users
**Experience Context**: Task Management and Productivity Platform
**Experience Goal**: Enable users to efficiently organize, track, and complete their work tasks while collaborating with team members

---

## Scenario 1: New User Onboarding and First Task Creation

**Scenario Context**: Sarah, a project manager at a growing startup, has just signed up for the task management platform after her team outgrew their current solution. She needs to quickly understand the platform's capabilities and create her first project with tasks to evaluate if it meets her team's needs.

### Workflow Variation 1A: Guided Onboarding Flow

**User Goal**: Successfully set up her workspace and create her first meaningful project with tasks to evaluate the platform's fit for her team's workflow.

**Business Goal**: Increase user activation rate and reduce time-to-value by guiding users through core features, leading to higher trial-to-paid conversion.

**Screen Flow**:

**1.0 Welcome Dashboard [type: primary]**
- HOW IT IS REACHED: Direct navigation after account creation
- NAVBAR PRESENCE: Yes
- Page Goal: Orient new users and provide clear next steps for getting started
- Screen Description:
  1. Welcome message with user's name and company
  2. Progress indicator showing onboarding steps (0/4 completed)
  3. "Start Your First Project" primary CTA
  4. Quick overview of platform benefits
  5. Skip onboarding option for experienced users
  6. Help and support access
- Design Problems:
  - HMW reduce cognitive load for new users while showing platform value?
  - HMW accommodate both novice and experienced users in the same flow?
  - HMW build confidence that this platform will solve their specific problems?
- Design Opportunities:
  - What if we could personalize the onboarding based on company size and industry?
  - What if we showed real-time examples of how similar teams use the platform?
  - What if we provided templates based on their role and team structure?

**Pu.1 Project Creation Modal [type: modal]**
- HOW IT IS REACHED: Clicking "Start Your First Project" CTA button
- NAVBAR PRESENCE: No
- Page Goal: Capture essential project information while maintaining momentum
- Screen Description:
  1. Project name input field (required)
  2. Project description textarea (optional)
  3. Team member invitation field
  4. Project template selection (Marketing Campaign, Product Launch, etc.)
  5. Privacy settings (Team, Company, Private)
  6. "Create Project" primary CTA
  7. "Save as Draft" secondary option
- Design Problems:
  - HMW collect necessary information without overwhelming new users?
  - HMW help users choose appropriate templates without analysis paralysis?
  - HMW encourage team collaboration from the start?
- Design Opportunities:
  - What if we could auto-suggest project names based on common patterns?
  - What if we showed preview of what the project will look like with the selected template?
  - What if we could import existing project structures from other tools?

**2.0 Project Dashboard [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item after project creation
- NAVBAR PRESENCE: Yes
- Page Goal: Provide comprehensive project overview and enable task management
- Screen Description:
  1. Project header with name, description, and team members
  2. Progress overview (tasks completed, in progress, overdue)
  3. Task list with filtering and sorting options
  4. "Add Task" prominent CTA button
  5. Recent activity feed
  6. Project settings and sharing options
  7. Calendar view toggle
- Design Problems:
  - HMW present complex project information in a scannable format?
  - HMW make task creation feel effortless and intuitive?
  - HMW show project health at a glance?
- Design Opportunities:
  - What if we could automatically suggest tasks based on project type?
  - What if we showed intelligent insights about project progress and bottlenecks?
  - What if we could predict project completion dates based on current velocity?

**Pu.2 Task Creation Drawer [type: modal]**
- HOW IT IS REACHED: Clicking "Add Task" CTA button
- NAVBAR PRESENCE: No
- Page Goal: Enable quick task creation with appropriate detail level
- Screen Description:
  1. Task title input (required)
  2. Task description with rich text formatting
  3. Assignee selection dropdown
  4. Due date picker
  5. Priority level selector (High, Medium, Low)
  6. Tags and labels
  7. Subtask creation option
  8. "Create Task" primary CTA
- Design Problems:
  - HMW balance quick task creation with necessary detail capture?
  - HMW make task assignment clear and efficient?
  - HMW encourage proper task scoping and planning?
- Design Opportunities:
  - What if we could auto-assign tasks based on team member expertise?
  - What if we could suggest due dates based on project timeline and dependencies?
  - What if we could break down large tasks into subtasks automatically?

### Workflow Variation 1B: Self-Guided Exploration Flow

**User Goal**: Explore the platform independently and discover features organically while setting up her workspace.

**Business Goal**: Accommodate users who prefer self-discovery while ensuring they don't miss critical features, maintaining engagement through progressive disclosure.

**Screen Flow**:

**1.0 Main Dashboard [type: primary]**
- HOW IT IS REACHED: Direct navigation after account creation (skipping guided onboarding)
- NAVBAR PRESENCE: Yes
- Page Goal: Provide immediate value and clear navigation options for self-directed exploration
- Screen Description:
  1. Empty state with "Create Your First Project" CTA
  2. Feature highlights carousel
  3. Quick action buttons (Import Data, Browse Templates, Invite Team)
  4. Getting started checklist (collapsible)
  5. Recent activity placeholder
  6. Help resources and tutorials
- Design Problems:
  - HMW provide guidance without being prescriptive?
  - HMW showcase platform capabilities in an empty state?
  - HMW prevent users from feeling lost or overwhelmed?
- Design Opportunities:
  - What if we could show contextual tips based on user behavior?
  - What if we provided multiple entry points for different user preferences?
  - What if we could learn from user exploration patterns to improve onboarding?

**3.0 Templates Gallery [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item or "Browse Templates" button
- NAVBAR PRESENCE: Yes
- Page Goal: Help users discover relevant project structures and accelerate setup
- Screen Description:
  1. Template categories (Marketing, Development, Operations, etc.)
  2. Featured templates carousel
  3. Template cards with preview, description, and use count
  4. Search and filter functionality
  5. "Use Template" CTA on each card
  6. "Create Custom Template" option
- Design Problems:
  - HMW help users find relevant templates quickly?
  - HMW communicate template value and applicability?
  - HMW encourage template adoption without limiting creativity?
- Design Opportunities:
  - What if we could recommend templates based on user's industry or role?
  - What if we showed success stories from teams using each template?
  - What if users could customize templates before applying them?

**3.0-D Template Preview [type: detail]**
- HOW IT IS REACHED: Clicking on a template card
- NAVBAR PRESENCE: No
- Page Goal: Provide detailed template information to support informed decision-making
- Screen Description:
  1. Template overview and description
  2. Sample task structure and timeline
  3. Recommended team roles and responsibilities
  4. Success metrics and outcomes
  5. "Use This Template" primary CTA
  6. "Customize Template" secondary option
  7. Related templates suggestions
- Design Problems:
  - HMW provide enough detail without overwhelming users?
  - HMW show template value and expected outcomes?
  - HMW make template adoption feel low-risk?
- Design Opportunities:
  - What if we could show ROI data from teams using this template?
  - What if we provided video walkthroughs of template implementation?
  - What if we could connect users with template creators or successful users?

**NAVIGATION STRUCTURE**: 1.0 Welcome Dashboard | 2.0 Project Dashboard | 3.0 Templates Gallery

**USER JOURNEY FLOW**: 
- Variation 1A: 1.0 Welcome Dashboard → [click Start Project] → Pu.1 Project Creation Modal → [submit] → 2.0 Project Dashboard → [click Add Task] → Pu.2 Task Creation Drawer → [submit] → 2.0 Project Dashboard (updated)
- Variation 1B: 1.0 Main Dashboard → [click Browse Templates] → 3.0 Templates Gallery → [click template card] → 3.0-D Template Preview → [click Use Template] → 2.0 Project Dashboard

---

## Scenario 2: Experienced User Managing Multiple Projects

**Scenario Context**: Marcus, a senior operations manager, has been using the platform for 6 months and manages 8 active projects across different departments. He needs to quickly assess project health, reallocate resources, and identify bottlenecks across his portfolio.

### Workflow Variation 2A: Portfolio Overview Focus

**User Goal**: Get a comprehensive view of all projects, identify issues requiring attention, and make informed resource allocation decisions efficiently.

**Business Goal**: Increase user engagement and platform stickiness by providing powerful portfolio management capabilities that become essential to daily workflow.

**Screen Flow**:

**4.0 Portfolio Dashboard [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item
- NAVBAR PRESENCE: Yes
- Page Goal: Provide comprehensive portfolio overview with actionable insights
- Screen Description:
  1. Portfolio health summary with key metrics
  2. Project cards with status, progress, and risk indicators
  3. Resource allocation overview across projects
  4. Upcoming deadlines and overdue items
  5. Team workload distribution chart
  6. Quick action buttons for common tasks
  7. Customizable dashboard widgets
- Design Problems:
  - HMW present complex multi-project data in a digestible format?
  - HMW help users quickly identify projects needing attention?
  - HMW balance overview with actionable detail?
- Design Opportunities:
  - What if we could predict project risks using AI analysis?
  - What if we could automatically suggest resource reallocation?
  - What if we provided portfolio-level insights and recommendations?

**4.0-D Project Health Detail [type: detail]**
- HOW IT IS REACHED: Clicking on a project card's health indicator
- NAVBAR PRESENCE: No
- Page Goal: Provide detailed project analysis and specific recommendations
- Screen Description:
  1. Project health score breakdown
  2. Risk factors and mitigation suggestions
  3. Resource utilization analysis
  4. Timeline and milestone progress
  5. Team performance metrics
  6. Recommended actions prioritized by impact
  7. Historical trend analysis
- Design Problems:
  - HMW make complex project analytics accessible to non-technical users?
  - HMW provide actionable insights rather than just data?
  - HMW help users understand root causes of project issues?
- Design Opportunities:
  - What if we could benchmark project performance against similar projects?
  - What if we could simulate the impact of different resource allocation scenarios?
  - What if we provided automated alerts for critical project health changes?

### Workflow Variation 2B: Resource Management Focus

**User Goal**: Optimize team member allocation across projects, identify overloaded team members, and balance workloads effectively.

**Business Goal**: Demonstrate advanced resource management capabilities that justify premium pricing and increase user dependency on the platform.

**Screen Flow**:

**5.0 Resource Management [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item
- NAVBAR PRESENCE: Yes
- Page Goal: Enable effective resource allocation and workload balancing across projects
- Screen Description:
  1. Team member workload overview with capacity indicators
  2. Drag-and-drop task reassignment interface
  3. Skills matrix and availability calendar
  4. Workload distribution charts by time period
  5. Resource conflict alerts and suggestions
  6. Bulk reassignment tools
  7. Capacity planning projections
- Design Problems:
  - HMW make resource reallocation feel intuitive and low-risk?
  - HMW help users understand the impact of resource changes?
  - HMW balance automation with user control?
- Design Opportunities:
  - What if we could automatically suggest optimal resource allocation?
  - What if we could predict team member burnout risk?
  - What if we could recommend skill development based on project needs?

**Pu.3 Bulk Task Reassignment Modal [type: modal]**
- HOW IT IS REACHED: Clicking "Bulk Reassign" CTA button
- NAVBAR PRESENCE: No
- Page Goal: Enable efficient reassignment of multiple tasks while maintaining project continuity
- Screen Description:
  1. Task selection with filtering options
  2. Current assignee and new assignee columns
  3. Impact preview showing workload changes
  4. Reassignment reason dropdown
  5. Notification settings for affected team members
  6. "Preview Changes" and "Apply Reassignment" CTAs
  7. Rollback option for recent changes
- Design Problems:
  - HMW make bulk operations feel safe and reversible?
  - HMW communicate the impact of changes to all stakeholders?
  - HMW maintain task context and continuity during reassignment?
- Design Opportunities:
  - What if we could suggest optimal reassignment combinations?
  - What if we could automatically notify affected stakeholders with context?
  - What if we could track the success rate of different reassignment patterns?

**NAVIGATION STRUCTURE**: 4.0 Portfolio Dashboard | 5.0 Resource Management

**USER JOURNEY FLOW**:
- Variation 2A: 4.0 Portfolio Dashboard → [click project health indicator] → 4.0-D Project Health Detail → [click back] → 4.0 Portfolio Dashboard
- Variation 2B: 5.0 Resource Management → [click Bulk Reassign] → Pu.3 Bulk Task Reassignment Modal → [submit] → 5.0 Resource Management (updated)

---

## Scenario 3: Team Collaboration and Communication

**Scenario Context**: Lisa, a marketing coordinator, needs to collaborate with her design and content team on a product launch campaign. She needs to coordinate deliverables, share feedback, and ensure everyone stays aligned on project goals and timelines.

### Workflow Variation 3A: Task-Centric Collaboration

**User Goal**: Efficiently coordinate task dependencies, share feedback, and maintain clear communication around specific deliverables.

**Business Goal**: Increase team collaboration features usage and reduce churn by making the platform essential for team communication and coordination.

**Screen Flow**:

**2.1 Project Dashboard - Collaboration View [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item, toggling to collaboration view
- NAVBAR PRESENCE: Yes
- Page Goal: Facilitate team coordination and communication around project tasks
- Screen Description:
  1. Task board with collaboration indicators
  2. Team member activity feed
  3. Pending approvals and feedback requests
  4. Shared files and resources section
  5. Team communication panel
  6. Dependency visualization
  7. Collaboration metrics and engagement scores
- Design Problems:
  - HMW make task dependencies clear and manageable?
  - HMW encourage proactive communication around blockers?
  - HMW balance individual focus with team awareness?
- Design Opportunities:
  - What if we could automatically detect potential collaboration bottlenecks?
  - What if we could suggest optimal communication channels for different types of updates?
  - What if we could gamify team collaboration to increase engagement?

**2.1-D Task Collaboration Detail [type: detail]**
- HOW IT IS REACHED: Clicking on a task card with collaboration indicators
- NAVBAR PRESENCE: No
- Page Goal: Provide comprehensive task context and facilitate focused collaboration
- Screen Description:
  1. Task details with full context and history
  2. Threaded comments and feedback
  3. File attachments and version history
  4. Approval workflow status
  5. Related tasks and dependencies
  6. Time tracking and effort estimation
  7. Collaboration timeline and participant list
- Design Problems:
  - HMW keep task discussions focused and actionable?
  - HMW make file collaboration seamless and version-controlled?
  - HMW ensure important updates don't get lost in conversation?
- Design Opportunities:
  - What if we could automatically summarize long discussion threads?
  - What if we could suggest relevant team members to include in discussions?
  - What if we could integrate with external collaboration tools seamlessly?

### Workflow Variation 3B: Communication-Centric Collaboration

**User Goal**: Maintain clear team communication, share updates efficiently, and ensure everyone has visibility into project progress and decisions.

**Business Goal**: Position the platform as a comprehensive team collaboration solution, reducing dependency on external communication tools.

**Screen Flow**:

**6.0 Team Communication Hub [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item
- NAVBAR PRESENCE: Yes
- Page Goal: Centralize team communication and provide context-aware collaboration tools
- Screen Description:
  1. Unified activity feed across all projects
  2. Direct messaging and team channels
  3. Announcement and update broadcasting
  4. Meeting notes and decision tracking
  5. Shared resource library
  6. Team availability and status indicators
  7. Communication preferences and notification settings
- Design Problems:
  - HMW prevent communication overload while maintaining transparency?
  - HMW make important information discoverable and searchable?
  - HMW balance real-time communication with focused work time?
- Design Opportunities:
  - What if we could automatically route communications to appropriate channels?
  - What if we could provide AI-powered meeting summaries and action items?
  - What if we could suggest optimal communication timing based on team patterns?

**Pu.4 Team Update Composer [type: modal]**
- HOW IT IS REACHED: Clicking "Share Update" CTA button
- NAVBAR PRESENCE: No
- Page Goal: Enable efficient sharing of project updates and important information
- Screen Description:
  1. Update type selection (Progress, Blocker, Decision, etc.)
  2. Rich text editor with formatting options
  3. Audience selection (specific team members, projects, or company-wide)
  4. Attachment and link embedding
  5. Urgency level and notification preferences
  6. Scheduling options for delayed sending
  7. Template suggestions based on update type
- Design Problems:
  - HMW encourage regular communication without creating notification fatigue?
  - HMW help users craft clear and actionable updates?
  - HMW ensure updates reach the right audience at the right time?
- Design Opportunities:
  - What if we could suggest update content based on recent project activity?
  - What if we could automatically format updates for different audiences?
  - What if we could track update effectiveness and engagement?

**NAVIGATION STRUCTURE**: 2.1 Project Dashboard - Collaboration View | 6.0 Team Communication Hub

**USER JOURNEY FLOW**:
- Variation 3A: 2.1 Project Dashboard - Collaboration View → [click task with collaboration] → 2.1-D Task Collaboration Detail → [add comment/feedback] → 2.1 Project Dashboard - Collaboration View (updated)
- Variation 3B: 6.0 Team Communication Hub → [click Share Update] → Pu.4 Team Update Composer → [submit] → 6.0 Team Communication Hub (updated)

---

## Scenario 4: Performance Analytics and Reporting

**Scenario Context**: David, a department head, needs to prepare quarterly performance reports for executive leadership. He requires comprehensive analytics on team productivity, project success rates, and resource utilization to demonstrate department value and plan for the next quarter.

### Workflow Variation 4A: Executive Reporting Focus

**User Goal**: Generate comprehensive performance reports with executive-level insights and recommendations for strategic decision-making.

**Business Goal**: Demonstrate platform value through robust analytics capabilities, justifying enterprise pricing and increasing executive buy-in.

**Screen Flow**:

**7.0 Analytics Dashboard [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item
- NAVBAR PRESENCE: Yes
- Page Goal: Provide comprehensive performance analytics with executive-level insights
- Screen Description:
  1. Key performance indicators with trend analysis
  2. Customizable reporting widgets
  3. Team productivity metrics and benchmarks
  4. Project success rate analysis
  5. Resource utilization optimization insights
  6. Predictive analytics and forecasting
  7. Export and sharing options for reports
- Design Problems:
  - HMW present complex analytics in an accessible format for non-technical users?
  - HMW provide actionable insights rather than just raw data?
  - HMW enable customization without overwhelming users with options?
- Design Opportunities:
  - What if we could automatically generate narrative insights from data?
  - What if we could benchmark performance against industry standards?
  - What if we could provide predictive recommendations for performance improvement?

**Pu.5 Report Builder [type: modal]**
- HOW IT IS REACHED: Clicking "Create Report" CTA button
- NAVBAR PRESENCE: No
- Page Goal: Enable creation of customized reports tailored to specific audiences and purposes
- Screen Description:
  1. Report template selection (Executive Summary, Team Performance, etc.)
  2. Data source and time period selection
  3. Metric and visualization customization
  4. Audience and formatting preferences
  5. Automated scheduling options
  6. Collaboration and review workflow
  7. Export format selection (PDF, PowerPoint, etc.)
- Design Problems:
  - HMW make report creation intuitive for users without analytics expertise?
  - HMW ensure reports tell a compelling story with data?
  - HMW balance customization with ease of use?
- Design Opportunities:
  - What if we could suggest report content based on audience and purpose?
  - What if we could automatically update reports with real-time data?
  - What if we could provide report templates optimized for different executive preferences?

### Workflow Variation 4B: Operational Insights Focus

**User Goal**: Identify operational inefficiencies, optimize team performance, and make data-driven decisions for process improvement.

**Business Goal**: Increase platform stickiness by providing operational insights that become essential for daily management decisions.

**Screen Flow**:

**7.1 Operational Insights [type: primary]**
- HOW IT IS REACHED: Direct navigation via sidebar nav item or toggle from Analytics Dashboard
- NAVBAR PRESENCE: Yes
- Page Goal: Provide actionable operational insights for process optimization
- Screen Description:
  1. Process efficiency analysis with bottleneck identification
  2. Team performance patterns and optimization suggestions
  3. Resource allocation effectiveness metrics
  4. Workflow automation opportunities
  5. Quality metrics and improvement tracking
  6. Cost analysis and ROI calculations
  7. Comparative analysis across teams and projects
- Design Problems:
  - HMW help users identify the most impactful optimization opportunities?
  - HMW make operational data actionable for immediate implementation?
  - HMW balance detail with overview for different management levels?
- Design Opportunities:
  - What if we could automatically identify and prioritize improvement opportunities?
  - What if we could simulate the impact of different process changes?
  - What if we could provide step-by-step implementation guides for optimizations?

**7.1-D Process Optimization Detail [type: detail]**
- HOW IT IS REACHED: Clicking on a specific optimization opportunity
- NAVBAR PRESENCE: No
- Page Goal: Provide detailed analysis and implementation guidance for specific optimizations
- Screen Description:
  1. Detailed problem analysis with root cause identification
  2. Impact assessment and potential ROI calculation
  3. Step-by-step implementation plan
  4. Resource requirements and timeline estimation
  5. Risk assessment and mitigation strategies
  6. Success metrics and tracking plan
  7. Similar case studies and best practices
- Design Problems:
  - HMW make complex optimization recommendations accessible and actionable?
  - HMW provide confidence in the potential impact of changes?
  - HMW support implementation with practical guidance?
- Design Opportunities:
  - What if we could provide real-time coaching during optimization implementation?
  - What if we could connect users with others who have successfully implemented similar optimizations?
  - What if we could track and share the success stories of optimization implementations?

**NAVIGATION STRUCTURE**: 7.0 Analytics Dashboard | 7.1 Operational Insights

**USER JOURNEY FLOW**:
- Variation 4A: 7.0 Analytics Dashboard → [click Create Report] → Pu.5 Report Builder → [configure and submit] → 7.0 Analytics Dashboard (with new report)
- Variation 4B: 7.1 Operational Insights → [click optimization opportunity] → 7.1-D Process Optimization Detail → [review implementation plan] → 7.1 Operational Insights

---

## Universal Screens

**8.0 Settings [type: settings]**
- HOW IT IS REACHED: Clicking settings icon/link in navigation
- NAVBAR PRESENCE: No
- Page Goal: Centralize all user preferences and system configurations
- Screen Description:
  1. **Account Settings**: Profile information, password, security
  2. **Notification Preferences**: Email, push, in-app notification controls
  3. **Display Options**: Theme selection, layout preferences, dashboard customization
  4. **Accessibility Settings**: 
     - Screen reader compatibility toggle
     - High contrast mode toggle
     - Font size adjustment
     - Keyboard navigation preferences
     - Motor accessibility options
  5. **Integration Settings**: Connected apps, API access, data sync preferences
  6. **Privacy Controls**: Data sharing, analytics opt-out, export options
  7. **Team Settings**: Role management, permission controls (for admins)
- Design Problems:
  - HMW organize numerous settings without overwhelming users?
  - HMW make accessibility options discoverable and easy to configure?
  - HMW provide appropriate controls for different user roles?
- Design Opportunities:
  - What if we could automatically suggest optimal settings based on user behavior?
  - What if we could provide guided setup for complex integrations?
  - What if we could learn from user preferences to improve default settings?

**Error States**:

**Er.1 Network Connection Error [type: state]**
- Condition of existing screens when network connectivity is lost
- Shows retry options and offline capability information
- Maintains user context and unsaved work

**Er.2 Permission Denied [type: state]**
- Condition when users attempt unauthorized actions
- Provides clear explanation and alternative actions
- Includes contact information for access requests

**Er.3 Data Loading Failure [type: state]**
- Condition when data cannot be retrieved
- Offers refresh options and alternative navigation
- Maintains page structure with skeleton loading states

---

## ACCESSIBILITY NOTES

**Keyboard Navigation**:
- All primary screens support full keyboard navigation with logical tab order
- Skip links provided for main content areas
- Focus indicators clearly visible with 3px outline in brand color
- Escape key closes all modals and returns focus to trigger element

**ARIA Labels and Landmarks**:
- Main navigation marked with navigation landmark
- Content areas use main landmark
- Sidebar uses complementary landmark
- Form sections use appropriate fieldset and legend elements
- Dynamic content changes announced via aria-live regions

**Screen Reader Announcements**:
- Page title changes announced on navigation
- Form validation errors announced immediately
- Progress updates announced for long-running operations
- New notifications announced when received

**High Contrast Mode**:
- Toggled via Settings screen under Accessibility Settings
- Applies as CSS class site-wide affecting all screens
- Maintains minimum 7:1 contrast ratio for all text
- Preserves all functionality and layout structure

**Focus Management**:
- Focus trapped within modals during interaction
- Focus returned to trigger element when modals close
- Focus moves logically through form fields and interactive elements
- Focus visible indicators meet WCAG 2.1 AA standards

**Minimum Touch Targets**:
- All interactive elements minimum 44px touch target
- Adequate spacing between adjacent interactive elements
- Applies across all screen types and viewport sizes

---

## VIEWPORT BEHAVIOUR

**Desktop (1024px+)**:
- Full sidebar navigation with expanded labels
- Multi-column layouts for dashboard widgets
- Hover states and tooltips for enhanced interaction
- Drag-and-drop functionality fully enabled
- Modal dialogs centered with backdrop overlay

**Tablet (768px–1023px)**:
- Collapsible sidebar with icon-only navigation
- Responsive grid layouts with 2-column maximum
- Touch-optimized interaction targets
- Swipe gestures for navigation between sections
- Modal dialogs adapted for touch interaction

**Mobile (320px–767px)**:
- Bottom navigation bar replaces sidebar
- Single-column layouts throughout
- Full-screen modals replace overlay dialogs
- Optimized for thumb navigation
- Simplified data tables with horizontal scroll
- Progressive disclosure for complex information

These viewport adaptations apply universally across all primary screens and maintain full functionality while optimizing for each device context.