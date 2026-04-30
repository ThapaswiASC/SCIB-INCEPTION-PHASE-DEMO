# UI COMPONENT SPECIFICATIONS

## 1. INPUT REFERENCE (FROM AGENT-1)

**Page Name:** Kanban Board Dashboard

**Mapped Layout:** CSS Grid and Flexbox Mixed Layout
- Main container uses Flexbox (sidebar + main)
- Kanban board uses CSS Grid (3-column layout)
- Individual components use Flexbox for internal layout

**Components from A1:**
- KanbanBoardComponent (primary component)
- KanbanColumnComponent (three instances)
- TaskCardComponent (multiple instances)
- HeaderComponent
- SidebarComponent
- AddTaskModalComponent
- TaskDetailComponent
- ProgressAnalyticsComponent
- ReportBuilderComponent
- BoardConfigComponent

## 2. GLOBAL ARCHITECTURE SUMMARY

**Layout Type:** Grid / Flex / Mixed

**Main Sections:**
- Header (search, notifications, user avatar)
- Sidebar (navigation menu with task management, analytics, configuration)
- Main Content (three-column Kanban board)
- Modals (task creation, team assignment, configuration)

**Component Strategy:**

**Reused Components:**
- HeaderComponent → uicomplibraryplay/header
- SidebarComponent → uicomplibraryplay/navigation
- CardComponent → uicomplibraryplay/card
- ButtonComponent → uicomplibraryplay/button
- ModalComponent → uicomplibraryplay/modal
- FormFieldComponent → uicomplibraryplay/form
- BadgeComponent → uicomplibraryplay/badge
- AvatarComponent → uicomplibraryplay/avatar
- ToggleComponent → uicomplibraryplay/toggle
- SearchComponent → uicomplibraryplay/search

**New Components (if any):**
- KanbanBoardComponent (feature-specific)
- KanbanColumnComponent (feature-specific)
- TaskCardComponent (feature-specific)
- TaskDetailComponent (feature-specific)

## 3. FOLDER STRUCTURE

```
src/
 ├── app/
 │    ├── features/
 │    │   ├── kanban/
 │    │   │   ├── components/
 │    │   │   │   ├── kanban-board/
 │    │   │   │   │   ├── kanban-board.component.ts
 │    │   │   │   │   ├── kanban-board.component.html
 │    │   │   │   │   ├── kanban-board.component.scss
 │    │   │   │   │   └── kanban-board.component.spec.ts
 │    │   │   │   ├── kanban-column/
 │    │   │   │   └── task-card/
 │    │   │   ├── services/
 │    │   │   └── models/
 │    │   ├── analytics/
 │    │   ├── reports/
 │    │   └── configuration/
 │    ├── shared/
 │    │   ├── components/
 │    │   │   ├── layout/
 │    │   │   ├── ui/
 │    │   │   └── modals/
 │    │   ├── services/
 │    │   ├── models/
 │    │   └── guards/
 │    └── core/
 │        ├── interceptors/
 │        └── constants/
 ├── assets/
 └── environments/
```

## 4. COMPONENT IMPLEMENTATION

### 4.1 KanbanBoardComponent

**Component Type:** Feature

**Mapped From A1:** Primary Kanban board container with three-column layout

**Purpose:** Main container for three-column Kanban layout with drag-and-drop functionality

**Parent:** AppComponent

**Children:**
- KanbanColumnComponent (3 instances)
- AddTaskModalComponent
- TaskDetailComponent

**Dependencies:**
- TaskService
- NotificationService
- AuthService

**Library Components Used:**
- CardComponent → uicomplibraryplay/card
- ButtonComponent → uicomplibraryplay/button
- LoadingComponent → uicomplibraryplay/loading

**TypeScript Specification**

**Inputs:**
- tasks: Task[] (array of task objects)
- loading: boolean (loading state)
- error: string | null (error message)

**Outputs:**
- onTaskCreate: EventEmitter<Task> (task creation event)
- onTaskUpdate: EventEmitter<Task> (task update event)
- onTaskMove: EventEmitter<{taskId: string, newStatus: string}> (task move event)
- onTaskSelect: EventEmitter<string> (task selection event)

