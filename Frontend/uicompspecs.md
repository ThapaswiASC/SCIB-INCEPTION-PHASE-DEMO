# UI COMPONENT SPECIFICATIONS

## 1. INPUT REFERENCE (FROM AGENT-1)

**Page Name:** Kanban Board Application

**Mapped Layout:** Three-column CSS Grid layout with sidebar navigation

**Components from A1:**
- Sidebar Navigation Component
- Header Component  
- Kanban Board Component
- Task Column Component
- Task Card Component
- Task Detail Modal
- Add Task Modal
- Team Assignment Modal
- Report Configuration Modal
- Workflow Rules Modal

## 2. GLOBAL ARCHITECTURE SUMMARY

**Layout Type:** CSS Grid / Flex Mixed

**Main Sections:**
- Header (top navigation bar)
- Sidebar (left navigation panel)
- Main Content (kanban board area)
- Modals (overlay components)

**Component Strategy:** Reuse Angular Material components where possible, create custom Kanban-specific components

**Reused Components:**
- Angular Material Button
- Angular Material Card
- Angular Material Modal/Dialog
- Angular Material Form Fields
- Angular Material Badge
- Angular Material Avatar

**New Components (if any):**
- KanbanBoardComponent
- TaskColumnComponent
- TaskCardComponent
- SidebarNavigationComponent

## 3. FOLDER STRUCTURE

```
src/
 ├── pages/
 │    └── kanban/
 │         ├── kanban-page.component.tsx
 │
 ├── components/
 │    ├── layout/
 │    │    ├── sidebar-navigation.component.tsx
 │    │    └── header.component.tsx
 │    ├── feature/
 │    │    ├── kanban-board.component.tsx
 │    │    ├── task-column.component.tsx
 │    │    ├── task-card.component.tsx
 │    │    └── task-detail.component.tsx
 │    ├── shared/
 │    │    ├── button.component.tsx
 │    │    ├── modal.component.tsx
 │    │    ├── form-field.component.tsx
 │    │    ├── badge.component.tsx
 │    │    └── avatar.component.tsx
```

## 4. COMPONENT IMPLEMENTATION

### 4.1 KanbanPageComponent

**Component Type:** Page

**Mapped From A1:** Main Kanban Application Container

**Purpose:** Root page component that orchestrates the entire Kanban board application

**Parent:** App Component

**Children:**
- SidebarNavigationComponent
- HeaderComponent
- KanbanBoardComponent

**Dependencies:**
- Angular Router
- Angular Material
- RxJS

**Library Components Used:**
- MatSidenav → Sidebar container
- MatToolbar → Header toolbar

**TypeScript Specification**

**Inputs:**
- boardId: string (route parameter)
- userId: string (from auth service)

**Outputs:**
- onTaskCreate: EventEmitter<Task> (new task created)
- onTaskUpdate: EventEmitter<Task> (task updated)

**State:**
- isLoading: boolean (loading state)
- currentBoard: Board (active board data)
- sidebarOpen: boolean (sidebar visibility)

**Methods:**
- loadBoard() → void (fetch board data)
- toggleSidebar() → void (toggle sidebar)
- handleTaskCreate(task: Task) → void (handle new task)

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<div class="kanban-page">
  <mat-sidenav-container>
    <mat-sidenav [opened]="sidebarOpen">
      <sidebar-navigation 
        [boards]="boards" 
        [activeBoard]="currentBoard"
        (boardSelect)="onBoardSelect($event)">
      </sidebar-navigation>
    </mat-sidenav>
    
    <mat-sidenav-content>
      <header-component 
        [user]="currentUser"
        (menuToggle)="toggleSidebar()"
        (searchQuery)="onSearch($event)">
      </header-component>
      
      <main class="main-content">
        <kanban-board 
          [board]="currentBoard"
          [tasks]="tasks"
          (taskCreate)="handleTaskCreate($event)"
          (taskUpdate)="handleTaskUpdate($event)">
        </kanban-board>
      </main>
    </mat-sidenav-content>
  </mat-sidenav-container>
