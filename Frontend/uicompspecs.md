# UI COMPONENT SPECIFICATIONS

## 1. INPUT REFERENCE (FROM AGENT-1)

**Page Name:** Kanban Board Dashboard Application

**Mapped Layout:** CSS Grid and Flexbox hybrid layout with glass morphism design system

**Components from A1:**
- KanbanBoardComponent (three-column layout)
- KanbanColumnComponent (individual columns)
- TaskCardComponent (draggable task cards)
- HeaderComponent (search and user controls)
- SidebarComponent (navigation menu)
- AddTaskModalComponent (task creation)
- TaskDetailComponent (task details view)
- AnalyticsDashboardComponent (progress analytics)
- ReportBuilderComponent (report generation)
- ConfigurationDashboardComponent (settings)

## 2. GLOBAL ARCHITECTURE SUMMARY

**Layout Type:** CSS Grid / Flexbox / Mixed

**Main Sections:**
- Header (search, notifications, user controls)
- Sidebar (navigation with Task Management, Analytics, Configuration)
- Main Content (Kanban board with three columns)
- Modals (task creation, team assignment, configuration)

**Component Strategy:**

**Reused Components:**
- Button Component (from uicomplibraryplay)
- Card Component (glass morphism cards)
- Modal Component (overlay modals)
- Input Component (text inputs, search)
- Badge Component (status badges)
- Avatar Component (user avatars)
- Navigation Component (sidebar navigation)
- Toggle Component (switch toggles)

**New Components (if any):**
- KanbanBoardComponent (main board container)
- KanbanColumnComponent (column containers)
- TaskCardComponent (individual task cards)
- TaskDetailComponent (detailed task view)

## 3. FOLDER STRUCTURE

```
src/
 ├── pages/
 │    └── kanban-dashboard/
 │         ├── kanban-dashboard.component.ts
 │         ├── kanban-dashboard.component.html
 │         └── kanban-dashboard.component.scss
 │
 ├── components/
 │    ├── layout/
 │    │    ├── header/
 │    │    ├── sidebar/
 │    │    └── main-content/
 │    ├── features/
 │    │    ├── kanban/
 │    │    ├── analytics/
 │    │    ├── reports/
 │    │    └── configuration/
 │    ├── modals/
 │    │    ├── add-task-modal/
 │    │    ├── team-assign-modal/
 │    │    ├── report-config-modal/
 │    │    └── workflow-rules-modal/
 │    └── shared/
 │         ├── button/
 │         ├── card/
 │         ├── modal/
 │         ├── input/
 │         ├── badge/
 │         ├── avatar/
 │         └── toggle/
```

## 4. COMPONENT IMPLEMENTATION

### 4.1 KanbanBoardComponent

**Component Type:** Feature

**Mapped From A1:** Main three-column Kanban board container

**Purpose:** Primary Kanban board with drag-and-drop functionality and three-column layout (To Do, In Progress, Done)

**Parent:** KanbanDashboardComponent

**Children:**
- KanbanColumnComponent (3 instances)
- TaskCardComponent (multiple instances per column)

**Dependencies:**
- TaskService (API calls)
- DragDropModule (Angular CDK)

**Library Components Used:**
- Card Component → glass morphism container
- Button Component → action buttons
- Badge Component → task count indicators

**TypeScript Specification**

**Inputs:**
- tasks: Task[] (array of task objects)
- columns: Column[] (column configuration)
- loading: boolean (loading state)
- error: string (error message)

**Outputs:**
- onTaskMove: EventEmitter<TaskMoveEvent> (task moved between columns)
- onTaskSelect: EventEmitter<Task> (task selected for details)
- onTaskCreate: EventEmitter<void> (create new task)
- onTaskUpdate: EventEmitter<Task> (task updated)

**State:**
- selectedTask: Task | null (currently selected task)
- draggedTask: Task | null (task being dragged)
- columnData: ColumnData[] (column state data)

