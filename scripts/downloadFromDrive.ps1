$fileId = "TON_FILE_ID"
$output = "docs/DCevoModParse.xlsx"

# URL de téléchargement "simplifiée"
$url = "https://docs.google.com/spreadsheets/d/1oOo38g-E9RTJEygZKGc1WRQBhYAzuXBwpsNj2ffCd1w/edit?usp=drive_link"

Invoke-WebRequest -Uri $url -OutFile $output