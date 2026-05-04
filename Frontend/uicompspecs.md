# UI COMPONENT SPECIFICATIONS

## 1. INPUT REFERENCE (FROM AGENT-1)

**Page Name:** Kanban Board Application

**Mapped Layout:** CSS Grid and Flexbox Mixed Layout

**Components from A1:**
- KanbanBoardComponent (Main container with three-column layout)
- KanbanColumnComponent (Individual column containers)
- TaskCardComponent (Task display cards)
- AddTaskModalComponent (Task creation modal)
- HeaderComponent (Top navigation)
- SidebarComponent (Left navigation menu)
- SearchComponent (Search functionality)
- UserControlsComponent (User avatar and settings)

## 2. GLOBAL ARCHITECTURE SUMMARY

**Layout Type:** CSS Grid / Flexbox Mixed

**Main Sections:**
- Header (Search, notifications, user controls)
- Sidebar (Navigation menu with brand, task management, analytics, configuration)
- Main Content (Kanban board with three columns)
- Modals (Task creation, team assignment, configuration)

**Component Strategy:** Reuse existing library components where possible, create feature-specific components for business logic

**Reused Components:**
- Button (from uicomplibraryplay)
- Card (Glass morphism cards)
- Modal (Overlay with backdrop)
- Badge (Status indicators)
- Avatar (User profile circles)
- Toggle Switch (Settings toggles)
- Input Fields (Search, form inputs)
- Navigation Menu (Sidebar navigation)

**New Components (if any):**
- KanbanBoardComponent (Feature-specific)
- KanbanColumnComponent (Feature-specific)
- TaskCardComponent (Feature-specific)

## 3. FOLDER STRUCTURE

```
src/
 ├── pages/
 │    └── kanban-page/
 │         ├── kanban-page.component.ts
 │         ├── kanban-page.component.html
 │         └── kanban-page.component.scss
 │
 ├── components/
 │    ├── layout/
 │    │    ├── header/
 │    │    ├── sidebar/
 │    │    └── main-content/
 │    ├── feature/
 │    │    ├── kanban-board/
 │    │    ├── kanban-column/
 │    │    ├── task-card/
 │    │    └── add-task-modal/
 │    ├── shared/
 │    │    ├── button/
 │    │    ├── card/
 │    │    ├── modal/
 │    │    ├── badge/
 │    │    ├── avatar/
 │    │    └── search/
```

## 4. COMPONENT IMPLEMENTATION

### 4.1 KanbanBoardComponent

**Component Type:** Feature

**Mapped From A1:** KanbanBoardComponent - Main kanban board container with three-column layout

**Purpose:** Main container for kanban board with three columns (To Do, In Progress, Done)

**Parent:** KanbanPageComponent

**Children:**
- KanbanColumnComponent (3 instances)
- AddTaskModalComponent

**Dependencies:**
- TaskService
- Angular CDK Drag Drop

**Library Components Used:**
- Card → Glass morphism container
- Button → Add task button

**TypeScript Specification**

**Inputs:**
- loading: boolean (loading state)
- error: string (error message)

**Outputs:**
- taskCreated: EventEmitter<Task> (new task created)
- taskUpdated: EventEmitter<Task> (task updated)

**State:**
- columns: KanbanColumn[] (column configuration)
- tasks: Task[] (all tasks)
- selectedTask: Task | null (currently selected task)
- draggedTask: Task | null (task being dragged)
- isAddModalOpen: boolean (modal visibility)

**Methods:**
- ngOnInit() → Initialize component and load data
- loadTasks() → Fetch tasks from API
- onTaskMove(event: CdkDragDrop) → Handle task movement
- openAddModal() → Open task creation modal
- onTaskCreated(task: Task) → Handle new task creation

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<div class="kanban-board" role="region" aria-label="Kanban Board">
  <div class="board-header">
    <h2>Kanban Board</h2>
    <app-button (click)="openAddModal()">Add Task</app-button>
  </div>
  
  <div class="board-columns" *ngIf="!error; else errorTemplate">
    <app-kanban-column 
      *ngFor="let column of columns" 
      [column]="column"
      [tasks]="getTasksForColumn(column.id)"
      (taskMoved)="onTaskMove($event)"
      cdkDropList
      [cdkDropListData]="column.id">
    </app-kanban-column>
  </div>
  
  <ng-template #errorTemplate>
    <div class="error-state" role="alert">
      <p>{{ error }}</p>
      <app-button (click)="loadTasks()">Retry</app-button>
    </div>
  </ng-template>
  
  <app-add-task-modal 
    [isOpen]="isAddModalOpen"
    (taskCreated)="onTaskCreated($event)"
    (closed)="isAddModalOpen = false">
  </app-add-task-modal>
