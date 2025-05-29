$fileId = "1oOo38g-E9RTJEygZKGc1WRQBhYAzuXBwpsNj2ffCd1w"
$output = "docs/DCevoModParse.xlsx"

# URL correcte pour exporter une Google Sheet en Excel (.xlsx)
$url = "https://docs.google.com/spreadsheets/d/$fileId/export?format=xlsx"

Invoke-WebRequest -Uri $url -OutFile $output