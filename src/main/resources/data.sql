-- MERGE ... KEY(nom) : idempotent, pas d'erreur si les lignes existent déjà (syntaxe H2)
MERGE INTO role (nom, description) KEY(nom) VALUES
    ('ADMIN',    'Administrateur de l''application'),
    ('MEMBRE',   'Membre du bureau / du BDE'),
    ('ETUDIANT', 'Étudiant adhérent');

MERGE INTO staff_type (nom, description) KEY(nom) VALUES
    ('BAR',       'Service au bar'),
    ('EAU',       'Point d''eau / prévention'),
    ('SECURITE',  'Sécurité'),
    ('ENTREE',    'Accueil et contrôle des entrées'),
    ('VESTIAIRE', 'Gestion du vestiaire');