</div>
```

**CSS SPECIFICATION**

**Layout:**
- .kanban-board: display: flex, flex-direction: column
- .board-columns: display: grid, grid-template-columns: repeat(3, 1fr), gap: 1rem

**Spacing:**
- padding: 1.5rem
- gap: 1rem between columns

**Component Styles:**
- .kanban-board: background: var(--surface-color)
- .board-header: display: flex, justify-content: space-between, align-items: center
- .error-state: text-align: center, padding: 2rem

**Responsive:**
- Desktop (>1024px): 3 columns
- Tablet (768px-1024px): 2 columns, grid-template-columns: repeat(2, 1fr)
- Mobile (<768px): 1 column, grid-template-columns: 1fr

**API INTEGRATION**

- loadTasks() → GET /api/tasks → Load all tasks for board
- onTaskMove() → PUT /api/tasks/{id} → Update task status when moved

**ERROR HANDLING**

- API failure → Display error template with retry button
- Task move failure → Revert UI state and show snackbar
- Validation error → Show inline validation messages

**INTERACTION FLOW**

**User Action → UI → API**
- Load board → ngOnInit() → GET /api/tasks → populate columns
- Drag task → onTaskMove() → PUT /api/tasks/{id} → update task status
- Click Add Task → openAddModal() → show modal → submit → POST /api/tasks

### 4.2 KanbanColumnComponent

**Component Type:** Feature

**Mapped From A1:** KanbanColumnComponent - Individual column container for tasks

**Purpose:** Display tasks in a specific column with drag-drop functionality

**Parent:** KanbanBoardComponent

**Children:**
- TaskCardComponent (multiple instances)

**Dependencies:**
- Angular CDK Drag Drop

**Library Components Used:**
- Card → Column container
- Badge → Task count badge

**TypeScript Specification**

**Inputs:**
- column: KanbanColumn (column configuration)
- tasks: Task[] (tasks for this column)

**Outputs:**
- taskMoved: EventEmitter<CdkDragDrop<Task[]>> (task moved event)

**State:**
- isDropTarget: boolean (drop target highlight)
- taskCount: number (computed from tasks length)

**Methods:**
- onTaskDrop(event: CdkDragDrop) → Handle task drop
- onDragEnter() → Highlight drop target
- onDragLeave() → Remove highlight

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<div class="kanban-column" 
     cdkDropList
     [cdkDropListData]="column.id"
     (cdkDropListDropped)="onTaskDrop($event)"
     [class.drop-target]="isDropTarget">
  
  <div class="column-header" [attr.aria-labelledby]="column.id + '-header'">
    <h3 [id]="column.id + '-header'">{{ column.title }}</h3>
    <app-badge [count]="taskCount" variant="secondary"></app-badge>
  </div>
  
  <div class="column-content">
    <app-task-card 
      *ngFor="let task of tasks; trackBy: trackByTaskId"
      [task]="task"
      cdkDrag
      [cdkDragData]="task">
    </app-task-card>
    
    <div *ngIf="tasks.length === 0" class="empty-column">
      <p>No tasks in {{ column.title }}</p>
    </div>
  </div>
</div>
```

**CSS SPECIFICATION**

**Layout:**
- .kanban-column: display: flex, flex-direction: column, min-height: 400px
- .column-content: flex: 1, overflow-y: auto

**Spacing:**
- padding: 1rem
- gap: 0.75rem between tasks

**Component Styles:**
- .kanban-column: background: var(--surface-variant), border-radius: 8px
- .column-header: display: flex, justify-content: space-between, align-items: center
- .drop-target: border: 2px dashed var(--primary-color)
- .empty-column: text-align: center, color: var(--text-secondary)

**Responsive:**
- Mobile: reduce padding to 0.75rem

**API INTEGRATION**

- None (receives data from parent)

**ERROR HANDLING**

