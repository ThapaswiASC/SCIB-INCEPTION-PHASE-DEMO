# UI COMPONENT SPECIFICATIONS

## 1. INPUT REFERENCE (FROM AGENT-1)

**Page Name:** Dashboard Page

**Mapped Layout:** Main Dashboard Layout

**Components from A1:**
- HeaderComponent
- SidebarComponent
- DashboardMainContent
- AccountSummaryCard
- TransactionHistoryTable
- AddFundsModal

## 2. GLOBAL ARCHITECTURE SUMMARY

**Layout Type:** Mixed (Flex + Grid)

**Main Sections:**
- Header (Fixed top navigation)
- Sidebar (Collapsible navigation menu)
- Main Content (Dashboard content area)
- Modals (Overlay components)

**Component Strategy:** Maximize reuse of existing UI library components

**Reused Components:**
- Button (from ui-library/Button)
- Card (from ui-library/Card)
- Table (from ui-library/Table)
- Modal (from ui-library/Modal)
- Input (from ui-library/Input)
- Snackbar (from ui-library/Snackbar)

**New Components (if any):**
- DashboardMainContent (page-specific)
- AccountSummaryCard (feature-specific)

## 3. FOLDER STRUCTURE

```
src/
 ├── pages/
 │    └── dashboard/
 │         ├── DashboardPage.tsx
 │
 ├── components/
 │    ├── layout/
 │    │    ├── HeaderComponent.tsx
 │    │    └── SidebarComponent.tsx
 │    ├── feature/
 │    │    ├── AccountSummaryCard.tsx
 │    │    ├── TransactionHistoryTable.tsx
 │    │    └── AddFundsModal.tsx
 │    ├── shared/
 │         └── (reused components from ui-library)
```

## 4. COMPONENT IMPLEMENTATION

### 4.1 DashboardPage

**Component Type:** Page

**Mapped From A1:** Main dashboard container

**Purpose:** Root page component that orchestrates dashboard layout and data flow

**Parent:** App Router

**Children:**
- HeaderComponent
- SidebarComponent
- DashboardMainContent
- AddFundsModal

**Dependencies:**
- React hooks (useState, useEffect)
- API service layer
- UI library components

**Library Components Used:**
- Layout → ui-library/Layout
- Snackbar → ui-library/Snackbar

**TypeScript Specification**

**Inputs:**
- userId: string (from route params)
- initialData?: DashboardData (optional pre-loaded data)

**Outputs:**
- onError: (error: Error) => void
- onSuccess: (message: string) => void

**State:**
- dashboardData: DashboardData | null
- loading: boolean
- error: string | null
- showAddFundsModal: boolean

**Methods:**
- loadDashboardData() → Promise<void>
- handleAddFunds() → void
- handleError(error: Error) → void

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<Layout className="dashboard-layout">
  <HeaderComponent 
    user={currentUser}
    onMenuToggle={handleMenuToggle}
  />
  
  <div className="dashboard-container">
    <SidebarComponent 
      collapsed={sidebarCollapsed}
      activeItem="dashboard"
    />
    
    <main className="dashboard-main">
      <DashboardMainContent 
        data={dashboardData}
        loading={loading}
        onAddFunds={handleAddFunds}
      />
    </main>
  </div>
  
  {showAddFundsModal && (
    <AddFundsModal 
      onClose={handleCloseModal}
      onSubmit={handleFundsSubmit}
    />
  )}
  
  <Snackbar 
    message={snackbarMessage}
    type={snackbarType}
    open={showSnackbar}
  />
