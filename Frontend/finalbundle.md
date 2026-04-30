# FINAL UI PACKAGE BUNDLE

## 1. JIRA REQUIREMENT SUMMARY

**Story Description:**
Create Angular component for Kanban board container with three-column layout. Implementation includes creating Angular component at src/app/features/kanban/components/kanban-board/kanban-board.component.ts with @Component decorator and selector 'app-kanban-board'. Define template with three column containers using CSS Grid layout (grid-template-columns: repeat(3, 1fr)). Create component SCSS file with responsive breakpoints: desktop (>1024px), tablet (768px-1024px), mobile (<768px). Implement OnInit lifecycle hook to initialize column data structure. Add ARIA attributes for accessibility compliance. Implement error handling with *ngIf directive. Use Angular Material or custom CSS for styling.

**Acceptance Criteria:**
- KanbanBoardComponent created with proper Angular structure and decorators
- Template renders three distinct column containers with proper semantic HTML
- CSS Grid layout implemented with responsive breakpoints for desktop, tablet, and mobile
- ARIA attributes added for accessibility compliance
- Error state template implemented with conditional rendering
- Component compiles without errors and passes linting checks

**UI Tasks:**
- Create three distinct columns labeled 'To Do', 'In Progress', and 'Done' displayed horizontally
- Columns should adapt responsively while maintaining their distinct separation
- Implement proper semantic HTML structure
- Add accessibility features with ARIA attributes
- Ensure responsive design across all device types

## 2. UI COMPONENT ARCHITECTURE (FROM AGENT-1)

### Component Hierarchy:
```
├── AppComponent
├── LayoutComponents
│   ├── SidebarComponent
│   ├── HeaderComponent
│   └── MainContentComponent
├── KanbanComponents
│   ├── KanbanBoardComponent
│   ├── KanbanColumnComponent
│   ├── TaskCardComponent
│   └── TaskDetailComponent
├── ModalComponents
│   ├── AddTaskModalComponent
│   ├── TeamAssignModalComponent
│   ├── ReportConfigModalComponent
│   └── WorkflowRulesModalComponent
├── DashboardComponents
│   ├── AnalyticsDashboardComponent
│   ├── ReportBuilderComponent
│   ├── ConfigurationDashboardComponent
│   └── SettingsComponent
└── SharedComponents
    ├── ButtonComponent
    ├── CardComponent
    ├── ModalComponent
    ├── InputComponent
    ├── BadgeComponent
    ├── AvatarComponent
    ├── ToggleComponent
    └── FormComponents
```

### Layout Structure:
**Page Layout Hierarchy:**
```
├── App Container
│   ├── Sidebar Navigation
│   │   ├── Brand Section
│   │   ├── Task Management Menu
│   │   ├── Analytics Menu
│   │   └── Configuration Menu
│   └── Main Content Area
│       ├── Header
│       │   ├── Search Component
│       │   └── User Controls
│       └── Content Pages
│           ├── Kanban Board Dashboard
│           │   ├── Page Header
│           │   └── Three-Column Board
│           │       ├── To Do Column
│           │       ├── In Progress Column
│           │       └── Done Column
│           ├── Collaborative Board
│           ├── Analytics Dashboard
│           ├── Report Builder
│           ├── Configuration Dashboard
│           ├── Task Detail View
│           └── Settings Screen
└── Modal Overlays
    ├── Add Task Modal
    ├── Team Assignment Modal
    ├── Report Configuration Modal
    └── Workflow Rules Modal
```

### Component Responsibilities:

**KanbanBoardComponent:**
- Purpose: Main three-column Kanban board container
- Props: columns (array), tasks (array), loading (boolean), error (string)
- State: selectedTask, draggedTask, columnData
- Events: onTaskMove, onTaskSelect, onTaskCreate, onTaskUpdate
- API Binding: GET /api/tasks, PUT /api/tasks/{id}

**KanbanColumnComponent:**
- Purpose: Individual column within Kanban board
- Props: column (object), tasks (array), allowDrop (boolean)
- State: isDropTarget, taskCount
- Events: onDrop, onDragOver, onDragLeave
- API Binding: None (receives data from parent)

**TaskCardComponent:**
- Purpose: Individual task card within columns
- Props: task (object), draggable (boolean), selectable (boolean)
- State: isDragging, isSelected
- Events: onDragStart, onDragEnd, onClick, onEdit
- API Binding: None (receives data from parent)

### Data Flow:
**Parent → Child Mapping:**
- AppComponent → HeaderComponent (user, searchValue)
- AppComponent → SidebarComponent (activeRoute, menuItems)
- AppComponent → KanbanBoardComponent (tasks, columns, loading)
- KanbanBoardComponent → KanbanColumnComponent (column, tasks, allowDrop)
- KanbanColumnComponent → TaskCardComponent (task, draggable, selectable)
- AppComponent → AddTaskModalComponent (isOpen, initialData)
- AppComponent → TaskDetailComponent (taskId, isOpen)

### API Summary:
**Available APIs:**
- POST /api/tasks → Create new task
- GET /api/tasks → List tasks with filtering and pagination
- GET /api/tasks/{id} → Get specific task details
- PUT /api/tasks/{id} → Update existing task
- DELETE /api/tasks/{id} → Soft delete task
- GET /health → Basic health check
- GET /health/detailed → Detailed health check

**NOTE:** This section is the SOURCE OF TRUTH for structure.

## 3. UI COMPONENT SPECIFICATIONS (FROM AGENT-2)

### 3.1 KanbanBoardComponent

**TypeScript Specification:**
```typescript
import { Component, OnInit, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';
import { CdkDragDrop, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { Task, TaskStatus, Column } from '../../../models/task.model';
import { TaskService } from '../../../services/task.service';

@Component({
  selector: 'app-kanban-board',
  templateUrl: './kanban-board.component.html',
  styleUrls: ['./kanban-board.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class KanbanBoardComponent implements OnInit {
  @Input() tasks: Task[] = [];
  @Input() loading: boolean = false;
  @Input() error: string | null = null;
  
  @Output() taskMove = new EventEmitter<{task: Task, newStatus: TaskStatus}>();
  @Output() taskSelect = new EventEmitter<Task>();
  @Output() taskCreate = new EventEmitter<void>();
  @Output() taskUpdate = new EventEmitter<Task>();

  columns: Column[] = [
    { id: 'todo', title: 'To Do', status: TaskStatus.TODO, tasks: [] },
    { id: 'inprogress', title: 'In Progress', status: TaskStatus.IN_PROGRESS, tasks: [] },
    { id: 'done', title: 'Done', status: TaskStatus.DONE, tasks: [] }
  ];

  selectedTask: Task | null = null;
  draggedTask: Task | null = null;
  isAddTaskModalOpen: boolean = false;

  constructor(private taskService: TaskService) {}

  ngOnInit(): void {
    this.initializeColumns();
    this.loadTasks();
  }

  private initializeColumns(): void {
    this.columns.forEach(column => {
      column.tasks = this.tasks.filter(task => task.status === column.status);
    });
  }

  private loadTasks(): void {
    this.taskService.getTasks().subscribe({
      next: (tasks) => {
        this.tasks = tasks;
        this.initializeColumns();
      },
      error: (error) => {
        this.error = 'Failed to load tasks';
        console.error('Error loading tasks:', error);
      }
    });
  }

  onDrop(event: CdkDragDrop<Task[]>, targetColumn: Column): void {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      const task = event.previousContainer.data[event.previousIndex];
      const updatedTask = { ...task, status: targetColumn.status };
      
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );
      
      this.taskMove.emit({ task: updatedTask, newStatus: targetColumn.status });
      this.updateTaskStatus(updatedTask);
    }
  }

  private updateTaskStatus(task: Task): void {
    this.taskService.updateTask(task.id, task).subscribe({
      next: (updatedTask) => {
        console.log('Task updated successfully:', updatedTask);
      },
      error: (error) => {
        console.error('Error updating task:', error);
        this.error = 'Failed to update task status';
        // Revert the UI change on error
        this.loadTasks();
      }
    });
  }

  onTaskClick(task: Task): void {
    this.selectedTask = task;
    this.taskSelect.emit(task);
  }

  onAddTask(): void {
    this.isAddTaskModalOpen = true;
    this.taskCreate.emit();
  }

  onTaskCreated(newTask: Task): void {
    this.tasks.push(newTask);
    this.initializeColumns();
    this.isAddTaskModalOpen = false;
  }

  onModalClose(): void {
    this.isAddTaskModalOpen = false;
  }

  trackByTaskId(index: number, task: Task): string {
    return task.id;
  }

  trackByColumnId(index: number, column: Column): string {
    return column.id;
  }
}
```

**HTML Structure:**
```html
<div class="kanban-board-container" 
     role="main" 
     aria-label="Kanban Board Dashboard">
  
  <!-- Loading State -->
  <div *ngIf="loading" class="loading-state" role="status" aria-live="polite">
    <div class="loading-spinner"></div>
    <span class="loading-text">Loading tasks...</span>
  </div>

  <!-- Error State -->
  <div *ngIf="error && !loading" 
       class="error-state" 
       role="alert" 
       aria-live="assertive">
    <div class="error-icon">⚠️</div>
    <div class="error-message">{{ error }}</div>
    <button class="retry-button" (click)="loadTasks()">Retry</button>
  </div>

  <!-- Board Header -->
  <div class="board-header">
    <div class="board-title">
      <h1>Kanban Board</h1>
      <p class="board-subtitle">Manage your tasks efficiently</p>
    </div>
    <div class="board-actions">
      <button class="add-task-btn primary" 
              (click)="onAddTask()"
              aria-label="Add new task">
        <span class="btn-icon">+</span>
        Add Task
      </button>
    </div>
  </div>

  <!-- Kanban Board Grid -->
  <div class="kanban-grid" 
       *ngIf="!loading && !error"
       cdkDropListGroup>
    
    <div class="kanban-column" 
         *ngFor="let column of columns; trackBy: trackByColumnId"
         [attr.data-column-id]="column.id">
      
      <app-kanban-column
        [column]="column"
        [tasks]="column.tasks"
        [allowDrop]="true"
        (taskDrop)="onDrop($event, column)"
        (taskClick)="onTaskClick($event)"
        (taskEdit)="taskUpdate.emit($event)">
      </app-kanban-column>
      
    </div>
  </div>

  <!-- Add Task Modal -->
  <app-add-task-modal
    *ngIf="isAddTaskModalOpen"
    [isOpen]="isAddTaskModalOpen"
    (taskCreated)="onTaskCreated($event)"
    (modalClose)="onModalClose()">
  </app-add-task-modal>

  <!-- Task Detail Modal -->
  <app-task-detail
    *ngIf="selectedTask"
    [task]="selectedTask"
    [isOpen]="!!selectedTask"
    (taskUpdate)="taskUpdate.emit($event)"
    (modalClose)="selectedTask = null">
  </app-task-detail>

</div>
```

**CSS Specification:**
```scss
.kanban-board-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  padding: 1.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
  
  // Glass morphism background
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    z-index: -1;
  }
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 50vh;
  color: #ffffff;
  
  .loading-spinner {
    width: 40px;
    height: 40px;
    border: 4px solid rgba(255, 255, 255, 0.3);
    border-top: 4px solid #ffffff;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: 1rem;
  }
  
  .loading-text {
    font-size: 1.1rem;
    font-weight: 500;
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 50vh;
  color: #ffffff;
  text-align: center;
  
  .error-icon {
    font-size: 3rem;
    margin-bottom: 1rem;
  }
  
  .error-message {
    font-size: 1.2rem;
    margin-bottom: 1.5rem;
    max-width: 400px;
  }
  
  .retry-button {
    padding: 0.75rem 1.5rem;
    background: rgba(255, 255, 255, 0.2);
    border: 1px solid rgba(255, 255, 255, 0.3);
    border-radius: 8px;
    color: #ffffff;
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      background: rgba(255, 255, 255, 0.3);
      transform: translateY(-2px);
    }
  }
}

.board-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding: 1.5rem;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  
  .board-title {
    h1 {
      font-size: 2rem;
      font-weight: 700;
      color: #ffffff;
      margin: 0 0 0.5rem 0;
    }
    
    .board-subtitle {
      font-size: 1rem;
      color: rgba(255, 255, 255, 0.8);
      margin: 0;
    }
  }
  
  .board-actions {
    .add-task-btn {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.75rem 1.5rem;
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      border: none;
      border-radius: 8px;
      color: #ffffff;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
      
      .btn-icon {
        font-size: 1.2rem;
        font-weight: bold;
      }
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 25px rgba(79, 172, 254, 0.4);
      }
      
      &:active {
        transform: translateY(0);
      }
    }
  }
}

.kanban-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.5rem;
  flex: 1;
  min-height: 0;
  
  .kanban-column {
    display: flex;
    flex-direction: column;
    min-height: 500px;
  }
}

// Responsive Design
@media (max-width: 1024px) {
  .kanban-board-container {
    padding: 1rem;
  }
  
  .board-header {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
    
    .board-title h1 {
      font-size: 1.75rem;
    }
  }
  
  .kanban-grid {
    gap: 1rem;
  }
}

@media (max-width: 768px) {
  .kanban-board-container {
    padding: 0.75rem;
  }
  
  .kanban-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
    
    .kanban-column {
      min-height: 300px;
    }
  }
  
  .board-header {
    padding: 1rem;
    
    .board-title h1 {
      font-size: 1.5rem;
    }
    
    .add-task-btn {
      width: 100%;
      justify-content: center;
    }
  }
}

@media (max-width: 480px) {
  .kanban-board-container {
    padding: 0.5rem;
  }
  
  .board-header {
    padding: 0.75rem;
    
    .board-title h1 {
      font-size: 1.25rem;
    }
    
    .board-subtitle {
      font-size: 0.9rem;
    }
  }
}
```

### 3.2 KanbanColumnComponent

**TypeScript Specification:**
```typescript
import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';
import { CdkDragDrop } from '@angular/cdk/drag-drop';
import { Task, Column } from '../../../models/task.model';

@Component({
  selector: 'app-kanban-column',
  templateUrl: './kanban-column.component.html',
  styleUrls: ['./kanban-column.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class KanbanColumnComponent {
  @Input() column!: Column;
  @Input() tasks: Task[] = [];
  @Input() allowDrop: boolean = true;
  
  @Output() taskDrop = new EventEmitter<CdkDragDrop<Task[]>>();
  @Output() taskClick = new EventEmitter<Task>();
  @Output() taskEdit = new EventEmitter<Task>();

  isDropTarget: boolean = false;

  get taskCount(): number {
    return this.tasks.length;
  }

  get columnStatusClass(): string {
    return `column-${this.column.status.toLowerCase().replace('_', '-')}`;
  }

  onDrop(event: CdkDragDrop<Task[]>): void {
    this.isDropTarget = false;
    this.taskDrop.emit(event);
  }

  onDragOver(): void {
    if (this.allowDrop) {
      this.isDropTarget = true;
    }
  }

  onDragLeave(): void {
    this.isDropTarget = false;
  }

  onTaskClick(task: Task): void {
    this.taskClick.emit(task);
  }

  onTaskEdit(task: Task): void {
    this.taskEdit.emit(task);
  }

  trackByTaskId(index: number, task: Task): string {
    return task.id;
  }
}
```