**State:**
- selectedTask: Task | null (currently selected task)
- draggedTask: Task | null (task being dragged)
- columnData: KanbanColumn[] (column configuration)
- isModalOpen: boolean (modal visibility state)

**Methods:**
- ngOnInit() → Initialize component and load data
- loadTasks() → Fetch tasks from API
- onTaskDrop(event: CdkDragDrop<Task[]>) → Handle task drop events
- openAddTaskModal() → Open task creation modal
- closeModal() → Close active modal

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<div class="kanban-board-container" role="region" aria-label="Kanban Board">
  <div class="board-header">
    <h1>Kanban Board</h1>
    <button (click)="openAddTaskModal()">Add Task</button>
  </div>
  
  <div class="kanban-columns" *ngIf="!loading; else loadingTemplate">
    <app-kanban-column 
      *ngFor="let column of columnData" 
      [column]="column"
      [tasks]="getTasksByStatus(column.id)"
      (onTaskDrop)="onTaskDrop($event)"
      (onTaskAdd)="openAddTaskModal()"
      cdkDropList
      [cdkDropListData]="getTasksByStatus(column.id)">
    </app-kanban-column>
  </div>
  
  <ng-template #loadingTemplate>
    <app-loading></app-loading>
  </ng-template>
  
  <div *ngIf="error" class="error-message" role="alert">
    {{ error }}
  </div>
  
  <app-add-task-modal 
    [isOpen]="isModalOpen"
    (onSubmit)="onTaskCreate($event)"
    (onCancel)="closeModal()">
  </app-add-task-modal>
</div>
```

**CSS SPECIFICATION**

**Layout:**
- display: grid
- grid-template-columns: repeat(3, 1fr)
- gap: 1rem
- min-height: 100vh

**Spacing:**
- padding: 1rem
- gap: 1rem between columns

**Component Styles:**
- .kanban-board-container: main container
- .board-header: flex layout for title and actions
- .kanban-columns: grid layout for columns
- .error-message: error state styling

**Responsive:**
- Desktop (>1024px): 3 columns
- Tablet (768px-1024px): 2 columns, horizontal scroll
- Mobile (<768px): 1 column, vertical stack

**API INTEGRATION**

- loadTasks() → GET /api/tasks → Load all tasks with status filtering
- onTaskCreate() → POST /api/tasks → Create new task
- onTaskUpdate() → PUT /api/tasks/{id} → Update existing task
- onTaskMove() → PUT /api/tasks/{id} → Update task status

**ERROR HANDLING**

- API failure → show error message with retry option
- Network error → display offline indicator
- Validation error → inline form validation messages

**INTERACTION FLOW**

**User Action → UI → API**
- Click "Add Task" → open AddTaskModalComponent → submit form → POST /api/tasks
- Drag task between columns → update UI optimistically → PUT /api/tasks/{id}
- Click task card → open TaskDetailComponent → GET /api/tasks/{id}

### 4.2 KanbanColumnComponent

**Component Type:** Feature

**Mapped From A1:** Individual column container (To Do, In Progress, Done)

**Purpose:** Container for tasks in specific status with drop zone functionality

**Parent:** KanbanBoardComponent

**Children:**
- TaskCardComponent (multiple instances)

**Dependencies:**
- CdkDrag (Angular CDK)
- CdkDropList (Angular CDK)

**Library Components Used:**
- CardComponent → uicomplibraryplay/card
- BadgeComponent → uicomplibraryplay/badge

**TypeScript Specification**

**Inputs:**
- column: KanbanColumn (column configuration)
- tasks: Task[] (tasks for this column)
- title: string (column title)
- count: number (task count)

**Outputs:**
- onTaskDrop: EventEmitter<CdkDragDrop<Task[]>> (task drop event)
- onTaskAdd: EventEmitter<void> (add task event)

**State:**
- isDropTarget: boolean (drop zone active state)
- isLoading: boolean (loading state for column)

**Methods:**
- onDrop(event: CdkDragDrop<Task[]>) → Handle task drop
- addTask() → Emit add task event
- getTaskCount() → Return filtered task count

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<div class="kanban-column" 
     cdkDropList 
     [cdkDropListData]="tasks"
     (cdkDropListDropped)="onDrop($event)"
     [class.drop-target]="isDropTarget">
  
  <div class="column-header" [attr.aria-labelledby]="column.id + '-header'">
    <h2 [id]="column.id + '-header'">{{ column.title }}</h2>
    <app-badge [count]="tasks.length"></app-badge>
    <button (click)="addTask()" aria-label="Add task to {{ column.title }}">
      <i class="icon-plus"></i>
    </button>
  </div>
  
  <div class="column-content">
    <app-task-card 
      *ngFor="let task of tasks; trackBy: trackByTaskId"
      [task]="task"
      [assignee]="task.assignee"
      [badges]="task.badges"
      cdkDrag
      [cdkDragData]="task">
    </app-task-card>
    
    <div *ngIf="tasks.length === 0" class="empty-column">
      <p>No tasks in {{ column.title }}</p>
      <button (click)="addTask()">Add First Task</button>
    </div>
  </div>
</div>
```

