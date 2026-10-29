---
description: Rules and Best Practices for React frontend and Figma design system integration
applyTo: "onshore-frontend/**"
---


## Design System Integration Rules (Figma → Codebase)

### 1. Token Definitions
- **Location:** Store design tokens (colors, typography, spacing) in `src/theme/tokens.ts` or as JSON/YAML for easy transformation.
- **Format:** Use flat objects for tokens. Example:
  ```typescript
  // src/theme/tokens.ts
  export const colors = {
    primary: "#000000",
    secondary: "#FFFFFF",
    accent: "#FFB6C1",
    // ...other tokens
  };
  ```
- **Transformation:** If using Figma tokens plugin, automate token export/import via scripts in `ai/scripts/`.

### 2. Component Library
- **Location:** UI components live in `src/components/`.
- **Architecture:** Use atomic design (atoms, molecules, organisms) or feature-based folders.
- **Documentation:** Use Storybook (`.storybook/`) for component docs and visual testing.

### 3. Frameworks & Libraries
- **UI Framework:** React (see `package.json`).
- **Styling:** Use CSS Modules or Styled Components. Example:
  ```typescript
  import styles from './Button.module.css';
  // or
  import styled from 'styled-components';
  ```
- **Build System:** Vite (`vite.config.ts`).

### 4. Asset Management
- **Storage:** Place images/icons in `public/assets/`.
- **Referencing:** Use relative paths or import statements.
- **Optimization:** Use SVGs for icons, compress images, and configure Vite for asset optimization.
- **CDN:** If needed, configure CDN in Vite or via environment variables.

### 5. Icon System
- **Storage:** Store SVG icons in `public/assets/icons/`.
- **Usage:** Import SVGs as React components or use an icon library.
  ```typescript
  import { ReactComponent as LogoIcon } from '../assets/icons/logo.svg';
  ```
- **Naming:** Use kebab-case for icon filenames (e.g., `user-profile.svg`).

### 6. Styling Approach
- **Methodology:** Prefer CSS Modules for local scoping, or Styled Components for dynamic styles.
- **Global Styles:** Place in `src/theme/global.css` or `src/theme/GlobalStyle.ts`.
- **Responsive Design:** Use CSS media queries or utility libraries (e.g., `styled-system`).