**HTML Structure:**
```html
<div class="kanban-column-container"
     [class.drop-target]="isDropTarget"
     [ngClass]="columnStatusClass"
     role="region"
     [attr.aria-label]="column.title + ' column'">
  
  <!-- Column Header -->
  <div class="column-header">
    <div class="column-title">
      <h3>{{ column.title }}</h3>
      <span class="task-count" 
            [attr.aria-label]="taskCount + ' tasks in ' + column.title">
        {{ taskCount }}
      </span>
    </div>
    <div class="column-status-indicator" 
         [attr.data-status]="column.status">
    </div>
  </div>

  <!-- Tasks Container -->
  <div class="tasks-container"
       cdkDropList
       [cdkDropListData]="tasks"
       [cdkDropListDisabled]="!allowDrop"
       (cdkDropListDropped)="onDrop($event)"
       (cdkDropListEntered)="onDragOver()"
       (cdkDropListExited)="onDragLeave()"
       role="list"
       [attr.aria-label]="'Tasks in ' + column.title + ' column'">
    
    <!-- Task Cards -->
    <div *ngFor="let task of tasks; trackBy: trackByTaskId" 
         class="task-item"
         role="listitem">
      
      <app-task-card
        [task]="task"
        [draggable]="allowDrop"
        [selectable]="true"
        (taskClick)="onTaskClick(task)"
        (taskEdit)="onTaskEdit(task)">
      </app-task-card>
      
    </div>

    <!-- Empty State -->
    <div *ngIf="tasks.length === 0" 
         class="empty-state"
         role="status"
         aria-live="polite">
      <div class="empty-icon">📋</div>
      <p class="empty-message">No tasks in {{ column.title }}</p>
      <p class="empty-hint">Drag tasks here or create new ones</p>
    </div>

  </div>

  <!-- Drop Zone Indicator -->
  <div class="drop-zone-indicator" 
       *ngIf="isDropTarget"
       aria-hidden="true">
    <span class="drop-text">Drop task here</span>
  </div>

</div>
```

**CSS Specification:**
```scss
.kanban-column-container {
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: 1rem;
  min-height: 500px;
  transition: all 0.3s ease;
  position: relative;
  
  &.drop-target {
    border-color: #4facfe;
    background: rgba(79, 172, 254, 0.1);
    transform: scale(1.02);
    box-shadow: 0 8px 32px rgba(79, 172, 254, 0.3);
  }
  
  // Column status styling
  &.column-todo {
    border-left: 4px solid #ff6b6b;
  }
  
  &.column-in-progress {
    border-left: 4px solid #4ecdc4;
  }
  
  &.column-done {
    border-left: 4px solid #45b7d1;
  }
}

.column-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  
  .column-title {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    
    h3 {
      font-size: 1.1rem;
      font-weight: 600;
      color: #ffffff;
      margin: 0;
    }
    
    .task-count {
      display: flex;
      align-items: center;
      justify-content: center;
      min-width: 24px;
      height: 24px;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 12px;
      font-size: 0.8rem;
      font-weight: 600;
      color: #ffffff;
      padding: 0 0.5rem;
    }
  }
  
  .column-status-indicator {
    width: 12px;
    height: 12px;
    border-radius: 50%;
    
    &[data-status="TODO"] {
      background: #ff6b6b;
    }
    
    &[data-status="IN_PROGRESS"] {
      background: #4ecdc4;
    }
    
    &[data-status="DONE"] {
      background: #45b7d1;
    }
  }
}

.tasks-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  min-height: 200px;
  padding: 0.5rem 0;
  
  .task-item {
    transition: all 0.2s ease;
    
    &:hover {
      transform: translateY(-2px);
    }
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem 1rem;
  text-align: center;
  color: rgba(255, 255, 255, 0.7);
  
  .empty-icon {
    font-size: 2.5rem;
    margin-bottom: 1rem;
    opacity: 0.6;
  }
  
  .empty-message {
    font-size: 1rem;
    font-weight: 500;
    margin: 0 0 0.5rem 0;
    color: rgba(255, 255, 255, 0.8);
  }
  
  .empty-hint {
    font-size: 0.85rem;
    margin: 0;
    opacity: 0.7;
  }
}

.drop-zone-indicator {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(79, 172, 254, 0.2);
  border: 2px dashed #4facfe;
  border-radius: 16px;
  pointer-events: none;
  
  .drop-text {
    font-size: 1.1rem;
    font-weight: 600;
    color: #4facfe;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  }
}

// Responsive Design
@media (max-width: 768px) {
  .kanban-column-container {
    min-height: 300px;
    padding: 0.75rem;
  }
  
  .column-header {
    .column-title h3 {
      font-size: 1rem;
    }
  }
  
  .empty-state {
    padding: 1.5rem 0.75rem;
    
    .empty-icon {
      font-size: 2rem;
    }
    
    .empty-message {
      font-size: 0.9rem;
    }
    
    .empty-hint {
      font-size: 0.8rem;
    }
  }
}
```

### 3.3 TaskCardComponent

**TypeScript Specification:**
```typescript
import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';
import { Task, TaskPriority } from '../../../models/task.model';

@Component({
  selector: 'app-task-card',
  templateUrl: './task-card.component.html',
  styleUrls: ['./task-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TaskCardComponent {
  @Input() task!: Task;
  @Input() draggable: boolean = true;
  @Input() selectable: boolean = true;
  
  @Output() taskClick = new EventEmitter<Task>();
  @Output() taskEdit = new EventEmitter<Task>();
  @Output() dragStart = new EventEmitter<Task>();
  @Output() dragEnd = new EventEmitter<Task>();

  isDragging: boolean = false;
  isSelected: boolean = false;

  get priorityClass(): string {
    return `priority-${this.task.priority.toLowerCase()}`;
  }

  get priorityLabel(): string {
    const labels = {
      [TaskPriority.LOW]: 'Low Priority',
      [TaskPriority.MEDIUM]: 'Medium Priority', 
      [TaskPriority.HIGH]: 'High Priority',
      [TaskPriority.CRITICAL]: 'Critical Priority'
    };
    return labels[this.task.priority] || 'Unknown Priority';
  }

  get dueDateStatus(): 'overdue' | 'due-soon' | 'normal' {
    if (!this.task.dueDate) return 'normal';
    
    const now = new Date();
    const dueDate = new Date(this.task.dueDate);
    const diffDays = Math.ceil((dueDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24));
    
    if (diffDays < 0) return 'overdue';
    if (diffDays <= 2) return 'due-soon';
    return 'normal';
  }

  get formattedDueDate(): string {
    if (!this.task.dueDate) return '';
    return new Date(this.task.dueDate).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric'
    });
  }

  onClick(): void {
    if (this.selectable) {
      this.isSelected = !this.isSelected;
      this.taskClick.emit(this.task);
    }
  }

  onEdit(event: Event): void {
    event.stopPropagation();
    this.taskEdit.emit(this.task);
  }

  onDragStart(): void {
    this.isDragging = true;
    this.dragStart.emit(this.task);
  }

  onDragEnd(): void {
    this.isDragging = false;
    this.dragEnd.emit(this.task);
  }

  onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault();
      this.onClick();
    }
  }
}
```

**HTML Structure:**
```html
<div class="task-card"
     [class.dragging]="isDragging"
     [class.selected]="isSelected"
     [ngClass]="priorityClass"
     [attr.draggable]="draggable"
     [attr.tabindex]="selectable ? 0 : -1"
     [attr.role]="selectable ? 'button' : 'article'"
     [attr.aria-label]="'Task: ' + task.title + ', ' + priorityLabel"
     [attr.aria-pressed]="isSelected"
     cdkDrag
     [cdkDragDisabled]="!draggable"
     (click)="onClick()"
     (keydown)="onKeyDown($event)"
     (cdkDragStarted)="onDragStart()"
     (cdkDragEnded)="onDragEnd()">

  <!-- Drag Handle -->
  <div class="drag-handle" 
       *ngIf="draggable"
       cdkDragHandle
       aria-label="Drag to move task">
    <span class="drag-dots">⋮⋮</span>
  </div>

  <!-- Task Header -->
  <div class="task-header">
    <div class="task-priority">
      <span class="priority-badge" 
            [attr.aria-label]="priorityLabel">
        {{ task.priority }}
      </span>
    </div>
    <div class="task-actions">
      <button class="edit-btn"
              (click)="onEdit($event)"
              [attr.aria-label]="'Edit task: ' + task.title"
              tabindex="0">
        ✏️
      </button>
    </div>
  </div>

  <!-- Task Content -->
  <div class="task-content">
    <h4 class="task-title">{{ task.title }}</h4>
    <p class="task-description" 
       *ngIf="task.description"
       [title]="task.description">
      {{ task.description }}
    </p>
  </div>

  <!-- Task Meta -->
  <div class="task-meta">
    <!-- Assignee -->
    <div class="task-assignee" 
         *ngIf="task.assignee"
         [attr.aria-label]="'Assigned to: ' + task.assignee.name">
      <div class="assignee-avatar">
        <img [src]="task.assignee.avatar" 
             [alt]="task.assignee.name"
             *ngIf="task.assignee.avatar; else initials">
        <ng-template #initials>
          <span class="assignee-initials">
            {{ task.assignee.name.charAt(0).toUpperCase() }}
          </span>
        </ng-template>
      </div>
      <span class="assignee-name">{{ task.assignee.name }}</span>
    </div>

    <!-- Due Date -->
    <div class="task-due-date" 
         *ngIf="task.dueDate"
         [ngClass]="dueDateStatus"
         [attr.aria-label]="'Due date: ' + formattedDueDate">
      <span class="due-date-icon">📅</span>
      <span class="due-date-text">{{ formattedDueDate }}</span>
    </div>
  </div>

  <!-- Task Tags -->
  <div class="task-tags" 
       *ngIf="task.tags && task.tags.length > 0"
       role="list"
       aria-label="Task tags">
    <span class="task-tag" 
          *ngFor="let tag of task.tags"
          role="listitem"
          [attr.aria-label]="'Tag: ' + tag">
      {{ tag }}
    </span>
  </div>

  <!-- Drag Preview -->
  <div class="drag-preview" 
       *cdkDragPreview
       aria-hidden="true">
    <div class="preview-content">
      <h4>{{ task.title }}</h4>
      <span class="preview-priority">{{ task.priority }}</span>
    </div>
  </div>

</div>
```

**CSS Specification:**
```scss
.task-card {
  position: relative;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  padding: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    background: rgba(255, 255, 255, 0.2);
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  }
  
  &:focus {
    outline: 2px solid #4facfe;
    outline-offset: 2px;
  }
  
  &.selected {
    border-color: #4facfe;
    background: rgba(79, 172, 254, 0.2);
    box-shadow: 0 0 0 2px rgba(79, 172, 254, 0.3);
  }
  
  &.dragging {
    opacity: 0.6;
    transform: rotate(5deg);
    z-index: 1000;
  }
  
  // Priority styling
  &.priority-low {
    border-left: 4px solid #28a745;
  }
  
  &.priority-medium {
    border-left: 4px solid #ffc107;
  }
  
  &.priority-high {
    border-left: 4px solid #fd7e14;
  }
  
  &.priority-critical {
    border-left: 4px solid #dc3545;
    box-shadow: 0 0 10px rgba(220, 53, 69, 0.3);
  }
}

.drag-handle {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: grab;
  opacity: 0.6;
  transition: opacity 0.2s ease;
  
  &:hover {
    opacity: 1;
  }
  
  &:active {
    cursor: grabbing;
  }
  
  .drag-dots {
    font-size: 0.8rem;
    color: rgba(255, 255, 255, 0.8);
    line-height: 1;
  }
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 0.75rem;
  
  .task-priority {
    .priority-badge {
      display: inline-block;
      padding: 0.25rem 0.5rem;
      border-radius: 6px;
      font-size: 0.7rem;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      
      .priority-low & {
        background: rgba(40, 167, 69, 0.2);
        color: #28a745;
      }
      
      .priority-medium & {
        background: rgba(255, 193, 7, 0.2);
        color: #ffc107;
      }
      
      .priority-high & {
        background: rgba(253, 126, 20, 0.2);
        color: #fd7e14;
      }
      
      .priority-critical & {
        background: rgba(220, 53, 69, 0.2);
        color: #dc3545;
      }
    }
  }
  
  .task-actions {
    .edit-btn {
      background: none;
      border: none;
      padding: 0.25rem;
      border-radius: 4px;
      cursor: pointer;
      opacity: 0.7;
      transition: all 0.2s ease;
      
      &:hover {
        opacity: 1;
        background: rgba(255, 255, 255, 0.1);
      }
      
      &:focus {
        outline: 1px solid #4facfe;
        outline-offset: 1px;
      }
    }
  }
}

.task-content {
  margin-bottom: 0.75rem;
  
  .task-title {
    font-size: 1rem;
    font-weight: 600;
    color: #ffffff;
    margin: 0 0 0.5rem 0;
    line-height: 1.3;
    word-wrap: break-word;
  }
  
  .task-description {
    font-size: 0.85rem;
    color: rgba(255, 255, 255, 0.8);
    margin: 0;
    line-height: 1.4;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}

.task-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
  
  .task-assignee {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    
    .assignee-avatar {
      width: 24px;
      height: 24px;
      border-radius: 50%;
      overflow: hidden;
      background: rgba(255, 255, 255, 0.2);
      display: flex;
      align-items: center;
      justify-content: center;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
      
      .assignee-initials {
        font-size: 0.7rem;
        font-weight: 600;
        color: #ffffff;
      }
    }
    
    .assignee-name {
      font-size: 0.8rem;
      color: rgba(255, 255, 255, 0.9);
      font-weight: 500;
    }
  }
  
  .task-due-date {
    display: flex;
    align-items: center;
    gap: 0.25rem;
    padding: 0.25rem 0.5rem;
    border-radius: 6px;
    font-size: 0.75rem;
    font-weight: 500;
    
    &.normal {
      background: rgba(255, 255, 255, 0.1);
      color: rgba(255, 255, 255, 0.8);
    }
    
    &.due-soon {
      background: rgba(255, 193, 7, 0.2);
      color: #ffc107;
    }
    
    &.overdue {
      background: rgba(220, 53, 69, 0.2);
      color: #dc3545;
    }
    
    .due-date-icon {
      font-size: 0.7rem;
    }
  }
}

.task-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.25rem;
  
  .task-tag {
    display: inline-block;
    padding: 0.2rem 0.4rem;
    background: rgba(255, 255, 255, 0.15);
    border-radius: 4px;
    font-size: 0.7rem;
    color: rgba(255, 255, 255, 0.9);
    font-weight: 500;
  }
}

.drag-preview {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(79, 172, 254, 0.5);
  border-radius: 12px;
  padding: 1rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  
  .preview-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    h4 {
      font-size: 0.9rem;
      color: #333;
      margin: 0;
    }
    
    .preview-priority {
      font-size: 0.7rem;
      font-weight: 600;
      text-transform: uppercase;
      color: #666;
    }
  }
}

// Responsive Design
@media (max-width: 768px) {
  .task-card {
    padding: 0.75rem;
  }
  
  .task-content {
    .task-title {
      font-size: 0.9rem;
    }
    
    .task-description {
      font-size: 0.8rem;
      -webkit-line-clamp: 1;
    }
  }
  
  .task-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
  
  .drag-handle {
    display: none; // Hide drag handle on mobile
  }
}
```