**CSS SPECIFICATION**

**Layout:**
- display: flex
- flex-direction: column
- min-height: 500px
- background: #f8f9fa

**Spacing:**
- padding: 1rem
- margin-bottom: 1rem

**Component Styles:**
- .kanban-column: column container
- .column-header: flex layout for title and actions
- .column-content: scrollable task area
- .drop-target: highlight when drag target
- .empty-column: empty state styling

**Responsive:**
- Mobile: full width, reduced padding
- Tablet: flexible width with min-width
- Desktop: equal flex-grow

**API INTEGRATION**

- Task filtering by column status
- Real-time updates when tasks move between columns

**ERROR HANDLING**

- Drop operation failure → revert UI state
- Task loading error → show column error state

**INTERACTION FLOW**

**User Action → UI → API**
- Drop task in column → update task status → PUT /api/tasks/{id}
- Click add task → emit event to parent → open modal

### 4.3 TaskCardComponent

**Component Type:** Feature

**Mapped From A1:** Individual task display card with metadata

**Purpose:** Display task information with interaction capabilities

**Parent:** KanbanColumnComponent

**Children:** None

**Dependencies:**
- DatePipe
- RouterModule

**Library Components Used:**
- CardComponent → uicomplibraryplay/card
- BadgeComponent → uicomplibraryplay/badge
- AvatarComponent → uicomplibraryplay/avatar
- ButtonComponent → uicomplibraryplay/button

**TypeScript Specification**

**Inputs:**
- task: Task (task object)
- assignee: User (assigned user)
- badges: Badge[] (status badges)

**Outputs:**
- onClick: EventEmitter<string> (card click event)
- onEdit: EventEmitter<Task> (edit task event)
- onDelete: EventEmitter<string> (delete task event)
- onAssign: EventEmitter<string> (assign task event)

**State:**
- isSelected: boolean (selection state)
- isEditing: boolean (edit mode state)
- showActions: boolean (action menu visibility)

**Methods:**
- onCardClick() → Emit click event
- toggleActions() → Show/hide action menu
- editTask() → Emit edit event
- deleteTask() → Emit delete event
- assignTask() → Emit assign event

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<app-card class="task-card" 
          [class.selected]="isSelected"
          (click)="onCardClick()"
          [attr.aria-label]="'Task: ' + task.title">
  
  <div class="card-header">
    <h3 class="task-title">{{ task.title }}</h3>
    <button class="actions-toggle" 
            (click)="toggleActions($event)"
            aria-label="Task actions">
      <i class="icon-more"></i>
    </button>
  </div>
  
  <div class="card-content">
    <p class="task-description">{{ task.description | slice:0:100 }}</p>
    
    <div class="task-metadata">
      <div class="badges">
        <app-badge 
          *ngFor="let badge of badges"
          [type]="badge.type"
          [text]="badge.text">
        </app-badge>
      </div>
      
      <div class="task-details">
        <span class="due-date" *ngIf="task.dueDate">
          Due: {{ task.dueDate | date:'MMM dd' }}
        </span>
        <span class="priority" [class]="'priority-' + task.priority">
          {{ task.priority }}
        </span>
      </div>
    </div>
  </div>
  
  <div class="card-footer">
    <app-avatar 
      *ngIf="assignee"
      [user]="assignee"
      [size]="'small'"
      [showTooltip]="true">
    </app-avatar>
    
    <div class="task-stats">
      <span *ngIf="task.commentCount">{{ task.commentCount }} comments</span>
      <span *ngIf="task.attachmentCount">{{ task.attachmentCount }} files</span>
    </div>
  </div>
  
  <div class="action-menu" *ngIf="showActions" (clickOutside)="showActions = false">
    <button (click)="editTask()">Edit</button>
    <button (click)="assignTask()">Assign</button>
    <button (click)="deleteTask()" class="danger">Delete</button>
  </div>