**Methods:**
- loadTasks() → void (fetch tasks from API)
- moveTask(taskId: string, targetColumn: string) → void (move task between columns)
- selectTask(task: Task) → void (select task for details)
- createTask() → void (open create task modal)

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<div class="kanban-board-container">
  <div class="board-header">
    <h2>Kanban Board</h2>
    <button (click)="createTask()">Add Task</button>
  </div>
  
  <div class="kanban-grid" cdkDropListGroup>
    <app-kanban-column 
      *ngFor="let column of columns" 
      [column]="column"
      [tasks]="getTasksForColumn(column.id)"
      [allowDrop]="true"
      (onDrop)="moveTask($event)"
      (onTaskSelect)="selectTask($event)">
    </app-kanban-column>
  </div>
  
  <div *ngIf="loading" class="loading-state">
    Loading tasks...
  </div>
  
  <div *ngIf="error" class="error-state">
    {{ error }}
  </div>
</div>
```

**CSS SPECIFICATION**

**Layout:**
- display: grid
- grid-template-columns: repeat(3, 1fr)
- gap: 1rem

**Spacing:**
- padding: 1.5rem
- gap: 1rem between columns

**Component Styles:**
- .kanban-board-container (main container)
- .kanban-grid (grid layout)
- .board-header (header section)

**Responsive:**
- Desktop (>1024px): 3 columns side by side
- Tablet (768px-1024px): 2 columns with scroll
- Mobile (<768px): single column stack

**API INTEGRATION**

- loadTasks() → GET /api/tasks → fetch all tasks for board
- moveTask() → PUT /api/tasks/{id} → update task status/column

**ERROR HANDLING**

- API failure → show error message in UI
- Validation error → inline error messages
- Network timeout → retry mechanism with user notification

**INTERACTION FLOW**

**User Action → UI → API**
- Drag task → update column → PUT /api/tasks/{id} → refresh board state
- Click task → open TaskDetailComponent → GET /api/tasks/{id}
- Click "Add Task" → open AddTaskModalComponent → POST /api/tasks

### 4.2 KanbanColumnComponent

**Component Type:** Feature

**Mapped From A1:** Individual column within Kanban board

**Purpose:** Container for tasks in specific status (To Do, In Progress, Done) with drop zone functionality

**Parent:** KanbanBoardComponent

**Children:**
- TaskCardComponent (multiple instances)

**Dependencies:**
- DragDropModule (Angular CDK)

**Library Components Used:**
- Card Component → column container
- Badge Component → task count

**TypeScript Specification**

**Inputs:**
- column: Column (column configuration object)
- tasks: Task[] (tasks for this column)
- allowDrop: boolean (enable drop functionality)

**Outputs:**
- onDrop: EventEmitter<CdkDragDrop<Task[]>> (task dropped in column)
- onTaskSelect: EventEmitter<Task> (task selected)

**State:**
- isDropTarget: boolean (column is drop target)
- taskCount: number (number of tasks in column)

**Methods:**
- onTaskDrop(event: CdkDragDrop<Task[]>) → void (handle task drop)
- onTaskClick(task: Task) → void (handle task selection)

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<div class="kanban-column" 
     cdkDropList 
     [cdkDropListData]="tasks"
     (cdkDropListDropped)="onTaskDrop($event)">
  
  <div class="column-header">
    <h3>{{ column.title }}</h3>
    <app-badge [count]="taskCount" [variant]="column.badgeVariant"></app-badge>
  </div>
  
  <div class="column-content">
    <app-task-card 
      *ngFor="let task of tasks" 
      [task]="task"
      [draggable]="true"
      [selectable]="true"
      (onClick)="onTaskClick(task)"
      cdkDrag>
    </app-task-card>
  </div>
  
  <div class="column-footer" *ngIf="tasks.length === 0">
    <p>No tasks in {{ column.title }}</p>
  </div>
</div>
```

**CSS SPECIFICATION**

**Layout:**
- display: flex
- flex-direction: column
- min-height: 500px

**Spacing:**
- padding: 1rem
- margin-bottom: 0.5rem between tasks

**Component Styles:**
- .kanban-column (main column container)
- .column-header (header with title and count)
- .column-content (scrollable task area)
- .column-footer (empty state)

**Responsive:**
- Desktop: fixed width with scroll
- Mobile: full width stacked

**API INTEGRATION**

- None (receives data from parent KanbanBoardComponent)

**ERROR HANDLING**

- Drop validation → prevent invalid drops
- Empty state → show helpful message