### 3.4 HeaderComponent

**TypeScript Specification:**
```typescript
import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';
import { User } from '../../../models/user.model';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HeaderComponent {
  @Input() user!: User;
  @Input() searchValue: string = '';
  
  @Output() search = new EventEmitter<string>();
  @Output() notificationClick = new EventEmitter<void>();
  @Output() settingsClick = new EventEmitter<void>();
  @Output() profileClick = new EventEmitter<void>();

  isSearchFocused: boolean = false;
  showNotifications: boolean = false;
  showUserMenu: boolean = false;

  onSearchInput(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.search.emit(target.value);
  }

  onSearchFocus(): void {
    this.isSearchFocused = true;
  }

  onSearchBlur(): void {
    this.isSearchFocused = false;
  }

  onNotificationClick(): void {
    this.showNotifications = !this.showNotifications;
    this.notificationClick.emit();
  }

  onSettingsClick(): void {
    this.settingsClick.emit();
  }

  onProfileClick(): void {
    this.showUserMenu = !this.showUserMenu;
    this.profileClick.emit();
  }

  onUserMenuClose(): void {
    this.showUserMenu = false;
  }

  clearSearch(): void {
    this.search.emit('');
  }
}
```

**HTML Structure:**
```html
<header class="app-header" role="banner">
  
  <!-- Search Section -->
  <div class="search-section">
    <div class="search-container" 
         [class.focused]="isSearchFocused">
      <div class="search-icon" aria-hidden="true">
        🔍
      </div>
      <input type="text"
             class="search-input"
             [value]="searchValue"
             placeholder="Search tasks, projects, or people..."
             (input)="onSearchInput($event)"
             (focus)="onSearchFocus()"
             (blur)="onSearchBlur()"
             aria-label="Search tasks, projects, or people">
      <button class="search-clear"
              *ngIf="searchValue"
              (click)="clearSearch()"
              aria-label="Clear search">
        ✕
      </button>
    </div>
  </div>

  <!-- User Controls -->
  <div class="user-controls">
    
    <!-- Notifications -->
    <div class="control-item notification-control">
      <button class="notification-btn"
              [class.active]="showNotifications"
              (click)="onNotificationClick()"
              aria-label="View notifications"
              [attr.aria-expanded]="showNotifications">
        <span class="notification-icon">🔔</span>
        <span class="notification-badge" 
              *ngIf="user.notificationCount > 0"
              [attr.aria-label]="user.notificationCount + ' unread notifications'">
          {{ user.notificationCount > 99 ? '99+' : user.notificationCount }}
        </span>
      </button>
      
      <!-- Notification Dropdown -->
      <div class="notification-dropdown"
           *ngIf="showNotifications"
           role="menu"
           aria-label="Notifications menu">
        <div class="dropdown-header">
          <h3>Notifications</h3>
          <button class="close-btn" 
                  (click)="onNotificationClick()"
                  aria-label="Close notifications">
            ✕
          </button>
        </div>
        <div class="notification-list">
          <div class="notification-item" 
               *ngFor="let notification of user.notifications"
               role="menuitem">
            <div class="notification-content">
              <p class="notification-text">{{ notification.message }}</p>
              <span class="notification-time">{{ notification.timestamp | date:'short' }}</span>
            </div>
          </div>
          <div class="no-notifications" 
               *ngIf="!user.notifications || user.notifications.length === 0">
            <p>No new notifications</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Settings -->
    <div class="control-item">
      <button class="settings-btn"
              (click)="onSettingsClick()"
              aria-label="Open settings">
        <span class="settings-icon">⚙️</span>
      </button>
    </div>

    <!-- User Profile -->
    <div class="control-item user-profile">
      <button class="profile-btn"
              [class.active]="showUserMenu"
              (click)="onProfileClick()"
              [attr.aria-expanded]="showUserMenu"
              aria-label="User profile menu">
        <div class="user-avatar">
          <img [src]="user.avatar" 
               [alt]="user.name"
               *ngIf="user.avatar; else userInitials">
          <ng-template #userInitials>
            <span class="user-initials">
              {{ user.name.charAt(0).toUpperCase() }}
            </span>
          </ng-template>
        </div>
        <div class="user-info">
          <span class="user-name">{{ user.name }}</span>
          <span class="user-role">{{ user.role }}</span>
        </div>
        <span class="dropdown-arrow" 
              [class.rotated]="showUserMenu"
              aria-hidden="true">
          ▼
        </span>
      </button>
      
      <!-- User Menu Dropdown -->
      <div class="user-menu-dropdown"
           *ngIf="showUserMenu"
           role="menu"
           aria-label="User menu">
        <div class="menu-item" role="menuitem">
          <button class="menu-link">
            <span class="menu-icon">👤</span>
            Profile Settings
          </button>
        </div>
        <div class="menu-item" role="menuitem">
          <button class="menu-link">
            <span class="menu-icon">🎨</span>
            Appearance
          </button>
        </div>
        <div class="menu-item" role="menuitem">
          <button class="menu-link">
            <span class="menu-icon">🔐</span>
            Privacy & Security
          </button>
        </div>
        <div class="menu-divider"></div>
        <div class="menu-item" role="menuitem">
          <button class="menu-link logout">
            <span class="menu-icon">🚪</span>
            Sign Out
          </button>
        </div>
      </div>
    </div>

  </div>

  <!-- Backdrop for dropdowns -->
  <div class="dropdown-backdrop"
       *ngIf="showNotifications || showUserMenu"
       (click)="onUserMenuClose(); showNotifications = false"
       aria-hidden="true">
  </div>

</header>
```

**CSS Specification:**
```scss
.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
  height: 70px;
}

.search-section {
  flex: 1;
  max-width: 500px;
  
  .search-container {
    position: relative;
    display: flex;
    align-items: center;
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    border-radius: 12px;
    padding: 0.75rem 1rem;
    transition: all 0.3s ease;
    
    &.focused {
      background: rgba(255, 255, 255, 0.15);
      border-color: #4facfe;
      box-shadow: 0 0 0 3px rgba(79, 172, 254, 0.2);
    }
    
    .search-icon {
      margin-right: 0.75rem;
      font-size: 1.1rem;
      opacity: 0.7;
    }
    
    .search-input {
      flex: 1;
      background: none;
      border: none;
      outline: none;
      color: #ffffff;
      font-size: 1rem;
      
      &::placeholder {
        color: rgba(255, 255, 255, 0.6);
      }
    }
    
    .search-clear {
      background: none;
      border: none;
      color: rgba(255, 255, 255, 0.7);
      cursor: pointer;
      padding: 0.25rem;
      border-radius: 4px;
      transition: all 0.2s ease;
      
      &:hover {
        background: rgba(255, 255, 255, 0.1);
        color: #ffffff;
      }
    }
  }
}

.user-controls {
  display: flex;
  align-items: center;
  gap: 1rem;
  
  .control-item {
    position: relative;
  }
}

.notification-control {
  .notification-btn {
    position: relative;
    background: none;
    border: none;
    padding: 0.75rem;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover, &.active {
      background: rgba(255, 255, 255, 0.1);
    }
    
    .notification-icon {
      font-size: 1.2rem;
    }
    
    .notification-badge {
      position: absolute;
      top: 0.25rem;
      right: 0.25rem;
      background: #ff4757;
      color: #ffffff;
      font-size: 0.7rem;
      font-weight: 600;
      padding: 0.2rem 0.4rem;
      border-radius: 10px;
      min-width: 18px;
      text-align: center;
      line-height: 1;
    }
  }
  
  .notification-dropdown {
    position: absolute;
    top: calc(100% + 0.5rem);
    right: 0;
    width: 320px;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    border-radius: 12px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
    z-index: 1000;
    
    .dropdown-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1rem;
      border-bottom: 1px solid rgba(0, 0, 0, 0.1);
      
      h3 {
        margin: 0;
        font-size: 1.1rem;
        color: #333;
      }
      
      .close-btn {
        background: none;
        border: none;
        font-size: 1.2rem;
        cursor: pointer;
        padding: 0.25rem;
        border-radius: 4px;
        
        &:hover {
          background: rgba(0, 0, 0, 0.1);
        }
      }
    }
    
    .notification-list {
      max-height: 300px;
      overflow-y: auto;
      
      .notification-item {
        padding: 0.75rem 1rem;
        border-bottom: 1px solid rgba(0, 0, 0, 0.05);
        
        &:last-child {
          border-bottom: none;
        }
        
        &:hover {
          background: rgba(0, 0, 0, 0.05);
        }
        
        .notification-content {
          .notification-text {
            margin: 0 0 0.25rem 0;
            color: #333;
            font-size: 0.9rem;
            line-height: 1.4;
          }
          
          .notification-time {
            font-size: 0.8rem;
            color: #666;
          }
        }
      }
      
      .no-notifications {
        padding: 2rem 1rem;
        text-align: center;
        color: #666;
        
        p {
          margin: 0;
          font-style: italic;
        }
      }
    }
  }
}

.settings-btn {
  background: none;
  border: none;
  padding: 0.75rem;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    background: rgba(255, 255, 255, 0.1);
  }
  
  .settings-icon {
    font-size: 1.2rem;
  }
}

.user-profile {
  .profile-btn {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    background: none;
    border: none;
    padding: 0.5rem 0.75rem;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover, &.active {
      background: rgba(255, 255, 255, 0.1);
    }
    
    .user-avatar {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      overflow: hidden;
      background: rgba(255, 255, 255, 0.2);
      display: flex;
      align-items: center;
      justify-content: center;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
      
      .user-initials {
        font-size: 1rem;
        font-weight: 600;
        color: #ffffff;
      }
    }
    
    .user-info {
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      
      .user-name {
        font-size: 0.9rem;
        font-weight: 600;
        color: #ffffff;
        line-height: 1.2;
      }
      
      .user-role {
        font-size: 0.75rem;
        color: rgba(255, 255, 255, 0.7);
        line-height: 1.2;
      }
    }
    
    .dropdown-arrow {
      font-size: 0.8rem;
      color: rgba(255, 255, 255, 0.7);
      transition: transform 0.3s ease;
      
      &.rotated {
        transform: rotate(180deg);
      }
    }
  }
  
  .user-menu-dropdown {
    position: absolute;
    top: calc(100% + 0.5rem);
    right: 0;
    width: 220px;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    border-radius: 12px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
    z-index: 1000;
    padding: 0.5rem 0;
    
    .menu-item {
      .menu-link {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        width: 100%;
        padding: 0.75rem 1rem;
        background: none;
        border: none;
        text-align: left;
        cursor: pointer;
        transition: background 0.2s ease;
        color: #333;
        font-size: 0.9rem;
        
        &:hover {
          background: rgba(0, 0, 0, 0.05);
        }
        
        &.logout {
          color: #dc3545;
          
          &:hover {
            background: rgba(220, 53, 69, 0.1);
          }
        }
        
        .menu-icon {
          font-size: 1rem;
        }
      }
    }
    
    .menu-divider {
      height: 1px;
      background: rgba(0, 0, 0, 0.1);
      margin: 0.5rem 0;
    }
  }
}

.dropdown-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
  background: transparent;
}

// Responsive Design
@media (max-width: 768px) {
  .app-header {
    padding: 0.75rem 1rem;
    flex-direction: column;
    gap: 1rem;
    height: auto;
  }
  
  .search-section {
    order: 2;
    width: 100%;
    max-width: none;
  }
  
  .user-controls {
    order: 1;
    width: 100%;
    justify-content: space-between;
  }
  
  .user-profile {
    .profile-btn {
      .user-info {
        display: none;
      }
    }
    
    .user-menu-dropdown {
      width: 200px;
    }
  }
  
  .notification-dropdown {
    width: 280px !important;
  }
}

@media (max-width: 480px) {
  .app-header {
    padding: 0.5rem;
  }
  
  .search-section {
    .search-container {
      padding: 0.5rem 0.75rem;
      
      .search-input {
        font-size: 0.9rem;
      }
    }
  }
  
  .notification-dropdown {
    width: calc(100vw - 2rem) !important;
    right: -1rem;
  }
  
  .user-menu-dropdown {
    width: 180px;
  }
}
```

### 3.5 SidebarComponent

**TypeScript Specification:**
```typescript
import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';
import { Router } from '@angular/router';

interface MenuItem {
  id: string;
  label: string;
  icon: string;
  route: string;
  badge?: number;
  children?: MenuItem[];
}

interface MenuSection {
  title: string;
  items: MenuItem[];
}

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SidebarComponent {
  @Input() activeRoute: string = '';
  @Input() collapsed: boolean = false;
  
  @Output() navigate = new EventEmitter<string>();
  @Output() menuToggle = new EventEmitter<void>();

  expandedSections: Set<string> = new Set(['task-management']);

  menuSections: MenuSection[] = [
    {
      title: 'Task Management',
      items: [
        {
          id: 'kanban-board',
          label: 'Kanban Board',
          icon: '📋',
          route: '/kanban'
        },
        {
          id: 'collaborative-board',
          label: 'Collaborative Board',
          icon: '👥',
          route: '/collaborative',
          badge: 3
        }
      ]
    },
    {
      title: 'Analytics',
      items: [
        {
          id: 'progress-analytics',
          label: 'Progress Analytics',
          icon: '📊',
          route: '/analytics'
        },
        {
          id: 'report-builder',
          label: 'Report Builder',
          icon: '📈',
          route: '/reports'
        }
      ]
    },
    {
      title: 'Configuration',
      items: [
        {
          id: 'board-configuration',
          label: 'Board Configuration',
          icon: '⚙️',
          route: '/configuration'
        },
        {
          id: 'settings',
          label: 'Settings',
          icon: '🔧',
          route: '/settings'
        }
      ]
    }
  ];

  constructor(private router: Router) {}

  onNavigate(route: string): void {
    this.navigate.emit(route);
    this.router.navigate([route]);
  }

  onMenuToggle(): void {
    this.menuToggle.emit();
  }

  toggleSection(sectionTitle: string): void {
    if (this.expandedSections.has(sectionTitle)) {
      this.expandedSections.delete(sectionTitle);
    } else {
      this.expandedSections.add(sectionTitle);
    }
  }

  isSectionExpanded(sectionTitle: string): boolean {
    return this.expandedSections.has(sectionTitle);
  }

  isItemActive(route: string): boolean {
    return this.activeRoute === route || this.router.url === route;
  }
}
```