</app-card>
```

**CSS SPECIFICATION**

**Layout:**
- display: block
- margin-bottom: 0.5rem
- border-radius: 8px
- box-shadow: 0 2px 4px rgba(0,0,0,0.1)

**Spacing:**
- padding: 1rem
- gap: 0.5rem between elements

**Component Styles:**
- .task-card: main card styling
- .card-header: flex layout for title and actions
- .task-metadata: flex layout for badges and details
- .card-footer: flex layout for avatar and stats
- .action-menu: absolute positioned dropdown

**Responsive:**
- Mobile: reduced padding, stacked layout
- Desktop: hover effects, larger click targets

**API INTEGRATION**

- Task data display from GET /api/tasks
- Click events trigger navigation or modals

**ERROR HANDLING**

- Missing assignee data → show placeholder
- Image loading errors → fallback avatar

**INTERACTION FLOW**

**User Action → UI → API**
- Click card → navigate to detail view → GET /api/tasks/{id}
- Click edit → open edit modal → PUT /api/tasks/{id}
- Click delete → confirm dialog → DELETE /api/tasks/{id}

### 4.4 HeaderComponent

**Component Type:** Layout

**Mapped From A1:** Top navigation with search and user actions

**Purpose:** Global navigation header with search functionality

**Parent:** AppComponent

**Children:**
- SearchComponent
- AvatarComponent

**Dependencies:**
- AuthService
- NotificationService
- Router

**Library Components Used:**
- SearchComponent → uicomplibraryplay/search
- ButtonComponent → uicomplibraryplay/button
- AvatarComponent → uicomplibraryplay/avatar
- BadgeComponent → uicomplibraryplay/badge

**TypeScript Specification**

**Inputs:**
- user: User (current user object)
- notifications: Notification[] (notification array)

**Outputs:**
- onSearch: EventEmitter<string> (search query event)
- onNotificationClick: EventEmitter<void> (notification click event)
- onSettingsClick: EventEmitter<void> (settings click event)

**State:**
- searchQuery: string (current search query)
- isSearchFocused: boolean (search input focus state)
- notificationCount: number (unread notification count)

**Methods:**
- onSearchInput(query: string) → Handle search input
- clearSearch() → Clear search query
- openNotifications() → Open notification panel
- openSettings() → Navigate to settings
- logout() → Handle user logout

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<header class="app-header" role="banner">
  <div class="header-content">
    <div class="brand-section">
      <img src="assets/logo.svg" alt="TaskFlow Logo" class="logo">
      <h1 class="brand-title">TaskFlow</h1>
    </div>
    
    <div class="search-section">
      <app-search 
        [placeholder]="'Search tasks...'"
        [value]="searchQuery"
        (onSearch)="onSearchInput($event)"
        (onClear)="clearSearch()"
        [isFocused]="isSearchFocused">
      </app-search>
    </div>
    
    <div class="actions-section">
      <button class="notification-btn" 
              (click)="openNotifications()"
              [attr.aria-label]="'Notifications (' + notificationCount + ' unread)'">
        <i class="icon-bell"></i>
        <app-badge 
          *ngIf="notificationCount > 0"
          [count]="notificationCount"
          [type]="'danger'">
        </app-badge>
      </button>
      
      <button class="settings-btn" 
              (click)="openSettings()"
              aria-label="Settings">
        <i class="icon-settings"></i>
      </button>
      
      <div class="user-section">
        <app-avatar 
          [user]="user"
          [size]="'medium'"
          [showDropdown]="true"
          (onMenuClick)="logout()">
        </app-avatar>
      </div>
    </div>
  </div>
</header>
```