- Drop failure → Emit error to parent component
- Invalid task data → Skip rendering problematic tasks

**INTERACTION FLOW**

**User Action → UI → API**
- Drag task over column → highlight drop target
- Drop task → emit taskMoved event → parent handles API call

### 4.3 TaskCardComponent

**Component Type:** Feature

**Mapped From A1:** TaskCardComponent - Individual task card display

**Purpose:** Display individual task with metadata, assignee, and interaction capabilities

**Parent:** KanbanColumnComponent

**Children:** None

**Dependencies:**
- Angular CDK Drag Drop

**Library Components Used:**
- Card → Task container
- Avatar → Assignee avatar
- Badge → Priority and status badges

**TypeScript Specification**

**Inputs:**
- task: Task (task data)
- isSelected: boolean (selection state)

**Outputs:**
- taskClicked: EventEmitter<Task> (task clicked event)
- taskSelected: EventEmitter<Task> (task selected event)

**State:**
- isDragging: boolean (drag state)
- isHovered: boolean (hover state)

**Methods:**
- onClick() → Emit task clicked event
- onDragStart() → Set dragging state
- onDragEnd() → Clear dragging state

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<app-card class="task-card" 
          [class.selected]="isSelected"
          [class.dragging]="isDragging"
          (click)="onClick()"
          cdkDrag
          [cdkDragData]="task"
          (cdkDragStarted)="onDragStart()"
          (cdkDragEnded)="onDragEnd()">
  
  <div class="task-header">
    <h4 class="task-title">{{ task.title }}</h4>
    <app-badge [variant]="getPriorityVariant(task.priority)">
      {{ task.priority }}
    </app-badge>
  </div>
  
  <div class="task-content" *ngIf="task.description">
    <p class="task-description">{{ task.description | truncate:100 }}</p>
  </div>
  
  <div class="task-footer">
    <div class="task-meta">
      <span class="task-id">#{{ task.id }}</span>
      <span class="task-date">{{ task.dueDate | date:'short' }}</span>
    </div>
    
    <div class="task-assignee" *ngIf="task.assignedTo">
      <app-avatar 
        [src]="task.assignedTo.avatar"
        [name]="task.assignedTo.name"
        size="small">
      </app-avatar>
    </div>
  </div>
</app-card>
```

**CSS SPECIFICATION**

**Layout:**
- .task-card: display: flex, flex-direction: column, cursor: pointer
- .task-footer: display: flex, justify-content: space-between, align-items: center

**Spacing:**
- padding: 1rem
- margin-bottom: 0.75rem

**Component Styles:**
- .task-card: background: var(--surface), border-radius: 6px, box-shadow: var(--shadow-sm)
- .task-card:hover: box-shadow: var(--shadow-md), transform: translateY(-1px)
- .task-card.selected: border: 2px solid var(--primary-color)
- .task-card.dragging: opacity: 0.5, transform: rotate(5deg)

**Responsive:**
- Mobile: reduce padding to 0.75rem

**API INTEGRATION**

- None (receives data from parent)

**ERROR HANDLING**

- Missing task data → Display placeholder content
- Invalid assignee → Hide assignee section

**INTERACTION FLOW**

**User Action → UI → API**
- Click task → emit taskClicked → parent navigates to detail view
- Drag task → visual feedback → parent handles drop and API call

### 4.4 AddTaskModalComponent

**Component Type:** Feature

**Mapped From A1:** AddTaskModalComponent - Modal for creating new tasks

**Purpose:** Provide form interface for creating new tasks

**Parent:** KanbanBoardComponent

**Children:** None

**Dependencies:**
- Angular Reactive Forms
- UserService

**Library Components Used:**
- Modal → Container overlay
- Button → Form actions
- Input Fields → Form inputs
- Avatar → User selection

**TypeScript Specification**

**Inputs:**
- isOpen: boolean (modal visibility)
- users: User[] (available assignees)

**Outputs:**
- taskCreated: EventEmitter<Task> (new task created)
- closed: EventEmitter<void> (modal closed)

**State:**
- taskForm: FormGroup (reactive form)
- isSubmitting: boolean (submission state)
- validationErrors: ValidationErrors (form errors)

**Methods:**
- ngOnInit() → Initialize form
- onSubmit() → Submit new task
- onCancel() → Close modal without saving
- resetForm() → Clear form data

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<app-modal [isOpen]="isOpen" 
           (backdropClick)="onCancel()"
           [closeOnEscape]="true">
  
  <div class="modal-header">
    <h3>Add New Task</h3>
    <app-button variant="icon" (click)="onCancel()">
      <icon name="close"></icon>
    </app-button>
  </div>
  
  <form [formGroup]="taskForm" (ngSubmit)="onSubmit()" class="task-form">
    <div class="form-group">
      <label for="title">Task Title *</label>
      <app-input 
        id="title"
        formControlName="title"
        placeholder="Enter task title"
        [error]="getFieldError('title')">
      </app-input>
    </div>
    
    <div class="form-group">
      <label for="description">Description</label>
      <app-textarea 
        id="description"
        formControlName="description"
        placeholder="Enter task description"
        rows="3">
      </app-textarea>
    </div>
    
    <div class="form-row">
      <div class="form-group">
        <label for="priority">Priority</label>
        <app-select 
          id="priority"
          formControlName="priority"
          [options]="priorityOptions">
        </app-select>
      </div>
      
      <div class="form-group">
        <label for="dueDate">Due Date</label>
        <app-date-picker 
          id="dueDate"
          formControlName="dueDate">
        </app-date-picker>
      </div>
    </div>
    
    <div class="form-group">
      <label>Assign To</label>
      <div class="user-selection">
        <div *ngFor="let user of users" 
             class="user-option"
             [class.selected]="isUserSelected(user)"
             (click)="toggleUserSelection(user)">
          <app-avatar [src]="user.avatar" [name]="user.name" size="small"></app-avatar>
          <span>{{ user.name }}</span>
        </div>
      </div>
    </div>
    
    <div class="modal-actions">
      <app-button variant="secondary" (click)="onCancel()" [disabled]="isSubmitting">
        Cancel
      </app-button>
      <app-button type="submit" [loading]="isSubmitting" [disabled]="taskForm.invalid">
        Create Task
      </app-button>
    </div>
  </form>
</app-modal>
```