**HTML Structure:**
```html
<aside class="sidebar"
       [class.collapsed]="collapsed"
       role="navigation"
       aria-label="Main navigation">
  
  <!-- Brand Section -->
  <div class="brand-section">
    <div class="brand-logo">
      <div class="logo-icon">🎯</div>
      <h1 class="brand-name" 
          *ngIf="!collapsed"
          [@slideInOut]>
        TaskFlow
      </h1>
    </div>
    <button class="collapse-btn"
            (click)="onMenuToggle()"
            [attr.aria-label]="collapsed ? 'Expand sidebar' : 'Collapse sidebar'"
            [attr.aria-expanded]="!collapsed">
      <span class="collapse-icon" 
            [class.rotated]="collapsed">
        ◀
      </span>
    </button>
  </div>

  <!-- Navigation Menu -->
  <nav class="navigation-menu" role="menubar">
    
    <div class="menu-section"
         *ngFor="let section of menuSections"
         [attr.data-section]="section.title.toLowerCase().replace(' ', '-')">
      
      <!-- Section Header -->
      <div class="section-header"
           *ngIf="!collapsed">
        <button class="section-toggle"
                (click)="toggleSection(section.title)"
                [attr.aria-expanded]="isSectionExpanded(section.title)"
                [attr.aria-label]="'Toggle ' + section.title + ' section'">
          <span class="section-title">{{ section.title }}</span>
          <span class="section-arrow"
                [class.expanded]="isSectionExpanded(section.title)">
            ▼
          </span>
        </button>
      </div>

      <!-- Menu Items -->
      <ul class="menu-items"
          [class.collapsed-section]="!isSectionExpanded(section.title) && !collapsed"
          role="menu"
          [attr.aria-label]="section.title + ' menu items'">
        
        <li class="menu-item"
            *ngFor="let item of section.items"
            role="none">
          
          <button class="menu-link"
                  [class.active]="isItemActive(item.route)"
                  (click)="onNavigate(item.route)"
                  [attr.aria-current]="isItemActive(item.route) ? 'page' : null"
                  [attr.aria-label]="item.label"
                  role="menuitem">
            
            <span class="menu-icon" 
                  [attr.aria-hidden]="true">
              {{ item.icon }}
            </span>
            
            <span class="menu-label" 
                  *ngIf="!collapsed"
                  [@slideInOut]>
              {{ item.label }}
            </span>
            
            <span class="menu-badge"
                  *ngIf="item.badge && item.badge > 0 && !collapsed"
                  [attr.aria-label]="item.badge + ' notifications'"
                  [@slideInOut]>
              {{ item.badge > 99 ? '99+' : item.badge }}
            </span>
            
          </button>
          
          <!-- Tooltip for collapsed state -->
          <div class="menu-tooltip"
               *ngIf="collapsed"
               [attr.aria-hidden]="true">
            {{ item.label }}
            <span class="tooltip-badge" 
                  *ngIf="item.badge && item.badge > 0">
              {{ item.badge }}
            </span>
          </div>
          
        </li>
        
      </ul>
      
    </div>
    
  </nav>

  <!-- Footer Section -->
  <div class="sidebar-footer" 
       *ngIf="!collapsed"
       [@slideInOut]>
    <div class="footer-content">
      <p class="app-version">v2.1.0</p>
      <p class="copyright">© 2024 TaskFlow</p>
    </div>
  </div>

</aside>

<!-- Sidebar Overlay for mobile -->
<div class="sidebar-overlay"
     *ngIf="!collapsed"
     (click)="onMenuToggle()"
     [@fadeInOut]
     aria-hidden="true">
</div>
```

**CSS Specification:**
```scss
.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  height: 100vh;
  width: 280px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border-right: 1px solid rgba(255, 255, 255, 0.1);
  z-index: 200;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  
  &.collapsed {
    width: 70px;
    
    .menu-items {
      .menu-item {
        position: relative;
        
        &:hover .menu-tooltip {
          opacity: 1;
          visibility: visible;
          transform: translateX(0);
        }
      }
    }
  }
}

.brand-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.5rem 1rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  
  .brand-logo {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    
    .logo-icon {
      font-size: 1.8rem;
      display: flex;
      align-items: center;
      justify-content: center;
      width: 40px;
      height: 40px;
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      border-radius: 12px;
    }
    
    .brand-name {
      font-size: 1.4rem;
      font-weight: 700;
      color: #ffffff;
      margin: 0;
      letter-spacing: -0.5px;
    }
  }
  
  .collapse-btn {
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    border-radius: 8px;
    padding: 0.5rem;
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      background: rgba(255, 255, 255, 0.2);
    }
    
    .collapse-icon {
      display: block;
      color: #ffffff;
      font-size: 0.9rem;
      transition: transform 0.3s ease;
      
      &.rotated {
        transform: rotate(180deg);
      }
    }
  }
}

.navigation-menu {
  flex: 1;
  padding: 1rem 0;
  overflow-y: auto;
  
  .menu-section {
    margin-bottom: 1.5rem;
    
    &:last-child {
      margin-bottom: 0;
    }
  }
}

.section-header {
  padding: 0 1rem;
  margin-bottom: 0.5rem;
  
  .section-toggle {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    background: none;
    border: none;
    padding: 0.5rem 0;
    cursor: pointer;
    transition: all 0.2s ease;
    
    &:hover {
      .section-title {
        color: #4facfe;
      }
    }
    
    .section-title {
      font-size: 0.8rem;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 1px;
      color: rgba(255, 255, 255, 0.7);
      transition: color 0.2s ease;
    }
    
    .section-arrow {
      font-size: 0.7rem;
      color: rgba(255, 255, 255, 0.5);
      transition: transform 0.3s ease;
      
      &.expanded {
        transform: rotate(180deg);
      }
    }
  }
}

.menu-items {
  list-style: none;
  padding: 0;
  margin: 0;
  transition: all 0.3s ease;
  
  &.collapsed-section {
    max-height: 0;
    overflow: hidden;
    opacity: 0;
  }
  
  .menu-item {
    position: relative;
    
    .menu-link {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      width: 100%;
      padding: 0.75rem 1rem;
      background: none;
      border: none;
      text-align: left;
      cursor: pointer;
      transition: all 0.3s ease;
      border-radius: 0;
      position: relative;
      
      &:hover {
        background: rgba(255, 255, 255, 0.1);
        
        .menu-label {
          color: #4facfe;
        }
      }
      
      &.active {
        background: linear-gradient(135deg, rgba(79, 172, 254, 0.2) 0%, rgba(0, 242, 254, 0.1) 100%);
        border-right: 3px solid #4facfe;
        
        .menu-label {
          color: #4facfe;
          font-weight: 600;
        }
        
        .menu-icon {
          transform: scale(1.1);
        }
      }
      
      .menu-icon {
        font-size: 1.2rem;
        display: flex;
        align-items: center;
        justify-content: center;
        width: 24px;
        transition: transform 0.2s ease;
      }
      
      .menu-label {
        flex: 1;
        font-size: 0.95rem;
        font-weight: 500;
        color: rgba(255, 255, 255, 0.9);
        transition: color 0.2s ease;
      }
      
      .menu-badge {
        background: #ff4757;
        color: #ffffff;
        font-size: 0.7rem;
        font-weight: 600;
        padding: 0.2rem 0.4rem;
        border-radius: 10px;
        min-width: 18px;
        text-align: center;
        line-height: 1;
      }
    }
    
    .menu-tooltip {
      position: absolute;
      left: calc(100% + 0.5rem);
      top: 50%;
      transform: translateY(-50%) translateX(-10px);
      background: rgba(0, 0, 0, 0.9);
      color: #ffffff;
      padding: 0.5rem 0.75rem;
      border-radius: 6px;
      font-size: 0.8rem;
      font-weight: 500;
      white-space: nowrap;
      opacity: 0;
      visibility: hidden;
      transition: all 0.3s ease;
      z-index: 1000;
      
      &::before {
        content: '';
        position: absolute;
        left: -4px;
        top: 50%;
        transform: translateY(-50%);
        border: 4px solid transparent;
        border-right-color: rgba(0, 0, 0, 0.9);
      }
      
      .tooltip-badge {
        margin-left: 0.5rem;
        background: #ff4757;
        padding: 0.1rem 0.3rem;
        border-radius: 8px;
        font-size: 0.7rem;
      }
    }
  }
}

.sidebar-footer {
  padding: 1rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  
  .footer-content {
    text-align: center;
    
    .app-version {
      font-size: 0.8rem;
      color: rgba(255, 255, 255, 0.6);
      margin: 0 0 0.25rem 0;
    }
    
    .copyright {
      font-size: 0.75rem;
      color: rgba(255, 255, 255, 0.5);
      margin: 0;
    }
  }
}

.sidebar-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 199;
  display: none;
}

// Animations
@keyframes slideInOut {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes fadeInOut {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

// Responsive Design
@media (max-width: 1024px) {
  .sidebar {
    transform: translateX(-100%);
    
    &:not(.collapsed) {
      transform: translateX(0);
    }
  }
  
  .sidebar-overlay {
    display: block;
  }
}

@media (max-width: 768px) {
  .sidebar {
    width: 260px;
    
    &.collapsed {
      width: 60px;
    }
  }
  
  .brand-section {
    padding: 1rem 0.75rem;
    
    .brand-logo {
      .logo-icon {
        width: 36px;
        height: 36px;
        font-size: 1.6rem;
      }
      
      .brand-name {
        font-size: 1.2rem;
      }
    }
  }
  
  .menu-items {
    .menu-item {
      .menu-link {
        padding: 0.75rem;
        
        .menu-label {
          font-size: 0.9rem;
        }
      }
    }
  }
}

@media (max-width: 480px) {
  .sidebar {
    width: 240px;
  }
  
  .brand-section {
    padding: 0.75rem;
  }
  
  .menu-items {
    .menu-item {
      .menu-link {
        padding: 0.6rem 0.75rem;
      }
    }
  }
}
```

### 3.6 AddTaskModalComponent

**TypeScript Specification:**
```typescript
import { Component, Input, Output, EventEmitter, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Task, TaskPriority, TaskStatus } from '../../../models/task.model';
import { User } from '../../../models/user.model';
import { TaskService } from '../../../services/task.service';

@Component({
  selector: 'app-add-task-modal',
  templateUrl: './add-task-modal.component.html',
  styleUrls: ['./add-task-modal.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AddTaskModalComponent implements OnInit {
  @Input() isOpen: boolean = false;
  @Input() initialData: Partial<Task> | null = null;
  @Input() availableUsers: User[] = [];
  
  @Output() taskCreated = new EventEmitter<Task>();
  @Output() modalClose = new EventEmitter<void>();

  taskForm!: FormGroup;
  isSubmitting: boolean = false;
  validationErrors: { [key: string]: string } = {};
  
  priorityOptions = [
    { value: TaskPriority.LOW, label: 'Low', color: '#28a745' },
    { value: TaskPriority.MEDIUM, label: 'Medium', color: '#ffc107' },
    { value: TaskPriority.HIGH, label: 'High', color: '#fd7e14' },
    { value: TaskPriority.CRITICAL, label: 'Critical', color: '#dc3545' }
  ];
  
  statusOptions = [
    { value: TaskStatus.TODO, label: 'To Do' },
    { value: TaskStatus.IN_PROGRESS, label: 'In Progress' },
    { value: TaskStatus.DONE, label: 'Done' }
  ];

  constructor(
    private fb: FormBuilder,
    private taskService: TaskService
  ) {}

  ngOnInit(): void {
    this.initializeForm();
  }

  private initializeForm(): void {
    this.taskForm = this.fb.group({
      title: [this.initialData?.title || '', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      description: [this.initialData?.description || '', [Validators.maxLength(500)]],
      priority: [this.initialData?.priority || TaskPriority.MEDIUM, [Validators.required]],
      status: [this.initialData?.status || TaskStatus.TODO, [Validators.required]],
      assigneeId: [this.initialData?.assignee?.id || '', []],
      dueDate: [this.initialData?.dueDate ? new Date(this.initialData.dueDate).toISOString().split('T')[0] : '', []],
      tags: [this.initialData?.tags?.join(', ') || '', []]
    });
  }

  get formControls() {
    return this.taskForm.controls;
  }

  onSubmit(): void {
    if (this.taskForm.valid && !this.isSubmitting) {
      this.isSubmitting = true;
      this.validationErrors = {};
      
      const formValue = this.taskForm.value;
      const taskData: Partial<Task> = {
        title: formValue.title.trim(),
        description: formValue.description?.trim() || '',
        priority: formValue.priority,
        status: formValue.status,
        assignee: formValue.assigneeId ? this.availableUsers.find(u => u.id === formValue.assigneeId) : undefined,
        dueDate: formValue.dueDate ? new Date(formValue.dueDate).toISOString() : undefined,
        tags: formValue.tags ? formValue.tags.split(',').map((tag: string) => tag.trim()).filter((tag: string) => tag) : [],
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      };
      
      this.taskService.createTask(taskData).subscribe({
        next: (createdTask) => {
          this.taskCreated.emit(createdTask);
          this.onClose();
          this.isSubmitting = false;
        },
        error: (error) => {
          this.handleSubmissionError(error);
          this.isSubmitting = false;
        }
      });
    } else {
      this.markFormGroupTouched();
      this.validateForm();
    }
  }

  private handleSubmissionError(error: any): void {
    if (error.status === 400 && error.error?.validationErrors) {
      this.validationErrors = error.error.validationErrors;
    } else {
      this.validationErrors = {
        general: 'Failed to create task. Please try again.'
      };
    }
  }

  private markFormGroupTouched(): void {
    Object.keys(this.taskForm.controls).forEach(key => {
      this.taskForm.get(key)?.markAsTouched();
    });
  }

  private validateForm(): void {
    this.validationErrors = {};
    
    Object.keys(this.taskForm.controls).forEach(key => {
      const control = this.taskForm.get(key);
      if (control && control.invalid && control.touched) {
        this.validationErrors[key] = this.getErrorMessage(key, control.errors);
      }
    });
  }

  private getErrorMessage(fieldName: string, errors: any): string {
    if (errors['required']) {
      return `${fieldName.charAt(0).toUpperCase() + fieldName.slice(1)} is required`;
    }
    if (errors['minlength']) {
      return `${fieldName.charAt(0).toUpperCase() + fieldName.slice(1)} must be at least ${errors['minlength'].requiredLength} characters`;
    }
    if (errors['maxlength']) {
      return `${fieldName.charAt(0).toUpperCase() + fieldName.slice(1)} cannot exceed ${errors['maxlength'].requiredLength} characters`;
    }
    return `${fieldName.charAt(0).toUpperCase() + fieldName.slice(1)} is invalid`;
  }

  onClose(): void {
    this.taskForm.reset();
    this.validationErrors = {};
    this.isSubmitting = false;
    this.modalClose.emit();
  }

  onBackdropClick(event: Event): void {
    if (event.target === event.currentTarget) {
      this.onClose();
    }
  }

  onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Escape') {
      this.onClose();
    }
  }
}
```