**INTERACTION FLOW**

**User Action → UI → API**
- Drop task → validate drop → emit onDrop event → parent handles API call
- Click task → emit onTaskSelect → parent opens task details

### 4.3 TaskCardComponent

**Component Type:** Feature

**Mapped From A1:** Individual task card within columns

**Purpose:** Display task information with drag functionality and click interactions

**Parent:** KanbanColumnComponent

**Children:** None

**Dependencies:**
- DragDropModule (Angular CDK)

**Library Components Used:**
- Card Component → task container
- Badge Component → priority/status badges
- Avatar Component → assignee avatar

**TypeScript Specification**

**Inputs:**
- task: Task (task data object)
- draggable: boolean (enable drag functionality)
- selectable: boolean (enable click selection)

**Outputs:**
- onClick: EventEmitter<Task> (task clicked)
- onEdit: EventEmitter<Task> (edit task)
- onDragStart: EventEmitter<Task> (drag started)
- onDragEnd: EventEmitter<Task> (drag ended)

**State:**
- isDragging: boolean (currently being dragged)
- isSelected: boolean (currently selected)

**Methods:**
- handleClick() → void (handle task click)
- handleEdit() → void (handle edit action)
- onDragStart() → void (drag start handler)
- onDragEnd() → void (drag end handler)

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<app-card class="task-card" 
          [class.dragging]="isDragging"
          [class.selected]="isSelected"
          (click)="handleClick()"
          cdkDrag>
  
  <div class="task-header">
    <h4>{{ task.title }}</h4>
    <app-badge [variant]="task.priority" [text]="task.priority"></app-badge>
  </div>
  
  <div class="task-content">
    <p>{{ task.description | truncate:100 }}</p>
  </div>
  
  <div class="task-footer">
    <div class="task-meta">
      <span class="due-date" *ngIf="task.dueDate">{{ task.dueDate | date:'short' }}</span>
      <app-avatar [user]="task.assignee" [size]="'small'"></app-avatar>
    </div>
    
    <div class="task-actions">
      <app-button [variant]="'icon'" (click)="handleEdit()" [stopPropagation]="true">
        <i class="icon-edit"></i>
      </app-button>
    </div>
  </div>
</app-card>
```

**CSS SPECIFICATION**

**Layout:**
- display: block
- margin-bottom: 0.5rem

**Spacing:**
- padding: 1rem
- border-radius: 8px

**Component Styles:**
- .task-card (main card container)
- .task-header (title and priority)
- .task-content (description area)
- .task-footer (meta and actions)
- .dragging (drag state styling)

**Responsive:**
- All breakpoints: consistent card layout

**API INTEGRATION**

- None (receives data from parent)

**ERROR HANDLING**

- Missing data → show placeholder content
- Invalid dates → show "No due date"

**INTERACTION FLOW**

**User Action → UI → API**
- Click card → emit onClick → parent opens task details
- Click edit → emit onEdit → parent opens edit modal
- Drag card → emit onDragStart → parent handles drag state

### 4.4 HeaderComponent

**Component Type:** Layout

**Mapped From A1:** Top navigation header with search and user controls

**Purpose:** Global header with search functionality, notifications, and user controls

**Parent:** AppComponent

**Children:** None

**Dependencies:**
- FormsModule (search input)

**Library Components Used:**
- Input Component → search input
- Button Component → action buttons
- Avatar Component → user avatar

**TypeScript Specification**

**Inputs:**
- user: User (current user object)
- searchValue: string (current search value)

**Outputs:**
- onSearch: EventEmitter<string> (search query changed)
- onNotificationClick: EventEmitter<void> (notifications clicked)
- onSettingsClick: EventEmitter<void> (settings clicked)

**State:**
- isSearchFocused: boolean (search input focused)
- showNotifications: boolean (notifications panel open)

**Methods:**
- handleSearch(query: string) → void (handle search input)
- toggleNotifications() → void (toggle notifications panel)
- openSettings() → void (open settings)

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<header class="app-header">
  <div class="header-left">
    <div class="search-container">
      <app-input 
        type="search"
        placeholder="Search tasks..."
        [value]="searchValue"
        (valueChange)="handleSearch($event)"
        [icon]="'search'">
      </app-input>
    </div>
  </div>
  
  <div class="header-right">
    <app-button 
      [variant]="'icon'"
      (click)="toggleNotifications()"
      [class.active]="showNotifications">
      <i class="icon-bell"></i>
    </app-button>
    
    <app-button 
      [variant]="'icon'"
      (click)="openSettings()">
      <i class="icon-settings"></i>
    </app-button>
    
    <app-avatar [user]="user" [size]="'medium'"></app-avatar>
  </div>
</header>
```