**CSS SPECIFICATION**

**Layout:**
- .task-form: display: flex, flex-direction: column, gap: 1.5rem
- .form-row: display: grid, grid-template-columns: 1fr 1fr, gap: 1rem
- .modal-actions: display: flex, justify-content: flex-end, gap: 0.75rem

**Spacing:**
- padding: 1.5rem
- gap: 1rem between form groups

**Component Styles:**
- .modal-header: display: flex, justify-content: space-between, align-items: center
- .user-selection: display: flex, flex-wrap: wrap, gap: 0.5rem
- .user-option: display: flex, align-items: center, gap: 0.5rem, padding: 0.5rem, border-radius: 4px, cursor: pointer
- .user-option.selected: background: var(--primary-light), border: 1px solid var(--primary-color)

**Responsive:**
- Mobile: .form-row becomes single column, grid-template-columns: 1fr

**API INTEGRATION**

- onSubmit() → POST /api/tasks → Create new task with form data

**ERROR HANDLING**

- API failure → Display error message above form actions
- Validation errors → Show inline field errors
- Network timeout → Show retry option

**INTERACTION FLOW**

**User Action → UI → API**
- Open modal → initialize form → load users if needed
- Fill form → real-time validation → enable/disable submit
- Submit form → POST /api/tasks → emit taskCreated → close modal

### 4.5 HeaderComponent

**Component Type:** Layout

**Mapped From A1:** HeaderComponent - Top navigation with search and user controls

**Purpose:** Provide top navigation with search functionality and user controls

**Parent:** AppComponent

**Children:**
- SearchComponent
- UserControlsComponent

**Dependencies:**
- AuthService
- NotificationService

**Library Components Used:**
- Button → Action buttons
- Avatar → User avatar
- Input Fields → Search input

**TypeScript Specification**

**Inputs:**
- user: User (current user data)
- notifications: Notification[] (user notifications)

**Outputs:**
- searchQuery: EventEmitter<string> (search performed)
- notificationClicked: EventEmitter<Notification> (notification clicked)
- settingsClicked: EventEmitter<void> (settings accessed)

**State:**
- isSearchFocused: boolean (search input focus)
- unreadCount: number (unread notifications)