**HTML Structure:**
```html
<div class="modal-backdrop"
     *ngIf="isOpen"
     (click)="onBackdropClick($event)"
     (keydown)="onKeyDown($event)"
     tabindex="-1"
     role="dialog"
     aria-modal="true"
     aria-labelledby="modal-title"
     [@fadeInOut]>
  
  <div class="modal-container">
    
    <!-- Modal Header -->
    <div class="modal-header">
      <h2 id="modal-title" class="modal-title">
        {{ initialData ? 'Edit Task' : 'Create New Task' }}
      </h2>
      <button class="close-btn"
              (click)="onClose()"
              aria-label="Close modal"
              type="button">
        ✕
      </button>
    </div>

    <!-- Modal Body -->
    <div class="modal-body">
      
      <!-- General Error -->
      <div class="error-alert" 
           *ngIf="validationErrors['general']"
           role="alert"
           aria-live="assertive">
        <span class="error-icon">⚠️</span>
        {{ validationErrors['general'] }}
      </div>

      <!-- Task Form -->
      <form [formGroup]="taskForm" 
            (ngSubmit)="onSubmit()"
            class="task-form"
            novalidate>
        
        <!-- Title Field -->
        <div class="form-group">
          <label for="task-title" class="form-label required">
            Task Title
          </label>
          <input type="text"
                 id="task-title"
                 class="form-input"
                 [class.error]="validationErrors['title']"
                 formControlName="title"
                 placeholder="Enter task title..."
                 maxlength="100"
                 aria-describedby="title-error title-hint">
          <div id="title-hint" class="form-hint">
            Provide a clear, descriptive title for the task
          </div>
          <div id="title-error" 
               class="form-error" 
               *ngIf="validationErrors['title']"
               role="alert">
            {{ validationErrors['title'] }}
          </div>
        </div>

        <!-- Description Field -->
        <div class="form-group">
          <label for="task-description" class="form-label">
            Description
          </label>
          <textarea id="task-description"
                    class="form-textarea"
                    [class.error]="validationErrors['description']"
                    formControlName="description"
                    placeholder="Describe the task in detail..."
                    rows="4"
                    maxlength="500"
                    aria-describedby="description-error description-hint">
          </textarea>
          <div id="description-hint" class="form-hint">
            Optional: Add more details about what needs to be done
          </div>
          <div id="description-error" 
               class="form-error" 
               *ngIf="validationErrors['description']"
               role="alert">
            {{ validationErrors['description'] }}
          </div>
        </div>

        <!-- Priority and Status Row -->
        <div class="form-row">
          
          <!-- Priority Field -->
          <div class="form-group">
            <label for="task-priority" class="form-label required">
              Priority
            </label>
            <select id="task-priority"
                    class="form-select"
                    [class.error]="validationErrors['priority']"
                    formControlName="priority"
                    aria-describedby="priority-error">
              <option *ngFor="let option of priorityOptions" 
                      [value]="option.value">
                {{ option.label }}
              </option>
            </select>
            <div id="priority-error" 
                 class="form-error" 
                 *ngIf="validationErrors['priority']"
                 role="alert">
              {{ validationErrors['priority'] }}
            </div>
          </div>

          <!-- Status Field -->
          <div class="form-group">
            <label for="task-status" class="form-label required">
              Status
            </label>
            <select id="task-status"
                    class="form-select"
                    [class.error]="validationErrors['status']"
                    formControlName="status"
                    aria-describedby="status-error">
              <option *ngFor="let option of statusOptions" 
                      [value]="option.value">
                {{ option.label }}
              </option>
            </select>
            <div id="status-error" 
                 class="form-error" 
                 *ngIf="validationErrors['status']"
                 role="alert">
              {{ validationErrors['status'] }}
            </div>
          </div>
          
        </div>

        <!-- Assignee and Due Date Row -->
        <div class="form-row">
          
          <!-- Assignee Field -->
          <div class="form-group">
            <label for="task-assignee" class="form-label">
              Assignee
            </label>
            <select id="task-assignee"
                    class="form-select"
                    [class.error]="validationErrors['assigneeId']"
                    formControlName="assigneeId"
                    aria-describedby="assignee-error">
              <option value="">Unassigned</option>
              <option *ngFor="let user of availableUsers" 
                      [value]="user.id">
                {{ user.name }} ({{ user.role }})
              </option>
            </select>
            <div id="assignee-error" 
                 class="form-error" 
                 *ngIf="validationErrors['assigneeId']"
                 role="alert">
              {{ validationErrors['assigneeId'] }}
            </div>
          </div>

          <!-- Due Date Field -->
          <div class="form-group">
            <label for="task-due-date" class="form-label">
              Due Date
            </label>
            <input type="date"
                   id="task-due-date"
                   class="form-input"
                   [class.error]="validationErrors['dueDate']"
                   formControlName="dueDate"
                   [min]="new Date().toISOString().split('T')[0]"
                   aria-describedby="due-date-error">
            <div id="due-date-error" 
                 class="form-error" 
                 *ngIf="validationErrors['dueDate']"
                 role="alert">
              {{ validationErrors['dueDate'] }}
            </div>
          </div>
          
        </div>

        <!-- Tags Field -->
        <div class="form-group">
          <label for="task-tags" class="form-label">
            Tags
          </label>
          <input type="text"
                 id="task-tags"
                 class="form-input"
                 [class.error]="validationErrors['tags']"
                 formControlName="tags"
                 placeholder="Enter tags separated by commas..."
                 aria-describedby="tags-error tags-hint">
          <div id="tags-hint" class="form-hint">
            Optional: Add tags separated by commas (e.g., urgent, frontend, bug)
          </div>
          <div id="tags-error" 
               class="form-error" 
               *ngIf="validationErrors['tags']"
               role="alert">
            {{ validationErrors['tags'] }}
          </div>
        </div>

      </form>
      
    </div>

    <!-- Modal Footer -->
    <div class="modal-footer">
      <button type="button"
              class="btn btn-secondary"
              (click)="onClose()"
              [disabled]="isSubmitting">
        Cancel
      </button>
      <button type="submit"
              class="btn btn-primary"
              [class.loading]="isSubmitting"
              [disabled]="taskForm.invalid || isSubmitting"
              (click)="onSubmit()">
        <span *ngIf="!isSubmitting">
          {{ initialData ? 'Update Task' : 'Create Task' }}
        </span>
        <span *ngIf="isSubmitting" class="loading-content">
          <span class="loading-spinner"></span>
          {{ initialData ? 'Updating...' : 'Creating...' }}
        </span>
      </button>
    </div>

  </div>
  
</div>
```

**CSS Specification:**
```scss
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

.modal-container {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  width: 100%;
  max-width: 600px;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  animation: modalSlideIn 0.3s ease-out;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-50px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  
  .modal-title {
    font-size: 1.4rem;
    font-weight: 600;
    color: #ffffff;
    margin: 0;
  }
  
  .close-btn {
    background: rgba(255, 255, 255, 0.2);
    border: none;
    border-radius: 8px;
    padding: 0.5rem;
    cursor: pointer;
    font-size: 1.2rem;
    color: #ffffff;
    transition: all 0.2s ease;
    
    &:hover {
      background: rgba(255, 255, 255, 0.3);
      transform: scale(1.1);
    }
  }
}

.modal-body {
  flex: 1;
  padding: 1.5rem;
  overflow-y: auto;
  
  .error-alert {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem 1rem;
    background: rgba(220, 53, 69, 0.1);
    border: 1px solid rgba(220, 53, 69, 0.3);
    border-radius: 8px;
    color: #dc3545;
    margin-bottom: 1.5rem;
    
    .error-icon {
      font-size: 1.1rem;
    }
  }
}

.task-form {
  .form-group {
    margin-bottom: 1.5rem;
    
    .form-label {
      display: block;
      font-size: 0.9rem;
      font-weight: 600;
      color: #333;
      margin-bottom: 0.5rem;
      
      &.required::after {
        content: ' *';
        color: #dc3545;
      }
    }
    
    .form-input,
    .form-textarea,
    .form-select {
      width: 100%;
      padding: 0.75rem;
      border: 1px solid rgba(0, 0, 0, 0.2);
      border-radius: 8px;
      font-size: 0.95rem;
      background: rgba(255, 255, 255, 0.8);
      transition: all 0.3s ease;
      
      &:focus {
        outline: none;
        border-color: #4facfe;
        box-shadow: 0 0 0 3px rgba(79, 172, 254, 0.2);
        background: #ffffff;
      }
      
      &.error {
        border-color: #dc3545;
        box-shadow: 0 0 0 3px rgba(220, 53, 69, 0.2);
      }
      
      &::placeholder {
        color: rgba(0, 0, 0, 0.5);
      }
    }
    
    .form-textarea {
      resize: vertical;
      min-height: 100px;
      font-family: inherit;
    }
    
    .form-select {
      cursor: pointer;
      
      option {
        padding: 0.5rem;
      }
    }
    
    .form-hint {
      font-size: 0.8rem;
      color: rgba(0, 0, 0, 0.6);
      margin-top: 0.25rem;
      line-height: 1.4;
    }
    
    .form-error {
      font-size: 0.8rem;
      color: #dc3545;
      margin-top: 0.25rem;
      display: flex;
      align-items: center;
      gap: 0.25rem;
      
      &::before {
        content: '⚠️';
        font-size: 0.7rem;
      }
    }
  }
  
  .form-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1rem;
    
    @media (max-width: 480px) {
      grid-template-columns: 1fr;
      gap: 0;
    }
  }
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  padding: 1.5rem;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
  background: rgba(0, 0, 0, 0.02);
  
  .btn {
    padding: 0.75rem 1.5rem;
    border: none;
    border-radius: 8px;
    font-size: 0.95rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    
    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
      transform: none !important;
    }
    
    &.btn-secondary {
      background: rgba(0, 0, 0, 0.1);
      color: #333;
      
      &:hover:not(:disabled) {
        background: rgba(0, 0, 0, 0.15);
        transform: translateY(-1px);
      }
    }
    
    &.btn-primary {
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      color: #ffffff;
      
      &:hover:not(:disabled) {
        transform: translateY(-2px);
        box-shadow: 0 8px 25px rgba(79, 172, 254, 0.4);
      }
      
      &.loading {
        .loading-content {
          display: flex;
          align-items: center;
          gap: 0.5rem;
          
          .loading-spinner {
            width: 16px;
            height: 16px;
            border: 2px solid rgba(255, 255, 255, 0.3);
            border-top: 2px solid #ffffff;
            border-radius: 50%;
            animation: spin 1s linear infinite;
          }
        }
      }
    }
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes fadeInOut {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

// Responsive Design
@media (max-width: 768px) {
  .modal-backdrop {
    padding: 0.5rem;
  }
  
  .modal-container {
    max-height: 95vh;
  }
  
  .modal-header {
    padding: 1rem;
    
    .modal-title {
      font-size: 1.2rem;
    }
  }
  
  .modal-body {
    padding: 1rem;
  }
  
  .modal-footer {
    padding: 1rem;
    flex-direction: column;
    
    .btn {
      width: 100%;
      justify-content: center;
    }
  }
}

@media (max-width: 480px) {
  .modal-container {
    margin: 0;
    border-radius: 12px;
  }
  
  .task-form {
    .form-group {
      margin-bottom: 1rem;
      
      .form-input,
      .form-textarea,
      .form-select {
        padding: 0.6rem;
        font-size: 0.9rem;
      }
    }
  }
}
```

### 3.7 TaskDetailComponent