</div>
```

**CSS SPECIFICATION**

**Layout:**
- display: grid
- grid-template-areas: "sidebar header" "sidebar main"
- height: 100vh

**Spacing:**
- padding: 0
- gap: 0

**Component Styles:**
- .kanban-page: full viewport height
- .main-content: overflow-y auto, padding 16px

**Responsive:**
- Mobile (< 768px): sidebar overlay
- Tablet (768px - 1024px): collapsible sidebar
- Desktop (> 1024px): persistent sidebar

**API INTEGRATION**

- loadBoard() → GET /api/boards/{boardId} → fetch board configuration
- loadTasks() → GET /api/boards/{boardId}/tasks → fetch all tasks
- createTask() → POST /api/tasks → create new task

**ERROR HANDLING**

- API failure → show error snackbar notification
- Network error → display retry mechanism
- Validation error → inline form validation messages

**INTERACTION FLOW**

**User Action → UI → API**
- Page load → show loading spinner → fetch board data → render components
- Toggle sidebar → animate sidebar → update state
- Create task → open modal → validate → API call → refresh board

### 4.2 KanbanBoardComponent

**Component Type:** Feature

**Mapped From A1:** Three-column Kanban Board Layout

**Purpose:** Main board component displaying task columns with drag-and-drop functionality

**Parent:** KanbanPageComponent

**Children:**
- TaskColumnComponent (multiple instances)
- AddTaskModal

**Dependencies:**
- Angular CDK Drag Drop
- RxJS

**Library Components Used:**
- CdkDropList → Column drop zones
- CdkDrag → Draggable task cards

**TypeScript Specification**

**Inputs:**
- board: Board (board configuration)
- tasks: Task[] (array of tasks)

**Outputs:**
- taskCreate: EventEmitter<Task> (task creation event)
- taskUpdate: EventEmitter<Task> (task update event)
- taskMove: EventEmitter<TaskMoveEvent> (task moved between columns)

**State:**
- columns: Column[] (board columns)
- draggedTask: Task | null (currently dragged task)
- showAddModal: boolean (add task modal visibility)

**Methods:**
- onTaskDrop(event: CdkDragDrop) → void (handle task drop)
- openAddTaskModal() → void (show add task modal)
- filterTasksByColumn(columnId: string) → Task[] (get tasks for column)

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<div class="kanban-board" cdkDropListGroup>
  <div class="board-header">
    <h2>{{board.name}}</h2>
    <button mat-raised-button (click)="openAddTaskModal()">
      Add Task
    </button>
  </div>
  
  <div class="board-columns">
    <task-column 
      *ngFor="let column of columns"
      [column]="column"
      [tasks]="filterTasksByColumn(column.id)"
      (taskDrop)="onTaskDrop($event)"
      (taskEdit)="onTaskEdit($event)">
    </task-column>
  </div>
  
  <add-task-modal 
    [visible]="showAddModal"
    [columns]="columns"
    (taskCreate)="onTaskCreate($event)"
    (close)="showAddModal = false">
  </add-task-modal>
</div>
```

**CSS SPECIFICATION**

**Layout:**
- display: grid
- grid-template-columns: repeat(3, 1fr)
- gap: 16px
- height: calc(100vh - 120px)

**Spacing:**
- padding: 16px
- column-gap: 16px

**Component Styles:**
- .board-columns: display flex, overflow-x auto
- .board-header: flex layout, justify-content space-between

**Responsive:**
- Mobile: single column, vertical scroll
- Tablet: two columns
- Desktop: three columns

**API INTEGRATION**

- moveTask() → PUT /api/tasks/{taskId}/move → update task column
- createTask() → POST /api/tasks → create new task
- updateTask() → PUT /api/tasks/{taskId} → update task details

**ERROR HANDLING**

- Drag drop failure → revert task position, show error
- API error → show snackbar, maintain UI state
- Validation error → highlight invalid fields

**INTERACTION FLOW**

**User Action → UI → API**
- Drag task → visual feedback → drop → API call → update state
- Click add task → open modal → fill form → validate → create task
- Edit task → open detail modal → modify → save → API update