**Methods:**
- onSearch(query: string) → Emit search query
- onNotificationClick() → Handle notification click
- onSettingsClick() → Handle settings click
- onUserMenuClick() → Toggle user menu

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<header class="app-header" role="banner">
  <div class="header-content">
    <div class="header-left">
      <app-search 
        placeholder="Search tasks..."
        (searchQuery)="onSearch($event)"
        [isFocused]="isSearchFocused">
      </app-search>
    </div>
    
    <div class="header-right">
      <app-button 
        variant="icon"
        (click)="onNotificationClick()"
        [badge]="unreadCount"
        aria-label="Notifications">
        <icon name="bell"></icon>
      </app-button>
      
      <app-button 
        variant="icon"
        (click)="onSettingsClick()"
        aria-label="Settings">
        <icon name="settings"></icon>
      </app-button>
      
      <app-user-controls 
        [user]="user"
        (menuClicked)="onUserMenuClick()">
      </app-user-controls>
    </div>
  </div>
</header>
```

**CSS SPECIFICATION**

**Layout:**
- .app-header: display: flex, align-items: center, position: sticky, top: 0
- .header-content: display: flex, justify-content: space-between, align-items: center, width: 100%
- .header-right: display: flex, align-items: center, gap: 0.75rem

**Spacing:**
- padding: 1rem 1.5rem
- height: 64px

**Component Styles:**
- .app-header: background: var(--surface), border-bottom: 1px solid var(--border-color), z-index: 100
- .header-left: flex: 1, max-width: 400px

**Responsive:**
- Mobile: reduce padding, hide search on small screens

**API INTEGRATION**

- onSearch() → GET /api/tasks?search=query → Filter tasks based on search

**ERROR HANDLING**

- Search API failure → Show "Search unavailable" message
- Notification load failure → Hide notification badge

**INTERACTION FLOW**

**User Action → UI → API**
- Type in search → debounced search → GET /api/tasks → filter results
- Click notifications → show notification panel → mark as read
- Click settings → navigate to settings page

## 5. COMPONENT MAPPING SUMMARY

| A1 Component | Final Component | Type |
|--------------|----------------|---------|
| KanbanBoardComponent | KanbanBoardComponent | Feature |
| KanbanColumnComponent | KanbanColumnComponent | Feature |
| TaskCardComponent | TaskCardComponent | Feature |
| AddTaskModalComponent | AddTaskModalComponent | Feature |
| HeaderComponent | HeaderComponent | Layout |
| SidebarComponent | SidebarComponent | Layout |
| SearchComponent | SearchComponent | Shared |
| UserControlsComponent | UserControlsComponent | Shared |

## 6. DATA FLOW (FROM AGENT-1)

**Parent → Child:**
- AppComponent → HeaderComponent (user, notifications)
- AppComponent → SidebarComponent (activeRoute, menuItems)
- AppComponent → MainContentComponent (currentPage, data)
- KanbanBoardComponent → KanbanColumnComponent (column, tasks)
- KanbanColumnComponent → TaskCardComponent (task, assignee)
- KanbanBoardComponent → AddTaskModalComponent (isOpen, users)

## 7. API USAGE SUMMARY

**Component → API:**
- KanbanBoardComponent → GET /api/tasks (load board data)
- KanbanBoardComponent → PUT /api/tasks/{id} (update task status)
- AddTaskModalComponent → POST /api/tasks (create new task)
- SearchComponent → GET /api/tasks?search=query (search functionality)
- TaskDetailComponent → GET /api/tasks/{id} (load task details)

## 8. NOTES & CONSTRAINTS

- HTML must follow A1 structure strictly with three-column kanban layout
- Do NOT create new components unnecessarily - reuse library components
- Prefer reusable library components from uicomplibraryplay
- HTML must be pseudo-code ONLY (NOT real framework code)
- Implement proper ARIA attributes for accessibility compliance
- CSS Grid layout must use grid-template-columns: repeat(3, 1fr) for desktop
- Error state handling must be implemented with conditional rendering using *ngIf
- OnInit lifecycle hook must initialize column data structure as specified in Jira requirements
- Component must compile without errors and pass linting checks
- Responsive breakpoints: desktop (>1024px), tablet (768px-1024px), mobile (<768px)
- Drag and drop functionality must use Angular CDK
- Form validation must be implemented using Angular Reactive Forms
- API error handling must show user-friendly messages with retry options