**TypeScript Specification:**
```typescript
import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges, ChangeDetectionStrategy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Task, TaskPriority, TaskStatus, Comment } from '../../../models/task.model';
import { User } from '../../../models/user.model';
import { TaskService } from '../../../services/task.service';

@Component({
  selector: 'app-task-detail',
  templateUrl: './task-detail.component.html',
  styleUrls: ['./task-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TaskDetailComponent implements OnInit, OnChanges {
  @Input() task!: Task;
  @Input() isOpen: boolean = false;
  @Input() availableUsers: User[] = [];
  
  @Output() taskUpdate = new EventEmitter<Task>();
  @Output() taskDelete = new EventEmitter<string>();
  @Output() modalClose = new EventEmitter<void>();
  @Output() commentAdd = new EventEmitter<{taskId: string, comment: string}>();

  editForm!: FormGroup;
  commentForm!: FormGroup;
  
  isEditing: boolean = false;
  isLoading: boolean = false;
  isSubmitting: boolean = false;
  isAddingComment: boolean = false;
  
  comments: Comment[] = [];
  validationErrors: { [key: string]: string } = {};
  
  activeTab: 'details' | 'comments' | 'activity' = 'details';
  
  priorityOptions = [
    { value: TaskPriority.LOW, label: 'Low', color: '#28a745' },
    { value: TaskPriority.MEDIUM, label: 'Medium', color: '#ffc107' },
    { value: TaskPriority.HIGH, label: 'High', color: '#fd7e14' },
    { value: TaskPriority.CRITICAL, label: 'Critical', color: '#dc3545' }
  ];
  
  statusOptions = [
    { value: TaskStatus.TODO, label: 'To Do' },
    { value: TaskStatus.IN_PROGRESS, label: 'In Progress' },
    { value: TaskStatus.DONE, label: 'Done' }
  ];

  constructor(
    private fb: FormBuilder,
    private taskService: TaskService
  ) {}

  ngOnInit(): void {
    this.initializeForms();
    if (this.task) {
      this.loadTaskDetails();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['task'] && this.task) {
      this.initializeForms();
      this.loadTaskDetails();
    }
  }

  private initializeForms(): void {
    this.editForm = this.fb.group({
      title: [this.task?.title || '', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      description: [this.task?.description || '', [Validators.maxLength(500)]],
      priority: [this.task?.priority || TaskPriority.MEDIUM, [Validators.required]],
      status: [this.task?.status || TaskStatus.TODO, [Validators.required]],
      assigneeId: [this.task?.assignee?.id || '', []],
      dueDate: [this.task?.dueDate ? new Date(this.task.dueDate).toISOString().split('T')[0] : '', []],
      tags: [this.task?.tags?.join(', ') || '', []]
    });
    
    this.commentForm = this.fb.group({
      comment: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(500)]]
    });
  }

  private loadTaskDetails(): void {
    if (!this.task?.id) return;
    
    this.isLoading = true;
    this.taskService.getTaskById(this.task.id).subscribe({
      next: (taskDetails) => {
        this.task = taskDetails;
        this.comments = taskDetails.comments || [];
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading task details:', error);
        this.isLoading = false;
      }
    });
  }

  get formControls() {
    return this.editForm.controls;
  }

  get commentControl() {
    return this.commentForm.get('comment');
  }

  get formattedDueDate(): string {
    if (!this.task?.dueDate) return 'No due date';
    return new Date(this.task.dueDate).toLocaleDateString('en-US', {
      weekday: 'short',
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  get dueDateStatus(): 'overdue' | 'due-soon' | 'normal' {
    if (!this.task?.dueDate) return 'normal';
    
    const now = new Date();
    const dueDate = new Date(this.task.dueDate);
    const diffDays = Math.ceil((dueDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24));
    
    if (diffDays < 0) return 'overdue';
    if (diffDays <= 2) return 'due-soon';
    return 'normal';
  }

  get priorityColor(): string {
    const priority = this.priorityOptions.find(p => p.value === this.task?.priority);
    return priority?.color || '#6c757d';
  }

  onEdit(): void {
    this.isEditing = true;
    this.validationErrors = {};
  }

  onCancelEdit(): void {
    this.isEditing = false;
    this.initializeForms();
    this.validationErrors = {};
  }

  onSave(): void {
    if (this.editForm.valid && !this.isSubmitting) {
      this.isSubmitting = true;
      this.validationErrors = {};
      
      const formValue = this.editForm.value;
      const updatedTask: Partial<Task> = {
        ...this.task,
        title: formValue.title.trim(),
        description: formValue.description?.trim() || '',
        priority: formValue.priority,
        status: formValue.status,
        assignee: formValue.assigneeId ? this.availableUsers.find(u => u.id === formValue.assigneeId) : undefined,
        dueDate: formValue.dueDate ? new Date(formValue.dueDate).toISOString() : undefined,
        tags: formValue.tags ? formValue.tags.split(',').map((tag: string) => tag.trim()).filter((tag: string) => tag) : [],
        updatedAt: new Date().toISOString()
      };
      
      this.taskService.updateTask(this.task.id, updatedTask).subscribe({
        next: (updated) => {
          this.task = updated;
          this.taskUpdate.emit(updated);
          this.isEditing = false;
          this.isSubmitting = false;
        },
        error: (error) => {
          this.handleSubmissionError(error);
          this.isSubmitting = false;
        }
      });
    } else {
      this.markFormGroupTouched();
      this.validateForm();
    }
  }

  onDelete(): void {
    if (confirm('Are you sure you want to delete this task? This action cannot be undone.')) {
      this.taskService.deleteTask(this.task.id).subscribe({
        next: () => {
          this.taskDelete.emit(this.task.id);
          this.onClose();
        },
        error: (error) => {
          console.error('Error deleting task:', error);
          this.validationErrors = {
            general: 'Failed to delete task. Please try again.'
          };
        }
      });
    }
  }

  onAddComment(): void {
    if (this.commentForm.valid && !this.isAddingComment) {
      this.isAddingComment = true;
      const commentText = this.commentForm.value.comment.trim();
      
      this.commentAdd.emit({
        taskId: this.task.id,
        comment: commentText
      });
      
      // Simulate API call for comment addition
      setTimeout(() => {
        const newComment: Comment = {
          id: Date.now().toString(),
          text: commentText,
          author: { id: '1', name: 'Current User', avatar: '' },
          createdAt: new Date().toISOString()
        };
        
        this.comments.unshift(newComment);
        this.commentForm.reset();
        this.isAddingComment = false;
      }, 500);
    }
  }

  onTabChange(tab: 'details' | 'comments' | 'activity'): void {
    this.activeTab = tab;
  }

  onClose(): void {
    this.isEditing = false;
    this.validationErrors = {};
    this.activeTab = 'details';
    this.modalClose.emit();
  }

  onBackdropClick(event: Event): void {
    if (event.target === event.currentTarget) {
      this.onClose();
    }
  }

  onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Escape') {
      this.onClose();
    }
  }

  private handleSubmissionError(error: any): void {
    if (error.status === 400 && error.error?.validationErrors) {
      this.validationErrors = error.error.validationErrors;
    } else {
      this.validationErrors = {
        general: 'Failed to update task. Please try again.'
      };
    }
  }

  private markFormGroupTouched(): void {
    Object.keys(this.editForm.controls).forEach(key => {
      this.editForm.get(key)?.markAsTouched();
    });
  }

  private validateForm(): void {
    this.validationErrors = {};
    
    Object.keys(this.editForm.controls).forEach(key => {
      const control = this.editForm.get(key);
      if (control && control.invalid && control.touched) {
        this.validationErrors[key] = this.getErrorMessage(key, control.errors);
      }
    });
  }

  private getErrorMessage(fieldName: string, errors: any): string {
    if (errors['required']) {
      return `${fieldName.charAt(0).toUpperCase() + fieldName.slice(1)} is required`;
    }
    if (errors['minlength']) {
      return `${fieldName.charAt(0).toUpperCase() + fieldName.slice(1)} must be at least ${errors['minlength'].requiredLength} characters`;
    }
    if (errors['maxlength']) {
      return `${fieldName.charAt(0).toUpperCase() + fieldName.slice(1)} cannot exceed ${errors['maxlength'].requiredLength} characters`;
    }
    return `${fieldName.charAt(0).toUpperCase() + fieldName.slice(1)} is invalid`;
  }
}
```

**HTML Structure:**
```html
<div class="modal-backdrop"
     *ngIf="isOpen"
     (click)="onBackdropClick($event)"
     (keydown)="onKeyDown($event)"
     tabindex="-1"
     role="dialog"
     aria-modal="true"
     aria-labelledby="task-detail-title"
     [@fadeInOut]>
  
  <div class="task-detail-container">
    
    <!-- Loading State -->
    <div *ngIf="isLoading" class="loading-state">
      <div class="loading-spinner"></div>
      <span>Loading task details...</span>
    </div>

    <!-- Task Detail Content -->
    <div *ngIf="!isLoading" class="task-detail-content">
      
      <!-- Header -->
      <div class="detail-header">
        <div class="header-left">
          <div class="task-priority-badge" 
               [style.background-color]="priorityColor">
            {{ task.priority }}
          </div>
          <h2 id="task-detail-title" class="task-title">
            {{ task.title }}
          </h2>
        </div>
        <div class="header-actions">
          <button class="action-btn edit-btn"
                  *ngIf="!isEditing"
                  (click)="onEdit()"
                  aria-label="Edit task">
            ✏️ Edit
          </button>
          <button class="action-btn delete-btn"
                  (click)="onDelete()"
                  aria-label="Delete task">
            🗑️ Delete
          </button>
          <button class="close-btn"
                  (click)="onClose()"
                  aria-label="Close task details">
            ✕
          </button>
        </div>
      </div>

      <!-- Tab Navigation -->
      <div class="tab-navigation" role="tablist">
        <button class="tab-btn"
                [class.active]="activeTab === 'details'"
                (click)="onTabChange('details')"
                role="tab"
                [attr.aria-selected]="activeTab === 'details'"
                aria-controls="details-panel">
          📋 Details
        </button>
        <button class="tab-btn"
                [class.active]="activeTab === 'comments'"
                (click)="onTabChange('comments')"
                role="tab"
                [attr.aria-selected]="activeTab === 'comments'"
                aria-controls="comments-panel">
          💬 Comments ({{ comments.length }})
        </button>
        <button class="tab-btn"
                [class.active]="activeTab === 'activity'"
                (click)="onTabChange('activity')"
                role="tab"
                [attr.aria-selected]="activeTab === 'activity'"
                aria-controls="activity-panel">
          📈 Activity
        </button>
      </div>

      <!-- Tab Content -->
      <div class="tab-content">
        
        <!-- Details Tab -->
        <div class="tab-panel"
             *ngIf="activeTab === 'details'"
             id="details-panel"
             role="tabpanel"
             aria-labelledby="details-tab">
          
          <!-- General Error -->
          <div class="error-alert" 
               *ngIf="validationErrors['general']"
               role="alert">
            <span class="error-icon">⚠️</span>
            {{ validationErrors['general'] }}
          </div>

          <!-- View Mode -->
          <div *ngIf="!isEditing" class="view-mode">
            
            <!-- Task Meta -->
            <div class="task-meta">
              <div class="meta-row">
                <div class="meta-item">
                  <span class="meta-label">Status:</span>
                  <span class="meta-value status-badge" 
                        [attr.data-status]="task.status">
                    {{ statusOptions.find(s => s.value === task.status)?.label }}
                  </span>
                </div>
                <div class="meta-item">
                  <span class="meta-label">Priority:</span>
                  <span class="meta-value priority-badge" 
                        [style.color]="priorityColor">
                    {{ priorityOptions.find(p => p.value === task.priority)?.label }}
                  </span>
                </div>
              </div>
              
              <div class="meta-row">
                <div class="meta-item">
                  <span class="meta-label">Assignee:</span>
                  <div class="meta-value assignee-info" 
                       *ngIf="task.assignee; else unassigned">
                    <div class="assignee-avatar">
                      <img [src]="task.assignee.avatar" 
                           [alt]="task.assignee.name"
                           *ngIf="task.assignee.avatar; else initials">
                      <ng-template #initials>
                        <span>{{ task.assignee.name.charAt(0).toUpperCase() }}</span>
                      </ng-template>
                    </div>
                    <span class="assignee-name">{{ task.assignee.name }}</span>
                  </div>
                  <ng-template #unassigned>
                    <span class="meta-value unassigned">Unassigned</span>
                  </ng-template>
                </div>
                <div class="meta-item">
                  <span class="meta-label">Due Date:</span>
                  <span class="meta-value due-date" 
                        [ngClass]="dueDateStatus">
                    {{ formattedDueDate }}
                  </span>
                </div>
              </div>
            </div>

            <!-- Description -->
            <div class="task-description">
              <h4>Description</h4>
              <p *ngIf="task.description; else noDescription">
                {{ task.description }}
              </p>
              <ng-template #noDescription>
                <p class="no-content">No description provided</p>
              </ng-template>
            </div>

            <!-- Tags -->
            <div class="task-tags" *ngIf="task.tags && task.tags.length > 0">
              <h4>Tags</h4>
              <div class="tags-list">
                <span class="tag" *ngFor="let tag of task.tags">
                  {{ tag }}
                </span>
              </div>
            </div>

            <!-- Timestamps -->
            <div class="task-timestamps">
              <div class="timestamp">
                <span class="timestamp-label">Created:</span>
                <span class="timestamp-value">
                  {{ task.createdAt | date:'medium' }}
                </span>
              </div>
              <div class="timestamp" *ngIf="task.updatedAt !== task.createdAt">
                <span class="timestamp-label">Updated:</span>
                <span class="timestamp-value">
                  {{ task.updatedAt | date:'medium' }}
                </span>
              </div>
            </div>
            
          </div>

          <!-- Edit Mode -->
          <div *ngIf="isEditing" class="edit-mode">
            <form [formGroup]="editForm" class="edit-form">
              
              <!-- Title -->
              <div class="form-group">
                <label for="edit-title" class="form-label required">Title</label>
                <input type="text"
                       id="edit-title"
                       class="form-input"
                       [class.error]="validationErrors['title']"
                       formControlName="title"
                       maxlength="100">
                <div class="form-error" 
                     *ngIf="validationErrors['title']">
                  {{ validationErrors['title'] }}
                </div>
              </div>

              <!-- Description -->
              <div class="form-group">
                <label for="edit-description" class="form-label">Description</label>
                <textarea id="edit-description"
                          class="form-textarea"
                          [class.error]="validationErrors['description']"
                          formControlName="description"
                          rows="4"
                          maxlength="500">
                </textarea>
                <div class="form-error" 
                     *ngIf="validationErrors['description']">
                  {{ validationErrors['description'] }}
                </div>
              </div>

              <!-- Priority and Status -->
              <div class="form-row">
                <div class="form-group">
                  <label for="edit-priority" class="form-label required">Priority</label>
                  <select id="edit-priority"
                          class="form-select"
                          formControlName="priority">
                    <option *ngFor="let option of priorityOptions" 
                            [value]="option.value">
                      {{ option.label }}
                    </option>
                  </select>
                </div>
                <div class="form-group">
                  <label for="edit-status" class="form-label required">Status</label>
                  <select id="edit-status"
                          class="form-select"
                          formControlName="status">
                    <option *ngFor="let option of statusOptions" 
                            [value]="option.value">
                      {{ option.label }}
                    </option>
                  </select>
                </div>
              </div>

              <!-- Assignee and Due Date -->
              <div class="form-row">
                <div class="form-group">
                  <label for="edit-assignee" class="form-label">Assignee</label>
                  <select id="edit-assignee"
                          class="form-select"
                          formControlName="assigneeId">
                    <option value="">Unassigned</option>
                    <option *ngFor="let user of availableUsers" 
                            [value]="user.id">
                      {{ user.name }}
                    </option>
                  </select>
                </div>
                <div class="form-group">
                  <label for="edit-due-date" class="form-label">Due Date</label>
                  <input type="date"
                         id="edit-due-date"
                         class="form-input"
                         formControlName="dueDate">
                </div>
              </div>

              <!-- Tags -->
              <div class="form-group">
                <label for="edit-tags" class="form-label">Tags</label>
                <input type="text"
                       id="edit-tags"
                       class="form-input"
                       formControlName="tags"
                       placeholder="Enter tags separated by commas">
              </div>

              <!-- Form Actions -->
              <div class="form-actions">
                <button type="button"
                        class="btn btn-secondary"
                        (click)="onCancelEdit()"
                        [disabled]="isSubmitting">
                  Cancel
                </button>
                <button type="button"
                        class="btn btn-primary"
                        [class.loading]="isSubmitting"
                        [disabled]="editForm.invalid || isSubmitting"
                        (click)="onSave()">
                  <span *ngIf="!isSubmitting">Save Changes</span>
                  <span *ngIf="isSubmitting" class="loading-content">
                    <span class="loading-spinner"></span>
                    Saving...
                  </span>
                </button>
              </div>
              
            </form>
          </div>
          
        </div>

        <!-- Comments Tab -->
        <div class="tab-panel"
             *ngIf="activeTab === 'comments'"
             id="comments-panel"
             role="tabpanel"
             aria-labelledby="comments-tab">
          
          <!-- Add Comment Form -->
          <div class="add-comment-section">
            <h4>Add Comment</h4>
            <form [formGroup]="commentForm" 
                  (ngSubmit)="onAddComment()"
                  class="comment-form">
              <div class="form-group">
                <textarea class="comment-input"
                          formControlName="comment"
                          placeholder="Write a comment..."
                          rows="3"
                          maxlength="500">
                </textarea>
              </div>
              <div class="comment-actions">
                <button type="submit"
                        class="btn btn-primary btn-sm"
                        [disabled]="commentForm.invalid || isAddingComment">
                  <span *ngIf="!isAddingComment">Add Comment</span>
                  <span *ngIf="isAddingComment">Adding...</span>
                </button>
              </div>
            </form>
          </div>

          <!-- Comments List -->
          <div class="comments-section">
            <h4>Comments ({{ comments.length }})</h4>
            
            <div class="comments-list" *ngIf="comments.length > 0; else noComments">
              <div class="comment-item" 
                   *ngFor="let comment of comments">
                <div class="comment-header">
                  <div class="comment-author">
                    <div class="author-avatar">
                      <img [src]="comment.author.avatar" 
                           [alt]="comment.author.name"
                           *ngIf="comment.author.avatar; else authorInitials">
                      <ng-template #authorInitials>
                        <span>{{ comment.author.name.charAt(0).toUpperCase() }}</span>
                      </ng-template>
                    </div>
                    <span class="author-name">{{ comment.author.name }}</span>
                  </div>
                  <span class="comment-time">
                    {{ comment.createdAt | date:'short' }}
                  </span>
                </div>
                <div class="comment-content">
                  <p>{{ comment.text }}</p>
                </div>
              </div>
            </div>
            
            <ng-template #noComments>
              <div class="no-comments">
                <p>No comments yet. Be the first to comment!</p>
              </div>
            </ng-template>
            
          </div>
          
        </div>

        <!-- Activity Tab -->
        <div class="tab-panel"
             *ngIf="activeTab === 'activity'"
             id="activity-panel"
             role="tabpanel"
             aria-labelledby="activity-tab">
          
          <div class="activity-section">
            <h4>Task Activity</h4>
            <div class="activity-placeholder">
              <p>Activity tracking will be implemented in a future update.</p>
              <p>This will show task history, status changes, and user interactions.</p>
            </div>
          </div>
          
        </div>
        
      </div>
      
    </div>
    
  </div>
  
</div>
```