**CSS SPECIFICATION**

**Layout:**
- display: flex
- justify-content: space-between
- align-items: center
- height: 64px
- background: #ffffff
- border-bottom: 1px solid #e0e0e0

**Spacing:**
- padding: 0 1.5rem
- gap: 1rem between sections

**Component Styles:**
- .app-header: main header container
- .brand-section: flex layout for logo and title
- .search-section: flexible search area
- .actions-section: flex layout for action buttons
- .user-section: user avatar and dropdown

**Responsive:**
- Mobile: hide brand title, smaller search
- Tablet: condensed layout
- Desktop: full layout with all elements

**API INTEGRATION**

- onSearchInput() → GET /api/tasks (with search parameters)
- Notification data from notification service

**ERROR HANDLING**

- Search API failure → show search error state
- User data loading error → show default avatar

**INTERACTION FLOW**

**User Action → UI → API**
- Type in search → debounced search → GET /api/tasks?search=query
- Click notifications → open notification panel → GET /api/notifications
- Click settings → navigate to settings page

### 4.5 SidebarComponent

**Component Type:** Layout

**Mapped From A1:** Left navigation menu with sections

**Purpose:** Main navigation sidebar with collapsible sections

**Parent:** AppComponent

**Children:**
- NavigationComponent items

**Dependencies:**
- Router
- NavigationService

**Library Components Used:**
- NavigationComponent → uicomplibraryplay/navigation
- ButtonComponent → uicomplibraryplay/button
- ToggleComponent → uicomplibraryplay/toggle

**TypeScript Specification**

**Inputs:**
- menuItems: MenuItem[] (navigation menu items)
- activeItem: string (currently active menu item)

**Outputs:**
- onMenuItemClick: EventEmitter<string> (menu item click event)
- onToggleCollapse: EventEmitter<boolean> (sidebar collapse event)

**State:**
- isCollapsed: boolean (sidebar collapse state)
- activeSection: string (active navigation section)
- expandedSections: string[] (expanded section IDs)

**Methods:**
- toggleSidebar() → Toggle sidebar collapse
- navigateToItem(item: MenuItem) → Handle navigation
- toggleSection(sectionId: string) → Expand/collapse section
- isItemActive(item: MenuItem) → Check if item is active

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<aside class="sidebar" 
       [class.collapsed]="isCollapsed"
       role="navigation"
       aria-label="Main navigation">
  
  <div class="sidebar-header">
    <button class="collapse-toggle" 
            (click)="toggleSidebar()"
            [attr.aria-label]="isCollapsed ? 'Expand sidebar' : 'Collapse sidebar'">
      <i [class]="isCollapsed ? 'icon-expand' : 'icon-collapse'"></i>
    </button>
  </div>
  
  <nav class="sidebar-nav">
    <div class="nav-section" *ngFor="let section of menuItems">
      <h3 class="section-title" 
          (click)="toggleSection(section.id)"
          [attr.aria-expanded]="expandedSections.includes(section.id)">
        <i [class]="section.icon"></i>
        <span *ngIf="!isCollapsed">{{ section.title }}</span>
        <i class="expand-icon" 
           [class.rotated]="expandedSections.includes(section.id)"
           *ngIf="!isCollapsed">
        </i>
      </h3>
      
      <ul class="nav-items" 
          *ngIf="expandedSections.includes(section.id) || isCollapsed"
          [attr.aria-labelledby]="section.id + '-title'">
        <li *ngFor="let item of section.items">
          <a [routerLink]="item.route"
             [class.active]="isItemActive(item)"
             (click)="navigateToItem(item)"
             [attr.aria-label]="item.label">
            <i [class]="item.icon"></i>
            <span *ngIf="!isCollapsed">{{ item.label }}</span>
          </a>
        </li>
      </ul>
    </div>
  </nav>