### 4.3 TaskColumnComponent

**Component Type:** Feature

**Mapped From A1:** Individual Kanban Column

**Purpose:** Displays tasks in a specific column with drop zone functionality

**Parent:** KanbanBoardComponent

**Children:**
- TaskCardComponent (multiple instances)

**Dependencies:**
- Angular CDK Drag Drop

**Library Components Used:**
- CdkDropList → Drop zone container
- MatCard → Column container

**TypeScript Specification**

**Inputs:**
- column: Column (column configuration)
- tasks: Task[] (tasks in this column)

**Outputs:**
- taskDrop: EventEmitter<CdkDragDrop> (task dropped in column)
- taskEdit: EventEmitter<Task> (task edit requested)

**State:**
- isDropZoneActive: boolean (drop zone highlight)
- taskCount: number (number of tasks in column)

**Methods:**
- onTaskDrop(event: CdkDragDrop) → void (handle task drop)
- onTaskClick(task: Task) → void (handle task selection)
- getColumnColor() → string (get column theme color)

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<div class="task-column" [attr.data-column]="column.id">
  <div class="column-header">
    <h3 [style.color]="getColumnColor()">{{column.name}}</h3>
    <mat-chip class="task-count">{{taskCount}}</mat-chip>
  </div>
  
  <div 
    class="column-content"
    cdkDropList
    [cdkDropListData]="tasks"
    (cdkDropListDropped)="onTaskDrop($event)">
    
    <task-card 
      *ngFor="let task of tasks; trackBy: trackByTaskId"
      [task]="task"
      cdkDrag
      (click)="onTaskClick(task)"
      (edit)="taskEdit.emit(task)">
    </task-card>
    
    <div *ngIf="tasks.length === 0" class="empty-column">
      <p>No tasks in {{column.name}}</p>
    </div>
  </div>
</div>
```

**CSS SPECIFICATION**

**Layout:**
- display: flex
- flex-direction: column
- min-height: 400px
- width: 300px

**Spacing:**
- padding: 12px
- gap: 8px between tasks

**Component Styles:**
- .column-header: flex layout, background gradient
- .column-content: flex-grow 1, overflow-y auto
- .empty-column: centered text, muted color

**Responsive:**
- Mobile: full width, margin bottom
- Tablet/Desktop: fixed width with scroll

**API INTEGRATION**

- No direct API calls (handled by parent)

**ERROR HANDLING**

- Invalid drop → visual feedback, prevent drop
- Task load error → show placeholder message

**INTERACTION FLOW**

**User Action → UI → API**
- Drop task → validate drop zone → emit event to parent
- Click task → highlight selection → emit edit event

### 4.4 TaskCardComponent

**Component Type:** Feature

**Mapped From A1:** Individual Task Card

**Purpose:** Displays individual task information with drag functionality

**Parent:** TaskColumnComponent

**Children:** None

**Dependencies:**
- Angular CDK Drag Drop
- Angular Material

**Library Components Used:**
- MatCard → Card container
- MatChip → Priority/status badges
- MatIcon → Task icons

**TypeScript Specification**

**Inputs:**
- task: Task (task data)
- draggable: boolean (enable drag functionality)

**Outputs:**
- edit: EventEmitter<Task> (edit task event)
- delete: EventEmitter<string> (delete task event)

**State:**
- isSelected: boolean (selection state)
- isDragging: boolean (drag state)

**Methods:**
- onEditClick() → void (handle edit button)
- onDeleteClick() → void (handle delete button)
- getPriorityColor() → string (get priority color)
- formatDueDate() → string (format due date display)

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<mat-card 
  class="task-card"
  [class.selected]="isSelected"
  [class.dragging]="isDragging">
  
  <mat-card-header>
    <div class="task-priority">
      <mat-chip [style.background-color]="getPriorityColor()">
        {{task.priority}}
      </mat-chip>
    </div>
    <div class="task-actions">
      <button mat-icon-button (click)="onEditClick()">
        <mat-icon>edit</mat-icon>
      </button>
      <button mat-icon-button (click)="onDeleteClick()">
        <mat-icon>delete</mat-icon>
      </button>
    </div>
  </mat-card-header>
  
  <mat-card-content>
    <h4 class="task-title">{{task.title}}</h4>
    <p class="task-description">{{task.description}}</p>
    
    <div class="task-meta">
      <div class="assignee" *ngIf="task.assignee">
        <mat-icon>person</mat-icon>
        <span>{{task.assignee.name}}</span>
      </div>
      <div class="due-date" *ngIf="task.dueDate">
        <mat-icon>schedule</mat-icon>
        <span>{{formatDueDate()}}</span>
      </div>
    </div>
  </mat-card-content>
</mat-card>
```