**CSS Specification:**
```scss
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

.task-detail-container {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  width: 100%;
  max-width: 800px;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  animation: modalSlideIn 0.3s ease-out;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem;
  color: #666;
  
  .loading-spinner {
    width: 40px;
    height: 40px;
    border: 4px solid rgba(0, 0, 0, 0.1);
    border-top: 4px solid #4facfe;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: 1rem;
  }
}

.task-detail-content {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 1.5rem;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 1rem;
    flex: 1;
    
    .task-priority-badge {
      padding: 0.4rem 0.8rem;
      border-radius: 8px;
      color: #ffffff;
      font-size: 0.8rem;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }
    
    .task-title {
      font-size: 1.4rem;
      font-weight: 600;
      color: #ffffff;
      margin: 0;
      line-height: 1.3;
      word-break: break-word;
    }
  }
  
  .header-actions {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    
    .action-btn {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.5rem 1rem;
      background: rgba(255, 255, 255, 0.2);
      border: 1px solid rgba(255, 255, 255, 0.3);
      border-radius: 8px;
      color: #ffffff;
      font-size: 0.85rem;
      font-weight: 500;
      cursor: pointer;
      transition: all 0.2s ease;
      
      &:hover {
        background: rgba(255, 255, 255, 0.3);
        transform: translateY(-1px);
      }
      
      &.delete-btn:hover {
        background: rgba(220, 53, 69, 0.8);
        border-color: rgba(220, 53, 69, 0.9);
      }
    }
    
    .close-btn {
      background: rgba(255, 255, 255, 0.2);
      border: none;
      border-radius: 8px;
      padding: 0.5rem;
      cursor: pointer;
      font-size: 1.2rem;
      color: #ffffff;
      transition: all 0.2s ease;
      
      &:hover {
        background: rgba(255, 255, 255, 0.3);
        transform: scale(1.1);
      }
    }
  }
}

.tab-navigation {
  display: flex;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  background: rgba(0, 0, 0, 0.02);
  
  .tab-btn {
    flex: 1;
    padding: 1rem 1.5rem;
    background: none;
    border: none;
    font-size: 0.9rem;
    font-weight: 500;
    color: #666;
    cursor: pointer;
    transition: all 0.3s ease;
    border-bottom: 3px solid transparent;
    
    &:hover {
      background: rgba(0, 0, 0, 0.05);
      color: #333;
    }
    
    &.active {
      color: #4facfe;
      border-bottom-color: #4facfe;
      background: rgba(79, 172, 254, 0.05);
    }
  }
}

.tab-content {
  flex: 1;
  overflow-y: auto;
}

.tab-panel {
  padding: 1.5rem;
  
  .error-alert {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem 1rem;
    background: rgba(220, 53, 69, 0.1);
    border: 1px solid rgba(220, 53, 69, 0.3);
    border-radius: 8px;
    color: #dc3545;
    margin-bottom: 1.5rem;
  }
}

// View Mode Styles
.view-mode {
  .task-meta {
    margin-bottom: 2rem;
    
    .meta-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 1.5rem;
      margin-bottom: 1rem;
      
      @media (max-width: 600px) {
        grid-template-columns: 1fr;
        gap: 1rem;
      }
    }
    
    .meta-item {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
      
      .meta-label {
        font-size: 0.85rem;
        font-weight: 600;
        color: #666;
        text-transform: uppercase;
        letter-spacing: 0.5px;
      }
      
      .meta-value {
        font-size: 0.95rem;
        color: #333;
        
        &.status-badge {
          display: inline-block;
          padding: 0.4rem 0.8rem;
          border-radius: 6px;
          font-weight: 600;
          text-transform: uppercase;
          font-size: 0.8rem;
          
          &[data-status="TODO"] {
            background: rgba(255, 107, 107, 0.2);
            color: #ff6b6b;
          }
          
          &[data-status="IN_PROGRESS"] {
            background: rgba(78, 205, 196, 0.2);
            color: #4ecdc4;
          }
          
          &[data-status="DONE"] {
            background: rgba(69, 183, 209, 0.2);
            color: #45b7d1;
          }
        }
        
        &.priority-badge {
          font-weight: 600;
          text-transform: uppercase;
          font-size: 0.85rem;
        }
        
        &.assignee-info {
          display: flex;
          align-items: center;
          gap: 0.75rem;
          
          .assignee-avatar {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            overflow: hidden;
            background: rgba(0, 0, 0, 0.1);
            display: flex;
            align-items: center;
            justify-content: center;
            
            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
            }
            
            span {
              font-size: 0.9rem;
              font-weight: 600;
              color: #666;
            }
          }
          
          .assignee-name {
            font-weight: 500;
          }
        }
        
        &.unassigned {
          color: #999;
          font-style: italic;
        }
        
        &.due-date {
          &.overdue {
            color: #dc3545;
            font-weight: 600;
          }
          
          &.due-soon {
            color: #ffc107;
            font-weight: 600;
          }
        }
      }
    }
  }
  
  .task-description {
    margin-bottom: 2rem;
    
    h4 {
      font-size: 1.1rem;
      font-weight: 600;
      color: #333;
      margin: 0 0 1rem 0;
    }
    
    p {
      line-height: 1.6;
      color: #555;
      margin: 0;
      
      &.no-content {
        color: #999;
        font-style: italic;
      }
    }
  }
  
  .task-tags {
    margin-bottom: 2rem;
    
    h4 {
      font-size: 1.1rem;
      font-weight: 600;
      color: #333;
      margin: 0 0 1rem 0;
    }
    
    .tags-list {
      display: flex;
      flex-wrap: wrap;
      gap: 0.5rem;
      
      .tag {
        display: inline-block;
        padding: 0.3rem 0.6rem;
        background: rgba(79, 172, 254, 0.1);
        border: 1px solid rgba(79, 172, 254, 0.3);
        border-radius: 6px;
        font-size: 0.8rem;
        color: #4facfe;
        font-weight: 500;
      }
    }
  }
  
  .task-timestamps {
    padding-top: 1.5rem;
    border-top: 1px solid rgba(0, 0, 0, 0.1);
    
    .timestamp {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 0.5rem;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .timestamp-label {
        font-size: 0.85rem;
        color: #666;
        font-weight: 500;
      }
      
      .timestamp-value {
        font-size: 0.85rem;
        color: #333;
      }
    }
  }
}

// Edit Mode Styles
.edit-mode {
  .edit-form {
    .form-group {
      margin-bottom: 1.5rem;
      
      .form-label {
        display: block;
        font-size: 0.9rem;
        font-weight: 600;
        color: #333;
        margin-bottom: 0.5rem;
        
        &.required::after {
          content: ' *';
          color: #dc3545;
        }
      }
      
      .form-input,
      .form-textarea,
      .form-select {
        width: 100%;
        padding: 0.75rem;
        border: 1px solid rgba(0, 0, 0, 0.2);
        border-radius: 8px;
        font-size: 0.95rem;
        background: #ffffff;
        transition: all 0.3s ease;
        
        &:focus {
          outline: none;
          border-color: #4facfe;
          box-shadow: 0 0 0 3px rgba(79, 172, 254, 0.2);
        }
        
        &.error {
          border-color: #dc3545;
          box-shadow: 0 0 0 3px rgba(220, 53, 69, 0.2);
        }
      }
      
      .form-textarea {
        resize: vertical;
        min-height: 100px;
        font-family: inherit;
      }
      
      .form-error {
        font-size: 0.8rem;
        color: #dc3545;
        margin-top: 0.25rem;
      }
    }
    
    .form-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 1rem;
      
      @media (max-width: 600px) {
        grid-template-columns: 1fr;
      }
    }
    
    .form-actions {
      display: flex;
      justify-content: flex-end;
      gap: 1rem;
      padding-top: 1.5rem;
      border-top: 1px solid rgba(0, 0, 0, 0.1);
      
      .btn {
        padding: 0.75rem 1.5rem;
        border: none;
        border-radius: 8px;
        font-size: 0.95rem;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s ease;
        
        &:disabled {
          opacity: 0.6;
          cursor: not-allowed;
        }
        
        &.btn-secondary {
          background: rgba(0, 0, 0, 0.1);
          color: #333;
          
          &:hover:not(:disabled) {
            background: rgba(0, 0, 0, 0.15);
          }
        }
        
        &.btn-primary {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
          color: #ffffff;
          
          &:hover:not(:disabled) {
            transform: translateY(-1px);
            box-shadow: 0 4px 15px rgba(79, 172, 254, 0.4);
          }
          
          .loading-content {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            
            .loading-spinner {
              width: 16px;
              height: 16px;
              border: 2px solid rgba(255, 255, 255, 0.3);
              border-top: 2px solid #ffffff;
              border-radius: 50%;
              animation: spin 1s linear infinite;
            }
          }
        }
      }
    }
  }
}

// Comments Styles
.add-comment-section {
  margin-bottom: 2rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  
  h4 {
    font-size: 1.1rem;
    font-weight: 600;
    color: #333;
    margin: 0 0 1rem 0;
  }
  
  .comment-form {
    .form-group {
      margin-bottom: 1rem;
      
      .comment-input {
        width: 100%;
        padding: 0.75rem;
        border: 1px solid rgba(0, 0, 0, 0.2);
        border-radius: 8px;
        font-size: 0.95rem;
        font-family: inherit;
        resize: vertical;
        
        &:focus {
          outline: none;
          border-color: #4facfe;
          box-shadow: 0 0 0 3px rgba(79, 172, 254, 0.2);
        }
      }
    }
    
    .comment-actions {
      display: flex;
      justify-content: flex-end;
      
      .btn-sm {
        padding: 0.5rem 1rem;
        font-size: 0.85rem;
      }
    }
  }
}

.comments-section {
  h4 {
    font-size: 1.1rem;
    font-weight: 600;
    color: #333;
    margin: 0 0 1.5rem 0;
  }
  
  .comments-list {
    .comment-item {
      padding: 1rem;
      border: 1px solid rgba(0, 0, 0, 0.1);
      border-radius: 8px;
      margin-bottom: 1rem;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .comment-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 0.75rem;
        
        .comment-author {
          display: flex;
          align-items: center;
          gap: 0.75rem;
          
          .author-avatar {
            width: 28px;
            height: 28px;
            border-radius: 50%;
            overflow: hidden;
            background: rgba(0, 0, 0, 0.1);
            display: flex;
            align-items: center;
            justify-content: center;
            
            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
            }
            
            span {
              font-size: 0.8rem;
              font-weight: 600;
              color: #666;
            }
          }
          
          .author-name {
            font-size: 0.9rem;
            font-weight: 600;
            color: #333;
          }
        }
        
        .comment-time {
          font-size: 0.8rem;
          color: #666;
        }
      }
      
      .comment-content {
        p {
          margin: 0;
          line-height: 1.5;
          color: #555;
        }
      }
    }
  }
  
  .no-comments {
    text-align: center;
    padding: 2rem;
    color: #666;
    
    p {
      margin: 0;
      font-style: italic;
    }
  }
}

// Activity Styles
.activity-section {
  h4 {
    font-size: 1.1rem;
    font-weight: 600;
    color: #333;
    margin: 0 0 1.5rem 0;
  }
  
  .activity-placeholder {
    text-align: center;
    padding: 3rem 2rem;
    color: #666;
    
    p {
      margin: 0 0 0.5rem 0;
      
      &:last-child {
        margin-bottom: 0;
      }
    }
  }
}

// Animations
@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-50px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes fadeInOut {
  from { opacity: 0; }
  to { opacity: 1; }
}

// Responsive Design
@media (max-width: 768px) {
  .modal-backdrop {
    padding: 0.5rem;
  }
  
  .task-detail-container {
    max-height: 95vh;
  }
  
  .detail-header {
    padding: 1rem;
    flex-direction: column;
    gap: 1rem;
    
    .header-left {
      .task-title {
        font-size: 1.2rem;
      }
    }
    
    .header-actions {
      width: 100%;
      justify-content: space-between;
    }
  }
  
  .tab-navigation {
    .tab-btn {
      padding: 0.75rem 1rem;
      font-size: 0.85rem;
    }
  }
  
  .tab-panel {
    padding: 1rem;
  }
}

@media (max-width: 480px) {
  .task-detail-container {
    border-radius: 12px;
  }
  
  .detail-header {
    .header-left {
      flex-direction: column;
      align-items: flex-start;
      gap: 0.75rem;
      
      .task-title {
        font-size: 1.1rem;
      }
    }
    
    .header-actions {
      .action-btn {
        padding: 0.4rem 0.8rem;
        font-size: 0.8rem;
      }
    }
  }
  
  .edit-mode {
    .edit-form {
      .form-actions {
        flex-direction: column;
        
        .btn {
          width: 100%;
        }
      }
    }
  }
}
```

