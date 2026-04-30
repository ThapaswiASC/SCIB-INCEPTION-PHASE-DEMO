<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Flow Diagram</title>
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
    </style>
</head>
<body>
    <div class="container">
        <h1>User Flow Diagram</h1>
        <div class="mermaid">
            flowchart TD
                Start[User Visits Application]
                Start --> Login[Login Page]
                Login --> Auth{Authentication}
                Auth -->|Success| Dashboard[Main Dashboard]
                Auth -->|Failed| Login
                Dashboard --> Profile[User Profile]
                Dashboard --> Settings[Application Settings]
                Dashboard --> Reports[View Reports]
                Dashboard --> DataEntry[Data Entry Form]
                Profile --> EditProfile[Edit Profile Information]
                EditProfile --> SaveProfile[Save Changes]
                SaveProfile --> Dashboard
                Settings --> UpdateSettings[Update Preferences]
                UpdateSettings --> Dashboard
                Reports --> FilterReports[Filter Options]
                FilterReports --> ViewDetails[View Report Details]
                ViewDetails --> ExportReport[Export Report]
                ViewDetails --> Dashboard
                DataEntry --> ValidateData{Validate Input}
                ValidateData -->|Valid| SaveData[Save to Database]
                ValidateData -->|Invalid| ShowError[Show Error Message]
                ShowError --> DataEntry
                SaveData --> Confirmation[Show Success Message]
                Confirmation --> Dashboard
                Dashboard --> Logout[Logout]
                Logout --> Start
        </div>
    </div>
</body>
</html>