</Layout>
```

**CSS SPECIFICATION**

**Layout:**
- .dashboard-layout: display: flex, flex-direction: column, height: 100vh
- .dashboard-container: display: flex, flex: 1
- .dashboard-main: flex: 1, overflow-y: auto

**Spacing:**
- padding: 0 (handled by child components)
- gap: 0 (seamless layout)

**Component Styles:**
- .dashboard-layout
- .dashboard-container
- .dashboard-main

**Responsive:**
- Mobile: sidebar collapses to overlay
- Tablet: sidebar remains visible but narrower
- Desktop: full sidebar width

**API INTEGRATION**

- loadDashboardData() → GET /api/dashboard/{userId} → fetch user dashboard data
- handleFundsSubmit() → POST /api/fund-transfer → process fund addition

**ERROR HANDLING**

- API failure → show error snackbar with retry option
- Network error → show offline indicator
- Validation error → inline form validation messages

**INTERACTION FLOW**

**User Action → UI → API**
- Page load → show loading spinner → GET dashboard data → populate components
- Click "Add Funds" → open modal → fill form → POST fund transfer → refresh data
- Menu toggle → animate sidebar → update layout

### 4.2 HeaderComponent

**Component Type:** Layout

**Mapped From A1:** Top navigation header

**Purpose:** Global navigation and user controls

**Parent:** DashboardPage

**Children:**
- UserMenu dropdown
- NotificationBell

**Dependencies:**
- ui-library/Button
- ui-library/Avatar
- ui-library/Dropdown

**Library Components Used:**
- Button → ui-library/Button
- Avatar → ui-library/Avatar
- Dropdown → ui-library/Dropdown

**TypeScript Specification**

**Inputs:**
- user: UserProfile
- onMenuToggle: () => void
- notifications?: Notification[]

**Outputs:**
- onLogout: () => void
- onProfileClick: () => void

**State:**
- showUserMenu: boolean
- showNotifications: boolean

**Methods:**
- toggleUserMenu() → void
- toggleNotifications() → void
- handleLogout() → void

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<header className="app-header">
  <div className="header-left">
    <Button 
      variant="ghost" 
      onClick={onMenuToggle}
      icon="menu"
    />
    <h1 className="app-title">Dashboard</h1>
  </div>
  
  <div className="header-right">
    <Button 
      variant="ghost"
      icon="notifications"
      badge={notificationCount}
      onClick={toggleNotifications}
    />
    
    <Dropdown 
      trigger={<Avatar src={user.avatar} name={user.name} />}
      open={showUserMenu}
      onToggle={toggleUserMenu}
    >
      <DropdownItem onClick={onProfileClick}>Profile</DropdownItem>
      <DropdownItem onClick={handleLogout}>Logout</DropdownItem>
    </Dropdown>
  </div>
</header>
```

**CSS SPECIFICATION**

**Layout:**
- .app-header: display: flex, justify-content: space-between, align-items: center
- .header-left, .header-right: display: flex, align-items: center

**Spacing:**
- padding: 1rem 2rem
- gap: 1rem between items

**Component Styles:**
- .app-header (primary background, border-bottom)
- .app-title (typography scale)

**Responsive:**
- Mobile: hide app title, smaller padding
- Desktop: full layout

**API INTEGRATION**

- handleLogout() → POST /api/auth/logout → clear session
- loadNotifications() → GET /api/notifications → fetch user notifications

**ERROR HANDLING**

- Logout failure → show error message
- Notification load failure → show fallback state

**INTERACTION FLOW**

**User Action → UI → API**
- Click menu button → toggle sidebar → update parent state
- Click avatar → show dropdown → display user options
- Click logout → confirm dialog → POST logout → redirect to login

### 4.3 AccountSummaryCard

**Component Type:** Feature

**Mapped From A1:** Account balance and summary display

**Purpose:** Display user account balance and quick actions

**Parent:** DashboardMainContent

**Children:**
- Balance display
- Quick action buttons

**Dependencies:**
- ui-library/Card
- ui-library/Button
- Currency formatting utility

**Library Components Used:**
- Card → ui-library/Card
- Button → ui-library/Button

**TypeScript Specification**

**Inputs:**
- accountData: AccountSummary
- loading?: boolean
- onAddFunds: () => void
- onTransfer: () => void