**CSS SPECIFICATION**

**Layout:**
- display: flex
- justify-content: space-between
- align-items: center

**Spacing:**
- padding: 1rem 1.5rem
- height: 64px

**Component Styles:**
- .app-header (main header container)
- .header-left (search section)
- .header-right (controls section)
- .search-container (search input wrapper)

**Responsive:**
- Desktop: full layout
- Mobile: collapsed search, icon-only buttons

**API INTEGRATION**

- None (search handled by parent components)

**ERROR HANDLING**

- Search errors → show no results message

**INTERACTION FLOW**

**User Action → UI → API**
- Type in search → emit onSearch → parent filters data
- Click notifications → toggle panel → parent loads notifications
- Click settings → emit onSettingsClick → parent navigates to settings

### 4.5 SidebarComponent

**Component Type:** Layout

**Mapped From A1:** Left navigation sidebar with menu items

**Purpose:** Primary navigation with Task Management, Analytics, and Configuration sections

**Parent:** AppComponent

**Children:** None

**Dependencies:**
- RouterModule (navigation)

**Library Components Used:**
- Navigation Component → menu structure
- Button Component → menu items

**TypeScript Specification**

**Inputs:**
- activeRoute: string (current active route)
- menuItems: MenuItem[] (navigation menu structure)

**Outputs:**
- onNavigate: EventEmitter<string> (navigation requested)
- onMenuToggle: EventEmitter<string> (menu section toggled)

**State:**
- expandedSections: string[] (expanded menu sections)

**Methods:**
- navigate(route: string) → void (handle navigation)
- toggleSection(section: string) → void (toggle menu section)
- isActive(route: string) → boolean (check if route is active)

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<aside class="sidebar">
  <div class="brand-section">
    <div class="logo">
      <img src="assets/logo.svg" alt="TaskFlow">
    </div>
    <h1>TaskFlow</h1>
  </div>
  
  <nav class="navigation">
    <div class="nav-section">
      <h3 (click)="toggleSection('tasks')">Task Management</h3>
      <ul *ngIf="expandedSections.includes('tasks')">
        <li>
          <app-button 
            [variant]="'ghost'"
            [class.active]="isActive('/kanban')"
            (click)="navigate('/kanban')">
            Kanban Board
          </app-button>
        </li>
        <li>
          <app-button 
            [variant]="'ghost'"
            [class.active]="isActive('/collaborative')"
            (click)="navigate('/collaborative')">
            Collaborative Board
          </app-button>
        </li>
      </ul>
    </div>
    
    <div class="nav-section">
      <h3 (click)="toggleSection('analytics')">Analytics</h3>
      <ul *ngIf="expandedSections.includes('analytics')">
        <li>
          <app-button 
            [variant]="'ghost'"
            [class.active]="isActive('/analytics')"
            (click)="navigate('/analytics')">
            Progress Analytics
          </app-button>
        </li>
        <li>
          <app-button 
            [variant]="'ghost'"
            [class.active]="isActive('/reports')"
            (click)="navigate('/reports')">
            Report Builder
          </app-button>
        </li>
      </ul>
    </div>
    
    <div class="nav-section">
      <h3 (click)="toggleSection('config')">Configuration</h3>
      <ul *ngIf="expandedSections.includes('config')">
        <li>
          <app-button 
            [variant]="'ghost'"
            [class.active]="isActive('/board-config')"
            (click)="navigate('/board-config')">
            Board Configuration
          </app-button>
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
- width: 280px
- height: 100vh

**Spacing:**
- padding: 1.5rem 1rem

**Component Styles:**
- .sidebar (main container)
- .brand-section (logo and title)
- .navigation (menu structure)
- .nav-section (menu groups)