**CSS SPECIFICATION**

**Layout:**
- display: block
- margin-bottom: 8px
- cursor: grab (when draggable)

**Spacing:**
- padding: 12px
- margin: 4px 0

**Component Styles:**
- .task-card: border-radius 8px, box-shadow
- .task-title: font-weight 600, margin-bottom 8px
- .task-meta: flex layout, justify-content space-between

**Responsive:**
- All breakpoints: consistent card layout

**API INTEGRATION**

- deleteTask() → DELETE /api/tasks/{taskId} → remove task

**ERROR HANDLING**

- Delete failure → show confirmation dialog, handle error
- Invalid task data → show placeholder content

**INTERACTION FLOW**

**User Action → UI → API**
- Click edit → emit edit event → parent handles modal
- Click delete → show confirmation → API call → remove from UI
- Drag start → visual feedback → enable drop zones

### 4.5 SidebarNavigationComponent

**Component Type:** Layout

**Mapped From A1:** Left Navigation Sidebar

**Purpose:** Navigation menu for boards, projects, and application features

**Parent:** KanbanPageComponent

**Children:** None

**Dependencies:**
- Angular Router
- Angular Material

**Library Components Used:**
- MatList → Navigation list
- MatListItem → Navigation items
- MatIcon → Navigation icons

**TypeScript Specification**

**Inputs:**
- boards: Board[] (available boards)
- activeBoard: Board (currently selected board)
- user: User (current user info)

**Outputs:**
- boardSelect: EventEmitter<Board> (board selection event)
- navigationSelect: EventEmitter<string> (navigation item selected)

**State:**
- expandedSections: string[] (expanded menu sections)
- selectedItem: string (currently selected menu item)

**Methods:**
- onBoardSelect(board: Board) → void (handle board selection)
- toggleSection(section: string) → void (expand/collapse section)
- navigateTo(route: string) → void (handle navigation)

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<div class="sidebar-navigation">
  <div class="sidebar-header">
    <div class="user-info">
      <mat-icon>account_circle</mat-icon>
      <span>{{user.name}}</span>
    </div>
  </div>
  
  <mat-nav-list class="navigation-list">
    <mat-list-item 
      *ngFor="let board of boards"
      [class.active]="board.id === activeBoard?.id"
      (click)="onBoardSelect(board)">
      <mat-icon matListIcon>dashboard</mat-icon>
      <span matLine>{{board.name}}</span>
    </mat-list-item>
    
    <mat-divider></mat-divider>
    
    <mat-list-item (click)="navigateTo('/analytics')">
      <mat-icon matListIcon>analytics</mat-icon>
      <span matLine>Analytics</span>
    </mat-list-item>
    
    <mat-list-item (click)="navigateTo('/settings')">
      <mat-icon matListIcon>settings</mat-icon>
      <span matLine>Settings</span>
    </mat-list-item>
  </mat-nav-list>
