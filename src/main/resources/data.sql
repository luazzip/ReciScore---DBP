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
    ON CONFLICT (nombre) DO NOTHING;

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

-- ── USER_DESAFIO (inscripciones con progreso) ─────────────────
INSERT INTO user_desafio (user_id, desafio_id, progreso_actual, completado, activo, fecha_inscripcion)
VALUES
    (2, 1, 3, false, true, '2026-06-05 10:00:00'),   -- test: 3/5 en Reciclador Inicial
    (2, 3, 2, false, true, '2026-06-10 14:30:00'),   -- test: 2/8 en Guardián del Vidrio
    (3, 1, 5, true,  true, '2026-06-03 08:00:00'),   -- maría: ✅ completó Reciclador Inicial
    (3, 2, 4, false, true, '2026-06-08 09:15:00'),   -- maría: 4/10 en Rey del Plástico
    (4, 1, 1, false, true, '2026-06-15 16:45:00'),   -- carlos: 1/5 en Reciclador Inicial
    (1, 1, 5, true,  true, '2026-06-02 07:00:00'),   -- admin: ✅ completó Reciclador Inicial
    (1, 2, 8, false, true, '2026-06-05 09:00:00'),   -- admin: 8/10 en Rey del Plástico
    (1, 6, 5, false, true, '2026-06-10 12:00:00'),   -- admin: 5/7 en Racha Semanal
    (4, 6, 2, false, true, '2026-06-12 11:00:00')    -- carlos: 2/7 en Racha Semanal
    ON CONFLICT DO NOTHING;

-- ── REPORTE_RECICLAJE ────────────────────────────────────────────
INSERT INTO reporte_reciclaje (user_id, material_id, foto_url, tamano_objeto, numero_articulos, material_detectado_ia, confianza_ia, validado_ia, gps_validado, fecha)
VALUES
    -- admin (1) — 4 reportes
    (1, 1, 'https://example.com/foto1.jpg', 'MEDIANO', 3, true, 0.92, true,  true,  '2026-06-02 10:30:00'),
    (1, 6, 'https://example.com/foto2.jpg', 'GRANDE',  2, true, 0.88, true,  true,  '2026-06-05 14:15:00'),
    (1, 3, 'https://example.com/foto3.jpg', 'PEQUENO', 5, true, 0.95, true,  true,  '2026-06-10 09:00:00'),
    (1, 7, 'https://example.com/foto4.jpg', 'PEQUENO', 4, true, 0.91, true,  false, '2026-06-15 16:45:00'),

    -- test (2) — 2 reportes
    (2, 5, 'https://example.com/foto5.jpg', 'GRANDE',  1, true, 0.87, true,  true,  '2026-06-08 11:00:00'),
    (2, 4, 'https://example.com/foto6.jpg', 'MEDIANO', 2, false,0.45, false, true,  '2026-06-12 13:30:00'),

    -- maría (3) — 2 reportes
    (3, 2, 'https://example.com/foto7.jpg', 'PEQUENO', 6, true, 0.93, true,  true,  '2026-06-04 08:20:00'),
    (3, 8, 'https://example.com/foto8.jpg', 'MEDIANO', 3, true, 0.89, true,  true,  '2026-06-11 17:00:00'),

    -- carlos (4) — 2 reportes
    (4, 9, 'https://example.com/foto9.jpg', 'MEDIANO', 2, false,0.62, false, true,  '2026-06-14 12:00:00'),
    (4, 1, 'https://example.com/foto10.jpg','PEQUENO', 3, true, 0.96, true,  false, '2026-06-18 10:30:00');