**Responsive:**
- Desktop: full sidebar
- Tablet: collapsible sidebar
- Mobile: overlay sidebar

**API INTEGRATION**

- None (navigation only)

**ERROR HANDLING**

- Navigation errors → fallback to home route

**INTERACTION FLOW**

**User Action → UI → API**
- Click menu item → emit onNavigate → parent handles routing
- Click section header → toggle section → update expanded state

### 4.6 AddTaskModalComponent

**Component Type:** Modal

**Mapped From A1:** Modal for creating new tasks

**Purpose:** Form modal for creating new tasks with validation and API integration

**Parent:** AppComponent

**Children:** None

**Dependencies:**
- ReactiveFormsModule (form handling)
- TaskService (API calls)

**Library Components Used:**
- Modal Component → modal container
- Input Component → form fields
- Button Component → form actions
- Toggle Component → boolean fields

**TypeScript Specification**

**Inputs:**
- isOpen: boolean (modal visibility)
- initialData: Partial<Task> (pre-filled data)

**Outputs:**
- onSubmit: EventEmitter<Task> (task created)
- onCancel: EventEmitter<void> (modal cancelled)
- onClose: EventEmitter<void> (modal closed)

**State:**
- formData: FormGroup (task form data)
- isSubmitting: boolean (submission in progress)
- validationErrors: ValidationErrors (form validation errors)

**Methods:**
- submitTask() → void (submit new task)
- cancelModal() → void (cancel and close)
- resetForm() → void (reset form to initial state)
- validateForm() → boolean (validate form data)

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<app-modal 
  [isOpen]="isOpen"
  [title]="'Create New Task'"
  (onClose)="onClose.emit()">
  
  <form [formGroup]="formData" (ngSubmit)="submitTask()">
    <div class="form-section">
      <app-input 
        label="Task Title"
        formControlName="title"
        [required]="true"
        [error]="validationErrors?.title">
      </app-input>
      
      <app-input 
        label="Description"
        type="textarea"
        formControlName="description"
        [rows]="4">
      </app-input>
    </div>
    
    <div class="form-section">
      <app-input 
        label="Assignee"
        type="select"
        formControlName="assigneeId"
        [options]="availableUsers">
      </app-input>
      
      <app-input 
        label="Priority"
        type="select"
        formControlName="priority"
        [options]="priorityOptions">
      </app-input>
    </div>
    
    <div class="form-section">
      <app-input 
        label="Due Date"
        type="date"
        formControlName="dueDate">
      </app-input>
      
      <app-input 
        label="Status"
        type="select"
        formControlName="status"
        [options]="statusOptions">
      </app-input>
    </div>
    
    <div class="form-actions">
      <app-button 
        type="button"
        [variant]="'secondary'"
        (click)="cancelModal()"
        [disabled]="isSubmitting">
        Cancel
      </app-button>
      
      <app-button 
        type="submit"
        [variant]="'primary'"
        [loading]="isSubmitting"
        [disabled]="!formData.valid">
        Create Task
      </app-button>
    </div>
  </form>