### 6.1. Pill/Segmented Switch Pattern
- **Component:** Use `SegmentedSwitch` component from `src/components/SegmentedSwitch.tsx` for pill-style toggle controls
- **Design Specifications:**
  - **Container**: Single rounded pill (borderRadius: 24px) with white background and light gray border (#D8D8D8)
  - **Selected Side**: Dark background (#1D1D1D / fakeBlack), white text, checkmark icon, fills pill completely with no margin
  - **Unselected Side**: Transparent background (shows white pill), gray text (#B6B6B6), small margin to show border
  - **Border Treatment**: Selected side has rounded edge on outer side, straight edge in middle; unselected side has small margins
  - **Typography**: ABChanel 2022, Semi Bold (600), 15px, line-height 23px
  - **Height**: 48px to match form inputs
- **Usage Example:**
  ```typescript
  <SegmentedSwitch
    value={selectedValue}
    onChange={handleChange}
    options={[
      { value: 'OPTION1', label: 'Option 1' },
      { value: 'OPTION2', label: 'Option 2' }
    ]}
    data-testid="my-switch"
  />
  ```
- **When to Use**: Binary or small set of mutually exclusive options (2-4 choices), where visual prominence helps user understand current state
- **Accessibility**: Includes keyboard navigation (Arrow keys, Enter, Space), proper ARIA roles (radiogroup, radio), and focus indicators

### 6.2. Dropdown/Menu Pattern
- **Global Styling:** All dropdowns (navbar, form selects, autocompletes) use centralized theme styling defined in `src/theme/lightTheme.ts`
- **Design Specifications:**
  - **Background**: White (#FFFFFF)
  - **Min Width**: 200px
  - **Shadow**: Drop shadow on left, right, and bottom only (no top shadow)
    - Shadow spec: `2px 4px 8px rgba(0, 0, 0, 0.15), -2px 4px 8px rgba(0, 0, 0, 0.15)`
  - **Border Radius**: 0 (square corners per design system)
  - **Margin Top**: 4px gap from trigger element
  - **Item Padding**: 10px 16px
  - **Item Typography**: ABChanel 2022, 14px, line-height 20px
  - **Item Hover**: fakeWhite (#F9F9F9) background
- **Token Location:** `componentTokens.dropdown` in `src/theme/tokens.ts`
- **Usage:** No inline styling needed - MUI `<Menu>` and `<MenuItem>` components automatically use global theme
  ```typescript
  <Menu
    anchorEl={anchorEl}
    open={Boolean(anchorEl)}
    onClose={handleClose}
  >
    <MenuItem onClick={handleOption1}>Option 1</MenuItem>
    <MenuItem onClick={handleOption2}>Option 2</MenuItem>
  </Menu>
  ```
- **When to Use**: Navigation dropdowns, form selects, context menus, action menus
- **Note:** All future dropdowns will automatically inherit these styles from the theme

### 7. Layout Patterns
- **Two-Pane Layout**: For resource management interfaces (lists, CRUD), use the `TwoPanePage` component located in `src/components/layout/TwoPanePage.tsx`
  - **When to use**: Collections requiring sidebar list navigation with detail/form view (e.g., Markets, Locales, Sites, Templates)
  - **Features**: Collapsible left panel, dynamic content area, consistent header actions
  - **Required props**: `title`, `leftWidth`, `collapsedLeftWidth`, `collapsedLabel`, `actions`, `left`, `right`
  - **Example**:
    ```typescript
    <TwoPanePage
      title="MARKETS"
      leftWidth={280}
      collapsedLeftWidth={60}
      collapsedLabel="Show existing markets"
      actions={headerActions}
      left={<MarketsSidebar {...sidebarProps} />}
      right={<MarketsDetail {...detailProps} />}
    />
    ```
- **Standard Layout**: For single-focus pages without sidebar navigation, use the standard page components from `onshore-frontend/src/components`

### 7.1. Form Layout Standards
- **Width Constraints**: Forms should ALWAYS use full available width
  - **NEVER** set `maxWidth` constraints on form containers
  - Use `width: "100%"` on form container Box components
  - Let the parent layout (TwoPanePage or page container) control the maximum width
- **Padding**: All form containers MUST have consistent padding
  - Use `p: 3` (24px) on the main form container Box for proper breathing room
  - Example:
    ```typescript
    <Box
      display="flex"
      flexDirection="column"
      gap={3}
      sx={{ width: "100%", p: 3 }}
    >
      {/* Form content */}
    </Box>
    ```
- **Wrapper Components**: Edit/Create wrapper components should NOT add visual containers
  - Do NOT wrap forms in Box with borders, shadows, or extra padding
  - Keep wrappers clean and let the form handle its own styling
  - Bad: `<Box p={2} sx={{ border: '1px solid #eee', borderRadius: 1, boxShadow: 1 }}>`
  - Good: Direct render of form component without wrapper styling

### 8. Project Structure
- **Frontend:** Feature-based or atomic design folders under `src/`.
  ```
  src/
    components/
      layout/     # TwoPanePage, PageHeader, HidePanelButton
    pages/
    resources/    # Feature-based resource folders
    theme/
    utils/
  ```
- **Backend:** Java Spring Boot follows layered architecture (`controllers/`, `services/`, `models/`).

#### Example Integration
```typescript
// src/components/Button.tsx
import { colors } from '../theme/tokens';
import styled from 'styled-components';

const Button = styled.button`
  background: ${colors.primary};
  color: ${colors.secondary};
  padding: 8px 16px;
  border-radius: 4px;
`;

export default Button;
```

**Follow these rules to ensure Figma designs are accurately and efficiently integrated into the codebase.**
