INSERT INTO veranstaltung ( -- Recurve Ligen
  veranstaltung_id,
  veranstaltung_wettkampftyp_id,
  veranstaltung_name,
  veranstaltung_sportjahr,
  veranstaltung_ligaleiter,
  veranstaltung_ligaleiter_email,
  veranstaltung_meldedeadline,
  veranstaltung_dauer,
  veranstaltung_wettkampfdauer,
  veranstaltung_kampfrichter_anzahl)
VALUES

(0, 1, 'Würtembergliga', 2018, 'Andreas Böhm', 'a.b@bogenliga.de', 31.10.2017, 4,0,5, 1, NULL),
(1, 1, 'Landesliga Nord', 2018, 'Andreas Böhm', 'a.b@bogenliga.de', 31.10.2017, 4,0,5, 1, 0),
(2, 1, 'Landesliga Süd', 2018, 'Andreas Böhm', 'a.b@bogenliga.de', 31.10.2017, 4,0,5, 1, 0),
(3, 1, 'Relegation Landesliga Nord', 2018, 'Andreas Böhm', 'a.b@bogenliga.de', 31.10.2017, 4,0,5, 1, 1),
(4, 1, 'Relegation Landesliga Süd', 2018, 'Andreas Böhm', 'a.b@bogenliga.de', 31.10.2017, 4,0,5, 1, 2),
(5, 1, 'Bezirksoberliga Neckar', 2018, 'Cornelius Denker', 'c.d@bogenliga.de', 31.10.2017, 4,0,5, 1, 4),
(6, 1, 'Bezirksliga A Neckar', 2018, 'Cornelius Denker', 'c.d@bogenliga.de', 31.10.2017, 4,0,5, 1, 5),
(7, 1, 'Bezirksoberliga Oberschwaben', 2018, 'Edwin Freude', 'e.f@bogenliga.de', 31.10.2017, 4,0,5, 1, 4),
(8, 1, 'Bezirksliga A Oberschwaben', 2018, 'Andreas Böhm', 'a.b@bogenliga.de', 31.10.2017, 4,0,5, 1, 7),
(9, 1, 'Bezirksliga B Oberschwaben', 2018, 'Andreas Böhm', 'a.b@bogenliga.de', 31.10.2017, 4,0,5, 1, 8)
;

INSERT INTO veranstaltung ( -- Compound Ligen
  veranstaltung_id,
  veranstaltung_wettkampftyp_id,
  veranstaltung_name,
  veranstaltung_sportjahr,
  veranstaltung_ligaleiter,
  veranstaltung_ligaleiter_email,
  veranstaltung_meldedeadline,
  veranstaltung_dauer,
  veranstaltung_wettkampfdauer,
  veranstaltung_kampfrichter_anzahl)
VALUES
(20, 1, 'Bawü Compound Finale', 2018, 'Andreas Böhm', 'a.b@bogenliga.de', 31.10.2017, 4,0,5, 1, NULL),
(21, 1, 'Württembergliga Compound', 2018, 'Andreas Böhm', 'a.b@bogenliga.de', 31.10.2017, 4,0,5, 1, 20),
(22, 1, 'Landesliga A Compound', 2018, 'Andreas Böhm', 'a.b@bogenliga.de', 31.10.2017, 4,0,5, 1, 21),
(23, 1, 'Landesliga B Compound', 2018, 'Andreas Böhm', 'a.b@bogenliga.de', 31.10.2017, 4,0,5, 1, 22)
;