</div>
```

**CSS SPECIFICATION**

**Layout:**
- display: flex
- flex-direction: column
- height: 100vh
- width: 280px

**Spacing:**
- padding: 16px 0
- list-item padding: 12px 16px

**Component Styles:**
- .sidebar-navigation: background color, border-right
- .sidebar-header: padding 16px, border-bottom
- .active: background highlight, border-left accent

**Responsive:**
- Mobile: overlay sidebar, full width
- Tablet/Desktop: persistent sidebar

**API INTEGRATION**

- loadBoards() → GET /api/user/boards → fetch user boards

**ERROR HANDLING**

- Board load failure → show error message in sidebar
- Navigation error → fallback to dashboard

**INTERACTION FLOW**

**User Action → UI → API**
- Click board → highlight selection → emit event → parent loads board
- Click navigation → route change → load new page

## 5. COMPONENT MAPPING SUMMARY

| A1 Component | Final Component | Type |
|--------------|----------------|---------|
| Kanban Application | KanbanPageComponent | Page |
| Sidebar Navigation | SidebarNavigationComponent | Layout |
| Header | HeaderComponent | Layout |
| Kanban Board | KanbanBoardComponent | Feature |
| Task Column | TaskColumnComponent | Feature |
| Task Card | TaskCardComponent | Feature |
| Task Detail Modal | TaskDetailComponent | Feature |
| Add Task Modal | AddTaskModalComponent | Feature |
| Button | MatButton | Shared |
| Card | MatCard | Shared |
| Modal | MatDialog | Shared |

## 6. DATA FLOW (FROM AGENT-1)

**Parent → Child:**
- KanbanPage → SidebarNavigation (boards, activeBoard, user)
- KanbanPage → Header (user, searchQuery)
- KanbanPage → KanbanBoard (board, tasks)
- KanbanBoard → TaskColumn (column, tasks)
- TaskColumn → TaskCard (task, draggable)

**Child → Parent:**
- TaskCard → TaskColumn (edit, delete events)
- TaskColumn → KanbanBoard (taskDrop, taskEdit events)
- KanbanBoard → KanbanPage (taskCreate, taskUpdate, taskMove events)
- SidebarNavigation → KanbanPage (boardSelect event)

## 7. API USAGE SUMMARY

**Component → API:**
- KanbanPageComponent → GET /api/boards/{boardId} (load board)
- KanbanPageComponent → GET /api/boards/{boardId}/tasks (load tasks)
- KanbanBoardComponent → POST /api/tasks (create task)
- KanbanBoardComponent → PUT /api/tasks/{taskId}/move (move task)
- TaskCardComponent → DELETE /api/tasks/{taskId} (delete task)
- SidebarNavigationComponent → GET /api/user/boards (load user boards)

**API Endpoints Required:**
- GET /api/boards/{boardId} - Board configuration
- GET /api/boards/{boardId}/tasks - Task list
- POST /api/tasks - Create task
- PUT /api/tasks/{taskId} - Update task
- PUT /api/tasks/{taskId}/move - Move task between columns
- DELETE /api/tasks/{taskId} - Delete task
- GET /api/user/boards - User's accessible boards

## 8. NOTES & CONSTRAINTS

**Implementation Guidelines:**
- HTML must follow A1 structure strictly - three-column CSS Grid layout
- Do NOT create new components unnecessarily - reuse Angular Material components
- Prefer reusable library components over custom implementations
- HTML must be pseudo-code ONLY (NOT real framework code)
- Implement proper TypeScript interfaces for all data models
- Follow Angular style guide for component architecture
- Ensure accessibility compliance with ARIA attributes
- Implement proper error handling for all API interactions
- Use Angular CDK for drag-and-drop functionality
- Follow responsive design principles with mobile-first approach

**Technical Constraints:**
- Angular 15+ framework required
- Angular Material UI library mandatory
- TypeScript strict mode enabled
- RxJS for reactive programming patterns
- CSS Grid and Flexbox for layouts
- No external drag-drop libraries (use Angular CDK)

**Performance Considerations:**
- Implement OnPush change detection strategy
- Use trackBy functions for *ngFor loops
- Lazy load modal components
- Implement virtual scrolling for large task lists
- Optimize API calls with caching strategies

**Accessibility Requirements:**
- ARIA labels for all interactive elements
- Keyboard navigation support
- Screen reader compatibility
- High contrast mode support
- Focus management for modals and drag operations