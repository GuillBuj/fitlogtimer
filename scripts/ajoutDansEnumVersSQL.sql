-- 1. Renommer l’ancienne colonne
ALTER TABLE exercise RENAME COLUMN family TO old_family;

-- 2. Ajouter la nouvelle colonne avec l’énum élargi
ALTER TABLE exercise ADD COLUMN family ENUM(
  'ALL',
  'CURL',
  'ELEVATION',
  'PLANCHE',
  'PRESS',
  'TIRAGE_ELASTIQUE',
  'POMPES',
  'SQUAT',
  'DEADLIFT'
);

-- 3. Copier les données de l’ancienne colonne vers la nouvelle
UPDATE exercise SET family = old_family;

-- 4. Supprimer l’ancienne colonne
ALTER TABLE exercise DROP COLUMN old_family;