</aside>
```

**CSS SPECIFICATION**

**Layout:**
- display: flex
- flex-direction: column
- width: 280px (expanded), 64px (collapsed)
- height: 100vh
- background: #f8f9fa
- border-right: 1px solid #e0e0e0

**Spacing:**
- padding: 1rem 0
- gap: 0.5rem between sections

**Component Styles:**
- .sidebar: main sidebar container
- .sidebar-header: collapse toggle area
- .nav-section: navigation section grouping
- .nav-items: menu item list
- .collapsed: collapsed state styles

**Responsive:**
- Mobile: overlay sidebar, full collapse
- Tablet: auto-collapse on small screens
- Desktop: persistent sidebar

**API INTEGRATION**

- Navigation state persistence
- User preferences for sidebar state

**ERROR HANDLING**

- Navigation failure → show error toast
- Route not found → redirect to default

**INTERACTION FLOW**

**User Action → UI → API**
- Click menu item → navigate to route → load page data
- Toggle sidebar → save preference → update layout

### 4.6 AddTaskModalComponent

**Component Type:** Shared

**Mapped From A1:** Task creation form modal

**Purpose:** Modal form for creating new tasks

**Parent:** KanbanBoardComponent

**Children:**
- FormFieldComponent instances

**Dependencies:**
- ReactiveFormsModule
- TaskService
- UserService

**Library Components Used:**
- ModalComponent → uicomplibraryplay/modal
- FormFieldComponent → uicomplibraryplay/form
- ButtonComponent → uicomplibraryplay/button
- SelectComponent → uicomplibraryplay/select

**TypeScript Specification**

**Inputs:**
- isOpen: boolean (modal visibility)
- assignees: User[] (available assignees)

**Outputs:**
- onSubmit: EventEmitter<Task> (form submission event)
- onCancel: EventEmitter<void> (modal cancel event)
- onFieldChange: EventEmitter<{field: string, value: any}> (field change event)

**State:**
- formData: TaskForm (form data object)
- validationErrors: ValidationErrors (form validation errors)
- isSubmitting: boolean (submission state)

**Methods:**
- ngOnInit() → Initialize form
- onSubmit() → Handle form submission
- onCancel() → Close modal
- validateForm() → Validate form data
- resetForm() → Reset form to initial state

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<app-modal [isOpen]="isOpen" 
           [title]="'Add New Task'"
           [size]="'large'"
           (onClose)="onCancel()">
  
  <form [formGroup]="taskForm" (ngSubmit)="onSubmit()" class="task-form">
    <div class="form-row">
      <app-form-field 
        label="Task Title"
        [required]="true"
        [error]="validationErrors?.title">
        <input type="text" 
               formControlName="title"
               placeholder="Enter task title"
               [attr.aria-describedby]="validationErrors?.title ? 'title-error' : null">
      </app-form-field>
    </div>
    
    <div class="form-row">
      <app-form-field 
        label="Description"
        [error]="validationErrors?.description">
        <textarea formControlName="description"
                  placeholder="Enter task description"
                  rows="4">
        </textarea>
      </app-form-field>
    </div>
    
    <div class="form-row-group">
      <div class="form-col">
        <app-form-field 
          label="Priority"
          [required]="true"
          [error]="validationErrors?.priority">
          <app-select formControlName="priority"
                      [options]="priorityOptions"
                      placeholder="Select priority">
          </app-select>
        </app-form-field>
      </div>
      
      <div class="form-col">
        <app-form-field 
          label="Due Date"
          [error]="validationErrors?.dueDate">
          <input type="date" 
                 formControlName="dueDate"
                 [min]="today">
        </app-form-field>
      </div>
    </div>
    
    <div class="form-row">
      <app-form-field 
        label="Assignee"
        [error]="validationErrors?.assigneeId">
        <app-select formControlName="assigneeId"
                    [options]="assigneeOptions"
                    placeholder="Select assignee">
        </app-select>
      </app-form-field>
    </div>
    
    <div class="form-actions">
      <app-button type="button" 
                  [variant]="'secondary'"
                  (click)="onCancel()"
                  [disabled]="isSubmitting">
        Cancel
      </app-button>
      
      <app-button type="submit" 
                  [variant]="'primary'"
                  [loading]="isSubmitting"
                  [disabled]="taskForm.invalid">
        Create Task
      </app-button>
    </div>
  </form>
</app-modal>
```

