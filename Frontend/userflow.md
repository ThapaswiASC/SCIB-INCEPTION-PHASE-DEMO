<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kanban Board User Flow Diagram</title>
    <script type="module">
        import mermaid from 'https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.esm.min.mjs';
        mermaid.initialize({ startOnLoad: true });
    </script>
    <style>
        body {
            font-family: Arial, sans-serif;
            padding: 40px;
            background: #f5f5f5;
            margin: 0;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
        }
        h1 {
            text-align: center;
            color: #333;
            margin-bottom: 30px;
        }
        .mermaid {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            margin: 20px 0;
        }
        .description {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Kanban Board Application - User Flow Diagram</h1>
        
        <div class="description">
            <h3>User Flow Overview</h3>
            <p>This diagram illustrates the complete user journey through the Kanban Board application, including task management, collaboration features, analytics, and configuration options.</p>
        </div>

        <div class="mermaid">
flowchart TD
    Start[User Opens Application]
    Start --> Login[Authentication Check]
    Login --> Dashboard[Load Dashboard]
    
    Dashboard --> Header[Header Navigation]
    Dashboard --> Sidebar[Sidebar Menu]
    Dashboard --> MainContent[Main Content Area]
    
    Header --> Search[Search Tasks]
    Header --> Notifications[View Notifications]
    Header --> UserProfile[User Profile]
    
    Sidebar --> KanbanMenu[Kanban Board]
    Sidebar --> CollabMenu[Collaborative Board]
    Sidebar --> AnalyticsMenu[Progress Analytics]
    Sidebar --> ReportsMenu[Report Builder]
    Sidebar --> ConfigMenu[Board Configuration]
    
    KanbanMenu --> KanbanBoard[Display Kanban Board]
    KanbanBoard --> TodoColumn[To Do Column]
    KanbanBoard --> ProgressColumn[In Progress Column]
    KanbanBoard --> DoneColumn[Done Column]
    
    TodoColumn --> ViewTask[View Task Details]
    ProgressColumn --> ViewTask
    DoneColumn --> ViewTask
    
    TodoColumn --> MoveTask[Move Task Between Columns]
    ProgressColumn --> MoveTask
    DoneColumn --> MoveTask
    
    KanbanBoard --> AddTaskBtn[Click Add Task Button]
    AddTaskBtn --> AddTaskModal[Open Add Task Modal]
    AddTaskModal --> FillForm[Fill Task Form]
    FillForm --> SubmitTask[Submit New Task]
    SubmitTask --> RefreshBoard[Refresh Kanban Board]
    RefreshBoard --> KanbanBoard
    
    ViewTask --> TaskDetail[Task Detail View]
    TaskDetail --> EditTask[Edit Task]
    TaskDetail --> AssignTask[Assign Team Member]
    TaskDetail --> DeleteTask[Delete Task]
    
    EditTask --> UpdateForm[Update Task Form]
    UpdateForm --> SaveChanges[Save Changes]
    SaveChanges --> RefreshBoard
    
    AssignTask --> TeamModal[Team Assignment Modal]
    TeamModal --> SelectMember[Select Team Member]
    SelectMember --> ConfirmAssign[Confirm Assignment]
    ConfirmAssign --> RefreshBoard
    
    DeleteTask --> ConfirmDelete[Confirm Deletion]
    ConfirmDelete --> RemoveTask[Remove from Board]
    RemoveTask --> RefreshBoard
    
    MoveTask --> DragDrop[Drag and Drop]
    DragDrop --> UpdateStatus[Update Task Status]
    UpdateStatus --> RefreshBoard
    
    Search --> FilterResults[Filter Task Results]
    FilterResults --> DisplayFiltered[Display Filtered Tasks]
    DisplayFiltered --> KanbanBoard
    
    CollabMenu --> CollabBoard[Collaborative Board View]
    CollabBoard --> RealTimeUpdates[Real-time Updates]
    CollabBoard --> ActivityFeed[Activity Feed]
    CollabBoard --> TeamChat[Team Communication]
    
    AnalyticsMenu --> Analytics[Progress Analytics Dashboard]
    Analytics --> ViewMetrics[View Performance Metrics]
    Analytics --> ProgressCharts[Progress Charts]
    Analytics --> TeamStats[Team Statistics]
    
    ViewMetrics --> TimeRange[Select Time Range]
    TimeRange --> RefreshAnalytics[Refresh Analytics Data]
    RefreshAnalytics --> Analytics
    
    ReportsMenu --> ReportBuilder[Report Builder Dashboard]
    ReportBuilder --> SelectTemplate[Select Report Template]
    ReportBuilder --> CustomReport[Create Custom Report]
    ReportBuilder --> RecentReports[View Recent Reports]
    
    SelectTemplate --> ConfigReport[Configure Report Parameters]
    CustomReport --> ConfigReport
    ConfigReport --> GenerateReport[Generate Report]
    GenerateReport --> DownloadReport[Download Report]
    
    ConfigMenu --> BoardConfig[Board Configuration Panel]
    BoardConfig --> ColumnSettings[Column Settings]
    BoardConfig --> WorkflowRules[Workflow Rules]
    BoardConfig --> AutomationRules[Automation Rules]
    
    ColumnSettings --> EditColumns[Edit Column Properties]
    EditColumns --> SaveColumnConfig[Save Column Configuration]
    SaveColumnConfig --> RefreshBoard
    
    WorkflowRules --> DefineRules[Define Workflow Rules]
    DefineRules --> SaveWorkflow[Save Workflow Configuration]
    SaveWorkflow --> RefreshBoard
    
    AutomationRules --> SetAutomation[Set Automation Rules]
    SetAutomation --> SaveAutomation[Save Automation Configuration]
    SaveAutomation --> RefreshBoard
    
    Notifications --> ViewAlerts[View System Alerts]
    ViewAlerts --> MarkRead[Mark as Read]
    ViewAlerts --> NotificationSettings[Notification Settings]
    
    UserProfile --> ProfileSettings[Profile Settings]
    UserProfile --> AccountSettings[Account Settings]
    UserProfile --> Logout[Logout]
    
    ProfileSettings --> UpdateProfile[Update Profile Information]
    UpdateProfile --> SaveProfile[Save Profile Changes]
    
    AccountSettings --> SecuritySettings[Security Settings]
    AccountSettings --> PreferenceSettings[Preference Settings]
    
    Logout --> Login
        </div>
        
        <div class="description">
            <h3>Key User Flows</h3>
            <ul>
                <li><strong>Task Management:</strong> Create, edit, move, assign, and delete tasks across Kanban columns</li>
                <li><strong>Collaboration:</strong> Real-time updates, activity feeds, and team communication</li>
                <li><strong>Analytics:</strong> View progress metrics, charts, and team statistics</li>
                <li><strong>Reporting:</strong> Generate custom reports using templates or custom configurations</li>
                <li><strong>Configuration:</strong> Manage board settings, workflow rules, and automation</li>
                <li><strong>Navigation:</strong> Search functionality, notifications, and user profile management</li>
            </ul>
        </div>
    </div>
</body>
</html>