</app-modal>
```

**CSS SPECIFICATION**

**Layout:**
- Modal width: 600px
- Form layout: vertical stacking

**Spacing:**
- padding: 1.5rem
- gap: 1rem between sections

**Component Styles:**
- .form-section (form field groups)
- .form-actions (button container)

**Responsive:**
- Desktop: fixed width modal
- Mobile: full-screen modal

**API INTEGRATION**

- submitTask() → POST /api/tasks → create new task

**ERROR HANDLING**

- API failure → show error message in modal
- Validation errors → inline field errors
- Network timeout → retry option

**INTERACTION FLOW**

**User Action → UI → API**
- Fill form → validate fields → click Create → POST /api/tasks → emit onSubmit → close modal
- Click Cancel → emit onCancel → close modal without saving

### 4.7 TaskDetailComponent

**Component Type:** Feature

**Mapped From A1:** Detailed task view with comments and properties

**Purpose:** Comprehensive task view with editing, comments, and full task management

**Parent:** AppComponent

**Children:** None

**Dependencies:**
- ReactiveFormsModule (editing)
- TaskService (API calls)

**Library Components Used:**
- Modal Component → detail container
- Input Component → edit fields
- Button Component → actions
- Avatar Component → user avatars
- Badge Component → status/priority

**TypeScript Specification**

**Inputs:**
- taskId: string (task identifier)
- isOpen: boolean (component visibility)

**Outputs:**
- onUpdate: EventEmitter<Task> (task updated)
- onClose: EventEmitter<void> (component closed)
- onCommentAdd: EventEmitter<Comment> (comment added)

**State:**
- task: Task | null (current task data)
- comments: Comment[] (task comments)
- isEditing: boolean (edit mode active)
- isLoading: boolean (loading task data)
- editForm: FormGroup (edit form data)

**Methods:**
- loadTask(taskId: string) → void (fetch task details)
- toggleEdit() → void (toggle edit mode)
- saveTask() → void (save task changes)
- addComment(text: string) → void (add new comment)
- deleteTask() → void (delete current task)

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<app-modal 
  [isOpen]="isOpen"
  [title]="task?.title || 'Task Details'"
  [size]="'large'"
  (onClose)="onClose.emit()">
  
  <div *ngIf="isLoading" class="loading-state">
    Loading task details...
  </div>
  
  <div *ngIf="task && !isLoading" class="task-detail-content">
    <div class="task-header">
      <div class="task-meta">
        <app-badge [variant]="task.priority" [text]="task.priority"></app-badge>
        <app-badge [variant]="task.status" [text]="task.status"></app-badge>
        <span class="task-id">#{{ task.id }}</span>
      </div>
      
      <div class="task-actions">
        <app-button 
          [variant]="'secondary'"
          (click)="toggleEdit()">
          {{ isEditing ? 'Cancel' : 'Edit' }}
        </app-button>
        
        <app-button 
          *ngIf="isEditing"
          [variant]="'primary'"
          (click)="saveTask()">
          Save Changes
        </app-button>
      </div>
    </div>
    
    <div class="task-content">
      <div *ngIf="!isEditing" class="task-view">
        <h3>{{ task.title }}</h3>
        <p>{{ task.description }}</p>
        
        <div class="task-properties">
          <div class="property">
            <label>Assignee:</label>
            <app-avatar [user]="task.assignee" [size]="'small'"></app-avatar>
            <span>{{ task.assignee?.name }}</span>
          </div>
          
          <div class="property">
            <label>Due Date:</label>
            <span>{{ task.dueDate | date:'medium' }}</span>
          </div>
          
          <div class="property">
            <label>Created:</label>
            <span>{{ task.createdAt | date:'medium' }}</span>
          </div>
        </div>
      </div>
      
      <form *ngIf="isEditing" [formGroup]="editForm" class="task-edit">
        <app-input 
          label="Title"
          formControlName="title"
          [required]="true">
        </app-input>
        
        <app-input 
          label="Description"
          type="textarea"
          formControlName="description"
          [rows]="6">
        </app-input>
        
        <div class="form-row">
          <app-input 
            label="Priority"
            type="select"
            formControlName="priority"
            [options]="priorityOptions">
          </app-input>
          
          <app-input 
            label="Status"
            type="select"
            formControlName="status"
            [options]="statusOptions">
          </app-input>
        </div>
      </form>
    </div>
    
    <div class="task-comments">
      <h4>Comments ({{ comments.length }})</h4>
      
      <div class="comment-list">
        <div *ngFor="let comment of comments" class="comment">
          <app-avatar [user]="comment.author" [size]="'small'"></app-avatar>
          <div class="comment-content">
            <div class="comment-header">
              <span class="author">{{ comment.author.name }}</span>
              <span class="timestamp">{{ comment.createdAt | date:'short' }}</span>
            </div>
            <p>{{ comment.text }}</p>
          </div>
        </div>
      </div>
      
      <div class="add-comment">
        <app-input 
          placeholder="Add a comment..."
          type="textarea"
          [rows]="2"
          [(ngModel)]="newCommentText">
        </app-input>
        
        <app-button 
          [variant]="'primary'"
          (click)="addComment(newCommentText)"
          [disabled]="!newCommentText?.trim()">
          Add Comment
        </app-button>
      </div>
    </div>
  </div>
</app-modal>
```