**CSS SPECIFICATION**

**Layout:**
- display: flex
- flex-direction: column
- gap: 1.5rem
- max-width: 600px

**Spacing:**
- padding: 2rem
- gap: 1rem between form rows

**Component Styles:**
- .task-form: main form container
- .form-row: full-width form row
- .form-row-group: grouped form fields
- .form-col: column within group
- .form-actions: button container

**Responsive:**
- Mobile: single column layout
- Desktop: two-column layout for grouped fields

**API INTEGRATION**

- onSubmit() → POST /api/tasks → Create new task
- Load assignees → GET /api/users → Populate assignee dropdown

**ERROR HANDLING**

- API failure → show form error message
- Validation error → inline field validation
- Network error → retry mechanism

**INTERACTION FLOW**

**User Action → UI → API**
- Fill form → validate fields → submit → POST /api/tasks → close modal
- Cancel → close modal without saving
- Field change → validate → update UI state

## 5. COMPONENT MAPPING SUMMARY

| A1 Component | Final Component | Type |
|--------------|----------------|---------|
| KanbanBoardComponent | KanbanBoardComponent | Feature |
| KanbanColumnComponent | KanbanColumnComponent | Feature |
| TaskCardComponent | TaskCardComponent | Feature |
| HeaderComponent | HeaderComponent | Layout |
| SidebarComponent | SidebarComponent | Layout |
| AddTaskModalComponent | AddTaskModalComponent | Shared |
| TaskDetailComponent | TaskDetailComponent | Feature |
| ProgressAnalyticsComponent | ProgressAnalyticsComponent | Feature |
| ReportBuilderComponent | ReportBuilderComponent | Feature |
| BoardConfigComponent | BoardConfigComponent | Feature |

## 6. DATA FLOW (FROM AGENT-1)

**Parent → Child:**
- AppComponent → HeaderComponent (user, notifications)
- AppComponent → SidebarComponent (menuItems, activeRoute)
- AppComponent → KanbanBoardComponent (tasks, columns, loading)
- KanbanBoardComponent → KanbanColumnComponent (column, tasks)
- KanbanColumnComponent → TaskCardComponent (task, assignee)
- KanbanBoardComponent → AddTaskModalComponent (isOpen, assignees)
- HeaderComponent → SearchComponent (searchQuery, placeholder)
- SidebarComponent → NavigationComponent (menuItems, activeItem)

## 7. API USAGE SUMMARY

**Component → API:**
- KanbanBoardComponent → GET /api/tasks (load tasks with filtering)
- KanbanBoardComponent → PUT /api/tasks/{id} (update task status on drag)
- TaskCardComponent → GET /api/tasks/{id} (task detail view)
- TaskCardComponent → DELETE /api/tasks/{id} (task deletion)
- AddTaskModalComponent → POST /api/tasks (create new task)
- AddTaskModalComponent → GET /api/users (load assignee options)
- HeaderComponent → GET /api/tasks (search functionality)
- ProgressAnalyticsComponent → GET /api/tasks (analytics aggregation)
- TaskDetailComponent → PUT /api/tasks/{id} (task updates)

## 8. NOTES & CONSTRAINTS

- HTML must follow A1 structure strictly with three-column CSS Grid layout
- Do NOT create new components unnecessarily - reuse library components
- Prefer reusable library components from uicomplibraryplay
- HTML must be pseudo-code ONLY (NOT real framework code)
- Implement proper ARIA attributes for accessibility compliance
- Use Angular CDK for drag-and-drop functionality
- Implement responsive design with specified breakpoints
- Follow Angular best practices for component architecture
- Use OnPush change detection strategy for performance
- Implement proper error handling and loading states
- Ensure proper TypeScript typing for all component interfaces
- Follow secure coding practices for form validation and API calls
- Implement proper cleanup in component lifecycle hooks
- Use Angular Material design principles where applicable
- Ensure cross-browser compatibility for drag-and-drop features