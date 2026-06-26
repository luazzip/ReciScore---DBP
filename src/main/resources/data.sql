-- ============================================================
-- SEED DATA  (ON CONFLICT DO NOTHING → seguro en cada restart)
-- ============================================================

-- ── USERS ────────────────────────────────────────────────────
-- admin123
-- user123

INSERT INTO users (email, password, name, username, points, multiplier, profile_picture, location, reciclajes, nivel, racha_dias, fecha_registro, ultimo_login, role, version)
VALUES
    ('admin@reciscore.com',
     '$2a$10$qi/w1AsanqMfHVj3vhLdme4KmYr/cgjvSCItGYY63IivqWUiY8qcy',
     'Admin General', 'admin', 500, 1.0, NULL, 'Miraflores', 20, 3, 7, NOW(), NOW(), 'ADMIN', 0),

    ('test@reciscore.com',
     '$2a$10$rcQTatR6wL7SvfGKcq8u..jKPLXgMrdQfSb.g48B0UrO9RG7uq/Cm',
     'Usuario Test', 'testuser', 120, 1.0, NULL, 'San Isidro', 5, 1, 2, NOW(), NOW(), 'USER', 0),

    ('maria@reciscore.com',
     '$2a$10$EblZqNptyYvcLm/VwDptzu4CfbGWx0KmvLkp.MZ.cYuY8hgGW5gSq',
     'María López', 'marialopez', 340, 1.0, NULL, 'Barranco', 14, 2, 5, NOW(), NOW(), 'USER', 0),

    ('carlos@reciscore.com',
     '$2a$10$EblZqNptyYvcLm/VwDptzu4CfbGWx0KmvLkp.MZ.cYuY8hgGW5gSq',
     'Carlos Ríos', 'carlosrios', 80, 1.0, NULL, 'Surco', 3, 1, 0, NOW(), NOW(), 'USER', 0)
    ON CONFLICT (email) DO NOTHING;

-- ── MATERIAL ─────────────────────────────────────────────────
INSERT INTO material (name, points_per_kg, weight, category, recyclable)
VALUES
    ('Botella PET',        15.0, 0.03, 'PLASTICO', true),
    ('Bolsa plástica',      5.0, 0.01, 'PLASTICO', false),
    ('Botella de vidrio',  12.0, 0.40, 'VIDRIO',   true),
    ('Vidrio roto',         8.0, 0.50, 'VIDRIO',   false),
    ('Periódico',          10.0, 0.20, 'PAPEL',    true),
    ('Cartón',             12.0, 0.30, 'PAPEL',    true),
    ('Lata de aluminio',   20.0, 0.02, 'METAL',    true),
    ('Lata de acero',      14.0, 0.05, 'METAL',    true),
    ('Envase tetra pak',    6.0, 0.04, 'PAPEL',    false),
    ('Tubo de PVC',         4.0, 0.15, 'PLASTICO', false)
    ON CONFLICT (name) DO NOTHING;

-- ── PUNTO_MAPA ───────────────────────────────────────────────
INSERT INTO punto_mapa (latitude, longitude, nombre, tipo)
VALUES
    (-12.1219, -77.0282, 'Punto Verde Miraflores',  'ACOPIO_OFICIAL'),
    (-12.1100, -77.0350, 'Ecopunto San Isidro',      'ACOPIO_OFICIAL'),
    (-12.1500, -77.0200, 'Centro de Acopio Surco',   'ACOPIO_OFICIAL'),
    (-12.1450, -77.0050, 'Punto Limpio La Molina',   'ACOPIO_OFICIAL'),
    (-12.1300, -77.0180, 'Ecopunto Barranco',         'ACOPIO_OFICIAL'),
    (-12.0900, -77.0500, 'Zona Sucia Av. Brasil',     'ZONA_SUCIA'),
    (-12.1600, -77.0300, 'Zona Sucia Surco Norte',    'ZONA_SUCIA'),
    (-12.1050, -77.0150, 'Zona Sucia San Borja',      'ZONA_SUCIA')
    ON CONFLICT DO NOTHING;

-- ── DESAFIO ──────────────────────────────────────────────────
INSERT INTO desafio (titulo, descripcion, categoria, meta_valor, puntos, fecha_inicio, fecha_fin, activo)
VALUES
    ('Reciclador Inicial',  'Recicla 5 kg de materiales en un mes.',          'RECICLAJE', 5,  100, '2026-06-01 00:00:00', '2026-06-30 23:59:59', true),
    ('Rey del Plástico',    'Recicla 10 kg de plástico durante el mes.',      'RECICLAJE',  10, 200, '2026-06-01 00:00:00', '2026-06-30 23:59:59', true),
    ('Guardián del Vidrio', 'Lleva 8 kg de vidrio a un punto de acopio.',     'RECICLAJE',    8,  180, '2026-06-01 00:00:00', '2026-07-31 23:59:59', true),
    ('Maestro del Papel',   'Recicla 15 kg de papel o cartón.',               'RECICLAJE',     15, 150, '2026-06-01 00:00:00', '2026-07-31 23:59:59', true),
    ('Héroe del Metal',     'Recicla 6 kg de metal (latas, aluminio).',       'RECICLAJE',     6,  220, '2026-07-01 00:00:00', '2026-07-31 23:59:59', false),
    ('Racha Semanal',       'Mantén una racha de 7 días consecutivos.',       'RACHA',     7,  300, '2026-06-01 00:00:00', '2026-12-31 23:59:59', true)
    ON CONFLICT (titulo) DO NOTHING;