**CSS SPECIFICATION**

**Layout:**
- Modal width: 800px
- Two-column layout for properties

**Spacing:**
- padding: 2rem
- gap: 1.5rem between sections

**Component Styles:**
- .task-detail-content (main container)
- .task-header (title and actions)
- .task-content (main content area)
- .task-properties (property grid)
- .task-comments (comments section)
- .comment (individual comment)

**Responsive:**
- Desktop: full layout
- Mobile: stacked single column

**API INTEGRATION**

- loadTask() → GET /api/tasks/{id} → fetch task details
- saveTask() → PUT /api/tasks/{id} → update task
- addComment() → POST /api/tasks/{id}/comments → add comment
- deleteTask() → DELETE /api/tasks/{id} → remove task

**ERROR HANDLING**

- API failure → show error message in modal
- Validation errors → inline field errors
- Not found → show "Task not found" message

**INTERACTION FLOW**

**User Action → UI → API**
- Open task → GET /api/tasks/{id} → display task details
- Click Edit → enable edit mode → show form fields
- Save changes → validate → PUT /api/tasks/{id} → update UI
- Add comment → POST /api/tasks/{id}/comments → refresh comments

## 5. COMPONENT MAPPING SUMMARY

| A1 Component | Final Component | Type |
|--------------|----------------|---------|
| KanbanBoardComponent | KanbanBoardComponent | Feature |
| KanbanColumnComponent | KanbanColumnComponent | Feature |
| TaskCardComponent | TaskCardComponent | Feature |
| HeaderComponent | HeaderComponent | Layout |
| SidebarComponent | SidebarComponent | Layout |
| AddTaskModalComponent | AddTaskModalComponent | Modal |
| TaskDetailComponent | TaskDetailComponent | Feature |
| AnalyticsDashboardComponent | AnalyticsDashboardComponent | Feature |
| ReportBuilderComponent | ReportBuilderComponent | Feature |
| ConfigurationDashboardComponent | ConfigurationDashboardComponent | Feature |

## 6. DATA FLOW (FROM AGENT-1)

**Parent → Child:**

- AppComponent → HeaderComponent (user: User, searchValue: string)
- AppComponent → SidebarComponent (activeRoute: string, menuItems: MenuItem[])
- AppComponent → KanbanBoardComponent (tasks: Task[], columns: Column[], loading: boolean)
- KanbanBoardComponent → KanbanColumnComponent (column: Column, tasks: Task[], allowDrop: boolean)
- KanbanColumnComponent → TaskCardComponent (task: Task, draggable: boolean, selectable: boolean)
- AppComponent → AddTaskModalComponent (isOpen: boolean, initialData: Partial<Task>)
- AppComponent → TaskDetailComponent (taskId: string, isOpen: boolean)

## 7. API USAGE SUMMARY

**Component → API:**

- KanbanBoardComponent → GET /api/tasks (load board data)
- KanbanBoardComponent → PUT /api/tasks/{id} (update task status)
- AddTaskModalComponent → POST /api/tasks (create new task)
- TaskDetailComponent → GET /api/tasks/{id} (load task details)
- TaskDetailComponent → PUT /api/tasks/{id} (update task)
- TaskDetailComponent → DELETE /api/tasks/{id} (delete task)
- TaskDetailComponent → POST /api/tasks/{id}/comments (add comment)

## 8. NOTES & CONSTRAINTS

- HTML must follow A1 structure strictly with three-column CSS Grid layout
- Do NOT create new components unnecessarily - reuse library components from uicomplibraryplay
- Prefer reusable library components (Button, Card, Modal, Input, Badge, Avatar, Toggle)
- HTML must be pseudo-code ONLY (NOT real framework code)
- Implement proper ARIA attributes for accessibility compliance
- Use Angular CDK for drag-and-drop functionality
- Follow glass morphism design system with backdrop blur effects
- Implement responsive breakpoints: desktop (>1024px), tablet (768px-1024px), mobile (<768px)
- All API calls must include proper error handling and loading states
- Form validation must be implemented with inline error messages
- Components must be OnPush compatible for performance optimization
- Use TypeScript strict mode for type safety
- Implement proper lifecycle hooks (OnInit, OnDestroy) for resource management