**Outputs:**
- onActionClick: (action: string) => void

**State:**
- showBalance: boolean (for privacy toggle)

**Methods:**
- toggleBalanceVisibility() → void
- formatCurrency(amount: number) → string

**HTML STRUCTURE (PSEUDO CODE ONLY)**

```html
<Card className="account-summary-card">
  <CardHeader>
    <h2>Account Summary</h2>
    <Button 
      variant="ghost" 
      icon={showBalance ? "eye-off" : "eye"}
      onClick={toggleBalanceVisibility}
    />
  </CardHeader>
  
  <CardContent>
    <div className="balance-section">
      <span className="balance-label">Available Balance</span>
      <span className="balance-amount">
        {showBalance ? formatCurrency(accountData.balance) : "••••••"}
      </span>
    </div>
    
    <div className="quick-actions">
      <Button 
        variant="primary"
        onClick={onAddFunds}
      >
        Add Funds
      </Button>
      <Button 
        variant="secondary"
        onClick={onTransfer}
      >
        Transfer
      </Button>
    </div>
  </CardContent>
</Card>
```

**CSS SPECIFICATION**

**Layout:**
- .account-summary-card: full width, responsive
- .balance-section: flex column, center aligned
- .quick-actions: flex row, gap between buttons

**Spacing:**
- padding: 1.5rem
- gap: 1rem between sections

**Component Styles:**
- .balance-amount (large typography, primary color)
- .balance-label (muted text)
- .quick-actions (button spacing)

**Responsive:**
- Mobile: stack buttons vertically
- Desktop: horizontal button layout

**API INTEGRATION**

- Data comes from parent component (no direct API calls)

**ERROR HANDLING**

- Missing account data → show placeholder state
- Balance load error → show error message in card

**INTERACTION FLOW**

**User Action → UI → API**
- Click "Add Funds" → trigger parent callback → open modal
- Click "Transfer" → trigger parent callback → navigate to transfer page
- Toggle balance visibility → update local state → re-render

## 5. COMPONENT MAPPING SUMMARY

| A1 Component | Final Component | Type |
|--------------|----------------|------|
| HeaderComponent | HeaderComponent | Layout |
| SidebarComponent | SidebarComponent | Layout |
| DashboardMainContent | DashboardMainContent | Page |
| AccountSummaryCard | AccountSummaryCard | Feature |
| TransactionHistoryTable | TransactionHistoryTable | Feature |
| AddFundsModal | AddFundsModal | Feature |

## 6. DATA FLOW (FROM AGENT-1)

**Parent → Child:**
- DashboardPage → HeaderComponent (user, onMenuToggle)
- DashboardPage → SidebarComponent (collapsed, activeItem)
- DashboardPage → DashboardMainContent (data, loading, onAddFunds)
- DashboardMainContent → AccountSummaryCard (accountData, onAddFunds)
- DashboardMainContent → TransactionHistoryTable (transactions, loading)

## 7. API USAGE SUMMARY

**Component → API:**
- DashboardPage → GET /api/dashboard/{userId} (load dashboard data)
- HeaderComponent → POST /api/auth/logout (user logout)
- HeaderComponent → GET /api/notifications (load notifications)
- AddFundsModal → POST /api/fund-transfer (process fund addition)
- TransactionHistoryTable → GET /api/transactions (load transaction history)

## 8. NOTES & CONSTRAINTS

- HTML must follow A1 structure strictly
- Do NOT create new components unnecessarily - prefer reusing existing UI library components
- Prefer reusable library components over custom implementations
- HTML must be pseudo-code ONLY (NOT real framework code)
- All API integrations must include proper error handling
- Components must be responsive and accessible
- Follow TypeScript strict mode requirements
- Implement proper loading states for all async operations
- Use consistent naming conventions across all components
- Ensure proper prop drilling and state management
- All components must support theming through CSS custom properties