## 4. USER FLOW DIAGRAM (FROM AGENT-3)

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kanban Board User Flow Diagram</title>
    <script src="https://cdn.jsdelivr.net/npm/mermaid/dist/mermaid.min.js"></script>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 20px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: rgba(255, 255, 255, 0.95);
            border-radius: 16px;
            padding: 30px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            backdrop-filter: blur(10px);
        }
        h1 {
            text-align: center;
            color: #333;
            margin-bottom: 30px;
            font-size: 2.5rem;
            font-weight: 700;
        }
        .description {
            text-align: center;
            color: #666;
            margin-bottom: 40px;
            font-size: 1.1rem;
            line-height: 1.6;
        }
        .mermaid {
            text-align: center;
            background: #ffffff;
            border-radius: 12px;
            padding: 20px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
        }
        .legend {
            margin-top: 30px;
            padding: 20px;
            background: rgba(79, 172, 254, 0.1);
            border-radius: 12px;
            border-left: 4px solid #4facfe;
        }
        .legend h3 {
            color: #333;
            margin-top: 0;
        }
        .legend ul {
            color: #555;
            line-height: 1.8;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🎯 Kanban Board User Flow Diagram</h1>
        <div class="description">
            <p>This diagram illustrates the complete user journey through the Kanban Board application, showing all possible interactions, navigation paths, and API integrations.</p>
        </div>
        
        <div class="mermaid">
            flowchart TD
                Start[User Opens Application]
                Start --> Login[Authentication Check]
                Login --> Dashboard[Main Dashboard]
                
                Dashboard --> Sidebar[Sidebar Navigation]
                Dashboard --> Header[Header Controls]
                Dashboard --> MainContent[Main Content Area]
                
                Sidebar --> TaskMgmt[Task Management]
                Sidebar --> Analytics[Analytics]
                Sidebar --> Config[Configuration]
                
                TaskMgmt --> KanbanBoard[Kanban Board]
                TaskMgmt --> CollabBoard[Collaborative Board]
                
                Analytics --> ProgressAnalytics[Progress Analytics]
                Analytics --> ReportBuilder[Report Builder]
                
                Config --> BoardConfig[Board Configuration]
                Config --> Settings[Settings]
                
                Header --> Search[Search Tasks]
                Header --> Notifications[Notifications]
                Header --> UserProfile[User Profile]
                
                KanbanBoard --> TodoColumn[To Do Column]
                KanbanBoard --> InProgressColumn[In Progress Column]
                KanbanBoard --> DoneColumn[Done Column]
                
                TodoColumn --> TaskCard1[Task Card]
                InProgressColumn --> TaskCard2[Task Card]
                DoneColumn --> TaskCard3[Task Card]
                
                TaskCard1 --> DragDrop[Drag and Drop]
                TaskCard2 --> DragDrop
                TaskCard3 --> DragDrop
                
                DragDrop --> UpdateStatus[Update Task Status]
                UpdateStatus --> APICall1[PUT /api/tasks/id]
                APICall1 --> RefreshBoard[Refresh Board]
                
                TaskCard1 --> TaskDetail[Task Detail View]
                TaskCard2 --> TaskDetail
                TaskCard3 --> TaskDetail
                
                TaskDetail --> EditTask[Edit Task]
                TaskDetail --> AddComment[Add Comment]
                TaskDetail --> DeleteTask[Delete Task]
                
                EditTask --> APICall2[PUT /api/tasks/id]
                DeleteTask --> APICall3[DELETE /api/tasks/id]
                AddComment --> APICall4[POST /api/comments]
                
                KanbanBoard --> AddTaskBtn[Add Task Button]
                AddTaskBtn --> AddTaskModal[Add Task Modal]
                AddTaskModal --> TaskForm[Task Creation Form]
                TaskForm --> SubmitTask[Submit New Task]
                SubmitTask --> APICall5[POST /api/tasks]
                APICall5 --> RefreshBoard
                
                CollabBoard --> RealTimeActivity[Real-time Activity Feed]
                CollabBoard --> TeamAssign[Team Assignment]
                TeamAssign --> TeamModal[Team Assignment Modal]
                TeamModal --> AssignUser[Assign User to Task]
                AssignUser --> APICall6[PUT /api/tasks/id/assign]
                
                ProgressAnalytics --> ViewMetrics[View Progress Metrics]
                ProgressAnalytics --> GenerateCharts[Generate Analytics Charts]
                ViewMetrics --> FilterData[Filter Analytics Data]
                
                ReportBuilder --> SelectTemplate[Select Report Template]
                ReportBuilder --> ConfigReport[Configure Report]
                SelectTemplate --> ReportModal[Report Configuration Modal]
                ConfigReport --> GenerateReport[Generate Report]
                GenerateReport --> APICall7[POST /api/reports]
                
                BoardConfig --> WorkflowRules[Workflow Rules]
                BoardConfig --> ColumnSettings[Column Settings]
                WorkflowRules --> WorkflowModal[Workflow Rules Modal]
                ColumnSettings --> UpdateColumns[Update Column Configuration]
                
                Settings --> UserPrefs[User Preferences]
                Settings --> ThemeSettings[Theme Settings]
                Settings --> NotificationSettings[Notification Settings]
                UserPrefs --> SaveSettings[Save User Settings]
                SaveSettings --> APICall8[PUT /api/user/preferences]
                
                Search --> FilterTasks[Filter Tasks]
                FilterTasks --> SearchResults[Display Search Results]
                SearchResults --> APICall9[GET /api/tasks?search=query]
                
                Notifications --> ViewNotifications[View Notifications]
                ViewNotifications --> MarkAsRead[Mark as Read]
                MarkAsRead --> APICall10[PUT /api/notifications/id]
                
                UserProfile --> ProfileSettings[Profile Settings]
                UserProfile --> Logout[Logout]
                ProfileSettings --> UpdateProfile[Update Profile Information]
                UpdateProfile --> APICall11[PUT /api/user/profile]
                
                APICall1 --> Success1[Success Response]
                APICall2 --> Success2[Success Response]
                APICall3 --> Success3[Success Response]
                APICall4 --> Success4[Success Response]
                APICall5 --> Success5[Success Response]
                APICall6 --> Success6[Success Response]
                APICall7 --> Success7[Success Response]
                APICall8 --> Success8[Success Response]
                APICall9 --> Success9[Success Response]
                APICall10 --> Success10[Success Response]
                APICall11 --> Success11[Success Response]
                
                Success1 --> RefreshBoard
                Success2 --> TaskDetail
                Success3 --> KanbanBoard
                Success4 --> TaskDetail
                Success5 --> KanbanBoard
                Success6 --> CollabBoard
                Success7 --> ReportBuilder
                Success8 --> Settings
                Success9 --> SearchResults
                Success10 --> Notifications
                Success11 --> UserProfile
                
                RefreshBoard --> KanbanBoard
        </div>
        
        <div class="legend">
            <h3>📋 Flow Legend</h3>
            <ul>
                <li><strong>Blue Nodes:</strong> User Interface Components and Views</li>
                <li><strong>Green Nodes:</strong> User Actions and Interactions</li>
                <li><strong>Orange Nodes:</strong> API Calls and Backend Integration</li>
                <li><strong>Purple Nodes:</strong> Data Processing and State Updates</li>
                <li><strong>Arrows:</strong> Navigation and Data Flow Direction</li>
            </ul>
            
            <h3>🔗 Key Integration Points</h3>
            <ul>
                <li><strong>Task Management:</strong> Complete CRUD operations with drag-and-drop functionality</li>
                <li><strong>Real-time Collaboration:</strong> Live updates and team assignment features</li>
                <li><strong>Analytics & Reporting:</strong> Data visualization and report generation</li>
                <li><strong>User Management:</strong> Profile settings, preferences, and notifications</li>
                <li><strong>Search & Filter:</strong> Advanced task discovery and filtering capabilities</li>
            </ul>
        </div>
    </div>
    
    <script>
        mermaid.initialize({
            startOnLoad: true,
            theme: 'default',
            flowchart: {
                useMaxWidth: true,
                htmlLabels: true,
                curve: 'basis'
            }
        });
    </script>
</body>
</html>
```

## 5. QUALITY VALIDATION REPORT (FROM AGENT-4)

### Validation Summary:
**Overall Status:** Needs Improvement

**Coverage:**
- Architecture vs HTML: Pass (95% alignment)
- Architecture vs Specs: Fail (Multiple critical mismatches)
- Specs vs User Flow: Partial (60% flow coverage)

### Issues Found:

#### HIGH SEVERITY

**Missing Real-time Collaboration Components**
- Description: User Flow Diagram includes real-time activity feed and collaborative features, but neither Architecture nor Specifications implement WebSocket connections or real-time components.
- Impact: Critical user flows will be broken, collaborative features non-functional.
- Affected Components: CollabBoard, RealTimeActivity, TeamAssign components

**API Endpoint Mismatch for Comments**
- Description: User Flow shows POST /api/comments for adding comments, but Architecture only defines task-related endpoints (/api/tasks).
- Impact: Comment functionality will fail at runtime.
- Affected Components: TaskDetailComponent

**Missing Notification System Implementation**
- Description: User Flow includes notification viewing and management, but no notification components or APIs are defined in Architecture or Specs.
- Impact: Notification features completely non-functional.
- Affected Components: HeaderComponent notification button, notification management flows

**Incomplete Team Assignment Modal**
- Description: Architecture defines TeamAssignModalComponent but Specifications don't implement it, while User Flow requires team assignment functionality.
- Impact: Team collaboration features will be missing.
- Affected Components: TeamAssignModalComponent

#### MEDIUM SEVERITY

**Report Builder API Mismatch**
- Description: User Flow shows POST /api/reports for report generation, but Architecture doesn't define report-related APIs.
- Impact: Report generation functionality will fail.
- Affected Components: ReportBuilderComponent

**Missing Workflow Rules Implementation**
- Description: User Flow includes workflow rules configuration, but neither Architecture nor Specs implement WorkflowRulesModalComponent.
- Impact: Advanced board configuration features missing.
- Affected Components: WorkflowRulesModalComponent

**User Profile Management Gap**
- Description: User Flow shows profile settings and updates (PUT /api/user/profile), but Architecture doesn't define user management APIs.
- Impact: User profile features non-functional.
- Affected Components: HeaderComponent user controls

#### LOW SEVERITY

**Search Implementation Inconsistency**
- Description: Architecture mentions both local filtering and API search, but Specs don't clearly define the search strategy.
- Impact: Search behavior may be inconsistent.
- Affected Components: HeaderComponent search functionality

**Theme Settings Missing**
- Description: User Flow includes theme settings, but Specs don't implement theme switching functionality.
- Impact: User customization features limited.
- Affected Components: Settings components

### Recommendations:

#### Immediate Actions (High Priority):
- Implement missing modal components: TeamAssignModalComponent, WorkflowRulesModalComponent, ReportConfigModalComponent
- Add comment management API endpoints and update TaskDetailComponent
- Implement notification system with proper API endpoints and UI components
- Add WebSocket service for real-time collaboration features
- Create missing service classes: NotificationService, ReportService, UserService

#### Architecture Improvements:
- Add comprehensive API documentation for all user flow endpoints
- Implement proper error handling for all API interactions
- Add loading states for all async operations
- Implement proper state management for real-time features

#### Code Quality Enhancements:
- Add TypeScript interfaces for all API response types
- Implement proper form validation for all modal components
- Add comprehensive error boundaries for component failures
- Implement proper accessibility attributes for all interactive elements

## 6. PIPELINE ALIGNMENT SUMMARY

### Architecture ↔ Specs:
- **Aligned:** Core Kanban components (KanbanBoardComponent, KanbanColumnComponent, TaskCardComponent) are fully implemented according to architecture specifications
- **Misaligned:** Missing implementations for TeamAssignModalComponent, WorkflowRulesModalComponent, and ReportConfigModalComponent
- **Gap:** Real-time collaboration features defined in architecture but not implemented in specifications

### Specs ↔ User Flow:
- **Aligned:** Basic task CRUD operations, drag-and-drop functionality, and modal interactions are supported
- **Misaligned:** Advanced features like real-time collaboration, notification management, and report generation lack proper implementation
- **Gap:** Approximately 40% of user flow paths are not supported by current specifications

### Validation Coverage:
- **Complete:** Core Kanban board functionality is fully validated and aligned
- **Partial:** Modal components and API integrations have gaps in implementation
- **Missing:** Real-time features, advanced analytics, and collaborative tools need comprehensive implementation

## 7. IMPLEMENTATION NOTES FOR DEVELOPERS

### Development Priority Order:
1. **Phase 1:** Implement core Kanban functionality (KanbanBoardComponent, TaskCardComponent, AddTaskModalComponent)
2. **Phase 2:** Add task detail management (TaskDetailComponent with full CRUD operations)
3. **Phase 3:** Implement missing modal components (TeamAssignModalComponent, WorkflowRulesModalComponent)
4. **Phase 4:** Add real-time collaboration features and notification system
5. **Phase 5:** Implement analytics, reporting, and advanced configuration features

### Folder Structure Implementation:
- Follow the mandatory folder structure defined in Architecture section
- Implement components in the specified locations under `src/app/components/`
- Create services in `src/app/services/` for API integration
- Define models in `src/app/models/` for TypeScript interfaces

### API Integration Guidelines:
- Use the TaskService for all task-related operations
- Implement proper error handling for all API calls
- Add loading states for async operations
- Follow the API endpoint mappings defined in Architecture section

### Component Implementation Guidelines:
- Use Angular CDK for drag-and-drop functionality
- Implement proper accessibility attributes (ARIA labels, roles)
- Follow responsive design patterns with specified breakpoints
- Use glass morphism design system as defined in CSS specifications

### Testing Requirements:
- Add unit tests for all component methods and interactions
- Implement integration tests for API service calls
- Include end-to-end tests for critical user flows
- Test accessibility compliance with screen readers

### Performance Considerations:
- Implement OnPush change detection strategy for all components
- Use trackBy functions for *ngFor loops
- Implement lazy loading for dashboard sections
- Add virtual scrolling for large task lists

## 8. ISSUES FOUND

### State Management Mismatch in TransactionList
- **Severity:** High
- **Description:** TaskDetailComponent state management doesn't align with parent KanbanBoardComponent state updates
- **Impact:** Task updates may not reflect properly across components
- **Resolution:** Implement proper state synchronization between parent and child components

### Missing Component Coverage
- **Severity:** Medium
- **Description:** Several components defined in architecture are not implemented in specifications
- **Impact:** User flows will be incomplete, advanced features unavailable
- **Resolution:** Implement missing components: TeamAssignModalComponent, WorkflowRulesModalComponent, ReportConfigModalComponent

### Design System Inconsistency
- **Severity:** Low
- **Description:** Some components use different styling approaches than the defined glass morphism design system
- **Impact:** Visual inconsistency across the application
- **Resolution:** Standardize all components to use consistent design tokens and styling patterns

## 9. RECOMMENDATIONS

### Align State Management Implementation
- Implement centralized state management using Angular services or NgRx
- Ensure proper data flow between parent and child components
- Add proper event handling for task updates across the component tree

### Implement Missing Components
- Create TeamAssignModalComponent for user assignment functionality
- Develop WorkflowRulesModalComponent for advanced board configuration
- Build ReportConfigModalComponent for analytics and reporting features
- Add NotificationComponent for user notification management

### Update Styling to Follow Design System Tokens
- Standardize color palette across all components
- Implement consistent spacing and typography scales
- Ensure all components use glass morphism effects consistently
- Add proper responsive design patterns for all screen sizes

### Enhance API Integration
- Add comprehensive error handling for all API endpoints
- Implement proper loading states and user feedback
- Add retry mechanisms for failed API calls
- Implement proper caching strategies for improved performance

### Improve Accessibility
- Add comprehensive ARIA attributes to all interactive elements
- Implement proper keyboard navigation support
- Ensure screen reader compatibility for all components
- Add high contrast mode support for accessibility compliance

---

**Final Package Status:** The UI implementation package provides a solid foundation for core Kanban functionality but requires significant additional work to support the full feature set defined in the user flow diagram. Development teams should prioritize implementing missing components and ensuring proper API integration before proceeding with advanced features.